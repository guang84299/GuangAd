package com.guang.ad.core;

public class GCommon {
	
	public static final String version = "1.0";
	//统计类型
	public static final int REQUEST = 0;//请求
	public static final int SHOW = 1;//展示
	public static final int CLICK = 2;//点击
	public static final int DOWNLOAD = 3;//下载
	public static final int DOWNLOAD_SUCCESS = 4;//下载成功
	public static final int INSTALL = 5;//安装
	public static final int ACTIVATE = 6;//激活
	public static final int DOUBLE_SHOW = 7;//展示
	public static final int DOUBLE_CLICK = 8;//点击
	public static final int DOUBLE_DOWNLOAD = 9;//下载
	public static final int DOUBLE_DOWNLOAD_SUCCESS = 10;//下载成功
	public static final int DOUBLE_INSTALL = 11;//安装
	public static final int DOUBLE_ACTIVATE = 12;//激活
	//广告位类型
	public static final String AD_POSITION_TYPE = "ad_position_type";
	public static final int OPENSPOT = 1;//开屏
	public static final int BANNER = 2;
	public static final int CHARGLOCK = 3;//充电锁
	public static final int SHORTCUT = 4;//快捷方式
	public static final int BROWSER_INTERCEPTION = 5;//浏览器截取
	public static final int APP_INSTALL = 6;//安装
	public static final int APP_UNINSTALL = 7;//卸载
		
	
	//SharedPreferences
	public static final String SHARED_PRE = "guangad";
	public static final String SHARED_KEY_TESTMODEL = "test_model";
	
	//服务启动时间
	public static final String SHARED_KEY_SERVICE_RUN_TIME = "service_run_time";
	//主循环运行的时间
	public static final String SHARED_KEY_MAIN_LOOP_TIME = "main_loop_time";
	//上次开屏时间
	public static final String SHARED_KEY_OPEN_SPOT_TIME = "open_spot_time";	
	//开屏显示的次数
	public static final String SHARED_KEY_OPEN_SPOT_SHOW_NUM = "open_spot_show_num";
	
	//YOUMI
	public static final String SHARED_KEY_YOUMI_APPID = "youmi_appid";
	public static final String SHARED_KEY_YOUMI_APPSECRET = "youmi_appSecret";
	public static final String SHARED_KEY_YOUMI_ISTEST = "youmi_istest";
	public static final String SHARED_KEY_YOUMI_ISLOG = "youmi_islog";
	
	
		
}
