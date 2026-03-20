package com.tetris.tetrisburger_backend.infrastructure.persistence.mapper;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class StringMapperHelper {

    @Named("trim")
    public String trim(String s) {
        return s != null ? s.trim() : null;
    }
}