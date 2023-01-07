package org.example.algorithm.Annealing;

import org.example.TSP.City;
import org.example.TSP.Tour;
import org.example.GUI.TSPMainFrame;

import javax.swing.*;

public class Annealing {



    private static double acceptanceProbability(double energy, double newEnergy, double temperature) {
        // If the new solution is better, accept it
        if (newEnergy < energy) {
            return 1.0;
        }
        // If the new solution is worse, calculate an acceptance probability
        return Math.exp((energy - newEnergy) / temperature);
    }

    public static void simulateAnnealing(JTextField firstParam, JTextField secondParam, JTextArea areaTours, Tour primalTour){
        // define variables
        int bestK = 0, k = 0;
        double initTemp = 0, temp, coolingRate = 0;
        long startTime = System.nanoTime();
        // Set initial temp
        if (firstParam.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Задайте початкову температуру");
            return;
        } else {
            initTemp = Double.parseDouble(firstParam.getText());
        }

        temp = initTemp;

        // Cooling rate

        if (secondParam.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Задайте швидкість охолодження");
            return;
        } else {
            coolingRate = Double.parseDouble(secondParam.getText());
        }

        // Initialize intial solution
        Tour currentSolution = primalTour;

        areaTours.append("Initial solution distance: " + TSPMainFrame.df.format(currentSolution.getDistance())+"\n");
        areaTours.append("\n");
        // Set as current best
        Tour best = new Tour(currentSolution.getTour());
        // Loop until system has cooled
        while (temp > 0) {
            k++;
            areaTours.append("Iteration number: "+k+"\n");
            // Create new neighbour tour
            Tour newSolution = new Tour(currentSolution.getTour());
            // Get a random positions in the tour
            int tourPos1 = (int) (newSolution.tourSize() * Math.random());
            int tourPos2 = (int) (newSolution.tourSize() * Math.random());
            // Get the cities at selected positions in the tour
            City citySwap1 = newSolution.getCity(tourPos1);
            City citySwap2 = newSolution.getCity(tourPos2);
            // Swap them
            newSolution.setCity(tourPos2, citySwap1);
            newSolution.setCity(tourPos1, citySwap2);
            // Get energy of solutions
            double currentEnergy = currentSolution.getDistance();
            double neighbourEnergy = newSolution.getDistance();

            areaTours.append("New proposed solution distance: " + TSPMainFrame.df.format(newSolution.getDistance()) + "\n");

            TSPMainFrame.resultContainer.put(k, newSolution.getDistance());
            // Decide if we should accept the neighbour
            if (acceptanceProbability(currentEnergy, neighbourEnergy, temp) > Math.random()) {
                currentSolution = new Tour(newSolution.getTour());
            }

            areaTours.append("New accepted solution distance: " + TSPMainFrame.df.format(currentSolution.getDistance()) + "\n");
            // Keep track of the best solution found
            if (currentSolution.getDistance() < best.getDistance()) {
                best = new Tour(currentSolution.getTour());
                bestK = k;
            }

            areaTours.append("New best solution distance: " + TSPMainFrame.df.format(best.getDistance()) + "\n");
            areaTours.append("\n");
            // Cool system
            temp = temp - coolingRate;
        }

        areaTours.append("Final solution distance: " + best.getDistance()+  "\n");
        System.out.println("Tour: " + best);

        final Tour tour = best;

        TSPMainFrame.createAndShowGUI(tour);

        long estimatedTime = System.nanoTime() - startTime;
        areaTours.append("Annealing time: " + (estimatedTime/Math.pow(10, 9)) );

        String msg = "Кількість ітерацій: " + k + "\n" + "Найкраще рішення: " + TSPMainFrame.df.format(best.getDistance()) +
                "\n" + "Номер ітерації кращого рішення: " + bestK + "\n" + "Час роботи алгоритму: " +
                TSPMainFrame.df.format(estimatedTime/Math.pow(10, 9)) + " секунд";

        Object[] options = {"Додати", "Закрити"};
        int choice = JOptionPane.showOptionDialog(null, msg, "Result", JOptionPane.YES_NO_OPTION,
                                                    JOptionPane.INFORMATION_MESSAGE, null, options, options[1]);
        org.example.GUI.TSPMainFrame.checkAddResult(choice);
    }
}
