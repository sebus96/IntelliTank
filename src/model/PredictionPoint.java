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

/**
 * Repräsentation eines Vorhersagezeitpunkts. Dieser umfasst eine Tankstelle, bei der bis zu einem bestimmten Datum Preise
 * für eine Vorhersage verwendet dürfen, ein Datum für diese Vorhersage sowie ein Vorhersageobjekt imklusive der vorhergesagten
 * Preisliste. Eine Validierung gibt Werte zur Beurteilung der Vorhersage an, wenn eine Vorhersage getätigt wurde und echte
 * Preise als Referenz vorliegen.
 *
 * @author Sebastian Drath
 *
 */
public class PredictionPoint implements IPredictionStation {

    private GasStation station;
    private Date priceKnownUntil, predictionTime;
    private List<Price> predictedPrices;
    private Price predictedPrice;
    private PredictionUnit predictionUnit;
    private Validation validation;

    /**
     * Erstelle ein Voerhersagezeitpunkt-Objekt
     *
     * @param station Die Tankstelle, für die es erstellt werden soll
     * @param priceKnownUntil Die Zeit, bis zu der man die historischen Preise
     * verwenden darf zur Vorhersage
     * @param predictionTime Zeit, für die die Vorhersage gemacht werden soll
     */
    public PredictionPoint(GasStation station, Date priceKnownUntil, Date predictionTime) {
        this.station = station;
        if (priceKnownUntil.after(predictionTime)) {
            System.err.println("Prediction unnecessary! Price is known for requested prediction date.");
        }
        this.priceKnownUntil = priceKnownUntil;
        this.predictionTime = predictionTime;
        this.validation = new Validation();
    }

    @Override
	public GasStation getStation() {
		return station;
	}

    /**
     * Gibt das Datum zurück, bis zu dem die Preise an dieser Tankstelle als bekannt angenommen werden können.
     *
     * @return Datum bis zu dem Preise bekannt sind
     */
	public Date getPriceKnownUntil() {
		return priceKnownUntil;
	}

    @Override
	public void setPrediction(PredictionUnit pu) {
		this.predictionUnit = pu;
		this.predictedPrices = this.predictionUnit.testAndSetHourSteps();  // setzt die vorhergesagten Preise für 5 Wochen
		this.predictedPrice = null; // zurücksetzen des gespeicherten Preises, da eine neue Vorhersage hinzugefügt wurde
	}

    @Override
	public boolean isPredicted() {
		return predictionUnit != null;
	}

    @Override
	public Date getTime() {
		return predictionTime;
	}

    /**
     * Gibt den vorhergesagten Preis zurück für ein bestimmtes Datum
     * @param d Datum für den vorhergesagten Preis
     * @return Der vorhergesagte Preis
     */
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
    
    @Override
    public int getPredictedPriceListSize(){
    	return this.predictedPrices.size();
    }

    /**
     * Gibt ein Preislisten-Element aus der Liste zurück
     * @param index index des PReiselelemts in der Liste
     * @return Preiselement, das ausgewählt wurde
     */
    @Override
	public Price getPredictedPriceListElement(int index) {
		if(index < this.predictedPrices.size() && index >= 0)
			return this.predictedPrices.get(index);
		return null;
	}
    
    @Override
    public String toCSVString() {
    	DateFormat df = CSVManager.getDateFormat();
    	return df.format(this.priceKnownUntil) + ";"
    			+ df.format(this.predictionTime) + ";"
    			+ this.station.getID() + ";"
    			+ this.getPredictedPrice();
    }
	
    @Override
	public void setValidation(Validation v) {
		this.validation = v;
	}
	
    @Override
	public Validation getValidation() {
		return this.validation;
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

	/**
	 * Klasse für die Anzeige als Zeile innerhalb einer Tabelle in PredictionPointView.
	 *
	 * @author Sebastian Drath
	 *
	 */
	public static class TableRow{
		private PredictionPoint predictionPoint;
		
		/**
		 * Erstellt eine neue Zeile für eine Tabelle
		 *
		 * @param id Zeilen ID
		 * @param pp PredictionPoint Objekt
		 * @param knownTime bekannte Zeit
		 * @param predictionTime Vorhersagezeit
		 * @param price vorhergesagter Preis
		 * @param realPrice echter Preis
		 */
		public TableRow(int id, PredictionPoint pp, String knownTime, String predictionTime, double price, double realPrice) {
			this.predictionPoint = pp;
			idProperty().set(id);
			stationProperty().set(pp.getStation().getName());
			knownTimeProperty().set(knownTime);
			predictionTimeProperty().set(predictionTime);
			priceProperty().set(price/1000);
			realPriceProperty().set(realPrice/1000);
		}
		
		/**
		 * Gibt den Vorhersagezeitpunkt zurück.
		 *
		 * @return Vorhersagezeitpunkt dieser Zeile
		 */
		public PredictionPoint getPredictionPoint(){
			return predictionPoint;
		}
		
	    private IntegerProperty id;
	    /**
	     * Property für die ID einer Zeile
	     *
	     * @return ID-Property
	     */
	    public IntegerProperty idProperty() { 
	        if (id == null) id = new SimpleIntegerProperty(this, "id");
	        return id;
	    }
	    
		private StringProperty station;
		/**
		 * Property für den Tankstellennamen.
		 *
		 * @return Tankstellennamen-Property 
		 */
	    public StringProperty stationProperty() { 
	        if (station == null) station = new SimpleStringProperty(this, "station");
	        return station;
	    }

		private StringProperty knownTime;
		/**
		 * Property für die Zeit, bis zu der historische Daten für die Vorhersage verwendet werden.
		 *
		 * @return Property der bekannten Zeit
		 */
	    public StringProperty knownTimeProperty() { 
	        if (knownTime == null) knownTime = new SimpleStringProperty(this, "knownTime");
	        return knownTime;
	    }

		private StringProperty predictionTime;
		/**
		 * Property für die Vorhersagezeit.
		 *
		 * @return Vorhersagezeit-Property
		 */
	    public StringProperty predictionTimeProperty() { 
	        if (predictionTime == null) predictionTime = new SimpleStringProperty(this, "predictionTime");
	        return predictionTime;
	    }
		
		private DoubleProperty price;
		/**
		 * Property für den vorhergesagten Preis.
		 *
		 * @return Property des vorhergesagten Preises
		 */
	    public DoubleProperty priceProperty() { 
	        if (price == null) price = new SimpleDoubleProperty(this, "price");
	        return price;
	    }
		
		private DoubleProperty realPrice;
		/**
		 * Property für den echten Preis. Wenn kein echter Preis vorhanden ist, wird -1 gesetzt.
		 *
		 * @return Property des echten Preises
		 */
	    public DoubleProperty realPriceProperty() { 
	        if (realPrice == null) realPrice = new SimpleDoubleProperty(this, "realPrice");
	        return realPrice;
	    }
	};
}
