package com.raisahab.mysyncadapter;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MyContentProvider extends ContentProvider {

	public static final UriMatcher URI_MATCHER = buildUriMatcher();

	// Uri Matcher for the content provider
	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = MainActivity.PROVIDER;
		return matcher;
	}

	WellbeingDbHelper dbHelper;

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return new String();
	}

	@Override
	public boolean onCreate() {
		System.out.println("Content Provider");
		Context ctx = getContext();
		dbHelper = new WellbeingDbHelper(ctx);
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		//FETCH DATA
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cur = db.query(WellbeingDbHelper.TABLE_NAME, null, null, null, null, null, null);

		return cur;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		SQLiteDatabase mdb = dbHelper.getWritableDatabase();
		mdb.beginTransaction();
		try {
			System.out.println("Insert called");
			long id = mdb.insert(WellbeingDbHelper.TABLE_NAME, null, values);
			mdb.setTransactionSuccessful();
			if (id != -1)
				getContext().getContentResolver().notifyChange(uri, null);
			return User.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnsupportedOperationException("URI: " + uri + " not supported.");

		} finally {
			mdb.endTransaction();
		}

	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
}
