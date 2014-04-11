package com.dispatch_x12;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

// starting the class  
public class ChangePassword extends Activity {
	private String mMessage;
	private Pattern mPattern;
	private Matcher mMatcher;
	private static final String USERNAME_PATTERN = "^[A-Za-z0-9_-]{3,15}$";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPattern = Pattern.compile(USERNAME_PATTERN);

		setContentView(R.layout.changepass);

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	/*
	 * Handles Clicks from the layout
	 */

	public void ClickHandler(View v) {
		int id = v.getId();
		if (id == R.id.cancelpassword) {
			finish();
		} else if (id == R.id.changepassword) {
			String mEmail = AppSettings.getEmailAddress(this);
			EditText p1 = ((EditText) findViewById(R.id.et_pw1c));
			EditText p2 = ((EditText) findViewById(R.id.et_pw2c));
			TextView error = ((TextView) findViewById(R.id.tv_error));
			if (!p1.getText().toString().equals(p2.getText().toString())) {
				error.setText(R.string.passwordsdontmatch);
				return;
			}
			if (!validate(p1.getText().toString())) {
				error.setText(R.string.invalidpassword);
				return;
			}
			if (!validate(p2.getText().toString())) {
				error.setText(R.string.invalidpassword);
				return;
			}
			String mMemberid = Integer.toString(AppSettings.changePassword(
					mEmail, p1.getText().toString(), p2.getText().toString()));
			if (Integer.parseInt(mMemberid) < 1) {
				error.setText(R.string.memberidnotretrieved);
				return;
			}
			error.setText(mMemberid);
			finish();
		}
	}

	/**
	 * Validate username with regular expression
	 * 
	 * @param username
	 *            username for validation
	 * @return true valid username, false invalid username
	 */
	private boolean validate(final String username) {

		mMatcher = mPattern.matcher(username);
		return mMatcher.matches();

	}

	@Override
	public void onDestroy() {
		try {
			this.finalize();
		} catch (Throwable e) {
		}
		super.onDestroy();
	}

}