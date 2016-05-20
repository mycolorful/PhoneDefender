package per.yrj.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import per.yrj.model.AdminReceiver;
import per.yrj.utils.MD5;

public class HomeActivity extends Activity {
	private GridView gvTools;
	private String[] toolsName = { "手机防盗", "通讯卫士", "软件管理", "进程管理", "流量统计",
			"手机杀毒", "缓存管理", "高级工具", "设置中心" };
	private int[] toolsImg = { R.drawable.home_apps,
			R.drawable.home_callmsgsafe, R.drawable.home_netmanager,
			R.drawable.home_safe, R.drawable.home_settings,
			R.drawable.home_sysoptimize, R.drawable.home_taskmanager,
			R.drawable.home_tools, R.drawable.home_trojan };
	private SharedPreferences mPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_home);
		//激活设备管理器
		//-----------------------------------------------------------------------//
		ComponentName deviceAdmin = new ComponentName(this, AdminReceiver.class);
		DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		//如果设备管理器未激活则先激活设备管理器
		if(!dpm.isAdminActive(deviceAdmin)){
			Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdmin);
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "使用设备管理器来防止手机被盗后造成数据泄露");
			startActivity(intent);
		}
		//--------------------------------------------------------------------//
		gvTools = (GridView) findViewById(R.id.tools);
		mPref = getSharedPreferences("config", MODE_PRIVATE);
		gvTools.setAdapter(new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = View.inflate(HomeActivity.this, R.layout.home_list,
						null);
				ImageView ivToolImage = (ImageView) view
						.findViewById(R.id.toolImage);
				TextView tvToolName = (TextView) view
						.findViewById(R.id.toolName);

				ivToolImage.setImageResource(toolsImg[position]);
				tvToolName.setText(toolsName[position]);

				return view;
			}

			@Override
			public long getItemId(int position) {

				return position;
			}

			@Override
			public Object getItem(int position) {

				return toolsName[position];
			}

			@Override
			public int getCount() {

				return toolsName.length;
			}
		});
		gvTools.setOnItemClickListener(new OnItemClickListener() {

			private TextView tvTitle;
			private EditText etPsd;
			private EditText etConfirmPsd;
			private Button btOk;
			private Button btCancel;
			private AlertDialog dialog;

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					View v = getLayoutInflater().inflate(R.layout.set_password,
							null);
					AlertDialog.Builder builder = new AlertDialog.Builder(
							HomeActivity.this);
					builder.setView(v);
					dialog = builder.show();
					tvTitle = (TextView)v.findViewById(R.id.set_psd_title);
					etPsd = (EditText) v.findViewById(R.id.et_setPsd);
					etConfirmPsd = (EditText) v
							.findViewById(R.id.et_confirmPsd);
					btOk = (Button) v.findViewById(R.id.ok);
					btCancel = (Button) v.findViewById(R.id.cancel);
					String psd = mPref.getString("password", null);
					//如果密码为空则设置密码，否则确认密码
					if (psd == null) {
						setPassword();
					} else {
						confirmPassword(psd);
					}
					break;
				case 1:
					startActivity(new Intent(HomeActivity.this, CommunicationSafeActivity.class));
					break;
				case 2:
					startActivity(new Intent(HomeActivity.this, AppManagerActivity.class));
					break;
				case 3:
					startActivity(new Intent(HomeActivity.this, ProcessManagerActivity.class));
					break;
				case 4:

					break;
				case 5:
					startActivity(new Intent(HomeActivity.this,AntivirusActivity.class));
					break;
				case 6:

					break;
				case 7:
					startActivity(new Intent(HomeActivity.this, AtoolsActivity.class));
					break;
				case 8:
					startActivity(new Intent(HomeActivity.this,
							SettingActivity.class));
					break;

				default:
					break;
				}
			}

			/**
			 * 设置密码
			 */
			private void setPassword() {

				btOk.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						tvTitle.setText("设置密码");
						String psd = etPsd.getText().toString();
						String confirmPsd = etConfirmPsd.getText().toString();
						if (TextUtils.isEmpty(psd)
								|| TextUtils.isEmpty(confirmPsd)) {
							Toast.makeText(HomeActivity.this, "密码不能为空",
									Toast.LENGTH_SHORT).show();
						} else if (!psd.equals(confirmPsd)) {
							Toast.makeText(HomeActivity.this, "两次密码不一致",
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(HomeActivity.this, "设置密码成功",
									Toast.LENGTH_SHORT).show();
							mPref.edit().putString("password", MD5.toMD5(psd))
									.commit();
							dialog.dismiss();
							startActivity(new Intent(HomeActivity.this,
									SafeLeader1Activity.class));
						}
					}
				});
				btCancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
			}

			/**
			 * 判断密码
			 * 
			 * @param md5Psd
			 *            md5加密过的密码
			 */
			private void confirmPassword(final String md5Psd) {
				tvTitle.setText("输入密码");
				etConfirmPsd.setVisibility(View.GONE);
				btOk.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						String psd = etPsd.getText().toString();
						// 验证密码
						if (MD5.toMD5(psd).equals(md5Psd)) {
							dialog.dismiss();
							// 判断是否是第一次进入设置页面，如果是就先进入设置向导，否则直接进入
							if (getSharedPreferences("config", MODE_PRIVATE)
									.getBoolean("isFirstIn", true))
								startActivity(new Intent(HomeActivity.this,
										SafeLeader1Activity.class));
							else
								startActivity(new Intent(HomeActivity.this,
										SafeActivity.class));

						} else {
							Toast.makeText(HomeActivity.this, "密码错误",
									Toast.LENGTH_SHORT).show();
							etPsd.setText("");
						}

					}
				});
				btCancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
			}

		});
		super.onCreate(savedInstanceState);
	}
}
