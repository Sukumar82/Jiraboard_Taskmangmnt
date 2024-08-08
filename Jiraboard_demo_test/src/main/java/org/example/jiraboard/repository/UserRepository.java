package org.example.jiraboard.repository;

import org.example.jiraboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface UserRepository extends JpaRepository<User, String> {
    User findByUserName(String userName);
}
