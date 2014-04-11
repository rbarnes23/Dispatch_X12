package com.dispatch_x12;

import java.util.HashMap;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

public class CustomAutoCompleteTextView extends AutoCompleteTextView {
	String id = "";

	public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected String convertSelectionToString(Object selectedItem) {
		/**
		 * Each item in the autocompetetextview suggestion list is a hashmap
		 * object
		 */
		String name = "";

		if (selectedItem != null) {
			HashMap<String, String> hm = (HashMap<String, String>) selectedItem;
			id = hm.get("id");
			name = id+" "+hm.get("name");

		} else {
			name = null;
			id = null;
		}
		return name;
	}

	public String getSelectionId() {
		return id;
	}
}