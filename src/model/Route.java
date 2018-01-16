package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Route implements IPredictionStations {

    private double tankCapacity;
    private String name;
    private List<RefuelStop> route;
    private double totalEuros, totalKm, totalLiters, totalEuroBasic;
    private boolean showBasicStrategy;//entscheidet, welche Tankstrategie gezeigt werden soll: die Standart-Strategie oder die "schlaue"

    
    public Route(String name, int tankCapacity) {
        this.tankCapacity = tankCapacity;
        this.route = new ArrayList<>();
        this.name = name;
    }
    
    public boolean hasPredictions() {
    	boolean res = false;
    	for(RefuelStop rs: route) {
    		res = res | rs.isPredicted(); // sobald mindestens ein Tankstop vorhergesagt ist gibt die Methode true zurück
    	}
    	return res;
    }
    
    public String getName() {
        return name;
    }

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


    public int getLength() {
        if (route == null) {
            return 0;
        }
        return route.size();
    }

    public RefuelStop get(int i) {
        if (i < 0 || i >= route.size()) {
            //System.out.println("TT: " + i + " " + this.getLength());
            return null;
        }
        return route.get(i);
    }

    public double getTankCapacity() {
        return this.tankCapacity;
    }
    
    public Date getPriceKnownUntil() {
    	if(route == null || route.size() == 0) return null;
    	return route.get(0).getTime();
    }

    public void setTankCapacity(int capacity) {
        this.tankCapacity = capacity;
    }

    public void addRouteElement(GasStation station, Date time) {
        route.add(new RefuelStop(station, time));
    }
    
    public Validation getValidation() {
    	Validation res = new Validation();
    	for(RefuelStop r: this.route) {
    		res.add(r.getValidation());
    	}
    	return res;
    }
	
    @Override
	public String getType() {
    	return "Route";
    }

    @Override
    public String toString() {
        return "(Tank: " + this.tankCapacity + " L " + this.route + ")";
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((route == null) ? 0 : route.hashCode());
		long temp;
		temp = Double.doubleToLongBits(tankCapacity);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		Route other = (Route) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (route == null) {
			if (other.route != null)
				return false;
		} else if (!route.equals(other.route))
			return false;
		if (Double.doubleToLongBits(tankCapacity) != Double.doubleToLongBits(other.tankCapacity))
			return false;
		return true;
	}
}
