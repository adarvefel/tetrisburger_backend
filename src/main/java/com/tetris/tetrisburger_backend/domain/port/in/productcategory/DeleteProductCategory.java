package com.tetris.tetrisburger_backend.domain.port.in.productcategory;

public interface DeleteProductCategory {
    void delete(Integer id,Integer deletedBy);
}