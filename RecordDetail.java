package com.mdbiomedical.app.vion.vian_health.view;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Paint.Style;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfDocument.Page;
import android.graphics.pdf.PdfDocument.PageInfo;
//import android.graphics.pdf.PdfDocument;
//import android.graphics.pdf.PdfDocument.Page;
//import android.graphics.pdf.PdfDocument.PageInfo;
import android.net.ParseException;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;

import com.mdbiomedical.app.vion.vian_health.R;
import com.mdbiomedical.app.vion.vian_health.R.drawable;
import com.mdbiomedical.app.vion.vian_health.helper.DatabaseHelper;
import com.mdbiomedical.app.vion.vian_health.model.RecordList;
import com.mdbiomedical.app.vion.vian_health.util.ChangeView;
import com.mdbiomedical.app.vion.vian_health.util.DeviceConstant;
import com.mdbiomedical.app.vion.vian_health.view.HomeView.OnDoubleClick;
import com.mdbiomedical.app.vion.vian_health.view.HomeView.timerTask;

public class RecordDetail extends Activity {
	private final static String TAG = "RecordDetail";
	private GestureDetector gd;
	private View.OnTouchListener gestureListener;
	private View.OnTouchListener scrollListener;
	public static int iHrWidth = 100, iHrHeight = 100;
	public static int iECGWidth, iECGHeight;
	TextView mea_datetime;
	String datetime, pulse, sys, dia, filename, DeviceID = "",UserMode;
	int note;
	boolean note_enter;
	Bundle bundle;
	static LinearLayout ll_home_left_side;
	LinearLayout records_hr_bkgd;
	TextView records_pulse, records_sys, records_dia, records_pulse_txt;
	EditText records_note;
	// TextView[] tvaDay = new TextView[7];
	// TextView[] tvaWeekday = new TextView[7];
	boolean bECGMode = false;
	static ECG5SecView[] ivaECG = new ECG5SecView[6];
	LinearLayout hrTrendView;
	HrTrendView hrTrendGraph;
	LinearLayout home_bg;
	Boolean bEcgInit = false;
	DatabaseHelper databaseHelper = new DatabaseHelper(this);

	String dateString = DateFormat.getBestDateTimePattern(java.util.Locale.getDefault(), "yyyy/MM/dd HH:mm");
	java.text.DateFormat dateTimeInstance = new SimpleDateFormat(dateString, java.util.Locale.getDefault());
	String birthdateString = DateFormat.getBestDateTimePattern(java.util.Locale.getDefault(), "yyyy/MM/dd ");
	java.text.DateFormat birthdateTimeInstance = new SimpleDateFormat(birthdateString, java.util.Locale.getDefault());

	static int iAnalysisType;
	public static int iEcgCount = 0;
	public static int iHrCnt = 0;
	public static short[] rawData = new short[8704];
	public static short[] HRData = new short[90];
	public static int[] HRDataTimestamp = new int[90];
	byte[] FlashBuffer = new byte[256];
	byte[] Note = new byte[256];
	public static float ecgSize = 1;
	Timer timer = new Timer(true);
	private float currentDistance = 0;
	private float lastDistance = -1;

	private float mScaleFactor = 1.f;
	private ScaleGestureDetector mScaleDetector;
	ScrollView scrollView1;
	public static boolean isBlockScroll = false;
	Calendar calendar;
	Calendar birthDateCalendar;
	// bp use

	boolean baPeriodEnabled[] = { true, false, true, false };
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
	Intent intent;
	final int iaPeriod[] = { Color.rgb(82, 170, 205), Color.rgb(43, 92, 144), Color.rgb(198, 94, 55), Color.rgb(143, 56, 45) };

	int iaWhoCnt[] = { 0, 0, 0, 0, 0, 0 };
	int faWhoPercent[] = { 0, 0, 0, 0, 0, 0 };
	int iTotalCnt = 0;
	final int iaWho[] = { Color.rgb(123, 165, 67), Color.rgb(67, 152, 55), Color.rgb(59, 128, 57), Color.rgb(217, 163, 38), Color.rgb(211, 103, 30), Color.rgb(156, 31, 35) };

	public static DisplayMetrics dm = new DisplayMetrics();
	//
ProgressDialog.Builder builder;
	
