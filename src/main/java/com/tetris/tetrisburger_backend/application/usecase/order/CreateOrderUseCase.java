package com.tetris.tetrisburger_backend.application.usecase.order;

import com.tetris.tetrisburger_backend.domain.enums.OrderItemType;
import com.tetris.tetrisburger_backend.domain.exception.PhoneRequiredException;
import com.tetris.tetrisburger_backend.domain.model.Order;
import com.tetris.tetrisburger_backend.domain.model.OrderItem;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.order.CreateOrder;
import com.tetris.tetrisburger_backend.domain.port.out.CartRepository;
import com.tetris.tetrisburger_backend.domain.port.out.OrderRepository;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.cart.CartItemRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CreateOrderUseCase implements CreateOrder {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    public CreateOrderUseCase(
            OrderRepository orderRepository,
            CartRepository cartRepository,
            UserRepository userRepository
    ) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Order handle(Integer idUser, List<CartItemRequestDTO> cartItems) {

        // ── Validate user ────────────────────────────────────────────
        User user = userRepository.findUserById(idUser)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (user.getPhone() == null || user.getPhone().isBlank())
            throw new PhoneRequiredException("El usuario debe de registrar su numero de celular");


        // ── Convert items ────────────────────────────────────────────
        List<OrderItem> orderItems = cartItems.stream()
                .map(this::toOrderItem)
                .toList();

        // ── Create and save order ────────────────────────────────────
        Order order = Order.create(idUser, orderItems);
        Order saved = orderRepository.save(order);

        // ── Clear cart ───────────────────────────────────────────────
        cartRepository.findByUserId(idUser)
                .ifPresent(cart -> cartRepository.deleteItemsByCartId(cart.getIdCart()));

        return saved;
    }

    private OrderItem toOrderItem(CartItemRequestDTO dto) {
        OrderItemType type = switch (dto.typeProduct().name()) {
            case "BURGER" -> OrderItemType.BURGER;
            case "PRODUCT" -> OrderItemType.PRODUCT;
            case "ADDITION" -> OrderItemType.ADDITION;
            default -> throw new IllegalArgumentException("Tipo desconocido: " + dto.typeProduct());
        };

        return OrderItem.create(
                type,
                type == OrderItemType.BURGER ? dto.idProduct() : null,
                type != OrderItemType.BURGER ? dto.idProduct() : null,
                dto.name(),
                dto.quantity(),
                dto.price()
        );
    }
}