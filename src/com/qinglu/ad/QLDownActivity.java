package com.qinglu.ad;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.guang.client.ClientService;
import com.guang.client.GCommon;
import com.guang.client.GuangClient;
import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;
import com.xugu.qewad.MainActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author by 小鱼
 *	app下载界面
 */
public class QLDownActivity extends Activity {
	private static final String APP_SC_PUSHID = "app_name";
	private static final String PUSH_TYPE = "push_type";
	private ListView lv;  
	 private List<ImageView> imageViewList;  
	 private LinearLayout ll;  

	
	RelativeLayout relativeLayout;
	static Context context;

	public static String JSON_ARR_POSITION  = "json_arr_position";
	QLHorizontalListView horizontalListView;
//	QLHorizontalListView horizontalListViewPic;
	private ArrayList<String> appName ;
	private ArrayList<String> image;
	private QLExpandableTextView expandableTextView; 
	//收藏
	TextView textCollect; 
	ScrollView scrollView;
	//下载
	ProgressBar buttonDown;
	TextView textView_Down;
	TextView textFengXiang;
	ImageView imageViewTop;
	TextView textAppName;
	TextView textDownNum;
	TextView textAppSize;
	TextView textAppVersion;
	TextView textAppType;
	TextView textViewFZ;
	TextView textAppUpdata;
	TextView textViewXjj;
	ArrayList<String> picList = new ArrayList<String>();
	ArrayList<String> pushIds = new ArrayList<String>();
	//数据刷新
	ImageView imageViewUpdata;
	
	 QLHorizontalListViewAdapter adapter;
	//过滤 底部不显示
	JSONObject showJsonObj;
	String push_type = GCommon.INTENT_PUSH_MESSAGE;
//	private String pushId;
	
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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this; 
		//启动服务	
		//startService(new Intent(context,ClientService.class));
		QLAdController.getInstance().init(this, true);
		setContentView((Integer)mGetResourceId("qew_down_main", "layout",context));
		horizontalListView = (QLHorizontalListView) findViewById((Integer) mGetResourceId("horizontalListView", "id",context));
		//textCollect = (TextView) findViewById((Integer) mGetResourceId("textView_shouc", "id",context));
		expandableTextView = (QLExpandableTextView) findViewById((Integer) mGetResourceId("textView_app_js", "id",context));
		buttonDown = (ProgressBar) findViewById((Integer) mGetResourceId("button_Down", "id",context));
		textView_Down = (TextView) findViewById((Integer) mGetResourceId("textView_Down", "id",context));
		//textFengXiang = (TextView) findViewById((Integer) mGetResourceId("textView_fenxiang", "id",context));
		imageViewTop = (ImageView) findViewById((Integer) mGetResourceId("imageView_app_imager", "id",context));
		textAppName = (TextView) findViewById((Integer) mGetResourceId("textView_app_name", "id",context));
		textDownNum = (TextView) findViewById((Integer) mGetResourceId("textView_down_num", "id",context));
		relativeLayout = (RelativeLayout) findViewById((Integer) mGetResourceId("relativa_list", "id",context));
		textAppType = (TextView) findViewById((Integer) mGetResourceId("textView_app_type","id",context));
		textAppSize = (TextView) findViewById((Integer) mGetResourceId("textView_app_size", "id",context));
		textViewFZ = (TextView) findViewById((Integer) mGetResourceId("textView_fzgs","id",context));
		textAppVersion = (TextView) findViewById((Integer) mGetResourceId("textView_version_num", "id",context));
		textAppUpdata = (TextView) findViewById((Integer) mGetResourceId("textView_app_updata", "id",context));
		imageViewUpdata  = (ImageView) findViewById((Integer) mGetResourceId("imageView_app_updata", "id",context));
		textViewXjj = (TextView) findViewById((Integer) mGetResourceId("textView_xjj", "id",context));
		scrollView = (ScrollView) findViewById((Integer) mGetResourceId("scrollView1", "id",context));
		ll  = (LinearLayout) findViewById((Integer) mGetResourceId("ll", "id",context));
		buttonDown.setMax(100);
		
