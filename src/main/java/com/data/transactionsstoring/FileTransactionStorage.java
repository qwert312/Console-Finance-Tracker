package com.data.transactionsstoring;

import com.data.transactionunit.Transaction;
import com.data.transactionunit.TransactionType;
import com.exceptions.TransactionsFileCreationException;
import com.exceptions.TransactionsFileFormatException;
import com.exceptions.NoTransactionsException;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class FileTransactionStorage implements TransactionStorage {
    Path filePath;
    FileTransactionStorageValidator fileValidator;

    public FileTransactionStorage(Path filePath) throws TransactionsFileCreationException, TransactionsFileFormatException {
        fileValidator = new FileTransactionStorageValidator(filePath);
        fileValidator.createFileIfNotExists();
        fileValidator.ensureFileHasCorrectProperties();
        fileValidator.ensureFileHasCorrectValues();
        this.filePath = filePath;
    }

    public void addTransaction(Transaction transaction) {
        try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(filePath.toString(), true)))) {
            fileValidator.ensureFileHasCorrectProperties();
            fileValidator.ensureFileHasCorrectValues();

            int id = findLastFreeId();
            String stringOfValues = id + "," + transaction.toString().replace(" ", ",");
            printWriter.println(stringOfValues);
        } catch(TransactionsFileFormatException e) {
            throw new RuntimeException("The transactions file was manually modified incorrectly during program runtime. " +
                    "Details:\n"
                    + e.getMessage());
        } catch (IOException e) {
            //it shouldn't happen
            throw new RuntimeException("Unexpected IO exception during adding transaction to file.", e);
        }
    }

    private int findLastFreeId() throws IOException {
        return Files.lines(filePath).mapToInt(line -> Integer.parseInt(line.split(",")[0])).max().orElse(-1) + 1;
    }

    public Transaction getTransactionById(int id) throws NoTransactionsException {
        Transaction searchedTransaction = null;
        String line;

        try (BufferedReader fileReader = new BufferedReader(new FileReader(filePath.toString()))) {
            fileValidator.ensureFileHasCorrectProperties();
            fileValidator.ensureFileHasCorrectValues();
            while ((line = fileReader.readLine()) != null) {
                String[] currentLine = line.split(",");
                if (Integer.parseInt(currentLine[0]) == id) {
                    LocalDateTime dateTime = LocalDateTime.parse(currentLine[1]);
                    TransactionType type = TransactionType.valueOf(currentLine[2]);
                    BigDecimal sum = new BigDecimal(currentLine[3]);
                    searchedTransaction = new Transaction(dateTime, type, sum);
                    break;
                }
            }
        } catch (TransactionsFileFormatException e) {
            throw new RuntimeException("The transactions file was manually modified incorrectly during program runtime. Details:\n"
                    + e.getMessage());
        } catch (IOException e) {
            //it shouldn't happen
            throw new RuntimeException("Unexpected IO exception during getting values from file.", e);
        }

        if (searchedTransaction == null)
            throw new NoTransactionsException("No transaction with this Id: " + id);

        return searchedTransaction;
    }

    public Map<Integer, Transaction> getTransactionsBetweenDates(LocalDateTime startDateTime, LocalDateTime endDateTime)
        throws NoTransactionsException
    {
        Map<Integer, Transaction> searchedTransactions = new HashMap<>();
        String line;

        try (BufferedReader fileReader = new BufferedReader(new FileReader(filePath.toString()))) {
            fileValidator.ensureFileHasCorrectProperties();
            fileValidator.ensureFileHasCorrectValues();
            while ((line = fileReader.readLine()) != null) {
                String[] currentLine = line.split(",");
                if (startDateTime.isBefore(LocalDateTime.parse(currentLine[1])) && endDateTime.isAfter(LocalDateTime.parse(currentLine[1]))) {
                    int id = Integer.parseInt(currentLine[0]);
                    LocalDateTime dateTime = LocalDateTime.parse(currentLine[1]);
                    TransactionType type = TransactionType.valueOf(currentLine[2]);
                    BigDecimal sum = new BigDecimal(currentLine[3]);
                    searchedTransactions.put(id, new Transaction(dateTime, type, sum));
                }
            }
        } catch (TransactionsFileFormatException e) {
            throw new RuntimeException("The transactions file was manually modified incorrectly during program runtime. Details:\n"
                    + e.getMessage());
        } catch (IOException e) {
            //it shouldn't happen
            throw new RuntimeException("Unexpected IO exception during getting values from file.", e);
        }
        if (searchedTransactions.isEmpty())
            throw new NoTransactionsException("No transactions between " + startDateTime + " and " + endDateTime + ".");

        return searchedTransactions;
    }
}
