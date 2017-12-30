package view;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;

import controller.GasStationController;
import io.CSVManager;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.GasStation;
import model.RefuelStop;
import model.Route;
/**
 *
 * @author Admin
 */
public class MainView {
    
	Canvas canvas; 
	GraphicsContext gc;
	//GasStationController gsc;
        Map<Integer, Double> indexWithYCoordinate = new HashMap<>();
	Scene scene;
	BorderPane border;
        ScrollPane sp;
        MenuBar bar;
        SwitchButton switchButton;
        GasStationController gsc;
        Stage mainStage;
        
    public MainView(Stage primaryStage,GasStationController gsc) {
        
        mainStage = primaryStage;
        this.gsc = gsc;
        //Create the foundation: borderPane -> Scrollpane -> Canvas
        primaryStage.setTitle("IntelliTank");
        border = new BorderPane();
        sp = new ScrollPane();
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        border.setCenter(sp);
        
        scene = new Scene(border, 640, 600);
        mainStage.setResizable(false);
        mainStage.setScene(scene);

        //Methods to fill each part with content
        displayMenubar(border);
        Image icon = new Image("/img/gas-station.png");
        mainStage.getIcons().add(icon);
        //mainStage.show();
    }
    
    //iterates theough the entire map, which includes all gas stations on the route.    
    public void displayRoute(Route route) {

        canvas = new Canvas(640, 150 + 100 * route.getLength());//Canvas dimensions scale with the length of the route
        sp.setContent(canvas);
        gc = canvas.getGraphicsContext2D();
        //Erstellt einen Button, mit dem man zwischen den Tankstrategien wechseln kann
        switchButton = new SwitchButton(route,gc,/*(int)scene.getWidth() - 120*/640-125,10);
        //Iterates through the entire list
        for (int i = 0; i < route.getLength(); i++) {
	        displayGasStation(route,i);
        }
        displayResult(route);
        mainStage.show();
    }
       
    //displays menu bar on the top
    private void displayMenubar(BorderPane border) {    
        bar = new MenuBar();
        border.setTop(bar);
        
        setUpRouteTab();
    	setUpPredictionPointTab();
    	
    	Menu ueber = new Menu("über");
    	MenuItem itemUeber = new MenuItem("Mitwirkende");
    	ueber.getItems().addAll(itemUeber);
    	itemUeber.setOnAction(new EventHandler<ActionEvent>() {
    		@Override
    		public void handle(ActionEvent arg0) {
    			Alert alert = new Alert(AlertType.INFORMATION);
    			alert.setTitle("Mitwirkende");
    			alert.setHeaderText(null);
    			alert.setContentText("Dieses Projekt wurde von Sebastian Drath, Sezer Dursun, Axel Claassen und Burak Kadioglu entwickelt.");
    			alert.showAndWait();
    		}
		});
        //bar.getMenus().addAll(routen, vorhersagezeitpunkte, ueber);
    }
    
    //gets repeatedly called by the displayroute function. Creates an elipse and a line for a specific gas station
    private void displayGasStation(Route route, int index) {
        
        int circleStart = 100 + 100 * index;
        int circleWidth = 40;
        int circleHeight = 25;
        
        drawLineBetweenNodes(route,index,circleHeight);
        createGasPriceNode(route,index,circleStart,circleWidth,circleHeight);
        createFuelStatusRectangle(route,index,circleStart,circleHeight);
        createHyperlinkStationText(route,index,circleStart,circleHeight);
    }
    
    //gibt die länge eines Texts in pixel zurück
    private int getTextWidth(String stationName) {
        //System.out.println(gc.getFont().getName() + " nn " + gc.getFont().getStyle() + " nn "+ gc.getFont().getSize());
        //java.awt.Font f = new java.awt.Font();
        FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();
        Label label = new Label(stationName);
        label.setFont(Font.font(gc.getFont().getName(), FontWeight.THIN, FontPosture.REGULAR, gc.getFont().getSize()));
        //System.out.println(stationName + "'s width is: " + fontLoader.computeStringWidth(label.getText(), label.getFont()));
        return (int)fontLoader.computeStringWidth(label.getText(), label.getFont());
    }
    
