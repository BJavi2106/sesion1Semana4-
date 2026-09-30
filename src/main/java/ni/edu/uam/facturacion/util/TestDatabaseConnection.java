package ni.edu.uam.facturacion.util;

import java.sql.Connection;
import java.sql.SQLException;

public class TestDatabaseConnection {

    public static void main(String[] args) {

        try (Connection connection = DatabaseConnection.getConnection()) {

            System.out.println("=================================");
            System.out.println("CONEXION EXITOSA");
            System.out.println("=================================");
            System.out.println("Base de datos: "
                    + connection.getCatalog());
            System.out.println("Usuario: "
                    + connection.getMetaData().getUserName());
            System.out.println("Servidor: "
                    + connection.getMetaData().getDatabaseProductName());
            System.out.println("Version: "
                    + connection.getMetaData().getDatabaseProductVersion());
            System.out.println("=================================");

        } catch (SQLException e) {

            System.out.println("=================================");
            System.out.println("ERROR DE CONEXION");
            System.out.println("=================================");
            System.out.println("Mensaje: " + e.getMessage());
            System.out.println("=================================");

            e.printStackTrace();
        }
    }
}