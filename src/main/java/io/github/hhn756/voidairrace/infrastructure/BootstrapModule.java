package io.github.hhn756.voidairrace.infrastructure;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import org.jspecify.annotations.NonNull;

/**
 * bootstrap模块（或称“阶段”）
 * */
public interface BootstrapModule {
    /**
     * 插件执行bootstrap时执行一次
     *
     * @param context bootstrap上下文
     * */
    void onBootstrap(@NonNull BootstrapContext context);
}
