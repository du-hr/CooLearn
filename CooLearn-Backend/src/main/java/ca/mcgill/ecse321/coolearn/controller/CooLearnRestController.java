package ca.mcgill.ecse321.coolearn.controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.mail.internet.MimeMessage;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ca.mcgill.ecse321.coolearn.model.*;
import ca.mcgill.ecse321.coolearn.service.*;
import ca.mcgill.ecse321.coolearn.dto.*;
import static ca.mcgill.ecse321.coolearn.service.StringLiterals.STUDENT_DISCRIMINATOR;
import static ca.mcgill.ecse321.coolearn.service.StringLiterals.TUTOR_DISCRIMINATOR;
import static ca.mcgill.ecse321.coolearn.CooLearnApplication.currentUserRoleLoggedIn;


@CrossOrigin(origins = "*")
@RestController
public class CooLearnRestController {

	@Autowired
	CooLearnService service;

	@Autowired
	private JavaMailSender sender;

	/**
	 * ------ SUBMIT INTEREST FORM -----------
	 * (Example: https://getbootstrap.com/docs/4.0/examples/cover/#)
	 * GET - /: our company home page
	 * One button in the navigation bar will be linked to a google form
	 */

	/**
	 * ------ ADD A TUTOR -----------
	 * (Example: https://getbootstrap.com/docs/4.0/examples/cover/#)
	 * GET - /register: Returns the form to fill
	 * Additional Notes: page only accessible by manager, assume that security works 
	 * if time we can add password to access form - it's not accessible via website
	 * POST - /register: Creates a new Tutor and sends him an email
	 */
	@PostMapping(value = {"/register", "/register/"})
	@ResponseBody
	public UserRoleDto createTutor(@RequestBody TutorRegistrationForm tutorform) throws IllegalArgumentException {
		String error = "";
		if(tutorform.firstName == null || tutorform.firstName.length() == 0) {
			error += "First name not specified. ";
		}
		if(tutorform.lastName == null || tutorform.lastName.length() == 0) {
			error += "Last name not specified. ";
		}
		if(tutorform.emailAddress == null || tutorform.emailAddress.length() == 0) {
			error += "Email address not specified. ";
		}
		error = error.trim();
		if(error.length() > 0) {
			throw new IllegalArgumentException(error);
		}
		//TODO Is there any reason to have list instead of set?
		Set<Course> listOfCourses = new HashSet<Course>();
		if(tutorform.listOfCoursesName == null) {
			throw new IllegalArgumentException("No course selected!");
		}
		for(String str: tutorform.listOfCoursesName) {
			listOfCourses.add(service.getCourseByName(str));
		}
		PasswordGenerator passwordGenerator = new PasswordGenerator.PasswordGeneratorBuilder()
				.useDigits(true)
				.useLower(true)
				.useUpper(true)
				.build();
		String password = passwordGenerator.generate(16); 
		CoolearnUser tutor = service.createCoolearnUser(tutorform.emailAddress, tutorform.firstName, tutorform.lastName, password);
		UserRole tutorRole = service.createUserRole(tutor, TUTOR_DISCRIMINATOR, listOfCourses);
		try {
			sendEmailWithCredentials(tutorform.emailAddress, tutorform.lastName, password);
		} catch (Exception e) {
			e.getMessage();
		}
		return DtoConverter.convertToDto(tutorRole);
	}

	private void sendEmailWithCredentials(String email, String lastName, String password) throws Exception{
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		String content = "Dear Mr(s)." + lastName + ",\nIt is a great honour for us to have you in our team."
				+ "\nPlease find you password here: " + password + ".\n\n"
				+ "Looking forward to collaborating,\n"
				+ "Best regards,\nBilliard Gatheway, CEO TogetherWeCan";
		helper.setTo(email);
		helper.setSubject("Welcome to CooLearn! Credentials");
		helper.setText(content);

		sender.send(message);
	}

	@GetMapping(value = {"/tutors"})
	public List<TutorDto> getAllTutors() throws IllegalArgumentException {
		List<UserRole> userRoleslist = service.getAllUserRoles();
		List<TutorDto> returnedList = new ArrayList<TutorDto>();
		for(UserRole u: userRoleslist) {
			if(u instanceof Tutor) {
				returnedList.add((TutorDto) DtoConverter.convertToDto((Tutor) u));
			}
		}
		return returnedList;
	}

