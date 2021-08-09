package net.codejava.Domains;


import net.codejava.PasswordAnotation.ValidPassword;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Table
@Entity
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column (name = "user_id")
	private Long userId;

//	@NotNull(message = "stringValue has to be present")
	@NotEmpty
	@Column (name = "first_name")
	private String first_name;

//	@NotNull(message = "stringValue has to be present")
	@NotEmpty
	@Column (name = "lastName")
	private String lastName;

//	@NotNull(message = "stringValue has to be present")
	@Column(name = "username")
	private String username;

//	@NotNull(message = "stringValue has to be present")
	@Column (name = "password")
	private String password;

	@Column(name = "enabled")
	private boolean enabled = true;
	private String roles = "USER";

//	@NotNull(message = "stringValue has to be present")
	@NotEmpty
	private String user_address;

//	@NotNull(message = "stringValue has to be present")
	@NotEmpty
	private String user_number;
//
//	@NotNull
	private String MatchingPassword;


	
	public User() {}

	public String getMatchingPassword() {
		return MatchingPassword;
	}

	public void setMatchingPassword(String matchingPassword) {
		MatchingPassword = matchingPassword;
	}

	public String getUser_address() {
		return user_address;
	}

	public void setUser_address(String user_address) {
		this.user_address = user_address;
	}

	public String getUser_number() {
		return user_number;
	}

	public void setUser_number(String user_number) {
		this.user_number = user_number;
	}

	public Long getId() {
		return userId;
	}
	public void setId(Long id) {
		this.userId = id;
	}
	public String getFirst_name() {
		return first_name;
	}
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	public String getfirst_name() {
		return first_name;
	}
	public void setfirst_name(String first_name) {
		this.first_name = first_name;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
