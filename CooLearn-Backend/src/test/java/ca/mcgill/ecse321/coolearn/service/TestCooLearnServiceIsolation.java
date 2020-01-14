package ca.mcgill.ecse321.coolearn.service;

import static ca.mcgill.ecse321.coolearn.service.StringLiterals.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.util.*;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import ca.mcgill.ecse321.coolearn.dao.*;
import ca.mcgill.ecse321.coolearn.model.*;


@RunWith(MockitoJUnitRunner.class)
public class TestCooLearnServiceIsolation {
	
	@InjectMocks
	private CooLearnService service;
	@Mock
	private AvailabilityRepository availabilityDao;
	@Mock
	private CoolearnUserRepository coolearnUserDao;
	@Mock
	private CourseRepository courseDao;
	@Mock
	private ReviewRepository reviewDao;
	@Mock
	private RoomRepository roomDao;
	@Mock
	private SessionRepository sessionDao;
	@Mock
	private SpecificCourseRepository specificCourseDao;
	@Mock
	private SubjectRepository subjectDao;
	@Mock
	private UserRoleRepository userRoleDao;

	private static CoolearnUser user_1 = new CoolearnUser();
	private static CoolearnUser user_2 = new CoolearnUser();
	private static CoolearnUser user_3 = new CoolearnUser();
	private static CoolearnUser user_4 = new CoolearnUser();
	private static Student student = new Student();
	private static Tutor tutor = new Tutor();
	private static Session session_1 = new Session();
	private static Session session_2 = new Session();
	private static Session session_3 = new Session();
	private static UserRole role_1;
	private static UserRole role_2;