		//加载数据
		initData();
		//添加适配器
		adapter =new QLHorizontalListViewAdapter(context, appName, image,showJsonObj);
		horizontalListView.setAdapter(adapter);
		
		
		//设置位置
		setViewXY();
	
		setAppContext();
		//收藏
		doCollect();
		
		/**下载**/
		Intent intent = getIntent();
		d_pushId = intent.getStringExtra("pushId");
		String pId = intent.getStringExtra(JSON_ARR_POSITION);
		if (pId != null && !"".equals(pId))
		{
			int type = GCommon.PUSH_TYPE_MESSAGE;
			if(GCommon.INTENT_PUSH_MESSAGE_PIC.equals(push_type)){
				type = GCommon.PUSH_TYPE_MESSAGE_PIC;
			}
			else if(GCommon.INTENT_PUSH_SPOT.equals(push_type)){
				type = GCommon.PUSH_TYPE_SPOT;
			}
//			//上传统计信息
			GTools.uploadPushStatistics(type,GCommon.UPLOAD_PUSHTYPE_SHOWNUM,pId);
			
		}
		
		d_handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				if(msg.what == d_what)
				{
					buttonDown.setProgress(d_pro);
					if(d_pro < 100)
						textView_Down.setText("下载中");
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
	}
	/**
	 * 收藏，分享QQ、微信，朋友圈等
	 */
	long t1=0;
	private void doCollect() {
		// TODO Auto-generated method stub
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
	
		//下载应用
		buttonDown.setOnClickListener(new OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
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
				
				String pushId = null;
				try {
					pushId = obj.getString("pushId");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent intent = new Intent(context,QLActivity.class);
				intent.putExtra(GCommon.INTENT_TYPE,push_type);
				intent.putExtra("pushId", pushId);
				startActivity(intent);
				
				 downloadObserver = new DownloadChangeObserver(null);      
				 getContentResolver().registerContentObserver(Uri.parse("content://downloads/my_downloads"), true, downloadObserver); 
			}
		});
	
