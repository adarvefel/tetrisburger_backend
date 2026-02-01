package com.tetris.tetrisburger_backend.domain.exception;

public class BurgerNotFoundException extends DomainException {


  public BurgerNotFoundException(String message) {
    super(message);
  }

  public BurgerNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public BurgerNotFoundException(Integer burgerId) {
    super("Burger not found with ID: " + burgerId);
  }
}
