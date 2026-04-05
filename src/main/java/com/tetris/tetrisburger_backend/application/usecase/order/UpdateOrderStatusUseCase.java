package com.tetris.tetrisburger_backend.application.usecase.order;

import com.tetris.tetrisburger_backend.domain.enums.OrderStatus;
import com.tetris.tetrisburger_backend.domain.exception.OrderNotFoundException;
import com.tetris.tetrisburger_backend.domain.exception.ProductNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.Order;
import com.tetris.tetrisburger_backend.domain.model.Payment;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.order.UpdateOrderStatus;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.domain.port.out.InvoicePort;
import com.tetris.tetrisburger_backend.domain.port.out.OrderRepository;
import com.tetris.tetrisburger_backend.domain.port.out.PaymentRepository;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
public class UpdateOrderStatusUseCase implements UpdateOrderStatus {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final BurgerRepository burgerRepository;
    private final InvoicePort invoicePort;
    private final SimpMessagingTemplate messagingTemplate;

    public UpdateOrderStatusUseCase(OrderRepository orderRepository,
                                    PaymentRepository paymentRepository,
                                    ProductRepository productRepository,
                                    BurgerRepository burgerRepository,
                                    InvoicePort invoicePort,
                                    SimpMessagingTemplate messagingTemplate) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.productRepository = productRepository;
        this.burgerRepository = burgerRepository;
        this.invoicePort = invoicePort;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public Order handle(Integer idOrder, OrderStatus newStatus, Integer employeeId) {
        Order order = orderRepository.findById(idOrder)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Orden no encontrada: " + idOrder));

        validateTransition(order.getStatus(), newStatus);

        // ── Cancellation ─────────────────────────────────────────────
        if (newStatus == OrderStatus.CANCELLED_BY_EMPLOYEE) {

            if (order.getStatus() == OrderStatus.ACCEPTED ||
                    order.getStatus() == OrderStatus.IN_PROGRESS) {
                restoreStock(order, employeeId);
            }

            order.updateStatus(newStatus, employeeId);
            Order saved = orderRepository.save(order);
            messagingTemplate.convertAndSend(
                    "/topic/orders/" + order.getIdUser(),
                    Map.of("orderId", idOrder, "status", "CANCELADA",
                            "mensaje", "Tu orden ha sido cancelada")
            );
            return saved;
        }

        // ── Accept — requires payment and deducts stock ──────────────
        if (newStatus == OrderStatus.ACCEPTED) {
            Payment payment = paymentRepository.findByOrderId(idOrder)
                    .orElseThrow(() -> new IllegalStateException(
                            "El pago debe ser registrado antes de aceptar la ordern"));

            deductStock(order, employeeId);

            order.updateStatus(newStatus, employeeId);
            Order saved = orderRepository.save(order);
            invoicePort.createInvoice(saved, payment);
            return saved;
        }

        order.updateStatus(newStatus, employeeId);
        return orderRepository.save(order);
    }

    // ==================== PRIVATE METHODS ====================

    private void deductStock(Order order, Integer employeeId) {
        order.getItems().forEach(item -> {

            // ── Burger: deduct each ingredient
            if (item.getIdBurger() != null) {
                Burger burger = burgerRepository.findById(item.getIdBurger())
                        .orElseThrow(() -> new IllegalStateException(
                                "Hamburguesa no encontrada: " + item.getIdBurger()));

                burger.getIngredients().forEach(snapshot -> {
                    Product product = productRepository.findByIdForUpdate(snapshot.getIdProduct())
                            .orElseThrow(() -> new ProductNotFoundException(snapshot.getIdProduct()));

                    int totalQuantity = snapshot.getQuantity() * item.getQuantity();
                    product.adjustStock(-totalQuantity, employeeId);
                    productRepository.save(product);
                });
            }

            // ── Direct product: deduct the product stock
            if (item.getIdProduct() != null) {
                Product product = productRepository.findByIdForUpdate(item.getIdProduct())
                        .orElseThrow(() -> new ProductNotFoundException(item.getIdProduct()));

                product.adjustStock(-item.getQuantity(), employeeId);
                productRepository.save(product);
            }
        });
    }

    private void restoreStock(Order order, Integer employeeId) {
        order.getItems().forEach(item -> {

            // ── Burger: restore each ingredient
            if (item.getIdBurger() != null) {
                Burger burger = burgerRepository.findById(item.getIdBurger())
                        .orElseThrow(() -> new IllegalStateException(
                                "Hamburguesa no encontrada: " + item.getIdBurger()));

                burger.getIngredients().forEach(snapshot -> {
                    Product product = productRepository.findByIdForUpdate(snapshot.getIdProduct())
                            .orElseThrow(() -> new ProductNotFoundException(snapshot.getIdProduct()));

                    int totalQuantity = snapshot.getQuantity() * item.getQuantity();
                    product.adjustStock(+totalQuantity, employeeId);
                    productRepository.save(product);
                });
            }

            // ── Direct product: restore the product stock
            if (item.getIdProduct() != null) {
                Product product = productRepository.findByIdForUpdate(item.getIdProduct())
                        .orElseThrow(() -> new ProductNotFoundException(item.getIdProduct()));

                product.adjustStock(+item.getQuantity(), employeeId);
                productRepository.save(product);
            }
        });
    }

    private String toSpanish(OrderStatus status) {
        return switch (status) {
            case PENDING              -> "Pendiente";
            case ACCEPTED             -> "Aceptada";
            case IN_PROGRESS          -> "En progreso";
            case COMPLETED            -> "Completada";
            case CANCELLED_BY_EMPLOYEE -> "Cancelada";
        };
    }

    private void validateTransition(OrderStatus current, OrderStatus next) {
        boolean valid = switch (current) {
            case PENDING     -> next == OrderStatus.ACCEPTED || next == OrderStatus.CANCELLED_BY_EMPLOYEE;
            case ACCEPTED    -> next == OrderStatus.IN_PROGRESS || next == OrderStatus.CANCELLED_BY_EMPLOYEE;
            case IN_PROGRESS -> next == OrderStatus.COMPLETED;
            case COMPLETED, CANCELLED_BY_EMPLOYEE -> false;
        };

        if (!valid) {
            throw new IllegalStateException(
                    "Transición inválida: " + toSpanish(current) + " → " + toSpanish(next));
        }
    }
}