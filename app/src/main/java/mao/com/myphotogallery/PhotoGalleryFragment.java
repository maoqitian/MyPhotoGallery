package mao.com.myphotogallery;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mao.com.myphotogallery.http.FlickrFetchr;
import mao.com.myphotogallery.model.GalleryItem;
import mao.com.myphotogallery.thread.ThumbnailDownloader;

/**
 * 显示图片Fragment
 */
public class PhotoGalleryFragment extends Fragment{

    private RecyclerView mPhotoRecyclerView;
    private static final String TAG = "PhotoGalleryFragment";

    private List<GalleryItem> mItems = new ArrayList<>();

    private int currentPage = 1;//当前数据页数

    private PhotoAdapter mAdapter;

    private static final int ITEM_WIDTH = 300;//每个Item 的宽度
    private GridLayoutManager mGridLayoutManager;

    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setReenterTransition(true);//Activity 重新创建的时候不重新创建Fragment

        //使用AsyncTask 异步任务获取网络数据
        new FetchItemsTask().execute(currentPage);
        Handler responseHandler=new Handler();
        mThumbnailDownloader=new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
            @Override
            public void onThumbnailDownload(PhotoHolder photoHolder, Bitmap bitmap) {
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                photoHolder.bindGalleryItem(drawable);
            }
        });
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.e(TAG, "Background thread started");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mPhotoRecyclerView=view.findViewById(R.id.photo_recycler_view);

        //在fragment的视图创建时就计算并设置好网格列数。
        int spanCount = calcSpanCount();
        mGridLayoutManager=new GridLayoutManager(getActivity(),spanCount);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));

        mPhotoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        int lastPosition = -1;
                        //判断是否滑动到底部
                        if(newState == RecyclerView.SCROLL_STATE_IDLE){
                            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                            lastPosition=((GridLayoutManager)layoutManager).findLastVisibleItemPosition();
                            if(lastPosition == recyclerView.getLayoutManager().getItemCount()-1){//已经滑动到底部
                                currentPage+=1;
                                new FlickrFetchr().fetchItems(currentPage);//加载下一页
                            }
                        }
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                    }
                });


        //动态调整网格列
        mPhotoRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int spanCount = calcSpanCount();
                mGridLayoutManager.setSpanCount(spanCount);
            }
        });
        setupAdapter();
        return view;
    }
    //适应不同大小网格适配
    private int calcSpanCount() {
        Display defaultDisplay = getActivity().getWindowManager().getDefaultDisplay();
        Point point=new Point();
        defaultDisplay.getSize(point);
        int width=point.x;
        int spanCount= Math.round(width/ITEM_WIDTH);
        return spanCount;
    }

    private void setupAdapter() {
        if (mAdapter == null&& isAdded()) {//isAdded()检查确认fragment已与目 标activity相关联，从而保证getActivity()方法返回结果非空]
            mAdapter = new PhotoAdapter(mItems);
            mPhotoRecyclerView.setAdapter(mAdapter);
        }else {
            mAdapter.setItems(mItems);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.e(TAG, "Background thread destroyed");
    }

    public static PhotoGalleryFragment newInstance() {
        
        Bundle args = new Bundle();
        
        PhotoGalleryFragment fragment = new PhotoGalleryFragment();
        fragment.setArguments(args);

        return fragment;
    }


    private  class FetchItemsTask extends AsyncTask<Integer,Void, List<GalleryItem> >{

        @Override
        protected  List<GalleryItem>  doInBackground(Integer... params) {
            int page=params[0];
            return new FlickrFetchr().fetchItems(page);
        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            mItems = galleryItems;
            setupAdapter();
        }
    }


    private class PhotoHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        public PhotoHolder(View itemView) {
            super(itemView);
            mImageView =  itemView.findViewById(R.id.item_image_view);
        }
        public void bindGalleryItem(GalleryItem item) {

        }
        public void bindGalleryItem(Drawable drawable) {
             mImageView.setImageDrawable(drawable);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<GalleryItem> mGalleryItems;
        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        public void setItems(List<GalleryItem> items) {
            mGalleryItems = items;
        }
        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_gallery, null);
            return new PhotoHolder(view);
        }
        @Override
        public void onBindViewHolder(PhotoHolder photoHolder, int position)
        {
            GalleryItem galleryItem = mGalleryItems.get(position);
            Drawable drawable=getResources().getDrawable(R.drawable.bill_up_close);
            photoHolder.bindGalleryItem(drawable);
            mThumbnailDownloader.queueThumbnail(photoHolder,galleryItem.getmUrl());
        }
        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }
}
