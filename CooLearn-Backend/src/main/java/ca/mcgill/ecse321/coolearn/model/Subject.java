package ca.mcgill.ecse321.coolearn.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Set;
import javax.persistence.OneToMany;

@Entity
public class Subject{
   private String subjectName;

public void setSubjectName(String value) {
    this.subjectName = value;
}
@Id
public String getSubjectName() {
    return this.subjectName;
}
   private Set<Course> course;
   
   @OneToMany(mappedBy="subject" )
   public Set<Course> getCourse() {
      return this.course;
   }
   
   public void setCourse(Set<Course> courses) {
      this.course = courses;
   }
   
   }
