package view;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;

import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import model.GasStation;
import model.RefuelStop;
import model.Route;

/**
 * Füllt das Hauptfenster mit der Routenansicht
 * @author Axel Claassen, Burak Kadioglu
 */
public class RouteView extends ScrollPane {

	private Canvas canvas;
	private GraphicsContext gc;
	private Map<Integer, Double> indexWithYCoordinate;
	private List<Tooltip> stationTooltips;
	private List<String> stationnames;
	private SwitchButton switchButton;
	private Stage parent;
	private Route route;
	private Image imageDecline;
    
    public RouteView(Stage parent, Route route) {
        super();
        this.parent = parent;
        this.route=route;
        this.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        imageDecline = new Image(getClass().getResourceAsStream("/img/external-link.png"));
        init();
    }
    
    private void init() {
    	canvas = new Canvas(1000, 150 + 100 * route.getLength());//Canvas dimensions scale with the length of the route
//        canvas.widthProperty().bind(parent.widthProperty());
    	this.setContent(canvas);
        gc = canvas.getGraphicsContext2D();
        switchButton = new SwitchButton(route, gc,800 - 140, 10);
        //Erstellt einen Button, mit dem man zwischen den Tankstrategien wechseln kann
        //Iterates through the entire list
        indexWithYCoordinate = new HashMap<>();
        stationTooltips = new ArrayList<>();
    	stationnames = new ArrayList<>();
        for (int i = 0; i < route.getLength(); i++) {
            displayGasStation(i);
        }
        canvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
            	int index = decideStationnameClick(me.getX(), me.getY(), imageDecline.getWidth());
                if(index >= 0 && index < route.getLength()) {
                	if(me.getButton() == MouseButton.PRIMARY)
                		PriceDiagram.displayGasStation(route.get(index));
                	else if(me.getButton() == MouseButton.SECONDARY)
                		new ValidationContextMenu(route.get(index)).show(parent, me.getScreenX(), me.getScreenY());
                }
                //Falls der Button zum switchen der Tankstrategie gedrückt wurde
                if (switchButton.wasClicked((int) me.getX(), (int)me.getY())) {
                    switchButton.buttonPressed();
                    init();//mainView.displayRoute(gsc.getRoute());
                }
            }
        });
        canvas.setOnMouseMoved(new EventHandler<MouseEvent>() {
        	private int lastIndex = -1;
        	
			@Override
			public void handle(MouseEvent event) {
				int index = decideStationnameClick(event.getX(), event.getY(), imageDecline.getWidth());
				// set cursor
				if (index >= 0 && index < route.getLength() || switchButton.wasClicked((int) event.getX(), (int)event.getY())) {
					((Node)event.getSource()).setCursor(Cursor.HAND);
                } else {
                	((Node)event.getSource()).setCursor(Cursor.DEFAULT);
                }
				if(index >= 0 && index < route.getLength()) {
					Tooltip t = stationTooltips.get(index);
					if(t != null){
						t.show(canvas, event.getScreenX(), event.getScreenY()+10);
						lastIndex = index;
					}
				} else {
					if(lastIndex >= 0 && lastIndex < stationTooltips.size()) {
						Tooltip t = stationTooltips.get(lastIndex);
						if(t != null && t.isShowing()) t.hide();
						lastIndex = -1;
					}
				}
			}
        	
        });
        displayResult();
    }

    
    //gets repeatedly called by the displayroute function. Creates an elipse and a line for a specific gas station
    private void displayGasStation(int index) {

        int circleStart = 100 + 100 * index;
        int circleWidth = 40;
        int circleHeight = 25;

        drawLineBetweenNodes(index, circleHeight);
        createGasPriceNode(index, circleStart, circleWidth, circleHeight);
        createFuelStatusRectangle(index, circleStart, circleHeight);
        createHyperlinkStationText(index, circleStart, circleHeight);
    }

    private void createGasPriceNode( int index, int circleStart, int circleWidth, int circleHeight) {
        //Create an elipse with gas price in it(position dependent on counter)
        gc.setFill(Color.WHITE);
        gc.fillOval(180 - circleWidth / 2, circleStart, circleWidth, circleHeight);
        gc.setFill(Color.BLACK);
        gc.strokeOval(180 - circleWidth / 2, circleStart, circleWidth, circleHeight);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        double priceForStation = (double) route.get(index).getPredictedPrice() / 1000;
        if (route.get(index).isPriceGuessed()) {
            gc.setFill(Color.RED);
            gc.fillText((double) route.get(index).getGuessedPrice() / 1000 + "", 180, circleStart + circleHeight / 2);
            gc.setFill(Color.BLACK);
        } else {
            gc.fillText((priceForStation < 0? "-.---" : priceForStation) + "", 180, circleStart + circleHeight / 2);
        }
    }

    private void createFuelStatusRectangle( int index, int circleStart, int circleHeight) {
        RefuelStop rs = route.get(index);
        double currentGasPercentage = rs.getFuelAmount(route) / route.getTankCapacity() * 100;
        double currentRefillPercentage = rs.getRefillAmount(route) / route.getTankCapacity() * 100;
        //System.out.println(rs.getFuelAmount() + " " + rs.getRefillAmount());
        DecimalFormat f2 = new DecimalFormat("#0.00");
        DecimalFormat f1 = new DecimalFormat("#0.0");
        //f.setRoundingMode(RoundingMode.UP);
        //create a rectangle which shows the current gas status
        gc.setFill(Color.WHITE);
        gc.fillRect(30, circleStart, 100, circleHeight);
        gc.setFill(Color.BLACK);
        gc.fillRect(30, circleStart, currentGasPercentage, circleHeight);
        if (f2.format(Math.abs(rs.getFuelAmount(route))).charAt(3) == '0') {
            gc.fillText(f1.format(Math.abs(rs.getFuelAmount(route))) + " L", 30, circleStart - 10);
        } else {
            gc.fillText(f2.format(Math.abs(rs.getFuelAmount(route))) + " L", 30, circleStart - 10);
        }
        gc.setFill(Color.GREEN);
        gc.fillRect(30 + currentGasPercentage, circleStart, currentRefillPercentage, circleHeight);
        gc.setTextAlign(TextAlignment.RIGHT);
        if (f2.format(rs.getRefillAmount(route)).charAt(3) == '0') {
            gc.fillText("+ " + f1.format(rs.getRefillAmount(route)) + " L", 130, circleStart - 10);
        } else {
            gc.fillText("+ " + f2.format(rs.getRefillAmount(route)) + " L", 130, circleStart - 10);
        }
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFill(Color.BLACK);
        gc.strokeRect(30, circleStart, 100, circleHeight);
    }

    private void createHyperlinkStationText( int index, int circleStart, int circleHeight) {

        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFill(Color.BLUE);
        RefuelStop rs = route.get(index);
        GasStation s = rs.getStation();
//        String stationName = s.getName() + ", " + s.getPostcode() + " " + s.getLocation() + " (echter Preis: " + (s.getHistoricPrice(rs.getTime()) / 1000.0) + " Eur)";
        String stationName = s.getName() + ", " + s.getPostcode() + " " + s.getLocation();
//        stationName = "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW"; // 100 W's (breitester Buchstabe)
        if(stationName.length() > 50) {
        	this.stationTooltips.add(new Tooltip(stationName));
        	stationName = stationName.substring(0, 48);
        	stationName += "...";
        } else {
        	this.stationTooltips.add(null);
        }
        gc.fillText(stationName, 220, circleStart + circleHeight / 2);
        gc.setFill(Color.BLACK);
        //füge die Verlinkung zum Preisdiagramm ein
        double yCoordinate = circleStart + circleHeight / 2;
        indexWithYCoordinate.put(index, yCoordinate);
        this.stationnames.add(stationName);
        //Implementierung mit der DrawImage-Methode. �ffnet immer den letzten Graphen, da index am Ende auf Maximum eingestellt ist
        //das rechte Zeichen
        gc.drawImage(imageDecline, 220 + 10 + getTextWidth(stationName), circleStart + circleHeight / 2 - imageDecline.getHeight() / 2);
    }
    
    private int decideStationnameClick(double x, double y, double imageWidth) {
    	Set<Integer> indexSet = indexWithYCoordinate.keySet();
        Iterator<Integer> iter = indexSet.iterator();
        while (iter.hasNext()) {
            int indexTmp = iter.next();
            if (route.get(indexTmp) == null) {
                continue;
            }
            double yCoordinate = indexWithYCoordinate.get(indexTmp);
            //System.out.println((yCoordinate-5) + " < " + yPosition + " < " + (yCoordinate+5) + " ? X= " + me.getX());
            //System.out.println(gsc.getRoute().get(indexTmp) == null);// (gsc.getRoute().get(indexTmp) == null) + (gsc.getRoute().get(indexTmp).getStation() == null) + (gsc.getRoute().get(indexTmp).getStation().getName() == null));
            String gasStationName = stationnames.get(indexTmp);
            if ((x > 220) && (x < 220 + getTextWidth(gasStationName) + 10 + imageWidth) && (y > yCoordinate - (gc.getFont().getSize()+4) / 2) && (y < yCoordinate + (gc.getFont().getSize()+4) / 2)) {
                //System.out.println("index von methode: " + index);
                //System.out.println("index gespeichert: " + indexTmp);
            	return indexTmp;
            }
        }
        return -1;
    }

    private void drawLineBetweenNodes( int index, int circleHeight) {
        //If this is the first entry, dont add a line + Add distance next to it
        if (index != 0) {
            int lineStart = 100 + circleHeight + (index - 1) * 100;
            int lineEnd = 200 + (index - 1) * 100;
            gc.strokeLine(180, lineStart, 180, lineEnd/*TODO: Should length depend on distance between stations*/);
            GasStation a = route.get(index - 1).getStation();
            GasStation b = route.get(index).getStation();
            long diff = Math.abs(route.get(index).getTime().getTime() - route.get(index-1).getTime().getTime());
            String time = Long.toString(TimeUnit.MILLISECONDS.toMinutes(diff));//.substring(0,1); //TODO Warum Substring?
            if(time.equals("0"))
                time = "<1";
            gc.fillText(calculateDistance(a.getLatitude(), a.getLongitude(), b.getLatitude(), b.getLongitude()) + " km\t" + time + " min", 200, (lineStart + lineEnd) / 2);
        }
    }

    private void displayResult() {
        gc.setFill(Color.WHITE);
        gc.fillRect(30, 42, 288, 32);
        gc.setFill(Color.BLACK);
        gc.setFill(Color.WHITE);
        gc.fillRect(30, 26, 288, 16);
        gc.setFill(Color.BLACK);
        gc.strokeRect(30, 26, 288, 16);
        gc.strokeRect(30, 42, 288, 32);
        gc.strokeLine(126, 42, 126, 74);
        gc.strokeLine(222, 42, 222, 74);
        Image imgRoute = new Image(getClass().getResourceAsStream("/img/route.png"));
        gc.drawImage(imgRoute, 31, 27);
        Image imgKm = new Image(getClass().getResourceAsStream("/img/route-a-b.png"));
        gc.drawImage(imgKm, 31, 43);
        Image imgFuelGauge = new Image(getClass().getResourceAsStream("/img/fuel-gauge.png"));
        gc.drawImage(imgFuelGauge, 127, 41);
        Image imgEuro = new Image(getClass().getResourceAsStream("/img/euro.png"));
        gc.drawImage(imgEuro, 223, 43);
        gc.strokeLine(30, 42, 318, 42);
        gc.strokeLine(30, 26, 30, 74);
        gc.strokeLine(30, 26, 288, 26);
        gc.strokeLine(30, 74, 318, 74);
        DecimalFormat f = new DecimalFormat("#0.00");
        String outputKm = f.format(route.getTotalKm()) + " km";
        gc.setFont(new Font(12));
        gc.fillText(outputKm, 62, 58);
        gc.setFont(Font.getDefault());
        String outputFuelGauge = f.format(route.getTotalLiters()) + " L";
        gc.setFont(new Font(15));
        gc.fillText(outputFuelGauge, 158, 58);
        gc.setFont(Font.getDefault());
        String outputEuro = "";
        if (route.showBasicStrategy()) {
            outputEuro += f.format(route.getTotalEurosBasic()) + " \u20ac";
            gc.setFont(new Font(15));
            gc.fillText(outputEuro, 254, 58);
            gc.setFont(Font.getDefault());
        } else {
            outputEuro += f.format(route.getTotalEuros()) + " \u20ac";
            gc.setFont(new Font(15));
            gc.fillText(outputEuro, 254, 58);
            gc.setFont(Font.getDefault());
        }
        String nameAndTimeOfRoute = "Route: \"" + route.getName();
        Date date = route.getPriceKnownUntil();
        nameAndTimeOfRoute += "\" am " + new SimpleDateFormat("dd.MM.yyyy").format(date);
        gc.setFill(Color.BROWN);
        gc.fillText(nameAndTimeOfRoute, 46, 34);
        gc.setFill(Color.BLACK);
    }

    //gibt entfernung zwischen zwei Punkten auf der Karte (längen+breitengrad) zurück
    private double calculateDistance(double latA, double longA, double latB, double longB) {
        double latitudeA = Math.toRadians(latA);
        double longitudeA = Math.toRadians(longA);
        double latitudeB = Math.toRadians(latB);
        double longitudeB = Math.toRadians(longB);
        //DecimalFormat f = new DecimalFormat("#0.00"); 
        //f.setRoundingMode(RoundingMode.DOWN);
        double dist = 6378.388 * Math.acos((Math.sin(latitudeA) * Math.sin(latitudeB)) + (Math.cos(latitudeA) * Math.cos(latitudeB) * Math.cos(longitudeB - longitudeA)));
        dist *= 100;
        int distance = (int) dist;
        //String output = f.format(dist) + " km" + " / ";	
        //double consumption = 5.6*dist/100;
        //output += f.format(consumption) + " L verbraucht"; 
        return (double) distance / 100;
    }

    //gibt die länge eines Texts in pixel zurück
    private int getTextWidth(String stationName) {
        //System.out.println(gc.getFont().getName() + " nn " + gc.getFont().getStyle() + " nn "+ gc.getFont().getSize());
        //java.awt.Font f = new java.awt.Font();
        FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();
        Label label = new Label(stationName);
        label.setFont(Font.font(gc.getFont().getName(), FontWeight.THIN, FontPosture.REGULAR, gc.getFont().getSize()));
        //System.out.println(stationName + "'s width is: " + fontLoader.computeStringWidth(label.getText(), label.getFont()));
        return (int) fontLoader.computeStringWidth(label.getText(), label.getFont());
    }

}
