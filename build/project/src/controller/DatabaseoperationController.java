/**
 * Department of Computer Engineering, Eastern Mediterranean University.
 * @link http://cmpe.emu.edu.tr
 *
 * @author Sakit Atakishiyev, Std. ID: 117319
 * @version 1.0.0.1
 */
package controller;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import util.DatabaseOperation;
import util.Logger;
import util.R;
import util.ShowMessage;

/**
 * This class for Database operation. Such as adding/deleting/editing new
 * teacher,course, and room
 *
 * @author Sakit
 */
public class DatabaseoperationController implements Initializable {

    @SuppressWarnings("rawtypes")
	private ObservableList<ObservableList> data = FXCollections.observableArrayList();
    private Stage stage;
    private String tableName;
    @FXML
    private Button add;
    @FXML
    private Button edit;
    @FXML
    private Button delete;

    @FXML
    private TableView table;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    private void addNewItem() {
        if (tableName.equalsIgnoreCase("teacher")) {
            addTeacher();
        } else if (tableName.equalsIgnoreCase("room")) {
            addRoom();
        } else if (tableName.equalsIgnoreCase("course")) {
            addCourse();
        }
    }

    @FXML
    private void editItem() {
        int index = table.getSelectionModel().getSelectedIndex();
        if (index == -1) {
            ShowMessage.showWarning("Choose any information from the table.");
        } else {
            String selected = ((ObservableList) table.getItems().get(index)).get(0).toString();
            Action answer = ShowMessage.questionConfirm("Do you want to change " + selected + " info?");
            if (answer == Dialog.Actions.YES) {
                if (tableName.equalsIgnoreCase("teacher")) {
                    changeTeacher(selected, index);
                } else if (tableName.equalsIgnoreCase("room")) {
                    changeRoom(selected, index);
                } else if (tableName.equalsIgnoreCase("course")) {
                    changeCourse();
                }
            }else{
                flash(add, "Operation is cancelled");
            }
        }
        table.getSelectionModel().clearSelection();
    }

    @FXML
    private void deleteItem() {
        int index = table.getSelectionModel().getSelectedIndex();
        if (index == -1) {
            ShowMessage.showWarning("Choose any information from the table.");
        } else {
            String selected = ((ObservableList) table.getItems().get(index)).get(0).toString();
            Action answer = ShowMessage.questionConfirm("Do you want to delete " + selected + " info?");
            if (answer == Dialog.Actions.YES) {
                if (tableName.equalsIgnoreCase("teacher")) {
                    deleteTeacher(selected, index);
                } else if (tableName.equalsIgnoreCase("room")) {
                    deleteRoom(selected, index);
                } else if (tableName.equalsIgnoreCase("course")) {
                    deleteCourse(selected, index);
                }
            }else{
                flash(add, "Operation is cancelled");
            }
        }
        table.getSelectionModel().clearSelection();
    }

