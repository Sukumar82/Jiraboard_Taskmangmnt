package org.example.jiraboard.service;


import static org.junit.jupiter.api.Assertions.*;


import org.example.jiraboard.model.User;
import org.example.jiraboard.repository.UserRepository;
import org.example.jiraboard.security.JwtUtils;
import org.example.jiraboard.service.UserServiceImpl;
import org.example.jiraboard.service.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {


    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setUserName("sampleUser");
        user.setPassword("encodedPassword");
    }

    @Test
    void testCreateUser() {
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertNotNull(createdUser);
        assertEquals("sampleUser", createdUser.getUserName());
        assertEquals("encodedPassword", createdUser.getPassword());
    }

    @Test
    void testGetUserById() {
        when(userRepository.findById(any(String.class))).thenReturn(Optional.of(user));

        User foundUser = userService.getUserById(1L);

        assertNotNull(foundUser);
        assertEquals("sampleUser", foundUser.getUserName());
    }

    @Test
    void testUpdateUser() {
        when(userRepository.findById(any(String.class))).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.updateUser(1L, user);

        assertNotNull(updatedUser);
        assertEquals("sampleUser", updatedUser.getUserName());
    }

    @Test
    void testDeleteUser() {
        when(userRepository.findById(any(String.class))).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(any(User.class));

        assertDoesNotThrow(() -> userService.deleteUser(1L));
    }


}