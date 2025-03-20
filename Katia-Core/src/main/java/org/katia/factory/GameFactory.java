package org.katia.factory;

import org.katia.Logger;
import org.katia.game.Configuration;
import org.katia.game.Game;
import org.katia.game.Window;
import org.katia.gfx.SceneRenderer;
import org.katia.managers.AudioManager;
import org.katia.managers.InputManager;
import org.katia.managers.ResourceManager;
import org.katia.managers.SceneManager;
import org.katia.scripting.LuaScriptExecutioner;

/**
 * This class is responsible for instancing games.
 */
public abstract class GameFactory {

    /**
     * Create game from provided game project directory.
     * @param directory Path to root project directory.
     * @return Game
     */
    public static Game createGame(String directory) {
        Logger.log("Creating game:", directory);
        Game game = new Game();
        game.setDirectory(directory);
        game.setConfiguration(Configuration.load(directory + "/katia-conf.json"));
        game.setWindow(new Window(game));
        game.setAudioManager(new AudioManager(game));

        game.setResourceManager(new ResourceManager(game));
        game.setSceneManager(new SceneManager(game));
        game.setInputManager(new InputManager(game));
        game.setScriptExecutioner(new LuaScriptExecutioner(game));
        game.setSceneRenderer(new SceneRenderer(game));
        return game;
    }

    /**
     * Create game instance that can be used by editor.
     * @param directory Path to root project directory.
     * @return Game
     */
    public static Game createWindowLessGame(String directory) {
        Logger.log("Creating game for editor:", directory);
        Game game = new Game();
        game.setDirectory(directory);
        game.setConfiguration(Configuration.load(directory + "/katia-conf.json"));
        game.setAudioManager(new AudioManager(game));
        game.setResourceManager(new ResourceManager(game));
        game.setSceneManager(new SceneManager(game));
        // NOTE: We do not need:
        //  - Input manager
        //  - Lua Script Executioner
        game.setSceneRenderer(new SceneRenderer(game));
        return game;
    }


}