    //gibt entfernung zwischen zwei Punkten auf der Karte (längen+breitengrad) zurück
    private double calculateDistance(double latA, double longA, double latB, double longB) {
    	double latitudeA = Math.toRadians(latA);
    	double longitudeA = Math.toRadians(longA);
    	double latitudeB = Math.toRadians(latB);
    	double longitudeB = Math.toRadians(longB);
    	//DecimalFormat f = new DecimalFormat("#0.00"); 
        //f.setRoundingMode(RoundingMode.DOWN);
    	double dist = 6378.388*Math.acos((Math.sin(latitudeA)*Math.sin(latitudeB))+(Math.cos(latitudeA)*Math.cos(latitudeB)*Math.cos(longitudeB-longitudeA)));
        dist *= 100;
        int distance = (int)dist;
    	//String output = f.format(dist) + " km" + " / ";	
    	//double consumption = 5.6*dist/100;
    	//output += f.format(consumption) + " L verbraucht"; 
    	return (double)distance/100;
    }

    private void displayResult(Route route) {
        gc.setFill(Color.WHITE);
        gc.fillRect(30, 10, 366, 64);
        gc.setFill(Color.BLACK);
        gc.strokeRect(30, 10, 366, 64);
        gc.strokeLine(152, 10, 152, 74);
        gc.strokeLine(274, 10, 274, 74);
        Image imgKm = new Image(getClass().getResourceAsStream("/img/route-a-b.png"));
        gc.drawImage(imgKm, 30, 10);
        Image imgFuelGauge = new Image(getClass().getResourceAsStream("/img/fuel-gauge.png"));
        gc.drawImage(imgFuelGauge, 152, 10);
        Image imgEuro = new Image(getClass().getResourceAsStream("/img/euro.png"));
        gc.drawImage(imgEuro, 274, 10);
        DecimalFormat f = new DecimalFormat("#0.00");
        String outputKm = f.format(route.getTotalKm()) + " km";
        gc.setFont(new Font(12));
        gc.fillText(outputKm, 101, 42);
        gc.setFont(Font.getDefault());
        String outputFuelGauge = f.format(route.getTotalLiters()) + " L";
        gc.setFont(new Font(15));
        gc.fillText(outputFuelGauge, 223, 42);
        gc.setFont(Font.getDefault());
    	String outputEuro = "";
        if(route.showBasicStrategy()) {
            outputEuro += f.format(route.getTotalEurosBasic()) + " �";
            gc.setFont(new Font(15));
            gc.fillText(outputEuro, 345, 42);
            gc.setFont(Font.getDefault());
        }
        else {
            outputEuro += f.format(route.getTotalEuros()) + " �";
            gc.setFont(new Font(15));
            gc.fillText(outputEuro, 345, 42);
            gc.setFont(Font.getDefault());
        }
    }

    private void createGasPriceNode(Route route, int index, int circleStart, int circleWidth, int circleHeight) {
        //Create an elipse with gas price in it(position dependent on counter)
        gc.setFill(Color.WHITE);
        gc.fillOval(180-circleWidth/2, circleStart, circleWidth, circleHeight);
        gc.setFill(Color.BLACK);
        gc.strokeOval(180-circleWidth/2, circleStart, circleWidth, circleHeight);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        double priceForStation = (double)route.get(index).getPredictedPrice()/1000;
        if(route.get(index).isPriceGuessed() == true) {
            gc.setFill(Color.RED);
            gc.fillText((double)route.get(index).getGuessedPrice()/1000+"",180,circleStart + circleHeight/2);
            gc.setFill(Color.BLACK);
        }
        else
            gc.fillText(priceForStation + "",180,circleStart + circleHeight/2);
    }

