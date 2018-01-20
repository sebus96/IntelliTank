package controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import model.GasStation;
import model.IPredictionStation;
import model.Price;
import model.Validation;

/**
 * Verwaltet die Vorhersagen für einen Tankstop bzw. einen Vorhersagezeitpunkt (siehe {@link model.IPredictionStation}).
 * In dieser Klasse wird je nach eingestelltem Modus entschieden welche Art von Perzeptron verwendet wird (einlagig oder mehrlagig).
 * Es können Trainings gestartet werden und es kann die vorhergesagte Preisliste generiert werden.
 * Dafür werden die Preise stündlich 5 Wochen in die Zunkunft vorhergesagt und als Preisliste zurückgegeben.
 *
 * @author Sebastian Drath
 *
 */
public class PredictionUnit {
	private boolean PRINT_RESULTS = false;
	
	private IPredictionStation station;
	private Date trainUntil;
	private Perceptron perceptron;
	
	/**
	 * Der Modus entscheidet, ob ein einlagiges oder ein mehrlagiges Perzeptron verwendet wird.
	 *
	 * @author Sebastian Drath
	 *
	 */
	public enum Mode {/** einlagiges Perzeptron wird benutzt */ SINGLE_LAYER, /** mehrlagiges Perzeptron wird benutzt */ MULTI_LAYER};
	private Mode mode;

	/**
	 * Erstellt eine neue Vorhersageeinheit für eine {@link model.IPredictionStation} mit einem Datum, bis zu dem die Preise bekannt sind, und einem Modus (Art des Perzeptrons).
	 *
	 * @param station der Tankstop oder der Vorhersagezeitpunkt für den vorhergesagt werden soll
	 * @param trainUntil das Datum bis zu dem die historischen Preise verwendet werden
	 * @param mode der Modus (einlagig/mehrlagig)
	 */
	public PredictionUnit(IPredictionStation station, Date trainUntil, Mode mode) {
		this.station = station;
		this.trainUntil = trainUntil;
		this.mode = mode;
	}
	
	public PredictionUnit(IPredictionStation station, Perceptron p) {
		this(station, p.getUntil(), (p instanceof SingleLayerPerceptron? Mode.SINGLE_LAYER: Mode.MULTI_LAYER));
		this.perceptron = p;
	}
	
	/**
	 * Startet ein Training für die eingestellte Tankstelle. Dafür wird die Perzeptronart benutzt die über den Modus eingestellt ist.
	 * Gibt zurück, ob eine Vorhersage erstellt werden konnte. Es kann keine erstellt werden, wenn das Datum, bis zu dem Preise benutzt werden dürfen, vor dem ersten bekannten Preis liegt oder wenn zwischen dem
	 * Datum, bis zu dem Preise benutzt werden dürfen, und dem letzten bekannten Preis eine unbekannte Lücke liegt. Außerdem müssen die Preise über mindestens 2 Wochen bekannt sein.
	 *
	 * @return true, wenn trainiert werden konnte und Preise vorhanden sind die mindestens 2 Wochen lang verfügbar sind, false ansonsten
	 */
	public boolean train() {
		GasStation gs = station.getStation();
		Price firstPrice = gs.getPriceListElement(0);
		Price lastPrice = gs.getPriceListElement(gs.getPriceListSize()-1);
		if(trainUntil.before(firstPrice.getTime())) {
			System.err.println("No historic prices available for " + gs + "!");
			return false;
		}
		if(trainUntil.after(lastPrice.getTime())) {
			System.err.println("Prediction for " + gs + " too far in future!");
			return false;
		}
		if(trainUntil.getTime() - firstPrice.getTime().getTime() <= 2*7*24*60*60*1000) { // mindestens 2 Wochen verfügbare Preise (2 Wochen * 7 Tage * 24 Stunden * 60 Minuten * 60 Sekunden * 1000 Millisekunden)
			System.err.println("The prediction needs at least 2 weeks of historic prices.");
			return false;
		}
		if(perceptron != null && perceptron.isTrained()) return true;
		if(this.mode == Mode.SINGLE_LAYER)
			perceptron = new SingleLayerPerceptron(gs,0.08, 120, trainUntil);
		else
			perceptron = new MultiLayerPerceptron(gs, 100, trainUntil);
		boolean trainResult = perceptron.train();
		if(PRINT_RESULTS) System.out.println("Trainigsziel erreicht: " + (trainResult ? "Ja": "Nein"));
		return true;
	}
	
