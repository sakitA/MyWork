/**
 * Department of Computer Engineering, Eastern Mediterranean University.
 * @link http://cmpe.emu.edu.tr
 *
 * @author Sakit Atakishiyev, Std. ID: 117319
 * @version 1.0.0.1
 */
package GA;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.controlsfx.dialog.Dialogs;
import type.Course;
import type.Teacher;
import util.Info;
import util.Logger;
import util.Property;
import util.R;
import static util.R.prop;
import util.ShowMessage;

/**
 * This class control our chromosome. We create all individual population and
 * calculate its fitness value.
 *
 * @author Sakit
 */
public class TimeTable implements Comparable<TimeTable>, Serializable {

    private int ROOM_COUNT;
    private static final HashMap<Object, String> labTime = new HashMap<>();
    private static final Random rd = new Random();
    private final List<Teacher> teachers = new ArrayList<>();
    private final List<Course> courses = new ArrayList<>();
    private Info[][][] timeTable;
           
    private int fitness = 0;

    public TimeTable(){
         timeTable = new Info[R.MAX_DAY + 1][R.MAX_SLOT + 1][R.getRoomSize()];
    }
    /**
     * @see #setFitness(int)
     * @return fitness of chromosome
     */
    public int getFitness() {
        if (fitness == 0) {
            fitness = calculateFitness();
        }
        return fitness;
    }

    /**
     *
     * @param fitness is a measure of how good a solution. Best solution has low
     * fitness.
     */
    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    /**
     * add copy of teacher for doing operation
     *
     * @param teacher
     */
    protected void addTeacher(Teacher teacher) {
        teachers.add(teacher);
    }

    /**
     * add copy of course for doing operation
     *
     * @param course
     */
    protected void addCourse(Course course) {
        courses.add(course);
    }

    /**
     * set new chromosome/info
     *
     * @param day day of week
     * @param slot which time block of time
     * @param roomIndex which room
     * @param info chromosome
     */
    public void setInfo(int day, int slot, int roomIndex, Info info) {
        timeTable[day][slot][roomIndex] = info;
    }

    /**
     *
     * @return all chromosome information
     */
    public Info[][][] getTimeTable() {
        return timeTable;
    }

    /**
     *
     * @param day
     * @param slot
     * @param roomIndex
     * @return chromosome information at the given day and block time in the
     * given room
     */
    public Info getInfo(int day, int slot, int roomIndex) {
        return timeTable[day][slot][roomIndex];
    }

    private int calculateFitness() {
        fitness = 0;
        ROOM_COUNT = R.getRoomSize();
        for (int day = 1; day <= R.MAX_DAY; day++) {
            for (int slot = 1; slot <= R.MAX_SLOT; slot++) {
                for (int roomIndex = 0; roomIndex < ROOM_COUNT; roomIndex++) {
                    if (timeTable[day][slot][roomIndex] != null) {
                        fitness += calculateFitness(day, slot, timeTable[day][slot][roomIndex]);
                    }
                }
            }
        }
        return fitness;
    }

