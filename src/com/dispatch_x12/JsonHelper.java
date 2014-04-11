package com.dispatch_x12;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class JsonHelper {
	public static Object toJSON(Object object) throws JSONException {
		if (object instanceof Map) {
			JSONObject json = new JSONObject();
			Map map = (Map) object;
			for (Object key : map.keySet()) {
				json.put(key.toString(), toJSON(map.get(key)));
			}
			return json;
		} else if (object instanceof Iterable) {
			JSONArray json = new JSONArray();
			for (Object value : ((Iterable) object)) {
				json.put(value);
			}
			return json;
		} else {
			return object;
		}
	}

	public static boolean isEmptyObject(JSONObject object) {
		return object.names() == null;
	}

	public static Map<String, Object> getMap(JSONObject object, String key)
			throws JSONException {
		return toMap(object.getJSONObject(key));
	}

	public static Map<String, Object> toMap(JSONObject object)
			throws JSONException {
		Map<String, Object> map = new HashMap();
		Iterator keys = object.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			map.put(key, fromJson(object.get(key)));
		}
		return map;
	}

	public static List toList(JSONArray array) throws JSONException {
		List list = new ArrayList();
		for (int i = 0; i < array.length(); i++) {
			list.add(fromJson(array.get(i)));
		}
		return list;
	}

	private static Object fromJson(Object json) throws JSONException {
		if (json == JSONObject.NULL) {
			return null;
		} else if (json instanceof JSONObject) {
			return toMap((JSONObject) json);
		} else if (json instanceof JSONArray) {
			return toList((JSONArray) json);
		} else {
			return json;
		}
	}

	// This is a temporary fix to toList as many of the fields do not exist for
	// each record as it is now
	public static List<HashMap<String, String>> toMemberList(
			JSONArray jMembers, String tokenId) throws JSONException {
		JSONObject chatmessage;
		List<HashMap<String, String>> mMemberFindList = new ArrayList<HashMap<String, String>>();
		for (int j = 0; j < jMembers.length(); j++) {
			chatmessage = jMembers.getJSONObject(j);
			HashMap<String, String> memberMap = new HashMap<String, String>();
			memberMap.put("tokenid", tokenId);
			memberMap.put("id", chatmessage.optString("id"));
			memberMap.put("firstname", chatmessage.optString("firstname"));
			memberMap.put("lastname", chatmessage.optString("lastname"));
			memberMap.put("knownas", chatmessage.optString("knownas"));
			memberMap.put("company_id", chatmessage.optString("company_id"));
			memberMap.put("classification",
					chatmessage.optString("classification"));
			memberMap.put("payinfo", chatmessage.optString("payinfo"));
			memberMap.put("phonenos", chatmessage.optString("phonenos"));
			memberMap.put("email", chatmessage.optString("email"));
			memberMap.put("memberdate", chatmessage.optString("memberdate"));
			mMemberFindList.add(memberMap);
		}
		return mMemberFindList;

	}
}