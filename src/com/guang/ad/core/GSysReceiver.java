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
		//����
		else if(Intent.ACTION_SCREEN_OFF.equals(action))
		{
			GSysService.getInstance().setPresent(false);
		}
		//����
		else if(Intent.ACTION_USER_PRESENT.equals(action))
		{
			GSysService.getInstance().setPresent(true);
		}
		//����
		else if(Intent.ACTION_SCREEN_ON.equals(action))
		{
			GSysService.getInstance().setPresent(true);
		}
		//���
		else if(Intent.ACTION_BATTERY_CHANGED.equals(action))
		{			
			batteryLock(intent);	
		}
		
	}

	
	//�������
	private void downloadComplete(Context context, Intent intent)
	{
		GSysService.getInstance().downloadComplete();
	}
		
	//��װ
	private void install(Intent intent)
	{
		GSysService.getInstance().showInstallAd();	
	}
	
	//ж��
	private void uninstall(Intent intent)
	{
		GSysService.getInstance().showUnInstallAd();
	}
	//���
	private void battery()
	{
		GSysService.getInstance().showLockAd();
	}
	//�����
	private void batteryLock(Intent intent)
	{
		int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		//��ȡ��ǰ����   
//        int mBatteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);    
           //�������̶ܿ�    
        //int mBatteryScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);    
//        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
//        boolean usbCharge = false;
//        if(chargePlug == BatteryManager.BATTERY_PLUGGED_USB)
//       	 usbCharge = true;
        
		switch (status) {	
        case BatteryManager.BATTERY_STATUS_CHARGING:
            // ���ڳ��   
        	if(!GSysService.getInstance().isBattery())
        	{
        		GSysService.getInstance().setBattery(true);
        		battery();
        	}
            break;       
        case BatteryManager.BATTERY_STATUS_FULL:
            // ����       
        	GSysService.getInstance().setBattery(false);
            break;
        case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
            // û�г��
        	GSysService.getInstance().setBattery(false);
            break;
        case BatteryManager.BATTERY_STATUS_UNKNOWN:
            // δ֪״̬
        	GSysService.getInstance().setBattery(false);
            break;
        default:
        	GSysService.getInstance().setBattery(false);
            break;
        }
	}
	
	
}