    @SuppressWarnings("Convert2Lambda")
    private void createTable(List<String> columns) {
        table.getColumns().clear();
        int i = 0;
        for (String s : columns) {
            final int j = i++;
            TableColumn tcol = new TableColumn(s);
            tcol.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty(param.getValue().get(j).toString());
                }
            });

            tcol.setMinWidth(100);
            table.getColumns().add(tcol);
        }
    }

    public void setController(Stage stage, final String tableName, List<String> columns, ObservableList<ObservableList> result) {
        this.stage = stage;
        this.tableName = tableName;
        createTable(columns);
        data = result;
        table.setItems(data);
    }

    /*Thank you to Rob Mayhew for fading message function code
     http://robmayhew.com/javafx-2-0-fading-status-message/http://robmayhew.com/javafx-2-0-fading-status-message/
     */
    public static void flash(Node node, String message) {
        Font font = Font.font("Verdana", FontWeight.NORMAL, 14);
        Color boxColor = Color.GREY;
        Color textColor = Color.WHITE;
        double duration = 5000;
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

    /*----------------------operation for teacher------------------------------*/
    private void addTeacher() {
        ObservableList<String> val = FXCollections.observableArrayList();
        Optional<String> question = Dialogs.create()
                .owner(stage)
                .title("Input Dialog")
                .message("Enter teacher full name")
                .showTextInput("E.g: Name Surname");

        if (question.isPresent()) {
            String input = question.get();
            val.add(input);
            data.add(val);
            if (!SetresourceController.db.isConnected()) {
                SetresourceController.db.connectDB();
            }
            boolean ok = SetresourceController.db.insertTeacher(input);
            if (ok) {
                flash(add, input + " added to database successfully");
            } else {
                flash(add, "Database operation failed.Please try again");
                data.remove(val);
            }
        } else {
            flash(add, "Operation is canceled");
        }
    }

    private void changeTeacher(String selected, int index) {
        ObservableList<String> val = FXCollections.observableArrayList();
        Optional<String> question = Dialogs.create()
                .owner(stage)
                .title("Input Dialog")
                .message("Change teacher full name")
                .showTextInput(selected);

        if (question.isPresent() && !question.get().equals(selected)) {
            String input = question.get();
            val.add(input);
            data.set(index, val);
            if (!SetresourceController.db.isConnected()) {
                SetresourceController.db.connectDB();
            }
            boolean ok = SetresourceController.db.updateTeacher(selected, input);

            if (ok) {
                flash(edit, selected + " information has changed successfully. New information is:"
                        + input);
            }else {
                flash(edit, "Database operation failed.Please try again");
            }
        } else{
            flash(add, "Operation is canceled");
        } 
    }

    private void deleteTeacher(String selected, int index) {
        table.getItems().remove(index);
        if (!SetresourceController.db.isConnected()) {
            SetresourceController.db.connectDB();
        }
        boolean ok = SetresourceController.db.deleteTeacher(selected);
        if (ok) {
            flash(delete, selected + " information deleted");
        } else {
            flash(delete, "Database operation failed.Please try again");
        }
    }
    /*---------------------------------end-------------------------------------*/

    /*------------------------operation for room-------------------------------*/
    private void addRoom() {
        ObservableList<String> val = FXCollections.observableArrayList();
        Dialog dlg = new Dialog(stage, "Room Dialog");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 10));
        TextField name = new TextField();
        name.setPromptText("E.g CMPE129");

        grid.add(new Label("Enter room name:"), 0, 0);
        grid.add(name, 1, 0);
        CheckBox smart = new CheckBox("Smartboard exist");
        grid.add(smart, 1, 1);
        dlg.setContent(grid);
        dlg.getActions().addAll(Dialog.Actions.OK, Dialog.Actions.CANCEL);
        Action res = dlg.show();
        if (res == Dialog.Actions.OK) {
            String input = name.getText();
            boolean ok = false;
            if (input != null && input.length() != 0) {
                val.clear();
                input = input.toUpperCase();
                val.add(input);
                val.add(smart.isSelected() ? "true" : "false");
                data.add(val);

                if (!SetresourceController.db.isConnected()) {
                    SetresourceController.db.connectDB();
                }
                ok = SetresourceController.db.insertRoom(input, smart.isSelected());
            }

            if (ok && input != null) {
                flash(add, input + " added to database successfully");
            } else if (!ok) {
                flash(add, "Database operation failed.Please try again");
                data.remove(val);
            }
        } else {
            flash(add, "Operation is cancelled");
        }
    }

    private void changeRoom(String selected, int index) {
        ObservableList<String> val = FXCollections.observableArrayList();
        Dialog dlg = new Dialog(stage, "Login Dialog");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 10));
        TextField name = new TextField();
        name.setText(selected);

        grid.add(new Label("Enter room name:"), 0, 0);
        grid.add(name, 1, 0);
        CheckBox smart = new CheckBox("Smartboard exist");
        smart.setSelected(DatabaseOperation.isSmart(selected));
        grid.add(smart, 1, 1);
        dlg.setContent(grid);
        dlg.getActions().addAll(Dialog.Actions.OK, Dialog.Actions.CANCEL);
        Action res = dlg.show();
        if (res == Dialog.Actions.OK) {
            String input = name.getText();
            boolean ok = false;
            if (input != null && input.length() != 0) {
                val.clear();
                input = input.toUpperCase();
                val.add(input);
                val.add(smart.isSelected() ? "true" : "false");
                data.set(index, val);
                if (!SetresourceController.db.isConnected()) {
                    SetresourceController.db.connectDB();
                }
                ok = SetresourceController.db.updateRoom(selected, input, smart.isSelected());
            }

            if (ok && input != null) {
                flash(edit, selected + " information has changed successfully. New information is:"
                        + input);
            } else if (!ok) {
                flash(edit, "Database operation failed.Please try again");

            }
        } else {
            flash(edit, "Operation is cancelled");
        }
    }

    private void deleteRoom(String selected, int index) {
        table.getItems().remove(index);
        if (!SetresourceController.db.isConnected()) {
            SetresourceController.db.connectDB();
        }
        boolean ok = SetresourceController.db.deleteRoom(selected);
        if (ok) {
            flash(delete, selected + " information deleted");
        } else {
            flash(add, "Database operation failed.Please try again");
        }
    }
    /*-------------------------------end---------------------------------------*/

    /*----------------------course operation-----------------------------------*/
    private void addCourse() {
        ObservableList<String> val = FXCollections.observableArrayList();
        openCourseDialog(null);
        val.addAll(CoursedialogController.getResult());
        if (val.size() != 0) {
            data.add(val);
            if (!SetresourceController.db.isConnected()) {
                SetresourceController.db.connectDB();
            }
            final StringBuilder sb = new StringBuilder();
            for (String s : val) {
                sb.append(s).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);

            boolean ok = SetresourceController.db.insertCourse(sb.toString());
            if (ok) {
                flash(add, val.get(0) + " added to database successfully");
            } else {
                flash(add, "Database operation failed.Please try again");
                data.remove(val);
            }
        }else
            flash(add,"Operation is cancelled");
    }

    private void changeCourse() {
        ObservableList<String> val = FXCollections.observableArrayList();
        String old = (String) ((ObservableList) table.getSelectionModel().getSelectedItem()).get(0);
        openCourseDialog(getResource());
        val.addAll(CoursedialogController.getResult());
        if (!val.isEmpty()) {

            int index = table.getSelectionModel().getSelectedIndex();
            data.set(index, val);
            if (!SetresourceController.db.isConnected()) {
                SetresourceController.db.connectDB();
            }
            final StringBuilder sb = new StringBuilder();
            for (String s : val) {
                sb.append(s).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);

            boolean ok = SetresourceController.db.updateCourse(old, sb.toString());
            if (ok) {
                flash(edit, old + " information has changed successfully.");
            } else {
                flash(edit, "Database operation failed.Please try again");
            }
        }else
            flash(add,"Operation is cancelled");
    }

    private void deleteCourse(String selected, int index) {
        if (!SetresourceController.db.isConnected()) {
            SetresourceController.db.connectDB();
        }

        boolean ok = SetresourceController.db.deleteCourse(selected);
        if (ok) {
            table.getItems().remove(index);
            flash(delete, selected + " information deleted");
        } else {
            flash(add, "Database operation failed.Please try again");
        }
    }

    private void openCourseDialog(ResourceBundle resource) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/coursedialog.fxml"));
            loader.setResources(resource);
            Parent root = (Parent) loader.load();
            Scene scene = new Scene(root, 350, 400);
            Stage child = new Stage();
            CoursedialogController cc = loader.getController();
            cc.setStage(child);
            child.setScene(scene);
            child.getIcons().add(R.emu_icon);
            child.setTitle("Course Operation");
            child.setResizable(false);
            child.initModality(Modality.WINDOW_MODAL);
            child.initOwner(stage);
            child.showAndWait();
        } catch (IOException ex) {
            Logger.LOGS(ex.getMessage());
            ShowMessage.showError(ex);
        }
    }

    private ResourceBundle getResource() {
        final ObservableList<String> val = FXCollections.observableArrayList();
        int index = table.getSelectionModel().getSelectedIndex();
        if (index == -1) {
            ShowMessage.showWarning("Choose any information from the table.");
        } else {
            ResourceBundle rb = new ResourceBundle() {

                @Override
                protected Object handleGetObject(String key) {
                    val.addAll(data.get(table.getSelectionModel().getSelectedIndex()));
                    switch (key) {
                        case "course_id":
                            return val.get(0);
                        case "term":
                            return val.get(1);
                        case "category":
                            char[] c = val.get(2).toCharArray();
                            c[0] = (char) (c[0] ^ 32);
                            return new String(c);
                        case "type":
                            return val.get(3);
                        case "language":
                            return val.get(4).equals("en") ? "English" : "Turkish";
                        case "arco":
                            return val.get(5);
                    }
                    return null;
                }

                @Override
                public Enumeration<String> getKeys() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            };
            return rb;
        }
        return null;
    }

    /**
     * ********************************end*************************************
     */
}
