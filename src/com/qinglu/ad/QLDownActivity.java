package com.qinglu.ad;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.guang.client.GCommon;
import com.guang.client.controller.GOfferController;
import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author by 小鱼
 *	app下载界面
 */
public class QLDownActivity extends Activity {
	private LinearLayout ll;  
	private  ArrayList<JSONObject> list;
	private static Activity context;
	private QLHorizontalListView horizontalListView;
	private QLExpandableTextView expandableTextView; 
	//下载
	private ProgressBar buttonDown;
	private TextView textView_Down;
	private ImageView imageViewTop;
	private TextView textAppName;
	private TextView textDownNum;
	private TextView textAppSize;
	private TextView textAppVersion;
	private TextView textViewFZ;
	private TextView textAppUpdata;
	private TextView textViewXjj;
	//数据刷新
	private ImageView imageViewUpdata;
	
	private QLHorizontalListViewAdapter adapter;

	
	private DownloadChangeObserver downloadObserver;  
	private String d_pushId;
	private String d_pushType;
	private int d_pro;
	private Handler d_handler;
	private int d_what = 0x11;
	private int d_what_pause = 0x12;
	private int d_what_resume = 0x13;
	private int d_state = 0;
	private long d_downloadId;
	
	private long offerId;
	private JSONObject obj_self = null;
	private int intent_open_type;
	private int ad_position_type;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this; 
		setContentView((Integer)mGetResourceId("qew_down_main", "layout",context));
		horizontalListView = (QLHorizontalListView) findViewById((Integer) mGetResourceId("horizontalListView", "id",context));
		expandableTextView = (QLExpandableTextView) findViewById((Integer) mGetResourceId("textView_app_js", "id",context));
		buttonDown = (ProgressBar) findViewById((Integer) mGetResourceId("button_Down", "id",context));
		textView_Down = (TextView) findViewById((Integer) mGetResourceId("textView_Down", "id",context));
		imageViewTop = (ImageView) findViewById((Integer) mGetResourceId("imageView_app_imager", "id",context));
		textAppName = (TextView) findViewById((Integer) mGetResourceId("textView_app_name", "id",context));
		textDownNum = (TextView) findViewById((Integer) mGetResourceId("textView_down_num", "id",context));
		//relativeLayout = (RelativeLayout) findViewById((Integer) mGetResourceId("relativa_list", "id",context));
		//textAppType = (TextView) findViewById((Integer) mGetResourceId("textView_app_type","id",context));
		textAppSize = (TextView) findViewById((Integer) mGetResourceId("textView_app_size", "id",context));
		textViewFZ = (TextView) findViewById((Integer) mGetResourceId("textView_fzgs","id",context));
		textAppVersion = (TextView) findViewById((Integer) mGetResourceId("textView_version_num", "id",context));
		textAppUpdata = (TextView) findViewById((Integer) mGetResourceId("textView_app_updata", "id",context));
		imageViewUpdata  = (ImageView) findViewById((Integer) mGetResourceId("imageView_app_updata", "id",context));
		textViewXjj = (TextView) findViewById((Integer) mGetResourceId("textView_xjj", "id",context));
		ll  = (LinearLayout) findViewById((Integer) mGetResourceId("ll", "id",context));
		buttonDown.setMax(100);
		
		try {
			updateUI();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		addEvents();
		
		Intent intent = getIntent();
		intent_open_type = intent.getIntExtra(GCommon.INTENT_OPEN_DOWNLOAD, 0);
		ad_position_type = intent.getIntExtra(GCommon.AD_POSITION_TYPE, 1);
		//自己打开界面，需要上传统计
		if(GCommon.OPEN_DOWNLOAD_TYPE_SELF == intent_open_type)
		{
			GTools.uploadStatistics(GCommon.DOUBLE_SHOW,ad_position_type,offerId);
		}
	}
	
