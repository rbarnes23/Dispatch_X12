package com.dispatch_x12.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

//import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dispatch_x12.MessageProcessing;
import com.dispatch_x12.R;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import com.dispatch_x12.utilities.JsonHelper;

public class AddressAdapter extends BaseAdapter {
	private Context mContext;
	private ViewHolder holder;
	private ArrayList<HashMap<String, String>> mList;

	public AddressAdapter(Context context,
			ArrayList<HashMap<String, String>> arrayList) {
		super();
		mList = arrayList;
		mContext = context;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public HashMap<String, String> getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setRow(ArrayList<HashMap<String, String>> arrayList) {
		mList = arrayList;
		if (mList.size() < 1) {
			addRow();
		} else {
			notifyDataSetChanged();
		}
	}

	public void addRow() {
		HashMap<String, String> messageMap = new HashMap<String, String>();
		messageMap.put("fullAddress", "552 W Davis Blvd,Tampa,FL  33606");
		messageMap.put("addressOne", "552 W Davis Blvd");
		messageMap.put("addressTwo", "P.O.Box 552");
		messageMap.put("city", "Tampa");
		messageMap.put("state", "FL");
		messageMap.put("postalCode", "33606");
		messageMap.put("typeSpinnerSelected", "3");

		mList.add(messageMap);
		notifyDataSetChanged();
	}

	public JSONObject saveToJSON(JSONObject jInfo) throws JSONException {
		notifyDataSetChanged();
		JSONArray jArray = JsonHelper.hashMapToJson(mList);

		// JSONArray jArray = new JSONArray(mList); this was not working as
		// planned
		return jInfo.put("Address", jArray);
	}

	public void deleteRow(int row) {
		mList.remove(row);
		if (mList.size() == 0) {
			addRow();
		} else {
			notifyDataSetChanged();
		}
	}

	public void verifyAddress(int row) {
		Object params[] = { new Object(), new Object() };
		params[0] = holder.fullAddress.getText().toString();
		params[1] = mContext;
		List addressList = null;
		try {
			addressList = new MessageProcessing.verifyAddressNomatim().execute(
					params).get();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			e1.printStackTrace();
		}
		if (addressList != null && addressList.size() > 0) {
			Address addressFound = (Address) addressList.get(0);
			HashMap<String, String> addressMap = mList.get(row);
			addressMap.put("fullAddress", addressMap.get("fullAddress"));
			addressMap.put("addressOne", addressFound.getAddressLine(0));
			addressMap.put("addressTwo", addressMap.get("addressTwo"));
			addressMap.put("city", addressFound.getLocality());
			addressMap.put("state", addressFound.getAdminArea());
			addressMap.put("postalCode", addressFound.getPostalCode());
			addressMap
					.put("lon", Integer.toString((int) (addressFound
							.getLongitude() * 1E6)));
			addressMap.put("lat",
					Integer.toString((int) (addressFound.getLatitude() * 1E6)));
			if (addressMap.get("lon").length() > 0) {
				// use stateselector for this....does not work correcly now
				holder.verifyIndicator
						.setBackgroundResource(R.drawable.map2424);
			}

			mList.set(row, addressMap);

			notifyDataSetChanged();

		}

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = mInflater.inflate(R.layout.address, null);
			holder = new ViewHolder(view);
			view.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.row = position;
		HashMap<String, String> addressMap = mList.get(holder.row);
		holder.fullAddress.setText(addressMap.get("fullAddress"));

		holder.typeSpinner.setSelection(Integer.parseInt((String) addressMap
				.get("typeSpinnerSelected")));

		holder.addressOne.setText(addressMap.get("addressOne"));
		holder.addressTwo.setText(addressMap.get("addressTwo"));
		holder.city.setText(addressMap.get("city"));
		holder.state.setText(addressMap.get("state"));
		holder.postalCode.setText(addressMap.get("postalCode"));
		holder.lon.setText(addressMap.get("lon"));
		holder.lat.setText(addressMap.get("lat"));
		return view;
	}

	class ViewHolder {
		public ImageView verifyIndicator;
		public ImageView deleteIndicator;
		public EditText fullAddress;
		public Spinner typeSpinner;
		public EditText addressOne;
		public EditText addressTwo;
		public EditText city;
		public EditText state;
		public EditText postalCode;
		public Button addAddress;
		public TextView lon;
		public TextView lat;
		public int row;

		public ViewHolder(View view) {
			fullAddress = (EditText) view.findViewById(R.id.fullAddress);
			// attach the onFocusChange listener to the EditText
			fullAddress.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						v.setBackgroundColor(Color.rgb(255, 248, 220));
					} else {
						// set the row background white
						v.setBackgroundColor(Color.rgb(255, 255, 255));
						mList.get(row).put("fullAddress",
								fullAddress.getText().toString());
					}

				}

			});

			typeSpinner = (Spinner) view.findViewById(R.id.typeSpinner);
			// attach the onFocusChange listener to the EditText
			typeSpinner.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						v.setBackgroundColor(Color.rgb(255, 248, 220));
					} else {
						// set the row background white
						v.setBackgroundColor(Color.rgb(255, 255, 255));
					}

				}

			});

			typeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int position, long arg3) {

					mList.get(row).put("typeSpinnerSelected",
							Integer.toString(position));

				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// Dont do anything as this was set initially
				}
			});

			addressOne = (EditText) view.findViewById(R.id.addressOne);
			// attach the onFocusChange listener to the EditText
			addressOne.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						v.setBackgroundColor(Color.rgb(255, 248, 220));
					} else {
						// set the row background white
						v.setBackgroundColor(Color.rgb(255, 255, 255));
						mList.get(row).put("addressOne",
								addressOne.getText().toString());
					}

				}

			});

			addressTwo = (EditText) view.findViewById(R.id.addressTwo);
			// attach the onFocusChange listener to the EditText
			addressTwo.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						v.setBackgroundColor(Color.rgb(255, 248, 220));
					} else {
						// set the row background white
						v.setBackgroundColor(Color.rgb(255, 255, 255));
						mList.get(row).put("addressTwo",
								addressTwo.getText().toString());
					}

				}

			});

			city = (EditText) view.findViewById(R.id.city);
			// attach the onFocusChange listener to the EditText
			city.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						v.setBackgroundColor(Color.rgb(255, 248, 220));
					} else {
						// set the row background white
						v.setBackgroundColor(Color.rgb(255, 255, 255));
						mList.get(row).put("city", city.getText().toString());
					}

				}

			});

			state = (EditText) view.findViewById(R.id.state);
			// attach the onFocusChange listener to the EditText
			state.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						v.setBackgroundColor(Color.rgb(255, 248, 220));
					} else {
						// set the row background white
						v.setBackgroundColor(Color.rgb(255, 255, 255));
						mList.get(row).put("state", state.getText().toString());
					}

				}

			});

			postalCode = (EditText) view.findViewById(R.id.postalCode);
			// attach the onFocusChange listener to the EditText
			postalCode.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						v.setBackgroundColor(Color.rgb(255, 248, 220));
					} else {
						// set the row background white
						v.setBackgroundColor(Color.rgb(255, 255, 255));
						mList.get(row).put("postalCode",
								postalCode.getText().toString());
					}

				}

			});

			lon = (TextView) view.findViewById(R.id.lon);
			lat = (TextView) view.findViewById(R.id.lat);
			verifyIndicator = (ImageView) view
					.findViewById(R.id.verifyIndicator);
			verifyIndicator.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					verifyAddress(row);
				}
			});

			deleteIndicator = (ImageView) view
					.findViewById(R.id.deleteIndicator);
			deleteIndicator.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					deleteRow(row);
				}
			});

			addAddress = (Button) view.findViewById(R.id.addAddress);
			// attach the onFocusChange listener to the EditText
			addAddress.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						v.setBackgroundColor(Color.rgb(255, 248, 220));
					} else {
						// set the row background white
						v.setBackgroundColor(Color.rgb(255, 255, 255));
						// mList.get(row).put("fullAddress",
						// fullAddress.getText());
					}

				}

			});

			addAddress.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					addRow();

				}
			});

		}

	}

}