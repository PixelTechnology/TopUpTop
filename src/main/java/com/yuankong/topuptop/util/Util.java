package com.yuankong.topuptop.util;

import com.yuankong.topuptop.TopUpTop;
import com.yuankong.topuptop.config.LoadConfig;
import com.yuankong.topuptop.data.DataBase;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Util {
    public static BukkitTask task = null;
    public static int getNum(String str){
        try{
            return Integer.parseInt(str);
        }catch (Exception e){
            return -1;
        }
    }

    public static void updateRanks(){
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if(LoadConfig.isUseUpdateRate()){
                    updateRank();
                }

            }
        }.runTaskTimerAsynchronously(TopUpTop.instance,0,LoadConfig.getUpdateRate());

    }

    public static void updateRank(){
        DataBase.queryRanks(LoadConfig.getShowCount(), list -> {
            DataBase.ranksList.clear();
            for(String[] str:list){
                if(Integer.parseInt(str[2]) <= 0){
                    continue;
                }
                //OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(UUID.fromString(str[0]));
                DataBase.ranksList.add(str[1]);
            }
        });
    }
}