	private void updateUI() throws JSONException
	{
		Intent intent = getIntent();
		offerId = intent.getLongExtra("offerId", 0);
		JSONObject obj = GOfferController.getInstance().getOfferById(offerId);
		if(obj == null)
		{
			context.finish();
			return;
		}
		obj_self = obj;
		Bitmap bm = BitmapFactory.decodeFile(context.getFilesDir().getPath()+"/"+ obj.getString("apk_icon_path"));
		imageViewTop.setImageBitmap(bm);
		
		textAppName.setText(obj.getString("name"));
		textDownNum.setText("下载量："+obj.getInt("apk_downloads")+"次");
		textViewXjj.setText(obj.getString("apk_summary"));
		textAppVersion.setText(obj.getString("apk_version"));
		textAppSize.setText(obj.getDouble("apk_size")+"");
		textAppUpdata.setText(obj.getString("apk_updatedDate"));
		textViewFZ.setText(obj.getString("apk_developer"));		
		expandableTextView.setText(obj.getString("apk_describe"));
		
		String apk_pic_path_1 = obj.getString("apk_pic_path_1");
		String apk_pic_path_2 = obj.getString("apk_pic_path_2");
		String apk_pic_path_3 = obj.getString("apk_pic_path_3");
		String apk_pic_path_4 = obj.getString("apk_pic_path_4");
		String apk_pic_path_5 = obj.getString("apk_pic_path_5");
		String apk_pic_path_6 = obj.getString("apk_pic_path_6");
		List<String> picList = new ArrayList<String>();
		if(apk_pic_path_1 != null && !"".equals(apk_pic_path_1))
			picList.add(apk_pic_path_1);
		if(apk_pic_path_2 != null && !"".equals(apk_pic_path_2))
			picList.add(apk_pic_path_2);
		if(apk_pic_path_3 != null && !"".equals(apk_pic_path_3))
			picList.add(apk_pic_path_3);
		if(apk_pic_path_4 != null && !"".equals(apk_pic_path_4))
			picList.add(apk_pic_path_4);
		if(apk_pic_path_5 != null && !"".equals(apk_pic_path_5))
			picList.add(apk_pic_path_5);
		if(apk_pic_path_6 != null && !"".equals(apk_pic_path_6))
			picList.add(apk_pic_path_6);
		
		ll.removeAllViews();
		for (int k = 0; k < picList.size(); k++) 
		{  
			View view = LayoutInflater.from(context).inflate(
				(Integer) mGetResourceId("qew_list_item_2", "layout",context	), null);  
			ImageView iv=(ImageView) view.findViewById(
					(Integer) mGetResourceId("imageView_list_app_pic", "id",context));
			Bitmap bitmap= BitmapFactory.decodeFile(context.getFilesDir().getPath()+"/"+ picList.get(k)) ;
			iv.setImageBitmap(bitmap);  
			ll.addView(view);  
	    }
		
		list = new ArrayList<JSONObject>();
		String data = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_OFFER, "");
		JSONArray arr = new JSONArray(data);
		for (int i = 0; i < arr.length(); i++) {
			JSONObject obj2 = arr.getJSONObject(i);
			if (offerId != obj2.getLong("id")) {
				list.add(obj2);
			}
		}
		//添加适配器
		adapter =new QLHorizontalListViewAdapter(context, list);
		horizontalListView.setAdapter(adapter);
		
