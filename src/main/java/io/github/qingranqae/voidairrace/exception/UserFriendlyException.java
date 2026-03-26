package io.github.qingranqae.voidairrace.exception;

import net.kyori.adventure.text.Component;

public interface UserFriendlyException {
    /**
     * 获取显示给用户的异常消息
     * */
    Component getDisplayMessage();
}
