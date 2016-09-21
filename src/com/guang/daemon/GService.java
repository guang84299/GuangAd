package com.guang.daemon;




import com.guang.ad.core.GSysService;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;


public class GService extends Service{
	private Context context;
	//private int count = 0;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		context = this;
		
		GSysService.getInstance().start(context);
		super.onCreate();
	}
	
	
}
