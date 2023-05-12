/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package javaprojectclient;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.*;
import java.util.*;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.Timer;

/**
 *
 * @author nihalrahman
 */
public class JavaProjectClient implements Runnable{

    static Socket sock;
    static Scanner sin;
    static PrintWriter sout;
    static Thread t = new Thread();
    static WaitingRoom wr;
    
    
    public static void main(String[] args) {
        JavaProjectClient client = new JavaProjectClient();
        client.run();
    }
    
    
    @Override
    public void run(){ 
        new LoginInterface();
    }
    
}


class checkReady implements Runnable{
    @Override
    public void run(){
        String messages;
        try{
            while(JavaProjectClient.sin.hasNext()){
                if(JavaProjectClient.sin.nextLine().equals("Ready")){
                    JavaProjectClient.t.interrupt();
                    JavaProjectClient.wr.jf.setVisible(false);
                    new GameGUI();
                }
            }
        } catch(Exception e){}
    }
}


class LoginInterface{
    static JFrame jf = new JFrame();
    static TextField user = new TextField();
    static TextField server = new TextField();
    
    LoginInterface(){
        jf = new JFrame("Login Screen");
        JPanel jp = new JPanel();
        jp.setLayout(new GridLayout(4, 2, 4,4));
        
        JLabel userLabel = new JLabel("Username:");
        JLabel serverLabel = new JLabel("Server:");
        Button submit = new Button("Submit");
        submit.addActionListener(new ButtonPressed());
        
        jp.add(userLabel);
        jp.add(user);
        jp.add(serverLabel);
        jp.add(server);
        
        jp.add(submit);
        
        jf.add(jp);
        jf.setSize(500,500);
        jf.setVisible(true);
        
    }
}


class ButtonPressed implements ActionListener{
    @Override
    public void actionPerformed(ActionEvent e){  
        String username = LoginInterface.user.getText();
        String server = LoginInterface.server.getText();

        if(server != null){
            try{
                JavaProjectClient.sock = new Socket(server,5190);
                JavaProjectClient.sin = new Scanner(JavaProjectClient.sock.getInputStream());
                JavaProjectClient.sout = new PrintWriter(JavaProjectClient.sock.getOutputStream(), true);
            } catch (IOException ex){
            System.out.println("Sorry, Connection failed.");
            }
        }

        JavaProjectClient.sout.println(username);
        LoginInterface.jf.setVisible(false);
        String user = "";
        String wins = "";
        String loss = "";
        int counter = 0;
        try{
            while(JavaProjectClient.sin.hasNext()){
                if(counter == 0){
                   user = JavaProjectClient.sin.nextLine();
                }
                if(counter == 1){
                   wins = JavaProjectClient.sin.nextLine();
                }
                if(counter == 2){
                   loss = JavaProjectClient.sin.nextLine();
                   break;
                }
                counter += 1;
            }
        } catch(Exception e1){
            System.out.print(e1);
        }
        
        new WaitingRoom(user, wins, loss);

        checkReady isReady = new checkReady();
        JavaProjectClient.t = new Thread(isReady);
        JavaProjectClient.t.start();

    }
}

class WaitingRoom{
    
    static JFrame jf = new JFrame("Waiting Room");
    
    WaitingRoom(String username, String numWins, String numLoss){
        JPanel jp = new JPanel();
        jp.setLayout(new GridLayout(4, 1, 4,4));
        JLabel message = new JLabel("Waiting for another user to connect...");
        JLabel user = new JLabel("Username: " + username);
        JLabel wins = new JLabel("Number of Wins: " + numWins);
        JLabel loss = new JLabel("Number of Losses: " + numLoss);
        
        jp.add(message);
        jp.add(user);
        jp.add(wins);
        jp.add(loss);
        
        jf.add(jp);
        jf.setSize(500,500);
        jf.setVisible(true);
        
    }
        
}

class GameGUI{
    
    static JFrame jf = new JFrame("Game On!");
    
    GameGUI(){
        Test g = new Test();
        jf.setSize(500,500);
        
        
        Test g1 = new  Test();
        jf.add(g1);
        jf.setVisible(true);
    }
}

class Test extends JPanel implements ActionListener, KeyListener{
    Timer tm=new Timer(2,this);
    
    int x=0,y=0,velX=0,velY=0;
    
    Test(){
        tm.start();
        addKeyListener(this);
        setFocusable(true); 
        setFocusTraversalKeysEnabled(false); 
    }
    
     public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setColor(Color.red);
        g.fillRect(x,y,50,30);
    }     
     
    public void actionPerformed(ActionEvent e){
        
        
        if(y < 0)
        {
            velY=0;
            y = 0;  
        }

        if(y > 450)
        {
            velY=0;
            y = 450;  
        }

        y=y+velY;
        repaint();
        
    }
    
    public void keyPressed(KeyEvent e){
        int c = e.getKeyCode();

        if (c == KeyEvent.VK_UP)
           {
               velX = 0;
               velY = -3; // means up 
               JavaProjectClient.sout.println(String.valueOf(velY));
           }

        if (c == KeyEvent.VK_DOWN)  
        {
            velX = 0;
            velY = 3; 
            JavaProjectClient.sout.println(String.valueOf(velY));
        }
        System.out.println(JavaProjectClient.sin.nextLine());
        
    }
    public void keyTyped(KeyEvent e){}
    public void keyReleased(KeyEvent e){
        velX=0;
        velY=0;
     }
}


