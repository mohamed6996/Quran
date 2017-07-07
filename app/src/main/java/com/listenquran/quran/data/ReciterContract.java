package com.listenquran.quran.data;


import android.net.Uri;
import android.provider.BaseColumns;


public class ReciterContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private ReciterContract() {}

    public static final String CONTENT_AUTHORITY = "com.listenquran.quran";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_RECITER = "reciters";

    public static final class ReciterEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECITER).build();

        public static final String TABLE_NAME = "reciter";
        public static final String COLUMN_RECITER_NAME = "reciterName";
        public static final String COLUMN_SERVER = "reciterServer";
        public static final String COLUMN_SURAS = "reciterSuras";
        public static final String COLUMN_RECITER_LETTER = "reciterFirstLetter";
    }

// *****************************************************************************************************************
    // favorites table

    public static final String PATH_FAVORITE = "favorites";


    public static final class FavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();

        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_JSON_STRING = "json_string";

    }

}
