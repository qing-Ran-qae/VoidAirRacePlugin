package io.github.qingranqae.voidairrace.core.teamsystem;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public enum Teams {
    ONE("one", NamedTextColor.RED),
    TWO("two", NamedTextColor.GREEN),
    THREE("three", NamedTextColor.YELLOW),
    FOUR("four", NamedTextColor.DARK_PURPLE),
    FIVE("five", NamedTextColor.LIGHT_PURPLE),
    SIX("six", NamedTextColor.DARK_GREEN),
    SEVEN("seven", NamedTextColor.AQUA),
    EIGHT("eight", NamedTextColor.DARK_BLUE);

    private final String name;
    private final NamedTextColor color;

    Teams(String name, NamedTextColor color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public NamedTextColor getColor() {
        return color;
    }

    public TranslatableComponent getPrefix() {
        String key = "void_air_race.team." + name + ".prefix";
        return Component.translatable(key).color(TextColor.color(getColor()));
    }

    public TranslatableComponent getDisplayName() {
        String key = "void_air_race.team." + name + ".display_name";
        return Component.translatable(key).color(TextColor.color(getColor()));
    }
}
