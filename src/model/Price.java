package model;

import java.util.Date;

public class Price {
	private Date time;
	private int price;
	
	public Price(Date time, int price) {
		this.time = time;
		this.price = roundPrice(price);
	}
	
	public Date getTime() {
		return time;
	}
	
	public int getPrice() {
		return price;
	}
	
	public static int roundPrice(int price) {
		return (int)(Math.ceil(( price + 1 ) / 10.0 ) * 10 ) - 1;
	}
}
