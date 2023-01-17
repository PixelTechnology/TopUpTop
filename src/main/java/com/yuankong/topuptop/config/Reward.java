package com.yuankong.topuptop.config;

import java.util.List;

public class Reward {
    public final String key;
    public final int leastPoints;
    public final List<String> command;

    public Reward(String key, int leastPoints, List<String> command) {
        this.key = key;
        this.leastPoints = leastPoints;
        this.command = command;
    }

    public String getKey() {
        return key;
    }

    public int getLeastPoints() {
        return leastPoints;
    }

    public List<String> getCommand() {
        return command;
    }
}
