package ni.edu.uam.facturacion.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import ni.edu.uam.facturacion.dao.CategoriaDAO;
import ni.edu.uam.facturacion.mode1.Categoria;

import java.sql.SQLException;
import java.util.Optional;

public class CategoriaController {

    @FXML private TextField txtNombre;
    @FXML private CheckBox chkActiva;
    @FXML private TableView<Categoria> tblCategorias;
    @FXML private TableColumn<Categoria, Integer> colId;
    @FXML private TableColumn<Categoria, String> colNombre;
    @FXML private TableColumn<Categoria, Boolean> colActiva;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelarEdicion;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Label lblContador;

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final ObservableList<Categoria> categorias =
            FXCollections.observableArrayList();

    private Categoria categoriaEnEdicion;

    @FXML
    private void initialize() {
        configurarTabla();
        cargarCategoriasDesdeBD();
        establecerModoRegistro();

        tblCategorias.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, actual) -> {
                    boolean sinSeleccion = actual == null;
                    btnEditar.setDisable(sinSeleccion);
                    btnEliminar.setDisable(sinSeleccion);
                });

        txtNombre.textProperty().addListener((obs, anterior, nuevo) -> {
            if (nuevo != null && nuevo.length() > 100) {
                txtNombre.setText(nuevo.substring(0, 100));
                txtNombre.positionCaret(txtNombre.getText().length());
            }
        });
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colActiva.setCellValueFactory(new PropertyValueFactory<>("activa"));
        tblCategorias.setItems(categorias);
    }

    private void cargarCategoriasDesdeBD() {
        try {
            categorias.setAll(categoriaDAO.listar());
            actualizarContador();
        } catch (SQLException e) {
            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error de base de datos",
                    "No fue posible cargar las categorías."
            );
            System.err.println(e.getMessage());
        }
    }

    @FXML
    private void guardar() {
        try {
            Categoria categoria = obtenerCategoriaFormulario();

            Integer idExcluir = categoriaEnEdicion == null
                    ? null
                    : categoriaEnEdicion.getId();

            if (categoriaDAO.existeNombre(
                    categoria.getNombre(),
                    idExcluir
            )) {
                mostrarMensaje(
                        Alert.AlertType.WARNING,
                        "Categoría duplicada",
                        "Ya existe una categoría con ese nombre."
                );
                txtNombre.requestFocus();
                return;
            }

            if (categoriaEnEdicion == null) {
                categoriaDAO.insertar(categoria);
                mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Categoría registrada",
                        "La categoría se guardó correctamente."
                );
            } else {
                categoria.setId(categoriaEnEdicion.getId());
                categoriaDAO.actualizar(categoria);
                mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Categoría actualizada",
                        "Los cambios se guardaron correctamente."
                );
            }

            cargarCategoriasDesdeBD();
            categoriaEnEdicion = null;
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

    private Categoria obtenerCategoriaFormulario() {
        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre de la categoría es obligatorio."
            );
        }

        return new Categoria(
                null,
                nombre,
                chkActiva.isSelected()
        );
    }

    @FXML
    private void editarCategoria() {
        Categoria seleccionada =
                tblCategorias.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Seleccione una categoría",
                    "Debe seleccionar una categoría para actualizar."
            );
            return;
        }

        categoriaEnEdicion = seleccionada;
        txtNombre.setText(seleccionada.getNombre());
        chkActiva.setSelected(seleccionada.isActiva());

        btnGuardar.setText("Guardar cambios");
        btnCancelarEdicion.setVisible(true);
        btnCancelarEdicion.setManaged(true);
        txtNombre.requestFocus();
    }

    @FXML
    private void cancelarEdicion() {
        categoriaEnEdicion = null;
        establecerModoRegistro();
        limpiarFormulario();
    }

    @FXML
    private void eliminarCategoria() {
        Categoria seleccionada =
                tblCategorias.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Seleccione una categoría",
                    "Debe seleccionar una categoría para eliminar."
            );
            return;
        }

        try {
            if (categoriaDAO.tieneProductos(seleccionada.getId())) {
                mostrarMensaje(
                        Alert.AlertType.WARNING,
                        "No se puede eliminar",
                        "No puede eliminar la categoría porque tiene productos asociados."
                );
                return;
            }

            Alert confirmacion = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "¿Está seguro de eliminar la categoría?\n\n"
                            + seleccionada.getNombre(),
                    ButtonType.OK,
                    ButtonType.CANCEL
            );

            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText("Eliminar categoría");

            Optional<ButtonType> resultado =
                    confirmacion.showAndWait();

            if (resultado.isPresent()
                    && resultado.get() == ButtonType.OK) {

                categoriaDAO.eliminar(seleccionada.getId());
                cargarCategoriasDesdeBD();
                limpiarFormulario();

                mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Categoría eliminada",
                        "La categoría fue eliminada correctamente."
                );
            }

        } catch (SQLException e) {
            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error de base de datos",
                    "No fue posible eliminar la categoría."
            );
            System.err.println(e.getMessage());
        }
    }

    private void establecerModoRegistro() {
        btnGuardar.setText("Guardar categoría");
        btnCancelarEdicion.setVisible(false);
        btnCancelarEdicion.setManaged(false);
        btnEditar.setDisable(true);
        btnEliminar.setDisable(true);
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        chkActiva.setSelected(true);
        tblCategorias.getSelectionModel().clearSelection();
    }

    private void actualizarContador() {
        int cantidad = categorias.size();
        lblContador.setText(
                "Mostrando " + cantidad
                        + (cantidad == 1 ? " categoría" : " categorías")
        );
    }

    private void mostrarMensaje(
            Alert.AlertType tipo,
            String titulo,
            String mensaje
    ) {
        Alert alerta = new Alert(tipo, mensaje, ButtonType.OK);
        alerta.setTitle("Sistema de Facturación");
        alerta.setHeaderText(titulo);
        alerta.getDialogPane().getStyleClass().add("app-alert");

        switch (tipo) {
            case ERROR -> alerta.getDialogPane()
                    .getStyleClass().add("app-alert-error");
            case WARNING -> alerta.getDialogPane()
                    .getStyleClass().add("app-alert-warning");
            default -> alerta.getDialogPane()
                    .getStyleClass().add("app-alert-information");
        }

        alerta.showAndWait();
    }
}
