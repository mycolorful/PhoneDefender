package per.yrj.mobilesafe;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import per.yrj.utils.SmsUtils;
import per.yrj.utils.UIUtils;

public class AtoolsActivity extends Activity {

	private Button button;
	private ProgressDialog pd;
	@ViewInject(R.id.progressBar1)
	private ProgressBar progressBar1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
		ViewUtils.inject(this);

	}


	public void numberAddressQuery(View view){
		Intent intent = new Intent(this,NumberAddressQueryActivity.class);
		startActivity(intent);
	}
	/**
	 * 程序锁
	 * @param view
	 */
	public void appLock(View view){
		Intent intent = new Intent(this,AppLockActivity.class);
		startActivity(intent);
	}


	/**
	 * 备份短信
	 * @param view
	 */
	public void backUpsms(View view){
		//初始化一个进度条的对话框
		pd = new ProgressDialog(AtoolsActivity.this);
		pd.setTitle("提示");
		pd.setMessage("稍安勿躁。正在备份。你等着吧。。");
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.show();
		new Thread(){
			public void run() {
				boolean result = SmsUtils.backUp(AtoolsActivity.this,new SmsUtils.BackUpCallBackSms() {

					@Override
					public void onBackUpSms(int process) {
						pd.setProgress(process);
						progressBar1.setProgress(process);

					}

					@Override
					public void befor(int count) {
						pd.setMax(count);
						progressBar1.setMax(count);
					}
				});
				if(result){
					//安全弹吐司的方法
					UIUtils.showToast(AtoolsActivity.this, "备份成功");
				}else{
					UIUtils.showToast(AtoolsActivity.this, "备份失败");
				}
				pd.dismiss();
			};
		}.start();
	}
}
