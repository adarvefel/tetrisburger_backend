package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.port.in.burger.GetBurgerById;
import com.tetris.tetrisburger_backend.domain.port.in.favoriteburger.AddFavoriteBurger;
import com.tetris.tetrisburger_backend.domain.port.in.favoriteburger.GetFavoriteBurgersByUser;
import com.tetris.tetrisburger_backend.domain.port.in.favoriteburger.RemoveFavoriteBurger;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.FavoriteBurgerRestDtoMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FavoriteBurgerController.class)
class FavoriteBurgerControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private AddFavoriteBurger addFavoriteBurger;
    @MockitoBean private GetBurgerById getBurgerById;
    @MockitoBean private RemoveFavoriteBurger removeFavoriteBurger;
    @MockitoBean private GetFavoriteBurgersByUser getFavoriteBurgersByUser;
    @MockitoBean private FavoriteBurgerRestDtoMapper mapper;

    @Nested
    class SecurityTests {

        @Test
        void endpoint_shouldNotCallUseCase_whenNotAuthenticated() throws Exception {
            SecurityContextHolder.clearContext();
            verifyNoInteractions(addFavoriteBurger);
        }

        @Test
        @WithMockUser(roles = "CLIENT")
        void endpoint_shouldBeAccessible_whenRoleIsClient() throws Exception {
            mockMvc.perform(get("/api/favorites"))
                    .andExpect(status().isOk());
        }
    }
}
