package com.tetris.tetrisburger_backend.application.usecase.order;

import com.tetris.tetrisburger_backend.domain.enums.OrderStatus;
import com.tetris.tetrisburger_backend.domain.exception.OrderNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Order;
import com.tetris.tetrisburger_backend.domain.model.Payment;
import com.tetris.tetrisburger_backend.domain.port.in.order.UpdateOrderStatus;
import com.tetris.tetrisburger_backend.domain.port.out.InvoicePort;
import com.tetris.tetrisburger_backend.domain.port.out.OrderRepository;
import com.tetris.tetrisburger_backend.domain.port.out.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateOrderStatusUseCase implements UpdateOrderStatus {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final InvoicePort invoicePort;

    public UpdateOrderStatusUseCase(OrderRepository orderRepository,
                                    PaymentRepository paymentRepository,
                                    InvoicePort invoicePort) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.invoicePort = invoicePort;
    }

    @Override
    public Order handle(Integer idOrder, OrderStatus newStatus, Integer employeeId) {
        Order order = orderRepository.findById(idOrder)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Orden no encontrada: " + idOrder));

        validateTransition(order.getStatus(), newStatus);

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
            case PENDING    -> next == OrderStatus.ACCEPTED;
            case ACCEPTED   -> next == OrderStatus.IN_PROGRESS;
            case IN_PROGRESS -> next == OrderStatus.COMPLETED;
            default         -> false;
        };

        if (!valid) {
            throw new IllegalStateException(
                    "Transición inválida: " + current + " → " + next);
        }
    }
}