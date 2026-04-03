package com.tetris.tetrisburger_backend.application.usecase.order;

import com.tetris.tetrisburger_backend.domain.enums.OrderItemType;
import com.tetris.tetrisburger_backend.domain.exception.CartValidationException;
import com.tetris.tetrisburger_backend.domain.exception.PhoneRequiredException;
import com.tetris.tetrisburger_backend.domain.model.Order;
import com.tetris.tetrisburger_backend.domain.model.OrderItem;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.order.CreateOrder;
import com.tetris.tetrisburger_backend.domain.port.out.CartRepository;
import com.tetris.tetrisburger_backend.domain.port.out.OrderRepository;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import com.tetris.tetrisburger_backend.application.usecase.product.AdjustProductStockUseCase;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.cart.CartItemRequestDTO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CreateOrderUseCase implements CreateOrder {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AdjustProductStockUseCase adjustProductStockUseCase;

    public CreateOrderUseCase(
            OrderRepository orderRepository,
            CartRepository cartRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            AdjustProductStockUseCase adjustProductStockUseCase
    ) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.adjustProductStockUseCase = adjustProductStockUseCase;
    }

    @Override
    public Order handle(Integer idUser, List<CartItemRequestDTO> cartItems) {

        // Validar usuario
        User user = userRepository.findUserById(idUser)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Validar teléfono
        if (user.getPhone() == null || user.getPhone().isBlank())
            throw new PhoneRequiredException("El usuario debe tener un número de teléfono");

        // Revalidar todos los ítems antes de confirmar
        List<String> errors = new ArrayList<>();

        cartItems.stream()
                .filter(item -> OrderItemType.PRODUCT.name().equals(item.typeProduct().name()))
                .forEach(item -> productRepository.findById(item.idProduct()).ifPresentOrElse(
                        product -> {
                            if (!product.isAvailable())
                                errors.add("'" + item.name() + "' no está disponible");
                            else if (product.getQuantity() < item.quantity())
                                errors.add("'" + item.name() + "' solo tiene " +
                                        product.getQuantity() + " unidades disponibles");
                        },
                        () -> errors.add("'" + item.name() + "' ya no existe en el catálogo")
                ));

        if (!errors.isEmpty())
            throw new CartValidationException(errors);

        // Convertir items
        List<OrderItem> orderItems = cartItems.stream()
                .map(this::toOrderItem)
                .toList();

        // Descontar stock con lock atómico
        cartItems.stream()
                .filter(item -> OrderItemType.PRODUCT.name().equals(item.typeProduct().name()))
                .forEach(item -> adjustProductStockUseCase.adjustStock(
                        item.idProduct(),
                        -item.quantity(),
                        idUser
                ));

        // Contador de órdenes del día
        long dailyCount = orderRepository.maxDailySequence(LocalDate.now());
        Order order = Order.create(idUser, orderItems, dailyCount);

        // Guardar con retry por race condition
        Order saved;
        try {
            saved = orderRepository.save(order);
        } catch (DataIntegrityViolationException e) {
            long newCount = orderRepository.maxDailySequence(LocalDate.now());
            order.setOrderNumber(String.format("ORD-%s-%03d",
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    newCount + 1));
            saved = orderRepository.save(order);
        }

        // Limpiar carrito
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