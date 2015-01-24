package com.example.imgresize;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.imgresize.data.model.DownloadImage;
import com.example.imgresize.data.model.ImageModel;
import com.example.imgresize.data.model.data.assets.Parsing;

import org.json.JSONException;

public class MainActivity extends ActionBarActivity {

    public ArrayList<ImageModel> links = new ArrayList<>();
    ListView imageList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            links = Parsing.parseJSON(this);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }



    public void startDownloading(View view) {
        imageList = (ListView) findViewById(R.id.listView);

        new DownloadImage(MainActivity.this, new DownloadImage.IOnPicturesDownloader() {
            @Override
            public void onImagesDownloaded(ArrayList<Bitmap> images) {
                ImageAdapter adapter = new ImageAdapter(MainActivity.this, images);
                imageList.setAdapter(adapter);
                imageList.setVisibility(View.VISIBLE);
            }
        }).execute(links);
    }

    //_________________________ADAPTOR FOR LIST_______________________________________
    public class ImageAdapter extends BaseAdapter {
        Context context;
        ArrayList<Bitmap> images;

        public ImageAdapter(Context context, ArrayList<Bitmap> imgs) {
            //super(context, R.layout.image_row, R.id.imageInRow, imgs);

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