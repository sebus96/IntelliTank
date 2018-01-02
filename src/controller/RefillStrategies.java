package controller;

/**
 * Diese Klasse enthält Algorithmen für Tankstrategien
 *
 * @author Admin
 */

import model.Route;
import model.RefuelStop;
import model.Price;

public class RefillStrategies {

    
    /**
     * Von hier aus werden alle Tankstrategien ausgeführt
     * @param route Die route, auf die die Tankstrategien angewendet werden
     * sollen
     */
    public void calculateGasUsage(Route route) {
        this.calculateFPGSP(route);
        this.calculateBasicGasUsage(route);
    }

    /**
     * Normale Tankstrategie: Immer volltanken, sobald es nicht nur folgenden
     * Tankstelle reichen würde. Wenn kurz vor dem Ziel: Nur so viel Tanken wie
     * nötig, um am Ziel anzukommen
     *
     * @param route Die route, auf die die Tankstrategie angewendet werden soll
     */
    private void calculateBasicGasUsage(Route route) {

        final double gasUsedPerKm = 5.6 / 100;
        final double maxDistance = route.getTankCapacity() / gasUsedPerKm;
        double totalEuros = 0;
        double currentFuelAmount = 0;
        for (int i = 0; i + 1 < route.getLength(); i++) {
            route.get(i).setFuelAmountBasic(currentFuelAmount);
            double kmToNextStation = calculateDistance(route.get(i).getStation().getLatitude(), route.get(i).getStation().getLongitude(), route.get(i + 1).getStation().getLatitude(), route.get(i + 1).getStation().getLongitude());
            //Falls der Tank nicht bis zur nächsten Tankstelle reicht, berechne wie weit es bis zum Ziel wäre
            if (currentFuelAmount <= kmToNextStation * gasUsedPerKm) {
                double kmToGoal = 0;
                for (int j = i; j + 1 < route.getLength(); j++) {
                    kmToGoal += calculateDistance(route.get(j).getStation().getLatitude(), route.get(j).getStation().getLongitude(), route.get(j + 1).getStation().getLatitude(), route.get(j + 1).getStation().getLongitude());
                    if (kmToGoal > maxDistance) {
                        break;
                    }
                }
                //Falls das Ziel noch außer Reichweite ist, tanke voll
                if (kmToGoal > maxDistance) {
                    route.get(i).setRefillAmountBasic(route.getTankCapacity() - currentFuelAmount);
                    totalEuros += ((route.getTankCapacity() - currentFuelAmount) * (double) route.get(i).getPredictedPrice() / 1000);
                    currentFuelAmount = route.getTankCapacity();
                //Falls das Ziel mit einer Tankfüllung erreichbar wäre, tanke nur so viel wie nötig, um dorthin zu gelangen
                } else {
                    route.get(i).setRefillAmountBasic((kmToGoal * gasUsedPerKm) - currentFuelAmount);
                    totalEuros += ((kmToGoal * gasUsedPerKm) - currentFuelAmount) * ((double) route.get(i).getPredictedPrice() / 1000);
                    currentFuelAmount += (kmToGoal * gasUsedPerKm);
                }
            }
            currentFuelAmount -= (kmToNextStation * gasUsedPerKm);
        }
        route.setTotalEurosBasic(totalEuros);
    }

    /**
     * Berechnet die Tankstrategie für das Fixed Path Gas Station Problem(FPGSP): Es wird nur an den billigsten Tankstellen getankt, sodass der Gesamtpreis minimal ist.
     * Zunächst werden Previous- und Nextstations für jede Tankstelle bestimmt, bevor diese dann abgefahren werden.
     * @param route Die route, auf die die Tankstrategie angewendet werden soll
     */
    private void calculateFPGSP(Route route) {

        final double gasUsedPerKm = 5.6 / 100;
        final double maxDistance = route.getTankCapacity() / gasUsedPerKm;
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
        createRefillPlan(route, gasUsedPerKm);
    }

