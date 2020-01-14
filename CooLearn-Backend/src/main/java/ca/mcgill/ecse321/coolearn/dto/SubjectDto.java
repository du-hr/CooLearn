package ca.mcgill.ecse321.coolearn.dto;

import java.util.List;

public class SubjectDto {
	private String subjectName;
	private List<String> course;
	
	public SubjectDto() {
	}

	public SubjectDto(String subjectName) {
		this.subjectName = subjectName;
	}

	public SubjectDto(String subjectName, List<String> course) {
		this.subjectName = subjectName;
		this.course = course;
	}
	
	public void setSubjectName(String value) {
		this.subjectName = value;
	}
	public String getSubjectName() {
		return this.subjectName;
	}

	public List<String> getCourseIds() {
		return this.course;
	}

	public void setCourseDto(List<String> courses) {
		this.course = courses;
	}


}
