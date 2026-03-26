package io.github.qingranqae.voidairrace.infrastructure.listenerregistrar;

import io.github.qingranqae.voidairrace.infrastructure.util.ClassScanner;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ListenerRegistrar {
    /**
     * 自动扫描并注册所有实现了 Listener 接口的类。
     */
    public static void autoRegisterListeners(JavaPlugin mainClass) {
        List<Class<? extends Listener>> listenerClasses = ClassScanner.scanSubclasses(mainClass, Listener.class);
        PluginManager pm = Bukkit.getPluginManager();
        int success = 0, fail = 0, cancel = 0;
        for (Class<? extends Listener> clazz : listenerClasses) {
            try {
                if (!clazz.isAnnotationPresent(AutoRegistration.class)) {
                    cancel++;
                    continue;
                }

                Listener listener = clazz.getDeclaredConstructor().newInstance();
                pm.registerEvents(listener, mainClass);

                mainClass.getLogger().fine("自动注册监听器: " + clazz.getName());
                success++;
            } catch (Exception e) {
                mainClass.getLogger().warning("无法实例化事件监听器 " + clazz.getName() + "，请确保有无参构造器");
                fail++;
            }
        }
        mainClass.getLogger().fine("共找到 " + listenerClasses.size() + " 个事件监听器类，成功注册 " + success + " 个，失败/取消 " + fail + "/" + cancel + " 个");
    }
}
