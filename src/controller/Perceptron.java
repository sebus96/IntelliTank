package controller;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.GasStation;
import model.Price;

/**
* Perzeptron
*
* @author Sebastian
*/
public class Perceptron {
	
	private double rate;
	private int epochs;
	private final int precision = 13;
	private final int oldPriceNumber = 1;
	private double[] weights = new double[7+24+oldPriceNumber];
	private GasStation station;
	
	/**
	* Konstruktor zum Perzeptron mit Eingabeparameter Lernrate und Anzahl der Epochen.
	*
	* @param lernrate Lernrate
	* @param anzahlEpoche Anzahl der Epochen
	*/
	public Perceptron(GasStation gs, double rate, int epochs) {
		this.rate = rate;
		this.station = gs;
		this.epochs = epochs;
		for(int i = 0; i < weights.length; i++) {
			weights[i] = Math.random();
		}
	}
	
	/**
	* Perzeptron wird trainiert.
	*
	* @param traindata Datensatz zum Training
	* @return true||false Je nach dem, ob 95% richtig erkannt werden, haengt der Rueckgabewert davon ab
	*/
	public boolean train(Date until) {
		double totalDifference = 0;
		int epochCounter = 0;
		int listCounter = 0;
		do {
			totalDifference = 0;
			listCounter = 0;
			for(int i = 0; i < station.getPriceListSize(); i++) {
				Price p = station.getPriceListElement(i);
				if(p.getTime().after(until)) break;
				listCounter++;
				Calendar c = Calendar.getInstance();
				c.setTime(p.getTime());
				int[] hourVector = getHourVector(c.get(Calendar.HOUR_OF_DAY));
				int[] weekdayVector = getDayVector(c.get(Calendar.DAY_OF_WEEK));
				int[] lastPrices = getPriceVector(c);
				double out = output(p.getTime(), lastPrices);
				double dif = p.getPrice() - out;
				for(int j = 0; j < weekdayVector.length; j++)
					weights[j] += rate * dif * weekdayVector[j];
				for(int j = 0; j < hourVector.length; j++)
					weights[j+7] += rate * dif * hourVector[j];
				for(int j = 0; j < lastPrices.length; j++)
					weights[j+7+24] += rate * dif * (lastPrices[j] / 1000.0);
				totalDifference += Math.abs(dif);
				//if(epochCounter < 4 && i==0) System.out.println(out + " " + hour + " " + weekday + " " + dif + " (" + p.getPrice() + ")");
			}
			epochCounter++;
			//System.out.println(totalDifference);
		} while(totalDifference/listCounter > precision && epochCounter <= this.epochs);
		System.out.println(epochCounter + " Epochen");
		System.out.print("[");
		for(int i = 0; i < weights.length; i++) {
			if(i==7 || i==7+24) System.out.println();
			System.out.print(weights[i] + (i < weights.length-1 ? ", ": ""));
		}
		System.out.print("]\n");
		return totalDifference/listCounter <= precision;
	}
	
	private int[] getDayVector(int day) {
		if(day > 7 || day <= 0) return null;
		int[] res = new int[7];
		res[day-1] = 1;
		return res;
	}
	
	private int[] getHourVector(int hour) {
		if(hour >= 24 || hour < 0) return null;
		int[] res = new int[24];
		res[hour] = 1;
		return res;
	}
	
	private int[] getPriceVector(Calendar c) {
		int[] res = new int[oldPriceNumber];
		c.add(Calendar.HOUR_OF_DAY, -1 * oldPriceNumber);
		for(int i = 0; i < oldPriceNumber; i++) {
			res[i] = station.getPrice(c.getTime());
			c.add(Calendar.HOUR_OF_DAY, 1);
		}
		return res;
	}
	
	public boolean train() {
		return this.train(new Date());
	}
	
	/**
	* Berechnet das Skalarprodukt zwischen den Gewichten und dem Eingabevektor.
	* @param input Datum fuer das der Preis bestimmt werden soll
	* @return berechneter Preis
	*/
	public double output(Date d, int[] lastPrices) {
		if(lastPrices.length != this.oldPriceNumber) System.err.println("wrong input number of old prices");
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		int[] hour = getHourVector(c.get(Calendar.HOUR_OF_DAY));
		int[] weekday = getDayVector(c.get(Calendar.DAY_OF_WEEK));
		double res = 0;
		for(int i = 0; i < weekday.length; i++) {
			res += weekday[i]*weights[i];
		}
		for(int i = 0; i < hour.length; i++) {
			res += hour[i]*weights[i+7];
		}
		for(int i = 0; i < lastPrices.length; i++)
			res += (lastPrices[i] / 1000.0)*weights[i+24+7];
		return res;
	}
	
	/**
	* Berechnet das Skalarprodukt zwischen den Gewichten und dem Eingabevektor.
	* @param input Datum für das der Preis bestimmt werden soll
	* @return berechneter Preis
	*/
	public double output(Date d, List<Double> lastPrices) {
		if(lastPrices.size() != this.oldPriceNumber) System.err.println("wrong input number of old prices");
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		int[] hour = getHourVector(c.get(Calendar.HOUR_OF_DAY));
		int[] weekday = getDayVector(c.get(Calendar.DAY_OF_WEEK));
		double res = 0;
		for(int i = 0; i < weekday.length; i++) {
			res += weekday[i]*weights[i];
		}
		for(int i = 0; i < hour.length; i++) {
			res += hour[i]*weights[i+7];
		}
		for(int i = 0; i < lastPrices.size(); i++)
			res += ((double)lastPrices.get(i) / 1000.0)*weights[i+24+7];
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
}