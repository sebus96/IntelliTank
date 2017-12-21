package controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import model.GasStation;
import model.Price;

public class PredictionUnit {
	private GasStation gs;
	private Date trainUntil;
	private Perceptron p;
	
	private boolean PRINT_RESULTS = false;

	public PredictionUnit(GasStation gs, Date trainUntil) {
		this.gs = gs;
		this.trainUntil = trainUntil;
	}
	
	public boolean train() {
		if(trainUntil.before(gs.getPriceListElement(0).getTime())) {
			System.err.println("No historic prices available for " + gs + "!");
			return false;
		}
		int epoch = 100;
		p = new Perceptron(gs, 0.05, epoch);
		boolean trainResult = p.train( trainUntil);
		if(PRINT_RESULTS) System.out.println("Trainigsziel erreicht: " + (trainResult ? "Ja": "Nein"));
		return true;
	}
	
	public List<Price> testAndSetHourSteps() {
		if(p == null) {
			System.err.println("Train before using prediction!");
			return null;
		}
		double avgDiff = 0;
		double maxDiff = Double.MIN_VALUE;
		double avgPrice = 0;
		double minPrice = Double.MAX_VALUE;
		double maxPrice = Double.MIN_VALUE;
		double realAvgPrice = 0;
		double realMinPrice = Double.MAX_VALUE;
		double realMaxPrice = Double.MIN_VALUE;
		int ctr = 0;
		List<Double> lastPrices = new LinkedList<>();
		List<Price> predictedPriceList = new ArrayList<>();
		Calendar c = Calendar.getInstance();
		c.setTime(trainUntil);
		predictedPriceList.add(new Price(c.getTime(), gs.getHistoricPrice(c.getTime()))); // letzter bekannter Preis zu Beginn der Route eingefügt
		c.add(Calendar.HOUR_OF_DAY, (-1)*(p.getOldPriceNumber()-1));
		for(int i = 0; i < p.getOldPriceNumber(); i++) {
			lastPrices.add((double)gs.getHistoricPrice(c.getTime()));
			c.add(Calendar.HOUR_OF_DAY, 1);
		}
		for(int i = 0; i < 24*7*5; i++) { // 5 Wochen
			ctr++;
			double out = p.output(c.getTime(), lastPrices);
			double real = gs.getHistoricPrice(c.getTime());
			if(real < 0) System.err.println("Could not compare to real data. Real data is not available for " + gs + " at " + c.getTime());
			predictedPriceList.add(new Price(c.getTime() , (int)Math.round(out)));
			double diff = Math.abs(out-real);
			if(diff > maxDiff) maxDiff = diff;
			if(out < minPrice) minPrice = out;
			if (out > maxPrice) maxPrice = out;
			if(real < realMinPrice) realMinPrice = real;
			if (real > realMaxPrice) realMaxPrice = real;
			avgDiff += diff;
			avgPrice += out;
			realAvgPrice += real;
			lastPrices.remove(0);
			lastPrices.add(out);
			c.add(Calendar.HOUR_OF_DAY, 1);
		}
		avgDiff /= ctr;
		avgPrice /= ctr;
		realAvgPrice /= ctr;
		if(PRINT_RESULTS) System.out.println(gs + "\n"
				+ "Durchschnittsabweichung: " + ((int)avgDiff/1000.0) + " €\n"
				+ "Maximale Abweichung: " + ((int)maxDiff/1000.0) + " €\n"
				+ "Durchschnittspreis: " + ((int)avgPrice/1000.0) + " € (real: " + ((int)realAvgPrice/1000.0) + " €)\n"
				+ "Maximalpreis: " + ((int)maxPrice/1000.0) + " € (real: " + ((int)realMaxPrice/1000.0) + " €)\n"
				+ "Minimalpreis: " + ((int)minPrice/1000.0) + " € (real: " + ((int)realMinPrice/1000.0) + " €)\n");
		return predictedPriceList;
	}
	
//	public void startNetwork() {
//
//        Network network = new Network();
//        network.train(gs);
//        /*int counter = 0;
//        network.feedForward(data);*/
//	}
	
//	public void test(Date after) {
//		double avgDiff = 0;
//		double maxDiff = Double.MIN_VALUE;
//		int ctr = 0;
//		int[] lastPrice = {gs.getPriceListElement(0).getPrice()};
//		for(int i = 0; i < gs.getPriceListSize(); i++) {
//			Price pr = gs.getPriceListElement(i);
//			if(pr.getTime().before(after)){
//				lastPrice[0] = pr.getPrice();
//				continue;
//			}
//			ctr++;
//			double out = p.output(pr.getTime(), lastPrice); //TODO real price
//			double diff = Math.abs(out-pr.getPrice());
//			if(diff > maxDiff) {
//				maxDiff = diff;
//			}
//			avgDiff += diff;
//			lastPrice[0] = pr.getPrice();
//		}
//		System.out.println(ctr);
//		avgDiff /= ctr;
//		System.out.println("Average: " + ((int)avgDiff/1000.0) + " €" + "\nMaximum: " + ((int)maxDiff/1000.0) + " €");
//	}
}
