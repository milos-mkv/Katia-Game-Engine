package org.katia.editor;

import org.katia.Logger;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.nfd.NativeFileDialog;

import java.nio.ByteBuffer;

public abstract class EditorUtils {

    public static String openFileDialog() {
        try {
            var pointerBuffer = PointerBuffer.allocateDirect(1);
            NativeFileDialog.NFD_OpenDialog(pointerBuffer, null, (CharSequence) null);
            return pointerBuffer.getStringASCII().replace("\\", "/");
        } catch (Exception e) {
            Logger.log(Logger.Type.ERROR, "Failed to open file:", e.toString());
            return null;
        }
    }

    public static String openFolderDialog() {
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
