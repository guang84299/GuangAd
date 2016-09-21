package com.guang.daemon;

import android.app.Application;
import android.content.Context;

import com.guang.daemon.pro.GProApplication;
import com.guang.daemon.pro.GProClient;
import com.guang.daemon.pro.GProConfigurations;

public class GApplication extends Application {

	private GProClient mDaemonClient;

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		mDaemonClient = new GProClient(createDaemonConfigurations(base));
		mDaemonClient.onAttachBaseContext(base);		
	}

	private GProConfigurations createDaemonConfigurations(Context base) {
		GProConfigurations.DaemonConfiguration configuration1 = new GProConfigurations.DaemonConfiguration(
				base.getPackageName()+":pro1",
				GService.class.getCanonicalName(),
				GReceiver.class.getCanonicalName());
		GProConfigurations.DaemonConfiguration configuration2 = new GProConfigurations.DaemonConfiguration(
				base.getPackageName()+":pro2",
				GService2.class.getCanonicalName(),
				GReceiver2.class.getCanonicalName());
		GProConfigurations.DaemonListener listener = new MyDaemonListener();
		return new GProConfigurations(configuration1, configuration2,
				listener);
	}

	class MyDaemonListener implements GProConfigurations.DaemonListener {
		@Override
		public void onPersistentStart(Context context) {
		}

		@Override
		public void onDaemonAssistantStart(Context context) {
		}

		@Override
		public void onWatchDaemonDaed() {
		}
	}
}