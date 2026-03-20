package com.tetris.tetrisburger_backend.domain.exception;

public class ProductAlreadyDeletedException extends DomainException {
  public ProductAlreadyDeletedException(Integer id) {
    super("El producto con ID " + id + " ya fue eliminado o no existe");
  }
}
