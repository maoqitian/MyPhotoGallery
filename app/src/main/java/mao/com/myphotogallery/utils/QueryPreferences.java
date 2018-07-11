package mao.com.myphotogallery.utils;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * 搜索数据保存在sharePreferences
 * created by maoqitian
 */

public class QueryPreferences {
    private static final String PREF_SEARCH_QUERY = "searchQuery";
    private static final String PREF_LAST_RESULT_ID = "lastResultId";//最新返回结果
    private static final String PREF_IS_ALARM_ON = "isAlarmOn";//是否开启定时器

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

    //获取最近的搜索Id
    public static String getLastResultId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(PREF_LAST_RESULT_ID, null);
    }

    //存放最近的搜索Id
    public static void setLastResultId(Context context,String lastResultId){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_LAST_RESULT_ID,lastResultId)
                .apply();
    }

    //获取定时器的状态
    public static boolean isAlarmOn(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_IS_ALARM_ON, false);
    }

    //存放定时器的状态
    public static void setAlarmOn(Context context,boolean isOn){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_IS_ALARM_ON,isOn)
                .apply();
    }

}
