package org.katia.editor;

import org.katia.Logger;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.nfd.NativeFileDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

public abstract class EditorUtils {

    public static String openFileDialog() {
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            try {
                Process process = Runtime.getRuntime().exec(new String[]{"zenity", "--file-selection"});
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String filePath = reader.readLine();
                if (filePath != null) {
                    return filePath;
                } else {
                    Logger.log(Logger.Type.WARNING, "No file selected!");
                    return null;
                }
            } catch (IOException e) {
                Logger.log(Logger.Type.ERROR, e.toString());
                return null;
            }
        } else {
            try {
                var pointerBuffer = PointerBuffer.allocateDirect(1);
                NativeFileDialog.NFD_OpenDialog(pointerBuffer, null, (CharSequence) null);
                return pointerBuffer.getStringASCII().replace("\\", "/");
            } catch (Exception e) {
                Logger.log(Logger.Type.ERROR, "Failed to open file:", e.toString());
                return null;
            }
        }
    }

    public static String openFolderDialog() {
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            try {
                Process process = Runtime.getRuntime().exec(new String[]{"zenity", "--file-selection", "--directory"});
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String filePath = reader.readLine();
                if (filePath != null) {
                    return filePath;
                } else {
                    Logger.log(Logger.Type.WARNING, "No folder selected!");
                    return null;
                }
            } catch (IOException e) {
                Logger.log(Logger.Type.ERROR, e.toString());
                return null;
            }
        } else {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                PointerBuffer outPathPtr = stack.mallocPointer(1);
                int result = NativeFileDialog.NFD_PickFolder(outPathPtr, (ByteBuffer) null);

                if (result == NativeFileDialog.NFD_OKAY) {
                    String folder = MemoryUtil.memUTF8(outPathPtr.get(0));
                    Logger.log(Logger.Type.SUCCESS, "Open folder:", folder);
                    return folder;
                }
                return null;
            }
        }
    }

}
