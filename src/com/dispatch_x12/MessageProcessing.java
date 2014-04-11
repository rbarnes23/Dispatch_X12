package com.dispatch_x12;

import java.io.IOException;
import java.text.ParseException;
//import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.edilibrary.EdiSupport;
import com.securitylibrary.SecurityLibrary;
import com.tokenlibrary.Token;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

public class MessageProcessing {
	private static String mTo = null;
	private static boolean mLoggedin;
	private static ArrayList<HashMap<String, String>> jsonMessagelist = new ArrayList<HashMap<String, String>>();
	// private static ArrayList<HashMap<String, String>> ediList = new
	// ArrayList<HashMap<String, String>>();
	private static List<Integer> sMemberIds = new ArrayList<Integer>();
	private static ArrayList<HashMap<String, String>> mMemberFindList = new ArrayList<HashMap<String, String>>();
	private static ArrayList<HashMap<String, String>> mGroupList = new ArrayList<HashMap<String, String>>();
	private static List<String> mDetailList = new ArrayList<String>();
	private static LinkedHashMap<String, HeaderInfo> mGroups = new LinkedHashMap<String, HeaderInfo>();
	private static ArrayList<HeaderInfo> groupList = new ArrayList<HeaderInfo>();
	private static String mSessions;
	private static int mGroupSelectedPosition;
	private static String mGroupSelected;

	public static String[] processMessage(String message) throws JSONException {
		JSONObject jMessage = new JSONObject(message);
		String type = jMessage.getString("type");
		String[] returnMessage = { "", "0" };
		if (type.contentEquals("getactivities")) {
		} else if (type.contentEquals("getfriends")) {
			getFriends(message);
		} else if (type.contentEquals("findmember")) {
			findMember(message);

		} else if (type.contentEquals("getpoints")) {

		} else if (type.contentEquals("getintervals")) {

		} else if (type.contentEquals("getmessage")) {

			returnMessage = getMessage(message);
		} else if (type.contentEquals("edi")) {

			returnMessage = getMessage(message);

		} else if (type.contentEquals("getsessions")) {
			returnMessage[0] = getSessions(message);

		} else if (type.contentEquals("login")) {
			getLogin(message);
		} else {
		}

		return returnMessage;
	}

	public static String getTransActionType(JSONObject jMessage)
			throws JSONException {

		String type = "0";
		String tranType = jMessage.optString("ST01");
		if (tranType.contentEquals("204") || tranType.contentEquals("997")
				|| tranType.contentEquals("990")
				|| tranType.contentEquals("210")) {
			type = tranType;
		}
		return type;
	}

	private static void getFriends(String _message) {
		String mFriends = _message;
		if (_message != null) {

			try {
				mFriends = _message;
				JSONObject jsonLoginData = new JSONObject(mFriends);
				JSONObject jResponse = jsonLoginData.getJSONObject("response");
				JSONArray jto = jsonLoginData.getJSONArray("to");

				mTo = jsonLoginData.getString("memberid");
				// Set first item to senderid
				if (jto.length() > 0) {
					jto.put(0, mTo);
				}
				for (int i = 0; i < jto.length(); i++) {
					if (mTo.contentEquals(jto.getString(i))) {
						// mTo = jnames.getString(i);
						sMemberIds.add(jto.getInt(i));

						/*
						 * mHandler.post(new Runnable() {
						 * 
						 * @Override public void run() { setToast(mTo.toString()
						 * + " just logged in.", LONG); } });
						 */
						break;
					}
				}

			} catch (JSONException e) {
				// Dont track JSON exception
			}

		}
	}

