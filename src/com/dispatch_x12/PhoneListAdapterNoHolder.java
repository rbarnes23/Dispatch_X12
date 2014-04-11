package com.dispatch_x12;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;

public class PhoneListAdapterNoHolder extends
		ArrayAdapter<HashMap<String, CharSequence>> {
	private Context mContext;
	private ViewHolder holder;
	ArrayList<HashMap<String, CharSequence>> mList;
	LayoutInflater mInflater = (LayoutInflater) mContext
	.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	
	public PhoneListAdapterNoHolder(Context context,
			ArrayList<HashMap<String, CharSequence>> phoneList) {
		super(context, 0, phoneList);
		mList = phoneList;
		mContext = context;
	}
	
	
	public void addRow() {
		 HashMap<String, CharSequence> phoneMap = new HashMap<String, CharSequence>();
			phoneMap.put("phoneNo", "");
			phoneMap.put("typeSpinnerSelected","0");
		mList.add(phoneMap);
		notifyDataSetChanged();
	}
	public void deleteRow(int row) {
		mList.remove(row);
		if (mList.size() == 0) {
			addRow();
		} else {
			notifyDataSetChanged();
		}
	}

	public JSONObject createJSONArray(JSONObject jInfo) throws JSONException {
		JSONArray jArray=new  JSONArray(mList);
		return jInfo.put("Phone", jArray);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);//convertView;

//		if (convertView == null) {
//			LayoutInflater mInflater = (LayoutInflater) mContext
//					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = mInflater.inflate(R.layout.phoneno, null);
//			//holder = new ViewHolder(view);
//			//view.setTag(holder);
//
//		} else {
//			//holder = (ViewHolder) convertView.getTag();
//		}
		final int row = position;
		HashMap<String, CharSequence> phoneMap = mList.get(position);
		EditText phoneNo = (EditText) view.findViewById(R.id.phoneNo);
		phoneNo.requestFocus();
		Spinner typeSpinner = (Spinner) view.findViewById(R.id.typeSpinner);
		typeSpinner
		.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0,
					View arg1, int position, long arg3) {
				
				mList.get(row).put(
						"typeSpinnerSelected",
						Integer.toString(position));

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// Dont do anything as this was set initially
			}
		});

		phoneNo.setText(phoneMap.get("phoneNo"));
		typeSpinner.setSelection(Integer
				.parseInt((String) phoneMap
						.get("typeSpinnerSelected")));

		return view;
	}

	class ViewHolder {
		public ImageView deleteIndicator;
		public EditText phoneNo;
		public Spinner typeSpinner;
		public Button addPhoneNo;
		public int row;
		
		public ViewHolder(View view) {
			phoneNo = (EditText) view.findViewById(R.id.phoneNo);
			phoneNo.requestFocus();
			 phoneNo.addTextChangedListener(new TextWatcher() {

		            @Override
		            public void onTextChanged(CharSequence s, int start,
		                    int before, int count) {
		                String test = s.toString();
		                
		            }

		            @Override
		            public void beforeTextChanged(CharSequence s, int start,
		                    int count, int after) {

		            }

		            @Override
		            public void afterTextChanged(Editable s) {

		            }
		        });

	
	
			// attach the onFocusChange listener to the EditText
			phoneNo.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						v.setBackgroundColor(Color.CYAN);
					} else {
						// set the row background white
						v.setBackgroundColor(Color.rgb(255, 255, 255));
						mList.get(row).put("phoneNo", phoneNo.getText());
					}

				}

			});
			typeSpinner = (Spinner) view.findViewById(R.id.typeSpinner);
			typeSpinner
			.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0,
						View arg1, int position, long arg3) {
					
					mList.get(row).put(
							"typeSpinnerSelected",
							Integer.toString(position));

				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// Dont do anything as this was set initially
				}
			});

			deleteIndicator = (ImageView) view.findViewById(R.id.deleteIndicator);
			deleteIndicator.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					deleteRow(row);
					

				}
			});

			
			addPhoneNo= (Button) view.findViewById(R.id.addPhoneNo);
			addPhoneNo.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					addRow();
				}
			});

		}

	}

}