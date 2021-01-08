package exception;

public class NotExistingNodeException extends RuntimeException {

  public NotExistingNodeException() {
    super();
  }

  public NotExistingNodeException(String message) {
    super(message);
  }

}
