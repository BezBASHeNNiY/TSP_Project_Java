package org.example.algorithm.AntColonyOptimization;

import org.example.GUI.TSPMainFrame;
import org.example.TSP.Tour;
import org.example.TSP.TourManager;

import javax.swing.*;
import java.util.*;
import java.util.stream.IntStream;

import static org.example.GUI.TSPMainFrame.*;

public class AntColonyOptimization {

    private double c = 1.0;
    private double alpha = 1;
    private double beta = 5;
    private double evaporation = 0.5;
    private double Q = 500;
    private double antFactor;
    private double randomFactor;
    private double currTrail;

    private int maxIterations;
    int choice = 0;
    int globali;

    private int numberOfCities;
    private int numberOfAnts;
    private double graph[][];
    private double trails[][];
    private List<Ant> ants = new ArrayList<>();
    private Random random = new Random();
    private double probabilities[];

    private int currentIndex;
    public Tour primalTour = org.example.GUI.TSPMainFrame.primalTour;

    private int[] bestTourOrder;
    private double bestTourLength;
    private Tour bestTour;

    public AntColonyOptimization(Tour tour, double antParam, double randomParam) {
        graph = tour.toMatrix();
        antFactor = antParam;
        randomFactor = randomParam;

        numberOfCities = graph.length;
        numberOfAnts = (int) (numberOfCities * antFactor);

        trails = new double[numberOfCities][numberOfCities];
        probabilities = new double[numberOfCities];
        IntStream.range(0, numberOfAnts)
                .forEach(i -> ants.add(new Ant(numberOfCities)));
    }
    /**
     * Perform ant optimization
     */
    public void startAntOptimization(int firstPar, JTextArea textAreaTour) {
        //start time count
        long startTime = System.nanoTime();

        maxIterations = firstPar;
        JTextArea areaTour = textAreaTour;

        solve(maxIterations, areaTour);

        //time of algorithm work
        long estimatedTime = System.nanoTime() - startTime;
        TSPMainFrame.jTextAreaTours.append("Algorithm time: " + (estimatedTime / Math.pow(10, 9)));

        //Msg for option dialog
        String msg = "Найкраще рішення: " + df.format(bestTour.getDistance()) +
                "\n" + "Час роботи алгоритму: " + df.format(estimatedTime / Math.pow(10, 9)) +
                " секунд" + "\n" + "Додати ?";

        //option dialog with addResults(to diagram)
        Object[] options = {"Додати", "Закрити"};
        choice = JOptionPane.showOptionDialog(null, msg, "Result", JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, options, options[1]);
        checkAddResult(choice);
        createAndShowGUI(bestTour);
    }
    /**
     * Use this method to run the main logic
     */
    public Tour solve(int maxIterations, JTextArea areaTour) {
        setupAnts();
        clearTrails();
        IntStream.range(0, maxIterations)
                .forEach(i -> {
                    areaTour.append("Iteration : "+i+"\n");

                    moveAnts();
                    updateTrails();
                    updateBest();
                    //add tour info
                    areaTour.append(String.valueOf(bestTourLength)+"\n\n");
                    TSPMainFrame.resultContainer.put(i, bestTourLength);
                });
        System.out.println();
        System.out.println("Best tour order: " + toTour(bestTourOrder));

        bestTour = toTour(bestTourOrder.clone());

        return bestTour ;
    }
    /**
     * Prepare ants for the simulation
     */
    private void setupAnts() {
        IntStream.range(0, numberOfAnts)
                .forEach(i -> {
                    ants.forEach(ant -> {
                        ant.clear();
                        ant.visitCity(-1, random.nextInt(numberOfCities));
                    });
                });
        currentIndex = 0;
    }

    /**
     * At each iteration, move ants
     */
    private void moveAnts() {
        IntStream.range(currentIndex, numberOfCities - 1)
                .forEach(i -> {
                    ants.forEach(ant -> ant.visitCity(currentIndex, selectNextCity(ant)));
                    currentIndex++;
                });
    }

    /**
     * Select next city for each ant
     */
    private int selectNextCity(Ant ant) {
        int t = random.nextInt(numberOfCities - currentIndex);
        if (random.nextDouble() < randomFactor) {
            OptionalInt cityIndex = IntStream.range(0, numberOfCities)
                    .filter(i -> i == t && !ant.visited(i))
                    .findFirst();
            if (cityIndex.isPresent()) {
                return cityIndex.getAsInt();
            }
        }
        calculateProbabilities(ant);
        double r = random.nextDouble();
        double total = 0;
        for (int i = 0; i < numberOfCities; i++) {
            total += probabilities[i];
            if (total >= r) {
                return i;
            }
        }
        throw new RuntimeException("There are no other cities");
    }

    /**
     * Calculate the next city picks probabilites
     */
    public void calculateProbabilities(Ant ant) {
        int i = ant.trail[currentIndex];
        double pheromone = 0.0;
        for (int l = 0; l < numberOfCities; l++) {
            if (!ant.visited(l)) {
                pheromone += Math.pow(trails[i][l], alpha) * Math.pow(1.0 / graph[i][l], beta);
            }
        }
        for (int j = 0; j < numberOfCities; j++) {
            if (ant.visited(j)) {
                probabilities[j] = 0.0;
            } else {
                double numerator = Math.pow(trails[i][j], alpha) * Math.pow(1.0 / graph[i][j], beta);
                probabilities[j] = numerator / pheromone;
            }
        }
    }

    /**
     * Update trails that ants used
     */
    private void updateTrails() {
        for (int i = 0; i < numberOfCities; i++) {
            for (int j = 0; j < numberOfCities; j++) {
                trails[i][j] *= evaporation;
            }
        }
        for (Ant a : ants) {
            currTrail = a.trailLength(graph);
            double contribution = Q / a.trailLength(graph);
            for (int i = 0; i < numberOfCities - 1; i++) {
                trails[a.trail[i]][a.trail[i + 1]] += contribution;
            }
            trails[a.trail[numberOfCities - 1]][a.trail[0]] += contribution;
        }
    }

    /**
     * Update the best solution
     */
    private void updateBest() {
        if (bestTourOrder == null) {
            bestTourOrder = ants.get(0).trail;
            bestTourLength = ants.get(0).trailLength(graph);
        }
        for (Ant a : ants) {
            if (a.trailLength(graph) < bestTourLength) {
                bestTourLength = a.trailLength(graph);
                bestTourOrder = a.trail.clone();
            }
        }
    }

    /**
     * Clear trails after simulation
     */
    private void clearTrails() {
        IntStream.range(0, numberOfCities)
                .forEach(i -> {
                    IntStream.range(0, numberOfCities)
                            .forEach(j -> trails[i][j] = c);
                });
    }


    public Tour toTour(int[] tourOrder){
        Tour bestTour = new Tour();
        TourManager currTM = new TourManager();

        currTM.Clear();

        for (int i = 0; i <= (tourOrder.length)-1 ; i++){
            int tourPos = (tourOrder[i]);
            currTM.addCity(primalTour.getCity(tourPos));
        }
        bestTour.generate(currTM);
        //System.out.println(bestTour);

        return bestTour;
    }
}
