package ca.mcgill.ecse321.coolearn.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;

@Entity
public class Review{
   private float rating;

public void setRating(float value) {
    this.rating = value;
}
public float getRating() {
    return this.rating;
}
private String comment;

public void setComment(String value) {
    this.comment = value;
}
public String getComment() {
    return this.comment;
}
private Session session;

@ManyToOne(optional=false)
public Session getSession() {
   return this.session;
}

public void setSession(Session session) {
   this.session = session;
}

private Integer id;

public void setId(Integer value) {
    this.id = value;
}
@Id
@GeneratedValue()public Integer getId() {
    return this.id;
}
   private UserRole userRole;
   
   @ManyToOne(optional=false)
   public UserRole getUserRole() {
      return this.userRole;
   }
   
   public void setUserRole(UserRole userRole) {
      this.userRole = userRole;
   }
   
   }
