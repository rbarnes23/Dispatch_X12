package com.dispatch_x12;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tokenlibrary.Token;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ExpandableListView;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
public class LayOutLeftDrawer2013119 extends Fragment implements OnClickListener {

	private LinkedHashMap<String, HeaderInfo> mGroups = new LinkedHashMap<String, HeaderInfo>();
	private ArrayList<HeaderInfo> groupList = new ArrayList<HeaderInfo>();
	private ArrayList<HashMap<String, String>> mGroupList = new ArrayList<HashMap<String, String>>();
	private List<String> mDetailList = new ArrayList<String>();

	private ExpandableListAdapter listAdapter;
	private ExpandableListView myList;

	//private DrawView drawView;
	private String mMemberid;
	private static Context sContext;
	private Handler mHandler = new Handler();
	boolean mRedraw = false;
	int mAssetFilesCount = 0;
	AssetManager assetManager;

	private SpinnerAdapter spinnerAdapter;
	private static List<Integer> sMemberIds = new ArrayList<Integer>();
	InputMethodManager imm;
	private CustomAutoCompleteTextView mAutoComplete;
	String mMessage;
	String mSearch;

	ViewGroup root;
	private Context mContext;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext=getActivity();

//		// get reference to the ExpandableListView
//		myList = (ExpandableListView) root.findViewById(R.id.myList);
//		// create the adapter by passing your ArrayList data
//		listAdapter = new MyListAdapter(getActivity(), groupList);
//		// attach the adapter to thpe list
//		myList.setAdapter(listAdapter);

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//mContext = inflater.getContext();		
		if (savedInstanceState == null) {
			root = (ViewGroup) inflater.inflate(R.layout.friends_main, null);
		}
		return root;
	}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	void init(ViewGroup root) {
		//imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mAutoComplete = (CustomAutoCompleteTextView) root.findViewById(R.id.memberEntry);
		//mAutoComplete.setAdapter(adapter);
		mAutoComplete.setThreshold(1);
		mAutoComplete.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				//Object[] params = new Object[2];
				//params[0] = mMemberid;
				//params[1] = s.toString();
				mSearch=s.toString();
				
				new FindMembers().execute();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) { // TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) { // TODO Auto-generated method stub
				String search = s.toString();

			}

		});

		// add new item to the List
		Button addmembertogroup = (Button) root.findViewById(R.id.addmembertogroup);
		addmembertogroup.setOnClickListener(this);

		// listener for child row click
		myList.setOnChildClickListener(myListItemClicked);
		// listener for group heading click
		myList.setOnGroupClickListener(myListGroupClicked);
		// spinner.requestFocus();

	}

	void setMessage(String msg) {
		mMemberid=AppSettings.getMemberid();
			try {
				getSessions(msg);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			listAdapter.notifyDataSetChanged();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == newConfig.ORIENTATION_LANDSCAPE) {
		//	MainActivity.setToast("LAND", 1);
		} else {
		//	MainActivity.setToast("PORT", 1);
		}
	}
	
	

	private void addItemstoSpinner() {
		Spinner mSpinner = (Spinner) root.findViewById(R.id.group);
		spinnerAdapter = new SpinnerAdapter(mContext, mGroupList,
				R.layout.spinner_layout, new String[] { "_id", "group","sessionNo" },
				new int[] { R.id.spinnerId, R.id.spinnerTarget,R.id.spinnerNo });

		spinnerAdapter.setDropDownViewResource(R.layout.spinner_layout);
		mSpinner.setAdapter(spinnerAdapter);

		//mSpinner.setOnItemSelectedListener(new OnItemSelectedListener());
		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				TextView groupNo = (TextView) selectedItemView.findViewById(R.id.spinnerNo);
				TextView groupName = (TextView) selectedItemView.findViewById(R.id.spinnerTarget);
				TextView groupId = (TextView) selectedItemView.findViewById(R.id.spinnerId);
				
				String s = groupId.getText().toString();
				//MainActivity.setToast(s,LONG);
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }


		});
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}


	
	public void onClick(View v) {
		if (v.getId()==R.id.modifygroup) {
			EditText sessionName = (EditText) root.findViewById(R.id.sessionName);
			EditText sessionNo = (EditText) root.findViewById(R.id.sessionNo);
			String sName = sessionName.getText().toString();
			String sNo = sessionNo.getText().toString();
			Token updateToken = new Token();
			String sToken = updateToken.createSessionToken(mMemberid, sName, sNo);
			sendData(sToken);
//			MainActivity.setToast(sToken,Toast.LENGTH_LONG);
			
		}
		else if (v.getId()==R.id.addmembertogroup) {
			Spinner spinner = (Spinner) root.findViewById(R.id.group);
			//String group = spinner.getSelectedItem().toString();
			HashMap d = (HashMap) spinner.getSelectedItem();
			String group=d.get("group").toString();
			

			// String id = (String) memberMap.get("members");
			String memberName = mAutoComplete.getText().toString();
				// add a new item to the list
			int groupPosition = addMemberToGroup(group, memberName);
			HashMap<String, String> selectedCourse = (HashMap<String, String>) spinner
					.getSelectedItem();
			String courseid = selectedCourse.get("_id");
			String newMemberId = mAutoComplete.getSelectionId();
			Token newCourseMember = new Token();
			String newmember = newCourseMember.createSessionUpDateToken(
					mMemberid, courseid, newMemberId);

			sendData(newmember);
//			MainActivity.setToast(newmember, SHORT);
			// notify the list so that changes can take effect
			listAdapter.notifyDataSetChanged();

			// collapse all groups
			collapseAll();
			// expand the group where item was just added
			myList.expandGroup(groupPosition);
			// set the current group to be selected so that it becomes visible
			myList.setSelectedGroup(groupPosition);
			
		}
/*		switch (v.getId()) {
		case R.id.modifygroup:
			EditText sessionName = (EditText) findViewById(R.id.sessionName);
			EditText sessionNo = (EditText) findViewById(R.id.sessionNo);
			String sName = sessionName.getText().toString();
			String sNo = sessionNo.getText().toString();
			Token updateToken = new Token();
			String sToken = updateToken.createSessionToken(mMemberid, sName, sNo);
			sendData(sToken);
			MainActivity.setToast(sToken,Toast.LENGTH_LONG);
			break;
		// add entry to the List
		case R.id.addmembertogroup:

			Spinner spinner = (Spinner) findViewById(R.id.group);
			//String group = spinner.getSelectedItem().toString();
			HashMap d = (HashMap) spinner.getSelectedItem();
			String group=d.get("group").toString();
			

			// String id = (String) memberMap.get("members");
			String memberName = mAutoComplete.getText().toString();
				// add a new item to the list
			int groupPosition = addMemberToGroup(group, memberName);
			HashMap<String, String> selectedCourse = (HashMap<String, String>) spinner
					.getSelectedItem();
			String courseid = selectedCourse.get("_id");
			String newMemberId = mAutoComplete.getSelectionId();
			Token newCourseMember = new Token();
			String newmember = newCourseMember.createSessionUpDateToken(
					mMemberid, courseid, newMemberId);

			sendData(newmember);
			MainActivity.setToast(newmember, SHORT);
			// notify the list so that changes can take effect
			listAdapter.notifyDataSetChanged();

			// collapse all groups
			collapseAll();
			// expand the group where item was just added
			myList.expandGroup(groupPosition);
			// set the current group to be selected so that it becomes visible
			myList.setSelectedGroup(groupPosition);

			break;

		}
*/	}

	// method to expand all groups
	private void expandAll() {
		int count = listAdapter.getGroupCount();
		for (int i = 0; i < count; i++) {
			myList.expandGroup(i);
		}
	}

	// method to collapse all groups
	private void collapseAll() {
		int count = listAdapter.getGroupCount();
		for (int i = 0; i < count; i++) {
			myList.collapseGroup(i);
		}
	}


	// our child listener for expandable listviews
	private OnChildClickListener myListItemClicked = new OnChildClickListener() {

		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {

			// get the group header
			HeaderInfo headerInfo = groupList.get(groupPosition);
			// get the child info
			DetailInfo detailInfo = headerInfo.getDetailList().get(
					childPosition);
			  TextView childItem = (TextView) v.findViewById(R.id.childItem);
			  Drawable vback=v.getBackground();
			  if (vback.getLevel()==0){
			  vback.setLevel(1);}else{vback.setLevel(0);}
			// display it or do something with it
//			  MainActivity.setToast(headerInfo.getName() + "/"+ detailInfo.getName(), LONG);
			return false;
		}

	};

	// our group listener
	private OnGroupClickListener myListGroupClicked = new OnGroupClickListener() {

		public boolean onGroupClick(ExpandableListView parent, View v,
				int groupPosition, long id) {

			// get the group header
			HeaderInfo headerInfo = groupList.get(groupPosition);
			// display it or do something with it
//			MainActivity.setToast(headerInfo.getName().toString(), LONG);

			return false;
		}

	};




	public static void sendData(String aMessage) {
//NEED TO HAVE THIS		MainActivity.sendMessage(aMessage);
	};

	public class FindMembers extends
	AsyncTask<Object, Void, ArrayList<HashMap<String, String>>> {
//private Context context;// =MainActivity.getContext();
//ArrayList<HashMap<String, String>> memberList =new ArrayList<HashMap<String, String>>();;
String mTokenId;
@Override
protected void onPreExecute() {

	Token findmember = new Token();
	String findMember = findmember.findMember(mSearch, mMemberid);
//REMOVED	LayOutLeftDrawer.sendData(findMember);
	mTokenId = findmember.getID();
}

@Override
protected ArrayList<HashMap<String, String>> doInBackground(
		Object... params) {
	// Send the search String
	//String memberid = (String) params[0];
	//String search = (String) params[1];
	//context = (Context) params[2];
//	Token findmember = new Token();
//	String findMember = findmember.findMember(mSearch, mMemberid);
//	FriendsActivity.sendData(findMember);
//	mTokenId = findmember.getID();
	ArrayList<HashMap<String, String>> memberList = null;

	// Now get the member info returned;
	int mBreak = 0;
	for (int j = 1; j < 30; j++) {
		try {
			memberList = MessageProcessing.getMemberList();
			int size = memberList.size();
			for (int i = 0; i < size; i++) {
				if (mTokenId.contentEquals(memberList.get(i)
						.get("tokenid"))) {
					mBreak = 1;
//					memberList.add(memberList.get(i));
				}
//			if (mBreak == 1) {
//					break;// Break out of the i loop
//				}

			}
			if (mBreak == 1) {
				break; // Break out of the j loop
			}

			Thread.sleep(500);
		} catch (InterruptedException e) {
			 e.printStackTrace();
		}
		
	}
	return memberList;	

}


protected void onPostExecute(ArrayList<HashMap<String, String>> memberList) {
	// Keys used in Hashmap
	String[] from = { "id", "name" };

	// Ids of views in listview_layout
	int[] to = { R.id.spinnerId, R.id.spinnerTarget };

	SimpleAdapter adapter = new SimpleAdapter(sContext, memberList,
			R.layout.spinner_layout, from, to);

	
	mAutoComplete.setAdapter(adapter);
}

}	
	
