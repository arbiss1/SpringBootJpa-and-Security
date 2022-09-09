package net.codejava.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.codejava.utils.ValidPassword;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;


@Entity
@Table(name = "USER")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column (name = "USER_ID")
	private Long userId;
	@NotEmpty
	@Column (name = "FIRST_NAME")
	private String first_name;
	@NotEmpty
	@Column (name = "LAST_NAME")
	private String lastName;
	@Column(name = "USERNAME")
	private String username;
	@ValidPassword
	@Column (name = "PASSWORD")
	private String password;
	@Column(name = "ENABLED")
	private boolean enabled = true;
	@Column(name = "ROLES")
	private String roles = "USER";
	@NotEmpty(message = "not null")
	@Column(name = "USER_ADDRESS")
	private String userAddress;
	@NotEmpty
	@Column(name = "USER_NUMBER")
	private String userNumber;
	@Column(name = "MATCHING_PASSWORD")
	private String matchingPassword;
	@Column(name = "FILE_NAME")
	private String fileName;
	@Lob
	@Column(name = "IMAGE")
	private byte[] image;


}
