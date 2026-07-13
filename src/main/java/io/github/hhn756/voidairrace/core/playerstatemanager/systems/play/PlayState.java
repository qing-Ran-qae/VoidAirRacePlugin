package io.github.hhn756.voidairrace.core.playerstatemanager.systems.play;

import io.github.hhn756.voidairrace.core.playerstatemanager.systems.StateSystem;
import org.bukkit.NamespacedKey;

/**
 * 记录 {@code play} 状态体系下所有状态的名称
 * */
public enum PlayState {
    /**
     * 代表玩家目前空闲中，没有参加比赛
     * */
    FREE(new NamespacedKey(StateSystem.PLAY.getValue(), "free")),

    /**
     * 代表玩家正在参加比赛
     * */
    PLAYING(new NamespacedKey(StateSystem.PLAY.getValue(), "playing")),;

    private final NamespacedKey value;

    PlayState(NamespacedKey value) {
        this.value = value;
    }

    /**
     * 获取状态名命名空间值
     * */
    public NamespacedKey getValue() {
        return value;
    }
}
