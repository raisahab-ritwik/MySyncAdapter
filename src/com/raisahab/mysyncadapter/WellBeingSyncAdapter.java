package com.raisahab.mysyncadapter;

import java.util.ArrayList;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

public class WellBeingSyncAdapter extends AbstractThreadedSyncAdapter {

	public WellBeingSyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		Log.v("Raisahab", "Constructor");

	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
		Log.i("RAISAHAB", "on perform sync");
		try {

			WellBeingServerAccessor accessor = new WellBeingServerAccessor();
			// Get users from remote
			ArrayList<User> serverUserList = accessor.fetcUsers();
			for (int i = 0; i < serverUserList.size(); i++) {
				System.out.println("name" + serverUserList.get(i).getName());
				System.out.println("pone" + serverUserList.get(i).getPhone());
				System.out.println("email" + serverUserList.get(i).getEmail());

			}
			// Get users from local
			ArrayList<User> localUserList = new ArrayList<User>();
			Cursor cur = provider.query(User.CONTENT_URI, null, null, null, null);
			if (cur != null) {
				while (cur.moveToNext()) {
					localUserList.add(User.fromCursor(cur));
				}
				cur.close();
			}
			// See what Local users are missing on Remote
			ArrayList<User> usersToRemote = new ArrayList<User>();
			for (User person1 : localUserList) {
				// Loop arrayList1 items
				boolean found = false;
				for (User person2 : serverUserList) {
					if (person1.getName().equalsIgnoreCase(person2.getName())) {
						found = true;
					}
				}
				if (!found) {
					usersToRemote.add(person1);
					Log.d("Result2", "Missing remote " + person1.getName());
				}
			}
			// See what Remote users are missing on Local
			ArrayList<User> usersToLocal = new ArrayList<User>();
			for (User person2 : serverUserList) {
				// Loop arrayList1 items
				boolean found = false;
				for (User person1 : localUserList) {
					if (person2.getName().equalsIgnoreCase(person1.getName())) {
						found = true;
					}
				}
				if (!found) {
					usersToLocal.add(person2);
					Log.d("Result", "Local missing " + person2.getName());
				}
			}
			if (usersToRemote.size() == 0) {
				Log.d("WellBeingSyncAdapter", "> No local changes to update server");
			} else {
				Log.d("WellBeingSyncAdapter", "> Updating remote server with local changes");

				// Updating remote tv shows
				for (User remoteUser : usersToRemote) {
					Log.d("WellBeingSyncAdapter", "> Local -> Remote [" + remoteUser.getName() + "]");
					// Insert users to Server one by one
					accessor.postUser(remoteUser);
				}
			}

			if (usersToLocal.size() == 0) {
				Log.d("WellBeingSyncAdapter", "> No server changes to update local database");
			} else {
				Log.d("WellBeingSyncAdapter", "> Updating local database with remote changes");
				
								// Updating local tv shows
								int i = 0;
								ContentValues usersToLocalValues[] = new ContentValues[usersToLocal.size()];
								for (User localUser : usersToLocal) {
									Log.d("WellBeingSyncAdapter", "> Remote -> Local [" + localUser.getName() + "]");
									usersToLocalValues[i++] = localUser.getContentValues(localUser.getName(), localUser.getPhone(), localUser.getEmail());
								}
								provider.bulkInsert(User.CONTENT_URI, usersToLocalValues);
			}

			Log.d("WellBeingSyncAdapter", "> Finished.");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
