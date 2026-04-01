package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.model.Payment;
import com.tetris.tetrisburger_backend.domain.port.in.payment.CreatePayment;
import com.tetris.tetrisburger_backend.domain.port.in.payment.command.CreatePaymentCommand;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.payment.CreatePaymentRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.payment.PaymentResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.PaymentRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final CreatePayment createPayment;
    private final PaymentRestDtoMapper mapper;

    public PaymentController(CreatePayment createPayment, PaymentRestDtoMapper mapper) {
        this.createPayment = createPayment;
        this.mapper = mapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<PaymentResponseDTO> create(
            @Valid @RequestBody CreatePaymentRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails user) {

        CreatePaymentCommand command = new CreatePaymentCommand(
                dto.idOrder(),
                user.getId(),
                dto.paymentMethod()
        );

        Payment payment = createPayment.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponseDTO(payment));
    }
}
