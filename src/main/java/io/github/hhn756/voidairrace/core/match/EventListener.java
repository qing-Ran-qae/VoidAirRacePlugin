package io.github.hhn756.voidairrace.core.match;

import io.github.hhn756.voidairrace.event.PluginEnableEvent;
import io.github.hhn756.voidairrace.infrastructure.listenerregistrar.AutoRegistration;
import io.github.hhn756.voidairrace.service.config.Config;
import io.github.hhn756.voidairrace.service.config.YamlConfig;
import io.github.hhn756.voidairrace.service.config.files.FlagsKeys;
import io.github.hhn756.voidairrace.service.config.files.PublicFiles;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@AutoRegistration
public class EventListener implements Listener {

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        ComponentRegistry.load();
        MatchCoordinator.load();

        checkMatchAbort();
    }

    private void checkMatchAbort(){
        YamlConfig flagConfig = Config.getInstance().getYmlConfig(PublicFiles.FLAGS);
        boolean isAborted = flagConfig.get(FlagsKeys.MATCH_ABORTED, false);
        if (isAborted) {
            // TODO: 恢复上次异常结束的比赛


            // 标记 “比赛异常结束已处理”
            flagConfig.set(FlagsKeys.MATCH_ABORTED, false);
        }
    }
}
