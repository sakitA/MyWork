/**
 * Department of Computer Engineering, Eastern Mediterranean University.
 * @link http://cmpe.emu.edu.tr
 *
 * @author Sakit Atakishiyev, Std. ID: 117319
 * @version 1.0.0.1
 */
package controller;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import util.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.ButtonBar;
import org.controlsfx.control.ButtonBar.ButtonType;
import org.controlsfx.control.action.AbstractAction;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import util.DatabaseOperation;
import util.R;
import util.ShowMessage;

/**
 * This controller class control the main page.
 *
 * @author Sakit
 */
public class MainpageController implements Initializable {

    private static Stage stage;
    public static final ObservableList<String> openingCourses = FXCollections.observableArrayList();
    static boolean GENERATE;
    static boolean SOLUTION;

    private static void checkAllSelectedCourses() {
        final List<String> temp_list = new ArrayList<>();
        temp_list.addAll(openingCourses);

        for (String s : openingCourses) {
            if (!DatabaseOperation.isCourseExist(s)) {
                temp_list.remove(s);
                Logger.LOGI(s + " is not exist so it will be deleted.");
            }
        }
        openingCourses.clear();
        openingCourses.addAll(temp_list);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    /*close the program*/
    @FXML
    private void exit() {
        System.exit(0);
    }

    @FXML
    private void options() {
        // Create the custom dialog.
        Dialog dlg = new Dialog(stage, "Login Dialog");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 10));

        popSize.setPromptText(String.valueOf(GenerateController.populationSize));
        genSize.setPromptText(String.valueOf(GenerateController.generationSize));

        grid.add(new Label("Population size:"), 0, 0);
        grid.add(popSize, 1, 0);
        grid.add(new Label("Generation size:"), 0, 1);
        grid.add(genSize, 1, 1);

        ButtonBar.setType(save, ButtonType.OK_DONE);
        save.disabledProperty().set(true);

        // Do some validation (using the Java 8 lambda syntax).
        popSize.textProperty().addListener((observable, oldValue, newValue) -> {
            save.disabledProperty().set(newValue.trim().isEmpty());
            if(!newValue.matches("[0-9]+") ){
                ShowMessage.showWarning("Please only number");
                save.disabledProperty().set(false);
            }
        });
        
        genSize.textProperty().addListener((observable, oldValue, newValue) -> {
            save.disabledProperty().set(newValue.trim().isEmpty());
            if(!newValue.matches("[0-9]+") ){
                ShowMessage.showWarning("Please only number");
                save.disabledProperty().set(false);
            }
        });

        dlg.setMasthead("Change default features of genetic algorithm");
        dlg.setContent(grid);
        dlg.getActions().addAll(save, Dialog.Actions.CANCEL);

