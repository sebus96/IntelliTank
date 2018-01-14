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
    private static Map<Integer, String> messages = new HashMap<Integer, String>() {
		private static final long serialVersionUID = -6975514177601226819L;

		{
                    put(0, "[ Die angezuzeigende Nachricht konnte nicht gefunden werden! ]");
                    //1XX = Informationen
                    put(101, "Dieses Projekt wurde von Axel Claassen, Burak Kadioglu und Sebastian Drath entwickelt.");
                    put(102,"Hier wird angezeigt, wie gut die Route vorhergesagt wurde. Methodenaufruf in view/MainView setUpValidateButton()");
                    //2XX = Warnungen
                    put(201, "Warnung 201: Fehler beim Lesen der Datei Tankstellen.csv. Möglicherweise werden Daten fehlerhaft dargestellt.");
                    put(202, "Warnung 202: Für die ausgewählte Tankstelle konnte keine Vorhersage durchgeführt werden");
                    put(203, "Warnung 203: Für eine oder mehrere der ausgewählten Tankstellen konnte keine Vorhersage durchgeführt werden");
                    //3XX = Errors
                    put(301, "Error 301: Die Datei Tankstellen.csv wurde nicht gefunden!\n\nDas Programm konnte nicht gestartet werden.");
                    put(302, "Error 302: Die ausgewählte Route konnte nicht geladen werden. Datei möglicherweise fehlerhaft oder nicht mehr vorhanden.");
                    put(303, "Error 303: Vorhersagezeitpunkte konnten nicht geladen werden. Datei möglicherweise fehlerhaft oder nicht mehr vorhanden.");
                    put(304, "Error 304: Fehler in der Routenstrategie: Möglicherweise ist die Tankkapazität zu klein gewählt.");
    
		};
	};
    
    
    
    public static void displayMessage(int textId) {
        //Verhindert, dass die selbe Meldung durch z.B. Schleifen mehrfach übereinander angezeigt wird
        if (!infoAlert.isShowing() || (infoAlert.isShowing() && !messages.get(textId).equals(infoAlert.getContentText()))) {
            
            if(textId == 101) {
                infoAlert.setTitle("Mitwirkende");
                Image teamImg = new Image("/img/team.png");
                ImageView teamView = new ImageView(teamImg);
                infoAlert.setGraphic(teamView);
            } else {
            	infoAlert = new Alert(AlertType.INFORMATION);
            	infoAlert.setTitle("Nachricht");
            }
            setIcon(infoAlert);
            
            infoAlert.setHeaderText(null);
            if(messages.containsKey(textId))
                infoAlert.setContentText(messages.get(textId));
            else
                infoAlert.setContentText(messages.get(0));
            infoAlert.show();
        }
    }

    public static void displayWarning(int textId) {

        //Verhindert, dass die selbe Meldung durch z.B. Schleifen mehrfach übereinander angezeigt wird
        if (!warnAlert.isShowing() || (warnAlert.isShowing() && !messages.get(textId).equals(warnAlert.getContentText()))) {
        	setIcon(warnAlert);
        	
        	warnAlert.setTitle("Warnung");
            warnAlert.setHeaderText(null);
            if(messages.containsKey(textId))
                warnAlert.setContentText(messages.get(textId));
            else
                warnAlert.setContentText(messages.get(0));
            warnAlert.show();
        }
    }

    public static void displayError(int textId) {

        //Verhindert, dass die selbe Meldung durch z.B. Schleifen mehrfach übereinander angezeigt wird
        if (!errorAlert.isShowing() || (errorAlert.isShowing() && !messages.get(textId).equals(errorAlert.getContentText()))) {
        	setIcon(errorAlert);
        	
        	errorAlert.setTitle("Error");
            errorAlert.setHeaderText(null);
            if(messages.containsKey(textId))
                errorAlert.setContentText(messages.get(textId));
            else
                errorAlert.setContentText(messages.get(0));
            errorAlert.show();
        }
    }

    private static void setIcon(Alert a) {
    	if(a.getDialogPane().getScene() != null) {
            Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
            Image iconImg = new Image("/img/gas-station.png");
            stage.getIcons().add(iconImg);
        }
    }
}
