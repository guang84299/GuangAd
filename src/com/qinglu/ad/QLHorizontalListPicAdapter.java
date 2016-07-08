package com.qinglu.ad;

import java.util.ArrayList;

import com.guang.client.GCommon;
import com.guang.client.tools.GTools;
import com.xugu.qewad.MainActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;


@SuppressLint("NewApi")
public class QLHorizontalListPicAdapter extends BaseAdapter{  
   private ArrayList<String> list ;
   private Context mContext;  
   private LayoutInflater mInflater;  
   private int selectIndex = -1;  
 
   public QLHorizontalListPicAdapter(Context context,ArrayList<String> list){  
       this.mContext = context;  
       this.list = list;
//       mInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       mInflater = LayoutInflater.from(mContext);  
   }  
   @Override  
   public int getCount() {  
       return list.size();  
   }  
   @Override  
   public Object getItem(int position) {  
       return position;  
   }  
 
   @Override  
   public long getItemId(int position) {  
       return position;  
   }  
 
   @Override  
   public View getView(int position, View convertView, ViewGroup parent) {  
       ViewHolder holder;  
       if(convertView==null){  
    	   //æ¨¡æ?
           convertView = mInflater.inflate(
        		   (Integer)GTools.getResourceId("qew_list_item_2", "layout"),
        		   null);   
           
           holder = new ViewHolder(convertView);  
           convertView.setTag(holder);  
       }else{  
           holder=(ViewHolder)convertView.getTag();  
           
       }  
       if(position == selectIndex){  
           convertView.setSelected(true);  
       }else{  
           convertView.setSelected(false);  
       }  
       holder.bindData(position);
       return convertView;  
   }  
 
  
   private  class ViewHolder {   
       private ImageView mImage;
       public ViewHolder(View convertView){
    	   mImage=(ImageView)convertView.findViewById((Integer) GTools.getResourceId("imageView_list_app_pic", "id"));  
       }
       /**
        * ç»???°æ?
        * @param position
        */
       public void bindData(int position) {
			// TODO Auto-generated method stub
    	   Bitmap bitmap= BitmapFactory.decodeFile(mContext.getFilesDir().getPath()+"/"+ list.get(position)) ;
    		WindowManager wm = (WindowManager) QLDownActivity.getContext()
    				.getSystemService(Context.WINDOW_SERVICE);
    		int width = wm.getDefaultDisplay().getWidth();
    		int height = wm.getDefaultDisplay().getHeight();
    	    mImage.setImageBitmap(bitmap);
    	    	
       }
   }  
   public void setSelectIndex(int i){  
       selectIndex = i;  
   } 
 
}