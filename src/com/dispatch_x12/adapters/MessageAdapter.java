package com.dispatch_x12.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import com.dispatch_x12.MainActivity;
import com.dispatch_x12.R;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.dispatch_x12.utilities.Constant;

public class MessageAdapter extends SimpleAdapter {
	private int[] colors = new int[] { 0x3066FF66, 0x30FFFF6F };
	private Context mContext;
	private ViewHolder holder;
	ArrayList<HashMap<String, String>> msgList;

	public MessageAdapter(Context context,
			ArrayList<HashMap<String, String>> msg, int resource,
			String[] from, int[] to) {
		super(context, msg, resource, from, to);
		mContext = context;
		msgList = msg;
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
			view = mInflater.inflate(R.layout.messagerowlist, null);
			holder = new ViewHolder(view);
			view.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder._id.setTag(position);
		holder._id.setText(msgList.get(position).get("_id"));
		holder.identificationNo.setText(msgList.get(position).get(
				"identificationNo"));

		holder.fullName.setTag(position);
		holder.fullName.setText(Integer.toString(position + 1));
		final String test = msgList.get(position).get("message");
		// Trimming the string is just for testing
		holder.message.setText(test.substring(0, 80));

		final String pos = Integer.toString(position);
		holder.message.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Message msgToActivity = MainActivity.mUiHandler.obtainMessage(
						Constant.MESSAGE, test);
				msgToActivity.arg1 = Integer.parseInt((String) holder._id
						.getText());
				sendUpdateMessage(msgToActivity);
			}
		});

		holder.signedReceiver.setTag(position);
		holder.signedReceiver.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				Message msgToActivity = MainActivity.mUiHandler.obtainMessage(
						Constant.STOPINFO, test);
				Bundle bundle = new Bundle();
				bundle.putString("_id", (String) holder._id.getText());
				bundle.putString("identificationNo",
						(String) holder.identificationNo.getText());
				bundle.putString("position", (String) pos);
				msgToActivity.setData(bundle);

				msgToActivity.arg1 = Integer.parseInt(pos);// need to send
															// object id of
															// selected order
				sendUpdateMessage(msgToActivity);
			}
		});

		int colorPos = position % colors.length;
		view.setBackgroundColor(colors[colorPos]);

		return view;
	}

	class ViewHolder {
		public TextView _id;
		public TextView identificationNo;
		public TextView message;
		public TextView fullName;
		public Button signedReceiver;

		public ViewHolder(View view) {
			_id = (TextView) view.findViewById(R.id._id);
			identificationNo = (TextView) view
					.findViewById(R.id._identificationNo);
			message = (TextView) view.findViewById(R.id.message);
			fullName = (TextView) view.findViewById(R.id.fullname);
			signedReceiver = (Button) view.findViewById(R.id.signedReceiver);
			message.setClickable(true);

		}

	}

	private boolean sendUpdateMessage(Message msgToActivity) {

		return MainActivity.mUiHandler.sendMessage(msgToActivity);
	}

}