	@Before
	public void setMockOutput() {
		// TODO Clean up and make database setup uniform
		// TODO Clean up redundant assertEquals

		/*
		*	Mock environment set-up for reviewDao JPA Query when calling from CRUD methods:
		* ----Callee (DAO)-------------------Caller (CooLearnService)
		* 	1. findByStudent(),findByTutor(),
		* 	    save()<--------------------------- createReview()
		 */
		user_4.setEmailAddress("student@gmail.com");
		user_4.setFirstName("Jacob");
		user_4.setLastName("Silcoff");
		user_4.setPassword("pass123");

		role_1 = student;
		role_1.setUser(user_4);
		Room roomSessionTest = new Room();
		roomSessionTest.setRoomNumber(1);
		session_1.setRoom(roomSessionTest);
		session_1.setId(1);
		session_2.setRoom(roomSessionTest);
		session_2.setId(2);
		session_3.setRoom(roomSessionTest);
		session_3.setId(3);
		when(reviewDao.existsById(1)).thenAnswer((InvocationOnMock invocation) -> {
			return true;
		});
		when(reviewDao.findById(1)).thenAnswer((InvocationOnMock invocation) -> {
			Review review  = new Review();
			review.setId(1);
			Optional<Review> reviewOptional = Optional.of(review);
			return reviewOptional;
		});
		when(sessionDao.findByStudent(student)).thenAnswer((InvocationOnMock invocation) -> {
			List<Session> sessionList = new ArrayList<>();
			sessionList.add(session_1);
			sessionList.add(session_2);
			sessionList.add(session_3);
			Set<Session> sessionSet = new HashSet<>();
			sessionSet.add(session_1);
			sessionSet.add(session_2);
			sessionSet.add(session_3);
			student.setSession(sessionSet);
			return sessionList;
		});

		user_3.setEmailAddress("xxx@ooo.com");
		user_3.setFirstName("Donald");
		user_3.setLastName("Trump");
		user_3.setPassword("pass123");

		role_2 = tutor;
		role_2.setUser(user_3);
		when(sessionDao.findByTutor(tutor)).thenAnswer((InvocationOnMock invocation) -> {
			List<Session> sessionList = new ArrayList<>();
			sessionList.add(session_1);
			sessionList.add(session_2);
			sessionList.add(session_3);
			Set<Session> sessionSet = new HashSet<>();
			sessionSet.add(session_1);
			sessionSet.add(session_2);
			sessionSet.add(session_3);
			tutor.setSession(sessionSet);
			return sessionList;
		});

		/*
		*	Mock environment set-up for userRoleDao JPA Query when calling from CRUD methods:
		* ----Callee (DAO)-------------------Caller (CooLearnService)
		* 	1. save() <--------------------------- createUserRole()
		* 	2. existsById(),findById <------------ getUserRole()
		* 	3. existsByUser(),findByUser() <------ getUserRolesByCoolearnUser()
		*   4. findAll() <------------------------ getAllUserRoles()
		 */
		when(courseDao.existsByName("AAA")).thenAnswer((InvocationOnMock invocation) -> {
			return true;
		});
		when(courseDao.existsByName("BBB")).thenAnswer((InvocationOnMock invocation) -> {
			return true;
		});
		when(courseDao.existsByName("CCC")).thenAnswer((InvocationOnMock invocation) -> {
			return true;
		});
		when(userRoleDao.existsById(1234)).thenAnswer((InvocationOnMock invocation) -> {
			return true;
		});
		when(userRoleDao.findById(1234)).thenAnswer((InvocationOnMock invocation) -> {
			CoolearnUser user = new CoolearnUser();
			//TODO verify that it is an emailAddress
			user.setEmailAddress("student@gmail.com");
			user.setFirstName("Jacob");
			user.setLastName("Silcoff");
			//TODO make the creation more secure by hashing password
			user.setPassword("pass123");

			// 	create a non-null tutor
			Calendar start = Calendar.getInstance();
			start.set(2019, Calendar.MAY, 27);
			Date startDate = new Date(start.getTimeInMillis());
			Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
			Calendar end = Calendar.getInstance();
			end.set(2019,Calendar.NOVEMBER,11);
			Date endDate = new Date (end.getTimeInMillis());
			Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
			Availability availability = new Availability();
			availability.setDayOfWeek(DayOfWeek.WEDNESDAY);
			availability.setStartDate(startDate);
			availability.setEndDate(endDate);
			availability.setStartTime(startTime);
			availability.setEndTime(endTime);
			Tutor tutor = new Tutor();
			Set<Availability> setOfAvailabilities = new HashSet<Availability>();
			setOfAvailabilities.add(availability);
			setOfAvailabilities.add(availabilityDao.findById(37).get());
			tutor.setAvailability(setOfAvailabilities);

			UserRole role = tutor;
			role.setUser(user);

			Optional<UserRole> userRole = Optional.of(role);
			return userRole;
		});
		user_1.setEmailAddress("123@456.com");
		when(userRoleDao.existsByUser(user_1)).thenAnswer((InvocationOnMock invocation) -> {
			return true;
		});
		user_2.setEmailAddress("456@789.com");
		when(userRoleDao.existsByUser(user_2)).thenAnswer((InvocationOnMock invocation) -> {
			return false;
		});
		when(userRoleDao.findByUser(user_1)).thenAnswer((InvocationOnMock invocation) -> {
			// 	create a non-null tutor
			Calendar start = Calendar.getInstance();
			start.set(2019, Calendar.MAY, 27);
			Date startDate = new Date(start.getTimeInMillis());
			Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
			Calendar end = Calendar.getInstance();
			end.set(2019,Calendar.NOVEMBER,11);
			Date endDate = new Date (end.getTimeInMillis());
			Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
			Availability availability = new Availability();
			availability.setDayOfWeek(DayOfWeek.WEDNESDAY);
			availability.setStartDate(startDate);
			availability.setEndDate(endDate);
			availability.setStartTime(startTime);
			availability.setEndTime(endTime);
			Tutor tutor = new Tutor();
			Set<Availability> setOfAvailabilities = new HashSet<Availability>();
			setOfAvailabilities.add(availability);
			setOfAvailabilities.add(availabilityDao.findById(37).get());
			tutor.setAvailability(setOfAvailabilities);

			UserRole role = tutor;
			role.setUser(user_1);
			List<UserRole> list = new ArrayList<>();
			list.add(role);
			return list;
		});

		/*
		*	Mock environment set-up for coolearnUserDao JPA Query when calling from CRUD methods:
		* ----Callee (DAO)-------------------Caller (CooLearnService)
		* 	1. save(),existsByEmailAddress() <----------- createCooLearnUser()
		* 	2. existsByEmailAddress(),
		* 		findByEmailAddress <--------------------- getCoolearnUser()
		* 	3. findAll() <------------------------------- getAllCoolearnUsers()
		 */
		when(coolearnUserDao.existsByEmailAddress("ALREADY EXISTS")).thenAnswer((InvocationOnMock invocation) -> {
			return true;
		});

		when(coolearnUserDao.existsByEmailAddress("success.test@gmail.com")).thenAnswer((InvocationOnMock invocation) -> {
			return true;
		});
		/*
		*	Mock environment set-up for specificCourseDao JPA Query when calling from CRUD methods:
		* ----Callee (DAO)-------------------Caller (CooLearnService)
		* 	1. save(),existsByName() <----------- createSpecificCourse()
		* 	2. findAll() <----------------------- getAllSpecificCourses()
		* 	3. save() <-------------------------- updatePriceOfSpecificCourse()
		* 	4. findByTutor() <------------------- getSpecificCoursesByTutor()
		* 	5. findById() <---------------------- getSpecificCourse()
		* 	6. findByCourse() <------------------ getAllSpecificCoursesByCourse()
		 */
		when(specificCourseDao.findByTutor(any(Tutor.class))).thenAnswer((InvocationOnMock invocation) -> {
			Course course = new Course();
			course.setName("ALREADY EXISTS");
			// SET UP TUTOR
			Tutor tutor = new Tutor();
			Calendar start = Calendar.getInstance();
			start.set(2019, Calendar.MAY, 27);
			Date startDate = new Date(start.getTimeInMillis());
			Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
			Calendar end = Calendar.getInstance();
			end.set(2019,Calendar.NOVEMBER,11);
			Date endDate = new Date (end.getTimeInMillis());
			Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
			Availability availability = new Availability();
			availability.setDayOfWeek(DayOfWeek.MONDAY);
			availability.setStartDate(startDate);
			availability.setEndDate(endDate);
			availability.setStartTime(startTime);
			availability.setEndTime(endTime);
			Set<Availability> setOfAvailabilities = new HashSet<Availability>();
			setOfAvailabilities.add(availability);
			tutor.setAvailability(setOfAvailabilities);
			SpecificCourse specificCourse = new SpecificCourse();
			specificCourse.setCourse(course);
			specificCourse.setHourlyRate(15);
			specificCourse.setTutor(tutor);
			List<SpecificCourse> list = new ArrayList<>();
			list.add(specificCourse);
			return list;
		});
		when(specificCourseDao.findById(any(Integer.class))).thenAnswer((InvocationOnMock invocation) -> {
			Course course = new Course();
			course.setName("ALREADY EXISTS");
			// SET UP TUTOR
			Tutor tutor = new Tutor();
			Calendar start = Calendar.getInstance();
			start.set(2019, Calendar.MAY, 27);
			Date startDate = new Date(start.getTimeInMillis());
			Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
			Calendar end = Calendar.getInstance();
			end.set(2019,Calendar.NOVEMBER,11);
			Date endDate = new Date (end.getTimeInMillis());
			Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
			Availability availability = new Availability();
			availability.setDayOfWeek(DayOfWeek.MONDAY);
			availability.setStartDate(startDate);
			availability.setEndDate(endDate);
			availability.setStartTime(startTime);
			availability.setEndTime(endTime);
			Set<Availability> setOfAvailabilities = new HashSet<Availability>();
			setOfAvailabilities.add(availability);
			tutor.setAvailability(setOfAvailabilities);
			SpecificCourse specificCourse = new SpecificCourse();
			specificCourse.setCourse(course);
			specificCourse.setHourlyRate(15);
			specificCourse.setTutor(tutor);
			Optional<SpecificCourse> specificCourseOptional = Optional.of(specificCourse);
			return specificCourseOptional;
		});

		when(specificCourseDao.findAll()).thenAnswer((InvocationOnMock invocation) -> {
			Course course = new Course();
			course.setName("ALREADY EXISTS");
			// SET UP TUTOR
			Tutor tutor = new Tutor();
			Calendar start = Calendar.getInstance();
			start.set(2019, Calendar.MAY, 27);
			Date startDate = new Date(start.getTimeInMillis());
			Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
			Calendar end = Calendar.getInstance();
			end.set(2019,Calendar.NOVEMBER,11);
			Date endDate = new Date (end.getTimeInMillis());
			Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
			Availability availability = new Availability();
			availability.setDayOfWeek(DayOfWeek.MONDAY);
			availability.setStartDate(startDate);
			availability.setEndDate(endDate);
			availability.setStartTime(startTime);
			availability.setEndTime(endTime);
			Set<Availability> setOfAvailabilities = new HashSet<Availability>();
			setOfAvailabilities.add(availability);
			tutor.setAvailability(setOfAvailabilities);
			SpecificCourse specificCourse = new SpecificCourse();
			specificCourse.setCourse(course);
			specificCourse.setHourlyRate(15);
			specificCourse.setTutor(tutor);
			List<SpecificCourse> list = new ArrayList<>();
			list.add(specificCourse);
			return list;
		});

		when(specificCourseDao.findByCourse(any(Course.class))).thenAnswer((InvocationOnMock invocation) -> {
			Course course = new Course();
			course.setName("ALREADY EXISTS");
			// SET UP TUTOR
			Tutor tutor = new Tutor();
			Calendar start = Calendar.getInstance();
			start.set(2019, Calendar.MAY, 27);
			Date startDate = new Date(start.getTimeInMillis());
			Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
			Calendar end = Calendar.getInstance();
			end.set(2019,Calendar.NOVEMBER,11);
			Date endDate = new Date (end.getTimeInMillis());
			Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
			Availability availability = new Availability();
			availability.setDayOfWeek(DayOfWeek.MONDAY);
			availability.setStartDate(startDate);
			availability.setEndDate(endDate);
			availability.setStartTime(startTime);
			availability.setEndTime(endTime);
			Set<Availability> setOfAvailabilities = new HashSet<Availability>();
			setOfAvailabilities.add(availability);
			tutor.setAvailability(setOfAvailabilities);
			SpecificCourse specificCourse = new SpecificCourse();
			specificCourse.setCourse(course);
			specificCourse.setHourlyRate(15);
			specificCourse.setTutor(tutor);
			List<SpecificCourse> list = new ArrayList<>();
			list.add(specificCourse);
			return list;
		});
		/*
		*	Mock environment set-up for roomDao JPA Query when calling from CRUD methods:
		* ----Callee (DAO)-------------------Caller (CooLearnService)
		* 	1. save() <-------------------------- createRoom()
		* 	2. findByRoomNumber() <-------------- getRoom()
		* 	3. findByRoomsize() <---------------- getRoomBySize()
		* 	4. findAll() <----------------------- getAllRooms()
		 */
		when(roomDao.findByRoomNumber(1)).thenAnswer((InvocationOnMock invocation) -> {
			Room room = new Room();
			room.setRoomNumber(1);
			room.setRoomsize(RoomSize.LARGE);
			Optional<Room> roomOptional = Optional.of(room);
			return roomOptional;
		});
		when(roomDao.findByRoomNumber(2)).thenAnswer((InvocationOnMock invocation) -> {
			Room room = new Room();
			Optional<Room> roomOptional = Optional.of(room);
			return roomOptional;
		});
		when(roomDao.findByRoomsize(RoomSize.LARGE)).thenAnswer((InvocationOnMock invocation) -> {
			Room room = new Room();
			room.setRoomNumber(1);
			room.setRoomsize(RoomSize.LARGE);
			List<Room> rooms= new ArrayList<>();
			rooms.add(room);
			return rooms;
		});
		when(roomDao.findByRoomsize(RoomSize.SMALL)).thenAnswer((InvocationOnMock invocation) -> {
			List<Room> rooms= new ArrayList<>();
			return rooms;
		});
		when(roomDao.findAll()).thenAnswer((InvocationOnMock invocation) -> {
			Room room = new Room();
			room.setRoomNumber(1);
			room.setRoomsize(RoomSize.LARGE);
			List<Room> rooms= new ArrayList<>();
			rooms.add(room);
			return rooms;
		});

		/*
		*	Mock environment set-up for availabilityDao JPA Query when calling from CRUD methods:
		* ----Callee (DAO)-------------------Caller (CooLearnService)
		* 	1. none <---------------------------- checkAvailabilityExistsForTutor(),getAllAvailabilitiesByTutorId
		* 	2. save() <-------------------------- createAvailability(),updateAvailability(),deleteAvailability()
		* 	3. delete() <------------------------ deleteAvailability()
		* 	4. findAll() <----------------------- getAllAvailabilities()
		* 	5. existsById() <---------- getAvailabilityById()
		 */

		when(availabilityDao.findById(anyInt())).thenAnswer((InvocationOnMock invocation) -> {
			Calendar start = Calendar.getInstance();
			start.set(2019, Calendar.MAY, 27);
			Date startDate = new Date(start.getTimeInMillis());
			Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
			Calendar end = Calendar.getInstance();
			end.set(2019,Calendar.NOVEMBER,11);
			Date endDate = new Date (end.getTimeInMillis());
			Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
			Availability availability = new Availability();
			availability.setDayOfWeek(DayOfWeek.MONDAY);
			availability.setStartDate(startDate);
			availability.setEndDate(endDate);
			availability.setStartTime(startTime);
			availability.setEndTime(endTime);
			availability.setId(37);
			Optional<Availability> availabilityOptional = Optional.of(availability);
			return availabilityOptional;
		});
		// Tutor
		when(userRoleDao.findById(888)).thenAnswer((InvocationOnMock invocation) -> {
			CoolearnUser user = new CoolearnUser();
			//TODO verify that it is an emailAddress
			user.setEmailAddress("student@gmail.com");
			user.setFirstName("Jacob");
			user.setLastName("Silcoff");
			//TODO make the creation more secure by hashing password
			user.setPassword("pass123");
			Subject subject = new Subject();
			subject.setSubjectName("SOFTWARE ENGINEERING");

			Calendar start = Calendar.getInstance();
			start.set(2019, Calendar.MAY, 27);
			Date startDate = new Date(start.getTimeInMillis());
			Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
			Calendar end = Calendar.getInstance();
			end.set(2019,Calendar.NOVEMBER,11);
			Date endDate = new Date (end.getTimeInMillis());
			Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
			Availability availability = new Availability();
			availability.setDayOfWeek(DayOfWeek.WEDNESDAY);
			availability.setStartDate(startDate);
			availability.setEndDate(endDate);
			availability.setStartTime(startTime);
			availability.setEndTime(endTime);
			Tutor tutor = new Tutor();
			Set<Availability> setOfAvailabilities = new HashSet<Availability>();
			setOfAvailabilities.add(availability);
			setOfAvailabilities.add(availabilityDao.findById(37).get());
			tutor.setAvailability(setOfAvailabilities);

			UserRole role = tutor;
			role.setUser(user);

			Optional<UserRole> userRole = Optional.of(role);

			return userRole;
		});
		// Student
		when(userRoleDao.findById(999)).thenAnswer((InvocationOnMock invocation) -> {
			CoolearnUser user = new CoolearnUser();
			//TODO verify that it is an emailAddress
			user.setEmailAddress("student@gmail.com");
			user.setFirstName("Jacob");
			user.setLastName("Silcoff");
			//TODO make the creation more secure by hashing password
			user.setPassword("pass123");

			Student student = new Student();

			UserRole role = student;
			role.setUser(user);

			Optional<UserRole> userRole = Optional.of(role);

			return userRole;
		});
		when(userRoleDao.existsById(888)).thenAnswer((InvocationOnMock invocation) -> {
			return true;
		});
		when(userRoleDao.existsById(999)).thenAnswer((InvocationOnMock invocation) -> {
			return true;
		});
		when(availabilityDao.existsById(37)).thenAnswer((InvocationOnMock invocation) -> {
			return true;
		});
		/*
		*	Mock environment set-up for subjectDao JPA Query when calling from CRUD methods:
		* ----Callee (DAO)-------------------Caller (CooLearnService)
		* 	1. save() <-------------------------- createSubject()
		* 	2. findBySubjectName() <------------- getSubjectByName()
		* 	3. findAll() <----------------------- getAllSubjects()
		* 	4. existsBySubejectName() <---------- createSubject, getSubjectByName(), deleteSubject()
		* 	5. delete() <------------------------ deleteSubject()
		 */
		when(subjectDao.existsBySubjectName("GETBYNAME SUCCESS TEST")).thenAnswer((InvocationOnMock invocation) -> {
			return true;
		});
		when(subjectDao.existsBySubjectName("DELETE SUCCESS TEST")).thenAnswer((InvocationOnMock invocation) -> {
			return true;
		});
		when(subjectDao.existsBySubjectName("ALREADY EXISTS")).thenAnswer((InvocationOnMock invocation) -> {
			return true;
		});
		when(subjectDao.existsBySubjectName("NON-EXIST")).thenAnswer((InvocationOnMock invocation) -> {
			return false;
		});
		when(subjectDao.findAll()).thenAnswer((InvocationOnMock invocation) -> {
			String name = "POLITICAL SCIENCE";
			String name_2 = "RELIGIOUS STUDIES";
			String name_3 = "PSYCHIATRY";
			Subject subject = new Subject();
			subject.setSubjectName(name);
			Subject subject_2 = new Subject();
			subject_2.setSubjectName(name_2);
			Subject subject_3 = new Subject();
			subject_3.setSubjectName(name_3);
			List<Subject> list = new ArrayList<>();
			list.add(subject);
			list.add(subject_2);
			list.add(subject_3);
			return list;
		});
		when(subjectDao.findBySubjectName(anyString())).thenAnswer((InvocationOnMock invocation) -> {
			String name = "POLITICAL SCIENCE";
			Subject subject = new Subject();
			subject.setSubjectName(name);
			return subject;
		});
		doNothing().when(subjectDao).delete(any(Subject.class));

		/*
		*	Mock environment set-up for courseDao JPA Query when calling from CRUD methods:
		* ----Callee (DAO)-------------------Caller (CooLearnService)
		* 	1. save() <-------------------------- createCourse()
		* 	2. findByName() <-------------------- getCourseByName()
		* 	3. findBySubject() <----------------- getCourseBySubject()
		* 	4. findByEducationalLevel() <-------  getCourseByEducationalLevel()
		* 	5. findAll() <----------------------- getAllCourses()
		* 	6. existsByName <-------------------- createCourse(),getCourseByName(),deleteCourse()
		* 	7. delete() <----------------------- deleteCourse()
		 */
		when(courseDao.existsByName("NON-EXIST")).thenAnswer((InvocationOnMock invocation) -> {
			return false;
		});
		when(courseDao.existsByName("ALREADY EXISTS")).thenAnswer((InvocationOnMock invocation) -> {
			return true;
		});
		when(courseDao.existsByName("GETBYNAME SUCCESS TEST")).thenAnswer((InvocationOnMock invocation) -> {
			return true;
		});
		when(courseDao.findByName(anyString())).thenAnswer((InvocationOnMock invocation) -> {
			String name = "Introduction to Software Engineering";
			Subject subject = new Subject();
			subject.setSubjectName("SOFTWARE ENGINEERING");
			Course course = new Course();
			course.setName(name.toUpperCase());
			course.setEducationalLevel(EducationLevel.UNIVERSITY);
			course.setSubject(subject);
			Optional<Course> courseOptional = Optional.of(course);
			return courseOptional;
		});
		when(courseDao.findBySubject(any(Subject.class))).thenAnswer((InvocationOnMock invocation) -> {
			Subject subject = new Subject();
			subject.setSubjectName("SOFTWARE ENGINEERING");
			Course course_1 = new Course();
			course_1.setName("AAA");
			course_1.setEducationalLevel(EducationLevel.UNIVERSITY);
			course_1.setSubject(subject);
			Course course_2 = new Course();
			course_2.setName("BBB");
			course_2.setEducationalLevel(EducationLevel.UNIVERSITY);
			course_2.setSubject(subject);
			Course course_3 = new Course();
			course_3.setName("CCC");
			course_3.setEducationalLevel(EducationLevel.UNIVERSITY);
			course_3.setSubject(subject);
			List<Course> courseList = new ArrayList<>();
			courseList.add(course_1);
			courseList.add(course_2);
			courseList.add(course_3);
			return courseList;
		});

		when(courseDao.findByEducationalLevel(any(EducationLevel.class))).thenAnswer((InvocationOnMock invocation) -> {
			Subject subject = new Subject();
			subject.setSubjectName("SOFTWARE ENGINEERING");
			Course course_1 = new Course();
			course_1.setName("AAA");
			course_1.setEducationalLevel(EducationLevel.UNIVERSITY);
			course_1.setSubject(subject);
			Course course_2 = new Course();
			course_2.setName("BBB");
			course_2.setEducationalLevel(EducationLevel.UNIVERSITY);
			course_2.setSubject(subject);
			Course course_3 = new Course();
			course_3.setName("CCC");
			course_3.setEducationalLevel(EducationLevel.UNIVERSITY);
			course_3.setSubject(subject);
			List<Course> courseList = new ArrayList<>();
			courseList.add(course_1);
			courseList.add(course_2);
			courseList.add(course_3);
			return courseList;
		});

		when(courseDao.findAll()).thenAnswer((InvocationOnMock invocation) -> {
			Subject subject = new Subject();
			subject.setSubjectName("SOFTWARE ENGINEERING");
			Course course_1 = new Course();
			course_1.setName("AAA");
			course_1.setEducationalLevel(EducationLevel.UNIVERSITY);
			course_1.setSubject(subject);
			Course course_2 = new Course();
			course_2.setName("BBB");
			course_2.setEducationalLevel(EducationLevel.UNIVERSITY);
			course_2.setSubject(subject);
			Course course_3 = new Course();
			course_3.setName("CCC");
			course_3.setEducationalLevel(EducationLevel.UNIVERSITY);
			course_3.setSubject(subject);
			List<Course> courseList = new ArrayList<>();
			courseList.add(course_1);
			courseList.add(course_2);
			courseList.add(course_3);
			return courseList;
		});
		when(courseDao.existsByName("DELETE SUCCESS TEST")).thenAnswer((InvocationOnMock invocation) -> {
			return true;
		});
		doNothing().when(courseDao).delete(any(Course.class));


		// Whenever anything is saved, just return the parameter object
		Answer<?> returnParameterAsAnswer = (InvocationOnMock invocation) -> {
			return invocation.getArgument(0);
		};
		when(availabilityDao.save(any(Availability.class))).thenAnswer(returnParameterAsAnswer);
		when(coolearnUserDao.save(any(CoolearnUser.class))).thenAnswer(returnParameterAsAnswer);
		when(courseDao.save(any(Course.class))).thenAnswer(returnParameterAsAnswer);
		when(reviewDao.save(any(Review.class))).thenAnswer(returnParameterAsAnswer);
		when(roomDao.save(any(Room.class))).thenAnswer(returnParameterAsAnswer);
//		when(sessionDao.save(any(Session.class))).thenAnswer(returnParameterAsAnswer);
		when(specificCourseDao.save(any(SpecificCourse.class))).thenAnswer(returnParameterAsAnswer);
		when(subjectDao.save(any(Subject.class))).thenAnswer(returnParameterAsAnswer);
		when(userRoleDao.save(any(UserRole.class))).thenAnswer(returnParameterAsAnswer);
	}

