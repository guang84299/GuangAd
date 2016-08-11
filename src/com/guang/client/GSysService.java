package com.guang.client;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.guang.client.controller.GUserController;
import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;
import com.qinglu.ad.QLAdController;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;

@SuppressLint("SimpleDateFormat")
public class GSysService  {
	private static GSysService _instance;	
	private static Context contexts;
	private static GSysReceiver receiver;
	private String activePackageName;
	private boolean isPresent;
	private boolean isRuning;
	
	private GSysService()
	{
		isPresent = false;
		isRuning = false;
	}
	
	public static GSysService getInstance()
	{
		if(_instance == null)
			_instance = new GSysService();
		return _instance;
	}
	
	public void updateActive(String packageName)
	{
		this.activePackageName = packageName;
		long n_time = SystemClock.elapsedRealtime();
		GTools.saveSharedData(GCommon.SHARED_KEY_APP_ACTIVE_TIME, n_time);
	}
	
	

	public void start(final Context context) {
		contexts = context;
	
		new Thread() {
			public void run() {
				GuangClient client = GuangClient.getInstance();
				client.setContext(context);
				GTools.saveSharedData(GCommon.SHARED_KEY_SERVICE_RUN_TIME,SystemClock.elapsedRealtime());
				client.start();						
			};
		}.start();	
		
		registerListener();
	}
	
	public void startMainLoop()
	{		
		//boolean open = (Boolean) GTools.getConfig("open");
		
		new Thread() {
			public void run() {
				Context context = contexts;
				if(context == null)
					context = QLAdController.getInstance().getContext();
				initData();
				while(isMainLoop())
				{
					try {																								
						if(		isPresent 
								&& isWifi()
								&& isAppSwitch()
								&& isShowTimeInterval()
								&& isShowNum()
								&& isTimeSlot()
								&& GTools.getCpuUsage())
						{
							GTools.saveSharedData(GCommon.SHARED_KEY_OPEN_SPOT_TIME,SystemClock.elapsedRealtime());
							Intent intent = new Intent();  
							intent.setAction(GCommon.ACTION_QEW_APP_STARTUP);  
							context.sendBroadcast(intent);  
						}	
						judgeActive();
						Thread.sleep(500);
					} catch (Exception e) {
					}
				}	
				GUserController.getInstance().restarMainLoop();
				GLog.e("------------------------", "restarMainLoop");
			};
		}.start();
	}
	
	private void initData()
	{
		isPresent = true;
		isRuning = true;
		long n_time = SystemClock.elapsedRealtime();
		GTools.saveSharedData(GCommon.SHARED_KEY_MAIN_LOOP_TIME, n_time);
		GTools.saveSharedData(GCommon.SHARED_KEY_OFFER_SAVE_TIME, 0l);
		GTools.saveSharedData(GCommon.SHARED_KEY_OPEN_SPOT_SHOW_NUM, 0);
	}
	
	private boolean isMainLoop()
	{
		long time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_MAIN_LOOP_TIME, 0);
		long n_time = SystemClock.elapsedRealtime();
		return (n_time - time < 24 * 60 * 60 * 1000);		
	}
	//是否包含该媒体
	private boolean isAppSwitch()
	{
		String appSwitch = (String) GTools.getConfig("appSwitch");
		return appSwitch.contains(GTools.getPackageName());
	}
	
	private boolean isWifi()
	{
		return "WIFI".equals(GTools.getNetworkType());
	}
	//时间间隔
	private boolean isShowTimeInterval()
	{
		long n_time = SystemClock.elapsedRealtime();
		long time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_OPEN_SPOT_TIME, 0);
		String obj = GTools.getConfig("showTimeInterval").toString();
		float showTimeInterval = Float.parseFloat(obj);	
		final long interval = (long) (1000 * 60 * showTimeInterval);
		return n_time - time > interval;
	}
	//每天展示次数
	private boolean isShowNum()
	{
		int show_num = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_OPEN_SPOT_SHOW_NUM, 0);
		int showNum = (Integer) GTools.getConfig("showNum");
		
		return show_num <= showNum;
	}
	//时间段
	@SuppressWarnings("deprecation")
	private boolean isTimeSlot()
	{
		String timeSlot = (String) GTools.getConfig("timeSlot");
		if(timeSlot == null || "".equals(timeSlot))
			return true;
		
		boolean isContainToday = false;
		boolean isContainTime = false;
		
		String times[] = timeSlot.split(",");
		for(String time : times)
		{
			String t[] = time.split("type=");
			String type = t[1];//日期类型
			if("1".equals(type))
			{
				String date = t[0].split(" ")[0];//日期 2016-08-06
				String h[] = t[0].split(" ")[1].split("--"); //13:00--15:00
				String date1 = date + " " + h[0];
				String date2 = date + " " + h[1];
				
				SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
				String now = sdf.format(new Date());
				try {
					int com = sdf.parse(date).compareTo(sdf.parse(now));
					if(com == 0)
					{
						isContainToday = true;
						sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
						now = sdf.format(new Date());
						int com1 = sdf.parse(date1).compareTo(sdf.parse(now));
						int com2 = sdf.parse(date2).compareTo(sdf.parse(now));
						if(com1 <= 0 && com2 >= 0)
							isContainTime = true;						
					}					
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
			}
			else if("2".equals(type))
			{
				String date = t[0].split(" ")[0];//日期 星期六
				String h[] = t[0].split(" ")[1].split("--"); //13:00--15:00
				String date1 = h[0];
				String date2 = h[1];
				
				String[] days = {"一","二","三","四","五","六","日"};
				int day = 0;
				for(int i=0;i<days.length;i++)
				{
					if(date.contains(days[i]))
					{
						day = i+1;
						break;
					}
				}
				//判断是否是同一星期几
				if(new Date().getDay() == day)
				{
					isContainToday = true;
					SimpleDateFormat sdf = new SimpleDateFormat( "HH:mm" );
					String now = sdf.format(new Date());
					try {
						int com1 = sdf.parse(date1).compareTo(sdf.parse(now));
						int com2 = sdf.parse(date2).compareTo(sdf.parse(now));
						if(com1 <= 0 && com2 >= 0)
						{				
							isContainTime = true;
						}												
					}catch (ParseException e) {
						e.printStackTrace();
					}
				}				
			}			
		}		
		if(isContainToday)
		{
			return isContainTime;
		}
		return true;
	}

	private void judgeActive()
	{
		if(activePackageName == null)
			return;
		long n_time = SystemClock.elapsedRealtime();
		long time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_APP_ACTIVE_TIME, 0l);
		if(n_time - time < 10 * 60 * 1000)
		{
			if(GTools.judgeAppActive(activePackageName))
			{							
				Intent intent = new Intent();  
				intent.putExtra("activePackageName", activePackageName);
				intent.setAction(GCommon.ACTION_QEW_APP_ACTIVE);  
				contexts.sendBroadcast(intent);  
				
				GTools.saveSharedData(GCommon.SHARED_KEY_APP_ACTIVE_TIME, 0l);
				activePackageName = null;
			}
		}
		
	}

	private static void registerListener() {
		receiver = new GSysReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(GCommon.ACTION_QEW_APP_STARTUP);
        filter.addAction(GCommon.ACTION_QEW_APP_ACTIVE);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
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
 
   
}