	private static void getLogin(String _message) throws JSONException {

		List<Integer> mMemberIds = new ArrayList<Integer>();
		String mMessage = null;
		if (_message != null) {
			mMessage = _message;

			JSONObject jsonLoginData = new JSONObject(mMessage);
			JSONArray jnames = jsonLoginData.getJSONArray("names");
			JSONArray jto = jsonLoginData.getJSONArray("to");

			mTo = jsonLoginData.getString("memberid");
			// Set first item to senderid
			if (jto.length() > 0) {
				jto.put(0, mTo);
			}
			for (int i = 0; i < jto.length(); i++) {
				if (mTo.contentEquals(jto.getString(i))) {
					mTo = jnames.getString(i);
					sMemberIds.add(jto.getInt(i));

					// Let the new user get the
					// latest drawing
					Token mMessageToken = new Token();
					// Send info to friends
					if (!mLoggedin) {
						String chatString;
						// chatString =
						// mMessageToken.createMessagetoFriendsToken(
						// mMemberid, drawView.getLatestDrawing(), false);
						// sendMessage(chatString);

					}

					// setToast(mTo.toString() + " just logged in.", LONG);
				}

				break;
			}
		}
	}

	private static String[] getMessage(String _Message) {
		String returnMessage[] = { "", "0", "" };
		String ivs = "12345678";
		String mId;

		if (_Message != null) {
			try {
				JSONObject jResponse = new JSONObject(_Message);
				String message = jResponse.getString("response");
				// String rate = jResponse.optString("RATES");
				mId = jResponse.getString("_id");
				if (jResponse.getString("type").contentEquals("edi")) {
					message = SecurityLibrary.decryptString(message,
							Constant.EDKEY, ivs);
				}
				EdiSupport Etest = new EdiSupport();
				jResponse = Etest.parseEdiStringtoJSON(message);
				// //////////////////////////////////////////////
				/*
				 * getStops(jResponse); JSONArray jArrayStops =
				 * jResponse.getJSONArray("STOPS");
				 * 
				 * for (int i = 0; i < jArrayStops.length(); i++) { JSONObject
				 * jObj = jArrayStops.getJSONObject(i); cDate =
				 * ediDate.parse(jObj.optString("G6202"));
				 * 
				 * returnMessage[0] += jObj.getString("S501") + ": " +
				 * jObj.getString("N102") + " " + jObj.getString("N301") + " " +
				 * jObj.getString("N401") + " " + jObj.getString("N402") + " " +
				 * jObj.getString("N403") + "\n" +
				 * Etest.getDeliveryStatus(jObj.optString("G6201")) + " " +
				 * newFormat.format(cDate) + " " + jObj.optString("G6204") + " "
				 * + jObj.optString("G6205") + "\n"; }
				 */
				// //////////////////////////////////////////////
				// returnMessage[0] =jArrayStops.toString();
				returnMessage[0] = jResponse.toString();
				returnMessage[1] = getTransActionType(jResponse);
				returnMessage[2] = mId;
				return returnMessage;
			} catch (JSONException e1) {
				// Different message types exist handle them
				e1.printStackTrace();
			}
			// If a regular message add to the Message list
			try {
				final JSONObject chatmessage = new JSONObject(_Message);

				// if
				// (chatmessage.optBoolean("isMsg")||chatmessage.optBoolean("MSG"))
				// {
				if (chatmessage.optBoolean("MSG")) {
					String mMessage = chatmessage.optString("response");
					if (mMessage.length() < 1) {
						mMessage = chatmessage.optString("message");
						if (chatmessage.optString("type").contentEquals("edi")) {
							mMessage = SecurityLibrary.decryptString(mMessage,
									Constant.EDKEY, ivs);
						}
					}
					// JSONArray mstat = chatmessage.optJSONArray("mStat");
					// JSONArray jRates = getRates(chatmessage);
					String rates = chatmessage.optString("RATES");// Returns
																	// empty
																	// string if
																	// their are
																	// no rates
																	// available
																	// for this
																	// customer
					// mTo = mstat.optJSONObject(0).getString("name");
					mTo = getTo(chatmessage);
					mId = chatmessage.optString("_id");
					String status = chatmessage.optString("status");
					// if (chatmessage.optString("names").length() > 0) {
					// String[] mNames = chatmessage.getString("names").split(
					// ",");
					// mTo = mNames[0].replace("\"", "").replace("[", "");
					// }
					HashMap<String, String> messageMap = new HashMap<String, String>();
					messageMap.put("fullname", mTo);
					messageMap.put("message", mMessage);
					messageMap.put("status", status);
					messageMap.put("_id", mId);
					messageMap.put("rates", rates);
					// Add to the top of the list as messages come in
					// jsonMessagelist.add(0, messageMap);
					jsonMessagelist.add(messageMap);
					// messageAdapter.notifyDataSetChanged();

				}
			} catch (JSONException e1) {
				// Different message types exist handle them
				e1.printStackTrace();
			}

		}
		return returnMessage;
	}

