package mao.com.myphotogallery.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import mao.com.myphotogallery.service.PhotoGalleryService;
import mao.com.myphotogallery.utils.QueryPreferences;

/**
 * 监听开机的广播
 */
public class StartupReceiver extends BroadcastReceiver {
    private static final String TAG = "StartupReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received broadcast intent: " + intent.getAction());
        //重启设备，获取获取定时器的状态来决定是否获取最新的数据
        boolean isAlarmOn = QueryPreferences.isAlarmOn(context);
        PhotoGalleryService.setServiceAlarm(context,isAlarmOn);
    }
}
