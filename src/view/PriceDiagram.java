package view;

import java.text.SimpleDateFormat;
import java.time.temporal.ValueRange;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.ContextMenuEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.StringConverter;
import model.GasStation;
import model.IPredictionStation;
import model.Price;

/**
 * Erzeugt die Graphen für historische und vorhersagte Preise der einzelnen Tankstellen.
 * @author Axel Claassen, Burak Kadioglu, Sebastian Drath
 *
 */
public class PriceDiagram {
    private static List<IPredictionStation> gasStations = new ArrayList<>();
    private static Stage priceStage;
    private static boolean showHistoric = true;
    private static RadioMenuItem predictionItem;

    /**
     * Öffnet ein neues Fenster mit den historischen Preisen der Tankstelle. Ist das Fenster bereits offen, wird die Kurve in anderer Farbe hinzugefügt.
     * @param gs IPredictionStation der Tankstelle
     * @param window Hauptfenster
     */
    public static void displayGasStation(IPredictionStation gs, Window window) {
    	boolean showWarning = false;
    	if(!showHistoric && !gs.isPredicted()) {
    		if(gasStations.isEmpty()) {
    			showHistoric = true; // zeige Warnung am Ende, damit sie nicht von der priceStage verdeckt wird
    			showWarning = true;
    			setContextMenuSelection();
    		} else {
        		PopupBox.displayWarning(202);
    			return;
    		}
    	}
        if (priceStage == null) {
            gasStations = new ArrayList<>();
            priceStage = new Stage();
            priceStage.initOwner(window);
            priceStage.setWidth(800);
            priceStage.setHeight(600);
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
        if(showWarning) PopupBox.displayWarning(202);
    }
    
    /**
     * Generiert einen Graphen für die Anzeige der Preise einer Tankstelle der Route. 
     */
    private static void generateDiagram() {
        if(priceStage.isShowing()) {
            priceStage.hide();
        }
    	double xMin = Double.MAX_VALUE;
    	double xMax = Double.MIN_VALUE;
    	boolean unPredictedStations = false;
        
        //Schaut, wie breit der Wertebereich in X-Richtung sein muss(von welchem Datum bis zu welchem) um alle Tankstellenpreise im Graphen darstellen zu können
    	for(IPredictionStation ps : gasStations) {
        	if(showHistoric) {
        		// Es werden nur die historischen Daten angezeigt und dementsprechend wird das früheste startdatum und späteste enddatum der historischen preise gesucht
	        	GasStation g = ps.getStation();
	        	Price firstElement = g.getPriceListElement(0);
	        	Price lastElement = g.getPriceListElement(g.getPriceListSize()-1);
	        	
	            if(firstElement.getTime().getTime() < xMin) {
	                xMin = firstElement.getTime().getTime();
	            }
	            if(lastElement.getTime().getTime() > xMax) {
	                xMax = lastElement.getTime().getTime();
	            }
        	} else {
        		if(!ps.isPredicted()) {
        			unPredictedStations = true;
                	continue;
                }
        		// Es werden nur die vorhergesagten Daten angezeigt und dementsprechend wird das früheste startdatum und späteste enddatum der vorhergesagten Preise gesucht
        		Price firstElement = ps.getPredictedPriceListElement(0);
	        	Price lastElement = ps.getPredictedPriceListElement(ps.getPredictedPriceListSize()-1);
	        	
	            if(firstElement.getTime().getTime() < xMin) {
	            	xMin = firstElement.getTime().getTime();
	            }
	            if(lastElement.getTime().getTime() > xMax) {
	            	xMax = lastElement.getTime().getTime();
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
        Scene scene = new Scene(lineChart, priceStage.getWidth(), priceStage.getHeight()); //, 640, 600
        setContextMenu(scene);
        int minY = 1000,maxY = 2000;
        for (IPredictionStation ps : gasStations) {
        	ValueRange vr;
            if(showHistoric) vr = addSeries(lineChart, ps.getStation());
            else vr = addPredictedSeries(lineChart, ps);
            minY = Math.min(minY, (int)vr.getMinimum());
            maxY = Math.max(maxY, (int)vr.getMaximum());
        }
        yAxis.setLowerBound(minY);
        yAxis.setUpperBound(maxY);
        
    	if(unPredictedStations) {
	    	if(gasStations.size() == 1) PopupBox.displayWarning(202);
	    	else PopupBox.displayWarning(203);
    	}

        Image icon = new Image("/img/gas-station.png");
        priceStage.getIcons().add(icon);
        priceStage.setScene(scene);
        if(!priceStage.isShowing()) {
            priceStage.show();
        }
    }
    
    /**
     * Fügt die Daten der angeklickten Tankstelle(Series) dem Linechart hinzu, um sie anzeigen zu lassen.
     * @param lc Darstellung der Punktdaten. Welchem Linechart die Daten hinzugefügt werden sollen
     * @param gs Die Tankstelle, für die die Daten angezeigt werden sollen.
     * @return Minimum und Maximum der gesetzten Preise
     */
    private static ValueRange addSeries(LineChart<Number, Number> lc ,GasStation gs) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(gs.getName());
        Calendar c = Calendar.getInstance();
        c.setTime(gs.getPriceListElement(0).getTime());
        c.set(Calendar.HOUR_OF_DAY, 12);
        c.set(Calendar.MINUTE, 0);
        int sum = 0;
        int ctr = 0;
        int minPrice = Integer.MAX_VALUE;
        int maxPrice = Integer.MIN_VALUE;
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
                	double price = sum / ctr;
                	if(price > maxPrice) maxPrice = (int)price;
                	if(price < minPrice) minPrice = (int)price;
                    series.getData().add(new XYChart.Data<Number, Number>(p.getTime().getTime(), price));
                }
                sum = 0;
                ctr = 0;
            }
            lastPrice = p;
        }
        lc.getData().add(series);
        return ValueRange.of(minPrice, maxPrice);
    }
    /**
     * Fügt die Vorhergesagten Preise dem Linechart hinzu, um sie darzustellen
     * @param lc Darstellung der Punktdaten. Welchem Linechart die Daten hinzugefügt werden sollen
     * @param ps Die IPredictionStation, für die die Daten angezeigt werden sollen.
     * @return Minimum und Maximum der gesetzten Preise
     */
    private static ValueRange addPredictedSeries(LineChart<Number, Number> lc ,IPredictionStation ps) {
        if(!ps.isPredicted()) return ValueRange.of(1000, 2000);
    	XYChart.Series<Number, Number> seriesPred = new XYChart.Series<>();
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        seriesPred.setName(ps.getStation().getName() + " Vorhersage");
        series.setName(ps.getStation().getName());
        int minPrice = Integer.MAX_VALUE;
        int maxPrice = Integer.MIN_VALUE;
        for (int i = 0; i < ps.getPredictedPriceListSize(); i++) {
            Price p = ps.getPredictedPriceListElement(i);
            double p_real = ps.getStation().getHistoricPrice(p.getTime());
            if(p_real > maxPrice) maxPrice = (int)p_real;
            if(p.getPrice() > maxPrice) maxPrice = p.getPrice();
            if(p_real < minPrice) minPrice = (int)p_real;
            if(p.getPrice() < minPrice) minPrice = p.getPrice();
            seriesPred.getData().add(new XYChart.Data<Number, Number>(p.getTime().getTime(), p.getPrice()));
            if(p_real > 0 ) series.getData().add(new XYChart.Data<Number, Number>(p.getTime().getTime(), p_real ));
        }
        lc.getData().add(seriesPred);
        lc.getData().add(series);
        return ValueRange.of(minPrice, maxPrice);
    }
    
