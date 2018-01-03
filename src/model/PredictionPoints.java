package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PredictionPoints implements IPredictionStations{
    private String name;
    private List<PredictionPoint> predictionPoints;

    
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
    				p.getStation().getName(),
    				p.getPriceKnownUntil().toString(),
    				p.getTime().toString(),
    				p.getPredictedPrice()
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
    
    @Override
    public String toString() {
    	return "(" + this.name + ": " + this.predictionPoints + ")";
    }
}
