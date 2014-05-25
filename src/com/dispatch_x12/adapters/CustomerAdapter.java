package com.dispatch_x12.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.dispatch_x12.utilities.JsonHelper;
import com.dispatch_x12.MainActivity;
import com.dispatch_x12.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class CustomerAdapter extends BaseAdapter {
	private Context mContext;
	private ViewHolder holder;
	private ArrayList<HashMap<String, String>> mList;

	private enum enumCompanyType {
		SH, CN, LTL, TL, BR, OTH
	};

	private enum enumIsaQualifierType {
		Q01, Q08, Q12, Q14, QZZ
	};

	private enum enumisaUsageIndicator {
		P, T
	};

	public CustomerAdapter(Context context,
			ArrayList<HashMap<String, String>> arrayList) {
		super();
		mList = arrayList;
		mContext = context;
	}

	// public View[] getListofViews() {
	// View[] view = { new View(mContext), new View(mContext),
	// new View(mContext), new View(mContext), new View(mContext) };
	// view[0] = holder.companyName;
	// view[1] = holder.icc;
	// view[2] = holder.isaID;
	// return view;
	// }

	public void setRow(JSONObject jList) {
		if (jList.length() > 0) {
			HashMap<String, String> messageMap = new HashMap<String, String>();
			messageMap.put("companyTypeSpinnerSelection",
					jList.optString("companyTypeSpinnerSelection"));
			messageMap.put("isaQualifierTypeSpinnerSelection",
					jList.optString("isaQualifierTypeSpinnerSelection"));
			messageMap.put("isaUsageIndicatorSpinnerSelection",
					jList.optString("isaUsageIndicatorSpinnerSelection"));
			messageMap.put("companyName", jList.optString("companyName"));
			messageMap.put("isaId", jList.optString("isaId"));
			messageMap.put("scac", jList.optString("scac"));
			messageMap.put("duns", jList.optString("duns"));
			messageMap.put("icc", jList.optString("icc"));
			messageMap.put("dot", jList.optString("dot"));
			mList.clear();
			mList.add(messageMap);
			notifyDataSetChanged();
		} else {
			addRow();
		}
	}

	public void addRow() {
		HashMap<String, String> messageMap = new HashMap<String, String>();
		messageMap.put("companyTypeSpinnerSelection", "1");
		messageMap.put("isaQualifierTypeSpinnerSelection", "1");
		messageMap.put("isaUsageIndicatorSpinnerSelection", "1");
		messageMap.put("companyName", "Quality Distribution,Inc");
		messageMap.put("isaId", "IDTampa");
		messageMap.put("scac", "GCDC");
		messageMap.put("duns", "DUNGCDC");
		messageMap.put("icc", "iccGCDC");
		messageMap.put("dot", "dotGCDC");

		mList.add(messageMap);
		notifyDataSetChanged();

	}

	public JSONObject saveToJSON(JSONObject jInfo) throws JSONException {
		notifyDataSetChanged();
		mList.get(0)
				.put("companyName", holder.companyName.getText().toString());
		mList.get(0).put("isaId", holder.isaId.getText().toString());
		// There will only be one customer
		HashMap<String, String> customerMap = mList.get(0);
		jInfo = (JSONObject) JsonHelper.toJSON(customerMap);
		return jInfo;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = mInflater.inflate(R.layout.customers, null);
			holder = new ViewHolder(view);
			view.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		HashMap<String, String> customerMap = mList.get(position);
		holder.companyName.setText(customerMap.get("companyName"));
		holder.companyTypeSpinner.setSelection(Integer
				.parseInt((String) customerMap
						.get("companyTypeSpinnerSelection")));
		holder.isaQualifierTypeSpinner.setSelection(Integer
				.parseInt((String) customerMap
						.get("isaQualifierTypeSpinnerSelection")));
		holder.isaUsageIndicatorSpinner.setSelection(Integer
				.parseInt((String) customerMap
						.get("isaUsageIndicatorSpinnerSelection")));
		holder.isaId.setText(customerMap.get("isaId"));
		holder.scac.setText(customerMap.get("scac"));
		holder.duns.setText(customerMap.get("duns"));
		holder.icc.setText(customerMap.get("icc"));
		holder.dot.setText(customerMap.get("dot"));

		return view;
	}

	class ViewHolder {
		public EditText companyName;
		public Spinner companyTypeSpinner;
		public Spinner isaQualifierTypeSpinner;
		public EditText isaId;
		public Spinner isaUsageIndicatorSpinner;
		public EditText scac;
		public EditText duns;
		public EditText icc;
		public EditText dot;

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
						mList.get(0).put("companyName",
								companyName.getText().toString());
					}

				}

			});

			companyTypeSpinner = (Spinner) view
					.findViewById(R.id.companyTypeSpinner);
			companyTypeSpinner
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int position, long arg3) {
							mList.get(0).put("companyTypeSpinnerSelection",
									Integer.toString(position));

						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
							// Dont do anything as this was set initially
						}
					});

			isaQualifierTypeSpinner = (Spinner) view
					.findViewById(R.id.isaQualifierTypeSpinner);
			isaQualifierTypeSpinner
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int position, long arg3) {
							mList.get(0).put(
									"isaQualifierTypeSpinnerSelection",
									Integer.toString(position));

						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
							// Dont do anything as this was set initially
						}
					});

			isaId = (EditText) view.findViewById(R.id.isaId);
			// attach the onFocusChange listener to the EditText
			isaId.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						v.setBackgroundColor(Color.rgb(255, 248, 220));
					} else {
						// set the row background white
						v.setBackgroundColor(Color.rgb(255, 255, 255));
						mList.get(0).put("isaID", isaId.getText().toString());
					}

				}

			});

			isaUsageIndicatorSpinner = (Spinner) view
					.findViewById(R.id.isaUsageIndicatorSpinner);
			isaUsageIndicatorSpinner
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int position, long arg3) {

							if (position == enumisaUsageIndicator.P.ordinal()) {
								String t = enumisaUsageIndicator.P.name();
								MainActivity.setToast("P: " + t, 0);
							} else {
								String t = enumisaUsageIndicator.T.name();
								MainActivity.setToast("P: " + t, 0);

							}
							;
							mList.get(0).put(
									"isaUsageIndicatorSpinnerSelection",
									Integer.toString(position));

						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
							// Dont do anything as this was set initially
						}
					});

			scac = (EditText) view.findViewById(R.id.scac);
			// attach the onFocusChange listener to the EditText
			scac.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						// v.setBackgroundColor(Color.rgb(255, 248, 220));
						v.setBackgroundColor(Color.rgb(255, 248, 220));
					} else {
						// set the row background white
						v.setBackgroundColor(Color.rgb(255, 255, 255));
						mList.get(0).put("scac", scac.getText().toString());
					}

				}

			});

			duns = (EditText) view.findViewById(R.id.duns);
			// attach the onFocusChange listener to the EditText
			duns.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						v.setBackgroundColor(Color.rgb(255, 248, 220));
					} else {
						// set the row background white
						v.setBackgroundColor(Color.rgb(255, 255, 255));
						mList.get(0).put("duns", duns.getText().toString());
					}

				}

			});

			icc = (EditText) view.findViewById(R.id.icc);
			// attach the onFocusChange listener to the EditText
			icc.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						v.setBackgroundColor(Color.rgb(255, 248, 220));
					} else {
						// set the row background white
						v.setBackgroundColor(Color.rgb(255, 255, 255));
						mList.get(0).put("icc", icc.getText().toString());
					}

				}

			});

			dot = (EditText) view.findViewById(R.id.dot);
			// attach the onFocusChange listener to the EditText
			dot.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						v.setBackgroundColor(Color.rgb(255, 248, 220));
					} else {
						// set the row background white
						v.setBackgroundColor(Color.rgb(255, 255, 255));
						mList.get(0).put("dot", dot.getText().toString());
					}

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