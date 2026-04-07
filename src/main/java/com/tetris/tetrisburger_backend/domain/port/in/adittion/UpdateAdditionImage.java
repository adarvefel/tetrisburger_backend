package com.tetris.tetrisburger_backend.domain.port.in.adittion;

import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.model.Addition;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

public interface UpdateAdditionImage {
    Addition handle(Integer id, FileData additionImage,Integer updatedBy);


}
