package com.turmericstore.service;

import com.turmericstore.dto.UserDTO;
import com.turmericstore.exception.BadRequestException;
import com.turmericstore.exception.ResourceNotFoundException;
import com.turmericstore.model.User;
import com.turmericstore.repository.UserRepository;
import com.turmericstore.util.ModelMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapperUtil modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, ModelMapperUtil modelMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDTO> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            return modelMapper.toUserDTOs(users);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch users", e);
        }
    }

    public UserDTO getUserById(String id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
            return modelMapper.toUserDTO(user);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch user with id: " + id, e);
        }
    }

    public UserDTO getUserByEmail(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
            return modelMapper.toUserDTO(user);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch user with email: " + email, e);
        }
    }

    public UserDTO createUser(UserDTO userDTO, String password) {
        try {
            // Check if email already exists
            if (userRepository.existsByEmail(userDTO.getEmail())) {
                throw new BadRequestException("Email is already taken");
            }

            User user = modelMapper.toUser(userDTO);
            user.setId(null); // Ensure we're creating a new user
            user.setPassword(passwordEncoder.encode(password));
            user.setCreatedAt(null); // Will be set in the repository
            user.setActive(true);

            // Set default roles if not provided
            if (user.getRoles() == null || user.getRoles().isEmpty()) {
                Set<String> roles = new HashSet<>();
                roles.add("ROLE_USER");
                user.setRoles(roles);
            }

            User savedUser = userRepository.save(user);
            return modelMapper.toUserDTO(savedUser);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to create user", e);
        }
    }

    public UserDTO updateUser(String id, UserDTO userDTO) {
        try {
            // Verify the user exists
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

            // If email is being changed, check if new email is already taken
            if (!existingUser.getEmail().equals(userDTO.getEmail()) &&
                    userRepository.existsByEmail(userDTO.getEmail())) {
                throw new BadRequestException("Email is already taken");
            }

            User user = modelMapper.toUser(userDTO);
            user.setId(id);
            user.setPassword(existingUser.getPassword()); // Keep the existing password

            User updatedUser = userRepository.save(user);
            return modelMapper.toUserDTO(updatedUser);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to update user with id: " + id, e);
        }
    }

    public void deleteUser(String id) {
        try {
            // Verify the user exists
            userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

            userRepository.delete(id);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to delete user with id: " + id, e);
        }
    }

    public void updateLastLogin(String id) {
        try {
            userRepository.updateLastLogin(id, System.currentTimeMillis());
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to update last login for user with id: " + id, e);
        }
    }

    public void changePassword(String id, String oldPassword, String newPassword) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

            // Verify old password
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                throw new BadRequestException("Current password is incorrect");
            }

            // Update with new password
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setUpdatedAt(System.currentTimeMillis());

            userRepository.save(user);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to change password for user with id: " + id, e);
        }
    }
}