package view;

import java.util.Date;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import model.GasStation;

public class PreisDiagramm implements Runnable{
	
//	public PreisDiagramm(Parent root) {
//		super(root);
//	}
	private GasStation gs;
	
	public PreisDiagramm(GasStation gs) {
		this.gs = gs;
	}

	public void generateDiagramm() {
		final Axis xAxis = new NumberAxis();
	    final Axis yAxis = new NumberAxis();
	    xAxis.setLabel("Datum");
	    yAxis.setLabel("Preis");
	    final LineChart<Date,Number> lineChart = 
                new LineChart<Date,Number>(xAxis,yAxis);
        lineChart.setTitle("Preisentwicklung");
        XYChart.Series series = new XYChart.Series();
        series.setName("Aktuelle Tankstelle");
//        Date date = new Date('DMy');
//        gs.getHistoricPrice(date);
                
        Stage stage = new Stage();
        for(int i= 0; i < gs.getPriceListSize(); i++) {
        	series.getData().add(new XYChart.Data(gs.getPriceListElement(i).getTime().getDate(), gs.getPriceListElement(i).getPrice()));
        }
        Scene scene  = new Scene(lineChart, 640, 480);
        lineChart.getData().setAll(series);
        stage.setScene(scene);
        stage.show();
	}

	@Override
	public void run() {
		generateDiagramm();
	}    
}