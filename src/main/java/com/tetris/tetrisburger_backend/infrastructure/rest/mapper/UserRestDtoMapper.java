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

/**
 * Mapper para convertir DTOs ↔ Commands ↔ Domain Models
 *
 * Flujo entrada:  RequestDTO → Command
 * Flujo salida:   User (Domain) → ResponseDTO
 */
@Mapper(componentModel = "spring")
public interface UserRestDtoMapper {

    // ============================================
    // DTO → COMMAND (ENTRADA)
    // ============================================

    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "password", target = "password")
    RegisterUserCommand toRegisterCommand(RegisterUserRequestDTO dto);

    @Mapping(source = "email", target = "email")
    @Mapping(source = "password", target = "password")
    LoginUserCommand toLoginCommand(LoginRequestDTO dto);



    default CreateUserByAdminCommand toCreateUserByAdminCommand(
            CreateUserByAdminRequestDTO dto) {
        if (dto == null) return null;

        return new CreateUserByAdminCommand(
                dto.userName(),
                dto.email(),
                dto.password(),
                dto.userImage(),
                stringToRole(dto.role()),
                dto.phone()

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
            UpdateUserByAdminRequestDTO dto) {
        if (dto == null) return null;

        return new UpdateUserByAdminCommand(
                idUser,
                dto.userName(),
                dto.email(),
                dto.password(),
                dto.userImage(),
                stringToRole(dto.role()),
                dto.phone()

        );
    }

    default DeleteProfileUserCommand toDeleteProfileUserCommand(Integer idUser) {
        return new DeleteProfileUserCommand(idUser);
    }

    @Mapping(source = "idUser", target = "idUser")
    @Mapping(source = "adminId", target = "deletedBy")
    DeleteUserByAdminCommand toDeleteUserByAdminCommand(
            Integer idUser,
            Integer adminId);


    // ============================================
    // DOMAIN USER → RESPONSE DTO (SALIDA)
    // ============================================

    // Respuesta de registro
    @Mapping(source = "idUser", target = "idUser")
    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "userImage", target = "userImage")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "createdAt", target = "createdAt")
    RegisterUserResponseDTO toRegisterResponseDTO(User user);

    @Mapping(source = "idUser", target = "idUser")
    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "userImage", target = "userImage")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    UserResponseDTO toUserResponseDTO(User user);
    // Mapea lista de usuarios (helper method)
    @Mapping(source = "idUser", target = "id")
    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    List<UserResponseDTO> toUserResponseDTOList(List<User> users);

    //  Mapea PageResponse<User> → ListUserResponseDTO
    default ListUserResponseDTO toListUserResponseDTO(PageResponse<User> pageResponse) {
        if (pageResponse == null) return null;

        List<UserResponseDTO> userDTOs = toUserResponseDTOList(pageResponse.content());

        return new ListUserResponseDTO(
                userDTOs,
                pageResponse.totalElements(),
                pageResponse.totalPages(),
                LocalDateTime.now()
        );
    }

    // Respuesta de admin creando usuario (con auditoría)
    @Mapping(source = "idUser", target = "idUser")
    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "userImage", target = "userImage")
    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "updatedBy", target = "updatedBy")
    CreateUserByAdminResponseDTO toCreateUserByAdminResponseDTO(User user);

    // Respuesta de admin actualizando usuario (con auditoría)
    @Mapping(source = "idUser", target = "idUser")
    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "userImage", target = "userImage")
    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "updatedBy", target = "updatedBy")
    UpdateUserByAdminResponseDTO toUpdateUserByAdminResponseDTO(User user);

    // Respuesta de usuario actualizando su perfil
    @Mapping(source = "idUser", target = "idUser")
    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "userImage", target = "userImage")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "updatedBy", target = "updatedBy")
    UpdateProfileUserResponseDTO toUpdateProfileUserResponseDTO(User user);

    @Mapping(source = "idUser", target = "idUser")
    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "userImage", target = "userImage")
    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "createdAt", target = "createdAt")
    GetUserProfileResponseDTO toGetUserProfileResponseDTO(User user);

    // Respuesta de eliminación de perfil
    default DeleteProfileUserDTO toDeleteProfileUserDTO(Integer idUser) {
        return new DeleteProfileUserDTO(
                "Perfil eliminado correctamente",
                idUser
        );
    }

    // Respuesta de admin eliminando usuario
    default DeleteUserByAdminDTO toDeleteUserByAdminDTO(Integer idUser) {
        return new DeleteUserByAdminDTO(
                "Usuario eliminado correctamente",  // message
                idUser                              // idUser
        );
    }


    // ============================================
    // CONVERSIONES COMPLEJAS
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
        if (role == null) return null;
        return role.name();
    }

}
