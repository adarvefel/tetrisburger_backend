package com.tetris.tetrisburger_backend.infrastructure.rest.mapper;

import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.ImageStatus;
import com.tetris.tetrisburger_backend.domain.model.Addition;
import com.tetris.tetrisburger_backend.domain.port.in.adittion.command.CreateAdditionCommand;
import com.tetris.tetrisburger_backend.domain.port.in.adittion.command.UpdateAdditionCommand;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.addition.AdditionResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.addition.CreateAdditionRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.addition.UpdateAdditionRequestDTO;
import org.mapstruct.Mapper;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdditionRestDtoMapper {

    default AdditionResponseDTO toAdditionResponseDTO(Addition addition, boolean imageWasSent) {
        if (addition == null) return null;

        ImageStatus imageStatus;
        if (!imageWasSent) {
            imageStatus = ImageStatus.NONE;
        } else if (addition.getImageUrl() == null || addition.getImageUrl().isBlank()) {
            imageStatus = ImageStatus.PENDING;
        } else {
            imageStatus = ImageStatus.UPLOADED;
        }

        return new AdditionResponseDTO(
                addition.getIdAddition(),
                addition.getName(),
                addition.getDescription(),
                addition.getPrice(),
                addition.getAvailable(),
                addition.getImageUrl(),
                addition.getImageKey(),
                imageStatus,
                addition.getCreatedAt(),
                addition.getUpdatedAt(),
                addition.getCreatedBy(),
                addition.getUpdatedBy()
        );
    }
    default CreateAdditionCommand toCreateAdditionCommand(
            CreateAdditionRequestDTO dto,
            MultipartFile additionImage,
            Integer userId
    ) {
        if (dto == null) return null;

        FileData imageData = FileData.from(additionImage);

        return new CreateAdditionCommand(
                dto.name(),
                dto.description(),
                dto.price(),
                dto.available(),
                imageData,
                userId
        );
    }

    default UpdateAdditionCommand toUpdateAdditionCommand(
            Integer id,
            UpdateAdditionRequestDTO dto,
            Integer userId
    ) {
        if (dto == null) return null;

        return new UpdateAdditionCommand(
                id,
                dto.name(),
                dto.description(),
                dto.price(),
                dto.available(),
                userId

        );
    }

    default List<AdditionResponseDTO> toResponseDTOList(List<Addition> additions) {
        if (additions == null || additions.isEmpty()) {
            return List.of();
        }
        return additions.stream()
                .map(addition -> toAdditionResponseDTO(addition, false))
                .toList();
    }
}
