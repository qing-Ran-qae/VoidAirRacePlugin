package io.github.qingranqae.voidairrace;

import io.github.qingranqae.voidairrace.command.MatchCommand;
import io.github.qingranqae.voidairrace.command.MenuCommand;
import io.github.qingranqae.voidairrace.corelayer.audiovisualservices.MatchMessage;
import io.github.qingranqae.voidairrace.corelayer.config.Config;
import io.github.qingranqae.voidairrace.event.PluginDisableEvent;
import io.github.qingranqae.voidairrace.event.PluginEnableEvent;
import io.github.qingranqae.voidairrace.test.Test;
import io.github.qingranqae.voidairrace.test.TestListener;
import io.github.qingranqae.voidairrace.uilayer.menu.MainMenu;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class VoidAirRace extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        Logger logger = this.getLogger();

        // 文字画
        logger.info(" __     ___    ____  ");
        logger.info(" \\ \\   / / \\  |  _ \\ ");
        logger.info("  \\ \\ / / _ \\ | |_) |");
        logger.info("   \\ V / ___ \\|  _ < ");
        logger.info("    \\_/_/   \\_\\_| \\_\\");

        // 注册事件监听器（暂时先这样写），之后改自动注册
        PluginManager plugManager = Bukkit.getPluginManager();
        plugManager.registerEvents(new TestListener(), this);
        plugManager.registerEvents(new MainMenu(), this);
        plugManager.registerEvents(new MatchCommand(), this);
        plugManager.registerEvents(Config.getInstance(), this);
        plugManager.registerEvents(new MatchMessage(), this);
        plugManager.registerEvents(new io.github.qingranqae.voidairrace.corelayer.mapsystem.maps.lobby.Listener(), this);

        // 暂时先这样写，之后改自动注册
        this.getCommand("menu").setExecutor(new MenuCommand());

        // test
        plugManager.registerEvents(new Test(), this);

        // 发布事件
        PluginEnableEvent enableEvent = new PluginEnableEvent(this);
        enableEvent.callEvent();
    }

    @Override
    public void onDisable() {
        // 发布事件
        PluginDisableEvent disableEvent = new PluginDisableEvent(this);
        disableEvent.callEvent();
    }
}