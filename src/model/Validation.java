package model;

/**
 * Datensammlung zur Validierung von erzeugten Vorhersagen. Es werden Messwerte, wie Durschnitte, Maxima und Minima, verwaltet.
 *
 * @author Sebastian Drath
 *
 */
public class Validation {
	private double avgDiff, maxDiff;
	private double avgPrice, minPrice , maxPrice;
	private double realAvgPrice , realMinPrice, realMaxPrice;
	private double avgDiffCurrentTime, maxDiffCurrentTime;
	private int counter, currentTimeCounter;
	
	/**
	 * Erstellt eine leere Validierung. Alle Werte werden auf 0 gesetzt. Die Minima und Maxima werden auf den höchstmöglichen bzw. kleinstmöglichen Wert gesetzt.
	 */
	public Validation() {
		this.avgDiff = 0;
		this.maxDiff = Double.MIN_VALUE;
		this.avgPrice = 0;
		this.minPrice = Double.MAX_VALUE;
		this.maxPrice = Double.MIN_VALUE;
		this.realAvgPrice = 0;
		this.realMinPrice = Double.MAX_VALUE;
		this.realMaxPrice = Double.MIN_VALUE;
		this.avgDiffCurrentTime = 0;
		this.maxDiffCurrentTime = Double.MIN_VALUE;
		this.counter = 0;
		this.currentTimeCounter = 0;
	}
	
	/**
	 * Erstellt eine Validierung mit Durchschnittspreis sowie minimalem und maximalem Preis. Diese Validierung wird für
	 * Vorhersagen verwendet, wenn keine echten Daten als Vergleich vorhanden sind, weil die Route bspw. in der Zunkunft liegt.
	 * Die Anzahl der eingeflossenen Elemente wird benötigt, damit mehrere Validierungen zu einer verknüpft werden können.
	 * So kann beispielsweise aus den Validierungen der Tankstops eine Validierung für eine Route generiert werden.
	 *
	 * @param avgPrice Durschnittspreis
	 * @param minPrice Minimaler Preis
	 * @param maxPrice Maximaler Preis
	 * @param counter Anzahl der Elemente, die in den Durchschnitt eingeflossen sind
	 */
	public Validation(double avgPrice, double minPrice, double maxPrice, int counter) {
		this();
		this.avgPrice = avgPrice;
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
		this.counter = counter;
	}
	
	/**
	 * Erstellt eine vollständige Validierung. Eine vollständige Validierung kann erstellt werden, wenn echte Preise als Vergleich verfügbar sind.
	 * Die Anzahl der eingeflossenen Elemente wird benötigt, damit mehrere Validierungen zu einer verknüpft werden können.
	 * So kann beispielsweise aus den Validierungen der Tankstops eine Validierung für eine Route generiert werden.
	 *
	 * @param avgDiff Durschnittsabweichung über den ganzen Vorhersagezeitraum
	 * @param maxDiff Maximale Abweichung über den ganzen Vorhersagezeitraum
	 * @param avgPrice Durchschnittlicher vorhergesagter Preis
	 * @param minPrice Minimaler vorhergesagter Preis
	 * @param maxPrice Maximaler vorhergesagter Preis
	 * @param realAvgPrice Durchschnittlicher echter Preis
	 * @param realMinPrice Minimaler echter Preis
	 * @param realMaxPrice Maximaler echter Preis
	 * @param avgDiffCurrentTime Durschnittsabweichung nur an den Vorhersagezeiten
	 * @param maxDiffCurrentTime Maximale Abweichung nur an den Vorhersagezeiten
	 * @param counter Anzahl der Elemente, die in die Durchschnitte (außer die Durchschnittsabweichung für die Vorhersagezeiten) eingeflossen sind
	 * @param currentTimeCounter Anzahl der Elemente, die in die Durchschnittsabweichung für die Vorhersagezeiten eingeflossen sind
	 */
	public Validation(double avgDiff, double maxDiff, double avgPrice, double minPrice, double maxPrice,
			double realAvgPrice, double realMinPrice, double realMaxPrice, double avgDiffCurrentTime, double maxDiffCurrentTime, int counter, int currentTimeCounter) {
		this.avgDiff = avgDiff;
		this.maxDiff = maxDiff;
		this.avgPrice = avgPrice;
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
		this.realAvgPrice = realAvgPrice;
		this.realMinPrice = realMinPrice;
		this.realMaxPrice = realMaxPrice;
		this.avgDiffCurrentTime = avgDiffCurrentTime;
		this.maxDiffCurrentTime = maxDiffCurrentTime;
		this.counter = counter;
		this.currentTimeCounter = currentTimeCounter;
	}
	
