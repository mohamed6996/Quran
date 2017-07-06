package com.listenquran.quran.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.listenquran.quran.data.ReciterContract.ReciterEntry;

/**
 * Created by lenovo on 6/29/2017.
 */

public class ReciterDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "reciter.db";
    private static final int DATABASE_VERSION = 1;

    public ReciterDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TABLE = "CREATE TABLE " + ReciterEntry.TABLE_NAME + " (" +
                ReciterEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ReciterEntry.COLUMN_RECITER_NAME + " TEXT, " +
                ReciterEntry.COLUMN_SERVER + " TEXT, " +
                ReciterEntry.COLUMN_SURAS + " TEXT, " +
                ReciterEntry.COLUMN_RECITER_LETTER + " TEXT ); " ;
             //   ReciterEntry.COLUMN_RECITER_SERVER + " INTEGER ); ";

        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReciterEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
