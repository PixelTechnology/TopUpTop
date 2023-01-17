package com.yuankong.topuptop.init;

import com.yuankong.easylib.util.timer.TimerUtil;
import com.yuankong.topuptop.TopUpTop;
import com.yuankong.topuptop.config.LoadConfig;
import com.yuankong.topuptop.data.DataBase;
import org.bukkit.Bukkit;

public class Timer implements TimerUtil {

    public static boolean isRewardTime = true;
    public static boolean isTime = true;
    @Override
    public void times(long times) {

        if(times < LoadConfig.getDateTime().getTime()){
            isTime = true;
            return;
        }

        isTime = false;

        if(times > LoadConfig.getDateTime().getTime()+5000){
            return;
        }
        if(!isRewardTime){
            return;
        }

        if(!LoadConfig.isIsReward()){
            return;
        }

        //发放奖励
        DataBase.queryRanks(LoadConfig.getRewardList().size(), list -> Bukkit.getScheduler().runTask(TopUpTop.instance,()->{

            for(int i = 0;i<LoadConfig.getRewardList().size();i++){
                if(i>=list.size()){
                    return;
                }
                if(Integer.parseInt(list.get(i)[2]) < LoadConfig.getRewardList().get(i).getLeastPoints()){
                    return;
                }
                //OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(UUID.fromString(list.get(i)[0]));
                for(String str:LoadConfig.getRewardList().get(i).getCommand()){
                    str = str.replaceAll("%player%",list.get(i)[1]);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), str);
                }
            }

        }));

        isRewardTime = false;

    }

}
