package view;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import controller.GasStationController;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
	@Override
    public void start(Stage primaryStage) {
     
        gsc = new GasStationController();
        
        //Create the foundation: borderPane -> Scrollpane -> Canvas
        primaryStage.setTitle("IntelliTank");
        border = new BorderPane();
        ScrollPane sp = new ScrollPane();
        border.setCenter(sp);
        canvas = new Canvas(640, 150 + 100 * gsc.getRoute().getLength());//Canvas dimensions scale with the length of the route
//        border.getChildren().add(canvas);
        sp.setContent(canvas);
        gc = canvas.getGraphicsContext2D();
        scene = new Scene(border, 800, 600);
        primaryStage.setScene(scene);
        
        //The NavigationBar is on the left side
        VBox vbox = new VBox();
        
        //The MenuBar is on the top
        MenuBar bar = new MenuBar();
       
        //Temporary
        mm = new MainModel();
        mm.calculateFPGSP(gsc.getRoute());
        mm.calculateBasicGasUsage(gsc.getRoute());
        //temporary
        
        //Methods to fill each part with content
        displayNavigationBar(vbox, border);
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
        
    	Menu ueber = new Menu("Über");
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
            gc.fillText(calculateDistance(a.getLatitude(), a.getLongitude(), b.getLatitude(), b.getLongitude()), 200, (lineStart + lineEnd)/2);
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
        
        Image imageDecline = new Image(getClass().getResourceAsStream("/img/external-link.png"));
        
        //Implementierung per Buttons. Funktionalität (Öffnen des entspr. Preisdiagramms) korrekt, jedoch "rutscht" das Zeichen beim Scrollen nicht runter
        //Das linke Zeichen
        Button bLink= new Button();
        bLink.setGraphic(new ImageView(imageDecline));
        bLink.setLayoutX(220);
        bLink.setLayoutY(circleStart + circleHeight/2);
        bLink.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
    			GasStation gs = gsc.getRoute().get(index).getStation();
    			PriceDiagram diagramm = new PriceDiagram(gs);
    			diagramm.generateDiagramm();
    			//Zur Kontrolle
    			System.out.println(circleStart + circleHeight/2);
			}
		});

        gc.fillText(gsc.getRoute().get(index).getStation().getName() + ", " + gsc.getRoute().get(index).getStation().getPostcode() + " " + gsc.getRoute().get(index).getStation().getLocation(), 220, circleStart + circleHeight/2);
       
        //Implementierung mit der DrawImage-Methode. Öffnet immer den letzten Graphen, da index am Ende auf Maximum eingestellt ist
        //das rechte Zeichen
        gc.drawImage(imageDecline, 225, (circleStart + circleHeight/2)+12);
        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent me) {
            	//Bei der Abfrage wurde der Y-Anteil nicht beachtet, da der einen sehr hohen Wert hat
            	//Wahrscheinlich weil es die letzte Tanke (letztes index)
            	if(me.getX() > 215 && me.getX() < 245) {
            		GasStation gs = gsc.getRoute().get(index).getStation();
        			PriceDiagram diagramm = new PriceDiagram(gs);
        			diagramm.generateDiagramm();
            	}
            	
            	//Zur Kontrolle
                System.out.println("Coordinate X -> " + me.getX());
                System.out.println("Coordinate Y -> " + me.getY());
                int x = circleStart + circleHeight/2;
                System.out.println("Curent Y: " + x);
            }
        });
        border.getChildren().add(bLink);
        
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
    }
    
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
    }
    
    private String calculateDistance(double latA, double longA, double latB, double longB) {
    	double latitudeA = Math.toRadians(latA);
    	double longitudeA = Math.toRadians(longA);
    	double latitudeB = Math.toRadians(latB);
    	double longitudeB = Math.toRadians(longB);
    	DecimalFormat f = new DecimalFormat("#0.00"); 
        f.setRoundingMode(RoundingMode.DOWN);
    	double dist = 6378.388*Math.acos((Math.sin(latitudeA)*Math.sin(latitudeB))+(Math.cos(latitudeA)*Math.cos(latitudeB)*Math.cos(longitudeB-longitudeA)));
    	String output = f.format(dist) + " km" + " / ";	
    	double consumption = 5.6*dist/100;
    	output += f.format(consumption) + " L verbraucht"; 
    	return output;
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
