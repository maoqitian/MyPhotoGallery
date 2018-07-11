package mao.com.myphotogallery.service;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.List;
import java.util.concurrent.TimeUnit;

import mao.com.myphotogallery.PhotoGalleryActivity;
import mao.com.myphotogallery.R;
import mao.com.myphotogallery.http.FlickrFetchr;
import mao.com.myphotogallery.model.GalleryItem;
import mao.com.myphotogallery.utils.QueryPreferences;

/**
 * 服务进程
 */
public class PhotoGalleryService extends IntentService {

    //自定义私有权限 ，保证广播只接收自己定义的消息
    public static final String PERM_PRIVATE = "com.bignerdranch.android.photogallery.PRIVATE";

    // Set interval to 1 minute 15分钟
    private static final long POLL_INTERVAL_MS = TimeUnit.MINUTES.toMillis(15);

    //广播标志常量
    public static final String ACTION_SHOW_NOTIFICATION =   "com.bignerdranch.android.photogallery.SHOW_NOTIFICATION";


    public static final String REQUEST_CODE = "REQUEST_CODE";
    public static final String NOTIFICATION = "NOTIFICATION";

    //启动服务
    public static Intent newInstance(Context context) {
        return new Intent(context,PhotoGalleryService.class);
    }


    //是否开启定时器服务
    public static boolean isServiceAlarmOn(Context context){
        Intent intent = PhotoGalleryService.newInstance(context);
        PendingIntent pendingIntent=PendingIntent.getService(context,0,intent,PendingIntent.FLAG_NO_CREATE);
        return pendingIntent != null;
    }

    //设定定时器任务
    public static void  setServiceAlarm(Context context,boolean isOn){
        Intent i=new Intent();
        PendingIntent pendingIntent=PendingIntent.getService(context,0,i,0);
        AlarmManager alarmManager= (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(isOn){
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
                    POLL_INTERVAL_MS,pendingIntent);
        }else {
            if(alarmManager != null){
                alarmManager.cancel(pendingIntent);
                pendingIntent.cancel();
            }
        }
        //保存定时器的状态
        QueryPreferences.setAlarmOn(context,isOn);
    }

    public PhotoGalleryService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(!isNetworkAvailableAndConnected()){
            return;
        }

        String storedQuery = QueryPreferences.getStoredQuery(this);
        String lastResultId = QueryPreferences.getLastResultId(this);
        List<GalleryItem> items;
        if(storedQuery==null){
           items=new FlickrFetchr().fetchRecentPhotos(1);
        }else {
           items=new FlickrFetchr().searchPhotos(storedQuery);
        }
        if(items.size() == 0){
            return;
        }

        String resultId = items.get(0).getmId();
        if(resultId.equals(lastResultId)){
            Log.i("毛麒添", "Got an old result: " + resultId);
        }else {
            Log.i("毛麒添", "Got an new result: " + resultId);
            Resources resources = getResources();
            Intent i = PhotoGalleryActivity.newIntent(this);
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, i, 0);
            //NotificationCompat.Builder notification=new NotificationCompat.Builder(this) 另一种方式 
            Notification notification=new Notification.Builder(this)
                    .setTicker(resources.getString(R.string.new_pictures_title))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(resources.getString(R.string.new_pictures_title))
                    .setContentText(resources.getString(R.string.new_pictures_text))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();

            /*NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(0,notification);

            //发送特定广播
            sendBroadcast(new Intent(ACTION_SHOW_NOTIFICATION),PERM_PRIVATE);*/
            showBackgroundNotification(0, notification);
        }
        QueryPreferences.setLastResultId(this,resultId);
    }

    private void showBackgroundNotification(int requestCode, Notification notification) {
        Intent intent=new Intent(ACTION_SHOW_NOTIFICATION);
        intent.putExtra(REQUEST_CODE,REQUEST_CODE);
        intent.putExtra(NOTIFICATION,notification);
        sendOrderedBroadcast(intent,PERM_PRIVATE,null,null,
                Activity.RESULT_OK,null,null);
    }

    /**
     * 检查网络是否可用
     * @return
     */
    public boolean isNetworkAvailableAndConnected(){
        ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable  = connectivityManager.getActiveNetworkInfo() != null;
        boolean isNetworkConnected  = isNetworkAvailable && connectivityManager.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;

    }
}
