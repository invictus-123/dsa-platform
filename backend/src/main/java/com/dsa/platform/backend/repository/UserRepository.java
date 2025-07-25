package com.dsa.platform.backend.repository;

import com.dsa.platform.backend.model.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Spring Data JPA repository for the {@link User} entity. */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

	/**
	 * Finds the user by their unique handle.
	 *
	 * @param handle
	 *            The handle to search for.
	 * @return An Optional containing the user if found, or an empty Optional
	 *         otherwise.
	 */
	Optional<User> findByHandle(String handle);

	/**
	 * Finds the user by their unique email.
	 *
	 * @param email
	 *            The email to search for.
	 * @return An Optional containing the user if found, or an empty Optional
	 *         otherwise.
	 */
	Optional<User> findByEmail(String email);
}
