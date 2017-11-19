package model;

import java.util.Date;

public class Price {
	private Date time;
	private int price;
	
	public Price(Date time, int price) {
		this.time = time;
		this.price = price;
	}
	
	public Date getTime() {
		return time;
	}
	
	public int getPrice() {
		return price;
	}
}
