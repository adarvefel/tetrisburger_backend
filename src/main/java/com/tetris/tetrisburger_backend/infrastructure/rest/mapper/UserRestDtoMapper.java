package com.tetris.tetrisburger_backend.infrastructure.rest.mapper;

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

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserRestDtoMapper {

    // ============================================
    // DTO → COMMAND (ENTRADA)
    // ============================================

    RegisterUserCommand toRegisterCommand(RegisterUserRequestDTO dto);

    LoginUserCommand toLoginCommand(LoginRequestDTO dto);

    // Commands con parámetros extra (custom methods)
        default CreateUserByAdminCommand toCreateUserByAdminCommand(
                CreateUserByAdminRequestDTO dto,
                Integer createdBy) {
            if (dto == null) return null;

            return new CreateUserByAdminCommand(
                    dto.userName(),
                    dto.email(),
                    dto.password(),
                    dto.userImage(),
                    stringToRole(dto.role()),
                    dto.phone(),
                    createdBy
            );
        }

    default UpdateProfileUserCommand toUpdateProfileUserCommand(
            Integer idUser,
            UpdateProfileUserRequestDTO dto) {
        if (dto == null) return null;

        return new UpdateProfileUserCommand(
                idUser,
                dto.userName(),
                dto.password(),
                dto.userImage(),
                dto.phone()
        );
    }

    default UpdateUserByAdminCommand toUpdateUserByAdminCommand(
            Integer idUser,
            UpdateUserByAdminRequestDTO dto,
            Integer updatedBy) {
        if (dto == null) return null;

        return new UpdateUserByAdminCommand(
                idUser,
                dto.userName(),
                dto.email(),
                dto.password(),
                dto.userImage(),
                stringToRole(dto.role()),
                dto.phone(),
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
    // DOMAIN USER → RESPONSE DTO (SALIDA)
    // ============================================

    // Mapeo automático (campos con mismo nombre)
    RegisterUserResponseDTO toRegisterResponseDTO(User user);

    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    UserResponseDTO toUserResponseDTO(User user);

    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    List<UserResponseDTO> toUserResponseDTOList(List<User> users);

    default ListUserResponseDTO toListUserResponseDTO(PageResponse<User> pageResponse) {
        if (pageResponse == null) return null;

        return new ListUserResponseDTO(
                toUserResponseDTOList(pageResponse.content()),
                pageResponse.totalElements(),
                pageResponse.totalPages(),
                LocalDateTime.now()
        );
    }

    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    CreateUserByAdminResponseDTO toCreateUserByAdminResponseDTO(User user);

    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    UpdateUserByAdminResponseDTO toUpdateUserByAdminResponseDTO(User user);

    UpdateProfileUserResponseDTO toUpdateProfileUserResponseDTO(User user);

    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    GetUserProfileResponseDTO toGetUserProfileResponseDTO(User user);

    default DeleteProfileUserDTO toDeleteProfileUserDTO(Integer idUser) {
        return new DeleteProfileUserDTO("Perfil eliminado correctamente", idUser);
    }

    default DeleteUserByAdminDTO toDeleteUserByAdminDTO(Integer idUser) {
        return new DeleteUserByAdminDTO("Usuario eliminado correctamente", idUser);
    }



    // ============================================
    // CONVERSIONES DE ENUM
    // ============================================

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
