package com.dispatch_x12;

import java.util.ArrayList;
import java.util.HashMap;

import com.dispatch_x12.MainActivity;
import com.dispatch_x12.R;
import com.dispatch_x12.adapters.CompanyListAdapter;
import com.dispatch_x12.adapters.LoadFilterAdapter;
import com.dispatch_x12.adapters.LoadListAdapter;
import com.dispatch_x12.adapters.UserListAdapter;
import com.dispatch_x12.adapters.VehicleListAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Spinner;
import com.dispatch_x12.utilities.Constant;


public class LayOutRightDrawer extends Fragment {
	ViewGroup root;
	private static ListView mList;// Going to use this list for multiple
									// purposes
	private ArrayList<HashMap<String, String>> mLoadList = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> mEmployeeList = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> mVehicleList = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> mCompanyList = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> mMessageType = new ArrayList<HashMap<String, String>>();

	private LoadListAdapter mLoadListAdapter;
	private CompanyListAdapter mCompanyListAdapter;
	private VehicleListAdapter mVehicleListAdapter;
	private UserListAdapter mUserListAdapter;

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

					if (mLoadList.size() > 0 && mLoadList != null) {
						if (type.contentEquals("AI")) {
							mLoadListAdapter.getFilter().filter(null);
						} else {
							mLoadListAdapter.getFilter().filter(type);
						}
					}
				}else
				if (className.contentEquals("CompanyListAdapter")) {
					String type = mMessageType.get(position).get("type");

					if (mCompanyList.size() > 0 && mCompanyList != null) {
						//if (type.contentEquals("AI")) {
							mCompanyListAdapter.getFilter().filter(null);
						//} else {
						//	companyListAdapter.getFilter().filter(type);
						//}
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

	public void setMessage(int type, ArrayList<HashMap<String, String>> msg) {
		/*
		 * Dont use notifydatasetchanged because we can only have the original
		 * list size onceI also want to change the adapter based on what i want
		 * to put in this list...employees/vehicles,etc
		 */
		Spinner spinnerStatus = (Spinner) root.findViewById(R.id.spinnerStatus);
		LoadFilterAdapter loadFilterAdapter = new LoadFilterAdapter(mContext,
				mMessageType);
		if (type == Constant.LOADDATA) {
			mMessageType = loadFilterAdapter.addRows();

			spinnerStatus.setAdapter(loadFilterAdapter);

			mLoadList.clear();
			mLoadList.addAll(msg);
			mLoadListAdapter = new LoadListAdapter(mContext,
					R.layout.loadsrowlist, mLoadList);
			mList.setAdapter(mLoadListAdapter);
			String className = mList.getAdapter().getClass().getSimpleName();
			setSpinnerFilter(className);
		} else if (type == Constant.COMPANYDATA) {
			mMessageType = loadFilterAdapter.addRows();

			spinnerStatus.setAdapter(loadFilterAdapter);
			mCompanyList.clear();

			mCompanyList.addAll(msg);
			mCompanyListAdapter = new CompanyListAdapter(mContext,
					R.layout.loadsrowlist, mCompanyList);
			mList.setAdapter(mCompanyListAdapter);
			String className = mList.getAdapter().getClass().getSimpleName();
			setSpinnerFilter(className);

		} else if (type == Constant.VEHICLEDATA) {
			mMessageType = loadFilterAdapter.addRows();

			spinnerStatus.setAdapter(loadFilterAdapter);

			mVehicleList.clear();
			mVehicleList.addAll(msg);
			mVehicleListAdapter = new VehicleListAdapter(mContext,
					R.layout.loadsrowlist, mVehicleList);
			mList.setAdapter(mVehicleListAdapter);
			String className = mList.getAdapter().getClass().getSimpleName();
			setSpinnerFilter(className);

		} else if (type == Constant.EMPLOYEEDATA) {
			mMessageType = loadFilterAdapter.addRows();

			spinnerStatus.setAdapter(loadFilterAdapter);

			mEmployeeList.clear();
			mEmployeeList.addAll(msg);
			mUserListAdapter = new UserListAdapter(mContext,
					R.layout.loadsrowlist, mEmployeeList);
			mList.setAdapter(mUserListAdapter);
			String className = mList.getAdapter().getClass().getSimpleName();
			setSpinnerFilter(className);
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