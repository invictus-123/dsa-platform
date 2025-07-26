package com.dsa.platform.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.dsa.platform.backend.model.User;
import com.dsa.platform.backend.model.shared.UserRole;
import com.dsa.platform.backend.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private CustomUserDetailsService customUserDetailsService;

	@Test
	void loadUserByUsername_whenUserExists_shouldReturnUserDetails() {
		String handle = "testuser";
		User mockUser = new User();
		mockUser.setHandle(handle);
		mockUser.setRole(UserRole.USER);
		when(userRepository.findByHandle(handle)).thenReturn(Optional.of(mockUser));

		UserDetails userDetails = customUserDetailsService.loadUserByUsername(handle);

		assertNotNull(userDetails);
		assertEquals(handle, userDetails.getUsername());
		assertTrue(userDetails.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("USER")));
	}

	@Test
	void loadUserByUsername_whenUserDoesNotExist_shouldThrowUsernameNotFoundException() {
		String handle = "nonexistentuser";
		when(userRepository.findByHandle(handle)).thenReturn(Optional.empty());

		UsernameNotFoundException exception = assertThrows(
				UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername(handle));

		assertEquals("User not found with handle: " + handle, exception.getMessage());
	}
}
