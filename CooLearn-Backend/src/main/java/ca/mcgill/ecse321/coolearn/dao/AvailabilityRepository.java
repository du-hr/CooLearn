package ca.mcgill.ecse321.coolearn.dao;

import ca.mcgill.ecse321.coolearn.model.DayOfWeek;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.coolearn.model.Availability;


import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.Optional;

public interface AvailabilityRepository extends CrudRepository<Availability, Integer> {


    List<Availability> findByStartDateAfterAndEndDateBefore (Date startDate, Date endDate);
    boolean existsByStartDateAfterAndEndDateBefore(Date startDate, Date endDate);

    List<Availability> findByStartTimeAfterAndEndTimeBefore (Time startTime, Time endTime);
    boolean existsByStartTimeAfterAndEndTimeBefore(Time startTime, Time endTime);

    List<Availability> findByDayOfWeek (DayOfWeek dayOfWeek);
    boolean existsByDayOfWeek(DayOfWeek dayOfWeek);
    Optional<Availability> findById(Integer id);
    boolean existsById(Integer id);
       
}
