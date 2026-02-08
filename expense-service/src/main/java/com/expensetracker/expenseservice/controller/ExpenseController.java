package com.expensetracker.expenseservice.controller;

import com.expensetracker.expenseservice.model.Expense;
import com.expensetracker.expenseservice.service.ExpenseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;

    // --- SALARY FLOW: Commented out old logic for clean minimal implementation ---
    // @GetMapping("/salary")
    // public String showSalaryForm(@RequestParam(value = "userId", required =
    // false) Long userIdParam, HttpSession session, Model model) {
    // Long userId = (Long) session.getAttribute("userId");
    // if (userId == null && userIdParam != null) {
    // session.setAttribute("userId", userIdParam);
    // userId = userIdParam;
    // }
    // if (userId == null) return "redirect:http://localhost:8082/login";
    // double salary = expenseService.getSalary(userId);
    // if (salary > 0) {
    // model.addAttribute("hasSalary", true);
    // model.addAttribute("salary", salary);
    // } else {
    // model.addAttribute("hasSalary", false);
    // }
    // return "salary";
    // }

    // @PostMapping("/salary")
    // public String setSalary(@RequestParam double salary, HttpSession session) {
    // Long userId = (Long) session.getAttribute("userId");
    // if (userId == null) return "redirect:http://localhost:8082/login";
    // expenseService.setSalary(userId, salary);
    // return "redirect:/expense";
    // }

    // @PostMapping("/salary/continue")
    // public String continueWithSalary(HttpSession session) {
    // Long userId = (Long) session.getAttribute("userId");
    // if (userId == null) return "redirect:http://localhost:8082/login";
    // return "redirect:/expense";
    // }

    // --- NEW MINIMAL SALARY FLOW ---
    @GetMapping("/salary")
    public String showSalaryForm(@RequestParam(value = "userId", required = false) Long userIdParam,
            HttpSession session) {
        if (userIdParam != null) {
            session.setAttribute("userId", userIdParam);
        }
        return "salary";
    }

    @PostMapping("/salary")
    public String setSalary(@RequestParam double salary, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null)
            return "redirect:http://localhost:8082/login";
        expenseService.setSalary(userId, salary);
        return "redirect:/expense";
    }

    // Hardcoded limits for simplicity. In a real app, these would be in a DB.
    private final java.util.Map<String, Double> categoryLimits = new java.util.HashMap<>() {
        {
            put("Food", 5000.0);
            put("Travel", 3000.0);
            put("Entertainment", 2000.0);
            put("Utilities", 4000.0);
            put("shopping", 5000.0);
            put("style", 2000.0);
            // Default for others
        }
    };

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null)
            return "redirect:http://localhost:8082/login";

        double salary = expenseService.getSalary(userId);
        double spent = expenseService.getTotalSpent(userId);
        List<Expense> expenses = expenseService.getAllExpenses(userId);

        // Calculate per-category spending
        java.util.Map<String, Double> categorySpending = expenses.stream()
                .collect(java.util.stream.Collectors.groupingBy(Expense::getCategory,
                        java.util.stream.Collectors.summingDouble(Expense::getAmount)));

        // Prepare progress data
        List<java.util.Map<String, Object>> progressList = new java.util.ArrayList<>();
        java.util.Set<String> allCategories = new java.util.HashSet<>(categoryLimits.keySet());
        allCategories.addAll(categorySpending.keySet());

        for (String category : allCategories) {
            double current = categorySpending.getOrDefault(category, 0.0);
            double limit = categoryLimits.getOrDefault(category, 2000.0); // Default limit 2000
            double percentage = (current / limit) * 100;

            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("name", category);
            map.put("current", current);
            map.put("limit", limit);
            map.put("percentage", Math.min(percentage, 100)); // Cap at 100 for bar width
            map.put("isOverBudget", current > limit);
            map.put("color", percentage > 90 ? "bg-danger" : (percentage > 50 ? "bg-warning" : "bg-info"));
            progressList.add(map);
        }

        model.addAttribute("salary", salary);
        model.addAttribute("remaining", salary - spent);
        model.addAttribute("categories", expenses.stream().map(Expense::getCategory).distinct().toList());
        model.addAttribute("progressList", progressList);

        return "dashboard";
    }

    @GetMapping("/expense")
    public String showExpenseForm(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null)
            return "redirect:http://localhost:8082/login";
        model.addAttribute("expense", new Expense());
        model.addAttribute("categories",
                expenseService.getAllExpenses(userId).stream().map(Expense::getCategory).distinct().toList());
        model.addAttribute("expensesByCategory", expenseService.getAllExpenses(userId).stream()
                .collect(java.util.stream.Collectors.groupingBy(Expense::getCategory)));
        model.addAttribute("remaining", expenseService.getSalary(userId) - expenseService.getTotalSpent(userId));
        return "expense";
    }

    @PostMapping("/expense")
    public String addExpense(@ModelAttribute Expense expense, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null)
            return "redirect:http://localhost:8082/login";
        expense.setUserId(userId);
        expenseService.addExpense(expense);
        model.addAttribute("expense", new Expense());
        model.addAttribute("categories",
                expenseService.getAllExpenses(userId).stream().map(Expense::getCategory).distinct().toList());
        model.addAttribute("expensesByCategory", expenseService.getAllExpenses(userId).stream()
                .collect(java.util.stream.Collectors.groupingBy(Expense::getCategory)));
        model.addAttribute("remaining", expenseService.getSalary(userId) - expenseService.getTotalSpent(userId));
        return "expense";
    }

    @GetMapping("/category/{category}")
    public String showCategory(@PathVariable String category, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null)
            return "redirect:http://localhost:8082/login";
        List<Expense> expenses = expenseService.getExpensesByCategory(userId, category);
        double totalSpent = expenses.stream().mapToDouble(Expense::getAmount).sum();
        model.addAttribute("category", category);
        model.addAttribute("expenses", expenses);
        model.addAttribute("totalSpent", totalSpent);
        return "category";
    }

    @GetMapping("/expense/edit/{id}")
    public String showEditExpenseForm(@PathVariable Long id, Model model) {
        Expense expense = expenseService.getExpenseById(id);
        model.addAttribute("expense", expense);
        return "edit-expense";
    }

    @PostMapping("/expense/edit/{id}")
    public String editExpense(@PathVariable Long id, @ModelAttribute Expense expense) {
        expenseService.updateExpense(id, expense);
        return "redirect:/category/" + expense.getCategory();
    }

    @GetMapping("/expense/delete/{id}")
    public String deleteExpense(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null)
            return "redirect:http://localhost:8082/login";

        Expense expense = expenseService.getExpenseById(id);
        if (expense == null || !expense.getUserId().equals(userId)) {
            // Security check failed or expense not found
            return "redirect:/dashboard";
        }

        String category = expense.getCategory();
        expenseService.deleteExpense(id);

        // Check if category is now empty
        List<Expense> remaining = expenseService.getExpensesByCategory(userId, category);
        if (remaining.isEmpty()) {
            return "redirect:/dashboard";
        }

        return "redirect:/category/" + category;
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:http://localhost:8082/logout";
    }
}

@RestController
@RequestMapping("/api")
class ExpenseRestController {
    @Autowired
    private ExpenseService expenseService;

    @PostMapping("/expense")
    public ResponseEntity<?> addExpense(@RequestBody Expense expense) {
        try {
            Expense saved = expenseService.addExpense(expense);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/expenses/{userId}")
    public List<Expense> getExpensesByUser(@PathVariable Long userId) {
        return expenseService.getAllExpenses(userId);
    }

    @GetMapping(value = "/expenses/{userId}/text", produces = "text/plain")
    public String getExpensesByUserAsText(@PathVariable Long userId) {
        List<Expense> expenses = expenseService.getAllExpenses(userId);
        StringBuilder sb = new StringBuilder();
        for (Expense exp : expenses) {
            sb.append("id: ").append(exp.getId()).append("\n");
            sb.append("title: ").append(exp.getTitle()).append("\n");
            sb.append("amount: ").append(exp.getAmount()).append("\n");
            sb.append("date: ").append(exp.getDate()).append("\n");
            sb.append("category: ").append(exp.getCategory()).append("\n");
            sb.append("description: ").append(exp.getDescription()).append("\n");
            sb.append("userId: ").append(exp.getUserId()).append("\n\n");
        }
        return sb.toString();
    }
}
