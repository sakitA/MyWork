/**
 * Department of Computer Engineering, Eastern Mediterranean University.
 * @link http://cmpe.emu.edu.tr
 *
 * @author Sakit Atakishiyev, Std. ID: 117319
 * @version 1.0.0.1
 */
package util;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialogs;

/**
 * This class is for logging information each operation.
 *
 * @author Sakit
 */
public class Logger {

    
    public static boolean LOGGER = false;
    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(Logger.class.getName());
    private static FileHandler FILE_HANDLER;
    private static Stage stage;
    private static final ConsoleHandler consoleHandler = new ConsoleHandler();

    /**
     * set log file to the given file
     *
     * @param fileName
     */
    public static void setFileHandler(final String fileName) {
        try {
            FILE_HANDLER = new FileHandler(fileName);
            FILE_HANDLER.setFormatter(new SimpleFormatter());
            LOG.addHandler(FILE_HANDLER);
            LOG.removeHandler(consoleHandler);
        } catch (IOException | SecurityException ex) {
            if (ex instanceof NoSuchFileException) {
                File file = new File(fileName);
                if (!file.exists() && file.getParentFile().mkdir()) {
                    try {
                        file.createNewFile();
                        LOGI("destionation is not exsist. created new one");
                    } catch (IOException ex1) {
                        Dialogs.create()
                                .owner(stage)
                                .title("Error")
                                .message("It cannot be created logger file. To solve\n"
                                        + "the problem please create \"timetable\" folder\n"
                                        + "under the user home directory.")
                                .showError();
                    }
                }
            }
        }
    }

    /**
     * set log file to console
     */
    public static void setConsoleHandler() {
        LOG.addHandler(new ConsoleHandler());
        LOG.removeHandler(FILE_HANDLER);
    }

    /**
     * log message at <b>INFO</b> level
     *
     * @param message
     */
    public static void LOGI(final String message) {
        if (!LOGGER) {
            return;
        }
        LOG.setLevel(Level.INFO);
        LOG.info(message);
    }

    /**
     * log message at <b>SEVERE</b> level
     *
     * @param message
     */
    public static void LOGS(final String message) {
        if (!LOGGER) {
            return;
        }
        LOG.setLevel(Level.SEVERE);
        LOG.severe(message);
    }

    /**
     * log message at <b>WARNING</b> level
     *
     * @param message
     */
    public static void LOGW(final String message) {
        if (!LOGGER) {
            return;
        }
        LOG.setLevel(Level.WARNING);
        LOG.warning(message);
    }

    public static void setStage(Stage stage) {
        Logger.stage = stage;
    }   
}