	public static ArrayList<HashMap<String, String>> getMessageList() {
		return jsonMessagelist;
	}

	public static HashMap<String, String> getLastMessage() {
		int lastindex = jsonMessagelist.size();
		return jsonMessagelist.get(lastindex);
	}

	public static JSONArray getStops(JSONObject jResponse) {
		JSONArray jArrayStops = new JSONArray();
		jArrayStops = jResponse.optJSONArray("STOPS");
		return jArrayStops;

	}

	public static JSONArray getRates(JSONObject jResponse) {
		JSONArray jArrayRates = new JSONArray();
		jArrayRates = jResponse.optJSONArray("RATES");
		if (jArrayRates == null)
			jArrayRates = new JSONArray();// Never send a null rates array
		return jArrayRates;

	}

	private static String getTo(JSONObject chatmessage) throws JSONException {
		String to = "UNKNOWN";
		JSONArray mstat = chatmessage.optJSONArray("mStat");
		String from = chatmessage.getString("memberid");
		if (mstat != null) {
			for (int i = 0; i < mstat.length(); i++) {
				if (mstat.getJSONObject(i).getString("id").contentEquals(from)) {
					to = mstat.optJSONObject(i).getString("name");
					break;
				}
			}
		}
		return to;

	}

	// Clear the memberlist so another search can be done
	public static ArrayList<HashMap<String, String>> getMemberList() {
		return mMemberFindList;
	}

	// Get the Latest member list
	public static ArrayList<HashMap<String, String>> getMemberList(
			String _Message) throws JSONException {
		clearMemberList();
		JSONObject chatmessage;
		if (_Message != null) {

			JSONObject jData = new JSONObject(_Message);
			String mResponse = jData.getString("response");
			String mTokenId = jData.getString("_id");
			JSONArray jMembers = new JSONArray(mResponse);
			mMemberFindList = (ArrayList<HashMap<String, String>>) JsonHelper
					.toMemberList(jMembers, mTokenId);
			ArrayList<HashMap<String, String>> mText = mMemberFindList;
		}
		return mMemberFindList;
	}

	private static void findMember(String _Message) throws JSONException {
		clearMemberList();
		JSONObject chatmessage;
		if (_Message != null) {

			JSONObject jData = new JSONObject(_Message);
			String mResponse = jData.getString("response");
			String mTokenId = jData.getString("_id");
			JSONArray jMembers = new JSONArray(mResponse);
			mMemberFindList = (ArrayList<HashMap<String, String>>) JsonHelper
					.toList(jMembers);
		}

	}

