package per.yrj.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SafeActivity extends Activity {
	private TextView tvSafeNum;
	private ImageView ivLockSafe;
	private SharedPreferences mSharePre;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_safe);
		getSharedPreferences("config", MODE_PRIVATE).edit().putBoolean("isFirstIn", false).commit();
		tvSafeNum = (TextView) findViewById(R.id.tv_safeNum);
		ivLockSafe = (ImageView) findViewById(R.id.iv_locksafe);
		mSharePre = getSharedPreferences("config", MODE_PRIVATE);
		tvSafeNum.setText(mSharePre.getString("safeNum", null));
		if(mSharePre.getBoolean("openProtect", false)){
			ivLockSafe.setImageResource(R.drawable.lock);
		}else{
			ivLockSafe.setImageResource(R.drawable.unlock);
		}
		super.onCreate(savedInstanceState);
	}
	
	public void enterSetLeader(View view){
		startActivity(new Intent(this, SafeLeader1Activity.class));
		finish();
	}
}
