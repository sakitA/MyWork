/**
 * Department of Computer Engineering, Eastern Mediterranean University.
 * @link http://cmpe.emu.edu.tr
 *
 * @author Sakit Atakishiyev, Std. ID: 117319
 * @version 1.0.0.1
 */
package util;

import controller.SetresourceController;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import javafx.scene.image.Image;
import type.Course;
import type.Teacher;

/**
 * This class contains all constant variables and resources such as image, icon,
 * messages and etc.
 *
 * @author Sakit
 */
@SuppressWarnings("StaticNonFinalUsedInInitialization")
public class R {

    private static File file;
    private static File term;
    public static final Properties prop = new Properties();
    public static HashMap<Object, Integer> COURSE_GROUP = new HashMap<>();
    public static HashMap<Object, Teacher> TEACHERS = new HashMap<>();
    public static HashMap<Object, Course> COURSES = new HashMap<>();
    public static List<String> ALLR = new ArrayList<>();
    public static List<Course> ALLC = new ArrayList<>();
    public static final String CMPE_URL = "http://cmpe.emu.edu.tr";
    public static final String path = System.getProperty("user.home") + File.separator + "timetable";
    public static final String SA_URL = "http://www.linkedin.com/in/sakitatakishiyev";
    public static final DatabaseOperation db = new DatabaseOperation();
    public static final Integer MAX_TERM = 8;
    public static final Integer MAX_DAY = 5;
    public static final Integer MAX_SLOT = 5;
    public static int roomCount;
    public static final int HARD_ERROR = 100000;
    public static final String[] WEEK_DAY = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    public static final String[] DAY_SLOT = {"", "8:30-10:20", "10:30-12:20", "12:30-14:20", "14:30-16:20", "16:30-18:20"};
    public static final String[] COURSE_CATEGORY = {"Core", "Service", "Area"};
    public static final String[] LANGUAGE = {"English", "Turkish"};
    public static final String[] COURSE_TYPE = {"CMPE", "CMSE", "BLGM"};
    public static final String[] STATUS = {"STRONG", "GOOD", "DEFECT"};

    /*------------------images-------------------------------------------*/
    public static final Image emu_icon = new Image("/images/emu_icon.jpg");
    public static final Image blue_add = new Image("/images/gadd.png");
    public static final Image grey_add = new Image("/images/badd.png");
    public static final Image blue_clock = new Image("/images/gclock.png");
    public static final Image grey_clock = new Image("/images/bclock.png");
    public static final Image blue_save = new Image("/images/gsave.png");
    public static final Image grey_save = new Image("/images/bsave.png");
    public static final Image correct = new Image("/images/correct.png");
    public static final Image wrong = new Image("/images/wrong.png");
    public static final Image tec_add = new Image("/images/teacher_add.png");
    public static final Image tec_delete = new Image("/images/teacher_delete.png");
    public static final Image tec_edit = new Image("/images/teacher_edit.png");
    public static final Image room_add = new Image("/images/room_add.png");
    public static final Image room_delete = new Image("/images/room_delete.png");
    public static final Image room_edit = new Image("/images/room_edit.png");
    public static final Image course_add = new Image("/images/course_add.png");
    public static final Image course_delete = new Image("/images/course_delete.png");
    public static final Image course_edit = new Image("/images/course_edit.png");
    public static final Image doc = new Image("/images/doc.png");
    public static final Image pdf = new Image("/images/pdf.png");
    public static final Image emuicon = new Image("/images/emuicon.png");

    // load property file if exist otherwise create new one and save all default
    // values in this file.
    static {
        Logger.LOGI("Starting to load properties data either default or existing one.");
        try {
        	if(!db.isConnected())
        		db.connectDB();
        	roomCount = db.getResult(DatabaseOperation.GET_ROOM, "name").size();
            file = new File(path + File.separator + "properties.dat");
            if (file.exists()) {
                prop.load(new FileInputStream(file));
                Logger.LOGI("Loaded from file");
            } else {
                if (!file.exists() && file.getParentFile().mkdir()) {
                    if (!file.createNewFile()) {
                        Logger.LOGS("file can not be created");
                    } else {
                        Logger.LOGI("file created successfully");
                    }
                }

                prop.load(R.class.getResourceAsStream("/data/default_prop.dat"));
                prop.store(new FileOutputStream(file), null);
                prop.load(new FileInputStream(file));
                Logger.LOGI("Default data store to the local file and loaded");
            }
        } catch (IOException ex) {
            Logger.LOGS(ex.getMessage());
            ShowMessage.showError(ex);
            System.exit(1);
        }
    }

    /**
     * store property file.
     */
    public static void storeProp() {
        try {
            prop.store(new FileOutputStream(file), null);
        } catch (FileNotFoundException ex) {
            Logger.LOGS(ex.getMessage());
            ShowMessage.showError(ex);
        } catch (IOException ex) {
            Logger.LOGS(ex.getMessage());
            ShowMessage.showError(ex);
        }
    }

    public static void initialize() {
        ALLC.clear();
        ALLR.clear();
        TEACHERS.clear();
        COURSES.clear();
        Logger.LOGI("Starting to read file");
        readFile(term);
    }

