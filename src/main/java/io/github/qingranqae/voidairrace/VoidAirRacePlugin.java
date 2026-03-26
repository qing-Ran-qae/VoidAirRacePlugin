package io.github.qingranqae.voidairrace;

import io.github.qingranqae.voidairrace.event.PluginEnableEvent;
import io.github.qingranqae.voidairrace.infrastructure.listenerregistrar.ListenerRegistrar;
import io.github.qingranqae.voidairrace.service.config.Config;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class VoidAirRacePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        // 文字画
        Logger logger = getLogger();
        logger.info(" __     ___    ____  ");
        logger.info(" \\ \\   / / \\  |  _ \\ ");
        logger.info("  \\ \\ / / _ \\ | |_) |");
        logger.info("   \\ V / ___ \\|  _ < ");
        logger.info("    \\_/_/   \\_\\_| \\_\\");

        // 自动注册所有 Bukkit 事件监听器
        ListenerRegistrar.autoRegisterListeners(this);

        // 发布插件启用事件
        new PluginEnableEvent(this).callEvent();
    }

    @Override
    public void onDisable() {
        getLogger().fine("禁用中...");
        Config.getInstance().saveAllConfigs();
    }
}