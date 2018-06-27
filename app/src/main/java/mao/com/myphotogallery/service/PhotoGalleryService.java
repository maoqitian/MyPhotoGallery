package mao.com.myphotogallery.service;

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


    // Set interval to 1 minute 15分钟
    private static final long POLL_INTERVAL_MS = TimeUnit.MINUTES.toMillis(15);

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
            Notification notification=new Notification.Builder(this)
                    .setTicker(resources.getString(R.string.new_pictures_title))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(resources.getString(R.string.new_pictures_title))
                    .setContentText(resources.getString(R.string.new_pictures_text))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(0,notification);
        }
        QueryPreferences.setLastResultId(this,resultId);
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
