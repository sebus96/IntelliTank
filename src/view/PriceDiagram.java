package view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.image.Image;
import javafx.scene.input.ContextMenuEvent;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.GasStation;
import model.IPredictionStation;
import model.Price;

public class PriceDiagram {

    private static List<IPredictionStation> gasStations = new ArrayList<>();
    private static Stage priceStage;
    private static boolean showHistoric = true;

    public static void displayGasStation(IPredictionStation gs) {
    	if(!showHistoric && !gs.isPredicted()) {
    		PopupBox.displayWarning(202);
    		return;
    	}
        if (priceStage == null) {
            gasStations = new ArrayList<>();
            priceStage = new Stage();
            priceStage.setTitle("Preisdiagramm");
            priceStage.setOnCloseRequest(event -> {
	            priceStage = null;
	            gasStations = new ArrayList<>();
            }); 
        }
        //Verhindert, dass die Tankstellen doppelt angezeigt werden. Die alte wird entfernt, damit die zuletzt angeklickte im Vordergrund steht
        if(gasStations.contains(gs))
            gasStations.remove(gs);
        gasStations.add(gs);
        generateDiagram();
        if(!priceStage.isShowing()) {
            priceStage.show();
        }
    }

    public static void generateDiagram() {
    	double xMin = Double.MAX_VALUE;
    	double xMax = Double.MIN_VALUE;
    	boolean unPredictedStations = false;
        
        //Schaut, wie breit der Wertebereich in X-Richtung sein muss(von welchem Datum bis zu welchem) um alle Tankstellenpreise im Graphen darstellen zu können
    	for(IPredictionStation ps : gasStations) {
        	if(showHistoric) {
	        	GasStation g = ps.getStation();
	            if(g.getPriceListElement(0).getTime().getTime() < xMin) {
	                xMin = g.getPriceListElement(0).getTime().getTime();
	            }
	            if(g.getPriceListElement(g.getPriceListSize()-1).getTime().getTime() > xMax) {
	                xMax = g.getPriceListElement(g.getPriceListSize()-1).getTime().getTime();
	            }
        	} else {
        		if(!ps.isPredicted()) {
        			unPredictedStations = true;
                	continue;
                }
                
	            if(ps.getPredictedPriceListElement(0).getTime().getTime() < xMin) {
	            	xMin = ps.getPredictedPriceListElement(0).getTime().getTime();
	            }
	            if(ps.getPredictedPriceListElement(ps.getPredictedPriceListSize()-1).getTime().getTime() > xMax) {
	            	xMax = ps.getPredictedPriceListElement(ps.getPredictedPriceListSize()-1).getTime().getTime();
	            }
        	}
        }
    	Date min = new Date(new Double(xMin).longValue());
    	Date max = new Date(new Double(xMax).longValue());
    	Calendar c_min = Calendar.getInstance();
    	c_min.setTime(min);
    	Calendar c_max = Calendar.getInstance();
    	c_max.setTime(max);
        double xSteps;
        if( showHistoric ){
        	xSteps = 1000 * 60 * 60 * 24 * 30.5;//2635200000.0; // one month in milliseconds
        	// auf vollen Monat abrunden
        	c_min.set(Calendar.DAY_OF_MONTH, 1);
        	c_min.set(Calendar.HOUR_OF_DAY, 0);
        	c_min.set(Calendar.MINUTE, 0);
        	
        	// auf vollen Monat aufrunden
        	c_max.set(Calendar.DAY_OF_MONTH, 2);
        	c_max.set(Calendar.HOUR_OF_DAY, 0);
        	c_max.set(Calendar.MINUTE, 0);
        	c_max.add(Calendar.MONTH, 1);
        } else {
        	xSteps = 1000 * 60 * 60 * 24; // one day in milliseconds
        	// auf vollen Tag abrunden
        	c_min.set(Calendar.HOUR_OF_DAY, 0);
        	c_min.set(Calendar.MINUTE, 0);
        	
        	// auf vollen Tag aufrunden
        	c_max.set(Calendar.HOUR_OF_DAY, 0);
        	c_max.set(Calendar.MINUTE, 0);
        	c_max.add(Calendar.DAY_OF_MONTH, 1);
        }
        xMin = c_min.getTime().getTime();
        xMax = c_max.getTime().getTime();
        NumberAxis xAxis = new NumberAxis(xMin, xMax, xSteps);
        NumberAxis yAxis = new NumberAxis(1000.0, 2000.0, 100.0);
        xAxis.setTickLabelFormatter(new StringConverter<Number>() {

            @Override
            public Number fromString(String s) {
                return Long.parseLong(s);
            }

            @Override
            public String toString(Number n) {
            	String format;
            	Date d = new Date(n.longValue());
            	if(showHistoric) {
            		format = "MM/yyyy";
            		// Korrektur der Labels zu vollen Monaten (Abweichung durch Annahme von 30.5 Tagen pro Monat)
            		Calendar c = Calendar.getInstance();
            		c.setTime(d);
            		if(c.get(Calendar.DAY_OF_MONTH) > 15) {
            			c.add(Calendar.MONTH, 1);
            		}
        			c.set(Calendar.DAY_OF_MONTH, 1);
        			c.set(Calendar.HOUR_OF_DAY, 0);
        			c.set(Calendar.MINUTE, 0);
        			d = c.getTime();
            	}
            	else format = "E dd.MM.yy";
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                return sdf.format(d);
            }

        });
        xAxis.setTickLabelRotation(90);
        xAxis.setLabel("Datum");
        yAxis.setLabel("Preis");
        final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);
        lineChart.setTitle("Preisentwicklung");
        Scene scene = new Scene(lineChart, 640, 600);
        setContextMenu(scene);
        for (IPredictionStation ps : gasStations) {
            if(showHistoric) addSeries(lineChart, ps.getStation());
            else addPredictedSeries(lineChart, ps);
        }
        
