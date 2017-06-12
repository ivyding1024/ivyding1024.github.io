package com.mdbiomedical.app.vion.vian_health.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.mdbiomedical.app.vion.vian_health.MainActivity;
import com.mdbiomedical.app.vion.vian_health.R;
import com.mdbiomedical.app.vion.vian_health.helper.DatabaseHelper;
import com.mdbiomedical.app.vion.vian_health.model.RecordList;
import com.mdbiomedical.app.vion.vian_health.service.BluetoothLeService;
import com.mdbiomedical.app.vion.vian_health.service.SampleGattAttributes;
import com.mdbiomedical.app.vion.vian_health.util.ChangeView;
import com.mdbiomedical.app.vion.vian_health.util.DeviceConstant;

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
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class DeviceView extends Activity {

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

	public static final byte BT_ERASE_FLASH = 6;
	public static final byte BT_ERASE_ALL_USERS=2;
	
	public static final byte BT_CONFIG_INFO = 7;
	public static final byte BT_CONFIG_INFO_DEVICE = 1;
	public static final byte BT_CONFIG_INFO_SETTING = 2;
	public static final byte BT_START = 8;
	public static final byte BT_START_BP = 1;
	public static final byte BT_START_ECG = 2;
	public static final byte BT_START_BP_ECG = 3;
	public final int BT_KEEP_ALIVE = 9;

	ProgressDialog procDialog = null;
	Timer single_timer = new Timer();
	TextView tvID_No, tvFW_Ver;
	ImageView iv_sync_with_phone, iv_key_beep, iv_heartbeat_beep, iv_12hr;
	LinearLayout switch_sync_with_phone, switch_key_beep, switch_heartbeat_beep, visible_12hr;
	private Switch s_switch_sync_with_phone, s_switch_key_beep, s_switch_heartbeat_beep, s_switch_12hr;
	Button btn_Done;

	boolean bSync_with_phone = true, bKey_beep = true, bHeartbeat_beep = true, benabled12Mod = false;
	boolean bGotDeviceInfo = false;
	boolean bCmdResult = true;

	private final static String TAG = BluetoothLeService.class.getSimpleName();
	private BluetoothLeService mBluetoothLeService;
	private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	private final String LIST_NAME = "NAME";
	private final String LIST_UUID = "UUID";
	private BluetoothGattCharacteristic characteristic1;
	private BluetoothGattCharacteristic characteristic2;
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

	private int[] signature = new int[3];
	private int[] versionTag = new int[6];
	private View.OnTouchListener swipeListener;
	float swipe_x = 0;
	boolean swipe_on = false;

	LinearLayout ll_delete;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_activity);

		init();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		mBluetoothLeService = HomeView.mBluetoothLeService;
		characteristic2 = HomeView.characteristic2;
		timer.schedule(new SendDownloadCmd(), 1000, 1000);
		sendcmdflag = true;

		// attach a listener to check for changes in state
		s_switch_sync_with_phone.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				bSync_with_phone = isChecked;

			}
		});
		s_switch_key_beep.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				bKey_beep = isChecked;
				if (isChecked) {
					Log.d("alex", "on");
				} else {
					Log.d("alex", "off");
				}

			}
		});
		s_switch_heartbeat_beep.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				bHeartbeat_beep = isChecked;
				if (isChecked) {
					Log.d("alex", "on");
				} else {
					Log.d("alex", "off");
				}

			}
		});

		s_switch_12hr.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				benabled12Mod = isChecked;
				if (isChecked) {
					Log.d("alex", "on");
				} else {
					Log.d("alex", "off");
				}

			}
		});

	}
	//sandy0914
		public static DisplayMetrics dm = new DisplayMetrics();
		//
	@Override
	protected void onResume() {
		super.onResume();
		HomeView.home_pressed = "disable";
		// loadData();
		int version = Integer.valueOf(android.os.Build.VERSION.SDK);
		if (version >= 11) {
		} else {
		}

		if (HomeView.ble_status == 2) {

			showReading();
		} else {
			Log.d("alex", "onResume jump");
			leaveMessage(DeviceView.this, R.string.app_name, R.string.not_read_from_dev);
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
		try {
			unregisterReceiver(mGattUpdateReceiver);
		} catch (IllegalArgumentException e) {
			Log.d("alex", "unregistered");
		}

		Log.d("device view", "unregisterReceiver() on onDestroy");
		System.gc();
	}

	private void init() {
		//sandy0914
				getWindowManager().getDefaultDisplay().getMetrics(dm);
				DeviceConstant.screenWidth = dm.widthPixels;
				DeviceConstant.screenHeight = dm.heightPixels;
				DeviceConstant.screenDPI = dm.densityDpi;
				
		tvID_No = (TextView) findViewById(R.id.tvID_No);
		tvFW_Ver = (TextView) findViewById(R.id.tvFW_Ver);

		// iv_sync_with_phone = (ImageView)
		// findViewById(R.id.iv_sync_with_phone);
		// iv_key_beep = (ImageView) findViewById(R.id.iv_key_beep);
		// iv_heartbeat_beep = (ImageView) findViewById(R.id.iv_heartbeat_beep);
		// iv_12hr = (ImageView) findViewById(R.id.iv_12hr);

		 visible_12hr = (LinearLayout) findViewById(R.id.visible_12hr);
		switch_sync_with_phone = (LinearLayout) findViewById(R.id.switch_sync_with_phone);
		switch_key_beep = (LinearLayout) findViewById(R.id.switch_key_beep);
		switch_heartbeat_beep = (LinearLayout) findViewById(R.id.switch_heartbeat_beep);
		// switch_12hr = (LinearLayout) findViewById(R.id.switch_12hr);
		btn_Done = (Button) findViewById(R.id.btn_Done);
		s_switch_sync_with_phone = (Switch) findViewById(R.id.s_switch_sync_with_phone);
		s_switch_key_beep = (Switch) findViewById(R.id.s_switch_key_beep);
		s_switch_heartbeat_beep = (Switch) findViewById(R.id.s_switch_heartbeat_beep);
		s_switch_12hr = (Switch) findViewById(R.id.s_switch_12hr);
		// set the switch to ON
		s_switch_sync_with_phone.setChecked(true);
		s_switch_key_beep.setChecked(true);
		s_switch_heartbeat_beep.setChecked(true);
		s_switch_12hr.setChecked(true);

		TextView tv_list_title = (TextView) findViewById(R.id.tv_list_title);

		LinearLayout ll_list_back = (LinearLayout) findViewById(R.id.ll_list_back);
		tv_list_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.03f));

		// 回上一頁
		ll_list_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				try {
					onBackPressed();
				} catch (Exception e) {
					finish();
				}

			}
		});
		
		//sandy20170515
		ll_delete=(LinearLayout) findViewById(R.id.ll_delete);
		ll_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				showdeleteMessage(DeviceView.this, R.string.delete_device_data, R.string.delete_data_message);

			}
		});
		
		
		
