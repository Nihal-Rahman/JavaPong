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
    int xVelocity;
    int yVelocity;
    int initialSpeed;

    Ball(int x, int y, int width, int height){
        super(x,y,width,height);
        random = new Random();
        // Random starting Ball x Velocity
        initialSpeed = random.nextInt(3);
        if(initialSpeed == 0){
            initialSpeed = 1;
        }
        // Random starting Ball x direction
	int randomXDirection = random.nextInt(2);
	if(randomXDirection == 0)
            randomXDirection--;
	setXDirection(randomXDirection*initialSpeed);
        
        // Random starting Ball x Velocity
        initialSpeed = random.nextInt(4);
        if(initialSpeed == 0){
            initialSpeed = 1;
        }
	// Random starting Ball y direction	
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