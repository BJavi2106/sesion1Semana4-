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
                SELECT id, nombre, activa
                FROM categoria
                ORDER BY nombre
                """;
        List<Categoria> categorias = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                categorias.add(new Categoria(
                        resultSet.getInt("id"),
                        resultSet.getString("nombre"),
                        resultSet.getBoolean("activa")
                ));
            }
        }
        return categorias;
    }

    public Categoria insertar(Categoria categoria) throws SQLException {
        String sql = """
                INSERT INTO categoria (nombre, activa)
                VALUES (?, ?)
                RETURNING id
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, categoria.getNombre());
            statement.setBoolean(2, categoria.isActiva());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    categoria.setId(resultSet.getInt("id"));
                }
            }
        }
        return categoria;
    }

    public boolean actualizar(Categoria categoria) throws SQLException {
        String sql = """
                UPDATE categoria
                SET nombre = ?, activa = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, categoria.getNombre());
            statement.setBoolean(2, categoria.isActiva());
            statement.setInt(3, categoria.getId());

            return statement.executeUpdate() > 0;
        }
    }

    public boolean eliminar(Integer id) throws SQLException {
        String sql = """
                DELETE FROM categoria
                WHERE id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean existeNombre(String nombre, Integer idExcluir)
            throws SQLException {

        String sql = """
                SELECT COUNT(*)
                FROM categoria
                WHERE LOWER(nombre) = LOWER(?)
                AND (? IS NULL OR id <> ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, nombre);

            if (idExcluir == null) {
                statement.setNull(2, java.sql.Types.INTEGER);
                statement.setNull(3, java.sql.Types.INTEGER);
            } else {
                statement.setInt(2, idExcluir);
                statement.setInt(3, idExcluir);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    public boolean tieneProductos(Integer categoriaId) throws SQLException {
        String sql = """
                SELECT COUNT(*)
                FROM producto
                WHERE categoria_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, categoriaId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }
}
