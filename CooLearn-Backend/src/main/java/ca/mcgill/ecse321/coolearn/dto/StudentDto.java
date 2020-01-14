package ca.mcgill.ecse321.coolearn.dto;

import java.util.List;

public class StudentDto extends UserRoleDto{
	private List<Integer> session;

	public StudentDto() {
	}

	public StudentDto(List<Integer> session) {
		this.session = session;
	}

	public List<Integer> getSessionIds() {
		return this.session;
	}

	public void setSessionIds(List<Integer> sessions) {
		this.session = sessions;
	}


}
