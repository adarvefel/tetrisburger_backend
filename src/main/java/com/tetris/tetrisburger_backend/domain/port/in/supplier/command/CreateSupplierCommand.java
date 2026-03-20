package com.tetris.tetrisburger_backend.domain.port.in.supplier.command;

import java.time.LocalDate;

public record CreateSupplierCommand(
        String name, String phone, String email, String address, LocalDate registrationDate
) {
}