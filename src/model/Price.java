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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + price;
		result = prime * result + ((time == null) ? 0 : time.hashCode());
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
		Price other = (Price) obj;
		if (price != other.price)
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		return true;
	}
}
