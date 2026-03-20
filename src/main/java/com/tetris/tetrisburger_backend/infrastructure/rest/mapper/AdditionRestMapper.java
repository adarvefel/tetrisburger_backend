package com.tetris.tetrisburger_backend.infrastructure.rest.mapper;

import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.ImageStatus;
import com.tetris.tetrisburger_backend.domain.model.Addition;
import com.tetris.tetrisburger_backend.domain.model.Supplier;
import com.tetris.tetrisburger_backend.domain.port.in.adittion.command.CreateAdditionCommand;
import com.tetris.tetrisburger_backend.domain.port.in.adittion.command.UpdateAdditionCommand;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.addition.AdditionResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.addition.CreateAdditionRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.addition.UpdateAdditionRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.supplier.SupplierResponseDTO;
import org.mapstruct.Mapper;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdditionRestMapper {


    // en AdditionRestDtoMapper.java
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
                addition.getDeletedAt()
        );
    }


    private String resolveImageStatus(boolean imageWasSent, String imageUrl) {
        if (!imageWasSent)                          return "NONE";
        if (imageUrl == null || imageUrl.isBlank()) return "PENDING";
        return "READY";
    }




    default CreateAdditionCommand toCreateAdditionCommand(
            CreateAdditionRequestDTO dto,
            MultipartFile additionImage
    ) {
        if (dto == null) return null;

        FileData imageData = FileData.from(additionImage);

        return new CreateAdditionCommand(
                dto.name(),
                dto.description(),
                dto.price(),
                dto.available(),
                imageData
        );
    }

    default UpdateAdditionCommand toUpdateAdditionCommand(
            Integer id,
            UpdateAdditionRequestDTO dto
    ) {
        if (dto == null) return null;

        return new UpdateAdditionCommand(
                id,
                dto.name(),
                dto.description(),
                dto.price(),
                dto.available()

        );
    }

    List<AdditionResponseDTO> toResponseDTOList(List<Addition> handle);




}
