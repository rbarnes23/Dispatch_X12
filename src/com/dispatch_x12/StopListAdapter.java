package com.dispatch_x12;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.metalev.multitouch.controller.MultiTouchController.PositionAndScale;
import org.osmdroid.bonuspack.location.NominatimPOIProvider;

import com.dispatch_x12.MessageProcessing.verifyAddress;
import com.dispatch_x12.R.color;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class StopListAdapter extends ArrayAdapter<HashMap<String, String>> {
	private int[] colors = new int[] { 0x3066FF66, 0x30FFFF6F };
	private int redColor = 0x30FF0000 ;
	private Context mContext;
	private ViewHolder holder;
	ArrayList<HashMap<String, String>> mStopList;
	public StopListAdapter(Context context,
			ArrayList<HashMap<String, String>> msg, int resource,
			String[] from, int[] to) {
		//super(context, msg, resource, from, to);
		super(context,0, msg);
		mContext = context;
		mStopList=msg;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
@Override	
	public  HashMap<String, String> getItem(int position) {
	     return mStopList.get(position);
	}
	
@Override	
public int getCount() {
return mStopList.size();
}


	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view =convertView;
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = mInflater.inflate(R.layout.stopsrowlist, null);
			holder = new ViewHolder(view);
			view.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.stopNo.setTag(position);
		holder._id.setText(mStopList.get(position).get("_id"));
		holder.lon.setText(mStopList.get(position).get("lon"));
		holder.lat.setText(mStopList.get(position).get("lat"));
		final int row = position;
		holder.geoCode.setOnClickListener(new OnClickListener() {
				
			public void onClick(View v) {
				String address=mStopList.get(row).get("address");
				//int lonlat[]=MessageProcessing.searchAddress(mContext, address);
				Object params[] = { new Object(), new Object() };
				params[0] = address;
				params[1] = mContext;
				List addressList = null;
				try {
					addressList = new MessageProcessing.verifyAddressNomatim()
							.execute(params).get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String toDisplay = "Row: "+Integer.toString(row);
				if (addressList != null && addressList.size() > 0) {
					Address addressFound = (Address) addressList.get(0);
					toDisplay=Integer.toString(row)+"|"+Double.toString(addressFound.getLatitude())+"|"+Double.toString(addressFound.getLongitude());
					
				}
				
			
				MainActivity.setToast(toDisplay, Toast.LENGTH_LONG);
			}
		});
		

		
		if (holder.lat.getText().length()>1){
			holder.geoCode.setText("GE");
			holder.geoCode.setBackgroundColor(colors[0]);
		}else{
			holder.geoCode.setText("NG");
			holder.geoCode.setBackgroundColor(redColor);
		}
		holder.identificationNo.setText(mStopList.get(position).get("identificationNo"));
		
		holder.stopNo.setText(Integer.toString(position+1));
		final String loadInfo=mStopList.get(position).get(
				"message");
		// Trimming the string is just for testing
		//holder.stopInfo.setText(loadInfo.substring(0, 80));
		holder.stopInfo.setText(loadInfo);
//		String address="1907 Redbridge Dr,Brandon,FL  33511";
//		Object params[] = {new Object(),new Object()};
//		params[0]=address;
//		params[1]=mContext;
//
//		try {
//			List addressList = new MessageProcessing.verifyAddress().execute(params).get();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		//final String pos= Integer.toString(position);

		holder.signedReceiver.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				//int row=(Integer) holder.stopNo.getTag();
				Message msgToActivity = MainActivity.mUiHandler.obtainMessage(Constant.STOPINFO,loadInfo);
				Bundle bundle = new Bundle();
				bundle.putString("_id",(String) holder._id.getText() );
				bundle.putString("message",loadInfo );
				bundle.putString("identificationNo",(String)  holder.identificationNo.getText());
				bundle.putString("position",Integer.toString(row));
				bundle.putString("type", "stopinfo");
				bundle.putString("lon", (String) holder.lon.getText());
				bundle.putString("lat", (String) holder.lat.getText());
				
				msgToActivity.setData(bundle);

				//msgToActivity.arg1=Integer.parseInt(pos);//need to send object id of selected order
				sendUpdateMessage(msgToActivity);
			}
		});
		
		int colorPos = position % colors.length;
		view.setBackgroundColor(colors[colorPos]);

		return view;
	}

	class ViewHolder {
		public TextView _id;
		public TextView identificationNo;
		public TextView lon;
		public TextView lat;
		public TextView stopInfo;
		public TextView stopNo;
		public Button signedReceiver;
		public Button geoCode;
		public ViewHolder(View view) {
			_id = (TextView) view.findViewById(R.id._id);
			lon = (TextView) view.findViewById(R.id.lon);
			lat = (TextView) view.findViewById(R.id.lat);
			identificationNo=(TextView) view.findViewById(R.id._identificationNo);
			stopInfo = (TextView) view.findViewById(R.id.stopInfo);
			stopNo = (TextView) view.findViewById(R.id.stopNo);
			geoCode = (Button) view.findViewById(R.id.geoCode);
			signedReceiver = (Button) view.findViewById(R.id.signedReceiver);
			stopInfo.setClickable(false);
			 

		}

	}
	private boolean sendUpdateMessage(Message msgToActivity) {

		return MainActivity.mUiHandler.sendMessage(msgToActivity);
	}

}
