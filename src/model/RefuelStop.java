package model;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import controller.PredictionUnit;
import io.CSVManager;

public class RefuelStop implements IPredictionStation {

    private Date time;
    private GasStation station;
    private int guessedPrice = 0;
    private boolean priceGuessed;
    
    private List<Price> predictedPrices;
	private PredictionUnit predictionUnit;

    //Variables below are needed for the Fixed Path Gas Station Problem
    private RefuelStop prevStation;
    private RefuelStop nextStation;
    private boolean breakPoint;
    private boolean nextStationBool;

    //Aktuelle Menge an Gas und wieviel an dem jeweiligen RefuelStop aufgefüllt wurde. die unteren beiden sind für die Standart Tankstrategie
    private double fuelAmount;
    private double refillAmount;
    
    private double fuelAmountBasic;
    private double refillAmountBasic;

    public void setFuelAmountBasic(double fuelAmountBasic) {
        this.fuelAmountBasic = fuelAmountBasic;
    }

    public void setRefillAmountBasic(double refillAmountBasic) {
        this.refillAmountBasic = refillAmountBasic;
    }
    

    public RefuelStop(GasStation station, Date time) {
        this.station = station;
        this.time = time;
//        this.predictedPrices = new ArrayList<>();
    }
    
    @Deprecated // normalerweise sollten hier nur die vorhergesagten Preise verwendet werden
    public int getHistoricPrice(Date d) { // TODO Bedarf?
    	return station.getHistoricPrice(d);
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
    	return this.getPredictedPrice(this.time);
    }
    
//    public void setPredictedPrices( List<Price> predicted) {
//    	this.predictedPrices = predicted;
//    }

    @Override
	public void setPrediction(PredictionUnit pu) {
		this.predictionUnit = pu;
		this.predictedPrices = this.predictionUnit.testAndSetHourSteps();
	}

    @Override
	public boolean isPredicted() {
		return predictionUnit != null;
	}

    public int getGuessedPrice() {
        return guessedPrice;
    }

    public void setGuessedPrice(int guessedPrice) {
        this.priceGuessed = true;
        this.guessedPrice = guessedPrice;
    }

    public boolean isPriceGuessed() {
        return priceGuessed;
    }

    public boolean isNextStation() {
        return nextStationBool;
    }

    public void setNextStationBool(boolean nextStationBool) {
        this.nextStationBool = nextStationBool;
    }


    public double getFuelAmount(Route route) {
        if(route.showBasicStrategy())
            return fuelAmountBasic;
        return fuelAmount;
        
    }

    public void setFuelAmount(double fuelAmount) {
        this.fuelAmount = fuelAmount;
    }

    public double getRefillAmount(Route route) {
        if(route.showBasicStrategy())
            return refillAmountBasic;
        return refillAmount;
    }

    public void setRefillAmount(double refillAmount) {
        this.refillAmount = refillAmount;
    }

    @Override
    public Date getTime() {
        return time;
    }

    @Override
    public GasStation getStation() {
        return station;
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
