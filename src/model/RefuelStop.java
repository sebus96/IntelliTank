package model;

import java.util.Date;
import java.util.List;

public class RefuelStop {

    private Date time;
    private GasStation station;
    private int guessedPrice = 0;
    
    private List<Price> predictedPrices;

    //Variables below are needed for the Fixed Path Gas Station Problem
    private RefuelStop prevStation;
    private RefuelStop nextStation;
    private boolean breakPoint;
    private boolean nextStationBool;
    private boolean priceGuessed;

    public RefuelStop(GasStation station, Date time) {
        this.station = station;
        this.time = time;
//        this.predictedPrices = new ArrayList<>();
    }
    
    @Deprecated // normalerweise sollten hier nur die vorhergesagten Preise verwendet werden
    public int getHistoricPrice(Date d) { // TODO Bedarf?
    	return station.getHistoricPrice(d);
    }
    
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
    
    public int getPredictedPrice() {
    	return this.getPredictedPrice(this.time);
    }
    
    public void setPredictedPrices( List<Price> predicted) {
    	this.predictedPrices = predicted;
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
    //the current amount of gas in the tank and how much gas is added at that station
    private double fuelAmount;
    private double refillAmount;

    public double getFuelAmount() {
        return fuelAmount;
    }

    public void setFuelAmount(double fuelAmount) {
        this.fuelAmount = fuelAmount;
    }

    public double getRefillAmount() {
        return refillAmount;
    }

    public void setRefillAmount(double refillAmount) {
        this.refillAmount = refillAmount;
    }

    public Date getTime() {
        return time;
    }

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
    public String toString() {
        return "(" + time + ": " + station + ")";
    }
}
