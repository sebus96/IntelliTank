package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Route {
	private double tankCapacity;
	private List<RefuelStop> route;
        private double totalEuros,totalKm,totalLiters,totalEuroBasic;
        private boolean showBasicStrategy;//entscheidet, welche Tankstrategie gezeigt werden soll: die Standart-Strategie oder die "schlaue"

    public boolean showBasicStrategy() {
        return showBasicStrategy;
    }

    public void setShowBasicStrategy(boolean showBasicStrategy) {
        this.showBasicStrategy = showBasicStrategy;
    }
        
    public double getTotalEurosBasic() {
        return totalEuroBasic;
    }

    public void setTotalEurosBasic(double totalEuroBasic) {
        this.totalEuroBasic = totalEuroBasic;
    }

    public double getTotalEuros() {
        return totalEuros;
    }

    public void setTotalEuros(double totalEuros) {
        this.totalEuros = totalEuros;
    }

    public double getTotalKm() {
        return totalKm;
    }

    public void setTotalKm(double totalKm) {
        this.totalKm = totalKm;
    }

    public double getTotalLiters() {
        return totalLiters;
    }

    public void setTotalLiters(double totalLiters) {
        this.totalLiters = totalLiters;
    }
	
	public Route( int tankCapacity ) {
		this.tankCapacity = tankCapacity;
		this.route = new ArrayList<>();
	}
	
	public int getLength() {
		return route.size();
	}
	
	public RefuelStop get(int i) {
		return route.get(i);
	}
	
	public double getTankCapacity() {
		return this.tankCapacity;
	}
	
	public void setTankCapacity(int capacity) {
		this.tankCapacity = capacity;
	}
	
	public void addRouteElement(GasStation station, Date time) {
		route.add(new RefuelStop(station, time));
	}
	
	@Override
	public String toString() {
		return "(Tank: " + this.tankCapacity + " L " + this.route + ")";
	}
	/*
	public List<RefuelStop> getRoute() {
		return route;
	}*/
}
