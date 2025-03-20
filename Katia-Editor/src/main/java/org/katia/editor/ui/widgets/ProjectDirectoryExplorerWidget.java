package org.katia.editor.ui.widgets;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiHoveredFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import org.katia.FileSystem;
import org.katia.Icons;
import org.katia.Logger;
import org.katia.editor.EditorUI;
import org.katia.editor.managers.EditorAssetManager;
import org.katia.editor.managers.ProjectManager;
import org.katia.editor.popups.FontCreatorPopup;
import org.katia.editor.ui.popups.ImagePreviewPopup;
import org.katia.editor.ui.popups.PopupManager;
import org.katia.editor.ui.windows.CodeEditorWindow;
import org.katia.gfx.resources.Texture;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProjectDirectoryExplorerWidget {

    String root;
    String path;
    List<Path> data;
    List<String> dirDepth;
    ImString searchBarTxt;

    /**
     * Directory explorer widget constructor.
     */
    public ProjectDirectoryExplorerWidget() {
        Logger.log(Logger.Type.INFO, "Creating folder explorer widget");
        this.root = null;
        this.path = null;
        this.data = new ArrayList<>();
        this.dirDepth = new ArrayList<>();
        this.searchBarTxt = new ImString();
    }


    public void setRootDirectory(String path) {
        this.root = path;
        this.loadDirectory(path);
    }

    public void loadDirectory(String path) {
        Logger.log(Logger.Type.INFO, "Directory explorer load directory:", path);
        if (Objects.equals(this.path, path)) {
            return;
        }
        this.path = path;
        this.data = FileSystem.readDirectoryData(path);
        setDirDepth(path);
    }

    private void setDirDepth(String path) {
        Path path1 = Path.of(path);
        Path relativePath = Path.of(this.root).relativize(path1);
        dirDepth.clear();
        dirDepth.add(root);
        Path current = Path.of(root);
        if (!path1.toAbsolutePath().toString().equals(Path.of(root).toAbsolutePath().toString())) {
            for (Path p : relativePath) {
                current = current.resolve(p);
                dirDepth.add(current.toAbsolutePath().toString());
            }
        }
    }

    /**
     * Render project directory explorer.
     */
    public void render() {
        ImGui.beginChild(root, -1, -1, false, ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0);

        ImGui.beginChild("Path trail", -220, 39);
        // Render project selected directory path trail.
        {
            ImGui.setCursorPos(10, 10);
            ImGui.text(Icons.FolderOpen);
            ImGui.sameLine();
            ImGui.setCursorPosY(5);
            ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
            for (int i = 0; i < dirDepth.size(); i++) {
                if (ImGui.button(FileSystem.getFileName(dirDepth.get(i)))) {
                    loadDirectory(dirDepth.get(i));
                }
                ImGui.sameLine();
                if (i + 1 < dirDepth.size()) {
                    ImGui.setCursorPosY(7);
                    ImGui.text(Icons.NextPart);
                    ImGui.sameLine();
                    ImGui.setCursorPosY(5);
                }
            }
            ImGui.popStyleColor();
        }
        ImGui.endChild();
        ImGui.sameLine();

        ImGui.beginChild("Search bar", -1, 39);
        // Render search bar.
        {
            ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 1);
            ImGui.pushStyleColor(ImGuiCol.Border, 0.3f, 0.3f, 0.3f, 1.0f);

            ImGui.setCursorPos(10, 9);

            ImGui.text(  Icons.Filter);
            ImGui.sameLine();
            ImGui.setCursorPosY(7);
            ImGui.setNextItemWidth(-1);
            ImGui.pushFont(EditorAssetManager.getInstance().getFonts().get("Default25"));
            {
                ImGui.inputText("##Search", searchBarTxt);
            }
            ImGui.popFont();
            ImGui.popStyleVar();
            ImGui.popStyleColor();
        }
        ImGui.endChild();
        Path clickedDirectory = null;
        Path clickedFile = null;

        ImGui.beginChild("Directory data window", -1, -20);
        {
            ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0.4F);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.4f, 0.4f, 0.4f, 0.4F);
            ImGui.pushFont(EditorAssetManager.getInstance().getFonts().get("Text15"));
            int fileIndex = 0;
            for (Path entry : data) {
                fileIndex++;
                String filename = FileSystem.getFilenameWithoutExtension(entry.getFileName().toString());

                if (!searchBarTxt.get().isEmpty() && !filename.contains(searchBarTxt.get())) {
                    continue;
                }

                ImVec2 cur = ImGui.getCursorPos();
                ImGui.setCursorPos(cur.x + 5, cur.y);

                String ext = FileSystem.getFileExtension(entry.getFileName().toString());
                if (FileSystem.isImageFile(entry.getFileName().toString())) {
                    Texture t = EditorAssetManager.getInstance()
                            .getImage(entry.toAbsolutePath().toString());

                    double widthRatio = (double) 90 / t.getWidth();
                    double heightRatio = (double) 90 / t.getHeight();
                    double scaleFactor = Math.min(widthRatio, heightRatio);

                    int newWidth = (int) (t.getWidth() * scaleFactor);
                    int newHeight = (int) (t.getHeight() * scaleFactor);
                    ImGui.setCursorPos(cur.x + 5 + ((float) (90 - newWidth) / 2), cur.y + ((float) (90 - newHeight) / 2));
                    ImGui.image(t.getId(), newWidth, newHeight);
                } else {
                    ImGui.image(getIcon(entry).getId(), 90, 90);
                }
                ImGui.setCursorPos(cur.x, cur.y + 85);
                renderTruncatedText(filename, 90);
                ImGui.setCursorPos(cur.x, cur.y);

                if (ImGui.button("##" + filename + fileIndex , 100, 110)) {
                    if (Files.isDirectory(entry)) {

                        clickedDirectory = entry;
                        Logger.log(clickedDirectory.toString());

                    } else  if (FileSystem.isSceneFile(entry.toString())) {
                        ProjectManager.getGame().getSceneManager().setActiveScene(filename);
                    } else if (FileSystem.isLuaFile(entry.toString())) {
                        EditorUI.getInstance().getWindow(CodeEditorWindow.class).openFile(entry.toString());
                    }
                    else {
//                    if (FileSystem.isImageFile(entry.getFileName().toString())) {
                        clickedFile = entry;
                        Logger.log(clickedFile.toString());
                    }
                }
                if (!Files.isDirectory(entry) && Objects.equals(ext, "lua")) {
                    if (ImGui.beginDragDropSource()) {
                        ImGui.setDragDropPayload("LuaScript", entry);
                        ImGui.text(entry.toString());
                        ImGui.endDragDropSource();
                    }
                }
                if (FileSystem.isImageFile(entry.toString())) {
                    if (ImGui.beginDragDropSource()) {
                        ImGui.setDragDropPayload("ImageFile", entry);
                        ImGui.text(entry.toString());
                        ImGui.endDragDropSource();
                    }
                }
                if (FileSystem.isFontFile(entry.toString())) {
                    if (ImGui.beginDragDropSource()) {
                        ImGui.setDragDropPayload("FontFile", entry);
                        ImGui.text(entry.toString());
                        ImGui.endDragDropSource();
                    }
                }
                if (FileSystem.isSoundFile(entry.toString())) {
                    if (ImGui.beginDragDropSource()) {
                        ImGui.setDragDropPayload("GameObject", entry);
                        ImGui.text(entry.toString());
                        ImGui.endDragDropSource();
                    }
                }



                if (ImGui.isItemHovered(ImGuiHoveredFlags.None)) {
                    if (ImGui.isMouseDoubleClicked(0) && Desktop.isDesktopSupported()) {
                        if (!Files.isDirectory(entry) && Objects.equals(ext, "lua")) {
                            System.out.println("ASD");
                        }
                    }
                    ImGui.beginTooltip();
                    ImGui.textDisabled(entry.toString());
                    ImGui.endTooltip();
                }
                if (ImGui.beginPopupContextItem()) {
                    if (ImGui.menuItem("Delete")) {
                    }
                    if (ImGui.menuItem("Rename")) {
                    }
                    ImGui.endPopup();
                }
                if ((cur.x + 200) < ImGui.getWindowWidth()) {
                    ImGui.sameLine();
                }
            }
            ImGui.popFont();
            ImGui.popStyleColor(3);
        }
        ImGui.endChild();
        ImGui.pushFont(EditorAssetManager.getInstance().getFonts().get("Text15"));
        ImGui.textDisabled(path);

        ImGui.popFont();
        if (clickedDirectory != null) {
            loadDirectory(clickedDirectory.toString());
        }

        ImGui.popStyleVar();

        ImGui.endChild();
        if (clickedFile != null) {
            if (FileSystem.isImageFile(clickedFile.toString())) {
//                ImGui.openPopup("Image Preview");
                PopupManager.getInstance().openPopup(ImagePreviewPopup.class, clickedFile.toString());
//                ImagePreviewPopup.setImage(clickedFile.toString());
            }
            if (FileSystem.isFontFile(clickedFile.toString())) {
                System.out.println("WT");
                ImGui.openPopup("Font Creator");
                FontCreatorPopup.setFont(clickedFile.toString());
            }
        }
