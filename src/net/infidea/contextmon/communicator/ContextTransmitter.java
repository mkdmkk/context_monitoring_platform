package net.infidea.contextmon.communicator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import net.infidea.contextmon.monitor.ContextMonitor;
import net.infidea.contextmon.setting.SettingActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class ContextTransmitter {
	private Context context = null;
	private ContextMonitor contextMonitor = null;
	private String url = "";
	private HttpClient client = null;
	private HttpResponse response = null;
	private StringEntity entity = null;
	private HttpPost post = null;
	private Timer transmissionTimer = null;

	public ContextTransmitter(Context context, ContextMonitor contextMonitor) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.contextMonitor = contextMonitor;
	}
	
	public void sendToServer() {
		try {
			client = new DefaultHttpClient();
			post = new HttpPost(url);
			entity = new StringEntity(""); // To be changed
			entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			post.setEntity(entity);
			response = client.execute(post);
			if(response != null) response.getEntity().getContent();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void start() {
		if((url = SettingActivity.getServerAddress()).equals("")) {
			Toast.makeText(context, "No server address.", Toast.LENGTH_LONG).show();
		} else {
			if(!url.startsWith("http://")) {
				url = "http://"+url;
			}
			transmissionTimer = new Timer();
			long transferPeriod = (long) (SettingActivity.getTransferPeriod()*1000);
			transmissionTimer.schedule(new TimerTask() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					sendToServer();
				}
			}, transferPeriod, transferPeriod);
		}
	}
	
	public void stop() {
		if(transmissionTimer != null) {
			transmissionTimer.cancel();
		}
	}
}
