package org.katia.gfx;

import lombok.Data;
import org.katia.FileSystem;
import org.katia.factory.TextureFactory;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTruetype;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

@Data
public class FontLoader {

    private final int bitmapWidth;
    private final int bitmapHeight;
    private final float fontSize;

    private ByteBuffer bitmap;
    private STBTTBakedChar.Buffer charData;
    private Texture texture;

    public FontLoader(String fontFilePath, float fontSize, int bitmapWidth, int bitmapHeight) {
        this.fontSize = fontSize;
        this.bitmapWidth = bitmapWidth;
        this.bitmapHeight = bitmapHeight;

        loadFont(fontFilePath);
        saveBitmap("test.png");
        texture = TextureFactory.createTexture("test.png");
    }

    /**
     * Loads a font file and creates a baked bitmap and glyph information.
     *
     * @param fontFilePath Path to the font file (TTF/OTF).
     */
    private void loadFont(String fontFilePath) {
        try {
            ByteBuffer fontBuffer = FileSystem.ioResourceToByteBuffer(fontFilePath, 1024);

            // Prepare a ByteBuffer for the baked bitmap
            bitmap = BufferUtils.createByteBuffer(bitmapWidth * bitmapHeight);
            charData = STBTTBakedChar.malloc(96); // For ASCII range 32-127

            // Bake the font bitmap
            int result = STBTruetype.stbtt_BakeFontBitmap(
                    fontBuffer, // Font data
                    fontSize,   // Font size in pixels
                    bitmap,     // Baked bitmap buffer
                    bitmapWidth, bitmapHeight, // Bitmap dimensions
                    32,         // First ASCII character
                    charData    // Baked character data
            );

            if (result <= 0) {
                throw new IllegalStateException("Failed to bake font bitmap.");
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to load font file: " + fontFilePath, e);
        }
    }

    /**
     * Saves the baked bitmap as a PNG image for debugging.
     *
     * @param outputPath Path to save the bitmap image.
     */
    public void saveBitmap(String outputPath) {
        try {
            BufferedImage image = new BufferedImage(bitmapWidth, bitmapHeight, BufferedImage.TYPE_BYTE_GRAY);
            for (int y = 0; y < bitmapHeight; y++) {
                for (int x = 0; x < bitmapWidth; x++) {
                    int value = bitmap.get(y * bitmapWidth + x) & 0xFF;
                    image.setRGB(x, y, new Color(value, value, value).getRGB());
                }
            }
            File outputFile = new File(outputPath);
            ImageIO.write(image, "png", outputFile);
            System.out.println("Font bitmap saved to " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves glyph information for a specific character.
     *
     * @param character The character to get glyph information for.
     * @return The baked character data.
     */
    public STBTTBakedChar getGlyphInfo(char character) {
        if (character < 32 || character > 127) {
            throw new IllegalArgumentException("Character out of range (must be ASCII 32-127). Found: " + (int) character);
        }
        return charData.get(character - 32);
    }

    /**
     * @return The baked bitmap as a ByteBuffer.
     */
    public ByteBuffer getBitmap() {
        return bitmap;
    }

    /**
     * @return The glyph information buffer.
     */
    public STBTTBakedChar.Buffer getCharData() {
        return charData;
    }
}
