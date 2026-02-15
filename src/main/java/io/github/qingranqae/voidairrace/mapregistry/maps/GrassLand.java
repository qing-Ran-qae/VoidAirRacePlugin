package io.github.qingranqae.voidairrace.mapregistry.maps;

import io.github.qingranqae.voidairrace.mapregistry.GameMap;
import io.github.qingranqae.voidairrace.match.Match;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

public class GrassLand implements GameMap {
    public GrassLand() {
        this.test = 1;
    }

    @Override
    public String getId() {
        return "GrassLand";
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("void_air_race.mapregistry.maps.grassland.name");
    }

    @Override
    public Component getDescription() {
        return Component.translatable("void_air_race.mapregistry.maps.grassland.description.line1");
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    public void selectedStart(Match match) {
        Bukkit.getServer().broadcast(Component.text("已使用 草原 开始比赛"));
    }

    @Override
    public void selectedOver(Match match) {
        Bukkit.getServer().broadcast(Component.text("已使用 草原 结束比赛"));
    }

    private int test;

    public int test() {
        test++;
        return test;
    }
}
