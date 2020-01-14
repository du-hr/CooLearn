package ca.mcgill.ecse321.coolearn.dto;


public class ReviewDto {
	private float rating;
	private String comment;
	private int sessionId;
	private Integer id;
	private int userRoleId;

	
	public ReviewDto() {
	}
	
	public ReviewDto(float rating, String comment, int session, Integer id, int userRole) {
		this.rating = rating;
		this.comment = comment;
		this.sessionId = session;
		this.id = id;
		this.userRoleId = userRole;
	}
	
	public void setRating(float value) {
		this.rating = value;
	}
	public float getRating() {
		return this.rating;
	}
	
	public void setComment(String value) {
		this.comment = value;
	}
	
	public String getComment() {
		return this.comment;
	}

	public int getSessionId() {
		return this.sessionId;
	}

	public void setSessionId(int session) {
		this.sessionId = session;
	}

	public void setId(Integer value) {
		this.id = value;
	}

	public Integer getId() {
		return this.id;
	}

	public int getUserRoleId() {
		return this.userRoleId;
	}

	public void setUserRoleDto(int userRole) {
		this.userRoleId = userRole;
	}

}
