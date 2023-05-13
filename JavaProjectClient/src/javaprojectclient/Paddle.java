/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaprojectclient;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author hossa
 */
public class Paddle {
    private double x,y,wsize,hsize;
    private Color color;
    
    Paddle(double a, double b, double w, double h, Color c){
        x= a;
        y = b;
        wsize = w;
        hsize = h;
        color = c;
    }
    
    public void drawSprite(Graphics2D g2d){
        Rectangle2D.Double square = new Rectangle.Double(x, y, wsize, hsize);
        g2d.setColor(color);
        g2d.fill(square);
    }
    
    public void moveV(double n){
        y+=n;
    }
    
    public void setY(double n){
        y = n;
    }
    
    public double getY(){
        return y;
    }
}
