package per.yrj.model;

import per.yrj.mobilesafe.R;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {
	private final String ALARM = "#*alarm*#";
	private final String GPS = "#*gps*#";
	private final String WIPEDATA = "#*wipedata*#";
	private final String LOCKSCREEN = "#*lockscreen*#";
	private String mMsgText = "地球";
	private String safeNum;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Object[] objects = (Object[]) intent.getExtras().get("pdus");
		safeNum = context.getSharedPreferences("config", Context.MODE_PRIVATE).getString("safeNum", null).split(":")[1];
		for(Object obj: objects){
			//获取短信发件人号码、内容
			SmsMessage message = SmsMessage.createFromPdu((byte[])obj);
			String address = message.getOriginatingAddress();
			String body = message.getMessageBody();
			if(address.length() == 14)
				address = address.substring(3, 14);
			if(safeNum.equals(address)){
				if(body.equals(ALARM)){
					alarm(context);
					abortBroadcast();
				}
				if(body.equals(GPS)){
					sendGps(context);
					abortBroadcast();
				}
				if(body.equals(LOCKSCREEN)){
					lockScreen(context);
				}
			}
		}
	}
	
	/**
	 * 锁屏
	 * @param context
	 */
	private void lockScreen(Context context) {
		ComponentName deviceAdmin = new ComponentName(context, AdminReceiver.class);
		DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		if(dpm.isAdminActive(deviceAdmin)){
			dpm.lockNow();
			dpm.resetPassword("getmyphoneback", 0);
		}
		else{
			//激活设备管理器
				Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
				intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdmin);
				intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "使用设备管理器来防止手机被盗后造成数据泄露");
				context.startActivity(intent);
		}
	}
	
	private void sendGps(Context contex) {
		LocationManager locationManager = (LocationManager) contex.getSystemService(Context.LOCATION_SERVICE);
		MyLocationListener myLocationListener = new MyLocationListener();
		Criteria criteria = new Criteria();
		criteria.setCostAllowed(true);
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		locationManager.requestLocationUpdates(
				locationManager.getBestProvider(criteria , true), 60, 100, myLocationListener);
		SmsManager smsManager = SmsManager.getDefault();
		Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria , true));
		String longitude = "经度" + location.getLongitude();
		String latitude = "纬度" + location.getLatitude();
		String altitude = "海拔" + location.getAltitude();
		String accuracy = "精度" + location.getAccuracy();
		mMsgText = longitude + "\n" + latitude + "\n" + altitude + "\n" + accuracy;
		smsManager.sendTextMessage(safeNum, null, mMsgText, null, null);
		locationManager.removeUpdates(myLocationListener);
	}

	private void alarm(final Context context) {
				MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.young_for_you);
				mediaPlayer.setVolume(1f, 1f);
				mediaPlayer.setLooping(true);
				mediaPlayer.start();
	}
	
	class MyLocationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {

			String longitude = "经度" + location.getLongitude();
			String latitude = "纬度" + location.getLatitude();
			String altitude = "海拔" + location.getAltitude();
			String accuracy = "精度" + location.getAccuracy();
			mMsgText = longitude + "\n" + latitude + "\n" + altitude + "\n" + accuracy;
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}
		
	}

}
