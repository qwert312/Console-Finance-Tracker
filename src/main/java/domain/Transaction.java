package domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Transaction {
    private final LocalDateTime dateTime;
    private final BigDecimal sum;
    private final TransactionType type;

    public Transaction(LocalDateTime date, TransactionType type, BigDecimal sum) {
        this.dateTime = date.truncatedTo(ChronoUnit.SECONDS);
        this.type = type;

        if (sum.compareTo(BigDecimal.valueOf(0)) < 0)
            this.sum = BigDecimal.valueOf(0);
        else
            this.sum = sum;

    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public TransactionType getType() {
        return type;
    }

    public Transaction copyTransaction() {
        return new Transaction(dateTime, type, sum);
    }

    @Override
    public String toString() {
        return  dateTime.toString() + " " + type.name() + " " + sum.toString();
    }
}
