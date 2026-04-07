package com.tetris.tetrisburger_backend.domain.port.in.burger;

import com.tetris.tetrisburger_backend.domain.model.Burger;
import java.util.List;

public interface GetFeaturedBurgers {
    List<Burger> handle();
}