package mao.com.myphotogallery.model;

import java.util.List;

/**
 *
 */

public class PhotosItem {
    public Photos photos;
    public static class Photos{
        public List<GalleryItem> photo;
    }
}
