package per.yrj.mobilesafe;

import per.yrj.view.SettingItemView;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class SettingActivity extends Activity {
	private SettingItemView isAutoUpdate;
	private SharedPreferences mPrefe;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_setting);
		super.onCreate(savedInstanceState);
		isAutoUpdate = (SettingItemView) findViewById(R.id.auto_update);
		mPrefe = getSharedPreferences("config", MODE_PRIVATE);
		isAutoUpdate.setChecked(mPrefe.getBoolean("auto_update", true));
	}
	
	public void onClick(View view){
			if(isAutoUpdate.isChecked()){
				isAutoUpdate.setChecked(false);
				mPrefe.edit().putBoolean("auto_update", false).commit();
			}else{
				isAutoUpdate.setChecked(true);
				mPrefe.edit().putBoolean("auto_update", true).commit();
			}
	}
}
