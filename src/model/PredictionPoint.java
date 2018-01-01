package model;

import java.util.Date;
import java.util.List;

public class PredictionPoint implements IPredictionStation {
	private GasStation station;
	private Date priceKnownUntil, predictionTime;
	private List<Price> predictedPrices;
	private Price predictedPrice;
	
	public PredictionPoint(GasStation station, Date priceKnownUntil, Date predictionTime) {
		this.station = station;
		if (priceKnownUntil.after(predictionTime)) {
			System.err.println("Prediction unnecessary! Price is known for requested prediction date.");
		}
		this.priceKnownUntil = priceKnownUntil;
		this.predictionTime = predictionTime;
	}

	public GasStation getStation() {
		return station;
	}

	public Date getPriceKnownUntil() {
		return priceKnownUntil;
	}

	public Date getTime() {
		return predictionTime;
	}
	
	public int getPredictedPrice(Date d) {
    	int prevPrice = -1;
    	if(predictedPrices == null || predictedPrices.size() == 0) return -1;
		for(Price p: this.predictedPrices) {
			if(p.getTime().after(d)) {
				return prevPrice;
			}
			prevPrice = p.getPrice();
		}
		System.err.println("Could not predict prices for more than 1 month!");
		return -1;
    }
    
    public int getPredictedPrice() {
    	if(predictedPrice == null || !predictedPrice.getTime().equals(this.predictionTime)) {
    		predictedPrice = new Price(this.predictionTime, this.getPredictedPrice(this.predictionTime));
    	}
    	return predictedPrice.getPrice();
    }
    
    public void setPredictedPrices( List<Price> predicted) {
    	this.predictedPrices = predicted;
    }
}
