package ca.mcgill.ecse321.coolearn.dao;

import ca.mcgill.ecse321.coolearn.model.*;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends CrudRepository<Course, Integer>{

    Optional<Course> findByName(String name);
    List<Course> findByEducationalLevel(EducationLevel educationLevel);
    List<Course> findBySubject(Subject subject);

    List<Course> findBySubjectAndEducationalLevel(Subject subject, EducationLevel educationLevel);
    Optional<Course> findByNameAndSubjectAndEducationalLevel(String name, Subject subject, EducationLevel educationLevel);

    boolean existsByName(String name);
    boolean existsByNameAndSubjectAndEducationalLevel(String name, Subject subject, EducationLevel educationLevel);
    long count();



}
