package org.katia;

import org.katia.core.EngineCore;
import org.katia.core.Scene;
import org.katia.core.components.SpriteComponent;
import org.katia.factory.GameObjectFactory;
import org.katia.factory.SceneFactory;
import org.katia.game.Game;

public class Main {

    public static void main(String[] args) {
        try {
            EngineCore.initialize();
        } catch (RuntimeException e) {
            Logger.log(Logger.Type.ERROR, e.toString());
        }

        Game game = new Game();
//        game.run().dispose();
//
        Scene scene = SceneFactory.createScene("Main", game.getConfiguration().getWidth(), game.getConfiguration().getHeight());

        var go = GameObjectFactory.createGameObject();
        SpriteComponent s = new SpriteComponent();
        s.setTexture("C:\\Users\\milos\\OneDrive\\Pictures\\Screenshots\\Screenshot 2024-03-10 192322.png");
        go.addComponent(s);
        scene.addGameObject(go);

        Scene ss = SceneFactory.generateSceneFromJson(SceneFactory.generateJsonFromScene(scene));
        EngineCore.dispose();
    }
}