package com.tetris.tetrisburger_backend.application.usecase.cart;

import com.tetris.tetrisburger_backend.domain.exception.InvalidCartItemException;
import com.tetris.tetrisburger_backend.domain.model.Cart;
import com.tetris.tetrisburger_backend.domain.model.CartItem;
import com.tetris.tetrisburger_backend.domain.port.in.cart.AddCart;
import com.tetris.tetrisburger_backend.domain.port.out.CartRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class AddCartUseCase implements AddCart {

    private final CartRepository cartRepository;

    public AddCartUseCase(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }
    @Override
    public Cart handle(Integer idUser, List<CartItem> items) {

        try {
            Cart cart = cartRepository.findByUserId(idUser)
                    .orElseGet(() -> Cart.createForUser(idUser));

            cart.validateItems(items);

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

        } catch (InvalidCartItemException e) {
            throw e;
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new InvalidCartItemException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidCartItemException("Error sincronizando carrito:"+ e.getMessage());
        }
    }
}