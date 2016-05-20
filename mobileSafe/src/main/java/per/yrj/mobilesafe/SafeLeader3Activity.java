package per.yrj.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

public class SafeLeader3Activity extends BaseSafeLeader {
	private EditText etSafeNum;
	private SharedPreferences mSharePre;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_safeleader3);
		etSafeNum = (EditText) findViewById(R.id.et_safe_num);
		mSharePre = getSharedPreferences("config", MODE_PRIVATE);
		if(!TextUtils.isEmpty(mSharePre.getString("safeNum", null)))
			etSafeNum.setText(mSharePre.getString("safeNum", null));
		super.onCreate(savedInstanceState);
	}
	
	public void choosePeople(View v){
		startActivityForResult(new Intent(this, PeopleListActivity.class), 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 0){
			if(resultCode == 1){
				etSafeNum.setText(data.getStringExtra("info"));
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void nextPage() {
		//如果安全号码为空则不能进入到下一步
		if(TextUtils.isEmpty(etSafeNum.getText()))
			return;
		else{
			mSharePre.edit().putString("safeNum", etSafeNum.getText().toString()).commit();
		}
		startActivity(new Intent(this, SafeLeader4Activity.class));
		finish();
	}

	@Override
	public void latsPage() {
		startActivity(new Intent(this, SafeLeader2Activity.class));
		finish();
	}
}