	private static String getSessions(String _message) throws JSONException {
		mGroupSelected = "{\"$oid\":\"519cb40e27543b5781285bab\"}";// AppSettings.getGroupId();
		mGroupSelected = "{\"$oid\":\"5180beff2754bf6fff38828c\"}";
		if (mGroupSelected == null) {
			mGroupSelected = "";
		}

		if (_message != null) {

			JSONObject chatmessage = new JSONObject(_message);
			String message = chatmessage.optString("message");
			JSONArray jSessionsArray = new JSONArray(message);
			int lensessions = jSessionsArray.length();
			for (int m = 0; m < lensessions; m++) {
				if (m == 0) {
					// JSONObject jmessage= jSessionsArray.getJSONObject(m);
					// String groupmessage=jmessage.getString("message");
					JSONArray jGroupArray = new JSONArray(
							jSessionsArray.getString(m));
					mSessions = jGroupArray.toString();
					String mMessage = "";// = chatmessage.optString("message");
					// JSONArray jGroupArray = new JSONArray(
					// chatmessage.getString("message"));

					int len = jGroupArray.length();
					for (int j = 0; j < len; j++) {
						String group = jGroupArray.getJSONObject(j).getString(
								"sessionName")
								+ " "
								+ jGroupArray.getJSONObject(j).getString(
										"sessionNo") + "  ";
						JSONObject jgroupobject = jGroupArray.getJSONObject(j);
						if (jgroupobject.getString("_id").contentEquals(
								mGroupSelected)) {
							mGroupSelectedPosition = j;
						}
						HashMap<String, String> fieldsMap = new HashMap<String, String>();
						Iterator iter = jgroupobject.keys();
						while (iter.hasNext()) {
							String key = (String) iter.next();
							String value = jgroupobject.getString(key);
							fieldsMap.put(key, value);
						}
						fieldsMap.put("group", group);
						// Add all group information to the grouplist
						mGroupList.add(fieldsMap);

						// Detail Array has info on members that belong to group
						JSONArray jDetailArray = new JSONArray(jGroupArray
								.getJSONObject(j).getString("members"));
						for (int k = 0; k < jDetailArray.length(); k++) {
							String detail = jDetailArray.getJSONObject(k)
									.optString("id")
									+ " "
									+ jDetailArray.getJSONObject(k).optString(
											"name") + "  ";
							mDetailList.add(detail);
							addMemberToGroup(group, detail);
						}
						mMessage += group;
					}
					// }
					/*
					 * mHandler.post(new Runnable() {
					 * 
					 * @Override public void run() {
					 * 
					 * // addItemstoGroupSpinner(); setMemberInfo(); } });
					 */} else if (m > 0) {
					JSONObject jmessages = new JSONObject(
							jSessionsArray.getString(m));
					getMessage(jmessages.toString());
				}
			}
		}
		return _message;
	}

	private static int addMemberToGroup(CharSequence _group, String _memberName) {

		int groupPosition = 0;
		HeaderInfo headerInfo = null;

		// check the hash map if the group already exists
		headerInfo = mGroups.get(_group);
		// add the group if doesn't exists
		if (headerInfo == null) {

			headerInfo = new HeaderInfo();
			headerInfo.setName(_group);
			mGroups.put((String) _group, headerInfo);
			groupList.add(headerInfo);

		}

		// get the children for the group
		ArrayList<DetailInfo> detailList = headerInfo.getDetailList();
		// size of the children list
		int listSize = detailList.size();
		// add to the counter
		listSize++;

		// create a new child and add that to the group
		DetailInfo detailInfo = new DetailInfo();
		detailInfo.setSequence(String.valueOf(listSize));
		detailInfo.setName(_memberName);
		detailList.add(detailInfo);
		headerInfo.setDetailList(detailList);

		// find the group position inside the list
		groupPosition = groupList.indexOf(headerInfo);
		return groupPosition;
	}

	// Clear the memberlist so another search can be done
	private static int clearMemberList() {
		mMemberFindList.clear();
		return mMemberFindList.size();
	}

	protected static String getSessions() {
		return mSessions;
	}

