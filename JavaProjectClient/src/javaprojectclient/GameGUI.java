/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaprojectclient;

import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;
import java.util.Scanner;
import javax.swing.*;

/**
 *
 * @author Nihal Rahman and Rakeeb Hossain
 */
public class GameGUI  {
    private static JFrame jf = new JFrame("Player: " + JavaProjectClient.playerID);
    private Paddle player1;
    private Paddle player2;
    private Ball ball;
    private Score score;
    static DrawingComponent dc;
    private Timer t;
    private boolean down = false;
    private boolean up = false;
    private ReadFromServer rfsRunnable;
    private WriteToServer wtsRunnable;
    static boolean won = false;
    
    GameGUI(){ 
        gameScreen();
    }
        
    private void gameScreen() {
        jf.setSize(600,550);
        rfsRunnable = new ReadFromServer(JavaProjectClient.sin);
        wtsRunnable = new WriteToServer(JavaProjectClient.sout);
        createPaddles();
        createBall();
        score = new Score();
        dc = new DrawingComponent();
        jf.add(dc);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
        
        Thread readThread = new Thread(rfsRunnable);
        Thread writeThread = new Thread(wtsRunnable);
        
        readThread.start();
        writeThread.start();
        
        animationSetUp();
        keyActions();
    }
    
    private void createPaddles(){
        if(JavaProjectClient.playerID == 1){
            player1 = new Paddle(15,225,25,100,Color.BLUE);
            player2 = new Paddle(550,225,25,100,Color.RED);
        }
        else{
            player2 = new Paddle(15,225,25,100,Color.BLUE);
            player1 = new Paddle(550,225,25,100,Color.RED);
        }
    }
    
    private void createBall() {
        ball = new Ball(285,210,30,30); 
    }
    // Initial Graphic at Start
    private class DrawingComponent extends JComponent{
        protected void paintComponent(Graphics g){
            Graphics2D g2d = (Graphics2D) g;
            player1.drawSprite(g2d);
            player2.drawSprite(g2d);
            ball.draw(g2d);
            score.draw(g2d);
        }
    }
    // Continuously Animates and Checks for collisions
    private void animationSetUp(){
        // Everytime Timer ticks the action is Performed
        ActionListener al = new ActionListener(){
            public synchronized void actionPerformed(ActionEvent a){
                int speed = 3;
                if(down){
                    if(player1.getYValue() > 400){
                        speed = 0;
                    }
                    player1.moveV(speed);
                    dc.repaint();
                    
                }
                if(up){
                    if(player1.getYValue() < 0){
                        speed = 0;
                    }
                    player1.moveV(-speed);
                    dc.repaint();
                }
                
                ball.move();
                checkCollision();
                dc.repaint();
            }
        };
        t = new Timer(5, al);
        t.start();
    }
    // Reads for key inputs 
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
    
    public void checkCollision(){
        // Collision w/ Paddle 1
        if(((ball.y >= player1.getYValue()) && (ball.y <= player1.getYValue() + 100)) && (ball.x >= 15 && ball.x <= 40)) {
            ball.x = 65;
            ball.setXDirection(-ball.xVelocity);
        }
        // Collision w/ Paddle 2
        else if(((ball.y >= player2.getYValue()) && (ball.y <= player2.getYValue() + 100)) && (ball.x >= 550 && ball.x <= 575)) {
            ball.x = 520;
            ball.setXDirection(-ball.xVelocity);
        }
        
        // Checks for point scored
        if (ball.x < 0) {
            createBall();
            createPaddles();
            score.player2++;
            ball.setXDirection(-ball.xVelocity);
	}
        else if(ball.x > 570) {
            createBall();
            createPaddles();
            score.player1++;
            ball.setXDirection(-ball.xVelocity);
	}
        
        // Checks for collision with top and bottom
        if(ball.y < 0){
            ball.y = 0;
            ball.setYDirection(-ball.yVelocity);
        }
        else if(ball.y > 500) {
            ball.y = 500;
            ball.setYDirection(-ball.yVelocity);
	}
//         Checks for win
        if (score.player1 > 10 || score.player2 > 10) {
            if(score.player1 > 10 && JavaProjectClient.playerID == 1){
                won = true;
                JavaProjectClient.sout.println("Winner");
                JavaProjectClient.sout.println(JavaProjectClient.userName);
            }
            else if(score.player2 > 10 && JavaProjectClient.playerID == 2){
                won = true;
                JavaProjectClient.sout.println("Winner");
                JavaProjectClient.sout.println(JavaProjectClient.userName);
            }
            jf.setVisible(false);
            t.stop();
            new EndScreen();
        } 
    }
    
    // Clients reads eachother's paddles positions
    // Client 2 will read in ball position
    class ReadFromServer implements Runnable {
        private Scanner dataIn;
        
        ReadFromServer(Scanner in){
            dataIn = in;
        }
        
        public void run(){
            while(true){
                if(player2 != null){
                    try{
                        String message;
                        while((message = dataIn.nextLine()) instanceof String){
                            if(message.equals("Here is paddle info")){
                                int value = dataIn.nextInt();
                                player2.setY(value);
                            }
                            else if(message.equals("Here is ball info")){
                                ball.x = dataIn.nextInt();
                                ball.y = dataIn.nextInt();
                                dc.repaint();
                            }
                        }
                    }catch(Exception e){}
                }
            }
        }
    }
    
    // Clients send paddle positions
    // Client 1 sends ball position
    class WriteToServer implements Runnable {
        private PrintWriter dataOut;
        
        WriteToServer(PrintWriter out){
            dataOut = out;
        }
        
        public void run(){
            try{
                while(true){
                    if(player1 != null){
                        dataOut.println(player1.getYValue());
                        if(JavaProjectClient.playerID == 1){
                            dataOut.println(ball.x);
                            dataOut.println(ball.y);
                        }
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
// EndScreen Displays winner and Leaderboard
class EndScreen {
    EndScreen() { 
        JFrame jf = new JFrame("Game Over");
        JPanel jp = new JPanel();
        jp.setLayout(new GridLayout(2, 1, 4,4));
        JLabel winner = new JLabel();
        if(GameGUI.won){
            winner.setText("You Won!");
        }
        else{
            winner.setText("You Lost!");
        }
        jp.add(winner);
        TextArea leaderboard = new TextArea();
        leaderboard.append("Here's the updated leaderboard:" +"\n");
        for (String[] data: JavaProjectClient.leaderBoardData) {
            leaderboard.append("Username: " + data[0] + " Wins: "+ data[1] + " Loss: " + data[2] + "\n");
        }
        jp.add(leaderboard);
        
        jf.setSize(1000,1000);
        jf.add(jp);
        
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
