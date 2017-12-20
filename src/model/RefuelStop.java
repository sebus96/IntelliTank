package model;

import java.util.Date;

public class RefuelStop {

    private Date time;
    private GasStation station;

    //Variables below are needed for the Fixed Path Gas Station Problem
    private RefuelStop prevStation;
    private RefuelStop nextStation;
    private boolean breakPoint;
    private boolean nextStationBool;

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

    public RefuelStop(GasStation station, Date time) {
        this.station = station;
        this.time = time;
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
