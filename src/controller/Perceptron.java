package controller;
import java.util.Calendar;
import java.util.Date;

import model.GasStation;
import model.Price;

/**
* Perzeptron-Klasse, in der ein Perzeptron trainiert wird.
*
* @author Burak Kadioglu
*/
public class Perceptron {
	
	private double rate = 0f;
	private int epochNumber = 0;
	private int precision = 30;
	private double[] weights = new double[7+24];
	
	/**
	* Konstruktor zum Perzeptron mit Eingabeparameter Lernrate und Anzahl der Epochen.
	*
	* @param lernrate Lernrate
	* @param anzahlEpoche Anzahl der Epochen
	*/
	public Perceptron(double rate, int epochNumber) {
		this.rate = rate;
		this.epochNumber = epochNumber;
		for(int i = 0; i < weights.length; i++) {
			weights[i] = Math.random();// * 100 + 50;
		}
	}
	
	/**
	* Perzeptron wird trainiert.
	*
	* @param traindata Datensatz zum Training
	* @return true||false Je nach dem, ob 95% richtig erkannt werden, hängt der Rückgabewert davon ab
	*/
	public boolean train(GasStation gs, Date until) {
		double totalDifference = 0;
		int epochCounter = 0;
		int listCounter = 0;
		do {
			totalDifference = 0;
			listCounter = 0;
			for(int i = 0; i < gs.getPriceListSize(); i++) {
				Price p = gs.getPriceListElement(i);
				listCounter++;
				//if(p.getTime().after(until)) break;
				Calendar c = Calendar.getInstance();
				c.setTime(p.getTime());
				int[] hour = getHourVector(c.get(Calendar.HOUR_OF_DAY));
				int[] weekday = getDayVector(c.get(Calendar.DAY_OF_WEEK));
				double out = output(p.getTime());
				double dif = p.getPrice() - out;
				for(int j = 0; j < weekday.length; j++)
					weights[j] += rate * dif * weekday[j];
				for(int j = 0; j < hour.length; j++)
					weights[j+7] += rate * dif * hour[j];
				totalDifference += Math.abs(dif);
				//if(ctr < 4 && i==0) System.out.println(out + " " + hour + " " + weekday + " " + dif + " (" + p.getPrice() + ")");
			}
			epochCounter++;
		} while(totalDifference/listCounter > precision && epochCounter <= this.epochNumber);
		return totalDifference/listCounter <= precision;
	}
	
	private int[] getDayVector(int day) {
		int[] res = new int[7];
		res[day-1] = 1;
		return res;
	}
	
	private int[] getHourVector(int hour) {
		int[] res = new int[24];
		res[hour] = 1;
		return res;
	}
	
	public boolean train(GasStation gs) {
		return this.train(gs, new Date());
	}
	
	/**
	* Berechnet das Skalarprodukt zwischen den Gewichten und dem Eingabevektor.
	* @param input Datum für das der Preis bestimmt werden soll
	* @return berechneter Preis
	*/
	public double output(Date d) {
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
		return res;
	}
	
	public double output(int inHour, int inDay) {
		int[] hour = getHourVector(inHour);
		int[] weekday = getDayVector(inDay);
		double res = 0;
		for(int i = 0; i < weekday.length; i++) {
			res += weekday[i]*weights[i];
		}
		for(int i = 0; i < hour.length; i++) {
			res += hour[i]*weights[i+7];
		}
		return res;
	}
	
	/**
	* Gibt Anzahl der Epochen zurück.
	* @return anzahlEpochen
	*/
	public int getEpochen() {
		return this.epochNumber;
	}
}