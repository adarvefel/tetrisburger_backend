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
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Pruebas Unitarias de UserRestDtoMapper")
class UserRestDtoMapperTest {

    private UserRestDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserRestDtoMapperImpl(); // ← fix: componentModel="spring" requiere new Impl()
    }

    @Nested
    @DisplayName("Pruebas de Mapeo DTO a Command")
    class DtoToCommandTests {

        @Test
        @DisplayName("Debería mapear RegisterUserRequestDTO a RegisterUserCommand")
        void shouldMapRegisterUserRequestDtoToCommand() {
            RegisterUserRequestDTO dto = new RegisterUserRequestDTO(
                    "John Doe", "john@example.com", "Password123!", "recaptcha-token"
            );
            RegisterUserCommand command = mapper.toRegisterCommand(dto);

            assertThat(command).isNotNull();
            assertThat(command.userName()).isEqualTo("John Doe");
            assertThat(command.email()).isEqualTo("john@example.com");
            assertThat(command.password()).isEqualTo("Password123!");
        }

        @Test
        @DisplayName("Debería mapear LoginRequestDTO a LoginUserCommand")
        void shouldMapLoginRequestDtoToCommand() {
            LoginRequestDTO dto = new LoginRequestDTO(
                    "user@example.com", "Password123", "recaptcha-token"
            );
            LoginUserCommand command = mapper.toLoginCommand(dto);

            assertThat(command).isNotNull();
            assertThat(command.email()).isEqualTo("user@example.com");
            assertThat(command.password()).isEqualTo("Password123");
        }

        @Test
        @DisplayName("Debería mapear CreateUserByAdminRequestDTO a CreateUserByAdminCommand con imagen")
        void shouldMapCreateUserByAdminRequestDtoToCommandWithImage() {
            MockMultipartFile imageFile = new MockMultipartFile(
                    "userImage", "profile.jpg", "image/jpeg", "test image content".getBytes()
            );
            CreateUserByAdminRequestDTO dto = new CreateUserByAdminRequestDTO(
                    "joseComeGordas", "jose56@gmail.com", "amogorda1234",
                    imageFile, "EMPLOYEE", "3017342342"
            );
            CreateUserByAdminCommand command = mapper.toCreateUserByAdminCommand(dto, imageFile, 1);

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
            CreateUserByAdminRequestDTO dto = new CreateUserByAdminRequestDTO(
                    "No Image User", "noimage@example.com", "Pass123456", null, "CLIENT", null
            );
            CreateUserByAdminCommand command = mapper.toCreateUserByAdminCommand(dto, null, 1);

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
            CreateUserByAdminRequestDTO dto = new CreateUserByAdminRequestDTO(
                    "Admin User", "admin@example.com", "AdminPass123", null, "ADMIN", "3001234567"
            );
            CreateUserByAdminCommand command = mapper.toCreateUserByAdminCommand(dto, null, 1);

            assertThat(command).isNotNull();
            assertThat(command.role()).isEqualTo(Role.ADMIN);
        }

        @Test
        @DisplayName("Debería mapear UpdateProfileUserRequestDTO a UpdateProfileUserCommand")
        void shouldMapUpdateProfileUserRequestDtoToCommand() {
            UpdateProfileUserRequestDTO dto = new UpdateProfileUserRequestDTO(
                    "Juan Perez", "NuevaPassword123", "3001234567"
            );
            UpdateProfileUserCommand command = mapper.toUpdateProfileUserCommand(1, dto);

            assertThat(command).isNotNull();
            assertThat(command.idUser()).isEqualTo(1);
            assertThat(command.userName()).isEqualTo("Juan Perez");
            assertThat(command.password()).isEqualTo("NuevaPassword123");
            assertThat(command.phone()).isEqualTo("3001234567");
        }

        @Test
        @DisplayName("Debería mapear UpdateProfileUserRequestDTO con campos opcionales null")
        void shouldMapUpdateProfileUserRequestDtoWithNullOptionalFields() {
            UpdateProfileUserRequestDTO dto = new UpdateProfileUserRequestDTO(
                    null, null, "3009999999"
            );
            UpdateProfileUserCommand command = mapper.toUpdateProfileUserCommand(2, dto);

            assertThat(command).isNotNull();
            assertThat(command.idUser()).isEqualTo(2);
            assertThat(command.userName()).isNull();
            assertThat(command.password()).isNull();
            assertThat(command.phone()).isEqualTo("3009999999");
        }

        @Test
        @DisplayName("Debería mapear MultipartFile a UpdateProfileImageCommand")
        void shouldMapMultipartFileToUpdateProfileImageCommand() {
            MockMultipartFile imageFile = new MockMultipartFile(
                    "profileImage", "new-profile.png", "image/png", "new profile image".getBytes()
            );
            UpdateProfileImageCommand command = mapper.toUpdateProfileImageCommand(1, imageFile);

            assertThat(command).isNotNull();
            assertThat(command.idUser()).isEqualTo(1);
            assertThat(command.fileBytes()).isNotEmpty();
            assertThat(command.contentType()).isEqualTo("image/png");
            assertThat(command.originalFileName()).isEqualTo("new-profile.png");
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando UpdateProfileImageCommand recibe imagen vacía")
        void shouldThrowExceptionWhenUpdateProfileImageCommandReceivesEmptyImage() {
            MockMultipartFile emptyFile = new MockMultipartFile(
                    "profileImage", "empty.jpg", "image/jpeg", new byte[0]
            );
            assertThatThrownBy(() -> mapper.toUpdateProfileImageCommand(1, emptyFile))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("userImage es requerido");
        }

        @Test
        @DisplayName("Debería lanzar excepción cuando UpdateProfileImageCommand recibe null")
        void shouldThrowExceptionWhenUpdateProfileImageCommandReceivesNull() {
            assertThatThrownBy(() -> mapper.toUpdateProfileImageCommand(1, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("userImage es requerido");
        }

        @Test
        @DisplayName("Debería mapear UpdateUserByAdminRequestDTO a UpdateUserByAdminCommand")
        void shouldMapUpdateUserByAdminRequestDtoToCommand() {
            UpdateUserByAdminRequestDTO dto = new UpdateUserByAdminRequestDTO(
                    "Updated Admin User", "updated@example.com",
                    "NewAdminPass123", "EMPLOYEE", "3001111111"
            );
            UpdateUserByAdminCommand command = mapper.toUpdateUserByAdminCommand(5, dto, 1);

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
            MockMultipartFile imageFile = new MockMultipartFile(
                    "adminImage", "admin-photo.jpg", "image/jpeg", "admin photo content".getBytes()
            );
            UpdateUserImageByAdminCommand command = mapper.toUpdateUserImageByAdminCommand(3, imageFile, 1);

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
            MockMultipartFile emptyFile = new MockMultipartFile(
                    "adminImage", "empty.jpg", "image/jpeg", new byte[0]
            );
            assertThatThrownBy(() -> mapper.toUpdateUserImageByAdminCommand(3, emptyFile, 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("userImage es requerido");
        }

        @Test
        @DisplayName("Debería crear DeleteProfileUserCommand")
        void shouldCreateDeleteProfileUserCommand() {
            DeleteProfileUserCommand command = mapper.toDeleteProfileUserCommand(10);

            assertThat(command).isNotNull();
            assertThat(command.idUser()).isEqualTo(10);
        }

        @Test
        @DisplayName("Debería mapear DeleteUserByAdminCommand")
        void shouldMapDeleteUserByAdminCommand() {
            DeleteUserByAdminCommand command = mapper.toDeleteUserByAdminCommand(15, 1);

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
            User user = User.createClient("New User", "new@example.com", "hashedPass");
            user.setIdUser(1);

            RegisterUserResponseDTO dto = mapper.toRegisterResponseDTO(user);

            assertThat(dto).isNotNull();
            assertThat(dto.idUser()).isEqualTo(1);
            assertThat(dto.userName()).isEqualTo("New User");
            assertThat(dto.email()).isEqualTo("new@example.com");
            assertThat(dto.createdAt()).isNotNull();
        }

        @Test
        @DisplayName("Debería mapear User a UserResponseDTO sin URL")
        void shouldMapUserToUserResponseDtoWithoutUrl() {
            User user = User.createClient("Test User", "test@example.com", "hashedPass");
            user.setIdUser(2);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            UserResponseDTO dto = mapper.toUserResponseDTO(user);

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
            User user = User.createClient("User With Image", "image@example.com", "hashedPass");
            user.setIdUser(3);
            String imageUrl = "https://s3.amazonaws.com/bucket/image.jpg";

            UserResponseDTO dto = mapper.toUserResponseDTO(user, imageUrl, ImageStatus.UPLOADED);

            assertThat(dto).isNotNull();
            assertThat(dto.idUser()).isEqualTo(3);
            assertThat(dto.userName()).isEqualTo("User With Image");
            assertThat(dto.userImage()).isEqualTo(imageUrl);
            assertThat(dto.imageStatus()).isEqualTo(ImageStatus.UPLOADED);
        }

        @Test
        @DisplayName("Debería mapear User a UserResponseDTO con campos de auditoría")
        void shouldMapUserToUserResponseDtoWithAuditFields() {
            User user = User.createByAdmin(
                    "Audit User", "audit@example.com", "hashedPass",
                    Role.EMPLOYEE, "+573001234567", null, null, 1
            );
            user.setIdUser(4);
            user.setUpdatedAt(LocalDateTime.now());
            user.setUpdatedBy(1);

            UserResponseDTO dto = mapper.toUserResponseDTO(user);

            assertThat(dto).isNotNull();
            assertThat(dto.createdBy()).isEqualTo(1);
            assertThat(dto.updatedBy()).isEqualTo(1);
            assertThat(dto.deletedBy()).isNull();
            assertThat(dto.deletedAt()).isNull();
        }

        @Test
        @DisplayName("Debería mapear lista de Users a UserResponseDTOList")
        void shouldMapListOfUsersToUserResponseDtoList() {
            User user1 = User.createClient("User 1", "user1@example.com", "pass1");
            user1.setIdUser(1);
            User user2 = User.createClient("User 2", "user2@example.com", "pass2");
            user2.setIdUser(2);

            List<UserResponseDTO> dtos = mapper.toUserResponseDTOList(Arrays.asList(user1, user2));

            assertThat(dtos).hasSize(2);
            assertThat(dtos.get(0).idUser()).isEqualTo(1);
            assertThat(dtos.get(0).userName()).isEqualTo("User 1");
            assertThat(dtos.get(1).idUser()).isEqualTo(2);
            assertThat(dtos.get(1).userName()).isEqualTo("User 2");
        }

        @Test
        @DisplayName("Debería mapear User a CreateUserByAdminResponseDTO sin URL")
        void shouldMapUserToCreateUserByAdminResponseDtoWithoutUrl() {
            User user = User.createByAdmin(
                    "joseComeGordas", "jose56@gmail.com", "hashedPass",
                    Role.EMPLOYEE, "3017342342", null, null, 1
            );
            user.setIdUser(4);
            user.setUpdatedAt(LocalDateTime.now());
            user.setUpdatedBy(1);

            CreateUserByAdminResponseDTO dto = mapper.toCreateUserByAdminResponseDTO(user);

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
            User user = User.createByAdmin(
                    "Admin User With Image", "adminimage@example.com", "hashedPass",
                    Role.ADMIN, null, "image-key", "image.jpg", 1
            );
            user.setIdUser(5);
            String imageUrl = "https://cdn.example.com/images/admin.jpg";

            CreateUserByAdminResponseDTO dto = mapper.toCreateUserByAdminResponseDTO(
                    user, imageUrl, ImageStatus.UPLOADED
            );

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
            User user = User.createByAdmin(
                    "Updated User", "updated@example.com", "hashedPass",
                    Role.CLIENT, "3009999999", "key-123", "updated.jpg", 1
            );
            user.setIdUser(6);
            String imageUrl = "https://cdn.example.com/updated.jpg";

            UpdateUserByAdminResponseDTO dto = mapper.toUpdateUserByAdminResponseDTO(
                    user, imageUrl, ImageStatus.UPLOADED
            );

            assertThat(dto).isNotNull();
            assertThat(dto.idUser()).isEqualTo(6);
            assertThat(dto.userName()).isEqualTo("Updated User");
            assertThat(dto.userImage()).isEqualTo(imageUrl);
            assertThat(dto.imageStatus()).isEqualTo(ImageStatus.UPLOADED.name());
            assertThat(dto.role()).isEqualTo("CLIENT");
        }

        @Test
        @DisplayName("Debería mapear User a UpdateProfileUserResponseDTO con URL")
        void shouldMapUserToUpdateProfileUserResponseDtoWithUrl() {
            User user = User.createClient("Juan Perez", "juanperez@example.com", "hashedPass");
            user.setIdUser(7);
            String imageUrl = "https://cdn.example.com/profile.jpg";

            UpdateProfileUserResponseDTO dto = mapper.toUpdateProfileUserResponseDTO(
                    user, imageUrl, ImageStatus.UPLOADED
            );

            assertThat(dto).isNotNull();
            assertThat(dto.idUser()).isEqualTo(7);
            assertThat(dto.userName()).isEqualTo("Juan Perez");
            assertThat(dto.userImage()).isEqualTo(imageUrl);
            assertThat(dto.imageStatus()).isEqualTo(ImageStatus.UPLOADED.name());
        }

        @Test
        @DisplayName("Debería mapear User a GetUserProfileResponseDTO con URL")
        void shouldMapUserToGetUserProfileResponseDtoWithUrl() {
            User user = User.createClient("Profile User", "getprofile@example.com", "hashedPass");
            user.setIdUser(8);
            String imageUrl = "https://cdn.example.com/getprofile.jpg";

            GetUserProfileResponseDTO dto = mapper.toGetUserProfileResponseDTO(
                    user, imageUrl, ImageStatus.UPLOADED
            );

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
            User user1 = User.createClient("User 1", "user1@example.com", "pass1");
            user1.setIdUser(1);
            User user2 = User.createClient("User 2", "user2@example.com", "pass2");
            user2.setIdUser(2);

            PageResponse<User> pageResponse = new PageResponse<>(
                    Arrays.asList(user1, user2), 0, 10, 2L, 1
            );
            ListUserResponseDTO dto = mapper.toListUserResponseDTO(pageResponse);

            assertThat(dto).isNotNull();
            assertThat(dto.users()).hasSize(2);
            assertThat(dto.totalElements()).isEqualTo(2L);
            assertThat(dto.totalPages()).isEqualTo(1);
            assertThat(dto.timestamp()).isNotNull();
        }

        @Test
        @DisplayName("Debería mapear PageResponse con contenido personalizado a ListUserResponseDTO")
        void shouldMapPageResponseWithCustomContentToListUserResponseDto() {
            User user1 = User.createClient("User 1", "user1@example.com", "pass1");
            user1.setIdUser(1);

            PageResponse<User> pageResponse = new PageResponse<>(
                    List.of(user1), 0, 10, 1L, 1
            );
            List<UserResponseDTO> customContent = List.of(
                    mapper.toUserResponseDTO(user1, "https://url1.com", ImageStatus.UPLOADED)
            );
            ListUserResponseDTO dto = mapper.toListUserResponseDTO(pageResponse, customContent);

            assertThat(dto).isNotNull();
            assertThat(dto.users()).hasSize(1);
            assertThat(dto.users().get(0).userImage()).isEqualTo("https://url1.com");
            assertThat(dto.totalElements()).isEqualTo(1L);
            assertThat(dto.totalPages()).isEqualTo(1);
        }

        @Test
        @DisplayName("Debería retornar null cuando PageResponse es null")
        void shouldReturnNullWhenPageResponseIsNull() {
            assertThat(mapper.toListUserResponseDTO(null)).isNull();
        }
    }

    @Nested
    @DisplayName("Pruebas de Delete DTOs")
    class DeleteDtosTests {

        @Test
        @DisplayName("Debería crear DeleteProfileUserDTO")
        void shouldCreateDeleteProfileUserDto() {
            DeleteProfileUserDTO dto = mapper.toDeleteProfileUserDTO(10);

            assertThat(dto).isNotNull();
            assertThat(dto.message()).isEqualTo("Perfil eliminado correctamente");
            assertThat(dto.idUser()).isEqualTo(10);
        }

        @Test
        @DisplayName("Debería crear DeleteUserByAdminDTO")
        void shouldCreateDeleteUserByAdminDto() {
            DeleteUserByAdminDTO dto = mapper.toDeleteUserByAdminDTO(20);

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
            MockMultipartFile file = new MockMultipartFile(
                    "testFile", "test.jpg", "image/jpeg", "test content".getBytes()
            );
            FileData fileData = mapper.multipartToFileData(file);

            assertThat(fileData).isNotNull();
            assertThat(fileData.originalFilename()).isEqualTo("test.jpg");
            assertThat(fileData.contentType()).isEqualTo("image/jpeg");
            assertThat(fileData.bytes()).isNotEmpty();
        }

        @Test
        @DisplayName("Debería retornar null cuando MultipartFile es null")
        void shouldReturnNullWhenMultipartFileIsNull() {
            assertThat(mapper.multipartToFileData(null)).isNull();
        }

        @Test
        @DisplayName("Debería retornar null cuando MultipartFile está vacío")
        void shouldReturnNullWhenMultipartFileIsEmpty() {
            MockMultipartFile emptyFile = new MockMultipartFile(
                    "emptyFile", "empty.jpg", "image/jpeg", new byte[0]
            );
            assertThat(mapper.multipartToFileData(emptyFile)).isNull();
        }

        @Test
        @DisplayName("Debería convertir String a Role CLIENT")
        void shouldConvertStringToClientRole() {
            assertThat(mapper.stringToRole("CLIENT")).isEqualTo(Role.CLIENT);
        }

        @Test
        @DisplayName("Debería convertir String a Role ADMIN")
        void shouldConvertStringToAdminRole() {
            assertThat(mapper.stringToRole("ADMIN")).isEqualTo(Role.ADMIN);
        }

        @Test
        @DisplayName("Debería convertir String a Role EMPLOYEE")
        void shouldConvertStringToEmployeeRole() {
            assertThat(mapper.stringToRole("EMPLOYEE")).isEqualTo(Role.EMPLOYEE);
        }

        @Test
        @DisplayName("Debería convertir String en minúsculas a Role")
        void shouldConvertLowercaseStringToRole() {
            assertThat(mapper.stringToRole("employee")).isEqualTo(Role.EMPLOYEE);
        }

        @Test
        @DisplayName("Debería retornar null cuando String de role es inválido")
        void shouldReturnNullWhenRoleStringIsInvalid() {
            assertThat(mapper.stringToRole("INVALID_ROLE")).isNull();
        }

        @Test
        @DisplayName("Debería retornar null cuando String de role es null")
        void shouldReturnNullWhenRoleStringIsNull() {
            assertThat(mapper.stringToRole(null)).isNull();
        }

        @Test
        @DisplayName("Debería convertir Role a String CLIENT")
        void shouldConvertClientRoleToString() {
            assertThat(mapper.roleToString(Role.CLIENT)).isEqualTo("CLIENT");
        }

        @Test
        @DisplayName("Debería convertir Role a String ADMIN")
        void shouldConvertAdminRoleToString() {
            assertThat(mapper.roleToString(Role.ADMIN)).isEqualTo("ADMIN");
        }

        @Test
        @DisplayName("Debería convertir Role a String EMPLOYEE")
        void shouldConvertEmployeeRoleToString() {
            assertThat(mapper.roleToString(Role.EMPLOYEE)).isEqualTo("EMPLOYEE");
        }

        @Test
        @DisplayName("Debería retornar null cuando Role es null")
        void shouldReturnNullWhenRoleIsNull() {
            assertThat(mapper.roleToString(null)).isNull();
        }
    }

    @Nested
    @DisplayName("Pruebas de ImageStatus")
    class ImageStatusTests {

        @Test
        @DisplayName("Debería manejar ImageStatus NONE")
        void shouldHandleImageStatusNone() {
            User user = User.createClient("None User", "none@example.com", "pass");
            user.setIdUser(1);

            UserResponseDTO dto = mapper.toUserResponseDTO(user, null, ImageStatus.NONE);

            assertThat(dto.imageStatus()).isEqualTo(ImageStatus.NONE);
            assertThat(dto.userImage()).isNull();
        }

        @Test
        @DisplayName("Debería manejar ImageStatus PENDING")
        void shouldHandleImageStatusPending() {
            User user = User.createClient("Pending User", "pending@example.com", "pass");
            user.setIdUser(1);

            UserResponseDTO dto = mapper.toUserResponseDTO(user, null, ImageStatus.PENDING);

            assertThat(dto.imageStatus()).isEqualTo(ImageStatus.PENDING);
        }

        @Test
        @DisplayName("Debería manejar ImageStatus UPLOADED")
        void shouldHandleImageStatusUploaded() {
            User user = User.createClient("Uploaded User", "uploaded@example.com", "pass");
            user.setIdUser(1);
            String imageUrl = "https://cdn.example.com/uploaded.jpg";

            UserResponseDTO dto = mapper.toUserResponseDTO(user, imageUrl, ImageStatus.UPLOADED);

            assertThat(dto.imageStatus()).isEqualTo(ImageStatus.UPLOADED);
            assertThat(dto.userImage()).isEqualTo(imageUrl);
        }

        @Test
        @DisplayName("Debería manejar ImageStatus FAILED")
        void shouldHandleImageStatusFailed() {
            User user = User.createClient("Failed User", "failed@example.com", "pass");
            user.setIdUser(1);

            UserResponseDTO dto = mapper.toUserResponseDTO(user, null, ImageStatus.FAILED);

            assertThat(dto.imageStatus()).isEqualTo(ImageStatus.FAILED);
            assertThat(dto.userImage()).isNull();
        }

        @Test
        @DisplayName("Debería probar todos los ImageStatus en CreateUserByAdminResponseDTO")
        void shouldTestAllImageStatusInCreateUserByAdminResponseDto() {
            User user = User.createByAdmin(
                    "Status Test", "status@example.com", "pass",
                    Role.CLIENT, null, null, null, 1
            );
            user.setIdUser(1);

            assertThat(mapper.toCreateUserByAdminResponseDTO(user, null, ImageStatus.NONE)
                    .imageStatus()).isEqualTo(ImageStatus.NONE);
            assertThat(mapper.toCreateUserByAdminResponseDTO(user, null, ImageStatus.PENDING)
                    .imageStatus()).isEqualTo(ImageStatus.PENDING);
            assertThat(mapper.toCreateUserByAdminResponseDTO(user, "https://url.com/img.jpg", ImageStatus.UPLOADED)
                    .imageStatus()).isEqualTo(ImageStatus.UPLOADED);
            assertThat(mapper.toCreateUserByAdminResponseDTO(user, null, ImageStatus.FAILED)
                    .imageStatus()).isEqualTo(ImageStatus.FAILED);
        }
    }

    @Nested
    @DisplayName("Pruebas de Casos Especiales")
    class SpecialCasesTests {

        @Test
        @DisplayName("Debería manejar CreateUserByAdminRequestDTO con nombre mínimo de 3 caracteres")
        void shouldHandleCreateUserByAdminRequestDtoWithMinimumNameLength() {
            // User.validateUserName exige mínimo 3 caracteres
            CreateUserByAdminRequestDTO dto = new CreateUserByAdminRequestDTO(
                    "Joe", "joe@example.com", "Pass123456", null, "CLIENT", null
            );
            CreateUserByAdminCommand command = mapper.toCreateUserByAdminCommand(dto, null, 1);

            assertThat(command).isNotNull();
            assertThat(command.userName()).hasSize(3);
        }

        @Test
        @DisplayName("Debería manejar UpdateProfileUserRequestDTO con nombre de 3 caracteres")
        void shouldHandleUpdateProfileUserRequestDtoWithThreeCharName() {
            UpdateProfileUserRequestDTO dto = new UpdateProfileUserRequestDTO(
                    "Bob", "NewPass123", null
            );
            UpdateProfileUserCommand command = mapper.toUpdateProfileUserCommand(1, dto);

            assertThat(command.userName()).hasSize(3);
        }

        @Test
        @DisplayName("Debería manejar User con todos los roles en UserResponseDTO")
        void shouldHandleUserWithAllRolesInUserResponseDto() {
            for (Role role : Role.values()) {
                User user = User.createByAdmin(
                        "Test User", "test@example.com", "pass",
                        role, null, null, null, 1
                );
                user.setIdUser(1);

                UserResponseDTO dto = mapper.toUserResponseDTO(user);

                assertThat(dto.role()).isEqualTo(role.name());
            }
        }

        @Test
        @DisplayName("Debería manejar CreateUserByAdminRequestDTO con ejemplo de email")
        void shouldHandleCreateUserByAdminRequestDtoWithExampleEmail() {
            CreateUserByAdminRequestDTO dto = new CreateUserByAdminRequestDTO(
                    "joseComeGordas", "jose56@gmail.com", "amogorda1234",
                    null, "EMPLOYEE", "3017342342"
            );
            CreateUserByAdminCommand command = mapper.toCreateUserByAdminCommand(dto, null, 1);

            assertThat(command.email()).isEqualTo("jose56@gmail.com");
            assertThat(command.phone()).isEqualTo("3017342342");
        }
    }
}
