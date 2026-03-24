package com.tetris.tetrisburger_backend.application.usecase.order;

import com.tetris.tetrisburger_backend.domain.enums.OrderItemType;
import com.tetris.tetrisburger_backend.domain.exception.PhoneRequiredException;
import com.tetris.tetrisburger_backend.domain.model.Order;
import com.tetris.tetrisburger_backend.domain.model.OrderItem;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.order.CreateOrder;
import com.tetris.tetrisburger_backend.domain.port.out.CartRepository;
import com.tetris.tetrisburger_backend.domain.port.out.OrderRepository;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.cart.CartItemRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class CreateOrderUseCase implements CreateOrder {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CreateOrderUseCase(
            OrderRepository orderRepository,
            CartRepository cartRepository,
            ProductRepository productRepository,
            UserRepository userRepository
    ) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Order handle(Integer idUser, List<CartItemRequestDTO> cartItems) {

        // Validar usuario y teléfono
        User user = userRepository.findUserById(idUser)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

// Log temporal
        System.out.println(">>> phone del usuario: " + user.getPhone());
        System.out.println(">>> idUser: " + user.getIdUser());

        if (user.getPhone() == null || user.getPhone().isBlank())
            throw new PhoneRequiredException("El usuario debe de tener un numero de telefono");
        // Convertir items
        List<OrderItem> orderItems = cartItems.stream()
                .map(this::toOrderItem)
                .toList();

        // Restar stock de productos
        cartItems.stream()
                .filter(item -> "PRODUCT".equals(item.typeProduct().name()))
                .forEach(item -> {
                    Product product = productRepository.findById(item.idProduct())
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Producto no encontrado: " + item.idProduct()));
                    product.adjustStock(-item.quantity(), idUser);
                    productRepository.save(product);
                });

        // Contador de órdenes del día
        long dailyCount = orderRepository.countByOrderDate(LocalDate.now());

        // Crear y guardar orden
        Order order = Order.create(idUser, orderItems, dailyCount);
        Order saved = orderRepository.save(order);

        // Limpiar carrito
        cartRepository.findByUserId(idUser)
                .ifPresent(cart -> cartRepository.deleteItemsByCartId(cart.getIdCart()));

        // Notificar al negocio con datos del cliente

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