package io.github.hhn756.voidairrace.core.team;

import io.github.hhn756.voidairrace.constants.TranslateKeys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

/**
 * 定义所有队伍
 * */
public enum Teams {
    ONE("one", NamedTextColor.RED, TranslateKeys.Team.ONE_PREFIX, TranslateKeys.Team.ONE_DISPLAY_NAME),
    TWO("two", NamedTextColor.GREEN, TranslateKeys.Team.TWO_PREFIX, TranslateKeys.Team.TWO_DISPLAY_NAME),
    THREE("three", NamedTextColor.YELLOW, TranslateKeys.Team.THREE_PREFIX, TranslateKeys.Team.THREE_DISPLAY_NAME),
    FOUR("four", NamedTextColor.DARK_PURPLE, TranslateKeys.Team.FOUR_PREFIX, TranslateKeys.Team.FOUR_DISPLAY_NAME),
    FIVE("five", NamedTextColor.LIGHT_PURPLE, TranslateKeys.Team.FIVE_PREFIX, TranslateKeys.Team.FIVE_DISPLAY_NAME),
    SIX("six", NamedTextColor.DARK_GREEN, TranslateKeys.Team.SIX_PREFIX, TranslateKeys.Team.SIX_DISPLAY_NAME),
    SEVEN("seven", NamedTextColor.AQUA, TranslateKeys.Team.SEVEN_PREFIX, TranslateKeys.Team.SEVEN_DISPLAY_NAME),
    EIGHT("eight", NamedTextColor.DARK_BLUE, TranslateKeys.Team.EIGHT_PREFIX, TranslateKeys.Team.EIGHT_DISPLAY_NAME);

    private final String id;
    private final NamedTextColor color;
    private final TranslatableComponent prefix;
    private final TranslatableComponent displayName;

    Teams(String id, NamedTextColor color, String prefixKey, String displayNameKey) {
        this.id = id;
        this.color = color;
        prefix = Component.translatable(prefixKey).color(TextColor.color(color()));
        displayName = Component.translatable(displayNameKey).color(TextColor.color(color()));
    }

    /**
     * @return 队伍id（名称）
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
