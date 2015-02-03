package com.example.imgresize.data.model.data.assets;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.imgresize.MainActivity;
import com.example.imgresize.R;

public class ProgressNotification {
    public static final int ID = 0;

    static NotificationManager mNotifyManager;
    static NotificationCompat.Builder mBuilder;

        public static void startNotification(Context context){

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

            mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder = new NotificationCompat.Builder(context);
            mBuilder
                    .setContentTitle("Загрузка началась!222!11")
                    .setContentText("Идет загрузка")
                    .setSmallIcon(R.drawable.downloading)
                    .setContentIntent(pendingIntent);

        }

        public static void callProgressBar(int size, int incr){
            mBuilder.setProgress(size, incr, false);
            mBuilder.setContentText(incr+" изображений успешно скачано");
            mNotifyManager.notify(ID, mBuilder.build());

            if(incr==size){
                mBuilder.setContentText("Загрузка завершена!")
                        .setProgress(0,0,false);
                mNotifyManager.notify(ID, mBuilder.build());
            }
        }


    }