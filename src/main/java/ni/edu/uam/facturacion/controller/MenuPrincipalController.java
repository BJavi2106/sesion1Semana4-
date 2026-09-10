package ni.edu.uam.facturacion.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import ni.edu.uam.facturacion.util.SceneManager;

public class MenuPrincipalController {

    @FXML
    private void abrirProductos(ActionEvent event) {

        System.out.println("Botón Gestionar Productos presionado.");

        try {

            Node node = (Node) event.getSource();

            Stage stage =
                    (Stage) node.getScene().getWindow();

            SceneManager.cambiarEscena(
                    stage,
                    "/ni/edu/uam/facturacion/fxml/producto.fxml",
                    "Gestión de Productos"
            );

        } catch (Exception e) {

            e.printStackTrace();

            mostrarError(
                    "No se pudo abrir la ventana de productos.\n\n"
                            + "Detalle: "
                            + e.getMessage()
            );
        }
    }

    @FXML
    private void acercaDe() {

        Alert alerta =
                new Alert(Alert.AlertType.INFORMATION);

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

        Alert alerta =
                new Alert(Alert.AlertType.CONFIRMATION);

        alerta.setTitle("Salir");

        alerta.setHeaderText(null);

        alerta.setContentText(
                "¿Desea salir de la aplicación?"
        );

        if (alerta.showAndWait()
                .orElse(ButtonType.CANCEL)
                == ButtonType.OK) {

            System.exit(0);
        }
    }

    private void mostrarError(String mensaje) {

        Alert alerta =
                new Alert(Alert.AlertType.ERROR);

        alerta.setTitle("Error");

        alerta.setHeaderText(null);

        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }
}