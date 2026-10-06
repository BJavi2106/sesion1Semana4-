package ni.edu.uam.facturacion.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/tienda_javafx";

    private static final String USER =
            "postgres";

    private static final String PASSWORD =
            "1234";

    private DatabaseConnection() {
    }

    public static Connection getConnection()
            throws SQLException {

        try {

            Class.forName(
                    "org.postgresql.Driver"
            );

        } catch (ClassNotFoundException e) {

            throw new SQLException(
                    "No se encontró el driver de PostgreSQL.",
                    e
            );
        }

        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }
}