package com.dispatch_x12;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.dispatch_x12.AppSettings;
import com.dispatch_x12.CustomAutoCompleteTextView;
import com.dispatch_x12.MainActivity;
import com.dispatch_x12.MessageProcessing;
import com.dispatch_x12.R;
import com.dispatch_x12.utilities.Constant;
import com.edilibrary.Create210;
import com.edilibrary.Create997;
import com.tokenlibrary.Token;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;
import com.dispatch_x12.services.ChatService;

public class LayOutOne extends Fragment {
	ViewGroup root;
	Context mContext;
	private int mGroupSelectedPosition;
	private JSONObject mJsonShipment = new JSONObject();
	private InputMethodManager imm;
	private CustomAutoCompleteTextView mAutoComplete;
	private String mSearch;
	private String mMemberid = AppSettings.getMemberid();
	private SimpleAdapter mAutoCompleteAdapter;
	private ArrayList<HashMap<String, String>> mMemberList;
	private ArrayList<HashMap<String, String>> mLoadList;
	private TextView shipmentIdentificationNo;
	private TextView identificationCodeQualifier;
	private TextView identificationCode;
	private TextView methodOfPayment;
	private TextView driverSelected;
	private Spinner spinnerStatus;
	private TextView contactInfo;
	private TextView specialInstructionsDetail;
	private TextView shipperName;
	private TextView shipperAddress1;
	private TextView shipperAddress2;
	private TextView shipperCity;
	private TextView shipperState;
	private TextView shipperPostalCode;
	private TextView billToName;
	private TextView billToAddress1;
	private TextView billToAddress2;
	private TextView billToCity;
	private TextView billToState;
	private TextView billToPostalCode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		root = (ViewGroup) inflater.inflate(R.layout.master204, null);
		shipmentIdentificationNo = (TextView) root.findViewById(R.id.shipmentIdentificationNo);//B204;
//		weightUnitCode = (TextView) root.findViewById(R.id.weightUnitCode);//B205
		methodOfPayment = (TextView) root.findViewById(R.id.methodOfPayment);//B206
		identificationCodeQualifier = (TextView) root.findViewById(R.id.identificationCodeQualifier);//N103;
		identificationCode = (TextView) root.findViewById(R.id.identificationCode);//N104;
		driverSelected = (TextView) root.findViewById(R.id.driversSelected);
		spinnerStatus = (Spinner) root.findViewById(R.id.spinnerStatus);
		contactInfo = (TextView) root.findViewById(R.id.contactInfo);
		specialInstructionsDetail = (TextView) root
				.findViewById(R.id.specialInstructionsDetail);
		shipperName = (TextView) root.findViewById(R.id.shipperName);
		shipperAddress1 = (TextView) root.findViewById(R.id.shipperAddress1);
		shipperAddress2 = (TextView) root.findViewById(R.id.shipperAddress2);
		shipperCity = (TextView) root.findViewById(R.id.shipperCity);
		shipperState = (TextView) root.findViewById(R.id.shipperState);
		shipperPostalCode = (TextView) root
				.findViewById(R.id.shipperPostalCode);
		billToName = (TextView) root.findViewById(R.id.billToName);
		billToAddress1 = (TextView) root.findViewById(R.id.billToAddress1);
		billToAddress2 = (TextView) root.findViewById(R.id.billToAddress2);
		billToCity = (TextView) root.findViewById(R.id.billToCity);
		billToState = (TextView) root.findViewById(R.id.billToState);
		billToPostalCode = (TextView) root.findViewById(R.id.billToPostalCode);

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

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// try {
		// buttonListener = (OnButtonPressListener) getActivity();
		// } catch (ClassCastException e) {
		// throw new ClassCastException(activity.toString()
		// + " must implement onButtonPressed");
		// }
	}

	void init(ViewGroup root) {
		Spinner statusSpinner = (Spinner) root.findViewById(R.id.spinnerStatus);
		statusSpinner.setSelection(mGroupSelectedPosition, true);
		statusSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			String createTransaction;

			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				mGroupSelectedPosition = position;
				// TextView status = (TextView) selectedItemView;
				// if (status.getText().equals("Accept")) {
				Create997 edi997 = new Create997();
				switch (position) {
				case 0: // New Tender
					// try {
					createTransaction = "RESET";// edi997.create997(mJsonShipment,
												// "A");
					break;
				case 1: // Accept Tender
					try {
						createTransaction = edi997
								.create997(mJsonShipment, "A");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;

				case 2: // Decline Tender
					try {
						createTransaction = edi997
								.create997(mJsonShipment, "D");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				case 3: // In Transit
					try {
						createTransaction = edi997
								.create997(mJsonShipment, "I");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				case 4: // Delivered
					try {
						Create210 edi210 = new Create210();
						createTransaction = edi210.create210(mJsonShipment,
								"D", "XXXX", "Driver", "300004", "1235", "TL",
								"53", "TL");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				case 5: // Invoice
					try {
						createTransaction = edi997
								.create997(mJsonShipment, "I");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				case 6: // Paid
					createTransaction = "PAID";
					break;

				default:
					break;
				}
				MainActivity.setToast("Status: " + createTransaction,
						Toast.LENGTH_LONG);

			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// TODO Auto-generated method stub
			}
		});
		imm = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		mAutoComplete = (CustomAutoCompleteTextView) root
				.findViewById(R.id.memberEntry);
		// mAutoComplete.setAdapter(adapter);
		mAutoComplete.setThreshold(1);
		// Keys used in Hashmap
		String[] from = { "id", "knownas" };

		// Ids of views in listview_layout
		int[] to = { R.id.spinnerId, R.id.spinnerTarget };

		mAutoCompleteAdapter = new SimpleAdapter(mContext, mMemberList,
				R.layout.spinner_layout, from, to);

		mAutoComplete.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int pos,
					long id) {
				String ids = mMemberList.get(pos).get("payinfo");
				Message msg = MainActivity.mUiHandler.obtainMessage();
				msg.what = Constant.PAYDRIVER;
				Bundle bundle = new Bundle();
				bundle.putString("message", ids);
				msg.setData(bundle);
				MainActivity.mUiHandler.sendMessage(msg);
				// Toast.makeText(mContext, ids, Toast.LENGTH_LONG).show();
				mAutoComplete.setText("");
				imm.hideSoftInputFromWindow(mAutoComplete.getWindowToken(), 0);
			}
		});

		mAutoComplete.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				// Object[] params = new Object[2];
				// params[0] = mMemberid;
				// params[1] = s.toString();
				mSearch = s.toString();
				findMembers(mSearch);
				// imm.hideSoftInputFromWindow(mAutoComplete.getWindowToken(),
				// 0);
				// new findMembers().execute();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) { // TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) { // TODO Auto-generated method stub
				// String search = s.toString();

			}

		});

		/*
		 * Button but=(Button)root.findViewById(R.id.button1);
		 * but.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { Message msg = new Message();
		 * msg.what= 1; // msg.obj= "Message From First Fragment"; //
		 * buttonListener.onButtonPressed(msg); } });
		 */}

	public void setMessage(JSONObject msg) {
		String type = null;
		type = msg.optString("type");
		if (type.contentEquals("findmember")) {

			String response = msg.optString("response");
			// ArrayList<HashMap<String, String>> memberList = null;
			mMemberList = null;
			try {
				mMemberList = MessageProcessing.getMemberList(msg.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Keys used in Hashmap
			String[] from = { "id", "knownas" };

			// Ids of views in listview_layout
			int[] to = { R.id.spinnerId, R.id.spinnerTarget };

			mAutoCompleteAdapter = new SimpleAdapter(mContext, mMemberList,
					R.layout.spinner_layout, from, to);

			mAutoComplete.setAdapter(mAutoCompleteAdapter);

		} else {
			mJsonShipment = msg;

			String contactInformation = msg.optString("G6101") + ":"
					+ msg.optString("G6102") + ":" + msg.optString("G6103")
					+ ":" + msg.optString("G6104");
			;
			String instructions = msg.optString("NTE02");
			String address = msg.optString("N301").replaceAll("~", "").trim()
					+ ", " + msg.optString("N401").trim() + ", "
					+ msg.optString("N402").trim() + " "
					+ msg.optString("N403").trim();
			address = msg.optString("N102").trim() + "\n" + address;
			shipmentIdentificationNo.setText(msg.optString("B204").trim());
			identificationCodeQualifier.setText(msg.optString("N103").trim());
			identificationCode.setText(msg.optString("N104").replaceAll("~", "").trim());
			
			shipperName.setText(msg.optString("N102").trim());
			shipperAddress1.setText(msg.optString("N301").replaceAll("~", "")
					.trim());
			shipperCity.setText(msg.optString("N401").trim());
			shipperState.setText(msg.optString("N402").trim());
			shipperPostalCode.setText(msg.optString("N403").trim());
			methodOfPayment.setText(msg.optString("B206").replaceAll("~", "").trim());
			billToName.setText(msg.optString("N102").trim());
			billToAddress1.setText(msg.optString("N301").replaceAll("~", "")
					.trim());
			billToCity.setText(msg.optString("N401").trim());
			billToState.setText(msg.optString("N402").trim());
			billToPostalCode.setText(msg.optString("N403").trim());

			// billToName.setText(address);
			specialInstructionsDetail.setText(instructions);
			contactInfo.setText(contactInformation);
		}
		// TextView txt = (TextView) root.findViewById(R.id.billTo);
		// txt.setText(msg.obj.toString());
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

	private void findMembers(String search) {
		Token findMembers = new Token();
		String findMember = findMembers.findMember(search, mMemberid);
		Message msg = ChatService.mServiceHandler.obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putString("message", findMember);
		msg.what = Constant.FINDMEMBER;
		msg.setData(bundle);
		sendMessage(msg);
	}

	// public class findMembers extends
	// AsyncTask<Object, Void, ArrayList<HashMap<String, String>>> {
	// // private Context context;// =MainActivity.getContext();
	// // ArrayList<HashMap<String, String>> memberList =new
	// // ArrayList<HashMap<String, String>>();;
	// String mTokenId;
	//
	// @Override
	// protected void onPreExecute() {
	//
	// Token findMembers = new Token();
	// String findMember = findMembers.findMember(mSearch, mMemberid);
	// Message msg = ChatService.mServiceHandler.obtainMessage();
	// Bundle bundle = new Bundle();
	// bundle.putString("message", findMember);
	// msg.what = Constant.FINDMEMBER;
	// msg.setData(bundle);
	// ChatService.mServiceHandler.sendMessage(msg);
	// // FriendsActivity.sendData(findMember);
	// mTokenId = findMembers.getID();
	// }
	//
	// @Override
	// protected ArrayList<HashMap<String, String>> doInBackground(
	// Object... params) {
	// // Send the search String
	// // String memberid = (String) params[0];
	// // String search = (String) params[1];
	// // context = (Context) params[2];
	// // Token findmember = new Token();
	// // String findMember = findmember.findMember(mSearch, mMemberid);
	// // FriendsActivity.sendData(findMember);
	// // mTokenId = findmember.getID();
	// ArrayList<HashMap<String, String>> memberList = null;
	//
	// // Now get the member info returned;
	// int mBreak = 0;
	// for (int j = 1; j < 30; j++) {
	// try {
	// memberList = MessageProcessing.getMemberList();
	// int size = memberList.size();
	// for (int i = 0; i < size; i++) {
	// if (mTokenId.contentEquals(memberList.get(i).get(
	// "tokenid"))) {
	// mBreak = 1;
	// // memberList.add(memberList.get(i));
	// }
	// // if (mBreak == 1) {
	// // break;// Break out of the i loop
	// // }
	//
	// }
	// if (mBreak == 1) {
	// break; // Break out of the j loop
	// }
	//
	// Thread.sleep(500);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	//
	// }
	// return memberList;
	//
	// }
	//
	// protected void onPostExecute(
	// ArrayList<HashMap<String, String>> memberList) {
	// // Keys used in Hashmap
	// String[] from = { "id", "name" };
	//
	// // Ids of views in listview_layout
	// int[] to = { R.id.spinnerId, R.id.spinnerTarget };
	//
	// SimpleAdapter adapter = new SimpleAdapter(mContext, memberList,
	// R.layout.spinner_layout, from, to);
	//
	// mAutoComplete.setAdapter(adapter);
	// }
	//
	// }

}