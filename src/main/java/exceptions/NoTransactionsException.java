package exceptions;

public class NoTransactionsException extends UnsupportedOperationException  {
    public NoTransactionsException(String message) {
        super(message);
    }
}
