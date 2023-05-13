/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaprojectclient;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.PrintWriter;
import java.util.Scanner;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;

/**
 *
 * @author hossa
 */
public class GameGUI  {
    static JFrame jf = new JFrame("Player: " + JavaProjectClient.playerID);
    private Paddle player1;
    private Paddle player2;
    private Ball ball;
    private DrawingComponent dc;
    private Timer t;
    private boolean down = false;
    private boolean up = false;
    private ReadFromServer rfsRunnable;
    private WriteToServer wtsRunnable;
    
    GameGUI(){
        jf.setSize(600,550);
        rfsRunnable = new ReadFromServer(JavaProjectClient.sin);
        wtsRunnable = new WriteToServer(JavaProjectClient.sout);
        createPaddles();
        ball = new Ball(300,225,30,30); 

        dc = new DrawingComponent();
        jf.add(dc);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
        
        animationSetUp();
        keyActions();
        
        Thread readThread = new Thread(rfsRunnable);
        Thread writeThread = new Thread(wtsRunnable);
        
        readThread.start();
        writeThread.start();
    }
    
    private void createPaddles(){
        if(JavaProjectClient.playerID == 1){
            player1 = new Paddle(15,0,25,100,Color.BLUE);
            player2 = new Paddle(550,0,25,100,Color.RED);
        }
        else{
            player2 = new Paddle(15,0,25,100,Color.BLUE);
            player1 = new Paddle(550,0,25,100,Color.RED);
        }
    }
    
    
    private class DrawingComponent extends JComponent{
        protected void paintComponent(Graphics g){
            Graphics2D g2d = (Graphics2D) g;
            player1.drawSprite(g2d);
            player2.drawSprite(g2d);
            ball.draw(g2d);
        }
    }
    
    private void animationSetUp(){
        ActionListener al = new ActionListener(){
            public void actionPerformed(ActionEvent a){
                int speed = 3;
                if(down){
                    if(player1.getY() > 400){
                        speed = 0;
                    }
                    player1.moveV(speed);
                    
                }
                if(up){
                    if(player1.getY() < 0){
                        speed = 0;
                    }
                    player1.moveV(-speed);
                }
                ball.setXDirection(speed);
                ball.move();
                dc.repaint();
            }
        };
        
        t = new Timer(5, al);
        t.start();
    }
    
    private void keyActions(){
        KeyListener kl = new KeyListener(){
            public void keyTyped(KeyEvent e){}
            
            public void keyPressed(KeyEvent e){
                int c = e.getKeyCode();
                
                if(c == KeyEvent.VK_DOWN){
                    down = true;
                }
                if(c == KeyEvent.VK_UP){
                    up = true;
                }
            }
            
            public void keyReleased(KeyEvent e){
                int c = e.getKeyCode();
                
                if(c == KeyEvent.VK_DOWN){
                    down = false;
                }
                if(c == KeyEvent.VK_UP){
                    up = false;
                }
            }
        };
        
        jf.addKeyListener(kl);
        jf.setFocusable(true);
    }
    
    class ReadFromServer implements Runnable {
        private Scanner dataIn;
        
        ReadFromServer(Scanner in){
            dataIn = in;
        }
        
        public void run(){
            while(true){
                if(player2 != null){
                    String value;
                    try{
                        while(((value = dataIn.nextLine()) != null)|| !(value = dataIn.nextLine()).isEmpty()){
                            player2.setY(Double.parseDouble(value));
                        }
                    }catch(Exception e){}

                }

            }
        }
    }

    class WriteToServer implements Runnable {
        private PrintWriter dataOut;
        
        WriteToServer(PrintWriter out){
            dataOut = out;
        }
        
        public void run(){
            try{
                while(true){
                    if(player1 != null){
                        dataOut.println(player1.getY());
                    }
                    try{
                        Thread.sleep(25);
                    }catch(InterruptedException e){
                        System.out.println(e);
                    }
                }
            }catch(Exception ex){
                System.out.println(ex);
            }
        }
    }
    
}
