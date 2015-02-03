package com.example.imgresize.data.model.data.assets;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MySQLiteHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "paths";
    public static final int DB_VERSION = 2;
    public static final String TABLE_NAME = "images";
    public static final String ID= "_id";
    public static final String PATH= "path";

    public Context context;

    public MySQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        Toast.makeText(context, "constructor called", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE "+TABLE_NAME+"("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+PATH+" VARCHAR(254));");
        }
        catch (SQLiteException e){
            Toast.makeText(context, "CREATING ERROR!", Toast.LENGTH_LONG).show();
        }
        Toast.makeText(context, "onCreate called", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE "+TABLE_NAME+" IF EXISTS");
            onCreate(db);
        }
        catch (SQLiteException e){
            Toast.makeText(context, "Updating error! called", Toast.LENGTH_LONG).show();
        }
        Toast.makeText(context, "onUpgrade", Toast.LENGTH_LONG).show();
    }
}