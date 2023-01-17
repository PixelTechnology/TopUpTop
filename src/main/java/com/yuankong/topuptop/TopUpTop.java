package com.yuankong.topuptop;

import com.yuankong.easylib.api.EasyLibApi;
import com.yuankong.topuptop.config.LoadConfig;
import com.yuankong.topuptop.init.HDManager;
import com.yuankong.topuptop.init.Init;
import com.yuankong.topuptop.init.PAPI;
import com.yuankong.topuptop.init.Timer;
import com.yuankong.topuptop.listener.EventHandler;
import com.yuankong.topuptop.listener.EventHandler2;
import com.yuankong.topuptop.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class TopUpTop extends JavaPlugin {
    public static JavaPlugin instance;
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        LoadConfig.load();
        getLogger().info("========TopUpTop已开启========");
        Bukkit.getPluginManager().registerEvents(new EventHandler(),this);
        Bukkit.getPluginManager().registerEvents(new EventHandler2(),this);
        EasyLibApi.registerTimer(this,new Timer());
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PAPI().register();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!"tut".equalsIgnoreCase(command.getName())) {
            return false;
        }

        if (args.length == 1 && "look".equalsIgnoreCase(args[0])) {
            if(!(sender instanceof Player)){
                sender.sendMessage("该指令无法在控制台运行");
                return true;
            }
            Init.info(sender,sender.getName());
            return true;
        }

        if (!sender.isOp()) {
            return true;
        }

        command.setUsage("§a/tut reload -重载 \n§a/tut take <player> <num> -扣除玩家充值记录 \n§a/tut add <player> <num> -给予玩家充值记录 \n§a/tut top -查看当前排行榜 \n§a/tut info <player> -查看玩家信息 \n§a/tut look -查看自己的信息");

        if (args.length == 1 && "reload".equalsIgnoreCase(args[0])) {

            int showCount = LoadConfig.getShowCount();
            int updateRate = LoadConfig.getUpdateRate();
            boolean useUpdateRate = LoadConfig.isUseUpdateRate();

            LoadConfig.reload();
            Timer.isRewardTime = true;
            if(Util.task != null && !Util.task.isCancelled()){
                Util.task.cancel();
            }
            Util.updateRanks();
            Init.lastTime = 0;

            if(showCount != LoadConfig.getShowCount() || updateRate != LoadConfig.getUpdateRate() || useUpdateRate != LoadConfig.isUseUpdateRate()){
                if(Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")){
                    HDManager.unload();
                    HDManager.top();
                }

            }

            sender.sendMessage("[TopUpTop]"+ChatColor.GREEN + "配置已重载完成");
            return true;
        }

        if (args.length == 3 && "take".equalsIgnoreCase(args[0])) {
            int num = Util.getNum(args[2]);
            if(num<=0){
                sender.sendMessage("§4参数错误!");
                return true;
            }
            Init.makePointsRecord(sender,args[1],-num);
            return true;
        }

        if (args.length == 3 && "add".equalsIgnoreCase(args[0])) {
            int num = Util.getNum(args[2]);
            if(num<=0){
                sender.sendMessage("§4参数错误!");
                return true;
            }
            Init.makePointsRecord(sender,args[1],num);
            return true;
        }

        if (args.length >= 1 && args.length <= 2 && "top".equalsIgnoreCase(args[0])) {
            if(args.length == 1){
                Init.top(sender,1);
                return true;
            }else{
                int num = Util.getNum(args[1]);
                if(num<1){
                    sender.sendMessage("§4参数错误!");
                    return true;
                }
                Init.top(sender,num);
            }
            return true;
        }

        if (args.length == 2 && "info".equalsIgnoreCase(args[0])) {
            Init.info(sender,args[1]);
            return true;
        }

        return !sender.isOp();
    }

    @Override
    public void onDisable() {}
}
