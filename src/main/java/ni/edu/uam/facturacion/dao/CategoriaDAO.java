package ni.edu.uam.facturacion.dao;

import ni.edu.uam.facturacion.mode1.Categoria;
import ni.edu.uam.facturacion.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public List<Categoria> listar() throws SQLException {

        String sql = """
                SELECT
                    id,
                    nombre,
                    activa
                FROM categoria
                ORDER BY nombre
                """;

        List<Categoria> categorias = new ArrayList<>();

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            while (resultSet.next()) {

                Categoria categoria =
                        new Categoria(
                                resultSet.getInt("id"),
                                resultSet.getString("nombre"),
                                resultSet.getBoolean("activa")
                        );

                categorias.add(categoria);
            }
        }

        return categorias;
    }
}