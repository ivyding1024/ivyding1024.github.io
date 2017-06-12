package com.mdbiomedical.app.vion.vian_health.view;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.mdbiomedical.app.vion.vian_health.R;
import com.mdbiomedical.app.vion.vian_health.R.anim;
import com.mdbiomedical.app.vion.vian_health.R.drawable;
import com.mdbiomedical.app.vion.vian_health.R.id;
import com.mdbiomedical.app.vion.vian_health.R.layout;
import com.mdbiomedical.app.vion.vian_health.R.string;
import com.mdbiomedical.app.vion.vian_health.service.BluetoothLeService;
import com.mdbiomedical.app.vion.vian_health.util.ChangeView;
import com.mdbiomedical.app.vion.vian_health.util.DeviceConstant;

//import com.facebook.Session;

public class ScanView extends Activity {
	private final static String TAG = "eric";
	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
	public static final String BLE_SETTING = "BLE_SETTING";
	public static final String BLE_ON = "BLE_ON";
	public static final String BLE_MAC = "BLE_MAC";
	public static final String BLE_DEV_NAME = "BLE_DEV_NAME";

	final int TABLE_LIST_SIZE = 8;

	// public static ImageView iv_ble_switch;
	public Switch scan_switch;
	ProgressBar ctrlActivityIndicator;

	LinearLayout ll_settings_devices;
	LinearLayout ll_settings_ble;
	TextView tvMAC;

	public static boolean bluetoothSwitch = true;

