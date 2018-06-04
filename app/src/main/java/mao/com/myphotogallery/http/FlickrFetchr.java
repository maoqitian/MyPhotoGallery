package mao.com.myphotogallery.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 基本网络连接
 */
public class FlickrFetchr {

    //获取网络数据字节数组
    public byte[] getUrlBytes(String urlSpec ) throws IOException{
        URL url=new URL(urlSpec);
        HttpURLConnection httpURLConnection= (HttpURLConnection) url.openConnection();
        try{

            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            InputStream inputStream = httpURLConnection.getInputStream();
            if(httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK){//连接不成功
                throw new IOException(httpURLConnection.getResponseMessage() +  ": with " +  urlSpec);
            }
            int bytesRead=0;
            byte[] buffer=new byte[1024];
            while((bytesRead=inputStream.read(buffer))>0){
                baos.write(buffer,0,bytesRead);
            }
            baos.close();
            return baos.toByteArray();
        }finally {
           httpURLConnection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

}
