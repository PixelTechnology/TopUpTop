package com.yuankong.topuptop.data;

import org.bukkit.entity.Player;

public class PlayerData {
    public Player player;
    public int points;

    public PlayerData(Player player, int points) {
        this.player = player;
        this.points = points;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
        DataBase.updateData(player.getUniqueId(),DataBase.points, String.valueOf(this.points));
    }

    public void addPoints(int points){
        this.points = this.points+points;
        DataBase.updateData(player.getUniqueId(),DataBase.points, String.valueOf(this.points));
    }
}
