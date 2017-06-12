package com.mdbiomedical.app.vion.vian_health.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.mdbiomedical.app.vion.vian_health.R;
import com.mdbiomedical.app.vion.vian_health.util.ChangeView;
import com.mdbiomedical.app.vion.vian_health.util.DeviceConstant;
import com.mdbiomedical.app.vion.vian_health.util.UIUtils;
import com.mdbiomedical.app.vion.vian_health.view.RecordDetail.OnDoubleClick;

import java.text.DateFormatSymbols;

public class AnalysisView extends Activity {
	public static final String ANALYSIS_SETTING = "ANALYSIS_SETTING";
	public static final String ANA_DATEFROM = "ANA_DATEFROM";
	public static final String ANA_MORNING = "ANA_MORNING";
	public static final String ANA_DAYTIME = "ANA_DAYTIME";
	public static final String ANA_EVENING = "ANA_EVENING";
	public static final String ANA_NIGHT = "ANA_NIGHT";
	public static final String ANA_GRAPH = "ANA_GRAPH";

	final public static int WEEK = 0, MONTH = 1, YEAR = 2;

	TextView tv_ana_date;
	//Switch iv_sw_morning, iv_sw_daytime, iv_sw_evening, iv_sw_night;
	ImageView iv_chk_trend, iv_chk_statistic, iv_chk_distribution;
	LinearLayout ll_ana_sel_date;
	WheelView year, month, day;

	int lastYear;

	Calendar calendar;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", java.util.Locale.getDefault());

	String dateString = DateFormat.getBestDateTimePattern(java.util.Locale.getDefault(), "yyyy/MM/dd ");
	java.text.DateFormat dateTimeInstance = new SimpleDateFormat(dateString, java.util.Locale.getDefault());
	Intent intent;
	private GestureDetector gd;
	private View.OnTouchListener gestureListener;
	SharedPreferences settings;
	
	//sandy0914
		public static DisplayMetrics dm = new DisplayMetrics();
		//
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.analysis_activity);
	

		init();
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					v.performClick();
					//clickSwitch(v);
				}

				return true;
			}
		};
		// iv_sw_morning.setOnTouchListener(gestureListener);
		// iv_sw_daytime.setOnTouchListener(gestureListener);
		// iv_sw_evening.setOnTouchListener(gestureListener);
		// iv_sw_night.setOnTouchListener(gestureListener);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		HomeView.home_pressed = "disable";
		SharedPreferences settings = getSharedPreferences(ANALYSIS_SETTING, 0);
		//int iGraph = settings.getInt(ANA_GRAPH, 0);
		//setGraphSelection(iGraph);

