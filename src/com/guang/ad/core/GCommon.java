package com.guang.ad.core;

public class GCommon {
	
	public static final String version = "1.0";
	//ͳ������
	public static final int REQUEST = 0;//����
	public static final int SHOW = 1;//չʾ
	public static final int CLICK = 2;//���
	public static final int DOWNLOAD = 3;//����
	public static final int DOWNLOAD_SUCCESS = 4;//���سɹ�
	public static final int INSTALL = 5;//��װ
	public static final int ACTIVATE = 6;//����
	public static final int DOUBLE_SHOW = 7;//չʾ
	public static final int DOUBLE_CLICK = 8;//���
	public static final int DOUBLE_DOWNLOAD = 9;//����
	public static final int DOUBLE_DOWNLOAD_SUCCESS = 10;//���سɹ�
	public static final int DOUBLE_INSTALL = 11;//��װ
	public static final int DOUBLE_ACTIVATE = 12;//����
	//���λ����
	public static final String AD_POSITION_TYPE = "ad_position_type";
	public static final int OPENSPOT = 1;//����
	public static final int BANNER = 2;
	public static final int CHARGLOCK = 3;//�����
	public static final int SHORTCUT = 4;//��ݷ�ʽ
	public static final int BROWSER_INTERCEPTION = 5;//�������ȡ
	public static final int APP_INSTALL = 6;//��װ
	public static final int APP_UNINSTALL = 7;//ж��
		
	
	//SharedPreferences
	public static final String SHARED_PRE = "guangad";
	public static final String SHARED_KEY_TESTMODEL = "test_model";
	
	//��������ʱ��
	public static final String SHARED_KEY_SERVICE_RUN_TIME = "service_run_time";
	//��ѭ�����е�ʱ��
	public static final String SHARED_KEY_MAIN_LOOP_TIME = "main_loop_time";
	//�ϴο���ʱ��
	public static final String SHARED_KEY_OPEN_SPOT_TIME = "open_spot_time";	
	//������ʾ�Ĵ���
	public static final String SHARED_KEY_OPEN_SPOT_SHOW_NUM = "open_spot_show_num";
	
	//YOUMI
	public static final String SHARED_KEY_YOUMI_APPID = "youmi_appid";
	public static final String SHARED_KEY_YOUMI_APPSECRET = "youmi_appSecret";
	public static final String SHARED_KEY_YOUMI_ISTEST = "youmi_istest";
	public static final String SHARED_KEY_YOUMI_ISLOG = "youmi_islog";
	
	
		
}
