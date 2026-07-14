package io.github.hhn756.voidairrace;

import io.github.hhn756.voidairrace.infrastructure.BootstrapModule;
import io.github.hhn756.voidairrace.infrastructure.util.ClassScanner;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jspecify.annotations.NonNull;

import java.net.URL;
import java.util.Collection;

public class Bootstrap implements PluginBootstrap {
    /**
     * 引导各模块
     * */
    @Override
    public void bootstrap(@NonNull BootstrapContext context) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL codeSourceUrl = getClass().getProtectionDomain().getCodeSource().getLocation();
        ComponentLogger logger = context.getLogger();

        // 扫描所有实现了 BootstrapModule 的类
        Collection<Class<BootstrapModule>> modules = ClassScanner.scanSubclasses(
                classLoader, codeSourceUrl, BootstrapModule.class, "io.github.hhn756.voidairrace"
        );
        logger.debug("共扫描到 {} 个 BootstrapModule 实现：", modules.size());
        for (Class<BootstrapModule> module : modules) {
            logger.debug(module.getName());
        }

        // 实例化并调用
        for (Class<BootstrapModule> moduleClass : modules) {
            try {
                BootstrapModule module = moduleClass.getDeclaredConstructor().newInstance();
                module.onBootstrap(context);
                logger.debug("已引导模块: {}", moduleClass.getSimpleName());
            } catch (Exception e) {
                logger.error("无法实例化引导模块: {}", moduleClass.getName(), e);
            }
        }
    }
}
