package com.mdbiomedical.app.vion.vian_health.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.mdbiomedical.app.vion.vian_health.R;
import com.mdbiomedical.app.vion.vian_health.helper.DatabaseHelper;
import com.mdbiomedical.app.vion.vian_health.model.RecordList;
import com.mdbiomedical.app.vion.vian_health.util.ChangeView;
import com.mdbiomedical.app.vion.vian_health.util.DeviceConstant;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
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

public class DistributionActivity extends Activity {
	TextView tv_list_title,tv_distri_date;
	ImageView iva_distri_iv[] = new ImageView[6];
	TextView tv_distri_total, tv_distri_week, tv_distri_month, tv_distri_year;
	LinearLayout ll_distri_week, ll_distri_month, ll_distri_year;
	TextView tva_distri_t[] = new TextView[6];
	TextView tva_distri_p[] = new TextView[6];
	TextView tva_distri_c[] = new TextView[6];

	int iResID_iv[] = { R.id.iv_distri_optimal, R.id.iv_distri_normal, R.id.iv_distri_high_normal, R.id.iv_distri_grade1, R.id.iv_distri_grade2, R.id.iv_distri_grade3 };
	int iResID_t[] = { R.id.tv_distri_optimal_t, R.id.tv_distri_n_t, R.id.tv_distri_hn_t, R.id.tv_distri_grade1_t, R.id.tv_distri_grade2_t, R.id.tv_distri_grade3_t };
	int iResID_p[] = { R.id.tv_distri_optimal_p, R.id.tv_distri_normal_p, R.id.tv_distri_high_normal_p, R.id.tv_distri_grade1_p, R.id.tv_distri_grade2_p, R.id.tv_distri_grade3_p };
	int iResID_c[] = { R.id.tv_distri_optimal_c, R.id.tv_distri_normal_c, R.id.tv_distri_high_normal_c, R.id.tv_distri_grade1_c, R.id.tv_distri_grade2_c, R.id.tv_distri_grade3_c };

	List<RecordList> recordList = new ArrayList<RecordList>();
	DatabaseHelper databaseHelper = new DatabaseHelper(this);

	int iGraphHeight, iGraphWidth;

	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", java.util.Locale.getDefault());

	String dateString = DateFormat.getBestDateTimePattern(java.util.Locale.getDefault(), "yyyy/MM/dd");
	java.text.DateFormat dateTimeInstance = new SimpleDateFormat(dateString, java.util.Locale.getDefault());
	Paint myPaint = new Paint();

	final int WEEK = 0, MONTH = 1, YEAR = 2;
	int iPeriod = WEEK;
	Date dateFrom = new Date();

	boolean baPeriodEnabled[] = { true, true, true, true };
	final int iaWho[] = { Color.rgb(123, 165, 67), Color.rgb(67, 152, 55), Color.rgb(59, 128, 57), Color.rgb(217, 163, 38), Color.rgb(211, 103, 30), Color.rgb(156, 31, 35) };

