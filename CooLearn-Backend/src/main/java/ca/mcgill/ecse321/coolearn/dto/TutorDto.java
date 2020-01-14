package ca.mcgill.ecse321.coolearn.dto;

import java.util.List;

public class TutorDto extends UserRoleDto{
	private List<Integer> availability;
	private List<Integer> session;
	private List<Integer> teachingCourses;

	
	public TutorDto() {
	}

	public TutorDto(List<Integer> teachingCourses) {
		this.teachingCourses = teachingCourses;
	}

	public TutorDto(List<Integer> availability, List<Integer> session,
			List<Integer> teachingCourses) {
		this.availability = availability;
		this.session = session;
		this.teachingCourses = teachingCourses;
	}

	public List<Integer> getAvailabilityIds() {
		return this.availability;
	}

	public void setAvailabilityIds(List<Integer> availabilitys) {
		this.availability = availabilitys;
	}

	public List<Integer> getSessionIds() {
		return this.session;
	}

	public void setSessionIds(List<Integer> sessions) {
		this.session = sessions;
	}

	public List<Integer> getTeachingCourseIds() {
		return this.teachingCourses;
	}

	public void setTeachingCourseIds(List<Integer> courses) {
		this.teachingCourses = courses;
	}

}
