package view;

import java.util.Date;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import model.GasStation;

public class PreisDiagramm extends Scene {
	
	public PreisDiagramm(Parent root) {
		super(root);
	}

	public void generateDiagramm(GasStation gs) {
		final Axis xAxis = new NumberAxis();
	    final Axis yAxis = new NumberAxis();
	    xAxis.setLabel("Datum");
	    yAxis.setLabel("Preis");
	    final LineChart<Date,Number> lineChart = 
                new LineChart<Date,Number>(xAxis,yAxis);
        lineChart.setTitle("Preisentwicklung");
        XYChart.Series series = new XYChart.Series();
        series.setName("Aktuelle Tankstelle");
        
	}    
}