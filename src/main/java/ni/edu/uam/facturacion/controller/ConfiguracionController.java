package ni.edu.uam.facturacion.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import ni.edu.uam.facturacion.util.PreferenciasManager;
import ni.edu.uam.facturacion.util.TemaManager;

public class ConfiguracionController {

    @FXML
    private RadioButton rbClaro;

    @FXML
    private RadioButton rbOscuro;

    @FXML
    private ComboBox<String> cmbColorPrincipal;

    @FXML
    private ComboBox<String> cmbTamanoFuente;

    @FXML
    private CheckBox chkBarraHerramientas;

    @FXML
    private CheckBox chkBarraEstado;

    @FXML
    private CheckBox chkAnimaciones;

    private ToggleGroup grupoTema;

    @FXML
    private void initialize() {

        grupoTema = new ToggleGroup();

        rbClaro.setToggleGroup(grupoTema);
        rbOscuro.setToggleGroup(grupoTema);

        cmbColorPrincipal.getItems().addAll(
                "Azul",
                "Verde",
                "Morado",
                "Naranja",
                "Rojo"
        );

        cmbTamanoFuente.getItems().addAll(
                "Pequeña",
                "Normal",
                "Grande"
        );

        cargarPreferencias();

        configurarVistaPrevia();
    }

    private void cargarPreferencias() {

        if ("oscuro".equals(
                PreferenciasManager.getTema())) {

            rbOscuro.setSelected(true);

        } else {

            rbClaro.setSelected(true);
        }

        cmbColorPrincipal.setValue(
                convertirColorParaMostrar(
                        PreferenciasManager.getColorPrincipal()
                )
        );

        cmbTamanoFuente.setValue(
                convertirTamanoParaMostrar(
                        PreferenciasManager.getTamanoFuente()
                )
        );

        chkBarraHerramientas.setSelected(
                PreferenciasManager
                        .isMostrarBarraHerramientas()
        );

        chkBarraEstado.setSelected(
                PreferenciasManager
                        .isMostrarBarraEstado()
        );

        chkAnimaciones.setSelected(
                PreferenciasManager
                        .isAnimaciones()
        );
    }

    private void configurarVistaPrevia() {

        grupoTema.selectedToggleProperty()
                .addListener(
                        (observable, anterior, nuevo) ->
                                aplicarVistaPrevia()
                );

        cmbColorPrincipal.valueProperty()
                .addListener(
                        (observable, anterior, nuevo) ->
                                aplicarVistaPrevia()
                );

        cmbTamanoFuente.valueProperty()
                .addListener(
                        (observable, anterior, nuevo) ->
                                aplicarVistaPrevia()
                );

        chkBarraHerramientas.selectedProperty()
                .addListener(
                        (observable, anterior, nuevo) ->
                                aplicarVistaPrevia()
                );

        chkBarraEstado.selectedProperty()
                .addListener(
                        (observable, anterior, nuevo) ->
                                aplicarVistaPrevia()
                );

        chkAnimaciones.selectedProperty()
                .addListener(
                        (observable, anterior, nuevo) ->
                                aplicarVistaPrevia()
                );
    }

    private void aplicarVistaPrevia() {

        if (rbClaro == null
                || rbOscuro == null
                || cmbColorPrincipal == null
                || cmbTamanoFuente == null) {

            return;
        }

        String tema = rbOscuro.isSelected()
                ? "oscuro"
                : "claro";

        String color =
                convertirColorParaGuardar(
                        cmbColorPrincipal.getValue()
                );

        String tamano =
                convertirTamanoParaGuardar(
                        cmbTamanoFuente.getValue()
                );

        TemaManager.aplicarVistaPrevia(
                tema,
                color,
                tamano,
                chkBarraHerramientas.isSelected(),
                chkBarraEstado.isSelected(),
                chkAnimaciones.isSelected()
        );
    }

    @FXML
    private void guardar() {

        String tema = rbOscuro.isSelected()
                ? "oscuro"
                : "claro";

        String color =
                convertirColorParaGuardar(
                        cmbColorPrincipal.getValue()
                );

        String tamano =
                convertirTamanoParaGuardar(
                        cmbTamanoFuente.getValue()
                );

        PreferenciasManager.setTema(tema);

        PreferenciasManager.setColorPrincipal(
                color
        );

        PreferenciasManager.setTamanoFuente(
                tamano
        );

        PreferenciasManager.setMostrarBarraHerramientas(
                chkBarraHerramientas.isSelected()
        );

        PreferenciasManager.setMostrarBarraEstado(
                chkBarraEstado.isSelected()
        );

        PreferenciasManager.setAnimaciones(
                chkAnimaciones.isSelected()
        );

        TemaManager.aplicarConfiguracionActual();

        cerrarVentana();
    }

