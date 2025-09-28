package com.expensetracker.userservice.controller;

import com.expensetracker.userservice.model.User;
import com.expensetracker.userservice.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class UserMicroserviceController {
    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        try {
            userService.registerUser(user);
            model.addAttribute("message", "Registration successful! Please login.");
            return "login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute User user, Model model, HttpSession session) {
        User loggedIn = userService.loginUser(user.getUsername(), user.getPassword());
        if (loggedIn != null) {
            session.setAttribute("userId", loggedIn.getId());
            // Redirect to salary page on expense-service with userId as query param
            return "redirect:http://localhost:8081/salary?userId=" + loggedIn.getId();
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}

@RestController
@RequestMapping("/api")
class UserRestController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User saved = userService.registerUser(user);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping(value = "/users/text", produces = "text/plain")
    public String getAllUsersAsText() {
        List<User> users = userService.getAllUsers();
        StringBuilder sb = new StringBuilder();
        for (User user : users) {
            sb.append("id: ").append(user.getId()).append("\n");
            sb.append("username: ").append(user.getUsername()).append("\n");
            sb.append("password: ").append(user.getPassword()).append("\n");
            sb.append("email: ").append(user.getEmail()).append("\n\n");
        }
        return sb.toString();
    }
}
