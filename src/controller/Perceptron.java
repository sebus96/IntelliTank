package controller;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.GasStation;

public abstract class Perceptron {
	private int epochs;
	protected final int precision = 13;
	protected final int oldPriceNumber = 1;
	private GasStation station;
	
	/**
	* Konstruktor zum Perzeptron mit Eingabeparameter Lernrate und Anzahl der Epochen.
	*
	* @param lernrate Lernrate
	* @param anzahlEpoche Anzahl der Epochen
	*/
	public Perceptron(GasStation gs, int epochs) {
		this.station = gs;
		this.epochs = epochs;
	}
	
	/**
	* Perzeptron wird trainiert.
	*
	* @param traindata Datensatz zum Training
	* @return true||false Je nach dem, ob 95% richtig erkannt werden, haengt der Rueckgabewert davon ab
	*/
	public abstract boolean train(Date until);
	
	public abstract double feedForward(Date d, List<Double> lastPrices);
	
	protected int[] getDayVector(int day) {
		if(day > 7 || day <= 0) return null;
		int[] res = new int[7];
		res[day-1] = 1;
		return res;
	}
	
	protected int[] getHourVector(int hour) {
		if(hour >= 24 || hour < 0) return null;
		int[] res = new int[24];
		res[hour] = 1;
		return res;
	}
	
	protected int[] getPriceVector(Calendar c) {
		int[] res = new int[oldPriceNumber];
		c.add(Calendar.HOUR_OF_DAY, -1 * oldPriceNumber);
		for(int i = 0; i < oldPriceNumber; i++) {
			res[i] = station.getHistoricPrice(c.getTime());
			if(res[i] < 0) return null;
			c.add(Calendar.HOUR_OF_DAY, 1);
		}
		return res;
	}
	
	/**
	* Gibt Anzahl der Epochen zurueck.
	* @return anzahlEpochen
	*/
	public int getEpoches() {
		return this.epochs;
	}
	
	public int getOldPriceNumber() {
		return this.oldPriceNumber;
	}

	public GasStation getStation() {
		return station;
	}
}
