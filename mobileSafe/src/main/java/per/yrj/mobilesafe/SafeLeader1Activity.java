package per.yrj.mobilesafe;

import android.content.Intent;
import android.os.Bundle;

public class SafeLeader1Activity extends BaseSafeLeader {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_safeleader1);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void nextPage() {
		startActivity(new Intent(this, SafeLeader2Activity.class));
		finish();
	}

	@Override
	public void latsPage() {
		startActivity(new Intent(this, HomeActivity.class));
		finish();
	}
	
}
