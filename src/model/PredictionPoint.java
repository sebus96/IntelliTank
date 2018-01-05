package model;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import controller.PredictionUnit;
import io.CSVManager;
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
	private PredictionUnit predictionUnit;
	
	public PredictionPoint(GasStation station, Date priceKnownUntil, Date predictionTime) {
		this.station = station;
		if (priceKnownUntil.after(predictionTime)) {
			System.err.println("Prediction unnecessary! Price is known for requested prediction date.");
		}
		this.priceKnownUntil = priceKnownUntil;
		this.predictionTime = predictionTime;
	}

    @Override
	public GasStation getStation() {
		return station;
	}

	public Date getPriceKnownUntil() {
		return priceKnownUntil;
	}

    @Override
	public void setPrediction(PredictionUnit pu) {
		this.predictionUnit = pu;
		this.predictedPrices = this.predictionUnit.testAndSetHourSteps();
	}

    @Override
	public boolean isPredicted() {
		return predictionUnit != null;
	}

    @Override
	public Date getTime() {
		return predictionTime;
	}

    @Override
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

    @Override
    public int getPredictedPrice() {
    	if(predictedPrice == null || !predictedPrice.getTime().equals(this.predictionTime)) {
    		predictedPrice = new Price(this.predictionTime, this.getPredictedPrice(this.predictionTime));
    	}
    	return predictedPrice.getPrice();
    }
    
//    public void setPredictedPrices( List<Price> predicted) {
//    	this.predictedPrices = predicted;
//    }
    
    @Override
    public String toCSVString() {
    	DateFormat df = CSVManager.getDateFormat();
    	return df.format(this.priceKnownUntil) + ";"
    			+ df.format(this.predictionTime) + ";"
    			+ this.station.getID() + ";"
    			+ this.getPredictedPrice();
    }
    
    @Override
    public String toString() {
    	return "(" + this.station + ": " + this.predictionTime + ")\n";
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((predictedPrice == null) ? 0 : predictedPrice.hashCode());
		result = prime * result + ((predictionTime == null) ? 0 : predictionTime.hashCode());
		result = prime * result + ((priceKnownUntil == null) ? 0 : priceKnownUntil.hashCode());
		result = prime * result + ((station == null) ? 0 : station.hashCode());
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
		PredictionPoint other = (PredictionPoint) obj;
		if (predictionTime == null) {
			if (other.predictionTime != null)
				return false;
		} else if (!predictionTime.equals(other.predictionTime))
			return false;
		if (priceKnownUntil == null) {
			if (other.priceKnownUntil != null)
				return false;
		} else if (!priceKnownUntil.equals(other.priceKnownUntil))
			return false;
		if (station == null) {
			if (other.station != null)
				return false;
		} else if (!station.equals(other.station))
			return false;
		return true;
	}

	public static class TableRow{
		public TableRow(int id, String station, String knownTime, String predictionTime, double price, double realPrice) {
			idProperty().set(id);
			stationProperty().set(station);
			knownTimeProperty().set(knownTime);
			predictionTimeProperty().set(predictionTime);
			priceProperty().set(price/1000);
			realPriceProperty().set(realPrice/1000);
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
