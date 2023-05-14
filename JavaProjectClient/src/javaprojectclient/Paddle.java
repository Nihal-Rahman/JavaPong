/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaprojectclient;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 *
 * @author Nihal Rahman and Rakeeb Hossain
 */

public class Paddle extends Rectangle{
    private int x,y,width,height;
    private Color color;
    
    Paddle(int a, int b, int w, int h, Color c){
        super(a, b, w, h);
        x= a;
        y = b;
        width = w;
        height = h;
        color = c;
    }
    
    public void drawSprite(Graphics2D g2d){
        g2d.setColor(color);
        g2d.fillRect(x, y, width, height);
    }
    
    public void moveV(int n){
        y+=n;
    }
    
    public void setY(int n){
        y = n;
    }
    
    public int getYValue(){
        return y;
    }
}