	@PostMapping(value = {"/courses", "/courses/"})
	@ResponseBody
	public CourseDto createCourse(@RequestBody CourseForm courseForm) throws IllegalArgumentException {
		String error = "";
		if(courseForm.subjectName == null || courseForm.subjectName.length() == 0) {
			error += "Subject name is empty. ";
		}
		if(courseForm.name == null || courseForm.name.length() == 0) {
			error += "Course name is empty. ";
		}
		List<EducationLevel> values = Arrays.asList(EducationLevel.values());
		if(!values.contains(courseForm.educationLevel)) {
			error += "Education level is incorrectly specified.";
		}
		error = error.trim();
		if(error.length() > 0) {
			throw new IllegalArgumentException(error);
		}
		Subject subject = null;
		try {
			subject = service.getSubjectByName(courseForm.subjectName);
		} catch (IllegalArgumentException e) {
			if(e.getMessage().equals("No subject exists with this name.")) {
				subject = service.createSubject(courseForm.subjectName);
			}
		}
		Course course = service.createCourse(courseForm.name, subject, courseForm.educationLevel);
		//TODO Check for emptyness for form (null)
		return DtoConverter.convertToDto(course);
	}

	@GetMapping(value = {"/courses", "/courses/"})
	public List<CourseDto> getAllCourses() throws IllegalArgumentException {
		List<Course> listCourses = service.getAllCourses();
		List<CourseDto> returnedList = new ArrayList<CourseDto>();
		for(Course c: listCourses) {
			returnedList.add(DtoConverter.convertToDto(c));
		}
		return returnedList;
	}


	/**
	 * ------------- LOGIN -------------
	 * (Example: https://getbootstrap.com/docs/4.0/examples/sign-in/)
	 * Extra features - Login by gmail or facebook (should have the same email as the one in DB)
	 * GET - /login: returns the login page
	 * POST - /login: attempt to login to the system
	 * GET - /logout: renders page when you logout
	 * I your email has both roles, then ask which to log into (need to see how I will do it)
	 * 
	 */
	@PostMapping(value = {"/login", "/login/"})
	@ResponseBody
	public UserRoleDto login(@RequestBody LoginForm loginForm) throws IllegalArgumentException {
		String error = "";
		if(loginForm.emailAddress == null || loginForm.emailAddress.length() == 0) {
			error += "Email address is not specified. ";
		}
		if(loginForm.password == null || loginForm.password.length() == 0) {
			error += "Password is not specified.";
		}
		if (loginForm.type == null) {
			error += "User Type Cannot Be Null.";
		}
		error = error.trim();
		if(error.length() > 0) {
			throw new IllegalArgumentException(error);
		}
		CoolearnUser user = service.getCoolearnUser(loginForm.emailAddress);
		//TODO Hash password for security
		if(!user.getPassword().equals(loginForm.password)) {
			throw new IllegalArgumentException("Invalid username password combination!");
		}
		List<UserRole> userRoleList = service.getUserRolesByCoolearnUser(user);
		UserRole userRole = null;
		for (UserRole ur : userRoleList) {
			if ((loginForm.type.toUpperCase().equals("STUDENT") 
				&& ur instanceof Student)
				|| (loginForm.type.toUpperCase().equals("TUTOR")
				&& ur instanceof Tutor)){
				userRole = ur;
				break;
			}
		}
		if (userRole == null) {
			throw new IllegalArgumentException("User Type not Associated with this User");
		}
		
		//Add to login user id array
		if(currentUserRoleLoggedIn == null) {
			currentUserRoleLoggedIn = new HashSet<Integer>();
		}
		currentUserRoleLoggedIn.add(userRole.getId());

		//Add to google chrome local storage
		return DtoConverter.convertToDto(userRole);
	}

	@GetMapping(value = {"/logout", "/logout/"})
	public CoolearnUserDto logout() throws IllegalArgumentException {
		//Get UserRole id saved in local storage
		Integer id = 1; //TODO Fix this when front end works
		UserRole userRole = service.getUserRole(id);
		currentUserRoleLoggedIn.remove(id);
		CoolearnUser user = userRole.getUser();
		return DtoConverter.convertToDto(user);
	}

