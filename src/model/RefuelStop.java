package model;

import java.util.Date;

public class RefuelStop {
	private Date time;
	private GasStation station;
        
        //Variables below are needed for the Fixed Path Gas Station Problem
        private RefuelStop prevStation;
        private RefuelStop nextStation;
        private boolean breakPoint;
	
	public RefuelStop(GasStation station, Date time){
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
}
