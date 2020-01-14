package ca.mcgill.ecse321.coolearn.controller;

import ca.mcgill.ecse321.coolearn.model.*;
import ca.mcgill.ecse321.coolearn.dto.*;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

public abstract class DtoConverter {
	public static AvailabilityDto convertToDto(Availability o) {
		return new AvailabilityDto(o.getStartTime(), 
						o.getEndTime(), o.getStartDate(), o.getEndDate(),
						o.getDayOfWeek(),o.getId());
	}
	
	public static CoolearnUserDto convertToDto(CoolearnUser o) {
		CoolearnUserDto d = new CoolearnUserDto(o.getFirstName(), o.getLastName(),
						o.getEmailAddress(), o.getPassword());
		List<Integer> urs = new ArrayList<Integer>();
		Set<UserRole> list_userRole = o.getUserRole();
		if(list_userRole != null) {
			for (UserRole ur : o.getUserRole()) {
				urs.add(ur.getId());
			}
			d.setUserRole(urs);
		}	
		return d;
	}
	public static CourseDto convertToDto(Course o) {
		List<Integer> courses = new ArrayList<Integer>();
		if(o.getSpecificCourses() != null) {
			for (SpecificCourse sc : o.getSpecificCourses()) {
				courses.add(sc.getId());
			}
		}
		return new CourseDto(o.getName(), o.getEducationalLevel(),
					o.getSubject().getSubjectName(), courses);
	}
	public static ReviewDto convertToDto(Review o) {
		return new ReviewDto(o.getRating(),o.getComment(), o.getSession().getId(),
						o.getId(),o.getUserRole().getId());
	}
	public static RoomDto convertToDto(Room o) {
		List<Integer> sessions = new ArrayList<Integer>();
		for (Session s : o.getSession()) sessions.add(s.getId());
		return new RoomDto(o.getRoomNumber(),sessions,o.getRoomsize());
	}
	public static SessionDto convertToDto(Session o) {
		List<String> studs = new ArrayList<String>();
		List<Integer> studsId= new ArrayList<Integer>();
		List<Integer> revs = new ArrayList<Integer>();
		for (Student s : o.getStudent()){
			studs.add(s.getUser().getFirstName()+" "+s.getUser().getLastName());
			studsId.add(s.getId());
		} 
		if(o.getReview() != null) {
			for (Review r : o.getReview()) revs.add(r.getId());
		}
		int r = 0;
		if(o.getRoom() != null) {
			r = o.getRoom().getRoomNumber();
		}
		return new SessionDto(o.getStatus(), o.getStartTime(), o.getEndTime(),o.getDate(),
						studs,studsId,o.getTutor().getUser().getFirstName()+" "+o.getTutor().getUser().getLastName(),o.getTutor().getId(), o.getCourse().getName(),
						o.getId(),r, revs);
	}
	public static SpecificCourseDto convertToDto(SpecificCourse o) {
		return new SpecificCourseDto(o.getHourlyRate(), o.getTutor().getId(),
						o.getCourse().getName(), o.getId(), o.getTutor().getUser().getFirstName() + " " + o.getTutor().getUser().getLastName());
	}	
	public static SubjectDto convertToDto(Subject o) {
		List<String> courses = new ArrayList<String>();
		for (Course c : o.getCourse()) courses.add(c.getName());
		return new SubjectDto(o.getSubjectName(), courses);
	}
	public static UserRoleDto convertToDto(UserRole o) { //TODO check null 
		List<Integer> revs = new ArrayList<Integer>();
		if(o.getReview() != null) {
			for (Review r : o.getReview()) {
				revs.add(r.getId());
			}
		}
		
		if (o instanceof Tutor) {
			List<Integer> avs = new ArrayList<Integer>();
			List<Integer> sess = new ArrayList<Integer>();
			List<Integer> courses = new ArrayList<Integer>();
			if(((Tutor)o).getAvailability() != null) {
				for (Availability a : ((Tutor)o).getAvailability()) {
					avs.add(a.getId());
				}
			}
			if(((Tutor)o).getSession() != null) {
				for (Session s : ((Tutor)o).getSession()) {
					sess.add(s.getId());
				}
			}
			
			if(((Tutor)o).getTeachingCourses() != null) {
				for (SpecificCourse sc : ((Tutor)o).getTeachingCourses()) {
					courses.add(sc.getId()); 
				}
			}
			
			
			TutorDto t = new TutorDto(avs, sess,courses);
			t.setId(o.getId());
			if(o.getReview() != null) {
				t.setReviewIds(revs);
			}
			t.setUser(o.getUser().getEmailAddress());
			t.setName(o.getUser().getFirstName()+" "+o.getUser().getLastName());
			return t;
		}
		else if (o instanceof Student) {
			List<Integer> sessions = new ArrayList<Integer>();
			if(((Student)o).getSession() != null) {
				for (Session s : ((Student)o).getSession()) {
					sessions.add(s.getId());
				}
			}
			
			StudentDto s = new StudentDto(sessions);
			s.setId(o.getId());
			if(o.getReview() != null) {
				s.setReviewIds(revs);
			}
			s.setUser(o.getUser().getEmailAddress());
			s.setName(o.getUser().getFirstName()+" "+o.getUser().getLastName());
			return s;
		}
		else {
			return new UserRoleDto(o.getUser().getEmailAddress(), revs, o.getId());
		}
	}
}
