package controller;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.FederalState;
import model.GasStation;
import model.Holidays;

public abstract class Perceptron {
	private int epochs;
	protected final int precision = 13;
	protected final int oldPriceNumber = 5;
//	protected final int hourSteps = 1;
	private GasStation station;
	
	public Perceptron(GasStation gs, int epochs) {
		this.station = gs;
		this.epochs = epochs;
	}
	
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
		// nächtliche Stunden werden zusammengefasst
		if(hour > 21 && hour <= 23) {
			res[22] = 1;
			res[23] = 1;
		} else if(hour >= 0 && hour < 4) {
			res[0] = 1;
			res[1] = 1;
			res[2] = 1;
			res[3] = 1;
		} else if(hour >= 4 && hour < 6) {
			res[4] = 1;
			res[5] = 1;
		} else { 
			res[hour] = 1;
		}
		return res;
	}
	
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
	
	protected double getHoliday(Date date, FederalState state) {
		return (Holidays.isHoliday(date, state)? 0.2 : 0);
	}
	
	/**
	* Gibt Anzahl der Epochen zurueck.
	* @return Epochenanzahl
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
