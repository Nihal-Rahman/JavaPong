/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package javaprojectserver;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import java.time.LocalDateTime;

public class JavaProjectServer{
    private static final String URL = "jdbc:mariadb://localhost:3306/JPong";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "nihal1234";
    private static final int PORT = 5190;
    private int numPlayers = 0;
    private int maxPlayers = 2;
    private ServerSocket server;
    private Connection conn = null;
    private ReadFromClient p1ReadRunnable;
    private ReadFromClient p2ReadRunnable;
    private WriteToClient p1WriteRunnable;
    private WriteToClient p2WriteRunnable;
    private Socket p1Socket;
    private Socket p2Socket;
    private String p1y = String.valueOf(0.0);
    private String p2y = String.valueOf(0.0);
    
    
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

                sout.println(userData[0]);
                sout.println(userData[1]);
                sout.println(userData[2]);
                sout.println(numPlayers);
               
                
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
    
    
    private String[] obtainUser(Connection conn, String username) throws SQLException {
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
                        p1y = dataIn.nextLine();
                    }
                    else{
                        p2y = dataIn.nextLine();

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
                        System.out.println(p2y);
                        dataOut.println(Double.parseDouble(p2y));
                        //dataOut.println(p2y);
                    }
                    else{
                        //dataOut.println(p1y);
                        dataOut.println(Double.parseDouble(p1y));
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
