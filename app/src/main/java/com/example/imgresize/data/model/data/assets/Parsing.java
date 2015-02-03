package com.example.imgresize.data.model.data.assets;

import android.content.Context;

import com.example.imgresize.data.model.ImageModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class Parsing {

    public static ArrayList<ImageModel> parseJSON(Context context) throws JSONException, IOException {
        String json;
        try {
            InputStream is = context.getAssets().open("lampard.json");
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
        ArrayList<ImageModel> urls = new ArrayList<>();
        JSONArray m_jArry = JSObj.getJSONArray("URLS");

        for (int i = 0; i < m_jArry.length(); i++) {
            JSONObject o = (JSONObject) m_jArry.get(i);
            String imString = o.get("url").toString();
            ImageModel modelTest = new ImageModel(imString);
            urls.add(modelTest);
            // urls.add(o.get("url").toString());
        }
        return urls;
    }

}
