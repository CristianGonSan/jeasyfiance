package com.comiftouch.jeasyfinance.model.config;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

public class PropertiesManager {
    public final String USER_HOME = System.getProperty("user.home");
    private final Properties properties = new Properties();
    private final File externalFile;

    /**
     * Crea una instancia de PropertiesManager y se encarga de asegurar la existencia del archivo de configuración externo.
     * Si el archivo externo ya existe, carga las propiedades desde ese archivo.
     * Si el archivo externo no existe, intenta copiar el archivo interno al directorio externo y luego carga las propiedades desde el archivo copiado.
     *
     * @param internalFilePath La ruta del archivo interno de configuración.
     * @param externalFilePath El directorio externo donde se almacenará el archivo de configuración.
     */
    public PropertiesManager(String internalFilePath, String externalFilePath) {
        externalFile = new File(USER_HOME + File.separator + externalFilePath);

        if (externalFile.exists()) {
            try (FileInputStream fileInputStream = new FileInputStream(externalFile)) {
                properties.load(fileInputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        try (InputStream inputStream = getClass().getResourceAsStream(internalFilePath)) {
            if (inputStream != null) {
                externalFile.mkdirs();

                Files.copy(inputStream, externalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                if (externalFile.exists()) {
                    try (FileInputStream fileInputStream = new FileInputStream(externalFile)) {
                        properties.load(fileInputStream);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Devuelve el objeto Properties que contiene las propiedades cargadas desde el archivo de configuración.
     *
     * @return El objeto Properties con las propiedades cargadas.
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Guarda las propiedades en un archivo externo si este existe.
     * Si el archivo no existe, no se realiza ninguna acción.
     */
    public void store() {
        if (externalFile.exists()) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(externalFile)) {
                properties.store(fileOutputStream, null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Recarga las propiedades desde un archivo externo si este existe.
     * Si el archivo no existe, no se realiza ninguna acción.
     */
    public void reload() {
        if (externalFile.exists()) {
            try (FileInputStream fileInputStream = new FileInputStream(externalFile)) {
                properties.clear();
                properties.load(fileInputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public int getIntProperty(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    public int getIntProperty(String key, int defaultValue) {
        return Integer.parseInt(properties.getProperty(key));
    }

    public float getFloatProperty(String key) {
        return Float.parseFloat(properties.getProperty(key));
    }

    public float getFloatProperty(String key, float defaultValue) {
        return Float.parseFloat(properties.getProperty(key));
    }

    public boolean getBooleanProperty(String key) {
        return Boolean.parseBoolean(properties.getProperty(key));
    }

    public String getStringProperty(String key) {
        return properties.getProperty(key);
    }
}