    /**
     * Finde die "PreviousStation" für eine Tankstelle. Das ist die aktuelle oder eine vorige Tankstelle in Reichweite(anhängig von der Tankgröße des Autos), die den günstigsten Preis hat
     * @param route Die route, auf der die Tankstelle, für die die günstigste Prevstation gesucht werden soll, liegt.
     * @param i Die Nummer der Tankstelle auf der Route
     * @param maxDistance Die Maximale reichweite in km bzw. wie viele km das Auto mit vollem Tank fahren könnte.
     * @return Gibt die PreviousStation zurück. Das ist die aktuelle oder eine vorige Tankstelle in Reichweite(anhängig von der Tankgröße des Autos), die den günstigsten Preis hat.
     */
    private RefuelStop findPrevStation(Route route, int i, double maxDistance) {
        int prevStationNumber = i;
        double gasPrice = 0;
        //Falls ein Preis für den gesuchten Zeitpunkt existiert, speichere ihn
        if (route.get(i).getPredictedPrice() != -1) {
            gasPrice = route.get(i).getPredictedPrice();
        }
        //Falls er nicht existiert, vermute den Preis anhand umliegender Tankstellen
        else {
            gasPrice = guessPrice(route, i);
        }
        
        //Versucht nun die vorherigen Tankstellen abzulaufen, um zu sehen, ob dort eine günstiger ist
        double traveledDistance = 0;
        for (int j = i - 1; j >= 0; j--) {

            traveledDistance += calculateDistance(route.get(j).getStation().getLatitude(), route.get(j).getStation().getLongitude(), route.get(j + 1).getStation().getLatitude(), route.get(j + 1).getStation().getLongitude());
            if (traveledDistance > maxDistance) {
                break;
            }
            //Falls der Preis an einer vorherigen Tankstelle günstiger ist
            if (route.get(j).getPredictedPrice() < gasPrice) {
                //Prüfe, ob es tatsächlich einen Preis gibt
                if(route.get(j).getPredictedPrice() > 0) {
                    gasPrice = route.get(j).getPredictedPrice();
                    prevStationNumber = j;
                }
                //Falls nicht, vergleiche mit dem vermuteten Preis
                else if(guessPrice(route, j) < gasPrice){
                    gasPrice = guessPrice(route, j);
                    prevStationNumber = j;
                }
            }
        }
        return route.get(prevStationNumber);
    }

    /**
     * Finde die "NextStation" für eine Tankstelle. Das ist eine nachfolgende Tankstelle(nicht die aktuelle) in Reichweite(anhängig von der Tankgröße des Autos), die den günstigsten Preis hat.
     * @param route Die route, auf der die Tankstelle, für die die günstigste Nextstation gesucht werden soll, liegt.
     * @param i Die Nummer der Tankstelle auf der Route
     * @param maxDistance Die Maximale reichweite in km bzw. wie viele km das Auto mit vollem Tank fahren könnte.
     * @return Gibt die NextStation zurück. Das ist eine nachfolgende Tankstelle(nicht die aktuelle) in Reichweite(anhängig von der Tankgröße des Autos), die den günstigsten Preis hat.
     */
    private RefuelStop findNextStation(Route route, int i, double maxDistance) {

        double traveledDistance = 0;
        double gasPrice = 0;
        int nextStationNumber = -1;
        for (int j = i + 1; j < route.getLength(); j++) {
            traveledDistance += calculateDistance(route.get(j).getStation().getLatitude(), route.get(j).getStation().getLongitude(), route.get(j - 1).getStation().getLatitude(), route.get(j - 1).getStation().getLongitude());
            if (traveledDistance > maxDistance) {
                break;
            }
            //Falls dies der erste Durchlauf ist und zuvor keine NextStation gesetzt wurde
            if (gasPrice == 0) {
                if (route.get(j).getPredictedPrice() != -1) {
                    gasPrice = route.get(j).getPredictedPrice();
                } else {
                    gasPrice = guessPrice(route, j);
                    continue;
                }
                nextStationNumber = j;
            } 
            //Falls es ein späterer Durchlauf ist, vergleiche den aktuellen mit dem aktuell günstigsten Tankpreis
            else {
                if (route.get(j).getPredictedPrice() <= gasPrice) {
                    //Prüfe, ob es tatsächlich einen Preis gibt
                    if(route.get(j).getPredictedPrice() > 0) {
                        gasPrice = route.get(j).getPredictedPrice();
                        nextStationNumber = j;
                    }
                    //Falls nicht, vergleiche mit dem vermuteten Preis
                    else if(guessPrice(route, j) <= gasPrice) {
                        gasPrice = guessPrice(route, j);
                        nextStationNumber = j;
                    }
                }
            }
        }
        return route.get(nextStationNumber);
    }
    /**
     * Berechnet den Abstand in km zwischen 2 Punkten anhand ihrer Längen- und Breitengrade
     * @param latA Breitengrad Punkt A
     * @param longA Längengrad Punkt A
     * @param latB Breitengrad Punkt B
     * @param longB Längengrad Punkt B
     * @return der Abstand zwischen diesen Punkten in km
     */
    private double calculateDistance(double latA, double longA, double latB, double longB) {
        double latitudeA = Math.toRadians(latA);
        double longitudeA = Math.toRadians(longA);
        double latitudeB = Math.toRadians(latB);
        double longitudeB = Math.toRadians(longB);
        double dist = 6378.388 * Math.acos((Math.sin(latitudeA) * Math.sin(latitudeB)) + (Math.cos(latitudeA) * Math.cos(latitudeB) * Math.cos(longitudeB - longitudeA)));
        return dist;
    }

