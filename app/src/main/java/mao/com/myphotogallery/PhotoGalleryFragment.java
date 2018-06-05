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
import android.widget.TextView;

import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mao.com.myphotogallery.http.FlickrFetchr;
import mao.com.myphotogallery.model.GalleryItem;

/**
 * 显示图片Fragment
 */
public class PhotoGalleryFragment extends Fragment{

    private RecyclerView mPhotoRecyclerView;
    private static final String TAG = "PhotoGalleryFragment";

    private List<GalleryItem> mItems = new ArrayList<>();

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
        setupAdapter();
        return view;
    }

    private void setupAdapter() {
        if (isAdded()) {//isAdded()检查确认fragment已与目 标activity相关联，从而保证getActivity()方法返回结果非空
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    public static PhotoGalleryFragment newInstance() {
        
        Bundle args = new Bundle();
        
        PhotoGalleryFragment fragment = new PhotoGalleryFragment();
        fragment.setArguments(args);

        return fragment;
    }


    private class FetchItemsTask extends AsyncTask<Void,Void, List<GalleryItem> >{

        @Override
        protected  List<GalleryItem>  doInBackground(Void... voids) {
            return new FlickrFetchr().fetchItems();
        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            mItems = galleryItems;
            setupAdapter();
        }
    }


    private class PhotoHolder extends RecyclerView.ViewHolder {
        private TextView mTitleTextView;
        public PhotoHolder(View itemView) {
            super(itemView);
            mTitleTextView = (TextView) itemView;
        }
        public void bindGalleryItem(GalleryItem item)
        {
            mTitleTextView.setText(item.toString());
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<GalleryItem> mGalleryItems;
        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;   }
        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
        {   TextView textView = new TextView(getActivity());   return new PhotoHolder(textView);
        }
        @Override
        public void onBindViewHolder(PhotoHolder photoHolder, int position)
        {   GalleryItem galleryItem = mGalleryItems.get(position);
        photoHolder.bindGalleryItem(galleryItem);
        }
        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }
}
