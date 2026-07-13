package io.github.hhn756.voidairrace.custom;

import io.github.hhn756.voidairrace.constants.TranslateKeys;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * 记录一个游戏元素的元数据
 *
 * @param id 游戏元素的唯一标识，不可重复，参数内部计算
 * @param names 游戏元素名称，不参与内部计算。第一个元素为主要（常用）名称，后续所有均为别名
 * @param description 显示给玩家看的描述，每个元素对应一行
 * @param displayVersion 显示给玩家看的版本号，不参与内部计算，例如“1.23.4”、“25-07-9”、“第二版改”
 * @param version 代表用户内容版本的新旧度，用于内部计算，值越大越新
 * @param links 用户内容相关的链接（常为网页）
 * */
public record GameElementMeta(
        @NonNull NamespacedKey id,
        @Nullable List<@NonNull Component> names,
        @Nullable List<@NonNull Component> description,
        @Nullable List<@NonNull Component> authors,
        @Nullable Component displayVersion,
        @Nullable @Range(from = 1, to = Long.MAX_VALUE) Long version,
        @Nullable List<@NonNull Component> links
) {
    /**
     * @return 元素的第一个显示名称（主要名称），不存在时返回包含默认元素名的文本组件
     * */
    public @NonNull Component mainName() {
        if (names == null) return Component.translatable(
                TranslateKeys.Custom.GameElementMeta.DEFAULT_ELEMENT_NAME
        );
        return names.getFirst();
    }

    /**
     * 检查两个游戏元素的{@link GameElementMeta#id}是否相等
     * */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameElementMeta GameElementMeta = (GameElementMeta) o;
        return Objects.equals(id, GameElementMeta.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
