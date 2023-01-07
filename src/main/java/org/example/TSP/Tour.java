/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.example.TSP;

import java.util.ArrayList;
import java.util.Collections;

public class Tour {
    
    // Holds our tour of cities
    private ArrayList<City> tour = new ArrayList<City>();
    // Cache
    private double distance = 0.0d;
    private double fitness = 0;

    // Constructs a blank tour
    public Tour(){
        for (int i = 0; i < TourManager.numberOfCities(); i++) {
            tour.add(null);
        }
    }
 
    // Constructs a tour from another tour
    @SuppressWarnings("unchecked")
	public Tour(ArrayList<City> tour){
        this.tour = (ArrayList<City>) tour.clone();
    }
 
    // Returns tour information
    public ArrayList<City> getTour(){
        return tour;
    }
    
    public void Clear(){
        tour.clear();
    }
 
    // Creates a random individual
    public void generateIndividual() {
        // Loop through all our destination cities and add them to our tour
        for (int cityIndex = 0; cityIndex < TourManager.numberOfCities(); cityIndex++) {
          setCity(cityIndex, TourManager.getCity(cityIndex));
        }
        // Randomly reorder the tour
        Collections.shuffle(tour);
    }

    public void generate() {
        // Loop through all our destination cities and add them to our tour
        for (int cityIndex = 0; cityIndex < TourManager.numberOfCities(); cityIndex++) {
            setCity(cityIndex, TourManager.getCity(cityIndex));
        }
    }

    public void generate(TourManager TM) {
        // Loop through all our destination cities and add them to our tour
        for (int cityIndex = 0; cityIndex < TM.numberOfCities(); cityIndex++) {
            setCity(cityIndex, TM.getCity(cityIndex));
        }
    }
 
    // Gets a city from the tour
    public City getCity(int tourPosition) {
        return (City)tour.get(tourPosition);
    }
 
    // Sets a city in a certain position within a tour
    public void setCity(int tourPosition, City city) {
        tour.set(tourPosition, city);
        // If the tours been altered we need to reset the fitness and distance
        distance = 0;
    }
    // Gets the tours fitness
    public double getFitness() {
        if (fitness == 0) {
            fitness = 1/(double)getDistance();
        }
        return fitness;
    }
 
    // Gets the total distance of the tour
    public double getDistance(){
        if ( Double.compare(distance, 0.0d) == 0) {
            double tourDistance = 0.0d;
            // Loop through our tour's cities
            for (int cityIndex=0; cityIndex < tourSize(); cityIndex++) {
                // Get city we're traveling from
                City fromCity = getCity(cityIndex);
                // City we're traveling to
                City destinationCity;
                // Check we're not on our tour's last city, if we are set our
                // tour's final destination city to our starting city
                if(cityIndex+1 < tourSize()){
                    destinationCity = getCity(cityIndex+1);
                }
                else{
                    destinationCity = getCity(0);
                }
                // Get the distance between the two cities
                tourDistance += fromCity.distanceTo(destinationCity);
            }
            distance = tourDistance;
        }
        return distance;
    }
 
    // Get number of cities on our tour
    public int tourSize() {
        return tour.size();
    }

    // Check if the tour contains a city
    public boolean containsCity(City city){
        return tour.contains(city);
    }
 
   @Override
    public String toString() {
        String geneString = "|";
        for (int i = 0; i < tourSize(); i++) {
            geneString += getCity(i)+"|";
        }
        return geneString;
    }

    public double[][] toMatrix(){

        double[][] tour = new double[tourSize()][tourSize()];

        City cityFrom;
        City cityTo;

        for (int i = 0; i <= tourSize()-1; i++){
            for (int j = 0; j <= tourSize()-1; j++){
                if (i == j){
                    tour[i][j] = 0;
                } else {
                    //def city1
                    cityFrom = this.getCity(i);
                    //def city2
                    cityTo = this.getCity(j);
                    //distance between cities
                    tour[i][j] = cityFrom.distanceTo(cityTo);
                }
            }
        }
        return tour;
    }

    public int[][] toBranchMatrix(){

        int[][] tour = new int[tourSize()][tourSize()];

        City cityFrom;
        City cityTo;

        int INF = Integer.MAX_VALUE;

        for (int i = 0; i <= tourSize()-1; i++){
            for (int j = 0; j <= tourSize()-1; j++){
                if (i == j){
                    tour[i][j] = INF;
                } else {
                    //def city1
                    cityFrom = this.getCity(i);
                    //def city2
                    cityTo = this.getCity(j);
                    //distance between cities
                    tour[i][j] = (int) (cityFrom.distanceTo(cityTo));
                }
            }
        }
        return tour;
    }

}
