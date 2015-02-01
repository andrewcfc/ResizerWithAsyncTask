package com.example.imgresize;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.imgresize.data.model.DownloadImage;

public class MainActivity extends ActionBarActivity {

    ListView imageList;
    public static final String ACTION = "com.example.imageresize.DOWNLOADING_BITMAPS";
    public static final String PARAMS = "images";
    ImageReceive receive;
    ArrayList<String> paths = new ArrayList<>();
    ArrayList<Bitmap> images = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(ACTION);
        receive = new ImageReceive();
        registerReceiver(receive, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receive);
    }

    public void startDownloading(View view) {
        imageList = (ListView) findViewById(R.id.listView);
        startService(new Intent(MainActivity.this, DownloadImage.class));
    }



    //---------------------RECEIVER-------------------------
    public class ImageReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            paths = intent.getStringArrayListExtra(PARAMS);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            for(int i=0; i<paths.size(); i++) {
                images.add(BitmapFactory.decodeFile(paths.get(i), options));
            }

            ImageAdapter adapter = new ImageAdapter(MainActivity.this, images);
            imageList.setAdapter(adapter);
            imageList.setVisibility(View.VISIBLE);

            stopService(new Intent(MainActivity.this, DownloadImage.class));
        }
    }

    //_________________________ADAPTOR FOR LIST_______________________________________
    public class ImageAdapter extends BaseAdapter {
        Context context;

        ArrayList<Bitmap> images;

        public ImageAdapter(Context context, ArrayList<Bitmap> imgs) {
            this.images = imgs;
            this.context = context;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Object getItem(int position) {
            return images.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.image_row, parent, false);
            }
            ImageView img = (ImageView) row.findViewById(R.id.imageInRow);
            img.setImageBitmap(images.get(position));

            return row;
        }

    }
}