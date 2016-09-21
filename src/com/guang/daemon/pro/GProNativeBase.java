package com.guang.daemon.pro;

import android.content.Context;

/**
 * native base class
 * 
 * @author Mars
 *
 */
public class GProNativeBase {
	/**
	 * used for native
	 */
	protected 	Context			mContext;
	
    public GProNativeBase(Context context){
        this.mContext = context;
    }

    /**
     * native call back
     */
	protected void onDaemonDead(){
		GProIStrategy.Fetcher.fetchStrategy().onDaemonDead();
    }
    
}
