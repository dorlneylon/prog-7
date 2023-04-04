package itmo.lab7.utils.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Config class for reading configuration files.
 */
public class Config {

    private final HashMap<String, String> configMap;

    /**
     * Constructor for Config class.
     *
     * @param fileName The name of the configuration file.
     * @throws FileNotFoundException If the file is not found.
     */
    public Config(String fileName) throws FileNotFoundException {
        // Create a new file object with the given fileName
        File config = new File(fileName);

        // Check if the file exists
        if (config.exists()) {
            // If the file exists, read the file and store the contents in the configMap
            this.configMap = readFile(config);
            // Return from the method
            return;
        }
        // If the file does not exist, throw a FileNotFoundException
        throw new FileNotFoundException("File not found: " + fileName);
    }

    /**
     * Gets the value associated with the given key.
     *
     * @param key The key to look up.
     * @return The value associated with the given key, or null if the key is not found.
     */
    public String get(String key) {
        return configMap.get(key);
    }

    /**
     * Reads a configuration file and returns a {@link HashMap} with the key-value pairs.
     *
     * @param config The configuration file to read
     * @return A {@link HashMap} containing the key-value pairs from the configuration file
     */
    private HashMap<String, String> readFile(File config) {
        HashMap<String, String> configMap = new HashMap<>();
        try (Scanner scanner = new Scanner(config)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // Skipping comments and lines without "Key=Value"
                if (line.startsWith("#") || !line.contains("=")) continue;
                String[] split = line.split("=", 2);
                if (split.length == 2) configMap.put(split[0], split[1]);
            }
        } catch (FileNotFoundException ignored) {
            // Ignoring this, because file was existence checked
        }
        return configMap;
    }
}
