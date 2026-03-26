package io.github.qingranqae.voidairrace.core.mapsystem;

import io.github.qingranqae.voidairrace.service.config.Config;
import io.github.qingranqae.voidairrace.service.config.ObservableYamlConfiguration;
import io.github.qingranqae.voidairrace.service.config.files.ConfigFiles;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

/**
 * 游戏地图必须实现此接口<br>
 * 用于获取地图基本信息和控制地图初始化
 * */
public abstract class GameMap {
    /**
     * 此地图的 ID
     * */
    protected @NonNull String MAP_ID = "null";

    /**
     * 获取地图标识符<br>
     * 此方法返回值应始终固定且和其他地图重复
     *
     * @return 地图标识符
     * */
     public @NonNull String getId() {
         return MAP_ID;
     };

    /**
     * 获取地图显示名称
     *
     * @return 地图的显示名称
     * */
    public @NonNull Component getDisplayName() {
        return Component.text("null");
    };

    /**
     * 获取地图描述文本
     *
     * @return 地图的描述文本
     * */
    public @NonNull Component getDescription() {
        return Component.text("null");
    };

    /**
     * 插件首次启用或执行重新初始化命令时执行<br>
     * 注：此方法在主线程执行
     *
     * @return 当返回的 {@code CompletableFuture} 正常完成时，地图将被标记为“已初始化”，如果返回值异常完成那么将不会添加标记
     * */
    public @NonNull CompletableFuture<?> initAsync(JavaPlugin mainClass) {
        return CompletableFuture.completedFuture(null);
    }

    /**
     * 在重新初始化时执行一次<br>
     * 注：需通过地图初始化器重初始化，否则此方法不会被自动调用
     * */
    public void unInit(JavaPlugin mainClass) {}

    /**
     * 检查地图的初始化状态
     *
     * @return 如果地图已初始化则返回{@code true}，否则返回{@code false}
     * */
    public boolean isInited() {
        return MapInitializer.getInstance().isInited(MAP_ID);
    }

    /**
     * 获取属于此地图的配置文件
     *
     * @param configFile 目标文件（不包含扩展名）
     * */
    public @NonNull ObservableYamlConfiguration getConfigFile(ConfigFiles configFile) {
        return Config.getInstance().getConfig(configFile);
    }
}
