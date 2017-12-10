/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.text.DecimalFormat;

/**
 * This class contains Algorithms that can be executed by the controller
 *
 * @author Admin
 */
public class MainModel {
    //Calculate how much it would cost if you just filled the tank completely at every station(as comparison)
    public void calculateBasicGasUsage(Route route) {
    
        final double gasUsedPerKm = 5.6 / 100;
        final double maxDistance = route.getTankCapacity() / gasUsedPerKm;
        double totalEuros = 0;
        double currentFuelAmount = 0;
        double lostFuel = 0;
        for (int i = 0; i < route.getLength(); i++) {
            //fill tank completely at the first station
            if(i==0) {
                currentFuelAmount = route.getTankCapacity();
                totalEuros = route.getTankCapacity() * route.get(i).getStation().getPrice(route.get(i).getTime()) / 1000;
            }
            //fill the missing amount from last stop
            else {
                double distanceFromLastStation = calculateDistance(route.get(i).getStation().getLatitude(), route.get(i).getStation().getLongitude(), route.get(i - 1).getStation().getLatitude(), route.get(i - 1).getStation().getLongitude());
                lostFuel = gasUsedPerKm * distanceFromLastStation;
                
                totalEuros += lostFuel * route.get(i).getStation().getPrice(route.get(i).getTime()) / 1000;
                System.out.println("neu " + totalEuros);
            }
            
        }
        route.setTotalEuroBasic(totalEuros);
    }
    //FPGSP = Fixed Path Gas Station Problem
    public void calculateFPGSP(Route route) {

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

            if (route.get(i).equals(route.get(i).getPrevStation())) {
                route.get(i).setBreakPoint(true);
            }
            //System.out.println("Prev für " + route.get(i).getStation().getPrice(route.get(i).getTime()) + " ist " + route.get(i).getPrevStation().getStation().getPrice(route.get(i).getPrevStation().getTime()) + " bp: " + route.get(i).isBreakPoint());
            //System.out.println("------");
            if (i < route.getLength() - 1) {
                route.get(i).setNextStation(findNextStation(route, i, maxDistance));
            }
            //System.out.println(route.get(i).getPrevStation().getStation().getPrice(route.get(i).getPrevStation().getTime()) + " ---> " + route.get(i).getStation().getPrice(route.get(i).getTime()) + "(" + route.get(i).isBreakPoint() + ") ---> " + route.get(i).getNextStation().getStation().getPrice(route.get(i).getNextStation().getTime()));
            //System.out.println("Next für " + route.get(i).getStation().getPrice(route.get(i).getTime()) + " ist " + route.get(i).getNextStation().getStation().getPrice(route.get(i).getNextStation().getTime()) + " bp: " + route.get(i).isBreakPoint());
        }
        //for all stations, that are "nextstations" for any station, set nextstation to true
        for (int i = 0; i < route.getLength() - 1; i++) {
            route.get(i).getNextStation().setNextStationBool(true);
            //System.out.println("Ist " + route.get(i).getStation().getPrice(route.get(i).getTime()) + " next? : " + route.get(i).isNextStation());

        }

