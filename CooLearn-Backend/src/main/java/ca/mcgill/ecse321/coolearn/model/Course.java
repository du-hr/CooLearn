package ca.mcgill.ecse321.coolearn.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Set;
import javax.persistence.OneToMany;

@Entity
public class Course{
   private String name;

public void setName(String value) {
    this.name = value;
}
@Id
public String getName() {
    return this.name;
}
private EducationLevel educationalLevel;

public void setEducationalLevel(EducationLevel value) {
    this.educationalLevel = value;
}
public EducationLevel getEducationalLevel() {
    return this.educationalLevel;
}
   private Subject subject;
   
   @ManyToOne(optional=false)
   public Subject getSubject() {
      return this.subject;
   }
   
   public void setSubject(Subject subject) {
      this.subject = subject;
   }
   
   private Set<SpecificCourse> specificCourses;
   
   @OneToMany(mappedBy="course" )
   public Set<SpecificCourse> getSpecificCourses() {
      return this.specificCourses;
   }
   
   public void setSpecificCourses(Set<SpecificCourse> specificCoursess) {
      this.specificCourses = specificCoursess;
   }
   
   }
