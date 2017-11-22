package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GasStation {
	private int id;
	private String name, brand, street, location, houseNumber;
	private int postcode;
	private double longitude, latitude; // Laengen- und Breitengrad
	private List<Price> historicPrices;
	private List<Price> projectedPrices;
	
	public GasStation(int id, String name, String brand, String street, String houseNumber, int postcode, String location,
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
		this.projectedPrices = new ArrayList<Price>();
	}
	
//	public GasStation(Map<String, String> textAttributes, Map<String, Integer> numberAttributes, Map<String, Double> positionAttributes){
//		this.id = numberAttributes.get("ID");
//		this.name = textAttributes.get("ID");
//		this.brand = textAttributes.get("ID");
//		this.street = textAttributes.get("ID");
//		this.houseNumber = numberAttributes.get("ID");
//		this.postcode = numberAttributes.get("ID");
//		this.location = textAttributes.get("ID");
//		this.latitude = positionAttributes.get("ID"); // Breite
//		this.longitude = positionAttributes.get("ID"); // Laenge
//		this.historicPrices = new ArrayList<Price>();
//	}

	public int getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getBrand() {
		return brand;
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
	
	/**
	 * 
	 * 
	 * @param d Date for which the price is needed
	 * @return the price at the specified date
	 */
	public int getProjectedPrice(Date d) {
		int prevPrice = -1;
		for(Price p: this.projectedPrices) {
			if(p.getTime().after(d)) {
				return prevPrice;
			}
			prevPrice = p.getPrice();
			
		}
		return prevPrice;
	}
	
	public int getHistoricPrice(Date d) {
		int prevPrice = -1;
		for(Price p: this.historicPrices) {
			if(p.getTime().after(d)) {
				return prevPrice;
			}
			prevPrice = p.getPrice();
			
		}
		return prevPrice;
	}

    /**
     * @return the historicPrices
     *
    public List<Price> getHistoricPrices() {
        return historicPrices;
    }*/

    /**
     * @return the projectedPrices
     *
    public List<Price> getProjectedPrices() {
        return projectedPrices;
    }*/
}
