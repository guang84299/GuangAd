package com.qinglu.ad;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.guang.client.GCommon;
import com.guang.client.tools.GTools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author by 小鱼
 *	app下载界面
 */
public class QLDownActivity extends Activity {
	
	
	
	RelativeLayout relativeLayout;
	static Context context;

	public static String JSON_ARR_POSITION  = "json_arr_position";
	QLHorizontalListView horizontalListView;
	QLHorizontalListView horizontalListViewPic;
	private ArrayList<String> appName ;
	private ArrayList<String> image;
	private QLExpandableTextView expandableTextView; 
	//收藏
	TextView textCollect; 
	//下载
	Button buttonDown;
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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView((Integer) GTools.getResourceId("qew_down_main", "layout"));
		horizontalListView = (QLHorizontalListView) findViewById((Integer) GTools.getResourceId("horizontalListView", "id"));
		textCollect = (TextView) findViewById((Integer) GTools.getResourceId("textView_shouc", "id"));
		expandableTextView = (QLExpandableTextView) findViewById((Integer) GTools.getResourceId("textView_app_js", "id"));
		buttonDown = (Button) findViewById((Integer) GTools.getResourceId("button_Down", "id"));
		textFengXiang = (TextView) findViewById((Integer) GTools.getResourceId("textView_fenxiang", "id"));
		imageViewTop = (ImageView) findViewById((Integer) GTools.getResourceId("imageView_app_imager", "id"));
		textAppName = (TextView) findViewById((Integer) GTools.getResourceId("textView_app_name", "id"));
		textDownNum = (TextView) findViewById((Integer) GTools.getResourceId("textView_down_num", "id"));
		relativeLayout = (RelativeLayout) findViewById((Integer) GTools.getResourceId("relativa_list", "id"));
		textAppType = (TextView) findViewById((Integer) GTools.getResourceId("textView_app_type","id"));
		horizontalListViewPic = (QLHorizontalListView) findViewById((Integer) GTools.getResourceId("HorizontalListView_app_image", "id"));
		textAppSize = (TextView) findViewById((Integer) GTools.getResourceId("textView_app_size", "id"));
		textViewFZ = (TextView) findViewById((Integer) GTools.getResourceId("textView_fzgs","id"));
		textAppVersion = (TextView) findViewById((Integer) GTools.getResourceId("textView_version_num", "id"));
		textAppUpdata = (TextView) findViewById((Integer) GTools.getResourceId("textView_app_updata", "id"));
		imageViewUpdata  = (ImageView) findViewById((Integer) GTools.getResourceId("imageView_app_updata", "id"));
		textViewXjj = (TextView) findViewById((Integer) GTools.getResourceId("textView_xjj", "id"));
		//加载数据
		initData();
		//添加适配器
		adapter =new QLHorizontalListViewAdapter(context, appName, image,showJsonObj);
		horizontalListView.setAdapter(adapter);
	
		horizontalListViewPic.setAdapter(
				new QLHorizontalListPicAdapter(context, picList));
		//设置位置
		setViewXY();
	
		setAppContext();
		//收藏
		doCollect();
		
		
		Intent intent = getIntent();
		String pId = intent.getStringExtra(JSON_ARR_POSITION);
		if (pId != null && !"".equals(pId))
		{
			int type = GCommon.PUSH_TYPE_MESSAGE;
			if(GCommon.INTENT_PUSH_MESSAGE_PIC.equals(push_type)){
				type = GCommon.PUSH_TYPE_MESSAGE_PIC;
			}
			//上传统计信息
			try {
				GTools.uploadPushStatistics(type,GCommon.UPLOAD_PUSHTYPE_SHOWNUM,obj.getString("pushId"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
		
	/**
	 * 收藏，分享QQ、微信，朋友圈等
	 */
	private void doCollect() {
		// TODO Auto-generated method stub
		
		
		
		//收藏
		textCollect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			
		}});
		
		
		//下载应用
		buttonDown.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
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
				
			}
		});
	
		
		
//		//刷新推荐应用、更新数据
	     imageViewUpdata.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//循环打乱顺序
				ArrayList<String> aName = new ArrayList<String>();
				ArrayList<String> aImage = new ArrayList<String>();
				ArrayList<JSONObject> listjson = new ArrayList<JSONObject>();
				ArrayList<String> pd = new ArrayList<String>();
				if (appName.size()>0) {
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
		//获得意图携带的类型
		push_type = intent.getStringExtra(GCommon.INTENT_TYPE);
		//获得需要显示的应用的数据
		if (GCommon.INTENT_PUSH_MESSAGE.equals(push_type)) {
			obj = GTools.getPushShareData(GCommon.SHARED_KEY_PUSHTYPE_MESSAGE, -1);
			data = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_PUSHTYPE_MESSAGE, "");
		}else{
			obj = GTools.getPushShareData(GCommon.SHARED_KEY_PUSHTYPE_MESSAGE_PIC, -1);
			data = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_PUSHTYPE_MESSAGE_PIC, "");
		}
		
		//获得有所有的应用信息(广告数据)
		String allApp = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_AD_APP_DATA, "");
		
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
					}else
					{
							obj = GTools.getPushShareDataByPushId(GCommon.SHARED_KEY_PUSHTYPE_MESSAGE_PIC, pId);
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
						
						//更新UI
						textViewFZ.setText(showJson.getString("developer"));
						textDownNum.setText("下载量："+showJson.getString("downloads")+"次");
						textAppName.setText(showJson.getString("name"));
						textViewXjj.setText(showJson.getString("summary"));
						textAppSize.setText(showJson.getString("size_m")+"M");
						textAppVersion.setText(showJson.getString("version"));
						textAppUpdata.setText(showJson.getString("updatedDate"));
						expandableTextView.setText(showJson.getString("summary")+"/\n"+showJson.getString("describe"));
						//如果路径不为空，设置图片
						if(showJson.getString("icon_path") != null && !"".equals(showJson.getString("icon_path"))){
						   imageViewTop.setImageBitmap(
								   BitmapFactory.decodeFile(context.getFilesDir().getPath()+"/"+showJson.getString("icon_path"))
							 );
						}
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
				
				
				String pushId = pushIds.get(position) ;
			
//				try {
//					String ai = listJson.get(position).getString("adId");
//					for(int i =0;i<array.length();i++){
//						if(array.getJSONObject(i).getString("adId").equals(ai)){
//							pushId = array.getJSONObject(i).getString("pushId");
//						}
//					}
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				Intent intent = new  Intent(context,QLDownActivity.class);
				intent.putExtra(GCommon.INTENT_TYPE, push_type);
				intent.putExtra(JSON_ARR_POSITION ,pushId);
            	startActivity(intent);
            	
            	
            }
        });
		
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
	
}
