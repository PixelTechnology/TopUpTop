package com.yuankong.topuptop.init;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;
import com.yuankong.topuptop.TopUpTop;
import com.yuankong.topuptop.config.LoadConfig;
import com.yuankong.topuptop.data.DataBase;

public class HDManager implements PlaceholderReplacer {
    int top;
    public HDManager(int top){
        this.top = top;
    }
    @Override
    public String update() {
        if(DataBase.ranksList.size() >= top){
            return DataBase.ranksList.get(top-1);
        }
        return LoadConfig.getNullTop();
    }

    public static void top(){
        for(int i = 1; i <= LoadConfig.getShowCount(); i++){
            if(LoadConfig.isUseUpdateRate()){
                HologramsAPI.registerPlaceholder(TopUpTop.instance, "{topuptop_top_" + i + "}", LoadConfig.getUpdateRate(), new HDManager(i));
            }else{
                HologramsAPI.registerPlaceholder(TopUpTop.instance, "{topuptop_top_" + i + "}", 1.00, new HDManager(i));
            }

        }
    }

    public static void unload(){
        HologramsAPI.unregisterPlaceholders(TopUpTop.instance);
    }

}
