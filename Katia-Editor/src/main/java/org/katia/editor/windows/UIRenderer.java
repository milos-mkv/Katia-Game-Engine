package org.katia.editor.windows;

import java.util.HashMap;

public class UIRenderer {

    HashMap<Class<?>, UIComponent> components;

    public UIRenderer() {
        components = new HashMap<>();
        components.put(HierarchyWindow.class, new HierarchyWindow());
        components.put(InspectorWindow.class, new InspectorWindow());
        components.put(ProjectWindow.class, new ProjectWindow());
        components.put(ConsoleWindow.class, new ConsoleWindow());
        components.put(SceneWindow.class, new SceneWindow());
        components.put(MainMenuBar.class, new MainMenuBar());
    }

    public void render() {
        components.forEach((key, value) -> value.render());
    }

}
