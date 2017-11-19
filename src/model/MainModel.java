/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 * This class contains Algorithms that can be executed by the controller
 * @author Admin
 */
public class MainModel {
    
    //FPGSP = Fixed Path Gas Station Problem
    public void calculateFPGSP(Route route) {
        
        final double gasUsedPerHundredKm = 5.6;
        double maxDistance = route.getTankCapacity() / gasUsedPerHundredKm;
        //1. Go through all the stations s in the route and set prevStation and nextStation
        //  prevStation = the station before s(s included), which has the lowest gas price and would be in reach with a full tank
        //  nextStation = the station after s, which has the lowest gas price and would be in reach with a full tank

        for(int i = 0; i < route.getLength();i++) {
            /*
            First goal: 
                - find the prevStation of every station on the route
                    - to find the prevStation, we need to iterate through all the gas stations before
                      and find the one with the lowest price which is still in reach (<maxDistance) 
                    - if the current one is the prevStation, mark them as BreakPoint
                    ...
            */
            route.get(i).setPrevStation(route.get(i));
            double gasPrice = 0;
            for(int j = i ; i>=0 ; i--) {
                for(Price p : route.get(j).getStation().getHistoricPrices()) {

                    if(p.getTime().equals(route.get(j).getTime()))
                        gasPrice = p.getPrice();
                }
            }
        }
        

        //2. Identify breakpoints: if prevStation(i) = i, i = breakpoint
        //3. Execute the algorithm
        // the goal is to go from one breakpoint to the next until you reach the end of the route
        // if you cant reach the next breakpoint, fill tank completely,check next() and go there
        // at next() fill just enough to get to the breakpoint
    }
    
}
