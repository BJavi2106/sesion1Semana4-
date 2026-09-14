package ni.edu.uam.facturacion.util;

import java.util.prefs.Preferences;

public final class PreferenciasManager {

    private static final Preferences PREFERENCIAS =
            Preferences.userNodeForPackage(
                    PreferenciasManager.class
            );

    private static final String TEMA = "tema";
    private static final String COLOR_PRINCIPAL =
            "colorPrincipal";
    private static final String TAMANO_FUENTE =
            "tamanoFuente";
    private static final String MOSTRAR_BARRA_HERRAMIENTAS =
            "mostrarBarraHerramientas";
    private static final String MOSTRAR_BARRA_ESTADO =
            "mostrarBarraEstado";
    private static final String ANIMACIONES =
            "animaciones";

    private PreferenciasManager() {
    }

    public static String getTema() {
        return PREFERENCIAS.get(
                TEMA,
                "claro"
        );
    }

    public static void setTema(String tema) {
        PREFERENCIAS.put(TEMA, tema);
    }

    public static String getColorPrincipal() {
        return PREFERENCIAS.get(
                COLOR_PRINCIPAL,
                "azul"
        );
    }

    public static void setColorPrincipal(
            String colorPrincipal) {

        PREFERENCIAS.put(
                COLOR_PRINCIPAL,
                colorPrincipal
        );
    }

    public static String getTamanoFuente() {
        return PREFERENCIAS.get(
                TAMANO_FUENTE,
                "normal"
        );
    }

    public static void setTamanoFuente(
            String tamanoFuente) {

        PREFERENCIAS.put(
                TAMANO_FUENTE,
                tamanoFuente
        );
    }

    public static boolean isMostrarBarraHerramientas() {

        return PREFERENCIAS.getBoolean(
                MOSTRAR_BARRA_HERRAMIENTAS,
                true
        );
    }

    public static void setMostrarBarraHerramientas(
            boolean mostrar) {

        PREFERENCIAS.putBoolean(
                MOSTRAR_BARRA_HERRAMIENTAS,
                mostrar
        );
    }

    public static boolean isMostrarBarraEstado() {

        return PREFERENCIAS.getBoolean(
                MOSTRAR_BARRA_ESTADO,
                true
        );
    }

    public static void setMostrarBarraEstado(
            boolean mostrar) {

        PREFERENCIAS.putBoolean(
                MOSTRAR_BARRA_ESTADO,
                mostrar
        );
    }

    public static boolean isAnimaciones() {

        return PREFERENCIAS.getBoolean(
                ANIMACIONES,
                true
        );
    }

    public static void setAnimaciones(
            boolean activadas) {

        PREFERENCIAS.putBoolean(
                ANIMACIONES,
                activadas
        );
    }

    public static void restaurarValoresPredeterminados() {

        PREFERENCIAS.put(
                TEMA,
                "claro"
        );

        PREFERENCIAS.put(
                COLOR_PRINCIPAL,
                "azul"
        );

        PREFERENCIAS.put(
                TAMANO_FUENTE,
                "normal"
        );

        PREFERENCIAS.putBoolean(
                MOSTRAR_BARRA_HERRAMIENTAS,
                true
        );

        PREFERENCIAS.putBoolean(
                MOSTRAR_BARRA_ESTADO,
                true
        );

        PREFERENCIAS.putBoolean(
                ANIMACIONES,
                true
        );
    }
}