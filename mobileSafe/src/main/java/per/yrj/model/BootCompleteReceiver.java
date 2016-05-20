package per.yrj.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

public class BootCompleteReceiver extends BroadcastReceiver {
	SharedPreferences mShaPre;

	@Override
	public void onReceive(Context context, Intent intent) {
		mShaPre = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		String simNum = mShaPre.getString("simNum", null);
		String currentSimNum = ((TelephonyManager)(context.getSystemService(Context.TELEPHONY_SERVICE))).getSimSerialNumber();
		if(!TextUtils.isEmpty(simNum)){
			if(!simNum.equals(currentSimNum)){
				String num = mShaPre.getString("safeNum", null);
				System.out.println(num);
				num = num.split(":")[1];
				System.out.println(num);
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(num, null, "注意，手机SIM卡已更换", null, null);
			}
		}

	}

}
