package com.mdbiomedical.app.vion.vian_health.view;

import com.mdbiomedical.app.vion.vian_health.R;
import com.mdbiomedical.app.vion.vian_health.util.ChangeView;
import com.mdbiomedical.app.vion.vian_health.util.DeviceConstant;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutView extends Activity {
	//sandy0914
	public static DisplayMetrics dm = new DisplayMetrics();
	//
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity);
		//sandy0914
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		DeviceConstant.screenWidth = dm.widthPixels;
		DeviceConstant.screenHeight = dm.heightPixels;
		DeviceConstant.screenDPI = dm.densityDpi;

		
		TextView tv_tutorial_title = (TextView) findViewById(R.id.tv_tutorial_title);
		tv_tutorial_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.03f));
		
		ImageView iv_toturial_menu;
		iv_toturial_menu = (ImageView) findViewById(R.id.iv_toturial_menu);
		// ¦^¤W¤@­¶
		iv_toturial_menu.setOnClickListener(new OnClickListener() {

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
	public void onBackPressed() {

		setResult(RESULT_OK);
		finish();
		overridePendingTransition(R.anim.slide_no, R.anim.slide_out_left);
	}

	@Override
	public void onResume() {
		// TODO LC: preliminary support for views transitions
		this.overridePendingTransition(R.anim.left_in, R.anim.left_out);
		super.onResume();
		HomeView.home_pressed = "disable";
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d("alex", "on Pause");
		HomeView.home_pressed = "wait";
	}
}
