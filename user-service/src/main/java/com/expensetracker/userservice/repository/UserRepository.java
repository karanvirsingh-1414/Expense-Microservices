package com.expensetracker.userservice.repository;

import com.expensetracker.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
    boolean existsByUsernameOrEmail(String username, String email);
}
