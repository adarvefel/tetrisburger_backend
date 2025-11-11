package com.tetris.tetrisburger_backend.infrastructure.rest.mapper;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.model.Supplier;
import com.tetris.tetrisburger_backend.domain.port.in.supplier.command.CreateSupplierCommand;
import com.tetris.tetrisburger_backend.domain.port.in.supplier.command.UpdateSupplierCommand;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.supplier.CreateSupplierRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.supplier.ListSupplierResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.supplier.SupplierResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.supplier.UpdateSupplierRequestDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SupplierRestDtoMapper {
    default CreateSupplierCommand toCreateCommand(CreateSupplierRequestDTO dto) {
        if (dto == null) return null;
        return new CreateSupplierCommand(dto.getName(), dto.getPhone(), dto.getEmail(), dto.getAddress(), dto.getRegistrationDate());
    }

    default UpdateSupplierCommand toUpdateCommand(Integer id, UpdateSupplierRequestDTO dto) {
        if (dto == null) return null;
        return new UpdateSupplierCommand(id, dto.getName(), dto.getPhone(), dto.getEmail(), dto.getAddress(), dto.getRegistrationDate());
    }

    SupplierResponseDTO toResponseDTO(Supplier s);

    List<SupplierResponseDTO> toResponseDTOList(List<Supplier> list);

    default ListSupplierResponseDTO toListResponseDTO(PageResponse<Supplier> page) {
        if (page == null) return null;
        return ListSupplierResponseDTO.builder()
                .items(toResponseDTOList(page.content()))
                .page(page.page())
                .size(page.size())
                .totalElements(page.totalElements())
                .totalPages(page.totalPages())
                .build();
    }
}
