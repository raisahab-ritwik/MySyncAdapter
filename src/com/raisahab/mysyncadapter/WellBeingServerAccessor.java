package com.raisahab.mysyncadapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class WellBeingServerAccessor {

	static int CONNECTIONTIMEOUT = 10000;
	private String FETCH_URL = "http://seva60plus.co.in/syncRitwik/api.php";
	private String INSERT_URL = "http://seva60plus.co.in/syncRitwik/api.php";
	static StringBuilder sb = null;

	//=============================  Fetch Users ===================================
	public ArrayList<User> fetcUsers() {

		String response = useGet(FETCH_URL);
		ArrayList<User> userList = parseResponse(response);
		return userList;

	}

	private String useGet(String url) {
		String result = "";
		HttpClient httpclient = new DefaultHttpClient();

		// Prepare a request object
		HttpGet httpget = new HttpGet(url);
		httpget.setHeader("API_key", "API_value");

		// Execute the request
		HttpResponse response;
		try {
			// Examine the response status
			response = httpclient.execute(httpget);

			// Get hold of the response entity
			HttpEntity entity = response.getEntity();
			// If the response does not enclose an entity, there is no need
			// to worry about connection release

			if (entity != null) {

				// A Simple JSON Response Read
				InputStream instream = entity.getContent();
				result = convertStreamToString(instream);

				// now you have the string representation of the HTML request
				instream.close();
			}

		} catch (Exception e) {

			e.printStackTrace();

		}
		return result;
	}

	private static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	private ArrayList<User> parseResponse(String response) {

		System.out.println("Response is " + response);
		ArrayList<User> userList = null;
		try {
			JSONObject joJsonObject = new JSONObject(response);
			boolean status = joJsonObject.optString("status").trim().equalsIgnoreCase("ok") ? true : false;
			if (status) {
				String data = joJsonObject.optString("data").trim();
				JSONArray jArray = new JSONArray(data);
				if (jArray.length() > 0) {
					userList = new ArrayList<User>();
					for (int i = 0; i < jArray.length(); i++) {
						JSONObject jsonObject = jArray.getJSONObject(i);
						User user = new User();
						user.setName(jsonObject.optString("fname"));
						user.setLname(jsonObject.optString("lname"));
						user.setPhone(jsonObject.optString("phone"));
						user.setEmail(jsonObject.optString("email"));
						userList.add(user);
					}
				}
			} else {

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return userList;
	}

	//=============================  Insert User ==============================================

	public void postUser(User user) throws Exception {

		Log.v("fname", "fname " + user.getName().trim());
		Log.v("fname", "phone " + user.getLname().trim());
		Log.v("fname", "phone " + user.getPhone().trim());
		Log.v("fname", "email " + user.getEmail().trim());
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("fname", "\'" + user.getName().trim() + "\'");
		requestMap.put("lname", "\'" + user.getLname().trim() + "\'");
		requestMap.put("phone", "\'" + user.getPhone().trim() + "\'");
		requestMap.put("email", "\'" + user.getEmail().trim() + "\'");
		JSONObject jsonObject = new JSONObject(requestMap);
		String response = postMethodWay_json(INSERT_URL, jsonObject.toString().trim());
		Log.d("Post User ", "Response on post inset data:\n" + response);

	}

	private static String postMethodWay_json(String hostName, String data) throws Exception {

		sb = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), CONNECTIONTIMEOUT);
		HttpConnectionParams.setSoTimeout(client.getParams(), CONNECTIONTIMEOUT);
		HttpResponse response;

		try {
			HttpPost post = new HttpPost(hostName);

			StringEntity se = new StringEntity(data);
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			post.setEntity(se);
			response = client.execute(post);
			int statuscode = response.getStatusLine().getStatusCode();

			/* Checking response */
			if (statuscode == 200) {

				if (response != null) {
					InputStream in = response.getEntity().getContent();
					// InputStream in = /* your InputStream */;
					InputStreamReader is = new InputStreamReader(in);

					BufferedReader br = new BufferedReader(is);
					String read = br.readLine();

					while (read != null) {
						// System.out.println(read);
						sb.append(read);
						read = br.readLine();

					}
					// the entity
					Log.i("tAG", "" + sb.toString());
					return sb.toString();
				}
			} else {
				// the entity
				System.out.println("Response code is:: " + response.getStatusLine().getStatusCode());
				sb.append(statuscode);
				return sb.toString();
			}

		} catch (Exception e) {
			e.printStackTrace();
			sb.append(e.getLocalizedMessage());
			Log.i("connection", "Cannot Estabilish Connection! " + e.getLocalizedMessage());
		}

		return sb.toString();
	}

}
