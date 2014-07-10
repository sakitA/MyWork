/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import util.R;
import util.ShowMessage;

/**
 * FXML Controller class
 *
 * @author Sakit
 */
public class CoursedialogController implements Initializable {

    private static final ObservableList<String> list = FXCollections.observableArrayList();
    @FXML
    private TextField courseID;
    @FXML
    private TextField courseTerm;
    @SuppressWarnings("rawtypes")
	@FXML
    private ComboBox categoryCombo;
    @FXML
    private ComboBox typeCombo;
    @FXML
    private ComboBox languageCombo;
    @FXML
    private Button saveButton;
    private Stage stage;

    static ObservableList<String> getResult() {
        if (list.size() == 5) {
            return list;
        }
        list.clear();
        return list;
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        categoryCombo.getItems().addAll((Object[]) R.COURSE_CATEGORY);
        typeCombo.getItems().addAll((Object[]) R.COURSE_TYPE);
        languageCombo.getItems().addAll((Object[]) R.LANGUAGE);
        if (rb == null) {
            saveButton.setText("Add a new course");
        } else {
            saveButton.setText("Change " + rb.getString("course_id") + " information");
            courseID.setText(rb.getString("course_id"));
            courseTerm.setText(rb.getString("term"));
            categoryCombo.getSelectionModel().select(rb.getString("category"));
            typeCombo.getSelectionModel().select(rb.getString("type").toUpperCase());
            languageCombo.getSelectionModel().select(rb.getString("language"));
        }
    }

    @FXML
    private void saveInput() {
        list.clear();
        if (courseID.getText().length() == 0 || courseID.getText().trim().length() == 0) {
            showWarningMessage("Enter course ID");
            return;
        } else {
            list.add(courseID.getText().toUpperCase());
        }

        if (courseTerm.getText().length() == 0 || courseTerm.getText().trim().length() == 0) {
            showWarningMessage("Enter course term");
            return;
        } else {
            if (!courseTerm.getText().matches("[1-9]")) {
                showWarningMessage("Enter valid course term");
                return;
            } else {
                list.add(courseTerm.getText());
            }
        }

        if (categoryCombo.getSelectionModel().getSelectedIndex() == -1) {
            showWarningMessage("Choose course category");
            return;
        } else {
            list.add(((String) categoryCombo.getSelectionModel().getSelectedItem()).toLowerCase());
        }

        if (typeCombo.getSelectionModel().getSelectedIndex() == -1) {
            list.add("");
        } else {
            list.add((String) typeCombo.getSelectionModel().getSelectedItem());
        }

        if (languageCombo.getSelectionModel().getSelectedIndex() == -1) {
            showWarningMessage("Choose course language");
            return;
        } else {
            if (languageCombo.getSelectionModel().getSelectedItem().equals("English")) {
                list.add("en");
            } else {
                list.add("tr");
            }
        }

        ((Stage) saveButton.getScene().getWindow()).close();
    }

    private void showWarningMessage(String error) {
        ShowMessage.showWarning(error);
    }

    void setStage(Stage child) {
        stage = child;
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                list.clear();
            }
        });
    }
}
