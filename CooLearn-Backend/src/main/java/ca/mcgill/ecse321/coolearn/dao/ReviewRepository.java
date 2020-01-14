package ca.mcgill.ecse321.coolearn.dao;

import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

import ca.mcgill.ecse321.coolearn.model.Review;
import ca.mcgill.ecse321.coolearn.model.Session;
import ca.mcgill.ecse321.coolearn.model.UserRole;

public interface ReviewRepository extends CrudRepository<Review, Integer>{
	List<Review> findByUserRole(UserRole userRole);
	List<Review> findBySession(Session session);
	Review findBySessionAndUserRole(Session session, UserRole userRole);
	boolean existsByUserRole(UserRole userRole);
	Optional<Review> findById(Integer id);
	boolean existsById(Integer id);
	
}
