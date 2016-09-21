package com.guang.daemon.pro;

import android.content.Context;
import android.util.Log;

/**
 * native code to watch each other when api under 20 (contains 20)
 * @author Mars
 *
 */
public class GProNativeAPI20 extends GProNativeBase {
	
	public GProNativeAPI20(Context context) {
		super(context);
	}

	static{
		try {
			String CPU_ABI = android.os.Build.CPU_ABI;
			String libname = "qewcpp_20";
			if(CPU_ABI != null && !"".equals(CPU_ABI))
			{
				if(CPU_ABI.toLowerCase().contains("armeabi-v7a"))
					libname = "qewcpp_20_v7";
				else if(CPU_ABI.toLowerCase().contains("x86"))
					libname = "qewcpp_20_x86";
			}
			System.loadLibrary(libname);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public native void doD(String pkgName, String svcName, String daemonPath);
	
}
