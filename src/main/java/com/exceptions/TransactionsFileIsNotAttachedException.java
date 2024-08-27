package com.exceptions;

public class TransactionsFileIsNotAttachedException extends FinanceTrackerException {
    public TransactionsFileIsNotAttachedException(String message) {
        super(message);
    }
}
