package com.qinglu.ad;

import com.guang.client.tools.GTools;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class QLImageTextButton extends RelativeLayout {


	private ImageView imgView;  
	private TextView  textView;

    public QLImageTextButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub

    }
    
    public QLImageTextButton(Context context,AttributeSet attributeSet) {
        super(context, attributeSet);
        
        LayoutInflater.from(context).inflate((Integer)
        		GTools.getResourceId("qew_view_item", "layout"), this,true);
        
        this.imgView = (ImageView)findViewById((Integer) GTools.getResourceId("imgview", "id"));
        this.textView = (TextView)findViewById((Integer) GTools.getResourceId("textview", "id"));
        
        this.setClickable(true);
        this.setFocusable(true);
    }
    
    public void setImgResource(int resourceID) {
        this.imgView.setImageResource(resourceID);
    }
    
    public void setText(String text) {
        this.textView.setText(text);
    }
    
    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }
    
    public void setTextSize(float size) {
        this.textView.setTextSize(size);
    }
    
}