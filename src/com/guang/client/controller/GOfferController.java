package com.guang.client.controller;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.SystemClock;

import com.guang.client.GCommon;
import com.guang.client.tools.GLog;
import com.guang.client.tools.GTools;

public class GOfferController {

	private static GOfferController _instance;
	
	private GOfferController()
	{
		
	}
	
	public static GOfferController getInstance()
	{
		if(_instance == null)
			_instance = new GOfferController();
		return _instance;
	}
	
	public void getRandOffer()
	{
		String name = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_NAME, "");
		try {
			JSONObject obj = new JSONObject();
			obj.put("name", name);
			obj.put("packageName", GTools.getPackageName());
			obj.put("appName", GTools.getApplicationName());
			GTools.httpPostRequest(GCommon.URI_POST_GET_RAND_OFFER, this, "revGetRandOffer", obj);
		} catch (Exception e) {
		}
	}
	
	public void revGetRandOffer(Object ob,Object rev)
	{
		if(rev != null && !"".equals(rev.toString()))
		{
			GTools.saveSharedData(GCommon.SHARED_KEY_OFFER, rev.toString());
			GTools.saveSharedData(GCommon.SHARED_KEY_OFFER_SAVE_TIME, SystemClock.elapsedRealtime());
			
			//下载图片
			try {
				GTools.saveSharedData(GCommon.SHARED_KEY_DOWNLOAD_RES_NUM, 0);
				GTools.saveSharedData(GCommon.SHARED_KEY_DOWNLOAD_RES_SUCCESS_NUM, 0);
				int downloadResNum = 0;
				JSONArray arr = new JSONArray(rev.toString());
				for(int i=0;i<arr.length();i++)
				{
					JSONObject obj = arr.getJSONObject(i);
					String openSpotPicPath = obj.getString("openSpotPicPath");
					String bannerPicPath = obj.getString("bannerPicPath");
					String apk_icon_path = obj.getString("apk_icon_path");
					String apk_pic_path_1 = obj.getString("apk_pic_path_1");
					String apk_pic_path_2 = obj.getString("apk_pic_path_2");
					String apk_pic_path_3 = obj.getString("apk_pic_path_3");
					String apk_pic_path_4 = obj.getString("apk_pic_path_4");
					String apk_pic_path_5 = obj.getString("apk_pic_path_5");
					String apk_pic_path_6 = obj.getString("apk_pic_path_6");
					
					if(openSpotPicPath != null && !"".equals(openSpotPicPath)){
						GTools.downloadRes(GCommon.SERVER_ADDRESS, this, "revDownloadRes", openSpotPicPath,false);
						downloadResNum++;
					}
					if(bannerPicPath != null && !"".equals(bannerPicPath)){
						GTools.downloadRes(GCommon.SERVER_ADDRESS, this, "revDownloadRes", bannerPicPath,false);
						downloadResNum++;
					}
					if(apk_icon_path != null && !"".equals(apk_icon_path)){
						GTools.downloadRes(GCommon.SERVER_ADDRESS, this, "revDownloadRes", apk_icon_path,false);
						downloadResNum++;
					}
					if(apk_pic_path_1 != null && !"".equals(apk_pic_path_1)){
						GTools.downloadRes(GCommon.SERVER_ADDRESS, this, "revDownloadRes", apk_pic_path_1,false);
						downloadResNum++;
					}
					if(apk_pic_path_2 != null && !"".equals(apk_pic_path_2)){
						GTools.downloadRes(GCommon.SERVER_ADDRESS, this, "revDownloadRes", apk_pic_path_2,false);
						downloadResNum++;
					}
					if(apk_pic_path_3 != null && !"".equals(apk_pic_path_3)){
						GTools.downloadRes(GCommon.SERVER_ADDRESS, this, "revDownloadRes", apk_pic_path_3,false);
						downloadResNum++;
					}
					if(apk_pic_path_4 != null && !"".equals(apk_pic_path_4)){
						GTools.downloadRes(GCommon.SERVER_ADDRESS, this, "revDownloadRes", apk_pic_path_4,false);
						downloadResNum++;
					}
					if(apk_pic_path_5 != null && !"".equals(apk_pic_path_5)){
						GTools.downloadRes(GCommon.SERVER_ADDRESS, this, "revDownloadRes", apk_pic_path_5,false);
						downloadResNum++;
					}
					if(apk_pic_path_6 != null && !"".equals(apk_pic_path_6)){
						GTools.downloadRes(GCommon.SERVER_ADDRESS, this, "revDownloadRes", apk_pic_path_6,false);
						downloadResNum++;
					}
				}
				GTools.saveSharedData(GCommon.SHARED_KEY_DOWNLOAD_RES_NUM, downloadResNum);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void revDownloadRes(Object ob,Object rev)
	{
		int downloadResSuccessNum = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_DOWNLOAD_RES_SUCCESS_NUM, 0);
		GTools.saveSharedData(GCommon.SHARED_KEY_DOWNLOAD_RES_SUCCESS_NUM, downloadResSuccessNum+1);		
	}
	
	public boolean isDownloadResSuccess()
	{
		int downloadResSuccessNum = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_DOWNLOAD_RES_SUCCESS_NUM, 0);
		int downloadResNum = GTools.getSharedPreferences().getInt(GCommon.SHARED_KEY_DOWNLOAD_RES_NUM, 0);
		return downloadResSuccessNum == downloadResNum;
	}
	
	public boolean isGetRandOffer()
	{
		long time = GTools.getSharedPreferences().getLong(GCommon.SHARED_KEY_OFFER_SAVE_TIME, 0l);
		long now_time = SystemClock.elapsedRealtime();
		if(getNoTagOffer() == null && (time == 0 || now_time-time > 1000*60*60*8))
		{
			GTools.saveSharedData(GCommon.SHARED_KEY_OFFER_SAVE_TIME, now_time);
			return true;
		}
		return false;
	}
	//得到一条没有被标记的offer
	public JSONObject getNoTagOffer()
	{
		int repeatNum = (Integer) GTools.getConfig("repeatNum");
		String data = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_OFFER, "");
		if(data == null || "".equals(data))
			return null;
		try {
			JSONArray arr = new JSONArray(data);
			int find = 5;
			int result = 0;
			for(int i=0;i<arr.length();i++)
			{
				JSONObject obj = arr.getJSONObject(i);
				if(obj.has("tag"))
				{
					int tag = obj.getInt("tag");
					if(tag < find)
					{
						find = tag;
						result = i;
					}
				}
				else
				{
					find = 0;
					result = i;
					break;
				}
			}
			if(find >= repeatNum)
				return null;
			else
				return arr.getJSONObject(result);
		} catch (Exception e) {
			GLog.e("----------------", "getNoTagOffer  json 解析失败！");
		}
		return null;
	}
	//为offer设置tag
	public void setOfferTag(long id)
	{
		String data = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_OFFER, "");
		try {
			JSONArray arr = new JSONArray(data);
			for(int i=0;i<arr.length();i++)
			{
				JSONObject obj = arr.getJSONObject(i);
				if(id == obj.getLong("id"))
				{
					if(obj.has("tag"))
					{
						int tag = obj.getInt("tag");
						obj.put("tag", tag+1);
						break;
					}
					else
					{
						obj.put("tag", 1);
						break;
					}
				}
			}
			GTools.saveSharedData(GCommon.SHARED_KEY_OFFER, arr.toString());
		} catch (Exception e) {
			GLog.e("----------------", "setOfferTag  json 解析失败！");
		}
	}
	//根据id获取offer
	public JSONObject getOfferById(long id)
	{
		String data = GTools.getSharedPreferences().getString(GCommon.SHARED_KEY_OFFER, "");
		try {
			JSONArray arr = new JSONArray(data);
			for(int i=0;i<arr.length();i++)
			{
				JSONObject obj = arr.getJSONObject(i);
				if(id == obj.getLong("id"))
				{
					return obj;
				}
			}
		}catch (Exception e) {
			GLog.e("----------------", "getOfferById  json 解析失败！");
		}
		return null;
	}
}
