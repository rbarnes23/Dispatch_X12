package com.dispatch_x12;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class LayOutLeftDrawer extends Fragment {
	ViewGroup root;
	Context mContext;
	ExpandableMenuAdapter listAdapter;
	ExpandableListView menuList;
	private LinkedHashMap<String, HeaderInfo> mGroups = new LinkedHashMap<String, HeaderInfo>();
	private ArrayList<HeaderInfo> groupList = new ArrayList<HeaderInfo>();

	// Create ArrayList to hold parent Items and Child Items
	//private ArrayList<String> parentItems = new ArrayList<String>(); 
	//private ArrayList<Object> childItems = new ArrayList<Object>();
	// private ArrayList<HashMap<String, String>> mGroupList = new
	// ArrayList<HashMap<String, String>>();
	// private List<String> mDetailList = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = inflater.getContext();
		if (savedInstanceState == null) {
			root = (ViewGroup) inflater.inflate(R.layout.menu, null);
			
			// Create Expandable List and set it's properties
			menuList = (ExpandableListView) root
					.findViewById(R.id.expandableList);

			menuList.setDividerHeight(1);
//	        menuList.setGroupIndicator(null);
	        menuList.setClickable(true);
	        
//	        listAdapter = new MyExpandableAdapter(parentItems, childItems);
//		      listAdapter.setInflater(inflater, mContext); 
			addMemberToGroup("Messages", "Messages");
			addMemberToGroup("Forms", "204");
			addMemberToGroup("Forms", "210");
			addMemberToGroup("Maintenance", "Company");
			addMemberToGroup("Maintenance", "Employees");
			addMemberToGroup("Maintenance", "Vehicles");
			addMemberToGroup("Maintenance", "Rates");

			addMemberToGroup("Settings", "Settings");
			addMemberToGroup("Quit", "Quit");

			// create the adapter by passing your ArrayList data
			listAdapter = new ExpandableMenuAdapter(mContext, groupList);

			// attach the adapter to thpe list
			menuList.setAdapter(listAdapter);
	        listAdapter.notifyDataSetChanged();
			menuList.expandGroup(2);

		//expandAll();
		}
		return root;
	}

	// method to expand all groups
	private void expandAll() {
		int count = listAdapter.getGroupCount();
		for (int i = 0; i < count; i++) {
			menuList.expandGroup(i);
		}
	}

	// method to collapse all groups
	private void collapseAll() {
		int count = listAdapter.getGroupCount();
		for (int i = 0; i < count; i++) {
			menuList.collapseGroup(i);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	void init(ViewGroup root) {
	}

	void setMessage(String msg) {
		String mMemberid = AppSettings.getMemberid();

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

	// here we maintain members in various groups
	private int addMemberToGroup(CharSequence _group, String _memberName) {

		int groupPosition = 0;
		HeaderInfo headerInfo = null;

		// check the hash map if the group already exists
		headerInfo = mGroups.get(_group);
		// add the group if doesn't exists
		if (headerInfo == null) {

			headerInfo = new HeaderInfo();
			headerInfo.setName(_group);
			mGroups.put((String) _group, headerInfo);
			groupList.add(headerInfo);

		}

		// get the children for the group
		ArrayList<DetailInfo> detailList = headerInfo.getDetailList();
		// size of the children list
		int listSize = detailList.size();
		// add to the counter
		listSize++;

		// create a new child and add that to the group
		DetailInfo detailInfo = new DetailInfo();
		detailInfo.setSequence(String.valueOf(listSize));
		detailInfo.setName(_memberName);
		detailList.add(detailInfo);
		headerInfo.setDetailList(detailList);

		// find the group position inside the list
		groupPosition = groupList.indexOf(headerInfo);
		return groupPosition;
	}

}