package model;

/**
 * Interface für Objekte die validiert werden können.
 * Dazu zählen Route, Vorhersagezeitpunktliste, Tankstop oder Vorhersagezeitpunkt.
 *
 * @author Sebastian Drath
 *
 */
public interface Validateable {
	
	/**
	 * Gibt den Namen des Objekts zurück.
	 *
	 * @return Name
	 */
	public String getName();
	
	/**
	 * Gibt die Validierung des Objekts zurück.
	 *
	 * @return Validierung
	 */
	public Validation getValidation();
	
	/**
	 * Gibt den Typen des Objekts zurück.
	 *
	 * @return Typ
	 */
	public String getType();
}
