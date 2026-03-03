package io.github.qingranqae.voidairrace.core.playerstatemanager;

import io.github.qingranqae.voidairrace.constants.PlayerPDCKey;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class PlayerStateManager {
    private static final PlayerStateManager instance = new PlayerStateManager();

    public static PlayerStateManager getInstance() {
        return instance;
    }

    // ------

    private final NamespacedKey PDCStates = PlayerPDCKey.STATES.getValue();

    private PlayerStateManager() {}

    /**
     * 将指定玩家在指定状态体系切换到指定状态
     *
     * @param player 指定玩家
     * @param newState 新状态，自动识别状态所属体系
     * */
    public void toggleStatus(Player player, NamespacedKey newState) {
        StateRegistry stateRegistry = StateRegistry.getInstance();

        // 调用旧状态切出方法
        try {
            stateRegistry.getStateInstance(getState(player, newState.getNamespace())).onCutout(player);
        } catch (NullPointerException ignored) {
            // 在初始化状态时没有旧状态，直接跳过此异常即可，不会出问题
        }

        // 修改状态
        player.getPersistentDataContainer().set(stateToPDCKey(newState), PersistentDataType.STRING, newState.getKey());

        // 调用新状态切入方法
        stateRegistry.getStateInstance(newState).onCutin(player);
    }

    /**
     * 获取玩家在指定状态体系的状态
     *
     * @param player 指定玩家
     * @param system 体系名
     * */
    public NamespacedKey getState(Player player, String system) {
        return new NamespacedKey(
                system,
                player.getPersistentDataContainer().get( // 这里不会是 null，因为玩家初始化时一定会设置此值
                        stateSystemToPDCKey(system),
                        PersistentDataType.STRING
                )
        );
    }

    private NamespacedKey stateToPDCKey(NamespacedKey state) {
        return stateSystemToPDCKey(state.namespace());
    }

    private NamespacedKey stateSystemToPDCKey(String system) {
        return new NamespacedKey(
                PDCStates.namespace(),
                PDCStates.getKey() + "." + system
        );
    }
}