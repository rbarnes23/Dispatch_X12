package com.dispatch_x12;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

public class SpinnerAdapter extends SimpleAdapter {
	// Resources res;
	// private int[] colors = new int[] { 0x3066FF66, 0x30FFFF6F };
	private String mSession;
	private int mSelectedPosition;

	public String getSessionId(int position) {
		return mSession;
	}

	public SpinnerAdapter(Context context,
			ArrayList<HashMap<String, String>> mMemberList, int resource,
			String[] from, int[] to) {
		super(context, mMemberList, resource, from, to);
	//	mSession = AppSettings.getGroupId();
		// ArrayList<HashMap<String, String>> memberlist=mMemberList;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		/*
		 * Spinner spinner = (Spinner) parent; CheckBox groupCheckBox=(CheckBox)
		 * parent.findViewById(R.id.checkboxheader); if
		 * (groupCheckBox.isChecked()){groupCheckBox.setChecked(false);}else
		 * {groupCheckBox.setChecked(true);}
		 */// int colorPos = position % colors.length;

		// view.setBackgroundColor(colors[colorPos]);

		return view;
	}

}