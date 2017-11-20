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

    //FPGSP = Fixed Path Gas Station Problem
    public void calculateFPGSP(Route route) {

        final double gasUsedPerHundredKm = 5.6;
        final double maxDistance = route.getTankCapacity() / gasUsedPerHundredKm;
        //1. Go through all the stations i in the route and set prevStation and nextStation
        //  prevStation = the station before s(s included), which has the lowest gas price and would be in reach with a full tank
        //  nextStation = the station after s, which has the lowest gas price and would be in reach with a full tank
        //2. Identify breakpoints: if prevStation(i) = i, i = breakpoint

        for (int i = 0; i < route.getLength(); i++) {
            route.get(i).setPrevStation(findPrevStation(route, i, maxDistance));
            if (route.get(i).equals(route.get(i).getPrevStation())) {
                route.get(i).setBreakPoint(true);
            }
            route.get(i).setNextStation(findNextStation(route, i, maxDistance));

            //3. Execute the algorithm
            // the goal is to go from one breakpoint to the next until you reach the end of the route
            // if you cant reach the next breakpoint, fill tank completely,check next() and go there
            // at next() fill just enough to get to the breakpoint
        }
    }
    //1.1. Find the prevStation of every station. Mark it as breakpoint if its the cheapest compared to the pther stations in reach

    private RefuelStop findPrevStation(Route route, int i, double maxDistance) {
        //At first, set it to self, then look for a better match in the previous stations
        int prevStationNumber = i;
        double gasPrice = 0;
        //check historic and projected prices to find the price at that specific Date
        if (route.get(i).getStation().getHistoricPrice(route.get(i).getTime()) != -1) {
            gasPrice = route.get(i).getStation().getHistoricPrice(route.get(i).getTime());
        } else if (route.get(i).getStation().getProjectedPrice(route.get(i).getTime()) != -1) {
            gasPrice = route.get(i).getStation().getProjectedPrice(route.get(i).getTime());
        } else {
            System.out.println("Error: Gas Price for " + route.get(i).getStation().getName() + " not found for " + route.get(i).getTime());
        }

        //Try to find a better price in the previous Gas stations
        //saved the distance we traveled backwards and check if the station is still in range
        double traveledDistance = 0;
        for (int j = i - 1; j >= 0 && traveledDistance < maxDistance; j--) {

            //if a previous station has a better gas Price and does exist (>0), change it to the breakpoint
            //if it passes if and else if, it either was more expensive or the price was not found
            //(Thought: Could it be that the previous station was in HistoricPrices-list and the current one in the projectedPrices-list?)
            if (route.get(j).getStation().getProjectedPrice(route.get(j).getTime()) < gasPrice && route.get(j).getStation().getProjectedPrice(route.get(j).getTime()) > 0) {
                gasPrice = route.get(j).getStation().getProjectedPrice(route.get(j).getTime());
                prevStationNumber = j;
            } else if (route.get(j).getStation().getHistoricPrice(route.get(j).getTime()) < gasPrice && route.get(j).getStation().getHistoricPrice(route.get(j).getTime()) > 0) {
                gasPrice = route.get(j).getStation().getHistoricPrice(route.get(j).getTime());
                prevStationNumber = j;
            }
            traveledDistance += calculateDistance(route.get(j).getStation().getLatitude(), route.get(j).getStation().getLongitude(), route.get(j + 1).getStation().getLatitude(), route.get(j + 1).getStation().getLongitude());
        }
        //at this point we know the prevStation of i. return it.
        return route.get(prevStationNumber);
    }

    //1.2. Find the nextStation of every station. Has to be in reach aswell
    private RefuelStop findNextStation(Route route, int i, double maxDistance) {

        double traveledDistance = 0;
        int gasPrice = 0;
        int nextStationNumber = 0;
        for (int j = i + 1; j <= route.getLength() && traveledDistance < maxDistance; j++) {

            //if there is no nextStation set yet
            if (gasPrice == 0) {
                if (route.get(j).getStation().getHistoricPrice(route.get(j).getTime()) != -1) {
                    gasPrice = route.get(j).getStation().getHistoricPrice(route.get(j).getTime());
                } else if (route.get(j).getStation().getProjectedPrice(route.get(i).getTime()) != -1) {
                    gasPrice = route.get(j).getStation().getProjectedPrice(route.get(j).getTime());
                }
                nextStationNumber = j;
            } else {
                if (route.get(j).getStation().getProjectedPrice(route.get(j).getTime()) < gasPrice && route.get(j).getStation().getProjectedPrice(route.get(j).getTime()) > 0) {
                    gasPrice = route.get(j).getStation().getProjectedPrice(route.get(j).getTime());
                    nextStationNumber = j;
                } else if (route.get(j).getStation().getHistoricPrice(route.get(j).getTime()) < gasPrice && route.get(j).getStation().getHistoricPrice(route.get(j).getTime()) > 0) {
                    gasPrice = route.get(j).getStation().getHistoricPrice(route.get(j).getTime());
                    nextStationNumber = j;
                }
            }
            traveledDistance += calculateDistance(route.get(j).getStation().getLatitude(), route.get(j).getStation().getLongitude(), route.get(j - 1).getStation().getLatitude(), route.get(j - 1).getStation().getLongitude());
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
        DecimalFormat f = new DecimalFormat("#0.00");
        double dist = 6378.388 * Math.acos((Math.sin(latitudeA) * Math.sin(latitudeB)) + (Math.cos(latitudeA) * Math.cos(latitudeB) * Math.cos(longitudeB - longitudeA)));
        return dist;
    }
}
