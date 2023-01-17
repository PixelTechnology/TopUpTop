package com.yuankong.topuptop.listener;

import com.yuankong.easylib.event.ReloadConfigEvent;
import com.yuankong.easylib.event.SQLManagerFinishEvent;
import com.yuankong.topuptop.TopUpTop;
import com.yuankong.topuptop.config.LoadConfig;
import com.yuankong.topuptop.data.DataBase;
import com.yuankong.topuptop.init.HDManager;
import com.yuankong.topuptop.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventHandler2 implements Listener {
    @org.bukkit.event.EventHandler
    public void onEasyLibEvent(SQLManagerFinishEvent event) {
        DataBase.sqlManager = event.getSqlManager();
        DataBase.createTable(LoadConfig.getTableName());
        if(LoadConfig.isUseUpdateRate()){
            Util.updateRanks();
        }else{
            Util.updateRank();
        }

        TopUpTop.instance.getLogger().info("初始化完成!");

        Bukkit.getScheduler().runTask(TopUpTop.instance,()->{
            if(Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")){
                HDManager.top();
            }
        });

    }

    @org.bukkit.event.EventHandler
    public void onEasyLibEvent2(ReloadConfigEvent event) {
        DataBase.sqlManager = event.getSqlManager();
    }

    @org.bukkit.event.EventHandler
    public void onJoinEvent(PlayerJoinEvent event) {
        DataBase.putCacheData(event.getPlayer(),true);
    }

    @org.bukkit.event.EventHandler
    public void onLeaveEvent(PlayerQuitEvent event) {
        DataBase.playerDataHash.remove(event.getPlayer());
    }
}
