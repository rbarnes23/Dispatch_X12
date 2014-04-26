package com.dispatch_x12;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class X204Entry extends ListFragment {
	ViewGroup root;
	Context mContext;
	private LayoutInflater inflater;
	private MergeAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		root = (ViewGroup) inflater.inflate(R.layout.vehicleentry, null);

		init(root);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return root;
	}

	private void init(ViewGroup root) {
		addAdapters(3);
	}

	private void addAdapters(int noOfStops) {
		setListAdapter(null);
		adapter = null;
		adapter = new MergeAdapter();
		for (int i = 0; i < noOfStops; i++) {
			adapter.addView(buildSeparator("Registration Information"), false);
			adapter.addAdapter(buildVehicleRegistrationList());
			adapter.addView(buildSeparator("Insurance Documents"), false);
			adapter.addAdapter(buildInsuranceList());
		}
		setListAdapter(adapter);
	}

	public JSONArray createX204EntryMessage() throws JSONException {
		// Go thru adapters and build the JSONObject
		JSONArray jStopArray = new JSONArray();
		JSONObject jStop = new JSONObject();

		int adapterCount = adapter.getCount();
		// go thru the adapters and call the saveToJSON
		for (int count = 0; count < adapterCount; count++) {
			ListAdapter thisAdapter = adapter.getAdapter(count);
			if (thisAdapter.getClass().getSimpleName()
					.contentEquals("VehicleRegistrationAdapter")) {
				// If a stop exists put it in here...probably a better way to
				// handle this, but adapter count gives total rows of all
				// adapters
				if (jStop.length() > 0) {
					jStopArray.put(jStop);
					jStop = new JSONObject();
				}

				VehicleRegistrationAdapter vehicleRegistrationAdapter = (VehicleRegistrationAdapter) thisAdapter;
				jStop = vehicleRegistrationAdapter.saveToJSON(jStop);
			} else if (thisAdapter.getClass().getSimpleName()
					.contentEquals("InsuranceAdapter")) {
				InsuranceAdapter insuranceAdapter = (InsuranceAdapter) thisAdapter;
				jStop = insuranceAdapter.saveToJSON(jStop);
			}
		}

		// Add the last stop
		if (jStop.length() > 0) {
			jStopArray.put(jStop);
			jStop = new JSONObject();
		}

		return jStopArray;
	}

	private void setAdapterList(JSONObject jCompany) throws JSONException {
		// Go thru adapters and build the JSONObject
		JSONArray jArray = jCompany.getJSONArray("STOPS");
		int noOfRows = jArray.length();
		addAdapters(noOfRows);
		int row = 0;
		JSONObject jX204 = null;
		int rowCount = adapter.getCount();
		// go thru the adapters and call the saveToJSON
		for (int count = 0; count < rowCount; count++) {
			ListAdapter thisAdapter = adapter.getAdapter(count);
			if (thisAdapter == null) {
				continue;
			}
			if (thisAdapter.getClass().getSimpleName()
					.contentEquals("VehicleRegistrationAdapter")) {
				jX204 = jArray.getJSONObject(row);
				row++;
				VehicleRegistrationAdapter vehicleRegistrationAdapter = (VehicleRegistrationAdapter) thisAdapter;

				vehicleRegistrationAdapter.setRow(jX204);
			} else if (thisAdapter.getClass().getSimpleName()
					.contentEquals("InsuranceAdapter")) {
				InsuranceAdapter insuranceAdapter = (InsuranceAdapter) thisAdapter;
				JSONArray array = jX204.getJSONArray("Insurance");
				ArrayList<HashMap<String, String>> arrayList = (ArrayList<HashMap<String, String>>) JsonHelper
						.toList(array);
				insuranceAdapter.setRow(arrayList);
			}
			rowCount = adapter.getCount();	
		}
	}

	void setMessage(JSONObject msg) {
		if (msg.optInt("entryType") == 1) {
			try {
				setAdapterList(msg);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Take the message and put it into all the different adapters
		} else {
			try {
				JSONArray test = createX204EntryMessage();
				msg = new JSONObject();
				msg.put("STOPS", test);
				Message msgToSend = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("message", msg.toString());
				msgToSend.setData(bundle);
				msgToSend.what = Constant.VEHICLEDATA;
				sendMessage(msgToSend);

				MainActivity.setToast(msg.toString(), Toast.LENGTH_LONG);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// send message to service
	public void sendMessage(Message msg) {
		// only we need a handler to send message to any component.
		// here we will get the handler from the service first, then
		// we will send a message to the service.

		if (null != ChatService.mServiceHandler) {
			ChatService.mServiceHandler.sendMessage(msg);
		}
	}

	private VehicleRegistrationAdapter buildVehicleRegistrationList() {
		ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
		VehicleRegistrationAdapter adapter = new VehicleRegistrationAdapter(
				mContext, arrayList);
		adapter.addRow();
		return (adapter);
	}

	private InsuranceAdapter buildInsuranceList() {
		ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
		InsuranceAdapter adapter = new InsuranceAdapter(mContext, arrayList);
		adapter.addRow();
		return (adapter);
	}

	private View buildSeparator(String heading) {
		View header = inflater.inflate(R.layout.separator, null);
		TextView separatorText = (TextView) header.findViewById(R.id.separator);
		separatorText.setBackgroundColor(getResources().getColor(
				R.color.skyblue));
		((TextView) separatorText).setText(heading);
		return (header);
	}

}