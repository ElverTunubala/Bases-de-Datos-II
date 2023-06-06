/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trabajo3;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Conexion {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private Connection oracleConnection;

    public Conexion() {
        // Establecer la conexión a MongoDB
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("dbmongo");
        
        // Establecer la conexión a Oracle
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            String url = "jdbc:oracle:thin:@localhost:1521:XE";
            String user = "SYSTEM";
            String password = "yalanda66";
            oracleConnection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public MongoDatabase getDatabase() {
        return database;
    }
    public Connection getOracleConnection() {
        return oracleConnection;
    }

    public void close() {
        // Cerrar la conexión a MongoDB
        mongoClient.close();

        // Cerrar la conexión a Oracle
        try {
            oracleConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
    

