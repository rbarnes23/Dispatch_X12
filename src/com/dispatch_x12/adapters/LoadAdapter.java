package com.dispatch_x12.adapters;

import java.util.ArrayList;
import java.util.HashMap;


import com.dispatch_x12.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class LoadAdapter extends SimpleAdapter {
	private int[] colors = new int[] { 0x3066FF66, 0x30FFFF6F };
	private int redColor = 0x30FF0000;
	private Context mContext;
	private ViewHolder holder;
	ArrayList<HashMap<String, String>> mJsonShipment;

	public LoadAdapter(Context context, ArrayList<HashMap<String, String>> msg) {
		super(context, msg, (Integer) null, null, null);
		mContext = context;
		mJsonShipment = msg;
	}


	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = mInflater.inflate(R.layout.master204, null);
			holder = new ViewHolder(view);
			view.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		int colorPos = position % colors.length;
		view.setBackgroundColor(colors[colorPos]);

		return view;
	}

	class ViewHolder {
		public TextView _id;
		public TextView driverSelected;
		public TextView spinnerStatus;
		public TextView contactInfo;
		public TextView specialInstructionsDetail;
		public TextView shipperName;
		public TextView shipperAddress1;
		public TextView shipperAddress2;
		public TextView shipperCity;
		public TextView shipperState;
		public TextView shipperPostalCode;
		public TextView billToName;
		public TextView billToAddress1;
		public TextView billToAddress2;
		public TextView billToCity;
		public TextView billToState;
		public TextView billToPostalCode;

		public ViewHolder(View view) {
			_id = (TextView) view.findViewById(R.id._id);
			driverSelected = (TextView) view.findViewById(R.id.driversSelected);
			spinnerStatus = (TextView) view.findViewById(R.id.spinnerStatus);
			contactInfo = (TextView) view.findViewById(R.id.contactInfo);
			specialInstructionsDetail = (TextView) view
					.findViewById(R.id.specialInstructionsDetail);
			shipperName = (TextView) view.findViewById(R.id.shipperName);
			shipperAddress1 = (TextView) view
					.findViewById(R.id.shipperAddress1);
			shipperAddress2 = (TextView) view
					.findViewById(R.id.shipperAddress2);
			shipperCity = (TextView) view.findViewById(R.id.shipperCity);
			shipperState = (TextView) view.findViewById(R.id.shipperState);
			shipperPostalCode = (TextView) view
					.findViewById(R.id.shipperPostalCode);
			billToName = (TextView) view.findViewById(R.id.billToName);
			billToAddress1 = (TextView) view.findViewById(R.id.billToAddress1);
			billToAddress2 = (TextView) view.findViewById(R.id.billToAddress2);
			billToCity = (TextView) view.findViewById(R.id.billToCity);
			billToState = (TextView) view.findViewById(R.id.billToState);
			billToPostalCode = (TextView) view
					.findViewById(R.id.billToPostalCode);

		}

	}
}
