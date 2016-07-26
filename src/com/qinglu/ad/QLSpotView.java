package com.qinglu.ad;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.guang.client.GCommon;
import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;
import com.qinglu.ad.listener.QLSpotDialogListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class QLSpotView extends RelativeLayout{
	private int id;
	private  int s = 3;//关闭时间
	private ImageView close;
	private Bitmap viewBm;
	private Bitmap closeBm;
	private QLSpotDialogListener dialogListener;
	private QLSize size;
	private int type;//0：有米类型插屏 1：推送一个插屏
	
	public QLSpotView(Context context) {
		super(context);
	}
	
	
	public QLSpotView(Context context,int animationType,int type,QLSpotDialogListener dialogListener) {
		super(context);
		this.type = type;
		this.dialogListener = dialogListener;
		this.init(context, animationType);
	}
	
	

	public QLSize getSize() {
		return size;
	}

	public void setSize(QLSize size) {
		this.size = size;
	}

	public QLSpotDialogListener getDialogListener() {
		return dialogListener;
	}

	public void setDialogListener(QLSpotDialogListener dialogListener) {
		this.dialogListener = dialogListener;
	}
	
	private void init(final Context context,int animationType)
	{
		JSONObject obj =  GTools.getPushShareData(GCommon.SHARED_KEY_PUSHTYPE_SPOT, -1);	
		if(type == GCommon.SPOT_TYPE_PUSH)
			getSpotViewPush(context,animationType,obj);		
		else if(type == GCommon.SPOT_TYPE_APP)
			getSpotViewApp(context,animationType,obj);		
	}
	
	@SuppressLint("NewApi")
	private void getSpotViewApp(final Context context,int animationType,final JSONObject obj)
	{
		ImageView view = new ImageView(context);
		try {			
			viewBm = BitmapFactory.decodeFile(context.getFilesDir().getPath()+"/"+ obj.getString("picPath")) ;
			view.setImageBitmap(viewBm);
			//底层容器			
			final QLSpotView layout = this;

			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);		
			layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			//int dir = context.getResources().getConfiguration().orientation;
			QLSize ss = GTools.getScreenSize(context);
			layoutParams.width = ss.width;
			layoutParams.height = ss.height;
			
			view.setId(1);
			view.setScaleType(ScaleType.FIT_XY);

			layout.addView(view, layoutParams);	
			
			//关闭按钮		
			RelativeLayout.LayoutParams paramsClose = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
			
			LinearLayout layoutGray = new LinearLayout(context);
			layoutGray.setBackgroundColor(Color.BLACK);
			layoutGray.setAlpha(0.6f);
			layoutGray.setId(2);
			layoutGray.setLayoutParams(paramsClose);
			layout.addView(layoutGray);	
			
			paramsClose.width = (int) (ss.width*0.15);
			paramsClose.height = (int) (ss.width*0.08);
			
			RelativeLayout.LayoutParams paramsCloseText = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
			final TextView closeText = new TextView(context);
			closeText.setText(s+" 跳过");
			closeText.setTextColor(Color.WHITE);
			closeText.setTextSize(16);
			closeText.setLayoutParams(paramsCloseText);
			layout.addView(closeText);
			
			paramsCloseText.width = (int) (paramsClose.width*0.9);
			paramsCloseText.height = (int) (paramsClose.height*0.9);
			
			int m_w = (paramsClose.width - paramsCloseText.width)/2;
			int m_h = (paramsClose.height - paramsCloseText.height)/2;
			paramsCloseText.setMargins(0, 20+m_h, 20+m_w, 0);
			paramsCloseText.addRule(RelativeLayout.ALIGN_TOP, 1);
			paramsCloseText.addRule(RelativeLayout.ALIGN_RIGHT, 1);
			paramsClose.addRule(RelativeLayout.ALIGN_TOP, 1);
			paramsClose.addRule(RelativeLayout.ALIGN_RIGHT, 1);
			paramsClose.setMargins(0, 20, 20, 0);
			
			final Handler handler = new Handler(){
				@Override
				public void handleMessage(Message msg) {
					if(msg.what == 0x11)
					{
						if(s == -1)
						{
							layout.removeAllViews();
							ViewGroup parent = ( ViewGroup )layout.getParent();
							parent.removeView(layout);
							
							if(viewBm != null && !viewBm.isRecycled())
							{
								viewBm.recycle();
								viewBm = null;
							}					
							System.gc();
							
							if(dialogListener != null)
							{
								dialogListener.onSpotClosed();
							}
						}
						else
						{
							closeText.setText(s+" 跳过");
						}				
					}
					super.handleMessage(msg);
				}
			};
			
			new Thread(){
				public void run() {
					try {
						
						while(s > -1)
						{
							Thread.sleep(1200);
							s --;
							handler.sendEmptyMessage(0x11);				
						}				
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				};
			}.start();
			
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					s = -2;
					layout.removeAllViews();
					ViewGroup parent = ( ViewGroup )layout.getParent();
					parent.removeView(layout);
					//Toast.makeText(context, "开始为您下载应用...", 0).show();
					if(dialogListener != null)
					{
						dialogListener.onSpotClick(true);
					}
					if(viewBm != null && !viewBm.isRecycled())
					{
						viewBm.recycle();
						viewBm = null;
					}					
					System.gc();
					if(dialogListener != null)
					{
						dialogListener.onSpotClosed();
					}
				}
			});
			
			closeText.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					s = -2;
					layout.removeAllViews();
					ViewGroup parent = ( ViewGroup )layout.getParent();
					parent.removeView(layout);
					
					if(viewBm != null && !viewBm.isRecycled())
					{
						viewBm.recycle();
						viewBm = null;
					}					
					System.gc();
					
					if(dialogListener != null)
					{
						dialogListener.onSpotClosed();
					}
				}
			});
			if(dialogListener != null)
			{
				dialogListener.onShowSuccess();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	@SuppressLint("NewApi")
	private void getSpotViewPush(final Context context,int animationType,final JSONObject obj)
	{
		ImageView view = new ImageView(context);
		try {			
			viewBm = BitmapFactory.decodeFile(context.getFilesDir().getPath()+"/"+ obj.getString("picPath")) ;
			view.setImageBitmap(viewBm);
			//底层容器			
			final QLSpotView layout = this;
			
			//遮罩
			LinearLayout.LayoutParams layoutGrayParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
			layoutGrayParams.gravity = Gravity.CENTER;
			
			LinearLayout layoutGray = new LinearLayout(context);
			layoutGray.setBackgroundColor(Color.BLACK);
			layoutGray.setAlpha(0.6f);
			layoutGray.setLayoutParams(layoutGrayParams);
			layout.addView(layoutGray);	
			//广告
			QLSize ss = GTools.getScreenSize(context);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			
			layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			float width = viewBm.getWidth();
			float height = viewBm.getHeight();
			int dir = context.getResources().getConfiguration().orientation;
			if(dir == 1)
			{
				layoutParams.width = (int) (ss.width*0.9);
				layoutParams.height = (int) (ss.width*0.9/width*height);
			}
			else
			{
				layoutParams.height = (int) (ss.height*0.8);
				layoutParams.width = (int) (ss.height*0.8/height*width);
				
			}
			this.setSize(new QLSize(layoutParams.width, layoutParams.height));
			view.setId(1);
			view.setScaleType(ScaleType.FIT_XY);

			layout.addView(view, layoutParams);		
			
			//关闭按钮		
			RelativeLayout.LayoutParams paramsClose = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
			
			close = new ImageView(context);
			closeBm = BitmapFactory.decodeFile(context.getFilesDir().getPath()+"/images/close.png");
			close.setImageBitmap(closeBm);
			close.setVisibility(View.GONE);
			
			if(dir == 1)
			{
				paramsClose.width = (int) (ss.width*0.05);
				paramsClose.height = (int) (ss.width*0.05);
			}
			else
			{
				paramsClose.width = (int) (ss.height*0.05);
				paramsClose.height = (int) (ss.height*0.05);
			}
			
			paramsClose.addRule(RelativeLayout.ALIGN_TOP, 1);
			paramsClose.addRule(RelativeLayout.ALIGN_RIGHT, 1);
			
			layout.addView(close,paramsClose);
			
			//设置动画
			if(animationType == GCommon.ANIM_SIMPLE)
			{
				AnimationSet animaSet = new AnimationSet(true);
				AlphaAnimation anima = new AlphaAnimation((float) 0.5, 1);
				anima.setDuration(500);
				animaSet.addAnimation(anima);
				animaSet.setAnimationListener(new QLAnimationListener());
				view.startAnimation(animaSet);
			}
			else if(animationType == GCommon.ANIM_ADVANCE)
			{
				AnimationSet animaSet = new AnimationSet(true);
																
				ScaleAnimation sca1 = new ScaleAnimation(-0.8f, 1.f, 1.f, 1.f, layoutParams.width/2, layoutParams.height/2);
				sca1.setDuration(600);
				ScaleAnimation sca2 = new ScaleAnimation(1.2f, 1.f, 1.f, 1.f, layoutParams.width/2, layoutParams.height/2);
				sca2.setDuration(400);
							
				animaSet.addAnimation(sca1);
				animaSet.addAnimation(sca2);
				animaSet.setAnimationListener(new QLAnimationListener());
				view.startAnimation(animaSet);
			}
			else
			{
				close.setVisibility(View.VISIBLE);
			}
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					layout.removeAllViews();
					ViewGroup parent = ( ViewGroup )layout.getParent();
					parent.removeView(layout);
					//Toast.makeText(context, "开始为您下载应用...", 0).show();
					if(dialogListener != null)
					{
						dialogListener.onSpotClick(true);
					}
					if(viewBm != null && !viewBm.isRecycled())
					{
						viewBm.recycle();
						viewBm = null;
					}
					if(closeBm != null && !closeBm.isRecycled())
					{
						closeBm.recycle();
						closeBm = null;
					}
					System.gc();
					if(dialogListener != null)
					{
						dialogListener.onSpotClosed();
					}
				}
			});
			
			close.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					layout.removeAllViews();
					ViewGroup parent = ( ViewGroup )layout.getParent();
					parent.removeView(layout);
					
					if(viewBm != null && !viewBm.isRecycled())
					{
						viewBm.recycle();
						viewBm = null;
					}
					if(closeBm != null && !closeBm.isRecycled())
					{
						closeBm.recycle();
						closeBm = null;
					}
					System.gc();
					
					if(dialogListener != null)
					{
						dialogListener.onSpotClosed();
					}
				}
			});
			if(dialogListener != null)
			{
				dialogListener.onShowSuccess();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	class QLAnimationListener implements AnimationListener
	{

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			if(close != null)
			{
				close.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
