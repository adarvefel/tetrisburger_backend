package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.model.Invoice;
import com.tetris.tetrisburger_backend.domain.model.Order;
import com.tetris.tetrisburger_backend.domain.model.Payment;
import com.tetris.tetrisburger_backend.domain.model.User;
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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Component
public class InvoiceGeneratorAdapter implements InvoicePort {

    private final InvoiceRepository invoiceRepository;
    private final RestTemplate restTemplate;
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    @Value("${invoice.generator.api-key}")
    private String apiKey;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public InvoiceGeneratorAdapter(InvoiceRepository invoiceRepository,
                                   RestTemplate restTemplate,
                                   JavaMailSender mailSender,
                                   UserRepository userRepository) {
        this.invoiceRepository = invoiceRepository;
        this.restTemplate = restTemplate;
        this.mailSender = mailSender;
        this.userRepository = userRepository;
    }

    @Override
    public Invoice createInvoice(Order order, Payment payment) {
        Invoice invoice = Invoice.create(
                order.getIdOrder(),
                payment.getIdPayment(),
                payment.getAmount()
        );

        try {
            // 1. Generar PDF
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            List<Map<String, Object>> items = order.getItems().stream()
                    .map(item -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", item.getItemName());
                        map.put("quantity", item.getQuantity());
                        map.put("unit_cost", item.getSubtotal()
                                .divide(BigDecimal.valueOf(item.getQuantity()),
                                        2, RoundingMode.HALF_UP));
                        return map;
                    }).toList();

            Map<String, Object> body = new HashMap<>();
            body.put("from", "TetrisBurger");
            body.put("to", "Cliente #" + order.getIdUser());
            body.put("number", order.getOrderNumber());
            body.put("date", LocalDate.now().toString());
            body.put("currency", "cop");
            body.put("items", items);
            body.put("notes", "Gracias por tu pedido en TetrisBurger!" +
                    " | Pago: " + payment.getPaymentMethod());

            ResponseEntity<byte[]> response = restTemplate.exchange(
                    "https://invoice-generator.com",
                    HttpMethod.POST,
                    new HttpEntity<>(body, headers),
                    byte[].class
            );

            byte[] pdfBytes = response.getBody();

            // 2. Enviar por correo
            User user = userRepository.findUserById(order.getIdUser()).orElse(null);
            if (user != null && user.getEmail() != null) {
                sendInvoiceEmail(user.getEmail(), user.getUserName(),
                        order.getOrderNumber(), pdfBytes);
            }

            invoice.markAsIssued(
                    String.valueOf(order.getOrderNumber()),
                    "FV-" + order.getOrderNumber()
            );
            System.out.println("Factura enviada a: " +
                    (user != null ? user.getEmail() : "sin email"));

        } catch (Exception e) {
            invoice.markAsFailed();
            System.err.println("Error generando factura: " + e.getMessage());
            e.printStackTrace();
        }

        return invoiceRepository.save(invoice);
    }

    private void sendInvoiceEmail(String toEmail, String userName,
                                  String orderNumber, byte[] pdfBytes) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject("🍔 Tu factura TetrisBurger - Pedido " + orderNumber);
        helper.setText(
                "Hola " + userName + ",\n\n" +
                        "Gracias por tu pedido en TetrisBurger. " +
                        "Adjunto encontrarás tu factura.\n\n" +
                        "¡Buen provecho! 🍔",
                false
        );

        helper.addAttachment("factura-" + orderNumber + ".pdf",
                new ByteArrayResource(pdfBytes),
                "application/pdf");

        mailSender.send(message);
    }
}
