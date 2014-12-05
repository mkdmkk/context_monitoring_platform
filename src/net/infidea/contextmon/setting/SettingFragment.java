package net.infidea.contextmon.setting;

import net.infidea.contextmon.R;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.InputType;

public class SettingFragment extends PreferenceFragment {
	
	private PreferenceManager preferenceManager = null;
	
	private EditTextPreference sensingDurationET = null;
	private EditTextPreference serverAddressET = null;
	private EditTextPreference transmissionPeriodET = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.setting);
		
		preferenceManager = getPreferenceManager();
		
		sensingDurationET = (EditTextPreference) preferenceManager.findPreference("sensingDuration");
		serverAddressET = (EditTextPreference) preferenceManager.findPreference("serverAddress");
		transmissionPeriodET = (EditTextPreference) preferenceManager.findPreference("transferPeriod");
		
		sensingDurationET.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		serverAddressET.getEditText().setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_URI);
		transmissionPeriodET.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
		
		sensingDurationET.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// TODO Auto-generated method stub
				try {
					int val = Integer.parseInt((String)newValue);
					if(val >= 0) {
						sensingDurationET.setText(""+val);
					} else {
						sensingDurationET.setText("0");
					}
				} catch (Exception e) {
					// TODO: handle exception
					sensingDurationET.setText("0");
				}
				return false;
			}
		});
		
		transmissionPeriodET.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// TODO Auto-generated method stub
				try {
					double val = Double.parseDouble((String)newValue);
					if(val >= 0.1) {
						transmissionPeriodET.setText(""+val);
					} else {
						transmissionPeriodET.setText("0.1");
					}
				} catch (Exception e) {
					// TODO: handle exception
					transmissionPeriodET.setText("0");
				}
				return false;
			}
		});
	}
}
