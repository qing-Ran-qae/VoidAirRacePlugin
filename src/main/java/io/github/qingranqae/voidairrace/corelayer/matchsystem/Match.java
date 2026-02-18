package io.github.qingranqae.voidairrace.corelayer.matchsystem;

/**
 * 代表一局比赛
 * */
public class Match {
    private final MatchConfig config;
    private int remainingTime;

    public Match(MatchConfig config) {
        this.config = config;
        this.remainingTime = config.duration();
    }

    public MatchConfig getConfig() {
        return config;
    }

    public int getRemainingTime() {
        return remainingTime;
    }
}
