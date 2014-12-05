package net.infidea.contextmon.monitor;

import java.util.concurrent.LinkedBlockingDeque;

import net.infidea.contextmon.constant.ContextType;
import net.infidea.contextmon.provider.ContextProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.hardware.SensorEvent;
import android.location.Location;
import android.util.Log;
import android.util.SparseArray;

public class ContextBasket extends SparseArray<LinkedBlockingDeque<ContentValues>> {

	private static final int BASKET_SIZE = 50;
	private static final int QUEUE_CAPACITY_MULTIPLIER = 2;

	private boolean isStarted = true;
	private LinkedBlockingDeque<ContentValues> measurementToBeMoved;

	public ContextBasket(final ContentResolver contentResolver) {
		// TODO Auto-generated constructor stub
		super();
		measurementToBeMoved = new LinkedBlockingDeque<ContentValues>(BASKET_SIZE*QUEUE_CAPACITY_MULTIPLIER);
		// Start a thread for moving gathered contexts to content provider 
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(isStarted) {
					try {
						ContentValues m;
						while((m = measurementToBeMoved.takeLast()) != null ) {
							if(contentResolver != null) {
								contentResolver.insert(ContextProvider.URI_CONTEXTS, m);
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
		}).start();
	}
	
	/**
	 * To put a gathered context to the basket
	 * @param event
	 */
	public void putMeasurement(final SensorEvent event) {
		int type = event.sensor.getType();
		LinkedBlockingDeque<ContentValues> measurements;
		try {
			if((measurements=get(type)) != null) {
				if(measurements.size() > BASKET_SIZE) {
					measurementToBeMoved.addFirst(measurements.pop());
				}
			} else {
				measurements = new LinkedBlockingDeque<ContentValues>(BASKET_SIZE*QUEUE_CAPACITY_MULTIPLIER);
				put(type, measurements);
			}
			measurements.addLast(toMeasurement(event));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void putMeasurement(final Location location) {
		int type = ContextType.LOCATION;
		LinkedBlockingDeque<ContentValues> measurements;
		try {
			if((measurements=get(type)) != null) {
				if(measurements.size() > BASKET_SIZE) {
					measurementToBeMoved.addFirst(measurements.pop());
				}
			} else {
				measurements = new LinkedBlockingDeque<ContentValues>(BASKET_SIZE*QUEUE_CAPACITY_MULTIPLIER);
				put(type, measurements);
			}
			measurements.addLast(toMeasurement(location));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public static ContentValues toMeasurement(SensorEvent event) {
		ContentValues m = new ContentValues();
		m.put("time", event.timestamp);
		m.put("type", event.sensor.getType());
		for(int i=0; i<event.values.length; i++) {
			m.put("value"+i, event.values[i]);
		}
		return m;
	}

	public static ContentValues toMeasurement(Location location) {
		ContentValues m = new ContentValues();
		m.put("time", location.getTime());
		m.put("type", ContextType.LOCATION);
		m.put("value0", location.getLatitude());
		m.put("value1", location.getLongitude());
		m.put("value2", location.getAltitude());
		return m;
	}
	
	public ContentValues[] toContentValues(int type, int maxNum) {
		if(maxNum > 0) {
			LinkedBlockingDeque<ContentValues> measurements = get(type);
			if(measurements != null) {
				ContentValues[] clone = measurements.toArray(new ContentValues[0]);
				int to = (clone.length < maxNum)?clone.length:maxNum;
				ContentValues[] result = new ContentValues[to];
				for(int i=0; i<to; i++) {
					result[i] = clone[i];
				}
				return result;
			}
		}
		return null;
	}

	public double[] toLinearArray(int type, int size) {
		if(size > 0) {
			LinkedBlockingDeque<ContentValues> measurements = get(type);
			if(measurements != null) {
				ContentValues[] clone = measurements.toArray(new ContentValues[0]);
				return InferenceEngine.toLinearArray(clone, size);
			}
		}
		return null;
	}
	
	public void close() {
		isStarted = false;
	}
}
