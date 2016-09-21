package com.guang.daemon.pro;


import android.app.Application;
import android.content.Context;

/**
 * make your Application extends it 
 * @author Mars
 *
 */
public abstract class GProApplication extends Application{
	/**
	 * Daemon SDK needs the Daemon Configurations contains two process informations</br>
	 * see {@link GProConfigurations} and {@link GProConfigurations.DaemonConfiguration}
	 * 
	 * @return GProConfigurations 
	 */
	protected abstract GProConfigurations getDaemonConfigurations();
	
	
	
	private GProClient mDaemonClient;
	public GProApplication(){
		mDaemonClient = new GProClient(getDaemonConfigurations());
	}
	
	
	/**
	 * order to prevent performing super.attachBaseContext() by child class</br>
	 * if do it, it will cause the IllegalStateException if a base context has already been set.
	 */
	private boolean mHasAttachBaseContext = false;
	
	@Override
	public final void attachBaseContext(Context base) {
		if(mHasAttachBaseContext){
			return ;
		}
		mHasAttachBaseContext = true;
		super.attachBaseContext(base);
		mDaemonClient.onAttachBaseContext(base);
		attachBaseContextByDaemon(base);
	}
	
	/**
	 * instead of {{@link #attachBaseContext(Context)}, you can override this.</br>
	 * @param base
	 */
	public void attachBaseContextByDaemon(Context base){
		
	}
	
	
}
