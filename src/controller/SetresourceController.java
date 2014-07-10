/**
 * Department of Computer Engineering, Eastern Mediterranean University.
 * @link http://cmpe.emu.edu.tr
 *
 * @author Sakit Atakishiyev, Std. ID: 117319
 * @version 1.0.0.1
 */
package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import util.DatabaseOperation;
import util.Logger;
import util.Property;
import util.R;
import util.ShowMessage;

/**
 * FXML Controller class control the set resources. In this class we assign all
 * opening courses and their constraints, teachers and constraints, rooms and
 * their constraints. You can import previous resource if exist or you can
 * export for using later. In order to <code>import</code> the resource file
 * choose <code>Import</code> from <code>File</code> menu. To
 * <code>Export</code> choose <code>Export</code> from <code>File</code> menu.
 * In addition you can do database operation such as adding a new course,
 * teacher and room. Also you can edit or delete. To do database operation
 * choose <code>Database Operation</code> from <code>Edit</code> menu.
 *
 * @author Sakit
 */
public class SetresourceController implements Initializable {

    private Stage stage;
    public static final DatabaseOperation db = new DatabaseOperation();
    private ObservableList<ObservableList> content = FXCollections.observableArrayList();
    @SuppressWarnings("FieldMayBeFinal")
    private static TreeItem<String> rootItem = new TreeItem<>("TimeTable");
    private static boolean lab = false;
    private static boolean check=false;
    private static final FileChooser chooser = new FileChooser();
    private static final ToggleGroup roomGroup = new ToggleGroup();
    @FXML
    private Text timeText;
    @FXML
    private Text dayText;
    @FXML
    private Text courseText;
    @FXML
    private Text groupnumText;
    @FXML
    private TextField teacherMaxHour;
    @FXML
    private TextField courseMaxHour;
    @FXML
    private Button addOpeningCourse;
    @FXML
    private Button removeOpeningCourse;
    @FXML
    private Button courseLabRoomConstraint;
    @FXML
    private Button courseSetTime;
    @FXML
    private Button addRefSer;
    @FXML
    private Button removeRefSer;
    @FXML
    private Button saveCourseInfo;
    @FXML
    private Button deleteCourseInfo;
    @FXML
    private Button clearAllInfo;
    @FXML
    private Button finishOperation;
    @FXML
    private ListView openingCourseList;
    @FXML
    private ListView refserCourses;
    @FXML
    private ListView refserOpeningCourses;
    @FXML
    private GridPane roomGrid;
    @FXML
    private TreeView treeList;
    @FXML
    private ImageView addNewRefSer;
    @FXML
    private ImageView serClock;
    @FXML
    private ImageView refserSave;
    @FXML
    private ComboBox teacherComboBox;
    @FXML
    private CheckBox courseFixTime;
    @FXML
    private ToggleGroup refserToggle;
    @FXML
    private RadioButton refRadio;
    @FXML
    private RadioButton serRadio;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    @SuppressWarnings("Convert2Lambda")
    public void initialize(URL url, ResourceBundle rb) {
        clear();

        //set radio button data to identify itself
        refRadio.setUserData("ref");
        serRadio.setUserData("ser");

        // start timing
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        //add all opening course to the list
        openingCourseList.setItems(MainpageController.openingCourses);

        //connect database if not connected before
        if (!db.isConnected()) {
            db.connectDB();
        }

        //add all teachers
        teacherComboBox.setPrefWidth(teacherComboBox.getMinWidth());
        teacherComboBox.getItems().addAll(db.getResult(DatabaseOperation.GET_TEACHER, "name"));

        //if there is no selected any course disable remove button otherwise set enable
        if (openingCourseList.getItems().isEmpty()) {
            removeOpeningCourse.setDisable(true);
        } else {
            removeOpeningCourse.setDisable(false);
        }

        /*set openingCourseList item change listener*/
        openingCourseList.getSelectionModel().selectedItemProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
            treeList.getSelectionModel().clearSelection();
            //check if list is empty then disable remove button otherwise enable
            if (openingCourseList.getItems().isEmpty()) {
                removeOpeningCourse.setDisable(true);
            } else {
                removeOpeningCourse.setDisable(false);
            }

            if (oldValue != null) {
                save(refserToggle.getSelectedToggle().getUserData().toString(),
                        oldValue.toString());
            }
            //show selected course name and group if selected value difference from null
            if (newValue != null) {
                courseText.setText(newValue.toString());
                Integer g = R.COURSE_GROUP.get(newValue);
                g = g == null ? 1 : g + 1;
                groupnumText.setText(String.valueOf(g));

                String lecHour = R.prop.getProperty(newValue.toString() + "_hour", "");
                courseMaxHour.setText(lecHour);
                courseFixTime.setDisable(false);
                courseFixTime.setSelected(R.prop.containsKey(newValue.toString()+"%"+groupnumText.getText() + "_fix"));
                initRefSerCourse(newValue.toString(), refserToggle.getSelectedToggle().getUserData().toString());
                refserOpeningCourses.getItems().clear();
                restore(refserToggle.getSelectedToggle().getUserData().toString(),
                        newValue.toString());
            } else {
                courseText.setText("No Course");
                groupnumText.setText("No Group");
                courseMaxHour.setText("");
                courseFixTime.setDisable(true);
                refserCourses.getItems().clear();
                refserOpeningCourses.getItems().clear();

            }
            removeRefSer.setDisable(refserOpeningCourses.getItems().isEmpty());
        });

        //listen fix time checkbox to enable/disable set time button
        courseFixTime.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            Object cor = openingCourseList.getSelectionModel().getSelectedItem();
            if(cor!=null){
            String key = cor.toString() + "%" + groupnumText.getText();
            if (newValue) {
                courseSetTime.setVisible(true);
                R.prop.setProperty(key + "_fix", "T");
                Logger.LOGI(key+"_fix added to the property file");
            } else {
                courseSetTime.setVisible(false);

                if (R.prop.containsKey(key + "_fix")) {
                    R.prop.remove(key + "_fix");
                    Logger.LOGI(key + "_fix deleted from property file");
                }
                
                if (R.prop.containsKey(key + "_time")) {
                    R.prop.remove(key + "_time");
                    Logger.LOGI(key + "_time deleted from property file");
                }
            }
            
        }});

        //listen teacher combobox changing, so store selected teacher requests
        teacherComboBox.valueProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
            if (newValue != null) {
                String name = newValue.toString().replace(" ", "");
                String hour = R.prop.getProperty(name + "_hour", "");
                teacherMaxHour.setText(hour);
            } else {
                teacherMaxHour.setText("");
            }
        });

        //listen radio button changing
        refserToggle.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) -> {
            serClock.setVisible(serRadio.isSelected());

            Object obj = openingCourseList.getSelectionModel().getSelectedItem();
            if (obj != null) {
                save(oldValue.getUserData().toString(), obj.toString());
                initRefSerCourse(obj.toString(), newValue.getUserData().toString());
                restore(newValue.getUserData().toString(), obj.toString());
            }
            removeRefSer.setDisable(refserOpeningCourses.getItems().isEmpty());
        });

        //listen refserCourses to check exist information to avoid duplicate information
        refserCourses.getSelectionModel().selectedItemProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
            checkRefSer();
            removeRefSer.setDisable(refserOpeningCourses.getItems().isEmpty());
        });

        rootItem.setExpanded(true);
        treeList.setRoot(rootItem);
        treeList.setShowRoot(false);
        treeList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {

            @Override
            public void changed(ObservableValue<? extends TreeItem<String>> ov, TreeItem<String> oldValue, TreeItem<String> newValue) {
                if (newValue != null && newValue.getValue().contains("Group")) {
                    openingCourseList.getSelectionModel().select(newValue.getValue().substring(0, newValue.getValue().indexOf(",")));
                    courseText.setText(newValue.getValue().substring(0, newValue.getValue().indexOf(",")));
                    groupnumText.setText(newValue.getValue().substring(newValue.getValue().indexOf(":") + 1));
                    teacherComboBox.getSelectionModel().select(newValue.getParent().getValue());
                } else if (newValue != null && newValue.getParent().getValue().contains("Group")) {
                    openingCourseList.getSelectionModel().select(newValue.getParent().getValue().substring(0, newValue.getParent().getValue().indexOf(",")));
                    courseText.setText(newValue.getParent().getValue().substring(0, newValue.getParent().getValue().indexOf(",")));
                    groupnumText.setText(newValue.getParent().getValue().substring(newValue.getParent().getValue().indexOf(":") + 1));
                    teacherComboBox.getSelectionModel().select(newValue.getParent().getParent().getValue());
                } else if (newValue != null) {
                    openingCourseList.getSelectionModel().clearSelection();
                    teacherComboBox.getSelectionModel().select(newValue.getValue());
                }
            }
        });

        initializeRoomConstraint();
    }

    /*add new opening course*/
    @FXML
    private void addOpeningCourse() {
        MainpageController.chooseOpeningCourse(stage);
        removeOpeningCourse.setDisable(openingCourseList.getItems().isEmpty());
    }

    /*remove opening course*/
    @FXML
    private void removeOpeningCourse() {
        Object obj = openingCourseList.getSelectionModel().getSelectedItem();
        if (obj == null) {
            ShowMessage.showWarning("Please choose any course. Then press remove button.");
        } else {
            Action response = ShowMessage.questionConfirm("If you delete this course all information\n"
                    + "and its instruction information will be deleted.\nDo you want continue?");
            if (response == Dialog.Actions.YES) {
                openingCourseList.getItems().remove(obj);
                if (R.COURSE_GROUP.containsKey(obj)) {
                    int gr = (int) R.COURSE_GROUP.get(obj);
                    for (int i = 1; i <= gr; i++) {
                        deleteTreeItem(null, obj.toString());
                    }
                }
                Logger.LOGI(obj.toString() + " remove from the list.");
            }
        }
        openingCourseList.getSelectionModel().clearSelection();
    }

    //set teacher's hard constraint
    @FXML
    private void teacherHardConstraint() {
        Object obj = teacherComboBox.getSelectionModel().getSelectedItem();
        if (obj == null) {
            ShowMessage.showWarning("Please choose any teacher, then try again");
        } else {
            //to avoid choosing the same time for both hard & soft.\"H\" means soft.
            boolean[][] hard = getTimeConstraint(obj.toString(), "H");
            //to avoid choosing the same time for both hard & soft.\"S\" means soft.
            boolean[][] soft = getTimeConstraint(obj.toString(), "S");
            //open time window for teacher hard constraint.\"H\" means hard.
            createTimeWindow(obj.toString(), "H", hard, soft);
        }
    }

    //set teacher's soft constraint
    @FXML
    private void teacherSoftConstraint() {
        Object obj = teacherComboBox.getSelectionModel().getSelectedItem();
        if (obj == null) {
            ShowMessage.showWarning("Please choose any teacher, then try again");
        } else {
            //to avoid choosing the same time for both hard & soft.\"S\" means soft.
            boolean[][] soft = getTimeConstraint(obj.toString(), "S");
            //to avoid choosing the same time for both hard & soft.\"H\" means soft.
            boolean[][] hard = getTimeConstraint(obj.toString(), "H");
            //open time window for teacher hard constraint.\"H\" means hard.
            createTimeWindow(obj.toString(), "S", hard, soft);
        }
    }

    //set teacher room constraint
    @FXML
    private void teacherRoomConstraint() {
        Object tec = teacherComboBox.getSelectionModel().getSelectedItem();
        Object cor = openingCourseList.getSelectionModel().getSelectedItem();
        lab = false;
        String[] rooms;
        if (tec == null) {
            ShowMessage.showWarning("Please choose any teacher, then try again");
        } else {
            String key = tec.toString().replace(" ", "");
            if (cor != null && R.prop.containsKey(key + "_" + cor.toString() + "_room")) {
                rooms = R.prop.getProperty(key + "_" + cor.toString() + "_room").split(",");
            } else if (R.prop.containsKey(key + "_room")) {
                rooms = R.prop.getProperty(key + "_room").split(",");
            } else {
                rooms = R.prop.getProperty("default_room").split(",");
            }
            createRoomWindow(tec.toString(), createRoomWindow(rooms));
        }
    }

    //set course lectures time
    @FXML
    private void courseSetTime() {
        Object obj = openingCourseList.getSelectionModel().getSelectedItem();
        if (obj == null) {
            ShowMessage.showWarning("Please choose any course, then try again");
        } else if (!R.prop.containsKey(obj.toString() + "_hour")) {
            ShowMessage.showWarning("Please enter max hour for the " + obj.toString());
        } else {
            boolean[][] time = getTimeConstraint(courseText.getText() + "%" + groupnumText.getText(), "time");
            String key = courseText.getText() + "%" + groupnumText.getText();
            createTimeWindow(key, "time", time, null);
            int hour = Integer.parseInt(R.prop.getProperty(obj.toString() + "_hour"));
            if (R.prop.containsKey(key + "_time")
                    && R.prop.getProperty(key + "_time").split(",").length != hour) {
                ShowMessage.showWarning("You must enter " + hour + " different time for " + obj.toString());
                courseSetTime();
            }
        }
    }

    @FXML
    @SuppressWarnings("element-type-mismatch")
    //adding new reference/service course
    private void addNewRefSer() {
        Object obj = openingCourseList.getSelectionModel().getSelectedItem();
        if (obj == null) {
            ShowMessage.showWarning("Please choose any course then try again.");
        } else {
            if (!db.isConnected()) {
                db.connectDB();
            }

            List<String> result = db.getResult(refRadio.isSelected()
                    ? DatabaseOperation.GET_COURSE : DatabaseOperation.GET_SERVICE, "course_id");
            result.remove(obj);
            ObservableList<String> ob = refserCourses.getItems();
            ListView<CheckBox> lw = new ListView<>();
            lw.setTooltip(new Tooltip("To add/remove new course click on the course name."));
            for (String s : result) {
                CheckBox cell = new CheckBox(s);
                cell.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    if (newValue) {
                        if (!ob.contains(cell.getText())) {
                            ob.add(cell.getText());
                        }
                    } else {
                        ob.remove(cell.getText());
                    }
                });
                lw.getItems().add(cell);
            }
            StackPane root = new StackPane();
            root.getChildren().add(lw);
            Stage child = new Stage();
            child.setScene(new Scene(root, 350, 225));
            child.setTitle("Add new reference/service course.");
            child.getIcons().add(R.emu_icon);
            child.initOwner(stage);
            child.initModality(Modality.WINDOW_MODAL);
            child.setResizable(false);
            child.showAndWait();
        }
    }

    /*change add icon when mouse enter the region of icon*/
    @FXML
    private void addRefSerEnter() {
        addNewRefSer.setImage(R.grey_add);
    }

    /*restore previous add icon when mouse exit the region of icon*/
    @FXML
    private void addRefSerExit() {
        addNewRefSer.setImage(R.blue_add);
    }

    //set opening service course.
    @FXML
    private void setSerTime() {
        Object obj = refserOpeningCourses.getSelectionModel().getSelectedItem();
        if (obj == null) {
            ShowMessage.showInformation("Please choose course any opening service course\n"
                    + " then try again.");
        } else {
            boolean[][] time = getTimeConstraint(obj.toString(), "time");
            createTimeWindow(obj.toString(), "time", time, null);
        }
    }

    //save opening reference/service course
    @FXML
    private void saveRefSer() {
        Object obj = openingCourseList.getSelectionModel().getSelectedItem();
        if (obj == null) {
            ShowMessage.showWarning("Please choose any course then try again.");
        } else {
            String key = obj.toString() + "_temp_" + refserToggle.getSelectedToggle().getUserData().toString();
            final StringBuilder sb = new StringBuilder();
            for (Object s : refserOpeningCourses.getItems()) {
                sb.append(s).append(",");
            }
            if (sb.length() != 0) {
                sb.deleteCharAt(sb.length() - 1);
                R.prop.setProperty(key, sb.toString());
                flash(refserSave, "Information saved successfully.");
                Logger.LOGI(key + "=" + sb.toString() + " added to the temp properties file.");
            } else if (sb.length() == 0) {
                if (R.prop.containsKey(key)) {
                    R.prop.remove(key);
                    Logger.LOGI(key + "=" + sb.toString() + " removed from the temp properties file.");
                }
            }
        }
    }

    /*change save icon when mouse enter the region of icon*/
    @FXML
    private void refserSaveEnter() {
        refserSave.setImage(R.grey_save);
    }

    /*restore previous save icon when mouse exit the region of icon*/
    @FXML
    private void refserSaveExit() {
        refserSave.setImage(R.blue_save);
    }

    //adding opening reference/service course
    @FXML
    private void addRefSer() {
        Object obj = refserCourses.getSelectionModel().getSelectedItem();
        if (obj == null) {
            ShowMessage.showWarning("Please choose any reference/service course then"
                    + " try again.");
        } else {
            if (refRadio.isSelected()) {
                if (refserOpeningCourses.getItems().contains(obj)) {
                    ShowMessage.showInformation(obj.toString() + " already added");
                } else {
                    refserOpeningCourses.getItems().add(obj);
                }
            } else {
                Integer g = R.COURSE_GROUP.get(obj);
                g = g == null ? 1 : g + 1;
                if (g == 1) {
                    refserOpeningCourses.getItems().add(obj.toString() + "%" + g);
                    R.COURSE_GROUP.put(obj, g);
                } else {
                    Action response = ShowMessage.questionConfirm("If " + obj.toString() + " lectures time are different other\n"
                            + obj.toString() + " group lectures time add otherwise don't add.\nDo you want continue?");
                    if (response == Dialog.Actions.YES) {
                        refserOpeningCourses.getItems().add(obj.toString() + "%" + g);
                        R.COURSE_GROUP.put(obj, g);
                    } else {
                        List<Integer> groups = new ArrayList<>();
                        for (int i = 1; i < g; i++) {
                            groups.add(i);
                        }

                        Optional<Integer> responses = Dialogs.create()
                                .owner(stage)
                                .title("Choice Input Dialog")
                                .message("Choose group which lecture times the same:")
                                .showChoices(groups);

                        // One way to get the response value.
                        if (responses.isPresent()) {
                            int choice = responses.get();
                            String key = obj.toString() + "%" + choice;
                            if (!refserOpeningCourses.getItems().contains(key)) {
                                refserOpeningCourses.getItems().add(key);
                            }
                        }
                    }
                }
            }
            removeRefSer.setDisable(false);
        }
    }

    //remove opening course from list
    @FXML
    private void removeRefSer() {
        Object obj = refserOpeningCourses.getSelectionModel().getSelectedItem();
        if (obj == null) {
            ShowMessage.showWarning("Please choose any opnening reference/service course then try again");
        } else {
            if (refRadio.isSelected()) {
                Action respone = ShowMessage.questionConfirm("Do you want to delete the " + obj.toString()
                        + "?\nIf you delete the course, this course cannot\n"
                        + "be considered when algorithm runs.");
                if (respone == Dialog.Actions.YES) {
                    refserOpeningCourses.getItems().remove(obj);
                } else {
                    flash(dayText, "Operation is cancelled");
                }
            } else {
                Action response = ShowMessage.questionConfirm("If you delete " + obj.toString() + " course, algorithm\ndoes not"
                        + " consider as service course for this and previously courses you assigned.\nDo you want to continue?");

                if (response == Dialog.Actions.YES) {
                    String s = obj.toString();
                    s = s.substring(0, s.indexOf("%"));
                    Integer g = R.COURSE_GROUP.get(s);
                    R.COURSE_GROUP.put(s, --g);
                    ObservableList<String> os = refserOpeningCourses.getItems();
                    List<String> temp = new ArrayList<>();
                    int count = 1;
                    for (String ss : os) {
                        if (ss.startsWith(s)) {
                            if (count <= g) {
                                temp.add(s + "%" + count);
                                ++count;
                            }
                        } else {
                            temp.add(ss);
                        }
                    }
                    os.clear();
                    os.addAll(temp);
                }
                if (refserOpeningCourses.getItems().size() == 0) {
                    removeRefSer.setDisable(true);
                }
                refserOpeningCourses.getSelectionModel().clearSelection();
            }
        }
        if (refserOpeningCourses.getItems().isEmpty()) {
            removeRefSer.setDisable(true);
        }
    }

    //save course and its instruction info
    @FXML
    private void saveCourseInfo() {
        StringBuilder errors = new StringBuilder();
        String info = "";
        Object tec = teacherComboBox.getSelectionModel().getSelectedItem();
        Object cor = openingCourseList.getSelectionModel().getSelectedItem();
        if (cor == null || tec == null) {
            ShowMessage.showWarning("Please choose any course and teacher then try again.");
        } else {
            info += cor.toString() + ", Group:" + groupnumText.getText();
            if (teacherMaxHour.getText().length() == 0) {
                errors.append("Max hour has not set yet.").append("\n");
            }
            if (courseMaxHour.getText().length() == 0) {
                errors.append("Max lecture hour has not set yet.").append("\n");
            }

            if (!R.prop.containsKey(tec.toString().replace(" ", "") + "_" + cor.toString() + "_room")) {
                errors.append("Teacher room hard constraint has not set yet.").append("\n");
            }

            if (!R.prop.containsKey(cor.toString() + "_room")
                    || R.prop.getProperty(cor.toString() + "_room").length() == 0) {
                errors.append(cor.toString()).append(" lab room constraint has not set yet.").append("\n");
            }

            if (R.prop.containsKey(cor.toString() + "%" + groupnumText.getText() + "_fix")) {
                if (!R.prop.containsKey(cor.toString() + "%" + groupnumText.getText() + "_time")) {
                    errors.append(cor.toString()).append("%").append(groupnumText.getText())
                            .append("'s lectures time has not assigned").append("\n");
                } else {
                    R.prop.setProperty(cor.toString()
                            + "%" + groupnumText.getText() + "_time",
                            R.prop.getProperty(
                                    cor.toString() + "%" + groupnumText.getText() + "_time"));
                }
            }

            if (R.prop.containsKey(cor.toString() + "_temp_ser")) {
                String[] tmp = R.prop.getProperty(cor.toString() + "_temp_ser").split(",");
                for (String ob : tmp) {
                    if (ob.contains("%") && !R.prop.containsKey(ob + "_time")) {
                        errors.append(ob)
                                .append("'s lectures time has not assigned").append("\n");
                    }
                }
            }
            boolean exsist = addItemToTree(tec.toString(), info, errors.toString());
            if (!exsist) {
                Integer g = R.COURSE_GROUP.get(cor);
                R.COURSE_GROUP.put(cor, g == null ? 1 : g + 1);
            }
            openingCourseList.getSelectionModel().clearSelection();
            teacherComboBox.setValue(null);
            treeList.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void deleteCourseInfo() {
        TreeItem<String> selected = (TreeItem<String>) treeList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            ShowMessage.showInformation("Please choose any teacher or course then click the\n"
                    + "Delete button");
        } else {
            if (selected.getParent() != null) {
                String course;
                String teacher;
                boolean flag1 = selected.getValue().contains("Group");
                if (flag1) {
                    teacher = selected.getParent().getValue();
                } else if (selected.getParent().getParent() == null) {
                    teacher = selected.getValue();
                } else {
                    teacher = selected.getParent().getParent().getValue();
                }

                if (flag1) {
                    course = selected.getValue().substring(0, selected.getValue().indexOf(","));
                } else if (selected.getParent().getParent() == null) {
                    course = null;
                } else {
                    course = selected.getParent().getValue().substring(0, selected.getParent().getValue().indexOf(","));
                }

                String message = flag1 ? "Do you want to delete " + course + " information?"
                        : "Do you want to delete " + teacher + " information?\nNote: After this operation " + teacher + "'s courses will be deleted.";
                Action dr = ShowMessage.questionConfirm(message);
                if (dr == Dialog.Actions.YES) {
                    if (course == null) {
                        ObservableList<TreeItem<String>> os = FXCollections.observableArrayList();
                        os.addAll(selected.getChildren());
                        for (TreeItem<String> ti : os) {
                            course = ti.getValue().substring(0, ti.getValue().indexOf(","));
                            deleteTreeItem(teacher, course);
                        }
                    } else {
                        deleteTreeItem(teacher, course);
                    }
                }
            }
            treeList.getSelectionModel().clearSelection();
        }
    }

    /*clear treeList*/
    @FXML
    private void clearAllInfo() {
        Logger.LOGI("treeList now is cleared by user");
        treeList.getRoot().getChildren().clear();
        R.COURSE_GROUP.clear();
        teacherComboBox.getSelectionModel().clearSelection();
        openingCourseList.getSelectionModel().clearSelection();
        courseText.setText("No course");
        groupnumText.setText("No found");
    }

    @FXML
    private void finishOperation() {
        BufferedWriter resWrite = null;
        try {
            for (TreeItem<String> ti : rootItem.getChildren()) {
                for (TreeItem<String> tii : ti.getChildren()) {
                    if (!tii.isLeaf()) {
                        ShowMessage.showWarning(tii.getValue() + " information did not\n"
                                + "saved successfully. Correct it and try again");
                        tii.setExpanded(true);
                        return;
                    }
                }
            }
            Logger.LOGI("Everything ok starting to finish operation");
            Action ok = ShowMessage.questionConfirm("Do you want to save all resource for future work?");
            if (ok == Dialog.Actions.YES) {
                exportResource();
            }

            final File resource = new File(R.path + File.separator + "term.dat");
            final File prop = new File(R.path + File.separator + "term.prop.dat");
            resWrite = new BufferedWriter(new FileWriter(resource));
            if (!resource.exists()) {
                boolean f = resource.createNewFile();
                Logger.LOGI("term.dat file created status:" + f);
            }
            if (!prop.exists()) {
                boolean f = resource.createNewFile();
                Logger.LOGI("term.prop.dat file created status:" + f);
            }
            final StringBuilder sb = new StringBuilder();
            for (TreeItem<String> tec : rootItem.getChildren()) {
                final String teacher = tec.getValue();

                for (TreeItem<String> cor : tec.getChildren()) {
                    sb.delete(0, sb.length());
                    String course = cor.getValue().substring(0, cor.getValue().indexOf(","));
                    String group = cor.getValue().substring(cor.getValue().indexOf(":") + 1);
                    sb
                            .append(teacher).append(",")
                            .append(course).append(",")
                            .append(group);

                    String res = Property.getReferenceCourse(course);
                    if (res != null) {
                        for (String s : res.split(",")) {
                            sb.append(",@").append(s);
                        }
                        Logger.LOGI("Reference courses has been setted for:" + course);
                    } else {
                        Logger.LOGI(course + " has no any reference course");
                    }

                    String ser = Property.getServiceCourse(course);
                    if (ser != null) {
                        for (String s : ser.split(",")) {
                            sb.append(",$").append(s);
                        }
                        Logger.LOGI("Service courses has been setted for:" + course);
                    } else {
                        Logger.LOGI(course + " has no any service course");
                    }
                    resWrite.write(sb.toString());
                    resWrite.write("\n");
                }
            }
            R.prop.store(new FileOutputStream(prop), null);
            File coursesInfo = new File(R.path + File.separator + "term.info");
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(coursesInfo));
            oos.writeObject(R.COURSE_GROUP);
            Logger.LOGI("resource files are created. program finish succesffully.");

            Action act = ShowMessage.questionConfirm("Do you want to start generate solution?");
            if (act == Dialog.Actions.YES) {
                MainpageController.GENERATE = true;
            }else{
            	MainpageController.GENERATE = false;
            }
            stage.close();
        } catch (IOException ex) {
            Logger.LOGI(ex.getMessage());
            ShowMessage.showError(ex);
        } finally {
            if (resWrite != null) {
                try {
                    resWrite.close();
                    Logger.LOGI("resWriter is closed.");
                } catch (IOException ex) {
                    Logger.LOGI(ex.getMessage());
                    ShowMessage.showError(ex);
                }
            }
        }
    }

    /*open department web site on the browser*/
    @FXML
    private void goCMPE() {
        MainpageController.openWebSite(R.CMPE_URL);
    }

    //set course lab room constraint
    @FXML
    private void courseLabRoomConstraint() {
        Object cor = openingCourseList.getSelectionModel().getSelectedItem();
        lab = true;
        String[] rooms;
        if (cor == null) {
            ShowMessage.showWarning("Please choose any course, then try again");
        } else {
            String key = cor.toString();
            if (R.prop.containsKey(key + "_room")) {
                rooms = R.prop.getProperty(key + "_room").split(",");
            } else {
                rooms = R.prop.getProperty("default_lab").split(",");
            }
            createRoomWindow(cor.toString(), createRoomWindow(rooms));
        }
    }

    /*change clock icon when mouse enter the region of icon*/
    @FXML
    private void serClockEnter() {
        serClock.setImage(R.blue_clock);
    }

    /*restore previous save icon when mouse exit the region of icon*/
    @FXML
    private void serClockExit() {
        serClock.setImage(R.grey_clock);
    }

    //import previous resources and properties
    @FXML
    private void importResource() {
        Logger.LOGI("Importin resource started");
        openingCourseList.getSelectionModel().clearSelection();
        treeList.getSelectionModel().clearSelection();
        teacherComboBox.getSelectionModel().clearSelection();
        FileChooser fchoose = new FileChooser();
        fchoose.setTitle("Import resource file");
        fchoose.getExtensionFilters().clear();
        fchoose.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Resource file", "*.resource"));
        File file = fchoose.showOpenDialog(stage);
        if (file != null) {
            importRes(file);
        }
    }

    //export current resources and properties
    @FXML
    private void exportResource() {
        try {
            FileChooser fchoose = new FileChooser();
            fchoose.setTitle("Export resource file");
            fchoose.getExtensionFilters().clear();
            fchoose.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Resource file", "*.resource"));
            File file = fchoose.showSaveDialog(stage);
            if (file != null) {
                if (!file.getName().contains(".resource")) {
                    File temp = new File(file.getPath() + ".resource");
                    if (!temp.exists()) {
                        temp.createNewFile();
                    }
                    file = temp;
                }
                exportRes(file);
            }
        } catch (IOException ex) {
            Logger.LOGS(ex.getMessage());
            ShowMessage.showError(ex);
        }
    }

    /*close the set resource window/stage*/
    @FXML
    private void close() {
        stage.close();
    }

    @FXML
    private void help() {
        ShowMessage.showInformation("This part is not reday yet. Next update will be.");
    }

    //adding/deletin/editing on teacher table
    @FXML
    private void teacherOperation() {
        content = db.getAllRow("teacher");
        databaseOperation("Teacher");
    }

    //adding/deletin/editing on course table
    @FXML
    private void courseOperation() {
        content = db.getAllRow("course");
        databaseOperation("Course");
    }

    @FXML
    private void roomOperation() {
        content = db.getAllRow("room");
        databaseOperation("Room");
    }

    @FXML
    private void setRoomTime() {
        boolean[][] time = getTimeConstraint(roomGroup.getSelectedToggle().getUserData().toString(), "time");
        createTimeWindow(roomGroup.getSelectedToggle().getUserData().toString(), "time", time, null);
    }

    /*set teacher max hour in a day. 1 hour is 2 slot. E.x 8:30-10:20 is 1 hour.*/
    @FXML
    private void teacherHourChange(KeyEvent event) {
        if (event.getCode() == KeyCode.TAB || event.getCode() == KeyCode.BACK_SPACE
                || event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.ESCAPE) {
            return;
        }
        Object obj = teacherComboBox.getSelectionModel().getSelectedItem();
        if (obj == null) {
            teacherMaxHour.setText("");
            ShowMessage.showWarning("Please choose any teacher and try again.");
        } else {
            String input = teacherMaxHour.getText();
            if (!input.matches("^[1-9]$")) {
                ShowMessage.showWarning("Please enter valid input. Valid input is\n"
                        + " any number between [1-9]");
                teacherMaxHour.setText("");
            } else {
                R.prop.setProperty(
                        teacherComboBox.getValue().toString()
                        .replaceAll(" ", "") + "_hour", input);
            }
        }
    }

    /*set course max hour in a week. 1 hour is 2 slot. E.x 8:30-10:20 is 1 hour.*/
    @FXML
    private void courseHourChange(KeyEvent event) {
        if (event.getCode() == KeyCode.TAB || event.getCode() == KeyCode.BACK_SPACE
                || event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.ESCAPE) {
            return;
        }
        Object obj = openingCourseList.getSelectionModel().getSelectedItem();
        if (obj == null) {
            courseMaxHour.setText("");
            ShowMessage.showWarning("Please choose any course and try again.");
        } else {
            String input = courseMaxHour.getText();
            if (!input.matches("^[1-9]$")) {
                ShowMessage.showWarning("Please enter valid input. Valid input is\n"
                        + " any number between [1-9]");
                courseMaxHour.setText("");
            } else {
                R.prop.setProperty(
                        openingCourseList.getSelectionModel().getSelectedItem().toString() + "_hour", input);
            }
        }
    }
    //to create month day, year hour:minitue date format.
    final DateFormat format = new SimpleDateFormat("MMMM dd,yyyy HH:mm");

    //to show day and time on the window
    final Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), (ActionEvent event) -> {
        Date cal = new Date();
        final String s = format.format(cal);
        timeText.setText(s.substring(s.lastIndexOf(" ")));
        dayText.setText(s.substring(0, s.lastIndexOf(" ")));
    }));

    public void setStage(Stage stage) {
        this.stage = stage;
        this.stage.setOnCloseRequest((WindowEvent event) -> {
            timeline.stop();
            db.closeDB();
            R.storeProp();
            Logger.LOGI("Database connection is closed");
        });
    }

    private void clear() {
        openingCourseList.getItems().clear();
        teacherComboBox.getItems().clear();
    }

    /*show short message to user about the operation result*/
    public static void flash(Node node, String message) {
        Font font = Font.font("Verdana", FontWeight.NORMAL, 14);
        Color boxColor = Color.GREY;
        Color textColor = Color.WHITE;
        double duration = 3500;
        double arcH = 5;
        double arcW = 5;

        final Rectangle rectangle = new Rectangle();
        final Text text = new Text(message);
        text.setWrappingWidth(450);
        double x = 0;
        double y = 0;
        text.setLayoutX(x);
        text.setLayoutY(y);
        text.setFont(font);
        text.setFill(textColor);

        Scene scene = node.getScene();
        final Parent p = scene.getRoot();

        if (p instanceof Group) {
            Group group = (Group) p;
            group.getChildren().add(rectangle);
            group.getChildren().add(text);
        }
        if (p instanceof Pane) {
            Pane group = (Pane) p;
            group.getChildren().add(rectangle);
            group.getChildren().add(text);
        }

        Bounds bounds = text.getBoundsInParent();

        double sWidth = scene.getWidth();
        double sHeight = scene.getHeight();

        x = sWidth / 2 - (bounds.getWidth() / 2);
        y = sHeight / 2 - (bounds.getHeight() / 2);
        text.setLayoutX(x);
        text.setLayoutY(y);
        bounds = text.getBoundsInParent();
        double baseLineOffset = text.getBaselineOffset();

        rectangle.setFill(boxColor);
        rectangle.setLayoutX(x - arcW);
        rectangle.setLayoutY(y - baseLineOffset - arcH);
        rectangle.setArcHeight(arcH);
        rectangle.setArcWidth(arcW);
        rectangle.setWidth(bounds.getWidth() + arcW * 2);
        rectangle.setHeight(bounds.getHeight() + arcH * 2);

        FadeTransition ft = new FadeTransition(
                Duration.millis(duration), rectangle);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.play();
        ft.setOnFinished((ActionEvent event) -> {
            if (p instanceof Group) {
                Group group = (Group) p;
                group.getChildren().remove(rectangle);
                group.getChildren().remove(text);
            }
            if (p instanceof Pane) {
                Pane group = (Pane) p;
                group.getChildren().remove(rectangle);
                group.getChildren().remove(text);
            }
        });
        FadeTransition ft2 = new FadeTransition(
                Duration.millis(duration + (duration * .1)), text);
        ft2.setFromValue(1.0);
        ft2.setToValue(0.0);
        ft2.play();
    }

    //create time window/stage
    private void createTimeWindow(final String title, final String which, boolean[][] hard, boolean[][] soft) {
        final GridPane pane = new GridPane();
        pane.setPrefSize(450, 200);
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(10);
        pane.setVgap(10);
        final Button save = new Button("Save");
        save.setOnAction((ActionEvent event) -> {
            saveTime(pane, title, which);
            ((Stage) save.getScene().getWindow()).close();
        });
        save.setDefaultButton(true);
        GridPane.setHalignment(save, HPos.CENTER);
        save.setMinHeight(25);
        pane.add(save, 0, 0);
        for (int i = 1; i <= R.MAX_DAY; i++) {
            Text day = new Text(R.WEEK_DAY[i]);
            day.setId("day_slot");
            pane.add(day, i, 0);
            GridPane.setHalignment(day, HPos.CENTER);
        }

        for (int i = 1; i <= R.MAX_SLOT; i++) {
            Text slot = new Text(R.DAY_SLOT[i]);
            slot.setId("day_slot");
            pane.add(slot, 0, i);
            GridPane.setHalignment(slot, HPos.CENTER);
        }
        String tit = "";
        for (int i = 1; i <= R.MAX_DAY; i++) {
            for (int j = 1; j <= R.MAX_SLOT; j++) {
                CheckBox cb = new CheckBox();
                switch (which) {
                    case "H":
                        cb.setSelected(hard[j][i]);
                        cb.setDisable(soft[j][i]);
                        tit = title + "'s hard constraint";
                        break;
                    case "S":
                        cb.setSelected(soft[j][i]);
                        cb.setDisable(hard[j][i]);
                        tit = title + "'s soft constraint";
                        break;
                    case "time":
                        cb.setSelected(hard[j][i]);
                        tit = title + "'s lectures time";
                        break;
                }

                pane.add(cb, j, i);
                GridPane.setHalignment(cb, HPos.CENTER);
            }
        }
        //create stage
        Scene scene = new Scene(pane);
        Stage child = new Stage();
        scene.getStylesheets().add(getClass().getResource("/style/timestyle.css").toExternalForm());
        child.setTitle(tit);
        child.setScene(scene);
        child.getIcons().add(R.emu_icon);
        child.initModality(Modality.WINDOW_MODAL);
        child.setResizable(false);
        child.initOwner(stage);
        child.showAndWait();
    }

    //get time constraint
    private boolean[][] getTimeConstraint(String name, String which) {
        final boolean[][] time = new boolean[R.MAX_DAY + 1][R.MAX_SLOT + 1];
        final String key = name.replace(" ", "") + "_" + which;
        if (R.prop.containsKey(key)) {
            String[] times = R.prop.getProperty(key).split(",");
            for (String s : times) {
                int ds = Integer.valueOf(s.substring(1));
                int day = ds / 10;
                int slot = ds % 10;
                time[day][slot] = true;
            }
        }
        return time;
    }

    //store save time to the property file.
    private void saveTime(GridPane pane, String name, String which) {
        final String key = name.replace(" ", "") + "_" + which;
        if (which.equals("time")) {
            which = "T";
        }
        final StringBuilder sb = new StringBuilder();
        for (Node n : pane.getChildren()) {
            if (n instanceof CheckBox) {
                int day = GridPane.getColumnIndex(n);
                int slot = GridPane.getRowIndex(n);
                CheckBox cb = (CheckBox) n;
                if (cb.isSelected() && !cb.isDisable()) {
                    sb.append(which).append(10 * day + slot).append(",");
                }
            }
        }

        if (sb.length() != 0) {
            sb.deleteCharAt(sb.length() - 1);
            R.prop.setProperty(key, sb.toString());
            Logger.LOGI(key + "=" + sb.toString() + " added to "
                    + "the property file.");

        } else if (sb.length() == 0 && R.prop.containsKey(key)) {
            R.prop.remove(key);
            Object obj = openingCourseList.getSelectionModel().getSelectedItem();
            if (obj != null) {
                courseFixTime.setSelected(false);
            }
            if (key.contains("%") && R.COURSE_GROUP.containsKey(key.substring(0, key.indexOf("%")))) {
                R.COURSE_GROUP.remove(key.substring(0, key.indexOf("%")));
            }
            Logger.LOGI(key + " remove from propeties file.");
        }
    }

    //create room window/stage
    private void createRoomWindow(String title, GridPane gp) {
        BorderPane bp = new BorderPane();
        bp.setCenter(gp);
        BorderPane.setMargin(gp, new Insets(12, 12, 12, 12));
        BorderPane.setAlignment(gp, Pos.CENTER);
        Button selectAll = new Button("Select All");
        selectAll.setOnAction((ActionEvent event) -> {
            chooseRoom(gp, null, true);
        });
        selectAll.setDefaultButton(true);

        Button deselectAll = new Button("Deselect All");
        deselectAll.setOnAction((ActionEvent event) -> {
            chooseRoom(gp, null, false);
        });
        deselectAll.setDefaultButton(true);

        Button smart = new Button("Smart Board");
        smart.setDefaultButton(true);
        smart.setOnAction((ActionEvent t) -> {
            chooseRoom(gp, db.getResult(DatabaseOperation.GET_SMART_ROOM, "name"), true);
        });

        Button save = new Button("Save");
        save.setOnAction((ActionEvent event) -> {
            saveRoom(gp);
        });
        save.setDefaultButton(true);

        HBox hbox = new HBox(5);
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(selectAll, smart, deselectAll, save);
        BorderPane bp2 = new BorderPane();
        bp2.setCenter(hbox);
        BorderPane.setMargin(bp2, new Insets(12, 12, 12, 12));
        bp.setBottom(bp2);
        Scene scene = new Scene(bp);
        Stage child = new Stage();
        scene.getStylesheets().add(getClass().getResource("/style/timestyle.css").toExternalForm());
        child.setTitle(title + "'s " + (lab ? "lab" : "") + " room constraint.");
        child.setScene(scene);
        child.getIcons().add(R.emu_icon);
        child.initModality(Modality.WINDOW_MODAL);
        child.setResizable(false);
        child.initOwner(stage);
        child.showAndWait();
    }

    //create gridpane layout for room
    private GridPane createRoomWindow(String[] rooms) {
        if (!db.isConnected()) {
            db.connectDB();
        }
        final List<String> result = db.getResult(DatabaseOperation.GET_ROOM, "name");
        GridPane gp = new GridPane();
        ColumnConstraints cc = new ColumnConstraints();
        RowConstraints rc = new RowConstraints();
        cc.setFillWidth(true);
        rc.setFillHeight(true);
        gp.getColumnConstraints().add(cc);
        gp.getRowConstraints().add(rc);
        gp.setHgap(15);
        gp.setVgap(15);
        int x = 0, y = 0;
        for (String s : result) {
            CheckBox cb = new CheckBox(s);
            for (String check : rooms) {
                if (s.equals(check)) {
                    cb.setSelected(true);
                    break;
                }
            }
            cb.setMinWidth(90);
            cb.setMaxWidth(90);
            gp.add(cb, y, x);
            GridPane.setHalignment(cb, HPos.CENTER);
            x = (y == 4 ? x + 1 : x);
            y = (y == 4 ? 0 : y + 1);
        }
        return gp;
    }

    //choose selected room
    private void chooseRoom(GridPane gp, List rooms, boolean op) {
        for (Node n : gp.getChildren()) {

            if (n instanceof CheckBox) {
                CheckBox cb = (CheckBox) n;
                if (rooms != null && rooms.contains(cb.getText())) {
                    cb.setSelected(op);
                } else if (rooms == null) {
                    cb.setSelected(op);
                }
            }
        }
    }

    //store selected room to the property file.
    private void saveRoom(GridPane gp) {
        boolean flag = false;
        final StringBuilder sb = new StringBuilder();
        for (Node n : gp.getChildren()) {
            if (n instanceof CheckBox) {
                CheckBox cb = (CheckBox) n;
                if (cb.isSelected()) {
                    sb.append(cb.getText()).append(",");
                    flag = true;
                }
            }
        }
        if (!flag) {
            ShowMessage.showWarning("At least one room must be selected");
        } else {
            sb.deleteCharAt(sb.length() - 1);
            Object obj = openingCourseList.getSelectionModel().getSelectedItem();
            String key;
            if (lab) {
                key = obj.toString() + "_room";
            } else {
                key = teacherComboBox.getValue().toString().replace(" ", "")
                        + (obj == null ? "" : "_" + obj.toString()) + "_room";
            }
            R.prop.setProperty(key, sb.toString());
            Logger.LOGI(key + "=" + sb.toString() + " added to the property file");
            ((Stage) gp.getScene().getWindow()).close();
        }
    }

    //initialize reference/service which they are the same term.
    private void initRefSerCourse(String courseName, String which) {
        if (!db.isConnected()) {
            db.connectDB();
        }
        int t = (int) db.getResult(String.format(DatabaseOperation.GET_COURSE_TERM, courseName), 2);
        String lan = (String) db.getResult(String.format(DatabaseOperation.GET_COURSE_LANG, courseName), 5);
        refserCourses.getItems().clear();

        switch (which) {
            case "ref":
                refserCourses.getItems().addAll(db.getResult(String.format(DatabaseOperation.GET_COURSE_TERM_LANGUAGE, t, lan), "course_id"));
                break;
            case "ser":
                refserCourses.getItems().addAll(db.getResult(String.format(DatabaseOperation.GET_SERVICE_COURSE, t, lan), "course_id"));
                break;
        }

        refserCourses.getItems().remove(courseName);
    }

    //save done works to reduce users works.
    private void save(String key, String cor) {
        if (cor != null) {
            final StringBuilder sb = new StringBuilder();

            ObservableList<String> os = refserCourses.getItems();
            for (String s : os) {
                sb.append(s).append(",");
            }
            if (sb.length() != 0) {
                sb.deleteCharAt(sb.length() - 1);
                R.prop.setProperty(cor + "_temp_" + key, sb.toString());
            } else {
                if (R.prop.containsKey(cor + "_temp_" + key)) {
                    R.prop.remove(cor + "_temp_" + key);
                }
            }

            os = refserOpeningCourses.getItems();
            sb.delete(0, sb.length());
            for (String s : os) {
                switch (key) {
                    case "ser":
                        sb.append(s.substring(0, s.indexOf("%"))).append(",");
                        break;
                    case "ref":
                        sb.append(s).append(",");
                        break;
                }
            }

            if (sb.length() != 0) {
                sb.deleteCharAt(sb.length() - 1);
                R.prop.setProperty(cor + "_temp_" + key + "_sel", sb.toString());
            } else {
                if (R.prop.containsKey(cor + "_temp_" + key + "_sel")) {
                    R.prop.remove(cor + "_temp_" + key + "_sel");
                }
            }
            refserCourses.getItems().clear();
            refserOpeningCourses.getItems().clear();
        }
    }

    //to restore previous work to reduce the user's works
    private void restore(String key, String cor) {
        if (cor != null) {
            if (R.prop.containsKey(cor + "_temp_" + key)) {
                String[] list = R.prop.getProperty(cor + "_temp_" + key).split(",");
                for (String s : list) {
                    if (!refserCourses.getItems().contains(s) && !s.contains("%")) {
                        refserCourses.getItems().add(s);
                    }
                }
            }

            if (R.prop.containsKey(cor + "_temp_" + key + "_sel")) {
                String[] list = R.prop.getProperty(cor + "_temp_" + key + "_sel").split(",");
                ObservableList<String> os = refserOpeningCourses.getItems();
                for (String s : list) {
                    switch (key) {
                        case "ser":
                            int count = 1;
                            for (String ss : os) {
                                if (s.length() != 0 && ss.startsWith(s)) {
                                    ++count;
                                }
                            }
                            if (s.length() != 0) {
                                refserOpeningCourses.getItems().add(s + "%" + count);
                                R.COURSE_GROUP.put(s, count);
                            }
                            break;
                        case "ref":
                            if (s.length() != 0 && !refserOpeningCourses.getItems().contains(s)) {
                                refserOpeningCourses.getItems().add(s);
                            }
                            break;
                    }
                }
            }
        }
    }

    //check that the course selected before or not
    private void checkRefSer() {
        final List<String> list = new ArrayList<>();
        list.addAll(refserCourses.getItems());
        /*we dont need restore reference courses.because one course may be reference
         for one course but not others. so we just restore if service courses selected.*/
        if (serRadio.isSelected()) {
            for (String s : list) {
                Integer g = R.COURSE_GROUP.get(s);
                if (g != null) {
                    for (int i = 1; i <= g; i++) {
                        String temp = s + "%" + i;
                        if (R.prop.containsKey(temp + "_time")) {
                            if (!refserOpeningCourses.getItems().contains(temp)) {
                                refserOpeningCourses.getItems().add(temp);
                            }
                        }
                    }
                }
            }
        }
    }

    //add course and its instruction information to tree;
    private boolean addItemToTree(String tec, String info, String err) {
        rootItem.setExpanded(true);
        ImageView img = new ImageView(err.length() == 0 ? R.correct : R.wrong);
        TreeItem<String> empLeaf = new TreeItem<>(info, img);
        if (err.length() != 0) {
            TreeItem<String> error = new TreeItem<>(err);
            empLeaf.setExpanded(true);
            empLeaf.getChildren().add(error);
        }

        boolean found = false;
        boolean exsist = false;
        for (TreeItem<String> depNode : rootItem.getChildren()) {
            if (depNode.getValue().contentEquals(tec)) {
                for (TreeItem<String> ti : depNode.getChildren()) {
                    if (ti.getValue().contentEquals(info)) {
                        depNode.getChildren().set(depNode.getChildren().indexOf(ti), empLeaf);
                        exsist = true;
                        break;
                    }
                }
                if (!exsist) {
                    depNode.getChildren().add(empLeaf);
                }
                found = true;
                break;
            } else {
                for (TreeItem<String> ti : depNode.getChildren()) {
                    if (ti.getValue().contentEquals(info)) {
                        ShowMessage.showWarning(info + " already exsist. To add a new information\n"
                                + "please delete old one.");
                        return true;
                    }
                }
            }
        }
        if (!found) {
            TreeItem<String> depNode = new TreeItem<>(tec);
            rootItem.getChildren().add(depNode);
            depNode.getChildren().add(empLeaf);
            depNode.setExpanded(true);
        }
        if (!courseText.getText().equals("No course")) {
            flash(addRefSer,
                    courseText.getText() + ", group:" + groupnumText.getText() + " and its instructor's\ninformation"
                    + " saved succesfully.");
        }
        return exsist;
    }

    //import resource to the file
    @SuppressWarnings({"BroadCatchBlock", "TooBroadCatch"})
    private void importRes(File file) {
        final List<String> temp = new ArrayList<>();
        rootItem.getChildren().clear();
        R.COURSE_GROUP.clear();
        FileReader fr = null;
        BufferedReader br = null;
        List<String> list = new ArrayList<>();
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            R.prop.load(new FileInputStream(file.getPath() + ".prop"));
            String line;
            while ((line = br.readLine()) != null) {
                String tec = line.substring(0, line.indexOf(","));
                String cor = line.substring(line.indexOf(",") + 1, line.indexOf(":"));
                if (!openingCourseList.getItems().contains(cor) && !temp.contains(cor)) {
                    temp.add(cor);
                }
                Integer g = !R.COURSE_GROUP.containsKey(cor) ? 1 : R.COURSE_GROUP.get(cor) + 1;
                R.COURSE_GROUP.put(cor, g);
                cor += ", Group:" + g;
                String err = "";
                if (line.contains("Error:")) {
                    err = line.substring(line.lastIndexOf(":") + 1).replace("%", "\n");
                }
                addItemToTree(tec, cor, err);
            }
            flash(dayText, "Importing resource file isfinished successfully.");
            chooseImportCourse(temp);
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file.getPath() + ".info"));
            R.COURSE_GROUP = (HashMap<Object, Integer>) ois.readObject();

        } catch (IOException | ClassNotFoundException ex) {
            Logger.LOGS(ex.getMessage());
            ShowMessage.showError(ex);
        } finally {
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

    //export resource from file
    private void exportRes(File file) {
        FileWriter fr;
        BufferedWriter br = null;
        try {
            fr = new FileWriter(file);
            br = new BufferedWriter(fr);
            for (TreeItem<String> mather : rootItem.getChildren()) {
                String line = mather.getValue();
                for (TreeItem<String> child : mather.getChildren()) {
                    line += "," + child.getValue().substring(0, child.getValue().indexOf(",")) + ":";
                    if (!child.isLeaf()) {
                        line += "Error:" + child.getChildren().get(0).getValue().replace("\n", "%");
                    }
                    br.write(line);
                    br.write("\n");
                    line = mather.getValue();
                }
            }
            File propFile = new File(file.getPath() + ".prop");
            if (!propFile.exists()) {
                propFile.createNewFile();
            }
            R.prop.store(new FileOutputStream(propFile), null);

            File coursesInfo = new File(file.getPath() + ".info");
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(coursesInfo));
            oos.writeObject(R.COURSE_GROUP);
            flash(dayText, "Exporting resource file is successfully.");
        } catch (IOException ex) {
            Logger.LOGS(ex.getMessage());
            ShowMessage.showError(ex);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                Logger.LOGS(ex.getMessage());
                ShowMessage.showError(ex);
            }
        }
    }

    private void deleteTreeItem(String teacher, String course) {
        Integer g = R.COURSE_GROUP.get(course);
        boolean finish = false;
        int gr = 1;
        int tec = -1;
        TreeItem<String> cor = null;
        for (TreeItem<String> ti : rootItem.getChildren()) {
            for (TreeItem<String> tii : ti.getChildren()) {
                if ((teacher == null || ti.getValue().contentEquals(teacher))
                        && tii.getValue().contains(course) && cor == null) {
                    tec = rootItem.getChildren().indexOf(ti);
                    cor = tii;
                } else if (tii.getValue().contains(course)) {
                    String info = course + ", Group:" + gr;
                    tii.setValue(info);
                    gr++;
                    if (gr == g) {
                        finish = true;
                    }
                }
            }
        }
        (rootItem.getChildren().get(tec)).getChildren().remove(cor);
        if (rootItem.getChildren().get(tec).isLeaf()) {
            rootItem.getChildren().remove(tec);
        }
        R.COURSE_GROUP.put(course, --g);
    }

    //create room constraint window/stage
    private void initializeRoomConstraint() {
        roomGrid.getChildren().clear();
        ColumnConstraints cc = new ColumnConstraints();
        RowConstraints rc = new RowConstraints();
        cc.setFillWidth(true);
        rc.setFillHeight(true);
        roomGrid.getColumnConstraints().add(cc);
        roomGrid.getRowConstraints().add(rc);
        roomGrid.setHgap(15);
        roomGrid.setVgap(15);
        int x = 0, y = 0;
        if (!db.isConnected()) {
            db.connectDB();
        }
        final List<String> result = db.getResult(DatabaseOperation.GET_ROOM, "name");
        for (String s : result) {
            RadioButton rd = new RadioButton(s);
            rd.setUserData(s);
            rd.setMinWidth(90.0);
            roomGroup.getToggles().add(rd);
            roomGrid.add(rd, y, x);
            GridPane.setHalignment(rd, HPos.CENTER);
            x = (y == 4 ? x + 1 : x);
            y = (y == 4 ? 0 : y + 1);
        }
        roomGroup.selectToggle(roomGroup.getToggles().get(0));
    }

    //database operation
    private void databaseOperation(String tableName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/databaseoperation.fxml"));
            Parent view = (Parent) loader.load();
            Scene newScene = new Scene(view, 600, 400);
            newScene.getStylesheets().add(getClass().getResource("/style/" + tableName.toLowerCase() + ".css").toExternalForm());
            Stage viewStage = new Stage();
            viewStage.setScene(newScene);
            viewStage.getIcons().add(R.emu_icon);
            viewStage.setTitle(tableName + " Operation");
            DatabaseoperationController dbc = loader.getController();
            dbc.setController(viewStage, tableName, db.getColumns(tableName), content);
            viewStage.initModality(Modality.WINDOW_MODAL);
            viewStage.initOwner(stage);
            viewStage.showAndWait();
            refreshDatabase();
        } catch (IOException ex) {
            Logger.LOGS(ex.getMessage());
            ShowMessage.showError(ex);
        }
    }

    //refresh opening course list after changing any information in COURSE table
    private void refreshDatabase() {
        if (!db.isConnected()) {
            db.connectDB();
        }
        teacherComboBox.getItems().clear();
        teacherComboBox.getItems().addAll(db.getResult(DatabaseOperation.GET_TEACHER, "name"));
        initializeRoomConstraint();
        ShowMessage.showInformation("If you did any change on the COURSE table\n"
                + "please click the \'refresh\' button to update the courses list.\n"
                + "Otherwise you can get error or wrong result.");
    }

    private void chooseImportCourse(List<String> temp) {
        Text lbl = new Text("The following course(s) is(are) not in opening course list. Tick which course"
                + " you want to add to the opening course or untick to remove from imported list.");
        lbl.setWrappingWidth(295);
        lbl.setTextAlignment(TextAlignment.JUSTIFY);
        lbl.setStyle("-fx-font-size:14px;-fx-font-weight:bold;");
        ListView lw = new ListView<>();
        final List<String> remove = new ArrayList<>();
        remove.addAll(temp);
        for (String s : temp) {
            CheckBox cb = new CheckBox(s);
            cb.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (newValue) {
                    if (remove.contains(s)) {
                        remove.remove(s);
                    }
                    if (!openingCourseList.getItems().contains(s)) {
                        openingCourseList.getItems().add(s);
                    }
                } else {
                    if (!remove.contains(s)) {
                        remove.add(s);
                    }
                    if (openingCourseList.getItems().contains(s)) {
                        openingCourseList.getItems().remove(s);
                    }
                }
            });
            lw.getItems().add(cb);
        }
        final Button selectAll = new Button("Add all");
        selectAll.setDefaultButton(true);
        selectAll.setOnAction((ActionEvent event) -> {
            remove.clear();
            for (String s : temp) {
                if (!openingCourseList.getItems().contains(s)) {
                    openingCourseList.getItems().add(s);
                }
            }
            ((Stage) selectAll.getScene().getWindow()).close();
        });
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(lbl, selectAll, lw);
        VBox.setMargin(lbl, new Insets(0, 5, 0, 5));
        VBox.setMargin(selectAll, new Insets(0, 5, 0, 5));
        Stage child = new Stage();
        child.setTitle("Checking opening course");
        child.initModality(Modality.WINDOW_MODAL);
        child.initOwner(stage);
        Scene scene = new Scene(vbox, 300, 350);
        child.setScene(scene);
        child.setResizable(false);
        child.showAndWait();
        for (String s : remove) {
            if (R.COURSE_GROUP.containsKey(s)) {
                int gr = (int) R.COURSE_GROUP.get(s);
                for (int i = 1; i <= gr; i++) {
                    deleteTreeItem(null, s);
                }
            }
            Logger.LOGI(s + " remove from the treeList.");
        }
    }
}
