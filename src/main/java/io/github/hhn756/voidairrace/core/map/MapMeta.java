package io.github.hhn756.voidairrace.core.map;

import io.github.hhn756.voidairrace.core.custom.GameElement;
import io.github.hhn756.voidairrace.core.custom.GameElementMeta;
import io.github.hhn756.voidairrace.exception.RegistryException;
import io.github.hhn756.voidairrace.infrastructure.registry.Entry;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Constructor;

public class MapMeta implements Entry<NamespacedKey>, GameElement {
    /** 地图的元数据 */
    private final GameElementMeta elementMeta;
    /** 地图类型对象的构造器 */
    private final Constructor<? extends GameMap> mapConstructor;
    /** 地图是否可玩 */
    private final boolean playable;
    /** 最大参赛队伍数量 */
    private final Integer maxTeams;

    /**
     * 构造一个游戏地图注册项
     *
     * @param elementMeta 此地图的元数据
     * @param mapConstructor 此地图的<b>无参</b>构造器，{@code playable}为{@code true}时应返回{@link PlayableGameMap}；{@code playable}为{@code false}时应返回{@link GameMap}
     * @param playable 地图是否可游玩
     *
     * @throws RegistryException 如果{@code playable}的值和{@code mapConstructor}所构造的对象类型不匹配
     * */
    public MapMeta(
            @NonNull GameElementMeta elementMeta,
            @NonNull Constructor<? extends GameMap> mapConstructor,
            boolean playable,
            @Nullable @Range(from = 1, to = Integer.MAX_VALUE) Integer maxTeams
    ) throws RegistryException {
        if (playable) {
            if (!PlayableGameMap.class.isAssignableFrom(mapConstructor.getDeclaringClass())) {
                throw new RegistryException("地图 “" + elementMeta.id() + "” 被声明为可游玩的，构造器返回的对象类型也应是“可游玩地图”，实际却不是此类型", null);
            }
            if (maxTeams == null) {
                throw new RegistryException("地图 “" + elementMeta.id() + "” 被声明为可游玩的，应传入最大队伍数量但却未传入", null);
            }
        }
        this.elementMeta = elementMeta;
        this.mapConstructor = mapConstructor;
        this.playable = playable;
        this.maxTeams = maxTeams;
    }

    @Override
    public @NonNull NamespacedKey getKey() {
        return elementMeta.id();
    }

    @Override
    public @NonNull GameElementMeta getElementMeta() {
        return elementMeta;
    }

    /**
     * 获取此地图的新实例
     *
     * @return 此地图的新实例，{@link MapMeta#isPlayable()}为{@code true}时将返回{@link PlayableGameMap}，否则返回{@link GameMap}
     *
     * @throws RegistryException 如果构造器抛出了异常
     * */
    public @NonNull GameMap newInstance() throws RegistryException {
        try {
            return mapConstructor.newInstance();
        } catch (Exception e) {
            // 仅转义异常
            throw new RegistryException("实例化游戏地图失败：" + e.getLocalizedMessage(), null);
        }
    }

    /**
     * 在不实例化地图的情况下检查地图是否可玩
     *
     * @return 如果地图可游玩将返回{@code true}，否则返回{@code false}
     * */
    public boolean isPlayable() {
        return playable;
    }

    /**
     * 获取地图允许参赛的最大队伍数量
     *
     * @return 最大队伍数量
     * */
    @Range(from = 1, to = Integer.MAX_VALUE)
    public @Nullable Integer maxTeams() {
        return maxTeams;
    }
}
