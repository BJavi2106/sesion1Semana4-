package ni.edu.uam.facturacion.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import ni.edu.uam.facturacion.dao.CategoriaDAO;
import ni.edu.uam.facturacion.dao.ProductoDAO;
import ni.edu.uam.facturacion.mode1.Categoria;
import ni.edu.uam.facturacion.mode1.Producto;

import java.io.File;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class ProductoController {

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtExistencia;
    @FXML private TextField txtBuscar;

    @FXML private ComboBox<Categoria> cmbCategoria;
    @FXML private ComboBox<String> cmbFiltroEstado;
    @FXML private ComboBox<String> cmbFiltroCategoria;

    @FXML private CheckBox chkActivo;
    @FXML private ImageView imgProducto;

    @FXML private Button btnImagen;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelarEdicion;
    @FXML private Button btnCerrar;
    @FXML private Button btnLimpiarBusqueda;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;

    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Categoria> colCategoria;
    @FXML private TableColumn<Producto, BigDecimal> colPrecio;
    @FXML private TableColumn<Producto, Integer> colExistencia;
    @FXML private TableColumn<Producto, Boolean> colActivo;
    @FXML private Label lblContador;

    private final ObservableList<Producto> productos =
            FXCollections.observableArrayList();

    private final FilteredList<Producto> productosFiltrados =
            new FilteredList<>(productos, producto -> true);

    private final ObservableList<Categoria> categorias =
            FXCollections.observableArrayList();

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();

    private Producto productoEnEdicion;
    private String rutaImagen;

    private final NumberFormat formatoMoneda =
            NumberFormat.getCurrencyInstance(new Locale("es", "NI"));

    @FXML
    private void initialize() {
        configurarTabla();
        configurarFiltros();
        configurarBusqueda();
        configurarValidaciones();

        chkActivo.setSelected(true);
        establecerModoRegistro();
        cargarDatosDesdeBaseDeDatos();

        txtCodigo.requestFocus();
    }

    private void cargarDatosDesdeBaseDeDatos() {
        try {
            cargarCategoriasDesdeBD();
            cargarProductosDesdeBD();
        } catch (SQLException e) {
            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error de base de datos",
                    "No fue posible cargar los datos desde la base de datos."
            );
            System.err.println(e.getMessage());
        }
    }

    private void cargarCategoriasDesdeBD() throws SQLException {
        List<Categoria> lista = categoriaDAO.listar();

        categorias.setAll(lista);
        cmbCategoria.setItems(categorias);

        cmbFiltroCategoria.getItems().clear();
        cmbFiltroCategoria.getItems().add("Todas");

        for (Categoria categoria : categorias) {
            cmbFiltroCategoria.getItems().add(categoria.getNombre());
        }

        cmbFiltroCategoria.setValue("Todas");
    }

    private void cargarProductosDesdeBD() throws SQLException {
        List<Producto> lista = productoDAO.listar();
        productos.setAll(lista);
        aplicarFiltros();
    }

    private void configurarTabla() {
        tblProductos.setItems(productosFiltrados);

        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colExistencia.setCellValueFactory(new PropertyValueFactory<>("existencia"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        colPrecio.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal precio, boolean empty) {
                super.updateItem(precio, empty);
                setText(empty || precio == null ? null : formatoMoneda.format(precio));
            }
        });

        colActivo.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean activo, boolean empty) {
                super.updateItem(activo, empty);

                if (empty || activo == null) {
                    setText(null);
                    setStyle("");
                } else if (activo) {
                    setText("● Activo");
                    setStyle("-fx-text-fill: #15803d;-fx-font-weight: bold;");
                } else {
                    setText("● Inactivo");
                    setStyle("-fx-text-fill: #dc2626;-fx-font-weight: bold;");
                }
            }
        });

        tblProductos.getSelectionModel().selectedItemProperty()
                .addListener((observable, anterior, seleccionado) -> {
                    boolean haySeleccion = seleccionado != null;
                    btnEditar.setDisable(!haySeleccion);
                    btnEliminar.setDisable(!haySeleccion);
                });
    }

    private void configurarFiltros() {
        cmbFiltroEstado.setItems(
                FXCollections.observableArrayList(
                        "Todos", "Activos", "Inactivos"
                )
        );

        cmbFiltroEstado.setValue("Todos");

        cmbFiltroEstado.valueProperty()
                .addListener((observable, anterior, nuevo) -> aplicarFiltros());

        cmbFiltroCategoria.valueProperty()
                .addListener((observable, anterior, nuevo) -> aplicarFiltros());
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty()
                .addListener((observable, anterior, nuevo) -> aplicarFiltros());
    }

    private void aplicarFiltros() {
        String busqueda = txtBuscar.getText() == null
                ? ""
                : txtBuscar.getText().trim().toLowerCase(Locale.ROOT);

        String estado = cmbFiltroEstado.getValue();
        String categoria = cmbFiltroCategoria.getValue();

        productosFiltrados.setPredicate(producto -> {
            if (producto == null) {
                return false;
            }

            boolean coincideBusqueda =
                    busqueda.isBlank()
                            || contiene(producto.getCodigo(), busqueda)
                            || contiene(producto.getNombre(), busqueda)
                            || (
                                producto.getCategoria() != null
                                && contiene(
                                    producto.getCategoria().getNombre(),
                                    busqueda
                                )
                            );

            boolean coincideEstado =
                    "Todos".equals(estado)
                            || ("Activos".equals(estado) && producto.isActivo())
                            || ("Inactivos".equals(estado) && !producto.isActivo());

            boolean coincideCategoria =
                    categoria == null
                            || "Todas".equals(categoria)
                            || (
                                producto.getCategoria() != null
                                && producto.getCategoria().getNombre()
                                    .equalsIgnoreCase(categoria)
                            );

            return coincideBusqueda && coincideEstado && coincideCategoria;
        });

        actualizarContador();
    }

    private boolean contiene(String valor, String busqueda) {
        return valor != null
                && valor.toLowerCase(Locale.ROOT).contains(busqueda);
    }

    @FXML
    private void limpiarBusqueda() {
        txtBuscar.clear();
        cmbFiltroEstado.setValue("Todos");
        cmbFiltroCategoria.setValue("Todas");
        aplicarFiltros();
        txtBuscar.requestFocus();
    }

    private void actualizarContador() {
        int cantidad = productosFiltrados.size();

        lblContador.setText(
                "Mostrando "
                        + cantidad
                        + (cantidad == 1 ? " producto" : " productos")
        );
    }

    private void configurarValidaciones() {
        txtCodigo.textProperty().addListener((observable, anterior, nuevo) -> {
            if (nuevo != null && nuevo.length() > 50) {
                txtCodigo.setText(nuevo.substring(0, 50));
                txtCodigo.positionCaret(txtCodigo.getText().length());
            }
        });

        txtNombre.textProperty().addListener((observable, anterior, nuevo) -> {
            if (nuevo != null && nuevo.length() > 150) {
                txtNombre.setText(nuevo.substring(0, 150));
                txtNombre.positionCaret(txtNombre.getText().length());
            }
        });
    }

    /*
     * Semana 8:
     * valida los campos y construye el Producto antes de guardar.
     */
    private Producto obtenerProductoFormulario() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();

        if (codigo.isEmpty()) {
            throw new IllegalArgumentException("El código es obligatorio.");
        }

        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }

        Categoria categoria =
                cmbCategoria.getSelectionModel().getSelectedItem();

        if (categoria == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar una categoría."
            );
        }

        BigDecimal precio;

        try {
            precio = new BigDecimal(txtPrecio.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "El precio debe ser un valor numérico."
            );
        }

        if (precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El precio debe ser mayor que cero."
            );
        }

        int existencia;

        try {
            existencia = Integer.parseInt(txtExistencia.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "La existencia debe ser un número entero."
            );
        }

        if (existencia < 0) {
            throw new IllegalArgumentException(
                    "La existencia no puede ser negativa."
            );
        }

        return new Producto(
                null,
                codigo,
                nombre,
                categoria,
                precio,
                existencia,
                rutaImagen,
                chkActivo.isSelected()
        );
    }

    @FXML
    private void guardar() {
        try {
            Producto producto = obtenerProductoFormulario();

            Integer idExcluir = productoEnEdicion == null
                    ? null
                    : productoEnEdicion.getId();

            if (productoDAO.codigoExiste(
                    producto.getCodigo(),
                    idExcluir
            )) {
                mostrarMensaje(
                        Alert.AlertType.WARNING,
                        "Código duplicado",
                        "Ya existe un producto con ese código."
                );
                txtCodigo.requestFocus();
                return;
            }

            if (productoEnEdicion == null) {
                productoDAO.insertar(producto);

                mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Producto registrado",
                        "La información fue almacenada correctamente."
                );
            } else {
                producto.setId(productoEnEdicion.getId());

                boolean actualizado = productoDAO.actualizar(producto);

                if (!actualizado) {
                    mostrarMensaje(
                            Alert.AlertType.WARNING,
                            "Producto no actualizado",
                            "No fue posible actualizar el producto."
                    );
                    return;
                }

                mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Producto actualizado",
                        "La información fue actualizada correctamente."
                );
            }

            cargarProductosDesdeBD();

            productoEnEdicion = null;
            establecerModoRegistro();
            limpiarFormulario();

        } catch (IllegalArgumentException e) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Validación",
                    e.getMessage()
            );
        } catch (SQLException e) {
            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error de base de datos",
                    "No fue posible completar la operación."
            );
            System.err.println(e.getMessage());
        }
    }

    @FXML
    private void editarProducto() {
        Producto seleccionado =
                tblProductos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Seleccione un producto",
                    "Debe seleccionar un producto para actualizar."
            );
            return;
        }

        productoEnEdicion = seleccionado;

        txtCodigo.setText(seleccionado.getCodigo());
        txtNombre.setText(seleccionado.getNombre());
        cmbCategoria.setValue(seleccionado.getCategoria());

        txtPrecio.setText(
                seleccionado.getPrecioVenta().toPlainString()
        );

        txtExistencia.setText(
                String.valueOf(seleccionado.getExistencia())
        );

        chkActivo.setSelected(seleccionado.isActivo());

        rutaImagen = seleccionado.getRutaImagen();

        if (rutaImagen != null && !rutaImagen.isBlank()) {
            File archivo = new File(rutaImagen);

            if (archivo.exists()) {
                imgProducto.setImage(
                        new Image(archivo.toURI().toString())
                );
            } else {
                imgProducto.setImage(null);
            }
        } else {
            imgProducto.setImage(null);
        }

        btnGuardar.setText("Guardar cambios");
        btnCancelarEdicion.setVisible(true);
        btnCancelarEdicion.setManaged(true);

        txtCodigo.requestFocus();
    }

    @FXML
    private void cancelarEdicion() {
        productoEnEdicion = null;
        establecerModoRegistro();
        limpiarFormulario();
    }

    @FXML
    private void eliminarProducto() {
        Producto seleccionado =
                tblProductos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Seleccione un producto",
                    "Debe seleccionar un producto para eliminar."
            );
            return;
        }

        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION,
                "¿Está seguro de eliminar el producto?\n\n"
                        + seleccionado.getCodigo()
                        + " - "
                        + seleccionado.getNombre(),
                ButtonType.OK,
                ButtonType.CANCEL
        );

        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("Eliminar producto");

        Optional<ButtonType> resultado =
                confirmacion.showAndWait();

        if (resultado.isEmpty()
                || resultado.get() != ButtonType.OK) {
            return;
        }

        try {
            boolean eliminado =
                    productoDAO.eliminar(seleccionado.getId());

            if (!eliminado) {
                mostrarMensaje(
                        Alert.AlertType.WARNING,
                        "Producto no eliminado",
                        "No fue posible eliminar el producto."
                );
                return;
            }

            cargarProductosDesdeBD();
            limpiarFormulario();

            mostrarMensaje(
                    Alert.AlertType.INFORMATION,
                    "Producto eliminado",
                    "El producto fue eliminado correctamente."
            );

        } catch (SQLException e) {
            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error de base de datos",
                    "No fue posible completar la operación."
            );
            System.err.println(e.getMessage());
        }
    }

    @FXML
    private void seleccionarImagen() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Seleccionar imagen del producto");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Imágenes",
                        "*.png",
                        "*.jpg",
                        "*.jpeg",
                        "*.gif"
                )
        );

        Stage stage =
                (Stage) btnImagen.getScene().getWindow();

        File archivo =
                fileChooser.showOpenDialog(stage);

        if (archivo == null) {
            return;
        }

        rutaImagen = archivo.getAbsolutePath();

        imgProducto.setImage(
                new Image(archivo.toURI().toString())
        );
    }

    private void establecerModoRegistro() {
        btnGuardar.setText("Guardar producto");

        btnCancelarEdicion.setVisible(false);
        btnCancelarEdicion.setManaged(false);

        btnEditar.setDisable(true);
        btnEliminar.setDisable(true);
    }

    private void limpiarFormulario() {
        txtCodigo.clear();
        txtNombre.clear();
        txtPrecio.clear();
        txtExistencia.clear();

        cmbCategoria.getSelectionModel().clearSelection();

        chkActivo.setSelected(true);

        rutaImagen = null;
        imgProducto.setImage(null);

        tblProductos.getSelectionModel().clearSelection();
    }

    private void mostrarMensaje(
            Alert.AlertType tipo,
            String titulo,
            String mensaje
    ) {
        Alert alerta =
                new Alert(tipo, mensaje, ButtonType.OK);

        alerta.setTitle("Sistema de Facturación");
        alerta.setHeaderText(titulo);

        alerta.showAndWait();
    }

    @FXML
    private void cerrar() {
        Stage stage =
                (Stage) txtCodigo.getScene().getWindow();

        stage.close();
    }
}
