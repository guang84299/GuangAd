package com.qinglu.ad;


import com.guang.client.GCommon;
import com.guang.client.tools.GTools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;

@SuppressLint("NewApi")
public class QLNotifyActivity extends Activity{
	private Activity context;
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                 WindowManager.LayoutParams.FLAG_FULLSCREEN );
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
				
		WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		
		final LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值    
		p.width = width;  
		p.height = (int) (height*0.1);    
        p.x = 0;
        p.y = -height/2;
        getWindow().setAttributes(p); 
                
        Intent intent = getIntent();
		final String type = intent.getStringExtra(GCommon.INTENT_TYPE);
		final String pushId = intent.getStringExtra("pushId");
		String picPath = intent.getStringExtra("picPath");
		
        LinearLayout.LayoutParams layoutGrayParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		final LinearLayout layoutGray = new LinearLayout(this);
		//layoutGray.setAlpha(0.6f);
		//layoutGray.setBackgroundColor(Color.GRAY);
		layoutGray.setLayoutParams(layoutGrayParams);
		
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		ImageView view = new ImageView(this);
		Bitmap bitmap = BitmapFactory.decodeFile(this.getFilesDir().getPath()+"/"+ picPath) ;
		view.setImageBitmap(bitmap);
		view.setScaleType(ScaleType.CENTER_CROP);
		layoutGray.addView(view,layoutParams);
		
		this.setContentView(layoutGray);
		overridePendingTransition((Integer)GTools.getResourceId("qew_slide_in_top", "anim"), 
				(Integer)GTools.getResourceId("qew_slide_out_top", "anim"));
		
		 //上传统计信息
		GTools.uploadPushStatistics(GCommon.PUSH_TYPE_MESSAGE_PIC,GCommon.UPLOAD_PUSHTYPE_SHOWNUM,pushId);
		
		view.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,QLDownActivity.class);
				intent.putExtra(GCommon.INTENT_TYPE, type);
				intent.putExtra("pushId", pushId);
				context.startActivity(intent);
				
				context.finish();
			}
		});
		
		new Thread(){
			public void run() {
				try {
					Thread.sleep(1000*60*2);
					context.finish();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();
	}
}
