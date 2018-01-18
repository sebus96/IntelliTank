package model;

import java.util.Date;

/**
 * Stellt einen Preis an einem bestimmten Datum dar.
 *
 * @author Sebastian Drath
 *
 */
public class Price {
	private Date time;
	private int price;
	
	/**
	 * Erzeut einen neuen Preis zum Zeitpunkt des übergebenen Datums.
	 *
	 * @param time die Zeit, ab der der Preis gilt
	 * @param price der Preis
	 */
	public Price(Date time, int price) {
		this.time = time;
		this.price = roundPrice(price); // der Preis wird so gerundet, dass die dritte Stelle immer eine 9 enthält
	}
	
	/**
	 * Gibt die Zeit zurück.
	 *
	 * @return die Zeit des Preises
	 */
	public Date getTime() {
		return time;
	}
	
	/**
	 * Gibt den Preis zurück
	 *
	 * @return der Preis
	 */
	public int getPrice() {
		return price;
	}
	
	/**
	 * Rundet den Preis so, dass an der dritten Stelle nach dem Komma immer eine 9 steht. Dies wird gemacht, da
	 * Benzinpreise üblicherweise eine 9 als letzte Stelle aufweisen.
	 *
	 * @param price der ursprüngliche Preis
	 * @return der neue gerundete Preis
	 */
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
