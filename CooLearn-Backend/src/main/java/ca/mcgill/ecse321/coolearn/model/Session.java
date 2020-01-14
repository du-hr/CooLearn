package ca.mcgill.ecse321.coolearn.model;

import javax.persistence.Entity;
import java.sql.Time;
import java.sql.Date;
import java.util.Set;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;

@Entity
public class Session{
   private RequestStatus status;

public void setStatus(RequestStatus value) {
    this.status = value;
}
public RequestStatus getStatus() {
    return this.status;
}
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
private Date date;

public void setDate(Date value) {
    this.date = value;
}
public Date getDate() {
    return this.date;
}
private Set<Student> student;

@ManyToMany(mappedBy="session" )
public Set<Student> getStudent() {
   return this.student;
}

public void setStudent(Set<Student> students) {
   this.student = students;
}

private Tutor tutor;

@ManyToOne(optional=false)
public Tutor getTutor() {
   return this.tutor;
}

public void setTutor(Tutor tutor) {
   this.tutor = tutor;
}

private Course course;

@ManyToOne(optional=false)
public Course getCourse() {
   return this.course;
}

public void setCourse(Course course) {
   this.course = course;
}

private Room room;

@ManyToOne
public Room getRoom() {
   return this.room;
}

public void setRoom(Room room) {
   this.room = room;
}

private Set<Review> review;

@OneToMany(mappedBy="session" )
public Set<Review> getReview() {
   return this.review;
}

public void setReview(Set<Review> reviews) {
   this.review = reviews;
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
