package com.dispatch_x12;


import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class LayOutLeftDrawer extends Fragment {
	ViewGroup root;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// mContext = inflater.getContext();
		if (savedInstanceState == null) {
			root = (ViewGroup) inflater.inflate(R.layout.menu, null);
		}
		return root;
	}

	// OnButtonPressListener buttonListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// try {
		// buttonListener = (OnButtonPressListener) getActivity();
		// } catch (ClassCastException e) {
		// throw new ClassCastException(activity.toString()
		// + " must implement onButtonPressed");
		// }
	}

	void init(ViewGroup root) {
	}

	void setMessage(String msg) {
		String mMemberid = AppSettings.getMemberid();
		// try {
		// getSessions(msg);
		// setMemberInfo();
		// } catch (JSONException e) {
		// TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// listAdapter.notifyDataSetChanged();

	}

	// void setMessage(ArrayList<HashMap<String, CharSequence>> msg) {
	// // TextView txt = (TextView) root.findViewById(R.id.textView1);
	// // txt.setText(msg.obj.toString());
	// // msg.obj.toString()
	// intervalAdapter = new IntervalAdapter(mContext, msg,
	// R.layout.messagerowlist, new String[] { "intervalno",
	// "pacetext" }, new int[] { R.id.fullname, R.id.message });
	// mMessageList.setAdapter(intervalAdapter);
	//
	// // jsonIntervallist = msg;
	// intervalAdapter.notifyDataSetChanged();
	// }

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == newConfig.ORIENTATION_LANDSCAPE) {
			// MainActivity.setToast("LAND", 1);
		} else {
			// MainActivity.setToast("PORT", 1);
		}
	}


}