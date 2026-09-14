package ni.edu.uam.facturacion.util;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Window;

public final class TemaManager {

    private TemaManager() {
    }

    public static void aplicarConfiguracionActual() {

        aplicarConfiguracion(
                PreferenciasManager.getTema(),
                PreferenciasManager.getColorPrincipal(),
                PreferenciasManager.getTamanoFuente(),
                PreferenciasManager.isMostrarBarraHerramientas(),
                PreferenciasManager.isMostrarBarraEstado(),
                PreferenciasManager.isAnimaciones()
        );
    }

    public static void aplicarVistaPrevia(
            String tema,
            String colorPrincipal,
            String tamanoFuente,
            boolean mostrarBarraHerramientas,
            boolean mostrarBarraEstado,
            boolean animaciones) {

        aplicarConfiguracion(
                tema,
                colorPrincipal,
                tamanoFuente,
                mostrarBarraHerramientas,
                mostrarBarraEstado,
                animaciones
        );
    }

    private static void aplicarConfiguracion(
            String tema,
            String colorPrincipal,
            String tamanoFuente,
            boolean mostrarBarraHerramientas,
            boolean mostrarBarraEstado,
            boolean animaciones) {

        for (Window window : Window.getWindows()) {

            Scene scene = window.getScene();

            if (scene == null) {
                continue;
            }

            Parent root = scene.getRoot();

            if (root == null) {
                continue;
            }

            aplicarClasesRecursivamente(
                    root,
                    tema,
                    colorPrincipal,
                    tamanoFuente,
                    animaciones
            );

            actualizarVisibilidad(
                    root,
                    mostrarBarraHerramientas,
                    mostrarBarraEstado
            );

            root.applyCss();
            root.requestLayout();
        }
    }

    private static void aplicarClasesRecursivamente(
            Node nodo,
            String tema,
            String colorPrincipal,
            String tamanoFuente,
            boolean animaciones) {

        eliminarClasesPersonalizacion(nodo);

        nodo.getStyleClass().add(
                "theme-" + tema
        );

        nodo.getStyleClass().add(
                "accent-" + colorPrincipal
        );

        nodo.getStyleClass().add(
                "font-" + tamanoFuente
        );

        if (animaciones) {

            nodo.getStyleClass().add(
                    "animations-on"
            );

        } else {

            nodo.getStyleClass().add(
                    "animations-off"
            );
        }

        if (nodo instanceof Parent parent) {

            for (Node hijo :
                    parent.getChildrenUnmodifiable()) {

                aplicarClasesRecursivamente(
                        hijo,
                        tema,
                        colorPrincipal,
                        tamanoFuente,
                        animaciones
                );
            }
        }
    }

    private static void eliminarClasesPersonalizacion(
            Node nodo) {

        nodo.getStyleClass().removeIf(
                clase ->
                        clase.startsWith("theme-")
                                || clase.startsWith("accent-")
                                || clase.startsWith("font-")
                                || clase.equals("animations-on")
                                || clase.equals("animations-off")
        );
    }

    private static void actualizarVisibilidad(
            Parent root,
            boolean mostrarBarraHerramientas,
            boolean mostrarBarraEstado) {

        recorrerNodos(
                root,
                mostrarBarraHerramientas,
                mostrarBarraEstado
        );
    }

    private static void recorrerNodos(
            Node nodo,
            boolean mostrarBarraHerramientas,
            boolean mostrarBarraEstado) {

        if (nodo.getStyleClass().contains("toolbar")) {

            nodo.setVisible(
                    mostrarBarraHerramientas
            );

            nodo.setManaged(
                    mostrarBarraHerramientas
            );
        }

        if (nodo.getStyleClass().contains("status-bar")) {

            nodo.setVisible(
                    mostrarBarraEstado
            );

            nodo.setManaged(
                    mostrarBarraEstado
            );
        }

        if (nodo instanceof Parent parent) {

            for (Node hijo :
                    parent.getChildrenUnmodifiable()) {

                recorrerNodos(
                        hijo,
                        mostrarBarraHerramientas,
                        mostrarBarraEstado
                );
            }
        }
    }
}