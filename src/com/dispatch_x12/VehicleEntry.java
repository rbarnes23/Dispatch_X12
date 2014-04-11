package com.dispatch_x12;

import java.util.ArrayList;
import java.util.HashMap;

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

public class VehicleEntry extends ListFragment {
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

	void init(ViewGroup root) {

		adapter = new MergeAdapter();
		adapter.addView(buildSeparator("Registration Information"), false);
		adapter.addAdapter(buildVehicleRegistrationList());
		adapter.addView(buildSeparator("Insurance Documents"), false);
		adapter.addAdapter(buildInsuranceList());

		setListAdapter(adapter);
	}

	public JSONObject createVehicleEntryMessage(JSONObject jVehicle)
			throws JSONException {
		// Go thru adapters and build the JSONObject

		int adapterCount = adapter.getCount();
		// go thru the adapters and call the saveToJSON
		for (int count = 0; count < adapterCount; count++) {
			ListAdapter thisAdapter = adapter.getAdapter(count);
			if (thisAdapter.getClass().getSimpleName()
					.contentEquals("VehicleRegistrationAdapter")) {
				VehicleRegistrationAdapter vehicleRegistrationAdapter = (VehicleRegistrationAdapter) thisAdapter;
				jVehicle = vehicleRegistrationAdapter.saveToJSON(jVehicle);
			}
			 else
				 if(thisAdapter.getClass().getSimpleName().contentEquals("InsuranceAdapter")){
				 InsuranceAdapter insuranceAdapter=(InsuranceAdapter) thisAdapter;
				 jVehicle=insuranceAdapter.saveToJSON(jVehicle);
				 }

		}
		return jVehicle;
	}

	void setMessage(JSONObject msg) {
		try {
			msg = createVehicleEntryMessage(msg);
			MainActivity.setToast(msg.toString(), Toast.LENGTH_LONG);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		ArrayList<HashMap<String, CharSequence>> arrayList = new ArrayList<HashMap<String, CharSequence>>();
		VehicleRegistrationAdapter adapter = new VehicleRegistrationAdapter(mContext, arrayList);
		adapter.addRow();
		return (adapter);
	}
	
	private InsuranceAdapter buildInsuranceList() {
		ArrayList<HashMap<String, CharSequence>> arrayList = new ArrayList<HashMap<String, CharSequence>>();
		InsuranceAdapter adapter = new InsuranceAdapter(mContext, arrayList);
		adapter.addRow();
		return (adapter);
	}
	

	private View buildSeparator(String heading) {
		View header = inflater.inflate(R.layout.separator, null);
		TextView separatorText = (TextView) header.findViewById(R.id.separator);
		((TextView) separatorText).setText(heading);
		return (header);
	}

}