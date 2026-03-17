package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.model.Cart;
import com.tetris.tetrisburger_backend.domain.port.in.cart.AddCart;
import com.tetris.tetrisburger_backend.domain.port.in.cart.ClearCart;
import com.tetris.tetrisburger_backend.domain.port.in.cart.GetCart;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.cart.CartItemRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.cart.CartItemResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.CartRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final AddCart addCart;
    private final GetCart getCart;
    private final ClearCart clearCart;
    private final CartRestDtoMapper mapper;

    public CartController(AddCart addCart,
                          GetCart getCart,
                          ClearCart clearCart,
                          CartRestDtoMapper mapper) {
        this.addCart = addCart;
        this.getCart = getCart;
        this.clearCart = clearCart;
        this.mapper = mapper;
    }

    @PostMapping("/sync")
    public ResponseEntity<List<CartItemResponseDTO>> sync(
            @RequestBody List<CartItemRequestDTO> items,
            @AuthenticationPrincipal UserDetails userDetails) {

        Cart cart = addCart.handle(
                extractUserId(userDetails),
                mapper.toDomainList(items)
        );
        return ResponseEntity.ok(mapper.toResponseDTOList(cart));
    }

    @GetMapping
    public ResponseEntity<List<CartItemResponseDTO>> get(
            @AuthenticationPrincipal UserDetails userDetails) {

        Cart cart = getCart.handle(extractUserId(userDetails));
        return ResponseEntity.ok(mapper.toResponseDTOList(cart));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clear(
            @AuthenticationPrincipal UserDetails userDetails) {

        clearCart.handle(extractUserId(userDetails));
        return ResponseEntity.noContent().build();
    }

    private Integer extractUserId(UserDetails userDetails) {
        return ((CustomUserDetails) userDetails).getId();
    }
}
