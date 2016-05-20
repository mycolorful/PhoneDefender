package per.yrj.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class BaseSafeLeader extends Activity{
	private GestureDetector mDetector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mDetector = new GestureDetector(this, new android.view.GestureDetector.SimpleOnGestureListener(){
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				if(Math.abs(e1.getY() - e2.getY()) > 200){
					return super.onFling(e1, e2, velocityX, velocityY);
				}
				//X轴滑动速度太慢则忽略该手势
				if(Math.abs(velocityX) < 100){
					return super.onFling(e1, e2, velocityX, velocityY);
				}
				//识别返回手势
				if(e2.getX() - e1.getX() > 200){
					preStep(null);
					return true;
				}
				//识别下一步手势
				if(e1.getX() - e2.getX() > 200){
					nextStep(null);
					return true;
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	
	/**
	 * 下一步
	 * @param v
	 */
	public void nextStep(View v) {
		nextPage();
		overridePendingTransition(R.anim.next_enter_anim, R.anim.next_exit_anim);
	}
	

	/**
	 * 上一步
	 * @param v
	 */
	public void preStep(View v) {
		latsPage();
		overridePendingTransition(R.anim.pre_enter_anim, R.anim.pre_exit_anim);
	}
	
	public abstract void nextPage();
	public abstract void latsPage();

}
