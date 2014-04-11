package com.dispatch_x12;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Address;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class UserEntry extends ListFragment {
	ViewGroup root;
	Context mContext;
	private Address mLocation;
	private LayoutInflater inflater;
	private MergeAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		root = (ViewGroup) inflater.inflate(R.layout.customerentry, null);

		init(root);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// root = (ViewGroup) inflater.inflate(R.layout.master204, null);
		// mContext = inflater.getContext();
		// init(root);
		return root;
	}

	void init(ViewGroup root) {

		adapter = new MergeAdapter();
		adapter.addView(buildSeparator("User Information"), false);
		adapter.addAdapter(buildUserList());
		adapter.addView(buildSeparator("Address"), false);
		adapter.addAdapter(buildAddressList());
		adapter.addView(buildSeparator("Email"), false);
		adapter.addAdapter(buildEmailList());
		adapter.addView(buildSeparator("Phone Numbers"), false);
		adapter.addAdapter(buildPhoneList());
		adapter.addView(buildSeparator("Payroll Information"), false);
//		// Need to build adapter for insurance docs

		setListAdapter(adapter);
	}

	public JSONObject createUserEntryMessage(JSONObject jUser)
			throws JSONException {
		// Go thru adapters and build the JSONObject

		 int adapterCount = adapter.getCount();
		 // go thru the adapters and call the saveToJSON
		 for (int count =0;count<adapterCount;count++){
		 ListAdapter thisAdapter = adapter.getAdapter(count);
		 if(thisAdapter.getClass().getSimpleName().contentEquals("PhoneListAdapter")){
		 PhoneListAdapter phoneListAdapter=(PhoneListAdapter) thisAdapter;
		 jUser=phoneListAdapter.saveToJSON(jUser);
		 }
		 else
		 if(thisAdapter.getClass().getSimpleName().contentEquals("AddressAdapter")){
		 AddressAdapter addressAdapter=(AddressAdapter) thisAdapter;
		 jUser=addressAdapter.saveToJSON(jUser);
		 }
		 else
		 if(thisAdapter.getClass().getSimpleName().contentEquals("EmailListAdapter")){
		 EmailListAdapter emailAdapter=(EmailListAdapter) thisAdapter;
		 jUser=emailAdapter.saveToJSON(jUser);
		 }
		 else
		 if(thisAdapter.getClass().getSimpleName().contentEquals("UserAdapter")){
		 UserAdapter userAdapter=(UserAdapter) thisAdapter;
		 jUser=userAdapter.saveToJSON(jUser);
		 }
//		 else
//		 if(thisAdapter.getClass().getSimpleName().contentEquals("PayrollAdapter")){
//		 CustomerAdapter customerAdapter=(CustomerAdapter) thisAdapter;
//		 jUser=customerAdapter.saveToJSON(jUser);
//		 }
		
		 }
		return jUser;
	}

	void setMessage(JSONObject msg) {
		try {
			msg = createUserEntryMessage(msg);
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
	private UserAdapter buildUserList() {
		ArrayList<HashMap<String, CharSequence>> arrayList = new ArrayList<HashMap<String, CharSequence>>();
		UserAdapter adapter = new UserAdapter(mContext, arrayList);
		adapter.addRow();
		return (adapter);
	}

	private AddressAdapter buildAddressList() {
		ArrayList<HashMap<String, CharSequence>> arrayList = new ArrayList<HashMap<String, CharSequence>>();
		AddressAdapter adapter = new AddressAdapter(mContext, arrayList);
		adapter.addRow();
		return (adapter);
	}

	private PhoneListAdapter buildPhoneList() {
		ArrayList<HashMap<String, CharSequence>> arrayList = new ArrayList<HashMap<String, CharSequence>>();
		PhoneListAdapter adapter = new PhoneListAdapter(mContext, arrayList);
		adapter.addRow();

		return (adapter);
	}

	private EmailListAdapter buildEmailList() {
		ArrayList<HashMap<String, CharSequence>> arrayList = new ArrayList<HashMap<String, CharSequence>>();
		EmailListAdapter adapter = new EmailListAdapter(mContext, arrayList);
		adapter.addRow();
		return (adapter);
	}
	private EmailListAdapter buildPayrollList() {
		ArrayList<HashMap<String, CharSequence>> arrayList = new ArrayList<HashMap<String, CharSequence>>();
		EmailListAdapter adapter = new EmailListAdapter(mContext, arrayList);
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