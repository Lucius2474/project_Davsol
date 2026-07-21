/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Properties;
/**
 *
 * @author aalex
 */
public class ConnectionMySQL {
    

    public static Connection getConexion() {
        Properties propiedades = new Properties();
        try {
            propiedades.load(new FileInputStream("config.properties"));
            
            String ip = propiedades.getProperty("db.ip");
            String puerto = propiedades.getProperty("db.puerto");
            String db = propiedades.getProperty("db.nombre");
            String user = propiedades.getProperty("db.usuario");
            String password = propiedades.getProperty("db.password");
            
            String url = "jdbc:mysql://" + ip + ":" + puerto + "/" + db;
            
            return DriverManager.getConnection(url, user, password);
        } catch (IOException e) {
            System.out.println("Error: No se encontró el archivo config.properties externo.");
            return null;
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
            return null;
        }
    }
    
    public static void main(String[] args) {

        Connection con = ConnectionMySQL.getConexion();

        if (con != null) {
            System.out.println("Conexion exitosa ");
        } else {
            System.out.println("Error ");
        }
    }
}



