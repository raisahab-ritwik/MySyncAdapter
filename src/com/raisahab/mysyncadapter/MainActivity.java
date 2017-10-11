package com.raisahab.mysyncadapter;

import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SyncStatusObserver;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	private PopupWindow popupWindow;
	private Account appAccount;
	private Context mContext;
	private TextView tv_status;
	final String ACCOUNT_NAME = "Ritwik";
	public static final String ACCOUNT_TYPE = "com.raisahab.mysyncadapter";
	public static final String PROVIDER = "com.raisahab.provider";
	private Uri CONTENT_URI = Uri.parse("content://" + PROVIDER + "/users");
	SyncStatusObserver syncObserver = new SyncStatusObserver() {
		@Override
		public void onStatusChanged(final int which) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					refreshSyncStatus();
				}
			});
		}
	};
	Object handleSyncObserver;

	@Override
	protected void onResume() {
		super.onResume();
		handleSyncObserver = ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE | ContentResolver.SYNC_OBSERVER_TYPE_PENDING,
				syncObserver);
	}

	@Override
	protected void onPause() {
		if (handleSyncObserver != null)
			ContentResolver.removeStatusChangeListener(handleSyncObserver);
		super.onStop();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//Log.v(null, null);
		appAccount = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
		AccountManager accountManager = AccountManager.get(this);
		accountManager.addAccountExplicitly(appAccount, null, null);
		ContentResolver.setIsSyncable(appAccount, PROVIDER, 1);
		ContentResolver.setMasterSyncAutomatically(true);
		ContentResolver.setSyncAutomatically(appAccount, PROVIDER, true);
		mContext = MainActivity.this;
		tv_status = (TextView) findViewById(R.id.tv_status);
		refreshSyncStatus();

		((CheckBox) findViewById(R.id.cbIsSyncable)).setChecked(true);
		((CheckBox) findViewById(R.id.cbIsSyncable)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (appAccount == null) {
					Toast.makeText(mContext, "Please connect first", Toast.LENGTH_SHORT).show();
					return;
				}

				// Setting the syncable state of the sync adapter
				String authority = PROVIDER;
				ContentResolver.setIsSyncable(appAccount, authority, isChecked ? 1 : 0);
			}
		});

		((CheckBox) findViewById(R.id.cbAutoSync)).setChecked(true);
		((CheckBox) findViewById(R.id.cbAutoSync)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (appAccount == null) {
					Toast.makeText(mContext, "Please connect first", Toast.LENGTH_SHORT).show();
					return;
				}

				// Setting the autosync state of the sync adapter
				String authority = PROVIDER;
				System.out.println("is autosync : is cecked: " + isChecked);
				ContentResolver.setSyncAutomatically(appAccount, authority, isChecked);
			}
		});
	}

	public void onViewRemoteDataClick(View v) {

		new AsyncTask<Void, Void, ArrayList<User>>() {

			ProgressDialog progressDialog = new ProgressDialog(mContext);

			@Override
			protected void onPreExecute() {

				progressDialog.show();

			}

			@Override
			protected ArrayList<User> doInBackground(Void... nothing) {
				WellBeingServerAccessor serverAccessor = new WellBeingServerAccessor();
				try {
					return serverAccessor.fetcUsers();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(ArrayList<User> tvShows) {
				progressDialog.dismiss();
				if (tvShows != null) {
					showOnDialog("Server Data", tvShows);
				}
			}
		}.execute();

	}

	public void onViewLocalDataClick(View v) {

		WellbeingDbHelper dbHelper = new WellbeingDbHelper(mContext);
		ArrayList<User> userList = dbHelper.getUsers();
		System.out.println("User ist size " + userList.size());
		showOnDialog("Local Data", userList);

	}

	public void onAddLocalDataClick(View v) {

		/*
		 * ContentValues cv = new ContentValues();
		 * cv.put(WellbeingDbHelper.COL_NAME, "Sasha");
		 * cv.put(WellbeingDbHelper.COL_EMAIL, "sa@sa.com");
		 * cv.put(WellbeingDbHelper.COL_PHONE, "123");
		 * getContentResolver().insert(User.CONTENT_URI, cv);
		 */
		sowPopUpMenu(v);
	}

	public void onSyncClick(View v) {
		Log.d("raisahab", "" + appAccount);
		Toast.makeText(mContext, ".........Sync.........!!!", Toast.LENGTH_SHORT).show();
		Bundle bundle = new Bundle();
		bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true); // Performing a sync no matter if it's off
		bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true); // Performing a sync no matter if it's off
		ContentResolver.requestSync(appAccount, PROVIDER, bundle);

	}

	private void showOnDialog(String title, ArrayList<User> userList) {

		//Log.i("Local", "Local-------" + userList.get(0).getName());

		ArrayList<String> users = new ArrayList<>();

		if (userList != null) {
			for (int i = 0; i < userList.size(); i++) {
				users.add(userList.get(i).getName());
			}

		} else {
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(title);
		builder.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, users), null);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				dialogInterface.dismiss();
			}
		}).show();
	}

	private void refreshSyncStatus() {
		String status;

		if (ContentResolver.isSyncActive(appAccount, PROVIDER))
			status = "Status: Syncing..";
		else if (ContentResolver.isSyncPending(appAccount, PROVIDER))
			status = "Status: Pending..";
		else
			status = "Status: Idle";

		((TextView) findViewById(R.id.tv_status)).setText(status);
		Log.d("MainActivity", "refreshSyncStatus> " + status);
	}

	private void sowPopUpMenu(View v) {
		LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View popupView = layoutInflater.inflate(R.layout.form_layout, null);
		popupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
		popupWindow.setFocusable(true);
		popupWindow.update();
		Button btn_done = (Button) popupView.findViewById(R.id.bt_done);
		final EditText fname = (EditText) popupView.findViewById(R.id.fname);
		final EditText lname = (EditText) popupView.findViewById(R.id.lname);
		final EditText email = (EditText) popupView.findViewById(R.id.email);
		final EditText phone = (EditText) popupView.findViewById(R.id.phone);
		btn_done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				ContentValues cv = new ContentValues();
				cv.put(WellbeingDbHelper.COL_NAME, fname.getText().toString().trim());
				cv.put(WellbeingDbHelper.COL_LNAME, lname.getText().toString().trim());
				cv.put(WellbeingDbHelper.COL_EMAIL, email.getText().toString().trim());
				cv.put(WellbeingDbHelper.COL_PHONE, phone.getText().toString().trim());
				getContentResolver().insert(User.CONTENT_URI, cv);

				popupWindow.dismiss();

			}
		});

	}
}
