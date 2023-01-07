/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.example.GUI;

import org.example.TSP.City;
import org.example.TSP.Tour;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

/**
 *
 * @author volch
 */
public class TSPGUI {
	static class MyPanel extends JPanel {
		private static final long serialVersionUID = 7615629084996272465L;
		// if you want to increase the picture of the route, increase the koef
		public float koef = 1.6f;
		// pagging/margin of the route image
		int offset = 50;
		// default area size, our coordinates are within range 0-500
		int areaSize = 500;
		private final Tour tour;
		public MyPanel(Tour tour) {
			setBorder(BorderFactory.createLineBorder(Color.black));
			this.tour = tour;
		}
		void showTour(Graphics g){
			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(1.75F));
			// number of the current city in the route, starts at 1
			// for the first city we don't draw a line to the prev city
			int number = 1;
			int prevX = 0, prevY = 0,firstX = 0,firstY = 0;
			int max = (int)(offset + areaSize*koef);
			for (City city: tour.getTour()) {
				g.setColor(Color.BLUE);
				g.fillRect(offset + (int) ((city.getX() - 3)*koef), max - (int) ((city.getY() + 3)*koef), 8, 8);
				g.setColor(Color.RED);
				if (number == 1) {
					firstX = city.getX();
					firstY = city.getY();
				} else {
					g2.draw(new Line2D.Float((int)(prevX*koef + offset), (int)(max - prevY*koef), (int)(city.getX()*koef) + offset, max - (int)(city.getY()*koef)));
				}
				prevX = city.getX();
				prevY = city.getY();
				number++;
			}
			g2.draw(new Line2D.Float((int)(prevX*koef + offset), (int)(max - prevY*koef),(int)(firstX*koef) + offset, max - (int)(firstY*koef)));
		}

		public Dimension getPreferredSize() {
			// we define the size of the window dynamically
			return new Dimension((int)(offset*2 + areaSize*koef), (int)(offset*2 + areaSize*koef));
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawString("Resulting route, length = " + tour.getDistance(), 10, 20);
			g.setColor(Color.BLACK);
			g.drawRect(offset, offset, (int) (areaSize * koef), (int) (areaSize * koef));
			int step = (int) (areaSize * koef / 20);
			// creating grid
			for (int i = 1; i < 20; i++) {
				g.drawLine(offset - 5, i * step + offset, offset + 5 + (int) (areaSize * koef), i * step + offset);
				g.drawLine(offset + i * step, offset - 5, offset + i * step, offset + 5 + (int) (areaSize * koef));
				//draw x and y numbers
				g.drawString(String.valueOf(25 * i), i * step + offset - 10, offset + 15 + (int) (areaSize * koef));
				g.drawString(String.valueOf(0), offset - 10, offset + 15 + (int) (areaSize * koef));
				g.drawString(String.valueOf(500 - (25 * i)), offset - 25, i * step + offset + 5);
				g.drawString(String.valueOf(500), offset + (int) (areaSize * koef) - 10, offset + 15 + (int) (areaSize * koef));
				g.drawString(String.valueOf(500), offset - 25, offset + 5);
			}
			showTour(g);
		}
    }
}

