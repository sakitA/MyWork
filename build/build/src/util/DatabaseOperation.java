/**
 * Department of Computer Engineering, Eastern Mediterranean University.
 * @link http://cmpe.emu.edu.tr
 *
 * @author Sakit Atakishiyev, Std. ID: 117319
 * @version 1.0.0.1
 */
package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This class for database operation. Such as connect to the database, insert,
 * update, delete, and query.
 *
 * @author Sakit
 */
public class DatabaseOperation {

    private static final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    private static final String conectionURL = "jdbc:derby:CMPE";
    private static Connection conn;
    private static Statement statet;
    private static ResultSet result;
    private static PreparedStatement ps;

    public static boolean isCourseExist(String courseName) {
        if (conn != null) {
            try {
                Logger.LOGI("starting to check course is exist or not");
                result = statet.executeQuery("select course_id from course where course_id=\'" + courseName + "\'");
                boolean exist = result.next();
                Logger.LOGI(courseName+" exist:"+exist);
                return exist;
            } catch (SQLException ex) {
                Logger.LOGS("Error:" + ex.getMessage());
                ShowMessage.showError(ex);
            }
        } else {
            Logger.LOGI("Database connection is not satisfied");
        }
        return false;
    }

    /**
     * Default constructor initialize <b>Derby</b> embedded driver and connect
     * to the database
     */
    public DatabaseOperation() {
        try {
            Class.forName(driver);
            Logger.LOGI("Driver found you can now connect to database");
        } catch (ClassNotFoundException ex) {
            Logger.LOGS(driver + " not found.");
            ShowMessage.showError(ex);
            System.exit(1);
        }
    }

    public void connectDB() {
        if (conn == null) {
            try {
                conn = DriverManager.getConnection(conectionURL);
                statet = conn.createStatement();
                Logger.LOGI("Connection is satisfy");
            } catch (SQLException ex) {
                Logger.LOGS(ex.getMessage());
                ShowMessage.showError(ex);
                System.exit(1);
            }
        }
    }

