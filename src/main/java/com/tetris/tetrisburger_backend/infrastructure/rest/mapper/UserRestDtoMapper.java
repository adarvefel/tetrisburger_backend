package com.tetris.tetrisburger_backend.infrastructure.rest.mapper;

import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.model.Role;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.auth.command.LoginUserCommand;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.auth.LoginRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.user.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserRestDtoMapper {

    // ============================================
    // DTO → COMMAND
    // ============================================

    RegisterUserCommand toRegisterCommand(RegisterUserRequestDTO dto);

    LoginUserCommand toLoginCommand(LoginRequestDTO dto);

    default CreateUserByAdminCommand toCreateUserByAdminCommand(
            CreateUserByAdminRequestDTO dto,
            MultipartFile userImage,
            Integer createdBy
    ) {
        if (dto == null) return null;

        return new CreateUserByAdminCommand(
                dto.userName(),
                dto.email(),
                dto.password(),
                userImage,
                stringToRole(dto.role()),
                dto.phone(),
                createdBy
        );
    }

    default UpdateProfileUserCommand toUpdateProfileUserCommand(
            Integer idUser,
            UpdateProfileUserRequestDTO dto
    ) {
        if (dto == null) return null;

        return new UpdateProfileUserCommand(
                idUser,
                dto.userName(),
                dto.password(),
                null,
                dto.phone()
        );
    }

    default UpdateProfileImageCommand toUpdateProfileImageCommand(
            Integer idUser,
            MultipartFile userImage,
            Integer updatedBy
    ) {
        FileData fd = multipartToFileData(userImage);
        if (fd == null || fd.bytes() == null || fd.bytes().length == 0) {
            throw new IllegalArgumentException("userImage es requerido");
        }

        return new UpdateProfileImageCommand(
                idUser,
                fd.bytes(),
                fd.contentType(),
                fd.originalFilename(),
                updatedBy
        );
    }

    default UpdateUserByAdminCommand toUpdateUserByAdminCommand(
            Integer idUser,
            UpdateUserByAdminRequestDTO dto,
            Integer updatedBy
    ) {
        if (dto == null) return null;

        return new UpdateUserByAdminCommand(
                idUser,
                dto.userName(),
                dto.email(),
                dto.password(),
                null,
                stringToRole(dto.role()),
                dto.phone(),
                updatedBy
        );
    }

    default UpdateUserImageByAdminCommand toUpdateUserImageByAdminCommand(
            Integer idUser,
            MultipartFile userImage,
            Integer updatedBy
    ) {
        FileData fd = multipartToFileData(userImage);
        if (fd == null || fd.bytes() == null || fd.bytes().length == 0) {
            throw new IllegalArgumentException("userImage es requerido");
        }

        return new UpdateUserImageByAdminCommand(
                idUser,
                fd.bytes(),
                fd.contentType(),
                fd.originalFilename(),
                updatedBy
        );
    }

    default DeleteProfileUserCommand toDeleteProfileUserCommand(Integer idUser) {
        return new DeleteProfileUserCommand(idUser);
    }

    @Mapping(source = "idUser", target = "idUser")
    @Mapping(source = "adminId", target = "deletedBy")
    DeleteUserByAdminCommand toDeleteUserByAdminCommand(Integer idUser, Integer adminId);

    // ============================================
    // DOMAIN → RESPONSE DTO (salida)
    // ============================================

    RegisterUserResponseDTO toRegisterResponseDTO(User user);

    // --- Legacy (sin URL) ---
    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    UserResponseDTO toUserResponseDTO(User user);

    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    List<UserResponseDTO> toUserResponseDTOList(List<User> users);

    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    CreateUserByAdminResponseDTO toCreateUserByAdminResponseDTO(User user);

    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    UpdateUserByAdminResponseDTO toUpdateUserByAdminResponseDTO(User user);

    UpdateProfileUserResponseDTO toUpdateProfileUserResponseDTO(User user);

    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    GetUserProfileResponseDTO toGetUserProfileResponseDTO(User user);

    // --- Con URL + STATUS (los que usan tus controllers) ---
    @Mapping(source = "user.role", target = "role", qualifiedByName = "roleToString")
    @Mapping(source = "imageUrl", target = "userImage")
    @Mapping(source = "imageStatus", target = "imageStatus")
    UserResponseDTO toUserResponseDTO(User user, String imageUrl, String imageStatus);

    @Mapping(source = "user.role", target = "role", qualifiedByName = "roleToString")
    @Mapping(source = "imageUrl", target = "userImage")
    @Mapping(source = "imageStatus", target = "imageStatus")
    CreateUserByAdminResponseDTO toCreateUserByAdminResponseDTO(User user, String imageUrl, String imageStatus);

    @Mapping(source = "user.role", target = "role", qualifiedByName = "roleToString")
    @Mapping(source = "imageUrl", target = "userImage")
    @Mapping(source = "imageStatus", target = "imageStatus")
    UpdateUserByAdminResponseDTO toUpdateUserByAdminResponseDTO(User user, String imageUrl, String imageStatus);

    @Mapping(source = "imageUrl", target = "userImage")
    @Mapping(source = "imageStatus", target = "imageStatus")
    UpdateProfileUserResponseDTO toUpdateProfileUserResponseDTO(User user, String imageUrl, String imageStatus);

    @Mapping(source = "user.role", target = "role", qualifiedByName = "roleToString")
    @Mapping(source = "imageUrl", target = "userImage")
    @Mapping(source = "imageStatus", target = "imageStatus")
    GetUserProfileResponseDTO toGetUserProfileResponseDTO(User user, String imageUrl, String imageStatus);

    // ============================================
    // Paginación
    // ============================================

    default ListUserResponseDTO toListUserResponseDTO(PageResponse<User> pageResponse) {
        if (pageResponse == null) return null;

        return new ListUserResponseDTO(
                toUserResponseDTOList(pageResponse.content()),
                pageResponse.totalElements(),
                pageResponse.totalPages(),
                LocalDateTime.now()
        );
    }

    default ListUserResponseDTO toListUserResponseDTO(PageResponse<User> pageResponse, List<UserResponseDTO> contentWithUrls) {
        if (pageResponse == null) return null;

        return new ListUserResponseDTO(
                contentWithUrls,
                pageResponse.totalElements(),
                pageResponse.totalPages(),
                LocalDateTime.now()
        );
    }

    // ============================================
    // DELETE DTOS
    // ============================================

    default DeleteProfileUserDTO toDeleteProfileUserDTO(Integer idUser) {
        return new DeleteProfileUserDTO("Perfil eliminado correctamente", idUser);
    }

    default DeleteUserByAdminDTO toDeleteUserByAdminDTO(Integer idUser) {
        return new DeleteUserByAdminDTO("Usuario eliminado correctamente", idUser);
    }

    // ============================================
    // Helpers
    // ============================================

    @Named("multipartToFileData")
    default FileData multipartToFileData(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        try {
            return new FileData(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes()
            );
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el archivo", e);
        }
    }

    @Named("stringToRole")
    default Role stringToRole(String roleStr) {
        if (roleStr == null) return null;
        try {
            return Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Named("roleToString")
    default String roleToString(Role role) {
        return role == null ? null : role.name();
    }
}
