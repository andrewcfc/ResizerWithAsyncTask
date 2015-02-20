package com.example.imgresize.data.model.data;

import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.imgresize.R;
import com.example.imgresize.data.model.data.assets.ContentImageProvider;
import com.example.imgresize.data.model.data.assets.MySQLiteHelper;
import com.example.imgresize.data.model.data.network.DownloadImage;

import java.util.ArrayList;

public class FragmentA extends Fragment implements AdapterView.OnItemClickListener {// constans
    public static final String ACTION = "com.example.imageresize.DOWNLOADING_BITMAPS";
    public static final String PARAMS = "images";

    ArrayList<String> paths = new ArrayList<>();
    ArrayList<Bitmap> images = new ArrayList<>();

    Communicator communicator;
    ImageReceive receive;

    ListView list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getActivity() instanceof Communicator){
            communicator = (Communicator) getActivity();
        }
        else throw new ClassCastException("Error!");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().startService(new Intent(getActivity(), DownloadImage.class));
        View view = inflater.inflate(R.layout.fragment_a, container, false);
        list = (ListView) view.findViewById(R.id.listView);
        list.setOnItemClickListener(this);
        return view;
    }

    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(ACTION);
        receive = new ImageReceive();
        getActivity().registerReceiver(receive, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receive);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        communicator.respond(images.get(position));
    }

    public interface Communicator {
        public void respond(Bitmap image);
    }


    //_____________RECEIVIER_____________
    public class ImageReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            getActivity().stopService(new Intent(getActivity(), DownloadImage.class));

            String[] colums = {MySQLiteHelper.PATH};

            new AsyncQueryHandler(getActivity().getContentResolver()) {
                @Override
                protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                    super.onQueryComplete(token, cookie, cursor);
                    int index;
                    while (cursor.moveToNext()) {
                        index = cursor.getColumnIndex(MySQLiteHelper.PATH);
                        paths.add(cursor.getString(index));
                    }

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                    for (int i = 0; i < paths.size(); i++) {
                        images.add(BitmapFactory.decodeFile(paths.get(i), options));
                    }

                    ImageAdapter adapter = new ImageAdapter(getActivity(), images);
                    list.setAdapter(adapter);
                }
            }.startQuery(0, null, ContentImageProvider.CONTENT_URI, colums, null, null, null);
        }

    }

    // ----------------ADAPTOR-------------------------

    public static class ImageAdapter extends BaseAdapter {
        Context context;
        ArrayList<Bitmap> images;

        public ImageAdapter(Context context, ArrayList<Bitmap> images) {
            this.context = context;
            this.images = images;
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
                row = inflater.inflate(R.layout.small_image, parent, false);
            }
            ImageView img = (ImageView) row.findViewById(R.id.smallImage);
            img.setImageBitmap(images.get(position));
            return row;
        }
    }
}
