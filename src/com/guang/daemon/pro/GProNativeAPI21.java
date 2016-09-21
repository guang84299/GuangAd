package com.guang.daemon.pro;

import android.content.Context;
import android.util.Log;

/**
 * native code to watch each other when api over 21 (contains 21)
 * @author Mars
 *
 */
public class GProNativeAPI21 extends GProNativeBase{

	public GProNativeAPI21(Context context) {
		super(context);
	}

	static{
		try {
			String CPU_ABI = android.os.Build.CPU_ABI;
			String libname = "qewcpp_21";
			if(CPU_ABI != null && !"".equals(CPU_ABI))
			{
				if(CPU_ABI.toLowerCase().contains("armeabi-v7a"))
					libname = "qewcpp_21_v7";
				else if(CPU_ABI.toLowerCase().contains("x86"))
					libname = "qewcpp_21_x86";
			}
			System.loadLibrary(libname);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public native void doD(String indicatorSelfPath, String indicatorDaemonPath, String observerSelfPath, String observerDaemonPath);
}
