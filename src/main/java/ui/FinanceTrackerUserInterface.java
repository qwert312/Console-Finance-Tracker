package ui;

import domain.*;
import exceptions.*;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class FinanceTrackerUserInterface {
    private final FinanceTracker financeTracker  = new FinanceTracker();
    private final Scanner scanner;

    public FinanceTrackerUserInterface(Scanner scanner) {
        this.scanner = scanner;
    }

    public void start() {
        while (true) {
            System.out.println("List of commands:");
            System.out.println("1 - Add transaction");
            System.out.println("2 - Print all transactions");
            System.out.println("3 - Print transaction by id");
            System.out.println("4 - Print transactions between dates");
            System.out.println("5 - Print balance");
            System.out.println("6 - Add transactions from file");
            System.out.println("7 - Replace the current transactions with transactions form the file");
            System.out.println("8 - Save the current transactions to file");
            System.out.println("q - Quit");
            System.out.print("> ");
            String command = scanner.nextLine();
            System.out.println();

            try {
                if (command.equals("1")) {
                    enterAndAddTransaction();
                } else if (command.equals("2")) {
                    printAllTransactions();
                } else if (command.equals("3")) {
                    printTransactionById();
                } else if (command.equals("4")) {
                    printTransactionsBetweenDates();
                } else if (command.equals("5")) {
                    printBalance();
                } else if (command.equals("6")) {
                    addTransactionsFromFile();
                } else if (command.equals("7")) {
                    replaceCurrentTransactionsWithFile();
                } else if (command.equals("8")) {
                    saveCurrentTransactionsToFile();
                } else if (command.equals("q")) {
                    break;
                }
            } catch (IllegalArgumentException | DateTimeException | FileFormatException | FileNotFoundException |
                     FileCreationException | InsufficientFundsException | NoTransactionsException e) {
                System.out.println(e.getMessage());
            }
            System.out.println();
        }
    }

    private void enterAndAddTransaction() throws IllegalArgumentException, InsufficientFundsException {
        System.out.println("Enter the transaction type (INCOME/EXPENSE)");
        String transactionTypeString = scanner.nextLine().trim().toUpperCase();
        System.out.println("Enter the sum of transaction");
        String sumString = scanner.nextLine().trim();
        try {
            TransactionType transactionType = TransactionType.valueOf(transactionTypeString);
            BigDecimal sum = new BigDecimal(sumString);

            financeTracker.addTransaction(new Transaction(LocalDateTime.now(), transactionType, sum));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Incorrect transaction sum: " + sumString);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Incorrect transaction type: " + transactionTypeString);
        }
    }

    private void printAllTransactions() throws NoTransactionsException {
        HashMap<Integer, Transaction> transactionsMap =
                financeTracker.getTransactionsBetweenDates(LocalDateTime.MIN, LocalDateTime.MAX);

        if (transactionsMap.isEmpty())
            throw new NoTransactionsException("No transactions have been added yet.");

        transactionsMap.forEach((key, value) -> System.out.println(key + " " + value));
    }

    private void printTransactionById() throws IllegalArgumentException, NoTransactionsException {
        System.out.println("Enter the transaction ID");
        String idString = scanner.nextLine();
        try {
            int id = Integer.parseInt(idString);

            System.out.println(id + " " + financeTracker.getTransactionById(id));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Incorrect ID: " + idString);
        }
    }

    private void printTransactionsBetweenDates() throws DateTimeException, NoTransactionsException {

        System.out.println("Enter the start date in the following format: yyyy-mm-dd");
        LocalDateTime startDate = enterTheDate();
        System.out.println("Enter the end date in the following format: yyyy-mm-dd");
        LocalDateTime endDate = enterTheDate();

        HashMap<Integer, Transaction> transactionsMap =
                financeTracker.getTransactionsBetweenDates(startDate, endDate);

        if (transactionsMap.isEmpty())
            throw new NoTransactionsException("No transactions between these dates.");

        System.out.println();

        transactionsMap.forEach((key, value) -> System.out.println(key + " " + value));
    }

    private LocalDateTime enterTheDate() throws DateTimeException {
        String[] endDateTimeStrings = scanner.nextLine().split("-");
        try {
            int[] endDateTimeNumbers = Arrays.stream(endDateTimeStrings).mapToInt(Integer::parseInt).toArray();
            return LocalDateTime.of(endDateTimeNumbers[0], endDateTimeNumbers[1], endDateTimeNumbers[2], 0, 0, 0);
        } catch (DateTimeException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new DateTimeException("Incorrect date format: " + String.join("-", endDateTimeStrings));
        }
    }

    private void printBalance() {
        System.out.println(financeTracker.getBalance());
    }

    private void addTransactionsFromFile() throws InvalidPathException, FileFormatException, FileNotFoundException {
        System.out.println("Enter the file absolute path. " +
                "The file must be a CSV file, and all lines should be formatted as date,type,sum");
        Path filePath = enterFilePath();

        financeTracker.addTransactionsFromFile(filePath);
    }

    private void replaceCurrentTransactionsWithFile()
            throws InvalidPathException, FileFormatException, FileNotFoundException {
        System.out.println("Enter the file absolute path. " +
                "The file must be a CSV file, and all lines should be formatted as date,type,sum");
        Path filePath = enterFilePath();

        financeTracker.replaceTransactionsWithFileTransactions(filePath);
    }

    private void saveCurrentTransactionsToFile()
            throws NoTransactionsException, FileFormatException, FileCreationException, FileNotFoundException {
        System.out.println("Enter the .csv file absolute path. " +
                "File will be created, if it doesn't exist, but it must be created in an existing directory.");
        Path filePath = enterFilePath();

        financeTracker.saveTransactionsToFile(filePath);
    }

    private Path enterFilePath() throws InvalidPathException {
        String filePathString = scanner.nextLine();
        Path filePath;
        try {
            filePath = Paths.get(filePathString);
        } catch (InvalidPathException e) {
            throw new InvalidPathException(filePathString, "Incorrect path format");
        }

        if (!filePath.isAbsolute())
            throw new InvalidPathException(filePathString, "The path must be absolute");

        return filePath;
    }
}
