package controller;

import model.Price;
import model.RefuelStop;
/**
 * Diese Klasse enthält Algorithmen für Tankstrategien
 *
 * @author Axel Claassen
 */
import model.Route;

public class RefillStrategies {

	/**
	 * Der Benzinverbrauch pro Kilometer.
	 */
    public final static double GAS_USED_PER_KM = 5.6 / 100;

    /**
     * Von hier aus werden alle Tankstrategien ausgeführt
     *
     * @param route Die route, auf die die Tankstrategien angewendet werden
     * sollen
     * @return true, wenn eine Strategie erstellt werden konnte, ansonsten false
     */
    public boolean calculateGasUsage(Route route) {
        if(!route.hasPredictions()) { // kein Element wurde vorhergesagt
        	return false;
        }
        for(int i = 0; i < route.getLength(); i++) {
        	if(route.get(i).getPredictedPrice() < 0) {
        		if(!guessPrice(route,i)) {
        			return false; // es gibt keinen vorhergesagten Preis und es konnte kein Preis geraten werden (keine Tankstelle hat Preise)
        		}
        	}
        }
    	this.calculateFPGSP(route);
        this.calculateBasicGasUsage(route);
        this.validateStrategy(route);
        return true;
    }

    /**
     * Normale Tankstrategie: Immer volltanken, sobald es nicht nur folgenden
     * Tankstelle reichen würde. Wenn kurz vor dem Ziel: Nur so viel Tanken wie
     * nötig, um am Ziel anzukommen
     *
     * @param route Die route, auf die die Tankstrategie angewendet werden soll
     */
    private void calculateBasicGasUsage(Route route) {

        final double maxDistance = route.getTankCapacity() / GAS_USED_PER_KM;
        double totalEuros = 0;
        double currentFuelAmount = 0;
        for (int i = 0; i + 1 < route.getLength(); i++) {
            route.get(i).setFuelAmountBasic(currentFuelAmount);
            double kmToNextStation = route.get(i).getStation().getDistance( route.get(i + 1).getStation());
            //Falls der Tank nicht bis zur nächsten Tankstelle reicht, berechne wie weit es bis zum Ziel wäre
            if (currentFuelAmount <= kmToNextStation * GAS_USED_PER_KM) {
                double kmToGoal = 0;
                for (int j = i; j + 1 < route.getLength(); j++) {
                    kmToGoal += route.get(j).getStation().getDistance(route.get(j + 1).getStation());
                    if (kmToGoal > maxDistance) {
                        break;
                    }
                }
                //Falls das Ziel noch außer Reichweite ist, tanke voll
                if (kmToGoal > maxDistance) {
                    route.get(i).setRefillAmountBasic(route.getTankCapacity() - currentFuelAmount);
                    totalEuros += ((route.getTankCapacity() - currentFuelAmount) * (double) route.get(i).getPrice() / 1000);
                    currentFuelAmount = route.getTankCapacity();
                    //Falls das Ziel mit einer Tankfüllung erreichbar wäre, tanke nur so viel wie nötig, um dorthin zu gelangen
                } else {
                    route.get(i).setRefillAmountBasic((kmToGoal * GAS_USED_PER_KM) - currentFuelAmount);
                    totalEuros += ((kmToGoal * GAS_USED_PER_KM) - currentFuelAmount) * ((double) route.get(i).getPrice() / 1000);
                    currentFuelAmount += (kmToGoal * GAS_USED_PER_KM);
                }
            }
            currentFuelAmount -= (kmToNextStation * GAS_USED_PER_KM);
        }
        route.setTotalCostsBasic(totalEuros);
    }

    /**
     * Berechnet die Tankstrategie für das Fixed Path Gas Station
     * Problem(FPGSP): Es wird nur an den billigsten Tankstellen getankt, sodass
     * der Gesamtpreis minimal ist. Zunächst werden Previous- und Nextstations
     * für jede Tankstelle bestimmt, bevor diese dann abgefahren werden.
     *
     * @param route Die route, auf die die Tankstrategie angewendet werden soll
     */
    private void calculateFPGSP(Route route) {

        final double maxDistance = route.getTankCapacity() / GAS_USED_PER_KM;
        //1. Go through all the stations i in the route and set prevStation and nextStation
        //  prevStation = the station before s(s included), which has the lowest gas price and would be in reach with a full tank
        //  nextStation = the station after s, which has the lowest gas price and would be in reach with a full tank
        //2. Identify breakpoints: if prevStation(i) = i, i = breakpoint
        //System.out.println(route.get(0).getStation().getHistoricPrice(route.get(0).getTime()));
        //System.out.println("Tank: " + route.getTankCapacity() + "\nMaxDistance: " + maxDistance);
        for (int i = 0; i < route.getLength(); i++) {
            route.get(i).setPrevStation(findPrevStation(route, i, maxDistance));

            //Wenn die aktuelle Tankstelle die günstigste ist, markiere sie als Breakpoint
            if (route.get(i).equals(route.get(i).getPrevStation())) {
                route.get(i).setBreakPoint(true);
            }
            if (i < route.getLength() - 1) {
                route.get(i).setNextStation(findNextStation(route, i, maxDistance));
            }
        }
        //for all stations, that are "nextstations" for any station, set nextstation to true
        for (int i = 0; i < route.getLength() - 1; i++) {
            route.get(i).getNextStation().setNextStationBool(true);
        }

        //3. Execute the algorithm
        // the goal is to go from one breakpoint to the next until you reach the end of the route
        // if you cant reach the next breakpoint, fill tank completely,check next() and go there
        // at next() fill just enough to get to the breakpoint
        createRefillPlan(route);
    }