    /**
     * generate individual time table
     */
    public void generateTimeTable() {
        int day, slot, room, hour, index;
        boolean roomFlag, hourFlag, labRoomFlag, constraint;

        for (Course course : courses) {
            Teacher teacher = getTeacher(course.getTeacherName());
            Info info = null;
            if (course.isLectureTimeFixed()) {
                setFixTimeLecture(teacher, course, info);
                continue;
            }
            day = 0;
            slot = 0;
            room = -1;
            hour = -1;

            for (int section = 0, len = course.getCourseMaxHour(); section < len; section++) {
                hourFlag = false;

                if (section == 0) {
                    day = rd.nextInt(R.MAX_DAY) + 1;
                    slot = rd.nextInt(day == 3 ? 3 : 4) + 1;
                    index = rd.nextInt(teacher.getTeacherRoomConstraintCount(course.getCourseName()));
                    room = R.ALLR.indexOf(teacher.getTeacherRoomName(course.getCourseName(), index));
                    labRoomFlag = true;
                    roomFlag = isRoomFree(day, slot, room);
                } else if (section == 1) {
                    if (day == 1 && slot < 4) {
                        day = 3;
                    } else if (day + 2 > R.MAX_DAY) {
                        day = 1;
                    } else if (day != 1) {
                        day += 2;
                    }
                    labRoomFlag = true;
                    roomFlag = isRoomFree(day, slot, room);
                } else {
                    day = rd.nextInt(R.MAX_DAY) + 1;
                    slot = rd.nextInt(R.MAX_SLOT) + 1;
                    index = rd.nextInt(course.getCourseLabRoomsCount());
                    room = R.ALLR.indexOf(course.getCourseLabRoomName(index));
                    roomFlag = true;
                    hourFlag = true;
                    labRoomFlag = isRoomFree(day, slot, room);
                }

                for (int x = 0, len1 = teacher.getTeacherMaxHour(); section < 2 && x < len1; x++) {
                    hourFlag = isAllConstraintSatisfyByTeacher(teacher, course, day, slot, room, x);
                    if (hourFlag) {
                        hour = x;
                        break;
                    }
                }

                constraint = roomFlag && hourFlag && isAllConstraintSatisfyByCourse(course, room, section == 2);
                if (!constraint) {
                    modifyChromosome(teacher, course, info, section);
                } else if (!labRoomFlag) {
                    modifyChromosome(course, info, section);
                } else {
                    info = new Info(section == 2 ? null : teacher.getTeacherName(), course.getCourseName(), course.getCourseGroup(),
                            day, slot, hour, section, R.ALLR.get(room));
                    setInfo(day, slot, room, info);
                    if (section != 2) {
                        teacher.setTeacherTime(day, slot, hour, course.getCourseGroup(),
                                R.ALLR.get(room), course.getCourseName());
                    }
                    course.setCourseTime(day, slot, section);
                }
            }
        }
    }

