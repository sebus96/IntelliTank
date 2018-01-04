package model;

import java.util.Date;
import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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
    
    @Override
    public String toString() {
    	return "(" + this.station + ": " + this.predictionTime + ")\n";
    }

	public static class TableRow{
		public TableRow(int id, String station, String knownTime, String predictionTime, double price, double realPrice) {
			idProperty().set(id);
			stationProperty().set(station);
			knownTimeProperty().set(knownTime);
			predictionTimeProperty().set(predictionTime);
			priceProperty().set(price/1000);
			realPriceProperty().set(realPrice/1000);
			System.out.println("ff: " + realPriceProperty().get());
		}
		
	    private IntegerProperty id;
	    public IntegerProperty idProperty() { 
	        if (id == null) id = new SimpleIntegerProperty(this, "id");
	        return id;
	    }
	    
		private StringProperty station;
	    public StringProperty stationProperty() { 
	        if (station == null) station = new SimpleStringProperty(this, "station");
	        return station;
	    }

		private StringProperty knownTime;
	    public StringProperty knownTimeProperty() { 
	        if (knownTime == null) knownTime = new SimpleStringProperty(this, "knownTime");
	        return knownTime;
	    }

		private StringProperty predictionTime;
	    public StringProperty predictionTimeProperty() { 
	        if (predictionTime == null) predictionTime = new SimpleStringProperty(this, "predictionTime");
	        return predictionTime;
	    }
		
		private DoubleProperty price;
	    public DoubleProperty priceProperty() { 
	        if (price == null) price = new SimpleDoubleProperty(this, "price");
	        return price;
	    }
		
		private DoubleProperty realPrice;
	    public DoubleProperty realPriceProperty() { 
	        if (realPrice == null) realPrice = new SimpleDoubleProperty(this, "realPrice");
	        return realPrice;
	    }
	};
}
