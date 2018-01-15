package model;

public class Validation {
	private double avgDiff, maxDiff;
	private double avgPrice, minPrice , maxPrice;
	private double realAvgPrice , realMinPrice, realMaxPrice;
	private int counter;
	
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
	
	public Validation(double avgPrice, double minPrice, double maxPrice, int counter) {
		this();
		this.avgPrice = avgPrice;
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
		this.counter = counter;
	}
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
		if(v == null) return;
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
		if(this.avgDiff == 0 && this.maxDiff == Double.MIN_VALUE &&
				this.realAvgPrice == 0 && this.realMinPrice == Double.MAX_VALUE && this.realMaxPrice == Double.MIN_VALUE) {
			if(this.avgPrice == 0 && this.minPrice == Double.MAX_VALUE && this.maxPrice == Double.MIN_VALUE && this.counter == 0) {
				return null;
			} else {
				return "Es sind keine Vergleichsdaten vorhanden." + "\n\n"
						+ "Durchschnittspreis: " + ((int)avgPrice/1000.0) + "\n"
						+ "Maximalpreis: " + ((int)maxPrice/1000.0) + "\n"
						+ "Minimalpreis: " + ((int)minPrice/1000.0) + "\n";
			}
			
		}
		return "Durchschnittsabweichung:\t" + ((int)avgDiff/1000.0) + " \n"
				+ "Maximale Abweichung:\t\t" + ((int)maxDiff/1000.0) + " \n"
				+ "Durchschnittspreis:\t\t\t" + ((int)avgPrice/1000.0) + "  (real: " + ((int)realAvgPrice/1000.0) + " )\n"
				+ "Maximalpreis:\t\t\t\t" + ((int)maxPrice/1000.0) + "  (real: " + ((int)realMaxPrice/1000.0) + " )\n"
				+ "Minimalpreis:\t\t\t\t" + ((int)minPrice/1000.0) + "  (real: " + ((int)realMinPrice/1000.0) + " )\n";
	}
	
	public String toHTMLString() {
		String tableHeader = "<table style=\"font-size:12;\">";
		String row = "<tr><td><b>--name--:</b></td><td>--value--</td><td>€</td></tr>";
		String rowlong = "<tr><td><b>--name--:</b></td><td>--value--</td><td>€</td><td>(real: --realvalue--</td><td>€)</td></tr>";
		String[] names = {	"Durchschnittsabweichung"	, "Maximale Abweichung"	, "Durchschnittspreis"		, "Maximalpreis"			, "Minimalpreis"			};
		double[] values = {((int)avgDiff/1000.0)		, ((int)maxDiff/1000.0)	, ((int)avgPrice/1000.0)	, ((int)maxPrice/1000.0)	, ((int)minPrice/1000.0)	};
		double[] realValues = {((int)realAvgPrice/1000.0), ((int)realMaxPrice/1000.0),((int)realMinPrice/1000.0)};
		if(this.avgDiff == 0 && this.maxDiff == Double.MIN_VALUE &&
				this.realAvgPrice == 0 && this.realMinPrice == Double.MAX_VALUE && this.realMaxPrice == Double.MIN_VALUE) {
			if(this.avgPrice == 0 && this.minPrice == Double.MAX_VALUE && this.maxPrice == Double.MIN_VALUE && this.counter == 0) {
				return "Keine Validierung möglich.";
			} else {
				String res = "Es sind keine Vergleichsdaten vorhanden." + "<br><br>" + tableHeader;
				for(int i = 2; i < 5; i++) {
					res += row.replaceAll("--name--",names[i]).replaceAll("--value--", ""+values[i]);
				}
				return res + "</table>";
			}
			
		}
		String res = tableHeader;
		for(int i = 0; i < 2; i++) {
			res += row.replaceAll("--name--",names[i]).replaceAll("--value--", ""+values[i]);
		}
		for(int i = 2; i < 5; i++) {
			res += rowlong.replaceAll("--name--", names[i]).replaceAll("--value--", ""+values[i]).replaceAll("--realvalue--",""+realValues[i-2]);
		}
		return res + "</table>";
		/*return "<b>Durchschnittsabweichung:</b>&emsp;" + ((int)avgDiff/1000.0) + " <br>"
				+ "<b>Maximale Abweichung:</b>&emsp;&emsp;" + ((int)maxDiff/1000.0) + " <br>"
				+ "<b>Durchschnittspreis:</b>&emsp;&emsp;&emsp;" + ((int)avgPrice/1000.0) + "  (real: " + ((int)realAvgPrice/1000.0) + " )<br>"
				+ "<b>Maximalpreis:</b>&emsp;&emsp;&emsp;&emsp;" + ((int)maxPrice/1000.0) + "  (real: " + ((int)realMaxPrice/1000.0) + " )<br>"
				+ "<b>Minimalpreis:</b>&emsp;&emsp;&emsp;&emsp;" + ((int)minPrice/1000.0) + "  (real: " + ((int)realMinPrice/1000.0) + " )<br>";*/
	}
}
