package com.tetris.tetrisburger_backend.application.usecase.payment;

import com.tetris.tetrisburger_backend.domain.enums.OrderStatus;
import com.tetris.tetrisburger_backend.domain.enums.PaymentMethod;
import com.tetris.tetrisburger_backend.domain.exception.OrderNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Order;
import com.tetris.tetrisburger_backend.domain.model.Payment;
import com.tetris.tetrisburger_backend.domain.port.in.payment.CreatePayment;
import com.tetris.tetrisburger_backend.domain.port.in.payment.command.CreatePaymentCommand;
import com.tetris.tetrisburger_backend.domain.port.out.InvoicePort;
import com.tetris.tetrisburger_backend.domain.port.out.OrderRepository;
import com.tetris.tetrisburger_backend.domain.port.out.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class CreatePaymentUseCase implements CreatePayment {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public CreatePaymentUseCase(PaymentRepository paymentRepository,
                                OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;

    }

    @Override
    public Payment handle(CreatePaymentCommand command) {

        // 1. Validar que la orden existe y está en READY
        Order order = orderRepository.findById(command.idOrder())
                .orElseThrow(() -> new OrderNotFoundException("Orden no encontrada: " + command.idOrder()));

        if (order.getStatus() != OrderStatus.READY) {
            throw new IllegalStateException(
                    "La orden debe estar en READY. Estado actual: " + order.getStatus());
        }

        // 2. Resolver amountReceived según método de pago
        // Para CARD/TRANSFER no hay cambio → amountReceived = amount
        BigDecimal amountReceived = resolveAmountReceived(command);

        // 3. Payment.create() valida y calcula changeAmount internamente
        Payment payment = Payment.create(
                command.idOrder(),
                command.idUser(),
                command.paymentMethod(),
                command.amount(),
                amountReceived
        );
        Payment savedPayment = paymentRepository.save(payment);

        // 4. Marcar orden como COMPLETED
        order.updateStatus(OrderStatus.COMPLETED, command.idUser());
        orderRepository.save(order);
        return savedPayment;

    }

    private BigDecimal resolveAmountReceived(CreatePaymentCommand command) {
        if (command.paymentMethod() == PaymentMethod.CASH) {
            // Payment.create() lanzará IllegalArgumentException si es null o insuficiente
            return command.amountReceived();
        }
        // CARD / TRANSFER: el monto recibido es exactamente el total
        return command.amount();
    }
}
