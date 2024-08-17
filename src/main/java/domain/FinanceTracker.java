package domain;

import exceptions.*;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class FinanceTracker {
    private static int lastFreeID = 0;
    private HashMap<Integer, Transaction> transactions = new HashMap<>();
    public BigDecimal balance = new BigDecimal(0);

    public BigDecimal getBalance() {
        return balance;
    }

    public Transaction getTransactionById(int id) throws NoTransactionsException {
        Transaction transaction = transactions.get(id);

        if (transaction == null)
            throw new NoTransactionsException("No transaction with this ID: " + id);

        return transaction;
    }

    public HashMap<Integer, Transaction> getTransactionsBetweenDates(LocalDateTime startDate, LocalDateTime endDate)
            throws DateTimeException {
        if (startDate.isAfter(endDate))
            throw new DateTimeException("Start date cannot be after end date.");

        HashMap<Integer, Transaction> transactionsMap = new HashMap<>();
        for (Map.Entry<Integer, Transaction> transactionPair : transactions.entrySet()) {
            if (transactionPair.getValue().getDateTime().isAfter(startDate)
                    && transactionPair.getValue().getDateTime().isBefore(endDate))
                transactionsMap.put(transactionPair.getKey(), transactionPair.getValue());
        }

        return transactionsMap;
    }

    public void addTransaction(Transaction transaction) throws InsufficientFundsException {
        if (transaction.getType() == TransactionType.EXPENSE && balance.compareTo(transaction.getSum()) < 0)
            throw new InsufficientFundsException
                    ("Insufficient funds in the balance for the expense transaction of " + transaction.getSum().toString());

        transactions.put(lastFreeID, transaction);
        lastFreeID++;

        if (transaction.getType() == TransactionType.INCOME)
            balance = balance.add(transaction.getSum());
        else
            balance = balance.subtract(transaction.getSum());
    }

    public void addTransactionsFromFile(Path filePath) throws FileFormatException, FileNotFoundException {
        ensureFileIsExistAndReadable(filePath);
        ensureFileHasCorrectType(filePath);
        ensureFileHasCorrectNumberOfValuesInLines(filePath);

        HashMap<Integer, Transaction> transactionsCopy = copyTransactions();
        int copyOfLastFreeId = lastFreeID;

        safelyAddTransactionsFromFile(filePath, transactionsCopy, copyOfLastFreeId);
    }

    public void replaceTransactionsWithFileTransactions(Path filePath) throws FileFormatException, FileNotFoundException {
        ensureFileIsExistAndReadable(filePath);
        ensureFileHasCorrectType(filePath);
        ensureFileHasCorrectNumberOfValuesInLines(filePath);

        HashMap<Integer, Transaction> transactionsCopy = copyTransactions();
        int copyOfLastFreeId = lastFreeID;
        transactions.clear();
        lastFreeID = 0;

        safelyAddTransactionsFromFile(filePath, transactionsCopy, copyOfLastFreeId);
    }

    private void ensureFileIsExistAndReadable(Path filePath) throws FileFormatException, FileNotFoundException {
        if (!Files.exists(filePath))
            throw new FileNotFoundException("File doesn't exist: " + filePath);

        if (!Files.isReadable(filePath)) {
            throw new FileFormatException("File isn't readable: " + filePath);
        }
    }

    private void ensureFileHasCorrectType(Path filePath) throws FileFormatException {
        if (!filePath.toString().endsWith(".csv"))
            throw new FileFormatException("Incorrect file type! It should be CSV: " + filePath);
    }

    private void ensureFileHasCorrectNumberOfValuesInLines(Path filePath) throws FileFormatException {
        try (Stream<String> lines = Files.lines(filePath)) {
            if (!lines.allMatch(line -> line.contains(",") || line.split(",").length == 3))
                throw new FileFormatException("Lines in the file must contains 3 values each!");
        }
        catch (IOException e) {
            throw new RuntimeException("Unexpected IO exception during file processing", e);
        }
    }

    private HashMap<Integer, Transaction> copyTransactions() {
        HashMap<Integer, Transaction> transactionsCopy = new HashMap<>();

        for (Map.Entry<Integer, Transaction> transactionPair : transactions.entrySet())
            transactionsCopy.put(transactionPair.getKey(), transactionPair.getValue().copyTransaction());

        return transactionsCopy;
    }

    private void safelyAddTransactionsFromFile
            (Path filePath, HashMap<Integer, Transaction> transactionsCopy, Integer copyOfLastFreeId)
            throws FileFormatException {
        String line;
        int lineNumber = 0;
        try (BufferedReader fileReader = new BufferedReader(new FileReader(filePath.toString()))) {
            while ((line = fileReader.readLine()) != null) {
                String[] transactionLineArray = line.split(",");
                LocalDateTime dateTime = LocalDateTime.parse(transactionLineArray[0]);
                TransactionType type = TransactionType.valueOf(transactionLineArray[1]);
                BigDecimal sum = new BigDecimal(transactionLineArray[2]);

                addTransaction(new Transaction(dateTime, type, sum));

                lineNumber++;
            }
        } catch (InsufficientFundsException | RuntimeException e) {
            transactions = transactionsCopy;
            lastFreeID = copyOfLastFreeId;
            throw new FileFormatException("Incorrect values in the file lines. No transactions were added." +
                    "The exception occurred on line " + lineNumber);
        } catch (IOException e) {
            //it shouldn't happen
            throw new RuntimeException("Unexpected IO exception during file processing", e);
        }
    }

    public void saveTransactionsToFile(Path filePath)
            throws NoTransactionsException, FileNotFoundException, FileFormatException, FileCreationException {
        if (transactions.isEmpty())
            throw new NoTransactionsException("No transactions to save.");

        ensureFileCanBeCreated(filePath);
        createFileIfNotExists(filePath);
        ensureFileIsExistsAndWritable(filePath);
        ensureFileHasCorrectType(filePath);

        try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(filePath.toString())))) {
            for (Transaction transaction : transactions.values()) {
                String stringOfValues = transaction.toString().replace(" ", ",");
                printWriter.println(stringOfValues);
            }
        } catch (IOException e) {
            //it shouldn't happen
            throw new RuntimeException("Unexpected IO exception during file processing", e);
        }
    }

    private void ensureFileCanBeCreated(Path filePath) throws FileCreationException {
        try {
            Path tempFilePath = Files.createTempFile(filePath.getParent(), "temp", ".csv");
            File tempFile = new File(tempFilePath.toUri());
            tempFile.delete();
        } catch (IOException e) {
            throw new FileCreationException("File can't be created: " + filePath);
        }
    }

    private void createFileIfNotExists(Path filePath) throws FileCreationException {
        try {
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new FileCreationException("Unknown I/O exception occurred during file or directory creation: "
                    + filePath);
        }
    }

    public void ensureFileIsExistsAndWritable(Path filePath) throws FileNotFoundException {
        if (!Files.exists(filePath))
            throw new FileNotFoundException("File doesn't exist: " + filePath);

        if (!Files.isWritable(filePath))
            throw new FileNotFoundException("File isn't writable: " + filePath);
    }
}
