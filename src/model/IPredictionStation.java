package model;

import java.util.Date;

import controller.PredictionUnit;

public interface IPredictionStation {
	
	public GasStation getStation();
	
	public Date getTime();
	
	public int getPredictedPrice(Date d);
    
    public int getPredictedPrice();
    
    public int getPredictedPriceListSize();
    
    public Price getPredictedPriceListElement(int index);
    
//    public void setPredictedPrices( List<Price> predicted);
    
    public void setPrediction(PredictionUnit pu);
	
	public boolean isPredicted();
	
	public String toCSVString();
	
	public void setValidation(Validation v);
	
	public Validation getValidation();
}