//		iv_sw_morning.setChecked(settings.getBoolean(ANA_MORNING, true));
//		iv_sw_daytime.setChecked(settings.getBoolean(ANA_DAYTIME, true));
//		iv_sw_evening.setChecked(settings.getBoolean(ANA_EVENING, true));
//		iv_sw_night.setChecked(settings.getBoolean(ANA_NIGHT, true));
		/*
		 * Log.d("nick", "ANA_MORNING="+settings.getBoolean(ANA_MORNING,
		 * false)); Log.d("nick",
		 * "ANA_DAYTIME="+settings.getBoolean(ANA_DAYTIME, false));
		 * Log.d("nick", "ANA_EVENING="+settings.getBoolean(ANA_EVENING,
		 * false)); Log.d("nick", "ANA_NIGHT="+settings.getBoolean(ANA_NIGHT,
		 * false));
		 */

		calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -7);

		long l = settings.getLong(ANA_DATEFROM, calendar.getTime().getTime());
		Date d = new Date();
		d.setTime(l);
		calendar.setTime(d);
		// String today = dateFormat.format(calendar.getTime()).toString();
		tv_ana_date.setText(dateTimeInstance.format(calendar.getTime()));
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d("alex", "on Pause");
		HomeView.home_pressed = "wait";
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private void init() {
		//sandy0914
				getWindowManager().getDefaultDisplay().getMetrics(dm);
				DeviceConstant.screenWidth = dm.widthPixels;
				DeviceConstant.screenHeight = dm.heightPixels;
				DeviceConstant.screenDPI = dm.densityDpi;
				
		ll_ana_sel_date = (LinearLayout) findViewById(R.id.ll_ana_sel_date);

		tv_ana_date = (TextView) findViewById(R.id.tv_ana_date);

		//iv_sw_morning = (Switch) findViewById(R.id.iv_sw_morning);
		//iv_sw_daytime = (Switch) findViewById(R.id.iv_sw_daytime);
		//iv_sw_evening = (Switch) findViewById(R.id.iv_sw_evening);
		//iv_sw_night = (Switch) findViewById(R.id.iv_sw_night);
		
		
//		iv_sw_morning.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				
//				settings = getSharedPreferences(ANALYSIS_SETTING, 1);
//				settings.edit().putBoolean(ANA_MORNING, isChecked).commit();
//			}
//		});
//		iv_sw_daytime.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				settings = getSharedPreferences(ANALYSIS_SETTING, 1);
//				settings.edit().putBoolean(ANA_DAYTIME, isChecked).commit();
//
//			}
//		});
//		iv_sw_evening.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				settings = getSharedPreferences(ANALYSIS_SETTING, 1);
//				settings.edit().putBoolean(ANA_EVENING, isChecked).commit();
//
//			}
//		});
//		iv_sw_night.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				settings = getSharedPreferences(ANALYSIS_SETTING, 1);
//				settings.edit().putBoolean(ANA_NIGHT, isChecked).commit();
//
//			}
//		});
		
		iv_chk_trend = (ImageView) findViewById(R.id.iv_chk_trend);
		iv_chk_statistic = (ImageView) findViewById(R.id.iv_chk_statistic);
		iv_chk_distribution = (ImageView) findViewById(R.id.iv_chk_distribution);
		iv_chk_trend.setImageResource(R.drawable.transparent_10x10);
		iv_chk_statistic.setImageResource(R.drawable.transparent_10x10);
		iv_chk_distribution.setImageResource(R.drawable.transparent_10x10);
		ImageView iv_record_menu;
		iv_record_menu = (ImageView) findViewById(R.id.ll_go_back);
		TextView tv_analysis_title = (TextView) findViewById(R.id.tv_analysis_title);

		tv_analysis_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.03f));

		// ¦^¤W¤@­¶
		iv_record_menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				try {
					ChangeView.onBack();
				} catch (Exception e) {
					finish();
				}
			}
		});
		SharedPreferences settings = getSharedPreferences(ANALYSIS_SETTING, 0);
		calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -7);
		
		long temp = settings.getLong(ANA_DATEFROM, calendar.getTime().getTime());
		Date tempdate = new Date();
		tempdate.setTime(temp);
		calendar.setTime(tempdate);
		int tempYear =calendar.get(Calendar.YEAR);
		int tempMonth=calendar.get(Calendar.MONTH);
		int tempday=calendar.get(Calendar.DAY_OF_MONTH)-1;
		Log.e("time", "tempYear = "+  tempYear);
		

		Calendar calendar = Calendar.getInstance();

		month = (WheelView) findViewById(R.id.month);
		year = (WheelView) findViewById(R.id.year);
		day = (WheelView) findViewById(R.id.day);

		OnWheelChangedListener listener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				updateDays(year, month, day);
			}
		};

		// month
		int curMonth = calendar.get(Calendar.MONTH);
		String months[] = new String[12] ;
		for(int x=0;x<12;x++)
		{
			months[x]=getMonth(x);
		}
		month.setViewAdapter(new DateArrayAdapter(this, months, curMonth));
		month.setCurrentItem(tempMonth);
		month.addChangingListener(listener);
		month.setCyclic(true);

		final int iYearCount = 10;
		// year
		int curYear = calendar.get(Calendar.YEAR);
		lastYear = curYear;
		year.setViewAdapter(new DateNumericAdapter(this, curYear - iYearCount, curYear, 0));
		year.setCurrentItem(tempYear-(curYear - iYearCount));
		year.addChangingListener(listener);

		// day
		updateDays(year, month, day);
		day.setCurrentItem(tempday);
		day.setCyclic(true);

	}

	public String getMonth(int month) {
	    return new DateFormatSymbols().getMonths()[month];
	}
	
	@Override
	public void onBackPressed() {
		// if (DeviceConstant.statusBarHeight != 0) {
		// View view =
		// getWindow().getDecorView().findViewById(android.R.id.content);
		// try {
		// Bitmap leftSideImage = UIUtils.creatMenuLeftSidePic(view);
		//
		// if (leftSideImage != null) {
		// HomeView.iv_home_left_side_pic.setVisibility(View.VISIBLE);
		// HomeView.iv_home_left_side_pic.setImageBitmap(leftSideImage);
		// }
		// } catch (Exception e) {
		// HomeView.iv_home_left_side_pic.setVisibility(View.INVISIBLE);
		// }
		//
		// } else {
		// HomeView.iv_home_left_side_pic.setImageResource(R.drawable.transparent_10x10);
		// }

		setResult(RESULT_OK);
		finish();
		overridePendingTransition(R.anim.slide_no, R.anim.slide_out_left);
	}

