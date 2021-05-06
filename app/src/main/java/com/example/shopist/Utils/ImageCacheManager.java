package com.example.shopist.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shopist.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;
import java.io.ByteArrayOutputStream;
import java.util.Map;

public class ImageCacheManager {

    //represents the cache size (10MB for testing purposes)
//    private final static int cacheSize = 10 * 1024 * 1024;
    private final static int cacheSize = 10 * 1024 * 1024;


    //the string is intended to represent the image url at the server
    //the Bitmap is intended to represent the image itself
    private static LruCache<String, Bitmap> bitmapCache;

    public static LruCache<String, Bitmap> getLruInstance(){
        if(bitmapCache==null){
            bitmapCache = new LruCache<String, Bitmap>(cacheSize);
        }
        return bitmapCache;
    }

    public static boolean checkIfImageIsCached(String imageURL){
        //true: means the image is cached
        //false: means the image is not cached
        return bitmapCache.get(imageURL)!=null;
    }

    public static void addPhotoToCache(String imageURL, Bitmap bitmap){
        //first check if photo can fit in current cache
        if(!canPhotoFitInCache(bitmap)){
            Log.d("imageLoading","IMAGE TO BE ADDED DOES NOT FIT IN CURRENT CACHE. WILL DELETE STUFF");
            //photo cannot fit in current cache
            //evict older items (make enough space so it can fit the new photo)

            //size in bytes
            int photoSize = bitmapToByte(bitmap).length;

            //ordered from the least recently accessed to most recently accessed
            Map<String, Bitmap> currentCacheSnapshot= bitmapCache.snapshot();
            //in bytes
            int currentDeletedSize = 0;
            for(String url: currentCacheSnapshot.keySet()){
                Bitmap currentImage = bitmapCache.get(url);
                if(currentDeletedSize + bitmapToByte(currentImage).length >= photoSize){
                    currentDeletedSize += bitmapToByte(currentImage).length;
                    bitmapCache.remove(url);
                    break;
                }
                currentDeletedSize += bitmapToByte(currentImage).length;
                bitmapCache.remove(url);
            }
        }

        Log.d("imageLoading","PRODUCT PHOTO ADDED TO CACHE IN CACHEMANAGER.");

        //add photo to cache
        bitmapCache.put(imageURL, bitmap);
    }

    private static boolean canPhotoFitInCache(Bitmap bitmap){
        //size in Bytes
        int photoSize = bitmapToByte(bitmap).length;

        //size in kB
        int currentTotalSize = 0;
        Map<String, Bitmap> currentCacheSnapshot= bitmapCache.snapshot();
        for(String url: currentCacheSnapshot.keySet()){
            currentTotalSize+= bitmapToByte(bitmapCache.get(url)).length;
        }

        Log.d("imageLoading","MAX CACHE SIZE (IN kB): "+ (cacheSize/1024) );
        Log.d("imageLoading","CURRENT CACHE SIZE (IN kB): "+ (currentTotalSize/1024));
        Log.d("imageLoading","CURRENT PHOTO SIZE (IN kB):"+ (photoSize/1024));


        return (cacheSize/1024) - (currentTotalSize/1024) > (photoSize/1024);
    }

    private static byte[] bitmapToByte(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public static Bitmap retrieveBitmapContent(String url){
        return bitmapCache.get(url);
    }

    //return current cache size in kB
    public static int getCurrentCacheSize(){
        int size = 0;
        Map<String, Bitmap> currentCacheSnapshot= bitmapCache.snapshot();
        for(String url: currentCacheSnapshot.keySet()){
            size+= bitmapToByte(bitmapCache.get(url)).length;
        }
        return (size/1024);
    }

}