		QLSize ss = GTools.getScreenSize(this);
		int width = ss.width;
		int size = list.size();	
		LayoutParams layout =  horizontalListView.getLayoutParams();
		layout.width = width/5*size + width/60*2*(2*size-1);
		//设置视图位置
		horizontalListView.setX(width/2-layout.width/2);
	}
	
	private void addEvents()
	{
		//下载应用
		buttonDown.setOnClickListener(new OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
//				if(DownloadManager.STATUS_RUNNING == d_state)
//				{
//					 DownloadManager dowanloadmanager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE); 
//					 try {
//						dowanloadmanager.remove(d_downloadId);
//						d_handler.sendEmptyMessage(d_what_pause);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					return;
//				}
					
				String downloadPath = null;
				try {
					downloadPath = obj_self.getString("apkDownloadPath");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (downloadPath != null)
				{
					if(!downloadPath.contains("http://"))
						downloadPath = GCommon.SERVER_ADDRESS + downloadPath;
					GTools.downloadApk(downloadPath, ad_position_type,offerId,intent_open_type);
					
					downloadObserver = new DownloadChangeObserver(null);      
					context.getContentResolver().registerContentObserver(Uri.parse("content://downloads/my_downloads"), true, downloadObserver); 
					
					if(GCommon.OPEN_DOWNLOAD_TYPE_SELF == intent_open_type)
					{
						GTools.uploadStatistics(GCommon.DOUBLE_DOWNLOAD,ad_position_type,offerId);
					}
					else
					{
						GTools.uploadStatistics(GCommon.DOWNLOAD,ad_position_type,offerId);
					}
					
					buttonDown.setClickable(false);
				}					
			}
		});
			
		
		d_handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == d_what)
				{
					buttonDown.setProgress(d_pro);
					if(d_pro < 100)
						textView_Down.setText(d_pro+"%");
					else
						textView_Down.setText("下载完成");
				}
				else if(msg.what == d_what_pause)
				{
					textView_Down.setText("继续");
				}
				else if(msg.what == d_what_resume)
				{
					textView_Down.setText("暂停");
				}
				super.handleMessage(msg);
			}
			
		};
		
		//刷新推荐应用、更新数据
	     imageViewUpdata.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				List<JSONObject> listjson = new ArrayList<JSONObject>();			    
			    while(list.size() > 0)
			    {
			    	int index = GTools.getRand(0, list.size());
			    	listjson.add(list.get(index));
			    	list.remove(index);
			    }			    
			    for(JSONObject obj : listjson)
			    {
			    	list.add(obj);
			    }			     
				horizontalListView.setAdapter(new QLHorizontalListViewAdapter(context,list));
				Toast.makeText(context, "刷新成功", Toast.LENGTH_SHORT).show();
			  }
		});
	     
	     /**
		 * 底部应用ListView适配器、点击推荐应用，获取下表，显示详细内容
		 */
		horizontalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
            public void onItemClick(AdapterView<?> parent,
                                    View view,
                                    int position,
                                    long id) {
				Intent intent = new Intent(context,QLDownActivity.class);
				try {
					long offerId = list.get(position).getLong("id");
					GTools.uploadStatistics(GCommon.DOUBLE_CLICK,ad_position_type,offerId);
					
					intent.putExtra("offerId",offerId);
					intent.putExtra(GCommon.INTENT_OPEN_DOWNLOAD, GCommon.OPEN_DOWNLOAD_TYPE_SELF);
					intent.putExtra(GCommon.AD_POSITION_TYPE, ad_position_type);
					startActivity(intent);
					context.finish();
				} catch (JSONException e) {
					e.printStackTrace();
				}
            	
            }
        });
	}
	

	/**
	 * 获得上下文
	 * @return 
	 */
	public static Context getContext() {
		//需要显示的应用图片
		return context;
	}
	
	

	//获取资源id
	public static Object mGetResourceId(String name, String type,Context context) 
	{
		String className = context.getPackageName() +".R";
		try {
		Class<?> cls = Class.forName(className);
		for (Class<?> childClass : cls.getClasses()) 
		{
			String simple = childClass.getSimpleName();
			if (simple.equals(type)) 
			{
				for (Field field : childClass.getFields()) 
				{
					String fieldName = field.getName();
					if (fieldName.equals(name)) 
					{
						return field.get(null);
					}
				}
			}
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	class DownloadChangeObserver extends ContentObserver {  
			  
        public DownloadChangeObserver(Handler handler) {  
            super(handler);  
            // TODO Auto-generated constructor stub  
        }  
        @Override  
        public void onChange(boolean selfChange) {  
              try {
				queryDownloadStatus();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}     
        }  
        
        @SuppressLint("NewApi")
		private void queryDownloadStatus() throws JSONException {     
            DownloadManager.Query query = new DownloadManager.Query();   

			JSONObject obj =  GTools.getDownloadApkShareDataByOfferId(offerId);			
			long lastDownloadId = obj.getLong("id");
			d_downloadId = lastDownloadId;
            query.setFilterById(lastDownloadId);     
            DownloadManager dowanloadmanager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE); 
            Cursor c = dowanloadmanager.query(query);     
            if(c!=null&&c.moveToFirst()) {     
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));     
                d_state = status;
                //int reasonIdx = c.getColumnIndex(DownloadManager.COLUMN_REASON);    
              //  int titleIdx = c.getColumnIndex(DownloadManager.COLUMN_TITLE);    
                int fileSizeIdx =     
                  c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);        
                int bytesDLIdx =     
                  c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);    
               // String title = c.getString(titleIdx);    
                float fileSize = c.getInt(fileSizeIdx);    
                float bytesDL = c.getInt(bytesDLIdx);    
                float p = bytesDL / fileSize;
                d_pro = (int) (p*100);
                switch(status) {     
                case DownloadManager.STATUS_PAUSED:                	
                	 break;   
                case DownloadManager.STATUS_PENDING:   
                	 break;   
                case DownloadManager.STATUS_RUNNING:  
                	d_handler.sendEmptyMessage(d_what);
                    break;     
                case DownloadManager.STATUS_SUCCESSFUL:     
                    //完成    
                	d_handler.sendEmptyMessage(d_what);
                    GLog.e("------------------", "下载完成");    
                    //dowanloadmanager.remove(lastDownloadId);   
                    unRegisterContentObserver();
                    break;     
                case DownloadManager.STATUS_FAILED:     
                    //清除已下载的内容，重新下载    
                    GLog.e("---------------", "STATUS_FAILED");    
                    dowanloadmanager.remove(lastDownloadId);  
                    unRegisterContentObserver();
                    break;     
                }     
            }    
        }  
        
        private void unRegisterContentObserver()
        {
        	context.getContentResolver().unregisterContentObserver(downloadObserver);
        }
    }  
	
	
	@Override
	protected void onDestroy() {
		if(downloadObserver != null)
		context.getContentResolver().unregisterContentObserver(downloadObserver);
		super.onDestroy();
	}
	
