package com.enset.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connexion {

     
    public Connection connect() {
       Connection connection = null;
            // Replace with your database details
            String  url = "jdbc:postgresql://localhost:5555/vectordb";
            String username = "testuser";
            String password = "testpwd";
        try {
            // Attempt to establish a connection
            System.out.println("Connecting to the database...");
            connection = DriverManager.getConnection(url, username, password);

            if (connection != null) {
                System.out.println("Connection established successfully!");
            }
        } catch (SQLException e) {
            // Handle SQL exceptions
            System.err.println("Failed to connect to the database.");
            e.printStackTrace();
        } 
            
        return connection;
    }
}
