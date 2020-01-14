package ca.mcgill.ecse321.coolearn.dto;

import java.util.List;

public class UserRoleDto {
	private String user;
	private List<Integer> review;
	private Integer id;
	private String name;
	
	public UserRoleDto() {
	}
	
	public UserRoleDto(String user, Integer id) {
		this.user = user;
		this.id = id;
	}

	public UserRoleDto(String user, Integer id, String name){
		this.user = user;
		this.id = id;
		this.name=name;
	}

	public UserRoleDto(String user, List<Integer> review, Integer id) {
		this.user = user;
		this.review = review;
		this.id = id;
	}


	public String getUser() {
		return this.user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public List<Integer> getReview() {
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

	public String getName(){
		return this.name;
	}

	public void setName(String name){
		this.name=name;
	}
}
