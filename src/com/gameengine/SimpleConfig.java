package com.gameengine;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gameengine.data.PropertyStorage;

/**
 * Simple configuration manager for the game engine
 * Replaces the complex SecurityManager with basic configuration
 */
public class SimpleConfig implements PropertyStorage {
    
    private final Map<String, Object> properties;
    private final File dataRoot;
    
    public SimpleConfig() {
        this.properties = new HashMap<>();
        
        // Set default properties
        properties.put("screen-width", 1280);
        properties.put("screen-height", 720);
        properties.put("language-path", "lang");
        properties.put("default-language", "en");
        
        // Set data root to a simple directory
        String userHome = System.getProperty("user.home");
        this.dataRoot = new File(userHome, ".java-game-engine");
        
        // Create data directory if it doesn't exist
        if (!dataRoot.exists()) {
            dataRoot.mkdirs();
        }
    }
    
    @Override
    @Nullable
    public Object getProperty(@NotNull String key) {
        return properties.get(key);
    }
    
    /**
     * Set a property value
     */
    public void setProperty(@NotNull String key, @Nullable Object value) {
        properties.put(key, value);
    }
    
    /**
     * Get the data root directory
     */
    public File getDataRoot() {
        return dataRoot;
    }
    
    /**
     * Request a file or folder within the data root
     */
    @NotNull
    public File requestDataFolder(@NotNull String path) throws IOException {
        boolean isDir = path.endsWith("/") || path.endsWith(File.separator);
        File file = new File(dataRoot, path);
        
        // Create parent directories if they don't exist
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        
        if (isDir) {
            if (!file.exists()) {
                file.mkdirs();
            }
        } else {
            if (!file.exists()) {
                file.createNewFile();
            }
        }
        
        return file;
    }
}