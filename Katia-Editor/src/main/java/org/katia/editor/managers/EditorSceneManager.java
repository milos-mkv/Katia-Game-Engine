package org.katia.editor.managers;

import lombok.Data;
import lombok.Getter;
import org.katia.FileSystem;
import org.katia.Logger;
import org.katia.core.GameObject;
import org.katia.core.Scene;
import org.katia.factory.SceneFactory;

import java.util.List;

@Data
public class EditorSceneManager {

    @Getter
    static EditorSceneManager instance =new EditorSceneManager();

    Scene scene;
    String path;

    public EditorSceneManager() {
        Logger.log(Logger.Type.INFO, "Creating editor scene manager ...");
        scene = null;
        path = null;
    }

    /**
     * Open scene from file.
     * @param path Scene file path.
     * @throws RuntimeException When scene file is not valid.
     */
    public void openScene(String path) throws RuntimeException {
        Logger.log(Logger.Type.INFO, "Open scene from file:", path);

        String json = FileSystem.readFromFile(path);
        if (json.isEmpty()) {
            throw new RuntimeException("Scene file is not valid!");
        }

        Scene sceneFromJson = SceneFactory.generateSceneFromJson(json);
        if (sceneFromJson == null) {
            throw new RuntimeException("Scene file is not valid!");
        }

        this.scene = sceneFromJson;
        this.path = path;

        Logger.log(Logger.Type.SUCCESS, "Scene loaded:", path);
    }

}
