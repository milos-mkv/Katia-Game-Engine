package org.katia;

import org.katia.core.EngineCore;
import org.katia.core.Scene;
import org.katia.core.components.SpriteComponent;
import org.katia.factory.GameObjectFactory;
import org.katia.factory.SceneFactory;
import org.katia.factory.ShaderProgramFactory;
import org.katia.game.Game;

public class Main {

    public static void main(String[] args) {
        try {
            EngineCore.initialize();
        } catch (RuntimeException e) {
            Logger.log(Logger.Type.ERROR, e.toString());
        }


        Game.getInstance().run().dispose();

        EngineCore.dispose();
    }
}