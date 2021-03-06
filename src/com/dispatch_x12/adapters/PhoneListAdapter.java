package com.dispatch_x12.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dispatch_x12.utilities.JsonHelper;
import com.dispatch_x12.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;

public class PhoneListAdapter extends BaseAdapter
		 {
	private Context mContext;
	private ViewHolder holder;
	private ArrayList<HashMap<String, String>> mList;
	
	public PhoneListAdapter(Context context,
			ArrayList<HashMap<String, String>> phoneList) {
		super();
		mList = phoneList;
		mContext = context;
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
		 HashMap<String, String> phoneMap = new HashMap<String, String>();
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

	public JSONObject saveToJSON(JSONObject jInfo) throws JSONException {
		JSONArray jArray = JsonHelper.hashMapToJson(mList);
		return jInfo.put("Phone", jArray);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = mInflater.inflate(R.layout.phoneno, null);
			holder = new ViewHolder(view);
			view.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.row = position;
		HashMap<String, String> phoneMap = mList.get(position);
		holder.phoneNo.setText(phoneMap.get("phoneNo"));
		holder.typeSpinner.setSelection(Integer
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
			OnFocusChangeListener onFocusChanged= new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
								v.setBackgroundColor(Color.rgb(255, 248, 220));
					} else {
						// set the row background white
						v.setBackgroundColor(Color.rgb(255, 255, 255));
						if(v.getId()==R.id.phoneNo){
							mList.get(row).put("phoneNo", phoneNo.getText().toString());
						}
							
					}

				}
			};
			
			phoneNo = (EditText) view.findViewById(R.id.phoneNo);

			// attach the onFocusChange listener to the EditText
			phoneNo.setOnFocusChangeListener(onFocusChanged);
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
			// attach the onFocusChange listener to the EditText
			typeSpinner.setOnFocusChangeListener(onFocusChanged);
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

}