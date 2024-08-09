package org.example.jiraboard.service.interfaces;

import org.example.jiraboard.model.User;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserService {
    User createUser(User user);
    String generateToken(Authentication authentication);
    User getUserById(Long id);
    List<User> getAllUsers();
    User updateUser(Long id, User updatedUser);
    void deleteUser(Long id);
    User loadUserEntityByUsername(String username);
    boolean checkPassword(User user, String rawPassword);
}
