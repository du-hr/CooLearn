package ca.mcgill.ecse321.coolearn.dto;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

import ca.mcgill.ecse321.coolearn.model.RequestStatus;

public class SessionDto {
	private RequestStatus status;
	private Time startTime;
	private Time endTime;
	private Date date;
	private List<String> student;
	private List<Integer> studentId;
	private String tutor;
	private Integer tutorId;
	private String course;
	private Integer id;
	private int room;
	private List<Integer> review;


	public SessionDto() {
	}
	
	//TODO Decide whether to follow this format or call the other method will all parameters and set null or empty lists
	public SessionDto(RequestStatus status, Time startTime, Time endTime, Date date, List<String> student,
			String tutor, String course, Integer id) {
		this.status = status;
		this.startTime = startTime;
		this.endTime = endTime;
		this.date = date;
		this.student = student;
		this.tutor = tutor;
		this.course = course;
		this.id = id;
	}

	public SessionDto(RequestStatus status, Time startTime, Time endTime, Date date, List<String> student,
			List<Integer> studentId, String tutor,Integer tutorId, String course, Integer id, int room, List<Integer> review) {
		this.status = status;
		this.startTime = startTime;
		this.endTime = endTime;
		this.date = date;
		this.student = student;
		this.studentId=studentId;
		this.tutor = tutor;
		this.tutorId=tutorId;
		this.course = course;
		this.id = id;
		this.room = room;
		this.review = review;
	}
	public void setStatus(RequestStatus value) {
	    this.status = value;
	}
	public RequestStatus getStatus() {
	    return this.status;
	}

	public void setStartTime(Time value) {
	    this.startTime = value;
	}
	public Time getStartTime() {
	    return this.startTime;
	}

	public void setEndTime(Time value) {
	    this.endTime = value;
	}
	public Time getEndTime() {
	    return this.endTime;
	}

	public void setDate(Date value) {
	    this.date = value;
	}
	public Date getDate() {
	    return this.date;
	}

	public List<String> getStudentNames() {
	   return this.student;
	}

	public void setStudentNames(List<String> students) {
	   this.student = students;
	}

	public List<Integer> getStudentIds() {
		return this.studentId;
	}
 
	public void setStudentIds(List<Integer> studentIds) {
		this.studentId = studentIds;
	}

	public String getTutorName() {
	   return this.tutor;
	}

	public void setTutorName(String tutor) {
	   this.tutor = tutor;
	}

	public Integer getTutorId() {
		return this.tutorId;
	}
 
	public void setTutorName(Integer tutorId) {
		this.tutorId = tutorId;
	}

	public String getCourseName() {
	   return this.course;
	}

	public void setCourseName(String course) {
	   this.course = course;
	}

	public int getRoomId() {
	   return this.room;
	}

	public void setRoomId(int room) {
	   this.room = room;
	}

	public List<Integer> getReviewIds() {
	   return this.review;
	}

	public void setReviewIds(List<Integer> reviews) {
	   this.review = reviews;
	}

	public void setId(Integer value) {
	    this.id = value;
	}
	
	public Integer getId() {
	    return this.id;
	}
}
