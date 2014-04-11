/**
 * 
 */
package com.dispatch_x12;

import java.util.ArrayList;
import java.util.HashMap;

import com.tokenlibrary.Token;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.SimpleAdapter;

/**
 * @author Rick
 * 
 */
public class FindMembers extends
		AsyncTask<Object, Void, ArrayList<HashMap<String, String>>> {
	private Context context;// =MainActivity.getContext();
	private AutoCompleteTextView mAutoComplete;
	ArrayList<HashMap<String, String>> memberList =new ArrayList<HashMap<String, String>>();;
	String mTokenId;

	@Override
	protected ArrayList<HashMap<String, String>> doInBackground(
			Object... params) {
		// Send the search String
		String memberid = (String) params[0];
		String search = (String) params[1];
		context = (Context) params[2];
		mAutoComplete = (AutoCompleteTextView) params[3];
		Token findmember = new Token();
		String findMember = findmember.findMember(search, memberid);
		//REMOBED TEST FriendsActivity.sendData(findMember);
		mTokenId = findmember.getID();

		// Now get the member info returned;
		int mBreak = 0;
		for (int j = 1; j < 30; j++) {
			try {

				ArrayList<HashMap<String, String>> mMemberList = MessageProcessing.getMemberList();
				int size = mMemberList.size();
				for (int i = 0; i < size; i++) {
					if (mTokenId.contentEquals(mMemberList.get(i)
							.get("tokenid"))) {
						mBreak = 1;
						memberList.add(mMemberList.get(i));
					}
//				if (mBreak == 1) {
//						break;// Break out of the i loop
//					}

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

		SimpleAdapter adapter = new SimpleAdapter(context, memberList,
				R.layout.spinner_layout, from, to);

		
		mAutoComplete.setAdapter(adapter);
	}

}
