/**
 * Department of Computer Engineering, Eastern Mediterranean University.
 * @link http://cmpe.emu.edu.tr
 *
 * @author Sakit Atakishiyev, Std. ID: 117319
 * @version 1.0.0.1
 */
package controller;

import GA.GeneticAlgorithm;
import GA.Population;
import GA.TimeTable;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import type.Course;
import type.Teacher;
import util.DatabaseOperation;
import util.Info;
import util.Logger;
import util.R;
import static util.R.path;
import util.ShowMessage;

/**
 * FXML Controller class control the timetable fxml. In this class we analyze
 * the solution. We can see all teachers, courses, and rooms timetable in a
 * week. Also we can see alternative time table solutions.
 *
 * @author Sakit
 */
public class TimetableController implements Initializable {

    private final ObservableList<String> teachers = FXCollections.observableArrayList();
    private final ObservableList<String> courses = FXCollections.observableArrayList();
    private final ObservableList<String> rooms = FXCollections.observableArrayList();
    private final FileChooser chooser = new FileChooser();
    private final List<String> list1 = new ArrayList<>();
    private File printFile;
    private Info[][][] info;
    private String selected;
    private Stage stage;
    public  Population pop;
    public static boolean multiple = false;

    @FXML
    private ComboBox option;
    @FXML
    private BorderPane borderPane;
    @FXML
    private RadioButton teacher;
    @FXML
    private RadioButton course;
    @FXML
    private RadioButton room;
    @FXML
    private Button doc;
    @FXML
    private Button pdf;
    @FXML
    private Button showAll;
    @FXML
    private ListView popList;
    @FXML
    private GridPane timeGrid;
    @FXML
    private ToggleGroup listOption;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @SuppressWarnings("unchecked")
	@Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        for (Object o : R.TEACHERS.keySet()) {
            teachers.add((String) o);
        }

        for (Object o : R.COURSES.keySet()) {
            courses.add((String) o);
        }

        rooms.addAll(R.ALLR);

        teacher.setUserData("teacher");
        course.setUserData("course");
        room.setUserData("room");
        doc.setGraphic(new ImageView(R.doc));
        pdf.setGraphic(new ImageView(R.pdf));

        option = new ComboBox();
        option.getItems().addAll(teachers);
        option.setId("combo");
        option.setMinSize(150, 25);
        option.setPrefWidth(option.getMinWidth());
        option.getStylesheets().add(getClass().getResource("/style/main.css").toExternalForm());
        option.setPromptText("Choose " + (String) listOption.getSelectedToggle().getUserData());
        createTimeTable();
        option.getSelectionModel().selectedItemProperty().addListener((ObservableValue ov, Object t, Object t1) -> {
            clearTable();
            if (t1 != null) {
                selected = (String) t1;
                showResult();
            }
        });

