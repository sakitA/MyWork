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
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import util.R;
import util.TeacherInfo;

/**
 * This class holds all information about the teacher.
 * Such as teacher's name, hard & soft constraints, weekly lectures information
 * and etc.
 * @author Sakit
 */
public class Teacher implements Cloneable, Serializable{
    
    private String teacherName;
    private int teacherMaxHour;
    private boolean[][] teacherHardConstraint;
    private boolean[][] teacherSoftConstraint;
    private TeacherInfo[][] teacherTime;
    private HashMap<String, List<String>> teacherRooms;
    
    /**
     * default constructor initialize default value for all parameters.
     */
    public Teacher() {
        teacherName = null;
        teacherMaxHour = 0;
        teacherHardConstraint = new boolean[R.MAX_DAY + 1][R.MAX_SLOT + 1];
        teacherSoftConstraint = new boolean[R.MAX_DAY + 1][R.MAX_SLOT + 1];
        teacherTime = null;
        teacherRooms = new HashMap<>();
    }
    
    /**
     * create new teacher object with the given name and teacher's max hour
     * @param name
     * @param hour 
     */
    public Teacher(String name, int hour) {
        this();
        teacherName = name;
        teacherMaxHour = hour;
    }

    /**
     * 
     * @return teacher's name
     */
    public String getTeacherName() {
        return teacherName;
    }

    /**
     * set teacher's name
     * @param teacherName 
     */
    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    /**
     * 
     * @return teacher's max hour in a day_
     */
    public int getTeacherMaxHour() {
        return teacherMaxHour;
    }

    /**
     * set teacher's maximum hour in a day
     * @param teacherMaxHour 
     */
    public void setTeacherMaxHour(int teacherMaxHour) {
        this.teacherMaxHour = teacherMaxHour;
    }

    /**
     * add hard constraint for teacher
     * @param day
     * @param slot 
     */
    public void addTeacherHardConstraint(int day, int slot) {
        teacherHardConstraint[day][slot] = true;
    }

    /**
     * add soft constraint for teacher
     * @param day
     * @param slot 
     */
    public void addTeacherSoftConstraint(int day, int slot) {
        teacherSoftConstraint[day][slot] = true;
    }

    /**
     * add specially room for the given lecture for teacher
     * @param courseName
     * @param roomName 
     */
    public void addTeacherRoom(String courseName, String roomName) {
        final List<String> temp = (teacherRooms.containsKey(courseName) ? teacherRooms.get(courseName) : new ArrayList<>());
        if (!temp.contains(roomName)) {
            temp.add(roomName);
        }
        teacherRooms.put(courseName, temp);
    }

    /**
     * 
     * @param courseName
     * @return how many specially rooms exist which teacher can gives his lectures
     */
    public int getTeacherRoomConstraintCount(String courseName) {
        return teacherRooms.get(courseName).size();
    }

    /**
     * add list of specially rooms for teacher
     * @param courseName
     * @param rooms 
     */
    public void addTeacherRoomConstraint(String courseName, List<String> rooms) {
        teacherRooms.put(courseName, rooms);
    }

    /**
     * 
     * @param course
     * @param index
     * @return room name for the given lecture
     */
    public String getTeacherRoomName(String course, int index) {
        return teacherRooms.get(course).get(index);
    }

    /**
     * set teacher info for one lecture
     * @param day
     * @param slot
     * @param section
     * @param group
     * @param room
     * @param lectureName 
     */
    public void setTeacherTime(int day, int slot, int section, int group, String room, String lectureName) {
        TeacherInfo info = new TeacherInfo(slot, group, room, lectureName);
        teacherTime[day][section] = info;
    }

    /**
     * 
     * @param day
     * @param slot
     * @return true if the given time is a hard constraint for teacher.
     */
    public boolean isTeacherHardConstraint(int day, int slot) {
        return teacherHardConstraint[day][slot];
    }

    /**
     * 
     * @param day
     * @param slot
     * @return true if the given time is a soft constraint for teacher.
     */
    public boolean isTeacherSoftConstraint(int day, int slot) {
        return teacherSoftConstraint[day][slot];
    }

    /**
     * 
     * @param courseName
     * @param roomName
     * @return true if the given room is specially room for the given 
     * lecture and teacher
     */
    public boolean isTeacherRoom(String courseName, String roomName) {
        return teacherRooms.get(courseName).contains(roomName);
    }

    /**
     * 
     * @param day
     * @param section
     * @return true if teacher has no any lecture at the given time
     */
    public boolean isTeacherFree(int day, int section) {
        if (teacherTime[day][section] == null) {
            return true;
        }
        return teacherTime[day][section].getSlot() == 0;
    }

