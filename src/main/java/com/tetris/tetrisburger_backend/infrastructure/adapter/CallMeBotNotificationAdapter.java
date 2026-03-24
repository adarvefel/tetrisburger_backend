//package com.tetris.tetrisburger_backend.infrastructure.adapter;
//
//import com.tetris.tetrisburger_backend.domain.model.Order;
//import com.tetris.tetrisburger_backend.domain.model.OrderItem;
//import com.tetris.tetrisburger_backend.domain.model.WhatsappSettings;
//import com.tetris.tetrisburger_backend.domain.port.out.NotificationPort;
//import com.tetris.tetrisburger_backend.domain.port.out.WhatsappSettingsRepository;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.util.Optional;
//
//@Component
//public class CallMeBotNotificationAdapter implements NotificationPort {
//
//    private final WhatsappSettingsRepository settingsRepository;
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    public CallMeBotNotificationAdapter(WhatsappSettingsRepository settingsRepository) {
//        this.settingsRepository = settingsRepository;
//    }
//
////
////    @Override
////    public void notifyNewOrder(Order order, String clientPhone, String clientName) {
////        Optional<WhatsappSettings> settingsOpt = settingsRepository.findFirst();
////
////        if (settingsOpt.isEmpty()) return;
////
////        WhatsappSettings settings = settingsOpt.get();
////
////        if (!settings.isAutoSend()) return;
////
////        String message = buildMessage(order, clientPhone, clientName);
////
////        String url = UriComponentsBuilder
////                .fromHttpUrl("https://api.callmebot.com/whatsapp.php")
////                .queryParam("phone", settings.getBusinessNumber())
////                .queryParam("text", message)
////                .queryParam("apikey", settings.getApiKey())
////                .toUriString();
////
////        try {
////            restTemplate.getForObject(url, String.class);
////        } catch (Exception e) {
////            System.err.println("Error enviando WhatsApp: " + e.getMessage());
////        }
////    }
//
//    @Override
//    public void notifyNewOrder(Order order, String clientPhone, String clientName) {
//        System.out.println("📱 WhatsApp MOCK:");
//        System.out.println("   Orden: " + order.getOrderNumber());
//        System.out.println("   Cliente: " + clientName);
//        System.out.println("   Tel: " + clientPhone);
//        System.out.println("   Total: $" + order.getTotalAmount());
//        order.getItems().forEach(item ->
//                System.out.println("   - " + item.getItemName() + " x" + item.getQuantity())
//        );
//    }
//
//    private String buildMessage(Order order, String clientPhone, String clientName) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("🍔 *NUEVO PEDIDO*%0A");
//        sb.append("Orden: ").append(order.getOrderNumber()).append("%0A");
//        sb.append("Cliente: ").append(clientName).append("%0A");
//        sb.append("Tel: ").append(clientPhone).append("%0A");
//        sb.append("Total: $").append(order.getTotalAmount()).append("%0A");
//        sb.append("Items:%0A");
//        for (OrderItem item : order.getItems()) {
//            sb.append("  - ").append(item.getItemName())
//                    .append(" x").append(item.getQuantity())
//                    .append("%0A");
//        }
//        return sb.toString();
//    }
//}