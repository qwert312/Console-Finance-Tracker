package com.data.transactionsstoring;

import com.data.transactionunit.Transaction;
import com.exceptions.NoTransactionsException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class RuntimeTransactionStorage implements TransactionStorage {
    private int lastFreeID = 0;
    private final Map<Integer, Transaction> transactions = new HashMap<>();

    public void addTransaction(Transaction transaction)  {
        transactions.put(lastFreeID, transaction);
        lastFreeID++;
    }

    public Transaction getTransactionById(int id) throws NoTransactionsException {
        Transaction searchedTransaction = transactions.get(id);

        if (searchedTransaction == null)
            throw new NoTransactionsException("No transaction with this Id: " + id);

        return transactions.get(id);
    }

    public Map<Integer, Transaction> getTransactionsBetweenDates(LocalDateTime startDateTime, LocalDateTime endDateTime)
            throws NoTransactionsException
    {
        HashMap<Integer, Transaction> searchedTransactions = new HashMap<>();
        for (Map.Entry<Integer, Transaction> transactionPair : transactions.entrySet()) {
            if (transactionPair.getValue().dateTime().isAfter(startDateTime)
                    && transactionPair.getValue().dateTime().isBefore(endDateTime))
                searchedTransactions.put(transactionPair.getKey(), transactionPair.getValue());
        }

        if (searchedTransactions.isEmpty())
            throw new NoTransactionsException("No transactions between " + startDateTime + " and " + endDateTime + ".");

        return searchedTransactions;
    }
}
