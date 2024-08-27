package com.logic;

import com.data.transactionsstoring.TransactionStorage;
import com.data.transactionunit.Transaction;
import com.exceptions.NoTransactionsException;

import java.time.LocalDateTime;
import java.util.Map;

public class TransactionCopier {

    private TransactionCopier() {}

    public static void copyTransactionById(int id, TransactionStorage sourceStorage, TransactionStorage targetStorage)
            throws NoTransactionsException
    {
        Transaction searchedTransaction = sourceStorage.getTransactionById(id);
        targetStorage.addTransaction(searchedTransaction);
    }

    public static void copyTransactionsBetweenDates(LocalDateTime startDateTime, LocalDateTime endDateTime,
                                                    TransactionStorage sourceStorage, TransactionStorage targetStorage)
            throws NoTransactionsException
    {
        Map<Integer, Transaction> searchedTransactions = sourceStorage.getTransactionsBetweenDates(startDateTime, endDateTime);

        for (Transaction transaction: searchedTransactions.values())
            targetStorage.addTransaction(transaction);
    }
}
