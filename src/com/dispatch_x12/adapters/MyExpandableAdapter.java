/**
 * 
 */
package com.dispatch_x12.adapters;

import java.util.ArrayList;

import com.dispatch_x12.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Rick
 * 
 */
public class MyExpandableAdapter extends BaseExpandableListAdapter {
	private Context context;
	private ArrayList<Object> childtems;
	private LayoutInflater inflater;
	private ArrayList<String> parentItems, child;

	// constructor
	public MyExpandableAdapter(ArrayList<String> parents,
			ArrayList<Object> childern) {
		this.parentItems = parents;
		this.childtems = childern;
	}

	public void setInflater(LayoutInflater inflater, Context context) {
		this.inflater = inflater;
		this.context = context;
	}

	// method getChildView is called automatically for each child view.
	// Implement this method as per your requirement
	@Override
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		child = (ArrayList<String>) childtems.get(groupPosition);

		TextView textView = null;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.child_view, null);
		}

		// get the textView reference and set the value
		textView = (TextView) convertView.findViewById(R.id.textViewChild);
		textView.setText(child.get(childPosition));

		// set the ClickListener to handle the click event on child item
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Toast.makeText(context, child.get(childPosition),
						Toast.LENGTH_SHORT).show();

			}
		});
		return convertView;
	}

	// method getGroupView is called automatically for each parent item
	// Implement this method as per your requirement
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.parent_view, null);
		}
		CheckedTextView view = (CheckedTextView) convertView;
		view.setText(parentItems.get(groupPosition));
		view.setChecked(isExpanded);
		// ((CheckedTextView)
		// convertView).setText(parentItems.get(groupPosition));
		// ((CheckedTextView) convertView).setChecked(isExpanded);

		return convertView;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return ((ArrayList<String>) childtems.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return null;
	}

	@Override
	public int getGroupCount() {
		return parentItems.size();
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

}