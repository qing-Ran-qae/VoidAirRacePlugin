package io.github.qingranqae.voidairrace.corelayer.config;

import io.github.qingranqae.voidairrace.VoidAirRace;
import io.github.qingranqae.voidairrace.event.PluginDisableEvent;
import io.github.qingranqae.voidairrace.event.PluginEnableEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class Config implements Listener {
    private static Config instance;

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    // ------------

    private final HashMap<String, FileConfiguration> configs = new HashMap<>();
    private VoidAirRace mainClass;

    private Config() {}

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPluginEnable(PluginEnableEvent event) {
        this.mainClass = event.getMainClass();

        loadAllConfigs();
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        saveAllConfigs();
    }

    /**
     * 重新从储存读取所有配置到内存
     * */
    private void loadAllConfigs() {
        for (Map.Entry<String, FileConfiguration> i : configs.entrySet()) {
            String configName = i.getKey();
            FileConfiguration config = i.getValue();
            configs.put(configName, YamlConfiguration.loadConfiguration(configNameToOSFile(configName)));
        }
    }

    /**
     * 持久化内存中的所有配置
     * */
    private void saveAllConfigs() {
        int maxRetries = 3;
        for (Map.Entry<String, FileConfiguration> i : configs.entrySet()) {
            String configName  = i.getKey();
            FileConfiguration config = i.getValue();
            saveOneConfig(configName, config, maxRetries);
        }
    }

    private void saveOneConfig(String configName, FileConfiguration config, int maxRetries) {
        for (int i = 1; i <= maxRetries; i++) {
            try {
                config.save(configNameToOSFile(configName));
                break;
            } catch (IOException e) {
                if (i == maxRetries) {
                    mainClass.getLogger().log(Level.SEVERE, "无法保存配置文件 '" + configName + "' 总共尝试了 " + maxRetries + " 次都失败了", e);
                }
            }
        }
    }

    private File configNameToOSFile(String name) {
        return new File(mainClass.getDataFolder(), name + ".yml");
    }

    private File configNameToJarFile(String name) {
        return new File(name + ".yml");
    }

    public FileConfiguration getConfig(ConfigFiles config) {
        String configName = config.getFileName();
        File jarFile = configNameToJarFile(configName);
        File osFile = configNameToOSFile(configName);
        // 将 jar 中的配置文件复制到配置目录
        if (!osFile.isFile()) {
            mainClass.saveResource(jarFile.getPath(), false);
        }
        // 读取配置
        if (!configs.containsKey(configName)) {
            configs.put(configName, YamlConfiguration.loadConfiguration(osFile));
        }
        return configs.get(configName);
    }
}
