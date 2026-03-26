package io.github.qingranqae.voidairrace.core.result.map;

import io.github.qingranqae.voidairrace.core.result.base.OperationResult;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public class MapSelectedStartResult extends OperationResult {
    public MapSelectedStartResult(boolean success, @Nullable Component displayMessage) {
        super(success, displayMessage);
    }

    public static MapSelectedStartResult success() {
        return new MapSelectedStartResult(true, null);
    }

    public static MapSelectedStartResult failure(Component displayMessage) {
        return new MapSelectedStartResult(false, displayMessage);
    }
}