package com.turmericstore.service;

import com.turmericstore.dto.AuthRequest;
import com.turmericstore.dto.AuthResponse;
import com.turmericstore.dto.RegisterRequest;
import com.turmericstore.dto.UserDTO;
import com.turmericstore.exception.BadRequestException;
import com.turmericstore.exception.UnauthorizedException;
import com.turmericstore.model.User;
import com.turmericstore.repository.UserRepository;
import com.turmericstore.security.JwtTokenProvider;
import com.turmericstore.util.ModelMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final ModelMapperUtil modelMapper;

    @Autowired
    public AuthService(UserRepository userRepository, UserService userService, PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider, AuthenticationManager authenticationManager,
                       ModelMapperUtil modelMapper) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.modelMapper = modelMapper;
    }

    public AuthResponse login(AuthRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate tokens
            String token = tokenProvider.createToken(authentication);
            String refreshToken = tokenProvider.createRefreshToken(authentication);

            // Get user details for response
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new UnauthorizedException("User not found"));

            // Update last login timestamp
            userService.updateLastLogin(user.getId());

            return AuthResponse.builder()
                    .token(token)
                    .refreshToken(refreshToken)
                    .userId(user.getId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .roles(user.getRoles())
                    .expiresIn(tokenProvider.getExpirationTimeFromToken(token) - System.currentTimeMillis())
                    .build();
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Invalid email or password");
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Authentication failed", e);
        }
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        try {
            // Check if email already exists
            Optional<User> existingUser = userRepository.findByEmail(registerRequest.getEmail());
            if (existingUser.isPresent()) {
                throw new BadRequestException("Email is already registered");
            }

            // Create new user
            UserDTO userDTO = UserDTO.builder()
                    .email(registerRequest.getEmail())
                    .firstName(registerRequest.getFirstName())
                    .lastName(registerRequest.getLastName())
                    .phoneNumber(registerRequest.getPhoneNumber())
                    .active(true)
                    .build();

            // Set roles for new user
            Set<String> roles = new HashSet<>();
            roles.add("ROLE_USER");
            userDTO.setRoles(roles);

            // Create user in the system
            UserDTO createdUser = userService.createUser(userDTO, registerRequest.getPassword());

            // Authenticate the new user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            registerRequest.getEmail(),
                            registerRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate tokens
            String token = tokenProvider.createToken(authentication);
            String refreshToken = tokenProvider.createRefreshToken(authentication);

            return AuthResponse.builder()
                    .token(token)
                    .refreshToken(refreshToken)
                    .userId(createdUser.getId())
                    .email(createdUser.getEmail())
                    .fullName(createdUser.getFullName())
                    .roles(createdUser.getRoles())
                    .expiresIn(tokenProvider.getExpirationTimeFromToken(token) - System.currentTimeMillis())
                    .build();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Registration failed", e);
        }
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        String username = tokenProvider.getUsernameFromToken(refreshToken);

        try {
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UnauthorizedException("User not found"));

            // Create authentication object
            Set<GrantedAuthority> authorities = user.getRoles().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    username, null, authorities);

            // Generate new tokens
            String newToken = tokenProvider.createToken(authentication);
            String newRefreshToken = tokenProvider.createRefreshToken(authentication);

            return AuthResponse.builder()
                    .token(newToken)
                    .refreshToken(newRefreshToken)
                    .userId(user.getId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .roles(user.getRoles())
                    .expiresIn(tokenProvider.getExpirationTimeFromToken(newToken) - System.currentTimeMillis())
                    .build();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Token refresh failed", e);
        }
    }
}
