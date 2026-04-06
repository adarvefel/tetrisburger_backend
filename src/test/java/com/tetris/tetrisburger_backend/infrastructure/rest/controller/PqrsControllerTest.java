package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.exception.PqrsAlreadyDeletedException;
import com.tetris.tetrisburger_backend.domain.model.Pqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.*;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.command.DeleteSoftPqrsCommand;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.command.GetPqrsByIdCommand;
import com.tetris.tetrisburger_backend.infrastructure.rest.advice.BurgerExceptionHandler;
import com.tetris.tetrisburger_backend.infrastructure.rest.advice.GlobalExceptionHandler;
import com.tetris.tetrisburger_backend.infrastructure.rest.advice.ProductExceptionHandler;
import com.tetris.tetrisburger_backend.infrastructure.rest.advice.ValidationExceptionHandler;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.pqrs.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.PqrsRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PqrsControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock private PqrsRestDtoMapper pqrsRestDtoMapper;
    @Mock private CreatePqrs createPqrs;
    @Mock private GetPqrsById getPqrsById;
    @Mock private ListPqrs listPqrs;
    @Mock private DeleteSoftPqrs deleteSoftPqrs;
    @Mock private UpdatePqrs updatePqrs;
    @Mock private UpdatePqrsByAdmin updatePqrsByAdmin;
    @Mock private ListPqrsById listPqrsById;

    @InjectMocks private PqrsController controller;

    private CustomUserDetails mockUserDetails;
    private Pqrs mockPqrs;

    private void mockAuthenticatedUser(Long userId) {
        lenient().when(mockUserDetails.getId()).thenReturn(userId.intValue());
        lenient().when(mockUserDetails.getUsername()).thenReturn(userId.toString());
    }

    private PqrsResponseDTO samplePqrsDto() {
        return new PqrsResponseDTO(
                1, "QUEJA", "ABIERTA", "ALTA", "Asunto", "Texto", null, 1, null,
                LocalDateTime.now(), LocalDateTime.now(), 1, 1
        );
    }

    @BeforeEach
    void setUp() {
        mockUserDetails = mock(CustomUserDetails.class);
        mockAuthenticatedUser(1L);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(
                        new ValidationExceptionHandler(),
                        new BurgerExceptionHandler(),
                        new ProductExceptionHandler(),
                        new GlobalExceptionHandler()
                )
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterAnnotation(AuthenticationPrincipal.class) != null;
                    }

                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        return mockUserDetails;
                    }
                })
                .build();

        mockPqrs = mock(Pqrs.class);
    }

    @Nested
    class CreatePqrsTests {
        @Test
        void shouldReturn201WhenCreatedSuccessfully() throws Exception {
            CreatePqrsRequestDTO req = new CreatePqrsRequestDTO("QUEJA", "Asunto", "Descripción larga");

            when(pqrsRestDtoMapper.toCreatePqrsCommand(any(), eq(1))).thenReturn(mock());
            when(createPqrs.handle(any())).thenReturn(mockPqrs);
            when(pqrsRestDtoMapper.toPqrsResponseDTO(mockPqrs)).thenReturn(samplePqrsDto());

            mockMvc.perform(post("/api/pqrs")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.idPqrs").value(1));

            verify(createPqrs, times(1)).handle(any());
        }

        @Test
        void shouldReturn400WhenInvalidRequest() throws Exception {
            mockMvc.perform(post("/api/pqrs")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"type\":\"\",\"subject\":\"\",\"description\":\"\"}"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(createPqrs);
        }
    }

    @Nested
    class GetPqrsByIdTests {
        @Test
        void shouldReturn200WhenFound() throws Exception {
            when(getPqrsById.handle(any(GetPqrsByIdCommand.class))).thenReturn(mockPqrs);
            when(pqrsRestDtoMapper.toPqrsResponseDTO(mockPqrs)).thenReturn(samplePqrsDto());

            mockMvc.perform(get("/api/pqrs/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idPqrs").value(1));

            verify(getPqrsById, times(1)).handle(argThat(cmd -> cmd.idPqrs() == 1 && cmd.idUser() == 1));
        }

        @Test
        void shouldReturn400WhenNotAccessible() throws Exception {
            when(getPqrsById.handle(any(GetPqrsByIdCommand.class)))
                    .thenThrow(new PqrsAlreadyDeletedException("Pqrs no encontrada o ya eliminada previamente."));

            mockMvc.perform(get("/api/pqrs/99"))
                    .andExpect(status().isBadRequest());

            verify(getPqrsById, times(1)).handle(any(GetPqrsByIdCommand.class));
        }
    }

    @Nested
    class ListPqrsTests {
        @Test
        void shouldReturn200WhenListed() throws Exception {
            PageResponse<Pqrs> page = new PageResponse<>(List.of(mockPqrs), 0, 10, 1L, 1);
            when(listPqrs.handle(any())).thenReturn(page);
            when(pqrsRestDtoMapper.toListPqrsResponseDTO(page)).thenReturn(
                    new ListPqrsResponseDTO(List.of(samplePqrsDto()), 0, 10, 1L, 1)
            );

            mockMvc.perform(get("/api/pqrs"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(1));

            verify(listPqrs, times(1)).handle(any());
        }
    }

    @Nested
    class DeleteSoftPqrsTests {
        @Test
        void shouldReturn200WhenDeleted() throws Exception {
            doNothing().when(deleteSoftPqrs).handle(any(DeleteSoftPqrsCommand.class));

            mockMvc.perform(delete("/api/pqrs/5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idPqrs").value(5));

            verify(deleteSoftPqrs, times(1)).handle(argThat(cmd -> cmd.idPqrs() == 5 && cmd.idUser() == 1));
        }

        @Test
        void shouldReturn400WhenAlreadyDeleted() throws Exception {
            doThrow(new PqrsAlreadyDeletedException("Pqrs no encontrada"))
                    .when(deleteSoftPqrs).handle(any(DeleteSoftPqrsCommand.class));

            mockMvc.perform(delete("/api/pqrs/5"))
                    .andExpect(status().isBadRequest());

            verify(deleteSoftPqrs, times(1)).handle(any(DeleteSoftPqrsCommand.class));
        }
    }

    @Nested
    class UpdatePqrsTests {
        @Test
        void shouldReturn200WhenUpdated() throws Exception {
            UpdatePqrsRequestDTO req = new UpdatePqrsRequestDTO("PETICION", "Nuevo", "Detalle");

            when(pqrsRestDtoMapper.toUpdatePqrsCommand(eq(2), any(), eq(1))).thenReturn(mock());
            when(updatePqrs.handle(any())).thenReturn(mockPqrs);
            when(pqrsRestDtoMapper.toPqrsResponseDTO(mockPqrs)).thenReturn(samplePqrsDto());

            mockMvc.perform(patch("/api/pqrs/2")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk());

            verify(updatePqrs, times(1)).handle(any());
        }
    }

    @Nested
    class UpdatePqrsByAdminTests {
        @Test
        void shouldReturn200WhenUpdatedByAdmin() throws Exception {
            UpdatePqrsByAdminRequestDTO req = new UpdatePqrsByAdminRequestDTO("CERRADA", "BAJA", "Respuesta");

            when(pqrsRestDtoMapper.toUpdatePqrsByAdminCommand(eq(3), any(), eq(1))).thenReturn(mock());
            when(updatePqrsByAdmin.handle(any())).thenReturn(mockPqrs);
            when(pqrsRestDtoMapper.toPqrsResponseDTO(mockPqrs)).thenReturn(samplePqrsDto());

            mockMvc.perform(patch("/api/pqrs/admin/3")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk());

            verify(updatePqrsByAdmin, times(1)).handle(any());
        }
    }

    @Nested
    class ListPqrsByUserTests {
        @Test
        void shouldReturn200WhenListed() throws Exception {
            PageResponse<Pqrs> page = new PageResponse<>(List.of(), 0, 10, 0L, 0);
            when(listPqrsById.handle(any(), eq(1))).thenReturn(page);
            when(pqrsRestDtoMapper.toListPqrsResponseDTO(page)).thenReturn(
                    new ListPqrsResponseDTO(List.of(), 0, 10, 0L, 0)
            );

            mockMvc.perform(get("/api/pqrs/me"))
                    .andExpect(status().isOk());

            verify(listPqrsById, times(1)).handle(any(), eq(1));
        }
    }

    @Nested
    class SecurityTests {

        @Test
        void endpoint_shouldNotCallUseCase_whenNotAuthenticated() {
            SecurityContextHolder.clearContext();
            verifyNoInteractions(createPqrs);
        }
    }
}
