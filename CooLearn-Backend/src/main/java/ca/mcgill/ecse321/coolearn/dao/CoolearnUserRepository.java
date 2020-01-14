package ca.mcgill.ecse321.coolearn.dao;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.coolearn.model.CoolearnUser;
import ca.mcgill.ecse321.coolearn.model.UserRole;

import java.util.List;
import java.util.Optional;

public interface CoolearnUserRepository extends CrudRepository<CoolearnUser, String> {

    List<CoolearnUser> findByFirstName(String s);
    List<CoolearnUser> findByLastName(String s);
    List<CoolearnUser> findByFirstNameAndLastName(String f, String l);
    
    Optional<CoolearnUser> findByUserRole(UserRole userRole);
    CoolearnUser findByEmailAddress(String s);
    
    boolean existsByEmailAddress(String email);
    boolean existsByUserRole(UserRole userRole);
    
    
    long count();

}
