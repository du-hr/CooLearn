package ca.mcgill.ecse321.coolearn;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Abstraction of a session request that holds all relevant information of a request.
 * It is parcelable which allows for easy passage of information from one activity to another.
 *
 * @author group 11
 * @version 1.1
 */
public class SessionRequest implements Parcelable {
    private int id;
    private String status;
    private String startTime;
    private String endTime;
    private String date;
    private int tutorId;
    private List<String> studentNames;
    private List<Integer> studentIds;
    private List<Integer> reviewIds;
    private String tutorName;
    private String courseName;
    private int roomId;

    /**
     *  Constructor creates a new session request
     * @param id Session ID
     * @param status Status of this session
     * @param startTime Start time of this session (hh:mm:ss)
     * @param endTime End time of this session (hh:mm:ss)
     * @param date Date of this session in (yyyy-mm-dd)
     * @param tutorId ID of the tutor for this session
     * @param studentNames Names of the students registered for the session
     * @param studentIds IDs of the students registered for the session
     * @param reviewIds IDs of the reviews related to the session
     * @param tutorName Name of the tutor
     * @param courseName Name of the course
     * @param roomId Room number
     */
    public SessionRequest(int id, String status, String startTime, String endTime, String date, int tutorId, List<String> studentNames,
                          List<Integer> studentIds, List<Integer> reviewIds, String tutorName, String courseName, int roomId){
        this.id = id;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.tutorId = tutorId;
        this.studentNames = studentNames;
        this.studentIds = studentIds;
        this.reviewIds = reviewIds;
        this.tutorName = tutorName;
        this.courseName = courseName;
        this.roomId = roomId;
    }

    protected SessionRequest(Parcel in) {
        id = in.readInt();
        status = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        date = in.readString();
        tutorId = in.readInt();
        studentNames = in.createStringArrayList();
        tutorName = in.readString();
        courseName = in.readString();
        roomId = in.readInt();
    }

    public static final Creator<SessionRequest> CREATOR = new Creator<SessionRequest>() {
        @Override
        public SessionRequest createFromParcel(Parcel in) {
            return new SessionRequest(in);
        }

        @Override
        public SessionRequest[] newArray(int size) {
            return new SessionRequest[size];
        }
    };

    /**
     * @return this object as text to be displayed in the RequestListActivity.
     */
    @Override
    public String toString(){
        return "Course: " + courseName + "\n" + "Status: " + status + "\nDate: " + date + "\nStart Time: " + startTime + "\t\t\t\t\tEnd Time:" + endTime + "\nStudent(s): " + getStudents();
    }

    /**
     * @return Session ID
     */
    public int getId() {
        return id;
    }

    /**
     * @return Status of this session
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return Start Time of this session (hh:mm:ss)
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * @return End time of this session (hh:mm:ss)
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * @return Date of this session in (yyyy-mm-dd)
     */
    public String getDate() {
        return date;
    }

    /**
     * @return ID of the tutor for this session
     */
    public int getTutorId() {
        return tutorId;
    }

    /**
     * @return Names of the students registered for the session
     */
    public List<String> getStudentNames() {
        return studentNames;
    }

    /**
     * @return IDs of the students registered for the session
     */
    public List<Integer> getStudentIds() {
        return studentIds;
    }

    /**
     * @return IDs of the reviews related to the session
     */
    public List<Integer> getReviewIds() {
        return reviewIds;
    }

    /**
     * @return Name of the tutor
     */
    public String getTutorName() {
        return tutorName;
    }

    /**
     * @return Name of the course
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * @return Room number
     */
    public int getRoomId() {
        return roomId;
    }

    /**
     * @return The names of a student as a String with each name separated by a comma instead of a list.
     */
    public String getStudents(){
        String result = "";
        if(studentNames.size() > 1) {
            for (String student : studentNames) {
                result += student + ", ";
            }
        }
        else{
            result = studentNames.get(0);
        }
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(status);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeString(date);
        dest.writeInt(tutorId);
        dest.writeStringList(studentNames);
        dest.writeString(tutorName);
        dest.writeString(courseName);
        dest.writeInt(roomId);
    }
}