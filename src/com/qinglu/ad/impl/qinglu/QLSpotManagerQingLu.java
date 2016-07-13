package com.qinglu.ad.impl.qinglu;





import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.widget.RelativeLayout;

import com.guang.client.GCommon;
import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;
import com.qinglu.ad.QLActivity;
import com.qinglu.ad.QLSpotManager;
import com.qinglu.ad.QLSpotView;
import com.qinglu.ad.listener.QLSpotDialogListener;


public class QLSpotManagerQingLu implements QLSpotManager{
	private Context context;
	private Activity activity;
	private int animationType;
	
	public void updateContext(Context context)
	{
		this.context = context;
	}
	
	public QLSpotManagerQingLu(Context context)
	{
		this.context = context;		
		this.animationType = GCommon.ANIM_ADVANCE;		
	}
	@Override
	public void loadSpotAds() {
		
		//QLNetTools.httpRequestAd(context);
				     
	}

	@Override
	public void setSpotOrientation(int orientation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAnimationType(int animationType) {
		this.animationType = animationType;
	}

	@SuppressLint("NewApi")
	@Override
	public void showSpotAds(final Context con) {
//		QLSpotView view = new QLSpotView(con,this.animationType);
//		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
//		//params.gravity = Gravity.CENTER;
//		view.setLayoutParams(params);
//		Activity ac = (Activity)con;
//		ac.addContentView(view, params);
		String name = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_NAME, "");
		JSONObject data = new JSONObject();
		try {
			data.put("username", name);
		} catch (Exception e) {
		}
		GTools.httpPostRequest(GCommon.URI_GET_SPOT, null, null, data);
	}
	

	public void showSpotAd(Object obj,Object rev)
	{
		if(this.activity != null)
		{
			this.activity.finish();
			this.activity = null;
		}
		String pushId = "";
		try {
			pushId = GTools.getPushShareData(GCommon.SHARED_KEY_PUSHTYPE_SPOT, -1).getString("pushId");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Intent intent = new Intent(this.context, QLActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(GCommon.INTENT_TYPE, GCommon.INTENT_PUSH_SPOT);
		intent.putExtra("pushId", pushId);
		this.context.startActivity(intent);
	}
	
	@Override
	public void showSpotAds(Context con, QLSpotDialogListener spotDialogListener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadSplashSpotAds() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showSplashSpotAds(Context context, Class<?> targetActivity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean disMiss() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public int getAnimationType() {
		return this.animationType;
	}

	@Override
	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	

}
