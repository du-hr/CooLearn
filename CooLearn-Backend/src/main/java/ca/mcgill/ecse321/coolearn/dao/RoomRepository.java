package ca.mcgill.ecse321.coolearn.dao;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.coolearn.model.Room;
import ca.mcgill.ecse321.coolearn.model.RoomSize;
import java.util.*;


public interface RoomRepository extends CrudRepository<Room, Integer>{

    Optional<Room> findByRoomNumber(int roomNumber);

    List<Room> findByRoomsize(RoomSize roomsize);

}
