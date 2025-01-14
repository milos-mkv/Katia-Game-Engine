package org.katia.gfx;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.katia.Logger;
import org.lwjgl.stb.STBTTBakedChar;

import java.nio.ByteBuffer;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Font {

    String path;
    float size;
    int bitmapWidth;
    int bitmapHeight;
    STBTTBakedChar.Buffer charData;
    ByteBuffer bitmap;
    Texture texture;

    /**
     * Get information about provided character in font bitmap.
     * @param character Character.
     * @return STBTTBakedChar
     */
    @JsonIgnore
    public STBTTBakedChar getGlyphInfo(char character) {
        if (character < 32 || character > 127) {
            Logger.log(Logger.Type.ERROR, "Character out of range (must be ASCII 32-127). Found:", String.valueOf(character));
            return null;
        }
        return charData.get(character - 32);
    }
}
