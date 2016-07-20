package com.qinglu.ad;



import com.guang.client.ClientService;
import com.guang.client.GCommon;
import com.guang.client.GuangClient;
import com.guang.client.tools.GTools;
import com.qinglu.ad.impl.qinglu.QLSpotManagerQingLu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;


public class QLAdController {
	private static QLAdController controller;
	public static QLSpotManager spotManager;
	private Context context;
	
	private QLAdController()
	{
		
	}
	
	public static QLAdController getInstance()
	{
		if(controller == null)
		{
			controller = new QLAdController();					
		}	
		return controller;
	}
	
	public static QLSpotManager getSpotManager()
	{
		if(spotManager == null)
			spotManager = new QLSpotManagerQingLu(GuangClient.getContext());
		spotManager.updateContext(GuangClient.getContext());
		return spotManager;
	}
	
	
		
	public void init(Context context,Boolean isTestModel)
	{
		this.context = context;
		
		GTools.saveSharedData(GCommon.SHARED_KEY_TESTMODEL,isTestModel);
		
		 startService();
	}
	@SuppressLint("NewApi")
	public void startService()
	{
		Intent intent = new Intent(this.context, ClientService.class);
		this.context.startService(intent);
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	
	
}
