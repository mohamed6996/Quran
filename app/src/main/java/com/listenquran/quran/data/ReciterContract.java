package com.listenquran.quran.data;


import android.provider.BaseColumns;


public class ReciterContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private ReciterContract() {}

    public static final class ReciterEntry implements BaseColumns {
        public static final String TABLE_NAME = "reciter";
        public static final String COLUMN_RECITER_NAME = "reciterName";
        public static final String COLUMN_SERVER = "reciterServer";
        public static final String COLUMN_SURAS = "reciterSuras";
        public static final String COLUMN_RECITER_LETTER = "reciterFirstLetter";
    }
}
