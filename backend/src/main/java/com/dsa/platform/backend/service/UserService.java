package com.dsa.platform.backend.service;

import static com.dsa.platform.backend.converter.UserConverter.toUserFromRegisterRequestWithoutPassword;

import com.dsa.platform.backend.dto.request.RegisterRequest;
import com.dsa.platform.backend.exception.UserAlreadyExistsException;
import com.dsa.platform.backend.model.User;
import com.dsa.platform.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service class for managing users. */
@Service
public class UserService {
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * Registers a new user in the system.
	 *
	 * @param request
	 *            The registration request containing user details.
	 * @return The newly created and saved User entity.
	 * @throws UserAlreadyExistsException
	 *             if the username or email is already taken. @Transactional:
	 *             Ensures that the entire method runs within a single database
	 *             transaction. If any part fails, the transaction is rolled back.
	 */
	@Transactional
	public User registerUser(RegisterRequest request) {
		logger.info("Attempting to register new user with username: {}", request.handle());

		validateUserDoesNotExist(request.handle(), request.email());

		User newUser = toUserFromRegisterRequestWithoutPassword(request);
		String hashedPassword = passwordEncoder.encode(request.password());
		newUser.setPasswordHash(hashedPassword);

		User savedUser = userRepository.save(newUser);
		logger.info(
				"Successfully registered user with handle: {} and ID: {}", savedUser.getHandle(), savedUser.getId());
		return savedUser;
	}

	private void validateUserDoesNotExist(String handle, String email) {
		if (userRepository.findByHandle(handle).isPresent()) {
			logger.error("User with handle '{}' already exists", handle);
			throw new UserAlreadyExistsException("Handle '" + handle + "' is already taken.");
		}
		if (userRepository.findByEmail(email).isPresent()) {
			logger.error("User with email '{}' already exists", email);
			throw new UserAlreadyExistsException("Email '" + email + "' is already registered.");
		}
	}
}