        //3. Execute the algorithm
        // the goal is to go from one breakpoint to the next until you reach the end of the route
        // if you cant reach the next breakpoint, fill tank completely,check next() and go there
        // at next() fill just enough to get to the breakpoint
        createRefillPlan(route, gasUsedPerKm);
    }
    //1.1. Find the prevStation of every station. Mark it as breakpoint if its the cheapest compared to the pther stations in reach

    private RefuelStop findPrevStation(Route route, int i, double maxDistance) {
        //At first, set it to self, then look for a better match in the previous stations
        int prevStationNumber = i;
        double gasPrice = 0;
        //check prices to find the price at that specific Date
        if (route.get(i).getStation().getPrice(route.get(i).getTime()) != -1) {
            gasPrice = route.get(i).getStation().getPrice(route.get(i).getTime());
        }/* else if (route.get(i).getStation().getProjectedPrice(route.get(i).getTime()) != -1) {
            gasPrice = route.get(i).getStation().getProjectedPrice(route.get(i).getTime());
        } */ else {
            System.out.println("Error: Gas Price for " + route.get(i).getStation().getName() + " not found for " + route.get(i).getTime());
        }
        //System.out.println("Checking: " + gasPrice);
        //Try to find a better price in the previous Gas stations
        //saved the distance we traveled backwards and check if the station is still in range
        double traveledDistance = 0;
        for (int j = i - 1; j >= 0; j--) {

            traveledDistance += calculateDistance(route.get(j).getStation().getLatitude(), route.get(j).getStation().getLongitude(), route.get(j + 1).getStation().getLatitude(), route.get(j + 1).getStation().getLongitude());
            if (traveledDistance > maxDistance) {
                break;
            }
            //if a previous station has a better gas Price and does exist (>0), change it to the breakpoint
            //if it passes if and else if, it either was more expensive or the price was not found
            //(Thought: Could it be that the previous station was in HistoricPrices-list and the current one in the projectedPrices-list?)
            //System.out.println((i-j) + " before, the price is: " + route.get(j).getStation().getPrice(route.get(j).getTime()));
            if (route.get(j).getStation().getPrice(route.get(j).getTime()) < gasPrice && route.get(j).getStation().getPrice(route.get(j).getTime()) > 0) {
                gasPrice = route.get(j).getStation().getPrice(route.get(j).getTime());
                prevStationNumber = j;
                //System.out.println("new gas price: " + gasPrice);
            }/* else if (route.get(j).getStation().getPrice(route.get(j).getTime()) < gasPrice && route.get(j).getStation().getHistoricPrice(route.get(j).getTime()) > 0) {
                gasPrice = route.get(j).getStation().getPrice(route.get(j).getTime());
                prevStationNumber = j;
            }*/
            //traveledDistance += calculateDistance(route.get(j).getStation().getLatitude(), route.get(j).getStation().getLongitude(), route.get(j + 1).getStation().getLatitude(), route.get(j + 1).getStation().getLongitude());
            //System.out.println("CurrentDist: " + traveledDistance);
        }
        //at this point we know the prevStation of i. return it.
        //System.out.println("Returned will be: " + route.get(prevStationNumber).getStation().getPrice(route.get(prevStationNumber).getTime()));
        return route.get(prevStationNumber);
    }

    //1.2. Find the nextStation of every station. Has to be in reach aswell
    private RefuelStop findNextStation(Route route, int i, double maxDistance) {

        double traveledDistance = 0;
        int gasPrice = 0;
        int nextStationNumber = -1;
        for (int j = i + 1; j < route.getLength(); j++) {

            traveledDistance += calculateDistance(route.get(j).getStation().getLatitude(), route.get(j).getStation().getLongitude(), route.get(j - 1).getStation().getLatitude(), route.get(j - 1).getStation().getLongitude());
            if (traveledDistance > maxDistance) {
                break;
            }
            //if there is no nextStation set yet
            if (gasPrice == 0) {
                if (route.get(j).getStation().getPrice(route.get(j).getTime()) != -1) {
                    gasPrice = route.get(j).getStation().getPrice(route.get(j).getTime());
                }/* else if (route.get(j).getStation().getPrice(route.get(i).getTime()) != -1) {
                    gasPrice = route.get(j).getStation().getPrice(route.get(j).getTime());
                }*/
                nextStationNumber = j;
            } else {
                if (route.get(j).getStation().getPrice(route.get(j).getTime()) < gasPrice && route.get(j).getStation().getPrice(route.get(j).getTime()) > 0) {
                    gasPrice = route.get(j).getStation().getPrice(route.get(j).getTime());
                    nextStationNumber = j;
                }/* else if (route.get(j).getStation().getHistoricPrice(route.get(j).getTime()) < gasPrice && route.get(j).getStation().getHistoricPrice(route.get(j).getTime()) > 0) {
                    gasPrice = route.get(j).getStation().getHistoricPrice(route.get(j).getTime());
                    nextStationNumber = j;
                }*/
            }

        }
        //at this point, we know the nextStation. Set it
        return route.get(nextStationNumber);
    }

    //calculates the distance from station A to B in km
    private double calculateDistance(double latA, double longA, double latB, double longB) {
        double latitudeA = Math.toRadians(latA);
        double longitudeA = Math.toRadians(longA);
        double latitudeB = Math.toRadians(latB);
        double longitudeB = Math.toRadians(longB);
        double dist = 6378.388 * Math.acos((Math.sin(latitudeA) * Math.sin(latitudeB)) + (Math.cos(latitudeA) * Math.cos(latitudeB) * Math.cos(longitudeB - longitudeA)));
        return dist;
    }

    //Calculates the best way to refill along the way
    private void createRefillPlan(Route route, double gasUsedPerKm) {

        //RefuelStop refillStation = route.get(0).getNextStation();
        double currentTankStatus = 0;
        double totalEuros = 0, totalKm = 0;
        for (int i = 0; i < route.getLength(); i++) {
            //System.out.println("------");
            //System.out.println("Station " + route.get(i).getStation().getPrice(route.get(i).getTime()));
            //Update fuelStatus(which is gonna be the black bar) here
            if (i > 0) {
                double distanceFromLastStation = calculateDistance(route.get(i).getStation().getLatitude(), route.get(i).getStation().getLongitude(), route.get(i - 1).getStation().getLatitude(), route.get(i - 1).getStation().getLongitude());
                currentTankStatus -= gasUsedPerKm * distanceFromLastStation;
                route.get(i).setFuelAmount(currentTankStatus);
                totalKm += distanceFromLastStation;
            }

            //only refuel at break points or next stations. otherwise skip
            if (!route.get(i).isBreakPoint() && !route.get(i).isNextStation() || i == route.getLength()-1) {//.equals(refillStation)) {
                //System.out.println("Skip");
                //refillStation = route.get(i).getNextStation();
                continue;
            }

            double kmToNextTarget = 0;
            //if its a breakpoint+nextstation = fill tank completely. Only fill less if final gasstation is nearby. then just fill enough to reach it
            if (route.get(i).isNextStation() && route.get(i).isBreakPoint()) {//.equals(refillStation)) {

                if (route.get(i).getNextStation().isBreakPoint()) {
                    for (int j = i + 1; j < route.getLength(); j++) {

                        kmToNextTarget += calculateDistance(route.get(j).getStation().getLatitude(), route.get(j).getStation().getLongitude(), route.get(j - 1).getStation().getLatitude(), route.get(j - 1).getStation().getLongitude());

                        if (route.get(j).isNextStation() && route.get(j).isBreakPoint()) {//equals(route.get(i).getNextStation())/* oder next??*/) {
                            break;
                        }
                    }
                    //System.out.println("Its  a next station, but the upcoming nextstation is cheaper, so only fill enough to get there: " + kmToNextTarget);
                } else {
                    for (int j = i + 1; j < route.getLength(); j++) {

                        kmToNextTarget += calculateDistance(route.get(j).getStation().getLatitude(), route.get(j).getStation().getLongitude(), route.get(j - 1).getStation().getLatitude(), route.get(j - 1).getStation().getLongitude());
                        //refillStation = route.get(i).getNextStation();
                    }
                    //System.out.println("Its a next station and its cheaper than the upcoming one. Fill completely. Km to end: " + kmToNextTarget);
                    //System.out.println("New next: " + route.get(i).getStation().getPrice(route.get(i).getTime()));
                }
                //System.out.println("Its a next station + breakpoint station. Km to end: " + kmToNextTarget);
                //System.out.println("New next: " + route.get(i).getStation().getPrice(route.get(i).getTime()));
            } //Means that the current station is more expensive than "next", so only refuel the minimum
            else if (route.get(i).getNextStation().isBreakPoint() == true) {

                for (int j = i + 1; j < route.getLength(); j++) {

                    kmToNextTarget += calculateDistance(route.get(j).getStation().getLatitude(), route.get(j).getStation().getLongitude(), route.get(j - 1).getStation().getLatitude(), route.get(j - 1).getStation().getLongitude());

                    if (route.get(j).isBreakPoint() == true/* oder next??*/) {
                        break;
                    }
                }
                //System.out.println("Its  a breakpoint station only. Km to next breakpoint: " + kmToNextTarget);
            } else if (route.get(i).isNextStation()) {
                if (route.get(i).getNextStation().isBreakPoint()) {
                    for (int j = i + 1; j < route.getLength(); j++) {

                        kmToNextTarget += calculateDistance(route.get(j).getStation().getLatitude(), route.get(j).getStation().getLongitude(), route.get(j - 1).getStation().getLatitude(), route.get(j - 1).getStation().getLongitude());

                        if (route.get(j).equals(route.get(i).isNextStation())/* oder next??*/) {
                            break;
                        }
                    }
                    //System.out.println("Its  a next station only. The upcoming nextstation is cheaper, so only fill enough to get there: " + kmToNextTarget);
                } else {
                    for (int j = i + 1; j < route.getLength(); j++) {

                        kmToNextTarget += calculateDistance(route.get(j).getStation().getLatitude(), route.get(j).getStation().getLongitude(), route.get(j - 1).getStation().getLatitude(), route.get(j - 1).getStation().getLongitude());
                        //refillStation = route.get(i).getNextStation();
                    }
                    //System.out.println("Its a next station but not a breakpoint and its cheaper than the upcoming one. Fill completely. Km to end: " + kmToNextTarget);
                    //System.out.println("New next: " + route.get(i).getStation().getPrice(route.get(i).getTime()));
                }
            }
            if ((kmToNextTarget * gasUsedPerKm) + currentTankStatus > route.getTankCapacity()) {
                //System.out.println("Liters too much (" + (kmToNextTarget * gasUsedPerKm) + ") cut to " + (route.getTankCapacity() - currentTankStatus));
                route.get(i).setRefillAmount(route.getTankCapacity() - currentTankStatus);
                currentTankStatus = route.getTankCapacity();
            } else {
                double refillAmount = kmToNextTarget * gasUsedPerKm - currentTankStatus;
                route.get(i).setRefillAmount(refillAmount);
                currentTankStatus += refillAmount;
            }
            totalEuros += route.get(i).getRefillAmount() * route.get(i).getStation().getPrice(route.get(i).getTime()) / 1000;
            //System.out.println("Refill: " + route.get(i).getRefillAmount() + " bei " + ((double)route.get(i).getStation().getPrice(route.get(i).getTime())/1000) + "Eur /L");

        }
        route.setTotalKm(totalKm);
        route.setTotalLiters(totalKm * gasUsedPerKm);
        route.setTotalEuros(totalEuros);

    }
}
