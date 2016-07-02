package com.qinglu.ad;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.guang.client.GCommon;
import com.guang.client.GuangClient;
import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Notification.BigPictureStyle;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.RemoteViews;

@SuppressLint("NewApi")
public class QLNotifier {

	private static QLNotifier _instance = null;
	public static QLNotifier getInstance()
	{
		if(_instance == null)
		{
			_instance = new QLNotifier();
		}
		return _instance;
	}
	@SuppressLint("NewApi")
	public void show(Object ob,Object rev) throws JSONException {
		Context context = GuangClient.getContext();
		JSONObject obj = GTools.getPushShareData(GCommon.SHARED_KEY_PUSHTYPE_MESSAGE, -1);
		String title = obj.getString("title");
		String message = obj.getString("message");
		String picPath = obj.getString("picPath");
		String pushId = obj.getString("pushId");
		
		int notify_id = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_NOTIFY_ID, 1);
		GTools.saveSharedData(GCommon.SHARED_KEY_NOTIFY_ID, notify_id+1);
		
		RemoteViews remoteView = new RemoteViews(context.getPackageName(),(Integer) GTools.getResourceId("qew_notification", "layout"));  
		//remoteView.setImageViewResource((Integer) GTools.getResourceId("imageView", "id"),(Integer) GTools.getResourceId("qew_icon", "drawable"));  
		Bitmap bitmap = BitmapFactory.decodeFile(context.getFilesDir().getPath()+"/"+ picPath) ;
		remoteView.setImageViewBitmap((Integer) GTools.getResourceId("imageView", "id"), bitmap);
		remoteView.setTextViewText((Integer) GTools.getResourceId("title", "id") , title);  
		remoteView.setTextViewText((Integer)GTools.getResourceId("message", "id"), message); 
				
		Intent intent = new Intent(context,QLActivity.class);
		intent.putExtra(GCommon.INTENT_TYPE, GCommon.INTENT_PUSH_MESSAGE);
		intent.putExtra("pushId", pushId);
		PendingIntent contentIntent = PendingIntent.getActivity(context, notify_id,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
     
		Notification notify = new Notification.Builder(context)  
	        .setSmallIcon((Integer) GTools.getResourceId("qew_icon", "drawable"))  
	        .setWhen(System.currentTimeMillis())
	        .setOngoing(true)  
	        .setContent(remoteView)
	        .setContentIntent(contentIntent).setNumber(1).build(); 
	        
	    NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	    notify.defaults = Notification.DEFAULT_LIGHTS;
	    notify.defaults |= Notification.DEFAULT_SOUND;
	    notify.defaults |= Notification.DEFAULT_VIBRATE;
	    notify.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。  
	    manager.notify(notify_id, notify);
        
      //上传统计信息
		GTools.uploadPushStatistics(GCommon.PUSH_TYPE_MESSAGE,GCommon.UPLOAD_PUSHTYPE_SHOWNUM,pushId);
	}
	
	
	@SuppressLint("NewApi")
	public void showPic(Object ob,Object rev) throws JSONException {
		Context context = GuangClient.getContext();
		JSONObject obj = GTools.getPushShareData(GCommon.SHARED_KEY_PUSHTYPE_MESSAGE_PIC, -1);
//		String title = obj.getString("title");
//		String message = obj.getString("message");
		String picPath = obj.getString("picPath");
		String pushId = obj.getString("pushId");
		
		int notify_id = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_NOTIFY_ID, 1);
		GTools.saveSharedData(GCommon.SHARED_KEY_NOTIFY_ID, notify_id+1);
		
		RemoteViews remoteView = new RemoteViews(context.getPackageName(),(Integer) GTools.getResourceId("qew_notification_pic", "layout"));  		
		Bitmap bitmap = BitmapFactory.decodeFile(context.getFilesDir().getPath()+"/"+ picPath) ;
		remoteView.setImageViewBitmap((Integer) GTools.getResourceId("imageView", "id"), bitmap);	
		
		Intent intent = new Intent(context,QLActivity.class);
		intent.putExtra(GCommon.INTENT_TYPE, GCommon.INTENT_PUSH_MESSAGE_PIC);
		intent.putExtra("pushId", pushId);
		PendingIntent contentIntent = PendingIntent.getActivity(context, notify_id,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		Notification.Builder builder = new Notification.Builder(context);
		builder.setSmallIcon((Integer) GTools.getResourceId("qew_icon", "drawable"));
		builder.setWhen(System.currentTimeMillis());
		builder.setOngoing(true);		
		builder.setContentIntent(contentIntent).setNumber(1);
		builder.setContent(remoteView);
		//builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), (Integer) GTools.getResourceId("qew_icon", "drawable")));
		
		Notification notify = builder.build(); 
		//builder.setContent(remoteView);
		notify.bigContentView = remoteView;
	        
	    NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	    notify.defaults = Notification.DEFAULT_LIGHTS;
	    notify.defaults |= Notification.DEFAULT_SOUND;
	    notify.defaults |= Notification.DEFAULT_VIBRATE;
	    notify.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。  
	    manager.notify(notify_id, notify);
        
      //上传统计信息
		GTools.uploadPushStatistics(GCommon.PUSH_TYPE_MESSAGE_PIC,GCommon.UPLOAD_PUSHTYPE_SHOWNUM,pushId);
	}
	
	public void adAppDataRev(Object ob,Object rev)
	{
		JSONObject obj = null;
		String picPath = null;
		try {
			 obj = new JSONObject(rev.toString());	
			 //picPath = obj.getString("picPath");
		} catch (Exception e) {
		}		
		
		String s = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_AD_APP_DATA, "");
		JSONArray arr = null;
		if(s == null || "".equals(s))
			arr = new JSONArray();
		else
		{
			try {
				arr = new JSONArray(s);
			} catch (JSONException e) {
				arr = new JSONArray();
			}
		}
			
		arr.put(obj);

		while(arr.length() > 10)
		{
			arr.remove(0);
		}
		GTools.saveSharedData(GCommon.SHARED_KEY_AD_APP_DATA, arr.toString());

	}
}
