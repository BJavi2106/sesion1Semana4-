package ni.edu.uam.facturacion.dao;

import ni.edu.uam.facturacion.mode1.Categoria;
import ni.edu.uam.facturacion.mode1.Producto;
import ni.edu.uam.facturacion.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public List<Producto> listar() throws SQLException {

        String sql = """
                SELECT
                    p.id,
                    p.codigo,
                    p.nombre,
                    c.id AS categoria_id,
                    c.nombre AS categoria_nombre,
                    c.activa AS categoria_activa,
                    p.precio_venta,
                    p.existencia,
                    p.ruta_imagen,
                    p.activo
                FROM producto p
                INNER JOIN categoria c
                    ON p.categoria_id = c.id
                ORDER BY p.id
                """;

        List<Producto> productos = new ArrayList<>();

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
                                resultSet.getInt("categoria_id"),
                                resultSet.getString("categoria_nombre"),
                                resultSet.getBoolean("categoria_activa")
                        );

                Producto producto =
                        new Producto(
                                resultSet.getInt("id"),
                                resultSet.getString("codigo"),
                                resultSet.getString("nombre"),
                                categoria,
                                resultSet.getBigDecimal("precio_venta"),
                                resultSet.getInt("existencia"),
                                resultSet.getString("ruta_imagen"),
                                resultSet.getBoolean("activo")
                        );

                productos.add(producto);
            }
        }

        return productos;
    }


    public Producto insertar(Producto producto)
            throws SQLException {

        String sql = """
                INSERT INTO producto
                (
                    codigo,
                    nombre,
                    categoria_id,
                    precio_venta,
                    existencia,
                    ruta_imagen,
                    activo
                )
                VALUES
                (
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?
                )
                RETURNING id
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    producto.getCodigo()
            );

            statement.setString(
                    2,
                    producto.getNombre()
            );

            statement.setInt(
                    3,
                    producto.getCategoria().getId()
            );

            statement.setBigDecimal(
                    4,
                    producto.getPrecioVenta()
            );

            statement.setInt(
                    5,
                    producto.getExistencia()
            );

            statement.setString(
                    6,
                    producto.getRutaImagen()
            );

            statement.setBoolean(
                    7,
                    producto.isActivo()
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    producto.setId(
                            resultSet.getInt("id")
                    );
                }
            }
        }

        return producto;
    }


    public boolean actualizar(
            Producto producto
    ) throws SQLException {

        String sql = """
                UPDATE producto
                SET
                    codigo = ?,
                    nombre = ?,
                    categoria_id = ?,
                    precio_venta = ?,
                    existencia = ?,
                    ruta_imagen = ?,
                    activo = ?
                WHERE id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    producto.getCodigo()
            );

            statement.setString(
                    2,
                    producto.getNombre()
            );

            statement.setInt(
                    3,
                    producto.getCategoria().getId()
            );

            statement.setBigDecimal(
                    4,
                    producto.getPrecioVenta()
            );

            statement.setInt(
                    5,
                    producto.getExistencia()
            );

            statement.setString(
                    6,
                    producto.getRutaImagen()
            );

            statement.setBoolean(
                    7,
                    producto.isActivo()
            );

            statement.setInt(
                    8,
                    producto.getId()
            );

            return statement.executeUpdate() > 0;
        }
    }


    public boolean eliminar(
            Integer id
    ) throws SQLException {

        String sql = """
                DELETE FROM producto
                WHERE id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, id);

            return statement.executeUpdate() > 0;
        }
    }


    public boolean codigoExiste(
            String codigo,
            Integer idExcluir
    ) throws SQLException {

        String sql = """
                SELECT COUNT(*)
                FROM producto
                WHERE LOWER(codigo) = LOWER(?)
                AND (? IS NULL OR id <> ?)
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, codigo);

            if (idExcluir == null) {

                statement.setNull(
                        2,
                        java.sql.Types.INTEGER
                );

                statement.setNull(
                        3,
                        java.sql.Types.INTEGER
                );

            } else {

                statement.setInt(
                        2,
                        idExcluir
                );

                statement.setInt(
                        3,
                        idExcluir
                );
            }

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    return resultSet.getInt(1) > 0;
                }
            }
        }

        return false;
    }
}