package com.dispatch_x12;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;

import com.edilibrary.Create210;
import com.edilibrary.Create997;

//import org.osmdroid.views.MapView;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
//import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;
//import android.widget.SimpleAdapter;
import android.widget.Spinner;
//import android.widget.SpinnerAdapter;
//import android.widget.Toast;
//import android.widget.AdapterView.OnItemClickListener;

public class LayOutRightDrawer extends Fragment {
	ViewGroup root;
	private static ListView mList;// Going to use this list for multiple
									// purposes
	private ArrayList<HashMap<String, String>> loadList = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> employeeList = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> vehicleList = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> companyList = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> mMessageType = new ArrayList<HashMap<String, String>>();

	private LoadListAdapter loadListAdapter;
	private Context mContext;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return root;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		root = (ViewGroup) inflater.inflate(R.layout.layout_loadlist, null);
		mList = (ListView) root.findViewById(R.id.listview);
	}

	/*
	 * Need to make setSpinnerFilter polymorphic so spinner filters can be
	 * changed based on adapter passed
	 */
	private void setSpinnerFilter(final String className) {
		Spinner statusSpinner = (Spinner) root.findViewById(R.id.spinnerStatus);
		statusSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				if (className.contentEquals("LoadListAdapter")) {
					String type = mMessageType.get(position).get("type");

					if (loadList.size() > 0 && loadList != null) {
						if (type.contentEquals("AI")) {
							loadListAdapter.getFilter().filter(null);
						} else {
							loadListAdapter.getFilter().filter(type);
						}
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// TODO Auto-generated method stub
			}
		});

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	void setMessage(int type, ArrayList<HashMap<String, String>> msg) {
		/*
		 * Dont use notifydatasetchanged because we can only have the original
		 * list size onceI also want to change the adapter based on what i want
		 * to put in this list...employees/vehicles,etc
		 */
		Spinner spinnerStatus = (Spinner) root.findViewById(R.id.spinnerStatus);
		LoadFilterAdapter loadFilterAdapter = new LoadFilterAdapter(mContext,
				mMessageType);
		if (type == Constant.LOAD) {
			mMessageType = loadFilterAdapter.addRows();

			spinnerStatus.setAdapter(loadFilterAdapter);

			loadList.clear();
			loadList.addAll(msg);
			loadListAdapter = new LoadListAdapter(mContext,
					R.layout.loadsrowlist, loadList);
			mList.setAdapter(loadListAdapter);
			String className = mList.getAdapter().getClass().getSimpleName();
			setSpinnerFilter(className);

		} else if (type == Constant.COMPANY) {
			companyList.clear();
			companyList.addAll(msg);
			String test = msg.toString();

		} else if (type == Constant.VEHICLE) {
			vehicleList.clear();
			vehicleList.addAll(msg);
			String test = msg.toString();
		}

	}

	private boolean sendUpdateMessage(Message msgToActivity) {
		return MainActivity.mUiHandler.sendMessage(msgToActivity);
	}

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