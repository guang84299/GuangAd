package com.guang.ad;



import com.guang.ad.core.GCommon;
import com.guang.ad.core.tools.GTools;

import android.content.Context;



public class GAdController {
	private static GAdController controller;
	private Context context;
	
	private GAdController()
	{
		
	}
	
	public static GAdController getInstance()
	{
		if(controller == null)
		{
			controller = new GAdController();					
		}	
		return controller;
	}
		
	public void init(Context context,Boolean isTestModel)
	{
		this.context = context;
		
		GTools.saveSharedData(GCommon.SHARED_KEY_TESTMODEL,isTestModel);		
	}
	
	
	
	
}
