package ni.edu.uam.facturacion.util;

import java.sql.Connection;

public class TestDatabaseConnection {

    public static void main(String[] args) {

        System.out.println(
                "=========================================="
        );

        System.out.println(
                "PRUEBA DE CONEXION A POSTGRESQL"
        );

        System.out.println(
                "=========================================="
        );

        try (
                Connection connection =
                        DatabaseConnection.getConnection()
        ) {

            System.out.println(
                    "CONEXION EXITOSA"
            );

            System.out.println(
                    "Base de datos: tienda_javafx"
            );

            System.out.println(
                    "Servidor: localhost"
            );

            System.out.println(
                    "Puerto: 5432"
            );

            System.out.println(
                    "=========================================="
            );

        } catch (Exception e) {

            System.out.println(
                    "ERROR DE CONEXION"
            );

            System.out.println(
                    "=========================================="
            );

            e.printStackTrace();
        }
    }
}