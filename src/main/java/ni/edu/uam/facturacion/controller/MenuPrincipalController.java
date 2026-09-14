package ni.edu.uam.facturacion.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import ni.edu.uam.facturacion.util.SceneManager;

import java.io.IOException;

public class MenuPrincipalController {

    @FXML
    private void abrirProductos() {

        try {

            SceneManager.abrirVentana(
                    "/ni/edu/uam/facturacion/fxml/producto-view.fxml",
                    "Gestión de productos"
            );

        } catch (IOException e) {

            mostrarError(
                    "No fue posible abrir Productos.\n\n"
                            + e.getMessage()
            );
        }
    }

    @FXML
    private void abrirConfiguracion() {

        try {

            SceneManager.abrirVentana(
                    "/ni/edu/uam/facturacion/fxml/configuracion-view.fxml",
                    "Configuración del sistema"
            );

        } catch (IOException e) {

            mostrarError(
                    "No fue posible abrir Configuración.\n\n"
                            + e.getMessage()
            );
        }
    }

    @FXML
    private void acercaDe() {

        Alert alerta = new Alert(
                Alert.AlertType.INFORMATION,
                "EL GÜEGÜENSE\n\n"
                        + "Sistema de Facturación\n"
                        + "Versión 1.0\n\n"
                        + "Sistema desarrollado para la "
                        + "gestión y control de productos.",
                ButtonType.OK
        );

        alerta.setTitle("Acerca del sistema");
        alerta.setHeaderText("EL GÜEGÜENSE");

        alerta.getDialogPane()
                .getStyleClass()
                .add("app-alert");

        alerta.getDialogPane()
                .getStyleClass()
                .add("app-alert-information");

        alerta.showAndWait();
    }

    @FXML
    private void salir() {

        Alert alerta = new Alert(
                Alert.AlertType.CONFIRMATION,
                "¿Desea cerrar la aplicación?",
                ButtonType.OK,
                ButtonType.CANCEL
        );

        alerta.setTitle("Salir");
        alerta.setHeaderText("Cerrar sistema");

        if (alerta.showAndWait().orElse(
                ButtonType.CANCEL
        ) == ButtonType.OK) {

            Platform.exit();
        }
    }

    private void mostrarError(String mensaje) {

        Alert alerta = new Alert(
                Alert.AlertType.ERROR,
                mensaje,
                ButtonType.OK
        );

        alerta.setTitle("Error");
        alerta.setHeaderText(
                "No fue posible completar la operación"
        );

        alerta.getDialogPane()
                .getStyleClass()
                .add("app-alert");

        alerta.getDialogPane()
                .getStyleClass()
                .add("app-alert-error");

        alerta.showAndWait();
    }
}