    /**
     * Berechnet den günstigsten Preis, um vom Anfang bis zum Ende der Route zu gelangen. Previousstations und Nextstations müssen bekannt sein.
     * @param route Die route, für die die günstigste Tankstrategie bestimmt werden soll
     * @param gasUsedPerKm Wieviel Benzin Pro km verbraucht wird
     */
    private void createRefillPlan(Route route, double gasUsedPerKm) {

        double currentTankStatus = 0;
        double totalEuros = 0, totalKm = 0;
        for (int i = 0; i < route.getLength(); i++) {
            
            //Falls es nicht die erste Tankstelle auf der Route ist, bestimme den Abstand zur vorherigen Tankstelle und aktualisiere den Tankstatus. Für erste Tankstelle nicht nötig, da man mit leerem Tank startet.
            if (i > 0) {
                double distanceFromLastStation = calculateDistance(route.get(i).getStation().getLatitude(), route.get(i).getStation().getLongitude(), route.get(i - 1).getStation().getLatitude(), route.get(i - 1).getStation().getLongitude());
                currentTankStatus -= gasUsedPerKm * distanceFromLastStation;
                route.get(i).setFuelAmount(currentTankStatus);
                totalKm += distanceFromLastStation;
            }

            //Falls die aktuelle Tankstelle weder Breakpoint noch NextStation ist, tanke nicht
            if (!route.get(i).isBreakPoint() && !route.get(i).isNextStation() || i == route.getLength() - 1) {
                continue;
            }
            double kmToNextTarget = 0;
            //Falls die "next" Tankstelle ein Breakpoint ist, bedeutet das, dass sie billiger ist, als die aktuelle. Also hier nur das nötigste tanken
            if (route.get(i).getNextStation().isBreakPoint() == true) {
                for (int j = i + 1; j < route.getLength(); j++) {
                    kmToNextTarget += calculateDistance(route.get(j).getStation().getLatitude(), route.get(j).getStation().getLongitude(), route.get(j - 1).getStation().getLatitude(), route.get(j - 1).getStation().getLongitude());
                    if (route.get(j).isNextStation() || route.get(j).isBreakPoint()) {
                        break;
                    }
                }
            }
            //Falls die "next" Tankstelle kein Breakpoint ist, bedeutet das, dass die aktuelle Tankstelle billiger ist. Also volltanken bzw so viel wie nötig, falls kurz vor Ende der Route.
            else {
                for (int j = i + 1; j < route.getLength(); j++) {
                    kmToNextTarget += calculateDistance(route.get(j).getStation().getLatitude(), route.get(j).getStation().getLongitude(), route.get(j - 1).getStation().getLatitude(), route.get(j - 1).getStation().getLongitude());
                }
            }
            //Hier beginnt das eigentliche Tanken, vorher wurde nur gesetzt, für wieviel km getankt werden muss.
            //Falls das Ziel außerhalb der Reichweite liegt, tanke voll
            if ((kmToNextTarget * gasUsedPerKm) + currentTankStatus > route.getTankCapacity()) {
                route.get(i).setRefillAmount(route.getTankCapacity() - currentTankStatus);
                currentTankStatus = route.getTankCapacity();
            } 
            //Ansonsten Tanke so viel wie benötigt
            else {
                double refillAmount = kmToNextTarget * gasUsedPerKm - currentTankStatus;
                route.get(i).setRefillAmount(refillAmount);
                currentTankStatus += refillAmount;
            }
            //Addiere den Preis zu den bisherigen Kosten. Nehme dafür entweder den projezierten Preis oder den vermuteten, je nachdem ob er bekannt ist.
            if(route.get(i).getPredictedPrice() > 0) {
                totalEuros += route.get(i).getRefillAmount(route) * route.get(i).getPredictedPrice() / 1000;
            }
            else {
                totalEuros += route.get(i).getRefillAmount(route) * guessPrice(route, i) / 1000;
            }
        }
        route.setTotalKm(totalKm);
        route.setTotalLiters(totalKm * gasUsedPerKm);
        route.setTotalEuros(totalEuros);
    }
    /**
     * Vermutet den Preis anhand aller anderen Tankstellen auf der Route. Je näher eine andere Tankstelle ist, desto höher ist ihr Einfluss bei der Preisbestimmung.
     * @param route Die route, auf der die Tankstelle, für die der Preis vermutet werden soll, liegt.
     * @param i Die Nummer der Tankstelle auf der Route
     * @return Gibt den vermuteten Preis in Euro zurück
     */
    private double guessPrice(Route route, int i) {
        double guessedPrice = 0;
        double divisor = 0;
        
        //Iteriert durch alle Tankstellen der Route. Tankstellen, die keinen Preis haben, werden ausgelassen,da sie nichts zur Preisbestimmung beitragen können
        for (int j = 0; j < route.getLength(); j++) {
            if (route.get(j).getPredictedPrice(route.get(i).getTime()) < 0) {
                continue;
            }
            //Speichere den Preis sowie die Entfernung zur zu bestimmenden Tankstelle
            double priceAtStation = route.get(j).getPredictedPrice(route.get(i).getTime());
            double distanceToStation = calculateDistance(route.get(j).getStation().getLatitude(), route.get(j).getStation().getLongitude(), route.get(i).getStation().getLatitude(), route.get(i).getStation().getLongitude());
            guessedPrice += (1 / distanceToStation) * priceAtStation;
            divisor += (1 / distanceToStation);
        }
        guessedPrice /= divisor;
        int guessedPriceRounded = Price.roundPrice((int) guessedPrice);
        route.get(i).setGuessedPrice(guessedPriceRounded);
        return guessedPriceRounded;
    }
}
