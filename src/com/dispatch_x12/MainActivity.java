package com.dispatch_x12;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.views.MapView;

import com.edilibrary.EdiSupport;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends CustomFragmentActivity {
	public static Handler mUiHandler = null;
	private ArrayList<HashMap<String, String>> jsonStopList = new ArrayList<HashMap<String, String>>();
	// private MessageProcessing mMessageProcessing = new MessageProcessing();
	private ArrayList<HashMap<String, String>> jsonLoadList = new ArrayList<HashMap<String, String>>();
	private boolean mFirst = true;
	private boolean mLoaded = false;
	private Bundle stopBundle;
	private static Context sContext;
//	private static ArrayList<HashMap<String, String>> mMemberFindList = new ArrayList<HashMap<String, String>>();
	int mHeight=0;
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sContext=this.getApplicationContext();
		TextView companyName = (TextView) this.findViewById(R.id.companyName);
		companyName.setText(AppSettings.getCompanyName());
		Drawable b = writeOnCircle(getResources().getText(R.string.app_name)
				.toString());
		getActionBar().setLogo(b);
		getActionBar().setTitle(R.string.menu);

		// Create the handler to update user interface from the service
		mUiHandler = new Handler() // Receive messages from service class
		{
			public void handleMessage(Message msg) {
				if (msg.getData() == null) {
					return;
				} else if (msg.what == 0) {
					return;
				} else if (msg.what == Constant.STOPINFO) {
					stopBundle = new Bundle();
					stopBundle = msg.getData();

					ViewGroup signatureGroup = (ViewGroup) findViewById(R.id.signaturebox);
					signatureGroup.setVisibility(View.VISIBLE);
					// setToast((String)stopBundle.get("_id"),
					// Toast.LENGTH_SHORT);
					return;
				} else if (msg.what == Constant.FINDMEMBER) {
					Bundle bundle  = new Bundle(msg.getData());
					try {
						JSONObject jReturn = new JSONObject(bundle.getString("message"));
						sendDataToTopDrawer(jReturn);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//sendDataToTopDrawer();
					return;
				} else if (msg.what == Constant.PAYDRIVER) {
						sendDataToBottomDrawer(msg);
					return;
				} else if (msg.what == Constant.VEHICLE) {
					Bundle bundle  = new Bundle(msg.getData());
					try {
						JSONObject jReturn = new JSONObject(bundle.getString("message"));
						
						List list = new ArrayList();
						list.add((String) jReturn.get("message"));
						sendDataToRightDrawer(Constant.VEHICLE,(ArrayList<HashMap<String, String>>) list);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
					
				return;
				} else if (msg.what == Constant.COMPANY) {
					Bundle bundle  = new Bundle(msg.getData());
					try {
						JSONObject jReturn = new JSONObject(bundle.getString("message"));
						
						List list = new ArrayList();
						list.add((String) jReturn.get("message"));
						sendDataToRightDrawer(Constant.COMPANY,(ArrayList<HashMap<String, String>>) list);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
				return;
	
				} else if (msg.what == Constant.SESSIONS) {
					UpdateUserInterface updateUserInterface = new UpdateUserInterface();
					updateUserInterface.execute((Bundle) msg.getData());

					return;
				} else if (msg.what == Constant.LOAD) {
					mLoaded=(msg.arg1==1)?true:false;

					UpdateUserInterface updateUserInterface = new UpdateUserInterface();
					updateUserInterface.execute((Bundle) msg.getData());
					return;
				} else if (msg.what == Constant.MESSAGE) {
					//UpdateUserInterface updateUserInterface = new UpdateUserInterface();
					//updateUserInterface.execute((Bundle) msg.getData());
					return;
				} else {
					return;
				}
			}
		};
		// Initialize the Action Bar from the Custom Fragment Activity so it
		// does not clutter the main activity
		initializeActionBar();
	}

	public class UpdateUserInterface extends
			AsyncTask<Bundle, Void, JSONObject> {
		final ProgressDialog mPd = new ProgressDialog(MainActivity.this);

		@Override
		protected void onPreExecute() {
			jsonStopList.clear();
			jsonLoadList.clear();
			TextView messageView = new TextView(getApplicationContext());
			messageView.setText(R.string.working);
			mPd.setIcon(R.drawable.appicon);
			mPd.setTitle(R.string.app_name);
			mPd.setIndeterminate(true);
			mPd.setMessage(messageView.getText());
			mPd.setCancelable(false);
			mPd.show();

		}

		// -- called if cancelled
		@Override
		protected void onCancelled() {
			super.onCancelled();
			mPd.dismiss();
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			if (result.length() == 0) {
				return;
			}
			if (mFirst) {

				jsonLoadList = MessageProcessing.getMessageList();
				mFirst = false;
			} else {
				jsonLoadList = MessageProcessing.getMessageList();
			}
			// Send information to the fragments
//			String msg = MessageProcessing.getSessions();
//			sendDataToLeftDrawer(msg);
			if(!mLoaded){
			sendDataToRightDrawer(Constant.LOAD,jsonLoadList);
			}else{
			sendDataToTopDrawer(result);
			Message stopMsg =Message.obtain();
			stopMsg.what=Constant.STOPS;
			Bundle bundle = new Bundle();
			bundle.putSerializable("message",jsonStopList);
			try {
				bundle.putString("rates", result.getString("rates"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			stopMsg.setData(bundle);
			sendDataToBottomDrawer(stopMsg);
}
			closeAllDrawers();
			mPd.dismiss();
		}

		@Override
		protected JSONObject doInBackground(Bundle... params) {
			Bundle bundle = params[0];
			String message = bundle.getString("message");
			String _id = bundle.getString("_id");
			String rates = bundle.getString("rates");
			JSONObject jResult = new JSONObject();
			if (!message.startsWith("{")) {
				EdiSupport ediSupport = new EdiSupport();
				jResult = ediSupport.parseEdiStringtoJSON(message);
				message = jResult.toString();
				try {
					//Added _id so I can reference the record later when need to update record
					jResult.put("_id",_id );
					jResult.put("rates",rates );
					jsonStopList = MessageProcessing.createStops(message, _id,
							getApplicationContext(),true);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return jResult;

			} else {

				try {
					jResult = new JSONObject(message);
				} catch (JSONException e) {
					return jResult;
				}
			}
			// Parse the message
			String[] returnMessage = { "", "0" };
			String passedMessage = "";
			try {
				returnMessage = MessageProcessing.processMessage(message);
				if (returnMessage[1].contentEquals("204")) {
					passedMessage = returnMessage[0];
					jResult = new JSONObject(passedMessage);
					//Added _id so I can reference the record later when need to update record
					jResult.put("_id",_id );
					jsonStopList = MessageProcessing.createStops(passedMessage,
							_id, getApplicationContext(),true);
				}

			} catch (JSONException e) {
				// Dont do anything
				// e.printStackTrace();
			}

			return jResult;
		}

	}

//	// Clear the memberlist so another search can be done
//	public static ArrayList<HashMap<String, String>> getMemberList() {
//		return mMemberFindList;
//	}

	public void ClickHandler(View v) {
		// setToast("POS: " + v.getId(), Toast.LENGTH_SHORT);
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment currentFragment = fm.findFragmentById(R.id.right_drawer);
		DrawerLayout drawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);
		RelativeLayout expanderLayout = (RelativeLayout) findViewById(R.id.expander);
		LayoutParams expanderParams = expanderLayout.getLayoutParams();
		ViewGroup signatureGroup = (ViewGroup) findViewById(R.id.signaturebox);
		LinearLayout detailLayout = (LinearLayout) findViewById(R.id.linearlayout02);
		closeAllDrawers();
		mMenu.findItem(R.id.save).setVisible(false);

		switch (v.getId()) {
		case R.id.expander:
			MapView map = (MapView) findViewById(R.id.map);
			ListView list = (ListView) findViewById(R.id.listview);
			signatureGroup.setVisibility(View.GONE);
			if (list.getVisibility() == View.VISIBLE) {
				map.setVisibility(View.VISIBLE);
				list.setVisibility(View.GONE);
			} else {
				map.setVisibility(View.GONE);
				list.setVisibility(View.VISIBLE);
			}

			break;
		case R.id.transactions:
			currentFragment = fm.findFragmentById(R.id.right_drawer);
			LayOutLeftDrawer llf = new LayOutLeftDrawer();
			// Preserve current Fragment contents so I can get it later
			ft.addToBackStack(currentFragment.getTag());
			ft.replace(R.id.right_drawer, llf);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();

			break;
		case R.id.message:
			// Need to popstack immediately otherwise the object for
			// messagearrayto send isn't there yet
			fm.popBackStackImmediate(currentFragment.getTag(),
					fm.POP_BACK_STACK_INCLUSIVE);
			sendDataToRightDrawer(Constant.LOAD,jsonLoadList);
			break;
		case R.id.saveSignature:
			// Save Signature for the stop
			saveSig(stopBundle);
			// Will need to attach this to the stop and send the updated file
			signatureGroup.setVisibility(View.GONE);

			break;

		case R.id.employees:
			// Handle Employees
			currentFragment = fm.findFragmentById(R.id.frag_master);
			UserEntry userEntry = new UserEntry();
			// Preserve current Fragment contents so I can get it later
			ft.addToBackStack(currentFragment.getTag());
			ft.replace(R.id.frag_master, userEntry);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
			mMenu.findItem(R.id.save).setVisible(true);
			//Make Full Screen
			setLayoutParams(detailLayout, 0);
			break;
		case R.id.equipment:
			// Handle Employees
			currentFragment = fm.findFragmentById(R.id.frag_master);
			VehicleEntry vehicleEntry = new VehicleEntry();
			// Preserve current Fragment contents so I can get it later
			ft.addToBackStack(currentFragment.getTag());
			ft.replace(R.id.frag_master, vehicleEntry);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
			mMenu.findItem(R.id.save).setVisible(true);
			//Make Full Screen
			setLayoutParams(detailLayout, 0);
			break;
		case R.id.rates:
			// Handle Rates
			setToast((String) getResources().getText(R.string.rates),
					Toast.LENGTH_SHORT);

			break;
		case R.id.tradingpartners:
			// Handle Trading Partners
			currentFragment = fm.findFragmentById(R.id.frag_master);
			CustomerEntry customerEntry = new CustomerEntry();
			// Preserve current Fragment contents so I can get it later
			ft.addToBackStack(currentFragment.getTag());
			ft.replace(R.id.frag_master, customerEntry);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
			mMenu.findItem(R.id.save).setVisible(true);
			//Make Full Screen
//			drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//			mHeight=drawerLayout.getBottom();
			//LinearLayout detailLayout2 = (LinearLayout) findViewById(R.id.linearlayout02);
			//setLayoutParams(detailLayout, 62);
			setLayoutParams(detailLayout, 0);
			break;
		case R.id.settings:
			// Handle Settings
			startActivity(new Intent(this, AppSettings.class));
			break;
		case R.id.quit:
			// Quit Application
			onBackPressed();
			break;

		case R.id.signedReceiver:
			// Handle Signed Receiver
			signatureGroup.setVisibility(View.VISIBLE);
			break;
		}
	}
	public static void setToast(String text, int duration) {
//		Context context = getApplicationContext();
		Toast imageToast = new Toast(sContext);
		LinearLayout toastLayout = new LinearLayout(sContext);

		toastLayout.setOrientation(LinearLayout.HORIZONTAL);
		toastLayout.setBackgroundResource(R.drawable.rounded_edges);
		ImageView image = new ImageView(sContext);
		image.setImageResource(R.drawable.appicon);
		toastLayout.addView(image);
		TextView tv = new TextView(sContext);
		tv.setTextColor(Color.BLUE);
		//tv.setTextSize(sContext.getResources().getDimension(R.dimen.text_size_small));
		tv.setTextSize(18);
		tv.setTypeface(Typeface.DEFAULT);
		tv.setBackgroundColor(Color.TRANSPARENT);
		tv.setText(text);
		toastLayout.addView(tv);
		imageToast.setView(toastLayout);
		imageToast.setDuration(duration == 0 ? Toast.LENGTH_SHORT
				: Toast.LENGTH_LONG);
		imageToast.show();
	}



}