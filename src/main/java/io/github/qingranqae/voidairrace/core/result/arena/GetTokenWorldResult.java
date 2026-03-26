package io.github.qingranqae.voidairrace.core.result.arena;

import io.github.qingranqae.voidairrace.core.result.base.ValueResult;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.jspecify.annotations.Nullable;

public class GetTokenWorldResult extends ValueResult<World> {
    private GetTokenWorldResult(boolean success, @Nullable World value, @Nullable Component displayMessage) {
        super(success, value, displayMessage);
    }

    public static GetTokenWorldResult success(World world) {
        return new GetTokenWorldResult(true, world, null);
    }

    public static GetTokenWorldResult failure(Component displayMessage) {
        return new GetTokenWorldResult(false, null, displayMessage);
    }
}
