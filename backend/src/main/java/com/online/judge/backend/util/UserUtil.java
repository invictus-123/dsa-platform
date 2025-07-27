package com.online.judge.backend.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import com.online.judge.backend.exception.UserNotAuthorizedException;
import com.online.judge.backend.model.User;

@Component
public class UserUtil {
	UserUtil() {}

	/**
	 * Retrieves the currently authenticated user from the security context.
	 *
	 * @return The authenticated User object.
	 * @throws UserNotAuthorizedException if no authenticated user is found or if the principal is
	 *                    not an instance of User.
	 */
	public User getCurrentAuthenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new UserNotAuthorizedException("No authenticated user found in security context.");
		}

		Object principal = authentication.getPrincipal();
		if (principal instanceof User authenticatedUser) {
			return authenticatedUser;
		} else {
			throw new UserNotAuthorizedException("The authenticated principal is not an instance of the User class.");
		}
	}
}
