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

public class PayrollAdapter extends BaseAdapter
		 {
	private Context mContext;
	private ViewHolder holder;
	ArrayList<HashMap<String, String>> mList;
	
	public PayrollAdapter(Context context,
			ArrayList<HashMap<String, String>> payrollList) {
		super();
		mList = payrollList;
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
		 HashMap<String, String> payrollMap = new HashMap<String, String>();
			payrollMap.put("payrollInfo", "");
			payrollMap.put("typeSpinnerSelected","0");
		mList.add(payrollMap);
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
		return jInfo.put("payrollInfo", jArray);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = mInflater.inflate(R.layout.payroll, null);
			holder = new ViewHolder(view);
			view.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.row = position;
		HashMap<String, String> payrollMap = mList.get(position);
		holder.payrollInfo.setText(payrollMap.get("payrollInfo"));
		holder.typeSpinner.setSelection(Integer
				.parseInt((String) payrollMap
						.get("typeSpinnerSelected")));
		return view;
	}

	class ViewHolder {
		public ImageView deleteIndicator;
		public EditText payrollInfo;
		public Spinner typeSpinner;
		public Button addPayroll;
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
						if(v.getId()==R.id.payrollInfo){
							mList.get(row).put("payrollInfo", payrollInfo.getText().toString());
						}
							
					}

				}
			};
			
			payrollInfo = (EditText) view.findViewById(R.id.payrollInfo);

			// attach the onFocusChange listener to the EditText
			payrollInfo.setOnFocusChangeListener(onFocusChanged);
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

			
			addPayroll= (Button) view.findViewById(R.id.addPayroll);
			addPayroll.setOnClickListener(new OnClickListener() {
				
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