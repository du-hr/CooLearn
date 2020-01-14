package ca.mcgill.ecse321.coolearn.dao;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.coolearn.model.Subject;

public interface SubjectRepository extends CrudRepository<Subject, String>{

    Subject findBySubjectName (String value);
    boolean existsBySubjectName(String subjectName);

}
