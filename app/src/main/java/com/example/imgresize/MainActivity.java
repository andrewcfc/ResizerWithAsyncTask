package com.example.imgresize;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends ActionBarActivity {

    private ImageView mImageView;

    public ArrayList<String> links = new ArrayList<>();
    public ArrayList<Bitmap> pictures = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            links = parseJSON();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startDownloading(View view) {
        mImageView = (ImageView) findViewById(R.id.ImageField);
        new DownloadImage().execute(links);
    }


    // использую AsyncTask
    public class DownloadImage extends AsyncTask<ArrayList<String>, Void, ArrayList<Bitmap>> {

        @Override  // выполняется 1
        protected ArrayList<Bitmap> doInBackground(ArrayList<String>... arg0) {
            return downloadImage(arg0[0]);
        }

        // выполняется 2
        private ArrayList<Bitmap> downloadImage(ArrayList<String> _url) {
            ArrayList<Bitmap> pics = new ArrayList<>();
            URL url;
            BufferedOutputStream out;
            InputStream in;
            BufferedInputStream buf;

            for(int i=0; i<links.size(); i++){
                try {
                    url = new URL(_url.get(i));
                    in = url.openStream();

                    buf = new BufferedInputStream(in);

                    Bitmap bMap = BitmapFactory.decodeStream(buf);

                    if (in != null) {
                        in.close();
                    }
                    if (buf != null) {
                        buf.close();
                    }
                    pics.add(bMap);

                } catch (Exception e) {
                    Log.e("Error reading file", e.toString());
                }
            }

            return pics;
        }

        // выполняется 3
        protected void onPostExecute(ArrayList<Bitmap> image) {
            setImages(image);
        }

        // выполняется 4
        private void setImages(ArrayList<Bitmap> image)
        {
          pictures = image;
          Button showBtn = (Button) findViewById(R.id.showBtn);
          Button downloadBtn = (Button) findViewById(R.id.downloadBtn);
          downloadBtn.setVisibility(View.INVISIBLE);
          showBtn.setVisibility(View.VISIBLE);
        }
    }

    public void showImages(View v){
        mImageView.setImageBitmap(pictures.get(0));
    }

    public ArrayList<String> parseJSON() throws JSONException, IOException {
        String json;
        try {
            InputStream is = getAssets().open("lampard.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        JSONObject JSObj = new JSONObject(json);
        ArrayList<String> urls = new ArrayList<>();
        JSONArray m_jArry = JSObj.getJSONArray("URLS");

        for (int i = 0; i < m_jArry.length(); i++) {
            JSONObject o = (JSONObject) m_jArry.get(i);
            urls.add(o.get("url").toString());
        }
        return urls;
    }

}