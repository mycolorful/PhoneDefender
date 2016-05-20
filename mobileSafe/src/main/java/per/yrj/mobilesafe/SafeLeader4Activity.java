package per.yrj.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class SafeLeader4Activity extends BaseSafeLeader	 {
	private CheckBox cbIsSafeOn;
	private TextView tvSafeOn;
	private SharedPreferences mSharePre;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_safeleader4);
		cbIsSafeOn = (CheckBox) findViewById(R.id.cb_is_safe_on);
		tvSafeOn = (TextView) findViewById(R.id.tv_safe_on);
		mSharePre = getSharedPreferences("config", MODE_PRIVATE);
		cbIsSafeOn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					tvSafeOn.setText("已成功开启防盗保护");
					mSharePre.edit().putBoolean("openProtect", true).commit();
				}else{
					tvSafeOn.setText("你没有开启防盗保护");
					mSharePre.edit().putBoolean("openProtect", false).commit();
				}
			}
		});
		super.onCreate(savedInstanceState);
	}

	@Override
	public void nextPage() {
		startActivity(new Intent(this, SafeActivity.class));
		finish();
	}

	@Override
	public void latsPage() {
		startActivity(new Intent(this, SafeLeader3Activity.class));
		finish();
	}
}
