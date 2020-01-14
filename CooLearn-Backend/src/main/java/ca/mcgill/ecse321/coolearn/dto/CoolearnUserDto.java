package ca.mcgill.ecse321.coolearn.dto;

import java.util.List;


public class CoolearnUserDto {
	
	private String firstName;
	private String lastName;
	private String emailAddress;
	private List<Integer> userRoleIds;
	private String password;

	public CoolearnUserDto() {
	}
	
	public CoolearnUserDto(String firstName, String lastName, String emailAddress, List<Integer> userRole,
			String password) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailAddress = emailAddress;
		this.userRoleIds = userRole;
		this.password = password;
	}
	
	public CoolearnUserDto(String firstName, String lastName, String emailAddress, String password) {
		this(firstName, lastName, emailAddress, null, password); //TODO Decide whether to have it as null or empty UserRole list
	}

	public void setFirstName(String value) {
		this.firstName = value;
	}
	
	public String getFirstName() {
		return this.firstName;
	}

	public void setLastName(String value) {
		this.lastName = value;
	}
	
	public String getLastName() {
		return this.lastName;
	}

	public List<Integer> getUserRole() {
		return this.userRoleIds;
	}

	public void setUserRole(List<Integer> userRoles) {
		this.userRoleIds = userRoles;
	}

	public void setEmailAddress(String value) {
		this.emailAddress = value;
	}
	
	public String getEmailAddress() {
		return this.emailAddress;
	}

	public void setPassword(String value) {
		this.password = value;
	}
	
	public String getPassword() {
		return this.password;
	}
}
