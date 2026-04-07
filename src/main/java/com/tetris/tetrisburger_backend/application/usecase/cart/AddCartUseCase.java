package com.tetris.tetrisburger_backend.application.usecase.cart;

import com.tetris.tetrisburger_backend.domain.exception.InsufficientStockException;
import com.tetris.tetrisburger_backend.domain.exception.InvalidCartItemException;
import com.tetris.tetrisburger_backend.domain.exception.ProductNotAvailableException;
import com.tetris.tetrisburger_backend.domain.model.Addition;
import com.tetris.tetrisburger_backend.domain.model.Cart;
import com.tetris.tetrisburger_backend.domain.model.CartItem;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.cart.AddCart;
import com.tetris.tetrisburger_backend.domain.port.out.AdditionRepository;
import com.tetris.tetrisburger_backend.domain.port.out.CartRepository;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class AddCartUseCase implements AddCart {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final AdditionRepository additionRepository;

    public AddCartUseCase(
            CartRepository cartRepository,
            ProductRepository productRepository,
            AdditionRepository additionRepository
    ) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.additionRepository = additionRepository;
    }

    @Override
    public Cart handle(Integer idUser, List<CartItem> items) {

        try {
            Cart cart = cartRepository.findByUserId(idUser)
                    .orElseGet(() -> Cart.createForUser(idUser));

            cart.validateItems(items);

            items.stream()
                    .filter(CartItem::isProduct)
                    .forEach(item -> {
                        Product product = productRepository.findById(item.getIdItem())
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "Producto no encontrado: " + item.getIdItem()));

                        if (!product.isAvailable()) {
                            throw new ProductNotAvailableException(
                                    "'" + item.getName() + "' no está disponible en este momento");
                        }
                        if (product.getQuantity() < item.getQuantity()) {
                            throw new InsufficientStockException(
                                    "Stock insuficiente para '" + item.getName() +
                                            "'. Disponible: " + product.getQuantity());
                        }
                    });

            items.stream()
                    .filter(item -> item.getItemType() == CartItem.ItemType.ADDITION) // ajusta si el nombre es distinto
                    .forEach(item -> {
                        Addition addition = additionRepository.findById(item.getIdItem())
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "Adición no encontrada: " + item.getIdItem()));

                        if (!addition.isActive()) {
                            throw new ProductNotAvailableException(
                                    "'" + item.getName() + "' no está disponible en este momento");
                        }
                    });

            if (cart.getIdCart() == null) {
                cart = cartRepository.save(cart);
            }

            List<CartItem> validatedItems = items.stream()
                    .map(item -> CartItem.fromSync(
                            item.getItemType(),
                            item.getIdItem(),
                            item.getName(),
                            item.getImageUrl(),
                            item.getUnitPrice(),
                            item.getQuantity()
                    ))
                    .toList();

            cartRepository.replaceItems(cart.getIdCart(), validatedItems);
            cart.sync(validatedItems);

            return cart;

        } catch (InvalidCartItemException | ProductNotAvailableException | InsufficientStockException e) {
            throw e;
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new InvalidCartItemException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidCartItemException("Error sincronizando carrito: " + e.getMessage());
        }
    }
}