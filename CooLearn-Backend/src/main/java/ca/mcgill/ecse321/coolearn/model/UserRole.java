package ca.mcgill.ecse321.coolearn.model;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.ManyToOne;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;

@Entity
@Inheritance
@DiscriminatorColumn(name="UserRoleType")
public abstract class UserRole{
   private CoolearnUser user;
   
   @ManyToOne(optional=false)
   public CoolearnUser getUser() {
      return this.user;
   }
   
   public void setUser(CoolearnUser user) {
      this.user = user;
   }
   
   private Set<Review> review;
   
   @OneToMany(mappedBy="userRole" , cascade={CascadeType.ALL})
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
