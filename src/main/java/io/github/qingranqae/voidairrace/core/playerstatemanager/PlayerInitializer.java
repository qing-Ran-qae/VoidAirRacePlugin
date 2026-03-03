package io.github.qingranqae.voidairrace.core.playerstatemanager;

import io.github.qingranqae.voidairrace.constants.PlayerPDCKey;
import io.github.qingranqae.voidairrace.event.PlayerInitEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

class PlayerInitializer {
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
        PersistentDataContainer playerPDC = player.getPersistentDataContainer();
        Boolean initState = playerPDC.get(PlayerPDCKey.INITIALIZED.getValue(), PersistentDataType.BOOLEAN);

        // 检查玩家初始化状态
        if (initState == null || !initState) {
            // 在所有状态体系中进入默认状态
            for (StateSystemMeta system : StateRegistry.getInstance().getAllSystems().values()) {
                PlayerStateManager.getInstance().toggleStatus(
                        player,
                        new NamespacedKey(system.id(), system.defaultState())
                );
            }

            // 发布事件
            new PlayerInitEvent(player).callEvent();

            // 标记初始化
            playerPDC.set(PlayerPDCKey.INITIALIZED.getValue(), PersistentDataType.BOOLEAN, true);
        }
    }
}
