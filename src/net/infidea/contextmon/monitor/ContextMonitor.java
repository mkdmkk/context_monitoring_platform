package net.infidea.contextmon.monitor;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import net.infidea.contextmon.communicator.ContextTransmitter;
import net.infidea.contextmon.constant.ContextType;
import net.infidea.contextmon.experiment.BatteryConsExperiment;
import net.infidea.contextmon.setting.SettingActivity;
import net.infidea.contextmon.monitor.IContextMonitor;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.SparseArray;
import android.widget.Toast;

/**
 * ContextMonitor;
 * A service for measuring sensor values
 * It includes an interface for providing gathered measurements to other applications.
 * 
 * @author Moon Kwon Kim
 * Revision History:
 * v0.1, 131207, Moon Kwon Kim 
 */
public class ContextMonitor extends Service implements SensorEventListener, LocationListener {

	public static final String BROADCAST_URI = "kr.co.smartylab.cma.context"; 
	private final int INFERENCE_FREQUENCY = 100;

	// Context transmitter
	private ContextTransmitter contextTransmitter;

	// Sensor manager
	private SensorManager sensorManager;
	private int sensingFrequency;
	private SparseArray<SensorEvent> currentContext;
	private Location currentLocation;

	// Location manager
	private LocationManager locationManager;

	// Experiment
	private BatteryConsExperiment experiment;