	int faWhoPercent[] = { 0, 0, 0, 0, 0, 0 };
	int iTotalCnt = 0;
	int iaWhoCnt[] = { 0, 0, 0, 0, 0, 0 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.distribution_activity);

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
		//tv_list_title.setText(dateTimeInstance.format(dateFrom));
		tv_distri_date.setText(getResources().getString(R.string.datefrom)+": "+dateTimeInstance.format(dateFrom));
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
		//
	private void init() {
		//sandy0914
				getWindowManager().getDefaultDisplay().getMetrics(dm);
				DeviceConstant.screenWidth = dm.widthPixels;
				DeviceConstant.screenHeight = dm.heightPixels;
				DeviceConstant.screenDPI = dm.densityDpi;
				
		tv_distri_total = (TextView) findViewById(R.id.tv_distri_total);
		tv_distri_week = (TextView) findViewById(R.id.tv_distri_week);
		tv_distri_month = (TextView) findViewById(R.id.tv_distri_month);
		tv_distri_year = (TextView) findViewById(R.id.tv_distri_year);

		ll_distri_week = (LinearLayout) findViewById(R.id.ll_distri_week);
		ll_distri_month = (LinearLayout) findViewById(R.id.ll_distri_month);
		ll_distri_year = (LinearLayout) findViewById(R.id.ll_distri_year);

		for (int i = 0; i < 6; i++) {
			iva_distri_iv[i] = (ImageView) findViewById(iResID_iv[i]);
			tva_distri_t[i] = (TextView) findViewById(iResID_t[i]);
			tva_distri_t[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.015f));
			tva_distri_p[i] = (TextView) findViewById(iResID_p[i]);
			tva_distri_p[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.035f));
			tva_distri_c[i] = (TextView) findViewById(iResID_c[i]);
		}

		LinearLayout ll_list_back = (LinearLayout) findViewById(R.id.ll_list_back);
		tv_list_title = (TextView) findViewById(R.id.tv_list_title);
		tv_distri_date = (TextView) findViewById(R.id.tv_distri_date);
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

		ViewTreeObserver vto2 = iva_distri_iv[0].getViewTreeObserver();
		vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				iva_distri_iv[0].getViewTreeObserver().removeOnGlobalLayoutListener(this);
				iGraphHeight = iva_distri_iv[0].getHeight();
				iGraphWidth = iva_distri_iv[0].getWidth();
				Log.d("Hr", String.format("w=%d,  h=%d", iGraphWidth, iGraphHeight));

			}
		});

	}

	void calculate() {

		SharedPreferences settings = getSharedPreferences(AnalysisView.ANALYSIS_SETTING, 0);
		baPeriodEnabled[0] = settings.getBoolean(AnalysisView.ANA_MORNING, true);
		baPeriodEnabled[1] = settings.getBoolean(AnalysisView.ANA_DAYTIME, true);
		baPeriodEnabled[2] = settings.getBoolean(AnalysisView.ANA_EVENING, true);
		baPeriodEnabled[3] = settings.getBoolean(AnalysisView.ANA_NIGHT, true);

		iTotalCnt = 0;
		for (int j = 0; j < iaWhoCnt.length; j++) {
			iaWhoCnt[j] = 0;
		}

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
			// "record ="+rec.sDatetime+", sYMD="+sYMD+", equals="+step.equals(sYMD));

			if (baPeriodEnabled[iRange] == false)
				continue;

			rec.WHOIndicate = (rec.WHOIndicate == 0 ? 1 : rec.WHOIndicate);
			if (rec.WHOIndicate >= 1 && rec.WHOIndicate <= 6)
				iaWhoCnt[rec.WHOIndicate - 1]++;
		}

		for (int j = 0; j < iaWhoCnt.length; j++)
			iTotalCnt += iaWhoCnt[j];

		int iMaxP = 0, iType = 0;
		for (int j = 0; j < iaWhoCnt.length; j++) {
			if (iTotalCnt > 0)
				faWhoPercent[j] = iaWhoCnt[j] * 100 / iTotalCnt;
			else
				faWhoPercent[j] = 0;
			if (faWhoPercent[j] > iMaxP) {
				iMaxP = faWhoPercent[j];
				iType = j;
			}
			Log.d("nick", "Total=" + iTotalCnt + ", cnt=" + iaWhoCnt[j] + ", Percent=" + faWhoPercent[j]);
		}

		if (iTotalCnt > 0) {
			faWhoPercent[iType] = 100;
			for (int j = 0; j < iaWhoCnt.length; j++) {
				if (j != iType)
					faWhoPercent[iType] -= faWhoPercent[j];
			}
		}
	}

	void drawPie(ImageView v, int type) {
		Bitmap tempBitmap = Bitmap.createBitmap(iGraphWidth, iGraphHeight, Bitmap.Config.RGB_565);

		Canvas tempCanvas = new Canvas(tempBitmap);
		float CIRCLE_CENTER_X = iGraphWidth / 2;
		float CIRCLE_CENTER_Y = iGraphHeight / 2;

		myPaint.setColor(Color.rgb(255, 255, 255));
		myPaint.setStyle(Paint.Style.FILL);
		tempCanvas.drawRect(new RectF(0, 0, iGraphWidth, iGraphHeight), myPaint);

		myPaint.setColor(Color.rgb(0xEB, 0xEB, 0xEB));
		myPaint.setStrokeWidth(3);
		// tempCanvas.drawLine(0, i, iGraphWidth, i, myPaint);
		float fRadius = (float) (Math.min(iGraphWidth, iGraphHeight) * 0.4);
		tempCanvas.drawCircle(CIRCLE_CENTER_X, CIRCLE_CENTER_Y, fRadius, myPaint);

		int fPercent = 0;
		for (int j = 0; j < 6; j++) {
			if (faWhoPercent[j] == 0)
				continue;

			myPaint.setColor(Color.WHITE);
			float fAngle = (float) ((fPercent) * Math.PI * 2 - 0.5 * Math.PI);
			tempCanvas.drawLine(CIRCLE_CENTER_X, CIRCLE_CENTER_Y, (float) (CIRCLE_CENTER_X + fRadius * Math.cos(fAngle)), (float) (CIRCLE_CENTER_Y + fRadius * Math.sin(fAngle)), myPaint);

			if (j == type) {
				myPaint.setColor(iaWho[j]);
				tempCanvas.drawArc(new RectF(CIRCLE_CENTER_X - fRadius, CIRCLE_CENTER_Y - fRadius, CIRCLE_CENTER_X + fRadius, CIRCLE_CENTER_Y + fRadius), fPercent * 360 / 100 - 90,
						(faWhoPercent[j] * 360 / 100), true, myPaint);
			}
			fPercent += faWhoPercent[j];
		}

		// Attach the canvas to the ImageView
		v.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));

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

		Log.d("nick", "iPeriod=" + iPeriod);
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

		tv_distri_total.setText(getResources().getString(R.string.distribution_total100)+" " + iTotalCnt);

		for (int i = 0; i < 6; i++) {
			tva_distri_p[i].setText(String.valueOf((int) (faWhoPercent[i])) + "%");
			tva_distri_c[i].setText("of " + String.valueOf(iaWhoCnt[i]));
			drawPie(iva_distri_iv[i], i);
		}

	}
}
