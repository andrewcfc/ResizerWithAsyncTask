package com.example.imgresize.data.model.data.network;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.imgresize.MainActivity;
import com.example.imgresize.data.model.ImageModel;
import com.example.imgresize.data.model.data.FragmentA;
import com.example.imgresize.data.model.data.assets.ContentImageProvider;
import com.example.imgresize.data.model.data.assets.MySQLiteHelper;
import com.example.imgresize.data.model.data.assets.ProgressNotification;
import com.example.imgresize.data.model.data.assets.Parsing;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class DownloadImage extends Service {

    public ArrayList<ImageModel> links;
    public ImageAsyncDownloading downloader;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        try {
            links = Parsing.parseJSON(this);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "Загрузка стартовала", Toast.LENGTH_LONG).show();
        downloader = new ImageAsyncDownloading(DownloadImage.this, new ImageAsyncDownloading.IOnPicturesDownloader() {
            @Override
            public void onImagesDownloaded(ArrayList<String> links) {
                Intent intent = new Intent();
                intent.setAction(FragmentA.ACTION);
                intent.putExtra(FragmentA.PARAMS, links);
                sendBroadcast(intent);
            }
        });
        downloader.execute(links);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        downloader.cancel(true);
    }

    public static class ImageAsyncDownloading extends AsyncTask<ArrayList<ImageModel>, Void, ArrayList<String>> {

        Context context;
        private IOnPicturesDownloader checker;

        public ImageAsyncDownloading(Context context, IOnPicturesDownloader checker) {
            this.context = context;
            this.checker = checker;
        }

        public interface IOnPicturesDownloader {
            public void onImagesDownloaded(ArrayList<String> paths);
        }


        @SafeVarargs
        @Override
        protected final ArrayList<String> doInBackground(ArrayList<ImageModel>... arg0) {
            return downloadImage(arg0[0]);
        }


        private ArrayList<String> downloadImage(ArrayList<ImageModel> _url) {
            ArrayList<String> links = new ArrayList<>();
            String fileName, link;

            int incr=0;

            ProgressNotification.startNotification(context);

            MySQLiteHelper help = new MySQLiteHelper(context);
            SQLiteDatabase dataBase = help.getWritableDatabase();

            for (int i = 0; i < _url.size(); i++) {
                try {
                    link = _url.get(i).url;

                    fileName = link.substring(link.lastIndexOf('/') + 1, link.length());
                    File file = new File(context.getCacheDir() + fileName);

                    String whereClause = MySQLiteHelper.PATH+"=?";
                    String [] whereArgs = {file.getAbsolutePath()};

                    Cursor cursor = context.getContentResolver().query(ContentImageProvider.CONTENT_URI,
                            new String[]{MySQLiteHelper.PATH},
                            whereClause,
                            whereArgs,
                            null);

                    if(cursor.getCount()>0){
                        incr++;
                        ProgressNotification.callProgressBar(_url.size(), incr);
                        continue;
                    }

                    Bitmap bMap = getScaledBitmapFromUrl(_url.get(i).url, 350, 350);

                    incr++;
                    ProgressNotification.callProgressBar(_url.size(), incr);

                    links.add(file.getAbsolutePath());
                    bMap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MySQLiteHelper.PATH, file.getAbsolutePath());

                    context.getContentResolver().insert(ContentImageProvider.CONTENT_URI, contentValues);

                } catch (Exception e) {
                    Log.e("Error reading file", e.toString());
                }
            }

            dataBase.close();
            return links;
        }

        protected void onPostExecute(ArrayList<String> links) {
            checker.onImagesDownloaded(links);
        }

        private static Bitmap getScaledBitmapFromUrl(String imageUrl, int requiredWidth, int requiredHeight) {
            URL url = null;
            try {
                url = new URL(imageUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            try {
                assert url != null;
                BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);
            } catch (IOException e) {
                e.printStackTrace();
            }
            options.inSampleSize = calculateInSampleSize(options, requiredWidth, requiredHeight);
            options.inJustDecodeBounds = false;
            Bitmap bm = null;
            try {
                bm = BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bm;
        }


        private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;
            if (height > reqHeight || width > reqWidth) {
                if (width > height) {
                    inSampleSize = Math.round((float) height / (float) reqHeight);
                } else {
                    inSampleSize = Math.round((float) width / (float) reqWidth);
                }
            }
            return inSampleSize;
        }


    }
}