        // Request focus on the username field by default.
        // Platform.runLater(() -> popSize.requestFocus());
        dlg.show();
    }

    @FXML
    private void openGuidline() {
        ShowMessage.showInformation("This part is not reday yet. Next update will be.");
    }

    @FXML
    private void about() {
        ShowMessage.showInformation("This part is not reday yet. Next update will be.");
    }

    /*go to the department web site*/
    @FXML
    private void goCMPE() {
        openWebSite(R.CMPE_URL);
    }

    /*go to the developer linkedin profile*/
    @FXML
    private void goSA() {
        openWebSite(R.SA_URL);
    }

    /*open set resource window/stage*/
    @FXML
    private void setResource() {
        chooseOpeningCourse(stage);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/setresource.fxml"));
            Parent root = (Parent) loader.load();
            Stage child = new Stage();
            Scene scene = new Scene(root);

            child.setScene(scene);
            child.getIcons().add(R.emu_icon);
            child.setResizable(true);
            child.initOwner(stage);
            SetresourceController controller = loader.getController();
            controller.setStage(child);
            child.setTitle("Set Resources");
            child.initOwner(stage);
            child.initModality(Modality.WINDOW_MODAL);
            child.showAndWait();
            if (GENERATE) {
                generateSolution();
            }
        } catch (IOException ex) {
            Logger.LOGS(ex.getMessage());
            ShowMessage.showError(ex);
        }
    }

    @FXML
    private void generateSolution() {
        if (R.checkFile()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/generate.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 720, 300);
                Stage child = new Stage();
                GenerateController gc = loader.getController();
                gc.setStage(child);
                child.setScene(scene);
                child.getIcons().add(R.emu_icon);
                child.setTitle("Generate Solution");
                child.setResizable(false);
                child.initModality(Modality.WINDOW_MODAL);
                child.initOwner(stage);
                child.showAndWait();
                if (SOLUTION) {
                    showResult();
                }
            } catch (IOException ex) {
                Logger.LOGS(ex.getMessage());
                ShowMessage.showError(ex);
            }
        }
    }

    @FXML
    private void showResult() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/timetable.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1250, 400);
            scene.getStylesheets().add(MainpageController.class.getResource("/style/tooltip.css").toExternalForm());
            Stage child = new Stage();
            TimetableController ttc = loader.getController();
            ttc.setRes(child);
            child.setScene(scene);
            child.getIcons().add(R.emu_icon);
            child.setTitle("TimeTable Solution");
            child.initOwner(stage);
            child.show();
        } catch (IOException ex) {
            Logger.LOGS(ex.getMessage());
            ShowMessage.showError(ex);
        }
    }

    public static void openWebSite(final String url) {
        Desktop desktop = Desktop.getDesktop();

        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(URI.create(url));
            } catch (IOException ex) {
                Logger.LOGS(ex.getMessage());
                ShowMessage.showError(ex);
            }
        } else {
            Logger.LOGW("This system doesn't support Desktop");
            ShowMessage.showInformation("Your system doesn't support Desktop."
                    + "\nYou can browse manualy. http://cmpe.emu.edu.tr");
        }
    }

    public void setStage(Stage stage) {
        MainpageController.stage = stage;
    }

    /*before open the set resource window/stage user should choose the opening
     *course that term.*/
    public static void chooseOpeningCourse(Stage owner) {

        final DatabaseOperation db = new DatabaseOperation();
        CheckBoxTreeItem<String> rootItem = new CheckBoxTreeItem<>();
        rootItem.setExpanded(true);
        if (!db.isConnected()) {
            db.connectDB();
        }
        for (int i = 1; i <= R.MAX_TERM; i++) {
            final String query = String.format(DatabaseOperation.GET_COURSE_BY_TERM, i);
            final List<String> result = db.getResult(query, "course_id");
            final CheckBoxTreeItem<String> child = new CheckBoxTreeItem<>(i + " term courses");
            child.setExpanded(true);

            for (String s : result) {
                CheckBoxTreeItem<String> descent = new CheckBoxTreeItem<>(s);
                if (openingCourses.contains(s)) {
                    descent.setSelected(true);
                   
                } else {
                    descent.setSelected(false);
                }
                child.getChildren().add(descent);
                descent.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    if (newValue) {
                        openingCourses.add(descent.getValue());
                    } else {
                        openingCourses.remove(descent.getValue());
                    }
                });
            }
            rootItem.getChildren().add(child);
        }
        TreeView tree = new TreeView(rootItem);
        tree.setCellFactory(CheckBoxTreeCell.<String>forTreeView());
        tree.setShowRoot(false);

        StackPane root = new StackPane();
        root.getChildren().add(tree);
        Stage childStage = new Stage();
        childStage.getIcons().add(R.emu_icon);
        childStage.setTitle("Choose Opening Courses");
        Scene scene = new Scene(root, 300, 450);
        childStage.setScene(scene);
        childStage.initOwner(owner);
        childStage.setResizable(false);
        childStage.initModality(Modality.WINDOW_MODAL);
        childStage.showAndWait();
        checkAllSelectedCourses();
    }

    final TextField popSize = new TextField();
    final TextField genSize = new TextField();
    final Action save = new AbstractAction("Save") {
        // This method is called when the save button is clicked ...
        public void handle(ActionEvent ae) {
            Dialog d = (Dialog) ae.getSource();
            // Do the operation here.
            GenerateController.populationSize = Integer.valueOf(popSize.getText());
            GenerateController.generationSize = Integer.valueOf(genSize.getText());
            d.hide();
        }
    };
}
