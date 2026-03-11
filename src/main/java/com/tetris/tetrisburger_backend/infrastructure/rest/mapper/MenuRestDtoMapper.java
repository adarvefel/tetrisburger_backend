package com.tetris.tetrisburger_backend.infrastructure.rest.mapper;

import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.ImageStatus;
import com.tetris.tetrisburger_backend.domain.model.Menu;
import com.tetris.tetrisburger_backend.domain.model.MenuItem;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.menu.command.CreateMenuCommand;
import com.tetris.tetrisburger_backend.domain.port.in.menu.command.MenuItemCommand;
import com.tetris.tetrisburger_backend.domain.port.in.menu.command.UpdateMenuCommand;
import com.tetris.tetrisburger_backend.domain.port.in.menu.command.UpdateMenuImageCommand;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.menu.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {
                BurgerRestDtoMapper.class,
                ProductRestDtoMapper.class
        }
)
public interface MenuRestDtoMapper {
    @Mapping(target = "imageStatus", expression = "java(resolveImageStatus(menu.getImageUrl()))")
    @Mapping(target = "menuCategory", source = "menuCategory")
    @Mapping(target = "isAvailable", source = "available")
    MenuResponseDTO toResponseDTO(Menu menu);


    @Mapping(target = "burger", source = "burger")
    @Mapping(target = "product", source = "product")
    MenuItemResponseDTO toItemResponseDTO(MenuItem item);


    MenuItemCommand toItemCommand(MenuItemRequestDTO itemDTO);

    default CreateMenuCommand toCreateCommand(CreateMenuRequestDTO dto,
                                              MultipartFile image,
                                              Integer createdBy) {
        if (dto == null) return null;

        FileData imageData = null;
        if (image != null && !image.isEmpty()) {
            try {
                imageData = new FileData(
                        image.getOriginalFilename(),
                        image.getContentType(),
                        image.getBytes()
                );
            } catch (IOException e) {
                throw new RuntimeException("Error al procesar imagen", e);
            }
        }

        List<MenuItemCommand> items = dto.items() != null
                ? dto.items().stream().map(this::toItemCommand).toList()
                : List.of();

        return new CreateMenuCommand(
                dto.name(),
                dto.description(),
                dto.isAvailable(),
                dto.idMenuCategory(),
                items,
                imageData,
                createdBy
        );
    }

    default UpdateMenuCommand toUpdateCommand(Integer id,
                                              UpdateMenuRequestDTO dto,
                                              Integer updatedBy) {
        if (dto == null) return null;

        List<MenuItemCommand> items = dto.items() != null
                ? dto.items().stream().map(this::toItemCommand).toList()
                : List.of();

        return new UpdateMenuCommand(
                id,
                dto.name(),
                dto.description(),
                dto.isAvailable(),
                dto.idMenuCategory(),
                items,
                updatedBy
        );
    }


    @Mapping(target = "idProduct", source = "product.id")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "imageUrl", source = "product.imageUrl")
    ProductSummaryDTO toProductSummaryDTO(Product product);

    default UpdateMenuImageCommand toUpdateImageCommand(Integer id,
                                                        MultipartFile image,
                                                        Integer updatedBy) {
        if (image == null || image.isEmpty()) return null;

        try {
            FileData imageData = new FileData(
                    image.getOriginalFilename(),
                    image.getContentType(),
                    image.getBytes()
            );
            return new UpdateMenuImageCommand(id, imageData, updatedBy);
        } catch (IOException e) {
            throw new RuntimeException("Error al procesar imagen", e);
        }
    }

    default ImageStatus resolveImageStatus(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return ImageStatus.NONE;
        return ImageStatus.UPLOADED;
    }
}