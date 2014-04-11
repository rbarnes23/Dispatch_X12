package com.dispatch_x12;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LoadFilterAdapter extends BaseAdapter {
	private Context mContext;
	private ViewHolder holder;
	ArrayList<HashMap<String, String>> mList;

	public LoadFilterAdapter(Context context,
			ArrayList<HashMap<String, String>> mMessageType) {
		super();
		mList = mMessageType;
		mContext = context;
	}

	public ArrayList<HashMap<String, String>> addRows() {
		HashMap<String, String> typeMap = new HashMap<String, String>();
		typeMap.put("type", "AI");
		typeMap.put("description", "All Items");
		mList.add(typeMap);
		typeMap = new HashMap<String, String>();
		typeMap.put("type", "M");
		typeMap.put("description", "Messages Only");
		mList.add(typeMap);
		typeMap = new HashMap<String, String>();
		typeMap.put("type", "N");
		typeMap.put("description", "New Tenders");
		mList.add(typeMap);
		typeMap = new HashMap<String, String>();
		typeMap.put("type", "A");
		typeMap.put("description", "Accepted");
		mList.add(typeMap);
		typeMap = new HashMap<String, String>();
		typeMap.put("type", "D");
		typeMap.put("description", "Declined");
		mList.add(typeMap);
		typeMap = new HashMap<String, String>();
		typeMap.put("type", "I");
		typeMap.put("description", "In Transit");
		mList.add(typeMap);
		typeMap = new HashMap<String, String>();
		typeMap.put("type", "DL");
		typeMap.put("description", "Delivered");
		mList.add(typeMap);
		typeMap = new HashMap<String, String>();
		typeMap.put("type", "IN");
		typeMap.put("description", "Invoiced");
		mList.add(typeMap);
		typeMap = new HashMap<String, String>();
		typeMap.put("type", "PD");
		typeMap.put("description", "Paid");
		mList.add(typeMap);

		notifyDataSetChanged();
		return mList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getListView(position, convertView, parent);
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getListView(position, convertView, parent);
	}

	public View getListView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = mInflater.inflate(R.layout.spinner_filter, null);
			holder = new ViewHolder(view);
			view.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.row = position;
		HashMap<String, String> typeMap = mList.get(position);
		holder.type.setText(typeMap.get("type"));
		holder.description.setText(typeMap.get("description"));

		return view;

	}

	class ViewHolder {
		public TextView type;
		public TextView description;
		public int row;

		public ViewHolder(View view) {

			type = (TextView) view.findViewById(R.id.type);
			description = (TextView) view.findViewById(R.id.description);

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