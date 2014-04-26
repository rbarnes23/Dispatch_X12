package com.dispatch_x12;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

public class CompanyEntry extends ListFragment {
	ViewGroup root;
	Context mContext;
	private InputMethodManager imm;
	private String mMemberid = AppSettings.getMemberid();
	private ArrayList<HashMap<String, String>> mCompanyList;
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
		adapter.addView(buildSeparator("Company Information"), false);
		adapter.addAdapter(buildCompanyList());
		adapter.addView(buildSeparator("Address"), false);
		adapter.addAdapter(buildAddressList());
		adapter.addView(buildSeparator("Email"), false);
		adapter.addAdapter(buildEmailList());
		adapter.addView(buildSeparator("Phone Numbers"), false);
		adapter.addAdapter(buildPhoneList());
		adapter.addView(buildSeparator("Insurance Documents"), false);
		adapter.addAdapter(buildInsuranceList());
		setListAdapter(adapter);
	}

	private JSONObject createCompanyEntryMessage(JSONObject jCompany)
			throws JSONException {
		// Go thru adapters and build the JSONObject

		int adapterCount = adapter.getCount();
		// go thru the adapters and call the saveToJSON
		for (int count = 0; count < adapterCount; count++) {
			ListAdapter thisAdapter = adapter.getAdapter(count);
			if (thisAdapter.getClass().getSimpleName()
					.contentEquals("PhoneListAdapter")) {
				PhoneListAdapter phoneListAdapter = (PhoneListAdapter) thisAdapter;
				jCompany = phoneListAdapter.saveToJSON(jCompany);
			} else if (thisAdapter.getClass().getSimpleName()
					.contentEquals("AddressAdapter")) {
				AddressAdapter addressAdapter = (AddressAdapter) thisAdapter;
				jCompany = addressAdapter.saveToJSON(jCompany);
			} else if (thisAdapter.getClass().getSimpleName()
					.contentEquals("EmailListAdapter")) {
				EmailListAdapter emailAdapter = (EmailListAdapter) thisAdapter;
				jCompany = emailAdapter.saveToJSON(jCompany);
			} else if (thisAdapter.getClass().getSimpleName()
					.contentEquals("CustomerAdapter")) {
				CustomerAdapter companyAdapter = (CustomerAdapter) thisAdapter;
				jCompany = companyAdapter.saveToJSON(jCompany);
			} else if (thisAdapter.getClass().getSimpleName()
					.contentEquals("InsuranceAdapter")) {
				InsuranceAdapter insuranceAdapter = (InsuranceAdapter) thisAdapter;
				jCompany = insuranceAdapter.saveToJSON(jCompany);
			}

		}
		return jCompany;
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
					.contentEquals("CustomerAdapter")) {
				CustomerAdapter companyAdapter = (CustomerAdapter) thisAdapter;
				companyAdapter.setRow(jCompany);

			} else if (thisAdapter.getClass().getSimpleName()
					.contentEquals("InsuranceAdapter")) {
				InsuranceAdapter insuranceAdapter = (InsuranceAdapter) thisAdapter;
				JSONArray array = jCompany.getJSONArray("Insurance");
				ArrayList<HashMap<String, String>> arrayList = (ArrayList<HashMap<String, String>>) JsonHelper
						.toList(array);
				insuranceAdapter.setRow(arrayList);

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
			// Take the message and put it into all the different adapters
		} else {
			try {

				msg = createCompanyEntryMessage(msg);
				Message msgToSend = ChatService.mServiceHandler
						.obtainMessage(Constant.COMPANYDATA);
				Bundle bundle = new Bundle();
				bundle.putString("message", msg.toString());
				msgToSend.setData(bundle);
				sendMessage(msgToSend);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * Parse address into components
	 */

	public List parseAddress(String address) {
		Matcher m = Constant.ADDRESS_PATTERN.matcher(address);
		List<String> matches = new ArrayList<String>();
		while (m.find()) {
			matches.add(m.group());
		}
		// showResult(m, address);

		return matches;
	}

	/**
	 * Displays the result of the most recent match.
	 */
	void showResult(Matcher matcher, String targetText) {
		String result = "";
		if (targetText == null)
			result = "String to be searched is null.";
		else {
			result = "start() = " + matcher.start() + ", end() = "
					+ matcher.end() + "\n";
			for (int i = 0; i <= matcher.groupCount(); i++) {
				if (i > 0)
					result += "\n";
				result += "group(" + i + ") = \"" + matcher.group(i) + "\"";
			}
		}
		final EditText AddressOne = (EditText) root
				.findViewById(R.id.addressTwo);

		AddressOne.setText((CharSequence) result);

	}

	private class getAddress extends AsyncTask<String, Void, List<Address>> {

		@Override
		protected List<Address> doInBackground(String... params) {
			Geocoder coder = new Geocoder(mContext, Locale.ENGLISH);
			List<Address> addressList = null;
			try {
				addressList = coder.getFromLocationName(params[0], 1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return addressList;
		}

		@Override
		protected void onPostExecute(List<Address> addressList) {
			if (addressList != null) {
				for (int i = 0; i < 1; i++) {
					mLocation = addressList.get(i);
				}
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

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == newConfig.ORIENTATION_LANDSCAPE) {
			// MainActivity.setToast("LAND", 1);
		} else {
			// MainActivity.setToast("PORT", 1);
		}
	}

	private CustomerAdapter buildCompanyList() {
		ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
		CustomerAdapter adapter = new CustomerAdapter(mContext, arrayList);
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