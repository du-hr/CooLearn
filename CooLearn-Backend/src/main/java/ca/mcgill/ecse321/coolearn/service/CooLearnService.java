package ca.mcgill.ecse321.coolearn.service;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.mcgill.ecse321.coolearn.dao.*;
import ca.mcgill.ecse321.coolearn.model.*;


import static ca.mcgill.ecse321.coolearn.service.StringLiterals.STUDENT_DISCRIMINATOR;
import static ca.mcgill.ecse321.coolearn.service.StringLiterals.TUTOR_DISCRIMINATOR;
import static ca.mcgill.ecse321.coolearn.service.StringLiterals.MINIMUM_HOURLY_RATE;
@Service
public class CooLearnService {
	@Autowired
	SubjectRepository subjectRepository;
	@Autowired
	AvailabilityRepository availabilityRepository;
	@Autowired
	CoolearnUserRepository coolearnUserRepository;
	@Autowired
	CourseRepository courseRepository;
	@Autowired
	UserRoleRepository userRoleRepository;
	@Autowired
	ReviewRepository reviewRepository;
	@Autowired
	SessionRepository sessionRepository;
	@Autowired
	RoomRepository roomRepository;
	@Autowired
	SpecificCourseRepository SpecificCourseRepository;

	@Transactional
	public Session createSession(List<Student> students, Tutor tutor, Course course, Time startTime, Time endTime, Date date) {
		//When we create a session, the room is not assigned. At confirmation, we will find a room to assign
		String error = "";
		if (students == null || students.size() == 0) {
			error += "A session must be associated with at least one student.";
		}
		if(tutor == null) {
			error += "Tutor cannot be null. ";
		}
		if(course == null) {
			error += "Course cannot be null. ";
		}
		if(startTime == null) {
			error += "Start time cannot be null. ";
		}
		if(endTime == null) {
			error += "End time cannot be null. ";
		}
		if(endTime.before(startTime)) {
			error += "End time cannot be before start time. ";
		}
		if(date == null ) {
			error += "Date cannot be null.";
		}
		error = error.trim();
		if(error.length() > 0) {
			throw new IllegalArgumentException(error);
		}
		boolean isTutorTeachCourse = false;
		for (SpecificCourse sc: getSpecificCoursesByTutor(tutor)){
			if(sc.getCourse().getName().equals(course.getName())){
				isTutorTeachCourse=true;
			}
		}
		if (isTutorTeachCourse == false) {
			throw new IllegalArgumentException("A session must have a course that the Tutor is qualified to tutor");
		}

		if (!checkSessionFitsForTutor(tutor,startTime,endTime,date)) {
			throw new IllegalArgumentException("The specified tutor is not available at that time");
		}
		//TODO: add rooms permanently to the database once we set it to none
		if(getAllRooms().size() == 0) {
			for(int i=1; i <= 13; i++) {
				if(i <= 10) {
					createRoom(i, RoomSize.SMALL);
				} else {
					createRoom(i, RoomSize.LARGE);
				}
			}
		}
		Session session = new Session();
		session.setDate(date);
		session.setStartTime(startTime);
		session.setEndTime(endTime);
		session.setCourse(course);
		session.setTutor(tutor);
		session.setStatus(RequestStatus.PENDING);
		sessionRepository.save(session);

		Set<Student> studentSet = new HashSet<Student>();
		for (Student s : students) {
			studentSet.add(s);
			addSessionToStudent(s, session);
		}
		session.setStudent(studentSet);

		sessionRepository.save(session);
		return session;
	}

