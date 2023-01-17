package com.yuankong.topuptop.data;

import cc.carm.lib.easysql.api.SQLManager;
import com.yuankong.easylib.util.datebase.CallBack;
import com.yuankong.easylib.util.datebase.ToDoList;
import com.yuankong.easylib.util.datebase.Todo;
import com.yuankong.topuptop.TopUpTop;
import com.yuankong.topuptop.config.LoadConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DataBase {
    public static SQLManager sqlManager;
    public static HashMap<Player,PlayerData> playerDataHash = new HashMap<>();
    public static List<String> ranksList = new ArrayList<>();
    public static final String uuid = "uuid";
    public static final String player_name = "player_name";
    public static final String points = "points";

    public static void createTable(String tableName){
        if(sqlManager == null){
            TopUpTop.instance.getLogger().warning("createTable - 未连接数据库！");
            return;
        }
        sqlManager.createTable(tableName)
                .addColumn(uuid, "VARCHAR(40) NOT NULL UNIQUE KEY")
                .addColumn(player_name, "VARCHAR(20) NOT NULL")
                .addColumn(points, "VARCHAR(10) NOT NULL")
                .build().executeAsync((success) -> {
                    //操作成功回调
                    TopUpTop.instance.getLogger().info("表加载成功!");
                }, (exception, sqlAction) -> {
                    //操作失败回调
                    TopUpTop.instance.getLogger().warning("表加载失败，可能数据库连接失败!");
                });
    }

    public static void putCacheData(Player player,boolean status){
        if(sqlManager == null){
            TopUpTop.instance.getLogger().warning("putCacheData - 未连接数据库！");
            return;
        }

        sqlManager.createQuery()
                .inTable(LoadConfig.getTableName())
                .selectColumns(points)
                .addCondition(uuid,player.getUniqueId())
                .build().executeAsync((success) -> {
                    //操作成功回调
                    ConcurrentHashMap<String, String> data = new ConcurrentHashMap<>();
                    ResultSet resultSet = success.getResultSet();

                    while (resultSet.next()){
                        data.put(points,resultSet.getString(points));
                    }
                    Bukkit.getScheduler().runTask(TopUpTop.instance,()->{
                        if(data.containsKey(points)){

                            playerDataHash.put(player,new PlayerData(player,Integer.parseInt(data.get(points))));

                            if(status){
                                TopUpTop.instance.getLogger().info("玩家 " + player.getName() + " 数据加载成功!");
                            }

                        }else{
                            playerDataHash.put(player,new PlayerData(player,0));
                            insertData(player);
                        }

                    });

                }, (exception, sqlAction) -> {
                    //操作失败回调
                    //CustomDesignation.instance.getLogger().info("表加载失败，可能数据库连接失败!");
                    insertData(player);
                });
    }

    public static void insertData(Player player){
        if(sqlManager == null){
            TopUpTop.instance.getLogger().warning("insertData - 未连接数据库！");
            return;
        }
        sqlManager.createInsert(LoadConfig.getTableName())
                .setColumnNames(uuid,player_name,points)
                .setParams(player.getUniqueId(),player.getName(),0)
                .executeAsync((success) -> {
                    //操作成功回调
                    putCacheData(player,true);
                },(exception, sqlAction) -> {
                    //操作失败回调
                    //BadgeEvolution.instance.getLogger().info("数据插入失败!");
                    TopUpTop.instance.getLogger().warning("玩家 " + player.getName() + " 数据加载失败!");
                });
    }

    public static void updateData(UUID player, String column, String data){
        updateData(player, column, data, new CallBack() {
            @Override
            public void onSuccess() {}
            @Override
            public void onFail() {}
        });
    }

    public static void updateData(UUID player, String column, String data, CallBack callBack){
        if(sqlManager == null){
            TopUpTop.instance.getLogger().warning("updateData - 未连接数据库！");
            return;
        }
        sqlManager.createUpdate(LoadConfig.getTableName())
                .addCondition(uuid,player)
                .setColumnValues(column,data)
                .build().executeAsync((success) -> {
                    //操作成功回调
                    callBack.onSuccess();
                },(exception, sqlAction) -> {
                    //操作失败回调
                    TopUpTop.instance.getLogger().warning("数据更新失败");
                    callBack.onFail();
                });
    }

    public static void queryData(UUID uniqueId, String type, Todo todo) {
        if(sqlManager == null){
            TopUpTop.instance.getLogger().warning("queryData - 未连接数据库！");
            return;
        }
        sqlManager.createQuery()
                .inTable(LoadConfig.getTableName())
                .selectColumns(type)
                .addCondition(uuid, uniqueId)
                .build().executeAsync((success) -> {
                    ResultSet resultSet = success.getResultSet();
                    String data = null;
                    while (resultSet.next()){
                        data = resultSet.getString(type);
                    }
                    todo.callBack(data);
                }, (exception, sqlAction) -> {
                    //操作失败回调
                    TopUpTop.instance.getLogger().warning("操作失败,数据获取失败");
                });
    }

    public static void queryRanks(int count, ToDoList<String[]> toDo2) {
        if (sqlManager == null) {
            TopUpTop.instance.getLogger().warning("queryRanks - 未连接数据库！");
            return;
        }

        sqlManager.createQuery().withPreparedSQL("SELECT uuid,player_name,points FROM " + LoadConfig.getTableName() + " ORDER BY points+0 DESC LIMIT " + count).executeAsync((success) -> {
            //操作成功回调
            ResultSet resultSet =  success.getResultSet();
            List<String[]> list = new ArrayList<>();
            while (resultSet.next()){
                list.add(new String[]{resultSet.getString(uuid),resultSet.getString(player_name),resultSet.getString(points)});
            }

            toDo2.callBack(list);

        },(exception, sqlAction) -> {

            //操作失败回调
            TopUpTop.instance.getLogger().warning("数据查询失败!");
        });
    }

}