//		swipeListener = new View.OnTouchListener() {
//
//			public boolean onTouch(View v, MotionEvent event) {
//
//				// Log.d("alex", "disable touch isBlockScroll=" +
//				// isBlockScroll);
//				if (event.getAction() == MotionEvent.ACTION_DOWN) {
//					swipe_x = event.getX();
//					Log.d("alex", "down click");
//				} else if (event.getAction() == MotionEvent.ACTION_UP) {
//					if (swipe_x - event.getX() > 0) {
//						// 往左 關閉
//						Log.d("alex", "getX >0");
//						swipe_on = false;
//					}
//					if (swipe_x - event.getX() < 0) {
//						// 往右 啟動
//						Log.d("alex", "getX <0");
//						swipe_on = true;
//					}
//					if (swipe_x - event.getX() == 0) {
//						// 點擊 切換
//						Log.d("alex", "getX =0");
//						switch (v.getId()) {
//						case R.id.switch_sync_with_phone:
//							swipe_on = !bSync_with_phone;
//							break;
//						case R.id.switch_key_beep:
//							swipe_on = !bKey_beep;
//							break;
//						case R.id.switch_heartbeat_beep:
//							swipe_on = !bHeartbeat_beep;
//							break;
//						// case R.id.switch_12hr:
//						// swipe_on = !benabled12Mod;
//						// break;
//
//						}
//					}
//
//					if (v.getId() == R.id.switch_sync_with_phone) {
//						bSync_with_phone = swipe_on;
//						if (bSync_with_phone)
//							iv_sync_with_phone.setImageResource(R.drawable.toggle_buttons_on1);
//						else
//							iv_sync_with_phone.setImageResource(R.drawable.toggle_buttons_off1);
//					}
//					if (v.getId() == R.id.switch_key_beep) {
//						bKey_beep = swipe_on;
//						if (bKey_beep)
//							iv_key_beep.setImageResource(R.drawable.toggle_buttons_on1);
//						else
//							iv_key_beep.setImageResource(R.drawable.toggle_buttons_off1);
//					}
//					if (v.getId() == R.id.switch_heartbeat_beep) {
//						bHeartbeat_beep = swipe_on;
//						if (bHeartbeat_beep)
//							iv_heartbeat_beep.setImageResource(R.drawable.toggle_buttons_on1);
//						else
//							iv_heartbeat_beep.setImageResource(R.drawable.toggle_buttons_off1);
//					}
//					// if (v.getId() == R.id.switch_12hr) {
//					// benabled12Mod = swipe_on;
//					// if (benabled12Mod)
//					// iv_12hr.setImageResource(R.drawable.toggle_buttons_on1);
//					// else
//					// iv_12hr.setImageResource(R.drawable.toggle_buttons_off1);
//					// }
//				}
//				return true;
//			}
//
//		};

		// switch_sync_with_phone.setOnTouchListener(swipeListener);
		// switch_key_beep.setOnTouchListener(swipeListener);
		// switch_heartbeat_beep.setOnTouchListener(swipeListener);
		// switch_12hr.setOnTouchListener(swipeListener);
	}
	public void showdeleteMessage(Context context, int title, int msg) {
		new AlertDialog.Builder(context).setTitle(title).setMessage(msg).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface df, int i) {
			
				Log.d("sandy", "Delete onclick");
				if(state == STATE_CONNECTED) {
					Log.d("sandy", "Delete...");
                    data[1] = BT_ERASE_FLASH;
                    data[2] = BT_ERASE_ALL_USERS;
                    if (characteristic2 != null) {
                       
                        mBluetoothLeService.writeCharacteristic(characteristic2, data);

                    }

                }
				else
				{
					leaveMessage(DeviceView.this, R.string.app_name, R.string.not_read_from_dev);
				}


			}
		}).setNegativeButton(R.string.cancel,  new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface df, int i) {

			}
		}).show();
		//.show();
		

	}
	

	@Override
	public void onBackPressed() {

		setResult(RESULT_OK);
		single_timer.cancel();
		timer.cancel();
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

	public class SendDownloadCmd extends TimerTask {
		public void run() {
			Log.d("alex", "test");

			sec++;
			if (mBluetoothLeService != null) {
				state = HomeView.ble_status;

				if (state == STATE_CONNECTED && sendcmdflag) {
					Log.d("nick", "Get device info...");
					sendcmdflag = false;
					data[1] = BT_CONFIG_INFO;
					data[2] = BT_CONFIG_INFO_DEVICE;
					if (characteristic2 != null)
						mBluetoothLeService.writeCharacteristic(characteristic2, data);
				}

				if (state != STATE_CONNECTED) {
					showCancelFlag = 0;
				}

				if (writeFlag == 1 && sec > 1) {
					if (characteristic2 != null)
						mBluetoothLeService.writeCharacteristic(characteristic2, data);
					writeFlag = 1;
					sec = 0;
					retryCmdCount++;
				}

				if (retryCmdCount > 3) {
					writeFlag = 0;
					retryCmdCount = 0;
				}
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
				Log.d(TAG, "BroadcastReceiver onReceive  [" + action + "]");
				// mConnected = true;
				// updateConnectionState(R.string.connected);
				invalidateOptionsMenu();
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				Log.d(TAG, "BroadcastReceiver onReceive  [" + action + "]");
				// mConnected = false;
				// updateConnectionState(R.string.disconnected);
				invalidateOptionsMenu();
				// clearUI();
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				Log.d(TAG, "BroadcastReceiver onReceive  [" + action + "]");
				// Show all the supported services and characteristics on the
				// user interface.
				// displayGattServices(mBluetoothLeService.getSupportedGattServices());
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

				final int[] value = intent.getIntArrayExtra(BluetoothLeService.EXTRA_DATA);
				
				  for (int x = 0; x < value.length; x++) {
	                    Log.d(TAG, "value[" + x + "]=" + value[x]);
	                    Log.d("sandy", "value[" + x + "]=" + value[x]);
	                }
				showCancelFlag = 1;
				int format = value[1];
				int bpCmd = 0;
				if (value.length > 2)
					bpCmd = value[2];

				// Log.d(TAG, "download...format="+format+" bpCmd="+bpCmd);

				if (format == BT_CONFIG_INFO) {
					if (procDialog != null) {
						procDialog.dismiss();
					}
					if (bpCmd == BT_CONFIG_INFO_DEVICE) {

						signature[0] = value[3];
						signature[1] = value[4];
						signature[2] = value[5];
						versionTag[0] = value[6];
						versionTag[1] = value[7];
						versionTag[2] = value[8];
						versionTag[3] = value[9];
						versionTag[4] = value[10];
						versionTag[5] = value[11];
						int vDeviceID = value[12] * 256 * 256 * 256 + value[13] * 256 * 256 + value[14] * 256 + value[15];
						int MID = value[16] * 256 * 256 * 256 + value[17] * 256 * 256 + value[18] * 256 + value[19];
						tvID_No.setText(String.format("%x", vDeviceID));

						if (signature[0] == 'B' && signature[1] == 'P' && signature[2] == 'M') {
							// hidden 12-hour mode
							visible_12hr.setVisibility(View.VISIBLE);
						} else {
							// show 12-hour mode
							visible_12hr.setVisibility(View.INVISIBLE);
						}

						data[1] = BT_CONFIG_INFO;
						data[2] = BT_CONFIG_INFO_SETTING;
						if (characteristic2 != null)
							mBluetoothLeService.writeCharacteristic(characteristic2, data);

					} else if (bpCmd == BT_CONFIG_INFO_SETTING) {
						short vFirmwareVersion = (short) (value[3] * 256 + value[4]);
						bKey_beep = value[5] > 0 ? true : false;
						bHeartbeat_beep = value[6] > 0 ? true : false;
						// eric
						benabled12Mod = value[7] > 0 ? true : false;
						String v1, v2, v3, ver;

						ver = String.format("%d", vFirmwareVersion);
						if (ver.length() >= 5) {
							v3 = ver.substring(ver.length() - 2);
							ver = ver.substring(0, ver.length() - 2);

							v2 = ver.substring(ver.length() - 2);
							ver = ver.substring(0, ver.length() - 2);
							v1 = ver.substring(ver.length() - 1);

							tvFW_Ver.setText(String.format("%s.%s.%s", v1, v2, v3));
						} else {
							tvFW_Ver.setText("--------");
						}
						s_switch_key_beep.setChecked(bKey_beep);
						// if (bKey_beep)
						// iv_key_beep.setImageResource(R.drawable.toggle_buttons_on1);

						// else
						// iv_key_beep.setImageResource(R.drawable.toggle_buttons_off1);
						s_switch_heartbeat_beep.setChecked(bHeartbeat_beep);
						// if (bHeartbeat_beep)
						// iv_heartbeat_beep.setImageResource(R.drawable.toggle_buttons_on1);
						// else
						// iv_heartbeat_beep.setImageResource(R.drawable.toggle_buttons_off1);
						s_switch_12hr.setChecked(benabled12Mod);
						// if (benabled12Mod)
						// iv_12hr.setImageResource(R.drawable.toggle_buttons_on1);
						// else
						// iv_12hr.setImageResource(R.drawable.toggle_buttons_off1);

						bGotDeviceInfo = true;
					}

				} else if (format == BT_SETUP) {
					if (procDialog != null) {
						procDialog.dismiss();
					}
					if (bCmdResult == false) {
						showMessage(DeviceView.this, R.string.app_name, R.string.config_dev_ok);
						bCmdResult = true;
						bGotDeviceInfo = true;
						single_timer.cancel();
					}
				} else if (format == BT_MEASURE || format == BT_HEADER || format == BT_DOWNLOAD) {
					if (procDialog != null) {
						procDialog.dismiss();
					}
					single_timer.cancel();
					timer.cancel();
					try {
						unregisterReceiver(mGattUpdateReceiver);
					} catch (IllegalArgumentException e) {
						Log.d("alex", "unregistered");
					}
					leaveMessage(DeviceView.this, R.string.app_name, R.string.not_read_from_dev);

				}
				else if(format == 6)
				{
					
				}

			}
		}
	};

	// public void onSyncWithPhoneClick(View v) {
	// bSync_with_phone = !bSync_with_phone;
	// if (bSync_with_phone)
	// iv_sync_with_phone.setImageResource(R.drawable.toggle_buttons_on1);
	// else
	// iv_sync_with_phone.setImageResource(R.drawable.toggle_buttons_off1);
	// }

	// public void onKeyBeepClick(View v) {
	// bKey_beep = !bKey_beep;
	// if (bKey_beep)
	// iv_key_beep.setImageResource(R.drawable.toggle_buttons_on1);
	// else
	// iv_key_beep.setImageResource(R.drawable.toggle_buttons_off1);
	// }

	// public void onHeartbeatBeepClick(View v) {
	// bHeartbeat_beep = !bHeartbeat_beep;
	// if (bHeartbeat_beep)
	// iv_heartbeat_beep.setImageResource(R.drawable.toggle_buttons_on1);
	// else
	// iv_heartbeat_beep.setImageResource(R.drawable.toggle_buttons_off1);
	// }

	@SuppressWarnings("deprecation")
	public void onDownClick(View v) {

		showReading();
		bCmdResult = false;
		byte data[] = new byte[20];

		byte yyyy, MM, dd, hh, mm, ss;

		if (bSync_with_phone) {
			Date date = new Date();
			yyyy = (byte) (date.getYear() - 100);
			MM = (byte) (date.getMonth() + 1);
			dd = (byte) (date.getDate());
			hh = (byte) (date.getHours());
			mm = (byte) (date.getMinutes());
			ss = (byte) (date.getSeconds());
		} else {
			yyyy = MM = dd = hh = mm = ss = 0;
		}

		for (int i = 0; i < 20; i++)
			data[i] = 0;

		data[1] = BT_SETUP;
		data[2] = BT_SETUP_700X;
		data[3] = yyyy;
		data[4] = MM;
		data[5] = dd;
		data[6] = hh;
		data[7] = mm;
		data[8] = ss;
		data[9] = (byte) (bKey_beep == true ? 1 : 0);
		data[10] = (byte) (bHeartbeat_beep == true ? 1 : 0);
		// eric
		data[11] = (byte) (benabled12Mod == true ? 1 : 0);

		if (characteristic2 != null)
			mBluetoothLeService.writeCharacteristic(characteristic2, data);

		bGotDeviceInfo = false;

		sendcmdflag = true; // read from device

	}

	void showReading() {

		procDialog = ProgressDialog.show(DeviceView.this, DeviceView.this.getResources().getString(R.string.reading_from_device), DeviceView.this.getResources().getString(R.string.please_wait), true);

		single_timer = new Timer();
		single_timer.schedule(new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						if (procDialog != null)
							procDialog.dismiss();

						if (bGotDeviceInfo == false) {
							leaveMessage(DeviceView.this, R.string.app_name, R.string.not_read_from_dev);
							btn_Done.setEnabled(false);
							bCmdResult = true;
						}
					}
				});
			}
		}, 2000);
	}

}
