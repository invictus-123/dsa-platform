package com.dsa.platform.backend.model;

import com.dsa.platform.backend.model.shared.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Represents the 'users' table in the database. This class is a JPA entity that
 * maps to user data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
		name = "users",
		indexes = {
			@Index(name = "idx_users_handle", columnList = "handle"),
			@Index(name = "idx_users_email", columnList = "email")
		})
public class User implements UserDetails {

	/** The primary key for the users table. */
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	/** The user's first name. */
	@NotBlank
	@Size(max = 50)
	@Column(name = "first_name", nullable = false)
	private String firstName;

	/** The user's last name. */
	@NotBlank
	@Size(max = 50)
	@Column(name = "last_name", nullable = false)
	private String lastName;

	/** The user's unique public handle. */
	@NotBlank
	@Pattern(regexp = "^[a-zA-Z]\\w{2,20}$")
	@Column(name = "handle", nullable = false, unique = true)
	private String handle;

	/** The user's unique email address, used for login. */
	@NotBlank
	@Email
	@Column(name = "email", nullable = false, unique = true)
	private String email;

	/**
	 * The securely hashed password for the user. We never store plain-text
	 * passwords.
	 */
	@JsonIgnore // Exclude from JSON serialization to protect sensitive data
	@NotBlank
	@Column(name = "password_hash", nullable = false)
	private String passwordHash;

	/**
	 * The role of the user, which determines their permissions (e.g., 'USER' or
	 * 'ADMIN').
	 */
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	private UserRole role;

	/**
	 * The timestamp of when the user account was created. This field is
	 * automatically set when the user is created and is not updated.
	 */
	@PastOrPresent
	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(role.name()));
	}

	@Override
	public String getPassword() {
		return passwordHash;
	}

	@Override
	public String getUsername() {
		return handle;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
