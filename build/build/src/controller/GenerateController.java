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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import util.Logger;
import util.R;
import util.ShowMessage;

/**
 * FXML Controller class control the generate fxml
 *
 * @author Sakit
 */
public class GenerateController implements Initializable {

    private Population pop;
    private Stage stage;
    public static int populationSize = 10;
    public static int generationSize = 10;
    private static double done = 0.0;
    private static int total;
    private int best;
    private static final long secondsInMilli = 1000;
    private static final long minutesInMilli = secondsInMilli * 60;
    private static final long hoursInMilli = minutesInMilli * 60;
    private static long startTime;
    public static boolean init = false;

    @FXML
    private Text operation;
    @FXML
    private Text ellapse;
    @FXML
    private Text result;
    @FXML
    private Text load;
    @FXML
    private ProgressBar progress;
    @FXML
    private Button start;
    @FXML
    private Button cancel;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        done = 0.0;
        total = generationSize;
        cancel.setDisable(true);
    }

    @FXML
    private void startAlgorithm() {
        cancel.setDisable(false);
        if (start.getText().equals("Start")) {
            start.setText("Close");
            start.setDisable(true);
            startTime = System.currentTimeMillis();
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
            R.initialize();
            pop = new Population(populationSize, true);
            operation.setText("Operation:\tInitialize population finished.");
            result.setText("Best Solution:\t"+pop.getFittest().getFitness());
            progress.setProgress(done/total);
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            operation.textProperty().bind(task.titleProperty());
            result.textProperty().bind(task.messageProperty());
            progress.progressProperty().bind(task.progressProperty());
            thread.start();
        } else {
            task.cancel(true);
            stage.close();
        }
    }

    @FXML
    private void cancel() {
        task.cancel(true);
        stage.close();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    final Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {
            long different = System.currentTimeMillis() - startTime;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;
            final String time = String.format("%02d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
            ellapse.setText(time);
        }
    }));

    private void changeColor() {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                if (best >= 100000) {
                    result.setFill(Color.RED);
                } else if (best != 0) {
                    result.setFill(Color.BLUE);
                } else {
                    result.setFill(Color.GREEN);
                }

            }
        });
    }

    Task<Void> task = new Task<Void>() {

        @Override
        protected Void call() {
            try {
                updateTitle("Operation: Start to initialize "+populationSize+" population.");
                best = pop.getFittest().getFitness();
                changeColor();
                updateMessage("Best Solution:\t" + best / 100000 + " hard constraints violated");
                updateResult(done);
                for (int i = 1; i <= generationSize; i++) {
                    if (isCancelled()) {
                        break;
                    }

                    pop = GeneticAlgorithm.evolvePopulation(pop);
                    best = GeneticAlgorithm.bestSolution.getFitness();
                    updateTitle("Operation:\t"+i + ". population evaluated");

                    if (best >= 100000) {
                        updateMessage("Best Solution:\t" + best / 100000 + " hard constraints violated.");
                    } else if (best != 0) {
                        updateMessage("Best Solution:\tOnly soft constraint violated");
                    } else {
                        updateMessage("Best Solution:\tSuccessfull.");
                    }
                    updateProgress(++done, total);
                    updateResult(done);
                    changeColor();
                }
            } catch (Exception interrupted) {
                if (isCancelled()) {
                    updateMessage("Cancelled");
                    Logger.LOGW("Thread operation is cancelled.");
                    ShowMessage.showError(interrupted);
                    return null;
                }
                ShowMessage.showError(interrupted);
            }
            return null;
        }

        @Override
        protected void succeeded() {
            ObjectOutputStream oos = null;
            try {
                timeline.stop();
                updateTitle("Time Table Created");
                if (best >= 100000) {
                    updateMessage("Best Solution:\t" + best / 100000 + " hard constraints violated.");
                } else if (best != 0) {
                    updateMessage("Best Solution:\tOnly soft constraint violated");
                } else {
                    updateMessage("Best Solution:\tSuccessfull.");
                }   changeColor();
            load.setText("Generate Time Table:  Finish");
                start.setDisable(false);
                oos = new ObjectOutputStream(
                        new FileOutputStream(R.path+File.separator+"term.solution"));
                oos.writeObject(pop);
                init = true;
                Action act = ShowMessage.questionConfirm("Do you want to see the solution?");
                if(act==Dialog.Actions.YES)
                    MainpageController.SOLUTION = true;
                stage.close();
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(GenerateController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if(oos!=null)
                        oos.close();
                } catch (IOException ex) {
                    Logger.LOGS(ex.getMessage());
                    ShowMessage.showError(ex);
                }
            }
        }
    };

    private void updateResult(final double done) {
        Platform.runLater(() -> {
            load.setText("Generate Time Table:\t" + (int) (100 * done / total) + "% complete");
        });
    }
}
