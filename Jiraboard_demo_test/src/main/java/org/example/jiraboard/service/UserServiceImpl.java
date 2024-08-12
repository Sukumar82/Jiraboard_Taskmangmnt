package org.example.jiraboard.service;

import org.example.jiraboard.exception.CustomApplicationException;
import org.example.jiraboard.model.User;
import org.example.jiraboard.repository.UserRepository;
import org.example.jiraboard.security.JwtUtils;
import org.example.jiraboard.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of UserService interface, providing user-related operations.
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    /**
     * Constructor for UserServiceImpl, autowiring required dependencies.
     *
     * @param userRepository  UserRepository instance for database operations.
     * @param passwordEncoder PasswordEncoder instance for encoding passwords.
     * @param jwtUtils        JwtUtils instance for JWT operations.
     */
    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Loads a User entity by username.
     *
     * @param username The username of the user to load.
     * @return User entity if found, null otherwise.
     */
    @Override
    public User loadUserEntityByUsername(String username) {
        return userRepository.findByUserName(username);
    }

    /**
     * Checks if the provided raw password matches the encoded password of the user.
     *
     * @param user        The user whose password is to be checked.
     * @param rawPassword The raw password to check.
     * @return true if passwords match, false otherwise.
     */
    @Override
    public boolean checkPassword(User user, String rawPassword) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        logger.info("Checking password for user: {}", user.getUserName());
        logger.info("Raw password: '{}'", rawPassword);
        logger.info("Encoded password from database: '{}'", user.getPassword());

        // Perform password match
        boolean match = passwordEncoder.matches(rawPassword, user.getPassword());
        logger.info("Password match result: {}", match);

        // Re-encode the raw password for debugging purposes
        String reEncodedPassword = passwordEncoder.encode(rawPassword);
        logger.info("Re-encoded password for debugging: '{}'", reEncodedPassword);

        return match;

    }

    /**
     * Creates a new user, encoding their password before saving.
     *
     * @param user The user to create.
     * @return The saved user entity.
     */
    @Override
    public User createUser(User user) {
        logger.info("Creating user: {}", user.getUserName());

        // Encode the password
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        logger.info("Encoded password for new user: '{}'", encodedPassword);

        // Save the user
        User savedUser = userRepository.save(user);
        logger.info("User '{}' created with ID: {}", savedUser.getUserName(), savedUser.getUserId());

        return savedUser;
    }

    /**
     * Generates a JWT token for the authenticated user.
     *
     * @param authentication The authentication object.
     * @return JWT token as a String.
     */
    @Override
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtils.generateJwtToken(userDetails);
        logger.info("Generated JWT token for user: {}", userDetails.getUsername());
        return token;
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id The ID of the user.
     * @return User entity if found.
     * @throws RuntimeException if user not found.
     */
    @Override
    public User getUserById(Long id) {

        return userRepository.findById(id.toString()).orElseThrow(() ->
                new CustomApplicationException("User not found with ID: " + id, 404));


//        Optional<User> userOpt = userRepository.findById(id.toString());
//        if (userOpt.isPresent()) {
//            logger.info("User found with ID: {}", id);
//            return userOpt.get();
//        } else {
//            logger.error("User not found with ID: {}", id);
//            throw new RuntimeException("User not found with ID: " + id);
//        }
    }

    /**
     * Retrieves all users.
     *
     * @return List of all users.
     */
    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        logger.info("Retrieved {} users from the database.", users.size());
        return users;
    }

    /**
     * Updates an existing user's information.
     *
     * @param id          The ID of the user to update.
     * @param updatedUser The user entity with updated information.
     * @return The updated user entity.
     */
    @Override
    public User updateUser(Long id, User updatedUser) {
        User existingUser = getUserById(id);

        // Update username if provided
        if (updatedUser.getUserName() != null && !updatedUser.getUserName().isEmpty()) {
            logger.info("Updating username for user ID {}: '{}' -> '{}'", id, existingUser.getUserName(), updatedUser.getUserName());
            existingUser.setUserName(updatedUser.getUserName());
        }

        // Update password if provided
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            logger.info("Updating password for user ID {}", id);
            String encodedPassword = passwordEncoder.encode(updatedUser.getPassword());
            existingUser.setPassword(encodedPassword);
            logger.info("New encoded password: '{}'", encodedPassword);
        }

        // Save the updated user
        User savedUser = userRepository.save(existingUser);
        logger.info("User ID {} updated successfully.", id);

        return savedUser;
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id The ID of the user to delete.
     */
    @Override
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
        logger.info("User ID {} deleted successfully.", id);
    }

    /**
     * Manually encodes a password and logs it. Useful for debugging.
     *
     * @param rawPassword The raw password to encode.
     */
    public void logEncodedPassword(String rawPassword) {
        String encodedPassword = passwordEncoder.encode(rawPassword);
        logger.info("Manually encoded password for '{}': '{}'", rawPassword, encodedPassword);
    }

    /**
     * Manually checks if a raw password matches an encoded password. Useful for debugging.
     *
     * @param rawPassword     The raw password.
     * @param encodedPassword The encoded password.
     * @return true if matches, false otherwise.
     */
    public boolean manualPasswordMatch(String rawPassword, String encodedPassword) {
        boolean match = passwordEncoder.matches(rawPassword, encodedPassword);
        logger.info("Manual password match check. Raw: '{}', Encoded: '{}', Result: {}", rawPassword, encodedPassword, match);
        return match;
    }

    /**
     * Re-encodes and updates the password of a user in the database.
     * Useful if the encoding mechanism has changed.
     *
     * @param userId        The ID of the user.
     * @param newRawPassword The new raw password to set.
     */
    public void reEncodeAndUpdatePassword(Long userId, String newRawPassword) {
        User user = getUserById(userId);
        String newEncodedPassword = passwordEncoder.encode(newRawPassword);
        user.setPassword(newEncodedPassword);
        userRepository.save(user);
        logger.info("Password for user ID {} re-encoded and updated.", userId);
    }
}
