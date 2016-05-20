package per.yrj.mobilesafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import per.yrj.utils.StreamUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;


public class SplashActivity extends Activity {
	private static final int SHOW_UPDATE_DIALOG = 0X11;
	private static final int NET_ERROR = 0X12;
	private static final int JSON_ERROR = 0X13;
	private static final int URL_ERROR = 0X14;
	private static final int START_HOME = 0X15;
	private TextView tvVersion;
	private TextView tvDownload;
	private int mVersionCode;
	private int mCurrentVersionCode;
	private String mCurrentVersionName;
	private String mVersionName;
	private String mDownloadUrl;
	private String mDescription;
	private RelativeLayout rlRoot;
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_UPDATE_DIALOG:
				updateDialog();
				break;
			case NET_ERROR:
				Toast.makeText(SplashActivity.this, "网络错误", Toast.LENGTH_LONG).show();
				startHome();
				break;
			case JSON_ERROR:
				Toast.makeText(SplashActivity.this, "解析错误", Toast.LENGTH_LONG).show();	
				startHome();			
				break;
			case URL_ERROR:
				Toast.makeText(SplashActivity.this, "url错误", Toast.LENGTH_LONG).show();
				startHome();				
				break;
			case START_HOME:
				startHome();
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//    	设置全屏显示
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		copyDB("address.db");// 拷贝归属地查询数据库

		copyDB("antivirus.db");//拷贝病毒数据库文件

//      初始化
        tvVersion = (TextView) findViewById(R.id.version);
        tvDownload = (TextView) findViewById(R.id.downloadProBar);
        PackageManager packageManger = getPackageManager();
        PackageInfo packageInfo = null;
		try {
			packageInfo = packageManger.getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		mCurrentVersionCode = packageInfo.versionCode;
        mCurrentVersionName = packageInfo.versionName;
        tvVersion.setText("当前版本"+mCurrentVersionName);
        
        //为闪屏页面添加渐变动画
        rlRoot = (RelativeLayout) findViewById(R.id.rlRoot);
        AlphaAnimation anim = new AlphaAnimation(0.3f, 1f);
        anim.setDuration(2000);
        rlRoot.startAnimation(anim);
        
        //判断是否需要检查更新
        if(getSharedPreferences("config", MODE_PRIVATE).getBoolean("auto_update", true)){
        	checkAndUpdate();
        }else{
        	//如果不需要更新，那就延迟两秒发送handler消息跳转到主页面
        	handler.sendEmptyMessageDelayed(START_HOME,2000);
        }

    }
    
    /**
     * 检查并更新版本
     */
    private void checkAndUpdate(){
//      新建子线程，从网络获取版本信息，检查更新
        new Thread(){
        	public void run(){
//        		线程开始时计时
        		long startTime = System.currentTimeMillis();
//	        	初始化msg
        		Message msg = new Message();
        		HttpURLConnection conn = null;
        		InputStream inputStream = null;
	        	try {
					URL url = new URL("http://192.168.56.1:8080/update.json");
					conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(5000);//设置连接超时为5s
					conn.setReadTimeout(5000);//设置读取超时为5s
					conn.connect();
					//获取输入流
					inputStream = conn.getInputStream();
					String result = StreamUtils.readStream(inputStream);
					JSONObject jo = new JSONObject(result);
					mVersionName = jo.getString("versionName");
					mVersionCode = jo.getInt("versionCode");
					mDownloadUrl = jo.getString("downloadURL");
					mDescription = jo.getString("description");
//					如果当前版本号小于最新版本号
					if(mCurrentVersionCode < mVersionCode)
						msg.what = SHOW_UPDATE_DIALOG;
					else
						msg.what = START_HOME;
	        	} catch (MalformedURLException e) {
	        		msg.what = URL_ERROR;
	        		e.printStackTrace();
	        	} catch (IOException e) {
					msg.what = NET_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					msg.what = JSON_ERROR;
					e.printStackTrace();
				}
	        	finally{
	        		try {
	        			if(inputStream != null)
	        				inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
	        		if(conn != null)
	        			conn.disconnect();
//	        		计算线程一共花费时间，如果少于两秒则让线程休眠直到Splash展示2s
	        		long endTime = System.currentTimeMillis();
	        		long usedTime = endTime - startTime;
	        		if(usedTime < 2000)
						try {
							Thread.sleep(2000 - usedTime);
							Log.i("times", "used:"+usedTime+"  sleepTiem"+(2000-usedTime));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
	        		handler.sendMessage(msg);
	        	}
        	}
        }.start();
    }
    
    private void startHome(){
    	Intent intent = new Intent(this, HomeActivity.class);
    	startActivity(intent);
    	finish();
    }
    
    /**
     * 显示升级提示框
     */
    private void updateDialog(){
    	
			AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
			builder.setTitle("发现新版本！");
			builder.setMessage("1.更新版本"+mVersionName+System.getProperty("line.separator")+"2.更新内容："
			+mDescription);
			builder.setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					startHome();
					
				}
			});
			builder.setPositiveButton("立即升级", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					downloadNewVersion();
				}
			});
			builder.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					startHome();
				}
			});
			builder.show();
    }

    /**
     * 下载新版本
     */
    private void downloadNewVersion(){
    	if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
	    	String target = Environment.getExternalStorageDirectory()+"/newVersion.apk";
	    	HttpUtils http = new HttpUtils();
	    	http.download(mDownloadUrl, target, new RequestCallBack<File>() {
				
	    		/**
	    		 * 开始下载时显示进度
	    		 */
	    		@Override
	    		public void onStart() {
	    			tvDownload.setVisibility(View.VISIBLE);
	    			super.onStart();
	    		}
	    		
	    		/**
	    		 * 传输时显示下载百分比
	    		 */
	    		@Override
	    		public void onLoading(long total, long current, boolean isUploading) {
	    			tvDownload.setText("正在下载："+(current*1.0/total)*100+"%");
	    			Log.i("download", current+"");
	    			super.onLoading(total, current, isUploading);
	    		}
	    		
	    		/**
	    		 * 传输成功时隐藏下载进度
	    		 */
				@Override
				public void onSuccess(ResponseInfo<File> arg0) {
					tvDownload.setText("下载成功！");
					Intent intent = new Intent("android.intent.action.VIEW");
					intent.addCategory(Intent.CATEGORY_DEFAULT);
					intent.setDataAndType(Uri.fromFile(arg0.result), 
							"application/vnd.android.package-archive");
					startActivityForResult(intent, 0);
				}
	
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					
				}
				
			});
    	}
    }
 
    /**
     * 如果用户下载后进入安装页面点击了取消安装则执行此方法
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	startHome();
    	super.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	/**
	 * 拷贝数据库
	 *
	 * @param dbName
	 */
	private void copyDB(final String dbName) {
		new Thread(){
			public void run() {
				InputStream is = null;
				FileOutputStream fos = null;
				try {
					File file = new File(getFilesDir(),dbName);
					if(file.exists()&&file.length()>0){
						Log.i("copyDB","数据库" + dbName + "已存在!");
						return ;
					}
					is = getAssets().open(dbName);
					fos  = openFileOutput(dbName, MODE_PRIVATE);
					byte[] buffer = new byte[1024];
					int len = 0;
					while((len = is.read(buffer))!=-1){
						fos.write(buffer, 0, len);
					}
					Log.i("copyDB","数据库" + dbName + "拷贝成功!");
				} catch (Exception e) {
					Log.i("copyDB","数据库" + dbName + "拷贝失败!");
					e.printStackTrace();
				} finally {
					try {
						if (is != null) {
							is.close();
						}
						if(fos != null) {
							fos.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
}
