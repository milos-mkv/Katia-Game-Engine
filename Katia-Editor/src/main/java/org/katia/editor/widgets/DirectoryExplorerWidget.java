package org.katia.editor.widgets;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiHoveredFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import org.katia.FileSystem;
import org.katia.Logger;
import org.katia.editor.Editor;
import org.katia.editor.managers.EditorAssetManager;
import org.katia.factory.TextureFactory;
import org.katia.gfx.Texture;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DirectoryExplorerWidget {

    String root;
    String path;
    List<Path> data;
    List<String> dirDepth;
    ImString searchBarTxt;

    /**
     * Directory explorer widget constructor.
     */
    public DirectoryExplorerWidget() {
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

    public void render() {
        ImGui.beginChild(root, -1, -1, false, ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0);

        ImGui.beginChild("Path trail", -200, 39);
        ImGui.setCursorPos(10, 10);
        ImGui.text("\uf07c");
        ImGui.sameLine();
        ImGui.setCursorPosY(5);
        ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);

        for (int i = 0; i < dirDepth.size(); i++) {
            if (ImGui.button(FileSystem.getFileName(dirDepth.get(i)))) {
                loadDirectory(dirDepth.get(i));
            }
            ImGui.sameLine();
            if (i+1 < dirDepth.size()) {
                ImGui.setCursorPosY(7);
                ImGui.text("\uf0da");
                ImGui.sameLine();
                ImGui.setCursorPosY(5);
            }
        }
        ImGui.popStyleColor();
        ImGui.endChild();
        ImGui.sameLine();

        ImGui.beginChild("Search bar", -1, 39);
        ImGui.setCursorPos(10, 10);
        ImGui.text("\uf0b0");
        ImGui.sameLine();
        ImGui.setCursorPosY(7);
        ImGui.setNextItemWidth(-1);
        ImGui.pushStyleColor(ImGuiCol.Border, 0.3f, 0.3f, 0.3f, 1.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 1);
        ImGui.inputText("##Search", searchBarTxt);
        ImGui.popStyleVar();
        ImGui.popStyleColor();
        ImGui.endChild();
        ImGui.beginChild("Directory data window", -1, -20);
        ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0.4F);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.4f, 0.4f, 0.4f, 0.4F);
        if (ImGui.isWindowHovered()) {
            for (String g : Editor.getInstance().getDroppedFiles()) {
                System.out.println(g);
            }
        }

        ImGui.pushFont(EditorAssetManager.getInstance().getFonts().get("Text15"));
        Path clickedDirectory = null;
        for (Path entry : data) {
            String filename = FileSystem.getFilenameWithoutExtension(entry.getFileName().toString());

//            if (!searchBarTxt.get().isEmpty() && !filename.contains(searchBarTxt.get())) {
//                continue;
//            }

            ImVec2 cur = ImGui.getCursorPos();
            ImGui.setCursorPos(cur.x + 5, cur.y);

            String ext = FileSystem.getFileExtension(entry.getFileName().toString());
            String image;
            if(Files.isDirectory(entry)) {
                image = "FolderIcon";
            } else {
                if (FileSystem.isImageFile(entry.getFileName().toString()) ) {
                    image = "ImageFileIcon";
                } else
                if (Objects.equals(ext, "lua")) {
                    image = "LuaFileIcon";
                }else if (Objects.equals(ext, "json")) {
                    image = "JsonFileIcon";
                }else if (Objects.equals(ext, "scene")) {
                    image = "SceneFileIcon";
                }
                else {
                    image = "FileIcon";
                }
            }
            if (FileSystem.isImageFile(ext)) {
                Texture t = TextureFactory.createTexture(entry.toAbsolutePath().toString());

                double widthRatio = (double) 90 / t.getWidth();
                double heightRatio = (double) 90 / t.getHeight();
                double scaleFactor = Math.min(widthRatio, heightRatio);

                int newWidth = (int) (t.getWidth() * scaleFactor);
                int newHeight = (int) (t.getHeight() * scaleFactor);
                ImGui.setCursorPos(cur.x + 5 + ((90 - newWidth) / 2) , cur.y + ((90 - newHeight) / 2));

                ImGui.image(t.getId(), newWidth, newHeight, 0, 1, 1, 0);
            }
            else
                ImGui.image(EditorAssetManager.getInstance().getImages().get(image).getId(), 90, 90);
            ImGui.setCursorPos(cur.x, cur.y + 85);
            renderTruncatedText(filename, 90);
            ImGui.setCursorPos(cur.x, cur.y);

            if (ImGui.button("##"+filename, 100, 110)) {
                if (Files.isDirectory(entry)) {
                    clickedDirectory = entry;
                }
            }
            if (ImGui.isItemHovered(ImGuiHoveredFlags.None)) {
                if (ImGui.isMouseDoubleClicked(0) && Desktop.isDesktopSupported()) {
                    if (!Files.isDirectory(entry) && Objects.equals(ext, "lua")) {
                        System.out.println("ASD");
                     //   GameEngineUI.getInstance().addCodeEditor(entry);

//                        Desktop desktop = Desktop.getDesktop();
//                        try {
//                            desktop.open(new File(entry.toString()));
//                        } catch (IOException e) {
//                        }
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
            if((cur.x + 200) < ImGui.getWindowWidth())  {
                ImGui.sameLine();
            }
        }
        ImGui.popFont();
        ImGui.popStyleColor(3);

        ImGui.endChild();
        ImGui.pushFont(EditorAssetManager.getInstance().getFonts().get("Text15"));
        ImGui.textDisabled(path);

        ImGui.popFont();
        if (clickedDirectory != null) {
            loadDirectory(clickedDirectory.toString());
        }
        ImGui.popStyleVar();

        ImGui.endChild();
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
}
