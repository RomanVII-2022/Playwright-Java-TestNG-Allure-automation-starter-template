package org.automation.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public class ConfigManager {

    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static final String CONFIG_FILE = "test_data.json";
    private static ConfigManager instance;
    private List<PlatformConfig> platforms;


    private ConfigManager() {
        loadConfig();
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    private void loadConfig() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE);

            if (inputStream == null) {
                throw new IllegalArgumentException("Configuration file not found: " + CONFIG_FILE);
            }

            platforms = mapper.readValue(inputStream, new TypeReference<List<PlatformConfig>>() {});
            logger.info("Loaded {} platform configurations", platforms.size());
        } catch (Exception e) {
            logger.error("Error loading configuration file", e);
            throw new RuntimeException("Failed to load platform configuration", e);
        }
    }

    public PlatformConfig getPlatformConfig(String platformName) {
        Optional<PlatformConfig> platform =
                platforms.stream().filter(p -> p.getName().equalsIgnoreCase(platformName)).findFirst();

        if (platform.isEmpty()) {
            throw new IllegalArgumentException("Platform not found: " + platformName);
        }

        return platform.get();
    }

    public List<PlatformConfig> getAllPlatforms() {
        return platforms;
    }
}
