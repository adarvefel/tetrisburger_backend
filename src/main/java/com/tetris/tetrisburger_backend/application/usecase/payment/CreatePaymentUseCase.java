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

        Order order = orderRepository.findById(command.idOrder())
                .orElseThrow(() -> new OrderNotFoundException("Orden no encontrada: " + command.idOrder()));

        if (order.getStatus() != OrderStatus.READY) {
            throw new IllegalStateException(
                    "La orden debe estar en READY. Estado actual: " + order.getStatus());
        }

        BigDecimal amountReceived = resolveAmountReceived(command);

        Payment payment = Payment.create(
                command.idOrder(),
                command.idUser(),
                command.paymentMethod(),
                command.amount(),
                amountReceived
        );
        Payment savedPayment = paymentRepository.save(payment);

        order.updateStatus(OrderStatus.COMPLETED, command.idUser());
        orderRepository.save(order);

        return savedPayment;
    }

    private BigDecimal resolveAmountReceived(CreatePaymentCommand command) {
        if (command.paymentMethod() == PaymentMethod.CASH) {
            return command.amountReceived();
        }
        return command.amount();
    }
}