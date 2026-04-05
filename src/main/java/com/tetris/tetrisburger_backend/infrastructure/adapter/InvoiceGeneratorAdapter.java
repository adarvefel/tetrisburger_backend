package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.common.ImageUploadResult;
import com.tetris.tetrisburger_backend.domain.enums.OrderItemType;
import com.tetris.tetrisburger_backend.domain.model.Invoice;
import com.tetris.tetrisburger_backend.domain.model.Order;
import com.tetris.tetrisburger_backend.domain.model.Payment;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import com.tetris.tetrisburger_backend.domain.port.out.InvoicePort;
import com.tetris.tetrisburger_backend.domain.port.out.InvoiceRepository;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class InvoiceGeneratorAdapter implements InvoicePort {

    private final InvoiceRepository invoiceRepository;
    private final RestTemplate restTemplate;
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final ImageStoragePort imageStoragePort;

    @Value("${invoice.generator.api-key}")
    private String apiKey;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public InvoiceGeneratorAdapter(InvoiceRepository invoiceRepository,
                                   RestTemplate restTemplate,
                                   JavaMailSender mailSender,
                                   UserRepository userRepository,
                                   ImageStoragePort imageStoragePort) {
        this.invoiceRepository = invoiceRepository;
        this.restTemplate = restTemplate;
        this.mailSender = mailSender;
        this.userRepository = userRepository;
        this.imageStoragePort = imageStoragePort;
    }

    @Override
    public Invoice createInvoice(Order order, Payment payment) {
        Invoice invoice = Invoice.create(
                order.getIdOrder(),
                payment.getIdPayment(),
                payment.getAmount()
        );

        try {

            User user = userRepository.findUserById(order.getIdUser()).orElse(null);
            String userName = (user != null)
                    ? user.getUserName()
                    : "Cliente #" + order.getIdUser();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("from", "TetrisBurger");
            body.put("header", "FACTURA");
            body.put("to", userName);
            body.put("number", order.getOrderNumber());
            body.put("date", LocalDate.now().toString());
            body.put("currency", "cop");

            body.put("item_header", "Articulo");
            body.put("quantity_header", "Cant.");
            body.put("unit_cost_header", "Precio Unit.");
            body.put("amount_header", "Subtotal");
            body.put("subtotal_title", "Subtotal");
            body.put("balance_title", "Saldo pendiente");
            body.put("total_title", "TOTAL");
            body.put("date_title", "Fecha");
            body.put("invoice_number_title", "Factura N.");
            body.put("to_title", "Para");
            body.put("notes_title", "Notas");

            body.put("items", buildInvoiceItems(order));
            body.put("notes",
                    "Método de pago: " + payment.getPaymentMethod().getDisplayName() + "\n" +
                            "Estado: Pagado\n" +
                            "Gracias por tu compra"
            );

            body.put("logo", "https://tetrisburger-image.s3.amazonaws.com/products/logo.png");

            ResponseEntity<byte[]> response = restTemplate.exchange(
                    "https://invoice-generator.com",
                    HttpMethod.POST,
                    new HttpEntity<>(body, headers),
                    byte[].class
            );
            System.out.println(">>> Invoice API status: " + response.getStatusCode());
            System.out.println(">>> Invoice PDF size: " + (response.getBody() != null ? response.getBody().length : 0));


            if (response.getBody() == null) {
                throw new RuntimeException("Error generando factura: PDF vacío");
            }

            byte[] pdfBytes = response.getBody();

            ImageUploadResult result = imageStoragePort.uploadInvoicePdf(pdfBytes);
            String pdfUrl = imageStoragePort.getImageUrl(result.imageKey());

            if (user != null && user.getEmail() != null) {
                sendInvoiceEmail(
                        user.getEmail(),
                        userName,
                        order,
                        payment,
                        pdfBytes
                );
            }

            invoice.markAsIssued(
                    String.valueOf(order.getOrderNumber()),
                    "FV-" + order.getOrderNumber(),
                    pdfUrl
            );

            System.out.println("Factura enviada a: " +
                    (user != null ? user.getEmail() : "sin email"));

        } catch (Exception e) {
            invoice.markAsFailed();
            System.err.println(">>> ERROR generando factura: " + e.getClass().getName());
            System.err.println(">>> Mensaje: " + e.getMessage());
            System.err.println("Error generando factura: " + e.getMessage());
            e.printStackTrace();
        }

        return invoiceRepository.save(invoice);

    }

    // -------------------------------------------------------------------------
    // Items para el PDF (API externa)
    // -------------------------------------------------------------------------

    private List<Map<String, Object>> buildInvoiceItems(Order order) {
        List<Map<String, Object>> items = new ArrayList<>();

        for (var item : order.getItems()) {
            Map<String, Object> line = new LinkedHashMap<>();

            if (item.getItemType() == OrderItemType.BURGER) {
                line.put("name", item.getItemName());
            } else {
                line.put("name", "   + " + item.getItemName());
            }

            line.put("quantity", item.getQuantity());
            line.put("unit_cost", item.getUnitPrice());
            items.add(line);
        }

        return items;
    }

    // -------------------------------------------------------------------------
    // Email
    // -------------------------------------------------------------------------

    private void sendInvoiceEmail(String toEmail, String userName,
                                  Order order, Payment payment,
                                  byte[] pdfBytes) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject("Tu factura TetrisBurger - Pedido #" + order.getOrderNumber());
        helper.setText(buildHtmlEmail(userName, order, payment), true);

        helper.addAttachment(
                "factura-" + order.getOrderNumber() + ".pdf",
                new ByteArrayResource(pdfBytes),
                "application/pdf"
        );

        mailSender.send(message);
    }

    private String buildHtmlEmail(String userName, Order order, Payment payment) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("es", "CO"));
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String invoiceNumber = String.valueOf(order.getOrderNumber());

        StringBuilder itemRows = new StringBuilder();

        for (var item : order.getItems()) {

            if (item.getItemType() == OrderItemType.BURGER) {
                itemRows.append("""
                    <tr>
                      <td style="padding:12px 16px; border-bottom:1px solid #2a2a2a;
                                 color:#e0e0e0; font-family:'Courier New',monospace; font-weight:600;">
                        %s
                      </td>
                      <td style="padding:12px 16px; border-bottom:1px solid #2a2a2a;
                                 color:#e0e0e0; text-align:center; font-family:'Courier New',monospace;">
                        %d
                      </td>
                      <td style="padding:12px 16px; border-bottom:1px solid #2a2a2a;
                                 color:#e0e0e0; text-align:right; font-family:'Courier New',monospace;">
                        $ %s COP
                      </td>
                      <td style="padding:12px 16px; border-bottom:1px solid #2a2a2a;
                                 color:#ffffff; text-align:right; font-weight:bold; font-family:'Courier New',monospace;">
                        $ %s COP
                      </td>
                    </tr>
                """.formatted(
                        item.getItemName(),
                        item.getQuantity(),
                        nf.format(item.getUnitPrice()),
                        nf.format(item.getSubtotal())
                ));

            } else {
                itemRows.append("""
                    <tr style="background-color:#1e1e1e;">
                      <td style="padding:8px 16px 8px 32px; border-bottom:1px solid #252525;
                                 color:#aaaaaa; font-size:12px; font-family:'Courier New',monospace;">
                        + %s
                      </td>
                      <td style="padding:8px 16px; border-bottom:1px solid #252525;
                                 color:#888888; text-align:center; font-size:12px; font-family:'Courier New',monospace;">
                        %d
                      </td>
                      <td style="padding:8px 16px; border-bottom:1px solid #252525;
                                 color:#888888; text-align:right; font-size:12px; font-family:'Courier New',monospace;">
                        $ %s COP
                      </td>
                      <td style="padding:8px 16px; border-bottom:1px solid #252525;
                                 color:#aaaaaa; text-align:right; font-size:12px; font-family:'Courier New',monospace;">
                        $ %s COP
                      </td>
                    </tr>
                """.formatted(
                        item.getItemName(),
                        item.getQuantity(),
                        nf.format(item.getUnitPrice()),
                        nf.format(item.getSubtotal())
                ));
            }
        }

        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
              <meta charset="UTF-8"/>
              <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
            </head>
            <body style="margin:0; padding:0; background-color:#111111; font-family:Arial,sans-serif;">

              <table width="100%%" cellpadding="0" cellspacing="0"
                     style="background-color:#111111; padding:40px 0;">
                <tr>
                  <td align="center">
                    <table width="620" cellpadding="0" cellspacing="0"
                           style="background-color:#1a1a1a; border-radius:12px; overflow:hidden;
                                  border:1px solid #2e2e2e;">

                      <!-- HEADER -->
                      <tr>
                        <td style="padding:32px 40px; border-bottom:2px solid #2a2a2a;">
                          <table width="100%%" cellpadding="0" cellspacing="0">
                            <tr>
                              <td>
                                <div style="font-size:28px; font-weight:900; letter-spacing:-1px;">
                                  <span style="color:#e63946;">T</span>
                                  <span style="color:#f4a261;">E</span>
                                  <span style="color:#2a9d8f;">T</span>
                                  <span style="color:#e9c46a;">R</span>
                                  <span style="color:#264653;">I</span>
                                  <span style="color:#e63946;">S</span>
                                  <span style="color:#ffffff; font-size:16px; font-weight:400; margin-left:4px;">BURGER</span>
                                </div>
                              </td>
                              <td align="right">
                                <div style="color:#888888; font-size:12px; text-transform:uppercase; letter-spacing:2px;">
                                  Factura
                                </div>
                                <div style="color:#ffffff; font-size:28px; font-weight:700; font-family:'Courier New',monospace;">
                                  #%s
                                </div>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>

                      <!-- INFO CLIENTE Y FECHA -->
                      <tr>
                        <td style="padding:28px 40px; border-bottom:1px solid #2a2a2a;">
                          <table width="100%%" cellpadding="0" cellspacing="0">
                            <tr>
                              <td width="50%%">
                                <div style="color:#888888; font-size:11px; text-transform:uppercase;
                                            letter-spacing:1.5px; margin-bottom:6px;">De</div>
                                <div style="color:#ffffff; font-size:14px; font-weight:600;">TetrisBurger</div>
                                <div style="color:#aaaaaa; font-size:13px;">pedidos@tetrisburger.com</div>
                              </td>
                              <td width="50%%" align="right">
                                <div style="color:#888888; font-size:11px; text-transform:uppercase;
                                            letter-spacing:1.5px; margin-bottom:6px;">Para</div>
                                <div style="color:#ffffff; font-size:14px; font-weight:600;">%s</div>
                                <div style="color:#aaaaaa; font-size:13px;">Pedido #%s</div>
                              </td>
                            </tr>
                            <tr>
                              <td style="padding-top:20px;">
                                <div style="color:#888888; font-size:11px; text-transform:uppercase;
                                            letter-spacing:1.5px; margin-bottom:6px;">Fecha</div>
                                <div style="color:#ffffff; font-size:13px;">%s</div>
                              </td>
                              <td align="right" style="padding-top:20px;">
                                <div style="color:#888888; font-size:11px; text-transform:uppercase;
                                            letter-spacing:1.5px; margin-bottom:6px;">Metodo de Pago</div>
                                <div style="color:#ffffff; font-size:13px;">%s</div>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>

                      <!-- TABLA DE ITEMS -->
                      <tr>
                        <td style="padding:0 40px;">
                          <table width="100%%" cellpadding="0" cellspacing="0" style="border-collapse:collapse;">
                            <thead>
                              <tr style="background-color:#222222;">
                                <th style="padding:12px 16px; text-align:left; color:#888888; font-size:11px;
                                           text-transform:uppercase; letter-spacing:1.5px; font-weight:500;">
                                  Articulo
                                </th>
                                <th style="padding:12px 16px; text-align:center; color:#888888; font-size:11px;
                                           text-transform:uppercase; letter-spacing:1.5px; font-weight:500;">
                                  Cant.
                                </th>
                                <th style="padding:12px 16px; text-align:right; color:#888888; font-size:11px;
                                           text-transform:uppercase; letter-spacing:1.5px; font-weight:500;">
                                  Precio Unit.
                                </th>
                                <th style="padding:12px 16px; text-align:right; color:#888888; font-size:11px;
                                           text-transform:uppercase; letter-spacing:1.5px; font-weight:500;">
                                  Subtotal
                                </th>
                              </tr>
                            </thead>
                            <tbody>
                              %s
                            </tbody>
                          </table>
                        </td>
                      </tr>

                      <!-- TOTALES -->
                      <tr>
                        <td style="padding:24px 40px; border-top:2px solid #2a2a2a;">
                          <table width="100%%" cellpadding="0" cellspacing="0">
                            <tr>
                              <td></td>
                              <td width="260">
                                <table width="100%%" cellpadding="0" cellspacing="0">
                                  <tr>
                                    <td style="padding:6px 0; color:#888888; font-size:13px;">Subtotal</td>
                                    <td style="padding:6px 0; color:#e0e0e0; font-size:13px; text-align:right;
                                               font-family:'Courier New',monospace;">
                                      $ %s COP
                                    </td>
                                  </tr>
                                  <tr>
                                    <td style="padding:6px 0; color:#888888; font-size:13px;">Impuesto</td>
                                    <td style="padding:6px 0; color:#e0e0e0; font-size:13px; text-align:right;
                                               font-family:'Courier New',monospace;">
                                      $ 0,00 COP
                                    </td>
                                  </tr>
                                  <tr>
                                    <td colspan="2">
                                      <hr style="border:none; border-top:1px solid #333; margin:8px 0;"/>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td style="padding:8px 0; color:#ffffff; font-size:16px; font-weight:700;">
                                      TOTAL
                                    </td>
                                    <td style="padding:8px 0; color:#e63946; font-size:18px; font-weight:900;
                                               text-align:right; font-family:'Courier New',monospace;">
                                      $ %s COP
                                    </td>
                                  </tr>
                                </table>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>

                      <!-- NOTA -->
                      <tr>
                        <td style="padding:0 40px 32px 40px;">
                          <div style="background-color:#222222; border-radius:8px; padding:16px 20px;
                                      border-left:3px solid #e63946;">
                            <div style="color:#888888; font-size:11px; text-transform:uppercase;
                                        letter-spacing:1.5px; margin-bottom:6px;">Notas</div>
                            <div style="color:#cccccc; font-size:13px; line-height:1.6;">
                              Gracias por tu pedido en TetrisBurger, <strong style="color:#ffffff;">%s</strong>!<br/>
                              Tu factura en PDF esta adjunta. Buen provecho!
                            </div>
                          </div>
                        </td>
                      </tr>

                      <!-- FOOTER -->
                      <tr>
                        <td style="padding:20px 40px; background-color:#111111;
                                   border-top:1px solid #222222; text-align:center;">
                          <div style="color:#555555; font-size:11px;">
                            &copy; 2025 TetrisBurger &middot; Este correo fue generado automaticamente
                          </div>
                        </td>
                      </tr>

                    </table>
                  </td>
                </tr>
              </table>

            </body>
            </html>
        """.formatted(
                invoiceNumber,
                userName,
                order.getOrderNumber(),
                fecha,
                payment.getPaymentMethod().toString(),
                itemRows.toString(),
                nf.format(order.getTotalAmount()),
                nf.format(order.getTotalAmount()),
                userName
        );
    }
}