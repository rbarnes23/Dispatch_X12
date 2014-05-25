package com.dispatch_x12.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dispatch_x12.MainActivity;
import com.dispatch_x12.MessageProcessing;
import com.dispatch_x12.R;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.dispatch_x12.utilities.Constant;

public class LoadListAdapter extends BaseAdapter
		implements Filterable {
	private int[] colors = new int[] { 0x3066FF66, 0x30FFFF6F };
	private Context mContext;
	private ViewHolder holder;
	ArrayList<HashMap<String, String>> mList;
	ArrayList<HashMap<String, String>> mOriginalList = null;
	private ItemFilter mFilter = new ItemFilter();

	public LoadListAdapter(Context context, int resource,
			ArrayList<HashMap<String, String>> msg) {
		
		mContext = context;
		
		mList =msg;
		mOriginalList =new
				 ArrayList<HashMap<String,String>>();
		mOriginalList.addAll(msg);
	}

	
	@Override
	public Filter getFilter() {
		if (mFilter == null) {
			mFilter = new ItemFilter();
		}
		return mFilter;
	}

	private class ItemFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();			

			if (constraint != null && constraint.toString().length() > 0) {
				String filterString = constraint.toString().toLowerCase();

				ArrayList<HashMap<String, String>> filteredItems = new ArrayList<HashMap<String, String>>();
				
				//Store original list to list to be filtered
				final List<HashMap<String, String>> list = mOriginalList;
				int count = list.size();

				// Based on the status of the dropdown i.e. delivered,All messages,etc
				String filterableString;
				for (int i = 0; i < count; i++) {
					filterableString = list.get(i).get("status"); 
					if (filterableString.toLowerCase().contentEquals(filterString)) {
						filteredItems.add(list.get(i));
					}
				}

				results.values = filteredItems;
				results.count = filteredItems.size();
			} else {
				synchronized (this) {
					results.values = mOriginalList;
					results.count = mOriginalList.size();
				}
			}
			return results;
		}

		// Published results are the contents of the filtered list
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			mList = (ArrayList<HashMap<String, String>>) results.values;
			notifyDataSetChanged();

		}
	}


	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = mInflater.inflate(R.layout.loadsrowlist, null);
			holder = new ViewHolder(view);
			view.setTag(holder);

		} else {
			holder = (ViewHolder) view.getTag();
		}		
		final String loadInfo = mList.get(position).get("message");
		holder._id.setText(mList.get(position).get("_id"));
		holder.identificationNo.setText(mList.get(position).get(
				"identificationNo"));
		holder.status.setText(mList.get(position).get("status"));
		// holder.stopInformation.setTag(position);
		// holder.stopInformation.setText(Integer.toString(position + 1) + ": "
		// + mList.get(position).get("_id"));

		// Trimming the string is just for testing
		String stopInfo = MessageProcessing.createStopInfo(loadInfo,
				(String) holder._id.getText(), mContext);
		if (stopInfo.contentEquals(loadInfo)) {
			holder.stopInformation.setText("From: "
					+ mList.get(position).get("fullname"));

		} else {
			holder.stopInformation.setText(Integer.toString(position + 1)
					+ ": " + mList.get(position).get("_id"));

		}
		holder.message.setText(stopInfo);
		// holder.message.setText(loadInfo.substring(0, 80));

		final String pos = Integer.toString(position);
		holder.message.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Message msgToActivity = MainActivity.mUiHandler
						.obtainMessage(Constant.LOADDATA);
				msgToActivity.arg1 = 1;
				Bundle bundle = new Bundle();
				bundle.putString("message", loadInfo);
				bundle.putString("_id", (String) holder._id.getText());
				bundle.putString("status", (String) holder.status.getText());
				bundle.putString("position",  pos);
				String rates = mList.get(Integer.parseInt(pos)).get("rates");
				bundle.putString("rates", rates);
				msgToActivity.setData(bundle);
				sendUpdateMessage(msgToActivity);
			}
		});
		
		//Set the color of the list item every other row to make each item more readable
		int colorPos = position % colors.length;
		view.setBackgroundColor(colors[colorPos]);

		return view;
	}

	/*
	 * View holder keeps is used so we dont have to do a findviewbyid for each row
	 */
	class ViewHolder {
		public TextView _id;
		public TextView identificationNo;
		public TextView status;
		public TextView message;
		public TextView stopInformation;

		public ViewHolder(View view) {
			_id = (TextView) view.findViewById(R.id._id);
			identificationNo = (TextView) view
					.findViewById(R.id.identificationNo);
			status = (TextView) view
					.findViewById(R.id.status);

			message = (TextView) view.findViewById(R.id.message);
			stopInformation = (TextView) view
					.findViewById(R.id.stopInformation);
			message.setClickable(true);
		}

	}

	private boolean sendUpdateMessage(Message msgToActivity) {

		return MainActivity.mUiHandler.sendMessage(msgToActivity);
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

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
}