package mao.com.myphotogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

import mao.com.myphotogallery.http.FlickrFetchr;

/**
 * 显示图片Fragment
 */
public class PhotoGalleryFragment extends Fragment{

    private RecyclerView mPhotoRecyclerView;
    private static final String TAG = "PhotoGalleryFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setReenterTransition(true);//Activity 重新创建的时候不重新创建Fragment

        //使用AsyncTask 异步任务获取网络数据
        new FetchItemsTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mPhotoRecyclerView=view.findViewById(R.id.photo_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        return view;
    }

    public static PhotoGalleryFragment newInstance() {
        
        Bundle args = new Bundle();
        
        PhotoGalleryFragment fragment = new PhotoGalleryFragment();
        fragment.setArguments(args);

        return fragment;
    }


    private class FetchItemsTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {    String result = new FlickrFetchr().getUrlString("https://www.bignerdranch.com");
                Log.i(TAG, "Fetched contents of URL: " + result);
            } catch (IOException ioe) {
                Log.e(TAG, "Failed to fetch URL: ", ioe);
            }
            return null;
        }
    }

}
