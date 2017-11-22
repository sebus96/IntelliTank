package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Route {
	private double tankCapacity;
	private List<RefuelStop> route;
	
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
