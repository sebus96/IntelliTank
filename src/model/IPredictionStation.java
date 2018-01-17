package model;

import java.util.Date;

import controller.PredictionUnit;

/**
 * Interface für eine Tankstelle mit Zeitpunkt für Benzinpreisvorhersage.
 *
 * @author Sebastian Drath
 *
 */
public interface IPredictionStation {
	
	/**
	 * Gibt die Tankstelle zurück.
	 *
	 * @return Tankstelle
	 */
	public GasStation getStation();
	
	/**
	 * Gibt den Zeitpunkt für die Vorhersage zurück.
	 *
	 * @return Zeitpunkt
	 */
	public Date getTime();
    
	/**
	 * Gibt den vorhergesagten Preis für den Zeitpunkt zurück.
	 *
	 * Siehe getPredictedPrice(Date d).
	 *
	 * @return vorhergesagter Preis oder -1, wenn kein Preis gefunden wurde
	 */
    public int getPredictedPrice();
	
	/**
	 * Gibt den vorhergesagten Preis für einen beliebigen anderen Zeitpunkt zurück.
	 *
	 * Wenn sich der Zeitpunkt außerhalb des Vorhersagezeitraums von 5 Wochen befindet oder für dieses Objekt
	 * keine Vorhersage getätigt werden konnte, weil keine historischen Preis verfügbar sind, wird -1 zurückgegeben.
	 *
	 * @param d angefordertes Datum
	 * @return vorhergesagter Preis oder -1, wenn kein Preis gefunden wurde
	 */
	public int getPredictedPrice(Date d);
    
	/**
	 * Gibt die Länge der vorhergesagten Preisliste zurück
	 *
	 * @return Länge der vorhergesagten Preisliste
	 */
    public int getPredictedPriceListSize();
    
    /**
     * Gibt den Preislisteneintrag an der übergebenen Position zurück.
     *
     * @param index Index des Preislisteneintrags
     * @return Gibt den Preis zurück, oder null wenn dieses Element nicht existiert
     */
    public Price getPredictedPriceListElement(int index);
    
    /**
     * Setzt die Vorhersage für dieses Objekt. Dabei wird die Liste der vorhergesagten Preise für 5 Wochen gefüllt.
     *
     * @param pu Vorhersageeinheit für dieses Objekt
     */
    public void setPrediction(PredictionUnit pu);
	
    /**
     * Gibt zurück, ob für das Objekt eine Vorhersage vorliegt
     *
     * @return true, wenn vorhergesagt wurde, false, wenn nicht
     */
	public boolean isPredicted();
	
	/**
	 * Gibt den Text zurück der für dieses Objekt in die CSV exportiert wird.
	 *
	 * @return CSV-String für dieses Objekt
	 */
	public String toCSVString();
	
	/**
	 * Setzt eine Validierung für dieses Objekt.
	 *
	 * @param v die Validierung für dieses Objekt
	 */
	public void setValidation(Validation v);
	
	/**
	 * Gibt die gesetzte Validierung zurück.
	 *
	 * @return die Validierung oder null, wenn keine gesetzt wurde
	 */
	public Validation getValidation();
}
