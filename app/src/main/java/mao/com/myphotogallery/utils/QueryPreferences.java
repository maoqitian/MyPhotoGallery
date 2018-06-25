package mao.com.myphotogallery.utils;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * 搜索数据保存在sharePreferences
 * created by maoqitian
 */

public class QueryPreferences {
    private static final String PREF_SEARCH_QUERY = "searchQuery";

    public static void setStoredQuery(Context context,String query){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_SEARCH_QUERY,query)
                .apply();
    }

    public static String getStoredQuery(Context context){
        return  PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_SEARCH_QUERY,null);
    }
}
