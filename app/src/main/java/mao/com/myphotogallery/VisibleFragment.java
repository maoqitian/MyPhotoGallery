package mao.com.myphotogallery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import mao.com.myphotogallery.service.PhotoGalleryService;


/**
 * Fragment 抽象类
 * 动态广播注册与注销
 */

public abstract class VisibleFragment extends Fragment {

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter=new IntentFilter(PhotoGalleryService.ACTION_SHOW_NOTIFICATION);
        getActivity().registerReceiver(mBroadcastReceiver,intentFilter,PhotoGalleryService.PERM_PRIVATE,null);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }


    private BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getActivity(),"Got a broadcast:" + intent.getAction(),Toast.LENGTH_LONG).show();
            // If we receive this, we're visible, so cancel
            // the notification
               Log.i("maoqitian", "canceling notification");
               setResultCode(Activity.RESULT_CANCELED);
        }
    };
}
