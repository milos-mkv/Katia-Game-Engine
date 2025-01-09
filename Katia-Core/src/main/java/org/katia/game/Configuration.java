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
     * @return Configuration
     */
    static Configuration load() {
        ObjectMapper objectMapper = new ObjectMapper();
        Configuration configuration = null;
        try {
            configuration = objectMapper.readValue(FileSystem.readFromFile("katia-conf.json"), Configuration.class);
        } catch (JsonProcessingException e) {
            Logger.log(Logger.Type.ERROR, e.toString());
        }
        return configuration;
    }
}
