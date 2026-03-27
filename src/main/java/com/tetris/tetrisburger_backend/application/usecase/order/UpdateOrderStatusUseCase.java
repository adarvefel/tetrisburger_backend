package com.tetris.tetrisburger_backend.application.usecase.order;

import com.tetris.tetrisburger_backend.domain.enums.OrderStatus;
import com.tetris.tetrisburger_backend.domain.enums.PaymentMethod;
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
    public Order handle(Integer idOrder, OrderStatus newStatus,
                        Integer employeeId, PaymentMethod paymentMethod) {
        Order order = orderRepository.findById(idOrder)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Orden no encontrada: " + idOrder));

        if (newStatus == OrderStatus.ACCEPTED && paymentMethod == null) {
            throw new IllegalArgumentException(
                    "Se requiere el método de pago al aceptar la orden");
        }

        order.updateStatus(newStatus, employeeId);
        Order saved = orderRepository.save(order);


        if (newStatus == OrderStatus.ACCEPTED) {
            if (paymentMethod == null) {
                throw new IllegalArgumentException(
                        "Se requiere el método de pago al aceptar la orden");
            }
            Payment payment = Payment.create(
                    saved.getIdOrder(),
                    employeeId,
                    paymentMethod,
                    saved.getTotalAmount(),
                    saved.getTotalAmount()
            );
            Payment savedPayment = paymentRepository.save(payment);
            invoicePort.createInvoice(saved, savedPayment);
        }

        return saved;
    }
}