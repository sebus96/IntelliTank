package view;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.GasStation;

public class PriceDiagram {
	
	private GasStation gs;
	private String gsName;
	
	public PriceDiagram(GasStation gs, String gsName) {
		this.gs = gs;
		this.gsName = gsName;
	}

	public void generateDiagramm() {
		CategoryAxis xAxis = new CategoryAxis();
	    NumberAxis yAxis = new NumberAxis(1000.0, 2000.0, 100.0);
	    xAxis.setLabel("Datum");
	    yAxis.setLabel("Preis");
	    final LineChart<String,Number> lineChart = 
                new LineChart<String,Number>(xAxis,yAxis);
        lineChart.setTitle("Preisentwicklung");
        XYChart.Series series = new XYChart.Series();
        series.setName(gsName);
        Stage stage = new Stage();
        for(int i= 0; i < gs.getPriceListSize(); i++) {
        	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        	Date currentDate = gs.getPriceListElement(i).getTime(); 
            String date = sdf.format(gs.getPriceListElement(i).getTime());
        	series.getData().add(new XYChart.Data<String, Number>(sdf.format(currentDate), gs.getPriceListElement(i).getPrice()));
        	System.out.println("TEST: Datum: " + sdf.format(currentDate));
//        	System.out.println(gs.getPriceListElement(i).getTime());
        }
        Scene scene  = new Scene(lineChart, 640, 480);
        lineChart.getData().setAll(series);
        Image icon = new Image("/img/gas-station.png");
        stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.show();
	}    
}