package ca.mcgill.ecse321.coolearn.dao;

import org.springframework.data.repository.CrudRepository;
import ca.mcgill.ecse321.coolearn.model.RequestStatus;
import ca.mcgill.ecse321.coolearn.model.Room;
import ca.mcgill.ecse321.coolearn.model.Session;
import java.util.List;
import java.util.Optional;

import ca.mcgill.ecse321.coolearn.model.UserRole;
import java.sql.Date;
import java.sql.Time;

public interface SessionRepository extends CrudRepository<Session, Integer>{
	List<Session> findByTutor(UserRole userRole);
	boolean existsByTutor(UserRole userRole);
	List<Session> findByStudent(UserRole userRole);
	boolean existsByStudent(UserRole userRole);
	List<Session> findByTutorAndStatus(UserRole userRole, RequestStatus requestStatus);
	List<Session> findByStudentAndStatus(UserRole userRole, RequestStatus requestStatus);
	List<Session> findByDateAfter(Date date);
	List<Session> findByDateAfterAndStatus(Date date, RequestStatus status);
	List<Session> findByTutorAndDateBeforeAndEndTimeBeforeAndStatus(UserRole tutor, Date date,Time endTime ,RequestStatus status);
	List<Session> findByStudentAndDateAfterAndStatus(UserRole userRole, Date d, RequestStatus s);
	List<Session> findByTutorAndDateAfterAndStatus(UserRole userRole, Date d, RequestStatus s);
	List<Session> findByRoom(Room r);
	Optional<Session> findById(Integer id);
	boolean existsById(Integer id);
}
