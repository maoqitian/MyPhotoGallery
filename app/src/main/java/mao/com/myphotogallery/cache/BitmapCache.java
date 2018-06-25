package mao.com.myphotogallery.cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * 图片缓存
 * 使用LruCache
 */

public class BitmapCache {

    private LruCache<String,Bitmap> mMemoryCache;
    public BitmapCache(){
        int maxMemory= (int) (Runtime.getRuntime().maxMemory()/1024);
        int cacheSize=maxMemory/8;
        mMemoryCache=new LruCache<>(cacheSize);
    }

    /**
     * 把图片加入缓存中
     * @param key
     * @param bitmap
     */
    public void addBitmpToCache(String key,Bitmap bitmap){
        mMemoryCache.put(key, bitmap);
    }

    //从缓存中获取图片
    public Bitmap getBitmapFromCache(String key){
        return mMemoryCache.get(key);
    }
}
