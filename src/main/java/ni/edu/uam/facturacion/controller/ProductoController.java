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
import javafx.scene.control.cell.PropertyValueFactory;

import ni.edu.uam.facturacion.dao.CategoriaDAO;
import ni.edu.uam.facturacion.dao.ProductoDAO;
import ni.edu.uam.facturacion.mode1.Categoria;
import ni.edu.uam.facturacion.mode1.Producto;

import java.io.File;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

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
    private ComboBox<String> cmbFiltroEstado;

    @FXML
    private ComboBox<String> cmbFiltroCategoria;

    @FXML
    private CheckBox chkActivo;

    @FXML
    private ImageView imgProducto;

    @FXML
    private Button btnImagen;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnCancelarEdicion;

    @FXML
    private Button btnCerrar;

    @FXML
    private Button btnLimpiarBusqueda;

    @FXML
    private Button btnEditar;

    @FXML
    private Button btnEliminar;

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

    private final FilteredList<Producto> productosFiltrados =
            new FilteredList<>(
                    productos,
                    producto -> true
            );


    private final ObservableList<Categoria> categorias =
            FXCollections.observableArrayList();


    private final CategoriaDAO categoriaDAO =
            new CategoriaDAO();

    private final ProductoDAO productoDAO =
            new ProductoDAO();


    private Producto productoEnEdicion;

    private String rutaImagen;


    private final NumberFormat formatoMoneda =
            NumberFormat.getCurrencyInstance(
                    new Locale("es", "NI")
            );


    @FXML
    private void initialize() {

        configurarTabla();

        configurarFiltros();

        configurarBusqueda();

        configurarValidaciones();

        configurarTooltips();

        chkActivo.setSelected(true);

        establecerModoRegistro();

        cargarDatosDesdeBaseDeDatos();

        txtCodigo.requestFocus();
    }


    private void cargarDatosDesdeBaseDeDatos() {

        try {

            cargarCategoriasDesdeBD();

            cargarProductosDesdeBD();

        } catch (Exception e) {

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error de conexión",
                    "No fue posible cargar los datos desde PostgreSQL.\n\n"
                            + e.getMessage()
            );
        }
    }


    private void cargarCategoriasDesdeBD()
            throws Exception {

        List<Categoria> lista =
                categoriaDAO.listar();

        categorias.setAll(lista);

        cmbCategoria.setItems(categorias);

        cmbFiltroCategoria.getItems().clear();

        cmbFiltroCategoria
                .getItems()
                .add("Todas");

        for (Categoria categoria : categorias) {

            cmbFiltroCategoria
                    .getItems()
                    .add(categoria.getNombre());
        }

        cmbFiltroCategoria.setValue("Todas");
    }


    private void cargarProductosDesdeBD()
            throws Exception {

        List<Producto> lista =
                productoDAO.listar();

        productos.setAll(lista);

        aplicarFiltros();
    }


    private void configurarTabla() {

        tblProductos.setItems(
                productosFiltrados
        );


        colCodigo.setCellValueFactory(
                new PropertyValueFactory<>(
                        "codigo"
                )
        );


        colNombre.setCellValueFactory(
                new PropertyValueFactory<>(
                        "nombre"
                )
        );


        colCategoria.setCellValueFactory(
                new PropertyValueFactory<>(
                        "categoria"
                )
        );


        colPrecio.setCellValueFactory(
                new PropertyValueFactory<>(
                        "precioVenta"
                )
        );


        colExistencia.setCellValueFactory(
                new PropertyValueFactory<>(
                        "existencia"
                )
        );


        colActivo.setCellValueFactory(
                new PropertyValueFactory<>(
                        "activo"
                )
        );


        colPrecio.setCellFactory(
                columna ->
                        new TableCell<>() {

                            @Override
                            protected void updateItem(
                                    BigDecimal precio,
                                    boolean empty
                            ) {

                                super.updateItem(
                                        precio,
                                        empty
                                );

                                if (
                                        empty
                                                || precio == null
                                ) {

                                    setText(null);

                                } else {

                                    setText(
                                            formatoMoneda
                                                    .format(precio)
                                    );
                                }
                            }
                        }
        );


        colActivo.setCellFactory(
                columna ->
                        new TableCell<>() {

                            @Override
                            protected void updateItem(
                                    Boolean activo,
                                    boolean empty
                            ) {

                                super.updateItem(
                                        activo,
                                        empty
                                );

                                if (
                                        empty
                                                || activo == null
                                ) {

                                    setText(null);
                                    setStyle("");

                                } else if (activo) {

                                    setText(
                                            "● Activo"
                                    );

                                    setStyle(
                                            "-fx-text-fill: #15803d;"
                                                    + "-fx-font-weight: bold;"
                                    );

                                } else {

                                    setText(
                                            "● Inactivo"
                                    );

                                    setStyle(
                                            "-fx-text-fill: #dc2626;"
                                                    + "-fx-font-weight: bold;"
                                    );
                                }
                            }
                        }
        );


        tblProductos
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (
                                observable,
                                anterior,
                                seleccionado
                        ) -> {

                            boolean haySeleccion =
                                    seleccionado != null;

                            btnEditar.setDisable(
                                    !haySeleccion
                            );

                            btnEliminar.setDisable(
                                    !haySeleccion
                            );
                        }
                );
    }


    private void configurarFiltros() {

        cmbFiltroEstado.setItems(
                FXCollections.observableArrayList(
                        "Todos",
                        "Activos",
                        "Inactivos"
                )
        );

        cmbFiltroEstado.setValue(
                "Todos"
        );


        cmbFiltroEstado
                .valueProperty()
                .addListener(
                        (
                                observable,
                                anterior,
                                nuevo
                        ) -> aplicarFiltros()
                );


        cmbFiltroCategoria
                .valueProperty()
                .addListener(
                        (
                                observable,
                                anterior,
                                nuevo
                        ) -> aplicarFiltros()
                );
    }


    private void configurarBusqueda() {

        txtBuscar
                .textProperty()
                .addListener(
                        (
                                observable,
                                anterior,
                                nuevo
                        ) -> aplicarFiltros()
                );
    }


    private void aplicarFiltros() {

        String busqueda =
                txtBuscar.getText() == null
                        ? ""
                        : txtBuscar
                        .getText()
                        .trim()
                        .toLowerCase(
                                Locale.ROOT
                        );


        String estado =
                cmbFiltroEstado.getValue();


        String categoria =
                cmbFiltroCategoria.getValue();


        productosFiltrados.setPredicate(
                producto -> {

                    if (producto == null) {

                        return false;
                    }


                    boolean coincideBusqueda =
                            busqueda.isBlank()
                                    || contiene(
                                    producto
                                            .getCodigo(),
                                    busqueda
                            )
                                    || contiene(
                                    producto
                                            .getNombre(),
                                    busqueda
                            )
                                    || (
                                    producto
                                            .getCategoria()
                                            != null
                                            &&
                                            contiene(
                                                    producto
                                                            .getCategoria()
                                                            .getNombre(),
                                                    busqueda
                                            )
                            );


                    boolean coincideEstado =
                            "Todos".equals(estado)
                                    ||
                                    (
                                            "Activos".equals(estado)
                                                    && producto.isActivo()
                                    )
                                    ||
                                    (
                                            "Inactivos".equals(estado)
                                                    && !producto.isActivo()
                                    );


                    boolean coincideCategoria =
                            categoria == null
                                    || "Todas".equals(categoria)
                                    || (
                                    producto
                                            .getCategoria()
                                            != null
                                            &&
                                            producto
                                                    .getCategoria()
                                                    .getNombre()
                                                    .equalsIgnoreCase(
                                                            categoria
                                                    )
                            );


                    return coincideBusqueda
                            && coincideEstado
                            && coincideCategoria;
                }
        );


        actualizarContador();
    }


    private boolean contiene(
            String valor,
            String busqueda
    ) {

        return valor != null
                && valor
                .toLowerCase(
                        Locale.ROOT
                )
                .contains(busqueda);
    }


    @FXML
    private void limpiarBusqueda() {

        txtBuscar.clear();

        cmbFiltroEstado.setValue(
                "Todos"
        );

        cmbFiltroCategoria.setValue(
                "Todas"
        );

        aplicarFiltros();

        txtBuscar.requestFocus();
    }


    private void actualizarContador() {

        int cantidad =
                productosFiltrados.size();

        lblContador.setText(
                "Mostrando "
                        + cantidad
                        + (
                        cantidad == 1
                                ? " producto"
                                : " productos"
                )
        );
    }


    private void configurarValidaciones() {

        txtCodigo.textProperty()
                .addListener(
                        (
                                observable,
                                anterior,
                                nuevo
                        ) -> {

                            if (nuevo == null) {
                                return;
                            }

                            if (nuevo.length() > 50) {

                                txtCodigo.setText(
                                        nuevo.substring(
                                                0,
                                                50
                                        )
                                );

                                txtCodigo.positionCaret(
                                        txtCodigo
                                                .getText()
                                                .length()
                                );
                            }
                        }
                );


        txtNombre.textProperty()
                .addListener(
                        (
                                observable,
                                anterior,
                                nuevo
                        ) -> {

                            if (nuevo == null) {
                                return;
                            }

                            if (nuevo.length() > 150) {

                                txtNombre.setText(
                                        nuevo.substring(
                                                0,
                                                150
                                        )
                                );

                                txtNombre.positionCaret(
                                        txtNombre
                                                .getText()
                                                .length()
                                );
                            }
                        }
                );


        txtPrecio.textProperty()
                .addListener(
                        (
                                observable,
                                anterior,
                                nuevo
                        ) -> {

                            if (nuevo == null) {
                                return;
                            }

                            if (
                                    !nuevo.matches(
                                            "\\d*(\\.\\d*)?"
                                    )
                            ) {

                                txtPrecio.setText(
                                        anterior
                                );

                                txtPrecio.positionCaret(
                                        txtPrecio
                                                .getText()
                                                .length()
                                );
                            }
                        }
                );


        txtExistencia.textProperty()
                .addListener(
                        (
                                observable,
                                anterior,
                                nuevo
                        ) -> {

                            if (nuevo == null) {
                                return;
                            }

                            if (
                                    !nuevo.matches(
                                            "\\d*"
                                    )
                            ) {

                                txtExistencia.setText(
                                        anterior
                                );

                                txtExistencia.positionCaret(
                                        txtExistencia
                                                .getText()
                                                .length()
                                );
                            }
                        }
                );
    }


    @FXML
    private void seleccionarImagen() {

        FileChooser chooser =
                new FileChooser();

        chooser.setTitle(
                "Seleccionar imagen del producto"
        );

        chooser.getExtensionFilters()
                .add(
                        new FileChooser
                                .ExtensionFilter(
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
                    archivo
                            .toURI()
                            .toString();

            imgProducto.setImage(
                    new Image(rutaImagen)
            );
        }
    }


    @FXML
    private void guardar() {

        String codigo =
                txtCodigo
                        .getText()
                        .trim();

        String nombre =
                txtNombre
                        .getText()
                        .trim();


        if (codigo.isBlank()
                || nombre.isBlank()
                || cmbCategoria.getValue() == null
                || txtPrecio.getText().isBlank()
                || txtExistencia.getText().isBlank()) {

            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Datos incompletos",
                    "Complete todos los campos obligatorios."
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


            if (precio.signum() <= 0) {

                mostrarMensaje(
                        Alert.AlertType.WARNING,
                        "Precio inválido",
                        "El precio debe ser mayor que cero."
                );

                return;
            }


            if (existencia < 0) {

                mostrarMensaje(
                        Alert.AlertType.WARNING,
                        "Existencia inválida",
                        "La existencia no puede ser negativa."
                );

                return;
            }


            Integer idExcluir =
                    productoEnEdicion == null
                            ? null
                            : productoEnEdicion.getId();


            if (
                    productoDAO.codigoExiste(
                            codigo,
                            idExcluir
                    )
            ) {

                mostrarMensaje(
                        Alert.AlertType.WARNING,
                        "Código duplicado",
                        "Ya existe un producto con el código: "
                                + codigo
                );

                txtCodigo.requestFocus();

                return;
            }


            if (productoEnEdicion == null) {

                Producto nuevoProducto =
                        new Producto(
                                null,
                                codigo,
                                nombre,
                                cmbCategoria.getValue(),
                                precio,
                                existencia,
                                rutaImagen,
                                chkActivo.isSelected()
                        );


                productoDAO.insertar(
                        nuevoProducto
                );


                mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Producto registrado",
                        "El producto se guardó correctamente en PostgreSQL."
                );

            } else {

                productoEnEdicion.setCodigo(
                        codigo
                );

                productoEnEdicion.setNombre(
                        nombre
                );

                productoEnEdicion.setCategoria(
                        cmbCategoria.getValue()
                );

                productoEnEdicion.setPrecioVenta(
                        precio
                );

                productoEnEdicion.setExistencia(
                        existencia
                );

                productoEnEdicion.setRutaImagen(
                        rutaImagen
                );

                productoEnEdicion.setActivo(
                        chkActivo.isSelected()
                );


                productoDAO.actualizar(
                        productoEnEdicion
                );


                mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Producto actualizado",
                        "Los cambios se guardaron correctamente en PostgreSQL."
                );
            }


            cargarProductosDesdeBD();

            productoEnEdicion = null;

            establecerModoRegistro();

            limpiarFormulario();

            txtCodigo.requestFocus();


        } catch (NumberFormatException e) {

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Datos inválidos",
                    "El precio o la existencia no tienen un formato válido."
            );

        } catch (Exception e) {

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error al guardar",
                    "No fue posible guardar el producto en PostgreSQL.\n\n"
                            + e.getMessage()
            );
        }
    }


    @FXML
    private void editarProducto() {

        Producto seleccionado =
                tblProductos
                        .getSelectionModel()
                        .getSelectedItem();


        if (seleccionado == null) {

            return;
        }


        productoEnEdicion =
                seleccionado;


        txtCodigo.setText(
                seleccionado.getCodigo()
        );


        txtNombre.setText(
                seleccionado.getNombre()
        );


        seleccionarCategoriaPorId(
                seleccionado
                        .getCategoria()
                        .getId()
        );


        txtPrecio.setText(
                seleccionado
                        .getPrecioVenta()
                        .toPlainString()
        );


        txtExistencia.setText(
                String.valueOf(
                        seleccionado
                                .getExistencia()
                )
        );


        chkActivo.setSelected(
                seleccionado.isActivo()
        );


        rutaImagen =
                seleccionado.getRutaImagen();


        if (
                rutaImagen != null
                        && !rutaImagen.isBlank()
        ) {

            try {

                imgProducto.setImage(
                        new Image(rutaImagen)
                );

            } catch (Exception e) {

                imgProducto.setImage(null);
            }

        } else {

            imgProducto.setImage(null);
        }


        btnGuardar.setText(
                "Guardar cambios"
        );

        btnCancelarEdicion.setVisible(
                true
        );

        btnCancelarEdicion.setManaged(
                true
        );


        txtCodigo.requestFocus();
    }


    private void seleccionarCategoriaPorId(
            Integer categoriaId
    ) {

        if (categoriaId == null) {

            cmbCategoria.getSelectionModel()
                    .clearSelection();

            return;
        }


        for (Categoria categoria :
                cmbCategoria.getItems()) {

            if (
                    categoria.getId()
                            .equals(categoriaId)
            ) {

                cmbCategoria.setValue(
                        categoria
                );

                return;
            }
        }
    }


    @FXML
    private void cancelarEdicion() {

        productoEnEdicion = null;

        establecerModoRegistro();

        limpiarFormulario();

        txtCodigo.requestFocus();
    }


    @FXML
    private void eliminarProducto() {

        Producto seleccionado =
                tblProductos
                        .getSelectionModel()
                        .getSelectedItem();


        if (seleccionado == null) {

            return;
        }


        Alert confirmacion =
                new Alert(
                        Alert.AlertType.CONFIRMATION,
                        "¿Está seguro de eliminar el producto?\n\n"
                                + "Código: "
                                + seleccionado.getCodigo()
                                + "\n"
                                + "Nombre: "
                                + seleccionado.getNombre(),
                        ButtonType.OK,
                        ButtonType.CANCEL
                );


        confirmacion.setTitle(
                "Sistema de Facturación"
        );

        confirmacion.setHeaderText(
                "Confirmar eliminación"
        );


        Optional<ButtonType> resultado =
                confirmacion.showAndWait();


        if (
                resultado.isPresent()
                        && resultado.get()
                        == ButtonType.OK
        ) {

            try {

                productoDAO.eliminar(
                        seleccionado.getId()
                );


                cargarProductosDesdeBD();


                mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Producto eliminado",
                        "El producto fue eliminado correctamente de PostgreSQL."
                );


            } catch (Exception e) {

                mostrarMensaje(
                        Alert.AlertType.ERROR,
                        "Error al eliminar",
                        "No fue posible eliminar el producto.\n\n"
                                + e.getMessage()
                );
            }
        }
    }


    private void establecerModoRegistro() {

        btnGuardar.setText(
                "Guardar producto"
        );

        btnCancelarEdicion.setVisible(
                false
        );

        btnCancelarEdicion.setManaged(
                false
        );

        btnEditar.setDisable(
                true
        );

        btnEliminar.setDisable(
                true
        );
    }


    private void limpiarFormulario() {

        txtCodigo.clear();

        txtNombre.clear();

        cmbCategoria
                .getSelectionModel()
                .clearSelection();

        txtPrecio.clear();

        txtExistencia.clear();

        chkActivo.setSelected(
                true
        );

        imgProducto.setImage(
                null
        );

        rutaImagen = null;

        tblProductos
                .getSelectionModel()
                .clearSelection();
    }


    private void configurarTooltips() {

        configurarTooltip(
                txtBuscar,
                "Buscar por código, nombre o categoría."
        );

        configurarTooltip(
                cmbFiltroEstado,
                "Filtrar productos por estado."
        );

        configurarTooltip(
                cmbFiltroCategoria,
                "Filtrar productos por categoría."
        );
    }


    private void configurarTooltip(
            Control control,
            String texto
    ) {

        if (control == null) {

            return;
        }


        Tooltip tooltip =
                new Tooltip(texto);

        tooltip.setShowDelay(
                Duration.millis(300)
        );

        control.setTooltip(
                tooltip
        );
    }


    private void mostrarMensaje(
            Alert.AlertType tipo,
            String titulo,
            String mensaje
    ) {

        Alert alerta =
                new Alert(
                        tipo,
                        mensaje,
                        ButtonType.OK
                );

        alerta.setTitle(
                "Sistema de Facturación"
        );

        alerta.setHeaderText(
                titulo
        );

        alerta.showAndWait();
    }


    @FXML
    private void cerrar() {

        Stage stage =
                (Stage) txtCodigo
                        .getScene()
                        .getWindow();

        stage.close();
    }
}