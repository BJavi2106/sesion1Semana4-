module com.tuempresa.sesion1semana4 {

    requires javafx.controls;
    requires javafx.fxml;

    exports ni.edu.uam.facturacion.application;
    exports ni.edu.uam.facturacion.mode1;

    opens ni.edu.uam.facturacion.controller to javafx.fxml;
    opens ni.edu.uam.facturacion.mode1 to javafx.base;

}