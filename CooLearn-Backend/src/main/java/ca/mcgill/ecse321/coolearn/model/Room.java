package ca.mcgill.ecse321.coolearn.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Set;
import javax.persistence.OneToMany;

@Entity
public class Room{
   private Integer roomNumber;

public void setRoomNumber(Integer value) {
    this.roomNumber = value;
}
@Id
public Integer getRoomNumber() {
    return this.roomNumber;
}
private Set<Session> session;

@OneToMany(mappedBy="room" )
public Set<Session> getSession() {
   return this.session;
}

public void setSession(Set<Session> sessions) {
   this.session = sessions;
}

private RoomSize roomsize;

public void setRoomsize(RoomSize value) {
    this.roomsize = value;
}
public RoomSize getRoomsize() {
    return this.roomsize;
}
}
