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

public class UserEntry extends ListFragment {
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
		root = (ViewGroup) inflater.inflate(R.layout.customerentry, null);

		init(root);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
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
		adapter.addAdapter(buildPayrollList());

		setListAdapter(adapter);
	}

	public JSONObject createUserEntryMessage(JSONObject jUser)
			throws JSONException {
		// Go thru adapters and build the JSONObject

		int adapterCount = adapter.getCount();
		// go thru the adapters and call the saveToJSON
		for (int count = 0; count < adapterCount; count++) {
			ListAdapter thisAdapter = adapter.getAdapter(count);
			if (thisAdapter.getClass().getSimpleName()
					.contentEquals("PhoneListAdapter")) {
				PhoneListAdapter phoneListAdapter = (PhoneListAdapter) thisAdapter;
				jUser = phoneListAdapter.saveToJSON(jUser);
			} else if (thisAdapter.getClass().getSimpleName()
					.contentEquals("AddressAdapter")) {
				AddressAdapter addressAdapter = (AddressAdapter) thisAdapter;
				jUser = addressAdapter.saveToJSON(jUser);
			} else if (thisAdapter.getClass().getSimpleName()
					.contentEquals("EmailListAdapter")) {
				EmailListAdapter emailAdapter = (EmailListAdapter) thisAdapter;
				jUser = emailAdapter.saveToJSON(jUser);
			} else if (thisAdapter.getClass().getSimpleName()
					.contentEquals("UserAdapter")) {
				UserAdapter userAdapter = (UserAdapter) thisAdapter;
				jUser = userAdapter.saveToJSON(jUser);
			} else if (thisAdapter.getClass().getSimpleName()
					.contentEquals("PayrollAdapter")) {
				PayrollAdapter payrollAdapter = (PayrollAdapter) thisAdapter;
				jUser = payrollAdapter.saveToJSON(jUser);
			}

		}
		return jUser;
	}

	private void setAdapterList(JSONObject jCompany) throws JSONException {
		// Go thru adapters and build the JSONObject

		int adapterCount = adapter.getCount();
		// go thru the adapters and call the saveToJSON
		for (int count = 0; count < adapterCount; count++) {
			ListAdapter thisAdapter = adapter.getAdapter(count);
			if (thisAdapter == null) {
				continue;
			}
			if (thisAdapter.getClass().getSimpleName()
					.contentEquals("PhoneListAdapter")) {
				PhoneListAdapter phoneListAdapter = (PhoneListAdapter) thisAdapter;
				JSONArray array = jCompany.getJSONArray("Phone");
				ArrayList<HashMap<String, String>> arrayList = (ArrayList<HashMap<String, String>>) JsonHelper
						.toList(array);
				phoneListAdapter.setRow(arrayList);
			} else if (thisAdapter.getClass().getSimpleName()
					.contentEquals("AddressAdapter")) {
				AddressAdapter addressAdapter = (AddressAdapter) thisAdapter;
				JSONArray array = jCompany.getJSONArray("Address");
				ArrayList<HashMap<String, String>> arrayList = (ArrayList<HashMap<String, String>>) JsonHelper
						.toList(array);
				addressAdapter.setRow(arrayList);
			} else if (thisAdapter.getClass().getSimpleName()
					.contentEquals("EmailListAdapter")) {
				EmailListAdapter emailAdapter = (EmailListAdapter) thisAdapter;
				JSONArray array = jCompany.getJSONArray("Email");
				ArrayList<HashMap<String, String>> arrayList = (ArrayList<HashMap<String, String>>) JsonHelper
						.toList(array);
				emailAdapter.setRow(arrayList);

			} else if (thisAdapter.getClass().getSimpleName()
					.contentEquals("UserAdapter")) {
				UserAdapter userAdapter = (UserAdapter) thisAdapter;
				userAdapter.setRow(jCompany);

			} else if (thisAdapter.getClass().getSimpleName()
					.contentEquals("PayrollAdapter")) {
				PayrollAdapter payrollAdapter = (PayrollAdapter) thisAdapter;
				JSONArray array = jCompany.getJSONArray("payrollInfo");
				ArrayList<HashMap<String, String>> arrayList = (ArrayList<HashMap<String, String>>) JsonHelper
						.toList(array);
				payrollAdapter.setRow(arrayList);

			}

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
		} else {
			try {
				msg = createUserEntryMessage(msg);
				Message msgToSend = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("message", msg.toString());
				msgToSend.setData(bundle);
				msgToSend.what = Constant.EMPLOYEEDATA;
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

	private UserAdapter buildUserList() {
		ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
		UserAdapter adapter = new UserAdapter(mContext, arrayList);
		adapter.addRow();
		return (adapter);
	}

	private AddressAdapter buildAddressList() {
		ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
		AddressAdapter adapter = new AddressAdapter(mContext, arrayList);
		adapter.addRow();
		return (adapter);
	}

	private PhoneListAdapter buildPhoneList() {
		ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
		PhoneListAdapter adapter = new PhoneListAdapter(mContext, arrayList);
		adapter.addRow();

		return (adapter);
	}

	private EmailListAdapter buildEmailList() {
		ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
		EmailListAdapter adapter = new EmailListAdapter(mContext, arrayList);
		adapter.addRow();
		return (adapter);
	}

	private PayrollAdapter buildPayrollList() {
		ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
		PayrollAdapter adapter = new PayrollAdapter(mContext, arrayList);
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