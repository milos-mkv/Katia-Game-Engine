package org.katia.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.katia.FileSystem;
import org.katia.Logger;

@JsonDeserialize
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Configuration {

    String title;
    int width;
    int height;
    boolean vSync;
    boolean resizable;

    /**
     * Load configuration from file.
     * @param path Path to katia-conf.json file.
     * @return Configuration
     */
    public static Configuration load(String path) {
        Logger.log(Logger.Type.INFO, "Load configuration file:", path);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        Configuration configuration = null;
        try {
            configuration = objectMapper.readValue(FileSystem.readFromFile(path), Configuration.class);
        } catch (Exception e) {
            Logger.log(Logger.Type.ERROR, e.toString());
        }
        return configuration;
    }

    /**
     * Create json from configuration.
     * @param configuration Configuration.
     * @return String
     */
    public static String toJson(Configuration configuration) {
        Logger.log(Logger.Type.INFO, "Generate json from configuration:", configuration.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            return objectMapper.writeValueAsString(configuration);
        } catch (JsonProcessingException e) {
            Logger.log(Logger.Type.ERROR, e.toString());
            return null;
        }
    }

    /**
     * Check if configuration is valid.
     * @return boolean
     */
    public boolean isValid() {
        return (title != null && width > 100 && height > 100);
    }

}
