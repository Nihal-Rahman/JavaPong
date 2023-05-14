/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaprojectclient;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;

/**
 *
 * @author Nihal Rahman and Rakeeb Hossain
 */


public class Ball extends Rectangle {

    Random random;
    int xVelocity = 0 ;
    int yVelocity = 0 ;
    int initialSpeed = 2;

    Ball(int x, int y, int width, int height){
        super(x,y,width,height);
        random = new Random();
        
        xVelocity = random.nextInt(4);
        
        if(xVelocity == 0){
            xVelocity = 1;
        }
        
	int randomXDirection = random.nextInt(2);
	if(randomXDirection == 0)
	randomXDirection--;
	setXDirection(randomXDirection*initialSpeed);
        
        yVelocity = random.nextInt(4);
        
        if(yVelocity == 0){
            yVelocity = 1;
        }
		
	int randomYDirection = random.nextInt(2);
	if(randomYDirection == 0)
	randomYDirection--;
	setYDirection(randomYDirection*initialSpeed);
        
        
    }
        
    public void setXDirection(int randomXDirection) {
            xVelocity = randomXDirection;
    }
    public void setYDirection(int randomYDirection) {
            yVelocity = randomYDirection;
    }
    public void move() {
            x += xVelocity;
            y += yVelocity;
    }
    public void draw(Graphics g) {
            g.setColor(Color.black);
            g.fillOval(x, y, height, width);
    }
}