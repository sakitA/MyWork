/**
 * Department of Computer Engineering, Eastern Mediterranean University.
 * @link http://cmpe.emu.edu.tr
 *
 * @author Sakit Atakishiyev, Std. ID: 117319
 * @version 1.0.0.1
 */
package type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents course and its information. Course name, lectures time,
 * teacher name, group number, lab room and etc. information hold by this class
 *
 * @author Sakit
 */
public class Course implements Cloneable, Serializable {

    private String courseName;
    private String teacherName;
    private int courseGroup;
    private int courseMaxHour;
    private int[] courseTime;
    private boolean fixLectureTime;//to define that course lecture time is fixed or not
    private List<String> couresLabRooms;
    private List<String> courseReferenceCourses;
    private List<String> courseServiceCourses;
    private String[] references;//create one and use more time
    private String[] services;//create one and use more time

    /**
     * Default constructor to load default value.
     */
    public Course() {
        services = null;
        references = null;
        courseName = null;
        teacherName = null;
        courseTime = null;
        courseGroup = 0;
        courseMaxHour = 0;
        fixLectureTime = false;
        couresLabRooms = new ArrayList<>();
        courseServiceCourses = new ArrayList<>();
        courseReferenceCourses = new ArrayList<>();
    }

    /**
     * Constructor to initialize course name, teacher name, course group, and maximum lecture hour.
     * @param teacherName
     * @param courseName
     * @param courseGroup
     * @param maxHour 
     */
    public Course(String teacherName, String courseName, int courseGroup, int maxHour) {
        this();//firt call default constructor to initialize default value.
        this.teacherName = teacherName;
        this.courseName = courseName;
        this.courseGroup = courseGroup;
        courseMaxHour = maxHour;
        courseTime = new int[maxHour];
    }

    /**
     * 
     * @return teacher name of course
     */
    public String getTeacherName() {
        return teacherName;
    }

    /**
     * set teacher name of course
     * @param teacherName 
     */
    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    /**
     * 
     * @return course name
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * set course name
     * @param courseName 
     */
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    /**
     * 
     * @return course group number
     */
    public int getCourseGroup() {
        return courseGroup;
    }

    /**
     * set course group number
     * @param courseGroup 
     */
    public void setCourseGroup(int courseGroup) {
        this.courseGroup = courseGroup;
    }

    /**
     * 
     * @return maximum hour that can be apply in a week for this course
     */
    public int getCourseMaxHour() {
        return courseMaxHour;
    }

    /**
     * set maximum hour that can be apply in a week for this course
     * @param courseMaxHour 
     */
    public void setCourseMaxHour(int courseMaxHour) {
        this.courseMaxHour = courseMaxHour;
    }

    /**
     * Reference course is a course which students take at the same term
     * with this course in the same department
     * @return reference courses for this course
     */
    public String[] getReferences() {
        if (references == null) {
            references = courseReferenceCourses.toArray(new String[courseReferenceCourses.size()]);
        }
        return references;
    }

    /**
     * Service course is a course which students take with this course at the same
     * term but in different department.
     * @return services courses list for this course
     */
    public String[] getServices() {
        if (services == null) {
            services = courseServiceCourses.toArray(new String[courseServiceCourses.size()]);
        }
        return services;
    }

    /**
     * set course lecture time
     * @param day
     * @param slot
     * @param section 
     */
    public void setCourseTime(int day, int slot, int section) {
        courseTime[section] = 10 * day + slot;
    }

    /**
     * change course lecture time
     * @param day
     * @param slot
     * @param section 
     */
    public void changeCourseTime(int day, int slot, int section) {
        courseTime[section] = 10 * day + slot;
    }

    /**
     * add specially lab room for this course
     * @param roomName 
     */
    public void addCourseLabRoom(String roomName) {
        couresLabRooms.add(roomName);
    }

    /**
     * add reference course for this course.
     * @see #getReferences()  
     * @param courseName 
     */
    public void addReferenceCourse(String courseName) {
        courseReferenceCourses.add(courseName);
    }

    /**
     * add service course for this course
     * @param courseName 
     */
    public void addServiceCourse(String courseName) {
        courseServiceCourses.add(courseName);
    }

    /**
     * 
     * @param index
     * @return course lab room name
     */
    public String getCourseLabRoomName(int index) {
        return couresLabRooms.get(index);
    }

    /**
     * 
     * @return how many lab exist for this course
     */
    public int getCourseLabRoomsCount() {
        return couresLabRooms.size();
    }

    /**
     * 
     * @param roomName
     * @return true if the given room is lab room for this course
     */
    public boolean isCourseLabRoom(String roomName) {
        return couresLabRooms.contains(roomName);
    }

    /**
     * 
     * @param day
     * @param slot
     * @param section
     * @return true if course lectures time are the same.
     */
    public boolean isCourseSameDay(int day, int slot, int section) {
        return 10 * day + slot == courseTime[section];
    }

    /**
     * 
     * @return true if course's lecture times clash
     */
    public boolean isCourseTimeClash() {
        for (int section1 = 0; section1 < courseMaxHour; section1++) {
            for (int section2 = section1 + 1; courseTime[section1] != 0 && section2 < courseMaxHour; section2++) {
                if (courseTime[section2] != 0
                        && courseTime[section1] == courseTime[section2]) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 
     * @return unique hash code for identify the object
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.courseName);
        hash = 23 * hash + Objects.hashCode(this.teacherName);
        hash = 23 * hash + this.courseGroup;
        return hash;
    }

    /**
     * 
     * @param obj
     * @return true if the given object equals this object
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Course other = (Course) obj;
        if (!Objects.equals(this.courseName, other.courseName)) {
            return false;
        }
        if (!Objects.equals(this.teacherName, other.teacherName)) {
            return false;
        }
        return this.courseGroup == other.courseGroup;
    }

    /**
     * 
     * @return all lab rooms for this course
     */
    public List<String> getCouresLabRooms() {
        return couresLabRooms;
    }

    /**
     * set all lab rooms for this course
     * @param couresLabRooms 
     */
    public void setCouresLabRooms(List<String> couresLabRooms) {
        this.couresLabRooms = couresLabRooms;
    }

    /**
     * 
     * @return @see #getReferences() 
     */
    public List<String> getCourseReferenceCourses() {
        return courseReferenceCourses;
    }

    /**
     * set reference courses for this course
     * @param courseReferenceCourses 
     */
    public void setCourseReferenceCourses(List<String> courseReferenceCourses) {
        this.courseReferenceCourses = courseReferenceCourses;
    }

    /**
     * 
     * @return @see #getServices() 
     */
    public List<String> getCourseServiceCourses() {
        return courseServiceCourses;
    }

    /**
     * set all service courses for this course
     * @param courseServiceCourses 
     */
    public void setCourseServiceCourses(List<String> courseServiceCourses) {
        this.courseServiceCourses = courseServiceCourses;
    }

    /**
     * 
     * @return all information about the course
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(teacherName).append(",").append(courseName).append(", Group:").append(courseGroup);
        return sb.toString();
    }

    /**
     * 
     * @return copy this object
     * @throws CloneNotSupportedException 
     */
    @Override
    public Course clone() throws CloneNotSupportedException {
        Course course = (Course) super.clone();
        course.courseTime = new int[courseMaxHour];
        return course;
    }

    /**
     * set true if course's lecture times are fixed
     * @param isFixed 
     */
    public void setFixLectureTime(boolean isFixed){
        fixLectureTime = isFixed;
    }
    
    /**
     * 
     * @return true if course's lecture times are fixed.
     */
    public boolean isLectureTimeFixed(){
        return fixLectureTime;
    }
}
