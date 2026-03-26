package io.github.qingranqae.voidairrace.infrastructure;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import org.jspecify.annotations.NonNull;

/**
 * {@link io.github.qingranqae.voidairrace.VoidAirRaceBootstrap} 将在 bootstrap 阶段调用所有实现类中的 {@link BootstrapModule#onBootstrap(BootstrapContext)} 方法
 * */
public interface BootstrapModule {
    void onBootstrap(@NonNull BootstrapContext manager);
}