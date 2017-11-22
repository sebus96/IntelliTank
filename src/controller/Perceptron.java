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
	double[] weights = new double[2];
	
	/**
	* Konstruktor zum Perzeptron mit Eingabeparameter Lernrate und Anzahl der Epochen.
	*
	* @param lernrate Lernrate
	* @param anzahlEpoche Anzahl der Epochen
	*/
	public Perceptron(double rate, int epochNumber) {
		this.rate = rate;
		this.epochNumber = epochNumber;
		weights[0] = Math.random();// * 100 + 50;
		weights[1] = Math.random();// * 100 + 50;
	}
	
	/**
	* Perzeptron wird trainiert.
	*
	* @param traindata Datensatz zum Training
	* @return true||false Je nach dem, ob 95% richtig erkannt werden, hängt der Rückgabewert davon ab
	*/
	public boolean train(GasStation gs, Date until) {
		double totalDifference = 0;
		int ctr = 0;
		do {
			totalDifference = 0;
			for(int i = 0; i < gs.getPriceListSize(); i++) {
				Price p = gs.getPriceListElement(i);
				//if(p.getTime().after(until)) break;
				Calendar c = Calendar.getInstance();
				c.setTime(p.getTime());
				int hour = c.get(Calendar.HOUR_OF_DAY);
				int weekday = c.get(Calendar.DAY_OF_WEEK);
				double out = output(p.getTime());
				double dif = p.getPrice() - out;
				weights[0] += rate * dif * hour;
				weights[1] += rate * dif * weekday;
				totalDifference += dif/gs.getPriceListSize();
				if(ctr < 4 && i==0) System.out.println(out + " " + hour + " " + weekday + " " + dif + " (" + p.getPrice() + ")");
			}
			ctr++;
		} while(ctr < 10);//totalDifference > 50 && ctr <= this.epochNumber);
		return totalDifference <= 50;
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
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int weekday = c.get(Calendar.DAY_OF_WEEK);
		return (hour*weights[0] + weekday*weights[1]);
	}
	
	/**
	* Gibt Anzahl der Epochen zurück.
	* @return anzahlEpoche
	*/
	public int getEpochen() {
		return this.epochNumber;
	}
}