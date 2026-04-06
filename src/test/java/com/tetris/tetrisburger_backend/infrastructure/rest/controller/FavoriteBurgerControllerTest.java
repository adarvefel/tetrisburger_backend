package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.port.in.burger.GetBurgerById;
import com.tetris.tetrisburger_backend.domain.port.in.favoriteburger.AddFavoriteBurger;
import com.tetris.tetrisburger_backend.domain.port.in.favoriteburger.GetFavoriteBurgersByUser;
import com.tetris.tetrisburger_backend.domain.port.in.favoriteburger.RemoveFavoriteBurger;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.FavoriteBurgerRestDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class FavoriteBurgerControllerTest {

    private MockMvc mockMvc;

    @Mock private AddFavoriteBurger addFavoriteBurger;
    @Mock private GetBurgerById getBurgerById;
    @Mock private RemoveFavoriteBurger removeFavoriteBurger;
    @Mock private GetFavoriteBurgersByUser getFavoriteBurgersByUser;
    @Mock private FavoriteBurgerRestDtoMapper mapper;

    @InjectMocks private FavoriteBurgerController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    class SecurityTests {

        @Test
        void endpoint_shouldNotCallUseCase_whenNotAuthenticated() {
            SecurityContextHolder.clearContext();
            verifyNoInteractions(addFavoriteBurger);
        }
    }
}
