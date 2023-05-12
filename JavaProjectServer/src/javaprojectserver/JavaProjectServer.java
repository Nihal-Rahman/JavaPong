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
    static int numConnects = 0;
    
    static Map<String, Socket> clients = new HashMap<>();
    
    public static void main(String[] args) {

        Connection conn = null;
        try{
            conn = DriverManager.getConnection(URL,USERNAME,PASSWORD);
            System.out.println("Connected to the database");
        } catch(SQLException e){
            System.out.println("Caught exception: "+e.toString());
        }
        try {
            ServerSocket server = new ServerSocket(PORT);
            while (true){
                Socket client = server.accept(); //Blocking system call
                System.out.println("Got a connection from: "+client.getInetAddress());
                new ProcessConnection(client,conn).start();
            }
                
        } catch (IOException ex) {
            System.out.println("Unable to bind to port "+PORT);
        }
    }
}
class ProcessConnection extends Thread{
    Socket client;
    Connection conn;
    ProcessConnection(Socket newClient, Connection newConn){
        client = newClient;
        conn = newConn;
    }
    @Override
    public void run(){
        try{
            Scanner sin = new Scanner(client.getInputStream());
            PrintStream sout = new PrintStream(client.getOutputStream());
            String username = sin.nextLine();
            
            String[] userData = obtainUser(conn, username);
            
            JavaProjectServer.numConnects += 1;
            System.out.println(JavaProjectServer.numConnects);
            
            sout.println(userData[0]);
            sout.println(userData[1]);
            sout.println(userData[2]);
            sout.println(JavaProjectServer.numConnects);
            
            if(JavaProjectServer.numConnects == 2){
                broadcast("Ready");
            }
            
            /*
            while(true){
                if(sin.hasNext()){
                    String velY = sin.nextLine();
                    //System.out.println(velY);
                    broadcast(velY);
                }
            }
            */
            
            String message;
            while((message = sin.nextLine()) != null){
                broadcast(message);
            }
            
            /*
            client.close();
            sin.close();
            sout.close();
            */
        }catch(Exception e){
            System.out.println(e);
            System.out.println(client.getInetAddress()+" disconnected");
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
        
        JavaProjectServer.clients.put(username, client);
        return data;
    }
    
    
    private void logConnection(String username, String ipAddress, Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO LOGINS (username, ip_address, time) VALUES (?, ?, ?)");
        stmt.setString(1, username);
        stmt.setString(2, ipAddress);
        stmt.setString(3, LocalDateTime.now().toString());
        stmt.executeUpdate();
        stmt.close();
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
}