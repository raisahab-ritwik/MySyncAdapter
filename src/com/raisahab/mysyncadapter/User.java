package com.raisahab.mysyncadapter;

import java.io.Serializable;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class User implements Serializable {
	/**
	 * version u-id
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String lname;

	public String getLname() {
		return lname;
	}

	public void setLname(String lname) {
		this.lname = lname;
	}

	private String phone;
	private String email;
	public static final Uri CONTENT_URI = Uri.parse("content://" + MainActivity.PROVIDER + "/users");

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	// Create a TvShow object from a cursor
	public static User fromCursor(Cursor cur) {
		String name = cur.getString(cur.getColumnIndex(WellbeingDbHelper.COL_NAME));
		String lname = cur.getString(cur.getColumnIndex(WellbeingDbHelper.COL_LNAME));
		String phone = cur.getString(cur.getColumnIndex(WellbeingDbHelper.COL_PHONE));
		String email = cur.getString(cur.getColumnIndex(WellbeingDbHelper.COL_EMAIL));
		User user = new User();
		user.email = email;
		user.phone = phone;
		user.name = name;
		user.lname = lname;
		return user;
	}

	public ContentValues getContentValues(String name, String phone, String email) {
		ContentValues values = new ContentValues();
		values.put(WellbeingDbHelper.COL_NAME, name);
		values.put(WellbeingDbHelper.COL_LNAME, name);
		values.put(WellbeingDbHelper.COL_PHONE, phone);
		values.put(WellbeingDbHelper.COL_EMAIL, email);
		return values;
	}
}
