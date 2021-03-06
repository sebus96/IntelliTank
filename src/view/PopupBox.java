package view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.CSVManager;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Validateable;
import model.Validation;

/**
 * Diese Klasse dient für die Erzeugung der einzelnen Meldungen des Programms.
 * @author Axel Claassen, Burak Kadioglu, Sebastian Drath
 */
public class PopupBox {

	private static Window owner;
	private static Alert infoAlert = new Alert(AlertType.INFORMATION);
	private static Alert warnAlert = new Alert(AlertType.WARNING);
	private static Alert errorAlert = new Alert(AlertType.ERROR);

	private static Map<Integer, String> messages = new HashMap<Integer, String>() {
		private static final long serialVersionUID = -6975514177601226819L;

		{
			put(0, "[ Die angezuzeigende Nachricht konnte nicht gefunden werden! ]");
			// 1XX = Informationen
			put(101, "Dieses Projekt wurde von Axel Claassen, Burak Kadioglu und Sebastian Drath entwickelt.");
			put(102, "Hier wird angezeigt, wie gut die Route vorhergesagt wurde. Methodenaufruf in view/MainView setUpValidateButton()");
			// 2XX = Warnungen
			put(201, "Fehler beim Lesen der Datei Tankstellen.csv. Möglicherweise werden Daten fehlerhaft dargestellt.");
			put(202, "Für die ausgewählte Tankstelle konnte keine Vorhersage durchgeführt werden");
			put(203, "Für eine oder mehrere der ausgewählten Tankstellen konnte keine Vorhersage durchgeführt werden");
			put(204, "Die Datei Postleitzahl2Bundesland.csv wurde nicht gefunden!\n\nDen Tankstellen kann kein Bundesland zugeordnet werden. Feriendaten können bei der Vorhersage nicht benutzt werden.");
			put(205, "Falsches Format der Datei Postleitzahl2Bundesland.csv. Möglicherweise werden Daten fehlerhaft dargestellt.");
			put(206, "Feriendaten wurden nicht gefunden und können bei der Vorhersage nicht benutzt werden.");
			put(207, "Die Dateinamen der Feriendaten entsprechen nicht dem richtigen Format. Möglicherweise werden Daten fehlerhaft dargestellt.");
			put(208, "Falsches Format der Feriendaten. Möglicherweise werden Daten fehlerhaft dargestellt.");
			// 3XX = Errors
			put(301, "Die Datei Tankstellen.csv wurde nicht gefunden!\n\nDas Programm konnte nicht gestartet werden.");
			put(302, "Die ausgewählte Route konnte nicht geladen werden. Datei möglicherweise fehlerhaft formatiert oder nicht mehr vorhanden.");
			put(303, "Vorhersagezeitpunkte konnten nicht geladen werden. Datei möglicherweise fehlerhaft formatiert oder nicht mehr vorhanden.");
//			put(304, "Fehler in der Routenstrategie: Möglicherweise ist die Tankkapazität zu klein gewählt.");
			put(305, "Die historischen Benzinpreise wurden nicht gefunden. Es kann keine Vorhersage getätigt werden.");
			put(306, "Die Preise konnten für keinen Tankstop innerhalb der Route vorhergesagt werden. Möglicherweise konnten keine Preise importiert werden, die historischen Daten liegen zu weit zurück oder die verfügbaren historischen Preise umfassen weniger als 2 Wochen.");
			put(307, "Die Entfernung der Tankstops in der importierten Route ist zu groß für die gewählte Tankkapazität.");

		};
	};
	
	/**
	 * Initialisiert die Alerts so, dass sie immer relativ zum Hauptfenster angezeigt werden.
	 *
	 * @param window Hauptfenster
	 */
	public static void initOwner(Window window) {
		infoAlert.initOwner(window);
		warnAlert.initOwner(window);
		errorAlert.initOwner(window);
		owner = window;
	}
	
