package com.guang.ad.core.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import com.guang.ad.core.GCommon;
import com.guang.ad.core.GConfig;
import com.guang.ad.core.GSysService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

@SuppressLint("NewApi")
public class GTools {

	private static final String TAG = "GTools";

	// �õ���ǰSharedPreferences
	public static SharedPreferences getSharedPreferences() {
		Context context = GSysService.getContexts();
		return context.getSharedPreferences(GCommon.SHARED_PRE,
				Activity.MODE_PRIVATE);
	}

	// ����һ��share����
	public static <T> void saveSharedData(String key, T value) {
		SharedPreferences mySharedPreferences = getSharedPreferences();
		Editor editor = mySharedPreferences.edit();
		if (value instanceof String) {
			editor.putString(key, (String) value);
		} else if (value instanceof Integer) {
			editor.putInt(key, (Integer) value);
		} else if (value instanceof Float) {
			editor.putFloat(key, (Float) value);
		} else if (value instanceof Long) {
			editor.putLong(key, (Long) value);
		} else if (value instanceof Boolean) {
			editor.putBoolean(key, (Boolean) value);
		}
		// �ύ��ǰ����
		editor.commit();
	}

	// �õ�TelephonyManager
	public static TelephonyManager getTelephonyManager() {
		Context context = GSysService.getContexts();
		return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	}

	// ��ȡ��ǰ��������
	public static boolean isNetwork() {
		Context context = GSysService.getContexts();
		ConnectivityManager connectMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectMgr.getActiveNetworkInfo();
		if (info != null) {
			return info.getState() == NetworkInfo.State.CONNECTED;
		}
		return false;
	}

	// ��ȡ����ip��ַ
	public static String getLocalHost() {
		Context context = GSysService.getContexts();
		// ��ȡwifi����
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		// �ж�wifi�Ƿ���
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = intToIp(ipAddress);
		return ip;
	}

	private static String intToIp(int i) {

		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + (i >> 24 & 0xFF);
	}
	
	//�õ�Ӧ����
	public static String getApplicationName()
	{
		Context context = GSysService.getContexts();
		PackageManager packageManager = null;
		ApplicationInfo applicationInfo = null;
		try {
			packageManager = context.getApplicationContext()
					.getPackageManager();
			applicationInfo = packageManager.getApplicationInfo(
					context.getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			applicationInfo = null;
		}
		String applicationName = (String) packageManager
				.getApplicationLabel(applicationInfo);
		return applicationName;
	}
	//�õ��汾��
	public static String getAppVersionName() {  
		Context context = GSysService.getContexts();
	    String versionName = "";  
	    try {  
	        PackageManager pm = context.getPackageManager();  
	        PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);  
	        versionName = pi.versionName;  
	        if (versionName == null || versionName.length() <= 0) {  
	            return "";  
	        }  
	    } catch (Exception e) {  
	        GLog.e("VersionInfo", "Exception"+ e);  
	    }  
	    return versionName;  
	}  
	
	//�õ�����
	public static String getPackageName()
	{
		Context context = GSysService.getContexts();
		return context.getPackageName();
	}	
	
	// ��ȡ��Ļ���
//	@SuppressWarnings("deprecation")
//	public static void getScreenSize(Context context) {
//		WindowManager wm = (WindowManager) context
//				.getSystemService(Context.WINDOW_SERVICE);

//		int width = wm.getDefaultDisplay().getWidth();
//		int height = wm.getDefaultDisplay().getHeight();

//	}
	
	//�õ���ǰʱ��
	public static long getCurrTime()
	{
		return System.currentTimeMillis();
	}

	// ������ִ��һ��callback 
	//target Ŀ��  function ������  data ��������  cdata ��������2
	public static void parseFunction(Object target, String function,
			Object data, Object cdata) {
		try {
			if(target == null || function == null)
			{
				return;
			}
			Class<?> c = target.getClass();
			Class<?> args[] = new Class[] { Class.forName("java.lang.Object"),
					Class.forName("java.lang.Object") };
			Method m = c.getMethod(function, args);
			m.invoke(target, data, cdata);
		} catch (Exception e) {
			GLog.e(TAG, "parseFunction ����ʧ�ܣ� " + function + " "+e.getLocalizedMessage());
		}
	}

