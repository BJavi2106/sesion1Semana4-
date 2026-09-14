module com.tuempresa.sesion1Semana4 {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;

    opens ni.edu.uam.facturacion.controller
            to javafx.fxml;

    exports ni.edu.uam.facturacion.application;
    exports ni.edu.uam.facturacion.controller;
    exports ni.edu.uam.facturacion.mode1;
    exports ni.edu.uam.facturacion.util;
}