package com.example.user.repeat.ImageLoadHelp;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;


import android.os.AsyncTask;
import android.os.Handler;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.example.user.repeat.Other.BitmapTransformer;
import com.example.user.repeat.Other.Code;
import com.example.user.repeat.Other.HttpConnection;
import com.example.user.repeat.Other.URLs;
import com.example.user.repeat.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

public class ImageLoader {

    // Initialize MemoryCache
    MemoryCache memoryCache = new MemoryCache();

    FileCache fileCache;

    //Create Map (collection) to store image and image url in key value pair
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(
            new WeakHashMap<ImageView, String>());

    //handler to display images in UI thread
    Handler handler = new Handler();

    public ImageLoader(Context context) {

        //fileCache = new FileCache(context);

        // Creates a thread pool that reuses a fixed number of
        // threads operating off a shared unbounded queue.

    }

    // default image show in list (Before online image download)
    final int stub_id = R.drawable.peoople;

    public void DisplayImage(String userid, ImageView imageView) {
        //Store image and url in Map
        imageViews.put(imageView, userid);

        //Check image is stored in MemoryCache Map or not (see MemoryCache.java)
        Bitmap bitmap = memoryCache.get(userid);

        if (bitmap != null) {
            // if image is stored in MemoryCache Map then
            // Show image in listview row
            imageView.setImageBitmap(bitmap);
        } else {
            //queue Photo to download from url
            queuePhoto(userid, imageView);

            //Before downloading image show default image
            imageView.setImageResource(stub_id);
        }
    }

    private void queuePhoto(String userid, ImageView imageView) {
        PhotoToLoad p = new PhotoToLoad(userid, imageView);
        //Check if image already downloaded
        if (imageViewReused(p))
            return;

        new LoadPhotoTask(p).execute(userid);


    }

    //Task for the queue
    private class PhotoToLoad {
        public String userid;
        public ImageView imageView;

        public PhotoToLoad(String id, ImageView i) {
            userid = id;
            imageView = i;
        }
    }

    boolean imageViewReused(PhotoToLoad photoToLoad) {

        String tag = imageViews.get(photoToLoad.imageView);
        //Check url is already exist in imageViews MAP
        if (tag == null || !tag.equals(photoToLoad.userid))
            return true;
        return false;
    }

    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;

            // Show bitmap on UI
            if (bitmap != null)
                photoToLoad.imageView.setImageBitmap(bitmap);
            else
                photoToLoad.imageView.setImageResource(stub_id);
        }
    }

    public void clearCache() {
        //Clear cache directory downloaded images and stored data in maps
        memoryCache.clear();
        fileCache.clear();
    }

    private class LoadPhotoTask extends AsyncTask<String, Integer, Bitmap> {
        private PhotoToLoad p;

        public LoadPhotoTask(PhotoToLoad p) {
            this.p = p;
        }

        @Override
        protected Bitmap doInBackground(String... datas) {
            Integer result;
            try {
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("Role", "1"));
                params.add(new BasicNameValuePair("CustomerNo", "12345"));
                params.add(new BasicNameValuePair("FLaborNo", "12345"));
                params.add(new BasicNameValuePair("UserID", datas[0]));

                JSONObject jobj = new HttpConnection().PostGetJson(URLs.url_loadimage, params);
                if (jobj != null) {
                    result = jobj.getInt("success");
                    if (result == Code.Success) {
                        String base64 = jobj.getString("photo");
                        return BitmapTransformer.Base64ToBitmap(base64);
                    }
                }
            } catch (JSONException e) {
                Log.i("JSONException", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap == null)
                return;
            memoryCache.put(p.userid, bitmap);

            if (imageViewReused(p))
                return;

            // Get bitmap to display
            BitmapDisplayer bd = new BitmapDisplayer(bitmap, p);

            handler.post(bd);
        }
    }

}