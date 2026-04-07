package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Supplier;
import com.tetris.tetrisburger_backend.domain.port.in.supplier.*;
import com.tetris.tetrisburger_backend.domain.port.in.supplier.query.GetSupplierByIdQuery;
import com.tetris.tetrisburger_backend.domain.port.in.supplier.query.ListSuppliersQuery;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.supplier.CreateSupplierRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.supplier.ListSupplierResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.supplier.SupplierResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.supplier.UpdateSupplierRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.SupplierRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SupplierControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock private CreateSupplier createSupplier;
    @Mock private UpdateSupplier updateSupplier;
    @Mock private DeleteSupplier deleteSupplier;
    @Mock private GetSupplierById getSupplierById;
    @Mock private ListSuppliers listSuppliers;
    @Mock private SupplierRestDtoMapper mapper;

    @InjectMocks private SupplierController controller;

    private CustomUserDetails userDetails;
    private UsernamePasswordAuthenticationToken authToken;

    private static final Integer USER_ID   = 1;
    private static final Integer SUPPLIER_ID = 10;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();

        userDetails = mock(CustomUserDetails.class);
        lenient().when(userDetails.getId()).thenReturn(USER_ID);

        authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, Collections.emptyList()
        );
        SecurityContextHolder.setContext(new SecurityContextImpl(authToken));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/suppliers/{id}  — público
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    class GetSupplierTests {

        @Test
        void shouldReturn200_withSupplierDTO_whenFound() throws Exception {
            Supplier supplier = mock(Supplier.class);
            SupplierResponseDTO responseDTO = mock(SupplierResponseDTO.class);

            when(getSupplierById.get(any(GetSupplierByIdQuery.class))).thenReturn(supplier);
            when(mapper.toResponseDTO(supplier)).thenReturn(responseDTO);

            mockMvc.perform(get("/api/suppliers/{id}", SUPPLIER_ID))
                    .andExpect(status().isOk());

            verify(getSupplierById, times(1)).get(any(GetSupplierByIdQuery.class));
            verify(mapper, times(1)).toResponseDTO(supplier);
        }

        @Test
        void shouldPassCorrectId_toQuery() throws Exception {
            Supplier supplier = mock(Supplier.class);
            when(getSupplierById.get(any(GetSupplierByIdQuery.class))).thenReturn(supplier);
            when(mapper.toResponseDTO(supplier)).thenReturn(mock(SupplierResponseDTO.class));

            mockMvc.perform(get("/api/suppliers/{id}", SUPPLIER_ID))
                    .andExpect(status().isOk());

            // Verifica que el use case se llamó con la query correcta
            verify(getSupplierById).get(argThat(q -> q.id().equals(SUPPLIER_ID)));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/suppliers  — público con paginación
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    class ListSuppliersTests {

        @Test
        void shouldReturn200_withListResponseDTO() throws Exception {
            PageResponse<Supplier> pageResponse = new PageResponse<>(
                    List.of(mock(Supplier.class)), 0, 12, 1L, 1
            );
            ListSupplierResponseDTO responseDTO = mock(ListSupplierResponseDTO.class);

            when(listSuppliers.list(any(ListSuppliersQuery.class), any(PaginationRequest.class)))
                    .thenReturn(pageResponse);
            when(mapper.toListResponseDTO(pageResponse)).thenReturn(responseDTO);

            mockMvc.perform(get("/api/suppliers"))
                    .andExpect(status().isOk());

            verify(listSuppliers, times(1))
                    .list(any(ListSuppliersQuery.class), any(PaginationRequest.class));
        }

        @Test
        void shouldApplyDefaultPagination_whenNoParamsProvided() throws Exception {
            PageResponse<Supplier> pageResponse = new PageResponse<>(
                    List.of(), 0, 12, 0L, 0
            );
            when(listSuppliers.list(any(ListSuppliersQuery.class), any(PaginationRequest.class)))
                    .thenReturn(pageResponse);
            when(mapper.toListResponseDTO(pageResponse)).thenReturn(mock(ListSupplierResponseDTO.class));

            mockMvc.perform(get("/api/suppliers"))
                    .andExpect(status().isOk());

            // Verifica que se pasó PaginationRequest con defaults page=0, size=12
            verify(listSuppliers).list(
                    any(ListSuppliersQuery.class),
                    argThat(pr -> pr.getPage() == 0 && pr.getSize() == 12)
            );
        }

        @Test
        void shouldForwardQueryParam_toListSuppliersQuery() throws Exception {
            PageResponse<Supplier> pageResponse = new PageResponse<>(List.of(), 0, 12, 0L, 0);
            when(listSuppliers.list(any(ListSuppliersQuery.class), any(PaginationRequest.class)))
                    .thenReturn(pageResponse);
            when(mapper.toListResponseDTO(pageResponse)).thenReturn(mock(ListSupplierResponseDTO.class));

            mockMvc.perform(get("/api/suppliers").param("q", "proveedor"))
                    .andExpect(status().isOk());

            verify(listSuppliers).list(
                    argThat(q -> "proveedor".equals(q.q())),
                    any(PaginationRequest.class)
            );
        }

        @Test
        void shouldApplyCustomPagination_whenParamsProvided() throws Exception {
            PageResponse<Supplier> pageResponse = new PageResponse<>(List.of(), 2, 5, 0L, 0);
            when(listSuppliers.list(any(ListSuppliersQuery.class), any(PaginationRequest.class)))
                    .thenReturn(pageResponse);
            when(mapper.toListResponseDTO(pageResponse)).thenReturn(mock(ListSupplierResponseDTO.class));

            mockMvc.perform(get("/api/suppliers")
                            .param("page", "2")
                            .param("size", "5")
                            .param("sortBy", "email")
                            .param("direction", "DESC"))
                    .andExpect(status().isOk());

            verify(listSuppliers).list(
                    any(ListSuppliersQuery.class),
                    argThat(pr -> pr.getPage() == 2
                            && pr.getSize() == 5
                            && "email".equals(pr.getSortBy())
                            && "DESC".equals(pr.getDirection()))
            );
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/suppliers  — ADMIN
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    class CreateSupplierTests {
        @Test
        void shouldReturn201_whenSupplierCreated() throws Exception {
            Supplier created = mock(Supplier.class);

            when(mapper.toCreateCommand(any(), eq(USER_ID))).thenReturn(mock());
            when(createSupplier.create(any())).thenReturn(created);
            when(mapper.toResponseDTO(created)).thenReturn(mock(SupplierResponseDTO.class));

            // Body con los campos @NotBlank requeridos
            String validBody = """
            {
              "name": "Proveedor Test",
              "email": "proveedor@test.com",
              "phone": "3001234567"
            }
            """;

            mockMvc.perform(post("/api/suppliers")
                            .with(authentication(authToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validBody))
                    .andExpect(status().isCreated());

            verify(createSupplier, times(1)).create(any());
        }

        @Test
        void shouldPassUserIdFromPrincipal_toCreateCommand() throws Exception {
            when(mapper.toCreateCommand(any(), eq(USER_ID))).thenReturn(mock());
            when(createSupplier.create(any())).thenReturn(mock(Supplier.class));
            when(mapper.toResponseDTO(any())).thenReturn(mock(SupplierResponseDTO.class));

            String validBody = """
            {
              "name": "Proveedor Test",
              "email": "proveedor@test.com",
              "phone": "3001234567"
            }
            """;

            mockMvc.perform(post("/api/suppliers")
                            .with(authentication(authToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validBody))
                    .andExpect(status().isCreated());

            verify(mapper).toCreateCommand(any(), eq(USER_ID));
        }

    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /api/suppliers/{id}  — ADMIN
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    class UpdateSupplierTests {

        @Test
        void shouldReturn200_whenSupplierUpdated() throws Exception {
            Supplier updated = mock(Supplier.class);

            when(mapper.toUpdateCommand(eq(SUPPLIER_ID), any(), eq(USER_ID))).thenReturn(mock());
            when(updateSupplier.update(any())).thenReturn(updated);
            when(mapper.toResponseDTO(updated)).thenReturn(mock(SupplierResponseDTO.class));

            String validBody = """
            {
              "name": "Proveedor Actualizado",
              "email": "actualizado@test.com",
              "phone": "3009876543"
            }
            """;

            mockMvc.perform(put("/api/suppliers/{id}", SUPPLIER_ID)
                            .with(authentication(authToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validBody))
                    .andExpect(status().isOk());

            verify(updateSupplier, times(1)).update(any());
        }

        @Test
        void shouldPassPathVariableId_toUpdateCommand() throws Exception {
            when(mapper.toUpdateCommand(eq(SUPPLIER_ID), any(), eq(USER_ID))).thenReturn(mock());
            when(updateSupplier.update(any())).thenReturn(mock(Supplier.class));
            when(mapper.toResponseDTO(any())).thenReturn(mock(SupplierResponseDTO.class));

            String validBody = """
            {
              "name": "Proveedor Actualizado",
              "email": "actualizado@test.com",
              "phone": "3009876543"
            }
            """;

            mockMvc.perform(put("/api/suppliers/{id}", SUPPLIER_ID)
                            .with(authentication(authToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validBody))
                    .andExpect(status().isOk());

            verify(mapper).toUpdateCommand(eq(SUPPLIER_ID), any(), eq(USER_ID));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE /api/suppliers/{id}  — ADMIN
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    class DeleteSupplierTests {

        @Test
        void shouldReturn204_whenSupplierDeleted() throws Exception {
            doNothing().when(deleteSupplier).delete(SUPPLIER_ID, USER_ID);

            mockMvc.perform(delete("/api/suppliers/{id}", SUPPLIER_ID)
                            .with(authentication(authToken)))
                    .andExpect(status().isNoContent());

            verify(deleteSupplier, times(1)).delete(SUPPLIER_ID, USER_ID);
        }

        @Test
        void shouldPassIdAndUserId_toUseCase() throws Exception {
            doNothing().when(deleteSupplier).delete(SUPPLIER_ID, USER_ID);

            mockMvc.perform(delete("/api/suppliers/{id}", SUPPLIER_ID)
                            .with(authentication(authToken)))
                    .andExpect(status().isNoContent());

            verify(deleteSupplier).delete(eq(SUPPLIER_ID), eq(USER_ID));
        }

        @Test
        void shouldCallUseCaseExactlyOnce() throws Exception {
            doNothing().when(deleteSupplier).delete(SUPPLIER_ID, USER_ID);

            mockMvc.perform(delete("/api/suppliers/{id}", SUPPLIER_ID)
                            .with(authentication(authToken)))
                    .andExpect(status().isNoContent());

            verify(deleteSupplier, times(1)).delete(SUPPLIER_ID, USER_ID);
            verifyNoMoreInteractions(deleteSupplier);
        }
    }
}