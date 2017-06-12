package com.mdbiomedical.app.vion.vian_health.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

import com.mdbiomedical.app.vion.vian_health.R;
import com.mdbiomedical.app.vion.vian_health.helper.DatabaseHelper;
import com.mdbiomedical.app.vion.vian_health.model.RecordList;
import com.mdbiomedical.app.vion.vian_health.util.ChangeView;
import com.mdbiomedical.app.vion.vian_health.util.DeviceConstant;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.BitmapFactory.Options;
import android.graphics.Paint.Style;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfDocument.Page;
import android.graphics.pdf.PdfDocument.PageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintAttributes.Margins;
import android.print.PrintAttributes.Resolution;
import android.print.pdf.PrintedPdfDocument;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class BpPdfChooseView extends Activity {
	WheelView year, month, day;

	TextView dateFromText;

	DatabaseHelper databaseHelper;
	SharedPreferences settings;
	LinearLayout ll_ana_sel_date;
	int lastYear;
	Calendar calendar;
	File file;
	Calendar birthDateCalendar;
	// bp use
	
	ProgressDialog.Builder builder;
	AlertDialog dialog;

	boolean baPeriodEnabled[] = { true, true, true, true };
	int gender_order;
	int iaSysBP[][] = new int[31][4];
	int iaDiaBP[][] = new int[31][4];
	int iaPulse[][] = new int[31][4];
	int iaSysBPStastic[] = { 0, 0, 0, 0 };
	int iaDiaBPStastic[] = { 0, 0, 0, 0 };
	int iaPulseStastic[] = { 0, 0, 0, 0 };
	int iPeriod = AnalysisView.YEAR;
	int iSepPos = 0;
	String sSepText = "";
	String saBarText[] = new String[31];
	int iDataCount = 7;
	List<RecordList> recordList = new ArrayList<RecordList>();
	Path path = new Path();
	Rect bounds = new Rect();
	DashPathEffect dash = new DashPathEffect(new float[] { 10, 5 }, 0);
	DashPathEffect dashV = new DashPathEffect(new float[] { 10, 10 }, 0);

	final int iaPeriod[] = { Color.rgb(82, 170, 205), Color.rgb(43, 92, 144), Color.rgb(198, 94, 55), Color.rgb(143, 56, 45) };

	int iaWhoCnt[] = { 0, 0, 0, 0, 0, 0 };
	int faWhoPercent[] = { 0, 0, 0, 0, 0, 0 };
	int iTotalCnt = 0;
	final int iaWho[] = { Color.rgb(123, 165, 67), Color.rgb(67, 152, 55), Color.rgb(59, 128, 57), Color.rgb(217, 163, 38), Color.rgb(211, 103, 30), Color.rgb(156, 31, 35) };

	String dateString = DateFormat.getBestDateTimePattern(java.util.Locale.getDefault(), "yyyy/MM/dd ");
	java.text.DateFormat dateTimeInstance = new SimpleDateFormat(dateString, java.util.Locale.getDefault());

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bp_pdf_choose);

		dateFromText = (TextView) findViewById(R.id.datefromtext);

		databaseHelper = new DatabaseHelper(this);
		// settings = getSharedPreferences("UserInformation", 0);
		ll_ana_sel_date = (LinearLayout) findViewById(R.id.ll_ana_sel_date);
		calendar = Calendar.getInstance();
		birthDateCalendar = Calendar.getInstance();
		settings = getSharedPreferences("BpPdfChooseView", 0);
		
		long b = settings.getLong("BIRTHDATE", calendar.getTime().getTime());

		

		
		 long l = calendar.getTime().getTime();
		Date d = new Date();
		d.setTime(l);
		calendar.setTime(d);
		int curYear = calendar.get(Calendar.YEAR);
		Log.e("time", "curYear = "+  curYear);
		
		long temp = settings.getLong("DATEFROM", calendar.getTime().getTime());
		Date tempdate = new Date();
		tempdate.setTime(temp);
		calendar.setTime(tempdate);
		int tempYear =calendar.get(Calendar.YEAR);
		int tempMonth=calendar.get(Calendar.MONTH);
		Log.e("time", "tempYear = "+  tempYear);
		
		d = new Date();
		d.setTime(b);
		birthDateCalendar.setTime(d);
		LinearLayout ll_list_back;

		ll_list_back = (LinearLayout) findViewById(R.id.ll_list_back);
		// 回上一頁
		ll_list_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Log.e("ll_list_back", "on click");

				try {
					ChangeView.onBack();
				} catch (Exception e) {
					finish();
				}

			}
		});

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
		Log.e("curMonth", curMonth+"");
		String months[] = new String[12];
		for (int x = 0; x < 12; x++) {
			months[x] = getMonth(x);
		}
		month.setViewAdapter(new DateArrayAdapter(this, months, curMonth));
		month.setCurrentItem(tempMonth);
		month.addChangingListener(listener);
		month.setCyclic(true);

		final int iYearCount = 100;
		// year
		lastYear = curYear;
		year.setViewAdapter(new DateNumericAdapter(this, curYear - iYearCount, curYear, 0));
		Log.d("alex", "curYear=" + curYear);
		year.setCurrentItem(tempYear-(curYear - iYearCount));
		year.addChangingListener(listener);

		// day
		updateDays(year, month, day);
		day.setCurrentItem(calendar.get(Calendar.DAY_OF_MONTH) - 1);
		Log.e("cur_Day", calendar.get(Calendar.DAY_OF_MONTH) - 1+"");
		day.setCyclic(true);
		// firstNameText.setOnFocusChangeListener(new OnFocusChangeListener() {
		// @Override
		// public void onFocusChange(View v, boolean hasFocus) {
		// if (hasFocus) {
		//
		// firstNameText.setCursorVisible(true);
		// }
		// }
		// });

	}

	public String getMonth(int month) {
		return new DateFormatSymbols().getMonths()[month];
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
		settings = getSharedPreferences("BpPdfChooseView", 0);

		long l = settings.getLong("DATEFROM", calendar.getTime().getTime());
		Date d = new Date();
		d.setTime(l);
		Calendar calendar2 = Calendar.getInstance();
		;
		calendar2.setTime(d);
		Log.e("time", dateTimeInstance.format(calendar2.getTime()));
		dateFromText.setText(dateTimeInstance.format(calendar2.getTime()));
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d("alex", "on Pause");
		HomeView.home_pressed = "wait";
	}

	public void onDateFrom(View v) {
		hideKeyboard();
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

		calendar.set(lastYear - 100 + year.getCurrentItem(), month.getCurrentItem(), day.getCurrentItem() + 1);
		Log.e("time", "lastYear = "+lastYear+ "year.getCurrentItem() = " );

		// String today = dateFormat.format(calendar.getTime()).toString();
		dateFromText.setText(dateTimeInstance.format(calendar.getTime()));

		SharedPreferences settings;
		settings = getSharedPreferences("BpPdfChooseView", 0);
		settings.edit().putLong("DATEFROM", calendar.getTime().getTime()).commit();

	}

	public void onYear(View v) {
		iPeriod = AnalysisView.YEAR;
		shareBpPDF();
//		setResult(RESULT_OK);
//		finish();
//		overridePendingTransition(R.anim.slide_no, R.anim.slide_out_left);
	}

	public void onMonth(View v) {
		iPeriod = AnalysisView.MONTH;
		shareBpPDF();
//		setResult(RESULT_OK);
//		finish();
//		overridePendingTransition(R.anim.slide_no, R.anim.slide_out_left);
	}

	public void onWeek(View v) {
		iPeriod = AnalysisView.WEEK;
		shareBpPDF();
//		setResult(RESULT_OK);
//		finish();
//		overridePendingTransition(R.anim.slide_no, R.anim.slide_out_left);
	}

	public void shareBpPDF() {

		Log.d("alex", "in the command");
		// Toast.makeText(RecordDetail.this, iHrWidth + " x " + iHrHeight,
		// Toast.LENGTH_LONG).show();

	//	try {
			File file1 = new File("/mnt/sdcard/Vian_Health/");
			if (!file1.exists()) {
				file1.mkdirs();
			}

			file = new File("/mnt/sdcard/Vian_Health", "BPM Report"+ ".pdf");
			PrintAttributes printAttrs = new PrintAttributes.Builder().setColorMode(PrintAttributes.COLOR_MODE_COLOR).setMediaSize(PrintAttributes.MediaSize.ISO_A4)
					.setResolution(new Resolution("zooey", PRINT_SERVICE, 595, 842)).setMinMargins(Margins.NO_MARGINS).build();
			PdfDocument document = new PrintedPdfDocument(BpPdfChooseView.this, printAttrs);
			PageInfo pageInfo = new PageInfo.Builder(595, 842, 1).create();
			Page page = document.startPage(pageInfo);
			Log.d("alex", "pdfECGWidth" + pageInfo.getPageWidth());
			Log.d("alex", "pdfECGHeight" + pageInfo.getPageHeight());
			
			if (page != null) {
//				Paint myPaint = new Paint();
//				myPaint.setStrokeWidth((float) 0.2);
//				myPaint.setTextSize(12);
//				myPaint.setColor(Color.BLACK);
//				myPaint.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/arial.ttf"));
//				
//				page.getCanvas().drawText("hello", 100, 100, myPaint);
				// View view = findViewById(R.id.hrTrendGraph);//
				// getContentView();
				// here the solution
				float drawLeft = 30;
				float drawTop = 180;
				int width = 40;// cannot fit when size too big
				int height = 20;
				int pdfECGWidth = pageInfo.getPageWidth();
				Log.d("alex", "pdfECGWidth" + pdfECGWidth);
				int pdfECGHeight = pageInfo.getPageHeight() - height;
				Log.d("alex", "pdfECGHeight" + pdfECGHeight);
				Paint myPaint = new Paint();
				//
				myPaint.setAntiAlias(true);
				myPaint.setDither(true);
				Path trace = new Path();
				int iIndex = 0;

				float iGridHeight = pdfECGHeight / 5;
				float iGridWidth = 75;
				float iCellWidth = 21;
				float iSmallCellWidth = 3;
				float lastx = drawLeft + iSmallCellWidth * 5 * 35;
				float lasty = drawTop + iSmallCellWidth * 5 * 40;

				float fGen = 65536 / 600;
				float fMvAmp = pdfECGHeight * 2 / 27;
				float fPixelPerAmp = fMvAmp / fGen;

				float iBaseLine = drawTop + iCellWidth * 6;

				int ecg_grid1 = getResources().getColor(R.color.ecg_grid1);
				int ecg_grid2 = getResources().getColor(R.color.ecg_grid2);
				int ecg_grid3 = getResources().getColor(R.color.ecg_grid3);
				Options options = new BitmapFactory.Options();
				options.inScaled = false;

				SharedPreferences settings = getSharedPreferences("UserInformation", 0);
				String name = settings.getString("FIRST_NAME", "");
				name = name + " " + settings.getString("LAST_NAME", "");
				gender_order = settings.getInt("GENDER", 0);
				Log.e("GENDER", "gender_order = "+gender_order);
				String gender;
				if(gender_order==0)
					gender = getString(R.string.Male);
				else
					gender = getString(R.string.Female);
				String userHeight = settings.getString("HEIGHT", "170");
				String userWeight = settings.getString("WEIGHT", "65");
				float userHeightF = Float.valueOf(settings.getString("HEIGHT", "170")) / 100;
				float userWeightF = Float.valueOf(settings.getString("WEIGHT", "65"));
				myPaint.setStrokeWidth((float) 0.2);
				myPaint.setTextSize(12);
				myPaint.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/arial.ttf"));
				page.getCanvas().drawText(getResources().getString(R.string.bp_report), drawLeft, drawTop - iCellWidth * 6 - 10, myPaint);
				myPaint.setStrokeWidth((float) 2);
				page.getCanvas().drawLine(drawLeft, drawTop - iCellWidth * 6, lastx, drawTop - iCellWidth * 6, myPaint);
				myPaint.setStrokeWidth((float) 1.2);
				page.getCanvas().drawLine(drawLeft, drawTop - iCellWidth * 5 + iSmallCellWidth * 18, lastx, drawTop - iCellWidth * 5 + iSmallCellWidth * 18, myPaint);
				page.getCanvas().drawLine(drawLeft, 800, lastx, 800, myPaint);
				myPaint.setStrokeWidth((float) 0.2);
				myPaint.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/arial.ttf"));
				myPaint.setTextSize(9);
				page.getCanvas().drawText(getResources().getString(R.string.username) + ":", drawLeft, drawTop - iCellWidth * 5, myPaint);
				page.getCanvas().drawText(name + "", drawLeft + 130, drawTop - iCellWidth * 5, myPaint);
				page.getCanvas().drawText(getResources().getString(R.string.gender) + ":", drawLeft, drawTop - iCellWidth * 5 + iSmallCellWidth * 3, myPaint);
				page.getCanvas().drawText(gender + "", drawLeft + 130, drawTop - iCellWidth * 5 + iSmallCellWidth * 3, myPaint);
				page.getCanvas().drawText(getResources().getString(R.string.birth_date) + ":", drawLeft, drawTop - iCellWidth * 5 + iSmallCellWidth * 6, myPaint);
				page.getCanvas().drawText(dateTimeInstance.format(birthDateCalendar.getTime()) + "", drawLeft + 130, drawTop - iCellWidth * 5 + iSmallCellWidth * 6, myPaint);
				page.getCanvas().drawText(getResources().getString(R.string.height) + ":", drawLeft, drawTop - iCellWidth * 5 + iSmallCellWidth * 9, myPaint);
				page.getCanvas().drawText(userHeight + " (cm)", drawLeft + 130, drawTop - iCellWidth * 5 + iSmallCellWidth * 9, myPaint);
				page.getCanvas().drawText(getResources().getString(R.string.weight) + ":", drawLeft, drawTop - iCellWidth * 5 + iSmallCellWidth * 12, myPaint);
				page.getCanvas().drawText(userWeight + " (kg)", drawLeft + 130, drawTop - iCellWidth * 5 + iSmallCellWidth * 12, myPaint);
				page.getCanvas().drawText(getResources().getString(R.string.BMI) + ":", drawLeft, drawTop - iCellWidth * 5 + iSmallCellWidth * 15, myPaint);
				page.getCanvas().drawText("" + String.format("%.1f", userWeightF / (userHeightF * userHeightF)) + "", drawLeft + 130, drawTop - iCellWidth * 5 + iSmallCellWidth * 15, myPaint);

				page.getCanvas().drawText(getResources().getString(R.string.heartrate) + ":", drawLeft + (lastx - drawLeft) / 2, drawTop - iCellWidth * 5 + iSmallCellWidth * 6, myPaint);
				myPaint.setTextSize(24);
				page.getCanvas().drawText("- -", drawLeft + (lastx - drawLeft) / 2 + 130, drawTop - iCellWidth * 5 + iSmallCellWidth * 10, myPaint);
				myPaint.setTextSize(9);
				page.getCanvas().drawText(getResources().getString(R.string.bpm), drawLeft + (lastx - drawLeft) / 2 + 160, drawTop - iCellWidth * 5 + iSmallCellWidth * 12, myPaint);
				page.getCanvas().drawText(getResources().getString(R.string.recordedfrom) + ":", drawLeft + (lastx - drawLeft) / 2, drawTop - iCellWidth * 5 + iSmallCellWidth * 3, myPaint);
				String datefrom = dateTimeInstance.format(calendar.getTime());
				Log.e("datefrom", datefrom);
				switch (iPeriod) {
				case AnalysisView.YEAR:
					datefrom = datefrom + " (" + getResources().getString(R.string.year) + ")";
					break;
				case AnalysisView.MONTH:
					datefrom = datefrom + " (" + getResources().getString(R.string.month) + ")";
					break;
				case AnalysisView.WEEK:
					datefrom = datefrom + " (" + getResources().getString(R.string.week) + ")";
					break;
				}

				page.getCanvas().drawText(datefrom + "", drawLeft + (lastx - drawLeft) / 2 + 130, drawTop - iCellWidth * 5+ iSmallCellWidth * 3, myPaint);
				myPaint.setColor(Color.GRAY);
				myPaint.setStyle(Paint.Style.STROKE);
				myPaint.setStrokeWidth((float) 0.5);

				// Draw BP Trend background

				calculate();

				// Draw Trend

				float iWidth = lastx - drawLeft;
				float iHeight = iCellWidth * 14;
				float fBpBottom = iHeight * 356 / 590;
				float fPulseBottom = iHeight * 560 / 590;
				drawTop = drawTop - 30;
				myPaint.setStrokeWidth((float) 0.2);
				myPaint.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/arial.ttf"));
				myPaint.setStyle(Paint.Style.FILL);
				myPaint.setTextSize(11);
				myPaint.setTextAlign(Paint.Align.CENTER);
				// myPaint.setColor(Color.rgb(131, 131, 131));
				myPaint.setColor(Color.BLACK);
				page.getCanvas().drawText(getResources().getString(R.string.trend_analysis), drawLeft + (lastx - drawLeft) / 2, drawTop - iSmallCellWidth * 2, myPaint);

				myPaint.setColor(Color.WHITE);
				myPaint.setStyle(Paint.Style.FILL_AND_STROKE);
				page.getCanvas().drawRect(new RectF(drawLeft, drawTop, drawLeft + iWidth, drawTop + iHeight), myPaint);

				final int iUpBP = 200, iLowBP = 40, iBpStep = 20;
				int iGridCnt = (iUpBP - iLowBP) / iBpStep;
				float fHeightPerStep = fBpBottom / ((iUpBP - iLowBP) / iBpStep);

				int iMax = 0, iMin = 300;
				for (int i = 0; i < iDataCount; i++) {
					for (int j = 0; j < 4; j++) {
						if (baPeriodEnabled[j] == false)
							continue;

						if (iMax < iaPulse[i][j])
							iMax = iaPulse[i][j];
						if (iMin > iaPulse[i][j])
							iMin = iaPulse[i][j];
					}
				}

				if (iMin == 0)
					iMin = 60;
				if (iMax == 0)
					iMax = 80;

				final int iPulseStep = 20;
				int iUpPulse = 140, iLowPulse = 20;
				int iGridPCnt = (iUpPulse - iLowPulse) / iPulseStep;
				float fHeightPerPStep = (fPulseBottom - fBpBottom) / iGridPCnt;

				myPaint.setTextAlign(Paint.Align.RIGHT);
				myPaint.getTextBounds("180", 0, 3, bounds);
				float height2 = drawTop;
				float fLeft = drawLeft + 30;
				float fChartWidth = drawLeft + iWidth - fLeft;

				myPaint.setColor(Color.BLACK);
				myPaint.setStrokeWidth((float) 0.2);
				myPaint.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/arial.ttf"));
				myPaint.setTextSize(8);
				// draw BP axis unit
				for (int j = 0; j < iGridCnt; j++)
					page.getCanvas().drawText(String.valueOf(iUpBP - j * iBpStep), fLeft - 7, (float) (fHeightPerStep * j + height2 + 3), myPaint);

				// draw Pulse axis unit
				for (int j = 1; j < iGridPCnt; j++)
					page.getCanvas().drawText(String.valueOf(iUpPulse - j * iPulseStep), fLeft - 7, (float) (fBpBottom + fHeightPerPStep * j + height2 + 2), myPaint);
				
				myPaint.setTextAlign(Paint.Align.LEFT);
				myPaint.setTextSize(8);
				
				page.getCanvas().save();
				
				page.getCanvas().rotate(-90);
				page.getCanvas().drawText(getResources().getString(R.string.bp_mmhg), -drawTop - fHeightPerStep * 5, fLeft - 30, myPaint);
				page.getCanvas().drawText(getResources().getString(R.string.pulse_bpm), -drawTop - fHeightPerStep * 11, fLeft - 30, myPaint);
				page.getCanvas().restore();

				myPaint.setColor(Color.rgb(147, 147, 147));
				myPaint.setStrokeWidth(1);
				myPaint.setStyle(Paint.Style.STROKE);
				page.getCanvas().drawLine(fLeft, drawTop, drawLeft + iWidth, drawTop, myPaint);
				page.getCanvas().drawLine(fLeft, drawTop + fBpBottom, drawLeft + iWidth, drawTop + fBpBottom, myPaint);
				page.getCanvas().drawLine(fLeft, drawTop + fPulseBottom, drawLeft + iWidth, drawTop + fPulseBottom, myPaint);
				page.getCanvas().drawLine(fLeft, drawTop, fLeft, drawTop + fPulseBottom, myPaint);
				page.getCanvas().drawLine(drawLeft + iWidth, drawTop, drawLeft + iWidth, drawTop + fPulseBottom, myPaint);
				// draw BP horizontal dash lines
				myPaint.setColor(Color.rgb(173, 173, 173));
				myPaint.setStrokeWidth((float) 0.5);
				myPaint.setStyle(Paint.Style.FILL);
				// myPaint.setStyle(Style.STROKE);
				// myPaint.setPathEffect(dash);
				for (int j = 0; j < iGridCnt; j++)
					page.getCanvas().drawLine(fLeft, fHeightPerStep * j + height2, drawLeft + iWidth, fHeightPerStep * j + height2, myPaint);

				// draw Pulse horizontal dash lines
				for (int j = 1; j < iGridPCnt; j++)
					page.getCanvas().drawLine(fLeft, fBpBottom + fHeightPerPStep * j + height2, drawLeft + iWidth, fBpBottom + fHeightPerPStep * j + height2, myPaint);

				myPaint.setPathEffect(null);

				// draw X axis date
				final float fBottomSpace = 10f;
				myPaint.setStrokeWidth((float) 0.2);
				myPaint.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/arial.ttf"));
				myPaint.setTextSize(10);
				myPaint.setTextAlign(Paint.Align.CENTER);
				// myPaint.setColor(Color.rgb(131, 131, 131));
				myPaint.setColor(Color.BLACK);
				myPaint.setStyle(Style.FILL);
				float fSplitWidth = fChartWidth / (iDataCount);
				Log.d("alex", "iDataCount" + iDataCount + ";gettime" + calendar.get(Calendar.DAY_OF_MONTH));
				try {
					page.getCanvas().drawText(saBarText[0], fLeft + fSplitWidth / 2, fPulseBottom + fBottomSpace + height2, myPaint);
				} catch (Exception e) {

				}
				myPaint.setStrokeWidth((float) 0.2);
				myPaint.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/arial.ttf"));
				myPaint.setTextSize(10);
				myPaint.setStyle(Style.FILL);
				for (int i = 1; i < iDataCount; i++) {
					page.getCanvas().drawLine(fLeft + fSplitWidth * i, height2 + fPulseBottom, fLeft + fSplitWidth * i, fPulseBottom + fBottomSpace + height2, myPaint);
					try {

						page.getCanvas().drawText(saBarText[i], fLeft + fSplitWidth * i + fSplitWidth / 2, fPulseBottom + fBottomSpace + height2, myPaint);
					} catch (Exception e) {

					}

				}
				

				// draw separate vertical line
				if (iSepPos >= 0) {
					float fSepPos = fLeft + fSplitWidth * iSepPos;
					myPaint.setColor(Color.rgb(131, 131, 131));
					myPaint.setStrokeWidth((float) 0.5);
					// myPaint.setStyle(Style.STROKE);
					// myPaint.setPathEffect(dashV);

					page.getCanvas().drawLine(fSepPos, drawTop, fSepPos, fPulseBottom + drawTop, myPaint);

					myPaint.setPathEffect(null);

					final float fIndShift = 20;
					final float fIndHeight = 20;
					fSepPos += 10;
					myPaint.setStrokeWidth(1);
					myPaint.setStyle(Style.FILL);
					// path.rewind();
					// path.setFillType(Path.FillType.EVEN_ODD);
					// path.moveTo(fSepPos, fIndShift + drawTop);
					// path.lineTo(fSepPos + fIndHeight, fIndShift + fIndHeight
					// / 3 + drawTop);
					// path.lineTo(fSepPos, fIndShift + fIndHeight + drawTop);
					// path.lineTo(fSepPos, fIndShift + drawTop);
					// path.close();
					//
					// page.getCanvas().drawPath(path, myPaint);
					myPaint.setColor(Color.BLACK);
					myPaint.setStrokeWidth((float) 0.2);
					myPaint.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/arial.ttf"));
					myPaint.setTextSize(11);
					myPaint.setTextAlign(Paint.Align.LEFT);
					page.getCanvas().drawText(sSepText, fSepPos, drawTop + fIndShift + fIndHeight, myPaint);
					myPaint.setTypeface(null);
				}

				// float fDataWidth = (float) (iWidth - fLeft) / iDataCount;
				float fPeriodWidth = fSplitWidth / 4;
				float fBarW;
				fBarW = 3;
				Log.d("alex", "fBarW=" + fBarW);
				// Cancel circle radious with different size.
				switch (iPeriod) {
				case AnalysisView.WEEK:
					// fBarW=(fPeriodWidth-16)/4;
					fBarW = 6;
					break;
				case AnalysisView.MONTH:
					// fBarW=(fPeriodWidth-8)/4;
					fBarW = 2;
					break;
				case AnalysisView.YEAR:
				default:
					// fBarW=(fPeriodWidth-10)/4;
					fBarW = 3;
					break;
				}

				// draw BP bars & Pulse point
				myPaint.setStrokeWidth(1);
				myPaint.setStyle(Paint.Style.FILL);
				for (int i = 0; i < iDataCount; i++) {
					for (int j = 0; j < 4; j++) {
						if (baPeriodEnabled[j] == false)
							continue;

						if (iaDiaBP[i][j] < iLowBP)
							iaDiaBP[i][j] = iLowBP;

						myPaint.setColor(iaPeriod[j]);
						// draw BP bars
						float fMax = (iUpBP - iaSysBP[i][j]) * fHeightPerStep / iBpStep + fBarW;
						float fMin = (iUpBP - iaDiaBP[i][j]) * fHeightPerStep / iBpStep - fBarW;

						if (iaSysBP[i][j] > iLowBP) {
							// canvas.drawRect(fLeft+i*fDataWidth+fPeriodWidth*j+fPeriodWidth/2-fBarW,
							// fMax
							// ,
							// fLeft+i*fDataWidth+fPeriodWidth*j+fPeriodWidth/2+fBarW,
							// fMin, myPaint);
							page.getCanvas().drawCircle(fLeft + i * fSplitWidth + fPeriodWidth * j + fPeriodWidth / 2, drawTop + fMax, fBarW, myPaint);
							page.getCanvas().drawCircle(fLeft + i * fSplitWidth + fPeriodWidth * j + fPeriodWidth / 2, drawTop + fMin, fBarW, myPaint);
							Log.d("alex", "iaSysBP[" + i + "][" + j + "]=" + iaSysBP[i][j]);

							// draw Pulse point
							fMax = fBpBottom + (iUpPulse - iaPulse[i][j]) * fHeightPerPStep / iPulseStep;
							page.getCanvas().drawCircle(fLeft + i * fSplitWidth + fPeriodWidth * j + fPeriodWidth / 2, drawTop + fMax, fBarW, myPaint);
							Log.d("alex", "fMax=" + fMax);
						}

					}
				}

				// Draw Stastic
				fBarW = 3;
				drawTop = 480;
				drawLeft = 375;
				fHeightPerStep = fHeightPerPStep * 4 / 5;
				iGridCnt++;
				lasty = drawTop + fHeightPerStep * iGridCnt + fHeightPerStep * iGridPCnt + height / 2;
				float staticLastY = lasty;
				float iBpHeight = lasty - drawTop, iBpWidth = lastx - drawLeft;
				float fGridWidth = iBpWidth / 4;

				myPaint.setStrokeWidth((float) 0.2);
				myPaint.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/arial.ttf"));
				myPaint.setTextSize(12);
				// myPaint.setColor(Color.rgb(131, 131, 131));
				myPaint.setColor(Color.BLACK);
				myPaint.setTextAlign(Paint.Align.CENTER);
				page.getCanvas().drawText(getResources().getString(R.string.statistic_analysis), drawLeft + (lastx - drawLeft) / 2, drawTop - iSmallCellWidth * 2, myPaint);

				myPaint.setPathEffect(null);

				myPaint.setStyle(Paint.Style.STROKE);
				myPaint.setColor(Color.rgb(173, 173, 173));
				myPaint.setStrokeWidth(1);
				page.getCanvas().drawRect(new RectF(drawLeft, drawTop, lastx, lasty), myPaint);

				page.getCanvas().drawLine(drawLeft + (lastx - drawLeft) / 4, drawTop, drawLeft + (lastx - drawLeft) / 4, lasty, myPaint);
				page.getCanvas().drawLine(drawLeft + ((lastx - drawLeft) / 4) * 2, drawTop, drawLeft + ((lastx - drawLeft) / 4) * 2, lasty, myPaint);
				page.getCanvas().drawLine(drawLeft + ((lastx - drawLeft) / 4) * 3, drawTop, drawLeft + ((lastx - drawLeft) / 4) * 3, lasty, myPaint);
				page.getCanvas().drawLine(drawLeft, drawTop + fHeightPerStep * iGridCnt + height / 2, lastx, drawTop + fHeightPerStep * iGridCnt + height / 2, myPaint);
				page.getCanvas().save();
				myPaint.setStyle(Paint.Style.FILL);
				myPaint.setStrokeWidth((float) 0.2);
				myPaint.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/arial.ttf"));
				myPaint.setTextSize(7);
				myPaint.setTextAlign(Paint.Align.CENTER);
				// myPaint.setColor(Color.rgb(131, 131, 131));
				myPaint.setColor(Color.BLACK);
				setTextSizeForWidth(myPaint, 35, getResources().getString(R.string.morning), getResources().getString(R.string.daytime), getResources().getString(R.string.evening), getResources()
						.getString(R.string.night));
				Log.d("alex", "myPaint.getTextSize=" + myPaint.getTextSize());
				page.getCanvas().drawText(getResources().getString(R.string.morning), drawLeft + ((lastx - drawLeft) / 8), drawTop + height - 5, myPaint);
				page.getCanvas().drawText(getResources().getString(R.string.daytime), drawLeft + ((lastx - drawLeft) / 8) * 3, drawTop + height - 5, myPaint);
				page.getCanvas().drawText(getResources().getString(R.string.evening), drawLeft + ((lastx - drawLeft) / 8) * 5, drawTop + height - 5, myPaint);
				page.getCanvas().drawText(getResources().getString(R.string.night), drawLeft + ((lastx - drawLeft) / 8) * 7, drawTop + height - 5, myPaint);
				myPaint.setTextAlign(Paint.Align.LEFT);
				myPaint.setTextSize(8);
				
				page.getCanvas().save();
				
				page.getCanvas().rotate(-90);
				page.getCanvas().drawText(getResources().getString(R.string.bp_mmhg), -drawTop - fHeightPerStep * 7, drawLeft - 30, myPaint);
				page.getCanvas().drawText(getResources().getString(R.string.pulse_bpm), -drawTop - fHeightPerStep * 14, drawLeft - 30, myPaint);
				page.getCanvas().restore();

				myPaint.setStrokeWidth((float) 0.2);
				myPaint.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/arial.ttf"));
				myPaint.setTextSize(8);
				myPaint.setTextAlign(Paint.Align.RIGHT);
				Rect bounds = new Rect();
				myPaint.getTextBounds("1", 0, 1, bounds);

				for (int j = 1; j < iGridCnt; j++) {
					page.getCanvas().drawText(String.valueOf(iUpBP - (j - 1) * iBpStep), drawLeft - 10, drawTop + fHeightPerStep * j + (height / 2) + 3, myPaint);
				}
				for (int j = 1; j < iGridPCnt; j++) {
					page.getCanvas().drawText(String.valueOf(iUpPulse - j * iPulseStep), drawLeft - 10, drawTop + fHeightPerStep * (j + iGridCnt) + (height / 2) + 3, myPaint);
				}
				myPaint.setColor(Color.rgb(173, 173, 173));
				myPaint.setStrokeWidth((float) 0.5);
				// myPaint.setStyle(Style.STROKE);
				// myPaint.setPathEffect(new DashPathEffect(new float[] { 3, 6
				// }, 0));
				for (int j = 1; j < iGridCnt; j++) {
					page.getCanvas().drawLine(drawLeft, drawTop + fHeightPerStep * j + height / 2, lastx, drawTop + fHeightPerStep * j + height / 2, myPaint);
				}
				for (int j = 1; j < iGridPCnt; j++) {
					page.getCanvas().drawLine(drawLeft, drawTop + fHeightPerStep * (j + iGridCnt) + height / 2, lastx, drawTop + fHeightPerStep * (j + iGridCnt) + height / 2, myPaint);
				}
				myPaint.setTextAlign(Paint.Align.CENTER);
				myPaint.setPathEffect(null);

				for (int i = 0; i < 4; i++) {
					if (baPeriodEnabled[i] == false)
						continue;

					myPaint.setStyle(Paint.Style.FILL_AND_STROKE);
					myPaint.setColor(iaPeriod[i]);
					float fMax = (iUpBP - iaSysBPStastic[i]) * fHeightPerStep / 20;
					float fMin = (iUpBP - iaDiaBPStastic[i]) * fHeightPerStep / 20;
					if (iaSysBPStastic[i] < iaDiaBPStastic[i]) {
						fMax = fMin + fBarW;
					}
					if (iaDiaBPStastic[i] < iLowBP)
						iaDiaBPStastic[i] = iLowBP;

					if (iaSysBPStastic[i] > iLowBP) {
						// tempCanvas.drawRect(fLeft + i * fGridWidth +
						// fGridWidth / 2 -
						// fBarW, fMax, fLeft + i * fGridWidth + fGridWidth / 2
						// + fBarW,
						// fMin, myPaint);
						// tempCanvas.drawCircle(fLeft + i * fGridWidth +
						// fGridWidth /
						// 2, fMax, fBarW, myPaint);
						// tempCanvas.drawCircle(fLeft + i * fGridWidth +
						// fGridWidth /
						// 2, fMin, fBarW, myPaint);
						page.getCanvas().drawRect(drawLeft + i * fGridWidth + fGridWidth / 2 - 5, drawTop + fMax + height / 2 + fHeightPerStep, drawLeft + i * fGridWidth + fGridWidth / 2 + fBarW,
								drawTop + fMin + height / 2 + fHeightPerStep, myPaint);
						// page.getCanvas().drawCircle(drawLeft + i * fGridWidth
						// + fGridWidth, drawTop + fMax + height / 2, fBarW,
						// myPaint);
						// page.getCanvas().drawCircle(drawLeft + i * fGridWidth
						// + fGridWidth, drawTop + fMin + height / 2, fBarW,
						// myPaint);

						float fPulseMax = (iUpPulse - iaPulseStastic[i]) * fHeightPerStep / 20;
						page.getCanvas().drawCircle(drawLeft + i * fGridWidth + fGridWidth / 2 - 1, drawTop + fHeightPerStep * iGridCnt + fPulseMax + height / 2, fBarW, myPaint);

						myPaint.setColor(Color.rgb(20, 20, 20));
						myPaint.setStrokeWidth((float) 0.2);
						myPaint.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/arial.ttf"));
						myPaint.setTextSize(10);
						 page.getCanvas().drawText(String.valueOf(iaSysBPStastic[i]),
						 drawLeft + i * fGridWidth + fGridWidth/2 + height / 2-10
						 , drawTop + fMax + height / 2 + 12, myPaint);
						// Log.d("alex", "iaSysBP" +
						// String.valueOf(iaSysBPStastic[i]));
						 page.getCanvas().drawText(String.valueOf(iaDiaBPStastic[i]),
						 drawLeft + i * fGridWidth + fGridWidth/2 + height / 2-10
						 , drawTop + fMin + height / 2 + 22, myPaint);
						// Log.d("alex", "iaDiaBP" +
						// String.valueOf(iaDiaBPStastic[i]));
						 page.getCanvas().drawText(String.valueOf(iaPulseStastic[i]),
						 drawLeft + i * fGridWidth + fGridWidth/2 + height / 2-10
						 ,
						 drawTop + fHeightPerStep * iGridCnt + fPulseMax +
						 height / 2 -5, myPaint);

					}

				}

				// Draw Static Compare
				drawTop = lasty + iSmallCellWidth;
				fHeightPerStep = (float) (fHeightPerStep * 1.5);
				lasty = drawTop + fHeightPerStep * 3 + iSmallCellWidth * 3;

				myPaint.setPathEffect(null);

				myPaint.setStyle(Paint.Style.STROKE);
				myPaint.setColor(Color.rgb(173, 173, 173));
				myPaint.setStrokeWidth(1);
				page.getCanvas().drawRect(new RectF(drawLeft, drawTop, lastx, lasty), myPaint);
				page.getCanvas().drawLine(drawLeft, drawTop + fHeightPerStep, lastx, drawTop + fHeightPerStep, myPaint);
				page.getCanvas().drawLine(drawLeft, drawTop + fHeightPerStep + iSmallCellWidth * 5, lastx, drawTop + fHeightPerStep + iSmallCellWidth * 5, myPaint);
				page.getCanvas().drawLine(drawLeft + (lastx - drawLeft) / 2, drawTop + fHeightPerStep, drawLeft + (lastx - drawLeft) / 2, lasty, myPaint);
				page.getCanvas().drawLine(drawLeft + (lastx - drawLeft) / 4, drawTop + fHeightPerStep + iSmallCellWidth * 5, drawLeft + (lastx - drawLeft) / 4, lasty, myPaint);
				page.getCanvas().drawLine(drawLeft + (lastx - drawLeft) / 4 * 3, drawTop + fHeightPerStep + iSmallCellWidth * 5, drawLeft + (lastx - drawLeft) / 4 * 3, lasty, myPaint);
				myPaint.setStyle(Paint.Style.FILL);
				myPaint.setStrokeWidth((float) 0.2);
				myPaint.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/arial.ttf"));
				myPaint.setTextSize(10);
				myPaint.setTextAlign(Paint.Align.CENTER);
				// myPaint.setColor(Color.rgb(131, 131, 131));
				myPaint.setColor(Color.BLACK);
				page.getCanvas().drawText(getResources().getString(R.string.m_e_difference), drawLeft + (lastx - drawLeft) / 2, drawTop + fHeightPerStep - iSmallCellWidth * 2, myPaint);
				myPaint.setTextSize(8);
				page.getCanvas().drawText(getResources().getString(R.string.difference), drawLeft + (lastx - drawLeft) / 4, drawTop + fHeightPerStep + iSmallCellWidth * 4, myPaint);
				page.getCanvas().drawText(getResources().getString(R.string.average), drawLeft + ((lastx - drawLeft) / 4) * 3, drawTop + fHeightPerStep + iSmallCellWidth * 4, myPaint);
				myPaint.setTextSize(6);
				page.getCanvas().drawText(getResources().getString(R.string.sys) + getResources().getString(R.string.mmhg), drawLeft + ((lastx - drawLeft) / 8),
						drawTop + fHeightPerStep + iSmallCellWidth * 8, myPaint);
				page.getCanvas().drawText(getResources().getString(R.string.dia) + getResources().getString(R.string.mmhg), drawLeft + ((lastx - drawLeft) / 8) * 3,
						drawTop + fHeightPerStep + iSmallCellWidth * 8, myPaint);
				page.getCanvas().drawText(getResources().getString(R.string.sys) + getResources().getString(R.string.mmhg), drawLeft + ((lastx - drawLeft) / 8) * 5,
						drawTop + fHeightPerStep + iSmallCellWidth * 8, myPaint);
				page.getCanvas().drawText(getResources().getString(R.string.dia) + getResources().getString(R.string.mmhg), drawLeft + ((lastx - drawLeft) / 8) * 7,
						drawTop + fHeightPerStep + iSmallCellWidth * 8, myPaint);

				myPaint.setTextSize(13);
				myPaint.setColor(Color.rgb(20, 20, 20));
				if (iaSysBPStastic[0] == 0 || iaSysBPStastic[2] == 0) {
					page.getCanvas().drawText("--", drawLeft + ((lastx - drawLeft) / 8), drawTop + fHeightPerStep + iSmallCellWidth * 14, myPaint);
					page.getCanvas().drawText("--", drawLeft + ((lastx - drawLeft) / 8) * 3, drawTop + fHeightPerStep + iSmallCellWidth * 14, myPaint);
					page.getCanvas().drawText("--", drawLeft + ((lastx - drawLeft) / 8) * 5, drawTop + fHeightPerStep + iSmallCellWidth * 14, myPaint);
					page.getCanvas().drawText("--", drawLeft + ((lastx - drawLeft) / 8) * 7, drawTop + fHeightPerStep + iSmallCellWidth * 14, myPaint);
				} else {

					page.getCanvas().drawText(iaSysBPStastic[0] - iaSysBPStastic[2] + "", drawLeft + ((lastx - drawLeft) / 8), drawTop + fHeightPerStep + iSmallCellWidth * 14, myPaint);
					page.getCanvas().drawText((iaDiaBPStastic[0] - iaDiaBPStastic[2]) + "", drawLeft + ((lastx - drawLeft) / 8) * 3, drawTop + fHeightPerStep + iSmallCellWidth * 14, myPaint);
					page.getCanvas().drawText((iaSysBPStastic[0] + iaSysBPStastic[2]) / 2 + "", drawLeft + ((lastx - drawLeft) / 8) * 5, drawTop + fHeightPerStep + iSmallCellWidth * 14, myPaint);
					page.getCanvas().drawText((iaDiaBPStastic[0] + iaDiaBPStastic[2]) / 2 + "", drawLeft + ((lastx - drawLeft) / 8) * 7, drawTop + fHeightPerStep + iSmallCellWidth * 14, myPaint);
					myPaint.setTextSize(8);
					// myPaint.setColor(Color.rgb(131, 131, 131));
					myPaint.setColor(Color.BLACK);
					myPaint.setTextAlign(Paint.Align.LEFT);
					// page.getCanvas().drawText(getResources().getString(R.string.mmhg),
					// drawLeft + ((lastx - drawLeft) / 4) + iSmallCellWidth *
					// 4, drawTop + fHeightPerStep + fHeightPerStep * 2,
					// myPaint);
					// page.getCanvas().drawText(getResources().getString(R.string.mmhg),
					// drawLeft + ((lastx - drawLeft) / 4) * 3 + iSmallCellWidth
					// * 4, drawTop + fHeightPerStep + fHeightPerStep * 2,
					// myPaint);
				}

				// Draw Distribution
				drawTop = 480;
				drawLeft = fLeft;
				lastx = 330;
				myPaint.setStyle(Paint.Style.STROKE);
				myPaint.setColor(Color.rgb(173, 173, 173));
				myPaint.setStrokeWidth(1);
				page.getCanvas().drawRect(new RectF(drawLeft, drawTop, lastx, lasty), myPaint);

				float templine = staticLastY;
				page.getCanvas().drawLine(drawLeft, templine, lastx, templine, myPaint);

				page.getCanvas().drawLine(drawLeft, templine + (lasty - templine) / 2, lastx, templine + (lasty - templine) / 2, myPaint);
				page.getCanvas().drawLine(drawLeft + (lastx - drawLeft) / 3, templine, drawLeft + (lastx - drawLeft) / 3, lasty, myPaint);
				page.getCanvas().drawLine(drawLeft + (lastx - drawLeft) / 3 * 2, templine, drawLeft + (lastx - drawLeft) / 3 * 2, lasty, myPaint);
				// page.getCanvas().drawLine(drawLeft,
				// drawTop+fHeightPerStep+iSmallCellWidth*5, lastx,
				// drawTop+fHeightPerStep+iSmallCellWidth*5, myPaint);
				// page.getCanvas().drawLine(drawLeft + (lastx - drawLeft) / 2,
				// drawTop+fHeightPerStep+iSmallCellWidth*5, drawLeft + (lastx -
				// drawLeft) / 2, lasty, myPaint);
				myPaint.setStyle(Paint.Style.FILL);
				myPaint.setStrokeWidth((float) 0.2);
				myPaint.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/arial.ttf"));
				myPaint.setTextSize(12);
				// myPaint.setColor(Color.rgb(131, 131, 131));
				myPaint.setColor(Color.BLACK);
				myPaint.setTextAlign(Paint.Align.CENTER);
				page.getCanvas().drawText(getResources().getString(R.string.distribution_analysis), drawLeft + (lastx - drawLeft) / 2, drawTop - iSmallCellWidth * 2, myPaint);

				myPaint.setStyle(Paint.Style.FILL);
				myPaint.setStrokeWidth((float) 0.2);
				// myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,
				// Typeface.NORMAL));
				myPaint.setTextSize(8);
				myPaint.setTextAlign(Paint.Align.LEFT);
				// myPaint.setColor(Color.rgb(131, 131, 131));
				myPaint.setColor(Color.BLACK);
				page.getCanvas().drawText(getResources().getString(R.string.distribution_total100) + " " + iTotalCnt, drawLeft + fHeightPerStep - fBarW, drawTop + height, myPaint);
				myPaint.setTextAlign(Paint.Align.CENTER);
				page.getCanvas().drawText(getResources().getString(R.string.who_indi), drawLeft + (lastx - drawLeft) / 2, templine - height - 20, myPaint);
				myPaint.setTextSize(6);
				myPaint.setTextAlign(Paint.Align.LEFT);
				// Typeface tf = Typeface.createFromAsset(this.getAssets(),
				// "fonts/FuturaCondensed.ttf");
				// myPaint.setTypeface(Typeface.SERIF);
				page.getCanvas().drawText(getResources().getString(R.string.who_indi_desc1), drawLeft + drawLeft, templine - height - 10, myPaint);
				page.getCanvas().drawText(getResources().getString(R.string.who_indi_desc2), drawLeft + drawLeft, templine - height, myPaint);
				myPaint.setTextSize(8);
				myPaint.setTextAlign(Paint.Align.LEFT);
				height = 12;
				page.getCanvas().drawText(getResources().getString(R.string.optimal), drawLeft + fHeightPerStep - fBarW, templine + height, myPaint);
				page.getCanvas().drawText(getResources().getString(R.string.normal), drawLeft + fHeightPerStep - fBarW + (lastx - drawLeft) / 3, templine + height, myPaint);
				page.getCanvas().drawText(getResources().getString(R.string.high_normal), drawLeft + fHeightPerStep - fBarW + (lastx - drawLeft) / 3 * 2, templine + height, myPaint);
				page.getCanvas().drawText(getResources().getString(R.string.grade1), drawLeft + fHeightPerStep - fBarW, templine + (lasty - templine) / 2 + height, myPaint);
				page.getCanvas().drawText(getResources().getString(R.string.grade2), drawLeft + fHeightPerStep - fBarW + (lastx - drawLeft) / 3, templine + (lasty - templine) / 2 + height, myPaint);
				page.getCanvas().drawText(getResources().getString(R.string.grade3), drawLeft + fHeightPerStep - fBarW + (lastx - drawLeft) / 3 * 2, templine + (lasty - templine) / 2 + height,
						myPaint);
				page.getCanvas().drawText(String.valueOf((int) (faWhoPercent[0])) + "%" + " (" + String.valueOf(iaWhoCnt[0] + ")"), drawLeft + fHeightPerStep - fBarW, templine + height * 2, myPaint);
				page.getCanvas().drawText(String.valueOf((int) (faWhoPercent[1])) + "%" + " (" + String.valueOf(iaWhoCnt[1] + ")"), drawLeft + fHeightPerStep - fBarW + (lastx - drawLeft) / 3,
						templine + height * 2, myPaint);
				page.getCanvas().drawText(String.valueOf((int) (faWhoPercent[2])) + "%" + " (" + String.valueOf(iaWhoCnt[2] + ")"), drawLeft + fHeightPerStep - fBarW + (lastx - drawLeft) / 3 * 2,
						templine + height * 2, myPaint);
				page.getCanvas().drawText(String.valueOf((int) (faWhoPercent[3])) + "%" + " (" + String.valueOf(iaWhoCnt[3] + ")"), drawLeft + fHeightPerStep - fBarW,
						templine + (lasty - templine) / 2 + height * 2, myPaint);
				page.getCanvas().drawText(String.valueOf((int) (faWhoPercent[4])) + "%" + " (" + String.valueOf(iaWhoCnt[4] + ")"), drawLeft + fHeightPerStep - fBarW + (lastx - drawLeft) / 3,
						templine + (lasty - templine) / 2 + height * 2, myPaint);
				page.getCanvas().drawText(String.valueOf((int) (faWhoPercent[5])) + "%" + " (" + String.valueOf(iaWhoCnt[5] + ")"), drawLeft + fHeightPerStep - fBarW + (lastx - drawLeft) / 3 * 2,
						templine + (lasty - templine) / 2 + height * 2, myPaint);

				float CIRCLE_CENTER_X = drawLeft + (lastx - drawLeft) / 2;
				float CIRCLE_CENTER_Y = drawTop + (templine - drawTop) / 2 - 20;

				myPaint.setColor(Color.rgb(255, 255, 255));
				myPaint.setStyle(Paint.Style.FILL);
				// page.getCanvas().drawRect(new RectF(drawLeft, drawTop, lastx,
				// lasty), myPaint);

				myPaint.setColor(Color.WHITE);
				myPaint.setStrokeWidth(3);
				// tempCanvas.drawLine(0, i, iGraphWidth, i, myPaint);
				float fRadius = (float) (Math.min((lastx - drawLeft), (templine - drawTop)) * 0.26);
				page.getCanvas().drawCircle(CIRCLE_CENTER_X, CIRCLE_CENTER_Y, fRadius, myPaint);

				int fPercent = 0;
				for (int j = 0; j < 6; j++) {
					if (faWhoPercent[j] == 0)
						continue;

					myPaint.setColor(Color.WHITE);
					float fAngle = (float) ((fPercent) * Math.PI * 2 - 0.5 * Math.PI);
					page.getCanvas()
							.drawLine(CIRCLE_CENTER_X, CIRCLE_CENTER_Y, (float) (CIRCLE_CENTER_X + fRadius * Math.cos(fAngle)), (float) (CIRCLE_CENTER_Y + fRadius * Math.sin(fAngle)), myPaint);

					myPaint.setColor(iaWho[j]);
//					page.getCanvas().drawArc(new RectF(CIRCLE_CENTER_X - fRadius, CIRCLE_CENTER_Y - fRadius, CIRCLE_CENTER_X + fRadius, CIRCLE_CENTER_Y + fRadius), fPercent * 360 / 100 - 90,
//							(faWhoPercent[j] * 360 / 100) - 3, true, myPaint);
					Bitmap bitmapBackground= Bitmap.createBitmap(115,115, Bitmap.Config.ARGB_8888);
					Canvas canvas = new Canvas(bitmapBackground);
					
					RectF rectF=new RectF(0,0,(CIRCLE_CENTER_X + fRadius)-(CIRCLE_CENTER_X - fRadius),(CIRCLE_CENTER_X + fRadius)-(CIRCLE_CENTER_X - fRadius));
					Log.d("sandy", "(CIRCLE_CENTER_X + fRadius)-(CIRCLE_CENTER_X - fRadius)="+((CIRCLE_CENTER_X + fRadius)-(CIRCLE_CENTER_X - fRadius)));
					canvas.drawArc(rectF, fPercent * 360 / 100 - 90, (faWhoPercent[j] * 360 / 100) - 3, true, myPaint);
					page.getCanvas().setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
					page.getCanvas().drawBitmap(bitmapBackground,CIRCLE_CENTER_X - fRadius,CIRCLE_CENTER_Y - fRadius, myPaint);
					
					// Log.d("alex","fPercent * 360 / 100 - 90="+(fPercent * 360
					// / 100 -
					// 90)+",   (faWhoPercent[j] * 360 / 100)="+(faWhoPercent[j]
					// * 360 / 100));
					fPercent += faWhoPercent[j];

				}
				myPaint.setColor(Color.WHITE);
				myPaint.setStrokeWidth(3);
				// tempCanvas.drawLine(0, i, iGraphWidth, i, myPaint);
				fRadius = fRadius / 2;
				page.getCanvas().drawCircle(CIRCLE_CENTER_X, CIRCLE_CENTER_Y, fRadius, myPaint);
			}
			

			
			Log.e("shareBpPDF", "finishPage");
			document.finishPage(page);
			
			builder = new ProgressDialog.Builder(BpPdfChooseView.this);
			builder.setMessage("Loading....").setTitle("");

			dialog = builder.create();

			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
			
			new sendmail().execute(document);
//			Log.e("shareBpPDF", "FileOutputStream os");
//			FileOutputStream os = new FileOutputStream(file);
//			Log.e("shareBpPDF", "writeTo(os)");
//			document.writeTo(os);//use longggg time
//			Log.e("shareBpPDF", "document.close()");
//			document.close();
//			Log.e("shareBpPDF", "os.close()");
//			os.close();
//			Log.e("shareBpPDF", file.getAbsolutePath().toString());
//			// startActivity(new
//			// Intent(Intent.ACTION_SEND).setDataAndType(Uri.fromFile(file),
//			// "application/pdf"));
//
//			Intent email = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
//			email.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });
//			email.setData(Uri.parse(""));
//			email.putExtra(Intent.EXTRA_SUBJECT, "BPM Report");
//			email.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath().toString()));
//			email.setType("application/pdf");
//			email.putExtra(Intent.EXTRA_TEXT, "Send Report from "+getResources().getString(R.string.app_name));
//			startActivity(email);


//		} catch (IOException e) {
//			throw new RuntimeException("Error generating file", e);
//		}
	}
	
	
	private class sendmail extends AsyncTask<PdfDocument, Void, Void>
	{

		@Override
	    protected Void doInBackground(PdfDocument... document)
	    {
	        
			Log.d("alex", "in the command");
			// Toast.makeText(RecordDetail.this, iHrWidth + " x " + iHrHeight,
			// Toast.LENGTH_LONG).show();

			try {
				FileOutputStream os = new FileOutputStream(file);
				Log.e("shareBpPDF", "writeTo(os)");
				document[0].writeTo(os);//use longggg time
				Log.e("shareBpPDF", "document.close()");
				document[0].close();
				Log.e("shareBpPDF", "os.close()");
				os.close();
				Log.e("shareBpPDF", file.getAbsolutePath().toString());
				dialog.cancel();
				
				Intent email = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
				email.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });
				email.setData(Uri.parse(""));
				email.putExtra(Intent.EXTRA_SUBJECT, "BPM Report");
				email.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath().toString()));
				email.setType("application/pdf");
				email.putExtra(Intent.EXTRA_TEXT, "Send Report from "+getResources().getString(R.string.app_name));
				startActivity(email);



			} catch (IOException e) {
				throw new RuntimeException("Error generating file", e);
			}
			return null;
	    }
	}
	 

	
	
	public static String getLocaleLanguage() {
	    Locale l = Locale.getDefault();
	    return String.format("%s-%s", l.getLanguage(), l.getCountry());
	}

	private static void setTextSizeForWidth(Paint paint, float desiredWidth, String text1, String text2, String text3, String text4) {
		
		String language =getLocaleLanguage();
		Log.d("tina", " language = "+ language);
		if(language.equals("zh-TW")||language.equals("zh-CN"))
		{
			paint.setTextSize(7);
		}
		else
		{

		// Pick a reasonably large value for the test. Larger values produce
		// more accurate results, but may cause problems with hardware
		// acceleration. But there are workarounds for that, too; refer to
		// http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
		final float testTextSize = 48f;
		float desiredTextSize[] = new float[4];
		int maxDesiredTextSize = 0;

		// Get the bounds of the text, using our testTextSize.
		paint.setTextSize(testTextSize);
		Rect bounds = new Rect();

		//
		//
		// paint.getTextBounds(text2, 0, text2.length(), bounds);
		// desiredTextSize[1] = testTextSize * desiredWidth / bounds.width();
		// paint.getTextBounds(text3, 0, text3.length(), bounds);
		// desiredTextSize[2] = testTextSize * desiredWidth / bounds.width();
		// paint.getTextBounds(text4, 0, text4.length(), bounds);
		// desiredTextSize[3] = testTextSize * desiredWidth / bounds.width();
		int minX = 0;
		String text[] = new String[4];
		text[0] = text1;
		text[1] = text2;
		text[2] = text3;
		text[3] = text4;
		for (int x = 0; x < 4; x++) {
			// if(desiredTextSize[x]<minDesiredTextSize)
			// minDesiredTextSize=desiredTextSize[x];
			if (text[x].length() > maxDesiredTextSize) {
				maxDesiredTextSize = text[x].length();
				minX = x;
			}
			Log.d("alex", "text[" + "x" + "]=" + text[x] + ",text[x].length()=" + text[x].length());
		}

		paint.getTextBounds(text[minX], 0, text[minX].length(), bounds);

		desiredTextSize[0] = testTextSize * desiredWidth / bounds.width();
		paint.setTextSize(desiredTextSize[0]);
		Log.d("alex", "min=" + text[minX] + ";desiredTextSize[0]=" + desiredTextSize[0]);
	}
	}

	void calculate() {

		/*
		 * for(int i=0; i<31; i++) { for(int j=0; j<4; j++) {
		 * iaMaxBP[i][j]=110+j*5+i; iaMinBP[i][j]=70+j*7+i;
		 * iaPulse[i][j]=65+j*3+i; } }
		 */

		// Tre
		for (int i = 0; i < 31; i++) {
			for (int j = 0; j < 4; j++) {
				iaSysBP[i][j] = iaDiaBP[i][j] = iaPulse[i][j] = 0;
			}
		}

		// Sta
		for (int j = 0; j < iaSysBPStastic.length; j++) {
			iaSysBPStastic[j] = iaDiaBPStastic[j] = iaPulseStastic[j] = 0;
		}
		// Dis

		iTotalCnt = 0;
		for (int j = 0; j < iaWhoCnt.length; j++) {
			iaWhoCnt[j] = 0;
		}

		Date dateFrom = new Date();
		//
		// calendar = Calendar.getInstance();
		// calendar.setTime(Calendar.getInstance().getTime());
		// calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
		// calendar.set(Calendar.DAY_OF_MONTH, 12);

		Calendar calendar1 = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);  //把小時歸零
		calendar.set(Calendar.MINUTE, 0);//設定分鐘
		calendar.set(Calendar.SECOND, 0);//設定秒
		calendar1.setTime(calendar.getTime());
		dateFrom.setTime(calendar.getTimeInMillis());
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
		Log.e("nick", "calendar=" + calendar.getTime().toLocaleString());
		Log.e("nick", "calendar1=" + calendar1.getTime().toLocaleString());

		iSepPos = -1;
		int iDay = 0;
		if (iPeriod != AnalysisView.YEAR && calendar.get(Calendar.DAY_OF_MONTH) == 1) {
			iSepPos = 0;
			sSepText = formatMonth(calendar.get(Calendar.MONTH));
			// trend_graph.sSepText=saMonthText[calendar.get(Calendar.MONTH)];
		}

		while (calendar.before(calendar1)) {
			// Log.d("nick", calendar.getTime().toLocaleString());
			if (iPeriod != AnalysisView.YEAR) {
				saBarText[iDay] = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
			}
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			iDay++;

			if (iPeriod != AnalysisView.YEAR) {
				if (calendar.get(Calendar.DAY_OF_MONTH) == 1 && iSepPos == -1) {
					iSepPos = iDay;
					sSepText = formatMonth(calendar.get(Calendar.MONTH));
					// trend_graph.sSepText=saMonthText[calendar.get(Calendar.MONTH)];
				}
			}
		}

		if (iPeriod == AnalysisView.YEAR) {
			iDataCount = 12;
			calendar.setTime(dateFrom);
			for (int i = 0; i < 12; i++) {
				if (calendar.get(Calendar.MONTH) == 0) {
					iSepPos = i;
					sSepText = String.valueOf(calendar.get(Calendar.YEAR));
				}
				saBarText[i] = formatMonth(calendar.get(Calendar.MONTH));
				// trend_graph.saBarText[i]=saMonthText[calendar.get(Calendar.MONTH)];
				calendar.add(Calendar.MONTH, 1);
			}
		} else {
			iDataCount = iDay;
		}

		calendar.setTime(dateFrom);
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		if (iPeriod == AnalysisView.YEAR)
			dateFormat.applyPattern("yyMM");
		else
			dateFormat.applyPattern("yyMMdd");
		String sYMD = dateFormat.format(dateFrom).toString();

		int iaRangeCnt[] = { 0, 0, 0, 0 };
		int iaRangeCntStastic[] = { 0, 0, 0, 0 };
		int iRange;
		int iDayIdx = 0;
		recordList = databaseHelper.getRecords(dateFrom, iPeriod);
		Log.e("資料筆數", recordList.size()+"");
		for (int i = 0; i < recordList.size(); i++) {
			Log.e("資料筆數", recordList.size()+"");

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

			iaSysBPStastic[iRange] += rec.HighBloodPressure;
			iaDiaBPStastic[iRange] += rec.LowBloodPressure;
			iaPulseStastic[iRange] += rec.BPHeartRate;
			// iaPulse[iDayIdx][iRange]+=rec.HeartRate;
			iaRangeCnt[iRange]++;
			iaRangeCntStastic[iRange]++;
			Log.d("alex", "@@@@iaDiaBPStastic[" + iRange + "]=" + iaDiaBPStastic[iRange] + ",iaRangeCntStastic[" + iRange + "]=" + iaRangeCntStastic[iRange]);
			// Log.d("nick", "[" + rec.sDatetime + "] Sys=" +
			// rec.HighBloodPressure + ", Dia=" + rec.LowBloodPressure +
			// ", Pulse=" + rec.BPHeartRate);

			// Dia
			if (baPeriodEnabled[iRange] == false)
				continue;
			rec.WHOIndicate = (rec.WHOIndicate == 0 ? 1 : rec.WHOIndicate);
			if (rec.WHOIndicate >= 1 && rec.WHOIndicate <= 6) {
				iaWhoCnt[rec.WHOIndicate - 1]++;
				iTotalCnt++;
			}

		}
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
		for (int j = 0; j < 4; j++) {
			if (iaRangeCnt[j] > 0) {
				iaSysBP[iDayIdx][j] /= iaRangeCnt[j];
				iaDiaBP[iDayIdx][j] /= iaRangeCnt[j];
				iaPulse[iDayIdx][j] /= iaRangeCnt[j];

			}
		}
		for (int j = 0; j < 4; j++) {
			if (iaRangeCntStastic[j] > 0) {
				iaSysBPStastic[j] /= iaRangeCntStastic[j];
				iaDiaBPStastic[j] /= iaRangeCntStastic[j];
				Log.d("alex", "iaDiaBPStastic[" + j + "]=" + iaDiaBPStastic[j] + ",iaRangeCnt[" + j + "]=" + iaRangeCnt[j]);
				iaPulseStastic[j] /= iaRangeCntStastic[j];

			}
		}

		for (int i = 0; i < iDataCount; i++) {
			for (int j = 0; j < 4; j++) {
				iaSysBP[i][j] = iaSysBP[i][j];
				iaDiaBP[i][j] = iaDiaBP[i][j];
				iaPulse[i][j] = iaPulse[i][j];
				// Log.d("nick",
				// "Sys="+iaSysBP[i][j]+", Dia="+iaDiaBP[i][j]+", Pulse="+iaPulse[i][j]);
			}
		}

	}

	public String formatMonth(int month) {
		SimpleDateFormat formatter = new SimpleDateFormat("MMM", java.util.Locale.getDefault());
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, month);
		return formatter.format(calendar.getTime());
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

	private void hideKeyboard() {
		// Check if no view has focus:
		View view = this.getCurrentFocus();
		if (view != null) {
			InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
}
