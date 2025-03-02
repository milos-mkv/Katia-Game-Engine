package org.katia.editor.ui;

import lombok.Data;
import org.katia.Logger;
import org.katia.editor.menubar.MainMenuBar;

import java.util.HashMap;

@Data
public class UI {

    HashMap<Class<?>, UIComponent> components;

    /**
     * Editor UI Constructor.
     */
    public UI() {
        Logger.log(Logger.Type.INFO, "Editor UI Constructor");
        components = new HashMap<>();
        components.put(HierarchyWindow.class, new HierarchyWindow());
        components.put(InspectorWindow.class, new InspectorWindow());
        components.put(ProjectWindow.class, new ProjectWindow());
//        components.put(ConsoleWindow.class, new ConsoleWindow());
        components.put(SceneWindow.class, new SceneWindow());
        components.put(MainMenuBar.class, new MainMenuBar());
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T>  component) {
        return (T) components.get(component);
    }

    /**
     * Render editor UI.
     */
    public void render() {
        components.forEach((key, value) -> value.render());
    }
}