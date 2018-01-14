package model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PredictionPoints implements IPredictionStations{
    private String name;
    private List<PredictionPoint> predictionPoints;
    private DateFormat dateFormat = new SimpleDateFormat("EE dd.MM.yyyy HH:mm");

    
    public PredictionPoints(String name) {
        this.predictionPoints = new ArrayList<>();
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public ObservableList<PredictionPoint.TableRow> getList() {
    	List<PredictionPoint.TableRow> result = new ArrayList<>();
    	int ctr = 1;
    	for(PredictionPoint p : this.predictionPoints) {
    		result.add(new PredictionPoint.TableRow(
    				ctr++,
    				p,
    				dateFormat.format(p.getPriceKnownUntil()),
    				dateFormat.format(p.getTime()),
    				p.getPredictedPrice(),
    				p.getStation().getHistoricPrice(p.getTime())
    			)
    		);
    	}
    	return FXCollections.observableArrayList(result);
    }

    public int getLength() {
        if (predictionPoints == null) {
            return 0;
        }
        return predictionPoints.size();
    }

    public PredictionPoint get(int i) {
        if (i < 0 || i >= predictionPoints.size()) {
            //System.out.println("TT: " + i + " " + this.getLength());
            return null;
        }
        return predictionPoints.get(i);
    }

    public void addPredictionElement(GasStation station, Date priceKnownUntil, Date predictionTime) {
    	predictionPoints.add(new PredictionPoint(station, priceKnownUntil, predictionTime));
    }
    
    public Validation getValidation() {
    	Validation res = new Validation();
    	for(PredictionPoint p: this.predictionPoints) {
    		res.add(p.getValidation());
    	}
    	return res;
    }
    
    @Override
    public String toString() {
    	return "(" + this.name + ": " + this.predictionPoints + ")";
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((predictionPoints == null) ? 0 : predictionPoints.hashCode());
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
		PredictionPoints other = (PredictionPoints) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (predictionPoints == null) {
			if (other.predictionPoints != null)
				return false;
		} else if (!predictionPoints.equals(other.predictionPoints))
			return false;
		return true;
	}
}
