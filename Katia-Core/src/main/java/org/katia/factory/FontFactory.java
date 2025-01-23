package org.katia.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.katia.FileSystem;
import org.katia.Logger;
import org.katia.gfx.Font;
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
import java.util.HashMap;
import java.util.Map;

public abstract class FontFactory {

    static Map<String, Font> fonts;

    /**
     * Initialize font factory.
     */
    public static void initialize() {
        fonts = new HashMap<>();
    }

    /**
     * Loads a font file and creates a baked bitmap and glyph information.
     *
     * @param path Path to the font file (TTF/OTF).
     * @param fontSize Font size.
     * @param bitmapWidth Bitmap width.
     * @param bitmapHeight Bitmap height.
     */
    public static Font createFont(String path, float fontSize, int bitmapWidth, int bitmapHeight) {
        if (fonts.get(path) != null) {
            Logger.log(Logger.Type.INFO, "Font already loaded:", path);
            return fonts.get(path);
        }
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
            fonts.put(path, font);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            try {
               String json = objectMapper.writeValueAsString(font);
                Logger.log(Logger.Type.SUCCESS, json);
            } catch (JsonProcessingException e) {
                Logger.log(Logger.Type.ERROR, e.toString());
            }
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
