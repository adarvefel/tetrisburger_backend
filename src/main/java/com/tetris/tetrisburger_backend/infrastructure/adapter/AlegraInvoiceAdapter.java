//package com.tetris.tetrisburger_backend.infrastructure.adapter;
//
//import com.tetris.tetrisburger_backend.domain.model.Invoice;
//import com.tetris.tetrisburger_backend.domain.model.Order;
//import com.tetris.tetrisburger_backend.domain.model.OrderItem;
//import com.tetris.tetrisburger_backend.domain.model.Payment;
//import com.tetris.tetrisburger_backend.domain.port.out.InvoicePort;
//import com.tetris.tetrisburger_backend.domain.port.out.InvoiceRepository;
//import com.tetris.tetrisburger_backend.infrastructure.config.AlegraConfig;
//import org.springframework.context.annotation.Primary;
//import org.springframework.http.*;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.time.LocalDate;
//import java.util.*;
//
//@Component
//@Primary
//public class AlegraInvoiceAdapter implements InvoicePort {
//
//    private static final Integer DEFAULT_CLIENT_ID = 1;
//
//    private final RestTemplate restTemplate;
//    private final AlegraConfig alegraConfig;
//    private final InvoiceRepository invoiceRepository;
//
//    public AlegraInvoiceAdapter(RestTemplate restTemplate,
//                                AlegraConfig alegraConfig,
//                                InvoiceRepository invoiceRepository) {
//        this.restTemplate = restTemplate;
//        this.alegraConfig = alegraConfig;
//        this.invoiceRepository = invoiceRepository;
//    }
//
//    @Override
//    public Invoice createInvoice(Order order, Payment payment) {
//        Invoice invoice = Invoice.create(
//                order.getIdOrder(),
//                payment.getIdPayment(),
//                payment.getAmount()
//        );
//
//        try {
//            HttpHeaders headers = buildHeaders();
//
//            List<Map<String, Object>> items = order.getItems().stream()
//                    .map(this::buildItem)
//                    .toList();
//
//            Map<String, Object> body = new HashMap<>();
//            body.put("date", LocalDate.now().toString());
//            body.put("client", Map.of("id", DEFAULT_CLIENT_ID));
//            body.put("items", items);
//            body.put("observations", "Pedido TetrisBurger #" + order.getOrderNumber()
//                    + " | Pago: " + payment.getPaymentMethod());
//
//            ResponseEntity<Map> response = restTemplate.exchange(
//                    alegraConfig.getApiUrl() + "/invoices",
//                    HttpMethod.POST,
//                    new HttpEntity<>(body, headers),
//                    Map.class
//            );
//
//            String externalId = String.valueOf(response.getBody().get("id"));
//
//            // ✅ Fix 1 — numberTemplate es un objeto anidado
//            Map<String, Object> numberTemplate =
//                    (Map<String, Object>) response.getBody().get("numberTemplate");
//            String invoiceNumber = String.valueOf(numberTemplate.get("fullNumber"));
//
//            invoice.markAsIssued(externalId, invoiceNumber);
//            System.out.println("✅ Factura Alegra creada: " + invoiceNumber);
//
//        } catch (Exception e) {
//            invoice.markAsFailed();
//            System.err.println("❌ Error creando factura en Alegra: " + e.getMessage());
//            e.printStackTrace(); // 👈 temporal para debug
//        }
//
//        return invoiceRepository.save(invoice);
//    }
//
//    private Map<String, Object> buildItem(OrderItem item) {
//        Map<String, Object> map = new HashMap<>();
//        map.put("name", item.getItemName());
//        map.put("quantity", item.getQuantity());
//        // ✅ Fix 2 — divide con escala para evitar ArithmeticException
//        map.put("price", item.getSubtotal()
//                .divide(BigDecimal.valueOf(item.getQuantity()), 2, RoundingMode.HALF_UP));
//        return map;
//    }
//
//    private HttpHeaders buildHeaders() {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Authorization", alegraConfig.getAuthHeader());
//        return headers;
//    }
//}
