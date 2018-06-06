package mao.com.myphotogallery.http;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import mao.com.myphotogallery.model.GalleryItem;
import mao.com.myphotogallery.model.PhotosItem;

/**
 * 基本网络连接
 */
public class FlickrFetchr {

    private static final String TAG = "FlickrFetchr";
    private static final String API_KEY = " f2c037ce818631eab099ef43e54c3165 ";

    //获取网络数据字节数组
    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream inputStream = httpURLConnection.getInputStream();
            if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {//连接不成功
                throw new IOException(httpURLConnection.getResponseMessage() + ": with " + urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                baos.write(buffer, 0, bytesRead);
            }
            baos.close();
            return baos.toByteArray();
        } finally {
            httpURLConnection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    /**
     * 获取GalleryItem 数据
     * @param page 加载的是第几页数据
     */
    public  List<GalleryItem>  fetchItems(int page) {
        List<GalleryItem> items = new ArrayList<>();
        try {
        String url = Uri.parse("https://api.flickr.com/services/rest/")
                .buildUpon()
                .appendQueryParameter("method", "flickr.photos.getRecent")
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .appendQueryParameter("extras", "url_s")
                .appendQueryParameter("page", String.valueOf(page))
                .build().toString();
            String jsonStr=getUrlString(url);
            JSONObject jsonObject=new JSONObject(jsonStr);
            Log.e(TAG, "Received JSON: " + jsonStr);
            parseItems(items,jsonObject);
            //使用 Gson 解析 JSON 数据
           /* Gson gson=new Gson();
            PhotosItem fromJson = gson.fromJson(jsonStr, new TypeToken<PhotosItem>() {}.getType());
            items.addAll( fromJson.photos.photo);*/
        } catch (IOException  | JSONException e) {//| JSONException e
            e.printStackTrace();
        }
        return items;
    }

    /**
     * 解析JSON 数据
     * @param items
     * @param jsonBody
     */
    private void parseItems(List<GalleryItem> items, JSONObject jsonBody) throws IOException,JSONException {

        JSONObject photosJsonObject=jsonBody.getJSONObject("photos");
        JSONArray photojsonArray=photosJsonObject.getJSONArray("photo");
        for (int i = 0; i < photosJsonObject.length(); i++) {
            GalleryItem galleryItem=new GalleryItem();
            JSONObject photoJsonObject  = photojsonArray.getJSONObject(i);
            galleryItem.setmCaption(photoJsonObject.getString("title"));
            galleryItem.setmId(photoJsonObject.getString("id"));
            if(!photoJsonObject.has("url_s")){
               continue;
            }
            galleryItem.setmUrl(photoJsonObject.getString("url_s"));
            items.add(galleryItem);
        }
    }
}