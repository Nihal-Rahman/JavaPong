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
import java.awt.geom.Rectangle2D;
import javax.swing.Timer;

/**
 *
 * @author nihalrahman
 */
public class JavaProjectClient{

    static Socket sock;
    static Scanner sin;
    static PrintWriter sout;
    static Thread t = new Thread();
    static WaitingRoom wr;
    static int playerID;
    static String userName;
    
    
    public static void main(String[] args) {
        new LoginInterface();
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
        server = "localhost";
        
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
                }
                if(counter == 3){
                    JavaProjectClient.playerID = Integer.parseInt(JavaProjectClient.sin.nextLine());
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
        JavaProjectClient.userName = username;
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


