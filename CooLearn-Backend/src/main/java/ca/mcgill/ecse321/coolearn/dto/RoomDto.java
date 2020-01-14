package ca.mcgill.ecse321.coolearn.dto;

import java.util.List;

import ca.mcgill.ecse321.coolearn.model.RoomSize;

public class RoomDto {
	private Integer roomNumber;
	private List<Integer> session;
	private RoomSize roomsize;

	public RoomDto() {
	}

	public RoomDto(Integer roomNumber, RoomSize roomsize) {
		this.roomNumber = roomNumber;
		this.roomsize = roomsize;
	}
	
	public RoomDto(Integer roomNumber, List<Integer> session, RoomSize roomsize) {
		this.roomNumber = roomNumber;
		this.session = session;
		this.roomsize = roomsize;
	}

	public void setRoomNumber(Integer value) {
		this.roomNumber = value;
	}

	public Integer getRoomNumber() {
		return this.roomNumber;
	}

	public List<Integer> getSessionIds() {
		return this.session;
	}

	public void setSessions(List<Integer> sessions) {
		this.session = sessions;
	}

	public void setRoomsize(RoomSize value) {
		this.roomsize = value;
	}
	public RoomSize getRoomsize() {
		return this.roomsize;
	}
}
