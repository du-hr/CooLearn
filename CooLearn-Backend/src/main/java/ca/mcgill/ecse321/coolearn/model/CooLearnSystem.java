package ca.mcgill.ecse321.coolearn.model;

import javax.persistence.Entity;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;

@Entity
public class CooLearnSystem{
   private Set<CoolearnUser> user;
   
   @OneToMany(cascade={CascadeType.ALL})
   public Set<CoolearnUser> getUser() {
      return this.user;
   }
   
   public void setUser(Set<CoolearnUser> users) {
      this.user = users;
   }
   
   private Set<Session> session;
   
   @OneToMany(cascade={CascadeType.ALL})
   public Set<Session> getSession() {
      return this.session;
   }
   
   public void setSession(Set<Session> sessions) {
      this.session = sessions;
   }
   
   private Set<Room> room;
   
   @OneToMany(cascade={CascadeType.ALL})
   public Set<Room> getRoom() {
      return this.room;
   }
   
   public void setRoom(Set<Room> rooms) {
      this.room = rooms;
   }
   
   private Set<Course> course;
   
   @OneToMany(cascade={CascadeType.ALL})
   public Set<Course> getCourse() {
      return this.course;
   }
   
   public void setCourse(Set<Course> courses) {
      this.course = courses;
   }
   
   private Set<UserRole> userRole;
   
   @OneToMany(cascade={CascadeType.ALL})
   public Set<UserRole> getUserRole() {
      return this.userRole;
   }
   
   public void setUserRole(Set<UserRole> userRoles) {
      this.userRole = userRoles;
   }
   
   private Set<Subject> subject;
   
   @OneToMany(cascade={CascadeType.ALL})
   public Set<Subject> getSubject() {
      return this.subject;
   }
   
   public void setSubject(Set<Subject> subjects) {
      this.subject = subjects;
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
