package io.github.qingranqae.voidairrace.core.matchsystem;

import io.github.qingranqae.voidairrace.core.mapsystem.PlayableGameMap;
import io.github.qingranqae.voidairrace.exception.ConfigFieldInvalidException;
import io.github.qingranqae.voidairrace.exception.MapNotPlayableException;
import io.github.qingranqae.voidairrace.exception.NoContestantsException;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * 一局比赛的不可变配置记录。
 *
 * @param gameMap     比赛使用的地图实例
 * @param duration    比赛持续时间（单位：tick）
 * @param contestants 参赛玩家集合（通常是有队伍的玩家）
 */
public record MatchConfig(
        PlayableGameMap gameMap,
        int duration,
        Collection<? extends Player> contestants
) {
    /**
     * 验证当前配置是否有效，如果无效将会抛出异常
     *
     * @throws ConfigFieldInvalidException 当时长无效或地图为 null 时抛出
     * @throws NoContestantsException      参赛者列表为空时抛出
     */
    public void validate() throws ConfigFieldInvalidException, MapNotPlayableException {
        if (gameMap == null) {
            throw new ConfigFieldInvalidException("selectedMapId", "值为 null");
        }
        if (duration <= 0) {
            throw new ConfigFieldInvalidException("duration", "值小于等于 0");
        }
        if (contestants.isEmpty()) {
            throw new NoContestantsException("比赛配置中的参赛者列表为空");
        }
    }
}