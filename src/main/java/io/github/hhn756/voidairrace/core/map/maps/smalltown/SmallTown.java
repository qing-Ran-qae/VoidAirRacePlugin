package io.github.hhn756.voidairrace.core.map.maps.smalltown;

import io.github.hhn756.voidairrace.constants.Namespace;
import io.github.hhn756.voidairrace.constants.TranslateKeys;
import io.github.hhn756.voidairrace.core.custom.GameElementMeta;
import io.github.hhn756.voidairrace.core.map.PlayableGameMap;
import io.github.hhn756.voidairrace.core.match.Match;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class SmallTown extends PlayableGameMap {
    private static final NamespacedKey MAP_ID = Namespace.of("small_town");
    private static final GameElementMeta meta = new GameElementMeta(
            MAP_ID,
            List.of(
                    Component.translatable(TranslateKeys.Map.SmallTown.NAME)
            ),
            List.of(
                    Component.translatable(TranslateKeys.Map.SmallTown.DESCRIPTION_LINE1)
            ),
            List.of(
                    Component.translatable(TranslateKeys.Map.SmallTown.AUTHOR1)
            ),
            Component.translatable(TranslateKeys.Map.SmallTown.DISPLAY_VERSION),
            1L,
            null
    );

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public PlayableGameMap.@NonNull StartResult start(@NonNull Match match) {
        Bukkit.getServer().broadcast(Component.text("[debug] 已使用 小镇 开始比赛"));
        return StartResult.success();
    }

    @Override
    public @NonNull OverResult over(@NonNull Match match) {
        Bukkit.getServer().broadcast(Component.text("[debug] 已使用 小镇 结束比赛"));
        return OverResult.success();
    }

    @Override
    public @Range(from = 1, to = Integer.MAX_VALUE) int maxTeams() {
        return 0;
    }

    @Override
    public @NonNull GameElementMeta getElementMeta() {
        return meta;
    }
}