//	private void doCollect() {
//		// TODO Auto-generated method stub
//		textFengXiang.setText("分享");
//		textFengXiang.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				Toast.makeText(context,"此功能暂未开放，尽请期待", Toast.LENGTH_SHORT).show();
//			}
//		});
//		
//		
//		//收藏、创建桌面快捷
//		textCollect.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				try {
//			    Intent shortcut = new Intent(  
//			    "com.android.launcher.action.INSTALL_SHORTCUT"); 
//			    // 不允许重建  
//			    shortcut.putExtra("duplicate", false);  
//			    // 获得应用名字、设置名字  、获取应用pushId
//			    String p = obj.getString("pushId");
//			    String name = showJsonObj.getString("name");
//			    shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,name);
//			    // 获取图标、设置图标  
//			    Bitmap bmp = BitmapFactory.decodeFile(context.getFilesDir().getPath()+"/"+showJsonObj.getString("icon_path"));
//			    shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, bmp);
//			    // 设置意图和快捷方式关联程序  
//			    Intent launcherIntent = new Intent();
//		        launcherIntent.setAction(Intent.ACTION_MAIN);
//		        //意图携带数据
//		        launcherIntent.putExtra(APP_SC_PUSHID, p);
//		        launcherIntent.putExtra(PUSH_TYPE, push_type);
//		        launcherIntent.setClass(context, QLDownActivity.class);
//		        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//		        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launcherIntent);
//                // 发送广播
//                sendBroadcast(shortcut);                
//                Toast.makeText(context,"收藏成功！", Toast.LENGTH_SHORT).show();
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//			}});	
//	}
}