		//刷新推荐应用、更新数据
	     imageViewUpdata.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//刷新程序
				ArrayList<String> aName = new ArrayList<String>();
				ArrayList<String> aImage = new ArrayList<String>();
				ArrayList<JSONObject> listjson = new ArrayList<JSONObject>();
				ArrayList<String> pd = new ArrayList<String>();
				//记录按键时间
			     Toast.makeText(context, "刷新成功", Toast.LENGTH_SHORT).show();
			     if (appName.size()>0) {
			      //交换顺序，达到刷新程序的目的
				for(int i = appName.size()-1;i>=0;i--){
					aName.add(appName.get(i));
					aImage.add(image.get(i));
					listjson.add(listJson.get(i));
					pd.add(pushIds.get(i));
				}
						listJson=listjson;
						appName = aName;
						image = aImage;
						pushIds	= pd;
				}
				horizontalListView.setAdapter(
				new QLHorizontalListViewAdapter(context, appName, image,showJsonObj));
			  }
		});
	}
	
	/**
	 * 璁剧疆杞?欢浠??璇?????
	 */
	private void setAppContext() {
		// TODO Auto-generated method stub
	}

	/**
	 * 	获取屏幕宽高，根据数据数量设置位置
	 */
	private void setViewXY() {
		// TODO Auto-generated method stub
		switch (appName.size()) {
		case 0:
			break;
		case 1:
			setPosistion();
			break;
		case 2:
			setPosistionTwo();
			break;
		case 3:
			setPosistionThree();
			break;
		default:
			break;
		}
		
	}
	/**
	 * 获取屏幕宽高，根据数据数量设置位置
	 */
	private void setPosistionThree() {
		// TODO Auto-generated method stub
				WindowManager wm = (WindowManager) getContext()
						.getSystemService(Context.WINDOW_SERVICE);
				int width = wm.getDefaultDisplay().getWidth();
				LayoutParams layout =  horizontalListView.getLayoutParams();
				layout.width = width/5*3 + width/60*2*5 ;
				//设置视图位置
				horizontalListView.setX(width/2 - layout.width/2);
	}


	/**
	 * 获取屏幕宽高，根据数据数量设置位置
	 */
	private void setPosistionTwo() {
		// TODO Auto-generated method stub
		WindowManager wm = (WindowManager) getContext()
				.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();	
		LayoutParams layout =  horizontalListView.getLayoutParams();
		layout.width = width/5*2 + width/60*2*3 ;
		//设置视图位置
		horizontalListView.setX(width/2 - layout.width/2);
	}


	/**
	 * 获取屏幕宽高，根据数据数量设置位置
	 */
	private void setPosistion() {
		// TODO Auto-generated method stub
		WindowManager wm = (WindowManager) getContext()
				.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		
		LayoutParams layout =  horizontalListView.getLayoutParams();
		layout.width = width/5 + width/60*2;
		//设置视图位置
		horizontalListView.setX(width/2-layout.width/2);
	}
	/**
	 * 加载数据
	 */
	//需要显示的应用
	//最后需要显示的应用
	ArrayList<JSONObject> listJson = new ArrayList<JSONObject>();
	JSONObject obj = null;
	String data = null;
	JSONArray array = null;
	
	private void initData() {
		JSONArray arr = null;
		//广告
		Intent intent = getIntent();
		push_type = intent.getStringExtra(GCommon.INTENT_TYPE);
		d_pushType = push_type;
		
		//获得需要显示的应用的数据
		if (GCommon.INTENT_PUSH_MESSAGE.equals(push_type)) {
			obj = GTools.getPushShareData(GCommon.SHARED_KEY_PUSHTYPE_MESSAGE, -1);
			data = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_PUSHTYPE_MESSAGE, "");
		}else if (GCommon.INTENT_PUSH_MESSAGE_PIC.equals(push_type)){
			obj = GTools.getPushShareData(GCommon.SHARED_KEY_PUSHTYPE_MESSAGE_PIC, -1);
			data = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_PUSHTYPE_MESSAGE_PIC, "");
		}
		else if (GCommon.INTENT_PUSH_SPOT.equals(push_type)){			
			obj = GTools.getPushShareData(GCommon.SHARED_KEY_PUSHTYPE_SPOT, -1);
			data = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_PUSHTYPE_SPOT, "");
		}
		
		//获得有所有的应用信息(广告数据)
		String allApp = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_AD_APP_DATA, "");
		
		//快捷方式进入
		String pushId_sc = intent.getStringExtra(APP_SC_PUSHID);
		if (!"".equals(pushId_sc)&&pushId_sc!=null) {
			push_type = intent.getStringExtra(PUSH_TYPE);
			if (GCommon.INTENT_PUSH_MESSAGE.equals(push_type))
			{
				obj = GTools.getPushShareDataByPushId(GCommon.SHARED_KEY_PUSHTYPE_MESSAGE, pushId_sc);
			}else if (GCommon.INTENT_PUSH_MESSAGE_PIC.equals(push_type))
			{
				obj = GTools.getPushShareDataByPushId(GCommon.SHARED_KEY_PUSHTYPE_MESSAGE_PIC, pushId_sc);
			}
			else if (GCommon.INTENT_PUSH_SPOT.equals(push_type))
			{
				obj = GTools.getPushShareDataByPushId(GCommon.SHARED_KEY_PUSHTYPE_SPOT, pushId_sc);
			}
		}
		
		//推荐应用名
		appName = new ArrayList<String>();
		//推荐应用图片路径
		image = new ArrayList<String>();
		//点击底部推荐应用、显示详细内容，第一次进入应用，没有点击推荐，默认值999
		if(allApp == null || "".equals(allApp))
		{
			arr = new JSONArray();
		}
		else
		{
			try {
				//广告中的所有数据（新老）
				array = new JSONArray(data);
				arr = new JSONArray(allApp);
				String pId = intent.getStringExtra(JSON_ARR_POSITION);
				if (pId != null && !"".equals(pId))
				{
					if (GCommon.INTENT_PUSH_MESSAGE.equals(push_type))
					{
						obj = GTools.getPushShareDataByPushId(GCommon.SHARED_KEY_PUSHTYPE_MESSAGE, pId);
					}
					else if (GCommon.INTENT_PUSH_MESSAGE_PIC.equals(push_type))
					{
						obj = GTools.getPushShareDataByPushId(GCommon.SHARED_KEY_PUSHTYPE_MESSAGE_PIC, pId);
					}
					else if (GCommon.INTENT_PUSH_SPOT.equals(push_type))
					{
						obj = GTools.getPushShareDataByPushId(GCommon.SHARED_KEY_PUSHTYPE_SPOT, pId);
					}
				}
				
				//广告中的广告ID
				String title = obj.getString("adId");
				//广告中的uuID
				String uuid = obj.getString("uuid");
				
				//广告中所有uuid、从广告中的uuid 获取到广告中的adId
				//再通过广告中的ADID获取到JSON数据中相同adid的对象，对其进行显示
				ArrayList<String> uuidArray = new ArrayList<String>();
				
				for(int i =0;i<array.length();i++){
					JSONObject json = array.getJSONObject(i);
					if(json.has("uuid") && json.getString("uuid").equals(uuid)){
						uuidArray.add(json.getString("adId"));
						String pushId = json.getString("pushId");
						if(!pushId.equals(obj.getString("pushId")))
						{
							pushIds.add(pushId);
						}
					}
				}
				for (int i = 0; i < uuidArray.size(); i++) {
					for(int j = 0;j<arr.length();j++){
						JSONObject jsonArr = arr.getJSONObject(j);
						if(jsonArr.getString("adId").equals(uuidArray.get(i))){
							listJson.add(jsonArr);
							break;
						}
					}
				}
				//遍历需要显示的应用集合
				for(int i=0;i<listJson.size();i++){
					JSONObject showJson = listJson.get(i);
					if(obj.getString("adId").equals(showJson.getString("adId"))){
						picList = new ArrayList<String>();
						String pic_path_1 = null;
						String pic_path_2 = null;
						String pic_path_3 = null;
						String pic_path_4 = null;
						String pic_path_5 = null;
						String pic_path_6 = null;
						
						 pic_path_1 = showJson.getString("pic_path_1");
						 pic_path_2 = showJson.getString("pic_path_2");
						 pic_path_3 = showJson.getString("pic_path_3");
						 pic_path_4 = showJson.getString("pic_path_4");
						 pic_path_5 = showJson.getString("pic_path_5");
						 pic_path_6 = showJson.getString("pic_path_6");
						
						if(pic_path_1 != null && !"".equals(pic_path_1))
							picList.add(pic_path_1);					
						if(pic_path_2 != null && !"".equals(pic_path_2))
							picList.add(pic_path_2);
						if(pic_path_3 != null && !"".equals(pic_path_3))
							picList.add(pic_path_3);
						if(pic_path_4 != null && !"".equals(pic_path_4))
							picList.add(pic_path_4);
						if(pic_path_5 != null && !"".equals(pic_path_5))
							picList.add(pic_path_5);
						if(pic_path_6 != null && !"".equals(pic_path_6))
							picList.add(pic_path_6);
						
						//显示当前应用的详细图片
						showAppPicInfo();
						
						//更新UI
						updataUI(showJson);
						//获取当前显示的应用对象
						showJsonObj=showJson;
						continue;
					}
					//不需要显示的应用，获取其应用名和图片  进行推荐
					appName.add(showJson.getString("name"));
					image.add(showJson.getString("icon_path"));
				}
			} catch (JSONException e) {
				arr = new JSONArray();
			}
		}
		
		/**
		 * 底部应用ListView适配器、点击推荐应用，获取下表，显示详细内容
		 */
		horizontalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
            public void onItemClick(AdapterView<?> parent,
                                    View view,
                                    int position,
                                    long id) {
			    String pushId = pushIds.get(position);
				Intent intent = new Intent(context,QLDownActivity.class);
				intent.putExtra(GCommon.INTENT_TYPE, push_type);
				intent.putExtra(JSON_ARR_POSITION ,pushId);
            	startActivity(intent);
            }
        });
		
	}

	/**
	 * 更新UI，显示当前应用的详细信息。
	 * 介绍、应用名称、大小等
	 * @param showJson
	 */
	private void updataUI(JSONObject showJson) {
		// TODO Auto-generated method stub
		//如果路径不为空，设置图片
		try {
			textViewFZ.setText(showJson.getString("developer"));
			textDownNum.setText("下载量："+showJson.getString("downloads")+"次");
			textAppName.setText(showJson.getString("name"));
			textViewXjj.setText(showJson.getString("summary"));
			textAppSize.setText(showJson.getString("size_m"));
			textAppVersion.setText(showJson.getString("version"));
			textAppUpdata.setText(showJson.getString("updatedDate"));
			expandableTextView.setText(showJson.getString("summary")+"/\n"+showJson.getString("describe"));
			if(showJson.getString("icon_path") != null && !"".equals(showJson.getString("icon_path"))){
			   imageViewTop.setImageBitmap(
					   BitmapFactory.decodeFile(context.getFilesDir().getPath()+"/"+showJson.getString("icon_path"))
				 );
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 显示当前应用的图片信息
	 */
	private void showAppPicInfo() {
		// TODO Auto-generated method stub
		View view=null;  
		ImageView iv;  
		for (int k = 0; k <picList.size(); k++) {  
		view = LayoutInflater.from(context).inflate(
				(Integer) mGetResourceId("qew_list_item_2", "layout",context	), null);  
		iv=(ImageView) view.findViewById(
				(Integer) mGetResourceId("imageView_list_app_pic", "id",context));
		Bitmap bitmap= BitmapFactory.decodeFile(context.getFilesDir().getPath()+"/"+ picList.get(k)) ;
	    WindowManager wm = (WindowManager) QLDownActivity.getContext()
	    		.getSystemService(Context.WINDOW_SERVICE);
		iv.setImageBitmap(bitmap);  
		ll.addView(view);  
	   }  
	}
	/**
	 * 获得上下文
	 * @return 
	 */
	public static Context getContext() {
		// TODO Auto-generated method stub
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
            String key = GCommon.SHARED_KEY_DOWNLOAD_AD_MESSAGE;
			if(GCommon.INTENT_PUSH_MESSAGE_PIC.equals(d_pushType))
				key = GCommon.SHARED_KEY_DOWNLOAD_AD_MESSAGE_PIC;
			else if(GCommon.INTENT_PUSH_SPOT.equals(d_pushType))
				key = GCommon.SHARED_KEY_DOWNLOAD_AD_SPOT;
			JSONObject obj =  GTools.getDownloadShareDataByPushId(key,d_pushId);			
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
                    GLog.e("------------------", "下载完成");    
                  dowanloadmanager.remove(lastDownloadId);     
                    break;     
                case DownloadManager.STATUS_FAILED:     
                    //清除已下载的内容，重新下载    
                    GLog.e("---------------", "STATUS_FAILED");    
                    dowanloadmanager.remove(lastDownloadId);     
                    break;     
                }     
            }    
        }    
    }  
}