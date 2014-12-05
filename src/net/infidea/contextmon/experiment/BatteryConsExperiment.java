package net.infidea.contextmon.experiment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class BatteryConsExperiment {
	private final int CONTEXT_TYPE_SIZE = 20;
	
	private Context context;
	private boolean isBeforeStateChecked;
	private int[] cntByType;
	private long timeBegin;
	private int beforeBatteryLevel;
	
	private BroadcastReceiver batteryStatusReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			float batteryPercent = (batteryLevel * 100) / (float)intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
			// If battery level is 0, this is the first invocation.
			// Otherwise, it needs to compare the level to the previous measured battery level. 
			if(beforeBatteryLevel > 0 && beforeBatteryLevel > batteryLevel) {
				beforeBatteryLevel = batteryLevel;
				Intent broadcastIntent = new Intent("kr.co.smartylab.cma.experiment");
				if(isBeforeStateChecked) {
					long timeCurr = System.currentTimeMillis();
					long elapsedTime = timeCurr-timeBegin;
					broadcastIntent.putExtra("eventType", 2);
					broadcastIntent.putExtra("monitoringTime", elapsedTime);
					broadcastIntent.putExtra("batteryAfter", batteryPercent);
					broadcastIntent.putExtra("countByType", cntByType);

					// Clear the count for avoiding memory exceptions
					cntByType = new int[CONTEXT_TYPE_SIZE];
					timeBegin = timeCurr;
				} else {
					timeBegin = System.currentTimeMillis();
					broadcastIntent.putExtra("eventType", 1);
					broadcastIntent.putExtra("batteryBefore", batteryPercent);
					
					// First battery change is checked.
					cntByType = new int[CONTEXT_TYPE_SIZE];
					isBeforeStateChecked = true;
				}
				context.sendBroadcast(broadcastIntent);
			} else {
				beforeBatteryLevel = batteryLevel;
			}
		}
	};

	public BatteryConsExperiment(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	
	public void start() {
		isBeforeStateChecked = false;
		beforeBatteryLevel = 0;
		context.registerReceiver(batteryStatusReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	}

	public void finish() {
		try {
			context.unregisterReceiver(batteryStatusReceiver);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public int increaseCount(int type) {
		if(cntByType == null) return -1;
		return ++cntByType[type];
	}

}