	//TODO For now only prevent incorrect userRole to access information
	//Not secure, we will improve if we have time
	//TODO If someone know how we can input the name of th class to make it more general, then add it
	private boolean authorizeTutor(Integer userRole_id) {
		UserRole userRole = service.getUserRole(userRole_id);
		if(userRole instanceof Tutor) {
			return false;
		}
		return true;
	}

	private boolean authorizeStudent(Integer userRole_id) {
		UserRole userRole = service.getUserRole(userRole_id);
		if(userRole instanceof Student) {
			return false;
		}
		return true;
	}



	/**
	 * ------------ DASHBOARD ------------------
	 * 
	 * GET - /dashboard/{userRole_id}: dash board for a tutor 
	 * 
	 * 
	 */ 

	/**
	 * ---------------------- SET AVAILABILITY ---------------------------------------
	 * GET - /dashboard/{userRole_id}/availabilities: list of his current availabilities
	 * GET - /dashboard/{userRole_id}/availabilities/new: form to add new availabilities
	 * POST - /dashboard/{userRole_id}/availabilities: add new availabilities in db
	 * GET - /dashboard/{userRole_id}/availabilities/{id}/edit: form to add update availabilities
	 * PUT/PATCH - /dashboard/{userRole_id}/availabilities/{id}: updates availability in db
	 * DELETE - /dashboard/{userRole_id}/availabilities/{id}: deletes an availability
	 * 
	 */

	@GetMapping(value = { "/dashboard/{userRole_id}/availabilities", "/dashboard/{userRole_id}/availabilities/" })
	public List<AvailabilityDto> getAllAvailabilitiesByTutor(@PathVariable ("userRole_id") Integer userRole_id)
			throws IllegalArgumentException { //TODO do we always have to get tutor from userRole id?
		List<AvailabilityDto> availabilityDtos = new ArrayList<AvailabilityDto>();
		for (Availability availability : service.getAllAvailabilitiesByTutorId(userRole_id)){
			availabilityDtos.add(DtoConverter.convertToDto(availability));
		}
		return availabilityDtos;
	}

	// TODO
	@GetMapping(value = { "/dashboard/{userRole_id}/availabilities/new", "/dashboard/{userRole_id}/availabilities/new/" })
	public AvailabilityDto getAvailabilityUpdate(@PathVariable ("userRole_id") Integer userRole_id)
			throws IllegalArgumentException {
		return null;
	}

	@PostMapping(value = {"/dashboard/{userRole_id}/availabilities", "/dashboard/{userRole_id}/availabilities/"})
	@ResponseBody
	public AvailabilityDto createAvailability(@PathVariable("userRole_id") Integer userRole_id, @RequestBody AvailabilityForm availabilityForm)	throws IllegalArgumentException {
		Tutor tutor = (Tutor) service.getUserRole(userRole_id);
		//Make the conversion for String to Calendar and Date Format: 2017-04-01
		List<DayOfWeek> values = Arrays.asList(DayOfWeek.values());
		if(!values.contains(availabilityForm.dayOfWeek)) {
			throw new IllegalArgumentException("Education level is incorrectly specified.");
		}
		Availability av = service.createAvailability(tutor, convertToTime(availabilityForm.startTime), convertToTime(availabilityForm.endTime), convertToDate(availabilityForm.startDate), convertToDate(availabilityForm.endDate), availabilityForm.dayOfWeek);
		return DtoConverter.convertToDto(av);
	}

	private Date convertToDate(String date) {
		StringTokenizer tokenizer = new StringTokenizer(date, "-");
		ArrayList<String> yyyymmdd = new ArrayList<String>();
		while(tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			yyyymmdd.add(token);
		}
		Calendar calendar = Calendar.getInstance();
		//Warning: calendar is 0 - 11 but user 1 - 12 (need to substract 1 to month)
		calendar.set(Integer.parseInt(yyyymmdd.get(0)), Integer.parseInt(yyyymmdd.get(1)) - 1, Integer.parseInt(yyyymmdd.get(2)));
		Date returnDate = new Date(calendar.getTimeInMillis());
		return returnDate;
	}

	private Time convertToTime(String time) {
		return Time.valueOf(LocalTime.parse(time));
	}