	public static int[] searchAddress(Context mContext, String _address) {
		// GeoPoint mGeoPoint=null;
		int[] lonlat = { 0, 0 };
		Geocoder coder = new Geocoder(mContext, Locale.ENGLISH);
		List<Address> address = null;
		try {
			address = coder.getFromLocationName(_address, 1);
			if (address != null) {
				for (int i = 0; i < 1; i++) {
					Address location = address.get(i);
					lonlat[0] = (int) (location.getLatitude() * 1E6);
					lonlat[1] = (int) (location.getLongitude() * 1E6);
					// GeoPoint mGeoPoint = new GeoPoint(
					// (int) (location.getLatitude() * 1E6),
					// (int) (location.getLongitude() * 1E6));
				}
			}
		} catch (Exception e) {
			lonlat = searchNomatimAddress(mContext, _address);
			return lonlat;
		}
		// return mGeoPoint;
		return lonlat;
	}

	public static int[] searchNomatimAddress(Context mContext, String _address) {
		int[] lonlat = { 0, 0 };
		GeocoderNominatim coder = new GeocoderNominatim(mContext);
		coder.setService(Constant.MAPQUEST_SERVICE_URL);
		List<Address> address;
		try {
			address = coder.getFromLocationName(_address, 5);
			if (address.size() == 0) {
				return lonlat;
			}
			for (int i = 0; i < 1; i++) {
				Address location = address.get(i);
				lonlat[0] = (int) (location.getLatitude() * 1E6);
				lonlat[1] = (int) (location.getLongitude() * 1E6);
			}
		} catch (Exception e) {
			return lonlat;
		}
		return lonlat;
	}

	public static class verifyAddress extends
			AsyncTask<Object, Void, List<Address>> {

		@Override
		protected List<Address> doInBackground(Object... params) {
			Context context = (Context) params[1];
			String fullAddress = (String) params[0];
			Geocoder coder = new Geocoder(context, Locale.getDefault());
			List<Address> addressList = null;
			try {
				addressList = coder.getFromLocationName(fullAddress, 1);
			} catch (IOException e) {
				GeocoderNominatim nomatimCoder = new GeocoderNominatim(context);
				nomatimCoder.setService(Constant.MAPQUEST_SERVICE_URL);
				try {
					addressList = nomatimCoder.getFromLocationName(fullAddress,
							1);
				} catch (IOException e1) {
					return addressList;
				}
			}

			return addressList;
		}

		@Override
		protected void onPostExecute(List<Address> addressList) {
			super.onPostExecute(addressList);
		}
	}

	public static class verifyAddressNomatim extends
			AsyncTask<Object, Void, List<Address>> {

		@Override
		protected List<Address> doInBackground(Object... params) {
			Context context = (Context) params[1];
			String fullAddress = (String) params[0];
			List<Address> addressList = null;

			GeocoderNominatim nomatimCoder = new GeocoderNominatim(context,
					Locale.getDefault());
			nomatimCoder.setService(Constant.MAPQUEST_SERVICE_URL);
			try {
				addressList = nomatimCoder.getFromLocationName(fullAddress, 1);
			} catch (IOException e1) {
				addressList = getGoogleAddress(context, fullAddress);
			}
			if (addressList.size() == 0) {
				addressList = getGoogleAddress(context, fullAddress);
			}
			return addressList;
		}

		@Override
		protected void onPostExecute(List<Address> addressList) {
			super.onPostExecute(addressList);
		}
	}

	private static List<Address> getGoogleAddress(Context context,
			String fullAddress) {
		Geocoder coder = new Geocoder(context, Locale.getDefault());
		List<Address> addressList = null;
		try {
			addressList = coder.getFromLocationName(fullAddress, 1);
		} catch (IOException e) {
			// Dont do anything
			e.printStackTrace();
		}
		return addressList;
	}

