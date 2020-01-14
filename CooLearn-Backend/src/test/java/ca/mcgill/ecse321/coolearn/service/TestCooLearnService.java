package ca.mcgill.ecse321.coolearn.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.lang.IllegalArgumentException;
import ca.mcgill.ecse321.coolearn.model.*;
import ca.mcgill.ecse321.coolearn.dao.*;
import static ca.mcgill.ecse321.coolearn.service.StringLiterals.STUDENT_DISCRIMINATOR;
import static ca.mcgill.ecse321.coolearn.service.StringLiterals.TUTOR_DISCRIMINATOR;
import static ca.mcgill.ecse321.coolearn.service.StringLiterals.MINIMUM_HOURLY_RATE;

/**
 * 
 * IMPORTANT INFORMATION
 * CREATION OF A DATE SHOULD BE DONE USING CALENDAR
 * 		Calendar start = Calendar.getInstance();
 * 		start.set(2019, Calendar.MAY, 27);
 * 		Date start_date = new Date(start.getTimeInMillis());
 *
 *
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestCooLearnService {
	@Autowired
	private CooLearnService service;
	@Autowired
	private CoolearnUserRepository coolearnUserRepository;
	@Autowired
	private UserRoleRepository userRoleRepository;
	@Autowired
	private CourseRepository courseRepository;
	@Autowired
	private SubjectRepository subjectRepository;
	@Autowired
	private AvailabilityRepository availabilityRepository;
	@Autowired
	private SpecificCourseRepository specificCourseRepository;
	@Autowired
	private ReviewRepository reviewRepository;
	@Autowired
	private SessionRepository sessionRepository;
	@Autowired 
	private RoomRepository roomRepository;

	@After
	public void clearDatabase() {
		reviewRepository.deleteAll();
		specificCourseRepository.deleteAll();
		service.deleteAllSessionsAndUserRolesWithReferentialIntegrity();
		availabilityRepository.deleteAll();
		coolearnUserRepository.deleteAll();
		courseRepository.deleteAll();	
		subjectRepository.deleteAll();
		roomRepository.deleteAll();
	}	

	@Test
	public void testCreateSubject() {
		assertEquals(0, service.getAllSubjects().size());

		String name = "Political Science";

		try {
			service.createSubject(name);
		} catch (IllegalArgumentException e) {
			fail();
		}

		List<Subject> allSubjects = service.getAllSubjects();
		assertEquals(1, allSubjects.size());
		assertEquals(name.toUpperCase(), allSubjects.get(0).getSubjectName());
	}

	@Test
	public void testCreateSubjectNull() {
		assertEquals(0, service.getAllSubjects().size());

		String name = null;
		String error = null;

		try {
			service.createSubject(name);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}

		assertEquals("Subject name cannot be empty!", error);
		assertEquals(0, service.getAllSubjects().size());

	}

	@Test
	public void testCreateSubjectEmpty() {
		assertEquals(0, service.getAllSubjects().size());

		String name = "";
		String error = null;

		try {
			service.createSubject(name);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}

		assertEquals("Subject name cannot be empty!", error);
		assertEquals(0, service.getAllSubjects().size());

	}


	@Test
	public void testGetSubjectByName() {
		assertEquals(0, service.getAllSubjects().size());
		String name = "Political Science";
		service.createSubject(name);

		try {
			service.getSubjectByName(name);
		} catch (IllegalArgumentException e) {
			fail();
		}
		List<Subject> allSubjects = service.getAllSubjects();
		assertEquals(1, allSubjects.size());
		assertEquals(allSubjects.get(0).getSubjectName(),service.getSubjectByName(name).getSubjectName());
	}

	@Test
	public void testGetSubjectByNameNull() {
		assertEquals(0,service.getAllSubjects().size());
		String name = null;

		try {
			service.getSubjectByName(name);
		} catch (IllegalArgumentException e) {
			assertEquals("Subject name cannot be empty!", e.getMessage());
		}
	}

	@Test
	public void testGetSubjectByNameEmptyString() {
		assertEquals(0,service.getAllSubjects().size());
		String name = "";

		try {
			service.getSubjectByName(name);
		} catch (IllegalArgumentException e) {
			assertEquals("Subject name cannot be empty!", e.getMessage());
		}

	}

	@Test
	public void testGetAllSubjects() {
		assertEquals(0, service.getAllSubjects().size());

		String name = "Political Science";
		String name_2 = "Mathematics";
		String name_3 = "Religious Studies";
		service.createSubject(name);
		service.createSubject(name_2);
		service.createSubject(name_3);
		try {
			service.getSubjectByName(name);
			service.getSubjectByName(name_2);
			service.getSubjectByName(name_3);
		} catch (IllegalArgumentException e) {
			fail();
		}

		List<Subject> allSubjects = service.getAllSubjects();
		assertEquals(3, allSubjects.size());

	}

	@Test
	public void testDeleteSubject() {
		assertEquals(0, service.getAllSubjects().size());

		String name = "Political Science";
		String name_2 = "Religious Studies";
		try {
			service.createSubject(name);
			service.createSubject(name_2);
		} catch (IllegalArgumentException e) {
			fail();
		}
		service.deleteSubject(name_2);
		List<Subject> allSubjects = service.getAllSubjects();
		assertEquals(1, allSubjects.size());
		assertEquals(name.toUpperCase(), allSubjects.get(0).getSubjectName());
	}

	@Test
	public void testDeleteSubjectNullSubject() {
		assertEquals(0, service.getAllSubjects().size());

		String error = "";
		try {
			service.deleteSubject(null);
		}
		catch(IllegalArgumentException e){
			error = e.getMessage();
		}
		assertEquals("Subject name cannot be empty!", error);
	}

	@Test
	public void testDeleteSubjectEmptyString() {
		assertEquals(0, service.getAllSubjects().size());

		String error = "";
		try {
			service.deleteSubject("");
		}
		catch(IllegalArgumentException e){
			error = e.getMessage();
		}
		assertEquals("Subject name cannot be empty!", error);
	}

	@Test
	public void testcreateCoolearnUser() {
		assertEquals(0, service.getAllCoolearnUsers().size());
		try {
			service.createCoolearnUser("student@gmail.com","Jacob", "Silcoff", "pass123");
		} catch (IllegalArgumentException e) {
			fail();
		}
		assertEquals(1, service.getAllCoolearnUsers().size());
		assertEquals("student@gmail.com", service.getAllCoolearnUsers().get(0).getEmailAddress());
		assertEquals("Jacob", service.getAllCoolearnUsers().get(0).getFirstName());
		assertEquals("Silcoff", service.getAllCoolearnUsers().get(0).getLastName());	
		assertEquals("pass123", service.getAllCoolearnUsers().get(0).getPassword());
	}

	@Test
	public void testcreateCoolearnUserNull() {
		testcreateCoolearnUserBadInput(null);
	}

	@Test
	public void testcreateCoolearnUserEmpty() {
		testcreateCoolearnUserBadInput("");
	}

	@Test
	public void testcreateCoolearnUserSpaces() {
		testcreateCoolearnUserBadInput("    ");
	}

	public void testcreateCoolearnUserBadInput(String badInput) {
		assertEquals(0, service.getAllCoolearnUsers().size());
		String err = null;
		try {
			service.createCoolearnUser(badInput,"Jacob", "Silcoff", "pass123");
		} catch (IllegalArgumentException e) {
			err = e.getMessage();
		}
		assertEquals(0, service.getAllCoolearnUsers().size());
		assertEquals("Invalid email address!", err);
		try {
			service.createCoolearnUser("student@gmail.com",badInput, "Silcoff", "pass123");
		} catch (IllegalArgumentException e) {
			err = e.getMessage();
		}
		assertEquals(0, service.getAllCoolearnUsers().size());
		assertEquals("Invalid first name!", err);
		try {
			service.createCoolearnUser("student@gmail.com","Jacob", badInput, "pass123");
		} catch (IllegalArgumentException e) {
			err = e.getMessage();
		}
		assertEquals(0, service.getAllCoolearnUsers().size());
		assertEquals("Invalid last name!", err);
		try {
			service.createCoolearnUser("student@gmail.com","Jacob", "Silcoff", badInput);
		} catch (IllegalArgumentException e) {
			err = e.getMessage();
		}
		assertEquals(0, service.getAllCoolearnUsers().size());
		assertEquals("Invalid password!", err);
	}

	@Test
	public void testcreateCoolearnUserDuplicate() {
		assertEquals(0, service.getAllCoolearnUsers().size());
		try {
			service.createCoolearnUser("student@gmail.com","Jacob", "Silcoff", "pass123");
		} catch (IllegalArgumentException e) {
			fail();
		}
		assertEquals(1, service.getAllCoolearnUsers().size());
		try {
			service.createCoolearnUser("differentEmail@gmail.com","Jacob", "Silcoff", "pass123");
		} catch (IllegalArgumentException e) {
			fail();
		}
		String error = null;
		assertEquals(2, service.getAllCoolearnUsers().size());
		try {
			service.createCoolearnUser("student@gmail.com", "Different", "Name", "andpass");
		} catch(IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals(2, service.getAllCoolearnUsers().size());
		assertEquals("Email address is already taken!", error);
	}

	@Test
	public void testCreateAvailability() {
		assertEquals(0, service.getAllAvailabilities().size());
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.MAY, 27);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019,Calendar.NOVEMBER,11);
		Date endDate = new Date (end.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		CoolearnUser user = service.createCoolearnUser("iamtutor@gmail.com", "Foufi", "Foufinho", "password1");
		Subject engineering = service.createSubject("Engineering");
		Subject maths = service.createSubject("Mathematics");
		Course a = service.createCourse("Introduction to Software Engineering", engineering, EducationLevel.UNIVERSITY);
		Course b = service.createCourse("Calculus I", maths, EducationLevel.COLLEGE);
		Course c = service.createCourse("Calculus II", maths, EducationLevel.UNIVERSITY);
		Course d = service.createCourse("Computer Organization", engineering, EducationLevel.UNIVERSITY);
		Set<Course> setOfCourses = new HashSet<Course>();
		setOfCourses.add(a);
		setOfCourses.add(b);
		setOfCourses.add(c);
		setOfCourses.add(d);
		Tutor tutor = (Tutor) service.createUserRole(user, StringLiterals.TUTOR_DISCRIMINATOR, setOfCourses);

		try{
			service.createAvailability(tutor, startTime,endTime,startDate,endDate,DayOfWeek.WEDNESDAY);
		} catch (IllegalArgumentException e) {
			fail();
		}
		assertEquals(1,service.getAllAvailabilities().size());
		assertEquals(1, service.getAllAvailabilitiesByTutorId(tutor.getId()).size());
		assertEquals(startDate.toString(),service.getAllAvailabilities().get(0).getStartDate().toString());
		assertEquals(endDate.toString(),service.getAllAvailabilities().get(0).getEndDate().toString());
		assertEquals(startTime.toString(),service.getAllAvailabilities().get(0).getStartTime().toString());
		assertEquals(endDate.toString(),service.getAllAvailabilities().get(0).getEndDate().toString());
		assertEquals(DayOfWeek.WEDNESDAY,service.getAllAvailabilities().get(0).getDayOfWeek());

	}

	@Test
	public void testCreateAvailabilityEndTimeBeforeStartTime() {
		assertEquals(0, service.getAllAvailabilities().size());
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.MAY, 27);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("11:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019,Calendar.NOVEMBER,11);
		Date endDate = new Date (end.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("09:00"));
		CoolearnUser user = service.createCoolearnUser("iamtutor@gmail.com", "Foufi", "Foufinho", "password1");
		Subject engineering = service.createSubject("Engineering");
		Subject maths = service.createSubject("Mathematics");
		Course a = service.createCourse("Introduction to Software Engineering", engineering, EducationLevel.UNIVERSITY);
		Course b = service.createCourse("Calculus I", maths, EducationLevel.COLLEGE);
		Course c = service.createCourse("Calculus II", maths, EducationLevel.UNIVERSITY);
		Course d = service.createCourse("Computer Organization", engineering, EducationLevel.UNIVERSITY);
		Set<Course> setOfCourses = new HashSet<Course>();
		setOfCourses.add(a);
		setOfCourses.add(b);
		setOfCourses.add(c);
		setOfCourses.add(d);
		Tutor tutor = (Tutor) service.createUserRole(user, StringLiterals.TUTOR_DISCRIMINATOR, setOfCourses);
		String error = null;

		try{
			service.createAvailability(tutor, startTime,endTime,startDate,endDate,DayOfWeek.WEDNESDAY);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals(0,service.getAllAvailabilities().size());
		assertEquals("Availability end time cannot be before event start time!", error);
	}

	@Test
	public void testCreateAvailabilityStartDateNull() {
		assertEquals(0, service.getAllAvailabilities().size());
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.MAY, 27);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019,Calendar.NOVEMBER,11);
		Date endDate = new Date (end.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		CoolearnUser user = service.createCoolearnUser("iamtutor@gmail.com", "Foufi", "Foufinho", "password1");
		Subject engineering = service.createSubject("Engineering");
		Subject maths = service.createSubject("Mathematics");
		Course a = service.createCourse("Introduction to Software Engineering", engineering, EducationLevel.UNIVERSITY);
		Course b = service.createCourse("Calculus I", maths, EducationLevel.COLLEGE);
		Course c = service.createCourse("Calculus II", maths, EducationLevel.UNIVERSITY);
		Course d = service.createCourse("Computer Organization", engineering, EducationLevel.UNIVERSITY);
		Set<Course> setOfCourses = new HashSet<Course>();
		setOfCourses.add(a);
		setOfCourses.add(b);
		setOfCourses.add(c);
		setOfCourses.add(d);
		Tutor tutor = (Tutor) service.createUserRole(user, StringLiterals.TUTOR_DISCRIMINATOR, setOfCourses);
		String error = null;

		try{
			service.createAvailability(tutor, startTime,endTime,null,endDate,DayOfWeek.WEDNESDAY);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals(0,service.getAllAvailabilities().size());
		assertEquals("Availability start date cannot be empty!", error);

	}
	@Test
	public void testCreateAvailabilityEndDateNull() {
		assertEquals(0, service.getAllAvailabilities().size());
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.MAY, 27);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019,Calendar.NOVEMBER,11);
		Date endDate = new Date (end.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		CoolearnUser user = service.createCoolearnUser("iamtutor@gmail.com", "Foufi", "Foufinho", "password1");
		Subject engineering = service.createSubject("Engineering");
		Subject maths = service.createSubject("Mathematics");
		Course a = service.createCourse("Introduction to Software Engineering", engineering, EducationLevel.UNIVERSITY);
		Course b = service.createCourse("Calculus I", maths, EducationLevel.COLLEGE);
		Course c = service.createCourse("Calculus II", maths, EducationLevel.UNIVERSITY);
		Course d = service.createCourse("Computer Organization", engineering, EducationLevel.UNIVERSITY);
		Set<Course> setOfCourses = new HashSet<Course>();
		setOfCourses.add(a);
		setOfCourses.add(b);
		setOfCourses.add(c);
		setOfCourses.add(d);
		Tutor tutor = (Tutor) service.createUserRole(user, StringLiterals.TUTOR_DISCRIMINATOR, setOfCourses);
		String error = null;

		try{
			service.createAvailability(tutor, startTime,endTime,startDate,null,DayOfWeek.WEDNESDAY);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals(0,service.getAllAvailabilities().size());
		assertEquals("Availability end date cannot be empty!", error);
	}
	@Test
	public void testCreateAvailabilityStartTimeNull() {
		assertEquals(0, service.getAllAvailabilities().size());
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.MAY, 27);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019,Calendar.NOVEMBER,11);
		Date endDate = new Date (end.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		CoolearnUser user = service.createCoolearnUser("iamtutor@gmail.com", "Foufi", "Foufinho", "password1");
		Subject engineering = service.createSubject("Engineering");
		Subject maths = service.createSubject("Mathematics");
		Course a = service.createCourse("Introduction to Software Engineering", engineering, EducationLevel.UNIVERSITY);
		Course b = service.createCourse("Calculus I", maths, EducationLevel.COLLEGE);
		Course c = service.createCourse("Calculus II", maths, EducationLevel.UNIVERSITY);
		Course d = service.createCourse("Computer Organization", engineering, EducationLevel.UNIVERSITY);
		Set<Course> setOfCourses = new HashSet<Course>();
		setOfCourses.add(a);
		setOfCourses.add(b);
		setOfCourses.add(c);
		setOfCourses.add(d);
		Tutor tutor = (Tutor) service.createUserRole(user, StringLiterals.TUTOR_DISCRIMINATOR, setOfCourses);

		String error = "";

		try{
			service.createAvailability(tutor, null,endTime,startDate,endDate,DayOfWeek.WEDNESDAY);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals(0,service.getAllAvailabilities().size());
		assertEquals("Availability start time cannot be empty!", error);
	}

	@Test
	public void testCreateAvailabilityEndTimeNull() {
		assertEquals(0, service.getAllAvailabilities().size());
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.MAY, 27);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019,Calendar.NOVEMBER,11);
		Date endDate = new Date (end.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		CoolearnUser user = service.createCoolearnUser("iamtutor@gmail.com", "Foufi", "Foufinho", "password1");
		Subject engineering = service.createSubject("Engineering");
		Subject maths = service.createSubject("Mathematics");
		Course a = service.createCourse("Introduction to Software Engineering", engineering, EducationLevel.UNIVERSITY);
		Course b = service.createCourse("Calculus I", maths, EducationLevel.COLLEGE);
		Course c = service.createCourse("Calculus II", maths, EducationLevel.UNIVERSITY);
		Course d = service.createCourse("Computer Organization", engineering, EducationLevel.UNIVERSITY);
		Set<Course> setOfCourses = new HashSet<Course>();
		setOfCourses.add(a);
		setOfCourses.add(b);
		setOfCourses.add(c);
		setOfCourses.add(d);
		Tutor tutor = (Tutor) service.createUserRole(user, StringLiterals.TUTOR_DISCRIMINATOR, setOfCourses);

		String error = "";

		try{
			service.createAvailability(tutor, startTime,null,startDate,endDate,DayOfWeek.WEDNESDAY);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals(0,service.getAllAvailabilities().size());
		assertEquals("Availability end time cannot be empty!", error);
	}

	@Test
	public void testCreateAvailabilityDayOfWeekNull() {
		assertEquals(0, service.getAllAvailabilities().size());
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.MAY, 27);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019,Calendar.NOVEMBER,11);
		Date endDate = new Date (end.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		CoolearnUser user = service.createCoolearnUser("iamtutor@gmail.com", "Foufi", "Foufinho", "password1");
		Subject engineering = service.createSubject("Engineering");
		Subject maths = service.createSubject("Mathematics");
		Course a = service.createCourse("Introduction to Software Engineering", engineering, EducationLevel.UNIVERSITY);
		Course b = service.createCourse("Calculus I", maths, EducationLevel.COLLEGE);
		Course c = service.createCourse("Calculus II", maths, EducationLevel.UNIVERSITY);
		Course d = service.createCourse("Computer Organization", engineering, EducationLevel.UNIVERSITY);
		Set<Course> setOfCourses = new HashSet<Course>();
		setOfCourses.add(a);
		setOfCourses.add(b);
		setOfCourses.add(c);
		setOfCourses.add(d);
		Tutor tutor = (Tutor) service.createUserRole(user, StringLiterals.TUTOR_DISCRIMINATOR, setOfCourses);

		String error = "";

		try{
			service.createAvailability(tutor, startTime,endTime,startDate,endDate,null);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals(0,service.getAllAvailabilities().size());
		assertEquals("Availability day of week cannot be empty!", error);
	}


	@Test
	public void testGetAvailabilitiesByTutor(){
		assertEquals(0, service.getAllAvailabilities().size());
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.MAY, 27);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019,Calendar.NOVEMBER,11);
		Date endDate = new Date (end.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		CoolearnUser user = service.createCoolearnUser("iamtutor@gmail.com", "Foufi", "Foufinho", "password1");
		Subject engineering = service.createSubject("Engineering");
		Subject maths = service.createSubject("Mathematics");
		Course a = service.createCourse("Introduction to Software Engineering", engineering, EducationLevel.UNIVERSITY);
		Course b = service.createCourse("Calculus I", maths, EducationLevel.COLLEGE);
		Course c = service.createCourse("Calculus II", maths, EducationLevel.UNIVERSITY);
		Course d = service.createCourse("Computer Organization", engineering, EducationLevel.UNIVERSITY);
		Set<Course> setOfCourses = new HashSet<Course>();
		setOfCourses.add(a);
		setOfCourses.add(b);
		setOfCourses.add(c);
		setOfCourses.add(d);
		Tutor tutor = (Tutor) service.createUserRole(user, StringLiterals.TUTOR_DISCRIMINATOR, setOfCourses);
		Availability av = service.createAvailability(tutor, startTime,endTime,startDate,endDate,DayOfWeek.WEDNESDAY);
		assertEquals(1, service.getAllAvailabilities().size());
		int id = tutor.getId();
		List<Availability> availabilitiesOfTutor = new ArrayList<Availability>();
		try {
			availabilitiesOfTutor = service.getAllAvailabilitiesByTutorId(id);
		} catch (Exception e) {
			//TODO: handle exception
			fail();
		}

		assertEquals(DayOfWeek.WEDNESDAY, availabilitiesOfTutor.get(0).getDayOfWeek());


	}

	@Test
	public void testCreateAvailabilityEndDateBeforeStartDate() {
		assertEquals(0, service.getAllAvailabilities().size());
		Calendar start = Calendar.getInstance();
		start.set(2020, Calendar.MAY, 27);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019,Calendar.NOVEMBER,11);
		Date endDate = new Date (end.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		CoolearnUser user = service.createCoolearnUser("iamtutor@gmail.com", "Foufi", "Foufinho", "password1");
		Subject engineering = service.createSubject("Engineering");
		Subject maths = service.createSubject("Mathematics");
		Course a = service.createCourse("Introduction to Software Engineering", engineering, EducationLevel.UNIVERSITY);
		Course b = service.createCourse("Calculus I", maths, EducationLevel.COLLEGE);
		Course c = service.createCourse("Calculus II", maths, EducationLevel.UNIVERSITY);
		Course d = service.createCourse("Computer Organization", engineering, EducationLevel.UNIVERSITY);
		Set<Course> setOfCourses = new HashSet<Course>();
		setOfCourses.add(a);
		setOfCourses.add(b);
		setOfCourses.add(c);
		setOfCourses.add(d);
		Tutor tutor = (Tutor) service.createUserRole(user, StringLiterals.TUTOR_DISCRIMINATOR, setOfCourses);
		String error = null;

		try{
			service.createAvailability(tutor, startTime,endTime,startDate,endDate,DayOfWeek.WEDNESDAY);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals(0,service.getAllAvailabilities().size());
		assertEquals("Availability end date cannot be before event start date!", error);
	}

	@Test
	public void testUpdateAvailability() {
		assertEquals(0, service.getAllAvailabilities().size());
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.MAY, 27);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019,Calendar.NOVEMBER,11);
		Date endDate = new Date (end.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		CoolearnUser user = service.createCoolearnUser("iamtutor@gmail.com", "Foufi", "Foufinho", "password1");
		Subject engineering = service.createSubject("Engineering");
		Subject maths = service.createSubject("Mathematics");
		Course a = service.createCourse("Introduction to Software Engineering", engineering, EducationLevel.UNIVERSITY);
		Course b = service.createCourse("Calculus I", maths, EducationLevel.COLLEGE);
		Course c = service.createCourse("Calculus II", maths, EducationLevel.UNIVERSITY);
		Course d = service.createCourse("Computer Organization", engineering, EducationLevel.UNIVERSITY);
		Set<Course> setOfCourses = new HashSet<Course>();
		setOfCourses.add(a);
		setOfCourses.add(b);
		setOfCourses.add(c);
		setOfCourses.add(d);
		Tutor tutor = (Tutor) service.createUserRole(user, StringLiterals.TUTOR_DISCRIMINATOR, setOfCourses);
		Availability av =service.createAvailability(tutor, startTime,endTime,startDate,endDate,DayOfWeek.WEDNESDAY);
		Calendar new_start = Calendar.getInstance();
		new_start.set(2019, Calendar.JUNE, 27);
		Date new_startDate = new Date(new_start.getTimeInMillis());
		Calendar new_end = Calendar.getInstance();
		new_end.set(2020, Calendar.JANUARY, 01);
		Date new_endDate = new Date(new_end.getTimeInMillis());
		Time new_startTime  = Time.valueOf(LocalTime.parse("08:00"));
		Time new_endTime  = Time.valueOf(LocalTime.parse("12:00"));
		int tutor_id = tutor.getId();
		int av_id = av.getId();
		try {
			service.updateAvailability(tutor_id, av_id, new_startTime, new_endTime, new_startDate, new_endDate, DayOfWeek.FRIDAY);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		assertEquals(1,service.getAllAvailabilities().size());
		assertEquals(new_startDate.toString(),service.getAllAvailabilities().get(0).getStartDate().toString());
		assertEquals(new_endDate.toString(),service.getAllAvailabilities().get(0).getEndDate().toString());
		assertEquals(new_startTime.toString(),service.getAllAvailabilities().get(0).getStartTime().toString());
		assertEquals(new_endTime.toString(),service.getAllAvailabilities().get(0).getEndTime().toString());
	}

	 @Test
	 public void testDeleteAvailability() {
	 	assertEquals(0, service.getAllAvailabilities().size());
	 	Calendar start = Calendar.getInstance();
	 	start.set(2019, Calendar.MAY, 27);
	 	Date startDate = new Date(start.getTimeInMillis());
	 	Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
	 	Calendar end = Calendar.getInstance();
	 	end.set(2019,Calendar.NOVEMBER,11);
	 	Date endDate = new Date (end.getTimeInMillis());
	 	Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
	 	CoolearnUser user = service.createCoolearnUser("iamtutor@gmail.com", "Foufi", "Foufinho", "password1");
	 	Subject engineering = service.createSubject("Engineering");
	 	Subject maths = service.createSubject("Mathematics");
	 	Course a = service.createCourse("Introduction to Software Engineering", engineering, EducationLevel.UNIVERSITY);
	 	Course b = service.createCourse("Calculus I", maths, EducationLevel.COLLEGE);
	 	Course c = service.createCourse("Calculus II", maths, EducationLevel.UNIVERSITY);
	 	Course d = service.createCourse("Computer Organization", engineering, EducationLevel.UNIVERSITY);
	 	Set<Course> setOfCourses = new HashSet<Course>();
	 	setOfCourses.add(a);
	 	setOfCourses.add(b);
	 	setOfCourses.add(c);
	 	setOfCourses.add(d);
	 	Tutor tutor = (Tutor) service.createUserRole(user, StringLiterals.TUTOR_DISCRIMINATOR, setOfCourses);
	 	Availability avail = service.createAvailability(tutor, startTime,endTime,startDate,endDate,DayOfWeek.WEDNESDAY);
	 	assertEquals(1,service.getAllAvailabilities().size());
	 	try {
	 		service.deleteAvailability(tutor, avail);
	 	} catch (Exception e) {
	 		fail(e.getMessage());
	 	}
	 	assertEquals(0,service.getAllAvailabilities().size());
	 }

	@Test
	public void testCreateUserRoleStudentSuccess() {
		assertEquals(0, service.getAllCoolearnUsers().size());
		assertEquals(0, service.getAllUserRoles().size());
		//Need to create a user
		CoolearnUser user = service.createCoolearnUser("iamstudent@gmail.com", "Allouks", "Nippon", "fhskrwua");
		try {
			service.createUserRole(user, STUDENT_DISCRIMINATOR, null);
		} catch(IllegalArgumentException e) {
			fail();
		}

		List<UserRole> userRole = service.getAllUserRoles();
		assertEquals(1, userRole.size());
	}

	//	//ID STILL REFERENCE
	@Test
	public void testCreateUserRoleTutorSuccess() {
		assertEquals(0, service.getAllCoolearnUsers().size());
		assertEquals(0, service.getAllUserRoles().size());
		//Need to create a user
		CoolearnUser user = service.createCoolearnUser("iamtutor@gmail.com", "Foufi", "Foufinho", "password1");
		Subject engineering = service.createSubject("Engineering");
		Subject maths = service.createSubject("Mathematics");
		Course a = service.createCourse("Introduction to Software Engineering", engineering, EducationLevel.UNIVERSITY);
		Course b = service.createCourse("Calculus I", maths, EducationLevel.COLLEGE);
		Course c = service.createCourse("Calculus II", maths, EducationLevel.UNIVERSITY);
		Course d = service.createCourse("Computer Organization", engineering, EducationLevel.UNIVERSITY);
		Set<Course> setOfCourses = new HashSet<Course>();
		setOfCourses.add(a);
		setOfCourses.add(b);
		setOfCourses.add(c);
		setOfCourses.add(d);
		Tutor tutor = null;
		try {
			tutor = (Tutor) service.createUserRole(user, TUTOR_DISCRIMINATOR, setOfCourses);
		} catch(IllegalArgumentException e) {
			fail();
		}

		List<UserRole> userRole = service.getAllUserRoles();
		assertEquals(1, userRole.size());
		assertEquals(4, service.getAllSpecificCourses().size());
		assertEquals(4, service.getSpecificCoursesByTutor(tutor).size());
		//		assertEquals("Calculus II", service.getSpecificCoursesByTutor(tutor).get(3).getCourse().getName());
	}

	//	@Test
	//	public void testCreateUserRole() {
	//		assertEquals(0, service.getAllCoolearnUsers().size());
	//		assertEquals(0, service.getAllUserRoles().size());
	//		CoolearnUser CoolearnUser = null;
	//		try {
	//			CoolearnUser = service.createCoolearnUser("test@test.com","test","test","test");
	//		} catch(IllegalArgumentException e) {
	//			fail();
	//		}
	//		assertEquals(1, service.getAllCoolearnUsers().size());
	//		String error = null;
	//		try {
	//			service.createUserRole(null, "STUDENT");
	//		} catch (IllegalArgumentException e) {
	//			error = e.getMessage();
	//		}
	//		assertEquals("CoolearnUser cannot be null", error);
	//		assertEquals(0, service.getAllUserRoles().size());
	//		try {
	//			service.createUserRole(CoolearnUser, "STUDENTf");
	//		} catch (IllegalArgumentException e) {
	//			error = e.getMessage();
	//		}
	//		assertEquals("A user role should either be a tutor or a student!", error);
	//		assertEquals(0, service.getAllUserRoles().size());
	//		try {
	//			service.createUserRole(CoolearnUser, "STUDENT");
	//		} catch (IllegalArgumentException e) {
	//			fail();
	//		}
	//		assertEquals(1, service.getAllUserRoles().size());
	//		try {
	//			service.createUserRole(CoolearnUser, "TUTOR");
	//		} catch (IllegalArgumentException e) {
	//			fail();
	//		}
	//		assertEquals(2, service.getAllUserRoles().size());
	//	}

	@Test
	public void createUserRoleNullString() {
		assertEquals(0, service.getAllCoolearnUsers().size());
		assertEquals(0, service.getAllUserRoles().size());

		CoolearnUser user = service.createCoolearnUser("iamtutor@gmail.com", "Foufi", "Foufinho", "password1");
		Subject engineering = service.createSubject("Engineering");
		Subject maths = service.createSubject("Mathematics");
		Course a = service.createCourse("Introduction to Software Engineering", engineering, EducationLevel.UNIVERSITY);
		Set<Course> setOfCourses = new HashSet<Course>();
		setOfCourses.add(a);

		String error = "";
		try {
			service.createUserRole(user, null, setOfCourses);
		} catch(IllegalArgumentException e) {
			error = e.getMessage();
		}

		assertEquals("User role needs to be specified", error);
		assertEquals(0, service.getAllUserRoles().size());
	}

	@Test
	public void createUserRoleIncorrectString() {
		assertEquals(0, service.getAllCoolearnUsers().size());
		assertEquals(0, service.getAllUserRoles().size());

		CoolearnUser user = service.createCoolearnUser("iamtutor@gmail.com", "Foufi", "Foufinho", "password1");
		Subject engineering = service.createSubject("Engineering");
		Subject maths = service.createSubject("Mathematics");
		Course a = service.createCourse("Introduction to Software Engineering", engineering, EducationLevel.UNIVERSITY);
		Set<Course> setOfCourses = new HashSet<Course>();
		setOfCourses.add(a);

		String error = "";
		try {
			service.createUserRole(user, "Tutorito", setOfCourses);
		} catch(IllegalArgumentException e) {
			error = e.getMessage();
		}

		assertEquals("User role should be either tutor or student", error);
		assertEquals(0, service.getAllUserRoles().size());
	}

	@Test
	public void createUserRoleUserNull() {
		assertEquals(0, service.getAllCoolearnUsers().size());
		assertEquals(0, service.getAllUserRoles().size());

		CoolearnUser user = null;
		Subject engineering = service.createSubject("Engineering");
		Subject maths = service.createSubject("Mathematics");
		Course a = service.createCourse("Introduction to Software Engineering", engineering, EducationLevel.UNIVERSITY);
		Set<Course> setOfCourses = new HashSet<Course>();
		setOfCourses.add(a);

		String error = "";
		try {
			service.createUserRole(user, TUTOR_DISCRIMINATOR, setOfCourses);
		} catch(IllegalArgumentException e) {
			error = e.getMessage();
		}

		assertEquals("CoolearnUser cannot be null", error);
		assertEquals(0, service.getAllUserRoles().size());
	}

	@Test
	public void createUserRoleCoursesNull() {
		assertEquals(0, service.getAllCoolearnUsers().size());
		assertEquals(0, service.getAllUserRoles().size());

		CoolearnUser user = service.createCoolearnUser("iamtutor@gmail.com", "Foufi", "Foufinho", "password1");
		Subject engineering = service.createSubject("Engineering");
		Subject maths = service.createSubject("Mathematics");
		Course a = service.createCourse("Introduction to Software Engineering", engineering, EducationLevel.UNIVERSITY);
		Set<Course> setOfCourses = null;

		String error = "";
		try {
			service.createUserRole(user, TUTOR_DISCRIMINATOR, setOfCourses);
		} catch(IllegalArgumentException e) {
			error = e.getMessage();
		}

		assertEquals("A tutor should be created with a list of courses", error);
		assertEquals(0, service.getAllUserRoles().size());
	}

	@Test
	public void createUserRoleCoursesEmpty() {
		assertEquals(0, service.getAllCoolearnUsers().size());
		assertEquals(0, service.getAllUserRoles().size());

		CoolearnUser user = service.createCoolearnUser("iamtutor@gmail.com", "Foufi", "Foufinho", "password1");
		Subject engineering = service.createSubject("Engineering");
		Subject maths = service.createSubject("Mathematics");
		Course a = service.createCourse("Introduction to Software Engineering", engineering, EducationLevel.UNIVERSITY);
		Set<Course> setOfCourses =  new HashSet<Course>();

		String error = "";
		try {
			service.createUserRole(user, TUTOR_DISCRIMINATOR, setOfCourses);
		} catch(IllegalArgumentException e) {
			error = e.getMessage();
		}

		assertEquals("A tutor should be created with a list of courses", error);
		assertEquals(0, service.getAllUserRoles().size());
	}

	@Test
	public void createUserRoleSpaces() {

	}

	@Test
	public void createUserRoleDuplicate() {

	}

	@Test
	public void testCreateSession() {	
		assertEquals(0, service.getAllSessions().size());
		CoolearnUser studentCoolearnUser = service.createCoolearnUser("student@gmail.com","Jacob", "Silcoff", "pass123");
		CoolearnUser tutorCoolearnUser = service.createCoolearnUser("tutor@gmail.com","Marwan","Kanaan","otherpass");
		Student student = (Student) service.createUserRole(studentCoolearnUser, STUDENT_DISCRIMINATOR, null);
		Subject subj = service.createSubject("Computer Science");
		Course course = service.createCourse("Applied Machine Learning", subj, EducationLevel.UNIVERSITY);
		Set<Course> setOfCourses = new HashSet<Course>();
		setOfCourses.add(course);
		
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.OCTOBER, 24);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019, Calendar.OCTOBER, 24);
		Date endDate = new Date(start.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		
		Tutor tutor = (Tutor) service.createUserRole(tutorCoolearnUser, TUTOR_DISCRIMINATOR, setOfCourses);
		Availability a = service.createAvailability(tutor,startTime, endTime,
				startDate, endDate, DayOfWeek.THURSDAY);
		List<Student> students = new ArrayList<Student>();
		students.add((Student)student);
		
		Calendar session_occ = Calendar.getInstance();
		session_occ.set(2019, Calendar.OCTOBER, 24);
		Date date = new Date(session_occ.getTimeInMillis());
		Time tStart  = Time.valueOf(LocalTime.parse("09:30"));
		Time tEnd = Time.valueOf(LocalTime.parse("10:30"));
		
		Session s = null;
		try {
			s = service.createSession(students,(Tutor) tutor, course, tStart, tEnd, date);
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		}

		assertEquals(1,service.getAllSessions().size());
		List<Course> courses = service.getCourseByEducationalLevel(EducationLevel.UNIVERSITY);
		assertEquals(1, courses.size());

		try {
			Course c = service.getCourseByName("Applied Machine Learning");
			assertTrue(c != null);
			courses = service.getCourseBySubject(subj);
			assertEquals(courses.get(0).getName().toUpperCase(), "Applied Machine Learning".toUpperCase());
			s = service.getAllSessions().get(0);
			assertEquals(s.getStatus(), RequestStatus.PENDING);
			service.setSessionStatus(s, RequestStatus.ACCEPTED);
			assertEquals(s.getStatus(), RequestStatus.ACCEPTED);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testCreateSessionStudentNull() {	
		String error = "Hello";
		assertEquals(0, service.getAllSessions().size());
		CoolearnUser studentCoolearnUser = service.createCoolearnUser("student@gmail.com","Jacob", "Silcoff", "pass123");
		CoolearnUser tutorCoolearnUser = service.createCoolearnUser("tutor@gmail.com","Marwan","Kanaan","otherpass");
		Subject subj = service.createSubject("Computer Science");
		Course course = service.createCourse("Applied Machine Learning", subj, EducationLevel.UNIVERSITY);
		Set<Course> setOfCourses = new HashSet<Course>();
		setOfCourses.add(course);
		Tutor tutor = (Tutor) service.createUserRole(tutorCoolearnUser, TUTOR_DISCRIMINATOR, setOfCourses);
		
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.OCTOBER, 24);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019, Calendar.OCTOBER, 24);
		Date endDate = new Date(start.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		
		Availability a = service.createAvailability(tutor, startTime, endTime,
				startDate, endDate, DayOfWeek.THURSDAY);
		
		Calendar session_occ = Calendar.getInstance();
		session_occ.set(2019, Calendar.OCTOBER, 24);
		Date date = new Date(session_occ.getTimeInMillis());
		Time tStart  = Time.valueOf(LocalTime.parse("09:30"));
		Time tEnd = Time.valueOf(LocalTime.parse("10:30"));

		Session s = null;
		try {
			s = service.createSession(null,(Tutor) tutor, course, tStart, tEnd, date);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}

		assertEquals("A session must be associated with at least one student.", error);
	}

	@Test
	public void testCreateSessionTutorNull() {	
		String error = "";
		assertEquals(0, service.getAllSessions().size());
		CoolearnUser studentCoolearnUser = service.createCoolearnUser("student@gmail.com","Jacob", "Silcoff", "pass123");
		CoolearnUser tutorCoolearnUser = service.createCoolearnUser("tutor@gmail.com","Marwan","Kanaan","otherpass");
		Student student = (Student) service.createUserRole(studentCoolearnUser, STUDENT_DISCRIMINATOR, null);
		Subject subj = service.createSubject("Computer Science");
		Course course = service.createCourse("Applied Machine Learning", subj, EducationLevel.UNIVERSITY);
		Set<Course> setOfCourses = new HashSet<Course>();
		setOfCourses.add(course);
		List<Student> students = new ArrayList<Student>();
		students.add((Student)student);
		Calendar session_occ = Calendar.getInstance();
		session_occ.set(2019, Calendar.OCTOBER, 24);
		Date date = new Date(session_occ.getTimeInMillis());
		Time tStart  = Time.valueOf(LocalTime.parse("09:30"));
		Time tEnd = Time.valueOf(LocalTime.parse("10:30"));
		Session s = null;
		try {
			s = service.createSession(students, null, course, tStart, tEnd, date);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}

		assertEquals("Tutor cannot be null.", error);
	}

	@Test
	public void testCreateSessionCourseNull() {	
		String error = "";
		assertEquals(0, service.getAllSessions().size());
		CoolearnUser studentCoolearnUser = service.createCoolearnUser("student@gmail.com","Jacob", "Silcoff", "pass123");
		CoolearnUser tutorCoolearnUser = service.createCoolearnUser("tutor@gmail.com","Marwan","Kanaan","otherpass");
		Student student = (Student) service.createUserRole(studentCoolearnUser, STUDENT_DISCRIMINATOR, null);
		Subject subj = service.createSubject("Computer Science");
		Course course = service.createCourse("Applied Machine Learning", subj, EducationLevel.UNIVERSITY);
		Set<Course> setOfCourses = new HashSet<Course>();
		setOfCourses.add(course);
		Tutor tutor = (Tutor) service.createUserRole(tutorCoolearnUser, TUTOR_DISCRIMINATOR, setOfCourses);
		
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.OCTOBER, 24);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019, Calendar.OCTOBER, 24);
		Date endDate = new Date(start.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		
		Availability a = service.createAvailability(tutor, startTime, endTime,
				startDate, endDate, DayOfWeek.THURSDAY);
		List<Student> students = new ArrayList<Student>();
		students.add((Student)student);
		Calendar session_occ = Calendar.getInstance();
		session_occ.set(2019, Calendar.OCTOBER, 24);
		Date date = new Date(session_occ.getTimeInMillis());
		Time tStart  = Time.valueOf(LocalTime.parse("09:30"));
		Time tEnd = Time.valueOf(LocalTime.parse("10:30"));
		Session s = null;
		try {
			s = service.createSession(students, tutor, null, tStart, tEnd, date);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}

		assertEquals("Course cannot be null.", error);
	}

	//	@Test
	//	public void testCreateSessionNoAvailability() {	
	//		assertEquals(0, service.getAllSessions().size());
	//		CoolearnUser studentCoolearnUser = service.createCoolearnUser("student@gmail.com","Jacob", "Silcoff", "pass123");
	//		CoolearnUser tutorCoolearnUser = service.createCoolearnUser("tutor@gmail.com","Marwan","Kanaan","otherpass");
	//		Student student = (Student) service.createUserRole(studentCoolearnUser, STUDENT_DISCRIMINATOR, null);
	//		
	//		Subject subj = service.createSubject("Programming");
	//		Course course = service.createCourse("ECSE 321", subj, EducationLevel.UNIVERSITY);
	//		Set<Course> setOfCourses = new HashSet<Course>();
	//		setOfCourses.add(course);
	//		Tutor tutor = (Tutor) service.createUserRole(tutorCoolearnUser, TUTOR_DISCRIMINATOR, setOfCourses);
	//		Availability a = service.createAvailability(new Time(10,0,0), new Time(11,0,0),
	//				new Date(2018,10,10), new Date(2020,10,10), DayOfWeek.MONDAY);
	//		service.assignAvailability(a, tutor);
	//
	//		List<Student> students = new ArrayList<Student>();
	//		students.add((Student)student);
	//		Time tStart = new Time(9,30,0);
	//		Time tEnd = new Time(10,30,0);
	//		Date date = new Date(2019,10,25);
	//		String error = null;
	//		try {
	//			service.createSession(students,(Tutor) tutor,course,tStart, tEnd,date);
	//		} catch(IllegalArgumentException e) {
	//			error = e.getMessage();
	//		}
	//		assertEquals("The tutor has no availability for the desired time", error);
	//	}

	@Test
	public void testAcceptSession(){
		assertEquals(0, service.getAllSessions().size());
		assertEquals(0, service.getAllRooms().size()); 
		CoolearnUser studentCoolearnUser = service.createCoolearnUser("student@gmail.com","Jacob", "Silcoff", "pass123");
		CoolearnUser tutorCoolearnUser = service.createCoolearnUser("tutor@gmail.com","Marwan","Kanaan","otherpass");
		Student student = (Student) service.createUserRole(studentCoolearnUser, STUDENT_DISCRIMINATOR, null);
		Subject subj = service.createSubject("Computer Science");
		Course course = service.createCourse("Applied Machine Learning", subj, EducationLevel.UNIVERSITY);
		Set<Course> setOfCourses = new HashSet<Course>();
		setOfCourses.add(course);
		
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.OCTOBER, 24);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019, Calendar.OCTOBER, 24);
		Date endDate = new Date(start.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		
		Tutor tutor = (Tutor) service.createUserRole(tutorCoolearnUser, TUTOR_DISCRIMINATOR, setOfCourses);
		Availability a = service.createAvailability(tutor,startTime, endTime,
				startDate, endDate, DayOfWeek.THURSDAY);
		List<Student> students = new ArrayList<Student>();
		students.add((Student)student);
		
		Calendar session_occ = Calendar.getInstance();
		session_occ.set(2019, Calendar.OCTOBER, 24);
		Date date = new Date(session_occ.getTimeInMillis());
		Time tStart  = Time.valueOf(LocalTime.parse("09:30"));
		Time tEnd = Time.valueOf(LocalTime.parse("10:30"));
		Session session = service.createSession(students, tutor, course, tStart, tEnd, date);
//		Room roomu = service.createRoom(1, RoomSize.SMALL);
//		assertEquals(1, service.getAllRooms().size());
		boolean yossha = false;
		try {
			yossha=service.AcceptSession(session);
		} catch (Exception e) {
			//TODO: handle exception
			fail(e.getMessage());
		}
		assertEquals(true, yossha);
		assertEquals(RequestStatus.ACCEPTED, session.getStatus());
		assertEquals(1,  (int) service.getAllSessions().get(0).getRoom().getRoomNumber());
	}

//	@Test
//	public void testAcceptSessionRoomNotAvailable(){
//		assertEquals(0, service.getAllSessions().size());
//		assertEquals(0, service.getAllRooms().size());
//		CoolearnUser studentCoolearnUser = service.createCoolearnUser("student@gmail.com","Jacob", "Silcoff", "pass123");
//		CoolearnUser tutorCoolearnUser = service.createCoolearnUser("tutor@gmail.com","Marwan","Kanaan","otherpass");
//		Student student = (Student) service.createUserRole(studentCoolearnUser, STUDENT_DISCRIMINATOR, null);
//		Subject subj = service.createSubject("Computer Science");
//		Course course = service.createCourse("Applied Machine Learning", subj, EducationLevel.UNIVERSITY);
//		Set<Course> setOfCourses = new HashSet<Course>();
//		setOfCourses.add(course);
//		
//		Calendar start = Calendar.getInstance();
//		start.set(2019, Calendar.OCTOBER, 24);
//		Date startDate = new Date(start.getTimeInMillis());
//		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
//		Calendar end = Calendar.getInstance();
//		end.set(2019, Calendar.OCTOBER, 24);
//		Date endDate = new Date(start.getTimeInMillis());
//		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
//		
//		Tutor tutor = (Tutor) service.createUserRole(tutorCoolearnUser, TUTOR_DISCRIMINATOR, setOfCourses);
//		Availability a = service.createAvailability(tutor,startTime, endTime,
//				startDate, endDate, DayOfWeek.THURSDAY);
//		List<Student> students = new ArrayList<Student>();
//		students.add((Student)student);
//		
//		Calendar session_occ = Calendar.getInstance();
//		session_occ.set(2019, Calendar.OCTOBER, 24);
//		Date date = new Date(session_occ.getTimeInMillis());
//		Time tStart  = Time.valueOf(LocalTime.parse("09:30"));
//		Time tEnd = Time.valueOf(LocalTime.parse("10:30"));
//		Session session = service.createSession(students, tutor, course, tStart, tEnd, date);
//		Room roomu = service.createRoom(1, RoomSize.SMALL);
//		assertEquals(1, service.getAllRooms().size()); 
//		service.AcceptSession(session); 
//		Session sessha = service.createSession(students, tutor, course, tStart, tEnd, date);
//		boolean yossha;
//		String error="";
//		try {
//			yossha = service.AcceptSession(sessha);
//		} catch (Exception e) {
//			//TODO: handle exception
//			error = e.getMessage();
//		}
//		assertEquals("There are no room available of that size", error);
//		assertEquals(RequestStatus.PENDING, sessha.getStatus());
//		assertEquals(1, service.getAllSessionsByRoom(roomu).size());
//
//	}

	@Test
	public void testDenySession(){
		assertEquals(0, service.getAllSessions().size());
		assertEquals(0, service.getAllRooms().size());
		CoolearnUser studentCoolearnUser = service.createCoolearnUser("student@gmail.com","Jacob", "Silcoff", "pass123");
		CoolearnUser tutorCoolearnUser = service.createCoolearnUser("tutor@gmail.com","Marwan","Kanaan","otherpass");
		Student student = (Student) service.createUserRole(studentCoolearnUser, STUDENT_DISCRIMINATOR, null);
		Subject subj = service.createSubject("Computer Science");
		Course course = service.createCourse("Applied Machine Learning", subj, EducationLevel.UNIVERSITY);
		Set<Course> setOfCourses = new HashSet<Course>();
		setOfCourses.add(course);
		
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.OCTOBER, 24);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019, Calendar.OCTOBER, 24);
		Date endDate = new Date(start.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		
		Tutor tutor = (Tutor) service.createUserRole(tutorCoolearnUser, TUTOR_DISCRIMINATOR, setOfCourses);
		Availability a = service.createAvailability(tutor,startTime, endTime,
				startDate, endDate, DayOfWeek.THURSDAY);
		List<Student> students = new ArrayList<Student>();
		students.add((Student)student);
		
		Calendar session_occ = Calendar.getInstance();
		session_occ.set(2019, Calendar.OCTOBER, 24);
		Date date = new Date(session_occ.getTimeInMillis());
		Time tStart  = Time.valueOf(LocalTime.parse("09:30"));
		Time tEnd = Time.valueOf(LocalTime.parse("10:30"));
		Session session = service.createSession(students, tutor, course, tStart, tEnd, date);
		boolean yossha = false;
		try {
			yossha=service.DenySession(session);
		} catch (Exception e) {
			//TODO: handle exception
			fail(e.getMessage());
		}
		assertEquals(true, yossha);
		assertEquals(RequestStatus.REJECTED, session.getStatus());
	}

	@Test
	public void createCourse() {
		assertEquals(0, service.getAllCourses().size());
		assertEquals(0, service.getAllSubjects().size());
		String name = "Introduction to Software Engineering";
		Subject subject = service.createSubject("Physics");
		EducationLevel educationLevel = EducationLevel.SECONDARY_SCHOOL;

		try {
			service.createCourse(name, subject, educationLevel);
		} catch (IllegalArgumentException e) {
			// Check that no error occurred
			fail();
		}

		List<Course> allCourses = service.getAllCourses();

		assertEquals(1, allCourses.size());
		assertEquals(name.toUpperCase(), allCourses.get(0).getName());
		assertEquals(subject.getSubjectName(), allCourses.get(0).getSubject().getSubjectName());
		assertEquals(educationLevel, allCourses.get(0).getEducationalLevel());
	}

	@Test
	public void createCourseNull() {
		assertEquals(0, service.getAllCourses().size());
		String name = "Intro To Software Engineering";
		Subject subject = service.createSubject("Physics");
		EducationLevel educationLevel = EducationLevel.SECONDARY_SCHOOL;
		String error = null;
		try {
			service.createCourse(null, subject, educationLevel);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals(0, service.getAllCourses().size());
		assertEquals("Invalid course name!",error);
		try {
			service.createCourse(name, null, educationLevel);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals(0,service.getAllCourses().size());
		assertEquals("Null subject!",error);
		try {
			service.createCourse(name, subject, null);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals(0,service.getAllCourses().size());
		assertEquals("You need to pass an education level!",error);
	}

	@Test
	public void createCourseEmpty() {
		assertEquals(0, service.getAllCourses().size());
		String name = "";
		Subject subject = service.createSubject("Physics");
		EducationLevel educationLevel = EducationLevel.SECONDARY_SCHOOL;
		String error = null;
		try {
			service.createCourse(name, subject, educationLevel);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals(0, service.getAllCourses().size());
		assertEquals("Invalid course name!",error);
	}

	@Test
	public void createCourseDuplicate() {

	}

	@Test
	public void createCourseSpaces() {

	}

	@Test
	public void deleteAvailability() {
	}

	@Test
	public void testCreateRoom(){
		assertEquals(0, service.getAllRooms().size());

		try {
			service.createRoom(1, RoomSize.SMALL);
		} catch (IllegalArgumentException e) {
			fail();
		}

		String error = "";
		try {
			service.createRoom(1, null);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals("A room size must be specified", error);

		assertEquals(1, service.getAllRooms().size());
	}

	@Test
	public void testCreateRoomFail(){
		assertEquals(0, service.getAllRooms().size());
		String error= null;
		try {
			service.createRoom(0, RoomSize.SMALL);
		} catch (Exception e) {
			error = e.getMessage();
		}
		assertEquals("Invalid room number", error);
		assertEquals(0, service.getAllRooms().size());
	}
	@Test
	public void testGetRoom(){
		assertEquals(0, service.getAllRooms().size());
		service.createRoom(1, RoomSize.SMALL);
		assertEquals(1, service.getAllRooms().size());
		Room room = new Room();
		try {
			room = service.getRoom(1);
		} catch (IllegalArgumentException e) {
			fail();
		}
	}
	@Test
	public void testGetRoomInvalidNumber(){
		assertEquals(0, service.getAllRooms().size());
		Room room = new Room();
		String error = null;
		try {
			room = service.getRoom(0);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals("Invalid room number", error);
	}

	@Test
	public void testGetRoomNoRoom(){
		assertEquals(0, service.getAllRooms().size());
		Room room = new Room();
		String error = null;
		try {
			room = service.getRoom(2);
		} catch (NoSuchElementException e) {
			error = e.getMessage();
		}
		assertEquals("No room with this number", error);
	}
	@Test
	public void testGetRoomBySize(){
		assertEquals(0, service.getAllRooms().size());
		service.createRoom(1, RoomSize.SMALL);
		assertEquals(1, service.getAllRooms().size());
		List<Room> rooms = new ArrayList<Room>();
		try {
			rooms=service.getRoomBySize(RoomSize.SMALL);
		} catch (IllegalArgumentException e) {
			fail();
		}

		assertEquals(1, rooms.size());

		//Test for null RoomSize
		String error = "";
		try {
			rooms=service.getRoomBySize(null);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals("A room size must be specified", error);
	}

	@Test
	public void testGetRoomBySizeNoRoom(){
		assertEquals(0, service.getAllRooms().size());
		List<Room> rooms = new ArrayList<Room>();
		String error = null;
		try {
			rooms = service.getRoomBySize(RoomSize.LARGE);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals("No room of such size", error);
	}

	@Test
	public void testCreateReviewStudentSuccess() {	
		assertEquals(0, service.getAllSessions().size());
		assertEquals(0, service.getAllReviews().size());

		CoolearnUser studentCoolearnUser = service.createCoolearnUser("student@gmail.com","Jacob", "Silcoff", "pass123");
		CoolearnUser tutorCoolearnUser = service.createCoolearnUser("tutor@gmail.com","Marwan","Kanaan","otherpass");
		Student student = (Student) service.createUserRole(studentCoolearnUser, STUDENT_DISCRIMINATOR, null);

		Subject subj = service.createSubject("Computer Science");
		Course course = service.createCourse("Applied Machine Learning", subj, EducationLevel.UNIVERSITY);
		Set<Course> setCourse = new HashSet<Course>();
		setCourse.add(course);
		Tutor tutor = (Tutor) service.createUserRole(tutorCoolearnUser, TUTOR_DISCRIMINATOR, setCourse);

		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.OCTOBER, 24);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019, Calendar.OCTOBER, 24);
		Date endDate = new Date(start.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		
		Availability a = service.createAvailability(tutor, startTime, endTime,
				startDate, endDate, DayOfWeek.THURSDAY);
		List<Student> students = new ArrayList<Student>();
		students.add((Student)student);
	
		Calendar session_occ = Calendar.getInstance();
		session_occ.set(2019, Calendar.OCTOBER, 24);
		Date date = new Date(session_occ.getTimeInMillis());
		Time tStart  = Time.valueOf(LocalTime.parse("09:30"));
		Time tEnd = Time.valueOf(LocalTime.parse("10:30"));
		
		Session session = service.createSession(students,(Tutor) tutor,course,tStart, tEnd,date);
		//		assertEquals(session.getId(), service.getAllSessionsByStudent(students.get(0)).get(0).getId());
		Review r = null;
		try {
			r = service.createReview(student, session, "What a great student!", 4.5f);
		} catch (Exception e) {
			fail(e.getMessage());
		}	
		assertEquals(1, service.getAllReviews().size());
		assertEquals(1, service.getAllReviewsByUserRole(student).size());
	}

	@Test
	public void testUpdateReview(){
		assertEquals(0, service.getAllSessions().size());
		assertEquals(0, service.getAllReviews().size());

		CoolearnUser studentCoolearnUser = service.createCoolearnUser("student@gmail.com","Jacob", "Silcoff", "pass123");
		CoolearnUser tutorCoolearnUser = service.createCoolearnUser("tutor@gmail.com","Marwan","Kanaan","otherpass");
		Student student = (Student) service.createUserRole(studentCoolearnUser, STUDENT_DISCRIMINATOR, null);

		Subject subj = service.createSubject("Computer Science");
		Course course = service.createCourse("Applied Machine Learning", subj, EducationLevel.UNIVERSITY);
		Set<Course> setCourse = new HashSet<Course>();
		setCourse.add(course);
		Tutor tutor = (Tutor) service.createUserRole(tutorCoolearnUser, TUTOR_DISCRIMINATOR, setCourse);

		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.OCTOBER, 24);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019, Calendar.OCTOBER, 24);
		Date endDate = new Date(start.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		Calendar session_occ = Calendar.getInstance();
		session_occ.set(2019, Calendar.OCTOBER, 24);
		Date date = new Date(session_occ.getTimeInMillis());
		Time tStart  = Time.valueOf(LocalTime.parse("09:30"));
		Time tEnd = Time.valueOf(LocalTime.parse("10:30"));
		
		Availability a = service.createAvailability(tutor, startTime, endTime,
				startDate, endDate, DayOfWeek.THURSDAY);
		List<Student> students = new ArrayList<Student>();
		students.add((Student)student);

		Session session = service.createSession(students,(Tutor) tutor,course,tStart, tEnd,date);
		//		assertEquals(session.getId(), service.getAllSessionsByStudent(students.get(0)).get(0).getId());
		Review r = service.createReview(student, session, "What a great student!", 4.5f);
		try {
			service.updateReview(r.getId(), "This student sucks!", 1.5f);
		} catch (Exception e) {
			fail(e.getMessage());
		}

		assertEquals("This student sucks!", service.getAllReviews().get(0).getComment());
		assertEquals(1.5, service.getAllReviews().get(0).getRating(),0);

	}

	@Test
	public void testCreateReviewNullRecipient() {
		//TODO

		//		String error = "";
		//		try {
		//			service.createReview(null, session, "Great session", rating);
		//		}catch(IllegalArgumentException e) {
		//			error = e.getMessage();
		//		}
		//		assertEquals("Recipient of comment cannot be null.", error);

	}

	@Test
	public void testCreateReviewNullSession() {
		//TODO 

		//		String error = "";
		//		try {
		//			service.createReview(student, null, "Great session", rating);
		//		}catch(IllegalArgumentException e) {
		//			error = e.getMessage();
		//		}
		//		assertEquals("Recipient of comment cannot be null.", error);

	}
	@Test
	public void testCreateReviewNullComment() {
		//TODO

		//		String error = "";
		//		try {
		//			service.createReview(student, session, null, rating);
		//		}catch(IllegalArgumentException e) {
		//			error = e.getMessage();
		//		}
		//		assertEquals("Comment cannot be null.", error);

	}
	@Test
	public void testCreateReviewInvalidRating() {
		//TODO

		//		String error = "";
		//		try {
		//			service.createReview(student, session, "Great session", rating);
		//		}catch(IllegalArgumentException e) {
		//			error = e.getMessage();
		//		}
		//		assertEquals("Rating must be between 0 and 5.", error);

	}

	@Test
	public void testAddNewCourseForTutor() {

	}

	//ID STILL REFERENCE
	@Test
	public void testUpdatePriceSuccess() {	
		assertEquals(0,service.getAllSpecificCourses().size());
		CoolearnUser tutorCoolearnUser = service.createCoolearnUser("tutor@gmail.com","Marwan","Kanaan","otherpass");
		Subject subj = service.createSubject("Programming");
		Course course = service.createCourse("ECSE 321", subj, EducationLevel.UNIVERSITY);
		Set<Course> setCourses = new HashSet<Course>();
		setCourses.add(course);
		Tutor tutor = (Tutor) service.createUserRole(tutorCoolearnUser, TUTOR_DISCRIMINATOR, setCourses);

		List<SpecificCourse> listSpecificCourses = service.getSpecificCoursesByTutor(tutor);

		try {
			service.updatePriceOfSpecificCourse(50, listSpecificCourses.get(0));
			int id = listSpecificCourses.get(0).getId();
			SpecificCourse sc = service.getSpecificCourse(id);
			assertEquals((int) sc.getHourlyRate(), 50);
		} catch (Exception e) {
			fail();
		}

		assertEquals(1, service.getAllSpecificCourses().size());
		assertEquals(50, (int) service.getSpecificCoursesByTutor(tutor).get(0).getHourlyRate());
	}

	@Test
	public void testAddSessionToStudentSuccess() {
		assertEquals(0,service.getAllSessions().size());

		CoolearnUser tutorCoolearnUser = service.createCoolearnUser("tutor@gmail.com","Marwan","Kanaan","otherpass");
		Subject subj = service.createSubject("Programming");
		Course course = service.createCourse("ECSE 321", subj, EducationLevel.UNIVERSITY);
		Set<Course> setCourses = new HashSet<Course>();
		setCourses.add(course);
		Tutor tutor = (Tutor) service.createUserRole(tutorCoolearnUser, TUTOR_DISCRIMINATOR, setCourses);

		CoolearnUser studentCoolearnUser = service.createCoolearnUser("coco@136.com","Anderson","Rolland","12345asdfg");
		Student student = (Student) service.createUserRole(studentCoolearnUser, STUDENT_DISCRIMINATOR,null);
		assertEquals(0,service.getAllSessionsByStudent(student).size());

		Calendar s = Calendar.getInstance();
		s.set(2019, Calendar.MAY, 27);
		Date sessionDate = new Date(s.getTimeInMillis());
		Time sessionStartTime  = Time.valueOf(LocalTime.parse("09:00"));
		Time sessionEndTime  = Time.valueOf(LocalTime.parse("11:00"));

		Calendar a = Calendar.getInstance();
		a.set(2019,Calendar.MAY,1);
		Date availabilityStartDate = new Date(a.getTimeInMillis());
		Calendar availabilityEnd = Calendar.getInstance();
		availabilityEnd.set(2019,Calendar.DECEMBER,31);
		Date availabilityEndDate = new Date(availabilityEnd.getTimeInMillis());
		Availability availability = service.createAvailability(tutor,sessionStartTime,sessionEndTime,availabilityStartDate,availabilityEndDate,DayOfWeek.MONDAY);
		List<Availability> availabilities = new ArrayList<Availability>();
		availabilities.add(availability);

		List<Student> studentList = new ArrayList<Student>();
		studentList.add(student);
		Session session = service.createSession(studentList,tutor,course,sessionStartTime,sessionEndTime,sessionDate);
		assertEquals(1,service.getAllSessionsByStudent(student).size());

		CoolearnUser coolearn_student2 = service.createCoolearnUser("gebran@bassil.com", "gebzo", "bisso", "helahelaho");
		Student gebzo = (Student) service.createUserRole(coolearn_student2, StringLiterals.STUDENT_DISCRIMINATOR, null);
		assertEquals(0, service.getAllSessionsByStudent(gebzo).size());
		try {
			service.addSessionToStudent(gebzo, session);
		} catch (Exception e) {
			fail(e.getMessage());
		}

		assertEquals(1,service.getAllSessionsByStudent(gebzo).size());


	}

	@Test
	public void testUpdatePriceOfSpecificCourse() {
		CoolearnUser tutorCoolearnUser = service.createCoolearnUser("tutor@gmail.com","Marwan","Kanaan","otherpass");
		Subject subj = service.createSubject("Programming");
		Course course = service.createCourse("ECSE 321", subj, EducationLevel.UNIVERSITY);
		Set<Course> setCourses = new HashSet<Course>();
		setCourses.add(course);
		Tutor tutor = (Tutor) service.createUserRole(tutorCoolearnUser, TUTOR_DISCRIMINATOR, setCourses);
		double hourlyRate = 15;

		SpecificCourse specificCourse = service.createSpecificCourse(hourlyRate, course, tutor);

		try {
			service.updatePriceOfSpecificCourse(30, specificCourse);
		}catch(IllegalArgumentException e) {
			fail();
		}
	}

	@Test
	public void testUpdatePriceOfSpecificCourseBelowMinimumRate() {
		CoolearnUser tutorCoolearnUser = service.createCoolearnUser("tutor@gmail.com","Marwan","Kanaan","otherpass");
		Subject subj = service.createSubject("Programming");
		Course course = service.createCourse("ECSE 321", subj, EducationLevel.UNIVERSITY);
		Set<Course> setCourses = new HashSet<Course>();
		setCourses.add(course);
		Tutor tutor = (Tutor) service.createUserRole(tutorCoolearnUser, TUTOR_DISCRIMINATOR, setCourses);
		double hourlyRate = 15;

		SpecificCourse specificCourse = service.createSpecificCourse(hourlyRate, course, tutor);

		String error = "";
		try {
			service.updatePriceOfSpecificCourse(10, specificCourse);
		}catch(IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals("Hourly rate must be greater than " + StringLiterals.MINIMUM_HOURLY_RATE, error);
	}

	@Test
	public void testDeleteReviewSuccess(){
		assertEquals(0,service.getAllSessions().size());
		CoolearnUser tutorCoolearnUser = service.createCoolearnUser("tutor@gmail.com","Marwan","Kanaan","otherpass");
		Subject subj = service.createSubject("Programming");
		Course course = service.createCourse("ECSE 321", subj, EducationLevel.UNIVERSITY);
		Set<Course> setCourses = new HashSet<Course>();
		setCourses.add(course);
		Tutor tutor = (Tutor) service.createUserRole(tutorCoolearnUser, TUTOR_DISCRIMINATOR, setCourses);
		CoolearnUser studentCoolearnUser = service.createCoolearnUser("coco@136.com","Anderson","Rolland","12345asdfg");
		Student student = (Student) service.createUserRole(studentCoolearnUser, STUDENT_DISCRIMINATOR,null);
		assertEquals(0,service.getAllSessionsByStudent(student).size());
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.MAY, 27);
		Date start_date = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019,Calendar.JULY,27);
		Date end_date = new Date(end.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		Availability availability = service.createAvailability(tutor,startTime,endTime,start_date,end_date,DayOfWeek.MONDAY);
		List<Availability> availabilities = new ArrayList<Availability>();
		availabilities.add(availability);
		List<Student> studentList = new ArrayList<Student>();
		studentList.add(student);
		Calendar s = Calendar.getInstance();
		s.set(2019,Calendar.JUNE,10);
		Date session_date = new Date(s.getTimeInMillis());
		Session session = service.createSession(studentList,tutor,course,startTime,endTime,session_date);
		assertEquals(1,service.getAllSessions().size());
		Review review = service.createReview(tutor,session,"Good!",(float)4.5);
		assertEquals(1,service.getAllReviews().size());
		try {
			service.deleteReview(review);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		assertEquals(0,service.getAllReviews().size());
	}

	@Test
	public void testUpdatePriceOfSpecificCourseNullCourse() {
		CoolearnUser tutorCoolearnUser = service.createCoolearnUser("tutor@gmail.com","Marwan","Kanaan","otherpass");
		Subject subj = service.createSubject("Programming");
		Course course = service.createCourse("ECSE 321", subj, EducationLevel.UNIVERSITY);
		Set<Course> setCourses = new HashSet<Course>();
		setCourses.add(course);
		Tutor tutor = (Tutor) service.createUserRole(tutorCoolearnUser, TUTOR_DISCRIMINATOR, setCourses);
		double hourlyRate = 15;

		SpecificCourse specificCourse = service.createSpecificCourse(hourlyRate, course, tutor);

		String error = "";
		try {
			service.updatePriceOfSpecificCourse(20, null);
		}catch(IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals("There must be a course to be updated", error);
	}

	@Test
	public void testDeleteSessionSuccess(){
		assertEquals(0,service.getAllSessions().size());
		CoolearnUser tutorCoolearnUser = service.createCoolearnUser("tutor@gmail.com","Marwan","Kanaan","otherpass");
		Subject subj = service.createSubject("Programming");
		Course course = service.createCourse("ECSE 321", subj, EducationLevel.UNIVERSITY);
		Set<Course> setCourses = new HashSet<Course>();
		setCourses.add(course);
		Tutor tutor = (Tutor) service.createUserRole(tutorCoolearnUser, TUTOR_DISCRIMINATOR, setCourses);
		CoolearnUser studentCoolearnUser = service.createCoolearnUser("coco@136.com","Anderson","Rolland","12345asdfg");
		Student student = (Student) service.createUserRole(studentCoolearnUser, STUDENT_DISCRIMINATOR,null);
		assertEquals(0,service.getAllSessionsByStudent(student).size());
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.MAY, 27);
		Date date = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019,Calendar.MAY,27);;
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		Availability availability = service.createAvailability(tutor,startTime,endTime,date,date,DayOfWeek.MONDAY);
		List<Availability> availabilities = new ArrayList<Availability>();
		availabilities.add(availability);
		List<Student> studentList = new ArrayList<Student>();
		studentList.add(student);
		Session session = service.createSession(studentList,tutor,course,startTime,endTime,date);
		assertEquals(1,service.getAllSessions().size());
		try {
			service.deleteSession(session);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		assertEquals(0,service.getAllSessions().size());
	}

	@Test
	public void testCreateSpecificCourse() {
		assertEquals(0,service.getAllSpecificCourses().size());
		CoolearnUser tutorCoolearnUser = service.createCoolearnUser("tutor@gmail.com","Marwan","Kanaan","otherpass");
		Subject subj = service.createSubject("Programming");
		Course course = service.createCourse("ECSE 321", subj, EducationLevel.UNIVERSITY);
		Set<Course> setCourses = new HashSet<Course>();
		setCourses.add(course);
		Tutor tutor = (Tutor) service.createUserRole(tutorCoolearnUser, TUTOR_DISCRIMINATOR, setCourses);
		double hourlyRate = 15;

		try {
			service.createSpecificCourse(hourlyRate, course, tutor);
		}
		catch(IllegalArgumentException e) {
			fail();
		}

	}

//	@Test
//	public void testDeleteSessionByIdSuccess(){
//		assertEquals(0,service.getAllSessions().size());
//		CoolearnUser tutorCoolearnUser = service.createCoolearnUser("tutor@gmail.com","Marwan","Kanaan","otherpass");
//		Subject subj = service.createSubject("Programming");
//		Course course = service.createCourse("ECSE 321", subj, EducationLevel.UNIVERSITY);
//		Set<Course> setCourses = new HashSet<Course>();
//		setCourses.add(course);
//		Tutor tutor = (Tutor) service.createUserRole(tutorCoolearnUser, TUTOR_DISCRIMINATOR, setCourses);
//		CoolearnUser studentCoolearnUser = service.createCoolearnUser("coco@136.com","Anderson","Rolland","12345asdfg");
//		Student student = (Student) service.createUserRole(studentCoolearnUser, STUDENT_DISCRIMINATOR,null);
//		assertEquals(0,service.getAllSessionsByStudent(student).size());
//		Calendar start = Calendar.getInstance();
//		start.set(2019, Calendar.MAY, 27);
//		Date date = new Date(start.getTimeInMillis());
//		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
//		Calendar end = Calendar.getInstance();
//		end.set(2019,Calendar.MAY,27);
//		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
//		Availability availability = service.createAvailability(tutor,startTime,endTime,date,date,DayOfWeek.MONDAY);
//		List<Availability> availabilities = new ArrayList<Availability>();
//		availabilities.add(availability);
//		List<Student> studentList = new ArrayList<Student>();
//		studentList.add(student);
//		Session session = service.createSession(studentList,tutor,course,startTime,endTime,date);
//		assertEquals(1,service.getAllSessions().size());
//		try {
//			service.deleteSession(session.getId());
//		} catch (Exception e) {
//			fail(e.getMessage());
//		}
//		assertEquals(0,service.getAllSessions().size());
//	}

	@Test
	public void testGetAllReviewsBySessionSuccess(){
		assertEquals(0,service.getAllSessions().size());
		CoolearnUser tutorCoolearnUser = service.createCoolearnUser("tutor@gmail.com","Marwan","Kanaan","otherpass");
		Subject subj = service.createSubject("Programming");
		Course course = service.createCourse("ECSE 321", subj, EducationLevel.UNIVERSITY);
		Set<Course> setCourses = new HashSet<Course>();
		setCourses.add(course);
		Tutor tutor = (Tutor) service.createUserRole(tutorCoolearnUser, TUTOR_DISCRIMINATOR, setCourses);
		CoolearnUser studentCoolearnUser = service.createCoolearnUser("coco@136.com","Anderson","Rolland","12345asdfg");
		Student student = (Student) service.createUserRole(studentCoolearnUser, STUDENT_DISCRIMINATOR,null);
		assertEquals(0,service.getAllSessionsByStudent(student).size());
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.MAY, 27);
		Date date = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019,Calendar.MAY,27);;
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		Availability availability = service.createAvailability(tutor,startTime,endTime,date,date,DayOfWeek.MONDAY);
		List<Availability> availabilities = new ArrayList<>();
		availabilities.add(availability);
		List<Student> studentList = new ArrayList<>();
		studentList.add(student);
		Session session = service.createSession(studentList,tutor,course,startTime,endTime,date);
		assertEquals(1,service.getAllSessions().size());
		service.createReview(tutor,session,"Good!",(float)4.5);
		assertEquals(1,service.getAllReviews().size());
		List<Review> revs = new ArrayList<Review>();
		try {
			revs= service.getAllReviewsBySession(session);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		assertEquals(1,revs.size());
	}

	@Test
	public void testCreateSpecificCourseHourlyRate() {
		assertEquals(0,service.getAllSpecificCourses().size());
		CoolearnUser tutorCoolearnUser = service.createCoolearnUser("tutor@gmail.com","Marwan","Kanaan","otherpass");
		Subject subj = service.createSubject("Programming");
		Course course = service.createCourse("ECSE 321", subj, EducationLevel.UNIVERSITY);
		Set<Course> setCourses = new HashSet<Course>();
		setCourses.add(course);
		Tutor tutor = (Tutor) service.createUserRole(tutorCoolearnUser, TUTOR_DISCRIMINATOR, setCourses);
		double hourlyRate = 10;

		String error = "";
		try {
			service.createSpecificCourse(hourlyRate, course, tutor);
		}
		catch(IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals("The hourly rate for a course should be greater or equal to " + StringLiterals.MINIMUM_HOURLY_RATE, error );
	}

	@Test
	public void testCreateSpecificCourseNullCourse() {
		assertEquals(0,service.getAllSpecificCourses().size());
		CoolearnUser tutorCoolearnUser = service.createCoolearnUser("tutor@gmail.com","Marwan","Kanaan","otherpass");
		Subject subj = service.createSubject("Programming");
		Course course = service.createCourse("ECSE 321", subj, EducationLevel.UNIVERSITY);
		Set<Course> setCourses = new HashSet<Course>();
		setCourses.add(course);
		Tutor tutor = (Tutor) service.createUserRole(tutorCoolearnUser, TUTOR_DISCRIMINATOR, setCourses);
		double hourlyRate = 12.5;

		String error = "";
		try {
			service.createSpecificCourse(hourlyRate, null, tutor);
		}
		catch(IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals("The course should be specified", error );
	}

	@Test
	public void testCreateSpecificCourseNullTutor() {
		assertEquals(0,service.getAllSpecificCourses().size());
		CoolearnUser tutorCoolearnUser = service.createCoolearnUser("tutor@gmail.com","Marwan","Kanaan","otherpass");
		Subject subj = service.createSubject("Programming");
		Course course = service.createCourse("ECSE 321", subj, EducationLevel.UNIVERSITY);
		Set<Course> setCourses = new HashSet<Course>();
		setCourses.add(course);
		Tutor tutor = (Tutor) service.createUserRole(tutorCoolearnUser, TUTOR_DISCRIMINATOR, setCourses);
		double hourlyRate = 12.5;

		String error = "";
		try {
			service.createSpecificCourse(hourlyRate, course, null);
		}
		catch(IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals("The tutor should be specified", error);
	}

	@Test
	public void testCreateSpecificCourseNoCourseExistsByName() {
		assertEquals(0,service.getAllSpecificCourses().size());
		CoolearnUser tutorCoolearnUser = service.createCoolearnUser("tutor@gmail.com","Marwan","Kanaan","otherpass");
		Subject subj = service.createSubject("Programming");
		Course course = service.createCourse("ECSE 321", subj, EducationLevel.UNIVERSITY);
		assertEquals(true, courseRepository.existsByName(course.getName()));
		Set<Course> setCourses = new HashSet<Course>();
		setCourses.add(course);
		Tutor tutor = (Tutor) service.createUserRole(tutorCoolearnUser, TUTOR_DISCRIMINATOR, setCourses);
		double hourlyRate = 12.5;

		String error = "";
		try {
			Course dummy = new Course();
			dummy.setName("ECSE 999");
			dummy.setSubject(subj);
			dummy.setEducationalLevel(EducationLevel.COLLEGE);
			//dummy course is not persisted...
			service.createSpecificCourse(hourlyRate, dummy, tutor);
		}
		catch(IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals("The course with this name does not exist", error);
	}


	@Test
	public void testDeleteAvailabilityNull() {
		String error = "";
		try {
			service.deleteAvailability(null, null);
		}catch(IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals("Availability does not exist", error);
	}
	@Test
	public void testGetSpecificCoursesByTutor() {
		CoolearnUser user = service.createCoolearnUser("iamtutor@gmail.com", "Foufi", "Foufinho", "password1");
		Subject engineering = service.createSubject("Engineering");
		Subject maths = service.createSubject("Mathematics");
		Course a = service.createCourse("Introduction to Software Engineering", engineering, EducationLevel.UNIVERSITY);
		Course b = service.createCourse("Calculus I", maths, EducationLevel.COLLEGE);
		Course c = service.createCourse("Calculus II", maths, EducationLevel.UNIVERSITY);
		Course d = service.createCourse("Computer Organization", engineering, EducationLevel.UNIVERSITY);
		Set<Course> setOfCourses = new HashSet<Course>();
		setOfCourses.add(a);
		setOfCourses.add(b);
		setOfCourses.add(c);
		setOfCourses.add(d);
		Tutor tutor = (Tutor) service.createUserRole(user, TUTOR_DISCRIMINATOR, setOfCourses);

		List<UserRole> userRole = service.getAllUserRoles();
		assertEquals(1, userRole.size());
		assertEquals(4, service.getAllSpecificCourses().size());
		assertEquals(4, service.getSpecificCoursesByTutor(tutor).size());

		try {
			service.getSpecificCoursesByTutor(tutor);
		}catch(IllegalArgumentException e) {
			fail();
		}	

		assertEquals(4, service.getSpecificCoursesByTutor(tutor).size());
	}

	@Test
	public void testGetSpecificCoursesByTutorNullTutor() {
		String error = "";
		try {
			service.getSpecificCoursesByTutor(null);
		} catch(IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals("Tutor cannot be null", error);
	}
	
	@Test
	public void testDeleteCourseSuccess() {
		assertEquals(0, service.getAllCourses().size());
		//Create 1 course
		Subject subj = service.createSubject("Mathematics");
		Course c = service.createCourse("Calculus II", subj, EducationLevel.COLLEGE);
		
		Set<Course> set = new HashSet<>();
		set.add(c);
		
		CoolearnUser user = service.createCoolearnUser("bibo@biba.com", "Mohamad", "Baboguin", "passwordiz");
		Tutor t = (Tutor) service.createUserRole(user, TUTOR_DISCRIMINATOR, set);
		CoolearnUser user2 = service.createCoolearnUser("goallasso@goal.com", "United", "Chelsea", "1-0");
		Tutor t2 = (Tutor) service.createUserRole(user, TUTOR_DISCRIMINATOR, set);

		assertEquals(2, service.getAllSpecificCourses().size());
		
		try {
			service.deleteCourse(c.getName());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		}
		
		assertEquals(0, service.getAllSpecificCourses().size());
		assertEquals(0, service.getAllCourses().size());
	}
	
	@Test
	public void testDeleteCourseNull() {
		assertEquals(0, service.getAllCourses().size());
		//Create 1 course
		Subject subj = service.createSubject("Mathematics");
		Course c = service.createCourse("Calculus II", subj, EducationLevel.COLLEGE);
		
		Set<Course> set = new HashSet<>();
		set.add(c);
		
		CoolearnUser user = service.createCoolearnUser("bibo@biba.com", "Mohamad", "Baboguin", "passwordiz");
		Tutor t = (Tutor) service.createUserRole(user, TUTOR_DISCRIMINATOR, set);
		CoolearnUser user2 = service.createCoolearnUser("goallasso@goal.com", "United", "Chelsea", "1-0");
		Tutor t2 = (Tutor) service.createUserRole(user, TUTOR_DISCRIMINATOR, set);

		assertEquals(2, service.getAllSpecificCourses().size());
		String error = null;
		try {
			service.deleteCourse(null);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}
		
		assertEquals(2, service.getAllSpecificCourses().size());
		assertEquals(1, service.getAllCourses().size());
		assertEquals("Course name cannot be empty!", error);
		
	}
	
	
	@Test
	public void testGetAllSessionByRoom() {
		assertEquals(0, service.getAllRooms().size());
		
		CoolearnUser studentCoolearnUser = service.createCoolearnUser("student@gmail.com","Jacob", "Silcoff", "pass123");
		CoolearnUser tutorCoolearnUser = service.createCoolearnUser("tutor@gmail.com","Marwan","Kanaan","otherpass");
		Student student = (Student) service.createUserRole(studentCoolearnUser, STUDENT_DISCRIMINATOR, null);
		Subject subj = service.createSubject("Computer Science");
		Course course = service.createCourse("Applied Machine Learning", subj, EducationLevel.UNIVERSITY);
		Set<Course> setOfCourses = new HashSet<Course>();
		setOfCourses.add(course);
		
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.OCTOBER, 24);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019, Calendar.OCTOBER, 24);
		Date endDate = new Date(start.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		
		Tutor tutor = (Tutor) service.createUserRole(tutorCoolearnUser, TUTOR_DISCRIMINATOR, setOfCourses);
		Availability a = service.createAvailability(tutor,startTime, endTime,
				startDate, endDate, DayOfWeek.THURSDAY);
		List<Student> students = new ArrayList<Student>();
		students.add((Student)student);
		
		Calendar session_occ = Calendar.getInstance();
		session_occ.set(2019, Calendar.OCTOBER, 24);
		Date date = new Date(session_occ.getTimeInMillis());
		Time tStart  = Time.valueOf(LocalTime.parse("09:30"));
		Time tEnd = Time.valueOf(LocalTime.parse("10:30"));
		Session s = service.createSession(students,(Tutor) tutor, course, tStart, tEnd, date);
		//ACCEPT SESSION TO ADD (NEED TO CREATE 10 ROOMS IF NUMBER OF ROOMS IS EMPTY)
		service.createRoom(1, RoomSize.SMALL);
	}
	
	@Test
	public void testGetAllSessionByUserAndStatusAndDate() {
		assertEquals(0, service.getAllSessions().size());
		
		CoolearnUser studentCoolearnUser = service.createCoolearnUser("student@gmail.com","Jacob", "Silcoff", "pass123");
		CoolearnUser tutorCoolearnUser = service.createCoolearnUser("tutor@gmail.com","Marwan","Kanaan","otherpass");
		
		Student student = (Student) service.createUserRole(studentCoolearnUser, STUDENT_DISCRIMINATOR, null);
		Subject subj = service.createSubject("Computer Science");
		Course course = service.createCourse("Applied Machine Learning", subj, EducationLevel.UNIVERSITY);
		Set<Course> setOfCourses = new HashSet<Course>();
		setOfCourses.add(course);
		
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.OCTOBER, 24);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019, Calendar.OCTOBER, 24);
		Date endDate = new Date(start.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		
		Tutor tutor = (Tutor) service.createUserRole(tutorCoolearnUser, TUTOR_DISCRIMINATOR, setOfCourses);
		Availability a = service.createAvailability(tutor,startTime, endTime,
				startDate, endDate, DayOfWeek.THURSDAY);
		List<Student> students = new ArrayList<Student>();
		students.add((Student)student);
		
		Calendar session_occ = Calendar.getInstance();
		session_occ.set(2019, Calendar.OCTOBER, 24);
		Date date = new Date(session_occ.getTimeInMillis());
		Time tStart  = Time.valueOf(LocalTime.parse("09:30"));
		Time tEnd = Time.valueOf(LocalTime.parse("10:30"));
		
		Session	s = service.createSession(students,(Tutor) tutor, course, tStart, tEnd, date);

		//TODO Need accept session from dev	

	}
	
	@Test
	public void testGetAllSpecificCoursesByCourse() {
		assertEquals(0, service.getAllCourses().size());
		assertEquals(0, service.getAllSpecificCourses().size());
		//Create 1 course
		Subject subj = service.createSubject("Mathematics");
		Course c = service.createCourse("Calculus II", subj, EducationLevel.COLLEGE);
		
		Set<Course> set = new HashSet<>();
		set.add(c);
		CoolearnUser user = service.createCoolearnUser("bibo@biba.com", "Mohamad", "Baboguin", "passwordiz");
		Tutor t = (Tutor) service.createUserRole(user, TUTOR_DISCRIMINATOR, set);
		CoolearnUser user2 = service.createCoolearnUser("goallasso@goal.com", "United", "Chelsea", "1-0");
		Tutor t2 = (Tutor) service.createUserRole(user, TUTOR_DISCRIMINATOR, set);
		
		List<SpecificCourse> list = null;
		try {
			list = service.getAllSpecificCoursesByCourse(c);
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		}
		
		assertEquals(2, list.size());
		
	}
	
	@Test
	public void testGetCoolearnUser() {
		assertEquals(0, service.getAllCoolearnUsers().size());
		CoolearnUser tutorCoolearnUser = service.createCoolearnUser("tutor@gmail.com","Marwan","Kanaan","otherpass");
		
		CoolearnUser user = null;
		try {
			 user = service.getCoolearnUser("tutor@gmail.com");
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		assertEquals(user.getEmailAddress(), tutorCoolearnUser.getEmailAddress());
	}
	
	@Test
	public void testGetSessionById() {
		CoolearnUser studentCoolearnUser = service.createCoolearnUser("student@gmail.com","Jacob", "Silcoff", "pass123");
		CoolearnUser tutorCoolearnUser = service.createCoolearnUser("tutor@gmail.com","Marwan","Kanaan","otherpass");
		Student student = (Student) service.createUserRole(studentCoolearnUser, STUDENT_DISCRIMINATOR, null);
		Subject subj = service.createSubject("Computer Science");
		Course course = service.createCourse("Applied Machine Learning", subj, EducationLevel.UNIVERSITY);
		Set<Course> setOfCourses = new HashSet<Course>();
		setOfCourses.add(course);
		
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.OCTOBER, 24);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019, Calendar.OCTOBER, 24);
		Date endDate = new Date(start.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		
		Tutor tutor = (Tutor) service.createUserRole(tutorCoolearnUser, TUTOR_DISCRIMINATOR, setOfCourses);
		Availability a = service.createAvailability(tutor,startTime, endTime,
				startDate, endDate, DayOfWeek.THURSDAY);
		List<Student> students = new ArrayList<Student>();
		students.add((Student)student);
		
		Calendar session_occ = Calendar.getInstance();
		session_occ.set(2019, Calendar.OCTOBER, 24);
		Date date = new Date(session_occ.getTimeInMillis());
		Time tStart  = Time.valueOf(LocalTime.parse("09:30"));
		Time tEnd = Time.valueOf(LocalTime.parse("10:30"));
		Session s = service.createSession(students,(Tutor) tutor, course, tStart, tEnd, date);
		Session session = null;
		try {
			session = service.getSessionById(s.getId());
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		assertEquals(s.getId(), session.getId());
	}
	
	@Test
	public void testGetUserRolesByCoolearnUser() {
		CoolearnUser a = service.createCoolearnUser("a@gmail.com","Marwan","Kanaan","otherpass");
		Student student = (Student) service.createUserRole(a, STUDENT_DISCRIMINATOR, null);
		Subject subj = service.createSubject("Computer Science");
		Course course = service.createCourse("Applied Machine Learning", subj, EducationLevel.UNIVERSITY);
		Set<Course> setOfCourses = new HashSet<Course>();
		setOfCourses.add(course);
		Tutor t = (Tutor) service.createUserRole(a, TUTOR_DISCRIMINATOR, setOfCourses);
		
		List<UserRole> list = null;
		try {
			list = service.getUserRolesByCoolearnUser(a);
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		}
		
		assertEquals(2, list.size());
	}
	
	@Test
	public void testCheckAvailabilityExistsForTutorSuccess() {
		assertEquals(0, service.getAllCoolearnUsers().size());
		
		CoolearnUser tutorCoolearnUser = service.createCoolearnUser("tutor@gmail.com","Marwan","Kanaan","otherpass");
		
		Subject subj = service.createSubject("Computer Science");
		Course course = service.createCourse("Applied Machine Learning", subj, EducationLevel.UNIVERSITY);
		Set<Course> setOfCourses = new HashSet<Course>();
		setOfCourses.add(course);
		Tutor tutor = (Tutor) service.createUserRole(tutorCoolearnUser, TUTOR_DISCRIMINATOR, setOfCourses);

		//Availability
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.OCTOBER, 24);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019, Calendar.OCTOBER, 24);
		Date endDate = new Date(start.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		
		Calendar start2 = Calendar.getInstance();
		start2.set(2019, Calendar.OCTOBER, 25);
		Date startDate2 = new Date(start2.getTimeInMillis());
		Time startTime2  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end2 = Calendar.getInstance();
		end2.set(2019, Calendar.NOVEMBER, 28);
		Date endDate2 = new Date(start2.getTimeInMillis());
		Time endTime2  = Time.valueOf(LocalTime.parse("11:00"));

		Availability a, b = null;
		try {
			a = service.createAvailability(tutor,startTime, endTime, startDate, endDate, DayOfWeek.THURSDAY);
			b = service.createAvailability(tutor,startTime2, endTime2, startDate2, endDate2, DayOfWeek.TUESDAY);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		assertEquals(2, tutor.getAvailability().size());
	}
	
	//We can only avoid the case where they are exactly duplicate (we do not count the number of times it occurs)
	@Test
	public void testCheckAvailabilityExistsForTutorExactDuplicate() {
		assertEquals(0, service.getAllCoolearnUsers().size());
		
		CoolearnUser tutorCoolearnUser = service.createCoolearnUser("tutor@gmail.com","Marwan","Kanaan","otherpass");
		
		Subject subj = service.createSubject("Computer Science");
		Course course = service.createCourse("Applied Machine Learning", subj, EducationLevel.UNIVERSITY);
		Set<Course> setOfCourses = new HashSet<Course>();
		setOfCourses.add(course);
		Tutor tutor = (Tutor) service.createUserRole(tutorCoolearnUser, TUTOR_DISCRIMINATOR, setOfCourses);

		//Availability
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.OCTOBER, 24);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019, Calendar.OCTOBER, 24);
		Date endDate = new Date(start.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		
		Calendar start2 = Calendar.getInstance();
		start2.set(2019, Calendar.OCTOBER, 24);
		Date startDate2 = new Date(start2.getTimeInMillis());
		Time startTime2  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end2 = Calendar.getInstance();
		end2.set(2019, Calendar.OCTOBER, 24);
		Date endDate2 = new Date(start2.getTimeInMillis());
		Time endTime2  = Time.valueOf(LocalTime.parse("11:00"));

		Availability a, b = null;
		String error = null;
		try {
			a = service.createAvailability(tutor,startTime, endTime, startDate, endDate, DayOfWeek.THURSDAY);
			b = service.createAvailability(tutor,startTime2, endTime2, startDate2, endDate2, DayOfWeek.THURSDAY);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}
		
		assertEquals("This availability already exists for tutor.", error);
	}
	
	@Test
	public void testRoomCreation() {
		assertEquals(0, service.getAllSessions().size());
		CoolearnUser studentCoolearnUser = service.createCoolearnUser("student@gmail.com","Jacob", "Silcoff", "pass123");
		CoolearnUser tutorCoolearnUser = service.createCoolearnUser("tutor@gmail.com","Marwan","Kanaan","otherpass");
		Student student = (Student) service.createUserRole(studentCoolearnUser, STUDENT_DISCRIMINATOR, null);
		Subject subj = service.createSubject("Computer Science");
		Course course = service.createCourse("Applied Machine Learning", subj, EducationLevel.UNIVERSITY);
		Set<Course> setOfCourses = new HashSet<Course>();
		setOfCourses.add(course);
		
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.OCTOBER, 24);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019, Calendar.OCTOBER, 24);
		Date endDate = new Date(start.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		
		Tutor tutor = (Tutor) service.createUserRole(tutorCoolearnUser, TUTOR_DISCRIMINATOR, setOfCourses);
		Availability a = service.createAvailability(tutor,startTime, endTime,
				startDate, endDate, DayOfWeek.THURSDAY);
		List<Student> students = new ArrayList<Student>();
		students.add((Student)student);
		
		Calendar session_occ = Calendar.getInstance();
		session_occ.set(2019, Calendar.OCTOBER, 24);
		Date date = new Date(session_occ.getTimeInMillis());
		Time tStart  = Time.valueOf(LocalTime.parse("09:30"));
		Time tEnd = Time.valueOf(LocalTime.parse("10:30"));
		
		Session s = null;
		try {
			s = service.createSession(students,(Tutor) tutor, course, tStart, tEnd, date);
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		}
		
		assertEquals(13, service.getAllRooms().size());

	}
	
}
