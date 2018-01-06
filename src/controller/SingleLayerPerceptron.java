package controller;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.GasStation;
import model.Price;

/**
* einlagiges Perzeptron
*
* @author Sebastian
*/
public class SingleLayerPerceptron extends Perceptron {
	
	private double rate;
	private double[] weights = new double[7+24+oldPriceNumber];
	
	/**
	* Konstruktor zum Perzeptron mit Eingabeparameter Lernrate und Anzahl der Epochen.
	*
	* @param lernrate Lernrate
	* @param anzahlEpoche Anzahl der Epochen
	*/
	public SingleLayerPerceptron(GasStation gs, double rate, int epochs) {
		super(gs, epochs);
		this.rate = rate;
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
	@Override
	public boolean train(Date until) {
		double totalDifference = 0;
		int epochCounter = 0;
		int listCounter = 0;
		do {
			totalDifference = 0;
			listCounter = 0;
			for(int i = 0; i < getStation().getPriceListSize(); i++) {
				Price p = getStation().getPriceListElement(i);
				if(p.getTime().after(until)) break;
				listCounter++;
				Calendar c = Calendar.getInstance();
				c.setTime(p.getTime());
				int[] hourVector = getHourVector(c.get(Calendar.HOUR_OF_DAY));
				int[] weekdayVector = getDayVector(c.get(Calendar.DAY_OF_WEEK));
				int[] lastPrices = getPriceVector(c);
				if(lastPrices == null) continue;
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
		} while(totalDifference/listCounter > precision && epochCounter <= getEpoches());
		//System.out.println(epochCounter + " Epochen");
		/*System.out.print("[");
		for(int i = 0; i < weights.length; i++) {
			if(i==7 || i==7+24) System.out.println();
			System.out.print(weights[i] + (i < weights.length-1 ? ", ": ""));
		}
		System.out.print("]\n");*/
		return totalDifference/listCounter <= precision;
	}
	
	/**
	* Berechnet das Skalarprodukt zwischen den Gewichten und dem Eingabevektor.
	* @param input Datum fuer das der Preis bestimmt werden soll
	* @return berechneter Preis
	*/
	private double output(Date d, int[] lastPrices) {
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
	@Override
	public double feedForward(Date d, List<Double> lastPrices) {
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
}