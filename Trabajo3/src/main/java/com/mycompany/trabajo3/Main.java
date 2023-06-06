/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.trabajo3;

import com.mongodb.client.MongoDatabase;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        Conexion connection = new Conexion();
        MongoDatabase database = connection.getDatabase();
        System.out.println("Conexión exitosa a MongoDB: " + database.getName());

        try {
            Connection oracleConnection = connection.getOracleConnection();
            System.out.println("Conexión exitosa a Oracle");

            Estadisticas estadisticas = new Estadisticas(connection, database);

            // Primera generación de estadísticas
            estadisticas.generarEstadisticas();
            System.out.println("Estadísticas generadas exitosamente.");

            estadisticas.listarEstadisticas();

            // Segunda generación de estadísticas
            System.out.println("\nSegunda generación de estadísticas:");
            estadisticas.generarEstadisticas();
            System.out.println("Estadísticas generadas exitosamente.");

            estadisticas.listarEstadisticas();

            oracleConnection.close();
        } catch (SQLException e) {
            System.out.println("Error al conectar con Oracle: " + e.getMessage());
        } finally {
            connection.close();
        }
    }
}

