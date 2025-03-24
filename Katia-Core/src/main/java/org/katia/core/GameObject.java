package org.katia.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.katia.Logger;
import org.katia.core.components.Component;
import org.katia.core.components.TransformComponent;
import org.katia.factory.ComponentFactory;

import java.lang.ref.WeakReference;
import java.util.*;

/**
 * GameObject representation class.
 */
@JsonDeserialize
@NoArgsConstructor
@Data
public class GameObject {

    @JsonIgnore
    public static int TotalID = 0;

    private UUID id;
    private String name;
    private List<GameObject> children;
    private Map<Class<?>, Component> components;
    private boolean isFromPrefab = false;
    @JsonIgnore
    int selectID;

    @Getter
    private boolean active;

    @JsonIgnore
    @ToString.Exclude
    private WeakReference<GameObject> parent;

    /**
     * Game object constructor.
     * @param id ID.
     * @param name Name.
     */
    public GameObject(UUID id, String name) {
        this.id = id;
        this.name = name;
        this.children = new ArrayList<>();
        this.components = new LinkedHashMap<>();
        this.parent = null;
        this.active = true;
        this.selectID = ++TotalID;
    }

    /**
     * Remove this game object from its parent.
     */
    public void removeFromParent() {
        if (this.parent != null) {
            Objects.requireNonNull(this.parent.get()).removeChild(this);
        }
    }

    /**
     * Destroy game object.
     */
    public void dispose() {
        Logger.log(Logger.Type.DISPOSE, "Disposing of Game Object:", name);
        this.removeFromParent();
        components.forEach((_, component) -> component.dispose());
        for (int i = children.size() - 1; i >= 0; i--) {
            children.get(i).dispose();
        }
    }

    /**
     * Add child game object.
     * @param gameObject Game Object.
     */
    public void addChild(GameObject gameObject) {
        addChild(gameObject, this.children.size());
    }

    /**
     * Add child game object at index.
     * @param gameObject Game Object.
     * @param index Index.
     */
    public void addChild(GameObject gameObject, int index) {
        if (gameObject == this || gameObject.isChild(this)) {
            return;
        }
        gameObject.removeFromParent();
        this.children.add(index, gameObject);
        gameObject.setParent(this);
        gameObject.getComponent(TransformComponent.class)
                .setParent(getComponent(TransformComponent.class));
    }

    /**
     * Set parent game object.
     * @param gameObject Game Object.
     */
    public void setParent(GameObject gameObject) {
         this.parent = new WeakReference<GameObject>(gameObject);
         if (gameObject != null) {
             this.getComponent(TransformComponent.class).setParent(this.parent.get().getComponent(TransformComponent.class));
         } else {
             this.getComponent(TransformComponent.class).setParent(null);
         }
    }

    /**
     * Get index of child game object.
     * @param gameObject Game Object.
     * @return int
     */
    public int getChildIndex(GameObject gameObject) {
        return children.indexOf(gameObject);
    }

    /**
     * Remove child game object.
     * @param gameObject Game Object.
     */
    public void removeChild(GameObject gameObject) {
        this.children.remove(gameObject);
        if (gameObject != null) {
            gameObject.setParent(null);
        }
    }

    public void removeAllChildren() {
        for (var child : children) {
            child.setParent(null);
        }
        children = new ArrayList<>();
    }

    /**
     * Remove child game object at index.
     * @param index Index.
     */
    public void removeChild(int index) {
        try {
            GameObject gameObject = this.children.remove(index);
            gameObject.setParent(null);
        } catch (RuntimeException e) {
            Logger.log(Logger.Type.ERROR, "Failed to remove game object from children!");
        }
    }

    /**
     * Add component.
     * @param component Component.
     * @param <T> Component type.
     */
    public <T> void addComponent(T component) {
        this.components.put(component.getClass(), (Component) component);
    }

    /**
     * Remove component.
     * @param componentType Component type as string.
     */
    public void removeComponent(String componentType) {
        this.components.remove(ComponentFactory.getComponentClass(componentType));
    }

    /**
     * Get component.
     * @param componentType Component type.
     * @return Component
     * @param <T> Component original type.
     */
    @SuppressWarnings("unchecked")
    public <T> T getComponent(Class<T> componentType) {
        return (T) this.components.get(componentType);
    }

    /**
     * Get component.
     * @param componentType Component type as string.
     * @return Component
     * @param <T> Component original type.
     */
    @SuppressWarnings("unchecked")
    public <T> T getComponent(String componentType) {
        return (T) this.components.get(ComponentFactory.getComponentClass(componentType));
    }

    /**
     * Find child game object based on its name.
     * @param name Name of Game Object.
     * @return GameObject
     */
    public GameObject find(String name) {
        for (GameObject child : children) {
            if (child.getName().equals(name)) {
                return child;
            }
            GameObject inChild = child.find(name);
            if (inChild != null) {
                return inChild;
            }
        }
        return null;
    }

    /**
     * Find child game object based on its name.
     * @param name Name of Game Object.
     * @return GameObject
     */
    public GameObject find(UUID id) {
        for (GameObject child : children) {
            if (child.getId().equals(id)) {
                return child;
            }
            GameObject inChild = child.find(id);
            if (inChild != null) {
                return inChild;
            }
        }
        return null;
    }

    /**
     * Find child game object with provided select ID.
     * @param id Select ID.
     * @return GameObject
     */
    public GameObject findBySelectID(int id) {
        for (GameObject child : children) {
            if (child.getSelectID() == id) {
                return child;
            }
            GameObject inChild = child.findBySelectID(id);
            if (inChild != null) {
                return inChild;
            }
        }
        return null;
    }

    /**
     * Check if game object is child in this game object.
     * @param gameObject Game Object.
     * @return boolean
     */
    public boolean isChild(GameObject gameObject) {
        for (GameObject child : children) {
            if (child == gameObject || child.isChild(gameObject)) {
                return true;
            }
        }
        return false;
    }
}
