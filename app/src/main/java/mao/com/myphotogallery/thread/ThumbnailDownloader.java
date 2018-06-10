package mao.com.myphotogallery.thread;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import mao.com.myphotogallery.http.FlickrFetchr;

/**
 * 后台线程
 * @param <T>
 */
public class ThumbnailDownloader<T> extends HandlerThread {

    private static final String TAG = "ThumbnailDownloader";

    private static final int MESSAGE_DOWNLOAD = 0;
    private Handler mRequestHandler;
    private ConcurrentMap<T,String> mRequestMap = new ConcurrentHashMap<>();

    private Boolean mHasQuit = false;



    //接口回调通知主线程图片已经下载完成
    public interface ThumbnailDownloadListener<T>{
        void onThumbnailDownload(T target,Bitmap bitmap);
    }

    private ThumbnailDownloadListener<T> thumbnailDownloadListener;

    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> thumbnailDownloadListener){
        this.thumbnailDownloadListener=thumbnailDownloadListener;
    }

    /**
     *
     * @param responseHandler 来自主线程的 handler
     */
    private Handler mResponseHandler;
    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler=responseHandler;
    }

    public void queueThumbnail(T target, String url) {
        Log.e(TAG, "Got a URL: " + url);
        if(url==null){
            mRequestMap.remove(target);
        }else {
            mRequestMap.put(target,url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD).sendToTarget();
        }
    }

    //初始化mRequestHandler并定义该Handler在得到消息队列中的下载消息后应执行的 任务
    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        mRequestHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == MESSAGE_DOWNLOAD){
                    T target= (T) msg.obj;
                    Log.e(TAG, "Got a request for URL: " + mRequestMap.get(target));
                    handleRquest(target);
                }
            }
        };

    }

    //去除消息 加载图片
    private void handleRquest(final T target) {
        try {
        final String url = mRequestMap.get(target);
        if(url == null){
            return;
        }
            byte[] urlBytes = new FlickrFetchr().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(urlBytes, 0, urlBytes.length);
            Log.e(TAG, "Bitmap created");

            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                   if(mRequestMap.get(target)!= url || mHasQuit){
                      return;
                   }
                    mRequestMap.remove(target);
                    thumbnailDownloadListener.onThumbnailDownload(target,bitmap);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean quit() {
        mHasQuit=true;
        return super.quit();
    }
    //清除队列中的所有请求
    public void clearQueue(){
        mResponseHandler.removeMessages(MESSAGE_DOWNLOAD);
        mRequestMap.clear();
    }
}
