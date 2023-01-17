package com.yuankong.topuptop.config;

import com.yuankong.topuptop.TopUpTop;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoadConfig {
    public static String nullTop;
    public static String look;
    public static String date;
    public static int showCount;
    public static int topCount;
    public static int commandUpdate;
    public static boolean useUpdateRate;
    public static int updateRate;
    public static boolean isReward;
    public static String tableName;
    public static Date dateTime;

    public static List<Reward> rewardList = new ArrayList<>();

    public static void load() {
        Configuration config = TopUpTop.instance.getConfig();
        nullTop = config.getString("nullTop");
        look = config.getString("look");
        date = config.getString("date");
        showCount = config.getInt("showCount",10);
        topCount = config.getInt("topCount",10);
        commandUpdate = config.getInt("commandUpdate",60);
        useUpdateRate = config.getBoolean("useUpdateRate");
        updateRate = config.getInt("updateRate",60);
        isReward = config.getBoolean("isReward");

        tableName = config.getString("tableName","top_up_top");

        rewardList.clear();
        ConfigurationSection cs = config.getConfigurationSection("reward");
        for (String key : cs.getKeys(false)) {
            ConfigurationSection csx = cs.getConfigurationSection(key);
            rewardList.add(new Reward(key,csx.getInt("leastPoints"),csx.getStringList("command")));
        }

        SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd-HH:mm:ss");
        try {
            dateTime = format.parse(date);
        } catch (ParseException e) {
            TopUpTop.instance.getLogger().warning("配置文件date异常!");
            throw new RuntimeException(e);
        }
    }

    public static void reload() {
        TopUpTop.instance.reloadConfig();
        load();
    }

    public static String getNullTop() {
        return nullTop;
    }

    public static String getLook() {
        return look;
    }

    public static String getDate() {
        return date;
    }

    public static int getShowCount() {
        return showCount;
    }

    public static int getTopCount() {
        return topCount;
    }

    public static int getCommandUpdate() {
        return commandUpdate;
    }

    public static boolean isUseUpdateRate() {
        return useUpdateRate;
    }

    public static int getUpdateRate() {
        return updateRate;
    }

    public static List<Reward> getRewardList() {
        return rewardList;
    }

    public static boolean isIsReward() {
        return isReward;
    }

    public static String getTableName() {
        return tableName;
    }

    public static Date getDateTime() {
        return dateTime;
    }
}