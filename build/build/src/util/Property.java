/**
 * Department of Computer Engineering, Eastern Mediterranean University.
 * @link http://cmpe.emu.edu.tr
 *
 * @author Sakit Atakishiyev, Std. ID: 117319
 * @version 1.0.0.1
 */
package util;

/**
 *  This helper class. to read teacher,course and room properties from
 * property file.
 * @author Sakit
 */
public class Property {
    /*courses & rooms time property ending with _time. Ex:
    courseName%group_number_time, roomName_time*/
    private final static String time = "_time";
    
    /*courses reference courses property ending with _ref_sel 
    Ex: courseName_ref_sel*/
    private final static String ref = "_temp_ref_sel";
    
    /*courses service courses property ending with _ser_sel 
    Ex: courseName_ser_sel*/
    private final static String ser = "_temp_ser_sel";
    
    /*teacher & course max hour property ending with _hour
    ex: teacherName_hour, courseName_hour*/
    private final static String hour = "_hour";
    
    /*teacher room & course lab room property ending with _room.
    ex: teacherName_courseName_room, courseName_room*/
    private final static String room = "_room";
    
    /*teacher hard constraint property ending with _H.
    ex: teacherName_H*/
    private final static String hard = "_H";
    
    /*teacher soft constraint property ending with _H.
    ex: teacherName_S*/
    private final static String soft = "_S";
   
    /**
     *
     * @param teacherName
     * @return  teacher max hour in a day. null if teacher is not exist.
     */
    public static String getTeacherMaxHour(String teacherName){
        teacherName=teacherName.replace(" ", "");
        return R.prop.getProperty(teacherName+hour,null);
    }
    
    /**
     * 
     * @param teacherName
     * @return teacher hard constraint. null if teacher is not exist.
     */
    public static String getTeacherHardConstraint(String teacherName){
        teacherName=teacherName.replace(" ", "");
        return R.prop.getProperty(teacherName+hard,null);
    }
    
    /**
     * 
     * @param teacherName
     * @return teacher soft constraint. null if teacher is not exist.
     */
    public static String getTeacherSoftConstraint(String teacherName){
        teacherName=teacherName.replace(" ", "");
        return R.prop.getProperty(teacherName+soft,null);
    }
    
    
    /**
     * 
     * @param teacherName
     * @param courseName
     * @return teacher's specified room. if not found return default rooms 
     */
    public static String getTeacherRoom(String teacherName, String courseName){
        teacherName=teacherName.replace(" ", "");
        return R.prop.getProperty(teacherName+"_"+courseName+room,
                R.prop.getProperty("default_room"));
    }
    
    /**
     * 
     * @param courseName
     * @return courses specified lab rooms. if not found return default lab rooms
     */
    public static String getCourseRoom(String courseName){
        return R.prop.getProperty(courseName+room,
                R.prop.getProperty("default_lab"));
    }
    
    /**
     * 
     * @param courseName
     * @return maximum lecture hours can apply for this course in a week
     */
    public static String getCourseMaxHour(String courseName){
        return R.prop.getProperty(courseName+hour,null);
    }
    /**
     * '%' represent group number
     * @param courseName
     * @param groupNumber
     * @return if course lecture times known otherwise null
     */
    public static String getCourseTime(String courseName, int groupNumber){
        return R.prop.getProperty(courseName+"%"+groupNumber+time,null);
    }
    
    /**
     * 
     * @param courseName
     * @return reference course which opened the same term at the same
     * department
     */
    public static String getReferenceCourse(String courseName){
        return R.prop.getProperty(courseName+ref,null);
    }
    
    /**
     * 
     * @param courseName
     * @return service courses which opened the same term at the different
     * department
     */
    public static String getServiceCourse(String courseName){
        final String serv =  R.prop.getProperty(courseName+ser,null);
        final StringBuilder sb = new StringBuilder();
        if(serv!=null){
            final String[] services = serv.split(",");
            for(String s:services)
                if(sb.indexOf(s)==-1)
                    sb.append(s).append("%").append(R.COURSE_GROUP.get(s)).append(",");
            if(sb.length()!=0)
                sb.deleteCharAt(sb.length()-1);
        }
        return sb.length()!=0? sb.toString():null;
    }
    
    /**
     * 
     * @param roomName
     * @return if room reserved for other course return that times
     */
    public static String getRoomTime(String roomName){
        return R.prop.getProperty(roomName+time,null);
    }
}
