package io.github.qingranqae.voidairrace.core.playerstatemanager;

import io.github.qingranqae.voidairrace.constants.PlayerPDCKey;
import io.github.qingranqae.voidairrace.event.PlayerInitEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class PlayerInitializer {
    private static  PlayerInitializer instance;

    public static PlayerInitializer getInstance() {
        if (instance == null) instance = new PlayerInitializer();
        return instance;
    }

    // ------

    private PlayerInitializer() {}

    /**
     * 初始化指定玩家，如果它在此之前已经初始化过了那么不会执行任何操作
     *
     * @param player 指定玩家
     */
    public void initializePlayer(Player player) {
        // 检查玩家初始化状态
        if (isInitialized(player)) return;

        // 在所有状态体系中进入默认状态
        PlayerStateManager playerStateManager = PlayerStateManager.getInstance();
        for (StateSystemMeta system : StateRegistry.getInstance().getAllSystems().values()) {
            playerStateManager.toggle(
                    player,
                    new NamespacedKey(system.getId(), system.getDefaultState())
            );
        }

        // 发布事件
        new PlayerInitEvent(player).callEvent();

        // 标记初始化
        setInitState(player, true);
    }

    /**
     * 重新初始化指定玩家
     * */
    public void reInitPlayer(Player player) {
        setInitState(player, false);
        initializePlayer(player);
    }

    public boolean isInitialized(Player player) {
        Boolean initState = player.getPersistentDataContainer().get(
                PlayerPDCKey.INITIALIZED.getValue(),
                PersistentDataType.BOOLEAN
        );
        return initState != null && initState;
    }

    private void setInitState(Player player, Boolean newValue) {
        player.getPersistentDataContainer().set(
                PlayerPDCKey.INITIALIZED.getValue(),
                PersistentDataType.BOOLEAN,
                newValue
        );
    }
}
