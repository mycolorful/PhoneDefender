package per.yrj.mobilesafe;

import per.yrj.view.SettingItemView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

public class SafeLeader2Activity extends BaseSafeLeader {
	private SettingItemView setBindingSim;
	private SharedPreferences mSharePre;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_safeleader2);
		mSharePre = getSharedPreferences("config", MODE_PRIVATE);
		setBindingSim = (SettingItemView) findViewById(R.id.isBindingSim);
		setBindingSim.setChecked(!TextUtils.isEmpty(mSharePre.getString("simNum", null)));
		super.onCreate(savedInstanceState);
	}
	
	public void onClick(View v) {
		if(setBindingSim.isChecked()){
			setBindingSim.setChecked(false);
		}else{
			TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			String simNum = tm.getSimSerialNumber();
			mSharePre.edit().putString("simNum", simNum).commit();
			setBindingSim.setChecked(true);
		}
	}
	
	@Override
	public void nextPage() {
		//如果没有绑定sim卡则不能进入到下一步
		if(mSharePre.getString("simNum", null) == null)
			return;
		startActivity(new Intent(this, SafeLeader3Activity.class));
		finish();
	}

	@Override
	public void latsPage() {
		startActivity(new Intent(this, SafeLeader1Activity.class));
		finish();
	}
	
}