	// TODO
	@GetMapping(value = {"/dashboard/{userRole_id}/availabilities/{id}/edit","/dashboard/{userRole_id}/availabilities/{id}/edit/"})
	public AvailabilityDto getAvailabilityById(@PathVariable ("userRole_id") Integer userRole_id, @PathVariable ("id") Integer id)
			throws IllegalArgumentException { //TODO change so that it returns a form
		return null;
	}

	@PutMapping (value = { "/dashboard/{userRole_id}/availabilities/{id}", "/dashboard/{userRole_id}/availabilities/{id}/" })
	@ResponseBody
	public void updateAvailability(@PathVariable ("userRole_id") Integer userRole_id, @PathVariable ("id") Integer id, @RequestBody UpdateAvailabilityForm updateForm) throws IllegalArgumentException { 
		//TODO ask TA if parameters are returned in object of as separated values (returned from form)
		List<DayOfWeek> values = Arrays.asList(DayOfWeek.values());
		if(!values.contains(updateForm.dayOfWeek)) {
			throw new IllegalArgumentException("Education level is incorrectly specified.");
		}
		service.updateAvailability(userRole_id, id, convertToTime(updateForm.startTime), convertToTime(updateForm.endTime), convertToDate(updateForm.startDate), convertToDate(updateForm.endDate), updateForm.dayOfWeek);
	}

	@DeleteMapping (value = {"/dashboard/{userRole_id}/availabilities/{id}", "/dashboard/{userRole_id}/availabilities/{id}/"})
	public void deleteAvailability (@PathVariable ("userRole_id") Integer userRole_id, @PathVariable ("id") Integer id)
			throws IllegalArgumentException{	
		Availability availability = service.getAvailabilityById(id);
		UserRole userRole = service.getUserRole(userRole_id);
		if(!(userRole instanceof Tutor)) {
			throw new IllegalArgumentException("Invalid role");
		}
		Tutor tutor = (Tutor) userRole;
		if (!service.getAllAvailabilitiesByTutorId(userRole_id).contains(availability)) {
			throw new IllegalArgumentException("Invalid availability/tutor pair.");
		}
		service.deleteAvailability(tutor, service.getAvailabilityById(id));
	}

	/**
	 * -------------------------------- SET PRICE ----------------------------------
	 * Note: By course, I mean SpecificCourse
	 * GET - /dashboard/{userRole_id}/courses: list of his current specific courses
	 * (OPTIONAL) GET - /dashboard/{userRole_id}/courses/{course_id}: show page with information (the information is extra)
	 * (OPTIONAL) GET - /dashboard/{userRole_id}/courses/{course_id}/edit: form to add update price (minimum is already set)
	 * PUT/PATCH - /dashboard/{userRole_id}/courses/{course_id}: update price for specific course in db
	 * 
	 * IDEA: Each course will be in a card and you will be able to input a new price and press button update
	 * In other words, you will get a list of forms that you can update
	 */

	@GetMapping(value = {"/dashboard/{userRole_id}/courses", "/dashboard/{userRole_id}/courses/"})
	public List<SpecificCourseDto> getAllClassesOfTutor(@PathVariable ("userRole_id") Integer userRole_id)
			throws IllegalArgumentException{
		Tutor tutor = (Tutor) service.getUserRole(userRole_id);
		List<SpecificCourseDto> specificCourseDtos = new ArrayList<SpecificCourseDto>();
		for (SpecificCourse specificCourse : service.getSpecificCoursesByTutor(tutor)){
			specificCourseDtos.add(DtoConverter.convertToDto(specificCourse));
		}
		return specificCourseDtos;
	}

	@PutMapping(value = {"/dashboard/{userRole_id}/courses/{course_id}", "/dashboard/{userRole_id}/courses/{course_id}/"})
	public SpecificCourseDto updateSpecificCoursePrice(@PathVariable ("userRole_id") Integer userRole_id, 
			@PathVariable ("course_id") Integer course_id, @RequestParam  Double hourlyRate) throws IllegalArgumentException {

		Tutor tutor = (Tutor) service.getUserRole(userRole_id);
		SpecificCourse specificCourse = service.getSpecificCourse(course_id);
		specificCourse= service.updatePriceOfSpecificCourse(tutor, hourlyRate, specificCourse);

		return DtoConverter.convertToDto(specificCourse);
	}


