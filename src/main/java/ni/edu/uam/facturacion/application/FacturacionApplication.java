package ni.edu.uam.facturacion.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ni.edu.uam.facturacion.util.TemaManager;

public class FacturacionApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader =
                new FXMLLoader(
                        getClass().getResource(
                                "/ni/edu/uam/facturacion/fxml/menu-principal.fxml"
                        )
                );

        Scene scene =
                new Scene(
                        loader.load(),
                        900,
                        600
                );

        stage.setTitle(
                "Sistema de facturación"
        );

        stage.setScene(scene);

        stage.show();

        TemaManager.aplicarConfiguracionActual();
    }

    public static void main(String[] args) {

        launch(args);
    }
}