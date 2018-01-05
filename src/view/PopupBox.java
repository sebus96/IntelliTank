/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 *
 * @author Admin
 */
public class PopupBox {

    private static Alert infoAlert = new Alert(AlertType.INFORMATION);
    private static Alert warnAlert = new Alert(AlertType.WARNING);
    private static Alert errorAlert = new Alert(AlertType.ERROR);

    public static void displayMessage(String content) {

        if (!infoAlert.isShowing()) {
            infoAlert.setTitle("Information");
            infoAlert.setHeaderText(null);
            infoAlert.setContentText(content);
            infoAlert.showAndWait();
        }
    }

    public static void displayWarning(String content) {

        if (!warnAlert.isShowing() && content.equals(warnAlert.getContentText()) ) {
            warnAlert.setTitle("Warnung");
            warnAlert.setHeaderText(null);
            warnAlert.setContentText(content);
            warnAlert.showAndWait();
        }
    }

    public static void displayError(String content) {

        if (!errorAlert.isShowing() && content.equals(errorAlert.getContentText())) {
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText(content);
            errorAlert.showAndWait();
        }
    }

}
