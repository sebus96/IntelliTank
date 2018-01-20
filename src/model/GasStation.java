package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Repräsentation einer Tankstelle.
 * 
 * @author Sebastian Drath
 *
 */
public class GasStation implements Serializable {
	private static final long serialVersionUID = 1062502205845799620L;
	private int id;
	private String name, brand, street, location, houseNumber;
	private int postcode;
	private FederalState state;
	private double longitude, latitude; // Laengen- und Breitengrad
	private transient List<Price> historicPrices; // Wird beim serialisieren nicht mitgespeichert
	
	/**
	 * Generiert eine Tankstelle mit den übergebenen Attributen.
	 * 
	 * @param id die eindeutige ID einer Tankstelle
	 * @param name der Name einer Tankstelle
	 * @param brand der Name der Marke der Tankstelle
	 * @param street die Straße in der die Tankstelle liegt
	 * @param houseNumber die Hausnummer der Tankstelle
	 * @param postcode die Postleitzahl der Tankstelle
	 * @param state das Kürzel des Bundeslandes in dem die Tankstelle liegt
	 * @param location der Name des Ortes in dem die Tankstelle liegt
	 * @param longitude Längengrad der Tankstelle
	 * @param latitude Breitengrad der Tankstelle
	 */
	public GasStation(int id, String name, String brand, String street, String houseNumber, int postcode, FederalState state, String location,
			double longitude, double latitude) {
		this.id = id;
		this.name = name;
		this.brand = brand;
		this.street = street;
		this.houseNumber = houseNumber;
		this.postcode = postcode;
		this.location = location;
		this.latitude = latitude; // Breite
		this.longitude = longitude; // Laenge
		this.historicPrices = new ArrayList<Price>();
		this.state = state;
	}
	
	/**
	 * Gibt die ID zurück.
	 * 
	 * @return Tankstellen ID
	 */
	public int getID() {
		return id;
	}

	/**
	 * Gibt den Namen zurück.
	 * 
	 * @return Tankstellenname
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gibt die Marke zurück.
	 * 
	 * @return Tankstellenmarke
	 */
	public String getBrand() {
		return brand;
	}
	
	/**
	 * Gibt das Bundesland zurück.
	 * 
	 * @return Bundesland
	 */
	public FederalState getState() {
		return state;
	}
	
	/**
	 * Gibt den Straßennamen zurück.
	 *
	 * @return Straßenname
	 */
	public String getStreet() {
		return street;
	}

	/**
	 * Gibt den Ort zurück.
	 *
	 * @return Ort
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Gibt die Hausnummer zurück.
	 *
	 * @return Hausnummer
	 */
	public String getHouseNumber() {
		return houseNumber;
	}

	/**
	 * Gibt die Postleitzahl zurück.
	 *
	 * @return Postleitzahl
	 */
	public int getPostcode() {
		return postcode;
	}
	
	/**
	 * Bestimmt den Preis an dem übergebenen Datum mit Hilfe der historischen Daten.
	 * 
	 * Es wird immer der Preis der letzen Änderung vor dem angefragten Datum ausgegeben.
	 *
	 * @param date Datum zu dem ein Preis angefordert wird
	 * @return Gibt den Preis zu der Zeit zurück, -1, falls kein Preis gefunden wurde
	 */
	public int getHistoricPrice(Date date) {
		int prevPrice = -1;
		for(Price p: this.historicPrices) {
			if(p.getTime().after(date)) {
				return prevPrice;
			}
			prevPrice = p.getPrice();
		}
		return -1;//prevPrice;
	}
	
	/**
	 * Gibt die Größe der importierten historischen Preisliste zurück.
	 *
	 * @return Preislistengröße
	 */
	public int getPriceListSize() {
		return this.historicPrices.size();
	}
	
	/**
	 * Überprüft, ob Preise importiert wurden und gibt den Status zurück.
	 *
	 * @return true, wenn importierte Preis vorliegen, false ansonsten
	 */
	public boolean hasPriceList() {
		return this.historicPrices != null && this.historicPrices.size() > 0;
	}
	
	/**
	 * Gibt den Preislisteneintrag an der übergebenen Position zurück.
	 *
	 * @param index Index des Preislisteneintrags
	 * @return Gibt den Preis zurück, oder null wenn dieses Element nicht existiert
	 */
	public Price getPriceListElement(int index) {
		if(index < this.historicPrices.size() && index >= 0)
			return this.historicPrices.get(index);
		return null;
	}
	
	/**
     * Berechnet den Abstand in km zwischen 2 Tankstellen anhand ihrer Längen- und
     * Breitengrade. Wenn die übergebene Tankstelle null ist wird 0 zurückgegeben.
     *
     * @param station die Tankstelle, zu der der Abstand berechnet werden soll
     * @return der Abstand zwischen den Tankstellen in km
     */
    public double getDistance(GasStation station) {
    	if(station == null) return 0;
        double latitudeA = Math.toRadians(this.latitude);
        double longitudeA = Math.toRadians(this.longitude);
        double latitudeB = Math.toRadians(station.latitude);
        double longitudeB = Math.toRadians(station.longitude);
        double dist = 6378.388 * Math.acos((Math.sin(latitudeA) * Math.sin(latitudeB)) + (Math.cos(latitudeA) * Math.cos(latitudeB) * Math.cos(longitudeB - longitudeA)));
        //System.out.println("# " + Math.ceil(Math.pow(10, 10) * dist)/Math.pow(10, 10));
        return dist;
    }
	
	/**
	 * Setzt die Liste der historischen Preis für diese Tankstelle
	 *
	 * @param prices Liste der historischen Preise
	 */
	public void setPriceList(List<Price> prices) {
		this.historicPrices = prices;
	}
	
	@Override
	public String toString() {
		return name + " (" + id + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((brand == null) ? 0 : brand.hashCode());
		result = prime * result + ((houseNumber == null) ? 0 : houseNumber.hashCode());
		result = prime * result + id;
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + postcode;
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((street == null) ? 0 : street.hashCode());
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
		GasStation other = (GasStation) obj;
		if (brand == null) {
			if (other.brand != null)
				return false;
		} else if (!brand.equals(other.brand))
			return false;
		if (houseNumber == null) {
			if (other.houseNumber != null)
				return false;
		} else if (!houseNumber.equals(other.houseNumber))
			return false;
		if (id != other.id)
			return false;
		if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (postcode != other.postcode)
			return false;
		if (state != other.state)
			return false;
		if (street == null) {
			if (other.street != null)
				return false;
		} else if (!street.equals(other.street))
			return false;
		return true;
	}
}
