package org.automation.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Configuration manager that loads platform configurations from a JSON file.
 *
 * <p>This singleton class reads {@code platform_data.json} from the classpath,
 * substitutes environment variable placeholders ({@code ${ENV_VAR_NAME}}),
 * and provides lookup by platform name.</p>
 *
 * <p><b>Usage:</b> Access via {@link #getInstance()} and retrieve configurations
 * using {@link #getPlatformConfig(String)}.</p>
 *
 * <p><b>Thread Safety:</b> The instance is created lazily with double-checked locking
 * to ensure thread-safe initialization.</p>
 *
 * @since 1.0.0
 */
public class ConfigManager {

    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static final String CONFIG_FILE = "platform_data.json";
    private static ConfigManager instance;
    private List<PlatformConfig> platforms;

    /**
     * Private constructor that loads the configuration on first instantiation.
     */
    private ConfigManager() {
        loadConfig();
    }

    /**
     * Returns the singleton ConfigManager instance.
     *
     * @return the ConfigManager instance
     */
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    /**
     * Loads platform configurations from the classpath resource file.
     *
     * @throws RuntimeException if the configuration file cannot be loaded or parsed
     */
    private void loadConfig() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE);

            if (inputStream == null) {
                throw new IllegalArgumentException("Configuration file not found: " + CONFIG_FILE);
            }

            // Read raw JSON and substitute environment variables
            String rawJson = new String(inputStream.readAllBytes());
            String substitutedJson = substituteEnvVariables(rawJson);

            platforms = mapper.readValue(substitutedJson, new TypeReference<List<PlatformConfig>>() {});
            logger.info("Loaded {} platform configurations", platforms.size());
        } catch (Exception e) {
            logger.error("Error loading configuration file", e);
            throw new RuntimeException("Failed to load platform configuration", e);
        }
    }

    /**
     * Substitutes {@code ${ENV_VAR_NAME}} placeholders with actual environment variable values.
     *
     * <p>If an environment variable is not set, it is replaced with an empty string
     * and a warning is logged.</p>
     *
     * @param json the raw JSON string
     * @return the JSON string with substituted values
     */
    private String substituteEnvVariables(String json) {
        Pattern pattern = Pattern.compile("\\$\\{([A-Za-z_][A-Za-z0-9_]*)}");
        Matcher matcher = pattern.matcher(json);

        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String envVarName = matcher.group(1);
            String envVarValue = System.getenv(envVarName);

            if (envVarValue != null) {
                matcher.appendReplacement(result, Matcher.quoteReplacement(envVarValue));
            } else {
                // If env var is not set, use empty string or throw error
                logger.warn("Environment variable '{}' is not set. Using empty value.", envVarName);
                matcher.appendReplacement(result, "");
            }
        }
        matcher.appendTail(result);

        return result.toString();
    }

    /**
     * Returns the platform configuration for the given platform name.
     *
     * @param platformName the name of the platform (case-insensitive)
     * @return the PlatformConfig for the specified platform
     * @throws IllegalArgumentException if no platform with the given name exists
     */
    public PlatformConfig getPlatformConfig(String platformName) {
        Optional<PlatformConfig> platform =
                platforms.stream().filter(p -> p.getName().equalsIgnoreCase(platformName)).findFirst();

        if (platform.isEmpty()) {
            throw new IllegalArgumentException("Platform not found: " + platformName);
        }

        return platform.get();
    }

    /**
     * Returns all loaded platform configurations.
     *
     * @return the list of PlatformConfig objects
     */
    public List<PlatformConfig> getAllPlatforms() {
        return platforms;
    }

    /**
     * Returns the default browser for the given platform.
     *
     * @param platformName the platform name
     * @return the browser name, or "chromium" if not specified
     */
    public String getBrowserForPlatform(String platformName) {
        PlatformConfig config = getPlatformConfig(platformName);
        return config.getBrowser() != null ? config.getBrowser() : "chromium";
    }

    /**
     * Checks if the platform should run in headless mode.
     *
     * @param platformName the platform name
     * @return true if headless mode is enabled for this platform
     */
    public boolean isHeadlessForPlatform(String platformName) {
        PlatformConfig config = getPlatformConfig(platformName);
        return config.isHeadless() != null && config.isHeadless();
    }
}
