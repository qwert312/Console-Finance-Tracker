package com.FinanceTracker.data.transactionsstoring;

import com.FinanceTracker.exceptions.TransactionsFileFormatException;
import com.FinanceTracker.data.transactionunit.Transaction;
import com.FinanceTracker.data.transactionunit.TransactionType;
import com.FinanceTracker.exceptions.TransactionsFileCreationException;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileTransactionStorageValidator {
    private Path filePath;

    public FileTransactionStorageValidator(Path filePath) {
        this.filePath = filePath;
    }

    public void createFileIfNotExists() throws TransactionsFileCreationException {
        ensureFileCanBeCreated();

        try {
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new TransactionsFileCreationException("Unknown I/O exception occurred during file or directory creation: "
                    + filePath);
        }
    }

    private void ensureFileCanBeCreated() throws TransactionsFileCreationException {
        try {
            Path tempFilePath = Files.createTempFile(filePath.getParent(), "temp", ".csv");
            File tempFile = new File(tempFilePath.toUri());
            tempFile.delete();
        } catch (IOException e) {
            throw new TransactionsFileCreationException("File can't be created at this path: " + filePath);
        }
    }

    public void ensureFileHasCorrectProperties() throws TransactionsFileFormatException {
        ensureFileIsWritableAndReadable();
        ensureFileHasCorrectType();
    }


    private void ensureFileIsWritableAndReadable() throws TransactionsFileFormatException {
        if (!Files.isWritable(filePath))
            throw new TransactionsFileFormatException("File isn't writable: " + filePath);

        if (!Files.isReadable(filePath)) {
            throw new TransactionsFileFormatException("File isn't readable: " + filePath);
        }
    }

    private void ensureFileHasCorrectType() throws TransactionsFileFormatException {
        if (!filePath.toString().endsWith(".csv"))
            throw new TransactionsFileFormatException("Incorrect file type! It should be CSV: " + filePath);
    }

    public void ensureFileHasCorrectValues() throws TransactionsFileFormatException {
        int lineNumber = 0;
        String line;
        List<Integer> idList = new ArrayList<>();
        try (BufferedReader fileReader = new BufferedReader(new FileReader(filePath.toString()))) {
            while ((line = fileReader.readLine()) != null) {
                String[] transactionLineArray = line.split(",");

                if (transactionLineArray.length > 4)
                    throw new IllegalArgumentException();

                int id = Integer.parseInt(transactionLineArray[0]);
                if (id < 0 || idList.contains(id))
                    throw new IllegalArgumentException();
                idList.add(id);

                LocalDateTime dateTime = LocalDateTime.parse(transactionLineArray[1]);
                TransactionType type = TransactionType.valueOf(transactionLineArray[2]);
                BigDecimal sum = new BigDecimal(transactionLineArray[3]);

                new Transaction(dateTime, type, sum);

                lineNumber++;
            }
        } catch (IllegalArgumentException | DateTimeException e) {
            throw new TransactionsFileFormatException("Incorrect values in the file lines. " +
                    "The exception occurred on line " + lineNumber);
        } catch (IOException e) {
            //it shouldn't happen
            throw new RuntimeException("Unexpected IO exception during file processing", e);
        }
    }
}