//        ErrorPopup.render();
//        FontCreatorPopup.render();
    }

    /**
     * Render text with max width.
     * @param text Text.
     * @param maxWidth Max width of text.
     */
    private void renderTruncatedText(String text, float maxWidth) {
        float textWidth = ImGui.calcTextSize(text).x;
        if (textWidth > maxWidth) {
            int charCount = text.length();
            while (charCount > 0 && ImGui.calcTextSize(text.substring(0, charCount) + "...").x > maxWidth) {
                charCount--;
            }
            text = text.substring(0, charCount) + "...";
        }
        ImVec2 size = ImGui.calcTextSize(text);
        ImGui.setCursorPosX(ImGui.getCursorPosX() + (100 - size.x) / 2);
        ImGui.textUnformatted(text);
    }

    /**
     * Get texture icons for provided file.
     * @param file Path to file.
     * @return Texture
     */
    private Texture getIcon(Path file) {
        var images = EditorAssetManager.getInstance().getImages();
        if (Files.isDirectory(file)) {
            return images.get("FolderIcon");
        }
        if (FileSystem.isLuaFile(file.toString())) {
            return images.get("LuaFileIcon");
        }
        if (FileSystem.isJsonFile(file.toString())) {
            return images.get("JsonFileIcon");
        }
        if (FileSystem.isSoundFile(file.toString())) {
            return images.get("SoundFileIcon");
        }
        if (FileSystem.isFontFile(file.toString())) {
            return images.get("FontFileIcon");
        }
        if (FileSystem.isSceneFile(file.toString())) {
            return images.get("SceneFileIcon");
        }
        if (FileSystem.isPrefabFile(file.toString())) {
            return images.get("PrefabFileIcon");
        }
        return images.get("FileIcon");
    }
}
