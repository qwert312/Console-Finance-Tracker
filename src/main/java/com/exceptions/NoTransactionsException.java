package com.exceptions;

public class NoTransactionsException extends FinanceTrackerException  {
    public NoTransactionsException(String message) {
        super(message);
    }
}
