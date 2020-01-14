package ca.mcgill.ecse321.coolearn.dao;

import ca.mcgill.ecse321.coolearn.model.CoolearnUser;
import ca.mcgill.ecse321.coolearn.model.Review;
import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.coolearn.model.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserRoleRepository extends CrudRepository<UserRole, Integer>{

    Optional<UserRole> findById(Integer id);
    List<UserRole> findByUser(CoolearnUser consumer);
    Optional<UserRole> findByReview(Review review);
    
    boolean existsById(Integer id);
    boolean existsByUser(CoolearnUser consumer);
    boolean existsByReview(Review review);
    
    long count();

}
