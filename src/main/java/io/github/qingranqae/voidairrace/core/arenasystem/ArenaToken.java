package io.github.qingranqae.voidairrace.core.arenasystem;

import io.github.qingranqae.voidairrace.core.result.arena.GetTokenWorldResult;
import io.github.qingranqae.voidairrace.core.result.arena.LoadArenaResult;
import io.github.qingranqae.voidairrace.core.result.arena.ReturnArenaResult;
import io.github.qingranqae.voidairrace.core.result.arena.UnloadArenaWorldResult;
import org.jspecify.annotations.NonNull;

/**
 * 借用竞技场的凭据<br>
 * 借用者有义务保管返回变量，并还用完时归还
 */
public class ArenaToken {
    private final Integer arenaId;
    private final Integer uid;
    private final ArenaManager arenaManager;

    /**
     * package-private 构造器，仅 ArenaManager 可实例化
     */
    ArenaToken(Integer arenaId, Integer uid) {
        this.arenaId = arenaId;
        this.uid = uid;
        this.arenaManager = ArenaManager.getInstance();
    }

    /**
     * 获取竞技场 ID
     */
    public @NonNull Integer getArenaId() {
        return arenaId;
    }

    /**
     * 获取此借据的 uid
     */
    public @NonNull Integer getUid() {
        return uid;
    }

    /**
     * 凭此借据归还竞技场
     *
     * @see ArenaManager#returnArena(ArenaToken)
     */
    public @NonNull ReturnArenaResult returnArena() {
        return arenaManager.returnArena(this);
    }

    /**
     * 获取借据对应的竞技场世界
     *
     * @see ArenaManager#getTokenWorld(ArenaToken)
     */
    public @NonNull GetTokenWorldResult getWorld() {
        return arenaManager.getTokenWorld(this);
    }

    /**
     * 将指定竞技场数据加载到当前借据对应的竞技场世界中
     *
     * @param arenaName 要加载的竞技场名称（对应 {@code resources/arena/} 下的子目录名）
     *
     * @return 加载结果
     *
     * @see ArenaManager#loadArena(ArenaToken, String)
     */
    public @NonNull LoadArenaResult loadArena(String arenaName) {
        return arenaManager.loadArena(this, arenaName);
    }

    /**
     * 加载当前借据对应的竞技场世界（不加载具体竞技场数据）
     *
     * @return 加载结果
     *
     * @see ArenaManager#loadArenaWorld(ArenaToken)
     */
    public @NonNull LoadArenaResult loadArenaWorld() {
        return arenaManager.loadArenaWorld(this);
    }

    /**
     * 卸载当前借据对应的竞技场世界（不保存内存中的修改）
     *
     * @return 卸载结果
     *
     * @see ArenaManager#unloadArenaWorld(ArenaToken)
     */
    public @NonNull UnloadArenaWorldResult unloadArenaWorld() {
        return arenaManager.unloadArenaWorld(this);
    }
}