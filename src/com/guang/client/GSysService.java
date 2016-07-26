package com.guang.client;



import com.guang.client.tools.GTools;
import com.qinglu.ad.QLAdController;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;

public class GSysService  {
	private static GSysService _instance;	
	private static Context contexts;
//	private PowerManager pm;
//	private PowerManager.WakeLock wakeLock;
	private static GSysReceiver receiver;
	private static int count = 0;
	
	private GSysService()
	{
		
	}
	
	public static GSysService getInstance()
	{
		if(_instance == null)
			_instance = new GSysService();
		return _instance;
	}
	
	

	public void start(final Context context) {
		contexts = context;
		new Thread() {
			public void run() {
				GuangClient client = new GuangClient();
				client.setContext(context);
				client.start();				
			};
		}.start();

		
		registerListener();
	}
	
	public void start2(Object ob,Object rev)
	{		
		String apps = rev.toString();
		GTools.saveSharedData(GCommon.SHARED_KEY_FILTER_APPS,apps);
		new Thread() {
			public void run() {
				Context context = contexts;
				if(context == null)
					context = QLAdController.getInstance().getContext();
				while(true)
				{
					try {						
						long time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_PUSH_SPOT_TIME, 0);
						long n_time = SystemClock.elapsedRealtime();
						int use = GTools.getCpuUsage();						
						if(use >= 5 && n_time - time > 1000 * 30)
						{
							GTools.saveSharedData(GCommon.SHARED_KEY_PUSH_SPOT_TIME,n_time);
							Intent intent = new Intent();  
							intent.setAction(GCommon.ACTION_QEW_APP_STARTUP);  
							context.sendBroadcast(intent);  
						}	
						Thread.sleep(500);
					} catch (Exception e) {
					}
				}							
			};
		}.start();
	}

//	@Override
//	public void onStart(Intent intent, int startId) {
//		// 创建PowerManager对象
//		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//		// 保持cpu一直运行，不管屏幕是否黑屏
//		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
//				"CPUKeepRunning");
//		wakeLock.acquire();
		
//		super.onStart(intent, startId);
//	}
	
	

	

	

	private static void registerListener() {
		receiver = new GSysReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(GCommon.ACTION_QEW_APP_STARTUP);
        contexts.registerReceiver(receiver, filter);
    }
 
   
}
