package com.yuankong.topuptop.listener;

import com.yuankong.easylib.util.datebase.CallBack;
import com.yuankong.topuptop.TopUpTop;
import com.yuankong.topuptop.config.LoadConfig;
import com.yuankong.topuptop.data.DataBase;
import com.yuankong.topuptop.init.Timer;
import com.yuankong.topuptop.util.Util;
import org.black_ixx.playerpoints.event.PlayerPointsChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;

public class EventHandler implements Listener {
    @org.bukkit.event.EventHandler
    public void pointsEvent(PlayerPointsChangeEvent event) {
        if(event.getChange()<=0){
            return;
        }

        if (!Timer.isTime){
            return;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(event.getPlayerId());

        if(LoadConfig.isUseUpdateRate()){//是否使用定时刷新
            if(player.isOnline()){
                DataBase.playerDataHash.get(player.getPlayer()).addPoints(event.getChange());
            }else{
                DataBase.queryData(event.getPlayerId(), DataBase.points, s -> {
                    if(s == null){
                        TopUpTop.instance.getLogger().warning("出错了，玩家不存在!");
                        return;
                    }
                    int points = Integer.parseInt(s) + event.getChange();
                    DataBase.updateData(event.getPlayerId(),DataBase.points, String.valueOf(points));
                });
            }
        }else{
            DataBase.queryData(event.getPlayerId(), DataBase.points, s -> {
                if(s == null){
                    TopUpTop.instance.getLogger().warning("出错了，玩家不存在!");
                    return;
                }
                int points = Integer.parseInt(s) + event.getChange();
                DataBase.updateData(event.getPlayerId(), DataBase.points, String.valueOf(points), new CallBack() {
                    @Override
                    public void onSuccess() {
                        if(player.isOnline()){
                            DataBase.putCacheData(player.getPlayer(),false);
                        }
                        Util.updateRank();
                    }

                    @Override
                    public void onFail() {}
                });
            });
        }

    }


}
