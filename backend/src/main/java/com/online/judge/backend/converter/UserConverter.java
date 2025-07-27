package com.online.judge.backend.converter;

import com.online.judge.backend.dto.request.RegisterRequest;
import com.online.judge.backend.model.User;
import com.online.judge.backend.model.shared.UserRole;

public class UserConverter {
	public static User toUserFromRegisterRequestWithoutPassword(RegisterRequest request) {
		User user = new User();
		user.setHandle(request.handle());
		user.setEmail(request.email());
		user.setFirstName(request.firstName());
		user.setLastName(request.lastName());
		user.setRole(request.userRole() != null ? request.userRole() : UserRole.USER);
		return user;
	}

	private UserConverter() {
		// Prevent instantiation
	}
}
