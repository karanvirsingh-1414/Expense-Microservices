package com.expensetracker.apigateway;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController {
    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam String username, @RequestParam String email, @RequestParam String password, Model model) {
        // Forward registration to user-service
        RestTemplate restTemplate = new RestTemplate();
        String userServiceUrl = "http://localhost:8081/api/register";
        Map<String, String> req = new HashMap<>();
        req.put("username", username);
        req.put("email", email);
        req.put("password", password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(req, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(userServiceUrl, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                model.addAttribute("success", "Registration successful! Please login.");
                return "login";
            } else {
                model.addAttribute("error", "Registration failed. Try again.");
                return "register";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed. Try again.");
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username, @RequestParam String password, Model model) {
        // Forward login to user-service
        RestTemplate restTemplate = new RestTemplate();
        String userServiceUrl = "http://localhost:8081/api/login";
        Map<String, String> req = new HashMap<>();
        req.put("username", username);
        req.put("password", password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(req, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(userServiceUrl, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                // You can set session/cookie here if needed
                return "redirect:/";
            } else {
                model.addAttribute("error", "Login failed. Check your credentials.");
                return "login";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Login failed. Try again.");
            return "login";
        }
    }
}