    private void createFuelStatusRectangle(Route route, int index,int circleStart,int circleHeight) {
        RefuelStop rs = route.get(index);
        double currentGasPercentage = rs.getFuelAmount(route)/route.getTankCapacity() * 100;
        double currentRefillPercentage = rs.getRefillAmount(route)/route.getTankCapacity() * 100;
        //System.out.println(rs.getFuelAmount() + " " + rs.getRefillAmount());
        DecimalFormat f = new DecimalFormat("#0.0"); 
        //f.setRoundingMode(RoundingMode.UP);
        //create a rectangle which shows the current gas status
        gc.setFill(Color.WHITE);
        gc.fillRect(30, circleStart, 100, circleHeight);
        gc.setFill(Color.BLACK);
        //this variable is only temporary until gas management is implemented
        gc.fillRect(30, circleStart, currentGasPercentage, circleHeight);
        gc.fillText(f.format(Math.abs(rs.getFuelAmount(route))) + " L", 30, circleStart-10);
        gc.setFill(Color.GREEN);
        gc.fillRect(30 + currentGasPercentage, circleStart, currentRefillPercentage, circleHeight);
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.fillText("+ " + f.format(rs.getRefillAmount(route)) + " L", 130, circleStart-10);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFill(Color.BLACK);
        gc.strokeRect(30, circleStart, 100, circleHeight);

    }

