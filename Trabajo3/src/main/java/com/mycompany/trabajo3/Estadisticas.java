/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trabajo3;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Estadisticas {
    private Conexion connection;
    private MongoDatabase mongoDB;

    public Estadisticas(Conexion connection, MongoDatabase mongoDB) {
        this.connection = connection;
        this.mongoDB = mongoDB;
    }

    public void generarEstadisticas() {
        actualizarEstadisticasPorPais();
        actualizarEstadisticasPorCategoriaYGrupoEtario();
    }

    public void listarEstadisticas() {
        listarEstadisticasPorPais();
        listarEstadisticasPorCategoriaYGrupoEtario();
    }

    private void actualizarEstadisticasPorPais() {
        Connection oracleConnection = connection.getOracleConnection();
        try (Statement statement = oracleConnection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT c.nom_pais, SUM(vt.numero_unidades * vt.precio_unitario) AS total_vendido " +
                "FROM ciudad c " +
                "JOIN sucursal s ON c.nom_ciudad = s.nom_ciudad " +
                "JOIN venta v ON s.codigo_suc = v.cod_suc " +
                "JOIN venta_detalle vt ON v.codigo = vt.codigo_venta " +
                "GROUP BY c.nom_pais");

            MongoCollection<Document> collection = mongoDB.getCollection("totalventasporpais");
            // Verificar si la colección no existe y crearla si no
            if (!collectionExists(collection)) {
                mongoDB.createCollection("totalventasporpais");
                collection = mongoDB.getCollection("totalventasporpais");
            }

            // Obtener la cantidad de documentos antes de la carga
            long documentosAntes = collection.countDocuments();

            while (resultSet.next()) {
                String nombrePais = resultSet.getString("nom_pais");
                double totalVendido = resultSet.getDouble("total_vendido");

                Document document = new Document("nombrepais", nombrePais)
                        .append("totalvendido", totalVendido);
                collection.insertOne(document);
            }

            // Obtener la cantidad de documentos después de la carga
            long documentosDespues = collection.countDocuments();

            // Calcular la cantidad de documentos actualizados, sin actualizar y nuevos
            long documentosActualizados = 0;
            long documentosSinActualizar = 0;
            long documentosNuevos = documentosDespues - documentosAntes;

            // Imprimir los resultados
            System.out.println("Estadísticas de ventas por país:");
            System.out.println("- Cuántos documentos JSON tenía la colección ANTES de hacer la nueva carga: " + documentosAntes);
            System.out.println("- Con cuántos documentos JSON queda la colección a raíz de la nueva carga: " + documentosDespues);
            System.out.println("- Cuántos documentos se actualizaron en la colección: " + documentosActualizados);
            System.out.println("- Cuántos documentos no se actualizaron (quedaron iguales en la colección): " + documentosSinActualizar);
            System.out.println("- Cuántos documentos nuevos surgieron en la colección a raíz de la nueva carga: " + documentosNuevos);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void actualizarEstadisticasPorCategoriaYGrupoEtario() {
        Connection oracleConnection = connection.getOracleConnection();
        try (Statement statement = oracleConnection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT c.nombre_categoria, cli.grupo_etario, " +
                "SUM(dt.numero_unidades * dt.precio_unitario) AS total_vendido " +
                "FROM categoria c " +
                "JOIN detalle dt ON c.codigo_cat = dt.codigo_cat " +
                "JOIN venta v ON dt.codigo_venta = v.codigo " +
                "JOIN cliente cli ON v.cedula_cli = cli.cedula " +
                "GROUP BY c.nombre_categoria, cli.grupo_etario");

            MongoCollection<Document> collection = mongoDB.getCollection("totalventasporcatygedad");
            // Verificar si la colección no existe y crearla si no
            if (!collectionExists(collection)) {
                mongoDB.createCollection("totalventasporcatygedad");
                collection = mongoDB.getCollection("totalventasporcatygedad");
            }

            // Obtener la cantidad de documentos antes de la carga
            long documentosAntes = collection.countDocuments();

            while (resultSet.next()) {
                String nombreCategoria = resultSet.getString("nombre_categoria");
                String grupoEtario = resultSet.getString("grupo_etario");
                double totalVendido = resultSet.getDouble("total_vendido");

                Document document = new Document("nombrecategoria", nombreCategoria)
                        .append("grupoetario", grupoEtario)
                        .append("totalvendido", totalVendido);
                collection.insertOne(document);
            }

            // Obtener la cantidad de documentos después de la carga
            long documentosDespues = collection.countDocuments();

            // Calcular la cantidad de documentos actualizados, sin actualizar y nuevos
            long documentosActualizados = 0;
            long documentosSinActualizar = 0;
            long documentosNuevos = documentosDespues - documentosAntes;

            // Imprimir los resultados
            System.out.println("Estadísticas de ventas por categoría y grupo etario:");
            System.out.println("- Cuántos documentos JSON tenía la colección ANTES de hacer la nueva carga: " + documentosAntes);
            System.out.println("- Con cuántos documentos JSON queda la colección a raíz de la nueva carga: " + documentosDespues);
            System.out.println("- Cuántos documentos se actualizaron en la colección: " + documentosActualizados);
            System.out.println("- Cuántos documentos no se actualizaron (quedaron iguales en la colección): " + documentosSinActualizar);
            System.out.println("- Cuántos documentos nuevos surgieron en la colección a raíz de la nueva carga: " + documentosNuevos);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void listarEstadisticasPorPais() {
        MongoCollection<Document> collection = mongoDB.getCollection("totalventasporpais");
        long documentos = collection.countDocuments();
        System.out.println("Estadísticas de ventas por país:");
        System.out.println("- Cuántos documentos JSON tiene la colección: " + documentos);
        
        if (documentos > 0) {
            System.out.println("Documentos:");
            
            MongoCursor<Document> cursor = collection.find().iterator();
            
            while (cursor.hasNext()) {
                Document document = cursor.next();
                String nombrePais = document.getString("nombrepais");
                double totalVendido = document.getDouble("totalvendido");

                System.out.println("- País: " + nombrePais + ", Total vendido: " + totalVendido);
            }
            cursor.close();
        } else {
            System.out.println("La colección está vacía.");
        }
    }
    private void listarEstadisticasPorCategoriaYGrupoEtario() {
        MongoCollection<Document> collection = mongoDB.getCollection("totalventasporcatygedad");
        long documentos = collection.countDocuments();
        
        System.out.println("Estadísticas de ventas por categoría y grupo etario:");
        System.out.println("- Cuántos documentos JSON tiene la colección: " + documentos);
        
        if (documentos > 0) {
            System.out.println("Documentos:");
            MongoCursor<Document> cursor = collection.find().iterator();
            while (cursor.hasNext()) {
                Document document = cursor.next();
                String nombreCategoria = document.getString("nombrecategoria");
                String grupoEtario = document.getString("grupoetario");
                double totalVendido = document.getDouble("totalvendido");
                
                System.out.println("- Categoría: " + nombreCategoria + ", Grupo etario: " + grupoEtario + ", Total vendido: " + totalVendido);
            }
            cursor.close();
        } else {
            System.out.println("La colección está vacía.");
        }
    }
    private boolean collectionExists(MongoCollection<Document> collection) {
        return mongoDB.listCollectionNames().into(new ArrayList<>()).contains(collection.getNamespace().getCollectionName());
    }

}

