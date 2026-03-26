package io.github.qingranqae.voidairrace.core.result.arena;

import io.github.qingranqae.voidairrace.core.arenasystem.ArenaToken;
import io.github.qingranqae.voidairrace.core.result.base.ValueResult;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.Nullable;

public class BorrowArenaResult extends ValueResult<ArenaToken> {
    private BorrowArenaResult(boolean success, @Nullable ArenaToken value, @Nullable Component displayMessage) {
        super(success, value, displayMessage);
    }

    public static BorrowArenaResult success(ArenaToken token) {
        return new BorrowArenaResult(true, token, null);
    }

    public static BorrowArenaResult failure(Component displayMessage) {
        return new BorrowArenaResult(false, null, displayMessage);
    }
}
