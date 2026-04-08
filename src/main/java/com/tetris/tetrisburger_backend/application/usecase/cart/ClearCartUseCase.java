package com.tetris.tetrisburger_backend.application.usecase.cart;

 import com.tetris.tetrisburger_backend.domain.port.in.cart.ClearCart;
 import com.tetris.tetrisburger_backend.domain.port.out.CartRepository;
 import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ClearCartUseCase implements ClearCart {

    private final CartRepository cartRepository;

    public ClearCartUseCase(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public void handle(Integer idUser) {
        cartRepository.findByUserId(idUser).ifPresent(cart ->
                cartRepository.deleteItemsByCartId(cart.getIdCart())
        );
    }
}