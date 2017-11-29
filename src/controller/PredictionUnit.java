package controller;

import java.util.Date;

import model.GasStation;
import model.Price;

public class PredictionUnit {
	private GasStation gs;
	private Perceptron p;

	public PredictionUnit(GasStation gs) {
		this.gs = gs;
	}
	
	public void start() {
		int epoch = 100;
		p = new Perceptron(0.05, epoch);
		System.out.println(p.train(gs));
	}
	
	public int checkDate(Date d) {
		double res = p.output(d);
		System.out.println(gs.getID() + ") " + gs.getBrand() + " " + gs.getName() + ": " + (res/1000.0) + " €");
		return (int)res;
	}
	
	public int checkDayHour(int day, int hour) {
		double res = p.output(hour, day);
		return (int)res;
	}
	
	public void test() {
		double avgDiff = 0;
		double maxDiff = Double.MIN_VALUE;
		for(int i = 0; i < gs.getPriceListSize(); i++) {
			Price pr = gs.getPriceListElement(i);
			double out = p.output(pr.getTime());
			double diff = Math.abs(out-pr.getPrice());
			if(diff > maxDiff) {
				maxDiff = diff;
			}
			avgDiff += diff/gs.getPriceListSize();
		}
		System.out.println("Average: " + ((int)avgDiff/1000.0) + " €" + "\nMaximum: " + ((int)maxDiff/1000.0) + " €");
	}
	
//	public void startNetwork() {
//
//        Network network = new Network();
//        network.train(gs);
//        /*int counter = 0;
//        network.feedForward(data);*/
//	}
}
