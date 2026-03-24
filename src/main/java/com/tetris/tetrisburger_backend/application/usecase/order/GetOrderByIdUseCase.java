package com.tetris.tetrisburger_backend.application.usecase.order;

import com.tetris.tetrisburger_backend.domain.exception.OrderNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Order;
import com.tetris.tetrisburger_backend.domain.port.in.order.GetOrderById;
import com.tetris.tetrisburger_backend.domain.port.out.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetOrderByIdUseCase implements GetOrderById {

    private final OrderRepository orderRepository;

    public GetOrderByIdUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order handle(Integer idOrder, Integer idUser) {
        Order order = orderRepository.findById(idOrder)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Orden no encontrada: " + idOrder));

        if (!order.getIdUser().equals(idUser))
            throw new OrderNotFoundException(
                    "La orden no pertenece a este usuario");

        return order;
    }
}