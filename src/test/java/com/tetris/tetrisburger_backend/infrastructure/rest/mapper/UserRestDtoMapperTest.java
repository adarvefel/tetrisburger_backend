package com.tetris.tetrisburger_backend.infrastructure.rest.mapper;

import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.ImageStatus;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.model.Role;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.auth.command.LoginUserCommand;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.auth.LoginRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.auth.RegisterUserRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Pruebas Unitarias de UserRestDtoMapper")
class UserRestDtoMapperTest {

    private UserRestDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(UserRestDtoMapper.class);
    }

    @Nested
    @DisplayName("Pruebas de Mapeo DTO a Command")
    class DtoToCommandTests {

        @Test
        @DisplayName("Debería mapear RegisterUserRequestDTO a RegisterUserCommand")
        void shouldMapRegisterUserRequestDtoToCommand() {
            // Given
            RegisterUserRequestDTO dto = new RegisterUserRequestDTO(
                    "John Doe",
                    "john@example.com",
                    "Password123!",
                    "recaptcha-token"
            );

            // When
            RegisterUserCommand command = mapper.toRegisterCommand(dto);

            // Then
            assertThat(command).isNotNull();
            assertThat(command.userName()).isEqualTo("John Doe");
            assertThat(command.email()).isEqualTo("john@example.com");
            assertThat(command.password()).isEqualTo("Password123!");
        }

        @Test
        @DisplayName("Debería mapear LoginRequestDTO a LoginUserCommand")
        void shouldMapLoginRequestDtoToCommand() {
            // Given
            LoginRequestDTO dto = new LoginRequestDTO(
                    "user@example.com",
                    "Password123",
                    "recaptcha-token"
            );

            // When
            LoginUserCommand command = mapper.toLoginCommand(dto);

            // Then
            assertThat(command).isNotNull();
            assertThat(command.email()).isEqualTo("user@example.com");
            assertThat(command.password()).isEqualTo("Password123");
        }

        @Test
        @DisplayName("Debería mapear CreateUserByAdminRequestDTO a CreateUserByAdminCommand con imagen")
        void shouldMapCreateUserByAdminRequestDtoToCommandWithImage() {
            // Given
            MockMultipartFile imageFile = new MockMultipartFile(
                    "userImage",
                    "profile.jpg",
                    "image/jpeg",
                    "test image content".getBytes()
            );
            CreateUserByAdminRequestDTO dto = new CreateUserByAdminRequestDTO(
                    "joseComeGordas",
                    "jose56@gmail.com",
                    "amogorda1234",
                    imageFile,
                    "EMPLOYEE",
                    "3017342342"
            );

            // When
            CreateUserByAdminCommand command = mapper.toCreateUserByAdminCommand(dto, imageFile, 1);

            // Then
            assertThat(command).isNotNull();
            assertThat(command.userName()).isEqualTo("joseComeGordas");
            assertThat(command.email()).isEqualTo("jose56@gmail.com");
            assertThat(command.password()).isEqualTo("amogorda1234");
            assertThat(command.role()).isEqualTo(Role.EMPLOYEE);
            assertThat(command.phone()).isEqualTo("3017342342");
            assertThat(command.createdBy()).isEqualTo(1);
            assertThat(command.userImage()).isNotNull();
            assertThat(command.userImage().originalFilename()).isEqualTo("profile.jpg");
            assertThat(command.userImage().contentType()).isEqualTo("image/jpeg");
        }

        @Test
        @DisplayName("Debería mapear CreateUserByAdminRequestDTO sin imagen")
        void shouldMapCreateUserByAdminRequestDtoWithoutImage() {
            // Given
            CreateUserByAdminRequestDTO dto = new CreateUserByAdminRequestDTO(
                    "No Image User",
                    "noimage@example.com",
                    "Pass123456",
                    null,
                    "CLIENT",
                    null
            );

            // When
            CreateUserByAdminCommand command = mapper.toCreateUserByAdminCommand(dto, null, 1);

            // Then
            assertThat(command).isNotNull();
            assertThat(command.userName()).isEqualTo("No Image User");
            assertThat(command.email()).isEqualTo("noimage@example.com");
            assertThat(command.role()).isEqualTo(Role.CLIENT);
            assertThat(command.phone()).isNull();
            assertThat(command.userImage()).isNull();
        }

        @Test
        @DisplayName("Debería mapear CreateUserByAdminRequestDTO con rol ADMIN")
        void shouldMapCreateUserByAdminRequestDtoWithAdminRole() {
            // Given
            CreateUserByAdminRequestDTO dto = new CreateUserByAdminRequestDTO(
                    "Admin User",
                    "admin@example.com",
                    "AdminPass123",
                    null,
                    "ADMIN",
                    "3001234567"
            );

            // When
            CreateUserByAdminCommand command = mapper.toCreateUserByAdminCommand(dto, null, 1);

            // Then
            assertThat(command).isNotNull();
            assertThat(command.role()).isEqualTo(Role.ADMIN);
        }

        @Test
        @DisplayName("Debería mapear UpdateProfileUserRequestDTO a UpdateProfileUserCommand")
        void shouldMapUpdateProfileUserRequestDtoToCommand() {
            // Given
            UpdateProfileUserRequestDTO dto = new UpdateProfileUserRequestDTO(
                    "Juan Perez",
                    "NuevaPassword123",
                    "3001234567"
            );

            // When
            UpdateProfileUserCommand command = mapper.toUpdateProfileUserCommand(1, dto);

            // Then
            assertThat(command).isNotNull();
            assertThat(command.idUser()).isEqualTo(1);
            assertThat(command.userName()).isEqualTo("Juan Perez");
            assertThat(command.password()).isEqualTo("NuevaPassword123");
            assertThat(command.phone()).isEqualTo("3001234567");
        }

        @Test
        @DisplayName("Debería mapear UpdateProfileUserRequestDTO con campos opcionales null")
        void shouldMapUpdateProfileUserRequestDtoWithNullOptionalFields() {
            // Given
            UpdateProfileUserRequestDTO dto = new UpdateProfileUserRequestDTO(
                    null,
                    null,
                    "3009999999"
            );

            // When
            UpdateProfileUserCommand command = mapper.toUpdateProfileUserCommand(2, dto);

            // Then
            assertThat(command).isNotNull();
            assertThat(command.idUser()).isEqualTo(2);
            assertThat(command.userName()).isNull();
            assertThat(command.password()).isNull();
            assertThat(command.phone()).isEqualTo("3009999999");
        }

        @Test
        @DisplayName("Debería mapear MultipartFile a UpdateProfileImageCommand")
        void shouldMapMultipartFileToUpdateProfileImageCommand() {
            // Given
            MockMultipartFile imageFile = new MockMultipartFile(
                    "profileImage",
                    "new-profile.png",
                    "image/png",
                    "new profile image".getBytes()
            );

            // When
            UpdateProfileImageCommand command = mapper.toUpdateProfileImageCommand(1, imageFile);

            // Then
            assertThat(command).isNotNull();
            assertThat(command.idUser()).isEqualTo(1);
            assertThat(command.fileBytes()).isNotEmpty();
            assertThat(command.contentType()).isEqualTo("image/png");
            assertThat(command.originalFileName()).isEqualTo("new-profile.png");
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando UpdateProfileImageCommand recibe imagen vacía")
        void shouldThrowExceptionWhenUpdateProfileImageCommandReceivesEmptyImage() {
            // Given
            MockMultipartFile emptyFile = new MockMultipartFile(
                    "profileImage",
                    "empty.jpg",
                    "image/jpeg",
                    new byte[0]
            );

            // When & Then
            assertThatThrownBy(() -> mapper.toUpdateProfileImageCommand(1, emptyFile))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("userImage es requerido");
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando UpdateProfileImageCommand recibe null")
        void shouldThrowExceptionWhenUpdateProfileImageCommandReceivesNull() {
            // When & Then
            assertThatThrownBy(() -> mapper.toUpdateProfileImageCommand(1, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("userImage es requerido");
        }

        @Test
        @DisplayName("Debería mapear UpdateUserByAdminRequestDTO a UpdateUserByAdminCommand")
        void shouldMapUpdateUserByAdminRequestDtoToCommand() {
            // Given
            UpdateUserByAdminRequestDTO dto = new UpdateUserByAdminRequestDTO(
                    "Updated Admin User",
                    "updated@example.com",
                    "NewAdminPass123",
                    "EMPLOYEE",
                    "3001111111"
            );

            // When
            UpdateUserByAdminCommand command = mapper.toUpdateUserByAdminCommand(5, dto, 1);

            // Then
            assertThat(command).isNotNull();
            assertThat(command.idUser()).isEqualTo(5);
            assertThat(command.userName()).isEqualTo("Updated Admin User");
            assertThat(command.email()).isEqualTo("updated@example.com");
            assertThat(command.password()).isEqualTo("NewAdminPass123");
            assertThat(command.role()).isEqualTo(Role.EMPLOYEE);
            assertThat(command.phone()).isEqualTo("3001111111");
            assertThat(command.updatedBy()).isEqualTo(1);
        }

        @Test
        @DisplayName("Debería mapear MultipartFile a UpdateUserImageByAdminCommand")
        void shouldMapMultipartFileToUpdateUserImageByAdminCommand() {
            // Given
            MockMultipartFile imageFile = new MockMultipartFile(
                    "adminImage",
                    "admin-photo.jpg",
                    "image/jpeg",
                    "admin photo content".getBytes()
            );

            // When
            UpdateUserImageByAdminCommand command = mapper.toUpdateUserImageByAdminCommand(3, imageFile, 1);

            // Then
            assertThat(command).isNotNull();
            assertThat(command.idUser()).isEqualTo(3);
            assertThat(command.fileBytes()).isNotEmpty();
            assertThat(command.contentType()).isEqualTo("image/jpeg");
            assertThat(command.originalFileName()).isEqualTo("admin-photo.jpg");
            assertThat(command.updatedBy()).isEqualTo(1);
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando UpdateUserImageByAdminCommand recibe imagen vacía")
        void shouldThrowExceptionWhenUpdateUserImageByAdminCommandReceivesEmptyImage() {
            // Given
            MockMultipartFile emptyFile = new MockMultipartFile(
                    "adminImage",
                    "empty.jpg",
                    "image/jpeg",
                    new byte[0]
            );

            // When & Then
            assertThatThrownBy(() -> mapper.toUpdateUserImageByAdminCommand(3, emptyFile, 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("userImage es requerido");
        }

        @Test
        @DisplayName("Debería crear DeleteProfileUserCommand")
        void shouldCreateDeleteProfileUserCommand() {
            // When
            DeleteProfileUserCommand command = mapper.toDeleteProfileUserCommand(10);

            // Then
            assertThat(command).isNotNull();
            assertThat(command.idUser()).isEqualTo(10);
        }

        @Test
        @DisplayName("Debería mapear DeleteUserByAdminCommand")
        void shouldMapDeleteUserByAdminCommand() {
            // When
            DeleteUserByAdminCommand command = mapper.toDeleteUserByAdminCommand(15, 1);

            // Then
            assertThat(command).isNotNull();
            assertThat(command.idUser()).isEqualTo(15);
            assertThat(command.deletedBy()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Pruebas de Mapeo Domain a Response DTO")
    class DomainToResponseDtoTests {

        @Test
        @DisplayName("Debería mapear User a RegisterUserResponseDTO")
        void shouldMapUserToRegisterUserResponseDto() {
            // Given
            User user = User.createClient("New User", "new@example.com", "hashedPass");
            user.setIdUser(1);

            // When
            RegisterUserResponseDTO dto = mapper.toRegisterResponseDTO(user);

            // Then
            assertThat(dto).isNotNull();
            assertThat(dto.idUser()).isEqualTo(1);
            assertThat(dto.userName()).isEqualTo("New User");
            assertThat(dto.email()).isEqualTo("new@example.com");
            assertThat(dto.createdAt()).isNotNull();
        }

        @Test
        @DisplayName("Debería mapear User a UserResponseDTO sin URL")
        void shouldMapUserToUserResponseDtoWithoutUrl() {
            // Given
            User user = User.createClient("Test User", "test@example.com", "hashedPass");
            user.setIdUser(2);

            // When
            UserResponseDTO dto = mapper.toUserResponseDTO(user);

            // Then
            assertThat(dto).isNotNull();
            assertThat(dto.idUser()).isEqualTo(2);
            assertThat(dto.userName()).isEqualTo("Test User");
            assertThat(dto.email()).isEqualTo("test@example.com");
            assertThat(dto.role()).isEqualTo("CLIENT");
            assertThat(dto.createdAt()).isNotNull();
            assertThat(dto.updatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Debería mapear User a UserResponseDTO con URL e ImageStatus UPLOADED")
        void shouldMapUserToUserResponseDtoWithUrlAndUploadedStatus() {
            // Given
            User user = User.createClient("User With Image", "image@example.com", "hashedPass");
            user.setIdUser(3);
            String imageUrl = "https://s3.amazonaws.com/bucket/image.jpg";

            // When
            UserResponseDTO dto = mapper.toUserResponseDTO(user, imageUrl, ImageStatus.UPLOADED);

            // Then
            assertThat(dto).isNotNull();
            assertThat(dto.idUser()).isEqualTo(3);
            assertThat(dto.userName()).isEqualTo("User With Image");
            assertThat(dto.userImage()).isEqualTo(imageUrl);
            assertThat(dto.imageStatus()).isEqualTo(ImageStatus.UPLOADED);
        }

        @Test
        @DisplayName("Debería mapear User a UserResponseDTO con campos de auditoría")
        void shouldMapUserToUserResponseDtoWithAuditFields() {
            // Given
            User user = User.createByAdmin(
                    "Audit User",
                    "audit@example.com",
                    "hashedPass",
                    Role.EMPLOYEE,
                    "+573001234567",
                    null,
                    null,
                    1
            );
            user.setIdUser(4);

            // When
            UserResponseDTO dto = mapper.toUserResponseDTO(user);

            // Then
            assertThat(dto).isNotNull();
            assertThat(dto.createdBy()).isEqualTo(1);
            assertThat(dto.updatedBy()).isEqualTo(1);
            assertThat(dto.deletedBy()).isNull();
            assertThat(dto.deletedAt()).isNull();
        }

        @Test
        @DisplayName("Debería mapear lista de Users a UserResponseDTOList")
        void shouldMapListOfUsersToUserResponseDtoList() {
            // Given
            User user1 = User.createClient("User 1", "user1@example.com", "pass1");
            user1.setIdUser(1);
            User user2 = User.createClient("User 2", "user2@example.com", "pass2");
            user2.setIdUser(2);
            List<User> users = Arrays.asList(user1, user2);

            // When
            List<UserResponseDTO> dtos = mapper.toUserResponseDTOList(users);

            // Then
            assertThat(dtos).hasSize(2);
            assertThat(dtos.get(0).idUser()).isEqualTo(1);
            assertThat(dtos.get(0).userName()).isEqualTo("User 1");
            assertThat(dtos.get(1).idUser()).isEqualTo(2);
            assertThat(dtos.get(1).userName()).isEqualTo("User 2");
        }

        @Test
        @DisplayName("Debería mapear User a CreateUserByAdminResponseDTO sin URL")
        void shouldMapUserToCreateUserByAdminResponseDtoWithoutUrl() {
            // Given
            User user = User.createByAdmin(
                    "joseComeGordas",
                    "jose56@gmail.com",
                    "hashedPass",
                    Role.EMPLOYEE,
                    "3017342342",
                    null,
                    null,
                    1
            );
            user.setIdUser(4);

            // When
            CreateUserByAdminResponseDTO dto = mapper.toCreateUserByAdminResponseDTO(user);

            // Then
            assertThat(dto).isNotNull();
            assertThat(dto.idUser()).isEqualTo(4);
            assertThat(dto.userName()).isEqualTo("joseComeGordas");
            assertThat(dto.email()).isEqualTo("jose56@gmail.com");
            assertThat(dto.role()).isEqualTo("EMPLOYEE");
            assertThat(dto.phone()).isEqualTo("3017342342");
            assertThat(dto.createdAt()).isNotNull();
            assertThat(dto.updatedAt()).isNotNull();
            assertThat(dto.createdBy()).isEqualTo(1);
            assertThat(dto.updatedBy()).isEqualTo(1);
        }

        @Test
        @DisplayName("Debería mapear User a CreateUserByAdminResponseDTO con URL")
        void shouldMapUserToCreateUserByAdminResponseDtoWithUrl() {
            // Given
            User user = User.createByAdmin(
                    "Admin User With Image",
                    "adminimage@example.com",
                    "hashedPass",
                    Role.ADMIN,
                    null,
                    "image-key",
                    "image.jpg",
                    1
            );
            user.setIdUser(5);
            String imageUrl = "https://cdn.example.com/images/admin.jpg";

            // When
            CreateUserByAdminResponseDTO dto = mapper.toCreateUserByAdminResponseDTO(
                    user, imageUrl, ImageStatus.UPLOADED
            );

            // Then
            assertThat(dto).isNotNull();
            assertThat(dto.idUser()).isEqualTo(5);
            assertThat(dto.userName()).isEqualTo("Admin User With Image");
            assertThat(dto.userImage()).isEqualTo(imageUrl);
            assertThat(dto.imageStatus()).isEqualTo(ImageStatus.UPLOADED);
            assertThat(dto.role()).isEqualTo("ADMIN");
        }

        @Test
        @DisplayName("Debería mapear User a UpdateUserByAdminResponseDTO con URL")
        void shouldMapUserToUpdateUserByAdminResponseDtoWithUrl() {
            // Given
            User user = User.createByAdmin(
                    "Updated User",
                    "updated@example.com",
                    "hashedPass",
                    Role.CLIENT,
                    "3009999999",
                    "key-123",
                    "updated.jpg",
                    1
            );
            user.setIdUser(6);
            String imageUrl = "https://cdn.example.com/updated.jpg";

            // When
            UpdateUserByAdminResponseDTO dto = mapper.toUpdateUserByAdminResponseDTO(
                    user, imageUrl, ImageStatus.UPLOADED
            );

            // Then
            assertThat(dto).isNotNull();
            assertThat(dto.idUser()).isEqualTo(6);
            assertThat(dto.userName()).isEqualTo("Updated User");
            assertThat(dto.userImage()).isEqualTo(imageUrl);
            assertThat(dto.imageStatus()).isEqualTo(ImageStatus.UPLOADED);
            assertThat(dto.role()).isEqualTo("CLIENT");
        }

        @Test
        @DisplayName("Debería mapear User a UpdateProfileUserResponseDTO con URL")
        void shouldMapUserToUpdateProfileUserResponseDtoWithUrl() {
            // Given
            User user = User.createClient("Juan Perez", "juanperez@example.com", "hashedPass");
            user.setIdUser(7);
            String imageUrl = "https://cdn.example.com/profile.jpg";

            // When
            UpdateProfileUserResponseDTO dto = mapper.toUpdateProfileUserResponseDTO(
                    user, imageUrl, ImageStatus.UPLOADED
            );

            // Then
            assertThat(dto).isNotNull();
            assertThat(dto.idUser()).isEqualTo(7);
            assertThat(dto.userName()).isEqualTo("Juan Perez");
            assertThat(dto.userImage()).isEqualTo(imageUrl);
            assertThat(dto.imageStatus()).isEqualTo(ImageStatus.UPLOADED);
        }

        @Test
        @DisplayName("Debería mapear User a GetUserProfileResponseDTO con URL")
        void shouldMapUserToGetUserProfileResponseDtoWithUrl() {
            // Given
            User user = User.createClient("Profile User", "getprofile@example.com", "hashedPass");
            user.setIdUser(8);
            String imageUrl = "https://cdn.example.com/getprofile.jpg";

            // When
            GetUserProfileResponseDTO dto = mapper.toGetUserProfileResponseDTO(
                    user, imageUrl, ImageStatus.UPLOADED
            );

            // Then
            assertThat(dto).isNotNull();
            assertThat(dto.idUser()).isEqualTo(8);
            assertThat(dto.userName()).isEqualTo("Profile User");
            assertThat(dto.email()).isEqualTo("getprofile@example.com");
            assertThat(dto.userImage()).isEqualTo(imageUrl);
            assertThat(dto.imageStatus()).isEqualTo(ImageStatus.UPLOADED);
            assertThat(dto.role()).isEqualTo("CLIENT");
            assertThat(dto.createdAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Pruebas de Paginación")
    class PaginationTests {

        @Test
        @DisplayName("Debería mapear PageResponse a ListUserResponseDTO")
        void shouldMapPageResponseToListUserResponseDto() {
            // Given
            User user1 = User.createClient("User 1", "user1@example.com", "pass1");
            user1.setIdUser(1);
            User user2 = User.createClient("User 2", "user2@example.com", "pass2");
            user2.setIdUser(2);

            List<User> users = Arrays.asList(user1, user2);
            PageResponse<User> pageResponse = new PageResponse<>(
                    users,   // content
                    0,       // page
                    10,      // size
                    2L,      // totalElements
                    1        // totalPages
            );

            // When
            ListUserResponseDTO dto = mapper.toListUserResponseDTO(pageResponse);

            // Then
            assertThat(dto).isNotNull();
            assertThat(dto.users()).hasSize(2);
            assertThat(dto.totalElements()).isEqualTo(2L);
            assertThat(dto.totalPages()).isEqualTo(1);
            assertThat(dto.timestamp()).isNotNull();
        }

        @Test
        @DisplayName("Debería mapear PageResponse con contenido personalizado a ListUserResponseDTO")
        void shouldMapPageResponseWithCustomContentToListUserResponseDto() {
            // Given
            User user1 = User.createClient("User 1", "user1@example.com", "pass1");
            user1.setIdUser(1);

            List<User> users = List.of(user1);
            PageResponse<User> pageResponse = new PageResponse<>(
                    users,          // content
                    0,              // page
                    10,             // size
                    1L,             // totalElements
                    1               // totalPages
            );
            List<UserResponseDTO> customContent = List.of(
                    mapper.toUserResponseDTO(user1, "https://url1.com", ImageStatus.UPLOADED)
            );

            // When
            ListUserResponseDTO dto = mapper.toListUserResponseDTO(pageResponse, customContent);

            // Then
            assertThat(dto).isNotNull();
            assertThat(dto.users()).hasSize(1);
            assertThat(dto.users().get(0).userImage()).isEqualTo("https://url1.com");
            assertThat(dto.totalElements()).isEqualTo(1L);
            assertThat(dto.totalPages()).isEqualTo(1);
        }

        @Test
        @DisplayName("Debería retornar null cuando PageResponse es null")
        void shouldReturnNullWhenPageResponseIsNull() {
            // When
            ListUserResponseDTO dto = mapper.toListUserResponseDTO(null);

            // Then
            assertThat(dto).isNull();
        }
    }

    @Nested
    @DisplayName("Pruebas de Delete DTOs")
    class DeleteDtosTests {

        @Test
        @DisplayName("Debería crear DeleteProfileUserDTO")
        void shouldCreateDeleteProfileUserDto() {
            // When
            DeleteProfileUserDTO dto = mapper.toDeleteProfileUserDTO(10);

            // Then
            assertThat(dto).isNotNull();
            assertThat(dto.message()).isEqualTo("Perfil eliminado correctamente");
            assertThat(dto.idUser()).isEqualTo(10);
        }

        @Test
        @DisplayName("Debería crear DeleteUserByAdminDTO")
        void shouldCreateDeleteUserByAdminDto() {
            // When
            DeleteUserByAdminDTO dto = mapper.toDeleteUserByAdminDTO(20);

            // Then
            assertThat(dto).isNotNull();
            assertThat(dto.message()).isEqualTo("Usuario eliminado correctamente");
            assertThat(dto.idUser()).isEqualTo(20);
        }
    }

    @Nested
    @DisplayName("Pruebas de Helpers")
    class HelpersTests {

        @Test
        @DisplayName("Debería convertir MultipartFile a FileData")
        void shouldConvertMultipartFileToFileData() {
            // Given
            MockMultipartFile file = new MockMultipartFile(
                    "testFile",
                    "test.jpg",
                    "image/jpeg",
                    "test content".getBytes()
            );

            // When
            FileData fileData = mapper.multipartToFileData(file);

            // Then
            assertThat(fileData).isNotNull();
            assertThat(fileData.originalFilename()).isEqualTo("test.jpg");
            assertThat(fileData.contentType()).isEqualTo("image/jpeg");
            assertThat(fileData.bytes()).isNotEmpty();
        }

        @Test
        @DisplayName("Debería retornar null cuando MultipartFile es null")
        void shouldReturnNullWhenMultipartFileIsNull() {
            // When
            FileData fileData = mapper.multipartToFileData(null);

            // Then
            assertThat(fileData).isNull();
        }

        @Test
        @DisplayName("Debería retornar null cuando MultipartFile está vacío")
        void shouldReturnNullWhenMultipartFileIsEmpty() {
            // Given
            MockMultipartFile emptyFile = new MockMultipartFile(
                    "emptyFile",
                    "empty.jpg",
                    "image/jpeg",
                    new byte[0]
            );

            // When
            FileData fileData = mapper.multipartToFileData(emptyFile);

            // Then
            assertThat(fileData).isNull();
        }

        @Test
        @DisplayName("Debería convertir String a Role CLIENT")
        void shouldConvertStringToClientRole() {
            // When
            Role role = mapper.stringToRole("CLIENT");

            // Then
            assertThat(role).isEqualTo(Role.CLIENT);
        }

        @Test
        @DisplayName("Debería convertir String a Role ADMIN")
        void shouldConvertStringToAdminRole() {
            // When
            Role role = mapper.stringToRole("ADMIN");

            // Then
            assertThat(role).isEqualTo(Role.ADMIN);
        }

        @Test
        @DisplayName("Debería convertir String a Role EMPLOYEE")
        void shouldConvertStringToEmployeeRole() {
            // When
            Role role = mapper.stringToRole("EMPLOYEE");

            // Then
            assertThat(role).isEqualTo(Role.EMPLOYEE);
        }

        @Test
        @DisplayName("Debería convertir String en minúsculas a Role")
        void shouldConvertLowercaseStringToRole() {
            // When
            Role role = mapper.stringToRole("employee");

            // Then
            assertThat(role).isEqualTo(Role.EMPLOYEE);
        }

        @Test
        @DisplayName("Debería retornar null cuando String de role es inválido")
        void shouldReturnNullWhenRoleStringIsInvalid() {
            // When
            Role role = mapper.stringToRole("INVALID_ROLE");

            // Then
            assertThat(role).isNull();
        }

        @Test
        @DisplayName("Debería retornar null cuando String de role es null")
        void shouldReturnNullWhenRoleStringIsNull() {
            // When
            Role role = mapper.stringToRole(null);

            // Then
            assertThat(role).isNull();
        }

        @Test
        @DisplayName("Debería convertir Role a String CLIENT")
        void shouldConvertClientRoleToString() {
            // When
            String roleStr = mapper.roleToString(Role.CLIENT);

            // Then
            assertThat(roleStr).isEqualTo("CLIENT");
        }

        @Test
        @DisplayName("Debería convertir Role a String ADMIN")
        void shouldConvertAdminRoleToString() {
            // When
            String roleStr = mapper.roleToString(Role.ADMIN);

            // Then
            assertThat(roleStr).isEqualTo("ADMIN");
        }

        @Test
        @DisplayName("Debería convertir Role a String EMPLOYEE")
        void shouldConvertEmployeeRoleToString() {
            // When
            String roleStr = mapper.roleToString(Role.EMPLOYEE);

            // Then
            assertThat(roleStr).isEqualTo("EMPLOYEE");
        }

        @Test
        @DisplayName("Debería retornar null cuando Role es null")
        void shouldReturnNullWhenRoleIsNull() {
            // When
            String roleStr = mapper.roleToString(null);

            // Then
            assertThat(roleStr).isNull();
        }
    }

    @Nested
    @DisplayName("Pruebas de ImageStatus")
    class ImageStatusTests {

        @Test
        @DisplayName("Debería manejar ImageStatus NONE")
        void shouldHandleImageStatusNone() {
            // Given
            User user = User.createClient("None User", "none@example.com", "pass");
            user.setIdUser(1);

            // When
            UserResponseDTO dto = mapper.toUserResponseDTO(user, null, ImageStatus.NONE);

            // Then
            assertThat(dto.imageStatus()).isEqualTo(ImageStatus.NONE);
            assertThat(dto.userImage()).isNull();
        }

        @Test
        @DisplayName("Debería manejar ImageStatus PENDING")
        void shouldHandleImageStatusPending() {
            // Given
            User user = User.createClient("Pending User", "pending@example.com", "pass");
            user.setIdUser(1);

            // When
            UserResponseDTO dto = mapper.toUserResponseDTO(user, null, ImageStatus.PENDING);

            // Then
            assertThat(dto.imageStatus()).isEqualTo(ImageStatus.PENDING);
        }

        @Test
        @DisplayName("Debería manejar ImageStatus UPLOADED")
        void shouldHandleImageStatusUploaded() {
            // Given
            User user = User.createClient("Uploaded User", "uploaded@example.com", "pass");
            user.setIdUser(1);
            String imageUrl = "https://cdn.example.com/uploaded.jpg";

            // When
            UserResponseDTO dto = mapper.toUserResponseDTO(user, imageUrl, ImageStatus.UPLOADED);

            // Then
            assertThat(dto.imageStatus()).isEqualTo(ImageStatus.UPLOADED);
            assertThat(dto.userImage()).isEqualTo(imageUrl);
        }

        @Test
        @DisplayName("Debería manejar ImageStatus FAILED")
        void shouldHandleImageStatusFailed() {
            // Given
            User user = User.createClient("Failed User", "failed@example.com", "pass");
            user.setIdUser(1);

            // When
            UserResponseDTO dto = mapper.toUserResponseDTO(user, null, ImageStatus.FAILED);

            // Then
            assertThat(dto.imageStatus()).isEqualTo(ImageStatus.FAILED);
            assertThat(dto.userImage()).isNull();
        }

        @Test
        @DisplayName("Debería probar todos los ImageStatus en CreateUserByAdminResponseDTO")
        void shouldTestAllImageStatusInCreateUserByAdminResponseDto() {
            // Given
            User user = User.createByAdmin(
                    "Status Test", "status@example.com", "pass",
                    Role.CLIENT, null, null, null, 1
            );
            user.setIdUser(1);

            // When & Then - NONE
            CreateUserByAdminResponseDTO dtoNone = mapper.toCreateUserByAdminResponseDTO(
                    user, null, ImageStatus.NONE
            );
            assertThat(dtoNone.imageStatus()).isEqualTo(ImageStatus.NONE);

            // PENDING
            CreateUserByAdminResponseDTO dtoPending = mapper.toCreateUserByAdminResponseDTO(
                    user, null, ImageStatus.PENDING
            );
            assertThat(dtoPending.imageStatus()).isEqualTo(ImageStatus.PENDING);

            // UPLOADED
            CreateUserByAdminResponseDTO dtoUploaded = mapper.toCreateUserByAdminResponseDTO(
                    user, "https://url.com/image.jpg", ImageStatus.UPLOADED
            );
            assertThat(dtoUploaded.imageStatus()).isEqualTo(ImageStatus.UPLOADED);

            // FAILED
            CreateUserByAdminResponseDTO dtoFailed = mapper.toCreateUserByAdminResponseDTO(
                    user, null, ImageStatus.FAILED
            );
            assertThat(dtoFailed.imageStatus()).isEqualTo(ImageStatus.FAILED);
        }
    }

    @Nested
    @DisplayName("Pruebas de Casos Especiales")
    class SpecialCasesTests {

        @Test
        @DisplayName("Debería manejar CreateUserByAdminRequestDTO con nombre mínimo de 2 caracteres")
        void shouldHandleCreateUserByAdminRequestDtoWithMinimumNameLength() {
            // Given
            CreateUserByAdminRequestDTO dto = new CreateUserByAdminRequestDTO(
                    "Jo",
                    "jo@example.com",
                    "Pass123456",
                    null,
                    "CLIENT",
                    null
            );

            // When
            CreateUserByAdminCommand command = mapper.toCreateUserByAdminCommand(dto, null, 1);

            // Then
            assertThat(command).isNotNull();
            assertThat(command.userName()).hasSize(2);
        }

        @Test
        @DisplayName("Debería manejar UpdateProfileUserRequestDTO con nombre de 3 caracteres")
        void shouldHandleUpdateProfileUserRequestDtoWithThreeCharName() {
            // Given
            UpdateProfileUserRequestDTO dto = new UpdateProfileUserRequestDTO(
                    "Bob",
                    "NewPass123",
                    null
            );

            // When
            UpdateProfileUserCommand command = mapper.toUpdateProfileUserCommand(1, dto);

            // Then
            assertThat(command.userName()).hasSize(3);
        }

        @Test
        @DisplayName("Debería manejar User con todos los roles en UserResponseDTO")
        void shouldHandleUserWithAllRolesInUserResponseDto() {
            for (Role role : Role.values()) {
                // Given
                User user = User.createByAdmin(
                        "Test User", "test@example.com", "pass",
                        role, null, null, null, 1
                );
                user.setIdUser(1);

                // When
                UserResponseDTO dto = mapper.toUserResponseDTO(user);

                // Then
                assertThat(dto.role()).isEqualTo(role.name());
            }
        }

        @Test
        @DisplayName("Debería manejar CreateUserByAdminRequestDTO con ejemplo de email")
        void shouldHandleCreateUserByAdminRequestDtoWithExampleEmail() {
            // Given
            CreateUserByAdminRequestDTO dto = new CreateUserByAdminRequestDTO(
                    "joseComeGordas",
                    "jose56@gmail.com",
                    "amogorda1234",
                    null,
                    "EMPLOYEE",
                    "3017342342"
            );

            // When
            CreateUserByAdminCommand command = mapper.toCreateUserByAdminCommand(dto, null, 1);

            // Then
            assertThat(command.email()).isEqualTo("jose56@gmail.com");
            assertThat(command.phone()).isEqualTo("3017342342");
        }
    }
}