	public List<Price> testAndSetHourSteps() {
		if(perceptron == null) {
			System.err.println("Train before using prediction!");
			return null;
		}
		GasStation gs = station.getStation();
		double avgDiff = 0;
		double maxDiff = Double.MIN_VALUE;
		double avgPrice = 0;
		double minPrice = Double.MAX_VALUE;
		double maxPrice = Double.MIN_VALUE;
		double realAvgPrice = 0;
		double realMinPrice = Double.MAX_VALUE;
		double realMaxPrice = Double.MIN_VALUE;
		double diffCurrentTime = -1;
		int ctr = 0;
		List<Double> lastPrices = new LinkedList<>();
		List<Price> predictedPriceList = new ArrayList<>();
		Calendar c = Calendar.getInstance();
		c.setTime(trainUntil);
		predictedPriceList.add(new Price(c.getTime(), gs.getHistoricPrice(c.getTime()))); // letzter bekannter Preis zu Beginn der Route eingefügt
		c.add(Calendar.HOUR_OF_DAY, (-1)*(perceptron.oldPriceNumber-1));
		for(int i = 0; i < perceptron.oldPriceNumber; i++) {
			lastPrices.add((double)gs.getHistoricPrice(c.getTime()));
			c.add(Calendar.HOUR_OF_DAY, 1);
		}
		boolean realDataAvailable = true;
		// der Zeitpunkt zu dem vorhergesagt werden soll liegt vor der Kalenderzeit und nach dem ersten Preislistenelement
		// invertiertes before bzw after, damit auch der identische Zeitpunkt true zurückgibt
		if(!c.getTime().before(this.station.getTime()) && !predictedPriceList.get(0).getTime().after(this.station.getTime())) {
			diffCurrentTime = Math.abs(predictedPriceList.get(0).getPrice() - gs.getHistoricPrice(this.station.getTime()));
		}
		for(int i = 0; i < 24*7*5; i++) { // 5 Wochen
			ctr++;
			double out = perceptron.feedForward(c.getTime(), lastPrices);
			double real = gs.getHistoricPrice(c.getTime());
			Price p = new Price(c.getTime() , (int)Math.round(out));
			predictedPriceList.add(p);
			int price = p.getPrice();
			if(real < 0) realDataAvailable = false;
			double diff = Math.abs(price-real);
			if (!c.getTime().after(this.station.getTime())) {
				diffCurrentTime = diff;
			}
			if(diff > maxDiff) maxDiff = diff;
			if(price < minPrice) minPrice = price;
			if (price > maxPrice) maxPrice = price;
			if(real < realMinPrice) realMinPrice = real;
			if (real > realMaxPrice) realMaxPrice = real;
			avgDiff += diff;
			avgPrice += price;
			realAvgPrice += real;
			lastPrices.remove(0);
			lastPrices.add(out);
			c.add(Calendar.HOUR_OF_DAY, 1);
		}
		avgDiff /= ctr;
		avgPrice /= ctr;
		realAvgPrice /= ctr;
		if(diffCurrentTime < 0) System.err.println("Current price difference has not been set for " + gs + ". " + station.getTime() + " " + predictedPriceList.get(0).getTime());
		if(realDataAvailable) station.setValidation(new Validation(avgDiff, maxDiff, avgPrice, minPrice, maxPrice, realAvgPrice, realMinPrice, realMaxPrice, diffCurrentTime, diffCurrentTime, ctr, 1));
		else station.setValidation(new Validation(avgPrice, minPrice, maxPrice, ctr));
		return predictedPriceList;
	}
	
	public Perceptron getPerceptron() {
		return this.perceptron;
	}
}
