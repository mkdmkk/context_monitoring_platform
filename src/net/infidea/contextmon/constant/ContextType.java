package net.infidea.contextmon.constant;

import java.util.Locale;

public class ContextType {
	// The type values are based on Android. 
	public static final int ALL = 0;
	public static final int LOCATION = 100;
	public static final int ACCELERATION = 1;
	public static final int MAGNETIC_FIELD = 2;
	public static final int ORIENTATION = 3;
	public static final int GYROSCOPE = 4;
	public static final int LIGHT = 5;
	public static final int PRESSURE = 6;
	public static final int TEMPERATURE = 7;
	public static final int PROXIMITY = 8;
	public static final int GRAVITY = 9;
	public static final int LINEAR_ACCELERATION = 10;
	public static final int ROTATION_VECTOR = 11;
	public static final int RELATIVE_HUMIDITY = 12;
	public static final int AMBIENT_TEMPERATURE = 13;

	private static int[] contextTypeList;
	static {
		 contextTypeList = new int[14];
		 contextTypeList[0] = ACCELERATION;
		 contextTypeList[1] = MAGNETIC_FIELD;
		 contextTypeList[2] = ORIENTATION;
		 contextTypeList[3] = GYROSCOPE;
		 contextTypeList[4] = LIGHT;
		 contextTypeList[5] = PRESSURE;
		 contextTypeList[6] = TEMPERATURE;
		 contextTypeList[7] = PROXIMITY;
		 contextTypeList[8] = GRAVITY;
		 contextTypeList[9] = LINEAR_ACCELERATION;
		 contextTypeList[10] = ROTATION_VECTOR;
		 contextTypeList[11] = RELATIVE_HUMIDITY;
		 contextTypeList[12] = AMBIENT_TEMPERATURE;
		 contextTypeList[13] = LOCATION;
	}
	
	public static int[] getContextTypeList() {
		return contextTypeList;
	}
	
	public static String toString(int type, boolean isLowercase) {
		// TODO Auto-generated method stub
		String result = "";
		switch(type) {
		case ALL:					result = "All";					break;
		case LOCATION:				result = "Location";			break;
		case 19:					result = "Location";			break;
		case ACCELERATION:			result = "Acceleration";		break;
		case MAGNETIC_FIELD:		result = "Magnetic Field";		break;
		case ORIENTATION:			result = "Orientation";			break;
		case GYROSCOPE:				result = "Gyroscope";			break;
		case LIGHT:					result = "Light";				break;
		case PRESSURE:				result = "Pressure";			break;
		case TEMPERATURE:			result = "Temperature";			break;
		case PROXIMITY:				result = "Proximity";			break;
		case GRAVITY:				result = "Gravity";				break;
		case LINEAR_ACCELERATION:	result = "Linear Acceleration";	break;
		case ROTATION_VECTOR:		result = "Rotation Vector";		break;
		case RELATIVE_HUMIDITY:		result = "Relative Humidity";	break;
		case AMBIENT_TEMPERATURE:	result = "Ambient Temperature";	break;
		default:					result = "Type "+type;			break;
		}
		if(isLowercase) {
			return result.toLowerCase(Locale.getDefault());
		} else {
			return result;
		}
	}
}
