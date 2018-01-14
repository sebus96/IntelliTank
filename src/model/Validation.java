package model;

public class Validation {
	private double avgDiff, maxDiff;
	private double avgPrice, minPrice , maxPrice;
	private double realAvgPrice , realMinPrice, realMaxPrice;
	private int counter;
	
	public Validation(double avgDiff, double maxDiff, double avgPrice, double minPrice, double maxPrice,
			double realAvgPrice, double realMinPrice, double realMaxPrice, int counter) {
		this.avgDiff = avgDiff;
		this.maxDiff = maxDiff;
		this.avgPrice = avgPrice;
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
		this.realAvgPrice = realAvgPrice;
		this.realMinPrice = realMinPrice;
		this.realMaxPrice = realMaxPrice;
		this.counter = counter;
	}
	
	public Validation() {
		this.avgDiff = 0;
		this.maxDiff = Double.MIN_VALUE;
		this.avgPrice = 0;
		this.minPrice = Double.MAX_VALUE;
		this.maxPrice = Double.MIN_VALUE;
		this.realAvgPrice = 0;
		this.realMinPrice = Double.MAX_VALUE;
		this.realMaxPrice = Double.MIN_VALUE;
		this.counter = 0;
	}
	
	public void setDifference(double avgDiff, double maxDiff) {
		this.avgDiff = avgDiff;
		this.maxDiff = maxDiff;
	}
	
	public void setPrice(double avgPrice, double minPrice, double maxPrice) {
		this.avgPrice = avgPrice;
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
	}
	
	public void setRealPrice(double realAvgPrice, double realMinPrice, double realMaxPrice) {
		this.realAvgPrice = realAvgPrice;
		this.realMinPrice = realMinPrice;
		this.realMaxPrice = realMaxPrice;
	}
	
	public void setCounter(int counter) {
		this.counter = counter;
	}
	
	public void add(Validation v) {
		this.avgDiff = (this.avgDiff * this.counter + v.avgDiff * v.counter);
		this.maxDiff = Math.max(this.maxDiff, v.maxDiff);
		this.avgPrice = (this.avgPrice * this.counter + v.avgPrice * v.counter);
		this.minPrice = Math.min(this.minPrice, v.minPrice);
		this.maxPrice = Math.max(this.maxPrice, v.maxPrice);
		this.realAvgPrice = (this.realAvgPrice * this.counter + v.realAvgPrice * v.counter);
		this.realMinPrice = Math.min(this.realMinPrice, v.realMinPrice);
		this.realMaxPrice = Math.max(this.realMaxPrice, v.realMaxPrice);
		this.counter = this.counter + v.counter;
		this.avgDiff /= this.counter;
		this.avgPrice /= this.counter;
		this.realAvgPrice /= this.counter;
	}
	
	@Override
	public String toString() {
		return "Validierung" + "\n"
				+ "Durchschnittsabweichung: " + ((int)avgDiff/1000.0) + " \n"
				+ "Maximale Abweichung: " + ((int)maxDiff/1000.0) + " \n"
				+ "Durchschnittspreis: " + ((int)avgPrice/1000.0) + "  (real: " + ((int)realAvgPrice/1000.0) + " )\n"
				+ "Maximalpreis: " + ((int)maxPrice/1000.0) + "  (real: " + ((int)realMaxPrice/1000.0) + " )\n"
				+ "Minimalpreis: " + ((int)minPrice/1000.0) + "  (real: " + ((int)realMinPrice/1000.0) + " )\n";
	}
}
