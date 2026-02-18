package io.github.qingranqae.voidairrace.corelayer.matchsystem;

import io.github.qingranqae.voidairrace.corelayer.mapsystem.GameMap;
import io.github.qingranqae.voidairrace.exception.config.ConfigFieldInvalidException;
import io.github.qingranqae.voidairrace.exception.map.MapNotPlayableException;

/**
 * 一局比赛的配置
 */
public record MatchConfig(
        GameMap gameMap,
        int duration
) {
    /**
     * 验证当前配置是否有效
     *
     * @throws ConfigFieldInvalidException 当配置无效时抛出
     * @throws MapNotPlayableException 选择的地图不可游玩时抛出
     */
    public void validate() throws ConfigFieldInvalidException, MapNotPlayableException {
        if (gameMap == null) {
            throw new ConfigFieldInvalidException("selectedMapId", "值为 null");
        }
        if (!gameMap.isPlayable()) {
            throw new MapNotPlayableException("选择的地图 '" + gameMap.getId() + "' 不可游玩");
        }
        if (duration <= 0) {
            throw new ConfigFieldInvalidException("duration", "值小于等于 0");
        }
    }
}