	private static String formatStringToDate(String datetoFormat) {
		String reformattedStr = "No Date";
		try {
			reformattedStr = Constant.TODATEFORMAT.format(Constant.EDIDATE
					.parse(datetoFormat));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return reformattedStr;
	}

	public static String createStopInfo(String passedMessage, String _id,
			Context context) {
		String stopInfo = "";
		EdiSupport ediSupport = new EdiSupport();
		JSONObject jResult = ediSupport.parseEdiStringtoJSON(passedMessage);
		String message = jResult.toString();
		JSONArray jArray = jResult.optJSONArray("STOPS");
		for (int i = 0; i < jArray.length(); i++) {
			JSONObject jObj = jArray.optJSONObject(i);
			// stopInfo+="Stop No: "+
			// Integer.toString(i+1)+"  "+jObj.optString("G6202")+"  "+jObj.optString("S502").replaceAll("~",
			// "").trim()+"\n";

			stopInfo += "Stop No: " + Integer.toString(i + 1) + "  "
					+ formatStringToDate(jObj.optString("G6202")) + "  "
					+ jObj.optString("S502").replaceAll("~", "").trim() + "\n";
			// stopInfo+=_id+"\n";
			stopInfo += jObj.optString("N102") + "\n";
			stopInfo += jObj.optString("N401") + ", " + jObj.optString("N402")
					+ "\n\n";
		}
		if (stopInfo.length() < 1) {
			stopInfo = passedMessage;
		}// stopInfo = createStops(message,(String) _id, context,false);
		return stopInfo;
	}

	public static ArrayList<HashMap<String, String>> createStops(
			String passedMessage, String _id, Context context,
			boolean includeGeopoints) throws JSONException {
		JSONObject jResult;
		JSONArray stops;
		String resultString = null;
		ArrayList<HashMap<String, String>> jsonStopList = new ArrayList<HashMap<String, String>>();

		try {
			jResult = new JSONObject(passedMessage);
			String rates = jResult.optString("rates");
			resultString = jResult.getString("message");
		} catch (JSONException e) {
			resultString = passedMessage;
		}

		// stops = new JSONArray(resultString);
		jResult = new JSONObject(passedMessage);
		stops = getStops(jResult);// get an array of all the stops
		for (int i = 0; i < stops.length(); i++) {
			JSONObject jStop = stops.getJSONObject(i);
			HashMap<String, String> stopMap = new HashMap<String, String>();
			stopMap.put("_id", _id);
			stopMap.put("identificationNo", jStop.getString("N104"));

			stopMap.put(
					"fullname",
					context.getString(R.string.intervals)
							+ jStop.optInt("S501") + " Type: "
							+ jStop.optString("S502") + " Date of Appt: "
							+ jStop.optString("G6202"));
			String address = jStop.optString("N301").replaceAll("~", "") + ", "
					+ jStop.optString("N401") + ", " + jStop.optString("N402")
					+ " " + jStop.optString("N403");
			int[] location = null;

			if (includeGeopoints) {
				address.replaceAll("~", "");
				stopMap.put("address",address);
				location = searchAddress(context, address);
			}
			// if (location != null) {
			address = jStop.optString("N102") + "\n" + address;
			// address += " Lon: " + Integer.toString(location[1]) +
			// " Lat: "
			// + Integer.toString(location[0]);
			// }
			// Add the OID Segment
			address += "\nOrder ID: " + jStop.optString("OID01") + ":"
					+ jStop.optString("OID02") + ":" + jStop.optString("OID03")
					+ ":" + jStop.optString("OID04") + ":"
					+ jStop.optString("OID05") + ":" + jStop.optString("OID06")
					+ ":" + jStop.optString("OID07") + ":"
					+ jStop.optString("OID08");
			address += "\nContact Info: " + jStop.optString("G6101") + ":"
					+ jStop.optString("G6102") + ":" + jStop.optString("G6103")
					+ ":" + jStop.optString("G6104");
			JSONArray jDesc = jStop.optJSONArray("DESC");
			String desc = "\nDescriptions:\n";

			for (int j = 0; j < jDesc.length(); j++) {
				JSONObject jDescObj = jDesc.getJSONObject(j);
				desc += jDescObj.optString("L1101") + " "
						+ jDescObj.optString("L1102") + "\n";
			}
			stopMap.put("message", address + desc);
			if (location != null) {
				stopMap.put("lon", Integer.toString(location[1]));
				stopMap.put("lat", Integer.toString(location[0]));
			} else {
				stopMap.put("lon", Integer.toString(0));
				stopMap.put("lat", Integer.toString(0));

			}

			jsonStopList.add(i, stopMap);
		}

		return jsonStopList;
	}

	// public static Message createStops2(String passedMessage, String _id,
	// Context context) throws JSONException {
	// JSONObject jResult;
	// JSONArray stops;
	// String resultString = null;
	// ArrayList<HashMap<String, String>> jsonStopList = new
	// ArrayList<HashMap<String, String>>();
	//
	// try {
	// jResult = new JSONObject(passedMessage);
	// resultString = jResult.getString("message");
	// } catch (JSONException e) {
	// resultString = passedMessage;
	// }
	//
	// // stops = new JSONArray(resultString);
	// jResult = new JSONObject(passedMessage);
	// stops = getStops(jResult);// get an array of all the stops
	// for (int i = 0; i < stops.length(); i++) {
	// JSONObject jStop = stops.getJSONObject(i);
	// HashMap<String, String> stopMap = new HashMap<String, String>();
	// stopMap.put("_id", _id);
	// stopMap.put("identificationNo", jStop.getString("N104"));
	// stopMap.put(
	// "fullname",
	// context.getString(R.string.intervals)
	// + jStop.optInt("S501") + " Type: "
	// + jStop.optString("S502") + " Date of Appt: "
	// + jStop.optString("G6202"));
	// String address = jStop.optString("N301").replaceAll("~", "") + ", "
	// + jStop.optString("N401") + ", " + jStop.optString("N402")
	// + " " + jStop.optString("N403");
	// int[] location = searchAddress(context, address);
	// if (location != null) {
	// address = jStop.optString("N102") + "\n" + address;
	// // address += " Lon: " + Integer.toString(location[1]) +
	// // " Lat: "
	// // + Integer.toString(location[0]);
	// }
	// // Add the OID Segment
	// address += "\nOrder ID: " + jStop.optString("OID01") + ":"
	// + jStop.optString("OID02") + ":" + jStop.optString("OID03")
	// + ":" + jStop.optString("OID04") + ":"
	// + jStop.optString("OID05") + ":" + jStop.optString("OID06")
	// + ":" + jStop.optString("OID07") + ":"
	// + jStop.optString("OID08");
	// address += "\nContact Info: " + jStop.optString("G6101") + ":"
	// + jStop.optString("G6102") + ":" + jStop.optString("G6103")
	// + ":" + jStop.optString("G6104");
	// JSONArray jDesc = jStop.optJSONArray("DESC");
	// String desc = "\nDescriptions:\n";
	//
	// for (int j = 0; j < jDesc.length(); j++) {
	// JSONObject jDescObj = jDesc.getJSONObject(j);
	// desc += jDescObj.optString("L1101") + " "
	// + jDescObj.optString("L1102") + "\n";
	// }
	// stopMap.put("message", address + desc);
	// if (location != null) {
	// stopMap.put("lon", Integer.toString(location[1]));
	// stopMap.put("lat", Integer.toString(location[0]));
	// } else {
	// stopMap.put("lon", Integer.toString(0));
	// stopMap.put("lat", Integer.toString(0));
	//
	// }
	//
	// jsonStopList.add(i, stopMap);
	// }
	// Message msg = MainActivity.mUiHandler.obtainMessage();
	//
	// Bundle bundle = new Bundle();
	// bundle.putParcelableArrayList("message",
	// (ArrayList<? extends Parcelable>) jsonStopList);
	// msg.setData(bundle);
	// return msg;
	// }
}
