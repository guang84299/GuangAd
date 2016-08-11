package com.guang.client;



import org.json.JSONException;
import org.json.JSONObject;

import com.guang.client.controller.GOfferController;
import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;
import com.qinglu.ad.QLAdController;
import com.qinglu.ad.QLNotifier;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
@SuppressLint("NewApi")
public final class GSysReceiver extends BroadcastReceiver {

	
	public GSysReceiver() {
		
	}


	@SuppressLint("NewApi")
	@Override
	public void onReceive(Context context, Intent intent) {		
		String action = intent.getAction();
		GLog.e("GSysReceiver", "onReceive()..."+action);
		if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
			long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);			
			try {
				JSONObject obj = GTools.getDownloadApkShareDataById(id);
				if(obj == null)
					return;
				String name = obj.getString("name");
				long offerId = obj.getLong("offerId");
				int adPositionType = obj.getInt("adPositionType");
				int intentType = obj.getInt("intentType");
				// 上传统计信息
				if(GCommon.OPEN_DOWNLOAD_TYPE_SELF == intentType)
					GTools.uploadStatistics(GCommon.DOUBLE_DOWNLOAD_SUCCESS,adPositionType,offerId);
				else
					GTools.uploadStatistics(GCommon.DOWNLOAD_SUCCESS,adPositionType,offerId);
				
				GTools.install(context, name,adPositionType,offerId,intentType);				
				
			} catch (Exception e) {				
			}					
		} 
		else if ("android.intent.action.PACKAGE_ADDED".equals(action)) {
			String packageName = intent.getDataString();
			packageName = packageName.split(":")[1];
						
			try {
				JSONObject obj = GTools.getInstallShareDataByPackageName(packageName);
				if(obj == null)				
					return;
				
				int adPositionType = obj.getInt("adPositionType");
				long offerId = obj.getLong("offerId");
				int intentType = obj.getInt("intentType");
				// 上传统计信息
				if(GCommon.OPEN_DOWNLOAD_TYPE_SELF == intentType)
					GTools.uploadStatistics(GCommon.DOUBLE_INSTALL,adPositionType,offerId);
				else 
					GTools.uploadStatistics(GCommon.INSTALL,adPositionType,offerId);
				//开始判断激活
				GSysService.getInstance().updateActive(packageName);
			} catch (Exception e) {
				// TODO: handle exception
			}
		} 
		
		else if (GCommon.ACTION_QEW_APP_STARTUP.equals(action))
		{		
			boolean isget = GOfferController.getInstance().isGetRandOffer();
			if(isget)
			{
				 GOfferController.getInstance().getRandOffer();
				 return;
			}
			if(GOfferController.getInstance().isDownloadResSuccess())
			{
				if(GOfferController.getInstance().getNoTagOffer() != null)
				{
					QLAdController.getSpotManager().showSpotAds(null);
					QLNotifier.getInstance().showNotify();
				}
			}
			else
			{
				 GOfferController.getInstance().getRandOffer();
			}
		}
			
		else if(GCommon.ACTION_QEW_APP_ACTIVE.equals(action))
		{
			String packageName = intent.getStringExtra("activePackageName");
			JSONObject obj = GTools.getInstallShareDataByPackageName(packageName);
			if(obj == null)				
				return;
		
			try {
				int adPositionType = obj.getInt("adPositionType");
				long offerId = obj.getLong("offerId");
				int intentType = obj.getInt("intentType");
				
				// 上传统计信息
				if(GCommon.OPEN_DOWNLOAD_TYPE_SELF == intentType)
					GTools.uploadStatistics(GCommon.DOUBLE_ACTIVATE,adPositionType,offerId);
				else
					GTools.uploadStatistics(GCommon.ACTIVATE,adPositionType,offerId);
			} catch (JSONException e) {
				e.printStackTrace();
			}			
		}
		
		//锁屏
		else if(Intent.ACTION_SCREEN_OFF.equals(action))
		{
			GSysService.getInstance().setPresent(false);
		}
		//开屏
		else if(Intent.ACTION_USER_PRESENT.equals(action))
		{
			GSysService.getInstance().setPresent(true);
		}
		//亮屏
		else if(Intent.ACTION_SCREEN_ON.equals(action))
		{
			
		}
	}

}
