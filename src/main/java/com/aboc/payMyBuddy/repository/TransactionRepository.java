package com.aboc.payMyBuddy.repository;

import com.aboc.payMyBuddy.model.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Integer> {
    List<Transaction> findTransactionBySenderId(int id);
}