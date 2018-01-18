package model;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import controller.PredictionUnit;
import io.CSVManager;

/**
 * Repräsentation eines Tankstops innerhalb einer Route. Dieser umfasst eine Tankstelle, an der zu einer bestimmten Zeit getankt
 * werden soll sowie ein Vorhersageobjekt imklusive der vorhergesagten
 * Preisliste. Eine Validierung gibt Werte zur Beurteilung der Vorhersage an, wenn eine Vorhersage getätigt wurde und echte
 * Preise als Referenz vorliegen.
 * Außerdem werden relevante Variablen eines Tankstops für das Erzeugen der Tankstrategie verwaltet. (siehe Verwendung in {@link controller.RefillStrategies})
 *
 * @author Sebastian Drath
 *
 */
public class RefuelStop implements IPredictionStation {

    private Date time;
    private GasStation station;
    private int guessedPrice = -1;
    private boolean priceGuessed;
    
    private List<Price> predictedPrices;
    private Price predictedPrice;
	private PredictionUnit predictionUnit;
	private Validation validation;

    //Variablen für das Fixed Path Gas Station Problem
    private RefuelStop prevStation;
    private RefuelStop nextStation;
    private boolean breakPoint;
    private boolean nextStationBool;

    //Aktuelle Menge an Benzin und wieviel an dem jeweiligen RefuelStop aufgefüllt wurde (intelligente Strategie)
    private double fuelAmount;
    private double refillAmount;
    
    //Aktuelle Menge an Benzin und wieviel an dem jeweiligen RefuelStop aufgefüllt wurde (naive Standard Strategie)
    private double fuelAmountBasic;
    private double refillAmountBasic;

    public RefuelStop(GasStation station, Date time) {
        this.station = station;
        this.time = time;
		this.validation = new Validation();
    }

    @Override
	public Date getTime() {
	    return time;
	}

	@Override
	public GasStation getStation() {
	    return station;
	}

	@Override
    public int getPredictedPrice(Date d) {
    	int prevPrice = -1;
    	if(predictedPrices == null || predictedPrices.size() == 0) return -1;
		for(Price p: this.predictedPrices) {
			if(p.getTime().after(d)) {
				return prevPrice;
			}
			prevPrice = p.getPrice();
		}
		System.err.println("Could not predict prices for more than 1 month!");
		return -1;
    }

    @Override
    public int getPredictedPrice() {
    	if(predictedPrice == null || !predictedPrice.getTime().equals(this.time)) {
    		predictedPrice = new Price(this.time, this.getPredictedPrice(this.time));
    	}
    	return predictedPrice.getPrice();
    }
    
    @Override
    public int getPredictedPriceListSize(){
    	if(isPredicted())
    		return this.predictedPrices.size();
    	else return 0;
    }
    
    @Override
	public Price getPredictedPriceListElement(int index) {
		if(isPredicted() && index < this.predictedPrices.size() && index >= 0)
			return this.predictedPrices.get(index);
		return null;
	}

    @Override
	public void setPrediction(PredictionUnit pu) {
		this.predictionUnit = pu;
		this.predictedPrices = this.predictionUnit.testAndSetHourSteps(); // setzt die vorhergesagten Preise für 5 Wochen
		this.predictedPrice = null; // zurücksetzen des gespeicherten Preises, da eine neue Vorhersage hinzugefügt wurde
	}
	
    @Override
	public boolean isPredicted() {
		return predictionUnit != null;
	}

	@Override
	public void setValidation(Validation v) {
		this.validation = v;
	}
	
    @Override
	public Validation getValidation() {
		return this.validation;
	}

    /**
     * Setzt einen geratenen Preis. Der Preis wird auf Basis der anderen Tankstops innerhalb einer Route geraten, wenn kein vorhergesagter Preis vorhanden ist.
     *
     * @param guessedPrice geratener Preis
     */
    public void setGuessedPrice(int guessedPrice) {
        this.priceGuessed = true;
        this.guessedPrice = guessedPrice;
    }
    
