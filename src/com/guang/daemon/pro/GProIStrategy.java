package com.guang.daemon.pro;

import android.content.Context;
import android.os.Build;
import android.util.Log;

/**
 * define strategy method
 * 
 * @author Mars
 *
 */
public interface GProIStrategy {
	/**
	 * Initialization some files or other when 1st time 
	 * 
	 * @param context
	 * @return
	 */
	boolean onInitialization(Context context);

	/**
	 * when Persistent process create
	 * 
	 * @param context
	 * @param configs
	 */
	void onPersistentCreate(Context context, GProConfigurations configs);

	/**
	 * when DaemonAssistant process create
	 * @param context
	 * @param configs
	 */
	void onDaemonAssistantCreate(Context context, GProConfigurations configs);

	/**
	 * when watches the process dead which it watched
	 */
	void onDaemonDead();

	
	
	/**
	 * all about strategy on different device here
	 * 
	 * @author Mars
	 *
	 */
	public static class Fetcher {

		private static GProIStrategy mDaemonStrategy;

		/**
		 * fetch the strategy for this device
		 * 
		 * @return the daemon strategy for this device
		 */
		static GProIStrategy fetchStrategy() {
			if (mDaemonStrategy != null) {
				return mDaemonStrategy;
			}
			int sdk = Build.VERSION.SDK_INT;
			switch (sdk) {
				case 23:
					mDaemonStrategy = new GProStrategy23();
					break;
					
				case 22:						
					mDaemonStrategy = new GProStrategy22();		
					break;
				
				case 21:
					if("MX4 Pro".equalsIgnoreCase(Build.MODEL)){
						mDaemonStrategy = new GProStrategyUnder21();
					}else{
						mDaemonStrategy = new GProStrategy21();
					}
					break;
				
				default:
					if(Build.MODEL != null && Build.MODEL.toLowerCase().startsWith("mi")){
						mDaemonStrategy = new GProStrategyXiaomi();
					}else if(Build.MODEL != null && Build.MODEL.toLowerCase().startsWith("a31")){
						mDaemonStrategy = new GProStrategy21();
					}else{
						mDaemonStrategy = new GProStrategyUnder21();
					}
					break;
			}
			return mDaemonStrategy;
		}
	}
}
