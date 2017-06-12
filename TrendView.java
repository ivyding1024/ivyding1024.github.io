package com.mdbiomedical.app.vion.vian_health.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.mdbiomedical.app.vion.vian_health.R;
import com.mdbiomedical.app.vion.vian_health.R.layout;
import com.mdbiomedical.app.vion.vian_health.helper.DatabaseHelper;
import com.mdbiomedical.app.vion.vian_health.model.RecordList;
import com.mdbiomedical.app.vion.vian_health.util.AutoResizeTextView;
import com.mdbiomedical.app.vion.vian_health.util.ChangeView;
import com.mdbiomedical.app.vion.vian_health.util.DeviceConstant;
import com.mdbiomedical.app.vion.vian_health.util.AutoResizeTextView.OnSizeChangedListener;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TrendView extends Activity {

	final String saMonthText[] = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };

	TextView tv_list_title, tv_trend_date;
	LinearLayout ll_distri_week, ll_distri_month, ll_distri_year;
	TextView tv_distri_week, tv_distri_month, tv_distri_year;
	TextView bp_mmhg, pulse_bpm;
	TrendGraph trend_graph;
	HorizontalScrollView sc_trend_graph;
	AutoResizeTextView morning, evening, daytime, night;
	List<RecordList> recordList = new ArrayList<RecordList>();
	DatabaseHelper databaseHelper = new DatabaseHelper(this);

	int iBpHeight, iBpWidth;
	int iPulseHeight, iPulseWidth;
	int compareInt[] = new int[4];
	int compareIntCount = 0;
	SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd,yyyy", java.util.Locale.getDefault());
	String dateString = DateFormat.getBestDateTimePattern(java.util.Locale.getDefault(), "yyyy/MM/dd ");
	java.text.DateFormat dateTimeInstance = new SimpleDateFormat(dateString, java.util.Locale.getDefault());
	String MonthString = DateFormat.getBestDateTimePattern(java.util.Locale.getDefault(), "MMM");
	Paint myPaint = new Paint();

	// final static int WEEK=0, MONTH=1, YEAR=2;
	int iPeriod = AnalysisView.WEEK;
	Date dateFrom = new Date();

	boolean baPeriodEnabled[] = { true, true, true, true };

	public String formatMonth(int month) {
		SimpleDateFormat formatter = new SimpleDateFormat("MMM", java.util.Locale.getDefault());
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, month);
		return formatter.format(calendar.getTime());
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trend_activity);

		Bundle bundle = this.getIntent().getExtras();
		long l = bundle.getLong("Datetime");
		dateFrom.setTime(l);

		init();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		HomeView.home_pressed = "disable";
		// String d = dateFormat.format(dateFrom).toString();
		// tv_list_title.setText(dateTimeInstance.format(dateFrom));
		tv_trend_date.setText(getResources().getString(R.string.datefrom) + ": " + dateTimeInstance.format(dateFrom));
		drawButton();

		Timer single_timer = new Timer();
		single_timer.schedule(new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						onPeriodClick(tv_distri_week);
					}
				});
			}
		}, 500);
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
	//sandy0914
		public static DisplayMetrics dm = new DisplayMetrics();
	private void init() {
		//sandy0914
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		DeviceConstant.screenWidth = dm.widthPixels;
		DeviceConstant.screenHeight = dm.heightPixels;
		DeviceConstant.screenDPI = dm.densityDpi;
		
		
		morning = (AutoResizeTextView) findViewById(R.id.morning);
		daytime = (AutoResizeTextView) findViewById(R.id.daytime);
		evening = (AutoResizeTextView) findViewById(R.id.evening);
		night = (AutoResizeTextView) findViewById(R.id.night);
		bp_mmhg = (TextView) findViewById(R.id.bp_mmhg);
		pulse_bpm = (TextView) findViewById(R.id.pulse_bpm);

		// daytime.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)
		// (DeviceConstant.screenHeight * 0.02f));

		// night.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)
		// (DeviceConstant.screenHeight * 0.02f));
		bp_mmhg.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.017f));
		pulse_bpm.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.017f));

		tv_distri_week = (TextView) findViewById(R.id.tv_distri_week);
		tv_distri_month = (TextView) findViewById(R.id.tv_distri_month);
		tv_distri_year = (TextView) findViewById(R.id.tv_distri_year);

		ll_distri_week = (LinearLayout) findViewById(R.id.ll_distri_week);
		ll_distri_month = (LinearLayout) findViewById(R.id.ll_distri_month);
		ll_distri_year = (LinearLayout) findViewById(R.id.ll_distri_year);

		trend_graph = (TrendGraph) findViewById(R.id.trend_graph);
		sc_trend_graph = (HorizontalScrollView) findViewById(R.id.sc_trend_graph);

		LinearLayout ll_list_back = (LinearLayout) findViewById(R.id.ll_list_back);
		tv_list_title = (TextView) findViewById(R.id.tv_list_title);
		tv_trend_date = (TextView) findViewById(R.id.tv_trend_date);
		tv_list_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.03f));

		// ¦^¤W¤@­¶
		ll_list_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				try {
					ChangeView.onBack();
				} catch (Exception e) {
					finish();
				}

			}
		});

		ViewTreeObserver vto2 = trend_graph.getViewTreeObserver();
		vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				trend_graph.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				iBpHeight = trend_graph.getHeight();
				iBpWidth = trend_graph.getWidth();
				Log.d("nick", String.format("w=%d,  h=%d", iBpWidth, iBpHeight));

			}
		});

		morning.setOnSizeChangedListener(new OnSizeChangedListener() {

			@Override
			public void onSizeChanged(int w, int h, int oldw, int oldh) {
				// TODO Auto-generated method stub
				int size = morning.getSize();

				Log.d("alex", "morning.getSize()=" + size);
				// if(compareInt==0)
				// {
				// compareInt=size;
				// }
				// else
				// {
				// if(compareInt>size)
				// {
				// evening.setSizeDirect(size);
				// compareInt=-1;
				// }
				// else if(compareInt<size)
				// {
				// morning.setSizeDirect(compareInt);
				// compareInt=-1;
				// }
				// }
				int minsize = 1000;
				int minplace = 0;
				compareInt[0] = size;
				compareIntCount++;
				if (compareIntCount == 4) {
					for (int x = 0; x < 4; x++)
						if (compareInt[x] < minsize)
							minsize = compareInt[x];
					morning.setSizeDirect(minsize);
					daytime.setSizeDirect(minsize);
					evening.setSizeDirect(minsize);
					night.setSizeDirect(minsize);
					Log.d("alex", "morning  minsize" + minsize);
				}
			}

		});

		daytime.setOnSizeChangedListener(new OnSizeChangedListener() {

			@Override
			public void onSizeChanged(int w, int h, int oldw, int oldh) {
				// TODO Auto-generated method stub
				int size = daytime.getSize();
				Log.d("alex", "daytime.getSize()=" + size);
				// if (compareInt == 0) {
				// compareInt = size;
				// } else {
				// if (compareInt > size) {
				// evening.setSizeDirect(size);
				// compareInt = -1;
				// } else if (compareInt < size) {
				// morning.setSizeDirect(compareInt);
				// compareInt = -1;
				// }

				int minsize = 1000;
				int minplace = 0;
				compareInt[1] = size;
				compareIntCount++;
				if (compareIntCount == 4) {
					for (int x = 0; x < 4; x++)
						if (compareInt[x] < minsize)
							minsize = compareInt[x];
					morning.setSizeDirect(minsize);
					daytime.setSizeDirect(minsize);
					evening.setSizeDirect(minsize);
					night.setSizeDirect(minsize);
					Log.d("alex", "daytime  minsize" + minsize);
				}
			}
		});
		evening.setOnSizeChangedListener(new OnSizeChangedListener() {

			@Override
			public void onSizeChanged(int w, int h, int oldw, int oldh) {

				// TODO Auto-generated method stub
				int size = evening.getSize();
				Log.d("alex", "text_average.getSize()=" + size);
				// if (compareInt == 0) {
				// compareInt = size;
				// } else {
				// if (compareInt > size) {
				// morning.setSizeDirect(size);
				// compareInt = -1;
				// } else if (compareInt < size) {
				// evening.setSizeDirect(compareInt);
				// compareInt = -1;
				// }
				// }
				int minsize = 1000;
				int minplace = 0;
				compareInt[2] = size;
				compareIntCount++;
				if (compareIntCount == 4) {
					for (int x = 0; x < 4; x++)
						if (compareInt[x] < minsize)
							minsize = compareInt[x];
					morning.setSizeDirect(minsize);
					daytime.setSizeDirect(minsize);
					evening.setSizeDirect(minsize);
					night.setSizeDirect(minsize);
					Log.d("alex", "eveing  minsize" + minsize);
				}

			}

		});
		night.setOnSizeChangedListener(new OnSizeChangedListener() {

			@Override
			public void onSizeChanged(int w, int h, int oldw, int oldh) {
				// TODO Auto-generated method stub
				int size = night.getSize();
				Log.d("alex", "night.getSize()=" + size);
				// if (compareInt == 0) {
				// compareInt = size;
				// } else {
				// if (compareInt > size) {
				// evening.setSizeDirect(size);
				// compareInt = -1;
				// } else if (compareInt < size) {
				// morning.setSizeDirect(compareInt);
				// compareInt = -1;
				// }
				// }
				int minsize = 1000;
				int minplace = 0;
				compareInt[3] = size;
				compareIntCount++;
				if (compareIntCount == 4) {
					for (int x = 0; x < 4; x++)
						if (compareInt[x] < minsize)
							minsize = compareInt[x];
					morning.setSizeDirect(minsize);
					daytime.setSizeDirect(minsize);
					evening.setSizeDirect(minsize);
					night.setSizeDirect(minsize);
					Log.d("alex", "night  minsize" + minsize);
				}
			}

		});

	}

	void calculate() {
		int iaSysBP[][] = new int[31][4];
		int iaDiaBP[][] = new int[31][4];
		int iaPulse[][] = new int[31][4];
		/*
		 * for(int i=0; i<31; i++) { for(int j=0; j<4; j++) {
		 * iaMaxBP[i][j]=110+j*5+i; iaMinBP[i][j]=70+j*7+i;
		 * iaPulse[i][j]=65+j*3+i; } }
		 */

		for (int i = 0; i < 31; i++) {//init
			for (int j = 0; j < 4; j++) {
				iaSysBP[i][j] = iaDiaBP[i][j] = iaPulse[i][j] = 0;
			}
		}

		trend_graph.setPeriod(iPeriod);

		SharedPreferences settings = getSharedPreferences(AnalysisView.ANALYSIS_SETTING, 0);
		trend_graph.baPeriodEnabled[0] = baPeriodEnabled[0] = settings.getBoolean(AnalysisView.ANA_MORNING, true);
		trend_graph.baPeriodEnabled[1] = baPeriodEnabled[1] = settings.getBoolean(AnalysisView.ANA_DAYTIME, true);
		trend_graph.baPeriodEnabled[2] = baPeriodEnabled[2] = settings.getBoolean(AnalysisView.ANA_EVENING, true);
		trend_graph.baPeriodEnabled[3] = baPeriodEnabled[3] = settings.getBoolean(AnalysisView.ANA_NIGHT, true);

		Calendar calendar = Calendar.getInstance();
		Calendar calendar1 = Calendar.getInstance();
		calendar.setTime(dateFrom);
		calendar1.setTime(dateFrom);

		switch (iPeriod) {
		case AnalysisView.YEAR:
			calendar1.add(Calendar.YEAR, 1);
			break;
		case AnalysisView.MONTH:
			calendar1.add(Calendar.MONTH, 1);
			break;
		case AnalysisView.WEEK:
			calendar1.add(Calendar.DATE, 7);
			break;
		}
		Log.d("nick", "target=" + calendar1.getTime().toLocaleString());

		trend_graph.iSepPos = -1;
		int iDay = 0;
		if (iPeriod != AnalysisView.YEAR && calendar.get(Calendar.DAY_OF_MONTH) == 1) {
			trend_graph.iSepPos = 0;
			trend_graph.sSepText = formatMonth(calendar.get(Calendar.MONTH));
			// trend_graph.sSepText=saMonthText[calendar.get(Calendar.MONTH)];
		}

		while (calendar.before(calendar1)) {
			// Log.d("nick", calendar.getTime().toLocaleString());
			if (iPeriod != AnalysisView.YEAR) {
				trend_graph.saBarText[iDay] = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
			}
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			iDay++;

			if (iPeriod != AnalysisView.YEAR) {
				if (calendar.get(Calendar.DAY_OF_MONTH) == 1 && trend_graph.iSepPos == -1) {
					trend_graph.iSepPos = iDay;
					trend_graph.sSepText = formatMonth(calendar.get(Calendar.MONTH));
					// trend_graph.sSepText=saMonthText[calendar.get(Calendar.MONTH)];
				}
			}
		}

		if (iPeriod == AnalysisView.YEAR) {
			trend_graph.iDataCount = 12;
			calendar.setTime(dateFrom);
			for (int i = 0; i < 12; i++) {
				if (calendar.get(Calendar.MONTH) == 0) {
					trend_graph.iSepPos = i;
					trend_graph.sSepText = String.valueOf(calendar.get(Calendar.YEAR));
				}
				trend_graph.saBarText[i] = formatMonth(calendar.get(Calendar.MONTH));
				// trend_graph.saBarText[i]=saMonthText[calendar.get(Calendar.MONTH)];
				calendar.add(Calendar.MONTH, 1);
			}
		} else {
			trend_graph.iDataCount = iDay;
		}

		calendar.setTime(dateFrom);
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		if (iPeriod == AnalysisView.YEAR)
			dateFormat.applyPattern("yyMM");
		else
			dateFormat.applyPattern("yyMMdd");
		String sYMD = dateFormat.format(dateFrom).toString();

		int iaRangeCnt[] = { 0, 0, 0, 0 };
		int iRange;
		int iDayIdx = 0;
		recordList = databaseHelper.getRecords(dateFrom, iPeriod);
		for (int i = 0; i < recordList.size(); i++) {
			RecordList rec = recordList.get(i);

			if (rec.AnalysisType != RecordsView.TYPE_BP || rec.BPMNoiseFlag != 0)
				continue;

			int iHr = Integer.valueOf(rec.sDatetime.substring(6, 8));
			if (iHr >= 5 && iHr < 10)
				iRange = 0;
			else if (iHr >= 10 && iHr < 18)
				iRange = 1;
			else if (iHr >= 18 && iHr < 21)
				iRange = 2;
			else
				iRange = 3;

			String step;
			if (iPeriod == AnalysisView.YEAR)
				step = rec.sDatetime.substring(0, 4);
			else
				step = rec.sDatetime.substring(0, 6);

			// Log.d("nick",
			// "record ="+rec.sDatetime+", sYMD="+sYMD+", equals="+step.equals(sYMD));
			if (!step.equals(sYMD)) {
				for (int j = 0; j < 4; j++) {
					if (iaRangeCnt[j] > 0) {
						iaSysBP[iDayIdx][j] /= iaRangeCnt[j];
						iaDiaBP[iDayIdx][j] /= iaRangeCnt[j];
						iaPulse[iDayIdx][j] /= iaRangeCnt[j];
					}
					iaRangeCnt[j] = 0;
				}

				while (!step.equals(sYMD) && calendar.before(calendar1)) {
					if (iPeriod == AnalysisView.YEAR)
						calendar.add(Calendar.MONTH, 1);
					else
						calendar.add(Calendar.DAY_OF_MONTH, 1);

					sYMD = dateFormat.format(calendar.getTime()).toString();
					iDayIdx++;
					// Log.d("nick", sYMD);
				}
			}

			iaSysBP[iDayIdx][iRange] += rec.HighBloodPressure;
			iaDiaBP[iDayIdx][iRange] += rec.LowBloodPressure;
			iaPulse[iDayIdx][iRange] += rec.BPHeartRate;
			// iaPulse[iDayIdx][iRange]+=rec.HeartRate;
			iaRangeCnt[iRange]++;
			Log.d("nick", "[" + rec.sDatetime + "] Sys=" + rec.HighBloodPressure + ", Dia=" + rec.LowBloodPressure + ", Pulse=" + rec.BPHeartRate);

		}

		for (int j = 0; j < 4; j++) {
			if (iaRangeCnt[j] > 0) {
				iaSysBP[iDayIdx][j] /= iaRangeCnt[j];
				iaDiaBP[iDayIdx][j] /= iaRangeCnt[j];
				iaPulse[iDayIdx][j] /= iaRangeCnt[j];
			}
		}

		for (int i = 0; i < trend_graph.iDataCount; i++) {
			for (int j = 0; j < 4; j++) {
				trend_graph.iaSysBP[i][j] = iaSysBP[i][j];
				trend_graph.iaDiaBP[i][j] = iaDiaBP[i][j];
				trend_graph.iaPulse[i][j] = iaPulse[i][j];
				// Log.d("nick",
				// "Sys="+iaSysBP[i][j]+", Dia="+iaDiaBP[i][j]+", Pulse="+iaPulse[i][j]);
			}
		}
	}

	void drawButton() {
		if (iPeriod == AnalysisView.YEAR) {
			ll_distri_week.setBackgroundResource(0);
			tv_distri_week.setTextColor(0xff959595);

			ll_distri_month.setBackgroundResource(0);
			tv_distri_month.setTextColor(0xff959595);

			ll_distri_year.setBackgroundResource(R.drawable.distri_button);
			tv_distri_year.setTextColor(Color.WHITE);

		} else if (iPeriod == AnalysisView.MONTH) {
			ll_distri_week.setBackgroundResource(0);
			tv_distri_week.setTextColor(0xff959595);

			ll_distri_month.setBackgroundResource(R.drawable.distri_button);
			tv_distri_month.setTextColor(Color.WHITE);

			ll_distri_year.setBackgroundResource(0);
			tv_distri_year.setTextColor(0xff959595);
		} else {
			ll_distri_week.setBackgroundResource(R.drawable.distri_button);
			tv_distri_week.setTextColor(Color.WHITE);

			ll_distri_month.setBackgroundResource(0);
			tv_distri_month.setTextColor(0xff959595);

			ll_distri_year.setBackgroundResource(0);
			tv_distri_year.setTextColor(0xff959595);
		}

	}

	void showResult() {
		drawButton();

		trend_graph.invalidate();
	}

	public void onPeriodClick(View v) {
		iBpWidth = sc_trend_graph.getWidth();
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) trend_graph.getLayoutParams();

		switch (v.getId()) {
		case R.id.tv_distri_week:
			iPeriod = AnalysisView.WEEK;
			params.width = iBpWidth;
			break;
		case R.id.tv_distri_month:
			iPeriod = AnalysisView.MONTH;
			params.width = iBpWidth * 2;
			break;
		case R.id.tv_distri_year:
			iPeriod = AnalysisView.YEAR;
			params.width = iBpWidth;
			break;
		}
		trend_graph.setLayoutParams(params);

		calculate();
		showResult();

		Log.d("nick", "iPeriod=" + iPeriod);
	}

}
