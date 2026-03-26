package io.github.qingranqae.voidairrace.core.matchsystem;

import io.github.qingranqae.voidairrace.core.mapsystem.PlayableGameMap;
import io.github.qingranqae.voidairrace.core.result.match.ValidationResult;
import io.github.qingranqae.voidairrace.core.teamsystem.TeamRoster;
import io.github.qingranqae.voidairrace.service.config.files.GameSettingKeys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 一局比赛的不可变配置记录。
 *
 * @param gameMap     比赛使用的地图实例
 * @param duration    比赛持续时间（单位：tick）
 * @param contestants 参赛玩家集合（通常是有队伍的玩家）
 */
public record MatchConfig(
        @NonNull PlayableGameMap gameMap,
        int duration,
        @NonNull Collection<? extends Player> contestants
) {
    /**
     * 验证当前配置是否有效
     */
    public ValidationResult validate() {
        if (!gameMap.isReady()) return ValidationResult.failure(
                Component.translatable("void_air_race.match.matchconfig.validate.map_not_ready")
                        .arguments(gameMap.getDisplayName())
                        .color(NamedTextColor.RED));
        if (duration <= 0) return ValidationResult.failure(
                Component.translatable("void_air_race.match.matchconfig.validate.value_is_less_than_or_equal_to_0")
                        .arguments(Component.translatable(GameSettingKeys.MATCH_DURATION.getPath()))
                        .color(NamedTextColor.RED));
        // 检查是否有 2 个或更多队伍有玩家
        TeamRoster teamRoster = TeamRoster.getInstance();
        Set<Team> teamsWithPlayers = new HashSet<>();
        for (Player player : contestants) {
            Team team = teamRoster.getEntityTeam(player);
            teamsWithPlayers.add(team);
            if (teamsWithPlayers.size() >= 2) {
                break;
            }
        }
        if (teamsWithPlayers.size() < 2) {
            return ValidationResult.failure(
                    Component.translatable("void_air_race.match.matchconfig.validate.too_few_contestants")
            );
        }
        return ValidationResult.success();
    }
}