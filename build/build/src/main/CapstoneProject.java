/**
 * Department of Computer Engineering, Eastern Mediterranean University.
 * @link http://cmpe.emu.edu.tr
 * 
 * @author Sakit Atakishiyev, Std. ID: 117319
 * @version 1.0.0.1
 */
package main;

import controller.MainpageController;
import java.io.File;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.Logger;
import util.R;

/**
 * This class is the main class of application.
 * Program start from here.
 * @author Sakit
 */
public class CapstoneProject extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainpage.fxml"));
        Parent root = (Parent)loader.load();
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.getIcons().add(R.emu_icon);
        stage.setResizable(false);
        stage.setTitle("CMPE Time Table");
        MainpageController controller = loader.getController();
        controller.setStage(stage);
        Logger.setFileHandler(R.path+File.separator+"logger");
        Logger.setStage(stage);
        Logger.LOGI("Started");
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Logger.LOGGER = true;
        launch(args);        
    }    
}
