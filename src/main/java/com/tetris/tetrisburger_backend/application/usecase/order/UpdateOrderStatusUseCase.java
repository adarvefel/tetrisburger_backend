package com.tetris.tetrisburger_backend.application.usecase.order;

import com.tetris.tetrisburger_backend.domain.enums.OrderStatus;
import com.tetris.tetrisburger_backend.domain.exception.OrderNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Order;
import com.tetris.tetrisburger_backend.domain.model.Payment;
import com.tetris.tetrisburger_backend.domain.port.in.order.UpdateOrderStatus;
import com.tetris.tetrisburger_backend.domain.port.out.InvoicePort;
import com.tetris.tetrisburger_backend.domain.port.out.OrderRepository;
import com.tetris.tetrisburger_backend.domain.port.out.PaymentRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
public class UpdateOrderStatusUseCase implements UpdateOrderStatus {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final InvoicePort invoicePort;
    private final SimpMessagingTemplate messagingTemplate;

    public UpdateOrderStatusUseCase(OrderRepository orderRepository,
                                    PaymentRepository paymentRepository,
                                    InvoicePort invoicePort,
                                    SimpMessagingTemplate messagingTemplate) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.invoicePort = invoicePort;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public Order handle(Integer idOrder, OrderStatus newStatus, Integer employeeId) {
        Order order = orderRepository.findById(idOrder)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Orden no encontrada: " + idOrder));

        validateTransition(order.getStatus(), newStatus);

        // ── Cancelación — no requiere pago ──────────────────────────
        if (newStatus == OrderStatus.CANCELLED_BY_EMPLOYEE) {
            order.updateStatus(newStatus, employeeId);
            Order saved = orderRepository.save(order);
            messagingTemplate.convertAndSend(
                    "/topic/orders/" + order.getIdUser(),
                    Map.of("orderId", idOrder, "status", "CANCELADA",
                            "mensaje", "Tu orden ha sido cancelada por un empleado")
            );
            return saved;
        }

        // ── Aceptar — requiere pago registrado ──────────────────────
        if (newStatus == OrderStatus.ACCEPTED) {
            Payment payment = paymentRepository.findByOrderId(idOrder)
                    .orElseThrow(() -> new IllegalStateException(
                            "Se requiere registrar el pago antes de aceptar la orden"));

            order.updateStatus(newStatus, employeeId);
            Order saved = orderRepository.save(order);
            invoicePort.createInvoice(saved, payment);
            return saved;
        }

        order.updateStatus(newStatus, employeeId);
        return orderRepository.save(order);
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
                    "Transición inválida: " + current + " → " + next);
        }
    }
}