    /**
     * Gibt den vorhergesagten oder geratenen Preis für diesen Tankstop zurück. Der vorhergesagte Preis wird zurückgegeben, wenn kein geratener Preis gesetzt wurde. Wenn ein geratener Preis gesetzt wurde, weil keine
     * geeignete Vorhersage existiert, wird der geratene Preis zurückgegeben.
     *
     * @return vorhergesagter Preis oder der geratene, wenn dieser gesetzt wurde
     */
    public int getPrice() {
    	if(priceGuessed)
    		return this.guessedPrice;
    	else return this.getPredictedPrice();
    }

    /**
     * Gibt zurück, ob der Preis für diesen Tankstop geraten wurde.
     *
     * @return true, wenn der Preis geraten ist, false ansonsten
     */
    public boolean isPriceGuessed() {
        return priceGuessed;
    }

    /**
     * Gibt zurück, ob dieser Tankstop ein nächster günstigster Tankstop eines anderen ist. (siehe Verwendung in {@link controller.RefillStrategies})
     *
     * @return true, wenn der Tankstop ein nächster günstigerer eines anderen ist, false ansonsten
     */
    public boolean isNextStation() {
        return nextStationBool;
    }

    public void setNextStationBool(boolean nextStationBool) {
        this.nextStationBool = nextStationBool;
    }

    /**
     * Gibt den Tankfüllstand an diesem Tankstop für die momentan eingestellte Tankstrategie aus.
     *
     * @return Tankfüllstand
     */
    public double getFuelAmount() {
        if(Route.getStrategy() == Route.Strategy.BASIC)
            return fuelAmountBasic;
        return fuelAmount;
        
    }

    /**
     * Setzt den Tankfüllstand für die intelligente Tankstrategie.
     *
     * @param fuelAmount Tankfüllstand für die intelligente Tankstrategie
     */
    public void setFuelAmount(double fuelAmount) {
        this.fuelAmount = fuelAmount;
    }

    /**
     * Setzt den Tankfüllstand für die einfache Tankstrategie.
     *
     * @param fuelAmountBasic Tankfüllstand für die einfache Tankstrategie
     */
    public void setFuelAmountBasic(double fuelAmountBasic) {
	    this.fuelAmountBasic = fuelAmountBasic;
	}

    /**
     * Gibt die nachgetankte Benzinmenge an diesem Tankstop für die momentan eingestellte Tankstrategie aus.
     *
     * @return nachgetankte Benzinmenge in Litern
     */
	public double getRefillAmount() {
        if(Route.getStrategy() == Route.Strategy.BASIC)
            return refillAmountBasic;
        return refillAmount;
    }

	/**
     * Setzt die nachgetankte Benzinmenge für die intelligente Tankstrategie.
     *
     * @param refillAmount nachgetankte Benzinmenge in Litern für die intelligente Tankstrategie
     */
    public void setRefillAmount(double refillAmount) {
	    this.refillAmount = refillAmount;
	}

    /**
     * Setzt die nachgetankte Benzinmenge für die einfache Tankstrategie.
     *
     * @param refillAmountBasic nachgetankte Benzinmenge in Litern für die einfache Tankstrategie
     */
	public void setRefillAmountBasic(double refillAmountBasic) {
	    this.refillAmountBasic = refillAmountBasic;
	}

	public boolean isBreakPoint() {
        return breakPoint;
    }

    public void setBreakPoint(boolean bp) {
        this.breakPoint = bp;
    }

    /**
     * @return the prevStation
     */
    public RefuelStop getPrevStation() {
        return prevStation;
    }

    /**
     * @param prevStation the prevStation to set
     */
    public void setPrevStation(RefuelStop prevStation) {
        this.prevStation = prevStation;
    }

    /**
     * @return the nextStation
     */
    public RefuelStop getNextStation() {
        return nextStation;
    }

    /**
     * @param nextStation the nextStation to set
     */
    public void setNextStation(RefuelStop nextStation) {
        this.nextStation = nextStation;
    }
    
    @Override
    public String toCSVString() {
    	DateFormat df = CSVManager.getDateFormat();
    	return df.format(this.time) + ";"
    			+ this.station.getID() + ";"
    			+ this.getPredictedPrice() + ";"
    			+ this.refillAmount;
    }

    @Override
    public String toString() {
        return "(" + time + ": " + station + ")";
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((station == null) ? 0 : station.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RefuelStop other = (RefuelStop) obj;
		if (station == null) {
			if (other.station != null)
				return false;
		} else if (!station.equals(other.station))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		return true;
	}
}
