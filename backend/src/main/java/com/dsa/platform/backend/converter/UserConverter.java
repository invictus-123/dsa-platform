package com.dsa.platform.backend.converter;

import com.dsa.platform.backend.dto.request.RegisterRequest;
import com.dsa.platform.backend.model.User;
import com.dsa.platform.backend.model.shared.UserRole;

public class UserConverter {
	public static User toUserFromRegisterRequestWithoutPassword(RegisterRequest request) {
		User user = new User();
		user.setHandle(request.handle());
		user.setEmail(request.email());
		user.setFirstName(request.firstName());
		user.setLastName(request.lastName());
		user.setRole(UserRole.USER); // Default role for new users
		return user;
	}

	private UserConverter() {
		// Prevent instantiation
	}
}
