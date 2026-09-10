package ni.edu.uam.facturacion.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class FacturacionApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        URL recursoFXML = getClass().getResource(
                "/ni/edu/uam/facturacion/fxml/menu-principal.fxml"
        );

        if (recursoFXML == null) {
            throw new RuntimeException(
                    "No se encontró el archivo menu-principal.fxml. " +
                            "Verifique que esté en: " +
                            "src/main/resources/ni/edu/uam/facturacion/fxml/"
            );
        }

        FXMLLoader loader = new FXMLLoader(recursoFXML);

        Scene scene = new Scene(loader.load());

        stage.setTitle("Sistema de Facturación - Distribuidora El Güegüense");

        stage.setScene(scene);

        stage.setWidth(1000);

        stage.setHeight(650);

        stage.setMinWidth(800);

        stage.setMinHeight(500);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}