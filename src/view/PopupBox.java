/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.util.HashMap;
import java.util.Map;
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
    private static Map<Integer, String> messages = new HashMap<Integer, String>() {{
    
        //1XX = Informationen
        put(101, "Dieses Projekt wurde von Axel Claassen, Burak Kadioglu und Sebastian Drath entwickelt.");
        //2XX = Warnungen
        put(201, "Fehler beim Lesen der Datei Tankstellen.csv. Möglicherweise werden Daten fehlerhaft dargestellt.");
        //3XX = Errors
        put(301, "Die Datei Tankstellen.csv wurde nicht gefunden!\n\nDas Programm konnte nicht gestartet werden.");
        put(302, "Die ausgewählte Route konnte nicht geladen werden. Datei möglicherweise fehlerhaft oder nicht mehr vorhanden.");
        put(303, "Vorhersagezeitpunkte konnten nicht geladen werden. Datei möglicherweise fehlerhaft oder nicht mehr vorhanden.");
        put(304, "Fehler in der Routenstrategie: Möglicherweise ist die Tankkapazität zu klein gewählt.");
    };};
    
    
    
    public static void displayMessage(int textId) {
        //Verhindert, dass die selbe Meldung durch z.B. Schleifen mehrfach übereinander angezeigt wird
        if (!infoAlert.isShowing() || (infoAlert.isShowing() && !messages.get(textId).equals(infoAlert.getContentText()))) {
            
            if(textId == 101 && infoAlert.getDialogPane().getScene() != null) {
                Image teamImg = new Image("/img/team.png");
                ImageView teamView = new ImageView(teamImg);
                Stage stage = (Stage) infoAlert.getDialogPane().getScene().getWindow();
                Image iconImg = new Image("/img/gas-station.png");
                stage.getIcons().add(iconImg);
                infoAlert.setGraphic(teamView);
            }
            
            infoAlert.setTitle("Mitwirkende");
            infoAlert.setHeaderText(null);
            infoAlert.setContentText(messages.get(textId));
            infoAlert.show();
        }
    }

    public static void displayWarning(int textId) {

        //Verhindert, dass die selbe Meldung durch z.B. Schleifen mehrfach übereinander angezeigt wird
        if (!warnAlert.isShowing() || (warnAlert.isShowing() && !messages.get(textId).equals(warnAlert.getContentText()))) {
            warnAlert.setTitle("Warnung");
            warnAlert.setHeaderText(null);
            warnAlert.setContentText(messages.get(textId));
            warnAlert.show();
        }
    }

    public static void displayError(int textId) {

        //Verhindert, dass die selbe Meldung durch z.B. Schleifen mehrfach übereinander angezeigt wird
        if (!errorAlert.isShowing() || (errorAlert.isShowing() && !messages.get(textId).equals(errorAlert.getContentText()))) {
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText(messages.get(textId));
            errorAlert.show();
        }
    }

}
