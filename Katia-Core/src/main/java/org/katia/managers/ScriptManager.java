package org.katia.managers;

import org.katia.FileSystem;
import org.katia.Logger;
import org.katia.game.Game;

import java.nio.file.Files;
import java.util.HashMap;

public class ScriptManager {

    Game game;
    HashMap<String, String> scripts;
    String path;

    /**
     * Script manager constructor.
     * @param game Game instance.
     */
    public ScriptManager(Game game) {
        Logger.log(Logger.Type.INFO, "Creating script manager ...");
        this.game = game;
        this.path = game.getDirectory();
        this.scripts = new HashMap<>();

        loadScripts(path);
    }

    /**
     * Load scripts from provided directory.
     * @param path Path.
     */
    private void loadScripts(String path) {
        Logger.log(Logger.Type.INFO, "Loading scripts from:", path);
        FileSystem.readDirectoryData(path).stream().filter((entry) -> {
            if (Files.isDirectory(entry)) {
                return true;
            } else if (FileSystem.isLuaFile(entry.toString())) {
                scripts.put(FileSystem.getFilenameWithoutExtension(entry.getFileName().toString()), path);
            }
            return false;
        }).toList().forEach((dir) -> loadScripts(dir.toString()));
    }
}