    /**
     * calculate fitness value of the given chromosome
     *
     * @param day day of week
     * @param slot time block of the day
     * @param info chromosome
     * @return fitness value of the chromosome
     */
    public int calculateFitness(int day, int slot, Info info) {
        info.reset();
        int error = 0;
        int hard = 0;
        int soft = 0;
        String err;

        Teacher t = getTeacher(info.getTeacherName());
        Course c = getCourse(info.getCourseName(), info.getGroup());
        //check teacher constraint
        if (t != null) {
            if (t.isTeacherHardConstraint(day, slot)) {
                error += R.HARD_ERROR;
                ++hard;
                err = String.format("Day:%s, Time:%s is hard constraint for %s.",
                        R.WEEK_DAY[day], R.DAY_SLOT[slot], t.getTeacherName());
                info.addError(err);
            }

            if (t.isTeacherSoftConstraint(day, slot)) {
                ++error;
                ++soft;
                err = String.format("Day:%s, Time:%s is soft constraint for %s.",
                        R.WEEK_DAY[day], R.DAY_SLOT[slot], t.getTeacherName());
                info.addError(err);
            }

            if (t.isSameSlot(day)) {
                error += R.HARD_ERROR;
                ++hard;
                err = String.format("%s has two lectures at the same time on %s",
                        t.getTeacherName(), R.WEEK_DAY[day]);
                info.addError(err);
            }

            if (t.isContinouslyHour(day)) {
                error += R.HARD_ERROR;
                ++hard;
                err = String.format("%s lectures time are continously on %s",
                        t.getTeacherName(), R.WEEK_DAY[day]);
                info.addError(err);
            }

            if (t.isSameLecture(day)) {
                error += R.HARD_ERROR;
                ++hard;
                err = String.format("%s has same lecture on %s",
                        t.getTeacherName(), R.WEEK_DAY[day]);
                info.addError(err);
            }

            if (!t.isTeacherRoom(info.getCourseName(), info.getRoom())) {
                error += R.HARD_ERROR;
                ++hard;
                err = String.format("%s does not want to give any lecture in %s",
                        t.getTeacherName(), info.getRoom());
                info.addError(err);
            }

            if (day == 3 && slot > 3) {
                error += R.HARD_ERROR;
                ++hard;
                err = "On Wednesday is not able to assign any lecture to any teacher after 14:30";
                info.addError(err);
            }

            if (slot == 5) {
                error += R.HARD_ERROR;
                ++hard;
                err = "After 16:30 is not able to assign any lecture to any teacher";
                info.addError(err);
            }
            //end teacher constraint
        }

        //check course constraint
        if (t == null && !c.isCourseLabRoom(info.getRoom())) {
            error += R.HARD_ERROR;
            ++hard;
            err = String.format("%s is lab room for %s", info.getRoom(), info.getCourseName());
            info.addError(err);
        }

        if (c.isCourseTimeClash()) {
            error += R.HARD_ERROR;
            ++hard;
            err = String.format("%s clash with its own lectures time", info.getCourseName());
            info.addError(err);
        }

        //check reference course clash
        String[] references = c.getReferences();
        if (references != null) {
            for (String reference : references) {
                double totalError = 0;
                boolean isError = false;
                Integer g = R.COURSE_GROUP.get(reference);
                if (g != null) {
                    for (int i = 1; i <= g; i++) {
                        Course cor = getCourse(reference, g);
                        int temp_err = 0;
                        for (int section = 0, len = cor.getCourseMaxHour(); section < len; section++) {
                            if (cor.isCourseSameDay(day, slot, section)) {
                                ++temp_err;
                                err = String.format("There is clash with course:%s group:%d at %s on %s, section:%d",
                                        cor.getCourseName(), cor.getCourseGroup(),
                                        R.DAY_SLOT[slot], R.WEEK_DAY[day], section + 1);
                                info.addError(err);
                            }
                        }
                        if (temp_err == 0) {
                            isError = false;
                        } else if (g == 1 && temp_err != 0) {
                            isError = true;
                        } else {
                            totalError += (double) temp_err / cor.getCourseMaxHour();
                        }
                    }
                    if (!isError) {
                        error += 1000 * (totalError / g);
                        if (totalError != 0) {
                            ++soft;
                        }
                    } else {
                        error += R.HARD_ERROR;
                        ++hard;
                    }
                }
            }
        }

        //check service course clash
        String[] services = c.getServices();
        if (services != null) {
            for (String service : services) {
                int count = Integer.parseInt(service.substring(service.indexOf("%") + 1));
                boolean isError = false;
                double totalError = 0.0;
                for (int group = 1; group <= count; group++) {
                    //get all group hour and check that there is clash or not
                    String serviceCourseName = service.substring(0, service.indexOf("%"));
                    String temp = serviceCourseName + "%" + group + "_time";

                    //get service course sections
                    String[] sections = prop.getProperty(temp).split(",");
                    int temp_err = 0;
                    for (String section : sections) {
                        if (10 * day + slot == Integer.valueOf(section.substring(1))) {
                            err = String.format("There is clash with service course:%s group:%d at %s on %s",
                                    serviceCourseName, group,
                                    R.DAY_SLOT[slot], R.WEEK_DAY[day]);
                            info.addError(err);
                            ++temp_err;
                        }
                    }
                    if (temp_err == 0) {
                        isError = false;
                    } else if (count == 1 && temp_err != 0) {
                        //if there is only one group then this error hard constrain
                        //other case check other course group
                        isError = true;
                    } else {
                        totalError += (double) temp_err / sections.length;
                    }
                }
                if (isError) {
                    error += R.HARD_ERROR;
                    ++hard;
                } else if (!isError && services.length != 0) {
                    error += 1000 * (totalError / services.length);
                    if (totalError != 0) {
                        ++soft;
                    }
                }
            }
        }
        addChromosomeErrorDetail(info, error, hard, soft);
        return error;
    }

    public boolean isRoomFree(int day, int slot, int roomIndex) {
        if(day>R.MAX_DAY)
            System.err.println("Opsssssssssssssssssssssssss day broke");
        if(slot>R.MAX_SLOT)
            System.err.println("Opsssssssssssssssssssssssss slot broke");
        if(roomIndex>R.getRoomSize())
            System.err.println("Opsssssssssssssssssssssssss room broke");
        return timeTable[day][slot][roomIndex] == null;
    }