	/**
	 * ------------------------- SEE OTHER TUTOR PRICING ----------------------------
	 * Note: By course, I mean Course
	 * GET - /dashboard/{userRole_id}/{course_name}/specificCourses: list of specific courses (with price and tutor) given a course name
	 * (Extra - add filters and append to url query string)
	 * 
	 */
	@GetMapping(value = {"dashboard/{userRole_id}/{course_name}/specificCourses", "dashboard/{userRole_id}/{course_name}/specificCourses/"})
	@ResponseBody
	public List<SpecificCourseDto> seeOtherTutorPricing(@PathVariable("userRole_id") int userRole_id, @PathVariable("course_name") String course_name) throws IllegalArgumentException {
		String error = "";
		if(userRole_id <= 0){
			error += "An invalid tutor id has been specified";
		}
		if(course_name == null || course_name.length() == 0){
			error += "The course name has not been specified. ";
		}

		error.trim();
		if (error.length() > 0) {
			throw new IllegalArgumentException(error);
		}

		Tutor tutor = (Tutor) service.getUserRole(userRole_id);
		Course course = service.getCourseByName(course_name);

		List<SpecificCourseDto> specificCourseDtos = new ArrayList<>();
		for(SpecificCourse specificCourse: service.getAllSpecificCoursesByCourse(course)){
			specificCourseDtos.add(DtoConverter.convertToDto(specificCourse));
		}
		return specificCourseDtos;
	}

	/**
	 * -------------- STUDENT VIEWPOINT (FOR DEMONSTRATION PURPOSES ONLY) --------------
	 * If login determines that it is a student (we will manually add students to DB)
	 * Open form to submit a new request
	 * GET - /dashboard/{userRole_id}/request/new: form to fill request
	 * POST - /dashboard/{userRole_id}/request: add a new session request to tutor (inform a session has been created or if error resulted)
	 */
	@PostMapping(value = {"/users", "/users/"})
	@ResponseBody
	public UserRoleDto createNewStudent(@RequestBody StudentForm studentform) throws IllegalArgumentException {
		String error = "";
		if(studentform.firstName == null || studentform.firstName.length() == 0) {
			error += "First name is not specified. ";
		}
		if(studentform.lastName == null || studentform.lastName.length() == 0) {
			error += "Last name is not specified. ";
		}
		if(studentform.emailAddress == null || studentform.emailAddress.length() == 0) {
			error += "Email address is not specified. ";
		}
		if(studentform.password == null || studentform.password.length() == 0) {
			error += "Password is not specified.";
		}
		error = error.trim();
		if(error.length() > 0) {
			throw new IllegalArgumentException(error);
		}
		CoolearnUser user = service.createCoolearnUser(studentform.emailAddress, studentform.firstName, studentform.lastName, studentform.password);
		UserRole userRole = service.createUserRole(user, STUDENT_DISCRIMINATOR, null);
		return DtoConverter.convertToDto(userRole);
	}

	@GetMapping(value = {"/users", "/users/"} )
	public List<CoolearnUserDto> getAllUsers() {
		List<CoolearnUser> users = service.getAllCoolearnUsers();
		List<CoolearnUserDto> dto_list = new ArrayList<CoolearnUserDto>();
		for(CoolearnUser u: users) {
			dto_list.add(DtoConverter.convertToDto(u));
		}
		return dto_list;
	}

	/**
	 * ----------- SEE LIST OF PENDING REQUESTS AND ACCEPTED REQUESTS (not yet passed) --------------
	 * Assume that all tutors will submit before the session starts (extra feature to have deadline)
	 * GET - /dashboard/{userRole_id}: see the list of all requests (approved and pending requests with the status as button (you cannot accept to reject from here)
	 * 
	 */
	@GetMapping(value = {"/dashboard/{userRole_id}", "/dashboard/{userRole_id}/"})
	public List<SessionDto> seeListOfRequests(@PathVariable("userRole_id") int userRole_id){
		UserRole user =  service.getUserRole(userRole_id);
		if (!(user instanceof Tutor)) {
			throw new IllegalArgumentException("The current user is not a tutor, and therefore cannot see session requests");
		}
		Tutor tutor = (Tutor) user;
		List<SessionDto> sessionsDto = new ArrayList<SessionDto>();
		//TODO Change method and use
		for (Session s: service.getAllSessionsByUserAndStatus(tutor, RequestStatus.ACCEPTED)){
			if(s.getDate().after(new Date(System.currentTimeMillis()))) {
				sessionsDto.add(DtoConverter.convertToDto(s));
			}
		}
		for (Session s: service.getAllSessionsByUserAndStatus(tutor, RequestStatus.PENDING)){
			if(s.getDate().after(new Date(System.currentTimeMillis()))) {
				sessionsDto.add(DtoConverter.convertToDto(s));
			}
		}
		return sessionsDto;
	}

