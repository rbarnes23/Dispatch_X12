package com.dispatch_x12;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.securitylibrary.SecurityLibrary;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;

public class AppSettings extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {
	private static final String login = "login_settings";
	private static final String login_memberid = "login_memberid";
	private static final String changepassword = "changepassword";
	private static final String networkid = "networkid";
	private static final String networkingonoff = "networkingonoff";
	private static final String selectedgroupid = "selectedgroupid";
	private static final String companyid = "companyid";
	private static final String companyname = "companyname";
	private static final String routeprovider = "routeprovider";
	private static final String mapprovider = "mapprovider";
	private static final String zoomlevel = "zoomlevel";
	private static final String lastlocation = "lastlocation";
	private static final String uom = "uom";
	private static Boolean sChanged = false;
	private static Context sContext = null;
	private static TextView sTextLabel;// = new TextView(null);
	private static String sMessage;

	public static Boolean isChanged() {
		return sChanged;
	}

	// getMemberid used the new generated id paradigm
	public static String getMemberid() {
		String memberid = getMemberid(sContext);
		if (memberid.contentEquals("-1") || memberid.contentEquals("0")) {
			memberid = SecurityLibrary.generateUniqueId(10);
			setMemberid(sContext, memberid);
		}

		return memberid;
	}

	public static int getmemberid(String email) {
		int memberid = 0;

		if (!checkEmail(email)) {
			sTextLabel.setText(R.string.invalidemail);
			sMessage = (String) sTextLabel.getText();
			memberid = 0;
			Toast.makeText(sContext, sMessage, Toast.LENGTH_SHORT).show();
			return memberid;
		}
		try {
			String[] params = new String[2];
			params[0] = "http://mapmymotion.com/artytheartistgetmemberid.php";
			params[1] = email;
			memberid = new MemberidTask().execute(params).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (memberid > 0) {
			setMemberid(sContext, Integer.toString(memberid));
			setEmailAddress(sContext, email);

			sTextLabel.setText(R.string.memberidretrieved);
			sMessage = sTextLabel.getText() + " " + memberid;
		} else {
			sTextLabel.setText(R.string.memberidnotretrieved);
			sMessage = (String) sTextLabel.getText();
		}

		return memberid;
	}

	private static boolean checkEmail(String email) {
		return Constant.EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	}

	public static int newUser(String email, String p1, String p2, String fname,
			String lname) {
		int memberid = 0;

		if (!checkEmail(email)) {
			Toast.makeText(sContext, R.string.invalidemail, Toast.LENGTH_SHORT)
					.show();
			memberid = 0;
			return memberid;
		}

		// If any value is null return 0
		if (p1 == null || p2 == null || fname == null || lname == null) {
			sTextLabel.setText(R.string.invalidnewuser);
			sMessage = (String) sTextLabel.getText();
			Toast.makeText(sContext, R.string.invalidnewuser,
					Toast.LENGTH_SHORT).show();

			return memberid;
		}
		try {
			String[] params = new String[6];
			params[0] = "http://mapmymotion.com/artytheartistnewmember.php";
			params[1] = email;
			params[2] = p1;
			params[3] = p2;
			params[4] = fname;
			params[5] = lname;
			memberid = new MemberidTask().execute(params).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (memberid > -1) {
			setMemberid(sContext, Integer.toString(memberid));
			setEmailAddress(sContext, email);
			sTextLabel.setText(R.string.memberidretrieved);
			sMessage = (String) sTextLabel.getText();
			Toast.makeText(sContext, sMessage, Toast.LENGTH_SHORT).show();
		} else {
			sTextLabel.setText(R.string.memberidnotretrieved);
			sMessage = (String) sTextLabel.getText();
		}

		return memberid;
	}

	public static int changePassword(String email, String p1, String p2) {
		int memberid = 0;

		// String email = AppSettings.login;
		if (email == null) {
			return memberid;
		}

		// Wait for keyboard to disappear before moving from preferences
		Toast.makeText(sContext, R.string.waitforconfirmation,
				Toast.LENGTH_SHORT).show();
		try {
			String[] params = new String[4];
			params[0] = "http://mapmymotion.com/artytheartistchangepassword.php";
			params[1] = email;// emailaddress
			params[2] = p1;// password
			params[3] = "1";// sendmail
			memberid = new MemberidTask().execute(params).get();
		} catch (InterruptedException e) {
			// Never interrupted
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return memberid;
	}

	public static String getFriendsArray(String aMemberid) {
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("amemberid", aMemberid));
		InputStream is = null;

		// http post
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"http://mapmymotion.com/artytheartistgetfriendsarray.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);

			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		} catch (Exception e) {

			// Log.e("log_tag", "Error in http connection " + e.toString());
		}

		String result = null;
		// convert response to string
		try {

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			is.close();

			result = sb.toString();
		} catch (Exception e) {

			// Log.e("log_tag", "Error converting result " + e.toString());
			result = "Unable to retrieve List of Friends ";
		}
		return result;
	}

	public static String getTemplateList() {
		InputStream is = null;

		// http post
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"http://mapmymotion.com/getthemes.php");
			HttpResponse response = httpclient.execute(httppost);

			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		} catch (Exception e) {

			// Log.e("log_tag", "Error in http connection " + e.toString());
		}

