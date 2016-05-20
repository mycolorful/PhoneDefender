package per.yrj.view;

import per.yrj.mobilesafe.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingItemView extends RelativeLayout {

	private static final String NAMESPACE = "http://schemas.android.com/apk/res/per.yrj.mobilesafe";
	private String mTitle;
	private String mDescOn;
	private String mDescOff;
	private TextView tvTitle;
	private TextView tvDesc;
	private CheckBox checkBox;

	public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView();
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mTitle = attrs.getAttributeValue(NAMESPACE, "item_title");
		mDescOn = attrs.getAttributeValue(NAMESPACE,"desc_on");
		mDescOff = attrs.getAttributeValue(NAMESPACE,"desc_off");
		initView();
	}

	public SettingItemView(Context context) {
		super(context);
		initView();
	}

	private void initView() {
		View.inflate(getContext(), R.layout.setting_item, this);
		tvTitle = (TextView) findViewById(R.id.tvSettingItemTitle);
		tvDesc = (TextView) findViewById(R.id.tvSettingItemDesc);
		checkBox = (CheckBox) findViewById(R.id.cbIsAutoUpdate);
		tvTitle.setText(mTitle);
		if(checkBox.isChecked()){
			tvDesc.setText(mDescOn);
		}else{
			tvDesc.setText(mDescOff);
		}
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(checkBox.isChecked()){
					tvDesc.setText(mDescOn);
				}else{
					tvDesc.setText(mDescOff);
				}
			}
		});
	}

	public void setTitle(String title) {
		tvTitle.setText(title);
	}

	public void setDesc(String desc) {
		tvDesc.setText(desc);
	}
	
	public boolean isChecked(){
		return checkBox.isChecked();
	}
	
	public void setChecked(boolean isChecked){
		checkBox.setChecked(isChecked);
	}
}
