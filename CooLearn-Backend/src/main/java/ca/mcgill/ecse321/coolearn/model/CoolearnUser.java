package ca.mcgill.ecse321.coolearn.model;

import javax.persistence.Entity;
import java.util.Set;
import javax.persistence.OneToMany;
import javax.persistence.Id;

@Entity
public class CoolearnUser{
   private String firstName;

public void setFirstName(String value) {
    this.firstName = value;
}
public String getFirstName() {
    return this.firstName;
}
private String lastName;

public void setLastName(String value) {
    this.lastName = value;
}
public String getLastName() {
    return this.lastName;
}
private Set<UserRole> userRole;

@OneToMany(mappedBy="user" )
public Set<UserRole> getUserRole() {
   return this.userRole;
}

public void setUserRole(Set<UserRole> userRoles) {
   this.userRole = userRoles;
}

private String emailAddress;

public void setEmailAddress(String value) {
    this.emailAddress = value;
}
@Id
public String getEmailAddress() {
    return this.emailAddress;
}
private String password;

public void setPassword(String value) {
    this.password = value;
}
public String getPassword() {
    return this.password;
}
}
