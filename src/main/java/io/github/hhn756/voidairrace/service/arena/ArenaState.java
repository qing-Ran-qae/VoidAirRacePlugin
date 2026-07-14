package io.github.hhn756.voidairrace.service.arena;

import org.bukkit.World;
import org.jspecify.annotations.Nullable;

/**
 * 在竞技场管理器内部使用，代表一个竞技场的状态
 * */
class ArenaState {
    /** 仅 {@link ArenaManager} 可实例化 */
    ArenaState() {};

    /** 是否已借出 */
    private boolean borrowed = false;
    /** 对应世界实例，未加载时为 {@code null} */
    private @Nullable World loadedWorld = null;
    /** 对于此竞技场有效的借据的 uid，用于防止使用旧借据 */
    private @Nullable Integer activeTokenUid = null;

    /**
     * @return 竞技场借用状态，{@code true} 表示已被借用
     * */
    boolean isBorrowed() {
        return borrowed;
    }

    void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }

    /**
     * @return 竞技场已加载的世界实例，未加载世界时返回 {@code null}
     * */
    @Nullable World getLoadedWorld() {
        return loadedWorld;
    }

    void setLoadedWorld(@Nullable World loadedWorld) {
        this.loadedWorld = loadedWorld;
    }

    /**
     * @return 当前借用此竞技场的借据 uid，用于防止使用旧借据
     * */
    @Nullable Integer getActiveTokenUid() {
        return activeTokenUid;
    }

    void setActiveTokenUid(@Nullable Integer activeTokenUid) {
        this.activeTokenUid = activeTokenUid;
    }
}
