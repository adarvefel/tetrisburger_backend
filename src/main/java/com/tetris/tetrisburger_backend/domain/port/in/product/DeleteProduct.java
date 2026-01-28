package com.tetris.tetrisburger_backend.domain.port.in.product;

public interface DeleteProduct {
    void delete(Integer id, Integer deletedBy);

}