    public void closeDB() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                statet.close();
            }

            Logger.LOGI("Database connection is closed now.");
        } catch (SQLException ex) {
            Logger.LOGS(ex.getMessage());
            ShowMessage.showError(ex);
        } finally {
            conn = null;
            statet = null;
        }
    }

    public boolean isConnected() {
        try {
            return conn == null ? false : !conn.isClosed();
        } catch (SQLException ex) {
            Logger.LOGS(ex.getMessage());
            ShowMessage.showError(ex);
        }
        return false;
    }

    /*---------------------teacher operation-----------------------------------*/
    public boolean insertTeacher(String teacherName) {
        try {
            ps = conn.prepareCall(INSERT_TEACHER);
            ps.setString(1, teacherName);
            int ok = ps.executeUpdate();
            if (ok != 0) {
                Logger.LOGI(teacherName + " inserted to table succesfully");
            } else if (ok == 0) {
                Logger.LOGI("Insert a new teacher operation is failed");
            }
            return ok != 0;
        } catch (SQLException ex) {
            Logger.LOGS("Inserting Error:" + ex.getMessage());
            ShowMessage.showError(ex);
            return false;
        } finally {
            ps = null;
        }
    }

    public boolean deleteTeacher(String teacherName) {
        try {
            ps = conn.prepareStatement(DELETE_TEACHER);
            ps.setString(1, teacherName);
            int ok = ps.executeUpdate();
            if (ok != 0) {
                Logger.LOGI(teacherName + " deleted from table");
            } else if (ok == 0) {
                Logger.LOGS("Deleting teacher operation is failed");
            }
            return ok != 0;
        } catch (SQLException ex) {
            Logger.LOGS("Delete error:" + ex.getMessage());
            return false;
        } finally {
            ps = null;
        }
    }

    public boolean updateTeacher(String oldName, String newName) {
        return deleteTeacher(oldName) && insertTeacher(newName);
    }

    /*--------------------end teacher operation--------------------------------*/
    /*--------------------course operation-------------------------------------*/
    public boolean insertCourse(String courseInfo) {
        try {
            String[] values = courseInfo.split(",");
            ps = conn.prepareStatement(INSERT_COURSE);
            for (int i = 0; i < values.length; i++) {
                if (i == 1) {
                    ps.setInt(i + 1, Integer.parseInt(values[i]));
                } else {
                    ps.setString(i + 1, values[i]);
                }
            }
            int ok = ps.executeUpdate();
            if (ok != 0) {
                Logger.LOGS(courseInfo + " inserted to table succesfully");
            } else if (ok == 0) {
                Logger.LOGS("Insert course operation is failed");
            }
            return ok != 0;
        } catch (SQLException ex) {
            Logger.LOGS("insert course  error:" + ex.getMessage());
            ShowMessage.showError(ex);
            return false;
        } finally {
            ps = null;
        }
    }

    public boolean deleteCourse(String courseName) {
        try {
            ps = conn.prepareStatement(DELETE_COURSE);
            ps.setString(1, courseName);
            int ok = ps.executeUpdate();
            if (ok != 0) {
                Logger.LOGI(courseName + " deleted from table");
            } else if (ok == 0) {
                Logger.LOGS("Deleting course operation is failed");
            }
            return ok != 0;
        } catch (SQLException ex) {
            Logger.LOGS(ex.getMessage());
            ShowMessage.showError(ex);
            return false;
        }
    }

    public boolean updateCourse(String oldCourseName, String newCourseInfo) {
        return deleteCourse(oldCourseName) && insertCourse(newCourseInfo);
    }
    /*---------------------end course operation--------------------------------*/

    /*---------------------room operation--------------------------------------*/
    public boolean insertRoom(String roomName, boolean smartboard) {
        try {
            ps = conn.prepareCall(INSERT_ROOM);
            ps.setString(1, roomName);
            ps.setBoolean(2, smartboard);
            int ok = ps.executeUpdate();
            if (ok != 0) {
                Logger.LOGI(roomName + " inserted to table succesfully");
                ++R.roomCount;
            } else if (ok == 0) {
                Logger.LOGS("Insert room operation is failed");
            }
           
            return ok != 0;
        } catch (SQLException ex) {
            Logger.LOGS("Inserting Error:" + ex.getMessage());
            return false;
        } finally {
            ps = null;
        }
    }

    public boolean deleteRoom(String roomName) {
        try {
            ps = conn.prepareStatement(DELETE_ROOM);
            ps.setString(1, roomName);
            int ok = ps.executeUpdate();
            if (ok != 0) {
                Logger.LOGI(roomName + " deleted from table");
                if(R.roomCount>0)
                	--R.roomCount;
            } else if (ok == 0) {
                Logger.LOGS("Deleting room operation is failed");
            }
            return ok != 0;
        } catch (SQLException ex) {
            Logger.LOGS("Delete error:" + ex.getMessage());
            return false;
        } finally {
            ps = null;
        }
    }

    public boolean updateRoom(String oldName, String newName, boolean smartboard) {
        return deleteRoom(oldName) && insertRoom(newName, smartboard);
    }

    public static boolean isSmart(String roomName) {
        if (conn != null) {
            try {
                Logger.LOGI("starting to check room is smart or not");

                result = statet.executeQuery("select smartboard from room where name=\'" + roomName + "\'");
                if (result != null) {
                    Object obj = null;
                    while (result.next()) {
                        obj = result.getObject(1);
                        break;
                    }
                    Logger.LOGI(roomName + " has smartboard?" + (obj == null ? "false" : obj.equals(true)));
                    if (obj == null) {
                        return false;
                    }
                    return obj.equals(true);
                } else {
                    Logger.LOGI("There is no any result for smartboard checking query");
                }
            } catch (SQLException ex) {
                Logger.LOGS("Error:" + ex.getMessage());
                ShowMessage.showError(ex);
            }
        } else {
            Logger.LOGI("Database connection is not satisfied");
        }
        return false;
    }
    /*---------------------end room operation----------------------------------*/

    /*--------------------------execute query----------------------------------*/
    public List<String> getResult(String query, String columnName) {
        if (conn != null) {
            try {
                Logger.LOGI("starting to execute " + query + " query");

                result = statet.executeQuery(query);
                if (result != null) {
                    final List<String> list = new ArrayList<>();

                    while (result.next()) {
                        list.add(result.getString(columnName));
                    }
                    return list;
                } else {
                    Logger.LOGI("There is no any result for " + query + " query");
                }
            } catch (SQLException ex) {
                Logger.LOGS("Error:" + ex.getMessage());
                ShowMessage.showError(ex);
            }
        } else {
            Logger.LOGI("Database connection is not satisfied");
        }
        return null;
    }

    public ObservableList<ObservableList> getAllRow(String tableName) {
        ObservableList<ObservableList> data = FXCollections.observableArrayList();
        try {
            result = statet.executeQuery("select * from " + tableName);
            int len = result.getMetaData().getColumnCount();
            while (result.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();

                for (int i = 1; i <= len; i++) {
                    row.add(result.getString(i));
                }
                data.add(row);
            }
        } catch (SQLException ex) {
            Logger.LOGS(ex.getMessage());
            ShowMessage.showError(ex);
        }
        return data;
    }

    public List<String> getColumns(String tableName) {
        try {
            result = statet.executeQuery("select * from " + tableName);
            ResultSetMetaData rsm = result.getMetaData();
            int count = rsm.getColumnCount();
            final List<String> columns = new ArrayList<>();
            for (int i = 1; i <= count; i++) {
                columns.add(rsm.getColumnLabel(i));
            }
            return columns;
        } catch (SQLException ex) {
            Logger.LOGS("Column name error:" + ex.getMessage());
            ShowMessage.showError(ex);
        }
        return null;
    }

    public Object getResult(String query, int colIndex) {
        if (conn != null) {
            try {
                Logger.LOGI("starting to execute " + query + " query");

                result = statet.executeQuery(query);
                if (result != null) {
                    while (result.next()) {
                        return result.getObject(colIndex);
                    }
                } else {
                    Logger.LOGI("There is no any result for " + query + " query");
                }
            } catch (SQLException ex) {
                Logger.LOGS("Error:" + ex.getMessage());
                ShowMessage.showError(ex);
            }
        } else {
            Logger.LOGI("Database connection is not satisfied");
        }
        return null;
    }
    /*----------------------end of execute query-------------------------------*/

    /*--------------------------------Queries----------------------------------*/
    public static final String INSERT_TEACHER = "insert into TEACHER VALUES (?)";
    public static final String DELETE_TEACHER = "delete from TEACHER where name = ?";
    public static final String INSERT_COURSE = "insert into COURSE values(?,?,?,?,?)";
    public static final String DELETE_COURSE = "delete from COURSE where course_id = ?";
    public static final String INSERT_ROOM = "insert into ROOM values(?,?)";
    public static final String DELETE_ROOM = "delete from ROOM where name = ?";

    public static final String GET_COURSE_BY_TERM = "select course_id from course where term = %d and CATEGORY<> \'service\' order by course_id";
    public static final String GET_TEACHER = "select name from teacher order by name";
    public static final String GET_ROOM = "select name from room order by name";
    public static final String GET_SMART_ROOM = "select name from room where smartboard=true";
    public static final String GET_COURSE_TERM = "select * from course where course_id=\'%s\'";
    public static final String GET_COURSE_LANG = "select * from course where course_id=\'%s\'";
    public static final String GET_COURSE_TERM_LANGUAGE = "select course_id from course where term=%d and language=\'%s\' and category<>\'service\' order by course_id";
    public static final String GET_SERVICE_COURSE = "select course_id from course where term=%d and language=\'%s\' and category=\'service\' order by course_id";
    public static final String GET_COURSE = "select course_id from course where category<>\'service\' order by course_id";
    public static final String GET_SERVICE = "select course_id from course where category=\'service\' order by course_id";
}
