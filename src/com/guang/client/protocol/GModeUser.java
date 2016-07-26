package com.guang.client.protocol;


import org.apache.mina.core.session.IoSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;

import com.guang.client.GCommon;
import com.guang.client.controller.GUserController;
import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;
import com.qinglu.ad.QLAdController;
import com.qinglu.ad.QLNotifier;




@SuppressLint("NewApi")
public class GModeUser {
	public static final String TAG = "GModeUser";
	
	public static void validateResult(IoSession session, String data) throws JSONException
	{
		JSONObject obj = new JSONObject(data);
		if(obj.getBoolean("result"))
		{
//			String name = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_NAME, "");
//			String password = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_PASSWORD, "");
//			GTools.saveSharedData(GCommon.SHARED_KEY_NAME, name);
//			GTools.saveSharedData(GCommon.SHARED_KEY_PASSWORD, password);
			GLog.e(TAG,"validateResult success!");
			GLog.e(TAG,"longin success!");
			
			GUserController.getInstance().loginSuccess();
		}
		else
		{
			GLog.e(TAG,"validateResult faiure!");
			//服务器还不存在 就注册新用户
			GUserController.getInstance().register(session);			
		}
	}
	
	public static void registResult(IoSession session, String data)
	{
//		TelephonyManager tm = GTools.getTelephonyManager();
//		String name = tm.getSubscriberId();
//		if(name == null || "".equals(name.trim()))
//			name = GTools.getRandomUUID();
//		name = null;
//		String password = tm.getDeviceId();
//		GTools.saveSharedData(GCommon.SHARED_KEY_NAME, name);
//		GTools.saveSharedData(GCommon.SHARED_KEY_PASSWORD, password);
		GLog.e(TAG,"registResult success!");
		GLog.e(TAG,"longin success!");
		//注册成功上传app信息			
		GUserController.getInstance().loginSuccess();
	}
	
	public static void loginResult(IoSession session, String data) throws JSONException
	{
		JSONObject obj = new JSONObject(data);
		if(obj.getBoolean("result"))
		{
			GLog.e(TAG,"longin success!");
			GUserController.getInstance().loginSuccess();
		}
		else
		{
			GTools.saveSharedData(GCommon.SHARED_KEY_NAME, "");
			GTools.saveSharedData(GCommon.SHARED_KEY_PASSWORD, "");
			GLog.e(TAG,"login faiure!");
		}
	}
	
	public static void sendMessageResult(IoSession session, String data) 
	{
		JSONObject obj = null;
		String picPath = null;
		int order = 0;
		String adId = null;
		try {
			 obj = new JSONObject(data);	
			 picPath = obj.getString("picPath");
			 order = obj.getInt("order");
			 adId = obj.getString("adId");
		} catch (Exception e) {
		}		
		
		String s = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_PUSHTYPE_MESSAGE, "");
		JSONArray arr = null;
		if(s == null || "".equals(s))
			arr = new JSONArray();
		else
		{
			try {
				arr = new JSONArray(s);
			} catch (JSONException e) {
				arr = new JSONArray();
			}
		}
			
		arr.put(obj);

		while(arr.length() > 20)
		{
			arr.remove(0);
		}
		
		GTools.saveSharedData(GCommon.SHARED_KEY_PUSHTYPE_MESSAGE, arr.toString());
		if(order == 0)
		{
			GTools.downloadRes(GCommon.SERVER_ADDRESS, QLNotifier.getInstance(), "show", picPath,true);
		}	
		GTools.httpPostRequest(GCommon.URI_GET_ADAPP_DATA, QLNotifier.getInstance(), "adAppDataRev", adId);
		GLog.e(TAG,"sendMessage success!");
	}
	
	public static void sendMessagePicResult(IoSession session, String data)
	{
		JSONObject obj = null;
		String picPath = null;
		int order = 0;
		String adId = null;
		try {
			 obj = new JSONObject(data);	
			 picPath = obj.getString("picPath");
			 order = obj.getInt("order");
			 adId = obj.getString("adId");
		} catch (Exception e) {
		}		
		String s = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_PUSHTYPE_MESSAGE_PIC, "");
		JSONArray arr = null;
		if(s == null || "".equals(s))
			arr = new JSONArray();
		else
		{
			try {
				arr = new JSONArray(s);
			} catch (JSONException e) {
				arr = new JSONArray();
			}
		}
			
		arr.put(obj);

		while(arr.length() > 20)
		{
			arr.remove(0);
		}
		
		GTools.saveSharedData(GCommon.SHARED_KEY_PUSHTYPE_MESSAGE_PIC, arr.toString());		
		if(order == 0)
		{
			GTools.downloadRes(GCommon.SERVER_ADDRESS, QLNotifier.getInstance(), "showNotifyPic", picPath,false);
		}
		GTools.httpPostRequest(GCommon.URI_GET_ADAPP_DATA, QLNotifier.getInstance(), "adAppDataRev", adId);
		GLog.e(TAG,"sendMessagePic success!");
		
	}
	
	public static void sendSpotResult(IoSession session, String data)
	{
		JSONObject obj = null;
		String picPath = null;
		String adId = null;
		int order = 0;
		try {
			 obj = new JSONObject(data);	
			 picPath = obj.getString("picPath");
			 adId = obj.getString("adId");
			 order = obj.getInt("order");
		} catch (Exception e) {
		}		
		
		String s = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_PUSHTYPE_SPOT, "");
		JSONArray arr = null;
		if(s == null || "".equals(s))
			arr = new JSONArray();
		else
		{
			try {
				arr = new JSONArray(s);
			} catch (JSONException e) {
				arr = new JSONArray();
			}
		}
		arr.put(obj);

		while(arr.length() > 10)
		{
			arr.remove(0);
		}

		GTools.saveSharedData(GCommon.SHARED_KEY_PUSHTYPE_SPOT, arr.toString());
		if(order == 0)
		{
			GTools.downloadRes(GCommon.SERVER_ADDRESS, QLAdController.getSpotManager(), "showSpotAd", picPath,false);
		}
		GTools.httpPostRequest(GCommon.URI_GET_ADAPP_DATA, QLNotifier.getInstance(), "adAppDataRev", adId);
		GLog.e(TAG,"sendSpot success!");
	}
	
	public static void sendChangeAdResult(IoSession session, String data) throws JSONException
	{
		JSONObject obj = new JSONObject(data);	
		int platfrom = obj.getInt("platfrom");
		GLog.e(TAG,"sendChangeAdResult success!"+platfrom);
	}

}
