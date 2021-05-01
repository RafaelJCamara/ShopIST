package com.example.shopist.Utils;

import android.content.Intent;
import android.os.Bundle;
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

public class CacheManager {

    //represents the cache size (10MB for testing purposes)
    private final static int cacheSize = 10 * 1024 * 1024;

    //the string is intended to represent the image url at the server
    //the Bitmap is intended to represent the image itself
    private static LruCache<String, Bitmap> bitmapCache = new LruCache<String, Bitmap>(cacheSize);

    public static LruCache<String, Bitmap> getLruInstance(){
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
            //photo cannot fit in current cache
            //evict older items (make enough space so it can fit the new photo)

            //size in bytes
            int photoSize = bitmap.getAllocationByteCount();

            //ordered from the least recently accessed to most recently accessed
            Map<String, Bitmap> currentCacheSnapshot= bitmapCache.snapshot();
            //in bytes
            int currentDeletedSize = 0;
            for(String url: currentCacheSnapshot.keySet()){
                Bitmap currentImage = bitmapCache.get(url);
                if(currentDeletedSize+currentImage.getAllocationByteCount()>=photoSize){
                    currentDeletedSize += currentImage.getAllocationByteCount();
                    bitmapCache.remove(url);
                    break;
                }
                currentDeletedSize += currentImage.getAllocationByteCount();
                bitmapCache.remove(url);
            }
        }

        //add photo to cache
        bitmapCache.put(imageURL, bitmap);
    }

    private static boolean canPhotoFitInCache(Bitmap bitmap){
        //size in kB
        int photoSize = bitmap.getAllocationByteCount();
        Map<String, Bitmap> currentCacheSnapshot= bitmapCache.snapshot();
        //size in kB
        int currentTotalSize = 0;
        for(String url: currentCacheSnapshot.keySet()){
            currentTotalSize+=bitmapCache.get(url).getAllocationByteCount();
        }
        return ((cacheSize/1024)-currentTotalSize)>photoSize;
    }


}
