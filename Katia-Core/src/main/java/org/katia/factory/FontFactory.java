package org.katia.factory;

import org.katia.FileSystem;
import org.katia.Logger;
import org.katia.gfx.resources.Font;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTruetype;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class is responsible for generating font resources.
 */
public abstract class FontFactory {

    /**
     * Loads a font file and creates a baked bitmap and glyph information.
     * @param path Path to the font file (TTF/OTF).
     */
    public static Font createFont(String path) {
        float fontSize = 72;
        int bitmapWidth = 512;
        int bitmapHeight = 512;
        try {
            ByteBuffer fontBuffer = FileSystem.ioResourceToByteBuffer(path, 1024);
            ByteBuffer bitmap = BufferUtils.createByteBuffer(bitmapWidth * bitmapHeight);
            STBTTBakedChar.Buffer charData = STBTTBakedChar.malloc(96); // For ASCII range 32-127

            int result = STBTruetype.stbtt_BakeFontBitmap(fontBuffer, fontSize, bitmap, bitmapWidth, bitmapHeight, 32, charData);

            if (result <= 0) {
                Logger.log(Logger.Type.ERROR, "Failed to bake font bitmap.");
                return null;
            }
            Font font = new Font(path, fontSize, bitmapWidth, bitmapHeight, charData, bitmap, null);

            Path path1 = Paths.get(path);
            String imagePath = path1.getParent().toString() + "/" + FileSystem.getFilenameWithoutExtension(path1.getFileName().toString()) + ".png";
            saveBitmap(font, imagePath);
            font.setTexture(TextureFactory.createTexture(imagePath));

            return font;
        } catch (IOException e) {
            Logger.log(Logger.Type.ERROR, "Failed to load font file:", path, e.toString());
            return null;
        }
    }

    /**
     * Saves the baked bitmap as a PNG image for debugging.
     * @param font Font to save.
     * @param outputPath Path to save the bitmap image.
     */
    public static void saveBitmap(Font font, String outputPath) {
        try {
            BufferedImage image = new BufferedImage(font.getBitmapWidth(), font.getBitmapHeight(), BufferedImage.TYPE_BYTE_GRAY);
            for (int y = 0; y < font.getBitmapHeight(); y++) {
                for (int x = 0; x < font.getBitmapWidth(); x++) {
                    int value = font.getBitmap().get(y * font.getBitmapWidth() + x) & 0xFF;
                    image.setRGB(x, y, new Color(value, value, value).getRGB());
                }
            }
            File outputFile = new File(outputPath);
            ImageIO.write(image, "png", outputFile);
            Logger.log(Logger.Type.SUCCESS, "Font bitmap saved to:", outputPath);
        } catch (IOException e) {
            Logger.log(Logger.Type.ERROR, e.toString());
        }
    }

}