    private boolean isAllConstraintSatisfyByTeacher(Teacher teacher, Course course,
            int day, int slot, int roomIndex, int section) {
        if (!teacher.isTeacherFree(day, section)) {
            return false;
        }
        if (teacher.isTeacherHardConstraint(day, slot)) {
            return false;
        }
        if (teacher.isTeacherSoftConstraint(day, slot)) {
            return false;
        }
        if (!teacher.isTeacherRoom(course.getCourseName(), R.ALLR.get(roomIndex))) {
            return false;
        }
        if (teacher.isContinouslyHour(day, slot)) {
            return false;
        }
        if (teacher.isSameLecture(day, course.getCourseGroup(), course.getCourseName())) {
            return false;
        }
        return !teacher.isSameSlot(day, slot);
    }

    private boolean isAllConstraintSatisfyByCourse(Course course, int roomIndex, boolean isLab) {
        if (isLab && !course.isCourseLabRoom(R.ALLR.get(roomIndex))) {
            return false;
        }
        return !course.isCourseTimeClash();
    }

    private void addChromosomeErrorDetail(Info info, int chromosomeError, int hardConstrainCount, int softConstrainCount) {
        info.setError(chromosomeError);
        info.setHardConstraintError(hardConstrainCount);
        info.setSoftConstraintError(softConstrainCount);
        info.setStrong(chromosomeError == 0);
        info.setGood(chromosomeError > 0 && chromosomeError < R.HARD_ERROR);
        info.setDefect(chromosomeError >= R.HARD_ERROR);
    }

    /**
     *
     * @param teacherName
     * @return teacher object
     */
    public Teacher getTeacher(String teacherName) {
        if (teacherName == null) {
            return null;
        }
        for (Teacher t : teachers) {
            if (t.getTeacherName().equals(teacherName)) {
                return t;
            }
        }
        return null;
    }

    /**
     *
     * @param courseName
     * @param group
     * @return course object
     */
    public Course getCourse(String courseName, int group) {
        for (Course c : courses) {
            if (c.getCourseName().equals(courseName)
                    && c.getCourseGroup() == group) {
                return c;
            }
        }
        return null;
    }

    private void modifyChromosome(Teacher teacher, Course course, Info info, int section) {
        boolean check = false;
        int hour, index;
        final int count = teacher.getTeacherRoomConstraintCount(course.getCourseName());
        for (int day = 1; !check && day <= R.MAX_DAY; day++) {
            for (int slot = 1; !check && slot < R.MAX_SLOT; slot++) {
                if (day == 3 && slot > 3) {
                    break;
                }

                if (teacher.isSameLecture(day, course.getCourseGroup(), course.getCourseName())) {
                    break;
                }

                if (teacher.isSameSlot(day, slot)) {
                    continue;
                }

                if (teacher.isContinouslyHour(day, slot)) {
                    continue;
                }

                hour = -1;
                for (int x = 0, len = teacher.getTeacherMaxHour(); hour == -1 && x < len; x++) {
                    if (teacher.isTeacherFree(day, x)) {
                        hour = x;
                    }
                }

                for (int room = 0; !check && hour != -1 && room < count; room++) {
                    index = R.ALLR.indexOf(teacher.getTeacherRoomName(course.getCourseName(), room));
                    if (!isRoomFree(day, slot, index)) {
                        continue;
                    }

                    info = new Info(teacher.getTeacherName(), course.getCourseName(), course.getCourseGroup(),
                            day, slot, hour, section, R.ALLR.get(index));
                    setInfo(day, slot, index, info);
                    teacher.setTeacherTime(day, slot, hour, course.getCourseGroup(), R.ALLR.get(index), course.getCourseName());
                    course.setCourseTime(day, slot, section);
                    check = true;
                }
            }
        }
    }

