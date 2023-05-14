/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package javaprojectserver;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import java.util.ArrayList;

/**
 *
 * @author Nihal Rahman and Rakeeb Hossain
 */

public class JavaProjectServer{
    private static final String URL = "jdbc:mariadb://localhost:3306/JPong";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "nihal1234";
    private static final int PORT = 5190;
    private int numPlayers = 0;
    private int maxPlayers = 2;
    private ServerSocket server;
    private static Connection conn = null;
    private ReadFromClient p1ReadRunnable;
    private ReadFromClient p2ReadRunnable;
    private WriteToClient p1WriteRunnable;
    private WriteToClient p2WriteRunnable;
    private static Socket p1Socket;
    private static Socket p2Socket;
    private int p1y = 0;
    private int p2y = 0;
    private int ballx = 300;
    private int bally = 225;
    
    static Map<String, Socket> clients = new HashMap<>();
    
    JavaProjectServer(){
        try{
            conn = DriverManager.getConnection(URL,USERNAME,PASSWORD);
            System.out.println("Connected to the database");
        } catch(SQLException e){
            System.out.println("Caught exception: "+e.toString());
        }

        try {
            server = new ServerSocket(PORT);
                
        } catch (IOException ex) {
            System.out.println("Unable to bind to port "+PORT);
        }
        
        
    }
    
    public static void main(String[] args) {
        JavaProjectServer js = new JavaProjectServer();
        js.acceptConnections();
    }
    
    public void acceptConnections(){
        try{
            while(numPlayers < maxPlayers){
                Socket s = server.accept();
                numPlayers++;
                
                JavaProjectServer.clients.put(String.valueOf(numPlayers), s);
                
                Scanner sin = new Scanner(s.getInputStream());
                PrintStream sout = new PrintStream(s.getOutputStream());
                String username = sin.nextLine();
                
                String[] userData = obtainUser(conn, username);
                ArrayList<String[]> leaderBoardData = getLeaderboard(conn);
                sout.println(leaderBoardData.size());
                sout.println(userData[0]);
                sout.println(userData[1]);
                sout.println(userData[2]);
                sout.println(numPlayers);
               
                for (String[] data: leaderBoardData) {
                    sout.println(data[0]);
                    sout.println(data[1]);
                    sout.println(data[2]);
                }
                
                ReadFromClient rfc = new ReadFromClient(numPlayers, sin);
                WriteToClient wtc = new WriteToClient(numPlayers, sout);
                
                if(numPlayers == 1){
                    p1Socket = s;
                    p1ReadRunnable = rfc;
                    p1WriteRunnable = wtc;
                }
                else{
                    p2Socket = s;
                    p2ReadRunnable = rfc;
                    p2WriteRunnable = wtc;
                    
                    broadcast("Ready");
                    
                    Thread readThread1 = new Thread(p1ReadRunnable);
                    Thread readThread2 = new Thread(p2ReadRunnable);
                    
                    readThread1.start();
                    readThread2.start();
                    
                    Thread writeThread1 = new Thread(p1WriteRunnable);
                    Thread writeThread2 = new Thread(p2WriteRunnable);
                    
                    writeThread1.start();
                    writeThread2.start();
                   
                }
            }
        } catch(Exception ex){
            System.out.println("Connection error");
        }
    }
   
    private static String[] obtainUser(Connection conn, String username) throws SQLException {
        int win = 0;
        int loss = 0;
        String[] data = {username, String.valueOf(0), String.valueOf(0)};
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Leaderboard WHERE username=?");
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();
        boolean isThere = rs.next();
        if(!isThere){
           stmt = conn.prepareStatement("INSERT INTO Leaderboard (username) VALUES (?);");
           stmt.setString(1, username);
           stmt.setString(2, username);
           rs = stmt.executeQuery();      
        }
        else{
            win = rs.getInt("numWins");
            loss = rs.getInt("numLoss");
            data[1] = String.valueOf(win);
            data[2] = String.valueOf(loss);
        }
        
        stmt.close();
        rs.close();
        
        return data;
    }
    
    
    private static ArrayList<String[]> getLeaderboard (Connection conn) throws SQLException{
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Leaderboard");
        ResultSet rs = stmt.executeQuery();
        ArrayList<String[]> leaderboard = new ArrayList<String[]>(); 
        while (rs.next()) {
            String[] data = {"","",""};
            data[0]= rs.getString("userName");
            data[1]= String.valueOf(rs.getInt("numWins"));
            data[2]= String.valueOf(rs.getInt("numLoss")); 
            leaderboard.add(data);
        }
        return leaderboard;
    }
    
    private static void broadcast(String message) {
        for (Socket client : JavaProjectServer.clients.values()) {
            try {
                PrintStream sout = new PrintStream(client.getOutputStream());
                sout.println(message);
            } catch (IOException e) {
                 System.out.println("Unable to broadcast message");
            }
        }
    }
    
    private class ReadFromClient implements Runnable{
        private int playerID;
        private Scanner dataIn;
        
        public ReadFromClient(int pid, Scanner in){
            playerID = pid;
            dataIn = in;
        }
        
        public void run(){
            try{
                while(true){
                    if(playerID==1){
                        p1y = dataIn.nextInt();
                        ballx = dataIn.nextInt();
                        bally = dataIn.nextInt();
                    }
                    else{
                        p2y = dataIn.nextInt();

                    }
                }
            }
            catch(Exception ie){}
        }
    }
    
    private class WriteToClient implements Runnable{
        private int playerID;
        private PrintStream dataOut;
        
        public WriteToClient(int pid, PrintStream out){
            playerID = pid;
            dataOut = out;
        }
        
        public void run(){
            try{
                while(true){
                    if(playerID==1){
                        dataOut.println("Here is paddle info");
                        dataOut.println(p2y);
                    }
                    else{
                        dataOut.println("Here is paddle info");
                        dataOut.println(p1y);
                        dataOut.println("Here is ball info");
                        dataOut.println(ballx);
                        dataOut.println(bally);
                    }
                    try{
                        Thread.sleep(25);
                    }catch(InterruptedException e){
                        System.out.println(e);
                    }
                }
            }
            catch(Exception ie){}
        }
    }
}
