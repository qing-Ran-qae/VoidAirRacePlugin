package io.github.hhn756.voidairrace.core.custom;

import org.jspecify.annotations.NonNull;

/**
 * 代表一个游戏元素
 * */
public interface GameElement {
    /**
     * 获取此游戏元素的元数据
     * */
    @NonNull GameElementMeta getElementMeta();
}
