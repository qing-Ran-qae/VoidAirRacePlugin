package io.github.hhn756.voidairrace.core.map;

import io.github.hhn756.voidairrace.constants.Namespace;
import io.github.hhn756.voidairrace.custom.GameElement;
import io.github.hhn756.voidairrace.service.config.Config;
import io.github.hhn756.voidairrace.service.config.YamlConfig;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

/**
 * 游戏地图的基类<br>
 * 用于获取地图基本信息和控制地图初始化
 * */
public abstract class GameMap implements GameElement {
    /**
     * 子类未指定其ID时方法所用的默认地图ID<br>
     * 系统保留此ID，不可被任何地图使用
     * */
    public static final NamespacedKey DEFAULT_MAP_ID = Namespace.of("game_map_default_id");

    /**
     * 插件首次启用或执行重新初始化命令时执行<br>
     * 此方法在主线程执行
     *
     * @return 当返回的 {@link CompletableFuture} 正常完成时，地图将被标记为“已初始化”
     * */
    public @NonNull CompletableFuture<?> initAsync(JavaPlugin mainClass) {
        return CompletableFuture.completedFuture(null);
    }

    /**
     * 在重新初始化时执行一次，用于回到初始化前的状态<br>
     * 注：需通过{@link MapInitializer#reinitMap(NamespacedKey)}重初始化，否则此方法不会被自动调用
     * */
    public void unInit() {}

    /**
     * 检查地图的初始化状态
     *
     * @return 如果地图已初始化将返回{@code true}，否则返回{@code false}
     * */
    public boolean isInited() {
        return MapInitializer.getInstance().isInited(DEFAULT_MAP_ID);
    }

    /**
     * 获取一个属于游戏地图的配置文件（yaml格式）
     *
     * @param filePath 目标文件
     * */
    public @NonNull YamlConfig getYmlConfig(@NonNull String filePath) {
        return Config.getInstance().getYmlConfig(filePath);
    }

    /**
     * 获取一个属于此地图的资源的路径
     *
     * @param path 资源路径（含后缀名），相对地图目录
     *
     * @return 完整资源路径，以 {@code /} 开头
     * */
    public String resourcePath(String path) {
        return "/map/" + getElementMeta().id() + "/" + path;
    }
}
