package com.tetris.tetrisburger_backend.application.usecase.cart;

import com.tetris.tetrisburger_backend.domain.model.Cart;
import com.tetris.tetrisburger_backend.domain.port.in.cart.GetCart;
import com.tetris.tetrisburger_backend.domain.port.out.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetCartUseCase implements GetCart {

    private final CartRepository cartRepository;

    public GetCartUseCase(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public Cart handle(Integer idUser) {
        return cartRepository.findByUserId(idUser)
                .orElseGet(() -> Cart.createForUser(idUser));
    }
}