        listOption.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1) -> {
            multiple = false;
            option.setDisable(multiple);
            option.getItems().clear();
            option.getSelectionModel().clearSelection();
            option.setValue(null);
            clearTable();

            switch ((String) t1.getUserData()) {
                case "teacher":
                    option.getItems().addAll(teachers);
                    break;
                case "course":
                    option.getItems().addAll(courses);
                    break;
                case "room":
                    option.getItems().addAll(rooms);
                    break;
                default:
                    option.setDisable(true);
                    openCourseWindow();
            }
            option.setPromptText("Choose " + (String) t1.getUserData());
            showAll.setVisible(course.isSelected());
        });
        if (GenerateController.init) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path + File.separator + "term.solution"));
                pop = (Population) ois.readObject();
            } catch (IOException | ClassNotFoundException ex) {
                Logger.LOGS(ex.getMessage());
                ShowMessage.showError(ex);
            }
        }
        if (pop != null) {
            fillList();

            popList.getSelectionModel().select(0);

            info = pop.getTimeTable(0).getTimeTable();
        }
        popList.getSelectionModel().selectedItemProperty().addListener((ObservableValue ov, Object t, Object t1) -> {
            if (t1 != null) {
                info = pop.getTimeTable(
                        popList.getSelectionModel().getSelectedIndex()).getTimeTable();

                clearTable();
                if (!multiple && option.getSelectionModel().getSelectedItem() != null) {
                    showResult();
                } else {
                    showSelectedCourse();
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
	private void fillList() {
        popList.getItems().clear();
        if(GeneticAlgorithm.bestSolution!=null && GeneticAlgorithm.bestSolution.getFitness()!=pop.getFittest().getFitness()){
        	TimeTable tt = pop.getTimeTable(0);
        	pop.saveTimeTable(GeneticAlgorithm.bestSolution, 0);
        	pop.saveTimeTable(tt, pop.getPopulationSize()-1);
        	Logger.LOGI("Best solution of population is different best solution of mutation operation.");
        }
        Arrays.sort(pop.getTimeTable());
        
        for (int i = 0, len = pop.getPopulationSize(); i < len; i++) {
            popList.getItems().add((i + 1) + ". Solution(fitness:"
                    + pop.getTimeTable(i).getFitness() + ")");
        }
        popList.getSelectionModel().select(0);
        info = pop.getTimeTable(0).getTimeTable();
    }

    @FXML
    private void printDoc() throws IOException {
        printFile = new File(R.path + File.separator + "result.docx");
        String name;
        if (option.getSelectionModel().getSelectedItem() == null && !multiple) {

            ShowMessage.showInformation("Please choose " + listOption.getSelectedToggle().getUserData()
                    + " from combobox");
            return;
        } else if (!multiple) {
            name = (String) option.getSelectionModel().getSelectedItem();
        } else {
            name = "Time Table";
        }
        createDocTable(name);
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.PRINT)) {
                desktop.print(printFile);
                ShowMessage.showInformation("Operation is succesfully");
            }
        }
    }

    @FXML
    private void printPdf() throws IOException {
        if (createPdfTable() && Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.PRINT)) {
                desktop.print(printFile);
                ShowMessage.showInformation("Operation is succesfully");
            }
        }
    }

    private void clearTable() {
        for (Node node : timeGrid.getChildren()) {
            if (node instanceof VBox) {
                int row = GridPane.getRowIndex(node);
                int col = GridPane.getColumnIndex(node);
                if (row != 0 && col != 0) {
                    ((VBox) node).getChildren().clear();
                } else {
                }
            }
        }
    }

    void setRes(Stage stage1) {
        stage = stage1;
    }

    private void showResult() {
        if (!multiple) {
            clearTable();
        }
        for (int day = 1; day <= R.MAX_DAY; day++) {
            for (int slot = 1; slot <= R.MAX_SLOT; slot++) {
                for (int roomIn = 0, len = rooms.size(); roomIn < len; roomIn++) {
                    if (info[day][slot][roomIn] != null) {
                        final StringBuilder sb = new StringBuilder();
                        Info in = null;
                        switch ((String) listOption.getSelectedToggle().getUserData()) {
                            case "teacher":
                                if (info[day][slot][roomIn].getTeacherName() != null
                                        && info[day][slot][roomIn].getTeacherName().equals(selected)) {
                                    in = info[day][slot][roomIn];
                                    sb.append(in.getCourseName()).append("(")
                                            .append(in.getGroup()).append(") ")
                                            .append(in.getRoom());
                                }
                                break;
                            case "course":
                                if (info[day][slot][roomIn].getCourseName().equals(selected)) {
                                    in = info[day][slot][roomIn];
                                    if(multiple){
                                    	in = info[day][slot][roomIn];

                                        sb.append(in.getCourseName()).append("(")
                                                .append(in.getGroup()).append(") ").append(in.getRoom());

                                    	
                                    }else{
	                                    String name = in.getTeacherName();
	                                    sb.append(name == null ? "LAB" : name).append("(")
	                                            .append(in.getGroup()).append(") ").append(in.getRoom());
                                    }
                                }
                                break;
                            case "room":
                                if (info[day][slot][roomIn].getRoom().equals(selected)) {
                                    in = info[day][slot][roomIn];
                                    String name = in.getTeacherName();
                                    sb.append(name == null ? "LAB" : name).append("\n")
                                            .append(in.getCourseName()).append("(").append(in.getGroup()).append(")");
                                }
                                break;
                            default:
                                if (info[day][slot][roomIn].getCourseName().equals(selected)) {
                                    in = info[day][slot][roomIn];

                                    sb.append(in.getCourseName()).append("(")
                                            .append(in.getGroup()).append(") ").append(in.getRoom());
                                }
                        }

                        if (in != null) {
                            Label text = createLabel(sb.toString(), in, day, slot);
                            VBox vbox = getVBox(day, slot);
                            vbox.setAlignment(Pos.TOP_LEFT);
                            vbox.getChildren().add(text);
                            VBox.setMargin(text, new Insets(2, 5, 0, 5));
                        }
                    }
                }
            }
        }
    }

    private void createTimeTable() {
        timeGrid = new GridPane();
        timeGrid.setGridLinesVisible(true);

        ScrollPane sp = new ScrollPane();
        //BorderPane.setMargin(sp, new Insets(0.0, 0.0, 0.0, 50.0));
        sp.setContent(timeGrid);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sp.setFitToHeight(true);
        sp.setFitToWidth(true);
        sp.vvalueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
            timeGrid.setLayoutY(-new_val.doubleValue());
        });
        timeGrid.add(option, 0, 0);
        GridPane.setMargin(option, new Insets(5, 5, 5, 5));
        GridPane.setHalignment(option, HPos.CENTER);
        borderPane.setCenter(sp);

        for (int i = 1; i <= R.MAX_DAY; i++) {
            Text text = new Text(R.WEEK_DAY[i]);
            text.setFont(Font.font("Verdena", FontWeight.BOLD, 14));
            timeGrid.add(text, i, 0);
            GridPane.setHalignment(text, HPos.CENTER);
            GridPane.setVgrow(text, Priority.NEVER);
        }

        for (int i = 1; i <= R.MAX_SLOT; i++) {
            Text text = new Text(R.DAY_SLOT[i]);
            text.setFont(Font.font("Verdena", FontWeight.BOLD, 14));
            timeGrid.add(text, 0, i);
            GridPane.setHalignment(text, HPos.CENTER);
        }

        for (int i = 1; i <= R.MAX_DAY; i++) {
            for (int j = 1; j <= R.MAX_SLOT; j++) {
                VBox vbox = createVBox();
                timeGrid.add(vbox, i, j);
                GridPane.setHgrow(vbox, Priority.ALWAYS);
                GridPane.setVgrow(vbox, Priority.ALWAYS);
                GridPane.setHalignment(vbox, HPos.CENTER);
            }
        }
    }

    private VBox createVBox() {
        VBox vbox = new VBox();
        vbox.setSpacing(3);

        vbox.setOnDragOver((DragEvent event) -> {
            if (event.getGestureSource() != vbox
                    && event.getDragboard().hasString()) {
                /* allow for both moving, whatever user chooses */
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        vbox.setOnDragEntered((DragEvent event) -> {
            if (event.getGestureSource() != vbox
                    && event.getDragboard().hasString()) {
                vbox.setStyle("-fx-border-color:red;");
            }

            event.consume();
        });

        vbox.setOnDragExited((DragEvent event) -> {
            vbox.setStyle("-fx-border-color:grey;");

            event.consume();
        });

        vbox.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                int newDay = GridPane.getColumnIndex(vbox);
                int newSlot = GridPane.getRowIndex(vbox);
                Info temp = (Info) db.getContent(customFormat);
                Object obj = pop.getTimeTable(popList.getSelectionModel().getSelectedIndex());
                TimeTable timeTable = (TimeTable) obj;
                if (isChangeAble(newDay, newSlot, temp, timeTable)) {
                    fillList();
                    Label newTxt = createLabel(db.getString(), 
                            timeTable.getInfo(newDay, newSlot, rooms.indexOf(temp.getRoom())), newDay, newSlot);
                    vbox.getChildren().add(newTxt);
                    popList.getSelectionModel().select(obj);
                    success = true;
                } else {
                    success = false;
                }
            }
            /* let the source know whether the string was successfully
             * transferred and used */
            event.setDropCompleted(success);

            event.consume();
        });
        return vbox;
    }

    private VBox getVBox(int day, int slot) {
        for (Node node : timeGrid.getChildren()) {
            if (node instanceof VBox) {
                int x = GridPane.getRowIndex(node);
                int y = GridPane.getColumnIndex(node);
                if (y == day && x == slot) {
                    return (VBox) node;
                }
            }
        }
        return null;
    }

    @FXML
    private void save() throws IOException {
        chooser.setTitle("Save Solutions");
        chooser.getExtensionFilters().clear();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Solution file", "*.solution"));
        File save = chooser.showSaveDialog(stage);
        if (save != null) {
            if (!save.getName().contains(".solution")) {
                save = new File(save.getPath() + ".solution");
                if (!save.exists()) {
                    save.createNewFile();
                }
            }
            try (FileOutputStream fos = new FileOutputStream(save);
                    ObjectOutputStream out = new ObjectOutputStream(fos)) {
                List<String> tempTec = new ArrayList<>();
                tempTec.addAll(teachers);
                List<String> tempCor = new ArrayList<>();
                tempCor.addAll(courses);
                List<String> tempRo = new ArrayList<>();
                tempRo.addAll(rooms);
                Object[] obj = new Object[]{pop, tempTec, tempCor, tempRo};
                out.writeObject(obj);
            }
            ShowMessage.showInformation("All solutions are saved");
        }
    }

    @FXML
    private void open() {
        chooser.setTitle("Open Solutions");
        chooser.getExtensionFilters().clear();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Solution file", "*.solution"));
        File open = chooser.showOpenDialog(stage);
        if (open != null) {

            try (FileInputStream fin = new FileInputStream(open);
                    ObjectInputStream in = new ObjectInputStream(fin)) {
                Object[] obj = (Object[]) in.readObject();
                pop = (Population) obj[0];

                List<String> tempTec = (List<String>) obj[1];
                teachers.addAll(tempTec);

                List<String> tempCor = (List<String>) obj[2];
                courses.addAll(tempCor);

                List<String> tempRo = (List<String>) obj[3];
                rooms.addAll(tempRo);
                R.ALLR = rooms;
                option.getItems().clear();

                option.getItems().addAll(teachers);
                teacher.setSelected(true);
                fillList();
            } catch (FileNotFoundException ex) {
                Logger.LOGS(ex.getMessage());
                ShowMessage.showError(ex);
            } catch (IOException | ClassNotFoundException ex) {
                Logger.LOGS(ex.getMessage());
                ShowMessage.showError(ex);
            }
        }
    }

    private void createDocTable(String title) {
        XWPFDocument document = new XWPFDocument();
        XWPFParagraph paragraphOne = document.createParagraph();
        paragraphOne.setAlignment(ParagraphAlignment.CENTER);
        paragraphOne.setVerticalAlignment(TextAlignment.CENTER);
        paragraphOne.setBorderBottom(Borders.SINGLE);
        paragraphOne.setBorderTop(Borders.SINGLE);
        paragraphOne.setBorderRight(Borders.SINGLE);
        paragraphOne.setBorderLeft(Borders.SINGLE);
        paragraphOne.setBorderBetween(Borders.SINGLE);
        XWPFRun paragraphOneRunOne = paragraphOne.createRun();
        paragraphOneRunOne.setBold(true);
        paragraphOneRunOne.setText(title);
        paragraphOneRunOne.addBreak();
        // Create new table
        XWPFTable table = document.createTable(R.MAX_DAY + 1, R.MAX_SLOT + 1);

        for (int i = 0; i <= R.MAX_DAY; i++) {
            if (i == 0) {
                table.getRow(0).getCell(i).setText("Period");
            } else {
                table.getRow(0).getCell(i).setText(R.WEEK_DAY[i]);
            }
        }

        for (int j = 1; j <= R.MAX_SLOT; j++) {
            table.getRow(j).getCell(0).setText(R.DAY_SLOT[j]);
            for (int i = 1; i <= R.MAX_DAY; i++) {
                VBox vb = getVBox(i, j);
                final StringBuilder sb = new StringBuilder();
                for (Node n : vb.getChildren()) {
                    if (n instanceof Label) {
                        sb.append(((Label) n).getText()).append("\n");
                    }
                }
                if (sb.length() != 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                table.getRow(j).getCell(i).setText(sb.toString());
            }
        }

        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(printFile);
        } catch (FileNotFoundException ex) {
            Logger.LOGS(ex.getMessage());
            ShowMessage.showError(ex);
        }

        try {
            document.write(outStream);
            if (outStream != null) {
                outStream.close();
            }
        } catch (FileNotFoundException ex) {
            Logger.LOGS(ex.getMessage());
            ShowMessage.showError(ex);
        } catch (IOException ex) {
            Logger.LOGS(ex.getMessage());
            ShowMessage.showError(ex);
        }
    }

    private boolean createPdfTable() {
        try {
            printFile = new File(R.path + File.separator + "result.pdf");
            Document document = new Document();

            PdfWriter writer
                    = PdfWriter.getInstance(document, new FileOutputStream(printFile));

            document.open();
            document.newPage();
            String name;
            if (option.getSelectionModel().getSelectedItem() == null && !multiple) {

                ShowMessage.showWarning("Please choose " + listOption.getSelectedToggle().getUserData()
                        + " from combobox");
                return false;
            } else if (!multiple) {
                name = (String) option.getSelectionModel().getSelectedItem();
            } else {
                name = "Time Table";
            }

            PdfPTable table = getTable(name);
            document.add(table);
            document.close();

            return true;
        } catch (DocumentException | FileNotFoundException ex) {
            Logger.LOGS(ex.getMessage());
            ShowMessage.showError(ex);
        }
        return false;
    }

    private PdfPTable getTable(String title) {
        PdfPTable table = new PdfPTable(R.MAX_DAY + 1);
        com.itextpdf.text.Font titleFont = FontFactory.getFont("Verdena", 16, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font colNameFont = FontFactory.getFont("Verdena", 14, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font normalFont = FontFactory.getFont("Verdena", 8);
        table.getDefaultCell().setBackgroundColor(BaseColor.WHITE);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell(new Paragraph(title, titleFont));
        cell.setColspan(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);

        table.addCell(new Paragraph("Period", colNameFont));

        for (int i = 1; i <= R.MAX_DAY; i++) {
            table.addCell(new Paragraph(R.WEEK_DAY[i], colNameFont));
        }

        for (int j = 1; j <= R.MAX_SLOT; j++) {
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(new Paragraph(R.DAY_SLOT[j], colNameFont));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_TOP);
            for (int i = 1; i <= R.MAX_DAY; i++) {
                VBox vb = getVBox(i, j);
                final StringBuilder sb = new StringBuilder();
                for (Node n : vb.getChildren()) {
                    if (n instanceof Label) {
                        sb.append(((Label) n).getText()).append("\n");
                    }
                }
                if (sb.length() != 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                table.addCell(new Paragraph(sb.toString(), normalFont));
            }
        }
        return table;
    }

    private void openCourseWindow() {

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
                if (!courses.contains(s)) {
                    continue;
                }
                CheckBoxTreeItem<String> descent = new CheckBoxTreeItem<>(s);
                if (list1.contains(s)) {
                    descent.setSelected(true);
                } else {
                    descent.setSelected(false);
                }
                child.getChildren().add(descent);
                descent.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    if (newValue) {
                        if (courses.contains(descent.getValue())) {
                            list1.add(descent.getValue());
                        }
                    } else {
                        if (courses.contains(descent.getValue())) {
                            list1.remove(descent.getValue());
                        }
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
        childStage.setTitle("Choose courses");
        childStage.setScene(new Scene(root, 300, 450));
        childStage.initOwner(stage);
        childStage.setResizable(false);
        childStage.initModality(Modality.WINDOW_MODAL);
        childStage.showAndWait();
        showSelectedCourse();
    }

    private void showSelectedCourse() {
        clearTable();
        for (String cor : list1) {
            selected = cor;
            showResult();
        }
    }

    @FXML
    private void showAll() {
        if (courses.isEmpty()) {
            ShowMessage.showWarning("Please open any solution file then try again.");
            return;
        }
        multiple = true;
        option.setDisable(multiple);
        openCourseWindow();
        showSelectedCourse();
    }

    private Label createLabel(final String text, final Info info,
            final int day, final int slot) {

        Label label = new Label();
        label.setText(text);

        label.setOnDragDetected((MouseEvent event) -> {
            Dragboard db = label.startDragAndDrop(TransferMode.MOVE);

            /* Put a string on a dragboard */
            ClipboardContent content = new ClipboardContent();
            content.putString(label.getText());
            content.put(customFormat, info);
            db.setContent(content);
            event.consume();
        });

        final int d = day;
        final int s = slot;
        label.setOnDragDone((DragEvent event) -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                boolean status = getVBox(d, s).getChildren().remove(text);
                Logger.LOGI("Drag drop successfull status:"+status);
            }
            event.consume();
        });

        Tooltip toast = new Tooltip(info.toString());
        toast.setId("ttip");
        label.setTooltip(toast);
        if (info.isDefect()) {
            label.setTextFill(Color.RED);
        } else if (info.isGood()) {
            label.setTextFill(Color.BLUE);
        } else if (info.isStrong()) {
            label.setTextFill(Color.GREEN);
        }
        return label;
    }

    /**
     * The custom format
     */
    private static final DataFormat customFormat
            = new DataFormat("timetable.info");

    private boolean isChangeAble(int newDay, int newSlot, Info temp, TimeTable timeTable) {
        

        if (!timeTable.isRoomFree(newDay, newSlot, rooms.indexOf(temp.getRoom()))) {
            ShowMessage.showWarning("Room "+temp.getRoom()+" is not free at " + R.DAY_SLOT[newSlot]
                    + " on " + R.WEEK_DAY[newDay]);
            return false;
        }

        Info info1 = timeTable.getInfo(temp.getDay(), temp.getSlot(), rooms.indexOf(temp.getRoom()));
        Info info2 = timeTable.getInfo(newDay, newSlot, rooms.indexOf(temp.getRoom()));
        if (info1 == null && info2 != null) {
            info1 = info2;
            info2 = null;
        }
        if (info1 != null && info2 == null) {

            Teacher t = timeTable.getTeacher(info1.getTeacherName());
            Course c = timeTable.getCourse(info1.getCourseName(), info1.getGroup());
            int hour = -1;
            for(int i=0;t!=null && i<t.getTeacherMaxHour();i++){
            	if(t.isTeacherFree(newDay, i)){
            		hour = i;
            		break;
            	}
            }

            if(temp.getDay()==newDay){
            	hour = info1.getHour();
            }
            	
            if(t!=null  && hour==-1){
            	ShowMessage.showWarning(t.getTeacherName()+" is not free on "+R.WEEK_DAY[newDay]);
            	return false;
            }
            else if ((t != null && t.changeTeacherHour(temp.getDay(), temp.getSlot(),temp.getHour(),
                    newDay, newSlot, hour)) || (t == null)) {
                info1.setDay(newDay);
                info1.setSlot(newSlot);
                info1.setHour(hour);
                c.changeCourseTime(newDay, newSlot, info1.getSection());
                timeTable.setInfo(newDay, newSlot, rooms.indexOf(temp.getRoom()), info1);
                timeTable.setInfo(temp.getDay(), temp.getSlot(), rooms.indexOf(temp.getRoom()), null);
                timeTable.setFitness(0);
                timeTable.setFitness(timeTable.getFitness());
            } else {
                if(t!=null)
                	ShowMessage.showWarning(t.getTeacherName() + " is not free at "
                        + R.DAY_SLOT[newSlot] + " on " + R.WEEK_DAY[newDay]);
                else
                	ShowMessage.showWarning("This changes violated many hard constraints so operation canceled.");
               
                return false;
            }
            return true;
        }else{
            ShowMessage.showWarning("This changes violated many hard constraints so operation canceled.");
            return false;
        }
    }
}
