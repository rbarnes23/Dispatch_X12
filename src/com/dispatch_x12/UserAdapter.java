package com.dispatch_x12;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.view.View.OnFocusChangeListener;

public class UserAdapter extends
		BaseAdapter {
	private Context mContext;
	private ViewHolder holder;
	ArrayList<HashMap<String, CharSequence>> mList;
	
	public UserAdapter(Context context,
			ArrayList<HashMap<String, CharSequence>> phoneList) {
		super();
		mList = phoneList;
		mContext = context;
	}
	
	
	public void addRow() {
		 HashMap<String, CharSequence> userMap = new HashMap<String, CharSequence>();
			userMap.put("firstName", "");
			userMap.put("lasttName", "");
			userMap.put("typeSpinnerSelected","0");
		mList.add(userMap);
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
		// There will only be one customer
		notifyDataSetChanged();
		HashMap<String, CharSequence> userMap = mList.get(0);
		jInfo = (JSONObject) JsonHelper.toJSON(userMap);
		return jInfo;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = mInflater.inflate(R.layout.users, null);
			holder = new ViewHolder(view);
			view.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.row = position;
		HashMap<String, CharSequence> userMap = mList.get(position);
		holder.firstName.setText(userMap.get("firstName"));
		holder.lastName.setText(userMap.get("lastName"));

		holder.typeSpinner.setSelection(Integer
				.parseInt((String) userMap
						.get("typeSpinnerSelected")));

		return view;
	}

	class ViewHolder {
		public EditText firstName;
		public EditText lastName;
		public Spinner typeSpinner;
		public int row;
		
		public ViewHolder(View view) {
			firstName = (EditText) view.findViewById(R.id.firstName);
			// attach the onFocusChange listener to the EditText
			firstName.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
								v.setBackgroundColor(Color.rgb(255, 248, 220));
					} else {
						// set the row background white
						v.setBackgroundColor(Color.rgb(255, 255, 255));
						mList.get(row).put("firstName", firstName.getText());
					}

				}

			});

			lastName = (EditText) view.findViewById(R.id.lastName);
			
			// attach the onFocusChange listener to the EditText
			lastName.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
								v.setBackgroundColor(Color.rgb(255, 248, 220));
					} else {
						// set the row background white
						v.setBackgroundColor(Color.rgb(255, 255, 255));
						mList.get(row).put("lastName", lastName.getText());
					}

				}

			});
			
			
			typeSpinner = (Spinner) view.findViewById(R.id.userTypeSpinner);
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

		}

	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public HashMap<String, CharSequence> getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

}