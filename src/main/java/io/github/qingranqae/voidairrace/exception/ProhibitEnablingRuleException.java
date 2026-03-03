package io.github.qingranqae.voidairrace.exception;

import net.kyori.adventure.text.Component;

/**
 * 规则启用时抛出，标识规则在目前无法启用<br>
 * 可能是因为特定类型规则不允许重复启用或其他原因
 * */
public class ProhibitEnablingRuleException extends RuntimeException {
    private final Component displayMessage;

    /**
     * 构造一个禁止启用规则异常。
     *
     * @param message 异常信息描述
     */
    public ProhibitEnablingRuleException(String message) {
        super(message);
        this.displayMessage = null;
    }

    /**
     * 构造一个禁止启用规则异常。
     *
     * @param message 异常信息描述
     * @param displayMessage 显示给玩家的异常消息
     */
    public ProhibitEnablingRuleException(String message, Component displayMessage) {
        super(message);
        this.displayMessage = displayMessage;
    }

    /**
     * 获取显示给玩家的消息
     * */
    public Component getDisplayMessage() {
        return displayMessage;
    }
}