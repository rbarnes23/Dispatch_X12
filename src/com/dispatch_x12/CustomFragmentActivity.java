package com.dispatch_x12;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

public abstract class CustomFragmentActivity extends FragmentActivity implements
		DataToSendListener {
	protected DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	protected static Menu mMenu;


	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	protected void initializeActionBar() {
		// ActionBar ab = getActionBar();
		// ab.setTitle(R.string.billTo);
		// ab.setSubtitle(R.string.angle);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer icon to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description */
		R.string.drawer_close /* "close drawer" description */

		) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(getResources().getText(R.string.menu));
				// Drawable b = writeOnCircle(getResources().getText(
				// R.string.app_name).toString());
				// getActionBar().setLogo(b);

			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {

				if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
					getActionBar().setTitle(
							getResources().getText(R.string.menu));

					// Drawable b =
					// writeOnCircle(getResources().getText(R.string.menu).toString());
					//
					// getActionBar().setLogo(b);
				} else if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
					getActionBar().setTitle(
							getApplication().getText(R.string.loads));
					// Drawable b =
					// writeOnCircle(getResources().getText(R.string.loads).toString());
					// getActionBar().setLogo(b);
				} else {
					getActionBar().setTitle(
							getApplication().getText(R.string.menu));
					// Drawable b = writeOnCircle( getResources().getText(
					// R.string.app_name).toString());
					// getActionBar().setLogo(b);

				}

			}
		};
		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		// getActionBar().setHomeButtonEnabled(true);

	}

	// Used to close all drawers from MainActivity
	protected void closeAllDrawers() {
		mDrawerLayout.closeDrawers();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menuactionbar, menu);
		mMenu = menu;
		// mSearch = (EditText) mSearchItem.getActionView()
		// .findViewById( R.id.search );
		// mDelete = (Button) mSearchItem.getActionView()
		// .findViewById( R.id.delete );
		return true;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
		mDrawerToggle.setDrawerIndicatorEnabled(true);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			// setContentView(R.layout.activity_main);

			// MainActivity.setToast("portrait", 1);
		}
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// setContentView(R.layout.activity_main);

			// MainActivity.setToast("landscape", 1);
		}

		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean ret;
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		if (item.getItemId() == android.R.id.home) {
			// setToast("HOME", Toast.LENGTH_SHORT);
			if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
				mDrawerLayout.closeDrawer(Gravity.LEFT);
			} else {
				mDrawerLayout.closeDrawer(Gravity.RIGHT);
				mDrawerLayout.openDrawer(Gravity.LEFT);
			}

			ret = true;
		} else if (item.getItemId() == R.id.chat) {
			// Handle Settings
			// setToast("Chat", Toast.LENGTH_SHORT);
			if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
				mDrawerLayout.closeDrawer(Gravity.RIGHT);
			} else {
				mDrawerLayout.closeDrawer(Gravity.LEFT);
				mDrawerLayout.openDrawer(Gravity.RIGHT);
			}
			// if(item.isVisible()){
			// item.setVisible(false);}else{item.setVisible(true);};

			ret = true;
		} else if (item.getItemId() == R.id.loads) {
			// Handle Settings
			// setToast("Chat", Toast.LENGTH_SHORT);
			if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
				mDrawerLayout.closeDrawer(Gravity.RIGHT);
			} else {
				mDrawerLayout.closeDrawer(Gravity.LEFT);
				mDrawerLayout.openDrawer(Gravity.RIGHT);
			}

			ret = true;

		} else if (item.getItemId() == R.id.about) {
			// Handle Settings
			MainActivity.setToast("About", Toast.LENGTH_SHORT);
			ret = true;
		} else if (item.getItemId() == R.id.setup) {
			// Handle Settings
			startActivity(new Intent(this, AppSettings.class));
			// setToast("Settings", Toast.LENGTH_SHORT);
			ret = true;
		} else if (item.getItemId() == R.id.information) {
			// Handle Information
			Intent infoIntent = new Intent(this, InfoWebActivity.class);
			startActivity(infoIntent);
			ret = true;
		} else if (item.getItemId() == R.id.menu_edit) {
			// Handle Settings
			MainActivity.setToast("Edit", Toast.LENGTH_SHORT);
			ret = true;
		} else if (item.getItemId() == R.id.save) {
			// Handle Save on Current Fragment
			Fragment currentFragment = fm.findFragmentById(R.id.frag_master);

			String className = currentFragment.getClass().getName();
			if (className.contentEquals("com.dispatch_x12.CompanyEntry")) {
				JSONObject msg = new JSONObject();
				try {
					msg.put("type", 0);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				sendDataToCompanyEntry(msg);
			}
			if (className.contentEquals("com.dispatch_x12.UserEntry")) {
				JSONObject msg = new JSONObject();
				sendDataToUserEntry(msg);
			}
			if (className.contentEquals("com.dispatch_x12.VehicleEntry")) {
				JSONObject msg = new JSONObject();
				sendDataToVehicleEntry(msg);
			}
			if (className.contentEquals("com.dispatch_x12.X204Entry")) {
				JSONObject msg = new JSONObject();
				sendDataToX204Entry(msg);
			}

			ret = true;

		} else if (item.getItemId() == R.id.quit) {
			// Quit Application
			onBackPressed();
			ret = true;

		} else {
			ret = super.onOptionsItemSelected(item);
		}
		return ret;
	}

	@Override
	public void onBackPressed() {
		FragmentManager fm = getSupportFragmentManager();
		if (fm.getBackStackEntryCount() > 0) {
			fm.popBackStackImmediate();
			//Set layout back to the master detail default
			DrawerLayout drawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);
			LinearLayout detailLayout = (LinearLayout) findViewById(R.id.linearlayout02);
			drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
			int mHeight=drawerLayout.getBottom();
			setLayoutParams(detailLayout, mHeight);
			mMenu.findItem(R.id.save).setVisible(false);
		} else {
			new AlertDialog.Builder(this)
					.setMessage(R.string.exitapp)
					.setCancelable(false)
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									Intent commIntent = new Intent(
											getApplicationContext(),
											ChatService.class);

									stopService(commIntent);
									System.exit(0);
									// finish();

								}
							}).setNegativeButton(R.string.no, null).show();

		}
	}

	@Override
	public void sendDataToBottomDrawer(Message msg) {
		LayOutTwo Obj = (LayOutTwo) getSupportFragmentManager()
				.findFragmentById(R.id.frag_detail);
		Obj.setMessage(msg);

	}

	@Override
	public void sendDataToRightDrawer(int type,ArrayList<HashMap<String, String>> msg) {
		LayOutRightDrawer Obj = (LayOutRightDrawer) getSupportFragmentManager()
				.findFragmentById(R.id.right_drawer);
		Obj.setMessage(type, msg);
	}

	@Override
	public void sendDataToTopDrawer(JSONObject msg) {
		LayOutOne Obj = (LayOutOne) getSupportFragmentManager()
				.findFragmentById(R.id.frag_master);
		Obj.setMessage(msg);

	}

	@Override
	public void sendDataToLeftDrawer(String msg) {
		LayOutLeftDrawer Obj = (LayOutLeftDrawer) getSupportFragmentManager()
				.findFragmentById(R.id.left_drawer);
		Obj.setMessage(msg);

	}

	@Override
	public void sendDataToCompanyEntry(JSONObject msg) {
		CompanyEntry Obj = (CompanyEntry) getSupportFragmentManager()
				.findFragmentById(R.id.frag_master);
		Obj.setMessage(msg);

	}

	@Override
	public void sendDataToUserEntry(JSONObject msg) {
		UserEntry Obj = (UserEntry) getSupportFragmentManager()
				.findFragmentById(R.id.frag_master);
		Obj.setMessage(msg);

	}
	@Override
	public void sendDataToVehicleEntry(JSONObject msg) {
		VehicleEntry Obj = (VehicleEntry) getSupportFragmentManager()
				.findFragmentById(R.id.frag_master);
		Obj.setMessage(msg);
	}

	@Override
	public void sendDataToX204Entry(JSONObject msg) {
		X204Entry Obj = (X204Entry) getSupportFragmentManager()
				.findFragmentById(R.id.frag_master);
		Obj.setMessage(msg);
		mMenu.findItem(R.id.addStop).setVisible(true);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	@SuppressWarnings("deprecation")
	public BitmapDrawable writeOnCircle(String text) {
		// Set the size of the bitmap
		Rect rect = new Rect(0, 0, 50, 50);

		// Create a bitmap to write on
		Bitmap bm = Bitmap.createBitmap(rect.width(), rect.height(),
				Config.ARGB_8888);

		// Create canvas to draw on from the bitmap
		Canvas canvas = new Canvas(bm);
		// Make background of the canvas transparent
		canvas.drawColor(android.R.color.transparent);

		Paint paint = new Paint();
		paint.setStyle(Style.FILL);
		paint.setColor(Color.CYAN);
		// Draw the circle using the paint color and fill type
		canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2,
				paint);
		// Set the text style and color
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setTextSize(20);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setColor(Color.BLUE);
		// Check the size of the passed text
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, 1, bounds);
		// Draw the text
		canvas.drawText(text, canvas.getWidth() >> 1,
				(canvas.getHeight() + bounds.height()) >> 1, paint);
		// Return the created bitmap
		return new BitmapDrawable(getResources(), bm);
	}

	@SuppressWarnings("deprecation")
	public BitmapDrawable writeOnDrawable(int drawableId, String text) {
		// Create a bitmap to write on
		Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableId)
				.copy(Bitmap.Config.ARGB_8888, true);

		// Create canvas to draw on from the bitmap
		Canvas canvas = new Canvas(bm);
		// Make background of the canvas transparent
		canvas.drawColor(android.R.color.transparent);

		Paint paint = new Paint();
		paint.setStyle(Style.FILL);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setTextSize(40);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setColor(Color.BLUE);

		// Check the size of the passed text
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, 1, bounds);
		// Draw the text
		canvas.drawText(text, canvas.getWidth() >> 1,
				(canvas.getHeight() + bounds.height()) >> 1, paint);
		// Return the created bitmap
		return new BitmapDrawable(getResources(), bm);
	}

	public void saveSig(Bundle stopBundle) {
		DrawView drawView = (DrawView) findViewById(R.id.signaturePad);
		String signature = drawView.getStringFromCurrentBitmap();
		Message msg = ChatService.mServiceHandler
				.obtainMessage(Constant.STOPINFO);
		stopBundle.putString("signature", signature);
		msg.setData(stopBundle);
		ChatService.mServiceHandler.sendMessage(msg);

		// drawView.save("signature");
	}

	protected static void setLayoutParams(LinearLayout linearLayout, int height) {
		ViewGroup.LayoutParams params = linearLayout.getLayoutParams();
		int paramsHeight = params.height;
		params.height = height;
		linearLayout.setLayoutParams(params);
	}
}