	/**
	 * Verknüpft zwei Validierungen, um beispielsweise aus allen Validierungen der Tankstops eine für die gesamte Route zu generieren. Die verknüpfte Validierung wird in diesem Objekt gespeichert.
	 *
	 * @param v die zweite Validierung
	 */
	public void add(Validation v) {
		if(v == null) return;
		if(this.counter > 0 || v.counter > 0) {
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
		if (this.currentTimeCounter > 0 || v.currentTimeCounter > 0) {
			this.avgDiffCurrentTime = (this.avgDiffCurrentTime * this.currentTimeCounter + v.avgDiffCurrentTime * v.currentTimeCounter);
			this.maxDiffCurrentTime = Math.max(this.maxDiffCurrentTime, v.maxDiffCurrentTime);
			this.currentTimeCounter = this.currentTimeCounter + v.currentTimeCounter;
			this.avgDiffCurrentTime /= this.currentTimeCounter;
		}
	}
	
	@Override
	public String toString() {
		if(this.avgDiff == 0 && this.maxDiff == Double.MIN_VALUE &&
				this.realAvgPrice == 0 && this.realMinPrice == Double.MAX_VALUE && this.realMaxPrice == Double.MIN_VALUE &&
				this.avgDiffCurrentTime == 0 && this.currentTimeCounter == 0) {
			if(this.avgPrice == 0 && this.minPrice == Double.MAX_VALUE && this.maxPrice == Double.MIN_VALUE && this.counter == 0) {
				return null;
			} else {
				return "Es sind keine Vergleichsdaten vorhanden." + "\n\n"
						+ "Durchschnittspreis: " + ((int)avgPrice/1000.0) + "\n"
						+ "Maximalpreis: " + ((int)maxPrice/1000.0) + "\n"
						+ "Minimalpreis: " + ((int)minPrice/1000.0) + "\n";
			}
			
		}
		return "Durchschnittsabweichung:\t" + ((int)avgDiff/1000.0) + "(nur gefragter Zeitpunkt: " + ((int)avgDiffCurrentTime/1000.0) + ")\n"
				+ "Maximale Abweichung:\t\t" + ((int)maxDiff/1000.0) + "(nur gefragter Zeitpunkt: " + ((int)maxDiffCurrentTime/1000.0) + ")\n"
				+ "Durchschnittspreis:\t\t\t" + ((int)avgPrice/1000.0) + "  (real: " + ((int)realAvgPrice/1000.0) + " )\n"
				+ "Maximalpreis:\t\t\t\t" + ((int)maxPrice/1000.0) + "  (real: " + ((int)realMaxPrice/1000.0) + " )\n"
				+ "Minimalpreis:\t\t\t\t" + ((int)minPrice/1000.0) + "  (real: " + ((int)realMinPrice/1000.0) + " )\n";
	}
	
	/**
	 * Gibt einen HTML-String aus, der die Daten aus dieser Validierung in einer Tabelle darstellt.
	 *
	 * @return HTML-String
	 */
	public String toHTMLString() {
		// Anzeige der Abweichung an den Zeiten, an denen die Vorhersage angefordert wurde
		String currentTimeText = "nur gefragte" + (currentTimeCounter == 1? "r" : "") + " Zeitpunkt" + (currentTimeCounter == 1? "" : "e");
		String realPriceText = "echte Preise";
		String tableHeader = "<table style=\"font-size:12;\">";
		
		// Zeilen template bei Validierungen ohne echte Preise
		String row = "<tr><td><b>--name--:</b></td><td>--value--</td><td>€</td></tr>";
		// Zeilentemplate bei Validierungen mit echten Preisen
		String rowlong = "<tr><td><b>--name--:</b></td><td>--value--</td><td>€</td><td>(--secondname--: --secondvalue--</td><td>€)</td></tr>";
		
		// Werte mit denen die Templates gefüllt werden.
		// Die Längen aller Arrays sind identisch und der jeweils i-te Eintrag steht für die i-te Zeile in der Tabelle
		String[] names =	{	"Durchschnittsabweichung"		, "Maximale Abweichung"				, "Durchschnittspreis"			, "Maximalpreis"			, "Minimalpreis"			};
		String[] names2 =	{	currentTimeText					, currentTimeText					, realPriceText					, realPriceText				, realPriceText				};
		double[] values =	{((int)avgDiff/1000.0)				, ((int)maxDiff/1000.0)				, ((int)avgPrice/1000.0)		, ((int)maxPrice/1000.0)	, ((int)minPrice/1000.0)	};
		double[] values2 =	{((int)avgDiffCurrentTime/1000.0)	, ((int)maxDiffCurrentTime/1000.0)	, ((int)realAvgPrice/1000.0)	, ((int)realMaxPrice/1000.0), ((int)realMinPrice/1000.0)};
		
		if(this.avgDiff == 0 && this.maxDiff == Double.MIN_VALUE &&
				this.realAvgPrice == 0 && this.realMinPrice == Double.MAX_VALUE && this.realMaxPrice == Double.MIN_VALUE &&
				this.avgDiffCurrentTime == 0 && this.currentTimeCounter == 0) {
			if(this.avgPrice == 0 && this.minPrice == Double.MAX_VALUE && this.maxPrice == Double.MIN_VALUE && this.counter == 0) {
				// alle Werte sind 0 bzw. der Minimal- oder Maximalwert -> keine Validierung möglich
				return "Keine Validierung möglich.";
			} else {
				// Es sind keine echten Daten vorhanden -> Es wird nur minmal-, maximal- und Durchschnittpreis der Vorhersage angezeigt
				String res = "Es sind keine Vergleichsdaten vorhanden." + "<br><br>" + tableHeader;
				for(int i = 2; i < 5; i++) {
					res += row.replaceAll("--name--",names[i]).replaceAll("--value--", ""+values[i]);
				}
				return res + "</table>";
			}
			
		}
		// alle Daten sind verfügbar und werden angezeigt
		String res = tableHeader;
		for(int i = 0; i < names.length; i++) {
			res += rowlong.replaceAll("--name--", names[i]).replaceAll("--value--", ""+values[i]).replaceAll("--secondname--","" + names2[i]).replaceAll("--secondvalue--","" + values2[i]);
		}
		return res + "</table>";
	}
}
