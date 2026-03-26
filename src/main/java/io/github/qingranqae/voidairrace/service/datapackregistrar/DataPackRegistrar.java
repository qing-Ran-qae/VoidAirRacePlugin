package io.github.qingranqae.voidairrace.service.datapackregistrar;

import io.github.qingranqae.voidairrace.constants.Namespace;
import io.github.qingranqae.voidairrace.constants.ResourcePath;
import io.github.qingranqae.voidairrace.infrastructure.BootstrapModule;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.net.URISyntaxException;

public class DataPackRegistrar implements BootstrapModule {
    @Override
    public void onBootstrap(@NonNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.DATAPACK_DISCOVERY, event -> {
            try {
                event.registrar().discoverPack(
                        getClass().getResource(
                                "/" + ResourcePath.DATAPACK.getPath()
                        ).toURI(),
                        Namespace.namespace
                );
            } catch (IOException | URISyntaxException | NullPointerException e) {
                context.getLogger().warn(
                        Component.text("加载数据包失败：" + e.getLocalizedMessage())
                                .color(NamedTextColor.RED)
                );
            }
        });
    }
}
