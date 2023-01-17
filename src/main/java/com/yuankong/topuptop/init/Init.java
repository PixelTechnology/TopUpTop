package com.yuankong.topuptop.init;

import com.yuankong.easylib.util.datebase.CallBack;
import com.yuankong.topuptop.TopUpTop;
import com.yuankong.topuptop.config.LoadConfig;
import com.yuankong.topuptop.data.DataBase;
import com.yuankong.topuptop.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class Init {

    public static List<String> queryList = new ArrayList<>();
    public static long lastTime = 0;
    public static void makePointsRecord(CommandSender sender,String playerName,int num){
        OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(playerName);
        final int[] x = {num};
        DataBase.queryData(offlinePlayer.getUniqueId(), DataBase.points, data -> Bukkit.getScheduler().runTask(TopUpTop.instance,()->{
            if(data == null){
                sender.sendMessage(ChatColor.RED+"失败了，可能玩家不存在！");
                return;
            }

            x[0] = Integer.parseInt(data)+ x[0];
            if(x[0]<0){
                sender.sendMessage(ChatColor.RED+"数值大于拥有的数值，操作失败！");
            }else{
                DataBase.updateData(offlinePlayer.getUniqueId(), DataBase.points, String.valueOf(x[0]), new CallBack() {
                    @Override
                    public void onSuccess() {
                        sender.sendMessage(ChatColor.GREEN+"修改完成,建议查询信息以核对!");
                        if(offlinePlayer.isOnline()){
                            DataBase.putCacheData(offlinePlayer.getPlayer(),false);
                        }
                        if(!LoadConfig.isUseUpdateRate()){
                            Util.updateRank();
                        }
                    }

                    @Override
                    public void onFail() {}
                });

            }
        }));
    }

    public static void top(CommandSender sender,int page){
        page = page-1;
        long now = System.currentTimeMillis();
        if(now < lastTime + (LoadConfig.getCommandUpdate()* 1000L)){
            top2(sender,page);
        }else{
            int finalPage = page;
            DataBase.queryRanks(LoadConfig.getTopCount(), list -> {
                if(!list.isEmpty() && list.get(0)[0] != null){
                    queryList.clear();
                    for(int i = 0;i<list.size();i++){
                        int x = i+1;
                        queryList.add(x+". "+ list.get(i)[1] + " 充值点券: " + list.get(i)[2]);
                    }
                }
                Bukkit.getScheduler().runTask(TopUpTop.instance,()-> top2(sender, finalPage));

            });
            lastTime = System.currentTimeMillis();
        }

    }

    public static void top2(CommandSender sender,int page){
        StringBuilder str = new StringBuilder();
        str.append("==============================").append("\n");
        for(int i = page*10;i<(page*10)+10;i++){
            if(i>= queryList.size()){
                if(i == page*10){
                    sender.sendMessage(ChatColor.RED+"此页无数据!");
                    return;
                }
                break;
            }
            str.append(queryList.get(i)).append("\n");
            if(i == queryList.size()-1){
                str.append(ChatColor.GREEN).append("这已经是最后一页!").append("\n");
            }
        }
        str.append(ChatColor.WHITE).append("==============================");
        sender.sendMessage(str.toString());
    }

    public static void info(CommandSender sender,String playerName){
        OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(playerName);
        if(offlinePlayer.isOnline()){
            int x = DataBase.playerDataHash.get(offlinePlayer.getPlayer()).getPoints();

            if(sender.getName().equals(playerName)){
                sender.sendMessage(LoadConfig.getLook().replaceAll("%player%",playerName).replaceAll("%points%", String.valueOf(x)));
            }else{
                sender.sendMessage(playerName + " 充值点券: " + x);
            }

            return;
        }
        DataBase.queryData(offlinePlayer.getUniqueId(), DataBase.points, data -> Bukkit.getScheduler().runTask(TopUpTop.instance,()->{
            if(data == null){
                sender.sendMessage(ChatColor.RED+"失败了，可能玩家不存在！");
                return;
            }
            sender.sendMessage(playerName + " 充值点券: " + data);
        }));
    }



}
