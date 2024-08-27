package com.FinanceTracker.data.transactionunit;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record Transaction(LocalDateTime dateTime, TransactionType type, BigDecimal sum) {

    public Transaction {
        if (sum.compareTo(BigDecimal.valueOf(0)) < 0)
            sum = BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return  this.dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + " " + this.type.name() + " " + this.sum;
    }
}
