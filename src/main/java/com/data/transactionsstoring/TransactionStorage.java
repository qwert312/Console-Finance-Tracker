package com.data.transactionsstoring;

import com.data.transactionunit.Transaction;
import com.exceptions.NoTransactionsException;

import java.time.LocalDateTime;
import java.util.Map;

public interface TransactionStorage {

    void addTransaction(Transaction transaction);

    Transaction getTransactionById(int id) throws NoTransactionsException;

    Map<Integer, Transaction> getTransactionsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) throws NoTransactionsException;
}