    /**
     * Finde die "PreviousStation" für eine Tankstelle. Das ist die aktuelle
     * oder eine vorige Tankstelle in Reichweite(anhängig von der Tankgröße des
     * Autos), die den günstigsten Preis hat
     *
     * @param route Die route, auf der die Tankstelle, für die die günstigste
     * Prevstation gesucht werden soll, liegt.
     * @param i Die Nummer der Tankstelle auf der Route
     * @param maxDistance Die Maximale reichweite in km bzw. wie viele km das
     * Auto mit vollem Tank fahren könnte.
     * @return Gibt die PreviousStation zurück. Das ist die aktuelle oder eine
     * vorige Tankstelle in Reichweite(anhängig von der Tankgröße des Autos),
     * die den günstigsten Preis hat.
     */
    private RefuelStop findPrevStation(Route route, int i, double maxDistance) {
        int prevStationNumber = i;
        double gasPrice = route.get(i).getPrice();

        //Versucht nun die vorherigen Tankstellen abzulaufen, um zu sehen, ob dort eine günstiger ist
        double traveledDistance = 0;
        for (int j = i - 1; j >= 0; j--) {

            traveledDistance += route.get(j).getStation().getDistance( route.get(j + 1).getStation());
            if (traveledDistance > maxDistance) {
                break;
            }
            //Falls der Preis an einer vorherigen Tankstelle günstiger ist
            if (route.get(j).getPrice() < gasPrice) {
                gasPrice = route.get(j).getPrice();
                prevStationNumber = j;
            }
        }
        return route.get(prevStationNumber);
    }

    /**
     * Finde die "NextStation" für eine Tankstelle. Das ist eine nachfolgende
     * Tankstelle(nicht die aktuelle) in Reichweite(anhängig von der Tankgröße
     * des Autos), die den günstigsten Preis hat.
     *
     * @param route Die route, auf der die Tankstelle, für die die günstigste
     * Nextstation gesucht werden soll, liegt.
     * @param i Die Nummer der Tankstelle auf der Route
     * @param maxDistance Die Maximale reichweite in km bzw. wie viele km das
     * Auto mit vollem Tank fahren könnte.
     * @return Gibt die NextStation zurück. Das ist eine nachfolgende
     * Tankstelle(nicht die aktuelle) in Reichweite(anhängig von der Tankgröße
     * des Autos), die den günstigsten Preis hat.
     */
    private RefuelStop findNextStation(Route route, int i, double maxDistance) {

        double traveledDistance = 0;
        double gasPrice = 0;
        int nextStationNumber = -1;
        for (int j = i + 1; j < route.getLength(); j++) {
            traveledDistance += route.get(j).getStation().getDistance(route.get(j - 1).getStation());
            if (traveledDistance > maxDistance) {
                break;
            }
            //Falls dies der erste Durchlauf ist und zuvor keine NextStation gesetzt wurde
            if (gasPrice == 0) {
                gasPrice = route.get(j).getPrice();
                nextStationNumber = j;
                continue;
            } //Falls es ein späterer Durchlauf ist, vergleiche den aktuellen mit dem aktuell günstigsten Tankpreis
            else {
                if (route.get(j).getPrice() <= gasPrice) { //TODO hier kleiner gleich oben nur gleich?
                    gasPrice = route.get(j).getPrice();
                    nextStationNumber = j;
                }
            }
        }
        return route.get(nextStationNumber);
    }

