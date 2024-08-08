package org.example.jiraboard.repository;

import org.example.jiraboard.model.Task;
import org.example.jiraboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface TaskRepository extends JpaRepository<Task, Long> {

    Task findByTaskId(Long taskId);
}