	@GetMapping(value={"/request/{userRole_id}", "/request/{userRole_id}/"})
	public List<SessionDto> seeListOfCompletedRequests(@PathVariable("userRole_id") int userRole_id){
		UserRole user = service.getUserRole(userRole_id);
		if (!(user instanceof Tutor)) {
			throw new IllegalArgumentException("The current user is not a tutor, and therefore cannot see session requests");
		}
		Tutor tutor = (Tutor) user;
		List<SessionDto> sessionsDto = new ArrayList<SessionDto>();
		for(Session s: service.getAllSessionsByTutorAndDateBeforeAndEndTimeBeforeAndStatus(tutor, new Date(System.currentTimeMillis()), new Time(System.currentTimeMillis()), RequestStatus.ACCEPTED)){
			sessionsDto.add(DtoConverter.convertToDto(s));
		}
		return sessionsDto;
	}

	@PostMapping(value = {"/dashboard/{userRole_id}/request", "/dashboard/{userRole_id}/request/"})
	@ResponseBody
	public SessionDto createNewRequestByStudent(@RequestBody RequestFormByStudent requestform, @PathVariable("userRole_id") Integer userRoleId) throws IllegalArgumentException {
		String error = "";
		if(userRoleId == null || !(service.getUserRole(userRoleId) instanceof Student)) {
			throw new IllegalArgumentException("Unexpected!!");
		}
		Date date = convertToDate(requestform.date);
		Time startTime = convertToTime(requestform.startTime);
		Time endTime = convertToTime(requestform.endTime);

		if(date == null) {
			error += "Date is not specified. ";
		}
		if(date.before(new Date(System.currentTimeMillis()))) {
			error += "Date cannot be from the past. ";
		}
		if(startTime == null) {
			error += "Start time is not specified. ";
		}
		if(endTime == null || endTime.before(startTime)) {
			error += "End time is incorrectly specified. ";
		}
		if(service.getUserRole(requestform.tutorId) == null || !(service.getUserRole(requestform.tutorId) instanceof Tutor)) {
			error += "Tutor is not specified. ";
		}
		if(service.getSpecificCourse(requestform.specificCourseId) == null) {
			error += "Specific course does not exist.";
		}
		error = error.trim();
		if(error.length() > 0) {
			throw new IllegalArgumentException(error);
		}
		Course course = service.getSpecificCourse(requestform.specificCourseId).getCourse();
		Tutor tutor = (Tutor) service.getUserRole(requestform.tutorId);
		Student student = (Student) service.getUserRole(userRoleId);
		List<Student> studentList = new ArrayList<Student>();
		studentList.add(student);
		Session newSession = service.createSession(studentList, tutor, course, startTime, endTime, date);
		return DtoConverter.convertToDto(newSession);
	}


	/**
	 * --------------- SEE STUDENT HISTORY AND REVIEWS -----------------
	 * By request, I mean Session
	 * GET - /dashboard/{userRole_id}/{request_id}: see details about request (show page)
	 * GET - /dashboard/{userRole_id}/{request_id}/{userRole_id}: see history and review of userRole (force to userRole to be a student only)
	 * 
	 */

	@GetMapping(value = {"/dashboard/{userRole_id_tutor}/{userRole_id_student}","/dashboard/{userRole_id_tutor}/{userRole_id_student}/"})
	public List<ReviewDto> getStudentReviews(@PathVariable("userRole_id_student") Integer userRole_id_student)
			throws IllegalArgumentException{
		Student student = (Student)service.getUserRole(userRole_id_student);
		List<ReviewDto> reviewDtos = new ArrayList<ReviewDto>();
		for (Review review : service.getAllReviewsByUserRole(student)){
			reviewDtos.add(DtoConverter.convertToDto(review));
		}
		return reviewDtos;
	}

