package com.dispatch_x12;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.os.Message;
 
public interface DataToSendListener {
//    public void arrayToSend(ArrayList<HashMap<String, String>> msg);
    public void sendDataToBottomDrawer(Message msg);
	public void sendDataToRightDrawer(int type,ArrayList<HashMap<String, String>> msg);
    public void sendDataToTopDrawer(JSONObject msg);
    public void sendDataToLeftDrawer(String msg);
    public void sendDataToCompanyEntry(JSONObject msg);
    public void sendDataToUserEntry(JSONObject msg);
    public void sendDataToVehicleEntry(JSONObject msg);
    public void sendDataToX204Entry(JSONObject msg);

}