    private void createHyperlinkStationText(Route route, int index,int circleStart,int circleHeight) {
        
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFill(Color.BLUE);
        String stationName = route.get(index).getStation().getName() + ", " + route.get(index).getStation().getPostcode() + " " + route.get(index).getStation().getLocation();
        gc.fillText(stationName, 220, circleStart + circleHeight/2);
        gc.setFill(Color.BLACK);
        //füge die Verlinkung zum Preisdiagramm ein
        Image imageDecline = new Image(getClass().getResourceAsStream("/img/external-link.png"));
        double yCoordinate = circleStart + circleHeight/2;
        indexWithYCoordinate.put(index, yCoordinate);
        //Implementierung mit der DrawImage-Methode. �ffnet immer den letzten Graphen, da index am Ende auf Maximum eingestellt ist
        //das rechte Zeichen
        gc.drawImage(imageDecline, 220 + 10 + getTextWidth(stationName), circleStart + circleHeight/2 - imageDecline.getHeight()/2);
        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                if(canvas == null)
                    return;
                //System.out.println("POSS: " + me.getY() + " " + sp.getVvalue() + " CANVAS HEIGHT" + canvas.getHeight());
                int offsetPosition = (int) Math.round((canvas.getHeight() + bar.getHeight() -scene.getHeight()) * sp.getVvalue());
                //System.out.println("Viewable: min: " + offsetPosition + " max: " + (offsetPosition + scene.getHeight()) + " Total pos: " + ((int)me.getY() + offsetPosition) + " " + ((int)me.getSceneY() + offsetPosition));
            	int yPosition = ((int)me.getY() + offsetPosition - (int)bar.getHeight());
                Set<Integer> indexSet = indexWithYCoordinate.keySet();             	
            	Iterator<Integer> iter = indexSet.iterator();
            	while(iter.hasNext()) {
            		int indexTmp = iter.next();
                        if(gsc.getRoute().get(indexTmp) == null)
                            continue;
            		double yCoordinate = indexWithYCoordinate.get(indexTmp);
            		//System.out.println((yCoordinate-5) + " < " + yPosition + " < " + (yCoordinate+5) + " ? X= " + me.getX());
                        //System.out.println(gsc.getRoute().get(indexTmp) == null);// (gsc.getRoute().get(indexTmp) == null) + (gsc.getRoute().get(indexTmp).getStation() == null) + (gsc.getRoute().get(indexTmp).getStation().getName() == null));
                        String gasStationName = gsc.getRoute().get(indexTmp).getStation().getName() + ", " + gsc.getRoute().get(indexTmp).getStation().getPostcode() + " " + gsc.getRoute().get(indexTmp).getStation().getLocation();
            		if((me.getX() > 220) && (me.getX() < 220 + getTextWidth(gasStationName) + 10 + imageDecline.getWidth()/*TODO: textbreite einbeziehen*/) && (yPosition > yCoordinate-gc.getFont().getSize()/2) && (yPosition < yCoordinate+gc.getFont().getSize()/2)) {
                                //System.out.println("index von methode: " + index);
                		//System.out.println("index gespeichert: " + indexTmp);
            			GasStation gs = gsc.getRoute().get(indexTmp).getStation();
                                
                                PriceDiagram.displayGasStation(gs);
            			/*PriceDiagram diagramm = new PriceDiagram(gs);
            			diagramm.generateDiagramm();*/	
                                
            			break;
            		}
            	}
                //Falls der Button zum switchen der Tankstrategie gedrückt wurde
                if(switchButton.wasClicked((int)me.getX(),yPosition)) {
                    switchButton.buttonPressed();
                    displayRoute(route);
                }
            }
        });
    }

    private void drawLineBetweenNodes(Route route, int index,int circleHeight) {
        //If this is the first entry, dont add a line + Add distance next to it
        if(index != 0)
        {
            int lineStart = 100 + circleHeight + (index-1) * 100;
            int lineEnd = 200 + (index-1) * 100;
            gc.strokeLine(180, lineStart, 180, lineEnd/*TODO: Should length depend on distance between stations*/);
            GasStation a = route.get(index-1).getStation();
            GasStation b = route.get(index).getStation();
            gc.fillText(calculateDistance(a.getLatitude(), a.getLongitude(), b.getLatitude(), b.getLongitude()) + " km", 200, (lineStart + lineEnd)/2);
        }
    }

    private void setUpRouteTab() {
        
        Menu routes = new Menu("Routen");
        //Wenn der "Route"-Reiter gedrueckt wird, aktualisiere die Liste der Routen in dem Ordner (evtl setOnShowing?)
        routes.setOnShowing(new EventHandler<Event>() {
                    @Override
                    public void handle(Event e) {
                        //Entferne alles ausser die ersten 3(Import und 2 seperator) und füge danach alle aus dem Ordner hinzu
                        routes.getItems().remove(3,routes.getItems().size());
                        for(String s : CSVManager.readRouteNames()) {
                            MenuItem mi = new MenuItem(s);
                            mi.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(final ActionEvent e) {
                                    gsc.changeCurrentRoute(mi.getText());
                                }
                            });
                            routes.getItems().add(mi);
                        }
                    }});
    	
        MenuItem itemImportRoute = new MenuItem("Importieren");
    	itemImportRoute.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        FileChooser fc = new FileChooser();
                        File workingDirectory = new File(System.getProperty("user.dir"));
                        fc.setInitialDirectory(workingDirectory);
                        fc.getExtensionFilters().addAll(new javafx.stage.FileChooser.ExtensionFilter("CSV-Dateien", "*.csv"));
                        File selectedFile = fc.showOpenDialog(null);
                        if (selectedFile != null) {
                        	System.out.println("Sieg.");
                                //TODO: datei in owndata/routes kopieren
                        } else {
                        	System.out.println("Datei ist nicht valide.");
                        }
                    }
                });
        routes.getItems().addAll(itemImportRoute,new SeparatorMenuItem(),new SeparatorMenuItem());
        bar.getMenus().add(routes);
    }

    private void setUpPredictionPointTab() {
        Menu vorhersagezeitpunkte = new Menu("Vorhersagezeitpunkte");
    	MenuItem itemImportV = new MenuItem("Importieren");
    	itemImportV.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        FileChooser fc = new FileChooser();
                        fc.getExtensionFilters().addAll(new javafx.stage.FileChooser.ExtensionFilter("CSV-Dateien", "*.csv"));
                        File selectedFile = fc.showOpenDialog(null);
                        if (selectedFile != null) {
                        	System.out.println("Sieg.");
                        } else {
                        	System.out.println("Datei ist nicht valide.");
                        }
                    }
                });
    	vorhersagezeitpunkte.getItems().addAll(itemImportV);
        //bar.getMenus().addAll(routen, vorhersagezeitpunkte, ueber);
    }
    public void hide() {
        canvas = null;
        sp.setContent(canvas);
        this.mainStage.hide();
    }
}
