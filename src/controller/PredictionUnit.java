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

public class PredictionUnit {
	private boolean PRINT_RESULTS = false;
	
	private IPredictionStation station;
	private Date trainUntil;
	private Perceptron perceptron;
	
	public enum Mode {SINGLE_LAYER, MULTI_LAYER};
	private Mode mode;

	public PredictionUnit(IPredictionStation station, Date trainUntil, Mode mode) {
		this.station = station;
		this.trainUntil = trainUntil;
		this.mode = mode;
	}
	
	public boolean train() {
		GasStation gs = station.getStation();
		if(trainUntil.before(gs.getPriceListElement(0).getTime())) {
			System.err.println("No historic prices available for " + gs + "!");
			return false;
		}
		if(trainUntil.after(gs.getPriceListElement(gs.getPriceListSize()-1).getTime())) {
			System.err.println("Prediction for " + gs + " too far in future!");
			return false;
		}
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
		if(!c.getTime().before(this.station.getTime()) && !predictedPriceList.get(0).getTime().after(this.station.getTime())) {
			diffCurrentTime = 0;
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
			if (c.getTime().before(this.station.getTime())) {
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
}
