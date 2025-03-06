Katia Game Engine 
---

### Katia Core

---
- [ ] Game
  - [x] Scene Manager
  - [x] Input Manager

- [ ] Components - Remove resources from components.
  - [x] Component
  - [x] Camera component
  - [x] Script component
  - [x] Sprite component
  - [x] Text component
  - [x] Transform component
- [ ] Factory
  - [x] Component factory
  - [x] Game Object factory
---
### Katia Editor

---
#### Windows (Docks)
- [x] Hierarchy Window
  - [x] Improve looks (Add icons etc.)
  - [x] Create game object
  - [x] Delete game object
  - [x] Reorder game objects
  - [x] Create component based game objects
  - [x] Copy/Paste game object
  - [x] Add stripes
  - [x] Click on game object open in inspector
  - [x] Double click on game object move camera on it
- [x] Inspector Window
  - [x] General GO data section
  - [x] Transform component
  - [x] Camera component
  - [x] Script component
  - [x] Text component
  - [x] Sprite component
- [ ] Scene Window
- [ ] Project Window

#### Popups
- [x] Image preview popup

        GLFW.glfwSetFramebufferSizeCallback(handle, (long handle, int w, int h) -> {
            windowSize.set(w, h);
        });
        GLFW.glfwSetKeyCallback(handle, (window, key, scancode, action, mods) -> {
            if (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT) {
                if ((mods & GLFW.GLFW_MOD_CONTROL) != 0) { // Check if Ctrl is pressed
                    switch (key) {
                        case GLFW.GLFW_KEY_N:
//                            uiRenderer.get(MainMenuBar.class).getActions().put(MenuAction.CREATE_NEW_PROJECT, true);
break;
case GLFW.GLFW_KEY_O:
//                            uiRenderer.get(MainMenuBar.class).getActions().put(MenuAction.OPEN_PROJECT, true);
break;
case GLFW.GLFW_KEY_S:
//                            uiRenderer.get(MainMenuBar.class).getActions().put(MenuAction.SAVE_PROJECT, true);
break;
case GLFW.GLFW_KEY_W:
//                            uiRenderer.get(MainMenuBar.class).getActions().put(MenuAction.EXIT, true);
break;
}
}
}
});
GLFW.glfwSetDropCallback(handle, GLFWDropCallback.create((win, count, names) -> {
//            droppedFiles.clear();
//            for (int i = 0; i < count; i++) {
//                droppedFiles.add(GLFWDropCallback.getName(names, i));
//            }
}));

//            if (runGame != null) {
//                GLFW.glfwMakeContextCurrent(runGame.getWindow().getHandle());
//                runGame.update(null);
//
//                if (GLFW.glfwWindowShouldClose(runGame.getWindow().getHandle())) {
//                    runGame.dispose();
//                    runGame = null;
//                }
//            }