	/**
	 * --------------- SUBMIT A REVIEW OF A STUDENT ------------------
	 * (Extra feature) - Prevent a tutor to add a review before the session ends
	 * By request, I mean Session
	 * GET - /dashboard/{userRole_id}/{request_id}/{userRole_id}/new: form to add new review on student
	 * POST - /dashboard/{userRole_id}/{request_id}/{userRole_id}/: add new review on student to DB
	 * Note: restrict tutor to one review per student per session max	 
	 * GET - /dashboard/{userRole_id}/{request_id}/{userRole_id}/{review_id}/edit: form to update review on student
	 * PUT/PATCH - /dashboard/{userRole_id}/{request_id}/{userRole_id}/{review_id}: update review on student
	 * DELETE - /dashboard/{userRole_id}/{request_id}/{userRole_id}/{review_id}: delete review on student to DB
	 * 
	 */
	@PostMapping(value = {"dashboard/{reviewer_id}/{session_id}/{reviewee_id}", "dashboard/{reviewer_id}/{session_id}/{reviewee_id}/"})
	@ResponseBody
	public ReviewDto createReview(@PathVariable("reviewer_id") int reviewerId, @PathVariable("session_id") int sessionId, @PathVariable("reviewee_id") int revieweeId, @RequestBody ReviewForm reviewForm) {
		String error = getReviewErrors(reviewerId, sessionId, revieweeId);
		if (error != null) throw new IllegalArgumentException(error);
		UserRole recipient = service.getUserRole(revieweeId);
		Session session = service.getSessionById(sessionId);
		UserRole reviewer = service.getUserRole(reviewerId);	
		Review r = service.createReview(recipient, session, reviewForm.comments, reviewForm.rating);
		return DtoConverter.convertToDto(r);
	}

	@GetMapping(value={"dashboard/{reviewer_id}/{session_id}/{reviewee_id}", "dashboard/{reviewer_id}/{session_id}/{reviewee_id/}"})
	public ReviewDto getReviewBySessionAndStudent(@PathVariable("reviewer_id") int reviewerId, @PathVariable("session_id") Integer sessionId, @PathVariable("reviewee_id") Integer revieweeId){
		String err = getReviewErrors(reviewerId, sessionId, revieweeId);
		if (err != null) throw new IllegalArgumentException(err);
		UserRole recipient = service.getUserRole(revieweeId);
		Session session = service.getSessionById(sessionId);
		return DtoConverter.convertToDto(service.getReviewBySessionAndStudent(session, recipient));
	}

	@PutMapping(value={"dashboard/{reviewer_id}/{session_id}/{reviewee_id}/{review_id}", "dashboard/{reviewer_id}/{session_id}/{reviewee_id}/{review_id}/"})
	@ResponseBody
	public ReviewDto updateReview(@PathVariable("reviewer_id") int reviewerId, @PathVariable("session_id") int sessionId, @PathVariable("reviewee_id") int revieweeId,
			@PathVariable("review_id") int reviewId, @RequestBody ReviewForm reviewForm) {	
		String err = getReviewErrors(reviewerId, sessionId, revieweeId, reviewId);
		if (err!=null) throw new IllegalArgumentException(err);
		Review r = service.getReviewById(reviewId);
		service.updateReview(reviewId, reviewForm.comments, reviewForm.rating);
		return DtoConverter.convertToDto(r);
	}
	@DeleteMapping(value={"dashboard/{reviewer_id}/{session_id}/{reviewee_id}/{review_id}", "dashboard/{reviewer_id}/{session_id}/{reviewee_id}/{review_id}/"})
	@ResponseBody
	public ReviewDto deleteReview(@PathVariable("reviewer_id") int reviewerId, @PathVariable("session_id") int sessionId, @PathVariable("reviewee_id") int revieweeId,@PathVariable("review_id") int reviewId) {
		String err = getReviewErrors(reviewerId, sessionId, revieweeId, reviewId);
		if (err!=null) throw new IllegalArgumentException(err);
		Review r = service.getReviewById(reviewId);
		service.deleteReview(r);
		return DtoConverter.convertToDto(r);
	}
	/*
	 * Checks errors on a review being edited
	 **/
	private String getReviewErrors(int reviewerId, int sessionId, int revieweeId) {
		UserRole recipient = service.getUserRole(revieweeId);
		Session session = service.getSessionById(sessionId);
		UserRole reviewer = service.getUserRole(reviewerId);
		if (recipient instanceof Tutor) {
			if (!(reviewer instanceof Student)) return "Only a student can leave a review of a tutor.";
			if (session.getTutor().getId() != revieweeId) return "The tutor must be associated with the session you are reviewing.";
			boolean found = false;
			for (Student s : session.getStudent()) {
				if (s.getId() == reviewerId) {
					found = true;
					break;
				}
			}
			if (!found) return "Cannot write a review on a session you were not in!";
		} else if (recipient instanceof Student) {
			if (!(reviewer instanceof Tutor)) return "Only a tutor can leave a review of a student.";
			if (session.getTutor().getId() != reviewerId) return "You cannot review a student for a session you did not teach.";
			boolean found = false;
			for (Student s : session.getStudent()) {
				if (s.getId() == revieweeId) {
					found = true;
					break;
				}
			}	
			if (!found) return "Cannot write a review on a student not in the given session.";
		}
		return null;
	}