//	public void clickSwitch(View v) {
//
//		
//		boolean bOn = false;
//		switch (v.getId()) {
//
//		case R.id.iv_sw_morning:
//			settings = getSharedPreferences(ANALYSIS_SETTING, 1);
//			bOn = !settings.getBoolean(ANA_MORNING, true);
//			settings.edit().putBoolean(ANA_MORNING, bOn).commit();
//			break;
//		case R.id.iv_sw_daytime:
//			settings = getSharedPreferences(ANALYSIS_SETTING, 1);
//			bOn = !settings.getBoolean(ANA_DAYTIME, true);
//			settings.edit().putBoolean(ANA_DAYTIME, bOn).commit();
//			break;
//		case R.id.iv_sw_evening:
//			settings = getSharedPreferences(ANALYSIS_SETTING, 1);
//			bOn = !settings.getBoolean(ANA_EVENING, true);
//			settings.edit().putBoolean(ANA_EVENING, bOn).commit();
//			break;
//		case R.id.iv_sw_night:
//			settings = getSharedPreferences(ANALYSIS_SETTING, 1);
//			bOn = !settings.getBoolean(ANA_NIGHT, true);
//			settings.edit().putBoolean(ANA_NIGHT, bOn).commit();
//			break;
//		}
//
//		if (bOn == true) {
//			((ImageView) v).setImageResource(R.drawable.toggle_buttons_on1);
//		} else {
//			((ImageView) v).setImageResource(R.drawable.toggle_buttons_off1);
//		}
//
//	}

	void setGraphSelection(int i) {
		switch (i) {

		case 0:
			iv_chk_trend.setImageResource(R.drawable.selected);
			iv_chk_statistic.setImageResource(R.drawable.transparent_10x10);
			iv_chk_distribution.setImageResource(R.drawable.transparent_10x10);
			break;
		case 1:
			iv_chk_trend.setImageResource(R.drawable.transparent_10x10);
			iv_chk_statistic.setImageResource(R.drawable.selected);
			iv_chk_distribution.setImageResource(R.drawable.transparent_10x10);
			break;
		case 2:
			iv_chk_trend.setImageResource(R.drawable.transparent_10x10);
			iv_chk_statistic.setImageResource(R.drawable.transparent_10x10);
			iv_chk_distribution.setImageResource(R.drawable.selected);
			break;
		}

	}

	public void clickSelection(View v) {

		SharedPreferences settings;
		int iGraph = 0;
		switch (v.getId()) {

		case R.id.iv_trend:
			iGraph = 0;
			break;
		case R.id.iv_statistic:
			iGraph = 1;
			break;
		case R.id.iv_distribution:
			iGraph = 2;
			break;
		}

		setGraphSelection(iGraph);
		settings = getSharedPreferences(ANALYSIS_SETTING, 0);
		settings.edit().putInt(ANA_GRAPH, iGraph).commit();
		onAnalysisClick();
	}

	public void onAnalysisClick() {

		SharedPreferences settings = getSharedPreferences(ANALYSIS_SETTING, 0);
		int iGraph = settings.getInt(ANA_GRAPH, 0);

		intent = new Intent();
		switch (iGraph) {
		case 0:
			intent.setClass(AnalysisView.this, TrendView.class);
			break;
		case 1:
			intent.setClass(AnalysisView.this, StatisticView.class);
			break;
		case 2:
			intent.setClass(AnalysisView.this, DistributionActivity.class);
			break;
		}

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Bundle bundle = new Bundle();
		bundle.putLong("Datetime", calendar.getTime().getTime());
		intent.putExtras(bundle);

		startActivityForResult(intent, 136);
		overridePendingTransition(R.anim.slide_in_right, R.anim.left_out);

	}

	public void clickDatafrom(View v) {
		ll_ana_sel_date.getLayoutParams().height = 800;
		ll_ana_sel_date.requestLayout();

	}

	public void clickCancel(View v) {
		ll_ana_sel_date.getLayoutParams().height = 0;
		ll_ana_sel_date.requestLayout();
	}

	public void clickDone(View v) {
		ll_ana_sel_date.getLayoutParams().height = 0;
		ll_ana_sel_date.requestLayout();

		calendar.set(lastYear - 10 + year.getCurrentItem(), month.getCurrentItem(), day.getCurrentItem() + 1);

		// String today = dateFormat.format(calendar.getTime()).toString();
		tv_ana_date.setText(dateTimeInstance.format(calendar.getTime()));

		SharedPreferences settings;
		settings = getSharedPreferences(ANALYSIS_SETTING, 0);
		settings.edit().putLong(ANA_DATEFROM, calendar.getTime().getTime()).commit();

	}

	/**
	 * Updates day wheel. Sets max days according to selected month and year
	 */
	void updateDays(WheelView year, WheelView month, WheelView day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + year.getCurrentItem());
		calendar.set(Calendar.MONTH, month.getCurrentItem());

		int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		day.setViewAdapter(new DateNumericAdapter(this, 1, maxDays, calendar.get(Calendar.DAY_OF_MONTH) - 1));
		int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
		day.setCurrentItem(curDay - 1, true);
	}

	/**
	 * Adapter for numeric wheels. Highlights the current value.
	 */
	private class DateNumericAdapter extends NumericWheelAdapter {
		// Index of current item
		int currentItem;
		// Index of item to be highlighted
		int currentValue;

		/**
		 * Constructor
		 */
		public DateNumericAdapter(Context context, int minValue, int maxValue, int current) {
			super(context, minValue, maxValue);
			this.currentValue = current;
			setTextSize(24);
		}

		@Override
		protected void configureTextView(TextView view) {
			super.configureTextView(view);
			if (currentItem == currentValue) {
				view.setTextColor(0xFF0000F0);
			}
			view.setTypeface(Typeface.SANS_SERIF);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			currentItem = index;
			return super.getItem(index, cachedView, parent);
		}
	}

	/**
	 * Adapter for string based wheel. Highlights the current value.
	 */
	private class DateArrayAdapter extends ArrayWheelAdapter<String> {
		// Index of current item
		int currentItem;
		// Index of item to be highlighted
		int currentValue;

		/**
		 * Constructor
		 */
		public DateArrayAdapter(Context context, String[] items, int current) {
			super(context, items);
			this.currentValue = current;
			setTextSize(24);
		}

		@Override
		protected void configureTextView(TextView view) {
			super.configureTextView(view);
			if (currentItem == currentValue) {
				view.setTextColor(0xFF0000F0);
			}
			view.setTypeface(Typeface.SANS_SERIF);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			currentItem = index;
			return super.getItem(index, cachedView, parent);
		}
	}

}
