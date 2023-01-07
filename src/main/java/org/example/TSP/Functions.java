package org.example.TSP;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.util.HashMap;

public class Functions {

    public static XYSeries createSeries(String name, HashMap data){
        XYSeries series;
        series = new XYSeries(name);

        if (data.size() > 250) {
            for (int i = 0; i <= data.size(); i = i + (data.size() / 250)) {
                series.add((double) i, (Double) data.get(i));
                i++;
            }
        }else{
            for (int i = 1; i <= data.size(); i++) {
                series.add((double) i, (Double) data.get(i));
                i++;
            }
        }
        return series;
    }

    public static void addResult(HashMap resultContainer, int k, String algorithmName, XYSeriesCollection dataset){
        dataset.addSeries(Functions.createSeries(algorithmName + " results" + k, resultContainer));
        JOptionPane.showMessageDialog(null, "Рішення №"+k+" додано");
    }

    public static void createCity(int i, javax.swing.JTextArea jTextAreaCity){
        String name;
        name = "City "+(i+1);
        City city = new City(name);
        TourManager.addCity(city);
        jTextAreaCity.append("City #"+(i+1)+":"+"\n");
        jTextAreaCity.append("Name - "+name+"\n");
        jTextAreaCity.append("x - "+ city.getX() +", y - "+ city.getY() +"\n");
        jTextAreaCity.append("\n");
    }

    public static void createTour(int count, javax.swing.JTextArea jTextAreaCity, javax.swing.JTextField jTextFieldCount, int c){
        TourManager.Clear();
        jTextAreaCity.setText("");
        jTextFieldCount.setText(String.valueOf(count));

        c = Integer.parseInt(jTextFieldCount.getText());

        for (int i = 0 ; i < count ; i++ ) {
            createCity(i, jTextAreaCity);
        }
    }


}
