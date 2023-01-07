package org.example.algorithm.Branches;

import org.example.TSP.Tour;
import org.example.TSP.TourManager;

import java.text.DecimalFormat;
import java.util.*;

import static org.example.GUI.TSPMainFrame.createAndShowGUI;
import static org.example.GUI.TSPMainFrame.primalTour;

public class BranchAndBounds {
    private static final int M = -1;
    private static final String SYMBOL = "M";

    public static Tour start(int[][] path) {
        long start = System.currentTimeMillis();
        Stack<Integer> stack = new Stack<>();
        System.out.println("Read graph to file:");
        int[][] matrix = path;
        int[][] clone = clone(matrix);
        List<Integer> v = new ArrayList<>();
        for (int i = 1; i <= matrix.length; i++) {
            v.add(i);
        }
        printMatrix(matrix);
        int count = 1;
        while (matrix.length > 1) {
            System.out.println("\n##########################################");
            System.out.println("STAGE #" + count++ + ":");
            int[] di = getMinArray(matrix, false);
            matrix = diffMatrix(matrix, di, false);
            int[] dj = getMinArray(matrix, true);
            matrix = diffMatrix(matrix, dj, true);
            System.out.println("\ndi: " + Arrays.toString(di) + ";");
            System.out.println("dj: " + Arrays.toString(dj) + ";");
            System.out.println("\nMatrix after diff:");
            printMatrix(matrix);
            matrix = getPath(matrix, stack, v);
            System.out.println("\nReduction matrix:");
            printMatrix(matrix);
            if (matrix.length == 1) {
                push(stack, v.remove(0));
            }
            System.out.print("Path now: ");
            printStack(stack);
        }
        if (!stack.empty()) {
            stack.push(stack.get(0));
        }
        System.out.println("\n##########################################");
        System.out.println("\nAnswer:");
        System.out.println(stack);
        System.out.print("Path: ");
        Tour finalTour = toTour(stack);
        System.out.println(finalTour);
        System.out.println("Sum:  " + getSum(stack, clone));
        start = System.currentTimeMillis() - start;
        System.out.printf("Spent time: %d.%s sec.;", start / 1000, new DecimalFormat("000").format(start % 1000));

        createAndShowGUI(finalTour);
        return finalTour;

    }

    private static int[][] clone(int[][] martrix) {
        int[][] clone = new int[martrix.length][];
        int count = 0;
        for (int[] line : martrix) {
            clone[count++] = line.clone();
        }
        return clone;
    }

    private static int getSum(Stack<Integer> stack, int[][] clone) {
        int sum = 0;
        if (!stack.empty()) {
            int v = stack.pop();
            while (!stack.empty()) {
                sum += clone[v - 1][stack.peek() - 1];
                v = stack.pop();
            }
        }
        return sum;
    }

    private boolean isNumber(String number) {
        return number != null && number.matches("\\d+");
    }

    private static void printStack(Stack<Integer> stack) {
        StringBuilder sb = new StringBuilder();
        if (!stack.empty()) {
            for (Integer num : stack) {
                sb.append(num).append(" -> ");
            }
            sb.delete(sb.length() - 4, sb.length());
        }
        System.out.println(sb.toString());
    }

    private static int[][] getPath(int[][] matrix, Stack<Integer> stack, List<Integer> v) {
        int indexI = 0;
        int indexJ = 0;
        int max = 0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[i][j] == 0) {
                    int sum = getMin(matrix, i, false, j) + getMin(matrix, j, true, i);
                    if (sum > max) {
                        max = sum;
                        indexI = i;
                        indexJ = j;
                    }
                }
            }
        }
        matrix[indexJ][indexI] = M;
        matrix = removeLineAndRow(matrix, indexI, indexJ);
        push(stack, v.get(indexI));
        push(stack, v.get(indexJ));
        v.remove(indexI);
        return matrix;
    }

    private static void push(Stack<Integer> stack, int v) {
        if (stack.search(v) == -1) {
            stack.push(v);
        }
    }

    private static int[][] removeLineAndRow(int[][] matrix, int indexI, int indexJ) {
        int[][] result = new int[matrix.length - 1][matrix.length - 1];
        int countI = 0;
        int countJ;
        for (int i = 0; i < matrix.length; i++) {
            if (i != indexI) {
                countJ = 0;
                for (int j = 0; j < matrix.length; j++) {
                    if (j != indexJ) {
                        result[countI][countJ++] = matrix[i][j];
                    }
                }
                countI++;
            }
        }
        matrix = result;
        return matrix;
    }

    private static int getMin(int[][] matrix, int index, boolean row, int j) {
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < matrix.length; i++) {
            if (i != j) {
                int number = row ? matrix[i][index] : matrix[index][i];
                if (number != M && number < min) {
                    min = number;
                }
            }
        }
        return min;
    }

    private static int[] getMinArray(int[][] matrix, boolean row) {
        int[] res = new int[matrix.length];
        int count = 0;
        for (int i = 0; i < matrix.length; i++) {
            res[count++] = getMin(matrix, i, row, -1);
        }
        return res;
    }

    private static int[][] diffMatrix(int[][] matrix, int[] d, boolean row) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[i][j] != M) {
                    matrix[i][j] -= row ? d[j] : d[i];
                }
            }
        }
        return matrix;
    }

    private static void printMatrix(int[][] matrix) {
        int length = matrix.length;
        for (int[] line : matrix) {
            System.out.print("[");
            for (int index = 0; index < length; index++) {
                System.out.print(line[index] == M ? SYMBOL : line[index]);
                if (index < length - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("]");
        }
    }


    public static Tour toTour(Stack<Integer> tourOrder){
        Tour bestTour = new Tour();
        TourManager currTM = new TourManager();

        currTM.Clear();

        tourOrder.pop();

        while (!tourOrder.empty()){
            int tourPos = (tourOrder.pop());
            currTM.addCity(primalTour.getCity(tourPos-1));
        }
        bestTour.generate(currTM);
        //System.out.println(bestTour);

        return bestTour;
    }

}

