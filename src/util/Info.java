/**
 * Department of Computer Engineering, Eastern Mediterranean University.
 * @link http://cmpe.emu.edu.tr
 *
 * @author Sakit Atakishiyev, Std. ID: 117319
 * @version 1.0.0.1
 */
package util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represent chromosome of the genetic algorithm. Holds all information
 * about the time table for one course for one day.
 * @author Sakit
 */
public class Info implements Serializable {

    private String teacherName;
    private String courseName;
    private String room;
    private int group;
    private int day;
    private int slot;
    private int hour;
    private int section;
    private boolean strong;//there is no viloated hard constraints
    private boolean good;//only soft constraints violated
    private boolean defect;//more than one hard contraints violated
    private int error;//total viloated constraints
    private int hardConstraintError;//how many hard constraints viloated
    private int softConstraintError;//how many soft constraint viloated
    private List<String> errors;//all viloated constraints information 

    /**
     * Default constructor to load default values
     */
    public Info() {
        teacherName = null;
        courseName = null;
        room = null;
        group = 0;
        day = 0;
        slot = -1;
        hour = -1;
        section = 0;
        strong = false;
        good = false;
        defect = false;
        error = 0;
        hardConstraintError = 0;
        softConstraintError = 0;
        errors = new ArrayList<>();
    }

    /**
     * Initialize info object with the given teacher name, course name
     * group number, day slot, hour, section and room name
     * @param teacherName name of teacher which gives this course
     * @param courseName name of course
     * @param groupNumber group number of course
     * @param day which day this course will be
     * @param slot which time this course will be on the day 
     * @param hour teacher's lecture hour
     * @param section course's lecture hour. In our department every course has 3 section. 2 lecture, one lab
     * @param roomName name of room which this course will be.
     */
    public Info(String teacherName, String courseName, int groupNumber,
            int day, int slot, int hour, int section, String roomName) {
        this();
        this.teacherName = teacherName;
        this.courseName = courseName;
        group = groupNumber;
        this.day = day;
        this.slot = slot;
        room = roomName;
        this.hour = hour;
        this.section = section;
    }

    /**
     * 
     * @return teacher name
     */
    public String getTeacherName() {
        return teacherName;
    }

    /**
     * set teacher name for this course
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
     * @return room where course will be
     */
    public String getRoom() {
        return room;
    }

    /**
     * set room where course will be
     * @param room 
     */
    public void setRoom(String room) {
        this.room = room;
    }

    /**
     * 
     * @return group number of course
     */
    public int getGroup() {
        return group;
    }

    /**
     * set group number of course
     * @param group 
     */
    public void setGroup(int group) {
        this.group = group;
    }

    /**
     * 
     * @return day when course will be
     */
    public int getDay() {
        return day;
    }

    /**
     * set day when course will be
     * @param day 
     */
    public void setDay(int day) {
        this.day = day;
    }

    /**
     * 
     * @return time when course will be on the day
     */
    public int getSlot() {
        return slot;
    }

    /**
     * set time of course will be on the day
     * @param slot 
     */
    public void setSlot(int slot) {
        this.slot = slot;
    }

    /**
     * 
     * @return teacher's lecture hour
     */
    public int getHour() {
        return hour;
    }

    /**
     * set teacher's lecture hour
     * @param hour 
     */
    public void setHour(int hour) {
        this.hour = hour;
    }

    /**
     * 
     * @return which section of course
     */
    public int getSection() {
        return section;
    }

    /**
     * 
     * @param section In our department each course has 3 section. 2 of them are lecture section
     * and one of them for lab/tutorial
     */
    public void setSection(int section) {
        this.section = section;
    }

    /**
     * 
     * @return true if there is no any violated constraints
     */
    public boolean isStrong() {
        return strong;
    }

    /**
     * set that this chromosome is strong or not
     * @param strong if there is no any violated constraints then this chromosome
     * is strong.
     */
    public void setStrong(boolean strong) {
        this.strong = strong;
    }

    /**
     * 
     * @return true if only soft constraints are violated
     */
    public boolean isGood() {
        return good;
    }

    /**
     * set that chromosome if good or not
     * @param good if only soft constraints are violated then this chromosome is
     * good
     */
    public void setGood(boolean good) {
        this.good = good;
    }

    /**
     * 
     * @return return true if there are more than one violated hard constraints
     */
    public boolean isDefect() {
        return defect;
    }

    /**
     * set that chromosome is defected or not
     * @param defect if chromosome violated more than one hard constraints
     * then this chromosome is defected
     */
    public void setDefect(boolean defect) {
        this.defect = defect;
    }

    /**
     * 
     * @return total violated constraints
     */
    public int getError() {
        return error;
    }

    /**
     * set total violated constraints
     * @param error 
     */
    public void setError(int error) {
        this.error = error;
    }

    /**
     * 
     * @return how many hard constraints violated
     */
    public int getHardConstraintError() {
        return hardConstraintError;
    }

    /**
     * set how many hard constraints violated
     * @param hardConstraintError 
     */
    public void setHardConstraintError(int hardConstraintError) {
        this.hardConstraintError = hardConstraintError;
    }

    /**
     * 
     * @return how many soft constraints violated
     */
    public int getSoftConstraintError() {
        return softConstraintError;
    }

    /**
     * set how many soft constraints violated
     * @param softConstraintError 
     */
    public void setSoftConstraintError(int softConstraintError) {
        this.softConstraintError = softConstraintError;
    }

    /**
     * 
     * @return all information about violated information
     */
    public String getErrors() {
        final StringBuilder sb = new StringBuilder();
        for (String err : errors) {
            sb.append(err).append("\n");
        }
        if (sb.length() != 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.length()==0? "No constraints violated":sb.toString();
    }

    /**
     * add information about the violated constraint
     * @param error 
     */
    public void addError(String error) {
        errors.add(error);
    }

    /**
     * reset all values and initialize default values
     */
    public void reset() {
        defect = false;
        good = false;
        strong = false;
        hardConstraintError = 0;
        softConstraintError = 0;
        error = 0;
        errors.clear();
    }

    /**
     * 
     * @return unique hash code for identify the object
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.teacherName);
        hash = 79 * hash + Objects.hashCode(this.courseName);
        hash = 79 * hash + this.group;
        return hash;
    }

    /**
     * 
     * @param obj
     * @return if the given object equals this object
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Info other = (Info) obj;
        if (!Objects.equals(this.teacherName, other.teacherName)) {
            return false;
        }
        if (!Objects.equals(this.courseName, other.courseName)) {
            return false;
        }
        return this.group == other.group;
    }

    /**
     * 
     * @return all information about the chromosome
     */
    @Override
    public String toString() {

        final StringBuilder sb = new StringBuilder();
        final String status;
        if (strong) {
            status = R.STATUS[0];
        } else if (good) {
            status = R.STATUS[1];
        } else if (defect) {
            status = R.STATUS[2];
        } else {
            status = "UNKNOWN";
        }
        sb
                .append("Teacher:").append(teacherName == null ? "No teacher" : teacherName).append(".\n")
                .append("Course:").append(courseName).append(".\n")
                .append("Group:").append(group).append("\n")
                .append("Room:").append(room).append(".\n")
                .append("Day:").append(R.WEEK_DAY[day]).append(".\n")
                .append("Time:").append(R.DAY_SLOT[slot]).append(".\n")
                .append("Fitness:").append(error).append(".\n")
                .append("Violated constraints detail:").append(getErrors()).append(".\n")
                .append("Status:").append(status).append(".");
        return sb.toString();
    }
}
