package ni.edu.uam.facturacion.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public final class SceneManager {

    private SceneManager() {
    }

    public static void abrirVentana(
            String recurso,
            String titulo) throws IOException {

        URL url =
                SceneManager.class.getResource(
                        recurso
                );

        if (url == null) {

            throw new IOException(
                    "FXML no encontrado: " + recurso
            );
        }

        FXMLLoader loader =
                new FXMLLoader(url);

        Scene scene =
                new Scene(
                        loader.load()
                );

        Stage stage =
                new Stage();

        stage.setTitle(titulo);
        stage.setScene(scene);

        stage.initModality(
                Modality.APPLICATION_MODAL
        );

        stage.setResizable(true);

        /*
         * Aplicamos las preferencias antes de mostrar
         * la ventana.
         */
        TemaManager.aplicarConfiguracionActual();

        /*
         * showAndWait() ya muestra la ventana y espera
         * hasta que el usuario la cierre.
         */
        stage.showAndWait();
    }
}