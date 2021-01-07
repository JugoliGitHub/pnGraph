package main.exception;

public class WrongDimensionException extends RuntimeException {

  public WrongDimensionException() {
    super();
  }

  public WrongDimensionException(String message) {
    super(message);
  }
}
