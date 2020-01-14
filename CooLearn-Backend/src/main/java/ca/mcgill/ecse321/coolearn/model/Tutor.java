package ca.mcgill.ecse321.coolearn.model;
import javax.persistence.DiscriminatorValue;

import javax.persistence.Entity;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;

@Entity
@DiscriminatorValue("Tutor")
public class Tutor extends UserRole{
private Set<Availability> availability;
   
   @OneToMany(cascade={CascadeType.ALL})
   public Set<Availability> getAvailability() {
      return this.availability;
   }
   
   public void setAvailability(Set<Availability> availabilities) {
      this.availability = availabilities;
   }
   
   
   private Set<Session> session;
   
   @OneToMany(mappedBy="tutor" )
   public Set<Session> getSession() {
      return this.session;
   }
   
   public void setSession(Set<Session> sessions) {
      this.session = sessions;
   }
   
   private Set<SpecificCourse> teachingCourses;
   
   @OneToMany(mappedBy="tutor" )
   public Set<SpecificCourse> getTeachingCourses() {
      return this.teachingCourses;
   }
   
   public void setTeachingCourses(Set<SpecificCourse> courses) {
      this.teachingCourses = courses;
   }
   
   }
