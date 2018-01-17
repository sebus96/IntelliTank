package model;

/**
 * Interface für die Abfolge mehrerer IPredictionStations
 *
 * @author Sebastian Drath
 *
 */
public interface IPredictionStationList {
	
	/**
	 * Gibt den Namen zurück.
	 *
	 * @return Name
	 */
	public String getName();

	/**
	 * Gibt die Länge zurück.
	 *
	 * @return Länge
	 */
    public int getLength();

    /**
     * Gibt das Element an der übergebenen Position zurück.
     *
     * @param i Index des Elements
     * @return das Element
     */
    public IPredictionStation get(int i);
    
    /**
     * Gibt die Validierung für alle enthaltenen Tankstellen zurück.
     *
     * @return Validierung
     */
    public Validation getValidation();
    
    /**
     * Überprüft, ob mindestens ein Element eine Vorhersage hat.
     *
     * @return true, wenn mindestens eine Vorhersage existiert, false ansonsten
     */
    public boolean hasPredictions();
	
    /**
     * Gibt den Typ zurück
     *
     * @return Typ
     */
	public String getType();
}
