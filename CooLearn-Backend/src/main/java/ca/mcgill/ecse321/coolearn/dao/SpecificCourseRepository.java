package ca.mcgill.ecse321.coolearn.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.coolearn.model.Course;
import ca.mcgill.ecse321.coolearn.model.SpecificCourse;
import ca.mcgill.ecse321.coolearn.model.Tutor;

public interface SpecificCourseRepository extends CrudRepository<SpecificCourse, Integer>{

    List<SpecificCourse> findByHourlyRateLessThan(double hourlyRate);
    List<SpecificCourse> findByCourse(Course course);
	List<SpecificCourse> findByTutor(Tutor tutor);
	boolean existsByTutor(Tutor tutor);
}
