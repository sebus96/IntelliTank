package view;

import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import controller.GasStationController;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
//import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.GasStation;
import model.MainModel;
import model.RefuelStop;
/**
 *
 * @author Admin
 */
public class IntelliTank extends Application {
    
//  Map<Integer,GasStation> stationsOnRoute = new LinkedHashMap<Integer,GasStation>(); //linkedhashmap, because its in the order in which the entries were put into the map
	//List<GasStation> stationsOnRoute = new ArrayList<GasStation>();
	Canvas canvas; 
	GraphicsContext gc;
	GasStationController gsc;
	Scene scene;
	MainModel mm;
	BorderPane border;
	Map<Integer, Double> indexWithYCoordinate = new HashMap();
	ScrollPane sp;
        MenuBar bar;
	@Override
    public void start(Stage primaryStage) {
     
        gsc = new GasStationController();
        
        //Create the foundation: borderPane -> Scrollpane -> Canvas
        primaryStage.setTitle("IntelliTank");
        border = new BorderPane();
        sp = new ScrollPane();
        border.setCenter(sp);
        canvas = new Canvas(600, 150 + 100 * gsc.getRoute().getLength());//Canvas dimensions scale with the length of the route
//        border.getChildren().add(canvas);
        sp.setContent(canvas);
        gc = canvas.getGraphicsContext2D();
        scene = new Scene(border, 640, 600);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);

        //The MenuBar is on the top
        bar = new MenuBar();
       
        //Temporary
        mm = new MainModel();
        mm.calculateFPGSP(gsc.getRoute());
        mm.calculateBasicGasUsage(gsc.getRoute());
        //temporary
        
        //Methods to fill each part with content
        //displayNavigationBar(vbox, border);
        displayRoute();
        displayResult();
        displayMenubar(bar, border);
        Image icon = new Image("/img/gas-station.png");
        primaryStage.getIcons().add(icon);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    //iterates theough the entire map, which includes all gas stations on the route.    
    private void displayRoute() {
        //Iterates through the entire list
        for (int i = 0; i < gsc.getRoute().getLength(); i++) {
	        displayGasStation(i);
        }
    }
       