    /**
     * Setzt ein Kontextmenü, das per Rechtsklick auf eine Tankstelle angezeigt wird. 
     * @param scene Die Szene, in der das Kontextmenü geöffnet wurde
     */
    private static void setContextMenu(Scene scene) {
        ContextMenu contextMenu = new ContextMenu();
        ToggleGroup group = new ToggleGroup();
        EventHandler<ActionEvent> eh = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	if(predictionItem == null) return;
        		boolean before = showHistoric;
            	showHistoric = !predictionItem.isSelected();
            	if(before != showHistoric) generateDiagram();
            }
        };
        RadioMenuItem r = new RadioMenuItem("Zeige historische Preise");
        r.setSelected(showHistoric);
        r.setToggleGroup(group);
        r.setOnAction(eh);
        if(predictionItem == null) {
	        predictionItem = new RadioMenuItem("Zeige vorhergesagte Preise");
	        predictionItem.setSelected(!showHistoric);
	        predictionItem.setOnAction(eh);
        }
        predictionItem.setToggleGroup(group);
        contextMenu.getItems().addAll(predictionItem,r);
    	
    	scene.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {
                contextMenu.show(priceStage, event.getScreenX(), event.getScreenY());
            }
        });
    }
    
    /**
     * Setzt die Auswahl im Kontextmenü, wenn intern geändert wurde, ob historische oder vorhergesagte Preise angezeigt werden.
     * Dies ist nötig, wenn automatisch historische Daten angezeigt werden, weil keine Vorhersage getätigt werden konnte.
     * Dies ist der Fall wenn keine bzw nicht genügend Daten als Basis für eine Vorhersage vorhanden sind.
     */
    private static void setContextMenuSelection() {
    	if(predictionItem == null) return;
    	else predictionItem.setSelected(!showHistoric);
    }
    
    /*/**
     * Fügt die Vorhergesagten Preise dem Linechart hinzu, um sie darzustellen (in Tagesabständen)
     * @param lc Darstellung der Punktdaten. Welchem Linechart die Daten hinzugefügt werden sollen
     * @param ps Die IPredictionStation, für die die Daten angezeigt werden sollen.
     * /
    private static void addPredictedSeriesDaySteps(LineChart<Number, Number> lc ,IPredictionStation ps) {
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
}