		String result = null;
		// convert response to string
		try {

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			is.close();

			result = sb.toString();
		} catch (Exception e) {

			// Log.e("log_tag", "Error converting result " + e.toString());
			result = "Unable to retrieve Templates";
		}
		return result;
	}

	public static String getEmailAddress(Context context) {
		return getString(context, AppSettings.login);
	}

	private static boolean getBoolean(Context context, String tag) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getBoolean(tag, false);
	}

	private static String getString(Context context, String tag) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(tag, null);
	}

	public static String getMemberid(Context context) {
		return getString(context, AppSettings.login_memberid);
	}

	public static void setMemberid(Context context, String limit) {
		putString(context, AppSettings.login_memberid, limit);
	}

	public static boolean getChangePass() {
		return getBoolean(AppSettings.changepassword);
	}

	public static void setChangePass(Boolean aBool) {
		putBoolean(AppSettings.changepassword, aBool);
	}

	private static boolean getBoolean(String tag) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(sContext);
		return pref.getBoolean(tag, false);
	}

	public static String getGroupId() {
		return getString(sContext, AppSettings.selectedgroupid);
	}

	public static void setGroupId(String groupId) {
		putString(sContext, AppSettings.selectedgroupid, groupId);
	}

	public static String getCompanyId() {
		return getString(sContext, AppSettings.companyid);
	}

	public static void setCompanyId(String companyId) {
		putString(sContext, AppSettings.companyid, companyId);
	}

	public static String getCompanyName() {
		return getString(sContext, AppSettings.companyname);
	}

	public static void setCompanyName(String companyName) {
		putString(sContext, AppSettings.companyname, companyName);
	}

	public static String getRouteProvider() {
		return getString(sContext, AppSettings.routeprovider);
	}

	public static void setRouteProvider(String routeprovider) {
		putString(sContext, AppSettings.routeprovider, routeprovider);
	}

	public static String getMapProvider() {
		return getString(sContext, AppSettings.mapprovider);
	}

	public static void setMapProvider(String mapprovider) {
		putString(sContext, AppSettings.mapprovider, mapprovider);
	}

	public static String getZoomLevel() {
		return getString(sContext, AppSettings.zoomlevel);
	}