	// Context informer
	private InferenceEngine inferenceEngine;
	private Timer inferenceTimer;
	private TimerTask inferenceTimerTask = new TimerTask() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String label = inferenceEngine.classify(basket.toLinearArray(ContextType.LINEAR_ACCELERATION, InferenceEngine.ATTR_SIZE));
			broadcast(label);
		}
	};

	// Context provider
	private ContextBasket basket;
	private final IContextMonitor.Stub contextMonitor = new IContextMonitor.Stub() {

		@Override
		public void start() throws RemoteException {
			// TODO Auto-generated method stub

			/*
			 * Transmission configuration
			 * Prepare a context pack for transmitting
			 * Schedule the transmission timer 
			 */
			if(SettingActivity.isTransmissionAvailable()) {
				// Starting transmission timer thread
				// ...
			}

			/*
			 * Experiment
			 * Initialize a data set for experiment
			 */
			experiment = new BatteryConsExperiment(ContextMonitor.this);
			experiment.start();

			/*
			 * Sensor manipulation
			 * Add available contexts to the context list
			 * Generate a context view for each available context
			 */
			for(Sensor sensor:sensorManager.getSensorList(Sensor.TYPE_ALL)) {
				if(SettingActivity.isContextAvailable(sensor.getType())) {
					// Deciding frequency
					// ...
					sensorManager.registerListener(ContextMonitor.this, sensor, sensingFrequency);
				}
			}

			/*
			 * Location manipulation
			 * Add the location context to the context list if it is available
			 * Generate a context view for the location context
			 */
			if(SettingActivity.isContextAvailable(ContextType.LOCATION)) {
				Criteria criteria = new Criteria();
				criteria.setAccuracy(Criteria.ACCURACY_FINE);
				criteria.setAltitudeRequired(false);
				criteria.setBearingRequired(false);
				criteria.setCostAllowed(true);
				criteria.setPowerRequirement(Criteria.POWER_LOW);
				String bestProvider = locationManager.getBestProvider(criteria, true);
				if(!bestProvider.equals("")) {
					locationManager.requestLocationUpdates(bestProvider, 1000L, 1F, ContextMonitor.this);
				}
			}
		}

		@Override
		public void stop() throws RemoteException {
			// TODO Auto-generated method stub
			sensorManager.unregisterListener(ContextMonitor.this);
			locationManager.removeUpdates(ContextMonitor.this);
			if(contextTransmitter != null) {
				contextTransmitter.stop();
				contextTransmitter = null;
			}
			experiment.finish();
			if(basket != null) {
				basket = null;
			}
			if(inferenceEngine != null) {
				inferenceTimer.cancel();
				inferenceTimer = null;
				inferenceEngine = null;
			}
			registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		}

		@Override
		public ContentValues[] getContexts(int type, int maxNum) throws RemoteException {
			// TODO Auto-generated method stub
			return basket.toContentValues(type, maxNum);
		}

		@Override
		public ContentValues[] getCurrentContexts(int type) throws RemoteException {
			// TODO Auto-generated method stub
			ArrayList<ContentValues> result = new ArrayList<ContentValues>();
			switch(type) {
			case ContextType.ALL:
				if(currentLocation != null) {
					result.add(ContextBasket.toMeasurement(currentLocation));
				}
				int key;
				int size = currentContext.size();
				if(size < 0) {
					return null;
				} else {
					for(int i=0; i<currentContext.size(); i++) {
						key = currentContext.keyAt(i);
						result.add(ContextBasket.toMeasurement(currentContext.get(key)));
					}
				}
				break;
			case ContextType.LOCATION:
				if(currentLocation != null) {
					result.add(ContextBasket.toMeasurement(currentLocation));
				} else {
					return null;
				}
				break;
			default:
				if(currentContext.indexOfKey(type) >= 0) {
					result.add(ContextBasket.toMeasurement(currentContext.get(type)));
				} else {
					return null;
				}
				break;
			}
			return result.toArray(new ContentValues[0]);
		}
	};

	/**
	 * Methods for Life Cycle
	 */
	public int onStartCommand(Intent intent, int flags, int startId) {
		int approach = intent.getIntExtra("approach", 2);
		switch(intent.getIntExtra("sensingFrequency", 1)) {
		case 1:
			sensingFrequency = SensorManager.SENSOR_DELAY_NORMAL;
			break;
		case 2:
			sensingFrequency = SensorManager.SENSOR_DELAY_UI;
			break;
		case 3:
			sensingFrequency = SensorManager.SENSOR_DELAY_GAME;
			break;
		case 4:
			sensingFrequency = SensorManager.SENSOR_DELAY_FASTEST;
			break;
		}
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		switch(approach) {
		case 2:
			currentContext = new SparseArray<SensorEvent>();
			break;
		case 3:
			basket = new ContextBasket(null);
			break;
		case 4:
			basket = new ContextBasket(getContentResolver());
			break;
		case 5:
			basket = new ContextBasket(null);
			inferenceEngine = new InferenceEngine(this);
			inferenceTimer = new Timer();
			inferenceTimer.schedule(inferenceTimerTask, INFERENCE_FREQUENCY, INFERENCE_FREQUENCY);
			break;
		}
		if(intent.getBooleanExtra("adaptive", false)) {
			
		}
		return startId;
	};

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if(contextMonitor != null) {
			try {
				contextMonitor.stop();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(basket != null) {
			basket.close();
			basket = null;
		}
		if(inferenceEngine != null) {
			inferenceEngine.close();
			inferenceEngine = null;
		}
		if(currentContext != null) {
			currentContext = null;
		}
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return contextMonitor;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}

	/**
	 * Methods for SensorEventListener
	 * 
	 * A callback method for acquiring contexts
	 * If the feature, BASKET, is enabled, an acquired context is stored to the basket.  
	 */
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		int type = event.sensor.getType();
		if(basket != null) {
			basket.putMeasurement(event);
		} else {
			currentContext.put(type, event);
		}
		experiment.increaseCount(type);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	/**
	 * Methods for LocationListener
	 */
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Location: "+location.getLatitude()+", "+location.getLongitude(), Toast.LENGTH_SHORT).show(); 
		if(basket != null) {
			basket.putMeasurement(location);
		} else {
			currentLocation = location;
		}
		experiment.increaseCount(19);
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Private methods
	 */
	private void broadcast(String situation) {
		Intent intent = new Intent(BROADCAST_URI);
		intent.putExtra("situation", situation);
		sendBroadcast(intent);
	}
}
