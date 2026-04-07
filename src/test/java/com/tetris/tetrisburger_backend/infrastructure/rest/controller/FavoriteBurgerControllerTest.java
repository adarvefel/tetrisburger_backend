package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.FavoriteBurger;
import com.tetris.tetrisburger_backend.domain.model.FavoriteBurgerDetail;
import com.tetris.tetrisburger_backend.domain.port.in.burger.GetBurgerById;
import com.tetris.tetrisburger_backend.domain.port.in.favoriteburger.AddFavoriteBurger;
import com.tetris.tetrisburger_backend.domain.port.in.favoriteburger.GetFavoriteBurgersByUser;
import com.tetris.tetrisburger_backend.domain.port.in.favoriteburger.RemoveFavoriteBurger;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.favoriteburger.FavoriteBurgerRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.favoriteburger.FavoriteBurgerResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.favoriteburger.FavoriteBurgerSimpleResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.FavoriteBurgerRestDtoMapper;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FavoriteBurgerControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock private AddFavoriteBurger addFavoriteBurger;
    @Mock private GetBurgerById getBurgerById;
    @Mock private RemoveFavoriteBurger removeFavoriteBurger;
    @Mock private GetFavoriteBurgersByUser getFavoriteBurgersByUser;
    @Mock private FavoriteBurgerRestDtoMapper mapper;

    @InjectMocks private FavoriteBurgerController controller;

    private CustomUserDetails userDetails;
    private UsernamePasswordAuthenticationToken authToken;

    private static final Integer USER_ID = 1;
    private static final Integer BURGER_ID = 10;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();

        userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(USER_ID);

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
    // POST /api/favorites
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    class AddFavoriteTests {

        @Test
        void shouldReturn201_whenFavoriteAdded() throws Exception {
            FavoriteBurgerRequestDTO requestDTO = new FavoriteBurgerRequestDTO(BURGER_ID);
            FavoriteBurger favoriteBurger = mock(FavoriteBurger.class);
            FavoriteBurgerSimpleResponseDTO responseDTO = mock(FavoriteBurgerSimpleResponseDTO.class);

            when(addFavoriteBurger.handle(USER_ID, BURGER_ID)).thenReturn(favoriteBurger);
            when(mapper.toSimpleResponseDTO(favoriteBurger)).thenReturn(responseDTO);

            mockMvc.perform(post("/api/favorites")
                            .with(authentication(authToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isCreated());

            verify(addFavoriteBurger, times(1)).handle(USER_ID, BURGER_ID);
            verify(mapper, times(1)).toSimpleResponseDTO(favoriteBurger);
        }

        @Test
        void shouldPassCorrectUserIdAndBurgerId() throws Exception {
            FavoriteBurgerRequestDTO requestDTO = new FavoriteBurgerRequestDTO(BURGER_ID);
            FavoriteBurger favoriteBurger = mock(FavoriteBurger.class);

            when(addFavoriteBurger.handle(eq(USER_ID), eq(BURGER_ID))).thenReturn(favoriteBurger);
            when(mapper.toSimpleResponseDTO(favoriteBurger))
                    .thenReturn(mock(FavoriteBurgerSimpleResponseDTO.class));

            mockMvc.perform(post("/api/favorites")
                            .with(authentication(authToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isCreated());

            verify(addFavoriteBurger).handle(USER_ID, BURGER_ID);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE /api/favorites/{idBurger}
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    class RemoveFavoriteTests {

        @Test
        void shouldReturn200_withMessage_whenBurgerIsOnMenu() throws Exception {
            Burger burger = mock(Burger.class);
            when(burger.isOnMenu()).thenReturn(true);
            when(burger.getName()).thenReturn("Burger Clásica");

            when(getBurgerById.execute(BURGER_ID)).thenReturn(burger);
            doNothing().when(removeFavoriteBurger).handle(USER_ID, BURGER_ID);

            mockMvc.perform(delete("/api/favorites/{idBurger}", BURGER_ID)
                            .with(authentication(authToken)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message")
                            .value("Hamburguesa eliminada de favoritos"));

            verify(getBurgerById, times(1)).execute(BURGER_ID);
            verify(removeFavoriteBurger, times(1)).handle(USER_ID, BURGER_ID);
        }

        @Test
        void shouldReturn200_withCustomMessage_whenBurgerIsNotOnMenu() throws Exception {
            Burger burger = mock(Burger.class);
            when(burger.isOnMenu()).thenReturn(false);
            when(burger.getName()).thenReturn("Mi Burger Custom");

            when(getBurgerById.execute(BURGER_ID)).thenReturn(burger);
            doNothing().when(removeFavoriteBurger).handle(USER_ID, BURGER_ID);

            mockMvc.perform(delete("/api/favorites/{idBurger}", BURGER_ID)
                            .with(authentication(authToken)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message")
                            .value("Hamburguesa personalizada eliminada de favoritos y de tus creaciones"));

            verify(getBurgerById, times(1)).execute(BURGER_ID);
            verify(removeFavoriteBurger, times(1)).handle(USER_ID, BURGER_ID);
        }

        @Test
        void shouldGetBurgerBeforeRemoving() throws Exception {
            Burger burger = mock(Burger.class);
            when(burger.isOnMenu()).thenReturn(true);
            when(burger.getName()).thenReturn("Burger Clásica");

            when(getBurgerById.execute(BURGER_ID)).thenReturn(burger);
            doNothing().when(removeFavoriteBurger).handle(USER_ID, BURGER_ID);

            mockMvc.perform(delete("/api/favorites/{idBurger}", BURGER_ID)
                            .with(authentication(authToken)))
                    .andExpect(status().isOk());

            var inOrder = inOrder(getBurgerById, removeFavoriteBurger);
            inOrder.verify(getBurgerById).execute(BURGER_ID);
            inOrder.verify(removeFavoriteBurger).handle(USER_ID, BURGER_ID);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/favorites
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    class GetFavoritesTests {

        @Test
        void shouldReturn200_withListOfFavorites() throws Exception {
            FavoriteBurgerDetail detail = mock(FavoriteBurgerDetail.class);
            FavoriteBurgerResponseDTO responseDTO = mock(FavoriteBurgerResponseDTO.class);

            when(getFavoriteBurgersByUser.handle(USER_ID)).thenReturn(List.of(detail));
            when(mapper.toResponseDTOList(List.of(detail))).thenReturn(List.of(responseDTO));

            mockMvc.perform(get("/api/favorites")
                            .with(authentication(authToken)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());

            verify(getFavoriteBurgersByUser, times(1)).handle(USER_ID);
        }

        @Test
        void shouldReturn200_withEmptyList_whenNoFavorites() throws Exception {
            when(getFavoriteBurgersByUser.handle(USER_ID)).thenReturn(Collections.emptyList());
            when(mapper.toResponseDTOList(Collections.emptyList())).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/favorites")
                            .with(authentication(authToken)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        void shouldCallUseCaseExactlyOnce() throws Exception {
            when(getFavoriteBurgersByUser.handle(USER_ID)).thenReturn(Collections.emptyList());
            when(mapper.toResponseDTOList(any())).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/favorites")
                            .with(authentication(authToken)))
                    .andExpect(status().isOk());

            verify(getFavoriteBurgersByUser, times(1)).handle(USER_ID);
            verifyNoMoreInteractions(getFavoriteBurgersByUser);
        }
    }
}
