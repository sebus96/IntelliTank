package view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.GasStation;
import model.Price;

public class PriceDiagram {
	
	private GasStation gs;
	
	public PriceDiagram(GasStation gs) {
		this.gs = gs;
	}

	public void generateDiagramm() {
		double xMin = gs.getPriceListElement(0).getTime().getTime();
		double xMax = gs.getPriceListElement(gs.getPriceListSize()-1).getTime().getTime();
		double xSteps = 1000*60*60*24*30.5;//2635200000.0; // one month in milliseconds
		NumberAxis xAxis = new NumberAxis(xMin, xMax, xSteps);
		NumberAxis yAxis = new NumberAxis(1000.0, 2000.0, 100.0);
	    xAxis.setTickLabelFormatter(new StringConverter<Number>(){

			@Override
			public Number fromString(String s) {
				return Long.parseLong(s);
			}

			@Override
			public String toString(Number n) {
				SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
				return sdf.format(new Date(n.longValue()));
			}
	    	
	    });
	    xAxis.setTickLabelRotation(90);
	    xAxis.setLabel("Datum");
	    yAxis.setLabel("Preis");
	    final LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);
        lineChart.setTitle("Preisentwicklung");
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(gs.getName());
        Stage stage = new Stage();
        Calendar c = Calendar.getInstance();
    	c.setTime(gs.getPriceListElement(0).getTime());
        c.set(Calendar.HOUR_OF_DAY, 12);
        c.set(Calendar.MINUTE, 0);
        int sum = 0;
        int ctr = 0;
        Price lastPrice = gs.getPriceListElement(0);
        for(int i = 0; i < gs.getPriceListSize(); i++) {
        	Price p = gs.getPriceListElement(i);
        	long timeBetween = p.getTime().getTime() - lastPrice.getTime().getTime();
        	timeBetween /= 1000*60*60;
        	sum += lastPrice.getPrice()*timeBetween;
        	ctr += timeBetween;
        	if(p.getTime().after(c.getTime())) {
	        	c.add(Calendar.WEEK_OF_YEAR, 1);
	        	if(ctr > 0) series.getData().add(new XYChart.Data<Number, Number>(p.getTime().getTime(), sum/ctr));
	        	sum = 0;
	        	ctr = 0;
        	}
        	lastPrice = p;
        }
        Scene scene  = new Scene(lineChart, 640, 480);
        lineChart.getData().add(series);
        Image icon = new Image("/img/gas-station.png");
        stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.show();
	}    
}