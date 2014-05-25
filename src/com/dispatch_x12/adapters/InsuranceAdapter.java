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

public class InsuranceAdapter extends BaseAdapter {
	private Context mContext;
	private ViewHolder holder;
	ArrayList<HashMap<String, String>> mList;

	public InsuranceAdapter(Context context,
			ArrayList<HashMap<String, String>> insuranceList) {
		super();
		mList = insuranceList;
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
		HashMap<String, String> companyMap = new HashMap<String, String>();
		companyMap.put("companyName", "");
		companyMap.put("periodFrom", "");
		companyMap.put("periodTo", "");
		companyMap.put("typeSpinnerSelected", "0");
		mList.add(companyMap);
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
		notifyDataSetChanged();
		JSONArray jArray = JsonHelper.hashMapToJson(mList);
		return jInfo.put("Insurance", jArray);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = mInflater.inflate(R.layout.insurance, null);
			holder = new ViewHolder(view);
			view.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.row = position;
		HashMap<String, String> companyMap = mList.get(position);
		holder.companyName.setText(companyMap.get("companyName"));
		holder.periodFrom.setText(companyMap.get("periodFrom"));
		holder.periodTo.setText(companyMap.get("periodTo"));

		holder.typeSpinner.setSelection(Integer.parseInt((String) companyMap
				.get("typeSpinnerSelected")));

		return view;
	}

	class ViewHolder {
		public EditText companyName;
		public EditText periodFrom;
		public EditText periodTo;
		public Spinner typeSpinner;
		public ImageView deleteIndicator;
		public int row;
		public Button addCompany;

		public ViewHolder(View view) {
			companyName = (EditText) view.findViewById(R.id.companyName);
			// attach the onFocusChange listener to the EditText
			companyName.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						v.setBackgroundColor(Color.rgb(255, 248, 220));
					} else {
						// set the row background white
						v.setBackgroundColor(Color.rgb(255, 255, 255));
						mList.get(row).put("companyName",
								companyName.getText().toString());
					}

				}

			});

			periodFrom = (EditText) view.findViewById(R.id.periodFrom);

			// attach the onFocusChange listener to the EditText
			periodFrom.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						v.setBackgroundColor(Color.rgb(255, 248, 220));
					} else {
						// set the row background white
						v.setBackgroundColor(Color.rgb(255, 255, 255));
						mList.get(row).put("periodFrom",
								periodFrom.getText().toString());
					}

				}

			});

			periodTo = (EditText) view.findViewById(R.id.periodTo);

			// attach the onFocusChange listener to the EditText
			periodTo.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						v.setBackgroundColor(Color.rgb(255, 248, 220));
					} else {
						// set the row background white
						v.setBackgroundColor(Color.rgb(255, 255, 255));
						mList.get(row).put("periodTo",
								periodTo.getText().toString());
					}

				}

			});

			typeSpinner = (Spinner) view.findViewById(R.id.typeSpinner);
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

			deleteIndicator = (ImageView) view
					.findViewById(R.id.deleteIndicator);
			deleteIndicator.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					deleteRow(row);
				}
			});

			addCompany = (Button) view.findViewById(R.id.addCompany);
			addCompany.setOnClickListener(new OnClickListener() {

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