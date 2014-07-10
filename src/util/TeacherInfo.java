/**
 * Department of Computer Engineering, Eastern Mediterranean University.
 * @link http://cmpe.emu.edu.tr
 *
 * @author Sakit Atakishiyev, Std. ID: 117319
 * @version 1.0.0.1
 */
package util;

import java.io.Serializable;

/**
 * This helper class for teachers. To held teacher info, such as teacher lecture
 * time, name, and room.
 *
 * @author Sakit
 */
public class TeacherInfo implements Serializable{

    private String lectureName;
    private String roomName;
    private int slot;
    private int group;

    /**
     * default constructor to initialize teacher's lecture info in a day
     *
     * @param slot
     * @param group
     * @param roomName
     * @param lectureName
     */
    public TeacherInfo(int slot, int group, String roomName, String lectureName) {
        this.slot = slot;
        this.group = group;
        this.roomName = roomName;
        this.lectureName = lectureName;
    }

    /**
     *
     * @return teacher's lecture name
     */
    public String getLectureName() {
        return lectureName;
    }

    /**
     * set teacher's lecture name
     *
     * @param lectureName
     */
    public void setLectureName(String lectureName) {
        this.lectureName = lectureName;
    }

    /**
     *
     * @return room name where teacher gives lecture
     */
    public String getRoomName() {
        return roomName;
    }

    /**
     * set room where teacher gives lecture
     *
     * @param roomName
     */
    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    /**
     *
     * @return teacher's lecture time
     */
    public int getSlot() {
        return slot;
    }

    /**
     * set teacher lecture time
     *
     * @param slot
     */
    public void setSlot(int slot) {
        this.slot = slot;
    }

    /**
     *
     * @return teacher's lecture group number
     */
    public int getGroup() {
        return group;
    }

    /**
     * set teacher's lectures group number
     *
     * @param group
     */
    public void setGroup(int group) {
        this.group = group;
    }

    /**
     * change teacher's lecture time
     * @param slot 
     */
    public void changeSlot(int slot) {
        this.slot = slot;
    }

    /**
     *
     * @return teacher's info for one lecture.
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(lectureName).append(", Group").append(group).append(" in ").append(roomName)
                .append(" at ").append(R.DAY_SLOT[slot]);
        return sb.toString();
    }
}
