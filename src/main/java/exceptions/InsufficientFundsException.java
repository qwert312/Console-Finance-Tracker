package exceptions;

public class InsufficientFundsException extends UnsupportedOperationException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