    //read all teachers, opening courses and their constraints from file and 
    //create releated objects for each of them.
    private static void readFile(File term) {
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(term);
            br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] info = line.split(",");
                //if teacher exist we do not need create a new object for his/her
                Teacher tec = TEACHERS.containsKey(info[0])? TEACHERS.get(info[0]):
                        new Teacher(info[0],Integer.valueOf(Property.getTeacherMaxHour(info[0])));
                Course cor = new Course(info[0], info[1], Integer.parseInt(info[2]), 
                        Integer.parseInt(Property.getCourseMaxHour(info[1])));
                if(!COURSES.containsKey(info[1])){
                    for (int i = 3; i < info.length; i++) {
                        if (info[i].startsWith("@")) {
                            cor.addReferenceCourse(info[i].substring(1));
                        } else if (info[i].startsWith("$")) {
                            cor.addServiceCourse(info[i].substring(1));
                        }
                    }
                    
                    String[] rooms = Property.getCourseRoom(info[1]).split(",");
                    for (String s : rooms) {
                        if (s.length() != 0) {
                            cor.addCourseLabRoom(s);
                        }
                    }
                    
                    COURSES.put(info[1], cor);
                    String[] room = Property.getTeacherRoom(info[0], info[1]).split(",");
                    for (String s : room) {
                        if (s.length() != 0) {
                            tec.addTeacherRoom(info[1], s);
                        }
                    }
                }else{
                    cor.setCouresLabRooms(COURSES.get(info[1]).getCouresLabRooms());
                    cor.setCourseReferenceCourses(COURSES.get(info[1]).getCourseReferenceCourses());
                    cor.setCourseServiceCourses(COURSES.get(info[1]).getCourseServiceCourses());
                    
                    String[] room = Property.getTeacherRoom(info[0], info[1]).split(",");
                    for (String s : room) {
                        if (s.length() != 0) {
                            tec.addTeacherRoom(info[1], s);
                        }
                    }                    
                }
                cor.setFixLectureTime(Property.getCourseTime(info[1],Integer.parseInt(info[2]))!=null);
                
                if (!TEACHERS.containsKey(info[0])) {
                    String hard = Property.getTeacherHardConstraint(info[0]);
                    String soft = Property.getTeacherSoftConstraint(info[0]);
                    if(hard!=null){
                        for(String s:hard.split(",")){
                            int ds = Integer.parseInt(s.substring(1));
                            tec.addTeacherHardConstraint(ds/10, ds%10);
                        }
                    }
                    
                    if(soft!=null){
                        for(String s:soft.split(",")){
                            int ds = Integer.parseInt(s.substring(1));
                            tec.addTeacherSoftConstraint(ds/10, ds%10);
                        }
                    }
                    TEACHERS.put(info[0], tec);
                }
                ALLC.add(cor);
            }
            if(!SetresourceController.db.isConnected())
                SetresourceController.db.connectDB();
            ALLR.addAll(SetresourceController.db.getResult(DatabaseOperation.GET_ROOM, "name"));
            Logger.LOGI("All room added and start to sort");
            sort();
            Logger.LOGI("sort finish and reading file finish succesfully");
        } catch (FileNotFoundException ex) {
            Logger.LOGS(ex.getMessage());
            ShowMessage.showError(ex);
        } catch (IOException ex) {
            Logger.LOGS(ex.getMessage());
            ShowMessage.showError(ex);
        }  finally {
            if (fr != null) {
                try {
                    fr.close();
                    if (br != null) {
                        br.close();
                    }
                } catch (IOException ex) {
                    Logger.LOGS(ex.getMessage());
                    ShowMessage.showError(ex);
                }
            }
        }
    }
    
    private static void sort() {
        Course[] c = ALLC.toArray(new Course[ALLC.size()]);

        for (int i = 0; i < c.length; i++) {
            for (int j = i + 1; j < c.length; j++) {
                if (c[i].getCourseLabRoomsCount() > c[j].getCourseLabRoomsCount()) {
                        swap(c, i, j);
                } else if (c[i].getCourseLabRoomsCount() == c[j].getCourseLabRoomsCount()) {
                    if (TEACHERS.get(c[i].getTeacherName()).getTeacherRoomConstraintCount(c[i].getCourseName())
                            < TEACHERS.get(c[j].getTeacherName()).getTeacherRoomConstraintCount(c[j].getCourseName())) {
                            swap(c, i, j);
                    }
                }
            }
        }
        ALLC.clear();
        for(Course cr:c){
        	if(cr.isLectureTimeFixed())
        		ALLC.add(cr);        
        }
        
        for(Course cr:c){
        	if(!ALLC.contains(cr))
        		ALLC.add(cr);        
        }        
        
    }

    private static void swap(Course[] c, int i, int j) {
        Course temp = c[i];
        c[i] = c[j];
        c[j] = temp;
    }
    
    public static boolean checkFile(){
        term = null;
        try {
            term = new File(path + File.separator + "term.dat");
            if (!term.exists()) {
                Logger.LOGW("term file did not find");
                ShowMessage.showWarning("Resource file not assigned. Please set all resource file then try again.");
                return false;
            }
            prop.load(new FileInputStream(path + File.separator + "term.prop.dat"));
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path+File.separator+"term.info"));
            R.COURSE_GROUP = (HashMap<Object,Integer>)ois.readObject();
        } catch (FileNotFoundException ex) {
            Logger.LOGS("propety file did not find.error:" + ex.getMessage());
            ShowMessage.showError(ex);
            return false;
        } catch (IOException | ClassNotFoundException ex) {
            Logger.LOGS("input output error:" + ex.getMessage());
            ShowMessage.showError(ex);
            return false; 
        }
        return true;
    }

    public static int getRoomSize() {
        return roomCount;
    }
}