	/*
	*
	* -----------------------------CourseRepository CRUD Tests-----------------------------------------
	* 									Subtotal: 14 tests
	 */

	@Test
	public void testCreateCourseSuccess() {

		String name = "Introduction to Software Engineering";
		Subject subject = service.createSubject("Physics");
		EducationLevel educationLevel = EducationLevel.SECONDARY_SCHOOL;

		Course course = service.createCourse(name, subject, educationLevel);

		assertEquals(name.toUpperCase(), course.getName());
		assertEquals(subject.getSubjectName(), course.getSubject().getSubjectName());
		assertEquals(educationLevel, course.getEducationalLevel());
	}

	@Test
	public void testCreateCourseEmptyName() {

		String name = "";
		Subject subject = service.createSubject("Physics");
		EducationLevel educationLevel = EducationLevel.SECONDARY_SCHOOL;

		String error = null;
		try {
			service.createCourse(name,subject,educationLevel);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals("Invalid course name!", error);
	}

	@Test
	public void testCreateCourseNameAlreadyExisted() {

		String name = "ALREADY EXISTS";
		Subject subject = service.createSubject("Physics");
		EducationLevel educationLevel = EducationLevel.SECONDARY_SCHOOL;

		String error = null;
		try {
			service.createCourse(name,subject,educationLevel);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals("Course with this name already exists.", error);
	}

	@Test
	public void testCreateCourseNullSubject() {

		String name = "Introduction to Software Engineering";
		Subject subject = null;
		EducationLevel educationLevel = EducationLevel.SECONDARY_SCHOOL;

		String error = null;
		try {
			service.createCourse(name,subject,educationLevel);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals("Null subject!", error);
	}

	@Test
	public void testCreateCourseNullEducationLevel() {

		String name = "Introduction to Software Engineering";
		Subject subject = service.createSubject("Physics");
		EducationLevel educationLevel = null;

		String error = null;
		try {
			service.createCourse(name,subject,educationLevel);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals("You need to pass an education level!", error);
	}

	@Test
	public void testGetCourseByNameSuccess(){
		String result = "Introduction to Software Engineering";
		String name = "GETBYNAME SUCCESS TEST";
		Course course = service.getCourseByName(name);
		assertEquals(result.toUpperCase(),course.getName());
		assertEquals("SOFTWARE ENGINEERING", course.getSubject().getSubjectName());
		assertEquals(EducationLevel.UNIVERSITY,course.getEducationalLevel());
	}
	@Test
	public void testGetCourseByNameNonExisted() {

		String error = null;
		String name = "NON-EXIST";
		Subject subject = service.createSubject("Physics");
		EducationLevel educationLevel = EducationLevel.SECONDARY_SCHOOL;

		try {
			service.getCourseByName(name);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals("No course exists with this name", error);
	}

	// Non-primary kry does not need null checker
	@Test
	public void testGetCourseBySubjectSuccess(){
		Subject subject = service.createSubject("XXXX");
		assertEquals("AAA", service.getCourseBySubject(subject).get(0).getName());
		assertEquals("BBB", service.getCourseBySubject(subject).get(1).getName());
		assertEquals("CCC", service.getCourseBySubject(subject).get(2).getName());
	}

	@Test
	public void testGetCourseByEducationalLevelSuccess(){
		assertEquals("AAA", service.getCourseByEducationalLevel(EducationLevel.UNIVERSITY).get(0).getName());
		assertEquals("BBB", service.getCourseByEducationalLevel(EducationLevel.UNIVERSITY).get(1).getName());
		assertEquals("CCC", service.getCourseByEducationalLevel(EducationLevel.UNIVERSITY).get(2).getName());
	}

	@Test
	public void testGetAllCoursesSuccess(){
		assertEquals("AAA", service.getAllCourses().get(0).getName());
		assertEquals("BBB", service.getAllCourses().get(1).getName());
		assertEquals("CCC", service.getAllCourses().get(2).getName());
	}

	@Test
	public void testDeleteCourseSuccess() {
		String name = "Delete Success Test";
		// Since there is no return, we only need to verify the times that each Spring database method is called .
		service.deleteCourse(name.toUpperCase());
		verify(courseDao,times(1)).findByName(name.toUpperCase());
		//TODO  Not working
		//verify(courseDao,times(1)).delete(courseDao.findByName(name.trim().toUpperCase()).get());
	}


	@Test
	public void testDeleteCourseNonExisted() {
		String error = null;
		String name = "NON-EXIST";
		try {
			service.deleteCourse(name);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals("No course exists with this name", error);
	}

	@Test
	public void testDeleteCourseNull() {
		String error = "";
		try {
			service.deleteCourse(null);
		}
		catch(IllegalArgumentException e){
			error = e.getMessage();
		}
		assertEquals("Course name cannot be empty!", error);
	}

	@Test
	public void testDeleteCourseEmptyString() {
		String error = "";
		try {
			service.deleteCourse("");
		}
		catch(IllegalArgumentException e){
			error = e.getMessage();
		}
		assertEquals("Course name cannot be empty!", error);
	}



	/*
	*
	* -----------------------------SubjectRepository CRUD Tests-----------------------------------------
	* 									Subtotal: 13 tests
	 */

	// In this test, only createSubject(String) method is used and
	// this method only calls the save method (already covered in setMockOutput() ) in the DAO.
	@Test
	public void testCreateSubjectSuccess() {
		String name = "Create Success Test";
		assertEquals(name.toUpperCase(), service.createSubject(name).getSubjectName());
	}

	@Test
	public void testCreateSubjectAlreadyExits() {
		String error = null;
		String name = "ALREADY EXISTS";
		try {
			service.createSubject(name);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals("Subject with this name already exists.", error);
	}


	@Test
	public void testCreateSubjectEmpty() {
		String name = "";
		String error = null;
		try {
			service.createSubject(name);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals("Subject name cannot be empty!", error);
	}

	@Test
	public void testCreateSubjectNull() {
		String error = null;
		try {
			service.createSubject(null);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals("Subject name cannot be empty!", error);

	}

	@Test
	public void testGetSubjectByNameSuccess() {
		assertEquals("POLITICAL SCIENCE",service.getSubjectByName("GetByName Success Test").getSubjectName());
	}

	@Test
	public void testGetSubjectNonExisted() {
		String error = null;
		String name = "NON-EXIST";
		try {
			service.getSubjectByName(name);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals("No subject exists with this name.", error);
	}
	@Test
	public void testGetSubjectByNameNull() {
		try {
			service.getSubjectByName(null);
		} catch (IllegalArgumentException e) {
			assertEquals("Subject name cannot be empty!", e.getMessage());
		}
	}

	@Test
	public void testGetSubjectByNameEmptyString() {
		try {
			service.getSubjectByName("");
		} catch (IllegalArgumentException e) {
			assertEquals("Subject name cannot be empty!", e.getMessage());
		}
	}

	@Test
	public void testGetAllSubjects() {
		assertEquals(3, service.getAllSubjects().size());
	}

	@Test
	public void testDeleteSubject() {
		String name = "Delete Success Test";
		service.deleteSubject(name);
		// Since there is no return, we only need to verify the times that each Spring database method is called .
		verify(subjectDao,times(1)).findBySubjectName(name.toUpperCase());
		verify(subjectDao,times(1)).delete(subjectDao.findBySubjectName(name.toUpperCase()));
	}

	@Test
	public void testDeleteSubjectNonExisted() {
		String error = null;
		String name = "NON-EXIST";
		try {
			service.deleteSubject(name);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals("No subject exists with this name.", error);
	}

	@Test
	public void testDeleteSubjectNullSubject() {
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
		String error = "";
		try {
			service.deleteSubject("");
		}
		catch(IllegalArgumentException e){
			error = e.getMessage();
		}
		assertEquals("Subject name cannot be empty!", error);
	}

	/*
	*
	* -----------------------------AvailabilityRepository CRUD Tests-----------------------------------------
	* 									Subtotal: 9 tests
	 */

	@Test
	public void testCheckAvailabilityExistsForTutor(){
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.MAY, 27);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019,Calendar.NOVEMBER,11);
		Date endDate = new Date (end.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		Availability availability = new Availability();
		availability.setDayOfWeek(DayOfWeek.MONDAY);
		availability.setStartDate(startDate);
		availability.setEndDate(endDate);
		availability.setStartTime(startTime);
		availability.setEndTime(endTime);
		Tutor tutor = new Tutor();
		Set<Availability> setOfAvailabilities = new HashSet<Availability>();
		setOfAvailabilities.add(availability);
		tutor.setAvailability(setOfAvailabilities);

		//different objects with same attributes
		Calendar start_2 = Calendar.getInstance();
		start_2.set(2019, Calendar.MAY, 27);
		Date startDate_2 = new Date(start_2.getTimeInMillis());
		Time startTime_2  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end_2 = Calendar.getInstance();
		end_2.set(2019,Calendar.NOVEMBER,11);
		Date endDate_2 = new Date (end_2.getTimeInMillis());
		Time endTime_2  = Time.valueOf(LocalTime.parse("11:00"));
		Availability availability_2 = new Availability();
		availability_2.setDayOfWeek(DayOfWeek.MONDAY);
		availability_2.setStartDate(startDate_2);
		availability_2.setEndDate(endDate_2);
		availability_2.setStartTime(startTime_2);
		availability_2.setEndTime(endTime_2);
		assertEquals(true, service.checkAvailabilityExistsForTutor(tutor,availability_2));

		String error = "";
		try {
			service.checkAvailabilityExistsForTutor(null,availability);
		}
		catch(IllegalArgumentException e){
			error = e.getMessage();
		}
		assertEquals("Null tutor.", error);
		try {
			service.checkAvailabilityExistsForTutor(tutor,null);
		}
		catch(IllegalArgumentException e){
			error = e.getMessage();
		}
		assertEquals("Null availability.", error);
		start.set(2019, Calendar.JUNE, 27);
		Date startDate_3 = new Date(start.getTimeInMillis());
		availability_2.setStartDate(startDate_3);
		assertEquals(false, service.checkAvailabilityExistsForTutor(tutor,availability_2));
		// check if the automatic pointer works
		availability.setStartDate(startDate_3);
		assertEquals(true, service.checkAvailabilityExistsForTutor(tutor,availability_2));
	}

	@Test
	public void testCreateAvailabilityNewSuccess(){
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.MAY, 27);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019,Calendar.NOVEMBER,11);
		Date endDate = new Date (end.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		Tutor tutor = new Tutor();
		Availability availabilityCreated = service.createAvailability(tutor,startTime,endTime,startDate,endDate,DayOfWeek.MONDAY);
		assertEquals(startDate.toString(),availabilityCreated.getStartDate().toString());
		assertEquals(endDate.toString(),availabilityCreated.getEndDate().toString());
		assertEquals(startTime.toString(),availabilityCreated.getStartTime().toString());
		assertEquals(endTime.toString(),availabilityCreated.getEndTime().toString());
		assertEquals(DayOfWeek.MONDAY,availabilityCreated.getDayOfWeek());
		verify(userRoleDao,times(1)).save(tutor);
	}

	@Test
	public void testCreateAvailabilityAddSuccess(){
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.MAY, 27);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019,Calendar.NOVEMBER,11);
		Date endDate = new Date (end.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		Availability availability = new Availability();
		availability.setDayOfWeek(DayOfWeek.MONDAY);
		availability.setStartDate(startDate);
		availability.setEndDate(endDate);
		availability.setStartTime(startTime);
		availability.setEndTime(endTime);
		Tutor tutor = new Tutor();
		Set<Availability> setOfAvailabilities = new HashSet<Availability>();
		setOfAvailabilities.add(availability);
		tutor.setAvailability(setOfAvailabilities);

		//A different availability
		Calendar start_2 = Calendar.getInstance();
		start_2.set(2020, Calendar.MAY, 27);
		Date startDate_2 = new Date(start_2.getTimeInMillis());
		Time startTime_2  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end_2 = Calendar.getInstance();
		end_2.set(2020, Calendar.NOVEMBER,11);
		Date endDate_2 = new Date (end_2.getTimeInMillis());
		Time endTime_2  = Time.valueOf(LocalTime.parse("11:00"));
		Availability availabilityCreated = service.createAvailability(tutor,startTime_2,endTime_2,startDate_2,endDate_2,DayOfWeek.MONDAY);
		assertEquals(startDate_2.toString(),availabilityCreated.getStartDate().toString());
		assertEquals(endDate_2.toString(),availabilityCreated.getEndDate().toString());
		assertEquals(startTime_2.toString(),availabilityCreated.getStartTime().toString());
		assertEquals(endTime_2.toString(),availabilityCreated.getEndTime().toString());
		assertEquals(DayOfWeek.MONDAY,availabilityCreated.getDayOfWeek());
		verify(userRoleDao,times(1)).save(tutor);
	}

	@Test
	public void testCreateAvailabilityFail(){
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.MAY, 27);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019,Calendar.NOVEMBER,11);
		Date endDate = new Date (end.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		try {
			service.createAvailability(null,startTime,endTime,startDate,endDate,DayOfWeek.MONDAY);
		}
		catch(IllegalArgumentException e){
			assertEquals("Tutor unspecified!.", e.getMessage());
		}
		Tutor tutor = new Tutor();
		Availability availability = new Availability();
		availability.setDayOfWeek(DayOfWeek.MONDAY);
		availability.setStartDate(startDate);
		availability.setEndDate(endDate);
		availability.setStartTime(startTime);
		availability.setEndTime(endTime);
		Set<Availability> setOfAvailabilities = new HashSet<Availability>();
		setOfAvailabilities.add(availability);
		tutor.setAvailability(setOfAvailabilities);
		try {
			service.createAvailability(tutor,null,endTime,startDate,endDate,DayOfWeek.MONDAY);
		}
		catch(IllegalArgumentException e){
			assertEquals("Availability start time cannot be empty!", e.getMessage());
		}
		try {
			service.createAvailability(tutor,startTime,null,startDate,endDate,DayOfWeek.MONDAY);
		}
		catch(IllegalArgumentException e){
			assertEquals("Availability end time cannot be empty!", e.getMessage());
		}
		try {
			service.createAvailability(tutor,startTime,endTime,null,endDate,DayOfWeek.MONDAY);
		}
		catch(IllegalArgumentException e){
			assertEquals("Availability start date cannot be empty!", e.getMessage());
		}
		try {
			service.createAvailability(tutor,startTime,endTime,startDate,null,DayOfWeek.MONDAY);
		}
		catch(IllegalArgumentException e){
			assertEquals("Availability end date cannot be empty!", e.getMessage());
		}
		try {
			service.createAvailability(tutor,startTime,endTime,startDate,endDate,null);
		}
		catch(IllegalArgumentException e){
			assertEquals("Availability day of week cannot be empty!", e.getMessage());
		}
		end.set(2018,Calendar.NOVEMBER,11);
		Date endDate_2 = new Date (end.getTimeInMillis());
		try {
			service.createAvailability(tutor,startTime,endTime,startDate,endDate_2,DayOfWeek.MONDAY);
		}
		catch(IllegalArgumentException e){
			assertEquals("Availability end date cannot be before event start date!", e.getMessage());
		}
		Time endTime_2  = Time.valueOf(LocalTime.parse("03:00"));
		try {
			service.createAvailability(tutor,startTime,endTime_2,startDate,endDate,DayOfWeek.MONDAY);
		}
		catch(IllegalArgumentException e){
			assertEquals("Availability end time cannot be before event start time!", e.getMessage());
		}
		try {
			service.createAvailability(tutor,startTime,endTime,startDate,endDate,DayOfWeek.MONDAY);
		}
		catch(IllegalArgumentException e){
			assertEquals("This availability already exists for tutor.", e.getMessage());
		}
	}

	@Test
	public void testUpdateAvailability(){
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.MAY, 27);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019,Calendar.NOVEMBER,11);
		Date endDate = new Date (end.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		Availability availability = new Availability();
		availability.setDayOfWeek(DayOfWeek.MONDAY);
		availability.setStartDate(startDate);
		availability.setEndDate(endDate);
		availability.setStartTime(startTime);
		availability.setEndTime(endTime);
		Tutor tutor = new Tutor();
		Set<Availability> setOfAvailabilities = new HashSet<Availability>();
		setOfAvailabilities.add(availability);
		tutor.setAvailability(setOfAvailabilities);
		try{
			service.updateAvailability(tutor,availability,startTime,endTime,startDate,endDate,DayOfWeek.WEDNESDAY);
		}
		catch (IllegalArgumentException e){
			fail();
		}
		try {
			service.updateAvailability(null,availability,startTime,endTime,startDate,endDate,DayOfWeek.WEDNESDAY);
		} catch (IllegalArgumentException e) {
			assertEquals("Null tutor!",e.getMessage());
		}
		try {
			service.updateAvailability(tutor,null,startTime,endTime,startDate,endDate,DayOfWeek.WEDNESDAY);
		} catch (IllegalArgumentException e) {
			assertEquals("Null Availability!",e.getMessage());
		}
		try {
			service.updateAvailability(tutor,availability,null,endTime,startDate,endDate,DayOfWeek.WEDNESDAY);
		} catch (IllegalArgumentException e) {
			assertEquals("Availability start time cannot be empty!",e.getMessage());
		}
		try {
			service.updateAvailability(tutor,availability,startTime,null,startDate,endDate,DayOfWeek.WEDNESDAY);
		} catch (IllegalArgumentException e) {
			assertEquals("Availability end time cannot be empty!",e.getMessage());
		}
		try {
			service.updateAvailability(tutor,availability,startTime,endTime,null,endDate,DayOfWeek.WEDNESDAY);
		} catch (IllegalArgumentException e) {
			assertEquals("Availability start date cannot be empty!",e.getMessage());
		}
		try {
			service.updateAvailability(tutor,availability,startTime,endTime,startDate,null,DayOfWeek.WEDNESDAY);
		} catch (IllegalArgumentException e) {
			assertEquals("Availability end date cannot be empty!",e.getMessage());
		}
		try {
			service.updateAvailability(tutor,availability,startTime,endTime,startDate,endDate,null);
		} catch (IllegalArgumentException e) {
			assertEquals("Availability day of week cannot be empty!",e.getMessage());
		}
		end.set(2018,Calendar.NOVEMBER,11);
		Date endDate_2 = new Date (end.getTimeInMillis());
		try {
			service.updateAvailability(tutor,availability,startTime,endTime,startDate,endDate_2,DayOfWeek.WEDNESDAY);
		} catch (IllegalArgumentException e) {
			assertEquals("Availability end date cannot be before event start date!",e.getMessage());
		}
		Time endTime_2  = Time.valueOf(LocalTime.parse("03:00"));
		try {
			service.updateAvailability(tutor,availability,startTime,endTime_2,startDate,endDate,DayOfWeek.WEDNESDAY);
		} catch (IllegalArgumentException e) {
			assertEquals("Availability end time cannot be before event start date!",e.getMessage());
		}
		Calendar startNew = Calendar.getInstance();
		start.set(2020, Calendar.MAY, 27);
		Date startDateNew = new Date(startNew.getTimeInMillis());
		Time startTimeNew  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar endNew = Calendar.getInstance();
		end.set(2020,Calendar.NOVEMBER,11);
		Date endDateNew = new Date (endNew.getTimeInMillis());
		Time endTimeNew  = Time.valueOf(LocalTime.parse("11:00"));
		Availability availabilityNew = new Availability();
		availability.setDayOfWeek(DayOfWeek.MONDAY);
		availability.setStartDate(startDate);
		availability.setEndDate(endDate);
		availability.setStartTime(startTime);
		availability.setEndTime(endTime);
		Tutor tutorNew = new Tutor();
		Set<Availability> setOfAvailabilitiesNew = new HashSet<Availability>();
		setOfAvailabilities.add(availabilityNew);
		tutorNew.setAvailability(setOfAvailabilitiesNew);
		try {
			service.updateAvailability(tutorNew,availabilityNew,startTimeNew,endTimeNew,startDateNew,endDateNew,DayOfWeek.WEDNESDAY);
		} catch (IllegalArgumentException e) {
			assertEquals("Invalid availability/tutor pair.",e.getMessage());
		}
	}



	@Test
	public void testGetAllAvailabilities(){
		try {
			service.getAllAvailabilities();
		}
		catch(IllegalArgumentException e) {
			fail();
		}
	}

	@Test
	public void testGetAllAvailabilitiesByTutorId(){
		try {
			service.getAllAvailabilitiesByTutorId(888);
		}
		catch(IllegalArgumentException e) {
			fail();
		}
		try {
			service.getAllAvailabilitiesByTutorId(999);
		} catch (IllegalArgumentException e) {
			assertEquals("The id provided does not refer to a tutor",e.getMessage());
		}
	}

	@Test
	public void testGetAvailabilityById(){
		try {
			service.getAvailabilityById(37);
		}
		catch(IllegalArgumentException e) {
			fail();
		}
		try {
			service.getAvailabilityById(null);
		} catch (IllegalArgumentException e) {
			assertEquals("Id cannot be null",e.getMessage());
		}
		try {
			service.getAvailabilityById(38);
		} catch (IllegalArgumentException e) {
			assertEquals("This id value for an availability does not exist",e.getMessage());
		}
	}

	@Test
	public void testDeleteAvailability(){
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.MAY, 27);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019,Calendar.NOVEMBER,11);
		Date endDate = new Date (end.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		Availability availability = new Availability();
		availability.setDayOfWeek(DayOfWeek.MONDAY);
		availability.setStartDate(startDate);
		availability.setEndDate(endDate);
		availability.setStartTime(startTime);
		availability.setEndTime(endTime);
		Tutor tutor = new Tutor();
		Set<Availability> setOfAvailabilities = new HashSet<Availability>();
		setOfAvailabilities.add(availability);
		tutor.setAvailability(setOfAvailabilities);
		try {
			service.deleteAvailability(tutor,availability);
		}
		catch(IllegalArgumentException e) {
			fail();
		}
		try {
			service.deleteAvailability(null,availability);
		} catch (IllegalArgumentException e) {
			assertEquals("tutor is null",e.getMessage());
		}
		try {
			service.deleteAvailability(tutor,null);
		} catch (IllegalArgumentException e) {
			assertEquals("Availability does not exist",e.getMessage());
		}
		CoolearnUser user = new CoolearnUser();
		//TODO verify that it is an emailAddress
		user.setEmailAddress("student@gmail.com");
		user.setFirstName("Jacob");
		user.setLastName("Silcoff");
		//TODO make the creation more secure by hashing password
		user.setPassword("pass123");
		Subject subject = new Subject();
		subject.setSubjectName("SOFTWARE ENGINEERING");
		Tutor tutor_2 = new Tutor();
		tutor_2.setUser(user);
		try {
			service.deleteAvailability(tutor_2,availability);
		} catch (IllegalArgumentException e) {
			assertEquals("tutor has no availabilities",e.getMessage());
		}
		start.set(2020, Calendar.MAY, 27);
		end.set(2020,Calendar.NOVEMBER,11);
		Date startDate_2 = new Date(start.getTimeInMillis());
		Date endDate_2 = new Date (end.getTimeInMillis());
		Availability availability_2 = new Availability();
		availability_2.setDayOfWeek(DayOfWeek.MONDAY);
		availability_2.setStartDate(startDate_2);
		availability_2.setEndDate(endDate_2);
		availability_2.setStartTime(startTime);
		availability_2.setEndTime(endTime);
		try {
			service.deleteAvailability(tutor,availability);
		} catch (IllegalArgumentException e) {
			assertEquals("tutor does not have this availability",e.getMessage());
		}

	}

	/*
	*
	* -----------------------------RoomRepository CRUD Tests-----------------------------------------
	* 									Subtotal: 4 tests
	 */

	@Test
	public void testCreatRoom(){
		Room room  = service.createRoom(1, RoomSize.SMALL);
		Integer rn = 1;
		assertEquals(rn, room.getRoomNumber());

		String error = "";
		try {
			service.createRoom(1, null);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals("A room size must be specified", error);

		try {
			service.createRoom(0, null);
		} catch (IllegalArgumentException e) {
			assertEquals("Invalid room number", e.getMessage());
		}
	}

	@Test
	public void testGetRoom(){
		Room room = service.getRoom(1);
		Integer rn = 1;
		assertEquals(rn,room.getRoomNumber());
		assertEquals(RoomSize.LARGE,room.getRoomsize());
		try {
			service.getRoom(0);
		} catch (IllegalArgumentException e) {
			assertEquals("Invalid room number", e.getMessage());
		}

		//TODO not ACTUALLY covered, don't know why
		try {
			service.getRoom(2);
		} catch (NoSuchElementException e) {
			assertEquals("No room with this number", e.getMessage());
		}

	}

	@Test
	public void testGetRoomBySize(){
		try {
			service.getRoomBySize(null);
		} catch (IllegalArgumentException e) {
			assertEquals("A room size must be specified", e.getMessage());
		}
		Integer rn = 1;
		assertEquals(rn,service.getRoomBySize(RoomSize.LARGE).get(0).getRoomNumber());
		assertEquals(RoomSize.LARGE,service.getRoomBySize(RoomSize.LARGE).get(0).getRoomsize());
		try {
			service.getRoomBySize(RoomSize.SMALL);
		} catch (IllegalArgumentException e) {
			assertEquals("No room of such size", e.getMessage());
		}
	}

	@Test
	public void testGetAllRooms(){
		List<Room> rooms = service.getAllRooms();
		assertEquals(1,rooms.size());
		assertEquals(RoomSize.LARGE,rooms.get(0).getRoomsize());
		Integer rn = 1;
		assertEquals(rn,rooms.get(0).getRoomNumber());
	}

	/*
	*
	* -----------------------------SpecificCourseRepository CRUD Tests-----------------------------------------
	* 									Subtotal: 6 tests
	 */

	@Test
	public void createSpecificCourse(){
		Course course = new Course();
		course.setName("ALREADY EXISTS");
		// SET UP TUTOR
		Tutor tutor = new Tutor();
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.MAY, 27);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019,Calendar.NOVEMBER,11);
		Date endDate = new Date (end.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		Availability availability = new Availability();
		availability.setDayOfWeek(DayOfWeek.MONDAY);
		availability.setStartDate(startDate);
		availability.setEndDate(endDate);
		availability.setStartTime(startTime);
		availability.setEndTime(endTime);
		Set<Availability> setOfAvailabilities = new HashSet<Availability>();
		setOfAvailabilities.add(availability);
		tutor.setAvailability(setOfAvailabilities);

		try {
			service.createSpecificCourse(15, course, tutor);
		}
		catch(IllegalArgumentException e) {
			fail();
		}

		try {
			service.createSpecificCourse(0, course, tutor);
		}
		catch(IllegalArgumentException e) {
			assertEquals("The hourly rate for a course should be greater or equal to 12.5" ,e.getMessage());
		}
		try {
			service.createSpecificCourse(15, null, tutor);
		}
		catch(IllegalArgumentException e) {
			assertEquals("The course should be specified" ,e.getMessage());
		}
		try {
			service.createSpecificCourse(15, course, null);
		}
		catch(IllegalArgumentException e) {
			assertEquals("The tutor should be specified" ,e.getMessage());
		}
		course.setName("NON-EXIST");
		try {
			service.createSpecificCourse(15, course, tutor);
		}
		catch(IllegalArgumentException e) {
			assertEquals("The course with this name does not exist" ,e.getMessage());
		}
	}

	@Test
	public void testGetAllSpecificCourses(){
		try {
			service.getAllSpecificCourses();
		}
		catch(IllegalArgumentException e) {
			fail();
		}
	}

	@Test
	public void getAllSpecificCoursesByCourse(){
		Course course = new Course();
		course.setName("ALREADY EXISTS");
		try {
			service.getAllSpecificCoursesByCourse(course);
		}
		catch(IllegalArgumentException e) {
			fail();
		}
	}

	@Test
	public void testGetSpecificCourse(){
		try {
			service.getSpecificCourse(90);
		}
		catch(IllegalArgumentException e) {
			fail();
		}
		try {
			service.getSpecificCourse(-9);
		}
		catch(IllegalArgumentException e) {
			assertEquals("ID must be greater than 0" ,e.getMessage());
		}
	}

	@Test
	public void testGetSpecificCourseByTutor(){
		// SET UP TUTOR
		Tutor tutor = new Tutor();
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.MAY, 27);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019,Calendar.NOVEMBER,11);
		Date endDate = new Date (end.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		Availability availability = new Availability();
		availability.setDayOfWeek(DayOfWeek.MONDAY);
		availability.setStartDate(startDate);
		availability.setEndDate(endDate);
		availability.setStartTime(startTime);
		availability.setEndTime(endTime);
		Set<Availability> setOfAvailabilities = new HashSet<Availability>();
		setOfAvailabilities.add(availability);
		tutor.setAvailability(setOfAvailabilities);
		try {
			service.getSpecificCoursesByTutor(tutor);
		}
		catch(IllegalArgumentException e) {
			fail();
		}
		try {
			service.getSpecificCoursesByTutor(null);
		}
		catch(IllegalArgumentException e) {
			assertEquals("Tutor cannot be null" ,e.getMessage());
		}
	}

	@Test
	public void testUpdatePriceOfSpecificCourse(){
		Course course = new Course();
		course.setName("ALREADY EXISTS");
		// SET UP TUTOR
		Tutor tutor = new Tutor();
		Calendar start = Calendar.getInstance();
		start.set(2019, Calendar.MAY, 27);
		Date startDate = new Date(start.getTimeInMillis());
		Time startTime  = Time.valueOf(LocalTime.parse("09:00"));
		Calendar end = Calendar.getInstance();
		end.set(2019,Calendar.NOVEMBER,11);
		Date endDate = new Date (end.getTimeInMillis());
		Time endTime  = Time.valueOf(LocalTime.parse("11:00"));
		Availability availability = new Availability();
		availability.setDayOfWeek(DayOfWeek.MONDAY);
		availability.setStartDate(startDate);
		availability.setEndDate(endDate);
		availability.setStartTime(startTime);
		availability.setEndTime(endTime);
		Set<Availability> setOfAvailabilities = new HashSet<Availability>();
		setOfAvailabilities.add(availability);
		tutor.setAvailability(setOfAvailabilities);
		SpecificCourse specificCourse = new SpecificCourse();
		specificCourse.setCourse(course);
		specificCourse.setHourlyRate(15);
		specificCourse.setTutor(tutor);
		try {
			service.updatePriceOfSpecificCourse(17,specificCourse);
		}
		catch(IllegalArgumentException e) {
			fail();
		}
		try {
			service.updatePriceOfSpecificCourse(10,specificCourse);
		}
		catch(IllegalArgumentException e) {
			assertEquals("Hourly rate must be greater than 12.5" ,e.getMessage());
		}
		try {
			service.updatePriceOfSpecificCourse(17,null);
		}
		catch(IllegalArgumentException e) {
			assertEquals("There must be a course to be updated" ,e.getMessage());
		}


	}

	/*
	*
	* -----------------------------CooLearnUserRepository CRUD Tests-----------------------------------------
	* 									Subtotal: 3 tests
	 */

	@Test
	public void testCreateCooLearnUser(){
		try {
			service.createCoolearnUser("student@gmail.com","Jacob", "Silcoff", "pass123");
		} catch (IllegalArgumentException e) {
			fail();
		}
		try {
			service.createCoolearnUser("","Jacob", "Silcoff", "pass123");
		}
		catch(IllegalArgumentException e) {
			assertEquals("Invalid email address!" ,e.getMessage());
		}
		try {
			service.createCoolearnUser("ALREADY EXISTS","Jacob", "Silcoff", "pass123");
		}
		catch(IllegalArgumentException e) {
			assertEquals("Email address is already taken!" ,e.getMessage());
		}
		try {
			service.createCoolearnUser("student@gmail.com","", "Silcoff", "pass123");
		}
		catch(IllegalArgumentException e) {
			assertEquals("Invalid first name!" ,e.getMessage());
		}
		try {
			service.createCoolearnUser("student@gmail.com","Jacob", "", "pass123");
		}
		catch(IllegalArgumentException e) {
			assertEquals("Invalid last name!" ,e.getMessage());
		}
		try {
			service.createCoolearnUser("student@gmail.com","Jacob", "Silcoff", "");
		}
		catch(IllegalArgumentException e) {
			assertEquals("Invalid password!" ,e.getMessage());
		}
	}

	@Test
	public void testGetCooLearnUser(){
		try {
			service.getCoolearnUser("success.test@gmail.com");
		} catch (IllegalArgumentException e) {
			fail();
		}
		try {
			service.getCoolearnUser(null);
		}
		catch(IllegalArgumentException e) {
			assertEquals("Email address is invalid!" ,e.getMessage());
		}
		try {
			service.getCoolearnUser("random");
		}
		catch(IllegalArgumentException e) {
			assertEquals("No user with this email address!" ,e.getMessage());
		}
	}
	@Test
	public void testGetAllCoolearnUsers(){
		try {
			service.getAllCoolearnUsers();
		} catch (IllegalArgumentException e) {
			fail();
		}
	}

	/*
	*
	* -----------------------------UserRoleRepository CRUD Tests-----------------------------------------
	* 									Subtotal: 4 tests
	 */

	@Test
	public void testCreateUserRole(){
		// create CooLearnUser
		CoolearnUser user = new CoolearnUser();
		//TODO verify that it is an emailAddress
		user.setEmailAddress("student@gmail.com");
		user.setFirstName("Jacob");
		user.setLastName("Silcoff");
		//TODO make the creation more secure by hashing password
		user.setPassword("pass123");

		// crate set of courses
		Subject subject = new Subject();
		subject.setSubjectName("SOFTWARE ENGINEERING");
		Course course_1 = new Course();
		course_1.setName("AAA");
		course_1.setEducationalLevel(EducationLevel.UNIVERSITY);
		course_1.setSubject(subject);
		Course course_2 = new Course();
		course_2.setName("BBB");
		course_2.setEducationalLevel(EducationLevel.UNIVERSITY);
		course_2.setSubject(subject);
		Course course_3 = new Course();
		course_3.setName("CCC");
		course_3.setEducationalLevel(EducationLevel.UNIVERSITY);
		course_3.setSubject(subject);
		Set<Course> courseSet = new HashSet<>();
		courseSet.add(course_1);
		courseSet.add(course_2);
		courseSet.add(course_3);
		try {
			service.createUserRole(user,TUTOR_DISCRIMINATOR,courseSet);
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		}

		try {
			service.createUserRole(user,STUDENT_DISCRIMINATOR,courseSet);
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		}

		try {
			service.createUserRole(null,TUTOR_DISCRIMINATOR,courseSet);
		} catch (IllegalArgumentException e) {
			assertEquals("CoolearnUser cannot be null",e.getMessage());
		}

		try {
			service.createUserRole(user,null,courseSet);
		} catch (IllegalArgumentException e) {
			assertEquals("User role needs to be specified",e.getMessage());
		}

		try {
			service.createUserRole(user,TUTOR_DISCRIMINATOR,null);
		} catch (IllegalArgumentException e) {
			assertEquals("A tutor should be created with a list of courses",e.getMessage());
		}

		try {
			service.createUserRole(user,"popo",null);
		} catch (IllegalArgumentException e) {
			assertEquals("User role should be either tutor or student",e.getMessage());
		}
	}

	@Test
	public void testGetUserRole(){
		try {
			service.getUserRole(1234);
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		}
		try {
			service.getUserRole(0);
		} catch (IllegalArgumentException e) {
			assertEquals("The user role does not exist for this id.",e.getMessage());
		}

	}

	@Test
	public void testGetUserRolesByCoolearnUser(){
		try{
			service.getUserRolesByCoolearnUser(user_1);
		}
		catch (IllegalArgumentException e){
			fail(e.getMessage());
		}
		try{
			service.getUserRolesByCoolearnUser(user_2);
		}
		catch (IllegalArgumentException e){
			assertEquals("There are no user roles associated to this CoolearnUser!",e.getMessage());
		}
	}

	@Test
	public void testGetAllUserRoles(){
		try {
			service.getAllUserRoles();
		}
		catch (IllegalArgumentException e){
			fail(e.getMessage());
		}
	}

	/*
	*
	* -----------------------------ReviewRepository CRUD Tests-----------------------------------------
	* 									Subtotal: 7 tests
	 */
	@Test
	public void testCreateReview(){
		Session s = new Session();
		s.setId(100);
		try {
			service.createReview(tutor,session_1,"Good!",(float)4.5);
		}
		catch (IllegalArgumentException e){
			fail(e.getMessage());
		}

		try {
			service.createReview(student,session_1,"Good!",(float)4.5);
		}
		catch (IllegalArgumentException e){
			fail(e.getMessage());
		}

		try {
			service.createReview(null,session_1,"Good!",(float)4.5);
		}
		catch (IllegalArgumentException e){
			assertEquals("Recipient of comment cannot be null.",e.getMessage());
		}

		try {
			service.createReview(tutor,null,"Good!",(float)4.5);
		}
		catch (IllegalArgumentException e){
			assertEquals("Session cannot be null.",e.getMessage());
		}

		try {
			service.createReview(tutor,session_1,"",(float)4.5);
		}
		catch (IllegalArgumentException e){
			assertEquals("Comment cannot be null.",e.getMessage());
		}

		try {
			service.createReview(tutor,session_1,"Good!",(float)100000);
		}
		catch (IllegalArgumentException e){
			assertEquals("Rating must be between 0 and 5.",e.getMessage());
		}

		try {
			service.createReview(tutor,s,"Good!",(float)4.5);
		}
		catch (IllegalArgumentException e){
			assertEquals("Review of a tutor must be linked to a session associated with that tutor.",e.getMessage());
		}

		try {
			service.createReview(student,s,"Good!",(float)4.5);
		}
		catch (IllegalArgumentException e){
			assertEquals("Review of a student must be linked to a session associated with that student.",e.getMessage());
		}

	}

	@Test
	public void testGetAllReviews(){
		try{
			service.getAllReviews();
		}
		catch (IllegalArgumentException e){
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetAllReviewsByUserRole(){
		try{
			service.getAllReviewsByUserRole(role_1);
		}
		catch (IllegalArgumentException e){
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetAllReviewsBySession(){
		try{
			service.getAllReviewsBySession(session_1);
		}
		catch (IllegalArgumentException e){
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetReviewById(){
		try{
			service.getReviewById(1);
		}
		catch (IllegalArgumentException e){
			fail(e.getMessage());
		}

		try{
			service.getReviewById(null);
		}
		catch (IllegalArgumentException e){
			assertEquals("Id cannot be null",e.getMessage());
		}

		try{
			service.getReviewById(2);
		}
		catch (IllegalArgumentException e){
			assertEquals("This id value for a review does not exist",e.getMessage());
		}
	}

	@Test
	public void testUpdateReview(){
		try{
			service.updateReview(1,"Perfect!",(float)4.9);
		}
		catch (IllegalArgumentException e){
			fail(e.getMessage());
		}

//		try{
//			service.updateReview(3,"Perfect!",(float)4.9);
//		}
//		catch (IllegalArgumentException e){
//			fail(e.getMessage());
//		}

		try{
			service.updateReview(1,"",(float)4.9);
		}
		catch (IllegalArgumentException e){
			assertEquals("The comment cannot be empty",e.getMessage());
		}

		try{
			service.updateReview(1,"eeeee",(float)100000);
		}
		catch (IllegalArgumentException e){
			assertEquals("Rating must be between 0 and 5.",e.getMessage());
		}
	}

	@Test
	public void testDeleteReview(){
		Review review = new Review();
		review.setId(7);
		try{
			service.deleteReview(review);
		}
		catch (IllegalArgumentException e){
			fail(e.getMessage());
		}
	}
}