    /**
     * 
     * @param day
     * @param courseGroup
     * @param courseName
     * @return @see #isSameLecture(int) 
     */
    public boolean isSameLecture(int day, int courseGroup, String courseName) {
        for (int section = 0; section < teacherMaxHour; section++) {
            if (teacherTime[day][section] != null) {
                if (teacherTime[day][section].getLectureName().equals(courseName)
                        && teacherTime[day][section].getGroup() == courseGroup) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 
     * @param day
     * @return true if teacher has the same lecture in a day
     */
    public boolean isSameLecture(int day) {
        for (int section1 = 0; section1 < teacherMaxHour; section1++) {
            for (int section2 = section1 + 1; teacherTime[day][section1] != null && section2 < teacherMaxHour; section2++) {
                if (teacherTime[day][section2] != null) {
                    if (teacherTime[day][section1].getLectureName().equals(
                            teacherTime[day][section2].getLectureName())
                            && teacherTime[day][section1].getGroup() == teacherTime[day][section2].getGroup()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 
     * @param day
     * @param slot
     * @return @see #isSameSlot(int) 
     */
    public boolean isSameSlot(int day, int slot) {
        for (int section = 0; section < teacherMaxHour; section++) {
            if (teacherTime[day][section] != null
                    && teacherTime[day][section].getSlot() == slot) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @param day
     * @return true if teacher's lectures are at the same time in the given day
     */
    public boolean isSameSlot(int day) {
        for (int section1 = 0; section1 < teacherMaxHour; section1++) {
            for (int section2 = section1 + 1; teacherTime[day][section1] != null && section2 < teacherMaxHour; section2++) {
                if (teacherTime[day][section2] != null) {
                    if (teacherTime[day][section1].getSlot()
                            == teacherTime[day][section2].getSlot()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    
    /**
     * 
     * @param day
     * @param slot
     * @return @see #isContinouslyHour(int) 
     */
    public boolean isContinouslyHour(int day, int slot) {
        for (int section = 0; section < teacherMaxHour; section++) {
            if (teacherTime[day][section] != null
                    && teacherTime[day][section].getSlot() != 0
                    && Math.abs(teacherTime[day][section].getSlot() - slot) == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @param day
     * @return true if there is no break between two lectures for teacher in a day
     */
    public boolean isContinouslyHour(int day) {
        for (int section1 = 0; section1 < teacherMaxHour; section1++) {
            for (int section2 = section1 + 1; teacherTime[day][section1] != null && section2 < teacherMaxHour; section2++) {
                if (teacherTime[day][section2] != null) {
                    if (teacherTime[day][section1].getSlot() != 0
                            && teacherTime[day][section2].getSlot() != 0
                            && Math.abs(teacherTime[day][section1].getSlot() - teacherTime[day][section2].getSlot()) == 1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * change swap teacher's lecture time
     * @param oldDay
     * @param oldSlot
     * @param newDay
     * @param newSlot
     * @param section
     * @return true if operation is successfully
     */
    public boolean changeTeacherHour(int oldDay, int oldSlot, int newDay, int newSlot, int section) {
        if (teacherTime[oldDay][section] != null && (teacherTime[newDay][section] == null
                || (oldDay == newDay && oldSlot != newSlot))) {

            teacherTime[newDay][section] = teacherTime[oldDay][section];
            teacherTime[newDay][section].changeSlot(newSlot);
            if (oldDay != newDay) {
                teacherTime[oldDay][section] = null;
            }
            return true;
        }
        return false;
    }
    
    public boolean changeTeacherHour(int oldDay, int oldSlot, int oldSection, int newDay, int newSlot, int newSection) {
        if (teacherTime[oldDay][oldSection] != null && (teacherTime[newDay][newSection] == null
                || (oldDay == newDay && oldSlot != newSlot))) {

            teacherTime[newDay][newSection] = teacherTime[oldDay][oldSection];
            teacherTime[newDay][newSection].changeSlot(newSlot);
            if (oldDay != newDay || oldSection!=newSection) {
                teacherTime[oldDay][oldSection] = null;
            }
            return true;
        }
        return false;
    }

    /**
     * 
     * @return teacher's hard constraints
     */
    public boolean[][] getTeacherHardConstraint() {
        return teacherHardConstraint;
    }

    /**
     * set teacher hard constraints
     * @param teacherHardConstraint 
     */
    public void setTeacherHardConstraint(boolean[][] teacherHardConstraint) {
        this.teacherHardConstraint = teacherHardConstraint;
    }

    /**
     * 
     * @return teacher soft constraints
     */
    public boolean[][] getTeacherSoftConstraint() {
        return teacherSoftConstraint;
    }

    /**
     * set teacher soft constraints
     * @param teacherSoftConstraint 
     */
    public void setTeacherSoftConstraint(boolean[][] teacherSoftConstraint) {
        this.teacherSoftConstraint = teacherSoftConstraint;
    }

    /**
     * 
     * @return unique hash code to identify the object
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.teacherName);
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
        final Teacher other = (Teacher) obj;

        return Objects.equals(this.teacherName, other.teacherName);
    }

    /**
     * 
     * @return all information about teacher. Such as teacher name, lecture info
     * in a week and etc.
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(teacherName).append(":\n");
        for (int section = 0; section < teacherMaxHour; section++) {
            for (int day = 1; day <= R.MAX_DAY; day++) {
                if (teacherTime[day][section] != null) {
                    sb.append("\t").append("Hour ").append(section)
                            .append(": ").append(teacherTime[day][section])
                            .append("on ").append(R.WEEK_DAY[day])
                            .append(section == teacherMaxHour - 1 ? "" : "\n");
                }
            }
        }
        return sb.toString();
    }

    /**
     * 
     * @return copy of the teacher object
     * @throws CloneNotSupportedException 
     */
    @Override
    public Teacher clone() throws CloneNotSupportedException {
        Teacher teacher = (Teacher) super.clone();
        teacher.teacherTime = new TeacherInfo[R.MAX_DAY + 1][teacherMaxHour];
        return teacher;
    }

    /**
     * 
     * @return all teacher info
     */
    public TeacherInfo[][] getTeacherInfo() {
        return teacherTime;
    }

    /**
     * 
     * @param day
     * @return teacher info at the given day.
     */
    public TeacherInfo[] getTeacherInfo(int day) {
        return teacherTime == null ? null : teacherTime[day];
    }
}
