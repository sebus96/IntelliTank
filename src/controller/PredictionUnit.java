package controller;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import model.GasStation;

public class PredictionUnit {
	private GasStation gs;
	private Perceptron p;

	public PredictionUnit(GasStation gs) {
		this.gs = gs;
	}
	
	public void train(Date until) {
		int epoch = 100;
		p = new Perceptron(gs, 0.05, epoch);
		System.out.println(p.train( until));
	}
	
	public void testHourSteps(Date after) {
		double avgDiff = 0;
		double maxDiff = Double.MIN_VALUE;
		int ctr = 0;
		List<Double> lastPrices = new LinkedList<>();
		Calendar c = Calendar.getInstance();
		c.setTime(after);
		c.add(Calendar.HOUR_OF_DAY, (-1)*(p.getOldPriceNumber()-1));
		for(int i = 0; i < p.getOldPriceNumber(); i++) {
			lastPrices.add((double)gs.getPrice(c.getTime()));
			c.add(Calendar.HOUR_OF_DAY, 1);
		}
		for(int i = 0; i < 24*7*5; i++) { // 5 Wochen
			ctr++;
			double out = p.output(c.getTime(), lastPrices);
			double diff = Math.abs(out-gs.getPrice(c.getTime()));
			if(diff > maxDiff) {
				maxDiff = diff;
			}
			avgDiff += diff;
			lastPrices.remove(0);
			lastPrices.add(out);
			c.add(Calendar.HOUR_OF_DAY, 1);
		}
		avgDiff /= ctr;
		System.out.println("Average: " + ((int)avgDiff/1000.0) + " €" + "\nMaximum: " + ((int)maxDiff/1000.0) + " €");
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
