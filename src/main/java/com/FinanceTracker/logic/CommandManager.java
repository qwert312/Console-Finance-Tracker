package com.FinanceTracker.logic;

import com.FinanceTracker.exceptions.NoTransactionsException;
import com.FinanceTracker.exceptions.TransactionsFileCreationException;
import com.FinanceTracker.exceptions.TransactionsFileFormatException;
import com.FinanceTracker.exceptions.TransactionsFileIsNotAttachedException;
import com.FinanceTracker.data.transactionsstoring.FileTransactionStorage;
import com.FinanceTracker.data.transactionsstoring.RuntimeTransactionStorage;
import com.FinanceTracker.data.transactionunit.Transaction;
import com.FinanceTracker.data.transactionunit.TransactionType;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Map;

public class CommandManager {
    private final RuntimeTransactionStorage runtimeTransactionStorage = new RuntimeTransactionStorage();
    private FileTransactionStorage fileTransactionStorage;

    public void addTransaction(LocalDateTime dateTime, TransactionType transactionType,  BigDecimal sum) {
        runtimeTransactionStorage.addTransaction(new Transaction(dateTime, transactionType, sum));
    }

    public Transaction getTransactionById(int id) throws NoTransactionsException {
        return runtimeTransactionStorage.getTransactionById(id);
    }

    public Map<Integer, Transaction> getTransactionsBetweenDates(LocalDateTime startDateTime, LocalDateTime endDateTime)
            throws NoTransactionsException
    {
        return this.runtimeTransactionStorage.getTransactionsBetweenDates(startDateTime, endDateTime);
    }

    public Map<Integer, Transaction> getAllTransactions()
            throws NoTransactionsException {
        try {
            return runtimeTransactionStorage.getTransactionsBetweenDates(LocalDateTime.MIN, LocalDateTime.MAX);
        } catch (NoTransactionsException e) {
            throw new NoTransactionsException("There are no transactions.");
        }
    }

    public void attachFile(Path filePath) throws TransactionsFileCreationException, TransactionsFileFormatException {
        fileTransactionStorage = new FileTransactionStorage(filePath);
    }

    public void unattachFile() {
        fileTransactionStorage = null;
    }

    public void saveTransactionByIdToFile(int id) throws TransactionsFileIsNotAttachedException, NoTransactionsException {
        ensureThatFileIsAttached();

        TransactionCopier.copyTransactionById(id, runtimeTransactionStorage, fileTransactionStorage);
    }

    public void saveTransactionsBetweenDatesToFile(LocalDateTime startDateTime, LocalDateTime endDateTime)
            throws TransactionsFileIsNotAttachedException, NoTransactionsException {
        ensureThatFileIsAttached();

        TransactionCopier.copyTransactionsBetweenDates(startDateTime, endDateTime,
                runtimeTransactionStorage, fileTransactionStorage);
    }

    public void saveAllTransactionsToFile() throws TransactionsFileIsNotAttachedException, NoTransactionsException {
        ensureThatFileIsAttached();

        try {
            TransactionCopier.copyTransactionsBetweenDates(LocalDateTime.MIN, LocalDateTime.MAX,
                    runtimeTransactionStorage, fileTransactionStorage);
        } catch (NoTransactionsException e) {
            throw new NoTransactionsException("There are no transactions.");
        }
    }

    public void loadTransactionByIdFromFile(int id) throws TransactionsFileIsNotAttachedException, NoTransactionsException {
        ensureThatFileIsAttached();

        TransactionCopier.copyTransactionById(id, fileTransactionStorage, runtimeTransactionStorage);
    }

    public void loadTransactionsBetweenDatesFromFile(LocalDateTime startDateTime, LocalDateTime endDateTime)
            throws TransactionsFileIsNotAttachedException, NoTransactionsException {
        ensureThatFileIsAttached();

        TransactionCopier.copyTransactionsBetweenDates(startDateTime, endDateTime,
                fileTransactionStorage, runtimeTransactionStorage);
    }

    public void loadAllTransactionsFromFile() throws TransactionsFileIsNotAttachedException, NoTransactionsException {
        ensureThatFileIsAttached();

        try {
            TransactionCopier.copyTransactionsBetweenDates(LocalDateTime.MIN, LocalDateTime.MAX,
                    fileTransactionStorage, runtimeTransactionStorage);
        } catch (NoTransactionsException e) {
            throw new NoTransactionsException("There are no transactions.");
        }
    }

    private void ensureThatFileIsAttached() throws TransactionsFileIsNotAttachedException {
        if (fileTransactionStorage == null)
            throw new TransactionsFileIsNotAttachedException("No file attached for file operations.");
    }
}
