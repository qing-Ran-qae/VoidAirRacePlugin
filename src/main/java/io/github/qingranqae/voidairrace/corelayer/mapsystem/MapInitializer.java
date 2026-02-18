package io.github.qingranqae.voidairrace.corelayer.mapsystem;

import io.github.qingranqae.voidairrace.corelayer.config.Config;
import io.github.qingranqae.voidairrace.corelayer.config.ConfigFiles;
import io.github.qingranqae.voidairrace.event.PluginEnableEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.function.Supplier;

public class MapInitializer implements Listener {
    private MapInitializer() {}

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        initAllMaps();
    }

    private void initAllMaps() {
        HashMap<String, Supplier<GameMap>> maps = MapRegistry.getInstance().getAllMaps();
        FileConfiguration flags = Config.getInstance().getConfig(ConfigFiles.FLAGS);

        for (HashMap.Entry<String, Supplier<GameMap>> entry : maps.entrySet()) {
            GameMap mapInst = entry.getValue().get();
            String initFlag = mapInst.getId();

            boolean inited = flags.getBoolean(initFlag, false);
            if (!inited) {
                mapInst.init();
                flags.set(initFlag, true);
            }
        }
    }
}