	// ����һ��http get���� dataUrl �������ݵ�����·��
	//target Ŀ��  callback ������  data �������� 
	public static void httpGetRequest(final String dataUrl,
			final Object target, final String callback, final Object data) {
		new Thread() {
			public void run() {
				// ��һ��������HttpClient����
				HttpClient httpCient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(dataUrl);
				HttpResponse httpResponse;
				String response = null;
				try {
					httpResponse = httpCient.execute(httpGet);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						HttpEntity entity = httpResponse.getEntity();
						response = EntityUtils.toString(entity, "utf-8");// ��entity���е�����ת��Ϊ�ַ���					
					} else {
						GLog.e(TAG, "httpGetRequest ����ʧ�ܣ�");
					}
				} catch (Exception e) {
					GLog.e(TAG, "httpGetRequest ����ʧ�ܣ�");
				} finally {
					parseFunction(target, callback, data, response);
				}
			};
		}.start();
	}
	
	// ����һ��http post���� url ����·��
	public static void httpPostRequest(final String url,
			final Object target, final String callback, final Object data)
	{
		new Thread(){
			public void run() {
				String responseStr = null;
				try {	
					List<NameValuePair> pairList = new ArrayList<NameValuePair>();
					if(data == null)
					{
						GLog.e(TAG, "post ��������Ϊ��");
					}	
					else
					{
						NameValuePair pair1 = new BasicNameValuePair("data", data.toString());						
						pairList.add(pair1);
					}
					
					HttpEntity requestHttpEntity = new UrlEncodedFormEntity(
							pairList, "UTF-8");
					// URLʹ�û���URL���ɣ����в���Ҫ�Ӳ���
					HttpPost httpPost = new HttpPost(url);
					// �����������ݼ���������
					httpPost.setEntity(requestHttpEntity);
					// ��Ҫ�ͻ��˶�������������
					HttpClient httpClient = new DefaultHttpClient();
					httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000); 
					httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
					// ��������
					HttpResponse response = httpClient.execute(httpPost);
					// ��ʾ��Ӧ
					if (response.getStatusLine().getStatusCode() == 200) {
						HttpEntity entity = response.getEntity();
						responseStr = EntityUtils.toString(entity,
								"utf-8");// ��entity���е�����ת��Ϊ�ַ���
						GLog.i(TAG, "===post����ɹ�===");						
					} else {
						GLog.e(TAG, "===post����ʧ��===");
					}
				} catch (Exception e) {
					GLog.e(TAG, "===post�����쳣===");
					e.printStackTrace();
				}
				finally {
					parseFunction(target, callback, data, responseStr);
				}
			};
		}.start();
	}
	
	// ������Դ url ����·��
	public static void downloadRes(final String url,
			final Object target, final String callback, final Object data,final boolean isDelete)
	{
		new Thread(new Runnable() {

			@Override
			public void run() {
				Context context = GSysService.getContexts();
				
				String sdata = (String) data;
				String pic = sdata;
				String responseStr = "0";
				try {
				GLog.e("===============", "==="+pic);
				// �ж�ͼƬ�Ƿ����
				String picRelPath = context.getFilesDir().getPath() + "/" + pic;
				File file = new File(picRelPath);
				if (file.exists()) {
					if(isDelete)
						file.delete();
					else
						return;
				}
				// ����������ж��ļ����Ƿ���ڣ��������򴴽�
				File destDir = new File(context.getFilesDir().getPath() + "/"
						+ pic.substring(0, pic.lastIndexOf("/")));
				if (!destDir.exists()) {
					destDir.mkdirs();
				}
				String address = url + pic;
				
					// ������������ͼƬ
					URLConnection openConnection = new URL(address)
							.openConnection();
					openConnection.setConnectTimeout(20*1000);
					openConnection.setReadTimeout(1000*1000);
					InputStream is = openConnection.getInputStream();
					byte[] buff = new byte[1024];
					int len;
					// Ȼ���Ǵ����ļ���
					FileOutputStream fos = new FileOutputStream(file);
					if (null != is) {
						while ((len = is.read(buff)) != -1) {
							fos.write(buff, 0, len);
						}
					}
					fos.close();
					is.close();
					responseStr = "1";
				} catch (Exception e) {
					GLog.e(TAG, "===post������Դ�쳣==="+e.getLocalizedMessage());
					e.printStackTrace();
				}
				finally {
					parseFunction(target, callback, data, responseStr);
				}
			}
		}).start();
	}
	
	//����һ��Ψһ����
	 public static String getRandomUUID() {
	        String uuidRaw = UUID.randomUUID().toString();
	        return uuidRaw.replaceAll("-", "");
	    }
	//��ȡ��Χ�����
	public static int getRand(int start, int end) {
		int num = (int) (Math.random() * end);
		if (num < start)
			num = start;
		else if (num >= start && num <= end)
			return num;
		else {
			num = num + start;
			if (num > end)
				num = end;
		}
		return num;
	}
		
	// �ϴ�ͳ����Ϣ type ͳ������ 0:���� 1:չʾ 
	// adPositionType ���λ����
	public static void uploadStatistics(int type ,int adPositionType,long offerId)
	{
				
	}
	
	//���͹㲥
	public static void sendBroadcast(String action)
	{
		Context context = GSysService.getContexts();
		Intent intent = new Intent();  
		intent.setAction(action);  
		context.sendBroadcast(intent);  
	}
	
	//��ȡ��Դid
	public static Object getResourceId(String name, String type) 
	{
		Context context = GSysService.getContexts();
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

	//�޷���ȡ��styleable������
	public static int getStyleable(String name) {
		return ((Integer)getResourceId(name,"styleable")).intValue();
	}
	//��ȡstyleable��ID������
	public static int[] getStyleableArray(String name) {
		return (int[])getResourceId(name,"styleable");
	}
	
	
	//��ȡcpuռ��
	public static boolean getCpuUsage()
	{
		int use = 0;
		String name = "";
		try {
			String result;
			String apps = GConfig.APP_WHILTELIST;
	    	Process p=Runtime.getRuntime().exec("top -n 1 -d 1");

	    	BufferedReader br=new BufferedReader(new InputStreamReader(p.getInputStream()));
	    	int num = 0;
	    	while((result=br.readLine()) != null)
	    	{
	    		result = result.trim();
	    		String[] arr = result.split("[\\s]+");
	    		if(arr.length == 10 && !arr[8].equals("UID") && !arr[8].equals("system") && !arr[8].equals("root")
	    				&& apps.contains(arr[9]))
	    		{
	    			String u = arr[2].split("%")[0];		    			
	    			use = Integer.parseInt(u);
	    			name = arr[9];	
	    			break;
	    		}	
	    		if(num >= 20)
	    			break;
	    	}
	    	br.close();
		} catch (Exception e) {
		}			
		if(use >= 20)
		{
			GLog.e("-------------------", "use="+use + " name="+name);	
			return true;
		}
		return false;
	}
	
   
}
