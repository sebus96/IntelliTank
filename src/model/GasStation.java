package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class GasStation {
	private int id;
	private String name, brand, street, location, houseNumber;
	private int postcode;
	private FederalState state;
	private double longitude, latitude; // Laengen- und Breitengrad
	private List<Price> historicPrices;
	
	private Validation validation;
	
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

	public int getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getBrand() {
		return brand;
	}
	
	public FederalState getState() {
		return state;
	}

	public String getStreet() {
		return street;
	}

	public String getLocation() {
		return location;
	}

	public String getHouseNumber() {
		return houseNumber;
	}

	public int getPostcode() {
		return postcode;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}
	
	public int getHistoricPrice(Date d) {
		int prevPrice = -1;
		for(Price p: this.historicPrices) {
			if(p.getTime().after(d)) {
				return prevPrice;
			}
			prevPrice = p.getPrice();
		}
		return -1;//prevPrice;
	}
	
	public int getPriceListSize() {
		return this.historicPrices.size();
	}
	
	public boolean hasPriceList() {
		return this.historicPrices != null && this.historicPrices.size() > 0;
	}
	
	public Price getPriceListElement(int i) {
		if(i < this.historicPrices.size() && i >= 0)
			return this.historicPrices.get(i);
		return null;
	}
	
	public void setPriceList(List<Price> prices) {
		this.historicPrices = prices;
	}
	
	public void setValidation(Validation v) {
		this.validation = v;
	}
	
	public Validation getValidation() {
		return this.validation;
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
