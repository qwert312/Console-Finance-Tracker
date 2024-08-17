package exceptions;

public class NoTransactionsException extends Exception {
    public NoTransactionsException(String message) {
        super(message);
    }
}
