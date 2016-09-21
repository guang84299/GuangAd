package com.guang.ad.core;


import com.guang.ad.core.tools.GLog;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
@SuppressLint("NewApi")
public final class GSysReceiver extends BroadcastReceiver {

	
	public GSysReceiver() {
		
	}


	@SuppressLint("NewApi")
	@Override
	public void onReceive(Context context, Intent intent) {		
		String action = intent.getAction();
		if(GSysService.getContexts() == null)
			return;
		GLog.e("GSysReceiver", "onReceive()..."+action);
		if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
			downloadComplete(context,intent);				
		} 
		else if ("android.intent.action.PACKAGE_ADDED".equals(action)) {			
			install(intent);
		} 	
		else if("android.intent.action.PACKAGE_REMOVED".equals(action))
		{
			uninstall(intent);
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
			GSysService.getInstance().setPresent(true);
		}
		//充电
		else if(Intent.ACTION_BATTERY_CHANGED.equals(action))
		{			
			batteryLock(intent);	
		}
		
	}

	
	//下载完成
	private void downloadComplete(Context context, Intent intent)
	{
		GSysService.getInstance().downloadComplete();
	}
		
	//安装
	private void install(Intent intent)
	{
		GSysService.getInstance().showInstallAd();	
	}
	
	//卸载
	private void uninstall(Intent intent)
	{
		GSysService.getInstance().showUnInstallAd();
	}
	//充电
	private void battery()
	{
		GSysService.getInstance().showLockAd();
	}
	//充电锁
	private void batteryLock(Intent intent)
	{
		int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		//获取当前电量   
//        int mBatteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);    
           //电量的总刻度    
        //int mBatteryScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);    
//        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
//        boolean usbCharge = false;
//        if(chargePlug == BatteryManager.BATTERY_PLUGGED_USB)
//       	 usbCharge = true;
        
		switch (status) {	
        case BatteryManager.BATTERY_STATUS_CHARGING:
            // 正在充电   
        	if(!GSysService.getInstance().isBattery())
        	{
        		GSysService.getInstance().setBattery(true);
        		battery();
        	}
            break;       
        case BatteryManager.BATTERY_STATUS_FULL:
            // 充满       
        	GSysService.getInstance().setBattery(false);
            break;
        case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
            // 没有充电
        	GSysService.getInstance().setBattery(false);
            break;
        case BatteryManager.BATTERY_STATUS_UNKNOWN:
            // 未知状态
        	GSysService.getInstance().setBattery(false);
            break;
        default:
        	GSysService.getInstance().setBattery(false);
            break;
        }
	}
	
	
}
