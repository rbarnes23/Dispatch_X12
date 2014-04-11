package com.dispatch_x12;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

// starting the class  
public class NewUser extends Activity {
	String mMessage;
	private Pattern mPattern;
	private Matcher mMatcher;
	private static final String USERNAME_PATTERN = "^[A-Za-z0-9_-]{3,15}$";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPattern = Pattern.compile(USERNAME_PATTERN);

		setContentView(R.layout.newuser);

	}

	@Override
	public void onResume() {

		// new PopulateFriends().execute();

		super.onResume();
	}

	@Override
	public void onDestroy() {
		try {
			this.finalize();
		} catch (Throwable e) {
		}
		super.onDestroy();
	}

	public void ClickHandler(View v) {
		int id = v.getId();
		if (id == R.id.cancelmember) {
			finish();
		} else if (id == R.id.addmember) {
			EditText email = ((EditText) findViewById(R.id.et_emailaddress));
			EditText p1 = ((EditText) findViewById(R.id.et_pw));
			EditText p2 = ((EditText) findViewById(R.id.et_pw2));
			EditText fname = ((EditText) findViewById(R.id.et_fname));
			EditText lname = ((EditText) findViewById(R.id.et_lname));
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
			if (!validate(fname.getText().toString())) {
				error.setText(R.string.invalidfname);
				return;
			}
			if (!validate(lname.getText().toString())) {
				error.setText(R.string.invalidlname);
				return;
			}
			String mMemberid = Integer.toString(AppSettings.newUser(email
					.getText().toString(), p1.getText().toString(), p2
					.getText().toString(), fname.getText().toString(), lname
					.getText().toString()));
			if (Integer.parseInt(mMemberid) < 1) {
				error.setText(R.string.invalidnewuser);
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
}