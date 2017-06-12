package com.mdbiomedical.app.vion.vian_health.view;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mdbiomedical.app.vion.vian_health.R;
import com.mdbiomedical.app.vion.vian_health.helper.DatabaseHelper;
import com.mdbiomedical.app.vion.vian_health.model.RecordList;
import com.mdbiomedical.app.vion.vian_health.service.BluetoothLeService;
import com.mdbiomedical.app.vion.vian_health.service.SampleGattAttributes;
import com.mdbiomedical.app.vion.vian_health.util.ChangeView;
import com.mdbiomedical.app.vion.vian_health.util.DeviceConstant;
import com.mdbiomedical.app.vion.vian_health.util.UIUtils;

//import com.mdbiomedical.app.vion.view.HomeView.timerTask;

public class DownloadView extends Activity {
	public static final byte BT_WAIT = 0;
	public static final byte BT_HEADER = 1;
	public static final byte BT_STANDBY = 2;
	public static final byte BT_MEASURE = 3;
	public static final byte BT_DOWNLOAD = 4;
	public static final byte BT_DOWNLOAD_WAIT = 0;
	public static final byte BT_DOWNLOAD_U1_U2_COUNT = 1;
	public static final byte BT_DOWNLOAD_HEADER = 2;
	public static final byte BT_DOWNLOAD_RAWD = 3;

	public static final byte BT_SETUP = 5;
	public static final byte BT_SETUP_ID = 1;
	public static final byte BT_SETUP_700X = 2;

	public static final byte BT_ERASE_ALL_FLASH = 6;
	public static final byte BT_CONFIG_INFO = 7;
	public static final byte BT_CONFIG_INFO_DEVICE = 1;
	public static final byte BT_CONFIG_INFO_SETTING = 2;
	public static final byte BT_START = 8;
	public static final byte BT_START_BP = 1;
	public static final byte BT_START_ECG = 2;
	public static final byte BT_START_BP_ECG = 3;
	public final int BT_KEEP_ALIVE = 9;
	final int TABLE_LIST_SIZE = 6;

	LinearLayout ll_download_table;
	// List<PlayRecords> myDailyPlayResult = new ArrayList<PlayRecords>();
	List<RecordList> newRecordList = new ArrayList<RecordList>();
	List<short[]> sheaderList = new ArrayList<short[]>();
	List<byte[]> headerList = new ArrayList<byte[]>();
	ArrayList<Integer> checkedList = new ArrayList<Integer>();
	DatabaseHelper databaseHelper = new DatabaseHelper(this);
	String dateString = DateFormat.getBestDateTimePattern(java.util.Locale.getDefault(), "yyyy/MM/dd HH:mm");
	java.text.DateFormat dateTimeInstance = new SimpleDateFormat(dateString, java.util.Locale.getDefault());
	Toast tProc = null;
	ProgressDialog procDialog;
	int iDownloadFileCnt;

	//
	ProgressDialog progressDialog;
	//
	
	private final static String TAG = BluetoothLeService.class.getSimpleName();
	private BluetoothLeService mBluetoothLeService;
	private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	private final String LIST_NAME = "NAME";
	private final String LIST_UUID = "UUID";
	private BluetoothGattCharacteristic characteristic1;
	private BluetoothGattCharacteristic characteristic2;
	private int tmpseq = 0, seq = 0;
	private int lost = 0,q=1;
	private int state;
	private int sec;
	private boolean sendcmdflag;
	private byte[] data = new byte[20];
	Timer timer = new Timer(true);
	private static final int STATE_DISCONNECTED = 0;
	private static final int STATE_CONNECTING = 1;
	private static final int STATE_CONNECTED = 2;
	private char retryCmdCount;
	private char writeFlag;
	private char showCancelFlag;
	private int recordsCount1;
	private int recordsCount2;
	private int headerSeq;
	private int downloadCount;
	private int downloadBufSeq;
	private char downloadflag;
	private int currentSize;
	private int totalSize = 20480;
	private char cancelFlag;
	private byte[] FlashBuffer = new byte[256];
	private RecordList header = new RecordList();
	private byte[] rawDataBuf = new byte[20736];

	byte[] h;
	List<Integer> sortData = new ArrayList<Integer>();
	//1202
	Comparator<RecordList> comparator ;
	byte[] h1;
	
	//
	int [] deviceID=new int[40];
	//
	
