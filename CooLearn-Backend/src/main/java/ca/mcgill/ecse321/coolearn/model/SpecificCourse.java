package ca.mcgill.ecse321.coolearn.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;

@Entity
public class SpecificCourse{
   private double hourlyRate;

public void setHourlyRate(double value) {
    this.hourlyRate = value;
}
public double getHourlyRate() {
    return this.hourlyRate;
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

private Integer id;

public void setId(Integer value) {
    this.id = value;
}
@Id
@GeneratedValue()public Integer getId() {
    return this.id;
}
}
