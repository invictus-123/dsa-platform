package com.dsa.platform.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.dsa.platform.backend.model.User;
import com.dsa.platform.backend.model.shared.UserRole;
import com.dsa.platform.backend.repository.UserRepository;
import com.dsa.platform.backend.service.dto.RegisterRequest;
import com.dsa.platform.backend.service.exception.UserAlreadyExistsException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setHandle("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("User");
    }

    @Test
    void testRegisterUser_Success() {
        when(userRepository.findByHandle(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword123");
        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setHandle(registerRequest.getHandle());
        savedUser.setEmail(registerRequest.getEmail());
        savedUser.setPasswordHash("hashedPassword123");
        savedUser.setRole(UserRole.USER);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.registerUser(registerRequest);

        assertNotNull(result);
        assertEquals("testuser", result.getHandle());
        assertEquals("hashedPassword123", result.getPasswordHash());
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();
        assertEquals("testuser", capturedUser.getHandle());
        assertEquals(UserRole.USER, capturedUser.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_handleAlreadyExists() {
        when(userRepository.findByHandle("testuser")).thenReturn(Optional.of(new User()));

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.registerUser(registerRequest));

        assertEquals("Handle '" + registerRequest.getHandle() + "' is already taken.",
                exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterUser_emailAlreadyExists() {
        when(userRepository.findByHandle("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.registerUser(registerRequest));

        assertEquals("Email '" + registerRequest.getEmail() + "' is already registered.",
                exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}