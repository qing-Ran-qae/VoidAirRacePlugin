package io.github.qingranqae.voidairrace.config;

import io.github.qingranqae.voidairrace.VoidAirRace;
import io.github.qingranqae.voidairrace.pluginevent.PluginDisableEvent;import io.github.qingranqae.voidairrace.pluginevent.PluginEnableEvent;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.IOException;

public class Config implements Listener {
    private static Config instance;
    private YamlConfiguration config;
    private File config_file;
    private static final String CONFIG_FILE_NAME = "config.yml";
    private VoidAirRace mainClass;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPluginEnable(@NonNull PluginEnableEvent event) {
        this.mainClass = event.getMainClass();
        this.config_file = new File(this.mainClass.getDataFolder(), CONFIG_FILE_NAME);

        // 确保目录和文件存在
        this.configFileCheck();

        loadConfig();
    }

    @EventHandler
    public void onPluginDisable(@NonNull PluginDisableEvent event) {
        // 多次尝试保存
        int maxRetries = 3;
        for (int i = 0; i < maxRetries; i++) {
            try {
                getInstance().saveConfig();
                break; // 成功保存，退出循环
            } catch (IOException e) {
                if (i == maxRetries - 1) {
                    event.getMainClass().getLogger().severe("无法持久化配置，总共尝试了" + maxRetries + "次都失败了！错误消息：" + e.getMessage());
                    e.printStackTrace();
                } else {
                    try {
                        Thread.sleep(100); // 等待100ms再重试
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    private Config() {}

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    // ------ 读取 & 保存 ------

    /**
     * 从储存读取配置到内存
     * */
    public void loadConfig() {
        this.config = YamlConfiguration.loadConfiguration(config_file);

        this.selectedMapId = config.getString("selectedMapId");
        this.matchDuration = config.getInt("matchDuration");
    }

    /**
     * 持久化内存中的配置
     * */
    public void saveConfig() throws IOException {
        // 保存字段
        config.set("selectedMapId", selectedMapId);
        config.set("matchDuration", matchDuration);

        config.save(this.config_file);
    }

    private void configFileCheck() {
        if (!config_file.getParentFile().exists()) {
            config_file.getParentFile().mkdirs();
        }
        if (!config_file.exists()) {
            this.mainClass.saveResource(CONFIG_FILE_NAME, false);
        }
    }

    // ------ 字段 定义 & getter & setter ------

    private String selectedMapId;
    private int matchDuration;

    public String getSelectedMapId() {
        return selectedMapId;
    }

    public void setSelectedMapId(String newValue) {
        this.selectedMapId = newValue;
    }

    public int getMatchDuration() {
        return matchDuration;
    }

    public void setMatchDuration(int newValue) {
        this.matchDuration = newValue;
    }
}
