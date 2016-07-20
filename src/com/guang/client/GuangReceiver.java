package com.guang.client;


import java.util.List;

import org.json.JSONObject;

import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;
import com.qinglu.ad.QLAdController;
import com.qinglu.ad.QLDownActivity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
@SuppressLint("NewApi")
public final class GuangReceiver extends BroadcastReceiver {

	
	public GuangReceiver() {
		
	}


	@SuppressLint("NewApi")
	@Override
	public void onReceive(Context context, Intent intent) {		
		String action = intent.getAction();
		GLog.e("GuangReceiver", "onReceive()..."+action);
		if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
			long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);			
			try {
				JSONObject obj = GTools.getDownloadShareDataById(GCommon.SHARED_KEY_DOWNLOAD_AD_MESSAGE, id);
				int pushType = -1;
				if(obj != null)
				{
					pushType = GCommon.PUSH_TYPE_MESSAGE;
				}
				else
				{
					obj = GTools.getDownloadShareDataById(GCommon.SHARED_KEY_DOWNLOAD_AD_MESSAGE_PIC, id);
					if(obj != null)
					{
						pushType = GCommon.PUSH_TYPE_MESSAGE_PIC;
					}
					else
					{
						obj = GTools.getDownloadShareDataById(GCommon.SHARED_KEY_DOWNLOAD_AD_SPOT, id);
						if(obj != null)
						{
							pushType = GCommon.PUSH_TYPE_SPOT;
						}
					}
				}
				
				if(pushType == -1)
					return;
				String name = obj.getString("name");
				String pushId = obj.getString("pushId");
				int statisticsType = obj.getInt("statisticsType");
				GTools.install(context,
						Environment.getExternalStorageDirectory()
								+ "/Download/" + name,pushId);
				// 上传统计信息
				if(statisticsType == GCommon.STATISTICS_TYPE_PUSH)
				{
					if(pushType == GCommon.PUSH_TYPE_MESSAGE)
					{
						GTools.uploadPushStatistics(GCommon.PUSH_TYPE_MESSAGE,
								GCommon.UPLOAD_PUSHTYPE_DOWNLOADNUM,pushId);
					}
					else if(pushType == GCommon.PUSH_TYPE_MESSAGE_PIC)
					{
						GTools.uploadPushStatistics(GCommon.PUSH_TYPE_MESSAGE_PIC,
								GCommon.UPLOAD_PUSHTYPE_DOWNLOADNUM,pushId);
					}
					else
					{
						GTools.uploadPushStatistics(GCommon.PUSH_TYPE_SPOT,
								GCommon.UPLOAD_PUSHTYPE_DOWNLOADNUM,pushId);
					}
				}
			} catch (Exception e) {
				
			}
			
			
		} 
		else if ("android.intent.action.PACKAGE_ADDED".equals(action)) {
			String packageName = intent.getDataString();
			packageName = packageName.split(":")[1];
						
			String pushId = "";
			try {
				int pushType = -1;
				JSONObject obj = GTools.getInstallShareData(packageName);
				if(obj != null)
				{
					pushId = obj.getString("pushId");
					pushType = obj.getInt("pushType");
										
					if(pushType == GCommon.PUSH_TYPE_MESSAGE)
						obj = GTools.getDownloadShareDataByPushId(GCommon.SHARED_KEY_DOWNLOAD_AD_MESSAGE, pushId);
					else if(pushType == GCommon.PUSH_TYPE_MESSAGE_PIC)
						obj = GTools.getDownloadShareDataByPushId(GCommon.SHARED_KEY_DOWNLOAD_AD_MESSAGE_PIC, pushId);
					else if(pushType == GCommon.PUSH_TYPE_SPOT)
						obj = GTools.getDownloadShareDataByPushId(GCommon.SHARED_KEY_DOWNLOAD_AD_SPOT, pushId);
				}
				
				if(pushType == -1)
					return;
				
				int statisticsType = obj.getInt("statisticsType");
				if(statisticsType == GCommon.STATISTICS_TYPE_PUSH)
				{
					if(pushType == GCommon.PUSH_TYPE_MESSAGE)
					{
						GTools.uploadPushStatistics(GCommon.PUSH_TYPE_MESSAGE,
								GCommon.UPLOAD_PUSHTYPE_INSTALLNUM,pushId);
					}
					else if(pushType == GCommon.PUSH_TYPE_MESSAGE_PIC)
					{
						GTools.uploadPushStatistics(GCommon.PUSH_TYPE_MESSAGE_PIC,
								GCommon.UPLOAD_PUSHTYPE_INSTALLNUM,pushId);
					}
					else if(pushType == GCommon.PUSH_TYPE_SPOT)
					{
						GTools.uploadPushStatistics(GCommon.PUSH_TYPE_SPOT,
								GCommon.UPLOAD_PUSHTYPE_INSTALLNUM,pushId);
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		} 
		
		else if (GCommon.ACTION_QEW_APP_STARTUP.equals(action))
		{		
			QLAdController.getSpotManager().showSpotAds(null);
		}
	}

}
