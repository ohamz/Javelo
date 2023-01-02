package ch.epfl.javelo.gui;

import ch.epfl.javelo.Preconditions;

import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

/**
 * Public final class representing an OSM tile manager, its role is to get the tiles from a tile server
 * and store them in a memory cache and in a disk cache.
 */
public final class TileManager {
    private final Path path;
    private final String name;

    private final int MAP_SIZE_LIMIT = 100;
    private final Map<TileId, Image> cacheMemoryMap;

    /**
     * Public record representing an OSM Tile that contains 3 attributes,
     * a zoom level, an x coordinate and a y coordinate
     */
    public record TileId(int zoomLevel, int x, int y) {

        public TileId {
            Preconditions.checkArgument(isValid(zoomLevel, x, y));
        }

        public static boolean isValid(int zoomLevel, int x, int y) {
            double indexLimit = 1 << zoomLevel;
            return (zoomLevel >= 0  && x >= 0 && x < indexLimit && y >= 0 && y < indexLimit);
        }
    }

    /**
     * Public constructor of TileManager
     * @param path : destination of the cache
     * @param name : name of the server
     */
    public TileManager(Path path, String name) {
        this.path = path;
        this.name = name;
        cacheMemoryMap = new LinkedHashMap<>(MAP_SIZE_LIMIT, 0.75f,  true);
    }

    /**
     * Public method that returns the image of the given TileId
     * @param tileId : given tile identity
     * @return a JavaFX Image
     * @throws IOException if errors occur when opening files
     */
    public Image imageForTileAt(TileId tileId) throws IOException {
        if (cacheMemoryMap.containsKey(tileId)) {
            return cacheMemoryMap.get(tileId);
        }

        Path basePath = path
                .resolve(String.valueOf(tileId.zoomLevel))
                .resolve(String.valueOf(tileId.x)).resolve(tileId.y + ".png");

        createDirectory(basePath, tileId);
        Image image = createImage(basePath);
        addToCache(tileId, image);

        return image;
    }

    /**
     * Private method that creates the directory and transfers the wanted image from the browser
     * @param basePath : given destined path
     * @param tileId : given TileId
     * @throws IOException if errors occur when opening files
     */
    private void createDirectory(Path basePath, TileId tileId) throws IOException {
        if (!Files.exists(basePath)) {
            Path directoryPath = basePath.subpath(0, basePath.getNameCount() - 1);
            Files.createDirectories(directoryPath);
            URL u = new URL("https://" + name + "/" + tileId.zoomLevel
                    + "/" + tileId.x + "/" + tileId.y + ".png");
            URLConnection c = u.openConnection();
            c.setRequestProperty("User-Agent", "JaVelo");
            InputStream i = c.getInputStream();

            try(i; OutputStream o = new FileOutputStream(basePath.toFile())) {
                i.transferTo(o);
            }
        }
    }

    /**
     * Private method that creates and returns the image in the destined path
     * @param basePath : given destined path
     * @return a JavaFX Image
     * @throws IOException if errors occur when opening files
     */
    private Image createImage(Path basePath) throws IOException {
        Image image;
        try (InputStream i = new FileInputStream(basePath.toFile()))  {
            image = new Image(i);
        }
        return image;
    }

    /**
     * Private method that adds the given image to the local cache memory
     * and removes the less recently used if the cache is full
     * @param tileId : given TileId
     * @param image  : given JavaFX Image
     */
    private void addToCache(TileId tileId, Image image) {
        if (!(cacheMemoryMap.size() < MAP_SIZE_LIMIT)) {
            Iterator<TileId> iterator = cacheMemoryMap.keySet().iterator();
            TileId id = iterator.next();
            cacheMemoryMap.remove(id);
        }
        cacheMemoryMap.put(tileId, image);
    }


}
