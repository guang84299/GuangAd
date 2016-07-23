package com.guang.client;



import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.widget.Toast;

public class GSysService  {
	private static Context contexts;
//	private PowerManager pm;
//	private PowerManager.WakeLock wakeLock;
	private static GSysReceiver receiver;
	private static int count = 0;
	

	public static void start(final Context context) {
		contexts = context;
		new Thread() {
			public void run() {
				GuangClient client = new GuangClient();
				client.setContext(context);
				client.start();				
			};
		}.start();
		
		new Thread() {
			public void run() {
				while(true)
				{
					try {
						long time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_PUSH_SPOT_TIME, 0);
						long n_time = SystemClock.elapsedRealtime();
						int use = GTools.getCpuUsage();						
						if(use > 20 && n_time - time > 1000 * 30)
						{
							GLog.e("-------------------", "use="+use);
							GTools.saveSharedData(GCommon.SHARED_KEY_PUSH_SPOT_TIME,n_time);
							Intent intent = new Intent();  
							intent.setAction(GCommon.ACTION_QEW_APP_STARTUP);  
							context.sendBroadcast(intent);  
						}	
						Thread.sleep(50);
					} catch (Exception e) {
					}
				}							
			};
		}.start();
		
		registerListener();
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
