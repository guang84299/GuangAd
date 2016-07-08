package com.qinglu.ad;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.guang.client.GCommon;
import com.guang.client.tools.GTools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Â∞?±º
 * Ê∞¥Âπ≥Êª??ListView
 */
public class QLHorizontalListViewAdapter extends BaseAdapter{  
   private  ArrayList<String>appImage;  
   private  ArrayList<String> appName;  
   private Context mContext;  
   private LayoutInflater mInflater;  
   private int selectIndex = -1;  
   private JSONObject json;
   public QLHorizontalListViewAdapter(Context context,
		   ArrayList<String> appName,
		   ArrayList<String> appImage,
		   JSONObject json
		   ){  
       this.mContext = context;  
       this.appImage = appImage;  
       this.appName = appName;  
       this.json = json;
//       mInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       mInflater = LayoutInflater.from(mContext);  
   }  
   

   @Override  
   public int getCount() {  
       return appImage.size();  
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
    	   
    	   //≥ı ºªØ ”Õº
           convertView = mInflater.inflate(
        		   (Integer)GTools.getResourceId("qew_list_item", "layout"),
        		   null);  
       	WindowManager wm = (WindowManager) QLDownActivity.getContext()
				.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
           convertView.setPadding(width/60, width/60, width/60, width/60);
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
 
  
   Bitmap bitmap;
   public class ViewHolder { 
	   private TextView mTitle ;  
	   private ImageView mImage; 
       public ViewHolder(View convertView){
    	   mImage=(ImageView)convertView.findViewById((Integer) GTools.getResourceId("imageView_app_bottom", "id"));  
           mTitle=(TextView)convertView.findViewById((Integer) GTools.getResourceId("textView_app_bottom", "id"));
  
       }
       
       public void bindData(int position) {
    	   try {
			if(appImage.get(position)!= null 
					&& !"".equals(appImage.get(position))
					&& !json.getString("name").equals(appName.get(position))){
					bitmap = BitmapFactory.decodeFile(mContext.getFilesDir().getPath()+"/"+ appImage.get(position));
				   	WindowManager wm = (WindowManager) QLDownActivity.getContext()
							.getSystemService(Context.WINDOW_SERVICE);
					int width = wm.getDefaultDisplay().getWidth();
					int height = wm.getDefaultDisplay().getHeight();
				   	LayoutParams layout = mImage.getLayoutParams();
				   	layout.width = width/5;
				   	layout.height = width/5;
				   	mImage.setImageBitmap(bitmap);  
				   	mTitle.setText(appName.get(position));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     
   }  
   public void setSelectIndex(int i){  
       selectIndex = i;  
   } }
  
}