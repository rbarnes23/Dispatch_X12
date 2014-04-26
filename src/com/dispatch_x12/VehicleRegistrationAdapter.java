package com.dispatch_x12;

import java.util.ArrayList;
import java.util.HashMap;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
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
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;

public class VehicleRegistrationAdapter extends BaseAdapter {
	private Context mContext;
	private ViewHolder holder;
	ArrayList<HashMap<String, String>> mList;

	public VehicleRegistrationAdapter(Context context,
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


	public void setRow(JSONObject jList) {
		if (jList.length() > 0) {
			HashMap<String, String> messageMap = new HashMap<String, String>();
			messageMap.put("registeredTo", jList.optString("registeredTo"));
			messageMap.put("plate", jList.optString("plate"));
			messageMap.put("decal", jList.optString("decal"));
			messageMap.put("expires", jList.optString("expires"));
			messageMap.put("vin", jList.optString("vin"));
			messageMap.put("titleNo", jList.optString("titleNo"));
			messageMap.put("plateType", jList.optString("plateType"));

			messageMap.put("typeSpinnerSelected", jList.optString("typeSpinnerSelected"));
			messageMap.put("colorSpinnerSelected", jList.optString("colorSpinnerSelected"));
			messageMap.put("makeSpinnerSelected",jList.optString("makeSpinnerSelected"));
			messageMap.put("modelSpinnerSelected",jList.optString("modelSpinnerSelected"));
			messageMap.put("yearSpinnerSelected",jList.optString("yearSpinnerSelected"));

			mList.clear();
			mList.add(messageMap);
			notifyDataSetChanged();
		} else {
			addRow();
		}
	}
	

	public void addRow() {
		HashMap<String, String> messageMap = new HashMap<String, String>();
		messageMap.put("registeredTo", "");
		messageMap.put("plate", "");
		messageMap.put("decal", "");
		messageMap.put("expires", "");
		messageMap.put("vin", "");
		messageMap.put("titleNo", "");
		messageMap.put("plateType", "");

		messageMap.put("typeSpinnerSelected", "0");
		messageMap.put("colorSpinnerSelected", "0");
		messageMap.put("makeSpinnerSelected", "0");
		messageMap.put("modelSpinnerSelected", "0");
		messageMap.put("yearSpinnerSelected", "0");

		mList.add(messageMap);
		notifyDataSetChanged();
	}

	public JSONObject saveToJSON(JSONObject jInfo) throws JSONException {
		// There will only be one customer
		notifyDataSetChanged();
		HashMap<String, String> map = mList.get(0);
		jInfo = (JSONObject) JsonHelper.toJSON(map);
		return jInfo;
	}

	public void deleteRow(int row) {
		mList.remove(row);
		if (mList.size() == 0) {
			addRow();
		} else {
			notifyDataSetChanged();
		}
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.vehicleregistration, null);
			holder = new ViewHolder(view);
			view.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.row = position;
		HashMap<String, String> registrationMap = mList.get(holder.row);
		holder.registeredTo.setText(registrationMap.get("registeredTo"));
		holder.plate.setText(registrationMap.get("plate"));
		holder.decal.setText(registrationMap.get("decal"));
		holder.expires.setText(registrationMap.get("expires"));

		holder.typeSpinner.setSelection(Integer.parseInt((String) registrationMap
				.get("typeSpinnerSelected")));
		holder.colorSpinner.setSelection(Integer.parseInt((String) registrationMap
				.get("colorSpinnerSelected")));
		holder.makeSpinner.setSelection(Integer.parseInt((String) registrationMap
				.get("makeSpinnerSelected")));
		holder.modelSpinner.setSelection(Integer.parseInt((String) registrationMap
				.get("modelSpinnerSelected")));
		holder.yearSpinner.setSelection(Integer.parseInt((String) registrationMap
				.get("yearSpinnerSelected")));
		return view;
	}

	class ViewHolder {
		public ImageView deleteIndicator;
		public Spinner typeSpinner;
		public Spinner colorSpinner;
		public Spinner makeSpinner;
		public Spinner modelSpinner;
		public Spinner yearSpinner;

		public EditText registeredTo;
		public EditText plate;
		public EditText decal;
		public EditText expires;
		
		//vin,titleno and platetype not implemented yet
		public EditText vin;
		public EditText titleNo;
		public EditText plateType;
		
		public int row;

		public ViewHolder(View view) {
			registeredTo = (EditText) view.findViewById(R.id.registeredTo);
			// attach the onFocusChange listener to the EditText
			registeredTo.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
								v.setBackgroundColor(Color.rgb(255, 248, 220));
					} else {
						// set the row background white
						v.setBackgroundColor(Color.rgb(255, 255, 255));
						mList.get(row)
								.put("registeredTo", registeredTo.getText().toString());
					}

				}

			});
			plate = (EditText) view.findViewById(R.id.plate);
			// attach the onFocusChange listener to the EditText
			plate.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
								v.setBackgroundColor(Color.rgb(255, 248, 220));
					} else {
						// set the row background white
						v.setBackgroundColor(Color.rgb(255, 255, 255));
						mList.get(row)
								.put("plate", plate.getText().toString());
					}

				}

			});

			decal = (EditText) view.findViewById(R.id.decal);
			// attach the onFocusChange listener to the EditText
			decal.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
								v.setBackgroundColor(Color.rgb(255, 248, 220));
					} else {
						// set the row background white
						v.setBackgroundColor(Color.rgb(255, 255, 255));
						mList.get(row)
								.put("decal", decal.getText().toString());
					}

				}

			});

			expires = (EditText) view.findViewById(R.id.expires);
			// attach the onFocusChange listener to the EditText
			expires.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
								v.setBackgroundColor(Color.rgb(255, 248, 220));
					} else {
						// set the row background white
						v.setBackgroundColor(Color.rgb(255, 255, 255));
						mList.get(row)
								.put("expires", expires.getText().toString());
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
			makeSpinner = (Spinner) view.findViewById(R.id.makeSpinner);

			makeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int position, long arg3) {

					mList.get(row).put("makeSpinnerSelected",
							Integer.toString(position));

				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// Dont do anything as this was set initially
				}
			});
			modelSpinner = (Spinner) view.findViewById(R.id.modelSpinner);

			modelSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int position, long arg3) {

					mList.get(row).put("modelSpinnerSelected",
							Integer.toString(position));

				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// Dont do anything as this was set initially
				}
			});
			yearSpinner = (Spinner) view.findViewById(R.id.yearSpinner);

			yearSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int position, long arg3) {

					mList.get(row).put("yearSpinnerSelected",
							Integer.toString(position));

				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// Dont do anything as this was set initially
				}
			});
			colorSpinner = (Spinner) view.findViewById(R.id.colorSpinner);

			colorSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int position, long arg3) {

					mList.get(row).put("colorSpinnerSelected",
							Integer.toString(position));

				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// Dont do anything as this was set initially
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


		}

	}

}