	AlertDialog dialog;
	//sandy
	ImageView mea_user;
	/*
	 * private static final int SWIPE_MIN_DISTANCE = 120; private static final
	 * int SWIPE_THRESHOLD_VELOCITY = 200; private ViewFlipper mViewFlipper;
	 * 
	 * @SuppressWarnings("deprecation") private final GestureDetector detector =
	 * new GestureDetector(new SwipeGestureDetector()); private Context
	 * mContext;
	 */
	private void hideKeyboard() {
		// Check if no view has focus:
		View view = this.getCurrentFocus();
		if (view != null) {
			InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public class timerTask extends TimerTask {
		public void run() {
			ecgSize += 0.1;
			if (ecgSize > 4)
				ecgSize = 1;
			if (bEcgInit == true) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						drawECG();
					}

				});
			}
		}
	};

	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			Log.d("alex", "get onScaleBegin");

			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector arg0) {
			Log.d("alex", "get onScaleEnd");

		}

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			mScaleFactor *= detector.getScaleFactor();
			// Don't let the object get too small or too large.
			mScaleFactor = Math.max(0.5f, Math.min(mScaleFactor, 2.0f));
			Log.d("alex", "get onScale" + mScaleFactor);
			// DoSomething

			return true;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.records_detail);
		// timer.schedule(new timerTask(), 2000, 2000);
		gd = new GestureDetector(this, new OnDoubleClick());
		gestureListener = new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				mScaleDetector.onTouchEvent(event);

				// Log.d("alex", "event.getPointerCount()=" +
				// event.getPointerCount());

				if (event.getPointerCount() >= 2) {
					isBlockScroll = true;

					float offsetX = event.getX(0) - event.getX(1);
					float offsetY = event.getY(0) - event.getY(1);
					currentDistance = (float) Math.sqrt(offsetX * offsetX + offsetY * offsetY);

					if (lastDistance < 0) {
						lastDistance = currentDistance;
					} else {
						if (currentDistance - lastDistance > 5) {
							lastDistance = currentDistance;
							ecgSize += 0.2;
							if (ecgSize > 4)
								ecgSize = 4;
							if (bEcgInit == true) {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										drawECG();
									}

								});
							}

						} else if (lastDistance - currentDistance > 5) {
							lastDistance = currentDistance;
							ecgSize -= 0.2;
							if (ecgSize < 1)
								ecgSize = 1;
							if (bEcgInit == true) {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										drawECG();
									}

								});
							}
						}

					}

				} else if (event.getPointerCount() == 1) {
					isBlockScroll = false;
					lastDistance = -1;
				}
				if (gd.onTouchEvent(event)) {

					return false;
				}

				return true;
			}

		};
		scrollListener = new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				// Log.d("alex", "disable touch isBlockScroll=" +
				// isBlockScroll);
				if (event.getPointerCount() >= 2) {
					isBlockScroll = true;

					float offsetX = event.getX(0) - event.getX(1);
					float offsetY = event.getY(0) - event.getY(1);
					currentDistance = (float) Math.sqrt(offsetX * offsetX + offsetY * offsetY);

					if (lastDistance < 0) {
						lastDistance = currentDistance;
					} else {
						if (currentDistance - lastDistance > 5) {
							lastDistance = currentDistance;
							ecgSize += 0.2;
							if (ecgSize > 4)
								ecgSize = 4;
							if (bEcgInit == true) {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										drawECG();
									}

								});
							}

						} else if (lastDistance - currentDistance > 5) {
							lastDistance = currentDistance;
							ecgSize -= 0.2;
							if (ecgSize < 0.5)
								ecgSize = (float) 0.5;
							if (bEcgInit == true) {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										drawECG();
									}

								});
							}
						}

					}

				} else if (event.getPointerCount() == 1) {
					isBlockScroll = false;
					lastDistance = -1;
				}
				return isBlockScroll;
			}

		};
		bundle = this.getIntent().getExtras();
		datetime = bundle.getString("Datetime");
		iAnalysisType = bundle.getInt("AnalysisType");
		DeviceID = bundle.getString("DeviceID");
		filename = bundle.getString("Filename");
		//sandy
		UserMode=bundle.getString("UserMode");
		Log.d("alex", "Datetime" + datetime);
		init();

		HomeView.iirlowcut.resetIIRlowcut((short) 256, 0.8f);
		HomeView.firFilter.resetFIRfilter();

		ivaECG[0].setOnTouchListener(gestureListener);
		ivaECG[1].setOnTouchListener(gestureListener);
		ivaECG[2].setOnTouchListener(gestureListener);
		ivaECG[3].setOnTouchListener(gestureListener);
		ivaECG[4].setOnTouchListener(gestureListener);
		ivaECG[5].setOnTouchListener(gestureListener);

		home_bg.setOnTouchListener(gestureListener);
		// records_hr_bkgd.setOnTouchListener(gestureListener);
		// hrTrendGraph.setOnTouchListener(gestureListener);
		hrTrendGraph.invalidate();

	}

	private void init() {
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		DeviceConstant.screenWidth = dm.widthPixels;
		DeviceConstant.screenHeight = dm.heightPixels;
		DeviceConstant.screenDPI = dm.densityDpi;
		
		ivaECG[0] = (ECG5SecView) findViewById(R.id.iv_ECG0);
		ivaECG[1] = (ECG5SecView) findViewById(R.id.iv_ECG1);
		ivaECG[2] = (ECG5SecView) findViewById(R.id.iv_ECG2);
		ivaECG[3] = (ECG5SecView) findViewById(R.id.iv_ECG3);
		ivaECG[4] = (ECG5SecView) findViewById(R.id.iv_ECG4);
		ivaECG[5] = (ECG5SecView) findViewById(R.id.iv_ECG5);

		records_pulse = (TextView) findViewById(R.id.records_pulse);
		records_pulse_txt = (TextView) findViewById(R.id.records_pulse_txt);
		records_sys = (TextView) findViewById(R.id.records_sys);
		records_dia = (TextView) findViewById(R.id.records_dia);
		records_note = (EditText) findViewById(R.id.records_note);
		records_note.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.021f));

		// tvaDay[1] = (TextView) findViewById(R.id.day_d2);
		// tvaDay[2] = (TextView) findViewById(R.id.day_d1);
		// tvaDay[3] = (TextView) findViewById(R.id.day_in0);
		// tvaDay[4] = (TextView) findViewById(R.id.day_in1);
		// tvaDay[5] = (TextView) findViewById(R.id.day_in2);
		//
		// tvaWeekday[1] = (TextView) findViewById(R.id.weekday_d2);
		// tvaWeekday[2] = (TextView) findViewById(R.id.weekday_d1);
		// tvaWeekday[3] = (TextView) findViewById(R.id.weekday_in0);
		// tvaWeekday[4] = (TextView) findViewById(R.id.weekday_in1);
		// tvaWeekday[5] = (TextView) findViewById(R.id.weekday_in2);

		ll_home_left_side = (LinearLayout) findViewById(R.id.ll_home_left_side);
		records_hr_bkgd = (LinearLayout) findViewById(R.id.records_hr_bkgd);

		hrTrendGraph = (HrTrendView) findViewById(R.id.hrTrendGraph);
		hrTrendView = (LinearLayout) findViewById(R.id.hrTrendView);
		home_bg = (LinearLayout) findViewById(R.id.content_out);
		LinearLayout ll_list_back = (LinearLayout) findViewById(R.id.ll_list_back);
		TextView tv_list_title = (TextView) findViewById(R.id.tv_list_title);
		mea_datetime = (TextView) findViewById(R.id.mea_datetime);
		//sandy
		mea_user=(ImageView) findViewById(R.id.mea_user);
		
		// tvaDay[1].setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)
		// (DeviceConstant.screenHeight * 0.03f));
		//
		// tvaDay[2].setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)
		// (DeviceConstant.screenHeight * 0.03f));
		// tvaDay[3].setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)
		// (DeviceConstant.screenHeight * 0.03f));
		//
		// tvaDay[4].setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)
		// (DeviceConstant.screenHeight * 0.03f));
		//
		// tvaDay[5].setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)
		// (DeviceConstant.screenHeight * 0.03f));
		//
		// tvaWeekday[1].setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)
		// (DeviceConstant.screenHeight * 0.026f));
		// tvaWeekday[2].setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)
		// (DeviceConstant.screenHeight * 0.026f));
		// tvaWeekday[3].setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)
		// (DeviceConstant.screenHeight * 0.026f));
		// tvaWeekday[4].setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)
		// (DeviceConstant.screenHeight * 0.026f));
		// tvaWeekday[5].setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)
		// (DeviceConstant.screenHeight * 0.026f));

		records_pulse.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.065f));
		records_sys.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.065f));
		records_dia.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.065f));

		birthDateCalendar = Calendar.getInstance();
		SharedPreferences settings = getSharedPreferences("UserInformation", 0);
		Date d = new Date();
		long b = settings.getLong("BIRTHDATE", birthDateCalendar.getTime().getTime());
		d.setTime(b);
		birthDateCalendar.setTime(d);
		
		ScrollView scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
		// scrollView1.setScrollContainer(false);
		scrollView1.setOnTouchListener(scrollListener);
		String sDateTitle = "20" + datetime.substring(0, 2) + "/" + datetime.substring(2, 4) + "/" + datetime.substring(4, 6) + " " + datetime.substring(6, 8) + ":" + datetime.substring(8, 10) + ":"
				+ datetime.substring(10, 12);

		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(records_note.getWindowToken(), 0);

		tv_list_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.03f));
		tv_list_title.setText(R.string.record_title);
		// mea_datetime.setText(sDateTitle);

		SimpleDateFormat df2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cc = Calendar.getInstance();
		try {
			cc.setTime(df2.parse(sDateTitle));
		} catch (java.text.ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		mea_datetime.setText(dateTimeInstance.format(cc.getTime()));

		Typeface tf = Typeface.createFromAsset(this.getAssets(),  "fonts/FuturaCondensed.ttf");
		if ((bundle.getString("Pulse")) != "EE") {
			
			try {
				//1109
				Log.d("sandy","(UserMode)="+UserMode);
				
				if (bundle.getString("UserMode").equals("0")) {
					Log.d("sandy", "user1");
					mea_user.setImageResource(R.drawable.user1);
					Log.d("sandy", "user1_END");
				} 
				else
				{
					Log.d("sandy", "user2");
					
					mea_user.setImageResource(R.drawable.user2);
					Log.d("sandy", "user2_END");
					
					
			     }
				
				int temp = Integer.parseInt(bundle.getString("Pulse")) & 0x00FF;
				pulse = String.valueOf(temp);
               // int usermode=Integer.parseInt(bundle.getString("UserMode")) & 0x00FF;
				records_pulse.setText(pulse);
				
				if (iAnalysisType == RecordsView.TYPE_BP) {
					
					records_pulse_txt.setText(getResources().getString(R.string.pulse));

					int int_sys = Integer.parseInt(bundle.getString("Sys")) & 0x00FF;
					int int_dia = Integer.parseInt(bundle.getString("Dia")) & 0x00FF;
					sys = String.valueOf(int_sys);
					dia = String.valueOf(int_dia);
					records_sys.setText(sys);
					records_dia.setText(dia);
					if (int_sys < 140 && int_dia < 90) {
						records_sys.setTextColor(getResources().getColor(R.color.bp_N));
						records_dia.setTextColor(getResources().getColor(R.color.bp_N));
					} else if (int_sys < 160 && int_dia < 100) {
						records_sys.setTextColor(getResources().getColor(R.color.bp_1));
						records_dia.setTextColor(getResources().getColor(R.color.bp_1));
					} else if (int_sys < 180 && int_dia < 110) {
						records_sys.setTextColor(getResources().getColor(R.color.bp_2));
						records_dia.setTextColor(getResources().getColor(R.color.bp_2));
					}

					if (int_sys >= 180 || int_dia >= 110) {
						records_sys.setTextColor(getResources().getColor(R.color.bp_3));
						records_dia.setTextColor(getResources().getColor(R.color.bp_3));
					}

				} else {
//					Log.e("sandy","bundle.getInt(UserMode)"+bundle.getInt("UserMode"));
//					if (bundle.getInt("UserMode") == 0) {
//						mea_user.setImageResource(R.drawable.user1);
//						Log.d("alex", "0");
//					} else if (bundle.getInt("UserMode") == 1) {
//						mea_user.setImageResource(R.drawable.user2);
//						Log.d("alex", "1");
//					}
					records_pulse_txt.setText(getResources().getString(R.string.hr));
					records_sys.setText("- -");
					records_dia.setText("- -");
				}
			} catch (Exception ex) {
				records_pulse.setText("EE");
				records_sys.setText("- -");
				records_dia.setText("- -");
				pulse = "EE";

				if (iAnalysisType == RecordsView.TYPE_BP)
					records_pulse_txt.setText(getResources().getString(R.string.pulse));
				else
					records_pulse_txt.setText(getResources().getString(R.string.hr));
			}

		} else// EE��??��?����
		{
			records_pulse.setText("EE");
			records_sys.setText("- -");
			records_dia.setText("- -");
			pulse = "EE";
		}

		records_pulse.setTypeface(tf);
		records_sys.setTypeface(tf);
		records_dia.setTypeface(tf);

		try {
			RecordList rec = databaseHelper.getRecord(datetime);
			if (rec != null && !rec.sNote.isEmpty()) {
				records_note.setText(rec.sNote);
				records_hr_bkgd.invalidate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		records_note.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					records_hr_bkgd.setBackgroundResource(R.drawable.bg_1b);
					records_hr_bkgd.invalidate();
					records_note.setCursorVisible(true);
				}
			}
		});

		records_note.addTextChangedListener(new TextWatcher() {
			String lastdata = "";

			@Override
			public void onTextChanged(CharSequence text, int start, int before, int count) {
				try {
					if (records_note.getLineCount() > 2) {
						records_note.setText(lastdata);
						records_note.setSelection(records_note.length());
					} else {
						databaseHelper.updateNote(datetime, String.valueOf(text));
						lastdata = text.toString();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				lastdata = s.toString();

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		ImageView img = (ImageView) findViewById(R.id.button_ecg);
		img.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				chkECG();

			}
		});
		if (iAnalysisType == RecordsView.TYPE_BP) {
			img.setVisibility(View.INVISIBLE);
		}

		// ���?????�����
		ll_list_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (bECGMode) {
					chkECG();
					return;
				}
				try {
					ChangeView.onBack();
				} catch (Exception e) {
					finish();
				}

			}
		});

		ViewTreeObserver vto2 = hrTrendGraph.getViewTreeObserver();
		vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				hrTrendGraph.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				iHrHeight = hrTrendGraph.getHeight();
				iHrWidth = hrTrendGraph.getWidth();
				Log.d("Hr", String.format("w=%d,  h=%d", iHrWidth, iHrHeight));

			}
		});

		/*
		 * mContext = this; mViewFlipper = (ViewFlipper)
		 * this.findViewById(R.id.view_flipper);
		 * mViewFlipper.setOnTouchListener(new OnTouchListener() {
		 * 
		 * @Override public boolean onTouch(final View view, final MotionEvent
		 * event) { detector.onTouchEvent(event); return true; } });
		 */
		mScaleDetector = new ScaleGestureDetector(this, new ScaleListener());
	}

	/*
	 * class SwipeGestureDetector extends SimpleOnGestureListener {
	 * 
	 * @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float
	 * velocityX, float velocityY) { try { // right to left swipe if (e1.getX()
	 * - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) >
	 * SWIPE_THRESHOLD_VELOCITY) {
	 * mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext,
	 * R.anim.left_in));
	 * mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext,
	 * R.anim.left_out)); mViewFlipper.showNext();
	 * 
	 * bECGMode=true; ll_home_left_side.setVisibility(View.VISIBLE);
	 * if(bEcgInit==false) { drawECG(); bEcgInit=true; }
	 * ll_home_left_side.invalidate(); return true; } else if (e2.getX() -
	 * e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) >
	 * SWIPE_THRESHOLD_VELOCITY) {
	 * mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext,
	 * R.anim.right_in));
	 * mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation
	 * (mContext,R.anim.right_out)); mViewFlipper.showPrevious(); return true; }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); }
	 * 
	 * return false; } }
	 */

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		HomeView.home_pressed = "disable";
		String updateHRData="",updateHRDataTimestamp="",updateRawData="",updateECG="";//0502
		final short iDrawShift = 256 * 3;
		
		if(bundle.getString("HRData")==null)
		{
		FileInputStream dataIn = null;
		DataInputStream dataInD;
		try {
			dataIn = openFileInput(filename);
			dataInD = new DataInputStream(new BufferedInputStream(dataIn));
			dataInD.read(FlashBuffer);
			iHrCnt = dataInD.readInt();
			updateHRDataTimestamp+=iHrCnt+",";
			for (int i = 0; i < HRData.length; i++) {
				HRDataTimestamp[i] = dataInD.readInt();
				HRData[i] = dataInD.readShort();
				updateHRData+=HRData[i]+";";
				updateHRDataTimestamp+=HRDataTimestamp[i]+";";
				Log.d("sandy", "HRDataTimestamp["+i+"]=" + HRDataTimestamp[i] +  ", HRData["+i+"]=" + HRData[i]);
			}
			iEcgCount = dataInD.readInt();
			short iData,iData1;//iData1=for store rawdata
			// for (int i = 0; i < iDrawShift; i++) {
			// Log.d("download", "dataInD.readShort["+i+"]="+
			// dataInD.readShort());
			// }
			
			

			//
			Log.d("sandy", "updateHRData=" + updateHRData+",updateHRDataTimestamp="+updateHRDataTimestamp);
			//Log.d("sandy", "updateRawData="+updateRawData);
			for (int i = 0; i < iDrawShift; i++) {
				iData1 = dataInD.readShort();
				updateRawData+=iData1+";";
				//Log.d("sandy", "dataInD.readShort()=" + dataInD.readShort() );
				iData = HomeView.iirlowcut.IIRlowcutFiltering(iData1);
				iData = HomeView.firFilter.FIRfiltering(iData);
				updateECG+=iData+";";
			}

			for (int i = 0; i < rawData.length - iDrawShift; i++) {
				iData1 = dataInD.readShort();
				updateRawData+=iData1+";";
				iData = HomeView.iirlowcut.IIRlowcutFiltering(iData1);
				rawData[i] = HomeView.firFilter.FIRfiltering(iData);
				updateECG+=rawData[i]+";";
				//Log.d("sandy", "rawData["+i+"]=" + rawData[i] );
			}
			//Log.d("sandy", "updateECG="+updateECG);
			dataInD.read(Note);

			dataInD.close();
			Log.d(TAG, "iHrCnt=" + iHrCnt + ", iEcgCount=" + iEcgCount);
			Log.d("sandy", "iHrCnt=" + iHrCnt + ", iEcgCount=" + iEcgCount);
			RecordList rec = new RecordList();
			//rec.sDatetime=datetime;
			rec.ECG=updateECG;
			rec.RawData=updateRawData;
			rec.HRData=updateHRData;
			rec.HRDataTimeStamp=updateHRDataTimestamp;
			databaseHelper.updateECGdata(datetime, rec);
		
		} catch (Exception e) {
			Log.d("sandy", "no fileInputStream="+e.getMessage());
			e.printStackTrace();
		}
		}
		else
		{
			String[] aArray = bundle.getString("HRDataTimeStamp").split(",");
			iHrCnt=Integer.parseInt(aArray[0]);
			String[] bArray=aArray[1].split(";");
			String[] cArray = bundle.getString("HRData").split(";");
			for(int i=0;i < HRData.length; i++)
			{
				HRDataTimestamp[i] =Integer.parseInt(bArray[i]) ;
				HRData[i] =Short.parseShort(cArray[i]);
				Log.d("sandy", "nofilter_HRDataTimestamp["+i+"]=" + HRDataTimestamp[i] +  ", HRData["+i+"]=" + HRData[i]);
			}
			aArray = bundle.getString("ECG").split(";");
			Log.d("sandy", "aArray="+aArray.length);
			//HRDataTimestamp=aArray[1];
			for (int i = 0; i < rawData.length - iDrawShift; i++) {
				rawData[i] =Short.parseShort(aArray[iDrawShift+i]);
			}
			iEcgCount =8704;
			
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat();

		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.valueOf(datetime.substring(0, 2)) + 2000, Integer.valueOf(datetime.substring(2, 4)), Integer.valueOf(datetime.substring(4, 6)), Integer.valueOf(datetime.substring(6, 8)),
				Integer.valueOf(datetime.substring(8, 10)), Integer.valueOf(datetime.substring(10, 12)));
		calendar.add(Calendar.DAY_OF_MONTH, -2);
		
		String today;
		// for (int i = 1; i < tvaDay.length - 1; i++) {
		// Date tdt = calendar.getTime();// ������??��?����?�ate
		// dateFormat.applyPattern("dd");
		// today = dateFormat.format(tdt).toString();
		// tvaDay[i].setText(today);
		// dateFormat.applyPattern("EE");
		// today = dateFormat.format(tdt).toString();
		// tvaWeekday[i].setText(today);
		// calendar.add(Calendar.DAY_OF_MONTH, 1);
		// }
		hrTrendGraph.invalidate();
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
		System.gc();
	}

	@Override
	public void onBackPressed() {

		if (bECGMode) {
			chkECG();
			return;
		}
		RecordDetail.iHrCnt = 0;
		setResult(RESULT_OK);
		finish();
	}

	public void onNoteClick(View v) {
		Log.d("nick", "onClick");
		records_hr_bkgd.setBackgroundResource(R.drawable.bg_1b);
		records_hr_bkgd.invalidate();
		records_note.setCursorVisible(true);
	}

	public static void drawECG() {
		iECGWidth = ll_home_left_side.getWidth();
		// iECGHeight = ll_home_left_side.getHeight();
		iECGHeight = ll_home_left_side.getHeight();
		// for (int i = 0; i < ivaECG.length; i++) {
		// LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
		// ivaECG[i].getLayoutParams();
		// params.height = iECGHeight*6;
		//
		// ivaECG[i].setLayoutParams(params);
		// ivaECG[i].invalidate();
		//
		// }

		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ivaECG[0].getLayoutParams();
		params.height = iECGHeight * 6;

		ivaECG[0].setLayoutParams(params);
		ivaECG[0].invalidate();

	}

	void chkECG() {
		// sharepdf();
		ecgSize = 1;
		hideKeyboard();
		if (iAnalysisType == RecordsView.TYPE_BP)
			return;
		if (bECGMode == false) {
			bECGMode = true;
			records_note.setCursorVisible(false);
			ll_home_left_side.setVisibility(View.VISIBLE);
			if (bEcgInit == false) {
				drawECG();
				bEcgInit = true;
			}
		} else {
			bECGMode = false;
			bEcgInit = false;
			records_note.setFocusable(true);
			ll_home_left_side.setVisibility(View.INVISIBLE);
		}
	}

	public void onRecordClick(View v) {
		// Log.d("click", String.format("[click] w=%d,  h=%d",
		// ll_home_left_side.getWidth(), ll_home_left_side.getHeight()));

		View view = this.getCurrentFocus();
		if (view != null) {// ??��?��迤�??�詨?�� ���?��?����?��?���?��??���?���?????���?????
			InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			records_note.setCursorVisible(false);
			records_note.setFocusable(false);
			return;
		}

		if (iAnalysisType == RecordsView.TYPE_BP)
			return;// ??��?���?BP??��� ??��?��?�蝷箏�?���?????���?????

		chkECG();

	}
	File file;
	public void sharepdf() {

		Log.d("alex", "in the command");
		//Toast.makeText(RecordDetail.this, iHrWidth + " x " + iHrHeight, Toast.LENGTH_LONG).show();

		//try {
			File file1 = new File("/mnt/sdcard/Vian_Health/");
			if (!file1.exists()) {
				file1.mkdirs();
			}

			file = new File("/mnt/sdcard/Vian_Health", "ECG Report"+ ".pdf");
			PrintAttributes printAttrs = new PrintAttributes.Builder().setColorMode(PrintAttributes.COLOR_MODE_COLOR).setMediaSize(PrintAttributes.MediaSize.ISO_A4)
					.setResolution(new Resolution("zooey", PRINT_SERVICE, 5000, 5000)).setMinMargins(Margins.NO_MARGINS).build();
			PdfDocument document = new PrintedPdfDocument(RecordDetail.this, printAttrs);
			PageInfo pageInfo = new PageInfo.Builder(595, 842, 1).create();
			Page page = document.startPage(pageInfo);

			if (page != null) {
				// View view = findViewById(R.id.hrTrendGraph);//
				// getContentView();
				// here the solution
				int drawLeft = 30;
				int drawTop = 150;
				int width = 40;// cannot fit when size too big
				int height = 20;
				int pdfECGWidth = pageInfo.getPageWidth();
				Log.d("alex", "pdfECGWidth" + pdfECGWidth);
				int pdfECGHeight = pageInfo.getPageHeight() - height;
				Log.d("alex", "pdfECGHeight" + pdfECGHeight);
				Paint myPaint = new Paint();
				Path trace = new Path();
				int iIndex = 0;

				float iGridHeight = pdfECGHeight / 5;
				float iGridWidth = 75;
				float iCellWidth = 15;
				float iSmallCellWidth = 3;
				float lastx = drawLeft + iSmallCellWidth * 5 * 35;
				float lasty = drawTop + iSmallCellWidth * 5 * 40;

				float fGen = 65536 / 600;
				float fMvAmp = pdfECGHeight * 2 / 27;
				float fPixelPerAmp = fMvAmp / fGen/2;

				float iBaseLine = drawTop + iCellWidth * 6;

				int ecg_grid1 = getResources().getColor(R.color.ecg_grid1);
				int ecg_grid2 = getResources().getColor(R.color.ecg_grid2);
				int ecg_grid3 = getResources().getColor(R.color.ecg_grid3);
//				Options options = new BitmapFactory.Options();
//				options.inScaled = false;
//				Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.bluetooth_icon_2, options);
//
//				Bitmap logoResized = Bitmap.createScaledBitmap(logo, 100, 50, false);
//				page.getCanvas().drawBitmap(logoResized, lastx - 100, 100, myPaint);
//
//				myPaint.setStrokeWidth((float) 0.2);
//				myPaint.setTextSize(12);
//				myPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
//				page.getCanvas().drawText("Electrocardiogram Report", drawLeft, drawTop - iCellWidth * 6 - 10, myPaint);
//				myPaint.setStrokeWidth((float) 2);
//				page.getCanvas().drawLine(drawLeft, drawTop - iCellWidth * 6, lastx, drawTop - iCellWidth * 6, myPaint);
//				myPaint.setStrokeWidth((float) 0.2);
//				myPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
//				myPaint.setTextSize(10);
//				page.getCanvas().drawText("Hardware number:", drawLeft, drawTop - iCellWidth * 5, myPaint);
//				page.getCanvas().drawText("Record Time:", drawLeft, drawTop - iCellWidth * 4, myPaint);
//				page.getCanvas().drawText("Note:", drawLeft, drawTop - iCellWidth * 3, myPaint);
//				page.getCanvas().drawText("Hearth Rate:", drawLeft + (lastx - drawLeft) / 2, drawTop - iCellWidth * 5, myPaint);
//				page.getCanvas().drawText(mea_datetime.getText() + "", drawLeft + 100, drawTop - iCellWidth * 4, myPaint);
//				page.getCanvas().drawText(DeviceID + "", drawLeft + 100, drawTop - iCellWidth * 5, myPaint);
//				page.getCanvas().drawText(pulse + "", drawLeft + (lastx - drawLeft) / 2 + 100, drawTop - iCellWidth * 5, myPaint);
//				page.getCanvas().drawText("bpm", drawLeft + (lastx - drawLeft) / 2 + 120, drawTop - iCellWidth * 5, myPaint);
//
//				myPaint.setColor(Color.GRAY);
//				myPaint.setStyle(Paint.Style.STROKE);
//				myPaint.setStrokeWidth((float) 0.5);
//				page.getCanvas().drawLine(drawLeft, drawTop - iCellWidth * 2, lastx, drawTop - iCellWidth * 2, myPaint);
//				page.getCanvas().drawLine(drawLeft, drawTop - iCellWidth, lastx, drawTop - iCellWidth, myPaint);
				SharedPreferences settings = getSharedPreferences("UserInformation", 0);
				String name = settings.getString("FIRST_NAME", "");
				name = name + " " + settings.getString("LAST_NAME", "");
				int gender_order = settings.getInt("GENDER", 0);
				String gender;
				Log.e("GENDER", "gender_order = "+gender_order);
				if(gender_order==0)
					gender=getString(R.string.Male);
				else
					gender=getString(R.string.Female);
				String userHeight = settings.getString("HEIGHT", "170");
				String userWeight = settings.getString("WEIGHT", "65");
				float userHeightF = Float.valueOf(settings.getString("HEIGHT", "170")) / 100;
				float userWeightF = Float.valueOf(settings.getString("WEIGHT", "65"));
				myPaint.setStrokeWidth((float) 0.2);
				myPaint.setTextSize(12);
				myPaint.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/arial.ttf"));
				page.getCanvas().drawText(getResources().getString(R.string.ecg_title), drawLeft, drawTop - iCellWidth * 6 - 10, myPaint);
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
				page.getCanvas().drawText(birthdateTimeInstance.format(birthDateCalendar.getTime()) + "", drawLeft + 130, drawTop - iCellWidth * 5 + iSmallCellWidth * 6, myPaint);
				page.getCanvas().drawText(getResources().getString(R.string.height) + ":", drawLeft, drawTop - iCellWidth * 5 + iSmallCellWidth * 9, myPaint);
				page.getCanvas().drawText(userHeight + " (cm)", drawLeft + 130, drawTop - iCellWidth * 5 + iSmallCellWidth * 9, myPaint);
				page.getCanvas().drawText(getResources().getString(R.string.weight) + ":", drawLeft, drawTop - iCellWidth * 5 + iSmallCellWidth * 12, myPaint);
				page.getCanvas().drawText(userWeight + " (kg)", drawLeft + 130, drawTop - iCellWidth * 5 + iSmallCellWidth * 12, myPaint);
				page.getCanvas().drawText(getResources().getString(R.string.BMI) + ":", drawLeft, drawTop - iCellWidth * 5 + iSmallCellWidth * 15, myPaint);
				page.getCanvas().drawText("" + String.format("%.1f", userWeightF / (userHeightF * userHeightF)) + "", drawLeft + 130, drawTop - iCellWidth * 5 + iSmallCellWidth * 15, myPaint);

				page.getCanvas().drawText(getResources().getString(R.string.heartrate) + ":", drawLeft + (lastx - drawLeft) / 2, drawTop - iCellWidth * 5 + iSmallCellWidth * 6, myPaint);
				myPaint.setTextSize(24);
				page.getCanvas().drawText(records_pulse.getText()+"", drawLeft + (lastx - drawLeft) / 2 + 130, drawTop - iCellWidth * 5 + iSmallCellWidth * 10, myPaint);
				myPaint.setTextSize(9);
				page.getCanvas().drawText(getResources().getString(R.string.bpm), drawLeft + (lastx - drawLeft) / 2 + 160, drawTop - iCellWidth * 5 + iSmallCellWidth * 12, myPaint);
				page.getCanvas().drawText(getResources().getString(R.string.recordedfrom) + ":", drawLeft + (lastx - drawLeft) / 2, drawTop - iCellWidth * 5+ iSmallCellWidth * 3, myPaint);
				page.getCanvas().drawText(mea_datetime.getText() + "", drawLeft + (lastx - drawLeft) / 2 + 130, drawTop - iCellWidth * 5+ iSmallCellWidth * 3, myPaint);
				// Draw ECG background
				myPaint.setColor(ecg_grid1);
				myPaint.setStyle(Paint.Style.STROKE);
				myPaint.setStrokeWidth(1);
				for (int i = drawTop; i < lasty + 1; i += iCellWidth) {// ??��?���?????
					myPaint.setStrokeWidth(1);
					page.getCanvas().drawLine(drawLeft, i, lastx, i, myPaint);
					myPaint.setStrokeWidth((float) 0.5);

					for (float k = i + iSmallCellWidth; k < i + iSmallCellWidth * 5 && k < lasty; k += iSmallCellWidth) {

						page.getCanvas().drawLine(drawLeft, k, lastx, k, myPaint);// ??�唳帖蝺�?????
					}
				}

				myPaint.setStyle(Paint.Style.FILL_AND_STROKE);

				for (float k = drawLeft; k < lastx; k += iSmallCellWidth) {
					myPaint.setStrokeWidth((float) 0.5);
					myPaint.setColor(ecg_grid1);
					page.getCanvas().drawLine(k, drawTop, k, lasty, myPaint);// ??��?��?�蝺�?????
				}
				for (float j = drawLeft; j < lastx; j += iCellWidth) { // ��?�蝺�?????
					myPaint.setColor(ecg_grid2);
					myPaint.setStrokeWidth((float) 0.5);
					page.getCanvas().drawLine(j, drawTop, j, lasty, myPaint);
				}
				for (float i = drawLeft; i < lastx + 1; i += iGridWidth) {// ??��?��?�蝺�?????
					myPaint.setColor(ecg_grid3);
					myPaint.setStrokeWidth((float) 1.2);
					page.getCanvas().drawLine(i, drawTop, i, lasty, myPaint);

				}

				myPaint.setColor(Color.BLACK);
				myPaint.setStrokeWidth(1);

				page.getCanvas().drawLine(drawLeft + iCellWidth, drawTop + iCellWidth * 3, drawLeft + iCellWidth + iSmallCellWidth, drawTop + iCellWidth * 3, myPaint);
				page.getCanvas().drawLine(drawLeft + iCellWidth + iSmallCellWidth, drawTop + iCellWidth * 3, drawLeft + iCellWidth + iSmallCellWidth, drawTop + iCellWidth, myPaint);
				page.getCanvas().drawLine(drawLeft + iCellWidth + iSmallCellWidth, drawTop + iCellWidth, drawLeft + iCellWidth + iSmallCellWidth * 3, drawTop + iCellWidth, myPaint);
				page.getCanvas().drawLine(drawLeft + iCellWidth + iSmallCellWidth * 3, drawTop + iCellWidth, drawLeft + iCellWidth + iSmallCellWidth * 3, drawTop + iCellWidth * 3, myPaint);
				page.getCanvas().drawLine(drawLeft + iCellWidth + iSmallCellWidth * 3, drawTop + iCellWidth * 3, drawLeft + iCellWidth + iSmallCellWidth * 4, drawTop + iCellWidth * 3, myPaint);
				page.getCanvas().save();
				myPaint.setStyle(Paint.Style.FILL_AND_STROKE);
				myPaint.setStrokeWidth((float) 0.5);
				myPaint.setTextSize(5);
				page.getCanvas().drawText("1 mV", drawLeft + iCellWidth, drawTop + iCellWidth * 3 + iSmallCellWidth * 3, myPaint);
				page.getCanvas().restore();
				page.getCanvas().save();

				myPaint.setStyle(Paint.Style.STROKE);
				myPaint.setColor(Color.rgb(0, 0, 0));
				myPaint.setStrokeWidth((float) 0.5);
				// float fPos_forloop = drawLeft;
				float fPos = 0;
				for (int index_x = 0; index_x < 5; index_x++) {
					iIndex = index_x;
					float moveBaseLine = iBaseLine + iCellWidth * 7 * index_x;
					float fPos_forloop = drawLeft;
					int iDrawSt = (iIndex * 7 * 256);
					int iDrawCnt = ((iIndex + 1) * 7 * 256);

					if (iDrawCnt > RecordDetail.iEcgCount)
						iDrawCnt = RecordDetail.iEcgCount;
					iDrawCnt -= iDrawSt;
					if(index_x==4)
					{
						iDrawCnt = iDrawCnt-4*256;
					}
					Log.d("alex", "Index=" + iIndex + ", iDrawSt=" + iDrawSt + ", iDrawCnt=" + iDrawCnt);

					trace.rewind();
					if (iDrawSt > 0)
						iDrawSt -= 1;
					float fAmp = RecordDetail.rawData[iDrawSt] * fPixelPerAmp;// get
																				// start
																				// y
					// x��
					if (fAmp > moveBaseLine)
						fAmp = moveBaseLine;
					else if (fAmp < 0 - moveBaseLine)
						fAmp = 0 - moveBaseLine;
					Log.e(TAG,index_x+". trace.moveTo x = "+fPos_forloop + fAmp+"  y = "+ (moveBaseLine - fAmp));
					trace.moveTo(fPos_forloop, moveBaseLine - fAmp);// start
																	// point for
																	// each line

					for (int i = 0; i < iDrawCnt; i++) {
						fPos = i * iGridWidth / 256 + fPos_forloop;
						fAmp = RecordDetail.rawData[i + iDrawSt] * fPixelPerAmp;
						if (fAmp > moveBaseLine)
							fAmp = moveBaseLine;
						else if (fAmp < 0 - moveBaseLine)
							fAmp = 0 - moveBaseLine;

						trace.lineTo(fPos, moveBaseLine - fAmp);

					}
					Log.e(TAG,index_x+". trace.moveTo  fPos = " + fPos);
					fPos_forloop = fPos;
					page.getCanvas().drawPath(trace, myPaint);
					page.getCanvas().save();

				}

			}
			document.finishPage(page);
			builder = new ProgressDialog.Builder(RecordDetail.this);
			builder.setMessage("Loading....").setTitle("");

			dialog = builder.create();

			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
			
			new sendmail().execute(document);
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
				email.putExtra(Intent.EXTRA_SUBJECT, "ECG Report");
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
//			document.finishPage(page);
//			FileOutputStream os = new FileOutputStream(file);
//			document.writeTo(os);
//			document.close();
//			os.close();
//			Log.i("alex", file.getAbsolutePath().toString());
//
//			Intent email = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
//			email.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });
//			email.setData(Uri.parse(""));
//			email.putExtra(Intent.EXTRA_SUBJECT, "ECG Report");
//			email.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath().toString()));
//			email.setType("application/pdf");
//			email.putExtra(Intent.EXTRA_TEXT, "Send Report from "+getResources().getString(R.string.app_name));
//			startActivity(email);
//
//		} catch (IOException e) {
//			throw new RuntimeException("Error generating file", e);
//		}
//
//		//
//		// records_hr_trend.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//
//	}

	

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

		calendar = Calendar.getInstance();
		calendar.setTime(Calendar.getInstance().getTime());
		calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, 12);
		
		Calendar calendar1 = Calendar.getInstance();
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
		Log.d("nick", "calendar=" + calendar.getTime().toLocaleString());
		Log.d("nick", "calendar1=" + calendar1.getTime().toLocaleString());

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
		for (int i = 0; i < recordList.size(); i++) {

			RecordList rec = recordList.get(i);

			if (rec.AnalysisType != RecordsView.TYPE_BP || rec.BPMNoiseFlag != 0)
				continue;

			int iHr = Integer.valueOf(rec.sDatetime.substring(6, 8));
			if (iHr >= 5 && iHr < 9)
				iRange = 0;
			else if (iHr >= 10 && iHr <= 14)
				iRange = 1;
			else if (iHr >= 18 && iHr < 20)
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
			// Log.d("alex","iaDiaBPStastic["+iRange+"]="+iaDiaBPStastic[iRange]+",iaRangeCntStastic["+iRange+"]="+iaRangeCntStastic[iRange]);
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
				iaSysBPStastic[j] /= iaRangeCntStastic[j];
				iaDiaBPStastic[j] /= iaRangeCntStastic[j];
				// Log.d("alex","iaDiaBPStastic["+j+"]="+iaDiaBPStastic[j]+",iaRangeCnt["+j+"]="+iaRangeCnt[j]);
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

	public void sendpdf(View v){
		if (iAnalysisType == RecordsView.TYPE_BP) {
			intent = new Intent();
			intent.setClass(RecordDetail.this, BpPdfChooseView.class);
			startActivityForResult(intent, 136);
			overridePendingTransition(R.anim.slide_in_right, R.anim.left_out);
				
		}
		else if(iAnalysisType == RecordsView.TYPE_ECG) {
			sharepdf();
		}
		//
	}

	public String formatMonth(int month) {
		SimpleDateFormat formatter = new SimpleDateFormat("MMM", java.util.Locale.getDefault());
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, month);
		return formatter.format(calendar.getTime());
	}

	public class OnDoubleClick extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			// chkECG();
			
			return false;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {

			return super.onDoubleTapEvent(e);
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			hideKeyboard();
			return false;
		}
	}
	

}
