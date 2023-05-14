/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaprojectclient;

import java.awt.*;

/**
 *
 * @author Nihal Rahman and Rakeeb Hossain
 */

public class Score extends Rectangle{
    int player1;
    int player2;

    public void draw(Graphics g) {
        g.setColor(Color.white);
        g.setFont(new Font("Arial",Font.PLAIN,60));

        g.drawLine(300, 0, 300, 550);

        g.drawString(String.valueOf(player1/10)+String.valueOf(player1%10), 215, 50);
        g.drawString(String.valueOf(player2/10)+String.valueOf(player2%10), 320, 50);
    }
}
