package ca.mcgill.ecse321.coolearn.model;

import javax.persistence.Entity;
import java.sql.Time;
import java.sql.Date;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;

@Entity
public class Availability{
   private Time startTime;

public void setStartTime(Time value) {
    this.startTime = value;
}
public Time getStartTime() {
    return this.startTime;
}
private Time endTime;

public void setEndTime(Time value) {
    this.endTime = value;
}
public Time getEndTime() {
    return this.endTime;
}
private Date startDate;

public void setStartDate(Date value) {
    this.startDate = value;
}
public Date getStartDate() {
    return this.startDate;
}
private Date endDate;

public void setEndDate(Date value) {
    this.endDate = value;
}
public Date getEndDate() {
    return this.endDate;
}
private DayOfWeek dayOfWeek;

public void setDayOfWeek(DayOfWeek value) {
    this.dayOfWeek = value;
}
public DayOfWeek getDayOfWeek() {
    return this.dayOfWeek;
}
private Integer id;

public void setId(Integer value) {
    this.id = value;
}
@Id
@GeneratedValue()public Integer getId() {
    return this.id;
}
}
