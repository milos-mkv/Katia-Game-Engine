package org.katia;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class FileSystem {

    /**
     * Create new file.
     * @param path File to create.
     * @return boolean
     */
    public static boolean createFile(String path) {
        try {
            File file = new File(path);
            if (file.createNewFile()) {
                Logger.log(Logger.Type.SUCCESS, "File created!", path);
            } else {
                Logger.log(Logger.Type.WARNING, "File already exists!", path);
            }
            return true;
        } catch (IOException e) {
            Logger.log(Logger.Type.ERROR, "Failed to create file:", path);
            return false;
        }
    }

    /**
     * Check if provided file is json file.
     * @param path Path to file.
     * @return boolean
     */
    public static boolean isJsonFile(String path) {
        return (!Files.isDirectory(Paths.get(path))) && Objects.equals(getFileExtension(path), "json");
    }

    /**
     * Check if provided file is lua file.
     * @param path Path to file.
     * @return boolean
     */
    public static boolean isLuaFile(String path) {
        return (!Files.isDirectory(Paths.get(path))) && Objects.equals(getFileExtension(path), "lua");
    }

    /**
     * Check if provided file is font file.
     * @param path Path to file.
     * @return boolean
     */
    public static boolean isFontFile(String path) {
        return (!Files.isDirectory(Paths.get(path))) && (
                Objects.equals(getFileExtension(path), "ttf") || Objects.equals(getFileExtension(path), "otf") ||
                Objects.equals(getFileExtension(path), "TTF") || Objects.equals(getFileExtension(path), "OTF")
        );
    }

    /**
     * Read data from file.
     * @param path File path.
     * @return String
     */
    public static String readFromFile(String path) {
        String content = null;
        try {
            content = Files.readString(Paths.get(path), StandardCharsets.UTF_8);
            Logger.log(Logger.Type.SUCCESS, "Data read from file:", path);
        } catch (IOException e)  {
            Logger.log(Logger.Type.ERROR, e.toString());
        }
        return content;
    }

    /**
     * Save data to file.
     * @param path File path.
     * @param data Data to save.
     * @return boolean
     */
    public static boolean saveToFile(String path, String data) {
        try {
            Files.write(Paths.get(path), data.getBytes());
            Logger.log(Logger.Type.SUCCESS, "Data written to file:", path);
            return true;
        } catch (IOException e) {
            Logger.log(Logger.Type.ERROR, "Failed to write to file:", path);
            return false;
        }
    }

    /**
     * Create directory if it does not exist!
     * @param path Directory path.
     * @return boolean
     */
    public static boolean createDirectory(String path) {
        File directory = new File(path);
        return !directory.exists() && directory.mkdirs();
    }

    /**
     * Does directory exist.
     * @param path Path to directory.
     * @return boolean
     */
    public static boolean doesDirectoryExists(String path) {
        File directory = new File(path);
        return directory.exists();
    }

    /**
     * Get file extension.
     * @param path File path.
     * @return String
     */
    public static String getFileExtension(String path) {
        int lastDotIndex = path.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < path.length() - 1) {
            return path.substring(lastDotIndex + 1);
        } else {
            return null;
        }
    }

    static final List<String> supportedImageExtensions = List.of("jpg", "jpeg", "png", "bmp", "gif");

    /**
     * Check if file has image extension.
     * @param file File path.
     * @return boolean
     */
    public static boolean isImageFile(String file) {
        return supportedImageExtensions.contains(getFileExtension(file));
    }

    /**
     * Get filename without extension.
     * @param filename File name.
     * @return String
     */
    public static String getFilenameWithoutExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            filename = filename.substring(0, dotIndex);
        }
        return filename;
    }

    /**
     * Get all files and directories from provided root path directory.
     * @param path Root path directory.
     * @return List<Path>
     */
    public static List<Path> readDirectoryData(String path) {
        List<Path> directoryList = new ArrayList<>();
        List<Path> filesList = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(path))) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    directoryList.add(entry);
                }
                else {
                    filesList.add(entry);
                }
            }
        } catch (IOException | DirectoryIteratorException _) { }
        directoryList.addAll(filesList);
        return directoryList;
    }

    /**
     * Get all files and directories from provided root path directory. (Just file names)
     * @param path Root path directory.
     * @return List<String>
     */
    public static List<String> readDirectoryDataNames(String path) {
        List<Path> data = readDirectoryData(path);
        // Load all directory names from root to current selected directory.
        List<String> names = new ArrayList<>();
        for (Path p : data) {
            names.add(p.getFileName().toString());
        }
        return names;
    }

    /**
     * Get path depth from path1 to path2.
     * @param path1 Path 1.
     * @param path2 Path 2.
     * @return List<String>
     */
    public static List<String> getPathDepth(String path1, String path2) {
        Path p1 = Paths.get(path1);
        Path p2 = Paths.get(path2);
        Path relativePath = p1.relativize(p2);
        List<String> data = new ArrayList<>();

        if (p2.toAbsolutePath().toString().equals(p1.toAbsolutePath().toString())) {
            return null;
        }

        for (Path part : relativePath) {
            data.add(part.toString());
        }
        return data;
    }

    /**
     * Reads a file from the file system or classpath into a direct ByteBuffer.
     *
     * @param resource   The path to the file or resource.
     * @param bufferSize Initial buffer size.
     * @return A ByteBuffer containing the file's contents.
     * @throws IOException If the file cannot be read.
     */
    public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;

        Path path = Paths.get(resource);

        // Try to read from the file system
        if (Files.isReadable(path)) {
            try (SeekableByteChannel channel = Files.newByteChannel(path)) {
                buffer = ByteBuffer.allocateDirect((int) channel.size() + 1).order(ByteOrder.nativeOrder());
                while (channel.read(buffer) != -1) {
                    // Reading file into buffer
                }
                buffer.flip();
                return buffer;
            }
        }

        // Try to load as a resource from the classpath
        try (InputStream source = Utils.class.getClassLoader().getResourceAsStream(resource);
             ReadableByteChannel channel = Channels.newChannel(source)) {
            buffer = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder());

            while (true) {
                int bytes = channel.read(buffer);
                if (bytes == -1) break;

                if (buffer.remaining() == 0) {
                    // Expand the buffer if it's too small
                    ByteBuffer newBuffer = ByteBuffer.allocateDirect(buffer.capacity() * 2).order(ByteOrder.nativeOrder());
                    buffer.flip();
                    newBuffer.put(buffer);
                    buffer = newBuffer;
                }
            }
            buffer.flip();
        }

        return buffer;
    }
}
