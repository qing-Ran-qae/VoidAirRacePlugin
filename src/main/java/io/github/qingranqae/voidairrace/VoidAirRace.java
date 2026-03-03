package io.github.qingranqae.voidairrace;

import io.github.qingranqae.voidairrace.core.config.Config;
import io.github.qingranqae.voidairrace.event.PluginEnableEvent;
import io.github.qingranqae.voidairrace.util.ClassScanner;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class VoidAirRace extends JavaPlugin {
    @Override
    public void onEnable() {
        // 文字画
        getLogger().info(" __     ___    ____  ");
        getLogger().info(" \\ \\   / / \\  |  _ \\ ");
        getLogger().info("  \\ \\ / / _ \\ | |_) |");
        getLogger().info("   \\ V / ___ \\|  _ < ");
        getLogger().info("    \\_/_/   \\_\\_| \\_\\");

        // 自动注册所有事件监听器
        autoRegisterListeners();

        // 发布插件启用事件
        new PluginEnableEvent(this).callEvent();
    }

    @Override
    public void onDisable() {
        getLogger().info("禁用中...");
        Config.getInstance().saveAllConfigs();
    }

    /**
     * 自动扫描并注册所有实现了 Listener 接口的类。
     */
    private void autoRegisterListeners() {
        List<Class<? extends Listener>> listenerClasses = ClassScanner.scanSubclasses(this, Listener.class);
        PluginManager pm = Bukkit.getPluginManager();
        int success = 0, fail = 0;
        for (Class<? extends Listener> clazz : listenerClasses) {
            try {
                Listener listener = clazz.getDeclaredConstructor().newInstance();
                pm.registerEvents(listener, this);
                getLogger().fine("自动注册监听器: " + clazz.getName());
                success++;
            } catch (Exception e) {
                getLogger().warning("无法实例化事件监听器 " + clazz.getName() + "，请确保有无参构造器");
                fail++;
            }
        }
        getLogger().fine("共找到 " + listenerClasses.size() + " 个事件监听器类，成功注册 " + success + " 个，失败 " + fail + " 个");
    }
}