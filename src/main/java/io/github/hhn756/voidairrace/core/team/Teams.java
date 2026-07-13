package io.github.hhn756.voidairrace.core.team;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

/**
 * 定义所有队伍
 * */
public enum Teams {
    ONE("one", NamedTextColor.RED),
    TWO("two", NamedTextColor.GREEN),
    THREE("three", NamedTextColor.YELLOW),
    FOUR("four", NamedTextColor.DARK_PURPLE),
    FIVE("five", NamedTextColor.LIGHT_PURPLE),
    SIX("six", NamedTextColor.DARK_GREEN),
    SEVEN("seven", NamedTextColor.AQUA),
    EIGHT("eight", NamedTextColor.DARK_BLUE);

    private final String id;
    private final NamedTextColor color;
    private final TranslatableComponent prefix;
    private final TranslatableComponent displayName;

    Teams(String id, NamedTextColor color) {
        this.id = id;
        this.color = color;
        prefix = Component.translatable(
                "void_air_race.team." + id + ".prefix"
        ).color(TextColor.color(color()));
        displayName = Component.translatable(
                "void_air_race.team." + id + ".display_name"
        ).color(TextColor.color(color()));
    }

    /**
     * @return  队伍id（名称）
     * */
    public String id() {
        return id;
    }

    /**
     * @return 队伍颜色
     * */
    public NamedTextColor color() {
        return color;
    }

    /**
     * @return 队伍前缀文本
     * */
    public TranslatableComponent prefix() {
        return prefix;
    }

    /**
     * @return 队伍的显示名称
     * */
    public TranslatableComponent displayName() {
        return displayName;
    }
}