	private String getReviewErrors(int reviewerId, int sessionId, int revieweeId, int reviewId) {
		String err = getReviewErrors(reviewerId, sessionId, revieweeId);
		if (err != null) return err;
		Review r = service.getReviewById(reviewId);
		if (r.getSession().getId() != sessionId) {
			return "Review specified does not match given session id";
		}
		if (!(service.getUserRole(reviewerId) instanceof Tutor) 
				&& service.getSessionById(sessionId).getStudent().size() > 1) {
			//TODO: Update domain model to fix this potential issue?
			return "It could not be verified that the reviewer authored this review";
		}
		return null;
	}
	/**
	 * --------------- ACCEPT/DENY TUTORING SESSION ------------------
	 * By request, I mean Session
	 * Will be implemented as an additional feature to another route
	 * GET - /dashboard/{userRole_id}/{request_id}: see details about request (show page)
	 * If session is pending, you can accept and reject the request (and we will assume that you CANNOT modify your choice)
	 * 2 buttons (accepts - looks to see if empty room is available and makes payment) - Payment is an extra feature
	 * Deny the session will no longer appear the next time he will display the page (but it remains in DB as rejected - for the scope of this application, it is fine)
	 */

	@PutMapping(value={"dashboard/{userRole_id}/{request_id}/accept", "dashboard/{userRole_id}/{request_id}/accept/"})
	@ResponseBody
	public SessionDto acceptSession(@PathVariable("userRole_id") int userRoleId, @PathVariable("request_id") int sessionId) {
		checkSessionErrors(userRoleId, sessionId);
		Session s = service.getSessionById(sessionId);
		service.AcceptSession(s);
		return DtoConverter.convertToDto(s);
	}
	@PutMapping(value={"dashboard/{userRole_id}/{request_id}/decline", "dashboard/{userRole_id}/{request_id}/decline/"})
	@ResponseBody
	public SessionDto declineSession(@PathVariable("userRole_id") int userRoleId, @PathVariable("request_id") int sessionId) {
		checkSessionErrors(userRoleId,sessionId);
		Session s = service.getSessionById(sessionId);
		service.DenySession(s);
		return DtoConverter.convertToDto(s);
	}

	private void checkSessionErrors(int userRoleId, int sessionId) {
		Session s = service.getSessionById(sessionId);
		UserRole u = service.getUserRole(userRoleId);
		if (s.getStatus() != RequestStatus.PENDING) {
			throw new IllegalArgumentException("Cannot accept or reject a request that isn't pending");
		}
		if (!(u instanceof Tutor)) {
			throw new IllegalArgumentException("Only a tutor can accept or reject sessions.");
		}
		if (u.getId() != userRoleId) {
			throw new IllegalArgumentException("Cannot change session you are not the tutor for.");
		}
	}

	/**
	 * Utility
	 */
	@GetMapping(value = {"/getName/{userRole_id}"} )
	public String getUserName(@PathVariable ("userRole_id") Integer userRole_id) {
		UserRole userRole = service.getUserRole(userRole_id);
		return DtoConverter.convertToDto(userRole).getName();
	}
	
	@GetMapping(value = {"/getSession/{session_id}"} )
	public SessionDto getSessionById(@PathVariable ("session_id") Integer session_id) {
		Session session = service.getSessionById(session_id);
		return DtoConverter.convertToDto(session);
	}
}
