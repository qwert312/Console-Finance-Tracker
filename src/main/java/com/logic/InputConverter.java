package com.logic;

import com.data.transactionunit.TransactionType;
import com.exceptions.IncorrectInputException;

import java.math.BigDecimal;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;

public class InputConverter {
    private static final DateTimeFormatter dateTimeInputFormatter = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd[ HH:mm:ss]")
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .toFormatter();

    private InputConverter() {}

    public static int convertId (String idString) throws IncorrectInputException {
        try {
            return Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            throw new IncorrectInputException("Input string cannot be represented as number: " + idString);
        }
    }

    public static LocalDateTime convertDateTime(String dateTimeString) throws IncorrectInputException {
        try {
            return LocalDateTime.parse(dateTimeString, dateTimeInputFormatter);
        } catch (DateTimeParseException e) {
            throw new IncorrectInputException("Incorrect date time format: " + dateTimeString);
        }
    }

    public static TransactionType convertTransactionType(String transactionTypeString) throws IncorrectInputException {
        try {
            return TransactionType.valueOf(transactionTypeString);
        } catch (IllegalArgumentException e) {
            throw new IncorrectInputException("Incorrect transaction type: " + transactionTypeString);
        }
    }

    public static BigDecimal convertSum(String sumString) throws IncorrectInputException {
        try {
            return new BigDecimal(sumString);
        } catch (NumberFormatException e) {
            throw new IncorrectInputException("Input string cannot be represented as decimal number: " + sumString);
        }
    }

    public static Path convertPath(String pathString) throws IncorrectInputException {
        try {
            Path path = Paths.get(pathString);
            if (!path.isAbsolute())
                throw new IncorrectInputException("Incorrect path format " + pathString);
            return path;
        } catch (InvalidPathException e) {
            throw new IncorrectInputException("Incorrect path format " + pathString);
        }
    }
}
