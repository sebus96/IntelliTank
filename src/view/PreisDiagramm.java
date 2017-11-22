package view;

public class PreisDiagramm extends Scene {

	public void generateDiagramm(GasStation gs) {
		final NumberAxis xAxis = new NumberAxis();
	    final NumberAxis yAxis = new NumberAxis();
	    xAxis.setLabel("Datum");
	    yAxis.setLabel("Preis");
	    final LineChart<Date,Number> lineChart = 
                new LineChart<Number,Number>(xAxis,yAxis);
        lineChart.setTitle("Preisentwicklung");
        XYChart.Series series = new XYChart.Series();
        series.setName("Aktuelle Tankstelle");
        
	}    
}