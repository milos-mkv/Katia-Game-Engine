package org.katia.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    /**
     * Load configuration from file.
     * @param path Path to katia-conf.json file.
     * @return Configuration
     */
    static Configuration load(String path) {
        ObjectMapper objectMapper = new ObjectMapper();
        Configuration configuration = null;
        try {
            configuration = objectMapper.readValue(FileSystem.readFromFile(path), Configuration.class);
        } catch (JsonProcessingException e) {
            Logger.log(Logger.Type.ERROR, e.toString());
        }
        return configuration;
    }
}
