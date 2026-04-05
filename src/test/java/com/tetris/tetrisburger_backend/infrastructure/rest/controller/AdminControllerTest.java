//package com.tetris.tetrisburger_backend.infrastructure.rest.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.tetris.tetrisburger_backend.domain.common.ImageStatus;
//import com.tetris.tetrisburger_backend.domain.common.PageResponse;
//import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
//import com.tetris.tetrisburger_backend.domain.exception.UnauthorizedException;
//import com.tetris.tetrisburger_backend.domain.model.User;
//import com.tetris.tetrisburger_backend.domain.port.in.user.*;
//import com.tetris.tetrisburger_backend.domain.port.in.user.command.*;
//import com.tetris.tetrisburger_backend.domain.port.in.user.query.ListUsersQuery;
//import com.tetris.tetrisburger_backend.domain.port.in.user.query.SearchUsersByEmailQuery;
//import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
//import com.tetris.tetrisburger_backend.infrastructure.rest.dto.user.*;
//import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.UserRestDtoMapper;
//import com.tetris.tetrisburger_backend.infrastructure.rest.validator.ImageValidator;
//import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.core.MethodParameter;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.bind.support.WebDataBinderFactory;
//import org.springframework.web.context.request.NativeWebRequest;
//import org.springframework.web.method.support.HandlerMethodArgumentResolver;
//import org.springframework.web.method.support.ModelAndViewContainer;
//
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(MockitoExtension.class)
//@DisplayName("AdminController — Pruebas Unitarias")
//class AdminControllerTest {
//
//    private MockMvc mockMvc;
//    private final ObjectMapper objectMapper = new ObjectMapper()
//            .registerModule(new JavaTimeModule());
//
//    @Mock private CreateUserByAdmin      createUserByAdmin;
//    @Mock private ListUser               listUser;
//    @Mock private GetUserById            getUserById;
//    @Mock private UpdateUserByAdmin      updateUserByAdmin;
//    @Mock private UpdateUserImageByAdmin updateUserImageByAdmin;
//    @Mock private DeleteUserByAdmin      deleteUserByAdmin;
//    @Mock private SearchUsersByEmail     searchUsersByEmail;
//    @Mock private UserRestDtoMapper      mapper;
//    @Mock private ImageStoragePort       imageStoragePort;
//    @Mock private ImageValidator         imageValidator;
//
//    @InjectMocks
//    private AdminController controller;
//
//    private CustomUserDetails mockUserDetails;
//    private User              mockUser;
//
//    private HandlerMethodArgumentResolver authPrincipalResolver() {
//        return new HandlerMethodArgumentResolver() {
//            @Override
//            public boolean supportsParameter(MethodParameter p) {
//                return p.getParameterAnnotation(AuthenticationPrincipal.class) != null;
//            }
//            @Override
//            public Object resolveArgument(MethodParameter p, ModelAndViewContainer mvc,
//                                          NativeWebRequest req, WebDataBinderFactory binder) {
//                return mockUserDetails;
//            }
//        };
//    }
//
//    @BeforeEach
//    void setUp() {
//        mockUserDetails = mock(CustomUserDetails.class);
//        lenient().when(mockUserDetails.getId()).thenReturn(1);
//
//        mockMvc = MockMvcBuilders
//                .standaloneSetup(controller)
//                .setCustomArgumentResolvers(authPrincipalResolver())
//                .build();
//
//        mockUser = mock(User.class);
//        lenient().when(mockUser.getUserImageKey()).thenReturn(null);
//    }
//
//    // ================================================================
//    //  POST /api/admin/users
//    // ================================================================
//    @Nested
//    @DisplayName("POST /api/admin/users — Crear usuario")
//    class CreateUserTests {
//
//        @Test
//        @DisplayName("201 · Sin imagen: imageValidator NO se llama, imageStatus=NONE")
//        void shouldCreateUserWithoutImageAndReturn201() throws Exception {
//            when(mapper.toCreateUserByAdminCommand(any(), isNull(), eq(1))).thenReturn(mock());
//            when(createUserByAdmin.handle(any())).thenReturn(mockUser);
//            when(mapper.toCreateUserByAdminResponseDTO(eq(mockUser), isNull(), eq(ImageStatus.NONE)))
//                    .thenReturn(mock(CreateUserByAdminResponseDTO.class));
//
//            MockMultipartFile dataPart = new MockMultipartFile(
//                    "data", "", MediaType.APPLICATION_JSON_VALUE,
//                    objectMapper.writeValueAsBytes(
//                            new CreateUserByAdminRequestDTO("Juan", "Pérez", "juan@test.com", "pass123", "ROLE_USER"))
//            );
//
//            mockMvc.perform(multipart("/api/admin/users").file(dataPart))
//                    .andExpect(status().isCreated());
//
//            verify(createUserByAdmin).handle(any());
//            verify(imageValidator, never()).validate(any());
//            verify(mapper).toCreateUserByAdminResponseDTO(mockUser, null, ImageStatus.NONE);
//        }
//
//        @Test
//        @DisplayName("201 · Con imagen y key=null: imageValidator se llama, imageStatus=PENDING")
//        void shouldCreateUserWithImageKeyNullReturnPending() throws Exception {
//            when(mapper.toCreateUserByAdminCommand(any(), any(), eq(1))).thenReturn(mock());
//            when(createUserByAdmin.handle(any())).thenReturn(mockUser);
//            when(mapper.toCreateUserByAdminResponseDTO(eq(mockUser), isNull(), eq(ImageStatus.PENDING)))
//                    .thenReturn(mock(CreateUserByAdminResponseDTO.class));
//
//            MockMultipartFile dataPart = new MockMultipartFile(
//                    "data", "", MediaType.APPLICATION_JSON_VALUE,
//                    objectMapper.writeValueAsBytes(
//                            new CreateUserByAdminRequestDTO("Juan", "Pérez", "juan@test.com", "pass123", "ROLE_USER"))
//            );
//            MockMultipartFile imgPart = new MockMultipartFile(
//                    "userImage", "avatar.png", MediaType.IMAGE_PNG_VALUE, "bytes".getBytes()
//            );
//
//            mockMvc.perform(multipart("/api/admin/users").file(dataPart).file(imgPart))
//                    .andExpect(status().isCreated());
//
//            verify(imageValidator).validate(any());
//            verify(mapper).toCreateUserByAdminResponseDTO(mockUser, null, ImageStatus.PENDING);
//        }
//
//        @Test
//        @DisplayName("201 · Con imagen y key guardada: imageStatus=UPLOADED")
//        void shouldReturnUploadedWhenImageKeySaved() throws Exception {
//            when(mockUser.getUserImageKey()).thenReturn("users/juan-avatar.png");
//            when(imageStoragePort.getImageUrl("users/juan-avatar.png"))
//                    .thenReturn("https://cdn.example.com/users/juan-avatar.png");
//            when(mapper.toCreateUserByAdminCommand(any(), any(), eq(1))).thenReturn(mock());
//            when(createUserByAdmin.handle(any())).thenReturn(mockUser);
//            when(mapper.toCreateUserByAdminResponseDTO(
//                    eq(mockUser),
//                    eq("https://cdn.example.com/users/juan-avatar.png"),
//                    eq(ImageStatus.UPLOADED)))
//                    .thenReturn(mock(CreateUserByAdminResponseDTO.class));
//
//            MockMultipartFile dataPart = new MockMultipartFile(
//                    "data", "", MediaType.APPLICATION_JSON_VALUE,
//                    objectMapper.writeValueAsBytes(
//                            new CreateUserByAdminRequestDTO("Juan", "Pérez", "juan@test.com", "pass123", "ROLE_USER"))
//            );
//            MockMultipartFile imgPart = new MockMultipartFile(
//                    "userImage", "avatar.png", MediaType.IMAGE_PNG_VALUE, "bytes".getBytes()
//            );
//
//            mockMvc.perform(multipart("/api/admin/users").file(dataPart).file(imgPart))
//                    .andExpect(status().isCreated());
//
//            verify(mapper).toCreateUserByAdminResponseDTO(
//                    mockUser, "https://cdn.example.com/users/juan-avatar.png", ImageStatus.UPLOADED);
//        }
//
//        @Test
//        @DisplayName("201 · Archivo vacío tratado como sin imagen (imageWasSent=false)")
//        void shouldTreatEmptyFileAsNoImage() throws Exception {
//            when(mapper.toCreateUserByAdminCommand(any(), any(), eq(1))).thenReturn(mock());
//            when(createUserByAdmin.handle(any())).thenReturn(mockUser);
//            when(mapper.toCreateUserByAdminResponseDTO(eq(mockUser), isNull(), eq(ImageStatus.NONE)))
//                    .thenReturn(mock(CreateUserByAdminResponseDTO.class));
//
//            MockMultipartFile dataPart = new MockMultipartFile(
//                    "data", "", MediaType.APPLICATION_JSON_VALUE,
//                    objectMapper.writeValueAsBytes(
//                            new CreateUserByAdminRequestDTO("Juan", "Pérez", "juan@test.com", "pass123", "ROLE_USER"))
//            );
//            MockMultipartFile emptyImg = new MockMultipartFile(
//                    "userImage", "", MediaType.IMAGE_PNG_VALUE, new byte[0]
//            );
//
//            mockMvc.perform(multipart("/api/admin/users").file(dataPart).file(emptyImg))
//                    .andExpect(status().isCreated());
//
//            verify(imageValidator, never()).validate(any());
//            verify(mapper).toCreateUserByAdminResponseDTO(mockUser, null, ImageStatus.NONE);
//        }
//    }
//
//    // ================================================================
//    //  GET /api/admin/users
//    // ================================================================
//    @Nested
//    @DisplayName("GET /api/admin/users — Listar usuarios")
//    class ListUsersTests {
//
//        @Test
//        @DisplayName("200 · Parámetros por defecto, pasa ListUsersQuery al use-case")
//        void shouldListUsersWithDefaultParams() throws Exception {
//            PageResponse<User> page = new PageResponse<>(List.of(mockUser), 0, 10, 1L, 1);
//            when(listUser.execute(any(ListUsersQuery.class))).thenReturn(page);
//            when(mapper.toUserResponseDTO(eq(mockUser), isNull(), eq(ImageStatus.NONE)))
//                    .thenReturn(mock(UserResponseDTO.class));
//
//            mockMvc.perform(get("/api/admin/users")).andExpect(status().isOk());
//
//            verify(listUser).execute(any(ListUsersQuery.class));
//            verify(mapper).toUserResponseDTO(mockUser, null, ImageStatus.NONE);
//        }
//
//        @Test
//        @DisplayName("200 · Usuario con imagen → imageStoragePort resuelve URL, imageStatus=UPLOADED")
//        void shouldResolveUrlForUsersWithImage() throws Exception {
//            when(mockUser.getUserImageKey()).thenReturn("users/avatar.png");
//            when(imageStoragePort.getImageUrl("users/avatar.png"))
//                    .thenReturn("https://cdn.example.com/users/avatar.png");
//
//            PageResponse<User> page = new PageResponse<>(List.of(mockUser), 0, 10, 1L, 1);
//            when(listUser.execute(any())).thenReturn(page);
//            when(mapper.toUserResponseDTO(
//                    eq(mockUser), eq("https://cdn.example.com/users/avatar.png"), eq(ImageStatus.UPLOADED)))
//                    .thenReturn(mock(UserResponseDTO.class));
//
//            mockMvc.perform(get("/api/admin/users")).andExpect(status().isOk());
//
//            verify(imageStoragePort).getImageUrl("users/avatar.png");
//            verify(mapper).toUserResponseDTO(
//                    mockUser, "https://cdn.example.com/users/avatar.png", ImageStatus.UPLOADED);
//        }
//
//        @Test
//        @DisplayName("200 · Paginación personalizada se construye correctamente en ListUsersQuery")
//        void shouldPassCustomPaginationQuery() throws Exception {
//            PageResponse<User> page = new PageResponse<>(List.of(), 2, 5, 0L, 0);
//            when(listUser.execute(any(ListUsersQuery.class))).thenReturn(page);
//
//            mockMvc.perform(get("/api/admin/users")
//                            .param("page", "2")
//                            .param("size", "5")
//                            .param("sortBy", "email"))
//                    .andExpect(status().isOk());
//
//            verify(listUser).execute(argThat(q ->
//                    q.page() == 2 && q.size() == 5 && q.sortBy().equals("email")));
//        }
//
//        @Test
//        @DisplayName("200 · Lista vacía: mapper nunca se llama")
//        void shouldNotCallMapperWhenEmpty() throws Exception {
//            when(listUser.execute(any())).thenReturn(new PageResponse<>(List.of(), 0, 10, 0L, 0));
//
//            mockMvc.perform(get("/api/admin/users")).andExpect(status().isOk());
//
//            verify(mapper, never()).toUserResponseDTO(any(), any(), any());
//        }
//    }
//
//    // ================================================================
//    //  GET /api/admin/users/{id}
//    // ================================================================
//    @Nested
//    @DisplayName("GET /api/admin/users/{id} — Obtener usuario por ID")
//    class GetUserByIdTests {
//
//        @Test
//        @DisplayName("200 · Sin imagen → imageUrl=null, imageStatus=NONE")
//        void shouldReturnUserWithoutImage() throws Exception {
//            when(getUserById.handle(5)).thenReturn(mockUser);
//            when(mapper.toUserResponseDTO(mockUser, null, ImageStatus.NONE))
//                    .thenReturn(mock(UserResponseDTO.class));
//
//            mockMvc.perform(get("/api/admin/users/5")).andExpect(status().isOk());
//
//            verify(getUserById).handle(5);
//            verify(mapper).toUserResponseDTO(mockUser, null, ImageStatus.NONE);
//        }
//
//        @Test
//        @DisplayName("200 · Con imagen → imageUrl resuelto, imageStatus=UPLOADED")
//        void shouldReturnUserWithImage() throws Exception {
//            when(mockUser.getUserImageKey()).thenReturn("users/foto.png");
//            when(imageStoragePort.getImageUrl("users/foto.png"))
//                    .thenReturn("https://cdn.example.com/users/foto.png");
//            when(getUserById.handle(3)).thenReturn(mockUser);
//            when(mapper.toUserResponseDTO(
//                    mockUser, "https://cdn.example.com/users/foto.png", ImageStatus.UPLOADED))
//                    .thenReturn(mock(UserResponseDTO.class));
//
//            mockMvc.perform(get("/api/admin/users/3")).andExpect(status().isOk());
//
//            verify(imageStoragePort).getImageUrl("users/foto.png");
//            verify(mapper).toUserResponseDTO(
//                    mockUser, "https://cdn.example.com/users/foto.png", ImageStatus.UPLOADED);
//        }
//
//        @Test
//        @DisplayName("200 · El ID del path se pasa exactamente al use-case")
//        void shouldPassExactIdToUseCase() throws Exception {
//            when(getUserById.handle(42)).thenReturn(mockUser);
//            when(mapper.toUserResponseDTO(any(), any(), any())).thenReturn(mock(UserResponseDTO.class));
//
//            mockMvc.perform(get("/api/admin/users/42")).andExpect(status().isOk());
//
//            verify(getUserById).handle(42);
//            verify(getUserById, never()).handle(intThat(id -> id != 42));
//        }
//    }
//
//    // ================================================================
//    //  PUT /api/admin/users/{id}
//    // ================================================================
//    @Nested
//    @DisplayName("PUT /api/admin/users/{id} — Actualizar datos de usuario")
//    class UpdateUserTests {
//
//        @Test
//        @DisplayName("200 · imageWasSent=false siempre → imageStatus depende solo de la key")
//        void shouldUpdateAndReturnNoneWhenNoKey() throws Exception {
//            when(mapper.toUpdateUserByAdminCommand(eq(1), any(), eq(1))).thenReturn(mock());
//            when(updateUserByAdmin.handle(any())).thenReturn(mockUser);
//            when(mapper.toUpdateUserByAdminResponseDTO(mockUser, null, ImageStatus.NONE))
//                    .thenReturn(mock(UpdateUserByAdminResponseDTO.class));
//
//            mockMvc.perform(put("/api/admin/users/1")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content("{}"))
//                    .andExpect(status().isOk());
//
//            verify(updateUserByAdmin).handle(any());
//            verify(mapper).toUpdateUserByAdminResponseDTO(mockUser, null, ImageStatus.NONE);
//        }
//
//        @Test
//        @DisplayName("200 · adminId del token se propaga correctamente al command")
//        void shouldPropagateAdminIdToCommand() throws Exception {
//            when(mockUserDetails.getId()).thenReturn(99);
//            when(mapper.toUpdateUserByAdminCommand(eq(7), any(), eq(99))).thenReturn(mock());
//            when(updateUserByAdmin.handle(any())).thenReturn(mockUser);
//            when(mapper.toUpdateUserByAdminResponseDTO(any(), any(), any()))
//                    .thenReturn(mock(UpdateUserByAdminResponseDTO.class));
//
//            mockMvc.perform(put("/api/admin/users/7")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content("{}"))
//                    .andExpect(status().isOk());
//
//            verify(mapper).toUpdateUserByAdminCommand(eq(7), any(), eq(99));
//        }
//
//        @Test
//        @DisplayName("415 · Content-Type incorrecto")
//        void shouldReturn415WhenWrongContentType() throws Exception {
//            mockMvc.perform(put("/api/admin/users/1")
//                            .contentType(MediaType.TEXT_PLAIN)
//                            .content("texto"))
//                    .andExpect(status().isUnsupportedMediaType());
//
//            verifyNoInteractions(updateUserByAdmin);
//        }
//    }
//
//    // ================================================================
//    //  PUT /api/admin/users/{id}/image
//    // ================================================================
//    @Nested
//    @DisplayName("PUT /api/admin/users/{id}/image — Actualizar imagen")
//    class UpdateUserImageTests {
//
//        @Test
//        @DisplayName("200 · imageValidator.validate() se llama, imageStatus siempre=PENDING")
//        void shouldUpdateImageAndReturnPending() throws Exception {
//            when(mapper.toUpdateUserImageByAdminCommand(eq(1), any(), eq(1))).thenReturn(mock());
//            when(updateUserImageByAdmin.handle(any())).thenReturn(mockUser);
//            when(mapper.toUpdateUserByAdminResponseDTO(mockUser, null, ImageStatus.PENDING))
//                    .thenReturn(mock(UpdateUserByAdminResponseDTO.class));
//
//            MockMultipartFile imgPart = new MockMultipartFile(
//                    "userImage", "foto.png", MediaType.IMAGE_PNG_VALUE, "bytes".getBytes()
//            );
//
//            mockMvc.perform(multipart("/api/admin/users/1/image")
//                            .file(imgPart)
//                            .with(req -> { req.setMethod("PUT"); return req; }))
//                    .andExpect(status().isOk());
//
//            verify(imageValidator).validate(any());
//            verify(updateUserImageByAdmin).handle(any());
//            // Este endpoint hardcodea PENDING sin importar la key
//            verify(mapper).toUpdateUserByAdminResponseDTO(mockUser, null, ImageStatus.PENDING);
//        }
//
//        @Test
//        @DisplayName("200 · adminId del token se pasa correctamente al command")
//        void shouldPropagateAdminIdToImageCommand() throws Exception {
//            when(mockUserDetails.getId()).thenReturn(55);
//            when(mapper.toUpdateUserImageByAdminCommand(eq(8), any(), eq(55))).thenReturn(mock());
//            when(updateUserImageByAdmin.handle(any())).thenReturn(mockUser);
//            when(mapper.toUpdateUserByAdminResponseDTO(any(), any(), any()))
//                    .thenReturn(mock(UpdateUserByAdminResponseDTO.class));
//
//            MockMultipartFile imgPart = new MockMultipartFile(
//                    "userImage", "foto.png", MediaType.IMAGE_PNG_VALUE, "bytes".getBytes()
//            );
//
//            mockMvc.perform(multipart("/api/admin/users/8/image")
//                            .file(imgPart)
//                            .with(req -> { req.setMethod("PUT"); return req; }))
//                    .andExpect(status().isOk());
//
//            verify(mapper).toUpdateUserImageByAdminCommand(eq(8), any(), eq(55));
//        }
//
//        @Test
//        @DisplayName("400 · Sin parte userImage Spring rechaza la solicitud")
//        void shouldReturn400WhenImagePartMissing() throws Exception {
//            mockMvc.perform(multipart("/api/admin/users/1/image")
//                            .with(req -> { req.setMethod("PUT"); return req; }))
//                    .andExpect(status().isBadRequest());
//
//            verifyNoInteractions(updateUserImageByAdmin);
//            verifyNoInteractions(imageValidator);
//        }
//    }
//
//    // ================================================================
//    //  DELETE /api/admin/users/{id}
//    // ================================================================
//    @Nested
//    @DisplayName("DELETE /api/admin/users/{id} — Eliminar usuario")
//    class DeleteUserTests {
//
//        @Test
//        @DisplayName("200 · Command construido con userId e adminId correctos")
//        void shouldDeleteWithCorrectCommand() throws Exception {
//            when(mapper.toDeleteUserByAdminDTO(3)).thenReturn(mock(DeleteUserByAdminDTO.class));
//
//            mockMvc.perform(delete("/api/admin/users/3")).andExpect(status().isOk());
//
//            verify(deleteUserByAdmin).handle(argThat(cmd ->
//                    cmd.idUser() == 3 && cmd.idUser() == 1));
//        }
//
//        @Test
//        @DisplayName("200 · adminId del token cambia correctamente en el command")
//        void shouldIncludeCorrectAdminIdFromToken() throws Exception {
//            when(mockUserDetails.getId()).thenReturn(77);
//            when(mapper.toDeleteUserByAdminDTO(10)).thenReturn(mock(DeleteUserByAdminDTO.class));
//
//            mockMvc.perform(delete("/api/admin/users/10")).andExpect(status().isOk());
//
//            verify(deleteUserByAdmin).handle(argThat(cmd ->
//                    cmd.userId() == 10 && cmd.adminId() == 77));
//        }
//
//        @Test
//        @DisplayName("200 · mapper.toDeleteUserByAdminDTO recibe el id correcto del path")
//        void shouldCallMapperWithCorrectId() throws Exception {
//            when(mapper.toDeleteUserByAdminDTO(15)).thenReturn(mock(DeleteUserByAdminDTO.class));
//
//            mockMvc.perform(delete("/api/admin/users/15")).andExpect(status().isOk());
//
//            verify(mapper).toDeleteUserByAdminDTO(15);
//        }
//    }
//
//    // ================================================================
//    //  GET /api/admin/users/by-email
//    // ================================================================
//    @Nested
//    @DisplayName("GET /api/admin/users/by-email — Buscar por email")
//    class SearchByEmailTests {
//
//        @Test
//        @DisplayName("200 · Pasa email en SearchUsersByEmailQuery y PaginationRequest correctos")
//        void shouldSearchByEmailAndReturn200() throws Exception {
//            PageResponse<User> page = new PageResponse<>(List.of(mockUser), 0, 10, 1L, 1);
//            when(searchUsersByEmail.handle(any(SearchUsersByEmailQuery.class), any(PaginationRequest.class)))
//                    .thenReturn(page);
//            when(mapper.toUserResponseDTO(any(), any(), any())).thenReturn(mock(UserResponseDTO.class));
//
//            mockMvc.perform(get("/api/admin/users/by-email").param("email", "juan@test.com"))
//                    .andExpect(status().isOk());
//
//            verify(searchUsersByEmail).handle(
//                    argThat(q -> q.email().equals("juan@test.com")),
//                    any(PaginationRequest.class));
//        }
//
//        @Test
//        @DisplayName("200 · Paginación personalizada se construye en PaginationRequest")
//        void shouldApplyCustomPagination() throws Exception {
//            when(searchUsersByEmail.handle(any(), any()))
//                    .thenReturn(new PageResponse<>(List.of(), 1, 5, 0L, 0));
//
//            mockMvc.perform(get("/api/admin/users/by-email")
//                            .param("email", "pedro@test.com")
//                            .param("page", "1")
//                            .param("size", "5")
//                            .param("sortBy", "email"))
//                    .andExpect(status().isOk());
//
//            verify(searchUsersByEmail).handle(
//                    any(SearchUsersByEmailQuery.class),
//                    argThat(r -> r.page() == 1 && r.size() == 5 && r.sortBy().equals("email")));
//        }
//
//        @Test
//        @DisplayName("200 · Página vacía cuando no hay coincidencias (no 404)")
//        void shouldReturnEmptyPageNotNotFound() throws Exception {
//            when(searchUsersByEmail.handle(any(), any()))
//                    .thenReturn(new PageResponse<>(List.of(), 0, 10, 0L, 0));
//
//            mockMvc.perform(get("/api/admin/users/by-email").param("email", "noexiste@test.com"))
//                    .andExpect(status().isOk());
//
//            verify(mapper, never()).toUserResponseDTO(any(), any(), any());
//        }
//
//        @Test
//        @DisplayName("400 · Sin parámetro email Spring rechaza la solicitud")
//        void shouldReturn400WhenEmailParamMissing() throws Exception {
//            mockMvc.perform(get("/api/admin/users/by-email"))
//                    .andExpect(status().isBadRequest());
//
//            verifyNoInteractions(searchUsersByEmail);
//        }
//    }
//
//    // ================================================================
//    //  resolveImageStatus — casos de la lógica privada
//    // ================================================================
//    @Nested
//    @DisplayName("resolveImageStatus — Verificación de estados vía endpoint POST")
//    class ResolveImageStatusTests {
//
//        private void triggerCreateNoImage(String imageKey) throws Exception {
//            when(mockUser.getUserImageKey()).thenReturn(imageKey);
//            if (imageKey != null && !imageKey.isBlank()) {
//                when(imageStoragePort.getImageUrl(imageKey)).thenReturn("https://cdn/" + imageKey);
//            }
//            when(mapper.toCreateUserByAdminCommand(any(), any(), anyInt())).thenReturn(mock());
//            when(createUserByAdmin.handle(any())).thenReturn(mockUser);
//            when(mapper.toCreateUserByAdminResponseDTO(any(), any(), any()))
//                    .thenReturn(mock(CreateUserByAdminResponseDTO.class));
//
//            MockMultipartFile data = new MockMultipartFile(
//                    "data", "", MediaType.APPLICATION_JSON_VALUE,
//                    objectMapper.writeValueAsBytes(
//                            new CreateUserByAdminRequestDTO("A", "B", "a@b.com", "pass", "ROLE_USER"))
//            );
//            mockMvc.perform(multipart("/api/admin/users").file(data));
//        }
//
//        @Test
//        @DisplayName("imageWasSent=false + key=null → NONE")
//        void notSentKeyNull_shouldBeNone() throws Exception {
//            triggerCreateNoImage(null);
//            verify(mapper).toCreateUserByAdminResponseDTO(any(), any(), eq(ImageStatus.NONE));
//        }
//
//        @Test
//        @DisplayName("imageWasSent=false + key blank → NONE")
//        void notSentKeyBlank_shouldBeNone() throws Exception {
//            triggerCreateNoImage("   ");
//            verify(mapper).toCreateUserByAdminResponseDTO(any(), any(), eq(ImageStatus.NONE));
//        }
//
//        @Test
//        @DisplayName("imageWasSent=false + key presente → UPLOADED")
//        void notSentKeyPresent_shouldBeUploaded() throws Exception {
//            triggerCreateNoImage("users/avatar.png");
//            verify(mapper).toCreateUserByAdminResponseDTO(any(), any(), eq(ImageStatus.UPLOADED));
//        }
//    }
//}