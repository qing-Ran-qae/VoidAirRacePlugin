package io.github.hhn756.voidairrace.infrastructure.listenerregistrar;

import io.github.hhn756.voidairrace.infrastructure.util.ClassScanner;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

/**
 * 用于向 Bukkit 注册被注解为 {@link AutoRegistration} 的事件监听器
 * */
public class ListenerRegistrar {
    /**
     * 扫描并注册插件内的所有 bukkit 事件监听器
     */
    public static void RegisterAll(JavaPlugin mainClass) {
        Collection<Class<Listener>> listenerClasses = ClassScanner.scanSubclasses(Listener.class);
        PluginManager pm = Bukkit.getPluginManager();

        // 操作结果计数
        int success = 0, fail = 0;

        // 注册监听器
        for (Class<Listener> clazz : listenerClasses) {
            try {
                if (!clazz.isAnnotationPresent(AutoRegistration.class)) {
                    continue;
                }

                Listener listener = clazz.getDeclaredConstructor().newInstance();
                pm.registerEvents(listener, mainClass);

                success++;
            } catch (Exception e) {
                mainClass.getLogger().warning("无法注册事件监听器 " + clazz.getName() + "：" + e.getMessage());
                fail++;
            }
        }

        // 输出结果
        mainClass.getLogger().fine(
                "成功注册 " + success
                + " 个事件监听器，失败 " + fail
                + " 个"
        );
    }
}