    private void modifyChromosome(Course course, Info info, int section) {
        boolean check = false;
        int index;
        final int count = course.getCourseLabRoomsCount();
        for (int day = R.MAX_DAY; !check && day > 0; day--) {
            for (int slot = 1; !check && slot <= R.MAX_SLOT; slot++) {
                for (int room = 0; !check && room < count; room++) {
                    index = R.ALLR.indexOf(course.getCourseLabRoomName(room));
                    if (!isRoomFree(day, slot, index)) {
                        continue;
                    }
                    if (course.isCourseTimeClash()) {
                        continue;
                    }
                    info = new Info(null, course.getCourseName(), course.getCourseGroup(),
                            day, slot, -1, 2, R.ALLR.get(index));
                    setInfo(day, slot, index, info);
                    course.setCourseTime(day, slot, section);
                    check = true;
                }
            }
        }

    }

    /**
     *
     * @param that
     * @return -1 if this chromosome fitness less than the given chromosome
     * fitness value, 0 if they are equal, 1 if this chromosome fitness value
     * more than the given chromosome
     */
    @Override
    public int compareTo(TimeTable that) {
        if (this.fitness < that.fitness) {
            return -1;
        }
        if (this.fitness > that.fitness) {
            return 1;
        }
        return 0;
    }

    private void setFixTimeLecture(Teacher teacher, Course course, Info info) {
        Logger.LOGI("Start to initialize fix time course");
        String key = course.getCourseName() + "%" + course.getCourseGroup();
        String[] time = Property.getCourseTime(course.getCourseName(), course.getCourseGroup()).split(",");
        String labhour = null;
        if (!labTime.containsKey(key)) {
            List<String> groups = new ArrayList<>();
            groups.addAll(Arrays.asList(time));
            Optional<String> responses = Dialogs.create()
                    .title("Set lecture lab hour")
                    .message("Choose which time is lab hour for\n" + course.getCourseName()
                            + ", group " + course.getCourseGroup() + ", " + teacher.getTeacherName() + ":")
                    .showChoices(groups);
            if (!responses.isPresent()) {
                ShowMessage.showWarning("You must select lab hour.");
                setFixTimeLecture(teacher, course, info);
            } else {
                labhour = responses.get();
            }
        } else {
            labhour = labTime.get(key);
        }

        int section = 0;
        for (String s : time) {
            int ds = Integer.parseInt(s.substring(1));
            int day = ds / 10;
            int slot = ds % 10;
            String room = null;
            for (String ss : R.ALLR) {
                if (s.equals(labhour)) {
                    if (course.isCourseLabRoom(ss) && isRoomFree(day, slot, R.ALLR.indexOf(ss))) {
                        room = ss;
                        break;
                    }
                } else {
                    if (teacher.isTeacherRoom(course.getCourseName(), ss) && isRoomFree(day, slot, R.ALLR.indexOf(ss))) {
                        room = ss;
                        break;
                    }
                }
            }
            if (room == null) {
                Logger.LOGS("room can not assinged. you first assign fix time courses!!!!!!!!!!!!!!!!!!!!");
                ShowMessage.showError(new Exception("Error happened during initialize process. Please start program again"
                        + ".\nProgram can not assign room"));
                System.exit(1);
            }
            int hour = -1;
            for (int i = 0; i < teacher.getTeacherMaxHour(); i++) {
                if (teacher.isTeacherFree(day, i)) {
                    hour = i;
                    break;
                }
            }
            if (hour == -1) {
                Logger.LOGS("teacher hour can not assign. you first assign fix time courses!!!!!!!!!!!!!!!!!");
                ShowMessage.showError(new Exception("Error happened during initialize process. Please start program again.\n"
                        + "Program can not assign teacher hour."));
                System.exit(1);
            }
            info = new Info(s.equals(labhour)
                    ? null : teacher.getTeacherName(), course.getCourseName(), course.getCourseGroup(),
                    day, slot, hour, section, room);
            setInfo(day, slot, R.ALLR.indexOf(room), info);
            if (!s.equals(labhour)) {
                teacher.setTeacherTime(day, slot, hour, course.getCourseGroup(),
                        room, course.getCourseName());
            }
            course.setCourseTime(day, slot, s.equals(labhour) ? course.getCourseMaxHour() - 1 : section);
            if (!s.equals(labhour)) {
                ++section;
            }
            labTime.put(key,labhour);
        }
    }
}
