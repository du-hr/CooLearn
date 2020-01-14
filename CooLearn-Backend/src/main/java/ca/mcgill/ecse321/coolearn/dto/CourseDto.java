package ca.mcgill.ecse321.coolearn.dto;

import java.util.List;

import ca.mcgill.ecse321.coolearn.model.EducationLevel; //TODO check with TA if enumeration classes can be imported from model


public class CourseDto {
	private String name;
	private EducationLevel educationalLevel;
	private String subjectId;
	private List<Integer> specificCourseIds;

	
	public CourseDto() {
	}

	public CourseDto(String name, EducationLevel educationalLevel, String subject) {
		this(name, educationalLevel, subject, null); //TODO null or empty list
	}
	
	public CourseDto(String name, EducationLevel educationalLevel, String subjectId, List<Integer> specificCourseIds) {
		this.name = name;
		this.educationalLevel = educationalLevel;
		this.subjectId = subjectId;
		this.specificCourseIds = specificCourseIds;
	}
	
	public void setName(String value) {
		this.name = value;
	}
	public String getName() {
		return this.name;
	}

	public void setEducationalLevel(EducationLevel value) {
		this.educationalLevel = value;
	}
	public EducationLevel getEducationalLevel() {
		return this.educationalLevel;
	}

	public String getSubjectName() {
		return this.subjectId;
	}

	public void setSubjectName(String subject) {
		this.subjectId = subject;
	}

	public List<Integer> getSpecificCourseIds() {
		return this.specificCourseIds;
	}

	public void setSpecificCourseIds(List<Integer> specificCoursess) {
		this.specificCourseIds = specificCoursess;
	}

}