    	if(unPredictedStations) {
	    	if(gasStations.size() == 1) PopupBox.displayWarning(202);
	    	else PopupBox.displayWarning(203);
    	}

        Image icon = new Image("/img/gas-station.png");
        priceStage.getIcons().add(icon);
        priceStage.setScene(scene);
    }

    private static void addSeries(LineChart<Number, Number> lc ,GasStation gs) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(gs.getName());
        Calendar c = Calendar.getInstance();
        c.setTime(gs.getPriceListElement(0).getTime());
        c.set(Calendar.HOUR_OF_DAY, 12);
        c.set(Calendar.MINUTE, 0);
        int sum = 0;
        int ctr = 0;
        Price lastPrice = gs.getPriceListElement(0);
        for (int i = 0; i < gs.getPriceListSize(); i++) {
            Price p = gs.getPriceListElement(i);
            long timeBetween = p.getTime().getTime() - lastPrice.getTime().getTime();
            timeBetween /= 1000 * 60 * 60;
            sum += lastPrice.getPrice() * timeBetween;
            ctr += timeBetween;
            if (p.getTime().after(c.getTime())) {
            	while(p.getTime().after(c.getTime())) // wenn die Lücke zwischen zwei Preisen größer als eine Woche ist muss der Kalender um mehr als 1 Woche weitergesetzt werden
            		c.add(Calendar.WEEK_OF_YEAR, 1);
                if (ctr > 0) {
                    series.getData().add(new XYChart.Data<Number, Number>(p.getTime().getTime(), sum / ctr));
                }
                sum = 0;
                ctr = 0;
            }
            lastPrice = p;
        }
        lc.getData().add(series);
    }

    private static void addPredictedSeries(LineChart<Number, Number> lc ,IPredictionStation ps) {
        if(!ps.isPredicted()) return;
    	XYChart.Series<Number, Number> seriesPred = new XYChart.Series<>();
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        seriesPred.setName(ps.getStation().getName() + " Vorhersage");
        series.setName(ps.getStation().getName());
        for (int i = 0; i < ps.getPredictedPriceListSize(); i++) {
            Price p = ps.getPredictedPriceListElement(i);
            seriesPred.getData().add(new XYChart.Data<Number, Number>(p.getTime().getTime(), p.getPrice()));
            series.getData().add(new XYChart.Data<Number, Number>(p.getTime().getTime(), ps.getStation().getHistoricPrice(p.getTime())));
        }
        lc.getData().add(seriesPred);
        lc.getData().add(series);
    }

    /*private static void addPredictedSeries(LineChart<Number, Number> lc ,IPredictionStation ps) {
        if(!ps.isPredicted()) return;
    	XYChart.Series<Number, Number> seriesPred = new XYChart.Series<>();
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        seriesPred.setName(ps.getStation().getName() + " Vorhersage");
        series.setName(ps.getStation().getName());
        
        Calendar c = Calendar.getInstance();
        c.setTime(ps.getPredictedPriceListElement(0).getTime());
        c.set(Calendar.HOUR_OF_DAY, 12);
        c.set(Calendar.MINUTE, 0);
        int sum = 0;
        int ctr = 0;
        Price lastPrice = ps.getPredictedPriceListElement(0);
        for (int i = 0; i < ps.getPredictedPriceListSize(); i++) {
            Price p = ps.getPredictedPriceListElement(i);
            long timeBetween = p.getTime().getTime() - lastPrice.getTime().getTime();
            timeBetween /= 1000 * 60 * 60;
            sum += lastPrice.getPrice() * timeBetween;
            ctr += timeBetween;
            if (p.getTime().after(c.getTime())) {
            	while(p.getTime().after(c.getTime())) // wenn die Lücke zwischen zwei Preisen größer als ein Tag ist muss der Kalender um mehr als 1 Tag weitergesetzt werden
            		c.add(Calendar.DAY_OF_YEAR, 1);
                if (ctr > 0) {
                    seriesPred.getData().add(new XYChart.Data<Number, Number>(p.getTime().getTime(), sum / ctr));
                }
                sum = 0;
                ctr = 0;
            }
            lastPrice = p;
        }
        
        c.setTime(ps.getStation().getPriceListElement(0).getTime());
        c.set(Calendar.HOUR_OF_DAY, 12);
        c.set(Calendar.MINUTE, 0);
        sum = 0;
        ctr = 0;
        lastPrice = ps.getStation().getPriceListElement(0);
        for (int i = 0; i < ps.getStation().getPriceListSize(); i++) {
            Price p = ps.getStation().getPriceListElement(i);
            long timeBetween = p.getTime().getTime() - lastPrice.getTime().getTime();
            timeBetween /= 1000 * 60 * 60;
            sum += lastPrice.getPrice() * timeBetween;
            ctr += timeBetween;
            if (p.getTime().after(c.getTime())) {
            	while(p.getTime().after(c.getTime())) // wenn die Lücke zwischen zwei Preisen größer als ein Tag ist muss der Kalender um mehr als 1 Tag weitergesetzt werden
            		c.add(Calendar.DAY_OF_YEAR, 1);
                if (ctr > 0) {
                    series.getData().add(new XYChart.Data<Number, Number>(p.getTime().getTime(), sum / ctr));
                }
                sum = 0;
                ctr = 0;
            }
            lastPrice = p;
        }
        lc.getData().add(seriesPred);
        lc.getData().add(series);
    }*/
    
    private static void setContextMenu(Scene scene) {
        ContextMenu contextMenu = new ContextMenu();
 
        CheckMenuItem prediction = new CheckMenuItem("Zeige Vorhersage");
        prediction.setSelected(!showHistoric);
        prediction.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	boolean before = showHistoric;
            	showHistoric = !prediction.isSelected();
            	if(before != showHistoric) generateDiagram();
            }
        });
 
        contextMenu.getItems().addAll(prediction);
    	
    	scene.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {
                contextMenu.show(priceStage, event.getScreenX(), event.getScreenY());
            }
        });
    }
}
