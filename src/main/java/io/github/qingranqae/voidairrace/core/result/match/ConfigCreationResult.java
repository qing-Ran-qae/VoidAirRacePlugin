package io.github.qingranqae.voidairrace.core.result.match;

import io.github.qingranqae.voidairrace.core.matchsystem.MatchConfig;
import io.github.qingranqae.voidairrace.core.result.base.ValueResult;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public final class ConfigCreationResult extends ValueResult<MatchConfig> {
    private ConfigCreationResult(boolean success, @Nullable MatchConfig value, @Nullable Component displayMessage) {
        super(success, value, displayMessage);
    }

    public static ConfigCreationResult success(MatchConfig config) {
        return new ConfigCreationResult(true, config, null);
    }

    public static ConfigCreationResult failure(Component displayMessage) {
        return new ConfigCreationResult(false, null, displayMessage);
    }
}