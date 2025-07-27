package com.online.judge.backend.factory;

import com.online.judge.backend.model.User;
import com.online.judge.backend.model.shared.UserRole;

public class UserFactory {
	private static final String HANDLE = "testuser";
	private static final String FIRST_NAME = "First";
	private static final String LAST_NAME = "Last";
	private static final String EMAIL = "test@example.com";
	private static final String PASSWORD = "password123";
	private static final UserRole ROLE = UserRole.USER;

	public static User createUser() {
		return createUser(HANDLE);
	}

	public static User createUser(String handle) {
		return createUser(handle, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD, ROLE);
	}

	public static User createUser(String handle, UserRole role) {
		return createUser(handle, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD, role);
	}

	public static User createUser(
			String handle, String firstName, String lastName, String email, String password, UserRole role) {
		User user = new User();
		user.setHandle(handle);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setEmail(email);
		user.setPasswordHash(password);
		user.setRole(role);
		return user;
	}

	private UserFactory() {
		// Prevent instantiation
	}
}