	/**
	 * Erzeugt eine Pop-Up-Meldung als Information.
	 * Mitwirkende-Pop-Up-Meldung und beliebige Informations-Meldungen werden erzeugt.
	 * @param textId Nachricht der Pop-Up-Meldung
	 */
	public static void displayMessage(int textId) {
		// Verhindert, dass die selbe Meldung durch z.B. Schleifen mehrfach übereinander
		// angezeigt wird
		if (!infoAlert.isShowing()
				|| (infoAlert.isShowing() && !messages.get(textId).equals(infoAlert.getContentText()))) {

			if (textId == 101) {
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
			if (messages.containsKey(textId))
				infoAlert.setContentText(messages.get(textId));
			else
				infoAlert.setContentText(messages.get(0));
			infoAlert.showAndWait();
		}
	}
	/**
	 * Erzeugt Pop-Up-Meldung als Warnung.
	 * @param textId Nachricht der Pop-Up-Meldung
	 */
	public static void displayWarning(int textId) {

		// Verhindert, dass die selbe Meldung durch z.B. Schleifen mehrfach übereinander
		// angezeigt wird
		if (!warnAlert.isShowing()
				|| (warnAlert.isShowing() && !messages.get(textId).equals(warnAlert.getContentText()))) {
			setIcon(warnAlert);

			warnAlert.setTitle("Warnung");
			warnAlert.setHeaderText(null);
			if (messages.containsKey(textId))
				warnAlert.setContentText("Warnung " + textId + ": " + messages.get(textId));
			else
				warnAlert.setContentText(messages.get(0));
			warnAlert.showAndWait();
		}
	}
	/**
	 * Erzeugt Pop-Up-Meldung als Fehler.
	 * @param textId Nachricht der Pop-Up-Meldung
	 */
	public static void displayError(int textId) {

		// Verhindert, dass die selbe Meldung durch z.B. Schleifen mehrfach übereinander
		// angezeigt wird
		if (!errorAlert.isShowing()
				|| (errorAlert.isShowing() && !messages.get(textId).equals(errorAlert.getContentText()))) {
			setIcon(errorAlert);

			errorAlert.setTitle("Error");
			errorAlert.setHeaderText(null);
			if (messages.containsKey(textId))
				errorAlert.setContentText("Error " + textId + ": " + messages.get(textId));
			else
				errorAlert.setContentText(messages.get(0));
			errorAlert.showAndWait();
		}
	}
	
	/**
	 * Zeigt die Validierung des übergebenen Objekts an (Route, Vorhersagezeitpunktliste, Tankstop oder Vorhersagezeitpunkt).
	 * @param validateable Objekt zu dem die Validierung angezeigt werden soll
	 */
	public static void displayValidation(Validateable validateable) {
		displayValidation(validateable.getType() + " " + validateable.getName(), validateable.getValidation());
	}
	
	/**
	 * Zeigt die Validierung in einem neuen Fenster mit dem übergebenen Titel an.
	 * @param title Titel der Fensters
	 * @param validation die Validierung
	 */
	private static void displayValidation(String title, Validation validation) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initOwner(owner);
		alert.setTitle("Validierung");
		alert.setHeaderText("Validierung " + title);
		Image validImg = new Image("/img/validation.png");
		ImageView validView = new ImageView(validImg);
		validView.setFitWidth(64);
		validView.setFitHeight(64);
		alert.setGraphic(validView);
		PopupBox.setIcon(alert);

		WebView wv = new WebView();
		wv.prefHeightProperty().bind(alert.heightProperty());
		wv.prefWidthProperty().bind(alert.widthProperty());
		wv.setMinWidth(450);
		wv.getEngine().loadContent("<body style=\"background:#f4f4f4;font-family:system;font-size:12;\">"
				+ validation.toHTMLString() + "</body>");
		alert.getDialogPane().setContent(wv);
		alert.show();
	}
	
	/**
	 * Erzeugt Warnungen bzgl. der aktuellen Route.
	 * @param warnings Liste der Warnungen
	 */
	public static void displayRouteWarnings(List<String> warnings) {
		if (warnings == null || warnings.isEmpty())
			return;
		Alert alert = new Alert(AlertType.WARNING);
		alert.initOwner(owner);
		alert.setTitle("Routenwarnungen");
		alert.setHeaderText(null);
		PopupBox.setIcon(alert);
		String res = "";
		for (String e : warnings) 
			res += e + "\n";
		alert.setContentText(res);
		alert.show();
	}
	/**
	 * Gibt die Fehler, die beim initialen Import aufgetreten sind, aus.
	 */
	public static void displayImportWarnings() {
		for (int f : CSVManager.getOccuredFailures()) {
			if (f < 200) { // Nachricht
				displayMessage(f);
			} else if (f < 300) { // Warnung
				displayWarning(f);
			} else if (f < 400) { // Error
				displayError(f);
			}
		}
	}
	
	/**
	 * Setzt das Icon von den einzelnen Fenster (inkl. Pop-Up-Meldungen).
	 * @param a aktuelle Pop-Up-Meldung
	 */
	private static void setIcon(Alert a) {
		if (a.getDialogPane().getScene() != null) {
			Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
			Image iconImg = new Image("/img/gas-station.png");
			stage.getIcons().add(iconImg);
		}
	}
}
