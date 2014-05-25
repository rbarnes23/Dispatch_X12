package com.dispatch_x12.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import com.dispatch_x12.R;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MemberAdapter extends SimpleAdapter {
	Context mContext;
	ArrayList<HashMap<String, CharSequence>> mMemberlist;
	private int[] colors = new int[] { 0x3066FF66, 0x30FFFF6F };

	public MemberAdapter(Context context,
			ArrayList<HashMap<String, CharSequence>> messagelist, int resource,
			String[] from, int[] to) {
		super(context, messagelist, resource, from, to);
		mContext = context;
		mMemberlist = messagelist;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}


	@Override
	public int getCount() {
		return mMemberlist.size();
	}

	@Override
	public Object getItem(int pos) {
		return mMemberlist.get(pos);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;// super.getView(position, convertView, parent);

		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = mInflater.inflate(R.layout.messagerowlist, null);
			holder = new ViewHolder(view);

			view.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.membercheckbox.setTag(position);

/*		if (checked == -1) {
			holder.membercheckbox.setChecked(true);

		} else if (checked == -2) {
			holder.membercheckbox.setChecked(false);
		} else {
*/
		Object chk = (Object) mMemberlist.get(position).get(
				"membercheckbox");
//		CheckBox chk = (CheckBox) mMemberlist.get(position).get(
//					"membercheckbox");
			holder.membercheckbox.setChecked((Boolean) chk);

//		}

		holder.fullname.setText((CharSequence) mMemberlist.get(position).get(
				"fullname"));
		holder.message.setText((CharSequence) mMemberlist.get(position).get(
				"message"));

		/*
		 * else if (position==checked){ boolean ischecked=
		 * (chk.isChecked()?false:true); chk.setChecked(ischecked);
		 * 
		 * }
		 */
		// chk.setVisibility(position >= 0 ? View.VISIBLE
		// : View.GONE);

		int colorPos = position % colors.length;
		view.setBackgroundColor(colors[colorPos]);

		return view;
	}

	class ViewHolder {
		public CheckBox membercheckbox;
		public TextView message;
		public TextView fullname;

		public ViewHolder(View view) {
			message = (TextView) view.findViewById(R.id.message);
			fullname = (TextView) view.findViewById(R.id.fullname);
			membercheckbox = (CheckBox) view.findViewById(R.id.membercheckbox);
			/*
			 * membercheckbox.setOnClickListener(new OnClickListener() {
			 * 
			 * public void onClick(View v) { // TODO Auto-generated method stub
			 * CheckBox cb = (CheckBox) v; if (cb.isChecked()){ //int id =
			 * chk.getId(); cb.setChecked(true); } else { cb.setChecked(false);
			 * } } });
			 */
		}
	}

}