	private LeDeviceListAdapter mLeDeviceListAdapter;
	private BluetoothAdapter mBluetoothAdapter;
	private boolean mScanning;
	private Handler mHandler;
	private ListView list;
	private static final int REQUEST_ENABLE_BT = 1;
	// Stops scanning after 10 seconds.
	private static final long SCAN_PERIOD = 10000;
	ProgressDialog progress;
	Context context;
	AlertDialog dialog;
	ProgressDialog.Builder builder;
	int wait_time = 0;
	String[] namelist = new String[100];
	String[] addresslist = new String[100];
	Runnable runnable;

	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}

	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scan_activity);
		context = this;
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		init();

		mHandler = new Handler();
		progress = ProgressDialog.show(this, "", "Loading", true);

		SharedPreferences settings = getSharedPreferences(BLE_SETTING, 0);
		// bluetoothSwitch = settings.getBoolean(BLE_ON, true);
		tvMAC.setText(settings.getString(BLE_DEV_NAME, ""));

		setUI();

		// Use this check to determine whether BLE is supported on the device.
		// Then you can
		// selectively disable BLE-related features.
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT)
					.show();
			finish();
		}

		// Initializes a Bluetooth adapter. For API level 18 and above, get a
		// reference to
		// BluetoothAdapter through BluetoothManager.

		// Checks if Bluetooth is supported on the device.
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, R.string.error_bluetooth_not_supported,
					Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
	}

	public String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return capitalize(model);
		} else {
			return capitalize(manufacturer) + " " + model;
		}
	}

	private String capitalize(String s) {
		if (s == null || s.length() == 0) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		} else {
			return Character.toUpperCase(first) + s.substring(1);
		}
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);

		finish();
		overridePendingTransition(R.anim.slide_no, R.anim.slide_out_left);
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
				
		scan_switch = (Switch) findViewById(R.id.scan_switch);
		scan_switch.setChecked(true);
		ctrlActivityIndicator = (ProgressBar) findViewById(R.id.ctrlActivityIndicator);
		list = (ListView) findViewById(R.id.lv_settings_ble);
		ll_settings_devices = (LinearLayout) findViewById(R.id.ll_settings_devices);
		// ll_settings_ble = (LinearLayout) findViewById(R.id.ll_settings_ble);
		tvMAC = (TextView) findViewById(R.id.tvMAC);
		TextView tv_list_title = (TextView) findViewById(R.id.tv_list_title);

		LinearLayout ll_list_back = (LinearLayout) findViewById(R.id.ll_list_back);
		tv_list_title.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				(int) (DeviceConstant.screenHeight * 0.03f));

		// ??�?????��????
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

		scan_switch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				bluetoothSwitch = isChecked;
				if (bluetoothSwitch == true) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							// do the thing that takes a long time
							wait_time = 0;
							while (true) {
								if (HomeView.ble_status == 0
										|| HomeView.ble_status == 1
										|| HomeView.ble_status == 2) {

									try {
										scanLeDevice(true);
										bluetoothSwitch = true;
										setUI();
										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												progress.dismiss();
											}
										});
									} catch (IllegalArgumentException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

									return;
								}
								wait_time++;
								if (wait_time > 5) {
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											progress.dismiss();
											wait_time = 0;
											bluetoothSwitch = false;
											setUI();
										}
									});
									return;
								}

								else {
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								}

							}

						}
					}).start();
				} else {
					try {
						scanLeDevice(bluetoothSwitch);
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		// 1. Instantiate an AlertDialog.Builder with its constructor
		// AlertDialog.Builder builder = new AlertDialog.Builder(ScanView.this);
		//
		// // 2. Chain together various setter methods to set the dialog
		// // characteristics
		// builder.setMessage("a").setTitle("bb");
		//
		// builder.setNegativeButton(R.string.cancel, new
		// DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int id) {
		// // User cancelled the dialog
		// }
		// });
		// builder.setPositiveButton(R.string.ok, new
		// DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int id) {
		// // User clicked OK button
		// }
		// });
		//
		// // 3. Get the AlertDialog from create()
		// dialog = builder.create();
		//

	}

	private void setUI() {

		if (bluetoothSwitch == true) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// scan_switch.setText("STOP");
					scan_switch.setChecked(true);
					ctrlActivityIndicator.setVisibility(View.VISIBLE);
				}
			});

		} else {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// scan_switch.setText("START");
					scan_switch.setChecked(false);
					ctrlActivityIndicator.setVisibility(View.INVISIBLE);
				}
			});
		}
	}

	// switch
	public void clickSwitch(View v) {

		switch (v.getId()) {

		case R.id.scan_switch_l:
			bluetoothSwitch = !bluetoothSwitch;
			Log.d("alex", "bluetoothSwitch=" + bluetoothSwitch);
			progress = ProgressDialog.show(this, "", "Loading", true);
			new Thread(new Runnable() {
				@Override
				public void run() {
					// do the thing that takes a long time

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					wait_time = 0;
					while (true) {
						if (HomeView.ble_status == 0
								|| HomeView.ble_status == 1
								|| HomeView.ble_status == 2) {
							wait_time = -10;
							try {
								scanLeDevice(bluetoothSwitch);
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										progress.dismiss();
										setUI();
									}
								});
							} catch (IllegalArgumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							return;
						}

						wait_time++;
						if (wait_time > 5) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									progress.dismiss();
									wait_time = 0;
									bluetoothSwitch = false;
									setUI();
								}
							});
							return;
						} else {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

					}
				}
			}).start();

			setUI();
			SharedPreferences settings = getSharedPreferences(BLE_SETTING, 0);
			settings.edit().putBoolean(BLE_ON, bluetoothSwitch).commit();
			break;
		}

	}

	@Override
	protected void onResume() {

		super.onResume();
		HomeView.home_pressed = "disable";
		setUI();

		// Ensures Bluetooth is enabled on the device. If Bluetooth is not
		// currently enabled,
		// fire an intent to display a dialog asking the user to grant
		// permission to enable it.
		if (!mBluetoothAdapter.isEnabled()) {

			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}

		// Initializes list view adapter.
		mLeDeviceListAdapter = new LeDeviceListAdapter();
		list.setAdapter(mLeDeviceListAdapter);
		// mLeDeviceListAdapter = new LeDeviceListAdapter();
		// setListAdapter(mLeDeviceListAdapter);

		new Thread(new Runnable() {
			@Override
			public void run() {
				// do the thing that takes a long time
				wait_time = 0;
				while (true) {
					if (HomeView.ble_status == 0 || HomeView.ble_status == 1
							|| HomeView.ble_status == 2) {

						try {
							scanLeDevice(true);

						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						bluetoothSwitch = true;
						setUI();
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								progress.dismiss();
							}
						});
						return;
					}
					wait_time++;
					if (wait_time > 5) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								progress.dismiss();
								wait_time = 0;
								bluetoothSwitch = false;
								setUI();
							}
						});
						return;
					}

					else {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				}

			}
		}).start();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		HomeView.home_pressed = "wait";
		try {
			scanLeDevice(false);

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// mLeDeviceListAdapter.clear();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// User chose not to enable Bluetooth.
		if (requestCode == REQUEST_ENABLE_BT
				&& resultCode == Activity.RESULT_CANCELED) {
			finish();
			return;
		}
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void scanLeDevice(final boolean enable) {
		mHandler.removeCallbacks(runnable);
		if (enable) {
			// Stops scanning after a pre-defined scan period.

			runnable = new Runnable() {
				@Override
				public void run() {
					Log.w("alex", "postDelayed");
					while (true) {

						Log.w("alex", "postDelayed while");
						if (HomeView.ble_status == 0
								|| HomeView.ble_status == 1
								|| HomeView.ble_status == 2) {
							mScanning = false;
							if (mBluetoothAdapter.isEnabled())
								mBluetoothAdapter.stopLeScan(mLeScanCallback);
							invalidateOptionsMenu();
							// mLeDeviceListAdapter.clear();
							bluetoothSwitch = false;
							setUI();

							// scanLeDevice(bluetoothSwitch);
							break;
						} else {

							mScanning = false;
							bluetoothSwitch = false;
							setUI();
							break;

						}

					}

				}
			};
			mHandler.postDelayed(runnable, SCAN_PERIOD);
			mLeDeviceListAdapter.clear();
			mScanning = true;
			if (mBluetoothAdapter.isEnabled())
				mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mScanning = false;
			if (mBluetoothAdapter.isEnabled())
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					bluetoothSwitch = false;
					setUI();
				}
			});
			// mLeDeviceListAdapter.clear();
		}

	}

	// Adapter for holding devices found through scanning.
	private class LeDeviceListAdapter extends BaseAdapter {
		private ArrayList<BluetoothDevice> mLeDevices;
		private LayoutInflater mInflator;

		public LeDeviceListAdapter() {
			super();
			mLeDevices = new ArrayList<BluetoothDevice>();
			mInflator = ScanView.this.getLayoutInflater();
		}

		public void addDevice(BluetoothDevice device) {

			if (!mLeDevices.contains(device)) {

				mLeDeviceListAdapter.notifyDataSetChanged();
				mLeDevices.add(device);

			}
		}

		public BluetoothDevice getDevice(int position) {
			return mLeDevices.get(position);
		}

		public void clear() {
			mLeDevices.clear();
		}

		@Override
		public int getCount() {
			return mLeDevices.size();
		}

		@Override
		public Object getItem(int i) {
			return mLeDevices.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(final int i, View view, ViewGroup viewGroup) {

			ViewHolder viewHolder;

			if (view == null) {
				view = mInflator.inflate(R.layout.listitem_device, viewGroup,
						false);
				view.setMinimumHeight(20);
				viewHolder = new ViewHolder();
				viewHolder.deviceAddress = (TextView) view
						.findViewById(R.id.device_address);
				// viewHolder.deviceAddress.setVisibility(View.VISIBLE);
				viewHolder.deviceName = (TextView) view
						.findViewById(R.id.device_name);
				viewHolder.deviceRow = (LinearLayout) view
						.findViewById(R.id.device_row);
				view.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			final BluetoothDevice device = mLeDevices.get(i);
			final String deviceName = device.getName();

			if (deviceName != null)

				if (deviceName != null && deviceName.length() > 0) {
					viewHolder.deviceName.setText(deviceName);
					namelist[i] = deviceName + "";
					addresslist[i] = device.getAddress() + "";
				} else
					viewHolder.deviceName.setText(R.string.unknown_device);
			viewHolder.deviceAddress.setText(device.getAddress());

			viewHolder.deviceRow.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {

					SharedPreferences settings = getSharedPreferences(
							BLE_SETTING, 0);
					settings.edit().putString(BLE_MAC, addresslist[i]).commit();
					settings.edit().putString(BLE_DEV_NAME, namelist[i])
							.commit();
					tvMAC.setText(settings.getString(BLE_DEV_NAME, namelist[i]));
					HomeView.mDeviceAddress = addresslist[i];
					Log.d("address", " onClick HomeView.address="
							+ HomeView.mDeviceAddress);
					Log.d("address", " onClick addresslist[" + i + "]="
							+ addresslist[i]);
					// if (device.getName() == null) {
					// mLeDevices.clear();
					// } else {
					// SharedPreferences settings =
					// getSharedPreferences(BLE_SETTING, 0);
					// settings.edit().putString(BLE_MAC,
					// device.getAddress()).commit();
					// settings.edit().putString(BLE_DEV_NAME,
					// device.getName()).commit();
					// tvMAC.setText(settings.getString(BLE_DEV_NAME,
					// device.getName()));
					// HomeView.mDeviceAddress = device.getAddress();
					// }

					// progress.setCancelable(false);
					//
					// progress.setCanceledOnTouchOutside(false);
					// // progress.setButton("??��???", new Bt1DialogListener());
					// progress.setButton(DialogInterface.BUTTON_NEGATIVE,
					// "Cancel", new DialogInterface.OnClickListener() {
					// @Override
					// public void onClick(DialogInterface dialog, int which) {
					// dialog.dismiss();
					// }
					// });

					// progress = ProgressDialog.show(context, "Connecting",
					// "Connecting to" + namelist[i], true);
					HomeView.ble_status = 4;
					HomeView.sec_disconnect = 4;

					builder = new ProgressDialog.Builder(ScanView.this);
					builder.setMessage("Loading....").setTitle("");
					// builder.setNegativeButton(R.string.cancel, new
					// DialogInterface.OnClickListener() {
					// public void onClick(DialogInterface dialog, int id) {
					// // User cancelled the dialog
					// }
					// });
					dialog = builder.create();

					dialog.setCancelable(false);
					dialog.setCanceledOnTouchOutside(false);
					dialog.show();

					wait_time = 0;

					new ConnectTask().execute();

				}
			});

			return view;
		}
	}

	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (device.getName() != null) {
						Log.d(TAG,
								"..................getName=" + device.getName());
						//if (device.getName().equals("ME90         ")
						//		|| device.getName().equals("BM95         "))
					//	{
							mLeDeviceListAdapter.addDevice(device);
					//	}
					}

				}
			});
		}
	};

	static class ViewHolder {
		TextView deviceName;
		TextView deviceAddress;
		LinearLayout deviceRow;
	}

	private class ConnectTask extends AsyncTask<String, Void, String> {
		/**
		 * The system calls this to perform work in a worker thread and delivers
		 * it the parameters given to AsyncTask.execute()
		 */
		protected String doInBackground(String... urls) {
			wait_time = 0;
			mHandler.removeCallbacks(runnable);
			while (!mBluetoothAdapter.isEnabled() || HomeView.ble_status == 5) {
				wait_time++;
				if (wait_time > 5) {
					dialog.cancel();
					Toast.makeText(ScanView.this, "Bluetooth not open",
							Toast.LENGTH_SHORT).show();

					return "";

				} else {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			HomeView.ble_status = 4;
			HomeView.sec_disconnect = 4;

			wait_time = 0;
			while (true) {
				wait_time++;
				if (wait_time == 8) {
					dialog.cancel();
					builder.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// User cancelled the dialog
									wait_time = 50;
								}
							});
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// progress.dismiss();
							dialog = builder.create();
							dialog.show();
						}
					});

				}
				if (wait_time > 30) {
					try {
						scanLeDevice(false);

					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// progress.dismiss();
							dialog.cancel();
							wait_time = 0;
						}
					});
					return "";
				}
				if (HomeView.ble_status == 2) {
					try {
						scanLeDevice(false);

					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// progress.dismiss();
							dialog.cancel();
							wait_time = 0;
						}
					});
					return "";
				}

				else {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}

		}

		/**
		 * The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground()
		 */
		protected void onPostExecute(String result) {

		}
	}
}
