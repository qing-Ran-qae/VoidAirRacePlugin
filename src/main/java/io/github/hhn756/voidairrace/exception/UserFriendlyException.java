package io.github.hhn756.voidairrace.exception;

import net.kyori.adventure.text.Component;
import org.jspecify.annotations.Nullable;

public interface UserFriendlyException {
    /**
     * 获取显示给用户的异常消息，不应包含技术性信息
     * */
    @Nullable Component getDisplayMessage();
}