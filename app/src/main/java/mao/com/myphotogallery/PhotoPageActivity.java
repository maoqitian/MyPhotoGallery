package mao.com.myphotogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

import mao.com.myphotogallery.base.SingleFragmentActivity;

/**
 * 单个照片点击显示WebView Activity
 */
public class PhotoPageActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context, Uri uri){
        Intent intent=new Intent(context,PhotoPageActivity.class);
        intent.setData(uri);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return PhotoPageFragment.newInstance(getIntent().getData());
    }

    /**
     * 处理WebView 历史记录回退
     */
    @Override
    public void onBackPressed() {
        PhotoPageFragment PhotoPageFragment = (mao.com.myphotogallery.PhotoPageFragment) createFragment();
        if(PhotoPageFragment.mWebView.canGoBack()){
            PhotoPageFragment.mWebView.goBack();
        }else {
            super.onBackPressed();
        }
    }
}
