package ni.edu.uam.facturacion.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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


        // Código
        colCodigo.setStyle("-fx-alignment: CENTER-LEFT;");


        // Nombre
        colNombre.setStyle("-fx-alignment: CENTER-LEFT;");


        // Categoría
        colCategoria.setStyle("-fx-alignment: CENTER-LEFT;");


        // Precio
        colPrecio.setStyle("-fx-alignment: CENTER-RIGHT;");

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

                            setText(
                                    formatoMoneda.format(precio)
                            );
                        }
                    }
                }
        );


        // Existencia
        colExistencia.setStyle("-fx-alignment: CENTER;");


        // Estado
        colActivo.setStyle("-fx-alignment: CENTER;");

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


        // Ajuste automático de columnas
        tblProductos.widthProperty().addListener(
                (observable, anterior, nuevo) -> {

                    double ancho =
                            nuevo.doubleValue();

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
                                : producto.getCodigo()
                                .toLowerCase();

                String nombre =
                        producto.getNombre() == null
                                ? ""
                                : producto.getNombre()
                                .toLowerCase();

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

            int cantidad =
                    productosFiltrados.size();

            lblContador.setText(
                    "Mostrando "
                            + cantidad
                            + (cantidad == 1
                            ? " producto"
                            : " productos")
            );
        }
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

        if (txtCodigo.getText().isBlank()
                || txtNombre.getText().isBlank()
                || txtPrecio.getText().isBlank()
                || txtExistencia.getText().isBlank()
                || cmbCategoria.getValue() == null) {

            mensaje(
                    Alert.AlertType.WARNING,
                    "Complete los campos obligatorios."
            );

            return;
        }


        try {

            BigDecimal precio =
                    new BigDecimal(
                            txtPrecio
                                    .getText()
                                    .trim()
                    );

            int existencia =
                    Integer.parseInt(
                            txtExistencia
                                    .getText()
                                    .trim()
                    );


            if (precio.signum() <= 0
                    || existencia < 0) {

                mensaje(
                        Alert.AlertType.WARNING,
                        "Precio mayor que cero y existencia no negativa."
                );

                return;
            }


            Producto producto =
                    new Producto(
                            null,
                            txtCodigo
                                    .getText()
                                    .trim(),

                            txtNombre
                                    .getText()
                                    .trim(),

                            cmbCategoria
                                    .getValue(),

                            precio,

                            existencia,

                            rutaImagen,

                            chkActivo
                                    .isSelected()
                    );


            productos.add(producto);

            filtrarProductos(
                    txtBuscar.getText()
            );


            mensaje(
                    Alert.AlertType.INFORMATION,
                    "Producto agregado correctamente."
            );


            limpiar();

        } catch (NumberFormatException e) {

            mensaje(
                    Alert.AlertType.ERROR,
                    "Precio o existencia no válidos."
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
    }


    private void mensaje(
            Alert.AlertType tipo,
            String texto
    ) {

        new Alert(
                tipo,
                texto,
                ButtonType.OK
        ).showAndWait();
    }
}