package com.guang.client.controller;

import org.apache.mina.core.session.IoSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.guang.client.GCommon;
import com.guang.client.GSysService;
import com.guang.client.GuangClient;
import com.guang.client.mode.GUser;
import com.guang.client.protocol.GData;
import com.guang.client.protocol.GProtocol;
import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;
import com.qinglu.ad.QLAdController;
import com.qinglu.ad.QLNotifier;

@TargetApi(Build.VERSION_CODES.KITKAT)
@SuppressLint("NewApi")
public class GUserController {
	
	private static GUserController instance;
	public static boolean isLogin = false;
	private IoSession session;
	private GUserController(){}
	
	public static GUserController getInstance()
	{
		if(instance == null)
			instance = new GUserController();
		return instance;
	}
	
	private boolean isRegister()
	{
		String name = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_NAME, "");
		String password = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_PASSWORD, "");		
		if(name != null && password != null && !"".equals(name.trim()) && !"".equals(password.trim()))
			return true;
		return false;
	}

	public void login(IoSession session)
	{
		isLogin = false;
		this.session = session;
		if(isRegister())
		{
			String name = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_NAME, "");
			String password = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_PASSWORD, "");
			JSONObject obj = new JSONObject();
			try {
				obj.put(GCommon.SHARED_KEY_NAME, name);
				obj.put(GCommon.SHARED_KEY_PASSWORD, password);
				obj.put("networkType", GTools.getNetworkType());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			GData data = new GData(GProtocol.MODE_USER_LOGIN, obj.toString());
			session.write(data.pack());
		}
		else
		{					
			validate(session);
		}
	}
	//验证是否已经注册
	public void validate(IoSession session)
	{
		TelephonyManager tm = GTools.getTelephonyManager();
		String name = tm.getSubscriberId();
		if(name == null || "".equals(name.trim()))
			name = GTools.getRandomUUID();
		String password = tm.getDeviceId();	
		if(password == null || "".equals(password.trim()))
			password = GTools.getRandomUUID();
		GTools.saveSharedData(GCommon.SHARED_KEY_NAME, name);
		GTools.saveSharedData(GCommon.SHARED_KEY_PASSWORD, password);
		JSONObject obj = new JSONObject();
		try {
			obj.put(GCommon.SHARED_KEY_NAME, name);
			obj.put(GCommon.SHARED_KEY_PASSWORD, password);
			obj.put("networkType", GTools.getNetworkType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		GData data = new GData(GProtocol.MODE_USER_VALIDATE, obj.toString());
		session.write(data.pack());
	}
	
	public void register(IoSession session)
	{				
		String url = GCommon.MAP_BAIDU_URL + GTools.getLocalHost();
		GTools.httpGetRequest(url, this, "getLoction",session);
	}
	
	public void getLoction(Object obj_session,Object obj_data)
	{
		IoSession session = (IoSession) obj_session;
		String data = (String) obj_data;
		TelephonyManager tm = GTools.getTelephonyManager();
		GUser user = new GUser();
		String name = tm.getSubscriberId();
		if(name == null || "".equals(name.trim()))
			name = GTools.getRandomUUID();
		user.setName(name);
		String password = tm.getDeviceId();	
		if(password == null || "".equals(password.trim()))
			password = GTools.getRandomUUID();
		user.setPassword(password);
		user.setDeviceId(password);
		user.setPhoneNumber(tm.getLine1Number());
		user.setNetworkOperatorName(tm.getNetworkOperatorName());
		user.setSimSerialNumber(tm.getSimSerialNumber());
		user.setNetworkCountryIso(tm.getNetworkCountryIso());
		user.setNetworkOperator(tm.getNetworkOperator());		
		user.setPhoneType(tm.getPhoneType());
		user.setModel(android.os.Build.MODEL);
		user.setRelease(android.os.Build.VERSION.RELEASE);
		user.setNetworkType(GTools.getNetworkType());
		try {
			JSONObject obj = new JSONObject(data);
			if(obj.getInt("status") == 0)
			{
				JSONObject content = obj.getJSONObject("content");
				JSONObject obj2 = content.getJSONObject("address_detail");						
				String city = obj2.getString("city");//城市  
				String province = obj2.getString("province");//省份
				String district = obj2.getString("district");//区县 
				String street = obj2.getString("street");//街道
				
				user.setProvince(province);
				user.setCity(city);
				user.setDistrict(district);
				user.setStreet(street);
				
				//用户可能拒绝获取位置 需要捕获异常
				user.setLocation(tm.getCellLocation().toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			GTools.saveSharedData(GCommon.SHARED_KEY_NAME, name);
			GTools.saveSharedData(GCommon.SHARED_KEY_PASSWORD, password);
			
			GData gdata= new GData(GProtocol.MODE_USER_REGISTER,GUser.toJson(user));
			session.write(gdata.pack());
			
		}		
	}
	//发送心跳
	public void sendHeartBeat(IoSession session)
	{
		if(isLogin)
		{
			GData data = new GData(GProtocol.MODE_USER_HEART_BEAT, "1");
			session.write(data.pack());
		}	
		//GTools.sendBroadcast(GCommon.ACTION_QEW_APP_STARTUP);
	}
	//主动发送心跳
	public void sendHeartBeat()
	{
		GLog.e("---------------------", "sendHeartBeat");
		if(isLogin && session != null && session.isConnected())
		{
			GLog.e("---------------------", "sendHeartBeat  start");
			GData data = new GData(GProtocol.MODE_USER_HEART_BEAT, "1");
			session.write(data.pack());
			GLog.e("---------------------", "sendHeartBeat  end");
		}	
	}
	//上传app信息
	public void uploadAppInfos()
	{
		String name = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_NAME, "");
		try {
			JSONObject obj = new JSONObject();
			obj.put("packageName", GTools.getPackageName());
			obj.put("name", GTools.getApplicationName());
			obj.put("versionName", GTools.getAppVersionName());
			obj.put("sdkVersion", GCommon.version);
			obj.put("id", name);
			GTools.httpPostRequest(GCommon.URI_UPLOAD_APPINFO, this, null, obj);
		} catch (Exception e) {
		}
	}
	
	//登录成功
	public void loginSuccess()
	{
		GUserController.isLogin = true;
		
		//注册成功上传app信息
		GUserController.getInstance().uploadAppInfos();
		
		//登录成功下载必要资源
		GTools.downloadRes(GCommon.SERVER_ADDRESS, null, null, "images/close.png",true);
		GTools.httpGetRequest(GCommon.URI_GET_GET_PUSHAD_IDS, QLNotifier.getInstance(), "adIdDataRev",null);
		//得到app过滤资源
		GTools.httpGetRequest(GCommon.URI_GET_SDK_FILTER_APP, GSysService.getInstance(), "start2",null);
		//获取系统配置信息
		GTools.httpGetRequest(GCommon.URI_GET_AUTO_PUSH_SETTING, this, "revAutoPushSetting",null);
	}
	
	public void revAutoPushSetting(Object ob,Object rev)
	{
		JSONObject obj = null;
		boolean autoState = false;
		int autoPushType = 1;
		float waitTime = 0.1f;
		try {
			 obj = new JSONObject(rev.toString());	
			 autoState = obj.getBoolean("autoState");
			 autoPushType = obj.getInt("autoPushType");
			 waitTime = (float) obj.getDouble("waitTime");
		} catch (Exception e) {
		}
		final int type = autoPushType;
		final float time = waitTime;
		
		if(autoState)
		{
			new Thread(){
				public void run() {
					try {
						Thread.sleep((long) (1000*60*time));
						if(type == 1)
							QLNotifier.getInstance().showNotify();
						else
							QLAdController.getSpotManager().showSpotAds(GuangClient.getContext());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				};
			}.start();
		}
	}
}
