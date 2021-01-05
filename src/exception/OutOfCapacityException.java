package exception;

public class OutOfCapacityException extends RuntimeException {

  public OutOfCapacityException() { super(); }

  public OutOfCapacityException(String message) {
    super(message);
  }

}
