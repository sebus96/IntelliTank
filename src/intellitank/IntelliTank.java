package intellitank;

import java.text.DecimalFormat;
//hallo
import controller.GasStationController;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
//import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.GasStation;

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
	
	@Override
    public void start(Stage primaryStage) {
     
        gsc = new GasStationController();
        //Create the foundation: borderPane -> Scrollpane -> Canvas
        primaryStage.setTitle("Bla bla blubb");
        BorderPane border = new BorderPane();
        ScrollPane sp = new ScrollPane();
        border.setCenter(sp);
        canvas = new Canvas(640, 150 + 100 * gsc.getRoute().getLength());//Canvas dimensions scale with the length of the route
        sp.setContent(canvas);
        gc = canvas.getGraphicsContext2D();
        Scene scene = new Scene(border, 640, 480);
        primaryStage.setScene(scene);
        
        //The NavigationBar is on the left side
        VBox vbox = new VBox();
        
        //The MenuBar is on the top
        MenuBar bar = new MenuBar();
        
        //Methods to fill each part with content
        displayNavigationBar(vbox,border);
        displayRoute();
        displayMenubar(bar, border);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    //iterates theough the entire map, which includes all gas stations on the route.    
    private void displayRoute()
    {
        //Iterates through the entire list
        for (int i = 0; i < gsc.getRoute().getLength(); i++) {
	        //int key = entry.getKey();
	        
	        displayGasStation(i);
        }
    }
    
    //displays menu bar on the top
    private void displayMenubar(MenuBar bar, BorderPane border) {
    	Menu datei = new Menu("Datei");
//    	MenuItem add1 = new MenuItem("Example", new ImageView(new Image("menusample/new.png")));
//        datei.getItems().addAll(add1);
        
    	Menu hilfe = new Menu("Hilfe");
    	MenuItem add2 = new MenuItem("Mitwirkende");
    	hilfe.getItems().addAll(add2);
    	
        bar.getMenus().addAll(datei, hilfe);
    	border.setTop(bar);
    }
    
    //displays navigation bar on the left
    private void displayNavigationBar(VBox vbox, BorderPane border) {
        
    	vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(8);
    	
    	Button route = new Button("Route");
    	route.setPrefSize(125, 40);
    	route.setStyle("-fx-font-size: 18");
    	Image routeImg = new Image(getClass().getResourceAsStream("/img/route.png"), 35, 35, false, false);
    	route.setGraphic(new ImageView(routeImg));
    	route.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
		        displayRoute();
			}
		});
    	vbox.getChildren().add(route);
    	
    	MenuButton menuprice = new MenuButton("Preis");
    	menuprice.setStyle("-fx-font-size: 18");
    	menuprice.setPrefSize(125, 40);
    	Image euroImg = new Image(getClass().getResourceAsStream("/img/euro.png"), 35, 35, false, false);
    	menuprice.setGraphic(new ImageView(euroImg));
        for (int i = 0; i < gsc.getRoute().getLength(); i++) {
	        //int key = entry.getKey();
//	        vbox.getChildren().add(new Hyperlink(stationsOnRoute.get(i).getName()));
        	MenuItem menuitem = new MenuItem(gsc.getRoute().get(i).getStation().getName());
        	menuprice.getItems().add(menuitem);
        }
        vbox.getChildren().add(menuprice);
        border.setLeft(vbox);
    }
    // TEMPORARY UNTIL THE IO IS INTEGRATED. CURRENTLY, MAP IS FILLED WITH THESE STATIC VALUES
    /*private void fillMapWithStations() {
    	gsc.addGasStation(new GasStation(1,"CLASSIC Langballig","CLASSIC","Poststr.","5",24977,"Langballig",54.7976,9.63537));
        gsc.addGasStation(new GasStation(2,"Aral Tankstelle","ARAL","Nordstrasse","20",24943,"Flensburg",54.79709,9.476214));
        gsc.addGasStation(new GasStation(3,"WIKING FL-Nord","WIKING","Neustadt 14","",24939,"Flensburg",54.79669,9.42907));
        gsc.addGasStation(new GasStation(4,"Poetzsch TankTreff","Sonstige","Industrieweg","40",24952,"Harrislee",54.7941,9.37233));
        gsc.addGasStation(new GasStation(5,"team Tankstelle Niebüll","team","Gather Landstr.","29-31",25899,"Niebüll",54.791,8.8337));
        gsc.addGasStation(new GasStation(6,"bft-willer Station 158","bft","An der Nordstr. ","7",24989,"Streichmühle",54.783,9.6731));
        gsc.addGasStation(new GasStation(7,"Tankstelle","UNITOL","Ostring","59",25899,"Niebüll",54.781883,8.851878));
        gsc.addGasStation(new GasStation(8,"ELAN NIEBUELL","ELAN","BUSCH JOHANNSEN STR.","2",25899,"NIEBUELL",54.77996,8.83436));
    }*/
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
        gc.fillText(gsc.getRoute().get(index).getStation().getHouseNumber(),180,circleStart + circleHeight/2);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText(gsc.getRoute().get(index).getStation().getName() + ", " + gsc.getRoute().get(index).getStation().getPostcode() + " " + gsc.getRoute().get(index).getStation().getLocation(), 220, circleStart + circleHeight/2);
        
        //create a rectangle which shows the current gas status
        gc.setFill(Color.WHITE);
        gc.fillRect(30, circleStart, 100, circleHeight);
        gc.setFill(Color.BLACK);
        int currentGasPercentage = 10;//this variable is only temporary until gas management is implemented
        gc.fillRect(30, circleStart, currentGasPercentage/*TODO, depends on current gas status*/, circleHeight);
        gc.fillText("1,7 L", 30, circleStart-10);
        gc.setFill(Color.GREEN);
        gc.fillRect(30 + currentGasPercentage, circleStart, 50/*TODO, depends on current gas status*/, circleHeight);
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.fillText("+ 5,6 L", 130, circleStart-10);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFill(Color.BLACK);
        gc.strokeRect(30, circleStart, 100, circleHeight);
    }
    
    private String calculateDistance(double latA, double longA, double latB, double longB) {
    	double latitudeA = Math.toRadians(latA);
    	double longitudeA = Math.toRadians(longA);
    	double latitudeB = Math.toRadians(latB);
    	double longitudeB = Math.toRadians(longB);
    	DecimalFormat f = new DecimalFormat("#0.00"); 
    	double dist = 6378.388*Math.acos((Math.sin(latitudeA)*Math.sin(latitudeB))+(Math.cos(latitudeA)*Math.cos(latitudeB)*Math.cos(longitudeB-longitudeA)));
    	String output = f.format(dist) + "km" + " / ";	
    	double consumption = 5.6*dist/100;
    	output += f.format(consumption) + "L verbraucht"; 
    	return output;
    }
}