/*	public static String getData(String aType) {
		return sChat.getData(aType);
	}
*/
	public static List<Integer> getMemberids() {
		return sMemberIds;
	}




	private void getSessions(String _message) throws JSONException {
		// get reference to the ExpandableListView
		myList = (ExpandableListView) root.findViewById(R.id.myList);
		// create the adapter by passing your ArrayList data
		listAdapter = new ExpandableListAdapter(mContext, groupList);
		// attach the adapter to thpe list
		myList.setAdapter(listAdapter);

		if (_message != null) {

			JSONArray jGroupArray = new JSONArray(_message);

			String mMessage = "";
			int len = jGroupArray.length();
			for (int j = 0; j < len; j++) {
				String group = jGroupArray.getJSONObject(j).getString(
						"sessionName")
						+ " "
						+ jGroupArray.getJSONObject(j).getString("sessionNo")
						+ "  ";
				JSONObject jgroupobject = jGroupArray.getJSONObject(j);
				HashMap<String, String> fieldsMap = new HashMap<String, String>();
				Iterator iter = jgroupobject.keys();
				while (iter.hasNext()) {
					String key = (String) iter.next();
					String value = jgroupobject.getString(key);
					fieldsMap.put(key, value);
				}
				fieldsMap.put("group",group);
				// Add all group information to the grouplist
				mGroupList.add(fieldsMap);
				// Detail Array has info on members that belong to group
				JSONArray jDetailArray = new JSONArray(jGroupArray
						.getJSONObject(j).getString("members"));
				for (int k = 0; k < jDetailArray.length(); k++) {
					String detail = jDetailArray.getJSONObject(k).optString(
							"id")
							+ " "
							+ jDetailArray.getJSONObject(k).optString("name")
							+ "  ";
					mDetailList.add(detail);
					addMemberToGroup(group, detail);
				}
				mMessage += group;
			}
			mHandler.post(new Runnable() {
				@Override
				public void run() {

					addItemstoSpinner();

					//expandAll();

				}
			});
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