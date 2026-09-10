package ni.edu.uam.facturacion.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import ni.edu.uam.facturacion.mode1.Categoria;
import ni.edu.uam.facturacion.mode1.Producto;

import java.math.BigDecimal;

public class ProductoController {

    @FXML
    private TextField txtCodigo;

    @FXML
    private TextField txtNombre;

    @FXML
    private ComboBox<Categoria> cmbCategoria;

    @FXML
    private TextField txtPrecio;

    @FXML
    private TextField txtExistencia;

    @FXML
    private TableView<Producto> tablaProductos;

    @FXML
    private TableColumn<Producto, String> colCodigo;

    @FXML
    private TableColumn<Producto, String> colNombre;

    @FXML
    private TableColumn<Producto, String> colCategoria;

    @FXML
    private TableColumn<Producto, String> colPrecio;

    @FXML
    private TableColumn<Producto, String> colExistencia;

    @FXML
    private Label lblResultado;

    private final ObservableList<Producto> productos =
            FXCollections.observableArrayList();

    private final ObservableList<Categoria> categorias =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        configurarTabla();

        cargarCategorias();

        tablaProductos.setItems(productos);

        tablaProductos.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observable, anterior, seleccionado) -> {

                            if (seleccionado != null) {
                                cargarProducto(seleccionado);
                            }
                        }
                );
    }

    private void configurarTabla() {

        colCodigo.setCellValueFactory(
                dato -> new SimpleStringProperty(
                        dato.getValue().getCodigo()
                )
        );

        colNombre.setCellValueFactory(
                dato -> new SimpleStringProperty(
                        dato.getValue().getNombre()
                )
        );

        colCategoria.setCellValueFactory(
                dato -> new SimpleStringProperty(
                        dato.getValue().getCategoria() != null
                                ? dato.getValue().getCategoria().getNombre()
                                : ""
                )
        );

        colPrecio.setCellValueFactory(
                dato -> new SimpleStringProperty(
                        dato.getValue().getPrecioVenta() != null
                                ? dato.getValue().getPrecioVenta().toString()
                                : "0.00"
                )
        );

        colExistencia.setCellValueFactory(
                dato -> new SimpleStringProperty(
                        String.valueOf(
                                dato.getValue().getExistencia()
                        )
                )
        );
    }

    private void cargarCategorias() {

        categorias.clear();

        categorias.add(
                new Categoria(
                        1,
                        "Alimentos",
                        true
                )
        );

        categorias.add(
                new Categoria(
                        2,
                        "Bebidas",
                        true
                )
        );

        categorias.add(
                new Categoria(
                        3,
                        "Limpieza",
                        true
                )
        );

        categorias.add(
                new Categoria(
                        4,
                        "Higiene",
                        true
                )
        );

        cmbCategoria.setItems(categorias);
    }

    @FXML
    private void nuevoProducto() {

        limpiarCampos();

        lblResultado.setText(
                "Nuevo producto."
        );

        txtCodigo.requestFocus();
    }

    @FXML
    private void guardarProducto() {

        if (!validarCampos()) {
            return;
        }

        try {

            String codigo =
                    txtCodigo.getText().trim();

            if (existeCodigo(codigo)) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Código duplicado",
                        "Ya existe un producto con ese código."
                );

                return;
            }

            BigDecimal precio =
                    new BigDecimal(
                            txtPrecio.getText().trim()
                    );

            int existencia =
                    Integer.parseInt(
                            txtExistencia.getText().trim()
                    );

            Producto producto =
                    new Producto();

            producto.setCodigo(codigo);

            producto.setNombre(
                    txtNombre.getText().trim()
            );

            producto.setCategoria(
                    cmbCategoria.getValue()
            );

            producto.setPrecioVenta(precio);

            producto.setExistencia(existencia);

            producto.setActivo(true);

            productos.add(producto);

            lblResultado.setText(
                    "Producto guardado correctamente."
            );

            limpiarCampos();

        } catch (NumberFormatException e) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Datos inválidos",
                    "El precio debe ser decimal y la existencia debe ser un número entero."
            );
        }
    }

    @FXML
    private void editarProducto() {

        Producto producto =
                tablaProductos.getSelectionModel()
                        .getSelectedItem();

        if (producto == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Sin selección",
                    "Seleccione un producto para editar."
            );

            return;
        }

        if (!validarCampos()) {
            return;
        }

        try {

            String nuevoCodigo =
                    txtCodigo.getText().trim();

            if (!nuevoCodigo.equals(
                    producto.getCodigo())
                    && existeCodigo(nuevoCodigo)) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Código duplicado",
                        "Ya existe otro producto con ese código."
                );

                return;
            }

            BigDecimal precio =
                    new BigDecimal(
                            txtPrecio.getText().trim()
                    );

            int existencia =
                    Integer.parseInt(
                            txtExistencia.getText().trim()
                    );

            producto.setCodigo(nuevoCodigo);

            producto.setNombre(
                    txtNombre.getText().trim()
            );

            producto.setCategoria(
                    cmbCategoria.getValue()
            );

            producto.setPrecioVenta(precio);

            producto.setExistencia(existencia);

            tablaProductos.refresh();

            lblResultado.setText(
                    "Producto editado correctamente."
            );

        } catch (NumberFormatException e) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Datos inválidos",
                    "El precio debe ser decimal y la existencia debe ser un número entero."
            );
        }
    }

    @FXML
    private void eliminarProducto() {

        Producto producto =
                tablaProductos.getSelectionModel()
                        .getSelectedItem();

        if (producto == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Sin selección",
                    "Seleccione un producto para eliminar."
            );

            return;
        }

        Alert confirmacion =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmacion.setTitle(
                "Eliminar producto"
        );

        confirmacion.setHeaderText(null);

        confirmacion.setContentText(
                "¿Está seguro de eliminar el producto \""
                        + producto.getNombre()
                        + "\"?"
        );

        if (confirmacion.showAndWait()
                .orElse(ButtonType.CANCEL)
                == ButtonType.OK) {

            productos.remove(producto);

            limpiarCampos();

            lblResultado.setText(
                    "Producto eliminado correctamente."
            );
        }
    }

    @FXML
    private void limpiarProducto() {

        limpiarCampos();

        lblResultado.setText(
                "Campos limpiados."
        );
    }

    @FXML
    private void verDetalle() {

        Producto producto =
                tablaProductos.getSelectionModel()
                        .getSelectedItem();

        if (producto == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Sin selección",
                    "Seleccione un producto para ver su detalle."
            );

            return;
        }

        String detalle =
                "Código: " + producto.getCodigo() + "\n"
                        + "Nombre: " + producto.getNombre() + "\n"
                        + "Categoría: "
                        + (producto.getCategoria() != null
                        ? producto.getCategoria().getNombre()
                        : "Sin categoría")
                        + "\n"
                        + "Precio de venta: "
                        + producto.getPrecioVenta() + "\n"
                        + "Existencia: "
                        + producto.getExistencia() + "\n"
                        + "Estado: "
                        + (producto.isActivo()
                        ? "Activo"
                        : "Inactivo");

        mostrarAlerta(
                Alert.AlertType.INFORMATION,
                "Detalle del producto",
                detalle
        );
    }

    @FXML
    private void acercaDe() {

        Alert alerta =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alerta.setTitle("Acerca de");

        alerta.setHeaderText(
                "Sistema de Facturación"
        );

        alerta.setContentText(
                "Sistema de facturación JavaFX\n"
                        + "Distribuidora El Güegüense\n\n"
                        + "Universidad Americana (UAM)"
        );

        alerta.showAndWait();
    }

    @FXML
    private void salir() {

        Alert confirmacion =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmacion.setTitle("Salir");

        confirmacion.setHeaderText(null);

        confirmacion.setContentText(
                "¿Desea salir de la aplicación?"
        );

        if (confirmacion.showAndWait()
                .orElse(ButtonType.CANCEL)
                == ButtonType.OK) {

            System.exit(0);
        }
    }

    private boolean validarCampos() {

        if (txtCodigo.getText().trim().isEmpty()
                || txtNombre.getText().trim().isEmpty()
                || cmbCategoria.getValue() == null
                || txtPrecio.getText().trim().isEmpty()
                || txtExistencia.getText().trim().isEmpty()) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Datos incompletos",
                    "Debe completar todos los campos."
            );

            return false;
        }

        try {

            BigDecimal precio =
                    new BigDecimal(
                            txtPrecio.getText().trim()
                    );

            int existencia =
                    Integer.parseInt(
                            txtExistencia.getText().trim()
                    );

            if (precio.compareTo(
                    BigDecimal.ZERO) < 0) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Precio inválido",
                        "El precio no puede ser negativo."
                );

                return false;
            }

            if (existencia < 0) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Existencia inválida",
                        "La existencia no puede ser negativa."
                );

                return false;
            }

        } catch (NumberFormatException e) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Datos inválidos",
                    "Verifique el precio y la existencia."
            );

            return false;
        }

        return true;
    }

    private boolean existeCodigo(String codigo) {

        return productos.stream()
                .anyMatch(
                        producto ->
                                producto.getCodigo()
                                        .equalsIgnoreCase(codigo)
                );
    }

    private void cargarProducto(
            Producto producto) {

        txtCodigo.setText(
                producto.getCodigo()
        );

        txtNombre.setText(
                producto.getNombre()
        );

        cmbCategoria.setValue(
                producto.getCategoria()
        );

        if (producto.getPrecioVenta() != null) {

            txtPrecio.setText(
                    producto.getPrecioVenta()
                            .toString()
            );

        } else {

            txtPrecio.clear();
        }

        txtExistencia.setText(
                String.valueOf(
                        producto.getExistencia()
                )
        );
    }

    private void limpiarCampos() {

        txtCodigo.clear();

        txtNombre.clear();

        cmbCategoria.getSelectionModel()
                .clearSelection();

        txtPrecio.clear();

        txtExistencia.clear();

        tablaProductos.getSelectionModel()
                .clearSelection();
    }

    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        Alert alerta =
                new Alert(tipo);

        alerta.setTitle(titulo);

        alerta.setHeaderText(null);

        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }
}