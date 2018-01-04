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
	
	@Override
	public String toString() {
		return name + " (" + id + ")";
	}
}
