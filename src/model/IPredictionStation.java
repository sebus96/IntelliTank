package model;

import java.util.Date;
import java.util.List;

public interface IPredictionStation {
	
	public GasStation getStation();
	
	public Date getTime();
	
	public int getPredictedPrice(Date d);
    
    public int getPredictedPrice();
    
    public void setPredictedPrices( List<Price> predicted);
}
