package io.github.qingranqae.voidairrace.service.spawnutil;

import io.github.qingranqae.voidairrace.event.ConfigFieldChangeEvent;
import io.github.qingranqae.voidairrace.infrastructure.listenerregistrar.AutoRegistration;
import io.github.qingranqae.voidairrace.service.config.files.GameSettingKeys;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@AutoRegistration
public class EventListener implements Listener {
    @EventHandler
    public void onConfigChange(ConfigFieldChangeEvent event) {
        if (event.getField().equals(GameSettingKeys.SPAWN_LOCATION)) {
            SpawnUtil.updateSpawnLocation();
        }
    }
}
