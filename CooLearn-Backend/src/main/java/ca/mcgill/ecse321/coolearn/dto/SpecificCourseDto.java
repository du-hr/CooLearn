package ca.mcgill.ecse321.coolearn.dto;

public class SpecificCourseDto {
	private double hourlyRate;
	private int tutor;
	private String course;
	private Integer id;
	private String tutorName;


	public SpecificCourseDto() {
	}

	
	public SpecificCourseDto(double hourlyRate, int tutor, String course, Integer id, String tutorName) {
		this.hourlyRate = hourlyRate;
		this.tutor = tutor;
		this.course = course;
		this.id = id;
		this.tutorName = tutorName;
	}


	public void setHourlyRate(double value) {
		this.hourlyRate = value;
	}

	public double getHourlyRate() {
		return this.hourlyRate;
	}

	public int getTutor() {
		return this.tutor;
	}

	public void setTutor(int tutor) {
		this.tutor = tutor;
	}

	public String getCourse() {
		return this.course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public void setId(Integer value) {
		this.id = value;
	}
	
	public Integer getId() {
		return this.id;
	}
	
	public String getTutorName() {
		return tutorName;
	}

	public void setTutorName(String tutorName) {
		this.tutorName = tutorName;
	}
}
