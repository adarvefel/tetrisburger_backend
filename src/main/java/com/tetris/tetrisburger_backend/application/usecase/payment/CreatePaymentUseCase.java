package com.tetris.tetrisburger_backend.application.usecase.payment;
import com.tetris.tetrisburger_backend.domain.exception.OrderNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Order;
import com.tetris.tetrisburger_backend.domain.model.Payment;
import com.tetris.tetrisburger_backend.domain.port.in.payment.CreatePayment;
import com.tetris.tetrisburger_backend.domain.port.in.payment.command.CreatePaymentCommand;
import com.tetris.tetrisburger_backend.domain.port.out.OrderRepository;
import com.tetris.tetrisburger_backend.domain.port.out.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



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
                .orElseThrow(() -> new OrderNotFoundException(
                        "Orden no encontrada: " + command.idOrder()));

        if (paymentRepository.findByOrderId(command.idOrder()).isPresent())
            throw new IllegalStateException(
                    "Ya existe un pago registrado para esta orden");

        Payment payment = Payment.create(
                command.idOrder(),
                command.idUser(),
                command.paymentMethod(),
                order.getTotalAmount()
        );

        return paymentRepository.save(payment);
    }
}