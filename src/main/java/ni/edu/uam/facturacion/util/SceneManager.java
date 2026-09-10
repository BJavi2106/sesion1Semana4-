package ni.edu.uam.facturacion.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SceneManager {

    private SceneManager() {
    }

    public static void cambiarEscena(
            Stage stage,
            String rutaFXML,
            String titulo
    ) throws IOException {

        URL recurso = SceneManager.class.getResource(rutaFXML);

        if (recurso == null) {

            throw new IOException(
                    "No se encontró el archivo FXML:\n"
                            + rutaFXML
            );
        }

        FXMLLoader loader =
                new FXMLLoader(recurso);

        Parent root = loader.load();

        Scene scene = new Scene(root);

        stage.setTitle(titulo);

        stage.setScene(scene);

        stage.show();
    }
}