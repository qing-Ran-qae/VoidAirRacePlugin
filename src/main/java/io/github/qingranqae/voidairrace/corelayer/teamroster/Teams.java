package io.github.qingranqae.voidairrace.corelayer.teamroster;

import net.kyori.adventure.text.format.NamedTextColor;

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
}
