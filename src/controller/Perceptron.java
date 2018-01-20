package controller;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.FederalState;
import model.GasStation;
import model.Holidays;

/**
 * Abstrakte Klasse eines Perzeptrons zur Vorhersage von Preisdaten.
 *
 * @author Sebastian Drath
 *
 */
public abstract class Perceptron implements Serializable {
	private static final long serialVersionUID = -4217916422339681128L;
	/**
	 * Die Präzision, die erreicht werden soll. Trainings werden abgebrochen, sobald diese Präzision erreicht ist.
	 * Der Wert 10 entspricht dabei einem Cent, 100 steht für 10 Cent.
	 */
	public final double precision = 10;
	/**
	 * Gibt an, wie viele Stunden in die Vergangenheit geguckt wird für eine Vorhersage.
	 */
	public final int oldPriceNumber = 5;
	/**
	 * Gibt an, ob nächtliche Stunden zusammengefasst werden sollen. Dies wird gemacht, da nachts seltener Preisänderungen vorgenommen werden und somit geringere Unterschiede zwischen den Stunden exisiteren.
	 */
	public final boolean groupNightHours = true;
	
	private int epochs;
	private Date until;
	private GasStation station;
	private boolean trained;
	
	/**
	 * Erstellt ein neues Perzeptron für die Vorhersage der Preise für die Tankstelle.
	 * Die historischen Preise werden bis zum übergebenen Datum für die Vorhersage verwendet.
	 *
	 * @param gs die Tankstelle, auf deren Preisliste die Vorhersage beruht
	 * @param epochs die Anzahl der Epochen für die Vorhersage
	 * @param until das Datum, bis zu dem historische Preise verwendet werden dürfen
	 */
	public Perceptron(GasStation gs, int epochs, Date until) {
		this.station = gs;
		this.epochs = epochs;
		this.until = until;
	}
	
	/**
	 * Trainiert das Perzeptron mit der Preisliste der Tankstelle, damit es später Vorhersagen tätigen kann.
	 * Gibt zurück, ob die durchschnittliche Genauigkeit aus {@link controller.Perceptron#precision} erreicht werden konnte.
	 *
	 * @return true, wenn die definierte Genauigkeit erreicht wurde, false ansonsten
	 */
	public abstract boolean train();
	
	/**
	 * Gibt den vorhergesagten Preis auf Basis des Trainings für den angeforderten Tag und die übergebene Liste vorheriger Preise zurück.
	 *
	 * @param d Datum für die Vorhersage
	 * @param lastPrices Liste letzter Preise. Die Länge muss der Anzahl der für das Training verwendeten alten Preise entsprechen.
	 * @return der vorhergesagte Preis
	 */
	public abstract double feedForward(Date d, List<Double> lastPrices);
	
	/**
	 * Gibt einen Wochentag als Bitvektor zurück. Dabei ist das bit in dem 7 bit breiten Array gesetzt, das für den jeweiligen übergebenen Wochentag steht.
	 *
	 * @param day Wochentag (Zahlen von eins bis sieben)
	 * @return Array der Länge 7 für die einzelnen Tage
	 */
	protected int[] getDayVector(int day) {
		if(day > 7 || day <= 0) return null;
		int[] res = new int[7];
		res[day-1] = 1;
		return res;
	}
	
	/**
	 * Gibt eine Stunde als Bitvektor zurück. Dabei ist das bit in dem 24 bit breiten Array gesetzt, das für die jeweiligen übergebene Stunde steht.
	 * Wenn {@link controller.Perceptron#groupNightHours} gesetzt ist, werden nächtliche Stunden zusammengefasst.
	 *
	 * @param hour Stunde (Zahlen von 0 bis 23)
	 * @return Array der Länge 24 für die einzelnen Stunden
	 */
	protected int[] getHourVector(int hour) {
		if(hour >= 24 || hour < 0) return null;
		int[] res = new int[24];
		boolean group = groupNightHours;
		// nächtliche Stunden werden zusammengefasst
		if(hour > 21 && hour <= 23 && group) {
			res[22] = 1;
			res[23] = 1;
		} else if(hour >= 0 && hour < 4 && group) {
			res[0] = 1;
			res[1] = 1;
			res[2] = 1;
			res[3] = 1;
		} else if(hour >= 4 && hour < 6 && group) {
			res[4] = 1;
			res[5] = 1;
		} else { 
			res[hour] = 1;
		}
		return res;
	}
	
	/**
	 * Gibt ein Array vergangener Preise zurück. Als Ausgangszeit wird die Zeit aus dem übergebenen Kalender verwendet.
	 * Es wird so viele Stunden zurückgegangen, wie durch {@link controller.Perceptron#oldPriceNumber} angegeben ist.
	 * Der übergebene Kalender ist nach Verlassen dieser Methode eine Stunde weitergestellt als zu Beginn der Methode.
	 *
	 * @param c Kalender mit dem Ausgangsdatum
	 * @return Array der vergangenen Preise
	 */
	protected int[] getPriceVector(Calendar c) {
		int[] res = new int[oldPriceNumber];
		c.add(Calendar.HOUR_OF_DAY, -1 * oldPriceNumber);
		for(int i = 0; i < oldPriceNumber; i++) {
			res[i] = station.getHistoricPrice(c.getTime());
			if(res[i] < 0) return null; // not enough data available
			c.add(Calendar.HOUR_OF_DAY, 1);
		}
		return res;
	}
	
	/**
	 * Gibt einen Wert größer 0 zurück wenn an dem Datum und in dem Bundesland Ferien sind, ansonsten 0.
	 *
	 * @param date Datum
	 * @param state Bundesland
	 * @return Wert größer 0, wenn Ferien sind, ansonsten 0 
	 */
	protected double getHoliday(Date date, FederalState state) {
		// 0.2 hat sich in Tests als guter Wert gezeigt, da Ferien nicht so stark ins Gewicht fallen wie andere Faktoren
		return (Holidays.isHoliday(date, state)? 0.2 : 0);
	}
	
	/**
	* Gibt die Anzahl der Epochen zurück.
	* @return Epochenanzahl
	*/
	public int getEpoches() {
		return this.epochs;
	}

	/**
	 * Gibt die Tankstelle zurück
	 *
	 * @return Tankstelle
	 */
	public GasStation getStation() {
		return station;
	}
	
	/**
	 * Setzt die Tankstelle neu. Das Objekt muss allerdings mit dem vorherigen übereinstimmen, da sonst
	 * die Vorhersage nicht mehr passt. Hierbei geht es darum, dass nach einem import die Referenzen richtig gesetzt werden.
	 *
	 * @param gs die Tankstelle die gesetzt werden soll
	 * @return true, wenn die Station gesetzt werden konnte, weil sie der bisherigen entspricht, false ansonsten
	 */
	public boolean setStation(GasStation gs) {
		if(gs.equals(this.station)) {
			this.station = gs;
			return true;
		}
		return false;
	}
	
	/**
	 * Gibt das Datum zurück, bis zu dem die historischen Preisdaten zum Training verwendet werden.
	 *
	 * @return Datum bis zu dem Preise verwendet werden
	 */
	public Date getUntil() {
		return this.until;
	}
	
	/**
	 * Gibt zurück, ob das Perzeptron trainiert wurde
	 *
	 * @return true, wenn es trainiert wurde, false ansonsten
	 */
	public boolean isTrained() {
		return this.trained;
	}
	
	/**
	 * Setzt das Perzeptron als trainiert.
	 */
	protected void setTrained() {
		this.trained = true;
	}
}
