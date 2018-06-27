package mao.com.myphotogallery.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import mao.com.myphotogallery.http.FlickrFetchr;
import mao.com.myphotogallery.model.GalleryItem;
import mao.com.myphotogallery.utils.QueryPreferences;

/**
 * 服务进程
 */
public class PhotoGalleryService extends IntentService {

    //启动服务
    public static Intent newInstance(Context context) {
        return new Intent(context,PhotoGalleryService.class);
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
            Log.i("毛麒添", "Got an old result: " + resultId);
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
