package com.mdbiomedical.app.vion.vian_health.view;

import com.mdbiomedical.app.vion.vian_health.R;
import com.mdbiomedical.app.vion.vian_health.R.anim;
import com.mdbiomedical.app.vion.vian_health.R.id;
import com.mdbiomedical.app.vion.vian_health.R.layout;
import com.mdbiomedical.app.vion.vian_health.util.ChangeView;
import com.mdbiomedical.app.vion.vian_health.util.DeviceConstant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class RecordUserView extends Activity {
	public static int choose_user=-1;
	Intent intent;
	Context context;
	public static DisplayMetrics dm = new DisplayMetrics();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_user_view);
		context=this;
		init();
		
		
		
	}
	@Override
	public void onBackPressed() {
	
		setResult(RESULT_OK);

		finish();
		overridePendingTransition(R.anim.slide_no, R.anim.slide_out_left);
	}
	private void init() {
		//sandy0914
				getWindowManager().getDefaultDisplay().getMetrics(dm);
				DeviceConstant.screenWidth = dm.widthPixels;
				DeviceConstant.screenHeight = dm.heightPixels;
				DeviceConstant.screenDPI = dm.densityDpi;
				
		ImageView iv_settings_menu = (ImageView) findViewById(R.id.iv_settings_menu);

//		ll_settings_devices = (LinearLayout) findViewById(R.id.ll_settings_devices);

		TextView tv_settings_title = (TextView) findViewById(R.id.tv_settings_title);

		tv_settings_title.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				(int) (DeviceConstant.screenHeight * 0.03f));


		iv_settings_menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				try {
					ChangeView.onBack();
				} catch (Exception e) {
					finish();
				}
			}
		});

	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		HomeView.home_pressed="disable";
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d("alex", "on Pause");
		HomeView.home_pressed = "wait";
		}
	
	public void onUser1(View v) {
		choose_user=0;
		intent = new Intent();
		intent.setClass(RecordUserView.this, RecordsView.class);
		startActivityForResult(intent, 136);
		overridePendingTransition(R.anim.slide_in_right,
				R.anim.left_out);
		
	}
	public void onUser2(View v) {
		choose_user=1;
		intent = new Intent();
		intent.setClass(RecordUserView.this, RecordsView.class);
		startActivityForResult(intent, 136);
		overridePendingTransition(R.anim.slide_in_right,
				R.anim.left_out);
		
	}
	
}
