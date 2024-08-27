package com.FinanceTracker.exceptions;

public class FinanceTrackerException extends Exception {
    public FinanceTrackerException(String message) {
        super(message + "\n");
    }
}
