package ca.mcgill.ecse321.coolearn.dto;

import java.sql.Date;
import java.sql.Time;

import ca.mcgill.ecse321.coolearn.model.DayOfWeek;

public class AvailabilityDto {
	private Time startTime;
	private Time endTime;
	private Date startDate;
	private Date endDate;
	private DayOfWeek dayOfWeek;
	private Integer id;


	public AvailabilityDto() {
	}

	public AvailabilityDto(Time startTime, Time endTime, Date startDate, Date endDate, DayOfWeek dayOfWeek,
			Integer id) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.startDate = startDate;
		this.endDate = endDate;
		this.dayOfWeek = dayOfWeek;
		this.id = id;
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

	public void setStartDate(Date value) {
		this.startDate = value;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setEndDate(Date value) {
		this.endDate = value;
	}
	public Date getEndDate() {
		return this.endDate;
	}

	public void setDayOfWeek(DayOfWeek value) {
		this.dayOfWeek = value;
	}

	public DayOfWeek getDayOfWeek() {
		return this.dayOfWeek;
	}

	public void setId(Integer value) {
		this.id = value;
	}

	public Integer getId() {
		return this.id;
	}
}