//	public static void setDefaultZoomLevel(int defaultzoomlevel) {
//		putInt(sContext, AppSettings.defaultzoomlevel, defaultzoomlevel);
//	}

	public static int getLastLocation() {
		return getInt(sContext, AppSettings.lastlocation);
	}

	public static void setLastLocation(int lastlocation) {
		putInt(sContext, AppSettings.lastlocation, lastlocation);
	}

	public static String getNetworkid() {
		return getString(sContext, AppSettings.networkid);
	}

	public static void setNetworkid(Context context, String limit) {
		putString(context, AppSettings.networkid, limit);
	}

	public static boolean getNetworkStatus() {
		return getBoolean(sContext, AppSettings.networkingonoff);
	}

	public static void setNetworkStatus(String tag, Boolean aNS) {
		putBoolean(AppSettings.networkingonoff, aNS);
	}

	public static void setEmailAddress(Context context, String limit) {
		putString(context, AppSettings.login, limit);
	}

	public static String getUom() {
		return getString(sContext, AppSettings.uom);
	}

	public static void setUom(int lastlocation) {
		putString(sContext, AppSettings.uom, uom);
	}
	
	
	private static int getInt(Context context, String tag) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getInt(tag, 0);
	}

	private static float getFloat(Context context, String tag) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getFloat(tag, 0);
	}

	public static void putInt(Context context, String tag, int value) {
		// SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = pref.edit();

		editor.putInt(tag, value);
		editor.commit();
	}

	public static void putFloat(Context context, String tag, float value) {
		// SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = pref.edit();

		editor.putFloat(tag, value);
		editor.commit();
	}

	public static void putString(Context context, String tag, String value) {
		// SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = pref.edit();

		editor.putString(tag, value);
		editor.commit();
	}

	public static void putBoolean(String tag, Boolean value) {
		// SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(sContext);
		SharedPreferences.Editor editor = pref.edit();

		editor.putBoolean(tag, value);
		editor.commit();
	}

	public static String getHMS(long s) {
		int HH = (int) (s / 3600);
		int MM = (int) ((s - (HH * 3600)) / 60);
		int SS = (int) (s - (HH * 3600) - (MM * 60));
		sTextLabel.setText(R.string.durationis);
		return sTextLabel.getText()
				+ String.format(" %02d:%02d:%02d", HH, MM, SS);
	}

	public static String getMS(double m) {
		int MM = (int) m;
		int SS = (int) (m % 1 * 60);
		sTextLabel.setText(R.string.pacetext);
		return sTextLabel.getText() + String.format(" %02d:%02d", MM, SS);
	}

	public void setDefault() {
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// This has to go first...do not move
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// Restore UI state from the savedInstanceState.
		// This bundle has also been passed to onCreate.
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
		sChanged = true;
		if (key.equals("login_settings")) {
			int mId = getmemberid(getEmailAddress(sContext));
			setMemberid(sContext, Integer.toString(mId));
		}

		SharedPreferences.Editor editor = pref.edit();
		editor.commit();
		onContentChanged();
		// TODO Auto-generated method stub
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Set up a listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Unregister the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);

	}

	@Override
	public void onDestroy() {
		try {
			this.finalize();
		} catch (Throwable e) {
		}
		super.onDestroy();
	}

	public static void setContext(Context context) {
		sContext = context;
		sTextLabel = new TextView(context);

	}

}

class MemberidTask extends AsyncTask<String, Void, Integer> {
	int myid = -1;

	@Override
	protected Integer doInBackground(String... params) {

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		String url = params[0];
		if (url.contentEquals("http://mapmymotion.com/artytheartistgetmemberid.php")) {
			nameValuePairs.add(new BasicNameValuePair("email", params[1]));
		} else if (url
				.contentEquals("http://mapmymotion.com/artytheartistnewmember.php")) {
			nameValuePairs.add(new BasicNameValuePair("email", params[1]));
			nameValuePairs.add(new BasicNameValuePair("p1", params[2]));
			nameValuePairs.add(new BasicNameValuePair("p2", params[3]));
			nameValuePairs.add(new BasicNameValuePair("fname", params[4]));
			nameValuePairs.add(new BasicNameValuePair("lname", params[5]));
		} else if (url
				.contentEquals("http://mapmymotion.com/artytheartistchangepassword.php")) {
			nameValuePairs.add(new BasicNameValuePair("email", params[1]));
			nameValuePairs.add(new BasicNameValuePair("pass", params[2]));
			nameValuePairs.add(new BasicNameValuePair("sendmail", params[3]));

		}
		InputStream is = null;
		// http post
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);

			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		} catch (Exception e) {
			// Do nothing as memberid will be -1
		}

		String result = null;
		// convert response to string
		try {

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();

			result = sb.toString();

		} catch (Exception e) {
			// Do nothing as memberid will be -1
		}
		// parse json data
		try {
			/*
			 * if (result.contentEquals(null)) { return myid; }
			 */
			JSONArray jArray = new JSONArray(result);
			JSONObject json_data;
			json_data = jArray.getJSONObject(0);
			myid = json_data.getInt("memberid");
		} catch (JSONException e) {
			myid = -1;
		}
		return myid;
	}

}

class FriendsTask extends AsyncTask<String, Void, Integer> {
	int myid = -1;

	@Override
	protected Integer doInBackground(String... params) {

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("email", params[0]));
		InputStream is = null;
		// http post
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"http://mapmymotion.com/artytheartistgetmemberid.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);

			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		} catch (Exception e) {
			// Do nothing as memberid will be -1
		}

		String result = null;
		// convert response to string
		try {

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();

			result = sb.toString();

		} catch (Exception e) {
			// Do nothing as memberid will be -1
		}
		// parse json data
		try {
			/*
			 * if (result.contentEquals(null)) { return myid; }
			 */
			JSONArray jArray = new JSONArray(result);
			JSONObject json_data;
			json_data = jArray.getJSONObject(0);
			myid = json_data.getInt("memberid");
		} catch (JSONException e) {
			myid = -1;
		}
		return myid;
	}

}
