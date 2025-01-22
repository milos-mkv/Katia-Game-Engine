package org.katia.editor.windows;

import lombok.Data;
import org.katia.editor.menubar.MainMenuBar;

import java.util.HashMap;

@Data
public class UIRenderer {

    HashMap<Class<?>, UIComponent> components;

    public UIRenderer() {
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

    public void render() {
        components.forEach((key, value) -> value.render());
    }

}
