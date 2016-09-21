package com.guang.ad.core;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;

import com.guang.ad.core.tools.GLog;
import com.guang.ad.core.tools.GTools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;

@SuppressLint("SimpleDateFormat")
public class GSysService  {
	private static GSysService _instance;	
	private static Context contexts;
	private static GSysReceiver receiver;
	private boolean isPresent;
	private boolean isRuning;
	private boolean isBattery;
	
	private GSysService()
	{
		isPresent = false;
		isRuning = false;
		isBattery = false;
	}
	
	public static GSysService getInstance()
	{
		if(_instance == null)
			_instance = new GSysService();
		return _instance;
	}
	
	
	public void start(final Context context) {
		contexts = context;
		GTools.saveSharedData(GCommon.SHARED_KEY_SERVICE_RUN_TIME,GTools.getCurrTime());
		registerListener();
		
		startMainLoop();
	}
	
	public void startMainLoop()
	{		
		new Thread() {
			public void run() {
				initData();
				while(isMainLoop())
				{				
					try {		
						if(		isPresent 
								&& isShowAd()
								&& GTools.getCpuUsage()
								&& isMultiApp())
						{							
							appStartUp(); 
						}	
						Thread.sleep(100);
					} catch (Exception e) {
					}
				}	
				startMainLoop();
				GLog.e("------------------------", "restarMainLoop");
			};
		}.start();		
	}		
		
	private void initData()
	{
		isPresent = true;
		isRuning = true;
		long n_time = GTools.getCurrTime();
		GTools.saveSharedData(GCommon.SHARED_KEY_MAIN_LOOP_TIME, n_time);
	}
	
	private boolean isMainLoop()
	{
		long time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_MAIN_LOOP_TIME, 0);
		long n_time = GTools.getCurrTime();
		return (n_time - time < GConfig.MAIN_LOOP_TIME);		
	}
	private boolean isShowAd()
	{
		return  GTools.isNetwork()
				&& isShowTimeInterval()
				&& isShowNum();
	}
	//时间间隔
	private boolean isShowTimeInterval()
	{
		long n_time = GTools.getCurrTime();
		long time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_OPEN_SPOT_TIME, 0);
		return n_time - time > GConfig.OPEN_SPOT_INTERVAL;
	}
	//每天展示次数
	private boolean isShowNum()
	{
		int show_num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_OPEN_SPOT_SHOW_NUM, 0);		
		return show_num <= GConfig.OPEN_SPOT_SHOWNUM;
	}	
	
	public boolean isMultiApp()
	{
		String name = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
		File f = new File(name, "multiapp");
		if(!f.exists())
		{
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
		try {
			final FileOutputStream fos = new FileOutputStream(f);
			final FileLock fl = fos.getChannel().tryLock(); 
			if(fl != null && fl.isValid())
	        {
				new Thread(){
					public void run() {
						try {
							Thread.sleep(8000);
							deleteMultiApp(fl,fos);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}						
					};
				}.start();
				return true;
	        }
		} catch (Exception e1) {
			e1.printStackTrace();
		}  
		return false;
	}
	
	public void deleteMultiApp(FileLock fl,FileOutputStream fos)
	{
		try {
			if(fos != null)
				fos.close();			
		} catch (Exception e) {
		}		
        try {  
        	if(fl != null && fl.isValid())
        		fl.release();  
        } catch (IOException e) {  
        }  
	}
	//应用启动
	private void appStartUp()
	{
		GTools.saveSharedData(GCommon.SHARED_KEY_OPEN_SPOT_TIME,GTools.getCurrTime());
		
	}
		
	public void showLockAd()
	{
		if(isShowAd())
		{
			GTools.saveSharedData(GCommon.SHARED_KEY_OPEN_SPOT_TIME,GTools.getCurrTime());		

		}
	}
	
	public void showInstallAd()
	{
		if(isShowAd())
		{
			
		}
		GTools.saveSharedData(GCommon.SHARED_KEY_OPEN_SPOT_TIME,GTools.getCurrTime());			
	}
	
	public void showUnInstallAd()
	{
		if(isShowAd())
		GTools.saveSharedData(GCommon.SHARED_KEY_OPEN_SPOT_TIME,GTools.getCurrTime());			
	}
	
	public void downloadComplete()
	{
		if(isShowAd())
		{
			
		}
	}
	
	private static void registerListener() {
		receiver = new GSysReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        contexts.registerReceiver(receiver, filter);      
    }

	
	public boolean isPresent() {
		return isPresent;
	}

	public void setPresent(boolean isPresent) {
		this.isPresent = isPresent;
	}

	public boolean isRuning() {
		return isRuning;
	}

	public void setRuning(boolean isRuning) {
		this.isRuning = isRuning;
	}
	
	public boolean isBattery() {
		return isBattery;
	}

	public void setBattery(boolean isBattery) {
		this.isBattery = isBattery;
	}

	public static Context getContexts() {
		return contexts;
	}
   
}
