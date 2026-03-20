package com.tetris.tetrisburger_backend.domain.port.in.menu.command;

import com.tetris.tetrisburger_backend.domain.common.FileData;

public record UpdateMenuImageCommand(
        Integer idMenu,
        FileData imageData,
        Integer updatedBy
) {}