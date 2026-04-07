package com.tetris.tetrisburger_backend.application.usecase.order;

import com.tetris.tetrisburger_backend.domain.enums.OrderItemType;
import com.tetris.tetrisburger_backend.domain.exception.PhoneRequiredException;
import com.tetris.tetrisburger_backend.domain.exception.ProductNotAvailableException;
import com.tetris.tetrisburger_backend.domain.model.*;
import com.tetris.tetrisburger_backend.domain.port.in.order.CreateOrder;
import com.tetris.tetrisburger_backend.domain.port.out.*;
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
    private final ProductRepository productRepository;   // ← AGREGA
    private final AdditionRepository additionRepository;
    private final BurgerRepository burgerRepository; // ← AGREGA


    public CreateOrderUseCase(
            OrderRepository orderRepository,
            CartRepository cartRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            AdditionRepository additionRepository,
            BurgerRepository burgerRepository // ← AGREGA
    ) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.additionRepository = additionRepository;
        this.burgerRepository = burgerRepository; // ← AGREGA
    }

    @Override
    public Order handle(Integer idUser, List<CartItemRequestDTO> cartItems) {

        // ── Validate user ────────────────────────────────────────────
        User user = userRepository.findUserById(idUser)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (user.getPhone() == null || user.getPhone().isBlank())
            throw new PhoneRequiredException("El usuario debe de registrar su numero de celular");

        // ── Validate items ───────────────────────────────────────────
        cartItems.forEach(item -> {
            switch (item.typeProduct().name()) {

                case "PRODUCT" -> {
                    Product product = productRepository.findById(item.idProduct())
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Producto no encontrado: " + item.name()));

                    if (!product.isAvailable())
                        throw new ProductNotAvailableException(
                                "'" + item.name() + "' no está disponible en este momento");


                }

                case "ADDITION" -> {
                    Addition addition = additionRepository.findById(item.idProduct())
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Adición no encontrada: " + item.name()));

                    if (!addition.isActive())
                        throw new ProductNotAvailableException(
                                "'" + item.name() + "' no está disponible en este momento");
                }
                case "BURGER" -> {
                    Burger burger = burgerRepository.findActiveById(item.idProduct())
                            .orElseThrow(() -> new ProductNotAvailableException(
                                    "'" + item.name() + "' no está disponible en este momento"));

                    if (!burger.canBeOrdered())
                        throw new ProductNotAvailableException(
                                "'" + item.name() + "' no está disponible en este momento");

                    burger.getIngredients().forEach(ingredient -> {
                        Product product = productRepository.findById(ingredient.getIdProduct())
                                .orElseThrow(() -> new ProductNotAvailableException(
                                        "'" + item.name() + "' no puede ordenarse porque el ingrediente '"
                                                + ingredient.getProductName() + "' ya no existe"));

                        if (!product.isAvailable())
                            throw new ProductNotAvailableException(
                                    "'" + item.name() + "' no puede ordenarse porque el ingrediente '"
                                            + ingredient.getProductName() + "' no está disponible");
                    });
                }
            }
        });

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
            case "BURGER"   -> OrderItemType.BURGER;
            case "PRODUCT"  -> OrderItemType.PRODUCT;
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