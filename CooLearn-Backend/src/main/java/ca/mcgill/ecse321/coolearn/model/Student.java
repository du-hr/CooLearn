package ca.mcgill.ecse321.coolearn.model;
import javax.persistence.DiscriminatorValue;

import javax.persistence.Entity;
import java.util.Set;
import javax.persistence.ManyToMany;

@Entity
@DiscriminatorValue("Student")
public class Student extends UserRole{
   private Set<Session> session;
   
   @ManyToMany
   public Set<Session> getSession() {
      return this.session;
   }
   
   public void setSession(Set<Session> sessions) {
      this.session = sessions;
   }
   
   }
