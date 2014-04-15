package com.dispatch_x12;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CompanyEntry extends ListFragment {
	ViewGroup root;
	Context mContext;
	private InputMethodManager imm;
	private String mMemberid = AppSettings.getMemberid();
	private ArrayList<HashMap<String, String>> mCustomerList;
	private int mStatusSelectedPosition;
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
		adapter.addAdapter(buildCustomerList());
		adapter.addView(buildSeparator("Address"), false);
		adapter.addAdapter(buildAddressList());
		adapter.addView(buildSeparator("Email"), false);
		adapter.addAdapter(buildEmailList());
		adapter.addView(buildSeparator("Phone Numbers"), false);
		adapter.addAdapter(buildPhoneList());
		adapter.addView(buildSeparator("Insurance Documents"), false);
		adapter.addAdapter(buildInsuranceList());
		setListAdapter(adapter);
//		final ListView listView = (ListView) root.findViewById(android.R.id.list);
//		listView.post(new Runnable(){
//			  public void run() {
//			    listView.setSelection(listView.getCount() - 1);
//			  }});
	}

	public JSONObject createCustomerEntryMessage(JSONObject jCustomer)
			throws JSONException {
		// Go thru adapters and build the JSONObject

		 int adapterCount = adapter.getCount();
		 // go thru the adapters and call the saveToJSON
		 for (int count =0;count<adapterCount;count++){
		 ListAdapter thisAdapter = adapter.getAdapter(count);
		 if(thisAdapter.getClass().getSimpleName().contentEquals("PhoneListAdapter")){
		 PhoneListAdapter phoneListAdapter=(PhoneListAdapter) thisAdapter;
		 jCustomer=phoneListAdapter.saveToJSON(jCustomer);
		 }
		 else
		 if(thisAdapter.getClass().getSimpleName().contentEquals("AddressAdapter")){
		 AddressAdapter addressAdapter=(AddressAdapter) thisAdapter;
		 jCustomer=addressAdapter.saveToJSON(jCustomer);
		 }
		 else
		 if(thisAdapter.getClass().getSimpleName().contentEquals("EmailListAdapter")){
		 EmailListAdapter emailAdapter=(EmailListAdapter) thisAdapter;
		 jCustomer=emailAdapter.saveToJSON(jCustomer);
		 }
		 else
		 if(thisAdapter.getClass().getSimpleName().contentEquals("CustomerAdapter")){
		 CustomerAdapter customerAdapter=(CustomerAdapter) thisAdapter;
		 jCustomer=customerAdapter.saveToJSON(jCustomer);
		 }
		 else
		 if(thisAdapter.getClass().getSimpleName().contentEquals("InsuranceAdapter")){
		 InsuranceAdapter customerAdapter=(InsuranceAdapter) thisAdapter;
		 jCustomer=customerAdapter.saveToJSON(jCustomer);
		 }
		
		 }
		return jCustomer;
	}

	void setMessage(JSONObject msg) {
		try {
			msg = createCustomerEntryMessage(msg);
			MainActivity.setToast(msg.toString(), Toast.LENGTH_LONG);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	private CustomerAdapter buildCustomerList() {
		ArrayList<HashMap<String, CharSequence>> arrayList = new ArrayList<HashMap<String, CharSequence>>();
		CustomerAdapter adapter = new CustomerAdapter(mContext, arrayList);
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
//		final ListView listView = (ListView) root.findViewById(android.R.id.list);
//		listView.post(new Runnable(){
//			  public void run() {
//			    listView.setSelection(listView.getCount() - 1);
//			  }});

		return (adapter);
	}

	private EmailListAdapter buildEmailList() {
		ArrayList<HashMap<String, CharSequence>> arrayList = new ArrayList<HashMap<String, CharSequence>>();
		EmailListAdapter adapter = new EmailListAdapter(mContext, arrayList);
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