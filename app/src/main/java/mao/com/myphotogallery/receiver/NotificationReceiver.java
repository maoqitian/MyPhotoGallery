package mao.com.myphotogallery.receiver;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import mao.com.myphotogallery.service.PhotoGalleryService;

/**
 * 最后一个接 收目标broadcast
 */
public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "received result: " + getResultCode());
        if (getResultCode() != Activity.RESULT_OK) {
            // A foreground activity cancelled the broadcast
             return;
            }
            int requestCode = intent.getIntExtra(PhotoGalleryService.REQUEST_CODE, 0);
            Notification notification = (Notification) intent.getParcelableExtra(PhotoGalleryService.NOTIFICATION);
            NotificationManagerCompat notificationManager =NotificationManagerCompat.from(context);
            notificationManager.notify(requestCode, notification);

        }
}
