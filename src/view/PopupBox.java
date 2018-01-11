/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 *
 * @author Admin
 */
public class PopupBox {

    private static Alert infoAlert = new Alert(AlertType.INFORMATION);
    private static Alert warnAlert = new Alert(AlertType.WARNING);
    private static Alert errorAlert = new Alert(AlertType.ERROR);

    public static void displayMessage(String content) {
    	Image teamImg = new Image("/img/team.png");
    	ImageView teamView = new ImageView(teamImg);
    	Stage stage = (Stage) infoAlert.getDialogPane().getScene().getWindow();
    	Image iconImg = new Image("/img/gas-station.png");
        stage.getIcons().add(iconImg);
    	
        if (!infoAlert.isShowing()) {
            infoAlert.setTitle("Mitwirkende");
            infoAlert.setHeaderText(null);
            infoAlert.setContentText(content);
            infoAlert.setGraphic(teamView);
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