    //displays menu bar on the top
    private void displayMenubar(MenuBar bar, BorderPane border) {    	
    	Menu routen = new Menu("Routen");
    	MenuItem itemImportRoute = new MenuItem("Importieren");
    	itemImportRoute.setOnAction(
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
    	routen.getItems().addAll(itemImportRoute);
    	
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
        bar.getMenus().addAll(routen, vorhersagezeitpunkte, ueber);
    	border.setTop(bar);
    }
    
    //displays navigation bar on the left
    private void displayNavigationBar(VBox vbox, BorderPane border) {
//    	vbox.setAlignment(Pos.TOP_CENTER);
//        vbox.setPadding(new Insets(10));
//        vbox.setSpacing(8);
//    	Button route = new Button();
//    	route.setText("Route");
//    	route.setPrefSize(150, 40);
//    	route.setStyle("-fx-background-image:url(/img/route.png);"
//    			+ "-fx-background-size:35px;"
//    			+ "-fx-background-repeat:no-repeat;"
//    			+ "-fx-background-position:center;"
//    			+ "-fx-font-size: 16;"
//    			+ "-fx-font-weight:bold");
//    	route.setAlignment(Pos.BASELINE_LEFT);
//    	route.setOnAction(new EventHandler<ActionEvent>() {
//			
//			@Override
//			public void handle(ActionEvent arg0) {
//		        displayRoute();
//			}
//		});
//    	vbox.getChildren().add(route);
//    	final ComboBox<String> priceCombobox = new ComboBox<>();
//    	priceCombobox.setPromptText("Preis");
//    	priceCombobox.setPrefSize(150, 40);
//    	priceCombobox.setStyle("-fx-background-image:url(/img/euro.png);"
//    			+ "-fx-background-size:35px;"
//    			+ "-fx-background-repeat:no-repeat;"
//    			+ "-fx-background-position:center;"
//    			+ "-fx-font-size: 16;"
//    			+ "-fx-font-weight:bold");
//    	//Image euroImg = new Image(getClass().getResourceAsStream("/img/euro.png"), 35, 35, false, false);
//        ObservableList<String> menuItems = FXCollections.observableArrayList();
//        for(int i = 0; i < gsc.getRoute().getLength(); i++) {
//        	menuItems.add(gsc.getRoute().get(i).getStation().getName());
//        }
//        priceCombobox.getItems().addAll(menuItems);
//        priceCombobox.setOnAction(new EventHandler<ActionEvent>() {
//			
//			@Override
//			public void handle(ActionEvent arg0) {
//    			GasStation gs = gsc.getRoute().get(priceCombobox.getSelectionModel().getSelectedIndex()).getStation();
//    			PriceDiagram diagramm = new PriceDiagram(gs);
//    			diagramm.generateDiagramm();
//			}
//		});
//        vbox.getChildren().add(priceCombobox);
//        border.setLeft(vbox);
    }

    //gets repeatedly called by the displayroute function. Creates an elipse and a line for a specific gas station
    private void displayGasStation(int index) {
        
        int circleStart = 100 + 100 * index;
        int circleWidth = 40;
        int circleHeight = 25;

        //If this is the first entry, dont add a line + Add distance next to it
        if(index != 0)
        {
            int lineStart = 100 + circleHeight + (index-1) * 100;
            int lineEnd = 200 + (index-1) * 100;
            gc.strokeLine(180, lineStart, 180, lineEnd/*TODO: Should length depend on distance between stations*/);
            GasStation a = gsc.getRoute().get(index-1).getStation();
            GasStation b = gsc.getRoute().get(index).getStation();
            gc.fillText(calculateDistance(a.getLatitude(), a.getLongitude(), b.getLatitude(), b.getLongitude()) + " km", 200, (lineStart + lineEnd)/2);
        }
        
        //Create an elipse with gas price in it(position dependent on counter)
        gc.setFill(Color.WHITE);
        gc.fillOval(180-circleWidth/2, circleStart, circleWidth, circleHeight);
        gc.setFill(Color.BLACK);
        gc.strokeOval(180-circleWidth/2, circleStart, circleWidth, circleHeight);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        double priceForStation = (double)gsc.getRoute().get(index).getPredictedPrice()/1000;
        if(gsc.getRoute().get(index).isPriceGuessed() == true) {
            gc.setFill(Color.RED);
            gc.fillText((double)gsc.getRoute().get(index).getGuessedPrice()/1000+"",180,circleStart + circleHeight/2);
            gc.setFill(Color.BLACK);
        }
        else
            gc.fillText(priceForStation + "",180,circleStart + circleHeight/2);
        gc.setTextAlign(TextAlignment.LEFT);
        String stationName = gsc.getRoute().get(index).getStation().getName() + ", " + gsc.getRoute().get(index).getStation().getPostcode() + " " + gsc.getRoute().get(index).getStation().getLocation();
        gc.setFill(Color.BLUE);
        gc.fillText(stationName, 220, circleStart + circleHeight/2);
        gc.setFill(Color.BLACK);
        RefuelStop rs = gsc.getRoute().get(index);
        double currentGasPercentage = rs.getFuelAmount()/gsc.getRoute().getTankCapacity() * 100;
        double currentRefillPercentage = rs.getRefillAmount()/gsc.getRoute().getTankCapacity() * 100;
        //System.out.println(rs.getFuelAmount() + " " + rs.getRefillAmount());
        DecimalFormat f = new DecimalFormat("#0.0"); 
        //f.setRoundingMode(RoundingMode.UP);
        //create a rectangle which shows the current gas status
        gc.setFill(Color.WHITE);
        gc.fillRect(30, circleStart, 100, circleHeight);
        gc.setFill(Color.BLACK);
        //this variable is only temporary until gas management is implemented
        gc.fillRect(30, circleStart, currentGasPercentage, circleHeight);
        gc.fillText(f.format(Math.abs(rs.getFuelAmount())) + " L", 30, circleStart-10);
        gc.setFill(Color.GREEN);
        gc.fillRect(30 + currentGasPercentage, circleStart, currentRefillPercentage, circleHeight);
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.fillText("+ " + f.format(rs.getRefillAmount()) + " L", 130, circleStart-10);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFill(Color.BLACK);
        gc.strokeRect(30, circleStart, 100, circleHeight);

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
                //System.out.println("POSS: " + me.getY() + " " + sp.getVvalue() + " CANVAS HEIGHT" + canvas.getHeight());
                int offsetPosition = (int) Math.round((canvas.getHeight() + bar.getHeight() -scene.getHeight()) * sp.getVvalue());
                //System.out.println("Viewable: min: " + offsetPosition + " max: " + (offsetPosition + scene.getHeight()) + " Total pos: " + ((int)me.getY() + offsetPosition) + " " + ((int)me.getSceneY() + offsetPosition));
            	int yPosition = ((int)me.getY() + offsetPosition - (int)bar.getHeight());
                Set<Integer> indexSet = indexWithYCoordinate.keySet();             	
            	Iterator iter = indexSet.iterator();
            	while(iter.hasNext()) {
            		int indexTmp = (int) iter.next();
            		double yCoordinate = indexWithYCoordinate.get(indexTmp);
            		//System.out.println((yCoordinate-5) + " < " + yPosition + " < " + (yCoordinate+5) + " ? X= " + me.getX());
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
            	
            	//Zur Kontrolle
                //System.out.println("Coordinate X -> " + me.getX());
                //System.out.println("Coordinate Y -> " + me.getY());
            }
        });
    }

    private int getTextWidth(String stationName) {
        //System.out.println(gc.getFont().getName() + " nn " + gc.getFont().getStyle() + " nn "+ gc.getFont().getSize());
        //java.awt.Font f = new java.awt.Font();
        FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();
        Label label = new Label(stationName);
        label.setFont(Font.font(gc.getFont().getName(), FontWeight.THIN, FontPosture.REGULAR, gc.getFont().getSize()));
        //System.out.println(stationName + "'s width is: " + fontLoader.computeStringWidth(label.getText(), label.getFont()));
        return (int)fontLoader.computeStringWidth(label.getText(), label.getFont());
    }
    /*
    //Ist nur ein Test, nicht ernst nehmen
    public class ButtonForPriceDiagram extends StackPane {
    	int x = 0;
    	int y = 0;
    	public ButtonForPriceDiagram(int x, int y) {
    		Rectangle border = new Rectangle(20,20);
    		border.setFill(null);
    		
    		this.x = x;
    		this.y = y;
    		Image image = new Image("/img/external-link.png");
            ImageView imageView = new ImageView(image);
            Button buttonForPrice = new Button("", imageView);
            getChildren().add(buttonForPrice);
            
            setOnMouseClicked(event -> {
            	if(event.getButton() == MouseButton.PRIMARY) {
            		System.out.println("HALLO");
            	}
            });
    	}
    }*/
    
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

    private void displayResult() {
        DecimalFormat f = new DecimalFormat("#0.00"); 
        Label output = new Label("Auf " + f.format(gsc.getRoute().getTotalKm()) + "km wurden " + f.format(gsc.getRoute().getTotalLiters()) + "L verbraucht bei einem Preis von insgesamt " + f.format(gsc.getRoute().getTotalEuros()) + "Eur. (" + f.format(gsc.getRoute().getTotalEuroBasic()) + " Eur)");
    	Image gasStation = new Image(getClass().getResourceAsStream("/img/gasstation.png"), 5, 5, false, false);
        output.setGraphic(new ImageView(gasStation));
    	output.setStyle("-fx-font-size: 100px");
    	output.setFont(new Font("Arial", 30));
    	output.setPrefSize(3330, 30);
        gc.fillText(output.getText(),10,50);
    }
}