	private boolean checkSessionFitsForTutor(Tutor tutor, Time startTime, Time endTime, Date date) {
		SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEE"); // the day of the week spelled out completely
		System.out.println("1");
		for(Availability a : getAllAvailabilitiesByTutorId(tutor.getId())){
			//Check if start and end time are in range
			System.out.println("2");
			if(!( (startTime.before(a.getStartTime())) || (startTime.after(a.getEndTime()))) ) {
				System.out.println("3");
				//throw new IllegalArgumentException("Incorrect start time: request is for " + startTime.toString() + " and availability starts at " + a.getStartTime().toString() + "and end at " + a.getEndTime().toString());
				if(!((endTime.before(a.getStartTime())) || (endTime.after(a.getEndTime())))) {
					System.out.println("4");
					//throw new IllegalArgumentException("Incorrect end time: request is for " + endTime.toString() + " and availability end at " + a.getEndTime().toString());
					//At this point we know that they are both in the range of the professor availability
					if(startTime.before(endTime)) {
						System.out.println("5");
						//throw new IllegalArgumentException("End time should occur after start time");
						//At this stage, we know that start time and end time for a session are within the range
						//and in the correct order.
						String dayOfWeek = simpleDateformat.format(date);
						dayOfWeek = getDayOfWeekFromString(dayOfWeek);
						if(dayOfWeek.equals(a.getDayOfWeek().toString())) {
							System.out.println("6. Date for session: " + date + " start date: " + a.getStartDate() + " end date: " +a.getEndDate() + " equal start " + date.toString().equals(a.getStartDate().toString()));
							//throw new IllegalArgumentException("Incorrect day of work: availability is on a " + a.getDayOfWeek().toString() + "but request is for a " + dayOfWeek);
							//At this point, we have checked that date corresponds to a day where tutor works 
							//Check if date is within range
							if(date.toString().equals(a.getStartDate().toString()) || date.toString().equals(a.getEndDate().toString()) || !(date.before(a.getStartDate()) ||  date.after(a.getEndDate()))) {
								System.out.println("7");
								//throw new IllegalArgumentException("Date is not in interval: start date is " + a.getStartDate() + " and ends on " + a.getEndDate() + " but session is for " + date.toString());
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}


	private String getDayOfWeekFromString(String dayOfWeek) {
		DayOfWeek day = null;
		switch (dayOfWeek.toUpperCase()) {
		case "MON":
			day = DayOfWeek.MONDAY;
			break;
		case "TUE":
			day = DayOfWeek.TUESDAY;
			break;
		case "WED":
			day = DayOfWeek.WEDNESDAY;
			break;
		case "THU":
			day = DayOfWeek.THURSDAY;
			break;
		case "FRI":
			day = DayOfWeek.FRIDAY;
			break;
		case "SAT":
			day = DayOfWeek.SATURDAY;
			break;
		case "SUN":
			day = DayOfWeek.SUNDAY;
			break;
		default:
			throw new IllegalArgumentException("Incorrect date passed: passed" + dayOfWeek.toUpperCase());
		}
		return day.toString();
	}

	//Need to test all possibilities
	@Transactional
	public boolean addSessionToStudent(Student s, Session session) {
		if(getAllSessionsByStudent(s).isEmpty()) {
			Set<Session> sessions = new HashSet<Session>();
			sessions.add(session);
			s.setSession(sessions);
			userRoleRepository.save(s);
			return true;
		} else if(!getAllSessionsByStudent(s).isEmpty() && !getAllSessionsByStudent(s).contains(session)) {
			List<Session> listsessions = getAllSessionsByStudent(s);
			Set<Session> sessions = new HashSet<Session>(listsessions);
			sessions.add(session);
			s.setSession(sessions);
			userRoleRepository.save(s);
			return true;
		}
		return false;
	}

	@Transactional
	public boolean DenySession(Session session){
		if (session==null){
			throw new IllegalArgumentException("This session does not exist");
		}
		setSessionStatus(session, RequestStatus.REJECTED);
		sessionRepository.save(session);
		return true;
	}

	@Transactional
	public boolean AcceptSession(Session session){
		if (session==null){
			throw new IllegalArgumentException("This session does not exist");
		}
		if(session.getStudent().size()==1){
			assignRoomToSession(session, RoomSize.SMALL);
		}else{
			assignRoomToSession(session, RoomSize.LARGE);
		}
		setSessionStatus(session, RequestStatus.ACCEPTED);
		sessionRepository.save(session);
		return true;
	}

	@Transactional
	public boolean assignRoomToSession(Session session, RoomSize rSize){
		Room roomToAssign=null;
		for (Room r: getRoomBySize(rSize)){
			if(isRoomAvailable(r, session.getStartTime(), session.getEndTime(), session.getDate())){
				roomToAssign =r;
				break;
			}
		}
		if(roomToAssign == null){
			throw new IllegalArgumentException("There are no room available of that size");
		}
		session.setRoom(roomToAssign);
		roomToAssign.getSession().add(session);
		roomRepository.save(roomToAssign);
		sessionRepository.save(session);
		return true;

	}

	@Transactional
	public boolean isRoomAvailable(Room room, Time startTime, Time endTime, Date date){
		for(Session s : getAllSessionsByRoom(room)){
			if(date.toString().equals(s.getDate().toString())){
				if( ( (startTime.after(s.getStartTime()) ) && (startTime.before(s.getEndTime())) ) 
				|| ( (endTime.after(s.getStartTime()) ) && (endTime.before(s.getEndTime())) )
				|| ( (startTime.equals(s.getStartTime()) ) && (endTime.equals(s.getEndTime())) )){
					return false;
				}
			}
		}
		return true;
	}

	@Transactional
	public List<Session> getAllSessions() {
		return toList(sessionRepository.findAll());
	}

	@Transactional
	public List<Session> getAllSessionsByStudent(Student s) {
		return toList(sessionRepository.findByStudent(s));
	}

	@Transactional
	public List<Session> getAllSessionsByTutor(Tutor t) {
		return toList(sessionRepository.findByTutor(t));
	}

	@Transactional
	public Session setSessionStatus(Session s, RequestStatus r) {
		if (r == null || s == null) {
			throw new IllegalArgumentException("Cannot use null values to set session status");
		}
		s.setStatus(r);
		sessionRepository.save(s);
		return s;
	}

	@Transactional
	public List<Session> getAllSessionsByUserAndStatus(UserRole ur, RequestStatus status) {
		if (ur instanceof Tutor)
			return toList(sessionRepository.findByTutorAndStatus(ur, status));
		else if (ur instanceof Student)
			return toList(sessionRepository.findByStudentAndStatus(ur, status));
		else
			throw new IllegalArgumentException("Only Students and Tutors can have sessions");
	}

//	@Transactional
//	public List<Session> getAllSessionsByDateAfter(Date date) {
//		return toList(sessionRepository.findByDateAfter(date));
//	}
//
//	@Transactional
//	public List<Session> getAllSessionsByDateAfterAndStatus(Date date, RequestStatus s) {
//		return toList(sessionRepository.findByDateAfterAndStatus(date, s));
//	}
	@Transactional
	public List<Session> getAllSessionsByTutorAndDateBeforeAndEndTimeBeforeAndStatus(UserRole tutor, Date date, Time endTime, RequestStatus status){
		if (tutor instanceof Student){
			throw new IllegalArgumentException("The UserRole must be a Tutor");
		}
		return toList(sessionRepository.findByTutorAndDateBeforeAndEndTimeBeforeAndStatus(tutor, date, endTime, status));
	}


	@Transactional
	public List<Session> getAllSessionsByUserAndDateAfterAndStatus(UserRole usr, Date d, RequestStatus s) {
		if (usr instanceof Tutor) {
			return toList(sessionRepository.findByTutorAndDateAfterAndStatus(usr, d, s));
		}
		else if (usr instanceof Student) {
			return toList(sessionRepository.findByTutorAndDateAfterAndStatus(usr,d,s));
		}
		else {
			throw new IllegalArgumentException("Only Students and Tutors can have sessions");
		}
	}

	@Transactional
	public List<Session> getAllSessionsByRoom(Room r){
		if(r == null) {
			throw new IllegalArgumentException("Room cannot be null!");
		}
		return toList(sessionRepository.findByRoom(r));
	}

	@Transactional
	public Session getSessionById(Integer id) {
		if(id == null) {
			throw new IllegalArgumentException("Id cannot be null");
		} else if(!sessionRepository.existsById(id)) {
			throw new IllegalArgumentException("Session with this id doesn't exist!");
		}
		Optional<Session> session = sessionRepository.findById(id);
		return session.get() ;
	}

//	@Transactional
//	public void deleteSession(int id) {
//		//Delete from userRole first, then delete session
//		sessionRepository.deleteById(id);
//	}
//	
	

	@Transactional
	public void deleteSession(Session s) {
		//Delete from userRole first, then delete session
		if(s == null) {
			throw new IllegalArgumentException("Session cannot be null");
		}
		Set<Student> students = s.getStudent();
		for(Student student: students) {
			student.getSession().remove(s);
			userRoleRepository.save(student);
		}
		sessionRepository.delete(s);
	}

	@Transactional
	public Review createReview(UserRole recipient, Session session, String comment, float rating) {
		String error = "";
		if(recipient == null) {
			error += "Recipient of comment cannot be null. ";
		}
		if(session == null) {
			error += "Session cannot be null. ";
		}
		if(comment == null || comment.trim().length() == 0) {
			error += "Comment cannot be null. ";
		}
		if (rating < 0 || rating > 5) {
			error += "Rating must be between 0 and 5.";
		}
		error = error.trim();
		if(error.length() > 0) {
			throw new IllegalArgumentException(error);
		}
		boolean hasSession = false;
		if(recipient instanceof Student) {
			List<Session> listOfStudentSessions = getAllSessionsByStudent((Student) recipient);
			for(Session s: listOfStudentSessions) {
				if(s.getId().equals(session.getId())) {
					hasSession = true;
				}
			}
			if(!hasSession) {
				throw new IllegalArgumentException("Review of a student must be linked to a session associated with that student.");
			}
		} else {
			List<Session> listOfTutorSessions = getAllSessionsByTutor((Tutor) recipient);
			for(Session s: listOfTutorSessions) {
				if(s.getId().equals(session.getId())) {
					hasSession = true;
				}
			}
			if(!hasSession) {
				throw new IllegalArgumentException("Review of a tutor must be linked to a session associated with that tutor.");
			}
		}

		Review review = new Review();
		review.setUserRole(recipient);
		review.setSession(session);
		review.setComment(comment);
		review.setRating(rating);
		reviewRepository.save(review);
		return review;
	}

	@Transactional
	public List<Review> getAllReviews() {
		return toList(reviewRepository.findAll());
	}

	@Transactional
	public List<Review> getAllReviewsByUserRole(UserRole usr) {
		return toList(reviewRepository.findByUserRole(usr));
	}

	@Transactional
	public Review getReviewBySessionAndStudent(Session session, UserRole userRole){
		return reviewRepository.findBySessionAndUserRole(session, userRole);
	}

	@Transactional
	public List<Review> getAllReviewsBySession(Session session){
		return toList(reviewRepository.findBySession(session));
	}

	@Transactional
	public void updateReview(Integer rev_id, String newComment, Float newRating) {
		// Redundant
//		if(getReviewById(rev_id)==null){
//			throw new IllegalArgumentException("There are no review with this id");
//		}
		if(newComment==null || newComment.trim().length()==0){
			throw new IllegalArgumentException("The comment cannot be empty");
		}
		if(newRating<0 || newRating >5){
			throw new IllegalArgumentException("Rating must be between 0 and 5.");
		}
		Review r = getReviewById(rev_id);
		r.setComment(newComment);
		r.setRating(newRating);
		reviewRepository.save(r);
	}

	@Transactional
	public Review getReviewById(Integer id) {
		if(id == null) {
			throw new IllegalArgumentException("Id cannot be null");
		}
		if(!reviewRepository.existsById(id)) {
			throw new IllegalArgumentException("This id value for a review does not exist");
		}
		Optional<Review> review = reviewRepository.findById(id);
		return review.get();
	}

	@Transactional
	public void deleteReview(Review r) {
		reviewRepository.delete(r);
	}


	@Transactional
	public CoolearnUser createCoolearnUser(String emailAddress, String firstname, String lastname, String password){
		String error = "";
		if(isNullOrEmptyString(emailAddress)) {
			error += "Invalid email address! ";
		} else if(coolearnUserRepository.existsByEmailAddress(emailAddress)) {
			error += "Email address is already taken!";
		}
		if(isNullOrEmptyString(firstname)) {
			error += "Invalid first name! ";
		}
		if(isNullOrEmptyString(lastname)) {
			error += "Invalid last name! ";
		}
		if(isNullOrEmptyString(password)) {
			error += "Invalid password!";
		}
		error = error.trim();
		if(error.length() > 0) {
			throw new IllegalArgumentException(error);
		}

		CoolearnUser CoolearnUser = new CoolearnUser();
		//TODO verify that it is an emailAddress
		CoolearnUser.setEmailAddress(emailAddress);
		CoolearnUser.setFirstName(firstname.trim());
		CoolearnUser.setLastName(lastname.trim());
		//TODO make the creation more secure by hashing password
		CoolearnUser.setPassword(password);
		coolearnUserRepository.save(CoolearnUser);
		return CoolearnUser;
	}
	@Transactional
	public CoolearnUser getCoolearnUser(String emailAddress){
		if(isNullOrEmptyString(emailAddress)) {
			throw new IllegalArgumentException("Email address is invalid!");
		} else if(!coolearnUserRepository.existsByEmailAddress(emailAddress)) {
			throw new IllegalArgumentException("No user with this email address!");
		}
		CoolearnUser CoolearnUser =  coolearnUserRepository.findByEmailAddress(emailAddress);
		return CoolearnUser;
	}


	@Transactional
	public List<CoolearnUser> getAllCoolearnUsers() {
		return toList(coolearnUserRepository.findAll());
	}

	@Transactional
	public Course createCourse(String name, Subject subject, EducationLevel educationLevel){
		String error = "";
		if(isNullOrEmptyString(name)) {
			error += "Invalid course name! ";
		}
		error = error.trim();
		if(error.length() > 0) {
			throw new IllegalArgumentException(error);
		}
		String nameTrim = name.trim().toUpperCase(); // default
		if(courseRepository.existsByName(nameTrim)) {
			throw new IllegalArgumentException("Course with this name already exists.");
		}
		if(subject == null) {
			error += "Null subject! ";
		}
		if(educationLevel == null) {
			error += "You need to pass an education level!";
		}
		error = error.trim();
		if(error.length() > 0) {
			throw new IllegalArgumentException(error);
		}
		Course course = new Course();
		course.setName(nameTrim);
		course.setSubject(subject);
		course.setEducationalLevel(educationLevel);
		courseRepository.save(course);
		return course;
	}


	@Transactional
	public Course getCourseByName(String name){
		String error = "";
		if(isNullOrEmptyString(name)) {
			error += "Invalid course name! ";
		}
		error = error.trim();
		if(error.length() > 0) {
			throw new IllegalArgumentException(error);
		}
		if(!courseRepository.existsByName(name.trim().toUpperCase())) {
			throw new IllegalArgumentException("No course exists with this name");
		}
		Optional<Course> course = courseRepository.findByName(name.trim().toUpperCase());
		return course.get();
	}

	@Transactional
	public List<Course> getCourseBySubject(Subject subject){
		return toList(courseRepository.findBySubject(subject));
	}

	@Transactional
	public List<Course> getCourseByEducationalLevel(EducationLevel ed){
		return toList(courseRepository.findByEducationalLevel(ed));
	}

	@Transactional
	public List<Course> getAllCourses() {
		return toList(courseRepository.findAll());
	}

	@Transactional
	public void deleteCourse(String name){
		if (name == null || name.trim().length() == 0) {
			throw new IllegalArgumentException("Course name cannot be empty!");
		}
		if(!courseRepository.existsByName(name.trim().toUpperCase())) {
			throw new IllegalArgumentException("No course exists with this name");
		}
		Course course = courseRepository.findByName(name.trim().toUpperCase()).get();
		// Delete list of all specific course
		if(course.getSpecificCourses() != null) {
			for(SpecificCourse spc: course.getSpecificCourses()) {
				SpecificCourseRepository.delete(spc);
			}
		}
		courseRepository.delete(course);
	}

	@Transactional
	public UserRole createUserRole(CoolearnUser coolearnUser, String userRole, Set<Course> courses){
		if (coolearnUser == null) {
			throw new IllegalArgumentException("CoolearnUser cannot be null");
		}
		if(userRole == null) {
			throw new IllegalArgumentException("User role needs to be specified");
		} 
		//TODO check whether it already has a role (cannot have 2 similar)
		UserRole role = null;
		switch (userRole) {
		case TUTOR_DISCRIMINATOR:
			if(courses == null || courses.size() == 0) {
				throw new IllegalArgumentException("A tutor should be created with a list of courses");
			}
			Tutor tutor = new Tutor();
			role = tutor;
			role.setUser(coolearnUser);
			userRoleRepository.save(role);
			for(Course c: courses) {
				createSpecificCourse(MINIMUM_HOURLY_RATE, c, tutor);
			}			
			break;
		case STUDENT_DISCRIMINATOR:
			role = new Student();
			role.setUser(coolearnUser);
			userRoleRepository.save(role);
			break;

		default:
			throw new IllegalArgumentException("User role should be either tutor or student");
		}
		userRoleRepository.save(role);
		return role;
	}

	@Transactional
	public UserRole getUserRole(Integer id){
		if(!userRoleRepository.existsById(id)) {
			throw new IllegalArgumentException("The user role does not exist for this id.");
		}
		Optional<UserRole> userRole = userRoleRepository.findById(id);
		return userRole.get();
	}

	@Transactional
	public List<UserRole> getUserRolesByCoolearnUser(CoolearnUser CoolearnUser){
		if(!userRoleRepository.existsByUser(CoolearnUser)) {
			throw new IllegalArgumentException("There are no user roles associated to this CoolearnUser!");
		}
		List<UserRole> userRole = userRoleRepository.findByUser(CoolearnUser);
		return userRole;
	}

	@Transactional
	public List<UserRole> getAllUserRoles() {
		return toList(userRoleRepository.findAll());
	}

	private boolean isNullOrEmptyString(String str) {
		return (str == null) || (str.trim().length() ==0);
	}

	@Transactional
	public Subject createSubject(String name) {
		if (name == null || name.trim().length() == 0) {
			throw new IllegalArgumentException("Subject name cannot be empty!");
		}
		if (subjectRepository.existsBySubjectName(name.trim().toUpperCase())) {
			throw new IllegalArgumentException("Subject with this name already exists.");
		}
		Subject subject = new Subject();
		subject.setSubjectName(name.trim().toUpperCase()); // The default way to store subjects name are all in capital letters
		subjectRepository.save(subject);
		return subject;
	}

	@Transactional
	public Subject getSubjectByName(String name) {
		if (name == null || name.trim().length() == 0) {
			throw new IllegalArgumentException("Subject name cannot be empty!");
		}
		if (!subjectRepository.existsBySubjectName(name.trim().toUpperCase())) {
			throw new IllegalArgumentException("No subject exists with this name.");
		}
		Subject subject = subjectRepository.findBySubjectName(name.trim().toUpperCase());
		return subject;
	}

	@Transactional
	public List<Subject> getAllSubjects(){
		return toList(subjectRepository.findAll());
	}

	@Transactional
	public void deleteSubject(String name){
		if (name == null || name.trim().length() == 0) {
			throw new IllegalArgumentException("Subject name cannot be empty!");
		}
		if (!subjectRepository.existsBySubjectName(name.trim().toUpperCase())) {
			throw new IllegalArgumentException("No subject exists with this name.");
		}

		Subject subject = subjectRepository.findBySubjectName(name.trim().toUpperCase());
		subjectRepository.delete(subject);
	}

	/**
	 * Checks whether an availability exixts for a tutuor a given entity.
	 *
	 * @param tutor  must not be null
	 * @param availability must not be null
	 * @return will never be null.
	 * @throws IllegalArgumentException in case the given parameter is null.
	 */

	public boolean checkAvailabilityExistsForTutor(Tutor tutor, Availability availability){
		String error = "";
		if(tutor == null){
				error = error + "Null tutor. ";
		}
		if (availability == null){
			error = error + "Null availability.";
		}
		error = error.trim();
		if (error.length() > 0) {
			throw new IllegalArgumentException(error);
		}
		if (tutor.getAvailability() != null){
			for (Availability a : tutor.getAvailability()){
				if (a.getStartDate().toString().equals(availability.getStartDate().toString()) && a.getEndDate().toString().equals(availability.getEndDate().toString()) &&
						a.getStartTime().toString().equals(availability.getStartTime().toString()) && a.getEndTime().toString().equals(availability.getEndTime().toString()) &&
						a.getDayOfWeek().toString() == availability.getDayOfWeek().toString())
					return true;
			}
		}
		return false;
	}

	@Transactional
	public Availability createAvailability(Tutor tutor, Time startTime, Time endTime, Date startDate, Date endDate, DayOfWeek dayOfWeek){

		String error = "";
		if(tutor == null){
			throw new IllegalArgumentException("Tutor unspecified!.");
		}
		if (startDate == null) {
			error = error + "Availability start date cannot be empty! ";
		}
		if (endDate == null) {
			error = error + "Availability end date cannot be empty! ";
		}
		if (startDate != null && endDate != null && (endDate.before(startDate))) {
			error = error + "Availability end date cannot be before event start date!";
		}
		if (startTime == null) {
			error = error + "Availability start time cannot be empty! ";
		}
		if (endTime == null) {
			error = error + "Availability end time cannot be empty! ";
		}
		if (endTime != null && startTime != null && endTime.before(startTime)) {
			error = error + "Availability end time cannot be before event start time!";
		}
		if (dayOfWeek == null){
			error = error + "Availability day of week cannot be empty! ";
		}

		error = error.trim();
		if (error.length() > 0) {
			throw new IllegalArgumentException(error);
		}
		Availability availability = new Availability();
		availability.setStartTime(startTime);
		availability.setEndTime(endTime);
		availability.setStartDate(startDate);
		availability.setEndDate(endDate);
		availability.setDayOfWeek(dayOfWeek);
		// Do not save duplicate availability
		if (checkAvailabilityExistsForTutor(tutor,availability)){
			throw new IllegalArgumentException("This availability already exists for tutor.");
		}
		availabilityRepository.save(availability);

		if (tutor.getAvailability() == null){
			Set<Availability> availabilities = new HashSet<Availability>();
			availabilities.add(availability);
			tutor.setAvailability(availabilities);
		}else{
			tutor.getAvailability().add(availability);
		}
		// Using save() CRUD method to update a database entry
		userRoleRepository.save(tutor);
		return availability;
	}

	// Created a different for mock test
	@Transactional
	public Availability updateAvailability(Tutor tutor, Availability availability, Time startTime, Time endTime, Date startDate, Date endDate, DayOfWeek dayOfWeek){
		String error = "";
		if (tutor == null) {
			error = error + "Null tutor!";
		}
		if (availability == null) {
			error = error + "Null Availability! ";
		}
		if (startDate == null) {
			error = error + "Availability start date cannot be empty! ";
		}
		if (endDate == null) {
			error = error + "Availability end date cannot be empty! ";
		}
		if (startTime== null) {
			error = error + "Availability start time cannot be empty! ";
		}
		if (endTime == null) {
			error = error + "Availability end time cannot be empty! ";
		}
		if (dayOfWeek == null){
			error = error + "Availability day of week cannot be empty! ";
		}
		error = error.trim();
		if (error.length() > 0) {
			throw new IllegalArgumentException(error);
		}
		if (endTime.before(startTime)) {
			throw new IllegalArgumentException("Availability end time cannot be before event start date!");
		}
		if (endDate.before(startDate)) {
			throw new IllegalArgumentException("Availability end date cannot be before event start date!");
		}
		Set<Availability> availabilities = tutor.getAvailability();
		if (!availabilities.contains(availability)) {
			throw new IllegalArgumentException("Invalid availability/tutor pair.");
		}
		availability.setStartTime(startTime);
		availability.setEndTime(endTime);
		availability.setStartDate(startDate);
		availability.setEndDate(endDate);
		availability.setDayOfWeek(dayOfWeek);
		availabilityRepository.save(availability);
		return availability;
	}

	// Didn't think it's possible to test this in Mock environment
	@Transactional
	public Availability updateAvailability(Integer user_id, Integer av_id, Time startTime, Time endTime, Date startDate, Date endDate, DayOfWeek dayOfWeek){
		List<Availability> availabilities = getAllAvailabilitiesByTutorId(user_id);
		Availability availability = getAvailabilityById(av_id);
		if (!availabilities.contains(availability)) {
			throw new IllegalArgumentException("Invalid availability/tutor pair.");
		}
		String error = "";
		if (startDate == null) {
			error = error + "Availability start date cannot be empty! ";
		}
		if (endDate == null) {
			error = error + "Availability end date cannot be empty! ";
		}
		if (endDate.before(startDate)) {
			error = error + "Availability end date cannot be before event start date!";
		}
		if (startTime== null) {
			error = error + "Availability start time cannot be empty! ";
		}
		if (endTime == null) {
			error = error + "Availability end time cannot be empty! ";
		}
		if (endTime.before(startTime)) {
			error = error + "Availability end time cannot be before event start date!";
		}
		if (dayOfWeek == null){
			error = error + "Availability day of week cannot be empty! ";
		}
		error = error.trim();
		if (error.length() > 0) {
			throw new IllegalArgumentException(error);
		}
		availability.setStartTime(startTime);
		availability.setEndTime(endTime);
		availability.setStartDate(startDate);
		availability.setEndDate(endDate);
		availability.setDayOfWeek(dayOfWeek);
		availabilityRepository.save(availability);
		return availability;
	}

	@Transactional
	public void deleteAvailability (Tutor tutor, Availability availability){
		if (availability == null) {
			throw new IllegalArgumentException("Availability does not exist");
		}
		if(tutor == null) {
			throw new IllegalArgumentException("tutor is null");
		}
		if(tutor.getAvailability() == null) {
			throw new IllegalArgumentException("tutor has no availabilities");
		}
		if(!tutor.getAvailability().contains(availability)){
			throw new IllegalArgumentException("tutor does not have this availability");
		}
		tutor.getAvailability().remove(availability);
		userRoleRepository.save(tutor);
		availabilityRepository.delete(availability);
	}

	@Transactional
	public List<Availability> getAllAvailabilities(){
		return toList(availabilityRepository.findAll());
	}

	@Transactional
	public List<Availability> getAllAvailabilitiesByTutorId(Integer tutor_id){
		if(!(getUserRole(tutor_id) instanceof Tutor)){
			throw new IllegalArgumentException("The id provided does not refer to a tutor");
		}
		Tutor tutor = (Tutor) getUserRole(tutor_id);
		return toList(tutor.getAvailability());
	}

	@Transactional
	public Availability getAvailabilityById(Integer id){
		if(id == null) {
			throw new IllegalArgumentException("Id cannot be null");
		}
		if(!availabilityRepository.existsById(id)) {
			throw new IllegalArgumentException("This id value for an availability does not exist");
		}
		Optional<Availability> availability = availabilityRepository.findById(id);
		return availability.get();
	}

	@Transactional
	public SpecificCourse updatePriceOfSpecificCourse(Tutor t, double rate, SpecificCourse course) {
		//TODO Create another method without Tutor (This is Alex's way)
		String error = "";
		if(rate < MINIMUM_HOURLY_RATE){
			error += "Hourly rate must be greater than " + MINIMUM_HOURLY_RATE + " ";
		}
		if (course == null){
			error += "There must be a course to be updated ";
		}
		if (t == null) {
			error += "Tutor cannot be null ";
		}
		if (!t.getTeachingCourses().contains(course)) {
			error += "The specific course does not exist for this tutor ";
		}
		error = error.trim();
		if(error.length() > 0) {
			throw new IllegalArgumentException(error);
		}
		List<SpecificCourse> listSpecificCourse = getSpecificCoursesByTutor(t);
		SpecificCourse courseToUpdate = null;
		for(SpecificCourse sc: listSpecificCourse) {
			if(sc.equals(course)) {
				courseToUpdate = sc;
				break;
			}
		}
		if(courseToUpdate == null) {
			throw new IllegalArgumentException("Course does not exist for tutor");
		}
		courseToUpdate.setHourlyRate(rate);
		SpecificCourseRepository.save(courseToUpdate);
		return courseToUpdate;
	}

	// Working branch
	@Transactional
	public SpecificCourse updatePriceOfSpecificCourse(double rate, SpecificCourse course) {
		String error = "";
		if(rate < MINIMUM_HOURLY_RATE){
			error += "Hourly rate must be greater than " + MINIMUM_HOURLY_RATE + " ";
		}
		if (course == null){
			error += "There must be a course to be updated ";
		}
		error = error.trim();
		if(error.length() > 0) {
			throw new IllegalArgumentException(error);
		}

		course.setHourlyRate(rate);
		SpecificCourseRepository.save(course);
		return course;
	}

	@Transactional
	public List<SpecificCourse> getSpecificCoursesByTutor(Tutor t) {
		if(t == null) { //TODO check if tutor does not exists in DB
			throw new IllegalArgumentException("Tutor cannot be null");
		}	
		return toList(SpecificCourseRepository.findByTutor(t));
	}

	@Transactional
	public SpecificCourse getSpecificCourse(Integer id){
		if(id <= 0){
			throw new IllegalArgumentException("ID must be greater than 0");
		}
		Optional<SpecificCourse> op_SpecificCourse = SpecificCourseRepository.findById(id);
		if (!op_SpecificCourse.isPresent()){
			throw new NoSuchElementException();
		}
		SpecificCourse SpecificCourse = op_SpecificCourse.get();
		return SpecificCourse;
	}

	@Transactional
	public List<SpecificCourse> getAllSpecificCoursesByCourse(Course course){
		return toList(SpecificCourseRepository.findByCourse(course));
	}

	@Transactional
	public SpecificCourse createSpecificCourse(double hourlyRate, Course course, Tutor tutor) {
		//TODO We can automate this to create courses with minimum rate if it is not set		
		String error = "";
		if(hourlyRate < MINIMUM_HOURLY_RATE) {
			error += "The hourly rate for a course should be greater or equal to " + MINIMUM_HOURLY_RATE + " ";
		}
		if(course == null) {
			error += "The course should be specified ";
		} else if(!courseRepository.existsByName(course.getName())) {
			error += "The course with this name does not exist ";
		}
		if(tutor == null) {
			error += "The tutor should be specified ";
		} //TODO we can add verification that a tutor with this id exists
		//TODO check the specific course for that course does not already exist
		error = error.trim();
		if(error.length() > 0) {
			throw new IllegalArgumentException(error);
		}
		SpecificCourse specificCourse = new SpecificCourse();
		specificCourse.setCourse(course);
		specificCourse.setHourlyRate(hourlyRate);
		specificCourse.setTutor((Tutor) tutor);
		SpecificCourseRepository.save(specificCourse);
		return specificCourse;
	}

	@Transactional
	public List<SpecificCourse> getAllSpecificCourses(){
		return toList(SpecificCourseRepository.findAll());

	}

	@Transactional
	public Room createRoom(int roomNumber, RoomSize roomSize){
		if(roomNumber<1){
			throw new IllegalArgumentException("Invalid room number");
		}
		if(roomSize==null){
			throw new IllegalArgumentException("A room size must be specified");
		}
//		List<Room> listRooms = getAllRooms();
//		for(Room r: listRooms) {
//			if(r.getRoomNumber() == roomNumber) {
//				throw new IllegalArgumentException("Room already exist");
//			}
//		}
		Room room = new Room();
		room.setRoomNumber(roomNumber);
		room.setRoomsize(roomSize);
		roomRepository.save(room);
		return room;
	}

	@Transactional
	public Room getRoom(int roomNumber){
		if(roomNumber<1){
			throw new IllegalArgumentException("Invalid room number");
		}
		Optional<Room> op_room= roomRepository.findByRoomNumber(roomNumber);
		if (!op_room.isPresent()){
			throw new NoSuchElementException("No room with this number");
		}
		Room room = op_room.get();
		return room;
	}

	@Transactional
	public List<Room> getRoomBySize(RoomSize size){
		if(size==null){
			throw new IllegalArgumentException("A room size must be specified");
		}
		List<Room> rooms = toList(roomRepository.findByRoomsize(size));
		if (rooms.size()==0){
			throw new IllegalArgumentException("No room of such size");
		}
		return rooms;

	}

	@Transactional
	public void deleteAllSessionsAndUserRolesWithReferentialIntegrity(){
		List<UserRole> userRoles = getAllUserRoles();
		for(UserRole u: userRoles) {
			if(u instanceof Student) {
				Student student = (Student) u;
				List<Session> sessionsByStudent = getAllSessionsByStudent(student);
				
				for(Session s: sessionsByStudent) {
					student.getSession().remove(s);
					Tutor tutor = s.getTutor();
					tutor.getSession().remove(s);
					userRoleRepository.save(student);
					userRoleRepository.save(tutor);
					sessionRepository.delete(s);	
				}
				userRoleRepository.delete(student);
			} else {
				Tutor tutor = (Tutor) u;
				List<Session> sessionsByTutor = getAllSessionsByTutor(tutor);
				for(Session s: sessionsByTutor) {
					tutor.getSession().remove(s);
					Set<Student> students = s.getStudent();
					for(Student st : students){
						st.getSession().remove(s);
						userRoleRepository.save(st);
					}
					userRoleRepository.save(tutor);
					sessionRepository.delete(s);
				}
				userRoleRepository.delete(tutor);
			}
		}
	}

	@Transactional
	public List<Room> getAllRooms(){
		return toList(roomRepository.findAll());
	}

	private <T> List<T> toList(Iterable<T> iterable){
		List<T> resultList = new ArrayList<T>();
		if (iterable == null){
			throw new IllegalArgumentException("Empty iterable object!");
		}
		for (T t : iterable) {
			resultList.add(t);
		}
		return resultList;
	}

}
