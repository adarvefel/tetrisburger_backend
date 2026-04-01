package com.tetris.tetrisburger_backend.domain.port.in.supplier.command;

import java.time.LocalDate;

public record UpdateSupplierCommand(
        Integer id,
        String name,
        String phone,
        String email,
        String address,
        Integer updatedBy
) {}