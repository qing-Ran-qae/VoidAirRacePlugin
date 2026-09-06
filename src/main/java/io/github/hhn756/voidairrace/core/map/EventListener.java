package io.github.hhn756.voidairrace.core.map;

import io.github.hhn756.voidairrace.constants.Categories;
import io.github.hhn756.voidairrace.event.PluginEnableEvent;
import io.github.hhn756.voidairrace.infrastructure.listenerregistrar.AutoRegistration;
import io.github.hhn756.voidairrace.infrastructure.registry.Registry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * 地图模块的事件监听器
 * */
@AutoRegistration
public class EventListener implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    public void onPluginEnable(PluginEnableEvent event) {
        // 定义“游戏地图”类别，键计算：注册项元数据中的地图id
        Registry.getInstance().createCategory(Categories.MAP, MapEntry::getKey);

        MapInitializer.load();
        // 初始化所有地图
        MapInitializer.getInstance().initAllMapsAsync();
    }
}
