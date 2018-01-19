package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Repräsentation einer Route. Diese enthält eine Liste von Tankstops (siehe {@link model.RefuelStop}) und eine Tankkapazität,
 * die für die Berechnung einer intelligenten Tankstrategie verwendet wird. Die Ergebnisse der Tankstrategie werden
 * innerhalb von dieser Klasse sowie in {@link model.RefuelStop} gespeichert.
 *
 * @author Sebastian Drath
 *
 */
public class Route implements IPredictionStationList {

	/**
	 * Die Tankstrategie.
	 *
	 * @author Sebastian Drath
	 *
	 */
	public enum Strategy{
		/** Einfache Tankstrategie.
		 * Es wird immer getankt, wenn der Tank leer ist oder wenn er nicht bis zum nächsten Stop reicht.
		 * Die Route startet mit leerem Tank. Am Ende der Route wird nur so viel getankt, dass der Tank am Ziel leer ist. */
		BASIC,
		/** Intelligente Tankstrategie.
		 * Es wird so getankt, dass die Ausgaben minimiert werden. Die Route startet und endet mit leerem Tank. */
		SMART
	};
	
    private double tankCapacity;
    private String name;
    private List<RefuelStop> route;
    // Gesamtkosten bzw. Gesamtspritverbrauch für diese Route mit verschiedenen Tankstrategien
    private double totalEuros, totalKm, totalLiters, totalEuroBasic;
    private static Strategy strategy;//entscheidet, welche Tankstrategie gezeigt werden soll: die Standart-Strategie oder die "schlaue"

    /**
     * Erstellt eine neue Route mit dem Namen und der Tankkapazität. Die Tankstrategie wird initial auf smart gesetzt.
     *
     * @param name der Routenname
     * @param tankCapacity die Tankkapazität
     */
    public Route(String name, int tankCapacity) {
        this.tankCapacity = tankCapacity;
        this.route = new ArrayList<>();
        this.name = name;
        strategy = Strategy.SMART;
    }
    
    @Override
	public String getName() {
	    return name;
	}

	@Override
    public boolean hasPredictions() {
    	boolean res = false;
    	for(RefuelStop rs: route) {
    		res = res | rs.isPredicted(); // sobald mindestens ein Tankstop vorhergesagt ist gibt die Methode true zurück
    	}
    	return res;
    }
    
    @Override
	public int getLength() {
	    if (route == null) {
	        return 0;
	    }
	    return route.size();
	}

	@Override
	public RefuelStop get(int i) {
	    if (i < 0 || i >= route.size()) {
	        return null;
	    }
	    return route.get(i);
	}

	/**
	 * Gibt zurück, bis zu welchem Datum die Preise für die Vorhersage dieser Route verwendet werden dürfen.
	 *
	 * @return Datum, bis zu dem die historischen Preise für die Vorhersage verwendet werden
	 */
	public Date getPriceKnownUntil() {
		if(route == null || route.size() == 0) return null;
		return route.get(0).getTime();
	}

	/**
	 * Setzt die Tankkapazität dieser Route.
	 *
	 * @param capacity die Tankkapazität
	 */
	public void setTankCapacity(int capacity) {
	    this.tankCapacity = capacity;
	}

	/**
	 * Gibt die Tankkapazität für diese Route zurück.
	 *
	 * @return Tankkapazität
	 */
	public double getTankCapacity() {
	    return this.tankCapacity;
	}

	/**
	 * Fügt ein neuen Tankstop in diese Route ein.
	 *
	 * @param station die Tankstelle
	 * @param time die Zeit des Tankstops
	 */
	public void addRouteElement(GasStation station, Date time) {
	    route.add(new RefuelStop(station, time));
	}

	/**
     * Setzt die Gesamtkosten für die einfache Tankstrategie.
     *
     * @param totalEuroBasic Gesamtkosten der einfachen Tankstrategie
     */
    public void setTotalCostsBasic(double totalEuroBasic) {
        this.totalEuroBasic = totalEuroBasic;
    }

    /**
     * Gibt die Gesamtkosten der Route für die momentan eingestellte Tankstrategie aus.
     *
     * @return Gesamtkosten
     */
    public double getTotalCosts() {
    	if(strategy == Strategy.BASIC) {
    		return totalEuroBasic;
    	}
    	return totalEuros;
    }


    /**
     * Setzt die Gesamtkosten für die intelligente Tankstrategie.
     *
     * @param totalEuros Gesamtkosten der intelligenten Tankstrategie
     */
    public void setTotalCosts(double totalEuros) {
        this.totalEuros = totalEuros;
    }

    /**
     * Gibt die insgesamt gefahrene Strecke in Kilometern zurück.
     *
     * @return gefahrene Strecke
     */
    public double getTotalDistance() {
        return totalKm;
    }

    /**
     * Setzt die insgesamt gefahrene Strecke in Kilometern.
     *
     * @param totalKm gefahrene Strecke
     */
    public void setTotalDistance(double totalKm) {
        this.totalKm = totalKm;
    }

    /**
     * Gibt die insgesamt verbrauchte Benzinmenge in Litern aus.
     *
     * @return Benzinverbauch in Litern
     */
    public double getTotalFuelConsumption() {
        return totalLiters;
    }

    /**
     * Setzt die insgesamt verbrauchte Benzinmenge in Litern.
     *
     * @param totalLiters Benzinmenge in Litern
     */
    public void setTotalFuelConsumption(double totalLiters) {
        this.totalLiters = totalLiters;
    }

    public static Strategy getStrategy() {
	    return strategy;
	}

	/**
	 * Schaltet zwischen den beiden Stratgien um.
	 */
	public static void switchStrategy() {
	    if(strategy == Strategy.BASIC) {
	    	strategy = Strategy.SMART;
	    } else {
	    	strategy = Strategy.BASIC;
	    }
	}

	@Override
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
