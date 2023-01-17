package com.yuankong.topuptop.init;

import com.yuankong.topuptop.config.LoadConfig;
import com.yuankong.topuptop.data.DataBase;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class PAPI extends PlaceholderExpansion {
    @Override
    public String getIdentifier() {
        return "topuptop";
    }

    @Override
    public String getAuthor() {
        return "yuankong";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
    @Override
    public String onRequest(OfflinePlayer player, String params) {

        for(int i = 1; i<= LoadConfig.getShowCount(); i++){

            if(params.equals("top_"+i)){
                if(i<=DataBase.ranksList.size()){
                    return DataBase.ranksList.get(i-1);
                }else{
                    break;
                }

            }
        }
        return LoadConfig.getNullTop();
    }
}
