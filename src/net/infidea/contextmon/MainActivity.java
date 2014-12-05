package net.infidea.contextmon;

import java.util.Timer;
import java.util.TimerTask;

import net.infidea.contextmon.constant.ContextType;
import net.infidea.contextmon.monitor.ContextMonitor;
import net.infidea.contextmon.setting.SettingActivity;
import net.infidea.contextmon.R;
import net.infidea.contextmon.monitor.IContextMonitor;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private final int SETTING_REQCODE = 888;

	private Intent serviceIntent;
	
	private boolean isRunning = false;
	private boolean hasResult = false;

	private ImageButton startBt;

	private TextView statusTv;
	private TextView elapsedTimeTv;
	private TextView logTv;

	// Notification-related attributes
	private final int NOTIFICATION_ID = 26;
	private NotificationManager notificationManager;

	// Timer for elapsed time counting
	private int sensingDuration;
	private Timer elapsedTimer = new Timer();
	private int elapsedSeconds;

	private IContextMonitor contextMonitor;

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			switch(intent.getIntExtra("eventType", 0)) {
			case 1:
				logTv.append(String.format("Battery Status before Monitoring: %.2f%%\n\n", 
						intent.getFloatExtra("batteryBefore", -1)));
				break;
			case 2:
				hasResult = true;
				logTv.append(String.format("Battery Status after Monitoring: %.2f%%\nElapsed Time: %d ms\n",
						intent.getFloatExtra("batteryAfter", -1), 
						intent.getLongExtra("monitoringTime", -1)));
				int[] countByType = intent.getIntArrayExtra("countByType");
				int type = 0;
				for(int cnt:countByType) {
					if(cnt > 0) {
						logTv.append(ContextType.toString(type, false)+": "+cnt+" times\n");
					}
					type++;
				}
				logTv.append("\n");
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		((View) findViewById(R.id.settingBt)).setOnClickListener(this);
		((View) findViewById(R.id.aboutBt)).setOnClickListener(this);

		startBt = (ImageButton) findViewById(R.id.startBt);
		startBt.setOnClickListener(this);

		statusTv = (TextView) findViewById(R.id.statusTv);
		elapsedTimeTv = (TextView) findViewById(R.id.elapsedTimeTv);
		logTv = (TextView) findViewById(R.id.logTv);

		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		SettingActivity.init(this);
		setSettingInformation();

		registerReceiver(broadcastReceiver, new IntentFilter("kr.co.smartylab.cma.experiment"));
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		notificationManager.cancel(NOTIFICATION_ID);
		stopService(serviceIntent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		switch(requestCode) {
		case SETTING_REQCODE:
			setSettingInformation();
			break;
		default:
			break;
		}
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.startBt:
			// Toggle running status
			isRunning = isRunning?false:true;
			if(isRunning) {
				((ImageButton) v).setImageResource(R.drawable.ic_av_stop);
				// Start monitoring...
				hasResult = false;
				logTv.setText("Waiting for first battery change...\n\n");
				turnOnTimer();
				
				// Start service and monitoring
				startService();

				// Set to monitoring state
				statusTv.setText(getResources().getString(R.string.val_monitoring_state));
				statusTv.setTextColor(getResources().getColor(R.color.yellowgreen));
			} else {
				((ImageButton) v).setImageResource(R.drawable.ic_av_play);

				// Stop monitoring...
				turnOffTimer();
				if(!hasResult) {
					logTv.setText("");
				}
				try {
					contextMonitor.stop();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// Stop service
				stopService();

				// Set to idle state
				statusTv.setText(getResources().getString(R.string.val_idle_state));
				statusTv.setTextColor(getResources().getColor(R.color.white));
			}
			break;
		case R.id.settingBt:
			if(isRunning) {
				Toast.makeText(this, "Stop monitoring first.", Toast.LENGTH_LONG).show();
			} else {
				startActivityForResult(new Intent(this, SettingActivity.class), SETTING_REQCODE);
			}
			break;
		case R.id.aboutBt:
			startActivity(new Intent(this, AboutActivity.class));
			break;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);

		startActivity(intent);
	}

	private boolean startService() {
		serviceIntent = new Intent(this, ContextMonitor.class);
		serviceIntent.putExtra("approach", SettingActivity.getApproach());
		serviceIntent.putExtra("sensingFrequency", SettingActivity.getSensingFrequency());
		serviceIntent.putExtra("adaptive", SettingActivity.isAdaptiveAvailable());
		
		if(startService(serviceIntent) != null) {
			if(bindService(new Intent("kr.co.smartylab.cma.ContextMonitor"), new ServiceConnection() {

				@Override
				public void onServiceDisconnected(ComponentName name) {
					// TODO Auto-generated method stub
					contextMonitor = null;
				}

				@Override
				public void onServiceConnected(ComponentName name, IBinder service) {
					// TODO Auto-generated method stub
					contextMonitor = IContextMonitor.Stub.asInterface(service);
					try {
						contextMonitor.start();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}, BIND_IMPORTANT)) {
				// Success
				// Something...
				return true;
			} else {
				Log.d(getClass().getName(), "Service Binding Failed");
			}
		} else {
			// Fail to start service
		}
		return false;
	}
	
	private boolean stopService() {
		contextMonitor = null;
		return stopService(serviceIntent);
	}
	
	private void setSettingInformation() {
		// To update views after changing preferences
	}
	
	private void turnOnTimer() {
		elapsedTimer = new Timer();
		elapsedSeconds = 0;
		elapsedTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {

					public void run() {
						// TODO Auto-generated method stub
						elapsedTimeTv.setText((elapsedSeconds++)+" (s)");

						// When the elapsed time is over the sensing duration, stop monitoring.
						if(sensingDuration > 0 && sensingDuration < elapsedSeconds && isRunning == true) {
							startBt.performClick();
						}
					}
				});
			}
		}, 0, 1000);
	}

	private void turnOffTimer() {
		elapsedTimer.cancel();
		elapsedTimeTv.setText("");
	}
}
