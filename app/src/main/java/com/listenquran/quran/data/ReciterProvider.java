package com.listenquran.quran.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by lenovo on 7/6/2017.
 */

public class ReciterProvider extends ContentProvider {

    // reciter table
    public static final int RECITER = 100;
    public static final int RECITER_WITH_ID = 101;

    // favorite table
    public static final int FAVORITE = 200;
    public static final int FAVORITE_WITH_ID = 201;

    public static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // reciter table
        uriMatcher.addURI(ReciterContract.CONTENT_AUTHORITY, ReciterContract.PATH_RECITER, RECITER); // all table
        uriMatcher.addURI(ReciterContract.CONTENT_AUTHORITY, ReciterContract.PATH_RECITER + "/#", RECITER_WITH_ID); // single row
        // favorite table
        uriMatcher.addURI(ReciterContract.CONTENT_AUTHORITY, ReciterContract.PATH_FAVORITE, FAVORITE); // all table
        uriMatcher.addURI(ReciterContract.CONTENT_AUTHORITY, ReciterContract.PATH_FAVORITE + "/#", FAVORITE_WITH_ID); // single row

        return uriMatcher;
    }

    private ReciterDbHelper mDbHelper;


    @Override
    public boolean onCreate() {
        mDbHelper = new ReciterDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projections, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);

        Cursor returnCursor;
        switch (match) {
            case RECITER:
                returnCursor = db.query(ReciterContract.ReciterEntry.TABLE_NAME,
                        projections,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FAVORITE:
                returnCursor = db.query(ReciterContract.FavoriteEntry.TABLE_NAME,
                        projections,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri" + uri);
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        Uri returnUri;
        switch (match) {
            case RECITER:
                long id = db.insert(ReciterContract.ReciterEntry.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(ReciterContract.ReciterEntry.CONTENT_URI, id);

                } else {
                    throw new android.database.SQLException("Failed to insert row into" + uri);
                }
                break;
            case FAVORITE:
                long favoriteID = db.insert(ReciterContract.FavoriteEntry.TABLE_NAME, null, contentValues);
                if (favoriteID > 0) {
                    returnUri = ContentUris.withAppendedId(ReciterContract.FavoriteEntry.CONTENT_URI, favoriteID);
                    Toast.makeText(getContext(), "" + returnUri, Toast.LENGTH_SHORT).show();
                } else {
                    throw new android.database.SQLException("Failed to insert row into" + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri" + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        // Keep track of the number of deleted favorites
        int favoriteDeleted; // starts as 0
        switch (match) {
            // Handle the single item case, recognized by the ID included in the URI path
            case FAVORITE_WITH_ID:
                // Get the task ID from the URI path
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                favoriteDeleted = db.delete(ReciterContract.FavoriteEntry.TABLE_NAME, "_id=?", new String[]{id});
                Toast.makeText(getContext(), "" + favoriteDeleted, Toast.LENGTH_SHORT).show();

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (favoriteDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return favoriteDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
