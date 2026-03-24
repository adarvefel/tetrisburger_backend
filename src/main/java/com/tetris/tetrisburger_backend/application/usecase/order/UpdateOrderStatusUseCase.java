package com.tetris.tetrisburger_backend.application.usecase.order;

import com.tetris.tetrisburger_backend.domain.enums.OrderStatus;
import com.tetris.tetrisburger_backend.domain.exception.OrderNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Order;
import com.tetris.tetrisburger_backend.domain.port.in.order.UpdateOrderStatus;
import com.tetris.tetrisburger_backend.domain.port.out.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateOrderStatusUseCase implements UpdateOrderStatus {

    private final OrderRepository orderRepository;

    public UpdateOrderStatusUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order handle(Integer idOrder, OrderStatus newStatus, Integer employeeId) {
        Order order = orderRepository.findById(idOrder)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Orden no encontrada: " + idOrder));

        order.updateStatus(newStatus, employeeId);
        return orderRepository.save(order);
    }
}