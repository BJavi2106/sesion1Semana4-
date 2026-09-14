package ni.edu.uam.facturacion.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import ni.edu.uam.facturacion.mode1.Categoria;
import ni.edu.uam.facturacion.mode1.Producto;

import java.io.File;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class ProductoController {

    @FXML
    private TextField txtCodigo;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtPrecio;

    @FXML
    private TextField txtExistencia;

    @FXML
    private TextField txtBuscar;

    @FXML
    private ComboBox<Categoria> cmbCategoria;

    @FXML
    private CheckBox chkActivo;

    @FXML
    private ImageView imgProducto;

    @FXML
    private Button btnImagen;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnCerrar;

    @FXML
    private Button btnLimpiarBusqueda;

    @FXML
    private TableView<Producto> tblProductos;

    @FXML
    private TableColumn<Producto, String> colCodigo;

    @FXML
    private TableColumn<Producto, String> colNombre;

    @FXML
    private TableColumn<Producto, Categoria> colCategoria;

    @FXML
    private TableColumn<Producto, BigDecimal> colPrecio;

    @FXML
    private TableColumn<Producto, Integer> colExistencia;

    @FXML
    private TableColumn<Producto, Boolean> colActivo;

    @FXML
    private Label lblContador;

    private final ObservableList<Producto> productos =
            FXCollections.observableArrayList();

    private final ObservableList<Producto> productosFiltrados =
            FXCollections.observableArrayList();

    private String rutaImagen;

    private final NumberFormat formatoMoneda =
            NumberFormat.getCurrencyInstance(new Locale("es", "NI"));


    @FXML
    private void initialize() {

        cargarCategorias();

        configurarTabla();

        configurarBusqueda();

        configurarTooltips();

        configurarValidacionVisual();

        chkActivo.setSelected(true);

        actualizarContador();
    }


    private void cargarCategorias() {

        cmbCategoria.setItems(
                FXCollections.observableArrayList(
                        new Categoria(1, "Alimentos", true),
                        new Categoria(2, "Bebidas", true),
                        new Categoria(3, "Limpieza", true)
                )
        );
    }


    private void configurarTabla() {

        tblProductos.setItems(productosFiltrados);

        colCodigo.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("codigo")
        );

        colNombre.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("nombre")
        );

        colCategoria.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("categoria")
        );

        colPrecio.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("precioVenta")
        );

        colExistencia.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("existencia")
        );

        colActivo.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("activo")
        );

        colCodigo.setStyle("-fx-alignment: CENTER-LEFT;");
        colNombre.setStyle("-fx-alignment: CENTER-LEFT;");
        colCategoria.setStyle("-fx-alignment: CENTER-LEFT;");
        colPrecio.setStyle("-fx-alignment: CENTER-RIGHT;");
        colExistencia.setStyle("-fx-alignment: CENTER;");
        colActivo.setStyle("-fx-alignment: CENTER;");


        colPrecio.setCellFactory(column ->
                new TableCell<>() {

                    @Override
                    protected void updateItem(
                            BigDecimal precio,
                            boolean empty
                    ) {

                        super.updateItem(precio, empty);

                        if (empty || precio == null) {
                            setText(null);
                        } else {
                            setText(formatoMoneda.format(precio));
                        }
                    }
                }
        );


        colActivo.setCellFactory(column ->
                new TableCell<>() {

                    @Override
                    protected void updateItem(
                            Boolean activo,
                            boolean empty
                    ) {

                        super.updateItem(activo, empty);

                        if (empty || activo == null) {

                            setText(null);
                            setStyle("");

                        } else if (activo) {

                            setText("● Activo");

                            setStyle(
                                    "-fx-text-fill: #15803d;"
                                            + "-fx-font-weight: bold;"
                            );

                        } else {

                            setText("● Inactivo");

                            setStyle(
                                    "-fx-text-fill: #dc2626;"
                                            + "-fx-font-weight: bold;"
                            );
                        }
                    }
                }
        );


        tblProductos.widthProperty().addListener(
                (observable, anterior, nuevo) -> {

                    double ancho = nuevo.doubleValue();

                    if (ancho > 0) {

                        colCodigo.setPrefWidth(ancho * 0.13);
                        colNombre.setPrefWidth(ancho * 0.25);
                        colCategoria.setPrefWidth(ancho * 0.18);
                        colPrecio.setPrefWidth(ancho * 0.14);
                        colExistencia.setPrefWidth(ancho * 0.14);
                        colActivo.setPrefWidth(ancho * 0.16);
                    }
                }
        );
    }


    private void configurarBusqueda() {

        txtBuscar.textProperty().addListener(
                (observable, anterior, nuevoTexto) ->
                        filtrarProductos(nuevoTexto)
        );
    }


    private void filtrarProductos(String texto) {

        String busqueda =
                texto == null
                        ? ""
                        : texto.trim().toLowerCase();

        productosFiltrados.clear();

        if (busqueda.isBlank()) {

            productosFiltrados.addAll(productos);

        } else {

            for (Producto producto : productos) {

                String codigo =
                        producto.getCodigo() == null
                                ? ""
                                : producto.getCodigo().toLowerCase();

                String nombre =
                        producto.getNombre() == null
                                ? ""
                                : producto.getNombre().toLowerCase();

                String categoria =
                        producto.getCategoria() == null
                                ? ""
                                : producto.getCategoria()
                                .getNombre()
                                .toLowerCase();

                if (codigo.contains(busqueda)
                        || nombre.contains(busqueda)
                        || categoria.contains(busqueda)) {

                    productosFiltrados.add(producto);
                }
            }
        }

        actualizarContador();
    }


    @FXML
    private void limpiarBusqueda() {

        txtBuscar.clear();

        productosFiltrados.setAll(productos);

        actualizarContador();

        txtBuscar.requestFocus();
    }


    private void actualizarContador() {

        if (lblContador != null) {

            int cantidad = productosFiltrados.size();

            lblContador.setText(
                    "Mostrando "
                            + cantidad
                            + (cantidad == 1
                            ? " producto"
                            : " productos")
            );
        }
    }


    private void configurarValidacionVisual() {

        txtCodigo.textProperty().addListener(
                (observable, anterior, nuevo) ->
                        validarTexto(txtCodigo)
        );

        txtNombre.textProperty().addListener(
                (observable, anterior, nuevo) ->
                        validarTexto(txtNombre)
        );

        txtPrecio.textProperty().addListener(
                (observable, anterior, nuevo) ->
                        validarPrecio()
        );

        txtExistencia.textProperty().addListener(
                (observable, anterior, nuevo) ->
                        validarExistencia()
        );

        cmbCategoria.valueProperty().addListener(
                (observable, anterior, nuevo) ->
                        validarCategoria()
        );
    }


    private void validarTexto(TextField campo) {

        if (campo.getText().trim().isEmpty()) {

            marcarError(campo);

        } else {

            limpiarError(campo);
        }
    }


    private void validarCategoria() {

        if (cmbCategoria.getValue() == null) {

            marcarError(cmbCategoria);

        } else {

            limpiarError(cmbCategoria);
        }
    }


    private void validarPrecio() {

        String texto =
                txtPrecio.getText().trim();

        if (texto.isEmpty()) {

            marcarError(txtPrecio);
            return;
        }

        try {

            BigDecimal precio =
                    new BigDecimal(texto);

            if (precio.signum() <= 0) {

                marcarError(txtPrecio);

            } else {

                limpiarError(txtPrecio);
            }

        } catch (NumberFormatException e) {

            marcarError(txtPrecio);
        }
    }


    private void validarExistencia() {

        String texto =
                txtExistencia.getText().trim();

        if (texto.isEmpty()) {

            marcarError(txtExistencia);
            return;
        }

        try {

            int existencia =
                    Integer.parseInt(texto);

            if (existencia < 0) {

                marcarError(txtExistencia);

            } else {

                limpiarError(txtExistencia);
            }

        } catch (NumberFormatException e) {

            marcarError(txtExistencia);
        }
    }


    private void marcarError(Control control) {

        if (!control.getStyleClass().contains("field-error")) {

            control.getStyleClass().add("field-error");
        }
    }


    private void limpiarError(Control control) {

        control.getStyleClass().remove("field-error");
    }


    private void limpiarValidaciones() {

        limpiarError(txtCodigo);
        limpiarError(txtNombre);
        limpiarError(txtPrecio);
        limpiarError(txtExistencia);
        limpiarError(cmbCategoria);
    }


    private void configurarTooltips() {

        configurarTooltip(
                txtCodigo,
                "Código del producto\n"
                        + "Identificador del producto dentro del sistema."
        );

        configurarTooltip(
                txtNombre,
                "Nombre del producto\n"
                        + "Ingrese el nombre que tendrá el producto."
        );

        configurarTooltip(
                cmbCategoria,
                "Categoría\n"
                        + "Seleccione la categoría a la que pertenece el producto."
        );

        configurarTooltip(
                txtPrecio,
                "Precio de venta\n"
                        + "Ingrese un precio mayor que cero."
        );

        configurarTooltip(
                txtExistencia,
                "Existencia\n"
                        + "Cantidad disponible del producto."
        );

        configurarTooltip(
                chkActivo,
                "Estado del producto\n"
                        + "Indica si el producto se encuentra activo."
        );

        configurarTooltip(
                btnImagen,
                "Seleccionar imagen\n"
                        + "Permite elegir una imagen para el producto."
        );

        configurarTooltip(
                btnGuardar,
                "Guardar producto\n"
                        + "Registra el producto con los datos ingresados."
        );

        configurarTooltip(
                btnCerrar,
                "Cerrar ventana\n"
                        + "Cierra la gestión de productos."
        );

        configurarTooltip(
                txtBuscar,
                "Buscar productos\n"
                        + "Filtra el listado por código, nombre o categoría."
        );

        configurarTooltip(
                btnLimpiarBusqueda,
                "Limpiar búsqueda\n"
                        + "Muestra nuevamente todos los productos."
        );
    }


    private void configurarTooltip(
            Control control,
            String texto
    ) {

        Tooltip tooltip =
                new Tooltip(texto);

        tooltip.setShowDelay(
                Duration.millis(350)
        );

        tooltip.setShowDuration(
                Duration.seconds(8)
        );

        tooltip.setHideDelay(
                Duration.millis(150)
        );

        tooltip.setWrapText(true);

        tooltip.setMaxWidth(300);

        control.setTooltip(tooltip);
    }


    @FXML
    private void seleccionarImagen() {

        FileChooser chooser =
                new FileChooser();

        chooser.setTitle(
                "Seleccionar imagen del producto"
        );

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Imágenes",
                        "*.png",
                        "*.jpg",
                        "*.jpeg"
                )
        );

        File archivo =
                chooser.showOpenDialog(
                        txtCodigo
                                .getScene()
                                .getWindow()
                );

        if (archivo != null) {

            rutaImagen =
                    archivo.toURI().toString();

            imgProducto.setImage(
                    new Image(rutaImagen)
            );
        }
    }


    @FXML
    private void guardar() {

        boolean datosCompletos =
                !txtCodigo.getText().isBlank()
                        && !txtNombre.getText().isBlank()
                        && !txtPrecio.getText().isBlank()
                        && !txtExistencia.getText().isBlank()
                        && cmbCategoria.getValue() != null;

        if (!datosCompletos) {

            validarTexto(txtCodigo);
            validarTexto(txtNombre);
            validarPrecio();
            validarExistencia();
            validarCategoria();

            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Datos incompletos",
                    "Complete los campos obligatorios."
            );

            return;
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


            if (precio.signum() <= 0
                    || existencia < 0) {

                validarPrecio();
                validarExistencia();

                mostrarMensaje(
                        Alert.AlertType.WARNING,
                        "Datos no válidos",
                        "El precio debe ser mayor que cero "
                                + "y la existencia no puede ser negativa."
                );

                return;
            }


            Producto producto =
                    new Producto(
                            null,
                            txtCodigo.getText().trim(),
                            txtNombre.getText().trim(),
                            cmbCategoria.getValue(),
                            precio,
                            existencia,
                            rutaImagen,
                            chkActivo.isSelected()
                    );


            productos.add(producto);

            filtrarProductos(
                    txtBuscar.getText()
            );


            mostrarMensaje(
                    Alert.AlertType.INFORMATION,
                    "Producto registrado",
                    "El producto se agregó correctamente "
                            + "al catálogo."
            );


            limpiar();

        } catch (NumberFormatException e) {

            validarPrecio();
            validarExistencia();

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Datos no válidos",
                    "El precio o la existencia no tienen "
                            + "un formato válido."
            );
        }
    }


    @FXML
    private void cerrar() {

        Stage stage =
                (Stage) txtCodigo
                        .getScene()
                        .getWindow();

        stage.close();
    }


    private void limpiar() {

        txtCodigo.clear();

        txtNombre.clear();

        txtPrecio.clear();

        txtExistencia.clear();

        cmbCategoria
                .getSelectionModel()
                .clearSelection();

        chkActivo.setSelected(true);

        imgProducto.setImage(null);

        rutaImagen = null;

        limpiarValidaciones();
    }


    private void mostrarMensaje(
            Alert.AlertType tipo,
            String titulo,
            String texto
    ) {

        Alert alerta =
                new Alert(
                        tipo,
                        texto,
                        ButtonType.OK
                );

        alerta.setTitle(
                "Sistema de Facturación"
        );

        alerta.setHeaderText(titulo);

        alerta.getDialogPane()
                .getStyleClass()
                .add("app-alert");


        switch (tipo) {

            case INFORMATION ->
                    alerta.getDialogPane()
                            .getStyleClass()
                            .add("app-alert-information");

            case WARNING ->
                    alerta.getDialogPane()
                            .getStyleClass()
                            .add("app-alert-warning");

            case ERROR ->
                    alerta.getDialogPane()
                            .getStyleClass()
                            .add("app-alert-error");

            default -> {
            }
        }


        var hojaEstilos =
                getClass()
                        .getResource(
                                "/ni/edu/uam/facturacion/styles/app.css"
                        );

        if (hojaEstilos != null) {

            alerta.getDialogPane()
                    .getStylesheets()
                    .add(
                            hojaEstilos.toExternalForm()
                    );
        }


        alerta.showAndWait();
    }
}