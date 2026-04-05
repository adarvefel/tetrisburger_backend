package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Pqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.*;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PqrsControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    @BeforeEach
    void setUp() {
        mockUserDetails = mock(CustomUserDetails.class);
        lenient().when(mockUserDetails.getId()).thenReturn(1);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterAnnotation(AuthenticationPrincipal.class) != null;
                    }
                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        return mockUserDetails;
                    }
                })
                .build();

        mockPqrs = mock(Pqrs.class);
    }

    @Nested
    class CreatePqrsTests {
        @Test
        void shouldCreatePqrsAndReturn201() throws Exception {
            when(pqrsRestDtoMapper.toCreatePqrsCommand(any(), eq(1))).thenReturn(mock());
            when(createPqrs.handle(any())).thenReturn(mockPqrs);

            mockMvc.perform(post("/api/pqrs")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isOk());

            verify(createPqrs).handle(any());
        }
    }

    @Nested
    class GetPqrsByIdTests {
        @Test
        void shouldGetPqrsByIdAndReturn200() throws Exception {
            when(getPqrsById.handle(any())).thenReturn(mockPqrs);

            mockMvc.perform(get("/api/pqrs/1"))
                    .andExpect(status().isOk());

            verify(getPqrsById).handle(any());
        }
    }

    @Nested
    class UpdatePqrsTests {
        @Test
        void shouldUpdatePqrsAndReturn200() throws Exception {
            when(pqrsRestDtoMapper.toUpdatePqrsCommand(eq(1), any(), eq(1))).thenReturn(mock());
            when(updatePqrs.handle(any())).thenReturn(mockPqrs);

            mockMvc.perform(put("/api/pqrs/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isOk());

            verify(updatePqrs).handle(any());
        }
    }

    @Nested
    class UpdatePqrsByAdminTests {
        @Test
        void shouldUpdatePqrsByAdminAndReturn200() throws Exception {
            when(pqrsRestDtoMapper.toUpdatePqrsByAdminCommand(eq(1), any(), eq(1))).thenReturn(mock());
            when(updatePqrsByAdmin.handle(any())).thenReturn(mockPqrs);

            mockMvc.perform(put("/api/pqrs/admin/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isOk());

            verify(updatePqrsByAdmin).handle(any());
        }
    }

    @Nested
    class DeletePqrsTests {
        @Test
        void shouldDeletePqrsAndReturn200() throws Exception {
            mockMvc.perform(delete("/api/pqrs/1"))
                    .andExpect(status().isOk());

            verify(deleteSoftPqrs).handle(any());
        }
    }
}
