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
 * Füllt das Hauptfenster mit der Routenansicht.
 * @author Axel Claassen, Burak Kadioglu, Sebastian Drath
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
    
    /**
     * Initialisiert das Routenfenster.
     * @param parent Stage, auf der die Route dargestellt wird
     * @param route route, die angezeigt werden soll
     */
    public RouteView(Stage parent, Route route) {
        super();
        this.parent = parent;
        this.route=route;
        this.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        imageDecline = new Image(getClass().getResourceAsStream("/img/external-link.png"));
        init();
    }
    /**
     * Darstellung der Routenansicht auf dem Hauptfenster.
     */
    private void init() {
        //Canvas-größe ist abhängig von der Routenlänge
    	canvas = new Canvas(1000, 150 + 100 * route.getLength());
    	this.setContent(canvas);
        gc = canvas.getGraphicsContext2D();
        //Erstellt einen Button, mit dem man zwischen den Tankstrategien wechseln kann
        switchButton = new SwitchButton( gc,800 - 140, 10);
        indexWithYCoordinate = new HashMap<>();
        stationTooltips = new ArrayList<>();
    	stationnames = new ArrayList<>();
        //Geht durch die Liste aller Tankstellen, um sie einzeln darzustellen
        for (int i = 0; i < route.getLength(); i++) {
            displayGasStation(i);
        }
        //Listener der schaut, um auf eine der Tankstellen geklickt wurde
        canvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
            	int index = decideStationnameClick(me.getX(), me.getY(), imageDecline.getWidth());
                if(index >= 0 && index < route.getLength()) {
                	Tooltip t = stationTooltips.get(index);
                	// Blende Tooltip aus, wenn der noch angezeigt wird
					if(t != null && t.isShowing()) t.hide();
                	if(me.getButton() == MouseButton.PRIMARY)
                		PriceDiagram.displayGasStation(route.get(index), parent);
                	else if(me.getButton() == MouseButton.SECONDARY)
                		new ValidationContextMenu(route.get(index)).show(parent, me.getScreenX(), me.getScreenY());
                }
                //Falls der Button zum switchen der Tankstrategie gedrückt wurde
                if (switchButton.wasClicked((int) me.getX(), (int)me.getY())) {
                    switchButton.buttonPressed();
                    init();
                }
            }
        });
        //Falls über den Tankstellentext ge-hovert wurde, mache den Cursor zu einem Hand-Symbol
        canvas.setOnMouseMoved(new EventHandler<MouseEvent>() {
        	private int lastIndex = -1;
        	
			@Override
			public void handle(MouseEvent event) {
				int index = decideStationnameClick(event.getX(), event.getY(), imageDecline.getWidth());
				// setze cursor
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

    
    /**
     * Stelle jede einzelne Tankstelle Grafisch dar.
     * @param index Der index der Tankstelle innerhalb der Route
     */
    private void displayGasStation(int index) {

        int circleStart = 100 + 100 * index;
        int circleWidth = 40;
        int circleHeight = 25;

        drawLineBetweenNodes(index, circleHeight);
        createGasPriceNode(index, circleStart, circleWidth, circleHeight);
        createFuelStatusRectangle(index, circleStart, circleHeight);
        createHyperlinkStationText(index, circleStart, circleHeight);
    }

    /**
     * Erstellt den Kreis, der den Tankstellenpreis umschließt.
     * @param index Der index der Tankstelle innerhalb der Route
     * @param circleStart Position, ab der der Kreis gezeichnet werden soll
     * @param circleWidth Breite des Tankstellen-Kreises
     * @param circleHeight Höhe des Tankstellen-Kreises
     */
    private void createGasPriceNode( int index, int circleStart, int circleWidth, int circleHeight) {
        gc.setFill(Color.WHITE);
        gc.fillOval(180 - circleWidth / 2, circleStart, circleWidth, circleHeight);
        gc.setFill(Color.BLACK);
        gc.strokeOval(180 - circleWidth / 2, circleStart, circleWidth, circleHeight);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        double priceForStation = (double) (route.get(index).getPrice()+1) / 1000;
        if (route.get(index).isPriceGuessed()) {
            gc.setFill(Color.RED);
        }
        gc.fillText((priceForStation < 0? "-.---" : priceForStation) + "", 180, circleStart + circleHeight / 2);
        gc.setFill(Color.BLACK);
    }

    /**
     * Erstellt die Tankanzeige die zeigt, wie viel Tank nur vorhanden ist.
     * @param index Der index der Tankstelle innerhalb der Route
     * @param circleStart Position, ab der der Kreis gezeichnet werden soll
     * @param circleHeight Höhe des Tankstellen-Kreises
     */
    private void createFuelStatusRectangle( int index, int circleStart, int circleHeight) {
        RefuelStop rs = route.get(index);
        double currentGasPercentage = rs.getFuelAmount() / route.getTankCapacity() * 100;
        double currentRefillPercentage = rs.getRefillAmount() / route.getTankCapacity() * 100;
        DecimalFormat f2 = new DecimalFormat("#0.00");
        DecimalFormat f1 = new DecimalFormat("#0.0");
        gc.setFill(Color.WHITE);
        gc.fillRect(30, circleStart, 100, circleHeight);
        gc.setFill(Color.BLACK);
        gc.fillRect(30, circleStart, currentGasPercentage, circleHeight);
        if (f2.format(Math.abs(rs.getFuelAmount())).charAt(3) == '0') {
            gc.fillText(f1.format(Math.abs(rs.getFuelAmount())) + " L", 30, circleStart - 10);
        } else {
            gc.fillText(f2.format(Math.abs(rs.getFuelAmount())) + " L", 30, circleStart - 10);
        }
        gc.setFill(Color.GREEN);
        gc.fillRect(30 + currentGasPercentage, circleStart, currentRefillPercentage, circleHeight);
        gc.setTextAlign(TextAlignment.RIGHT);
        if (f2.format(rs.getRefillAmount()).charAt(3) == '0') {
            gc.fillText("+ " + f1.format(rs.getRefillAmount()) + " L", 130, circleStart - 10);
        } else {
            gc.fillText("+ " + f2.format(rs.getRefillAmount()) + " L", 130, circleStart - 10);
        }
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFill(Color.BLACK);
        gc.strokeRect(30, circleStart, 100, circleHeight);
    }

    /**
     * Erzeugt den klickbaren Tankstellen-Namen rechts.
     * @param index Der index der Tankstelle innerhalb der Route
     * @param circleStart Position, ab der der Kreis gezeichnet werden soll
     * @param circleHeight Höhe des Tankstellen-Kreises
     */
    private void createHyperlinkStationText( int index, int circleStart, int circleHeight) {

        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFill(Color.BLUE);
        RefuelStop rs = route.get(index);
        GasStation s = rs.getStation();
        String stationName = s.getName() + ", " + s.getPostcode() + " " + s.getLocation();
        if(stationName.length() > 50) {
        	this.stationTooltips.add(new Tooltip(stationName));
        	stationName = stationName.substring(0, 48);
        	stationName += "...";
        } else {
        	this.stationTooltips.add(null);
        }
        gc.fillText(stationName, 220, circleStart + circleHeight / 2);
        gc.setFill(Color.BLACK);
        double yCoordinate = circleStart + circleHeight / 2;
        indexWithYCoordinate.put(index, yCoordinate);
        this.stationnames.add(stationName);
        gc.drawImage(imageDecline, 220 + 10 + getTextWidth(stationName), circleStart + circleHeight / 2 - imageDecline.getHeight() / 2);
    }
    
    /**
     * Überprüft, ob auf eine der Tankstellennamen geklickt wurde.
     * @param x der x-Wert der Position, die angeklickt wurde
     * @param y der y-Wert der Position, die angeklickt wurde
     * @param imageWidth Breite des Icons hinter dem Tankstellennamen
     * @return Den Index der Tankstelle auf die geklickt wurde. -1, falls auf keine geklickt wurde
     */
    private int decideStationnameClick(double x, double y, double imageWidth) {
    	Set<Integer> indexSet = indexWithYCoordinate.keySet();
        Iterator<Integer> iter = indexSet.iterator();
        while (iter.hasNext()) {
            int indexTmp = iter.next();
            if (route.get(indexTmp) == null) {
                continue;
            }
            double yCoordinate = indexWithYCoordinate.get(indexTmp);
            String gasStationName = stationnames.get(indexTmp);
            if ((x > 220) && (x < 220 + getTextWidth(gasStationName) + 10 + imageWidth) && (y > yCoordinate - (gc.getFont().getSize()+4) / 2) && (y < yCoordinate + (gc.getFont().getSize()+4) / 2)) {
            	return indexTmp;
            }
        }
        return -1;
    }

    /**
     * Zeichnet eine Verbindungslinie zwischen den Tankstellenkreisen sowie die Enternung in Km und die Dauer in Minuten.
     * @param index Der index der Tankstelle innerhalb der Route
     * @param circleHeight Höhe des Tankstellen-Kreises
     */
    private void drawLineBetweenNodes(int index, int circleHeight) {
        if (index != 0) {
            int lineStart = 100 + circleHeight + (index - 1) * 100;
            int lineEnd = 200 + (index - 1) * 100;
            gc.strokeLine(180, lineStart, 180, lineEnd);
            GasStation a = route.get(index - 1).getStation();
            GasStation b = route.get(index).getStation();
            long diff = Math.abs(route.get(index).getTime().getTime() - route.get(index-1).getTime().getTime());
            String time = Long.toString(TimeUnit.MILLISECONDS.toMinutes(diff));
            if(time.equals("0"))
                time = "<1";
            gc.fillText(((int)(a.getDistance(b)*100)/100.0) + " km\t" + time + " min", 200, (lineStart + lineEnd) / 2);
        }
    }

    /**
     * Erstellt das Zusammenfassungsfenster mit Informationen über die Route.
     */
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
        String outputKm = f.format(route.getTotalDistance()) + " km";
        gc.setFont(new Font(12));
        gc.fillText(outputKm, 62, 58);
        gc.setFont(Font.getDefault());
        String outputFuelGauge = f.format(route.getTotalFuelConsumption()) + " L";
        gc.setFont(new Font(15));
        gc.fillText(outputFuelGauge, 158, 58);
        gc.setFont(Font.getDefault());
        String outputEuro = f.format(route.getTotalCosts()) + " \u20ac";
        gc.setFont(new Font(15));
        gc.fillText(outputEuro, 254, 58);
        gc.setFont(Font.getDefault());
        String nameAndTimeOfRoute = "Route: \"";
        String nameOfRoute = route.getName();
        if(nameOfRoute.length() > 23) {
        	nameOfRoute = nameOfRoute.substring(0, 23);
        	nameOfRoute += "...";
        }
        nameAndTimeOfRoute += nameOfRoute;
        Date date = route.getPriceKnownUntil();
        nameAndTimeOfRoute += "\" am " + new SimpleDateFormat("dd.MM.yyyy").format(date);
        gc.setFill(Color.BROWN);
        gc.fillText(nameAndTimeOfRoute, 46, 34);
        gc.setFill(Color.BLACK);
    }

    /**
     * Gibt die Länge in Pixel zurück, die die Schrift auf dem Canvas haben wird.
     * @param stationName Darzustellender Name der Tankstelle
     * @return Länge des Tankstellennamens in Pixel
     */
    private int getTextWidth(String stationName) {
        FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();
        Label label = new Label(stationName);
        label.setFont(Font.font(gc.getFont().getName(), FontWeight.THIN, FontPosture.REGULAR, gc.getFont().getSize()));
        return (int) fontLoader.computeStringWidth(label.getText(), label.getFont());
    }
}
