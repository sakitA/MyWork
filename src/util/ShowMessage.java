/**
 * Department of Computer Engineering, Eastern Mediterranean University.
 * @link http://cmpe.emu.edu.tr
 * 
 * @author Sakit Atakishiyev, Std. ID: 117319
 * @version 1.0.0.1
 */
package util;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

/**
 *  This class shows message dialog to inform user.
 * @author Sakit
 */
public class ShowMessage{
    
    /**
     * shows <b>WARNING</b> level message
     * @param message 
     */
    public static void showWarning(final String message){
        Dialogs.create()
                .title("Warning")
                .message(message)
                .showWarning();    
    }
    
    /**
     * shows <b>SEVERE</b> level message
     * @param message 
     */
    public static void showError(Throwable message){
        Dialogs.create()
                .title("Error")
                .message(message.getMessage())
                .showException(message);
    }
    
    /**
     * shows <b>INFORMATION</b> level message
     * @param message 
     */
    public static void showInformation(final String message){
        Dialogs.create()
                .title("Information")
                .message(message)
                .showInformation();
    }    
    
    public static Action questionConfirm(String message){
        return Dialogs.create()
                .title("Question")
                .message(message)
                .actions(Dialog.Actions.YES, Dialog.Actions.NO)
                .showConfirm();
    }
}
