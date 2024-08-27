package com.FinanceTracker.ui;

import com.FinanceTracker.data.transactionunit.Transaction;
import com.FinanceTracker.data.transactionunit.TransactionType;
import com.FinanceTracker.exceptions.*;
import com.FinanceTracker.logic.CommandManager;
import com.FinanceTracker.logic.InputConverter;
import com.FinanceTracker.exceptions.TransactionsFileCreationException;
import com.FinanceTracker.exceptions.TransactionsFileFormatException;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Scanner;

public class UserInterface {
    private final CommandManager commandManager = new CommandManager();
    private final Scanner scanner;
    private boolean isFileAttached = false;

    public UserInterface(Scanner scanner) {
        this.scanner = scanner;
    }

    public void callMainMenu() {
        while (true) {
            System.out.println("Main menu commands:");
            System.out.println("1 - Add transaction");
            System.out.println("2 - Print transactions");
            System.out.println("3 - Attach file");
            if (isFileAttached) {
                System.out.println("4 - Unattach file");
                System.out.println("5 - Save transactions to file");
                System.out.println("6 - Load transactions from file");
            }
            System.out.println("0 - Quit program");
            System.out.print("> ");
            String command = scanner.nextLine();
            System.out.println();

            try {
                if (command.equals("1")) {
                    enterAndAddTransaction();
                    System.out.println();
                } else if (command.equals("2")) {
                    callPrintMenu();
                } else if (command.equals("3")) {
                    attachFile();
                    System.out.println();
                } else if (command.equals("4") && isFileAttached) {
                    unattachFile();
                    System.out.println();
                } else if (command.equals("5") && isFileAttached) {
                    callSaveMenu();
                } else if (command.equals("6") && isFileAttached) {
                    callLoadMenu();
                } else if (command.equals("0")) {
                    break;
                }
            } catch (IncorrectInputException | TransactionsFileFormatException | TransactionsFileCreationException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void enterAndAddTransaction() throws IncorrectInputException {
        LocalDateTime dateTime = InputConverter.convertDateTime(enterDateTime("transaction"));
        System.out.println("Enter the transaction type (INCOME/EXPENSE)");
        TransactionType transactionType = InputConverter.convertTransactionType(scanner.nextLine().trim().toUpperCase());
        System.out.println("Enter the sum of transaction");
        BigDecimal sum = InputConverter.convertSum(scanner.nextLine().trim());

        this.commandManager.addTransaction(dateTime, transactionType, sum);
        printSuccessMessage(1);
    }

    private void callPrintMenu() {
        while (true) {
            System.out.println("Print menu commands:");
            System.out.println("1 - Print transaction by id");
            System.out.println("2 - Print transactions between dates");
            System.out.println("3 - Print all transactions");
            System.out.println("0 - Exit menu");
            System.out.print("> ");
            String command = scanner.nextLine();
            System.out.println();

            try {
                if (command.equals("1")) {
                    printTransactionById();
                    System.out.println();
                } else if (command.equals("2")) {
                    printTransactionsBetweenDates();
                    System.out.println();
                } else if (command.equals("3")) {
                    printAllTransactions();
                    System.out.println();
                } else if (command.equals("0")) {
                    break;
                }
            } catch (NoTransactionsException | IncorrectInputException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void printTransactionById() throws IncorrectInputException, NoTransactionsException {
        int id = InputConverter.convertId(enterId());

        System.out.println(id + " " + this.commandManager.getTransactionById(id));
    }

    private void printTransactionsBetweenDates() throws NoTransactionsException, IncorrectInputException {
        LocalDateTime startDateTime = InputConverter.convertDateTime(enterDateTime("start"));
        LocalDateTime endDateTime = InputConverter.convertDateTime(enterDateTime("end"));

        Map<Integer, Transaction> transactionsMap =
                this.commandManager.getTransactionsBetweenDates(startDateTime, endDateTime);

        System.out.println();

        transactionsMap.forEach((key, value) -> System.out.println(key + " " + value));
    }

    private void printAllTransactions() throws NoTransactionsException {
        Map<Integer, Transaction> transactionsMap =
                this.commandManager.getAllTransactions();

        transactionsMap.forEach((key, value) -> System.out.println(key + " " + value));
    }

    private void attachFile() throws IncorrectInputException, TransactionsFileCreationException, TransactionsFileFormatException {
        System.out.println("File must have the extension .csv, and it should be both readable and writable. " +
                "\nAll lines must be formatted as id,dateTime,type,sum. " +
                "Id cannot be less than 0, and dateTime must be presented in accordance with ISO 8601. " +
                "\nFile will be created, if it doesn't exist, but it must be created in an existing directory.");
        System.out.println("\nWARNING!\nPlease be cautious when manually editing the attached file. " +
                "If any of the conditions described above are violated during the changes, " +
                "the program will terminate with an error when attempting to work with the file. " +
                "\nIt is recommended to modify transaction files only through the program.");
        System.out.print("\nEnter the file absolute path: ");
        Path filePath = InputConverter.convertPath(scanner.nextLine());

        this.commandManager.attachFile(filePath);
        isFileAttached = true;

        printSuccessMessage(1);
    }

    private void unattachFile() {
        this.commandManager.unattachFile();
        isFileAttached = false;
        printSuccessMessage(0);
    }

    private void callSaveMenu() {
        while (true) {
            System.out.println("Save menu commands:");
            System.out.println("1 - Save transaction by id to file");
            System.out.println("2 - Save transactions between dates to file");
            System.out.println("3 - Save all transactions to file");
            System.out.println("0 - Exit menu");
            System.out.print("> ");
            String command = scanner.nextLine();
            System.out.println();

            try {
                if (command.equals("1")) {
                    saveTransactionByIdToFile();
                    System.out.println();
                } else if (command.equals("2")) {
                    saveTransactionsBetweenDatesToFile();
                    System.out.println();
                } else if (command.equals("3")) {
                    saveAllTransactionsToFile();
                    System.out.println();
                } else if (command.equals("0")) {
                    break;
                }
            } catch (TransactionsFileIsNotAttachedException | NoTransactionsException | IncorrectInputException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void saveTransactionByIdToFile()
            throws IncorrectInputException, TransactionsFileIsNotAttachedException, NoTransactionsException
    {
        int id = InputConverter.convertId(enterId());

        this.commandManager.saveTransactionByIdToFile(id);

        printSuccessMessage(1);
    }

    private void saveTransactionsBetweenDatesToFile()
            throws IncorrectInputException, TransactionsFileIsNotAttachedException, NoTransactionsException
    {
        LocalDateTime startDateTime = InputConverter.convertDateTime(enterDateTime("start"));
        LocalDateTime endDateTime = InputConverter.convertDateTime(enterDateTime("end"));

        this.commandManager.saveTransactionsBetweenDatesToFile(startDateTime, endDateTime);

        printSuccessMessage(1);
    }

    private void saveAllTransactionsToFile() throws TransactionsFileIsNotAttachedException, NoTransactionsException {
        this.commandManager.saveAllTransactionsToFile();

        printSuccessMessage(1);
    }

    private void callLoadMenu() {
        while (true) {
            System.out.println("Load menu commands:");
            System.out.println("1 - Load transaction by id from file");
            System.out.println("2 - Load transactions between dates from file");
            System.out.println("3 - Load all transactions from file");
            System.out.println("0 - Exit menu");
            System.out.print("> ");
            String command = scanner.nextLine();
            System.out.println();

            try {
                if (command.equals("1")) {
                    loadTransactionsByIdFromFile();
                    System.out.println();
                } else if (command.equals("2")) {
                    loadTransactionsBetweenDatesFromFile();
                    System.out.println();
                } else if (command.equals("3")) {
                    loadAllTransactionsFromFile();
                    System.out.println();
                } else if (command.equals("0")) {
                    break;
                }
            } catch (TransactionsFileIsNotAttachedException | NoTransactionsException | IncorrectInputException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void loadTransactionsByIdFromFile()
            throws IncorrectInputException, TransactionsFileIsNotAttachedException, NoTransactionsException
    {
        int id = InputConverter.convertId(enterId());

        this.commandManager.loadTransactionByIdFromFile(id);

        printSuccessMessage(1);
    }

    private void loadTransactionsBetweenDatesFromFile()
            throws IncorrectInputException, TransactionsFileIsNotAttachedException, NoTransactionsException
    {

        LocalDateTime startDateTime = InputConverter.convertDateTime(enterDateTime("start"));
        LocalDateTime endDateTime = InputConverter.convertDateTime(enterDateTime("end"));

        this.commandManager.loadTransactionsBetweenDatesFromFile(startDateTime, endDateTime);

        printSuccessMessage(1);
    }

    private void loadAllTransactionsFromFile() throws TransactionsFileIsNotAttachedException, NoTransactionsException {
        this.commandManager.loadAllTransactionsFromFile();

        printSuccessMessage(0);
    }

    private String enterId() {
        System.out.print("Enter the transaction ID: ");
        return scanner.nextLine().trim();
    }

    private String enterDateTime(String typeOfDateTime) {
        System.out.println("Enter the " + typeOfDateTime + " date and time in the following format: yyyy-MM-dd HH:mm:ss. " +
                "Or you can just enter date without time");
        return scanner.nextLine().trim();
    }

    private void printSuccessMessage(int numberOfEmptyLinesBeforeMessage) {
        System.out.println("\n".repeat(numberOfEmptyLinesBeforeMessage) + "Success!");
    }
}
