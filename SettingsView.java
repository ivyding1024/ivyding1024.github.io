package com.mdbiomedical.app.vion.vian_health.view;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mdbiomedical.app.vion.vian_health.R;
//import com.facebook.Session;
import com.mdbiomedical.app.vion.vian_health.util.ChangeView;
import com.mdbiomedical.app.vion.vian_health.util.DeviceConstant;
import com.mdbiomedical.app.vion.vian_health.util.UIUtils;

public class SettingsView extends Activity {
    
	Intent intent;
	Context context;
	protected void onDestroy() {  
        super.onDestroy();  
        System.gc();  
    } 
	
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);
		context=this;
		init();
		
	}
	

	@Override
	public void onBackPressed() {
//		if (DeviceConstant.statusBarHeight != 0) {
//			View view = getWindow().getDecorView().findViewById(android.R.id.content);
//			try {
//				Bitmap leftSideImage = UIUtils.creatMenuLeftSidePic(view);
//
//				if (leftSideImage != null) {
//					HomeView.iv_home_left_side_pic.setVisibility(View.VISIBLE);
//					HomeView.iv_home_left_side_pic.setImageBitmap(leftSideImage);
//				}
//			} catch (Exception e) {
//				HomeView.iv_home_left_side_pic.setVisibility(View.INVISIBLE);
//			}
//
//		} else {
//			HomeView.iv_home_left_side_pic.setImageResource(R.drawable.transparent_10x10);
//		}
		
		
		setResult(RESULT_OK);

		finish();
		overridePendingTransition(R.anim.slide_no, R.anim.slide_out_left);
	}
	//sandy0914
		public static DisplayMetrics dm = new DisplayMetrics();
		
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
	
	
	public void onScanDevices(View v) {
		intent = new Intent();
		intent.setClass(SettingsView.this, ScanView.class);
		startActivityForResult(intent, 136);
		overridePendingTransition(R.anim.slide_in_right,
				R.anim.left_out);
		
	}
	public void onUserInformation(View v) {
		intent = new Intent();
		intent.setClass(SettingsView.this, UserInformationView.class);
		startActivityForResult(intent, 136);
		overridePendingTransition(R.anim.slide_in_right,
				R.anim.left_out);
		
	}
	public void onDeviceSettings(View v) {
		intent = new Intent();
		intent.setClass(SettingsView.this, DeviceView.class);
		startActivityForResult(intent, 136);
		overridePendingTransition(R.anim.slide_in_right,
				R.anim.left_out);
		
	}
	
	public void onchiball(View v) {
		
		if(isPackageInstalled("com.mdbiomedical.app.vion",context))
		{
			String url = "md://300x";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.left_out);
		}
		else
		{
			String url = "market://details?id=com.mdbiomedical.app.vion.vian_health";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.left_out);
		}
		
		
		

		
	}
	private boolean isPackageInstalled(String packagename, Context context) {
	    PackageManager pm = context.getPackageManager();
	    try {
	        pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
	        return true;
	    } catch (NameNotFoundException e) {
	        return false;
	    }
	}
	

}
