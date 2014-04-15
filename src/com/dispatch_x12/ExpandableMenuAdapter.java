package com.dispatch_x12;

import java.util.ArrayList;

import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ExpandableMenuAdapter extends BaseExpandableListAdapter {

	// private Context mContext;
	private ArrayList<HeaderInfo> mGroupList;
	private LayoutInflater inflater;

	public ExpandableMenuAdapter(Context context,
			ArrayList<HeaderInfo> mGroupList) {
		// this.mContext = context;
		this.mGroupList = mGroupList;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		ArrayList<DetailInfo> detailList = mGroupList.get(groupPosition)
				.getDetailList();
		return detailList.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View view, ViewGroup parent) {

		final DetailInfo detailInfo = (DetailInfo) getChild(groupPosition,
				childPosition);
		if (view == null) {
			view = inflater.inflate(R.layout.child_row, null);
		}

		TextView sequence = (TextView) view.findViewById(R.id.sequence);
		sequence.setText(detailInfo.getSequence().trim() + ") ");
		TextView childItem = (TextView) view.findViewById(R.id.childItem);
		// childItem.setBackgroundColor(colors[0]);
		childItem.setText(detailInfo.getName().trim());

		// set the ClickListener to handle the click event on child item
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Message msgToActivity = MainActivity.mUiHandler.obtainMessage();
				// Change....but just a prototype
				String detailName = detailInfo.getName().toLowerCase();
				if (detailName.contentEquals("employees")) {
					msgToActivity.what = Constant.EMPLOYEEMENU;
				} else if (detailName.contentEquals("company")) {
					msgToActivity.what = Constant.COMPANYMENU;
				} else if (detailName.contentEquals("vehicles")) {
					msgToActivity.what = Constant.VEHICLEMENU;
				} else if (detailName.contentEquals("rates")) {
					msgToActivity.what = Constant.RATESMENU;

				} else if (detailName.contentEquals("messages")) {
					msgToActivity.what = Constant.MESSAGEMENU;
				}

				if (msgToActivity.what > 0) {
					sendUpdateMessage(msgToActivity);
				}

			}
		});

		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {

		ArrayList<DetailInfo> detailList = mGroupList.get(groupPosition)
				.getDetailList();
		return detailList.size();

	}

	@Override
	public Object getGroup(int groupPosition) {
		return mGroupList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return mGroupList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isLastChild, View view,
			ViewGroup parent) {

		final HeaderInfo headerInfo = (HeaderInfo) getGroup(groupPosition);
		if (view == null) {
			// LayoutInflater inflater = (LayoutInflater)
			// context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.group_heading, null);
		}

		TextView heading = (TextView) view.findViewById(R.id.heading);
		heading.setText(headerInfo.getName());
		// set the ClickListener to handle the click event on child item
		// view.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View view) {
		// Toast.makeText(context, headerInfo.getName(),
		// Toast.LENGTH_SHORT).show();
		//
		//
		// }
		// });

		return view;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
	}

	/**
	 * This method Updates Main Activity with the message.
	 * 
	 * @param message
	 *            - The message to be updated.
	 * @return true false.
	 * @exception None.
	 * @see None
	 */
	private boolean sendUpdateMessage(Message msgToActivity) {

		return MainActivity.mUiHandler.sendMessage(msgToActivity);
	}

}