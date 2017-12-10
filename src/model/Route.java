package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Route {
	private double tankCapacity;
	private List<RefuelStop> route;
        private double totalEuros,totalKm,totalLiters,totalEuroBasic;

    public double getTotalEuroBasic() {
        return totalEuroBasic;
    }

    public void setTotalEuroBasic(double totalEuroBasic) {
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
	/*
	public List<RefuelStop> getRoute() {
		return route;
	}*/
}
