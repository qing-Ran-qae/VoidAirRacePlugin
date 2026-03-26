package io.github.qingranqae.voidairrace.core.matchsystem;

import io.github.qingranqae.voidairrace.event.PluginEnableEvent;
import io.github.qingranqae.voidairrace.infrastructure.listenerregistrar.AutoRegistration;
import io.github.qingranqae.voidairrace.service.config.Config;
import io.github.qingranqae.voidairrace.service.config.ObservableYamlConfiguration;
import io.github.qingranqae.voidairrace.service.config.files.FlagsKeys;
import io.github.qingranqae.voidairrace.service.config.files.PublicFiles;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * 比赛系统的事件监听器，负责处理与比赛相关的 Bukkit 事件。
 * 目前主要用于在插件启用时恢复比赛状态（例如处理上次服务器关闭时未结束的比赛）。
 */
@AutoRegistration
public class EventListener implements Listener {

    /**
     * 在插件启用事件触发时执行。
     * 检查 flags.yml 中的 {@link FlagsKeys#ON_SERVER_STARTED_STOP_MATCH} 标志，
     * 若为 true，则强制结束当前比赛（如果有）并重置该标志。
     *
     * @param event 插件启用事件
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPluginEnable(PluginEnableEvent event) {
        // 初始化比赛调度器
        MatchCoordinator matchCoordinator = MatchCoordinator.getInstance(event.getMainClass());

        ObservableYamlConfiguration flagConfig = Config.getInstance().getConfig(PublicFiles.FLAGS);
        boolean stopMatchFlag = flagConfig.getBoolean(FlagsKeys.ON_SERVER_STARTED_STOP_MATCH);
        if (stopMatchFlag) {
            matchCoordinator.stopMatch(true);
            flagConfig.set(FlagsKeys.ON_SERVER_STARTED_STOP_MATCH, false);
        }
    }
}