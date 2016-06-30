package com.qinglu.ad;

import org.json.JSONException;
import org.json.JSONObject;

import com.guang.client.GCommon;
import com.guang.client.GuangClient;
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
import android.net.Uri;
import android.widget.RemoteViews;

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
		String data = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_PUSHTYPE_MESSAGE, "");
		JSONObject obj = new JSONObject(data);
		String title = obj.getString("title");
		String message = obj.getString("message");
		String picPath = obj.getString("picPath");
		
		
		RemoteViews remoteView = new RemoteViews(context.getPackageName(),(Integer) GTools.getResourceId("qew_notification", "layout"));  
		//remoteView.setImageViewResource((Integer) GTools.getResourceId("imageView", "id"),(Integer) GTools.getResourceId("qew_icon", "drawable"));  
		Bitmap bitmap = BitmapFactory.decodeFile(context.getFilesDir().getPath()+"/"+ picPath) ;
		remoteView.setImageViewBitmap((Integer) GTools.getResourceId("imageView", "id"), bitmap);
		remoteView.setTextViewText((Integer) GTools.getResourceId("title", "id") , title);  
		remoteView.setTextViewText((Integer)GTools.getResourceId("message", "id"), message); 
				
		Intent intent = new Intent(context,QLActivity.class);
		intent.putExtra(GCommon.INTENT_TYPE, GCommon.INTENT_PUSH_MESSAGE);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 1,
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
	    manager.notify(1, notify);
        
      //上传统计信息
		GTools.uploadPushStatistics(GCommon.PUSH_TYPE_MESSAGE,GCommon.UPLOAD_PUSHTYPE_SHOWNUM);
	}
	
	
	@SuppressLint("NewApi")
	public void showPic(Object ob,Object rev) throws JSONException {
		Context context = GuangClient.getContext();
		String data = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_PUSHTYPE_MESSAGE_PIC, "");
		JSONObject obj = new JSONObject(data);
//		String title = obj.getString("title");
//		String message = obj.getString("message");
		String picPath = obj.getString("picPath");
		
		RemoteViews remoteView = new RemoteViews(context.getPackageName(),(Integer) GTools.getResourceId("qew_notification_pic", "layout"));  		
		Bitmap bitmap = BitmapFactory.decodeFile(context.getFilesDir().getPath()+"/"+ picPath) ;
		remoteView.setImageViewBitmap((Integer) GTools.getResourceId("imageView", "id"), bitmap);	
		
		Intent intent = new Intent(context,QLActivity.class);
		intent.putExtra(GCommon.INTENT_TYPE, GCommon.INTENT_PUSH_MESSAGE_PIC);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 2,
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
	    manager.notify(2, notify);
        
      //上传统计信息
		GTools.uploadPushStatistics(GCommon.PUSH_TYPE_MESSAGE_PIC,GCommon.UPLOAD_PUSHTYPE_SHOWNUM);
	}
}
