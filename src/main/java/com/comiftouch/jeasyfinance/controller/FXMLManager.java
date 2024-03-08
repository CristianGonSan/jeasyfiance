package com.comiftouch.jeasyfinance.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.HashMap;

/**
 * Clase que administra la carga perezosa de FXML.
 */
public class FXMLManager {

    // Ruta base para los archivos FXML
    private String fxmlPaths = "/view/";

    // HashMap para almacenar nodos raíz (Parent) asociados a los nombres de FXML cargados
    private final HashMap<String, Parent> parents = new HashMap<>();

    // HashMap para almacenar controladores asociados a los nombres de FXML cargados
    private final HashMap<String, Object> controllers = new HashMap<>();

    /**
     * Constructor que permite especificar la ruta base para los archivos FXML.
     * @param fxmlPaths La ruta base para los archivos FXML.
     */
    public FXMLManager(String fxmlPaths) {
        this.fxmlPaths = fxmlPaths;
    }

    /**
     * Constructor por defecto.
     */
    public FXMLManager() {
    }

    /**
     * Carga un archivo FXML dado su nombre.
     * @param fxmlName El nombre del archivo FXML (sin la extensión .fxml).
     */
    private void loadFxml(String fxmlName) {
        // Construye la ruta completa al archivo FXML
        String path = fxmlPaths + fxmlName + ".fxml";

        // Crea un FXMLLoader para cargar el FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(path));

        try {
            // Carga el FXML y almacena el nodo raíz y el controlador asociado
            parents.put(fxmlName, fxmlLoader.load());
            controllers.put(fxmlName, fxmlLoader.getController());

            fxmlLoader.setRoot(null);
            fxmlLoader.setController(null);
            fxmlLoader.setLocation(null);
        } catch (IOException e) {
            // Lanza una excepción en caso de error durante la carga
            throw new RuntimeException(e);
        }
    }

    /**
     * Obtiene el nodo raíz (Parent) asociado a un archivo FXML dado su nombre.
     * @param fxmlName El nombre del archivo FXML (sin la extensión .fxml).
     * @return El nodo raíz asociado al FXML.
     */
    public Parent getParent(String fxmlName) {
        // Si el FXML no está cargado, lo carga
        if (!parents.containsKey(fxmlName)) {
            loadFxml(fxmlName);
        }

        // Devuelve el nodo raíz asociado al FXML
        return parents.get(fxmlName);
    }

    /**
     * Obtiene el controlador asociado a un archivo FXML dado su nombre.
     * @param fxml El nombre del archivo FXML (sin la extensión .fxml).
     * @param <T> El tipo del controlador.
     * @return El controlador asociado al FXML.
     */
    @SuppressWarnings("unchecked")
    public <T> T getController(String fxml) {
        return (T) controllers.get(fxml);
    }

    /**
     * Verifica si el archivo FXML asociado a un nombre ha sido cargado.
     * @param fxmlName El nombre del archivo FXML (sin la extensión .fxml).
     * @return true si el archivo FXML ha sido cargado, false en caso contrario.
     */
    public boolean containsParent(String fxmlName) {
        return parents.containsKey(fxmlName);
    }

    /**
     * Verifica si el controlador asociado a un nombre de archivo FXML ha sido almacenado.
     * @param fxmlName El nombre del archivo FXML (sin la extensión .fxml).
     * @return true si el controlador ha sido almacenado, false en caso contrario.
     */
    public boolean containsController(String fxmlName) {
        return controllers.containsKey(fxmlName);
    }

    /**
     * Limpia los HashMaps que almacenan nodos raíz y controladores.
     */
    public void clear() {
        parents.clear();
        controllers.clear();
    }

    /**
     * Obtiene la ruta base para los archivos FXML.
     * @return La ruta base para los archivos FXML.
     */
    public String getFxmlPaths() {
        return fxmlPaths;
    }
}