	List<byte[]> headerList1 = new ArrayList<byte[]>();
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download_activity);
Log.d("sandy", "DownloadView....oncreate()");
		init();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		mBluetoothLeService = HomeView.mBluetoothLeService;
		characteristic2 = HomeView.characteristic2;
		timer.schedule(new SendDownloadCmd(), 1000, 1000);
		sendcmdflag = true;

	}

	public int addViewPostion() {
		int lastdata = sortData.size();
		int temp = 0;
		if (lastdata == 0)
			return 0;
		for (int i = 0; i < sortData.size(); i++) {
			if (sortData.get(lastdata) < sortData.get(i)) {
				sortData.add(i, sortData.get(lastdata - 1));
				sortData.remove(lastdata - 1);
				return i;
			}
		}

		return 0;
	}

	/*
	 * private void setRecordList() { ll_download_table.removeAllViews();
	 * 
	 * for (int i = (recordViewCount - 1); i >= 0; i--) {
	 * 
	 * try { addRecordView(i); } catch (Exception e) { e.printStackTrace(); } }
	 * 
	 * if (recordViewCount < TABLE_LIST_SIZE) { addEmptytable(TABLE_LIST_SIZE -
	 * recordViewCount); }
	 * 
	 * }
	 */
	@Override
	protected void onResume() {
		super.onResume();
		// loadData();
		HomeView.home_pressed = "disable";
		int version = Integer.valueOf(android.os.Build.VERSION.SDK);
		if (version >= 11) {
		} else {
		}

		// setRecordList();

		if (mBluetoothLeService == null || HomeView.ble_status != 2) {

			Timer single_timer = new Timer();
			single_timer.schedule(new TimerTask() {
				@Override
				public void run() {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							addEmptytable(TABLE_LIST_SIZE);
						}
					});
				}
			}, 100);
		}
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
		timer.cancel();
		unregisterReceiver(mGattUpdateReceiver);
		Log.d("download", "unregisterReceiver() on onDestroy");
		// System.gc();
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
				
		ImageView iv_record_menu;
		iv_record_menu = (ImageView) findViewById(R.id.ll_go_back);
		ll_download_table = (LinearLayout) findViewById(R.id.ll_download_table);
		TextView tv_records_title = (TextView) findViewById(R.id.tv_download_title);

		progressDialog = new ProgressDialog(DownloadView.this);
		//1202
		 comparator = new Comparator<RecordList>() {
				public int compare(RecordList s1, RecordList s2) {
					//Log.d("sandy","s1="+s1.sDatetime+",s2="+s2.sDatetime);
					
					return s1.sDatetime.compareTo(s2.sDatetime);
				}
			};
		
		tv_records_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.03f));

		// 回上一頁
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
		if (characteristic2 != null && HomeView.ble_status == 2) {
			data[1] = BT_STANDBY;
			mBluetoothLeService.writeCharacteristic(characteristic2, data);
		}
		timer.cancel();
		// unregisterReceiver(mGattUpdateReceiver);

		setResult(RESULT_OK);
		finish();
		overridePendingTransition(R.anim.slide_no, R.anim.slide_out_left);

	}

	public void showMessage(Context context, int title, int msg) {
		new AlertDialog.Builder(context).setTitle(title).setMessage(msg).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface df, int i) {
				if (HomeView.ble_status != 2)
					onBackPressed();
			}
		}).show();

	}

	public void leaveMessage(Context context, int title, int msg) {
		new AlertDialog.Builder(context).setTitle(title).setMessage(msg).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface df, int i) {
				onBackPressed();
			}
		}).show();

	}

	public View addRecordView(final int i) {

		LayoutInflater factory = LayoutInflater.from(this);
		View myView = factory.inflate(R.layout.records_content_activity, null);

		LinearLayout ll_record_daily_sys = (LinearLayout) myView.findViewById(R.id.ll_record_daily_sys);
		LinearLayout ll_record_daily_dia = (LinearLayout) myView.findViewById(R.id.ll_record_daily_dia);
		LinearLayout ll_record_daily_bp_icon = (LinearLayout) myView.findViewById(R.id.ll_record_daily_bp_icon);
		LinearLayout ll_record_daily_bp_count = (LinearLayout) myView.findViewById(R.id.ll_record_daily_bp_count);
	//	LinearLayout sys_txt = (LinearLayout) myView.findViewById(R.id.sys_txt);
	//	LinearLayout dia_txt = (LinearLayout) myView.findViewById(R.id.dia_txt);

		int[] whoResId = { R.drawable.icon_green, R.drawable.icon_green, R.drawable.icon_green, R.drawable.icon_yellow, R.drawable.icon_orange, R.drawable.icon_red };
		TextView record_pulse_txt = (TextView) myView.findViewById(R.id.record_pulse_txt);
		TextView tv_record_date = (TextView) myView.findViewById(R.id.tv_record_date_text);
		LinearLayout ll_record_content = (LinearLayout) myView.findViewById(R.id.ll_record_content);
		TextView record_pulse = (TextView) myView.findViewById(R.id.record_pulse);
		TextView record_sys = (TextView) myView.findViewById(R.id.record_sys);
		TextView record_dia = (TextView) myView.findViewById(R.id.record_dia);
		Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/FuturaCondensed.ttf");
		record_pulse.setTypeface(tf);
		record_sys.setTypeface(tf);
		record_dia.setTypeface(tf);

		ImageView[] whoIcon = new ImageView[6];
		whoIcon[0] = (ImageView) myView.findViewById(R.id.who0);
		whoIcon[1] = (ImageView) myView.findViewById(R.id.who1);
		whoIcon[2] = (ImageView) myView.findViewById(R.id.who2);
		whoIcon[3] = (ImageView) myView.findViewById(R.id.who3);
		whoIcon[4] = (ImageView) myView.findViewById(R.id.who4);
		whoIcon[5] = (ImageView) myView.findViewById(R.id.who5);
		ImageView ecg_ok = (ImageView) myView.findViewById(R.id.ecg_ok);
		ImageView ecg_rhythm = (ImageView) myView.findViewById(R.id.ecg_rhythm);
		ImageView ecg_wave = (ImageView) myView.findViewById(R.id.ecg_wave);
		final CheckBox chk = (CheckBox) myView.findViewById(R.id.chk_record);
		chk.setVisibility(View.VISIBLE);

		//sandy
		LinearLayout ll_record_daily_bp=(LinearLayout) myView.findViewById(R.id.ll_record_daily_bp);
		LinearLayout ll_record_daily_ecg=(LinearLayout) myView.findViewById(R.id.ll_record_daily_ecg);
		ImageView ecg_slow = (ImageView) myView.findViewById(R.id.ecg_slow);
		ImageView ecg_fast = (ImageView) myView.findViewById(R.id.ecg_fast);
		ImageView ecg_pause = (ImageView) myView.findViewById(R.id.ecg_pause);
		//
		chk.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked)
					checkedList.add(Integer.valueOf(i));
				else
					checkedList.remove(Integer.valueOf(i));

				Log.d("sandy", "i=" + i + ", Checked=" + isChecked + ", List Cnt=" + checkedList.size());
			}
		});

		RecordList rec = newRecordList.get(i);
        //Log.d("sandy", "datetime="+rec.sDatetime+",seq="+rec.Seq);
		String datetime = "20" + rec.sDatetime.substring(0, 2) + "/" + rec.sDatetime.substring(2, 4) + "/" + rec.sDatetime.substring(4, 6) + " " + rec.sDatetime.substring(6, 8) + ":"
				+ rec.sDatetime.substring(8, 10) + ":" + rec.sDatetime.substring(10, 12);

		// tv_record_date.setText(datetime);
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cc = Calendar.getInstance();
		try {
			cc.setTime(df2.parse(datetime));
		} catch (java.text.ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		tv_record_date.setText(dateTimeInstance.format(cc.getTime()));
		if (rec.AnalysisType == RecordsView.TYPE_BP) {
ll_record_daily_bp.setVisibility(View.VISIBLE);
			ll_record_daily_ecg.setVisibility(View.INVISIBLE);
			record_pulse_txt.setText(getResources().getString(R.string.pulse));
			// invisible some text
			ecg_rhythm.setVisibility(View.INVISIBLE);
			ecg_wave.setVisibility(View.INVISIBLE);
			//ecg_ok.setVisibility(View.INVISIBLE);
	ecg_pause.setVisibility(View.INVISIBLE);
			if (rec.BPMNoiseFlag == 0) {
				rec.BPHeartRate = (rec.BPHeartRate & 0x00FF);
				record_pulse.setText(String.valueOf(rec.BPHeartRate));

				rec.HighBloodPressure = (rec.HighBloodPressure & 0x00FF);
				record_sys.setText(String.valueOf(rec.HighBloodPressure));

				rec.LowBloodPressure = (rec.LowBloodPressure & 0x00FF);
				record_dia.setText(String.valueOf(rec.LowBloodPressure));

				if (rec.HighBloodPressure < 140 && rec.LowBloodPressure < 90) {
					record_sys.setTextColor(getResources().getColor(R.color.bp_N));
					record_dia.setTextColor(getResources().getColor(R.color.bp_N));
				} else if (rec.HighBloodPressure < 160 && rec.LowBloodPressure < 100) {
					record_sys.setTextColor(getResources().getColor(R.color.bp_1));
					record_dia.setTextColor(getResources().getColor(R.color.bp_1));
				} else if (rec.HighBloodPressure < 180 && rec.LowBloodPressure < 110) {
					record_sys.setTextColor(getResources().getColor(R.color.bp_2));
					record_dia.setTextColor(getResources().getColor(R.color.bp_2));
				}

				if (rec.HighBloodPressure >= 180 || rec.LowBloodPressure >= 110) {
					record_sys.setTextColor(getResources().getColor(R.color.bp_3));
					record_dia.setTextColor(getResources().getColor(R.color.bp_3));
				}
Log.d("alex","rec.WHOIndicate="+rec.WHOIndicate);
				if (rec.WHOIndicate <= whoIcon.length)
					whoIcon[rec.WHOIndicate - 1].setImageResource(whoResId[rec.WHOIndicate - 1]);
			} else
				record_pulse.setText("EE");
		} else {
ll_record_daily_bp.setVisibility(View.INVISIBLE);
			ll_record_daily_ecg.setVisibility(View.VISIBLE);
			record_pulse_txt.setText(getResources().getString(R.string.hr));
			//ll_record_daily_dia.setVisibility(View.INVISIBLE);
			//ll_record_daily_sys.setVisibility(View.INVISIBLE);
			ll_record_daily_bp_icon.setVisibility(View.INVISIBLE);
			ll_record_daily_bp_count.setVisibility(View.INVISIBLE);
		//	if (sys_txt != null) {
	//			sys_txt.setVisibility(View.INVISIBLE);
	//			dia_txt.setVisibility(View.INVISIBLE);
	//		}

			if (rec.Noise == 0) {
				record_pulse.setText(String.valueOf(rec.HeartRate & 0x00FF));
				if (rec.Rhythm == 1)
					ecg_rhythm.setImageResource(R.drawable.rhythm_icon1);
				if (rec.Waveform == 1)
					ecg_wave.setImageResource(R.drawable.wave_icon1);
			//	if (rec.Rhythm == 0 && rec.Waveform == 0)
if(Integer.parseInt(record_pulse.getText().toString())<rec.Bradycardia)//slow
					{ecg_slow.setImageResource(R.drawable.slow_icon1);//alter
					Log.d("sandy", "rec.Bradycardia="+rec.Bradycardia+",rec.Bradycardiavalue"+rec.BradycardiaValue);}
					
				if(rec.TachycardiaValue==1||Integer.parseInt(record_pulse.getText().toString())>rec.Tachycardia)
					{ecg_fast.setImageResource(R.drawable.fast_icon1);//fast
					//Log.d("sandy", "rec.Tachycardiavalue="+rec.TachycardiaValue);
					}
				if (rec.Pause == 1)
					ecg_pause.setImageResource(R.drawable.pause_icon1);//pause
				if (rec.Rhythm == 0 && rec.Waveform == 0&&Integer.parseInt(record_pulse.getText().toString())<=rec.Tachycardia&&Integer.parseInt(record_pulse.getText().toString())>=rec.Bradycardia&&rec.Pause==0)
					ecg_ok.setImageResource(R.drawable.ok_icon1);
				//	ecg_ok.setImageResource(R.drawable.ok_icon1);
			} else
				record_pulse.setText("EE");
		}

		ll_record_content.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (chk.isChecked()) {
					chk.setChecked(false);

				} else {
					chk.setChecked(true);
				}

				return;
			}

		});

		ll_record_content.getLayoutParams().height = (int) (DeviceConstant.screenHeight * 0.15);
		ll_record_content.requestLayout();
		// Collections.sort(ll_download_table, comparator);
		ll_download_table.addView(myView, 0);

		return myView;

	}

	void uncheckAll() {
		View vi;
		for (int i = 0; i < ll_download_table.getChildCount(); i++) {
			vi = ll_download_table.getChildAt(i);
			CheckBox chk = (CheckBox) vi.findViewById(R.id.chk_record);
			if (chk != null)
				chk.setChecked(false);
		}
	}

	public void onDownloadClick(View v) {
		if (checkedList.size() == 0) {
			Toast.makeText(getBaseContext(), "Please select files!", Toast.LENGTH_LONG).show();
			return;
		}

		iDownloadFileCnt = checkedList.size();
		procDialog = new ProgressDialog(this);
		procDialog.setMessage("0% completed in file 1/" + iDownloadFileCnt);
		procDialog.setCancelable(false);
		procDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				data[1] = BT_DOWNLOAD;
				data[2] = BT_DOWNLOAD_WAIT;
				mBluetoothLeService.writeCharacteristic(characteristic2, data);
				writeFlag = 1;
				sec = 0;
				retryCmdCount = 0;
				currentSize = 0;

				checkedList.clear();
				uncheckAll();
			}
		});
		procDialog.show();

		int iFirst = checkedList.get(0).intValue();
		startDownloadAFile(iFirst);
	}

	boolean startDownloadAFile(int recordIndex) {
		Log.d("sandy", "startDownloadAFile index=" + recordIndex);
		if (recordIndex >= newRecordList.size())
			return false;

		header = newRecordList.get(recordIndex);
		Log.e("sandy", "startDownloadAFile_seq_header=" + header.Seq);
		Log.e("sandy", "startDownloadAFile_user_header=" + header.UserMode);
		if (header.AnalysisType == RecordsView.TYPE_BP) {
			saveFile(recordIndex);
			checkedList.remove(0);
			if (checkedList.size() > 0) {
				int iFirst = checkedList.get(0).intValue();
				startDownloadAFile(iFirst);
			} else {
				uncheckAll();
				procDialog.dismiss();
				showMessage(DownloadView.this, R.string.app_name, R.string.download_completed);
			}
		} else {
			downloadBufSeq = 1;
			data[1] = BT_DOWNLOAD;
			data[2] = BT_DOWNLOAD_RAWD;
			data[3] = (byte) header.UserMode;
			data[4] = (byte) header.Seq;
			data[5] = (byte) downloadBufSeq;
			mBluetoothLeService.writeCharacteristic(characteristic2, data);
			writeFlag = 1;
			sec = 0;
			retryCmdCount = 0;
			currentSize = 0;
			Log.d("download", "header.Seq=" + header.Seq + ", downloadBufSeq=" + downloadBufSeq + ", currentSize=" + currentSize + ",header.UserMode=" + header.UserMode);
		}

		return true;
	}

	public class SendDownloadCmd extends TimerTask {
		public void run() {

			sec++;
			if (mBluetoothLeService != null) {

				state = HomeView.ble_status;

				if (state == STATE_CONNECTED && sendcmdflag) {
					Log.d(TAG, "Download...");
					sendcmdflag = false;
					data[1] = BT_DOWNLOAD;
					data[2] = BT_DOWNLOAD_U1_U2_COUNT;
					if (characteristic2 != null)
						mBluetoothLeService.writeCharacteristic(characteristic2, data);
				}

				if (state != STATE_CONNECTED) {

					runOnUiThread(new Runnable() {
						@Override
						public void run() {

							leaveMessage(DownloadView.this, R.string.app_name, R.string.not_read_from_dev);
							timer.cancel();
						}
					});
				}

				if (writeFlag == 1 && sec > 1) {
					if (characteristic2 != null)
						mBluetoothLeService.writeCharacteristic(characteristic2, data);
					writeFlag = 1;
					sec = 0;
					retryCmdCount++;
				}

				if (retryCmdCount > 3 && data[1] == BT_DOWNLOAD && data[2] == BT_DOWNLOAD_RAWD) {
					writeFlag = 0;
					retryCmdCount = 0;
				}
			} else {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						leaveMessage(DownloadView.this, R.string.app_name, R.string.not_read_from_dev);
						timer.cancel();
					}
				});
			}

		}
	};

	private void displayGattServices(List<BluetoothGattService> gattServices) {
		if (gattServices == null)
			return;
		String uuid = null;
		String unknownServiceString = getResources().getString(R.string.unknown_service);
		String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
		ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
		ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();
		mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

		// Loops through available GATT Services.
		for (BluetoothGattService gattService : gattServices) {
			HashMap<String, String> currentServiceData = new HashMap<String, String>();
			uuid = gattService.getUuid().toString();
			currentServiceData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
			currentServiceData.put(LIST_UUID, uuid);
			gattServiceData.add(currentServiceData);

			ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
			List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
			ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

			// Loops through available Characteristics.
			for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
				charas.add(gattCharacteristic);
				HashMap<String, String> currentCharaData = new HashMap<String, String>();
				uuid = gattCharacteristic.getUuid().toString();
				currentCharaData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
				currentCharaData.put(LIST_UUID, uuid);
				gattCharacteristicGroupData.add(currentCharaData);

				Log.d(TAG, "uuid=" + uuid);
				if (uuid.equals("00002a36-0000-1000-8000-00805f9b34fb")) {
					// Log.d(TAG,"uuid equal = "+uuid);
					characteristic1 = gattCharacteristic;
				}
				if (uuid.equals("00002a37-0000-1000-8000-00805f9b34fb")) {
					// Log.d(TAG,"uuid equal ="+uuid);
					characteristic2 = gattCharacteristic;
				}
			}
			mGattCharacteristics.add(charas);
			gattCharacteristicData.add(gattCharacteristicGroupData);
		}

		SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(this, gattServiceData, android.R.layout.simple_expandable_list_item_2, new String[] { LIST_NAME, LIST_UUID },
				new int[] { android.R.id.text1, android.R.id.text2 }, gattCharacteristicData, android.R.layout.simple_expandable_list_item_2, new String[] { LIST_NAME, LIST_UUID }, new int[] {
						android.R.id.text1, android.R.id.text2 });
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				// Log.d(TAG, "BroadcastReceiver onReceive  [" + action + "]");
				// mConnected = true;
				// updateConnectionState(R.string.connected);
				invalidateOptionsMenu();
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				// Log.d(TAG, "BroadcastReceiver onReceive  [" + action + "]");
				// mConnected = false;
				// updateConnectionState(R.string.disconnected);
				// invalidateOptionsMenu();
				// clearUI();
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				// Log.d(TAG, "BroadcastReceiver onReceive  [" + action + "]");
				// Show all the supported services and characteristics on the
				// user interface.
				// displayGattServices(mBluetoothLeService.getSupportedGattServices());
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

				final int[] value = intent.getIntArrayExtra(BluetoothLeService.EXTRA_DATA);
				
				showCancelFlag = 1;
				seq = value[0];
				if (seq - tmpseq != 1 && tmpseq - seq != 255) {
					lost = 1;
					Log.e(TAG, "**********************seq=" + Integer.toString(seq) + ",tmpseq=" + Integer.toString(tmpseq));
					Log.e("sandy", "**********************seq=" + Integer.toString(seq) + ",tmpseq=" + Integer.toString(tmpseq));
				}
				tmpseq = seq;

				int format = value[1];
				int bpCmd = 0;
				if (value.length > 2)
					bpCmd = value[2];
				//Log.d("sandy", "697");
				// Log.d(TAG, "download...format="+format+" bpCmd="+bpCmd);

				if (format == BT_DOWNLOAD) {
					if (bpCmd == BT_DOWNLOAD_U1_U2_COUNT) {
						writeFlag = 0;
						recordsCount1 = value[3];
						recordsCount2 = value[4];
						downloadCount = 0;
						newRecordList.clear();
						headerList.clear();
						headerList1.clear();//1202
						checkedList.clear();
						Log.d(TAG, "newRecordList count=" + newRecordList.size() + ", returned size=" + (recordsCount1 + recordsCount2));

						if ((recordsCount1 + recordsCount2) > 0) {
							headerSeq = 0;
							data[1] = BT_DOWNLOAD;
							data[2] = BT_DOWNLOAD_HEADER;
							data[3] = (byte) headerSeq;
							mBluetoothLeService.writeCharacteristic(characteristic2, data);
							writeFlag = 1;
							sec = 0;
							retryCmdCount = 0;
						}
						// here Refresh UI.......
					} else if (bpCmd == BT_DOWNLOAD_HEADER) {
						//Log.d("sandy", "724");
						if (downloadCount < (recordsCount1 + recordsCount2)) {
							int Cmd = value[3];
						//	Log.d("sandy", "726");
							if (Cmd < 15) {
								for (int i = 0; i < 16; i++) {
//									if(Cmd>=1&&Cmd<3)
//									{
//										deviceID[i]=value[i+4];
//										Log.d("sandy","deviceID["+i+"]"+deviceID[i]);
//									}
									FlashBuffer[Cmd * 16 + i] = (byte) value[i + 4];
//									Log.d("sandy", "731_FlashBuffer["+(Cmd * 16 + i)+"]="+FlashBuffer[Cmd * 16 + i]);
								}
							} else if (Cmd == 15) {
								if (tProc != null)
									tProc.cancel();
//								tProc = Toast.makeText(getBaseContext(), String.valueOf(downloadCount + 1) + "/" + (recordsCount1 + recordsCount2), Toast.LENGTH_SHORT);
//								tProc.show();

								//1050816								
								if(downloadCount==0 && !progressDialog.isShowing())
								{
									progressDialog = new ProgressDialog(DownloadView.this);							
									progressDialog.setTitle(R.string.app_name);
								    progressDialog.setMessage("please wait......");								    
								    progressDialog.setMax((recordsCount1 + recordsCount2));
								    progressDialog.setCancelable(false);
								    progressDialog.setProgress(0);
								    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
								    progressDialog.show();
								}								
								progressDialog.incrementProgressBy(1);
//								if(downloadCount==(recordsCount1 + recordsCount2-1))								
//							      progressDialog.dismiss();
								//1050816-end
//								if(q==1&&downloadCount==1)
//								{
//									lost=1;
//									q=0;
//									Log.d("sandy", "testlost....data[3]="+headerSeq);
//								}
								Log.d("download", "download..............header Seqq=" + headerSeq);
								writeFlag = 0;
								if (lost == 1) {
									Log.d("sandy", "data[3]="+headerSeq);
									data[1] = BT_DOWNLOAD;
									data[2] = BT_DOWNLOAD_HEADER;
									data[3] = (byte) headerSeq;
									mBluetoothLeService.writeCharacteristic(characteristic2, data);
									writeFlag = 1;
									sec = 0;
									lost = 0;
									retryCmdCount = 0;
									return;
								}


								for (int i = 0; i < 16; i++)
									FlashBuffer[Cmd * 16 + i] = (byte) value[i + 4];

								RecordList rec = new RecordList();

								String datetime = String.format("%02d%02d%02d%02d%02d%02d", FlashBuffer[20], FlashBuffer[21], FlashBuffer[22], FlashBuffer[23], FlashBuffer[24], FlashBuffer[25]);

								rec.sDatetime = datetime;
								rec.Seq = FlashBuffer[0];
								rec.Signature = String.format("%c%c%c", FlashBuffer[1], FlashBuffer[2], FlashBuffer[3]);
								//Log.v("sandy", "Signature="+rec.Signature);
								rec.FirmwareVersion = FlashBuffer[4] + FlashBuffer[5] * 256;
								rec.HardwareVersion = FlashBuffer[6] + FlashBuffer[7] * 256;
								rec.HeaderSize = FlashBuffer[8] + FlashBuffer[9] * 256;
								rec.VersionTag = String.format("%02d%02d%02d%02d%02d%02d", FlashBuffer[10], FlashBuffer[11], FlashBuffer[12], FlashBuffer[13], FlashBuffer[14], FlashBuffer[15]);
								rec.DeviceID = FlashBuffer[16] + FlashBuffer[17] * 256 + FlashBuffer[18] * 256 * 256 + FlashBuffer[19] * 256 * 256 * 256;
								//rec.DeviceID=deviceID[0]+deviceID[1]*256+deviceID[2]*256*256+deviceID[3]*256*256*256;
								
								//Log.e("sandy","rec.DeviceID="+String.format("%x",FlashBuffer[16] + FlashBuffer[17] * 256 + FlashBuffer[18] * 256 * 256 + FlashBuffer[19] * 256 * 256 * 256));
								rec.SamplingRate = FlashBuffer[26] + FlashBuffer[27] * 256;
								rec.GainSetting = FlashBuffer[28];
								rec.Resolution = FlashBuffer[29];
								rec.Noise = FlashBuffer[30];
								rec.PhysicalMinimum = (FlashBuffer[31]&0xff) + FlashBuffer[32] * 256;
								rec.PhysicalMaximum = (FlashBuffer[33]&0xff) + FlashBuffer[34] * 256;
								rec.DigitalMinimum = (FlashBuffer[35]&0xff) + FlashBuffer[36] * 256;
								rec.DigitalMaximum = (FlashBuffer[37]&0xff) + FlashBuffer[38] * 256;
								rec.Prefiltering = FlashBuffer[39];
								rec.TotalSize = FlashBuffer[40] + FlashBuffer[41] * 256 + FlashBuffer[42] * 256 * 256 + FlashBuffer[43] * 256 * 256 * 256;
								rec.UserMode = FlashBuffer[44];
								rec.RSensitivity = FlashBuffer[45];
								rec.WSensitivity = FlashBuffer[46];
								rec.HeartRate = FlashBuffer[47];
								rec.Tachycardia = FlashBuffer[48];
								rec.Bradycardia = FlashBuffer[49];
								rec.Pause = FlashBuffer[50];
								rec.PauseValue = FlashBuffer[51];
								rec.Rhythm = FlashBuffer[52];
								rec.Waveform = FlashBuffer[53];
								rec.WaveformStable = FlashBuffer[54];
								rec.EntryPosition = FlashBuffer[55];
								rec.TachycardiaValue = FlashBuffer[56];
								rec.BradycardiaValue = FlashBuffer[57];
								rec.MID = FlashBuffer[58] + FlashBuffer[59] * 256 + FlashBuffer[60] * 256 * 256 + FlashBuffer[61] * 256 * 256 * 256;
								rec.BPMNoiseFlag = FlashBuffer[62];
								rec.BPHeartRate = FlashBuffer[63];

								rec.HighBloodPressure = FlashBuffer[64] + FlashBuffer[65] * 256;

								rec.LowBloodPressure = FlashBuffer[66] + FlashBuffer[67] * 256;
								rec.WHOIndicate = FlashBuffer[68];
								rec.DCValue = FlashBuffer[69] + FlashBuffer[70] * 256;
								rec.AnalysisType = FlashBuffer[71];
								rec.CheckSum = FlashBuffer[255];

								rec.sFilename = datetime + ".dat";

								newRecordList.add(rec);
								h = new byte[256];
								for (int i = 0; i < h.length; i++)
									h[i] = FlashBuffer[i];
								headerList.add(h);

								data[1] = BT_DOWNLOAD;
								data[2] = BT_DOWNLOAD_HEADER;
								data[3] = (byte) ++headerSeq;
								mBluetoothLeService.writeCharacteristic(characteristic2, data);
								writeFlag = 1;
								sec = 0;
								retryCmdCount = 0;
								downloadCount++;
								if (downloadCount == recordsCount1 + recordsCount2) {
									downloadflag = 1;
								}
								// here Refresh UI.......
								// sortData.add(datetime);

								addRecordView(downloadCount - 1);

							}
						}
						else //1202
						{
							if(downloadCount==(recordsCount1 + recordsCount2))
							{
								Log.d("sandy", "finish.....");
								
								ll_download_table.removeAllViews();
								
								Collections.sort(newRecordList, comparator);
								//h1 = new byte[256];
								//if(newRecordList.get(i).Seq==h[0])
								//for (int i = 0; i < h1.length; i++)
								//	h1[i] = h[i];
								//headerList1.add(h1);
								
								for(int i=0;i<downloadCount;i++)
								{
									for (int j = 0; j < downloadCount; j++)
									{
										//Log.d("sandy","newRecordList.get("+i+").sDatetime="+newRecordList.get(i).sDatetime);
										//Log.e("sandy", "newRecordList.get(i).Seq="+newRecordList.get(i).Seq+",headerList.get(j)[0]="+headerList.get(j)[0]+",,,,newRecordList.get("+i+").sDatetime="+newRecordList.get(i).sDatetime);
										if(newRecordList.get(i).Seq==headerList.get(j)[0]&&newRecordList.get(i).UserMode==headerList.get(j)[44])
										{
											//Log.e("sandy", "893.........");
											
											h1 = new byte[256];
											for (int i1 = 0; i1 < h1.length; i1++)
												h1[i1]=headerList.get(j)[i1];
											//Log.v("sandy", "h1[0]="+h1[0]);
											headerList1.add(h1);										
										}
									}
									//Log.d("sandy","newRecordList.get("+i+").sDatetime="+newRecordList.get(i).sDatetime);
									//Log.v("sandy", "1_seq======"+headerList1.get(i)[0]+",datetime="+ String.format("%02d%02d%02d%02d%02d%02d",headerList.get(i)[20],headerList.get(i)[21],headerList.get(i)[22],headerList.get(i)[23],headerList.get(i)[24],headerList.get(i)[25]));
									//Log.e("sandy", "headerlist_seq"+headerList.get(i)[0]);							
									addRecordView(i);
									
								}
									
							}//if
							downloadCount++;

							Thread t = new Thread(new Runnable() {
		                        @Override
		                        public void run() {
		                            try {   
		                                Thread.sleep(500);
		                                progressDialog.dismiss();
		                               // Log.d("sandy","zzzzzzzzzzzzzzzzzzzzzzzzzzzzz");
		                                } 
		                            catch (InterruptedException e) {
		                            
		                                e.printStackTrace();    
		                            }                           
		                        }
		                        });
		                        t.start();
							//progressDialog.dismiss();
//							Log.d("sandy","zzzzzzzzzzzzzzzzzzzzzzzzzzzzz");
							//downloadCount++;
						}
					} else if (bpCmd == BT_DOWNLOAD_RAWD) {
						int Cmd = value[3];
						if (Cmd < 15) {
							for (int i = 0; i < 16; i++)
								FlashBuffer[Cmd * 16 + i] = (byte) value[i + 4];
						} else if (Cmd == 15) {
							writeFlag = 0;
							// if(selectDic.count==0)
							// return;

							if (lost == 1) {
								lost = 0;
								data[1] = BT_DOWNLOAD;
								data[2] = BT_DOWNLOAD_RAWD;
								data[3] = (byte) header.UserMode;
								data[4] = (byte) header.Seq;
								data[5] = (byte) downloadBufSeq;
								mBluetoothLeService.writeCharacteristic(characteristic2, data);
								writeFlag = 1;
								sec = 0;
								retryCmdCount = 0;
								Log.d("download", "lost------header.Seq=" + header.Seq + ", downloadBufSeq=" + downloadBufSeq + ", currentSize=" + currentSize);
								return;
							}

							for (int i = 0; i < 16; i++)
								FlashBuffer[Cmd * 16 + i] = (byte) value[i + 4];

							if (currentSize <= 20480) {
								for (int i = 0; i < 256; i++)
									rawDataBuf[currentSize++] = FlashBuffer[i];
							}

							int percentage = (int) (currentSize * 100 / totalSize);
							if (percentage >= 100)
								percentage = 100;

							// display real-time percentage message
							procDialog.setMessage(String.valueOf(percentage) + "% completed in file " + (iDownloadFileCnt - checkedList.size() + 1) + "/" + iDownloadFileCnt);

							if (currentSize >= 20736) {
								// save trend in parameter.beatTimeIndexBuffer
								// save record and trend
								// ShowAlertMsg();
								data[1] = BT_DOWNLOAD;
								data[2] = BT_DOWNLOAD_WAIT;
								mBluetoothLeService.writeCharacteristic(characteristic2, data);
								writeFlag = 1;
								sec = 0;
								retryCmdCount = 0;
								currentSize = 0;
//Log.e("sandy", "982_checkedList.get(0).intValue()="+checkedList.get(0).intValue());
								saveFile(checkedList.get(0).intValue());
								checkedList.remove(0);
								if (checkedList.size() > 0) {
									int iFirst = checkedList.get(0).intValue();
									startDownloadAFile(iFirst);
								} else {
									uncheckAll();
									procDialog.dismiss();
									showMessage(DownloadView.this, R.string.app_name, R.string.download_completed);
								}

								// for (int i = 0; i < 18765; i++)

								// Log.d("download", "rawDataBuf[" + i + "]=" +
								// rawDataBuf[i]);

							} else {

								if (cancelFlag == 1)
									return;

								data[1] = BT_DOWNLOAD;
								data[2] = BT_DOWNLOAD_RAWD;
								data[3] = (byte) header.UserMode;
								data[4] = (byte) header.Seq;
								data[5] = (byte) ++downloadBufSeq;
								mBluetoothLeService.writeCharacteristic(characteristic2, data);
								writeFlag = 1;
								sec = 0;
								retryCmdCount = 0;

								Log.d("download", "header.Seq=" + header.Seq + ", downloadBufSeq=" + downloadBufSeq + ", currentSize=" + currentSize);

							}

						}
					}
				}
			}
		}
	};

	void saveFile(int recordIndex) {
		//byte[] FlashBuffer = headerList.get(recordIndex).clone();
		//Log.d("sandy", "headerList1.get(recordIndex)="+headerList1.get(recordIndex)[0]);
		byte[] FlashBuffer1 =headerList1.get(recordIndex).clone();//sort FlashBuffer
		int[] FlashBuffer=new int[FlashBuffer1.length];
		for(int i=0;i<FlashBuffer.length;i++)
		{
			FlashBuffer[i]=0xFF&FlashBuffer1[i];
		}
		// byte[] FlashBuffer=new byte[256];
		// for(int i=0; i<FlashBuffer.length; i++)
		// FlashBuffer[i]=headerList.get(recordIndex)[i];

		int iHrCnt = 0;
		int ecgCount = 8704;
		short[] HRData = new short[90];
		int[] HRDataTimestamp = new int[90];
		short[] rawData = new short[10240];
		String datetime = "";
		short iFirstByte, iSecondByte;
		for (int i = 0, j = 0; i < 8704; i++, j += 2) {
			iFirstByte = (short) (0x00FF & ((short) rawDataBuf[j]));
			iSecondByte = (short) (0x00FF & ((short) rawDataBuf[j + 1]));
			rawData[i] = (short) (iFirstByte << 8 | iSecondByte);
		}
		for (int i = 8704, j = 8704 * 2; i < rawData.length; i++, j += 2) {
			iFirstByte = (short) (0x00FF & ((short) rawDataBuf[j]));
			iSecondByte = (short) (0x00FF & ((short) rawDataBuf[j + 1]));
			rawData[i] = (short) (iFirstByte << 8 | iSecondByte);
		}
		for (int i = 8704, j = 8704 * 2; i < rawData.length; i++, j += 2) {
			iFirstByte = (short) (0x00FF & ((short) rawDataBuf[j]));
			iSecondByte = (short) (0x00FF & ((short) rawDataBuf[j + 1]));
			rawData[i] = (short) (iFirstByte << 8 | iSecondByte);
		}

		/*
		 * for(int i=0; i < 60 ; i++) { Log.d("download",
		 * "HR"+i+"="+String.format
		 * ("%x",rawDataBuf[(8704+i)*2])+", "+String.format
		 * ("%x",rawDataBuf[(8704+i)*2+1])); Log.d("download",
		 * "HR--T"+i+"="+String
		 * .format("%x",rawDataBuf[(9344+i)*2])+", "+String.format
		 * ("%x",rawDataBuf[(9344+i)*2+1])); }
		 */

		for (int i = 1; i < 128; i++) {
			if (i >= HRData.length)
				break;
			if (rawData[i + 8704] == -1)
				break;
			if (rawData[i + 8704] > 0)
				HRData[i - 1] = (short) (60 * 1000 / rawData[i + 8704]);
			else
				HRData[i - 1] = 0;

			iHrCnt++;
		}

		for (int i = 1; i < 128; i++) {
			if (i >= HRData.length)
				break;
			HRDataTimestamp[i - 1] = rawData[i + 8704 + 128 + 128 + 128 + 128 + 128];
		}

		Log.d("nick", "iHrCnt=" + iHrCnt);

		FileOutputStream out = null;
		DataOutputStream outS;
		try {

			datetime = String.format("%02d%02d%02d%02d%02d%02d", FlashBuffer[20], FlashBuffer[21], FlashBuffer[22], FlashBuffer[23], FlashBuffer[24], FlashBuffer[25]);
			Log.d("alex","FlashBuffer[71]="+FlashBuffer[71]+",RecordsView.TYPE_BP="+RecordsView.TYPE_BP);
			if (FlashBuffer[71] != RecordsView.TYPE_BP) {
				Log.d("alex","FlashBuffer[71]="+FlashBuffer[71]+",RecordsView.TYPE_BP="+RecordsView.TYPE_BP);
				out = openFileOutput(datetime + ".dat", Context.MODE_PRIVATE);
				outS = new DataOutputStream(new BufferedOutputStream(out));
				outS.write(FlashBuffer1, 0, 256);

				outS.writeInt(iHrCnt);
				for (int i = 0; i < HRData.length; i++) {
					outS.writeInt(HRDataTimestamp[i]);
					outS.writeShort(HRData[i]);
				}
				outS.writeInt(ecgCount);
				for (int i = 0; i < ecgCount; i++) {
					outS.writeShort(rawData[i]);
				}
				outS.close();

				/*
				 * 
				 * test erase this File file1 = new File("/mnt/sdcard/test/");
				 * if (!file1.exists()) { file1.mkdirs(); } File file = new
				 * File("/mnt/sdcard/test", datetime + ".dat"); out = new
				 * FileOutputStream(file); outS = new DataOutputStream(new
				 * BufferedOutputStream(out)); // outS.write(FlashBuffer, 0,
				 * 256); outS.write(rawDataBuf, 0, 20736); //
				 * outS.writeInt(iHrCnt); // for (int i = 0; i < HRData.length;
				 * i++) { // outS.writeInt(HRDataTimestamp[i]); //
				 * outS.writeShort(HRData[i]); // } // outS.writeInt(ecgCount);
				 * // for (int i = 0; i < ecgCount; i++) { //
				 * outS.writeShort(rawData[i]); // } outS.close();
				 */
				Log.d("alex", "file: " + datetime + ".dat" + " saved!");
				Log.d("sandy", "file: " + datetime + ".dat" + " saved!");
			}

			RecordList rec = new RecordList();
			rec.sDatetime = datetime;
			rec.Seq = FlashBuffer[0];
			rec.Signature = String.format("%c%c%c", FlashBuffer[1], FlashBuffer[2], FlashBuffer[3]);
			//Log.d("sandy", "addrecord_rec.Signature="+rec.Signature );
			rec.FirmwareVersion = FlashBuffer[4] + FlashBuffer[5] * 256;
			rec.HardwareVersion = FlashBuffer[6] + FlashBuffer[7] * 256;
			rec.HeaderSize = FlashBuffer[8] + FlashBuffer[9] * 256;
			rec.VersionTag = String.format("%c%c%c%c%c%c", FlashBuffer[10], FlashBuffer[11], FlashBuffer[12], FlashBuffer[13], FlashBuffer[14], FlashBuffer[15]);
			//rec.DeviceID = (0xFF&FlashBuffer[16]) + (0xFF&FlashBuffer[17]) * 256 + (0xFF&FlashBuffer[18]) * 256 * 256 + (0xFF&FlashBuffer[19]) * 256 * 256 * 256;
			rec.DeviceID =FlashBuffer[16] + FlashBuffer[17] * 256 + FlashBuffer[18] * 256 * 256 + FlashBuffer[19] * 256 * 256 * 256;
			Log.e("sandy", "addrecord_rec.DeviceID="+rec.DeviceID);
			
			rec.SamplingRate = FlashBuffer[26] + FlashBuffer[27] * 256;
			rec.GainSetting = FlashBuffer[28];
			rec.Resolution = FlashBuffer[29];
			rec.Noise = FlashBuffer[30];
			
			rec.PhysicalMinimum = FlashBuffer[31] + FlashBuffer1[32] * 256;//FlashBuffer正數, FlashBuffer1有負數
			rec.PhysicalMaximum = FlashBuffer[33] + FlashBuffer1[34] * 256;
			rec.DigitalMinimum = FlashBuffer[35] + FlashBuffer1[36] * 256;
			rec.DigitalMaximum = FlashBuffer[37] + FlashBuffer1[38] * 256;
			
			rec.Prefiltering = FlashBuffer[39];
			rec.TotalSize = FlashBuffer[40] + FlashBuffer[41] * 256 + FlashBuffer[42] * 256 * 256 + FlashBuffer[43] * 256 * 256 * 256;
			rec.UserMode = FlashBuffer[44];
			rec.RSensitivity = FlashBuffer[45];
			rec.WSensitivity = FlashBuffer[46];
			rec.HeartRate = FlashBuffer[47];
			rec.Tachycardia = FlashBuffer[48];
			rec.Bradycardia = FlashBuffer[49];
			rec.Pause = FlashBuffer[50];
			rec.PauseValue = FlashBuffer[51];
			rec.Rhythm = FlashBuffer[52];
			rec.Waveform = FlashBuffer[53];
			rec.WaveformStable = FlashBuffer[54];
			rec.EntryPosition = FlashBuffer[55];
			rec.TachycardiaValue = FlashBuffer[56];
			rec.BradycardiaValue = FlashBuffer[57];
			rec.MID = FlashBuffer[58] + FlashBuffer[59] * 256 + FlashBuffer[60] * 256 * 256 + FlashBuffer[61] * 256 * 256 * 256;
			rec.BPMNoiseFlag = FlashBuffer[62];
			rec.BPHeartRate = FlashBuffer[63];
			rec.HighBloodPressure = FlashBuffer[64] + FlashBuffer[65] * 256;
			rec.LowBloodPressure = FlashBuffer[66] + FlashBuffer[67] * 256;
			rec.WHOIndicate = FlashBuffer[68];
			rec.DCValue = FlashBuffer[69] + FlashBuffer[70] * 256;
			rec.AnalysisType = FlashBuffer[71];
			rec.CheckSum = FlashBuffer[255];

			rec.sFilename = datetime + ".dat";

			databaseHelper.addRecord(rec);
			
//			rec.sDatetime = "160121175555";
//			rec.HighBloodPressure = 123;
//			rec.LowBloodPressure=100;
//			databaseHelper.addRecord(rec);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void addEmptytable(int addList) {

		for (int i = 0; i < addList; i++) {

			LayoutInflater factory = LayoutInflater.from(this);
			View myView = factory.inflate(R.layout.records_content_activity, null);

			LinearLayout ll_record_content;
			ll_record_content = (LinearLayout) myView.findViewById(R.id.ll_record_content);
			ll_record_content.removeAllViews();
			ll_record_content.getLayoutParams().height = (int) (DeviceConstant.screenHeight * 0.15);
			ll_record_content.requestLayout();
			ll_download_table.addView(myView);
		}

	}

}
