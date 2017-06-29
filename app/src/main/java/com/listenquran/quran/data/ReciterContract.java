package com.listenquran.quran.data;


import android.provider.BaseColumns;


public class ReciterContract {
    public static final class ReciterEntry implements BaseColumns {
        public static final String TABLE_NAME = "reciter";
        public static final String COLUMN_RECITER_NAME = "name";
        public static final String COLUMN_RECITER_ID = "reciterID";
        public static final String COLUMN_RECITER_SERVER = "server";
    }
}