    /**
     * Berechnet den günstigsten Preis, um vom Anfang bis zum Ende der Route zu
     * gelangen. Previousstations und Nextstations müssen bekannt sein.
     *
     * @param route Die route, für die die günstigste Tankstrategie bestimmt
     * werden soll
     */
    private void createRefillPlan(Route route) {

        double currentTankStatus = 0;
        double totalEuros = 0, totalKm = 0;
        for (int i = 0; i < route.getLength(); i++) {
            
            //Falls es nicht die erste Tankstelle auf der Route ist, bestimme den Abstand zur vorherigen Tankstelle und aktualisiere den Tankstatus. Für erste Tankstelle nicht nötig, da man mit leerem Tank startet.
            if (i > 0) {
                double distanceFromLastStation = route.get(i).getStation().getDistance( route.get(i - 1).getStation());
                currentTankStatus -= GAS_USED_PER_KM * distanceFromLastStation;
                // Durch Rundungsfehler kann der Wert im knappen negativen Bereich liegen (in Testläufen 10^-16)
                if(currentTankStatus > -0.0000001 && currentTankStatus < 0) currentTankStatus = 0;
                route.get(i).setFuelAmount(currentTankStatus);
                totalKm += distanceFromLastStation;
            }

            //Falls die aktuelle Tankstelle weder Breakpoint noch NextStation ist, tanke nicht
            if (!route.get(i).isBreakPoint() && !route.get(i).isNextStation() || i == route.getLength() - 1) {
                continue;
            }
//            double kmToNextTarget = 0;
            double fuelToNextTarget = 0;
            //Falls die "next" Tankstelle ein Breakpoint ist, bedeutet das, dass sie billiger ist, als die aktuelle. Also hier nur das nötigste tanken
            if (route.get(i).getNextStation().isBreakPoint()) {
                for (int j = i + 1; j < route.getLength(); j++) {
                	double distance = route.get(j).getStation().getDistance( route.get(j - 1).getStation());
//                    kmToNextTarget += distance;
                    fuelToNextTarget += distance * GAS_USED_PER_KM;
                    assert route.get(j).getStation().getDistance( route.get(j - 1).getStation()) == route.get(j-1).getStation().getDistance( route.get(j).getStation());
                    if (route.get(j).isNextStation() || route.get(j).isBreakPoint()) {
                        break;
                    }
                }
            } //Falls die "next" Tankstelle kein Breakpoint ist, bedeutet das, dass die aktuelle Tankstelle billiger ist. Also volltanken bzw so viel wie nötig, falls kurz vor Ende der Route.
            else {
                for (int j = i + 1; j < route.getLength(); j++) {
                	double distance = route.get(j).getStation().getDistance( route.get(j - 1).getStation());
//                    kmToNextTarget += distance;
                    fuelToNextTarget += distance * GAS_USED_PER_KM;
                    assert route.get(j).getStation().getDistance( route.get(j - 1).getStation()) == route.get(j-1).getStation().getDistance( route.get(j).getStation());
                }
            }
            
            //Hier beginnt das eigentliche Tanken, vorher wurde nur gesetzt, für wieviel km getankt werden muss.
            //Falls das Ziel außerhalb der Reichweite liegt, tanke voll
            if (fuelToNextTarget > route.getTankCapacity()) {
                double refillAmount = route.getTankCapacity() - currentTankStatus;
            	route.get(i).setRefillAmount(refillAmount);
                currentTankStatus += refillAmount;
            } //Ansonsten Tanke so viel wie benötigt
            else {
                double refillAmount = fuelToNextTarget - currentTankStatus;
                if(refillAmount <= 0) refillAmount = 0; // wenn noch mehr Benzin im Tank vorhanden ist als bis zum nächsten Ziel benötigt wird (-> kein negatives Tanken!)
                route.get(i).setRefillAmount(refillAmount);
                currentTankStatus += refillAmount;
            }
            //Addiere den Preis zu den bisherigen Kosten.
            totalEuros += route.get(i).getRefillAmount() * route.get(i).getPrice() / 1000;
        }
        route.setTotalDistance(totalKm);
        route.setTotalFuelConsumption(totalKm * GAS_USED_PER_KM);
        route.setTotalCosts(totalEuros);
    }

    /**
     * Vermutet den Preis anhand aller anderen Tankstellen auf der Route. Je
     * näher eine andere Tankstelle ist, desto höher ist ihr Einfluss bei der
     * Preisbestimmung.
     *
     * @param route Die route, auf der die Tankstelle, für die der Preis
     * vermutet werden soll, liegt.
     * @param i Die Nummer der Tankstelle auf der Route
     * @return Gibt den vermuteten Preis in Euro zurück
     */
    private boolean guessPrice(Route route, int i) {
        double guessedPrice = 0;
        double divisor = 0;

        //Iteriert durch alle Tankstellen der Route. Tankstellen, die keinen Preis haben, werden ausgelassen,da sie nichts zur Preisbestimmung beitragen können
        for (int j = 0; j < route.getLength(); j++) {
            if (route.get(j).getPredictedPrice(route.get(i).getTime()) < 0) {
                continue;
            }
            //Speichere den Preis sowie die Entfernung zur zu bestimmenden Tankstelle
            double priceAtStation = route.get(j).getPredictedPrice(route.get(i).getTime());
            double distanceToStation = route.get(j).getStation().getDistance( route.get(i).getStation());
            guessedPrice += (1 / distanceToStation) * priceAtStation;
            divisor += (1 / distanceToStation);
        }
        guessedPrice /= divisor;
        int guessedPriceRounded = Price.roundPrice((int) guessedPrice);
        route.get(i).setGuessedPrice(guessedPriceRounded);
        return guessedPriceRounded > 0;
    }

    /**
     * Validiert die Berechnete Strategie
     * @param route die Route für die die Strategie berechnet wurde
     */
    private void validateStrategy(Route route) {
        
        for(int i = 0; i<route.getLength();i++) {
            if(route.get(i).getFuelAmount() < 0 || route.get(i).getRefillAmount() < 0) {
                //PopupBox.displayError(304);
            	System.err.println("Check route strategy result.");
                return;
            }
            
        }
    }
}
