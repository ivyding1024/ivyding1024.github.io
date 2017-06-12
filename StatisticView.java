package com.mdbiomedical.app.vion.vian_health.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StatisticView extends Activity {
	TextView tv_list_title, tv_statistic_date;
	TextView tv_stat_diff_sys,tv_stat_diff_dia, tv_stat_avg_sys,tv_stat_avg_dia;
	ImageView iv_stat_bp, iv_stat_pulse;
	LinearLayout ll_distri_week, ll_distri_month, ll_distri_year;
	TextView tv_distri_total, tv_distri_week, tv_distri_month, tv_distri_year;
	AutoResizeTextView text_difference, text_average;
	List<RecordList> recordList = new ArrayList<RecordList>();
	DatabaseHelper databaseHelper = new DatabaseHelper(this);
	int compareInt = 0;
	int iBpHeight, iBpWidth;
	int iPulseHeight, iPulseWidth;

	SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd,yyyy", java.util.Locale.getDefault());

	String dateString = DateFormat.getBestDateTimePattern(java.util.Locale.getDefault(), "yyyy/MM/dd");
	java.text.DateFormat dateTimeInstance = new SimpleDateFormat(dateString, java.util.Locale.getDefault());
	Paint myPaint = new Paint();

	final int WEEK = 0, MONTH = 1, YEAR = 2;
	int iPeriod = WEEK;
	Date dateFrom = new Date();

	final int iaPeriod[] = { Color.rgb(82, 170, 205), Color.rgb(43, 92, 144), Color.rgb(198, 94, 55), Color.rgb(143, 56, 45) };

	boolean baPeriodEnabled[] = { true, true, true, true };
	int iaSysBP[] = { 0, 0, 0, 0 };
	int iaDiaBP[] = { 0, 0, 0, 0 };
	int iaPulse[] = { 0, 0, 0, 0 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistic_activity);

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
		tv_statistic_date.setText(getResources().getString(R.string.datefrom) + ": " + dateTimeInstance.format(dateFrom));
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
				
		tv_distri_week = (TextView) findViewById(R.id.tv_distri_week);
		tv_distri_month = (TextView) findViewById(R.id.tv_distri_month);
		tv_distri_year = (TextView) findViewById(R.id.tv_distri_year);

		ll_distri_week = (LinearLayout) findViewById(R.id.ll_distri_week);
		ll_distri_month = (LinearLayout) findViewById(R.id.ll_distri_month);
		ll_distri_year = (LinearLayout) findViewById(R.id.ll_distri_year);

		tv_stat_diff_sys = (TextView) findViewById(R.id.tv_stat_diff_sys);
		tv_stat_diff_dia = (TextView) findViewById(R.id.tv_stat_diff_dia);
		tv_stat_avg_sys = (TextView) findViewById(R.id.tv_stat_avg_sys);
		tv_stat_avg_dia = (TextView) findViewById(R.id.tv_stat_avg_dia);

		text_difference = (AutoResizeTextView) findViewById(R.id.text_difference);
		text_average = (AutoResizeTextView) findViewById(R.id.text_average);
		// Log.d("alex", "text_difference.getSize()=" +
		// text_difference.getSize() + "       ;text_average.getSize()=" +
		// text_average.getSize());

		text_difference.setOnSizeChangedListener(new OnSizeChangedListener() {

			@Override
			public void onSizeChanged(int w, int h, int oldw, int oldh) {
				// TODO Auto-generated method stub
				int size = text_difference.getSize();
				Log.d("alex", "text_difference.getSize()=" + size);
				if (compareInt == 0) {
					compareInt = size;
				} else {
					if (compareInt > size) {
						text_average.setSizeDirect(size);
						compareInt = -1;
					} else if (compareInt < size) {
						text_difference.setSizeDirect(compareInt);
						compareInt = -1;
					}
				}
			}

		});

		text_average.setOnSizeChangedListener(new OnSizeChangedListener() {

			@Override
			public void onSizeChanged(int w, int h, int oldw, int oldh) {

				// TODO Auto-generated method stub
				int size = text_average.getSize();
				Log.d("alex", "text_average.getSize()=" + size);
				if (compareInt == 0) {
					compareInt = size;
				} else {
					if (compareInt > size) {
						text_difference.setSizeDirect(size);
						compareInt = -1;
					} else if (compareInt < size) {
						text_average.setSizeDirect(compareInt);
						compareInt = -1;
					}
				}

			}

		});

		// text_difference.setTextSize(1);
		// setTextSameSize(text_difference,text_average);

		iv_stat_bp = (ImageView) findViewById(R.id.iv_stat_bp);
		iv_stat_pulse = (ImageView) findViewById(R.id.iv_stat_pulse);

		LinearLayout ll_list_back = (LinearLayout) findViewById(R.id.ll_list_back);
		tv_list_title = (TextView) findViewById(R.id.tv_list_title);
		tv_statistic_date = (TextView) findViewById(R.id.tv_statistic_date);
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

		ViewTreeObserver vto2 = iv_stat_bp.getViewTreeObserver();
		vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				iv_stat_bp.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				iBpHeight = iv_stat_bp.getHeight();
				iBpWidth = iv_stat_bp.getWidth();
				// Log.d("nick", String.format("w=%d,  h=%d", iBpWidth,
				// iBpHeight));

			}
		});

		ViewTreeObserver vto1 = iv_stat_pulse.getViewTreeObserver();
		vto1.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				iv_stat_pulse.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				iPulseHeight = iv_stat_pulse.getHeight();
				iPulseWidth = iv_stat_pulse.getWidth();
				// Log.d("nick", String.format("w=%d,  h=%d", iPulseWidth,
				// iPulseHeight));

			}
		});

	}

	void setTextSameSize(AutoResizeTextView T1, AutoResizeTextView T2) {
		int t1Size = (int) T1.getTextSize() + 1;

		int t2Size = (int) T2.getTextSize() + 1;
		Log.d("alex", "t1Size=" + t1Size + "       ;t2Size=" + t2Size);
		if (t1Size > t2Size) {
			T1.setTextSize(TypedValue.COMPLEX_UNIT_PX, t2Size);
		} else if (t1Size < t2Size) {
			T2.setTextSize(TypedValue.COMPLEX_UNIT_PX, t1Size);
		}

	}

	void calculate() {

		SharedPreferences settings = getSharedPreferences(AnalysisView.ANALYSIS_SETTING, 0);
		baPeriodEnabled[0] = settings.getBoolean(AnalysisView.ANA_MORNING, true);
		baPeriodEnabled[1] = settings.getBoolean(AnalysisView.ANA_DAYTIME, true);
		baPeriodEnabled[2] = settings.getBoolean(AnalysisView.ANA_EVENING, true);
		baPeriodEnabled[3] = settings.getBoolean(AnalysisView.ANA_NIGHT, true);

		for (int j = 0; j < iaSysBP.length; j++) {
			iaSysBP[j] = iaDiaBP[j] = iaPulse[j] = 0;
		}

		int iaRangeCnt[] = { 0, 0, 0, 0 };
		int iRange;
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

			// Log.d("nick",
			// "record ="+rec.sDatetime+", ["+rec.HighBloodPressure+","+rec.LowBloodPressure+","+rec.BPHeartRate+"]");

			iaSysBP[iRange] += rec.HighBloodPressure;
			iaDiaBP[iRange] += rec.LowBloodPressure;
			iaPulse[iRange] += rec.BPHeartRate;
			// iaPulse[iDayIdx][iRange]+=rec.HeartRate;
			iaRangeCnt[iRange]++;
		}

		for (int j = 0; j < 4; j++) {
			if (iaRangeCnt[j] > 0) {
				iaSysBP[j] /= iaRangeCnt[j];
				iaDiaBP[j] /= iaRangeCnt[j];
				iaPulse[j] /= iaRangeCnt[j];
			}
		}
	}

	void drawButton() {
		if (iPeriod == YEAR) {
			ll_distri_week.setBackgroundResource(0);
			tv_distri_week.setTextColor(0xff959595);

			ll_distri_month.setBackgroundResource(0);
			tv_distri_month.setTextColor(0xff959595);

			ll_distri_year.setBackgroundResource(R.drawable.distri_button);
			tv_distri_year.setTextColor(Color.WHITE);

		} else if (iPeriod == MONTH) {
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
		Typeface tf = Typeface.createFromAsset(this.getAssets(),  "fonts/FuturaCondensed.ttf");
		if (iaSysBP[0] == 0 || iaSysBP[2] == 0) {
			tv_stat_diff_sys.setText("-");
			tv_stat_diff_dia.setText("-");
			tv_stat_avg_sys.setText("-");
			tv_stat_avg_dia.setText("-");
		} else {
			tv_stat_diff_sys.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.06f));
			tv_stat_diff_dia.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.06f));
			tv_stat_avg_sys.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.06f));
			tv_stat_avg_dia.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.06f));
			tv_stat_diff_sys.setTypeface(tf);
			tv_stat_diff_dia.setTypeface(tf);
			tv_stat_avg_sys.setTypeface(tf);
			tv_stat_avg_dia.setTypeface(tf);
			tv_stat_diff_sys.setText(String.valueOf(iaSysBP[0] - iaSysBP[2]));
			tv_stat_diff_dia.setText(String.valueOf(iaDiaBP[0] - iaDiaBP[2]));
			tv_stat_avg_sys.setText(String.valueOf((int) ((iaSysBP[0] + iaSysBP[2]) / 2)));
			tv_stat_avg_dia.setText(String.valueOf((int) ((iaDiaBP[0] + iaDiaBP[2]) / 2)));
			
		}

		drawBPGraph();
		drawPulseGraph();
	}

	public void onPeriodClick(View v) {
		switch (v.getId()) {
		case R.id.tv_distri_week:
			iPeriod = WEEK;
			break;
		case R.id.tv_distri_month:
			iPeriod = MONTH;
			break;
		case R.id.tv_distri_year:
			iPeriod = YEAR;
			break;
		}

		calculate();
		showResult();

		// Log.d("nick", "iPeriod="+iPeriod);
	}

	void drawBPGraph() {
		Bitmap tempBitmap = Bitmap.createBitmap(iBpWidth, iBpHeight, Bitmap.Config.RGB_565);

		final float fGridWidth = iBpWidth * 120.5f / 552;
		float fLeft = iBpWidth * 74 / 552;

		Canvas tempCanvas = new Canvas(tempBitmap);

		myPaint.setPathEffect(null);
		myPaint.setColor(Color.rgb(255, 255, 255));
		myPaint.setStyle(Paint.Style.FILL);
		tempCanvas.drawRect(new RectF(0, 0, iBpWidth, iBpHeight), myPaint);

		myPaint.setColor(Color.rgb(173, 173, 173));
		myPaint.setStrokeWidth(2);
		tempCanvas.drawLine(fLeft, 0, fLeft, iBpHeight, myPaint);

		for (int i = 1; i < 4; i++) {
			// if (i == 1 || i == 3)
			// continue;
			tempCanvas.drawLine(fLeft + i * fGridWidth, 0, fLeft + i * fGridWidth, iBpHeight, myPaint);

		}
		myPaint.setTextAlign(Paint.Align.LEFT);
		tempCanvas.save();
		myPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		myPaint.setStrokeWidth(1);
		myPaint.setTextSize((int) (DeviceConstant.screenHeight * 0.012f));
		float fTextW = myPaint.measureText(getResources().getString(R.string.bp_mmhg));
		tempCanvas.rotate(-90, 20, (iBpHeight - fTextW) / 2 + fTextW);
		tempCanvas.drawText(getResources().getString(R.string.bp_mmhg), 20, (iBpHeight - fTextW) / 2 + fTextW + 10, myPaint);
		tempCanvas.restore();

		final int iUpBP = 200, iLowBP = 40, iBpStep = 20;
		int iGridCnt = (200 - iLowBP) / iBpStep;
		float fHeightPerStep = iBpHeight / ((iUpBP - iLowBP) / iBpStep);

		myPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		myPaint.setStrokeWidth(2);
		myPaint.setTextSize((int) (DeviceConstant.screenHeight * 0.017f));
		myPaint.setTextAlign(Paint.Align.RIGHT);
		Rect bounds = new Rect();
		myPaint.getTextBounds("1", 0, 1, bounds);
		int height = bounds.height();
		for (int j = 1; j < iGridCnt; j++) {
			tempCanvas.drawText(String.valueOf(iUpBP - j * iBpStep), fLeft - 10, fHeightPerStep * j + height / 2, myPaint);
		}

		myPaint.setColor(Color.rgb(173, 173, 173));
		myPaint.setStrokeWidth(2);
		myPaint.setStyle(Style.STROKE);
		myPaint.setPathEffect(new DashPathEffect(new float[] { 5, 10 }, 0));
		for (int j = 1; j < iGridCnt; j++) {
			tempCanvas.drawLine(fLeft, fHeightPerStep * j, iBpWidth, fHeightPerStep * j, myPaint);
		}

		float fBarW = (float) iBpWidth * 17 / 555;

		myPaint.setTextAlign(Paint.Align.CENTER);
		myPaint.setPathEffect(null);

		for (int i = 0; i < 4; i++) {
			if (baPeriodEnabled[i] == false)
				continue;

			myPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			myPaint.setColor(iaPeriod[i]);
			float fMax = (iUpBP - iaSysBP[i]) * fHeightPerStep / 20 + fBarW;
			float fMin = (iUpBP - iaDiaBP[i]) * fHeightPerStep / 20 - fBarW;
			if (iaSysBP[i] < iaDiaBP[i]) {
				fMax = fMin + fBarW;
			}
			if (iaDiaBP[i] < iLowBP)
				iaDiaBP[i] = iLowBP;

			if (iaSysBP[i] > iLowBP) {
				// tempCanvas.drawRect(fLeft + i * fGridWidth + fGridWidth / 2 -
				// fBarW, fMax, fLeft + i * fGridWidth + fGridWidth / 2 + fBarW,
				// fMin, myPaint);
				// tempCanvas.drawCircle(fLeft + i * fGridWidth + fGridWidth /
				// 2, fMax, fBarW, myPaint);
				// tempCanvas.drawCircle(fLeft + i * fGridWidth + fGridWidth /
				// 2, fMin, fBarW, myPaint);

				tempCanvas.drawRect(fLeft + i * fGridWidth + fGridWidth / 2 - fBarW, fMax, fLeft + i * fGridWidth + fGridWidth / 2 + fBarW, fMin, myPaint);
				tempCanvas.drawCircle(fLeft + i * fGridWidth + fGridWidth / 2, fMax, fBarW, myPaint);
				tempCanvas.drawCircle(fLeft + i * fGridWidth + fGridWidth / 2, fMin, fBarW, myPaint);
				myPaint.setColor(Color.rgb(20, 20, 20));
				bounds = new Rect();
				myPaint.getTextBounds("1", 0, 1, bounds);
				height = bounds.height();

				// tempCanvas.drawText(String.valueOf(iaSysBP[i]), fLeft + i *
				// fGridWidth + fGridWidth / 2, fMax - fBarW - 10, myPaint);
				// tempCanvas.drawText(String.valueOf(iaDiaBP[i]), fLeft + i *
				// fGridWidth + fGridWidth / 2, fMin + fBarW + height + 10,
				// myPaint);

				tempCanvas.drawText(String.valueOf(iaSysBP[i]), fLeft + i * fGridWidth + fGridWidth / 2, fMax - fBarW - 20, myPaint);
				float a =fMax-fBarW-(fMax - fBarW - 10);
				
				float b = fMin + fBarW + height + 10-(fMin + fBarW);
				Log.e("text location", i+". iaSysBP = "+ a +" iaDiaBP = "+b);
				Log.d("alex", "iaSysBP" + String.valueOf(iaSysBP[i]));
				tempCanvas.drawText(String.valueOf(iaDiaBP[i]), fLeft + i * fGridWidth + fGridWidth / 2, fMin + fBarW + height + 20, myPaint);
				Log.d("alex", "iaDiaBP" + String.valueOf(iaDiaBP[i]));
			}
		}

		// Attach the canvas to the ImageView
		iv_stat_bp.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));

	}

	void drawPulseGraph() {
		Bitmap tempBitmap = Bitmap.createBitmap(iPulseWidth, iPulseHeight, Bitmap.Config.RGB_565);

		final float fGridWidth = iPulseWidth * 120.5f / 552;
		float fLeft = iPulseWidth * 74 / 552;

		int iMax = 0, iMin = 300;
		for (int i = 0; i < 4; i++) {
			if (baPeriodEnabled[i] == false)
				continue;

			if (iMax < iaPulse[i])
				iMax = iaPulse[i];
			if (iMin > iaPulse[i])
				iMin = iaPulse[i];
		}

		if (iMin == 0)
			iMin = 60;
		if (iMax == 0)
			iMax = 80;

		Canvas tempCanvas = new Canvas(tempBitmap);

		myPaint.setPathEffect(null);
		myPaint.setColor(Color.rgb(255, 255, 255));
		myPaint.setStyle(Paint.Style.FILL);
		tempCanvas.drawRect(new RectF(0, 0, iPulseWidth, iPulseHeight), myPaint);

		myPaint.setColor(Color.rgb(173, 173, 173));
		myPaint.setStrokeWidth(2);
		tempCanvas.drawLine(fLeft, 0, fLeft, iPulseHeight, myPaint);

		for (int i = 1; i < 4; i++) {
			// if (i == 1 || i == 3)
			// continue;
			tempCanvas.drawLine(fLeft + i * fGridWidth, 0, fLeft + i * fGridWidth, iPulseHeight, myPaint);
		}
		myPaint.setTextAlign(Paint.Align.LEFT);
		tempCanvas.save();
		myPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		myPaint.setStrokeWidth(1);
		myPaint.setTextSize((int) (DeviceConstant.screenHeight * 0.012f));
		float fTextW = myPaint.measureText(getResources().getString(R.string.pulse_bpm));
		tempCanvas.rotate(-90, 20, (iPulseHeight - fTextW) / 2 + fTextW);
		tempCanvas.drawText(getResources().getString(R.string.pulse_bpm), 20, (iPulseHeight - fTextW) / 2 + fTextW + 10, myPaint);
		// Log.d("alex","A="+(iPulseHeight - fTextW) / 2 +
		// fTextW+"fLeft - 10="+(fLeft - 10));
		tempCanvas.restore();

		final int iPulseStep = 20;
		int iUpPulse = ((int) (iMax / iPulseStep) + 3) * iPulseStep, iLowPulse = ((int) (iMin / iPulseStep) - 1) * iPulseStep;
		int iGridCnt = (iUpPulse - iLowPulse) / iPulseStep;
		float fHeightPerStep = iPulseHeight / iGridCnt;

		myPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		myPaint.setStrokeWidth(2);
		myPaint.setTextSize((int) (DeviceConstant.screenHeight * 0.017f));
		myPaint.setTextAlign(Paint.Align.RIGHT);
		Rect bounds = new Rect();
		myPaint.getTextBounds("1", 0, 1, bounds);
		int height = bounds.height();
		for (int j = 1; j < iGridCnt; j++) {
			tempCanvas.drawText(String.valueOf(iUpPulse - j * iPulseStep), fLeft - 10, fHeightPerStep * j + height / 2, myPaint);

		}

		myPaint.setColor(Color.rgb(173, 173, 173));
		myPaint.setStrokeWidth(2);
		myPaint.setStyle(Style.STROKE);
		myPaint.setPathEffect(new DashPathEffect(new float[] { 5, 10 }, 0));
		for (int j = 1; j < iGridCnt; j++) {
			tempCanvas.drawLine(fLeft, fHeightPerStep * j, iPulseWidth, fHeightPerStep * j, myPaint);
		}

		float fBarW = (float) iPulseWidth * 17 / 555;

		myPaint.setTextAlign(Paint.Align.CENTER);

		for (int i = 0; i < 4; i++) {
			if (baPeriodEnabled[i] == false)
				continue;
			if (iaPulse[i] == 0) {
				continue;
			}
			myPaint.setPathEffect(null);
			myPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			myPaint.setColor(iaPeriod[i]);
			float fMax = (iUpPulse - iaPulse[i]) * fHeightPerStep / 20;
			// tempCanvas.drawCircle(fLeft + i * fGridWidth + fGridWidth / 2,
			// fMax, fBarW, myPaint);
			tempCanvas.drawCircle(fLeft + i * fGridWidth + fGridWidth / 2, fMax, fBarW, myPaint);
			myPaint.setColor(Color.rgb(20, 20, 20));
			bounds = new Rect();
			myPaint.getTextBounds("1", 0, 1, bounds);
			height = bounds.height();

			// tempCanvas.drawText(String.valueOf(iaPulse[i]), fLeft + i *
			// fGridWidth + fGridWidth / 2, fMax - fBarW - 10, myPaint);
			tempCanvas.drawText(String.valueOf(iaPulse[i]), fLeft + i * fGridWidth + fGridWidth / 2, fMax - fBarW - 10, myPaint);

		}

		// Attach the canvas to the ImageView
		iv_stat_pulse.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));

	}

}
