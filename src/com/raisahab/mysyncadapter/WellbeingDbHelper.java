package com.raisahab.mysyncadapter;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

/**
 * Created by Udini on 6/22/13.
 */
public class WellbeingDbHelper extends SQLiteOpenHelper {

	//private static final String DATABASE_NAME = Environment.getExternalStorageDirectory() + "/SYNCADAPTER.db";
	private static final String DATABASE_NAME = "Database.db";
	//private static final String DATABASE_NAME = Environment.getExternalStorageDirectory() + "/KOOoo.db";
	private static final int DATABASE_VERSION = 1;

	// DB Table consts
	public static final String TABLE_NAME = "users";
	public static final String COL_ID = "_id";

	public static final String COL_NAME = "Name";
	public static final String COL_LNAME = "LName";

	public static final String COL_EMAIL = "Email";
	public static final String COL_PHONE = "Phone";

	// Database creation sql statement
	public static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + COL_ID + " integer   primary key autoincrement, "
			+ COL_NAME + " text, " + COL_LNAME + " text, " + COL_EMAIL + " text, " + COL_PHONE + " text" + ");";

	public WellbeingDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		Log.v("Database", "Datasbase created");
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(WellbeingDbHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

	public Boolean addUser(Context context, ContentValues cv) {

		SQLiteDatabase mdb = this.getWritableDatabase();
		mdb.beginTransaction();
		try {
			mdb.insert(TABLE_NAME, null, cv);
			mdb.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			mdb.endTransaction();
			return true;
		}

	}

	public ArrayList<User> getUsers() {

		Log.v("get ser", "get user called");
		ArrayList<User> userList = new ArrayList<>();

		SQLiteDatabase mdb = this.getReadableDatabase();
		Cursor cur = mdb.query(TABLE_NAME, null, null, null, null, null, null);

		if (cur.moveToFirst()) {
			do {
				User user = new User();
				user.setName(cur.getString(cur.getColumnIndex(COL_NAME)));
				user.setLname(cur.getString(cur.getColumnIndex(COL_LNAME)));
				user.setPhone(cur.getString(cur.getColumnIndex(COL_PHONE)));
				user.setEmail(cur.getString(cur.getColumnIndex(COL_EMAIL)));
				userList.add(user);
			} while (cur.moveToNext());
		}
		return userList;
	}
}
