package mao.com.myphotogallery;

import android.support.v4.app.Fragment;


import mao.com.myphotogallery.base.SingleFragmentActivity;

/**
 * API key f2c037ce818631eab099ef43e54c3165
 * 秘钥  722fe0676661ace6
 */

public class PhotoGalleryActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }

}