    @FXML
    private void restaurar() {

        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION,
                "¿Desea restaurar las opciones "
                        + "predeterminadas?\n\n"
                        + "Los valores se mostrarán como vista "
                        + "previa y deberán guardarse para conservarlos.",
                ButtonType.OK,
                ButtonType.CANCEL
        );

        confirmacion.setTitle(
                "Restaurar configuración"
        );

        confirmacion.setHeaderText(
                "Restaurar valores predeterminados"
        );

        aplicarEstiloAlerta(
                confirmacion,
                "app-alert-warning"
        );

        if (confirmacion.showAndWait().orElse(
                ButtonType.CANCEL
        ) == ButtonType.OK) {

            rbClaro.setSelected(true);

            cmbColorPrincipal.setValue(
                    "Azul"
            );

            cmbTamanoFuente.setValue(
                    "Normal"
            );

            chkBarraHerramientas.setSelected(true);
            chkBarraEstado.setSelected(true);
            chkAnimaciones.setSelected(true);

            aplicarVistaPrevia();
        }
    }

    @FXML
    private void cerrar() {

        if (!hayCambios()) {

            cerrarVentana();
            return;
        }

        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Los cambios realizados no se han guardado.\n\n"
                        + "¿Desea cancelar los cambios "
                        + "sin aplicarlos?",
                ButtonType.OK,
                ButtonType.CANCEL
        );

        confirmacion.setTitle(
                "Cancelar cambios"
        );

        confirmacion.setHeaderText(
                "¿Desea descartar los cambios?"
        );

        aplicarEstiloAlerta(
                confirmacion,
                "app-alert-warning"
        );

        if (confirmacion.showAndWait().orElse(
                ButtonType.CANCEL
        ) == ButtonType.OK) {

            TemaManager.aplicarConfiguracionActual();

            cerrarVentana();
        }
    }

    private boolean hayCambios() {

        String temaActual = rbOscuro.isSelected()
                ? "oscuro"
                : "claro";

        String colorActual =
                convertirColorParaGuardar(
                        cmbColorPrincipal.getValue()
                );

        String tamanoActual =
                convertirTamanoParaGuardar(
                        cmbTamanoFuente.getValue()
                );

        return !temaActual.equals(
                PreferenciasManager.getTema()
        )
                || !colorActual.equals(
                PreferenciasManager.getColorPrincipal()
        )
                || !tamanoActual.equals(
                PreferenciasManager.getTamanoFuente()
        )
                || chkBarraHerramientas.isSelected()
                != PreferenciasManager
                .isMostrarBarraHerramientas()
                || chkBarraEstado.isSelected()
                != PreferenciasManager
                .isMostrarBarraEstado()
                || chkAnimaciones.isSelected()
                != PreferenciasManager
                .isAnimaciones();
    }

    private void cerrarVentana() {

        Stage stage =
                (Stage) rbClaro
                        .getScene()
                        .getWindow();

        stage.close();
    }

    private void aplicarEstiloAlerta(
            Alert alerta,
            String clase) {

        alerta.getDialogPane()
                .getStyleClass()
                .add("app-alert");

        alerta.getDialogPane()
                .getStyleClass()
                .add(clase);
    }

    private String convertirColorParaGuardar(
            String color) {

        if (color == null) {
            return "azul";
        }

        return switch (color) {
            case "Verde" -> "verde";
            case "Morado" -> "morado";
            case "Naranja" -> "naranja";
            case "Rojo" -> "rojo";
            default -> "azul";
        };
    }

    private String convertirColorParaMostrar(
            String color) {

        if (color == null) {
            return "Azul";
        }

        return switch (color) {
            case "verde" -> "Verde";
            case "morado" -> "Morado";
            case "naranja" -> "Naranja";
            case "rojo" -> "Rojo";
            default -> "Azul";
        };
    }

    private String convertirTamanoParaGuardar(
            String tamano) {

        if (tamano == null) {
            return "normal";
        }

        return switch (tamano) {
            case "Pequeña" -> "pequena";
            case "Grande" -> "grande";
            default -> "normal";
        };
    }

    private String convertirTamanoParaMostrar(
            String tamano) {

        if (tamano == null) {
            return "Normal";
        }

        return switch (tamano) {
            case "pequena" -> "Pequeña";
            case "grande" -> "Grande";
            default -> "Normal";
        };
    }
}