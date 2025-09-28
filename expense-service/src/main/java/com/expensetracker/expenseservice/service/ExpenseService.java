package com.expensetracker.expenseservice.service;

import com.expensetracker.expenseservice.model.Expense;
import com.expensetracker.expenseservice.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;

    // Store salary per user
    private Map<Long, Double> userSalary = new ConcurrentHashMap<>();

    public void setSalary(Long userId, double salary) {
        userSalary.put(userId, salary);
    }
    public double getSalary(Long userId) {
        return userSalary.getOrDefault(userId, 0.0);
    }
    public Expense addExpense(Expense expense) {
        return expenseRepository.save(expense);
    }
    public List<Expense> getAllExpenses(Long userId) {
        return expenseRepository.findByUserId(userId);
    }
    public List<Expense> getExpensesByCategory(Long userId, String category) {
        return expenseRepository.findByUserIdAndCategory(userId, category);
    }
    public double getTotalSpent(Long userId) {
        return getAllExpenses(userId).stream().mapToDouble(Expense::getAmount).sum();
    }
    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id).orElse(null);
    }
    public void updateExpense(Long id, Expense updatedExpense) {
        Expense existing = expenseRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setTitle(updatedExpense.getTitle());
            existing.setAmount(updatedExpense.getAmount());
            existing.setDate(updatedExpense.getDate());
            existing.setCategory(updatedExpense.getCategory());
            existing.setDescription(updatedExpense.getDescription());
            expenseRepository.save(existing);
        }
    }
    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }
}
