package model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Repräsentation einer Sammlung von Vorhersagezeitpunkten. Die einzelnen
 * Elemente können in einer {@link view.PredictionPointView} angezeigt werden.
 *
 * @author Sebastian Drath
 *
 */
public class PredictionPointList implements IPredictionStationList {
	private String name;
	private List<PredictionPoint> predictionPoints;
	private DateFormat dateFormat = new SimpleDateFormat("EE dd.MM.yyyy HH:mm");

	/**
	 * Erstellt eine neue leere Liste von Vorhersagezeitpunkten.
	 *
	 * @param name
	 *            der Name der Vorhersagezeitpunkte
	 */
	public PredictionPointList(String name) {
		this.predictionPoints = new ArrayList<>();
		this.name = name;
	}

	/**
	 * Fügt einen neuen Vorhersagezeitpunkt zur Liste hinzu. Die übergebenen Parameter wurden dafür aus den Eingabedateien importiert.
	 *
	 * @param station die Tankstelle die besucht werden soll
	 * @param priceKnownUntil das Datum, bis zu dem die Preise für eine Vorhersage als bekannt angenommen werden dürfen
	 * @param predictionTime das Datum, für das eine Vorhersage bestimmt werden soll
	 */
	public void addPredictionElement(GasStation station, Date priceKnownUntil, Date predictionTime) {
		predictionPoints.add(new PredictionPoint(station, priceKnownUntil, predictionTime));
	}

	/**
	 * Erstellt aus den aktuellen Vorhersagezeitpunkten eine Liste von Zeilen, die in einer Tabelle angezeigt werden können.
	 *
	 * @return ObservableList aus Zeilen von Vorhersagezeitpunkten für eine Tabelle
	 */
	public ObservableList<PredictionPoint.TableRow> getList() {
		List<PredictionPoint.TableRow> result = new ArrayList<>();
		int ctr = 1;
		for (PredictionPoint p : this.predictionPoints) {
			result.add(new PredictionPoint.TableRow(ctr++, p, dateFormat.format(p.getPriceKnownUntil()),
					dateFormat.format(p.getTime()), p.getPredictedPrice(),
					p.getStation().getHistoricPrice(p.getTime())));
		}
		return FXCollections.observableArrayList(result);
	}

	@Override
	public PredictionPoint get(int i) {
		if (i < 0 || i >= predictionPoints.size()) {
			// System.out.println("TT: " + i + " " + this.getLength());
			return null;
		}
		return predictionPoints.get(i);
	}

	@Override
	public int getLength() {
		if (predictionPoints == null) {
			return 0;
		}
		return predictionPoints.size();
	}

	@Override
	public boolean hasPredictions() {
		boolean res = false;
		for (PredictionPoint pp : predictionPoints) {
			res = res | pp.isPredicted(); // sobald mindestens ein
											// Vorhersagepunkt vorhergesagt ist
											// gibt die Methode true zurück
		}
		return res;
	}

	@Override
	public Validation getValidation() {
		Validation res = new Validation();
		for (PredictionPoint p : this.predictionPoints) {
			res.add(p.getValidation());
		}
		return res;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getType() {
		return "Vorhersagezeitpunkte";
	}

	@Override
	public String toString() {
		return "(" + this.name + ": " + this.predictionPoints + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((predictionPoints == null) ? 0 : predictionPoints.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PredictionPointList other = (PredictionPointList) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (predictionPoints == null) {
			if (other.predictionPoints != null)
				return false;
		} else if (!predictionPoints.equals(other.predictionPoints))
			return false;
		return true;
	}
}
