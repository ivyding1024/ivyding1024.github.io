package com.mdbiomedical.app.vion.vian_health.view;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mdbiomedical.app.vion.vian_health.R;
import com.mdbiomedical.app.vion.vian_health.R.drawable;
import com.mdbiomedical.app.vion.vian_health.helper.DatabaseHelper;
import com.mdbiomedical.app.vion.vian_health.model.RecordList;
import com.mdbiomedical.app.vion.vian_health.model.User;
import com.mdbiomedical.app.vion.vian_health.service.AuthorityService;
import com.mdbiomedical.app.vion.vian_health.service.BluetoothLeService;
import com.mdbiomedical.app.vion.vian_health.service.FIRfilter;
import com.mdbiomedical.app.vion.vian_health.service.IIRlowcutFilter;
import com.mdbiomedical.app.vion.vian_health.service.MainService;
import com.mdbiomedical.app.vion.vian_health.service.SampleGattAttributes;
import com.mdbiomedical.app.vion.vian_health.service.model.ConstantS;
import com.mdbiomedical.app.vion.vian_health.service.model.UserDataItem;
import com.mdbiomedical.app.vion.vian_health.util.AsyncTaskUtilsPost;
import com.mdbiomedical.app.vion.vian_health.util.DeviceConstant;
import com.mdbiomedical.app.vion.vian_health.util.HttpUtils;
import com.mdbiomedical.app.vion.vian_health.util.ViewUtils;
import com.mdbiomedical.app.vion.vian_health.view.DeviceView.SendDownloadCmd;

public class HomeView extends Activity implements AnimationListener {

	// public static ImageView iv_home_left_side_pic;
	public static String lastview = "menu_measure";

	public static DisplayMetrics dm = new DisplayMetrics();
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

	int Debug_lose_package = 0;
	public static int Debug_error_code = 0;

	public static int Debug_connected = 0;
	TextView error133, error8, error19, erroro;
	public static int error133i = 0;
	public static int error8i = 0;
	public static int error19i = 0;
	public static String erroros = "";
	public static int e_service = 0;
	public static int null_serv = 0;
	private final static String TAG = "sandy";// BluetoothLeService.class.getSimpleName();

	public static Context context;
	HomeView me = this;
	public final static int ECG_CHART_MILLISEC = 5000;
	int test_score = 60, test_time = 900, animDuration = 200;
	int main_w, main_h, w, h;
	int left;
	Boolean settingResult = false, settingResultEnd = false;
	Date date = new Date();
	Random random = new Random();
	// int[] resId = { R.drawable.home_a, R.drawable.home_b, R.drawable.home_c,
	// R.drawable.home_d, R.drawable.home_e, R.drawable.home_f, };
	Animation animMenuTranslate;
	Animation animMenuTranslateOnResume;
	TextView tv_mea_datetime;
	ImageView iv_home_menu, mea_user, ecg_changer, mea_share;
	// TextView[] tvaDay = new TextView[7];
	// TextView[] tvaWeekday = new TextView[7];
	public static boolean bECGMode = false;
	private Handler handler = new Handler();
	Bitmap tempBitmap;
	Canvas tempCanvas;
	Paint myPaint = new Paint();
	public static int iECGWidth,iECGWidth1;
	public static int iECGHeight,iECGHeight1;
	public static long lPreTimestamp, lLineStartTime;
	public static long lEcgTimeElapsed;
	TextView hr, mmHg;
	static HrTrendView hrTrendGraph;
	LinearLayout mea_ble_status_v;
	LinearLayout mea_context;
	LinearLayout records_bar1, records_bar2;
	LinearLayout home_bg;
	LinearLayout mea_ble_view;
	TextView mea_ble_state;
	LinearLayout ll_after_mea, ll_before_mea, ll_mea_result;
	TextView records_pulse, records_sys, records_dia, records_pulse_txt;

	boolean bNewMeasure = true;
	boolean hasDevice = false;

	public static IIRlowcutFilter iirlowcut = new IIRlowcutFilter();
	public static FIRfilter firFilter = new FIRfilter();

	Intent intent;
	boolean menuOut = false;
	boolean bActivated;
	public static boolean bRunning = true;
	AnimParams animParams = new AnimParams();

	// AnimationSet anim1;
	// AnimationSet anim2;
	public static LinearLayout ll_main_bg, ll_menu;
	public static ECGImageView iv_ECG;
	public static LinearLayout menu_measure, menu_records, menu_analysis, menu_download, menu_information, menu_settings;

	DatabaseHelper databaseHelper = new DatabaseHelper(this);

	public static AsyncTask<List<String>, Void, Void> downloadPhotoTaskUtils;

	public static ProgressDialog myDialog;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm a", java.util.Locale.getDefault());

	String dateString = DateFormat.getBestDateTimePattern(java.util.Locale.getDefault(), "yyyy/MM/dd HH:mm");
	java.text.DateFormat dateTimeInstance = new SimpleDateFormat(dateString, java.util.Locale.getDefault());

	// BlE related variables
	private BluetoothAdapter mBluetoothAdapter;
	private boolean mScanning;

	private Intent gattServiceIntent;
	private boolean mConnected;
	private static final int STATE_DISCONNECTED = 0;
	private static final int STATE_CONNECTING = 1;
	private static final int STATE_CONNECTED = 2;

	private static final long SCAN_PERIOD = 10000;
	public static BluetoothLeService mBluetoothLeService;
	public static String mDeviceAddress;
	int state;
	private int sec, sec_receive;
	public static int sec_disconnect;
	public static int discover_service_wait = 0;
	public static int error_disconnect = 0;
	// private int sec_log;
	Timer timer = new Timer(true);
	private boolean isNotify;
	private boolean isEnabled;
	private boolean isReceive = true;
	private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	private BluetoothGattCharacteristic mNotifyCharacteristic;
	private final String LIST_NAME = "NAME";
	private final String LIST_UUID = "UUID";
	private BluetoothGattCharacteristic characteristic1;
	public static BluetoothGattCharacteristic characteristic2;

	int tmpseq = 0, seq = 0;
	int m_seq = 0, m_tmpseq = 0;
	int lost = 0;
	int format, bpCmd;
	public static int bpPulseRate, bpDiastolic;
	int MDTrendIndex, MDTrendIndexTmp;
	int MDRawData;

	byte[] FlashBuffer1 = new byte[256];
	int ChannelNo;
	int ChannelMSB;
	int ChannelData;
	final int CH_IR = 2;
	final int CH_PPG_RAW = 4;
	final int CH_PPG_FFT = 5;
	final int CH_PPG_RRI = 6;
	final int CH_BQ = 7;
	final int CH_COHERENCE = 8;
	final int CH_RSA = 12;
	final int CH_BEATINDEX = 11;
	final int CH_HR = 70;
	final int CH_MMHG = 71;
	final int CH_BEAT_INDEX = 72;
	final int CH_ECG = 73;
	final int CH_AUTOSCALE = 74;
	public static int ecgCount = 0, iDrawCnt = 0, displayCount = 0;
	public static short[] rawData = new short[8704];
	public static short[] filteredData = new short[8704];
	public static short[] displayData = new short[8704];
	public static short[] HRData = new short[90];
	public static int[] HRDataTimestamp = new int[90];
	public static short iHrCnt = 0;
	static int testcount = 0;
	private byte[] data = new byte[20];
	private GestureDetector gd;
	private View.OnTouchListener gestureListener;
	static boolean isECGDoubleTap = false;
	public static int ecgSize = 1;
	public static int ble_status = -1;
	private boolean sendcmdflag;
	boolean bKey_beep = false, bHeartbeat_beep = false;
	public static String home_pressed = "disable";// disable,wait,enable
	String today = dateFormat.format(date).toString();

	//sandy 1107
	LinearLayout before,mea_context2,mea_context2_heart,mea_context2_bp;
	public static ECGView iv_ECG1;
	
	TextView records_hr1,mea_datetime1,mea_context2_text;
	Boolean ble=false;
	ImageView mea_context_ble1,mea_context2_img,draw_line;
	public static int img= 0;
	
	//update server 
	List<RecordList> newRecordList = new ArrayList<RecordList>(); ;
	//END
	User user ;
	MainService mainService;
	int serverflag=0,downloadflag=0;
	AuthorityService authorityService = new AuthorityService();
	int[] FlashBuffer = new int[256];
	String email,password;
	String time;
	
	
	//com.mdbiomedical.app.vion.Lidl.service.NDKWrap NDKWrap=new com.mdbiomedical.app.vion.Lidl.service.NDKWrap();
	
	
	// not initialize
	// disconnect=0,connecting=1,connected=2
	// disable ble=3,enable=4
	// error ble = 5;
	// wait discover service = 6;
	public class timerTask extends TimerTask {
		public void run() {
			//Log.d("alex", "sec_disconnect=" + sec_disconnect +NDKWrap.helloString());
			if (home_pressed == "enable") {
				// home���?����??����蝺�?���迫����
				if (mBluetoothLeService != null) {

					try {
						mBluetoothLeService.disconnect();
						mBluetoothLeService.close();
						unbindService(mServiceConnection);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Log.e("alex", "not binded or service error");
					}
					mBluetoothLeService = null;
					//mBluetoothAdapter.disable();
					ble_status = 4;
					sec_disconnect = 2;
				}

				Log.d("alex", "home pressed");
				
				return;
			}
			//Log.e("sandy", "337_newRecordList.size()="+newRecordList.size());
			//update server   
			if(newRecordList.size()>0&&serverflag==0)
			{
				Log.e("sandy", "344_newRecordList.size()="+newRecordList.size());
				for(int i=0;i<newRecordList.size();i++)
				{
					Log.d("sandy", "newRecordList.get(i).sDatetime="+newRecordList.get(i).sDatetime);
					Log.d("sandy", "newRecordList.get(i).updateflag="+newRecordList.get(i).updateflag);
					if(newRecordList.get(i).updateflag == 0)//0:no update,1:updated
					{
						serverflag=0;
						//String url1="";
						 List<NameValuePair> addrawdata= new ArrayList<NameValuePair>();
						//Log.d("sandy", "newRecordList.get(i).AnalysisType="+newRecordList.get(i).AnalysisType);
						if(newRecordList.get(i).AnalysisType==RecordsView.TYPE_BP)
						{
							Log.d("sandy", "BP update" );
							time=newRecordList.get(i).sDatetime;
							//url1 = "http://"+ ConstantS.SERVER_ADDRESS+ "/addBPRawData.php";
							 addrawdata = new ArrayList<NameValuePair>();
							// Add Post Data
							 	addrawdata.add(new BasicNameValuePair("Datetime", newRecordList.get(i).sDatetime));
								addrawdata.add(new BasicNameValuePair("Seq",  String.valueOf(newRecordList.get(i).Seq)));
								addrawdata.add(new BasicNameValuePair("UserEmail",user.getEmail()));
								addrawdata.add(new BasicNameValuePair("Signature", newRecordList.get(i).Signature));
								addrawdata.add(new BasicNameValuePair("FirmwareVersion",String.valueOf( newRecordList.get(i).FirmwareVersion)));
								addrawdata.add(new BasicNameValuePair("HardwareVersion", String.valueOf( newRecordList.get(i).HardwareVersion)));
								addrawdata.add(new BasicNameValuePair("HeaderSize", String.valueOf(newRecordList.get(i).HeaderSize)));
								addrawdata.add(new BasicNameValuePair("VersionTag", newRecordList.get(i).VersionTag));
								addrawdata.add(new BasicNameValuePair("DeviceID", String.format("%x",newRecordList.get(i).DeviceID)));
								addrawdata.add(new BasicNameValuePair("SamplingRate", String.valueOf(newRecordList.get(i).SamplingRate)));
								
								addrawdata.add(new BasicNameValuePair("GainSetting",  String.valueOf(newRecordList.get(i).GainSetting)));
								addrawdata.add(new BasicNameValuePair("Resolution",  String.valueOf(newRecordList.get(i).Resolution)));
								addrawdata.add(new BasicNameValuePair("Noise",  String.valueOf(newRecordList.get(i).Noise)));
								addrawdata.add(new BasicNameValuePair("PhysicalMinimum",  String.valueOf(newRecordList.get(i).PhysicalMinimum)));
								addrawdata.add(new BasicNameValuePair("PhysicalMaximum",  String.valueOf(newRecordList.get(i).PhysicalMaximum)));
								addrawdata.add(new BasicNameValuePair("DigitalMinimum",  String.valueOf(newRecordList.get(i).DigitalMinimum)));
								addrawdata.add(new BasicNameValuePair("DigitalMaximum",  String.valueOf(newRecordList.get(i).DigitalMaximum)));			
								addrawdata.add(new BasicNameValuePair("Prefiltering",  String.valueOf(newRecordList.get(i).Prefiltering)));
								addrawdata.add(new BasicNameValuePair("TotalSize",  String.valueOf(newRecordList.get(i).TotalSize)));
								addrawdata.add(new BasicNameValuePair("UserMode",  String.valueOf(newRecordList.get(i).UserMode)));
								
								addrawdata.add(new BasicNameValuePair("RSensitivity",  String.valueOf(newRecordList.get(i).RSensitivity)));
								addrawdata.add(new BasicNameValuePair("WSensitivity",  String.valueOf(newRecordList.get(i).WSensitivity)));
								
								addrawdata.add(new BasicNameValuePair("HeartRate",  String.valueOf(newRecordList.get(i).HeartRate)));
								addrawdata.add(new BasicNameValuePair("Tachycardia",  String.valueOf(newRecordList.get(i).Tachycardia)));
								addrawdata.add(new BasicNameValuePair("Bradycardia",  String.valueOf(newRecordList.get(i).Bradycardia)));
								addrawdata.add(new BasicNameValuePair("Pause",  String.valueOf(newRecordList.get(i).Pause)));
								addrawdata.add(new BasicNameValuePair("PauseValue",  String.valueOf(newRecordList.get(i).PauseValue)));
								addrawdata.add(new BasicNameValuePair("Rhythm",  String.valueOf(newRecordList.get(i).Rhythm)));
								addrawdata.add(new BasicNameValuePair("Waveform",  String.valueOf(newRecordList.get(i).Waveform)));
								addrawdata.add(new BasicNameValuePair("WaveformStable",  String.valueOf(newRecordList.get(i).WaveformStable)));	
								addrawdata.add(new BasicNameValuePair("EntryPosition",  String.valueOf(newRecordList.get(i).EntryPosition)));
								addrawdata.add(new BasicNameValuePair("TachycardiaValue",  String.valueOf(newRecordList.get(i).TachycardiaValue)));
								
								addrawdata.add(new BasicNameValuePair("BradycardiaValue",  String.valueOf(newRecordList.get(i).BradycardiaValue)));
								addrawdata.add(new BasicNameValuePair("MID",  String.format("%x",newRecordList.get(i).MID)));
								addrawdata.add(new BasicNameValuePair("BPMNoiseFlag",  String.valueOf(newRecordList.get(i).BPMNoiseFlag)));
								addrawdata.add(new BasicNameValuePair("BPHeartRate",  String.valueOf(newRecordList.get(i).BPHeartRate)));								
								addrawdata.add(new BasicNameValuePair("HighBloodPressure",  String.valueOf(newRecordList.get(i).HighBloodPressure)));
								addrawdata.add(new BasicNameValuePair("LowBloodPressure",  String.valueOf(newRecordList.get(i).LowBloodPressure)));
								addrawdata.add(new BasicNameValuePair("WHOIndicate",  String.valueOf(newRecordList.get(i).WHOIndicate)));
								addrawdata.add(new BasicNameValuePair("DCValue",  String.valueOf(newRecordList.get(i).DCValue)));
								addrawdata.add(new BasicNameValuePair("AnalysisType",  String.valueOf(newRecordList.get(i).AnalysisType)));
								addrawdata.add(new BasicNameValuePair("CheckSum",  String.valueOf(newRecordList.get(i).CheckSum)));
								
								addrawdata.add(new BasicNameValuePair("Filename",  String.valueOf(newRecordList.get(i).sFilename)));						
								addrawdata.add(new BasicNameValuePair("Note",  newRecordList.get(i).sNote));
								//Log.d("sandy", "HRData="+ newRecordList.get(i).HRData);
								//
								new AsyncTaskUtilsPost(HomeView.this) {
									@Override
									public void refreshUI(String result) {
										if (result != null && result.equals("success")) { // 上傳成功回傳true
											databaseHelper.updateflag(time, 1);
											Log.e("sandy", "BP_updateRawdata success");
										}
										else
										{
											Log.e("sandy", "BP_updateRawdata fail");
											
											
										}
									}
									
								}.execute(addrawdata);
							//
						}
						else//ECG
						{
							time=newRecordList.get(i).sDatetime;
							String updateHRData="",updateHRDataTimestamp="",updateRawData="",updateECG="";
							Log.d("sandy", "ECG update" );
							if(newRecordList.get(i).HRData==null)
							{
								Log.v("sandy", "ECG filter...." );
								//newRecordList.get(i).serverflag=1;
								String filename=newRecordList.get(i).sFilename;
								byte[] FlashBuffer2 = new byte[256];
								int iHrCnt1 = 0;
								int iEcgCount = 0;
								
								 short[] rawData1 = new short[8704];
								short[] HRData1 = new short[90];
								int[] HRDataTimestamp1 = new int[90];
								
								
								//Log.d("sandy", "newRecordList.get(i).HRDataTimeStamp="+newRecordList.get(i).HRDataTimeStamp);
								final short iDrawShift = 256 * 3;
								FileInputStream dataIn = null;
								DataInputStream dataInD;
								try {
									dataIn = openFileInput(filename);
									dataInD = new DataInputStream(new BufferedInputStream(dataIn));
									dataInD.read(FlashBuffer2);
									iHrCnt1 = dataInD.readInt();
									updateHRDataTimestamp+=iHrCnt1+",";
									for (int j = 0; j < HRData1.length; j++) {
										HRDataTimestamp1[j] = dataInD.readInt();
										HRData1[j] = dataInD.readShort();
										updateHRData+=HRData1[j]+";";
										updateHRDataTimestamp+=HRDataTimestamp1[j]+";";
										//Log.d("sandy", "HRDataTimestamp["+j+"]=" + HRDataTimestamp[j] +  ", HRData["+j+"]=" + HRData[j]);
									}
									//Log.d("sandy", "HomeView_updateHRData=" + updateHRData+",updateHRDataTimestamp="+updateHRDataTimestamp);
									iEcgCount = dataInD.readInt();
									short iData2,iData1;//iData1=for store rawdata;
									
			
									for (int j = 0; j< iDrawShift; j++) {
										iData1 = dataInD.readShort();
										updateRawData+=iData1+";";
										//Log.d("sandy", "dataInD.readShort()=" + dataInD.readShort() );
										iData2 = HomeView.iirlowcut.IIRlowcutFiltering(iData1);
										iData2 = HomeView.firFilter.FIRfiltering(iData2);
										updateECG+=iData2+";";
									}
									
									for (int j = 0; j < rawData1.length - iDrawShift; j++) {
										iData1 = dataInD.readShort();
										updateRawData+=iData1+";";
										iData2 = HomeView.iirlowcut.IIRlowcutFiltering(iData1);
										rawData1[j] = HomeView.firFilter.FIRfiltering(iData2);
										updateECG+=rawData1[j]+";";
										//Log.d("sandy", "rawData["+i+"]=" + rawData[i] );
									}
									//dataInD.read(Note);
			
									dataInD.close();
								
									//Log.d("sandy", "iHrCnt1=" + iHrCnt1 + ", iEcgCount=" + iEcgCount);
									RecordList rec = new RecordList();
									//rec.sDatetime=datetime;
									rec.ECG=updateECG;
									rec.RawData=updateRawData;
									rec.HRData=updateHRData;
									rec.HRDataTimeStamp=updateHRDataTimestamp;
									databaseHelper.updateECGdata(newRecordList.get(i).sDatetime, rec);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							else//no filter direct update
							{
								Log.v("sandy", "no filter direct update" );
								updateECG=newRecordList.get(i).ECG;
								updateRawData=newRecordList.get(i).RawData;
								updateHRData=newRecordList.get(i).HRData;
								updateHRDataTimestamp=newRecordList.get(i).HRDataTimeStamp;
							}
							
							//url1 = "http://"+ ConstantS.SERVER_ADDRESS+ "/addECGRawData.php";
							 addrawdata = new ArrayList<NameValuePair>();
							// Add Post Data
							 	addrawdata.add(new BasicNameValuePair("Datetime", newRecordList.get(i).sDatetime));
								addrawdata.add(new BasicNameValuePair("Seq",  String.valueOf(newRecordList.get(i).Seq)));
								addrawdata.add(new BasicNameValuePair("UserEmail",user.getEmail()));
								addrawdata.add(new BasicNameValuePair("Signature", newRecordList.get(i).Signature));
								addrawdata.add(new BasicNameValuePair("FirmwareVersion",String.valueOf( newRecordList.get(i).FirmwareVersion)));
								addrawdata.add(new BasicNameValuePair("HardwareVersion", String.valueOf( newRecordList.get(i).HardwareVersion)));
								addrawdata.add(new BasicNameValuePair("HeaderSize", String.valueOf(newRecordList.get(i).HeaderSize)));
								addrawdata.add(new BasicNameValuePair("VersionTag", newRecordList.get(i).VersionTag));
								addrawdata.add(new BasicNameValuePair("DeviceID", String.format("%x",newRecordList.get(i).DeviceID)));
								addrawdata.add(new BasicNameValuePair("SamplingRate", String.valueOf(newRecordList.get(i).SamplingRate)));
								
								addrawdata.add(new BasicNameValuePair("GainSetting",  String.valueOf(newRecordList.get(i).GainSetting)));
								addrawdata.add(new BasicNameValuePair("Resolution",  String.valueOf(newRecordList.get(i).Resolution)));
								addrawdata.add(new BasicNameValuePair("Noise",  String.valueOf(newRecordList.get(i).Noise)));
								addrawdata.add(new BasicNameValuePair("PhysicalMinimum",  String.valueOf(newRecordList.get(i).PhysicalMinimum)));
								addrawdata.add(new BasicNameValuePair("PhysicalMaximum",  String.valueOf(newRecordList.get(i).PhysicalMaximum)));
								addrawdata.add(new BasicNameValuePair("DigitalMinimum",  String.valueOf(newRecordList.get(i).DigitalMinimum)));
								addrawdata.add(new BasicNameValuePair("DigitalMaximum",  String.valueOf(newRecordList.get(i).DigitalMaximum)));			
								addrawdata.add(new BasicNameValuePair("Prefiltering",  String.valueOf(newRecordList.get(i).Prefiltering)));
								addrawdata.add(new BasicNameValuePair("TotalSize",  String.valueOf(newRecordList.get(i).TotalSize)));
								addrawdata.add(new BasicNameValuePair("UserMode",  String.valueOf(newRecordList.get(i).UserMode)));
								
								addrawdata.add(new BasicNameValuePair("RSensitivity",  String.valueOf(newRecordList.get(i).RSensitivity)));
								addrawdata.add(new BasicNameValuePair("WSensitivity",  String.valueOf(newRecordList.get(i).WSensitivity)));
								
								addrawdata.add(new BasicNameValuePair("HeartRate",  String.valueOf(newRecordList.get(i).HeartRate)));
								addrawdata.add(new BasicNameValuePair("Tachycardia",  String.valueOf(newRecordList.get(i).Tachycardia)));
								addrawdata.add(new BasicNameValuePair("Bradycardia",  String.valueOf(newRecordList.get(i).Bradycardia)));
								addrawdata.add(new BasicNameValuePair("Pause",  String.valueOf(newRecordList.get(i).Pause)));
								addrawdata.add(new BasicNameValuePair("PauseValue",  String.valueOf(newRecordList.get(i).PauseValue)));
								addrawdata.add(new BasicNameValuePair("Rhythm",  String.valueOf(newRecordList.get(i).Rhythm)));
								addrawdata.add(new BasicNameValuePair("Waveform",  String.valueOf(newRecordList.get(i).Waveform)));
								addrawdata.add(new BasicNameValuePair("WaveformStable",  String.valueOf(newRecordList.get(i).WaveformStable)));	
								addrawdata.add(new BasicNameValuePair("EntryPosition",  String.valueOf(newRecordList.get(i).EntryPosition)));
								addrawdata.add(new BasicNameValuePair("TachycardiaValue",  String.valueOf(newRecordList.get(i).TachycardiaValue)));
								
								addrawdata.add(new BasicNameValuePair("BradycardiaValue",  String.valueOf(newRecordList.get(i).BradycardiaValue)));
								addrawdata.add(new BasicNameValuePair("MID",  String.format("%x",newRecordList.get(i).MID)));
								addrawdata.add(new BasicNameValuePair("BPMNoiseFlag",  String.valueOf(newRecordList.get(i).BPMNoiseFlag)));
								addrawdata.add(new BasicNameValuePair("BPHeartRate",  String.valueOf(newRecordList.get(i).BPHeartRate)));								
								addrawdata.add(new BasicNameValuePair("HighBloodPressure",  String.valueOf(newRecordList.get(i).HighBloodPressure)));
								addrawdata.add(new BasicNameValuePair("LowBloodPressure",  String.valueOf(newRecordList.get(i).LowBloodPressure)));
								addrawdata.add(new BasicNameValuePair("WHOIndicate",  String.valueOf(newRecordList.get(i).WHOIndicate)));
								addrawdata.add(new BasicNameValuePair("DCValue",  String.valueOf(newRecordList.get(i).DCValue)));
								addrawdata.add(new BasicNameValuePair("AnalysisType",  String.valueOf(newRecordList.get(i).AnalysisType)));
								addrawdata.add(new BasicNameValuePair("CheckSum",  String.valueOf(newRecordList.get(i).CheckSum)));
								
								addrawdata.add(new BasicNameValuePair("Filename",  String.valueOf(newRecordList.get(i).sFilename)));					
								addrawdata.add(new BasicNameValuePair("HRData", updateHRData.replaceAll(" ","+")));
								addrawdata.add(new BasicNameValuePair("HRDataTimeStamp",  updateHRDataTimestamp.replaceAll(" ","+")));
								addrawdata.add(new BasicNameValuePair("ECG",  updateECG.replaceAll(" ","+")));
								addrawdata.add(new BasicNameValuePair("RawData", updateRawData.replaceAll(" ","+")));
								addrawdata.add(new BasicNameValuePair("Note",  newRecordList.get(i).sNote));
//								updateECG=newRecordList.get(i).ECG;
//								updateRawData=newRecordList.get(i).RawData;
//								updateHRData=newRecordList.get(i).HRData;
//								updateHRDataTimestamp=newRecordList.get(i).HRDataTimeStamp;
								Log.d("sandy", "HRData="+ newRecordList.get(i).HRData);
							//	
								new AsyncTaskUtilsPost(HomeView.this) {
									@Override
									public void refreshUI(String result) {
										if (result != null && result.equals("success")) { // 上傳成功回傳true
											databaseHelper.updateflag(time, 1);
										}
										else
										{
											Log.e("sandy", "updateRawdata fail");
											serverflag=0;
										}
									}
									
								}.execute(addrawdata);
								
							//	
								
							//}// no filter
						}//ECG
						//////serverflag=1;
						
				
					}
					else
					{
						Log.v("sandy", "don't need Update");
						serverflag=1;
					}
				}//for
				loadData();
			}
			//update server end
			else
			{
				serverflag=1;
			}
			//serverflag=1;
			
			//download record data
		if(serverflag==1&&downloadflag==0)
			{
				downloadflag=1;
				Log.d("sandy", "user.getPassword()="+user.getPassword());
				String url = "http://" + ConstantS.SERVER_ADDRESS + "/getECGData.php?UserEmail=" + user.getEmail().replaceAll(" ", "+") + "&UserPassword=" + user.getPassword();
				// download
				new DownloadAsyncTask().execute(url);
				

			}
			//
			
			
			if (home_pressed == "wait") {
				home_pressed = "enable";
			}
			
			if (!menuOut && !bECGMode) {

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (!menuOut) {
							//tv_mea_datetime.setText(dateTimeInstance.format(Calendar.getInstance().getTime()));
							mea_datetime1.setText(dateTimeInstance.format(Calendar.getInstance().getTime()));
							if(ble==false)
							{
									mea_context_ble1.setImageResource(R.drawable.bluetooth_icon_2);
								    ble=true;
								   // Log.d("sandy", "ble");
								    }
								else
								{
									mea_context_ble1.setImageResource(R.drawable.bluetooth_icon_1);
									ble=false;
									//Log.d("sandy", "ble1");
								}
							
							
							
							// tvaWeekday[1].setText("sec_dis");
							// tvaDay[1].setText("" + sec_disconnect);

							// tvaWeekday[2].setText("ble_sta");
							// tvaDay[2].setText("" + ble_status);

							// tvaWeekday[3].setText("null_serv");
							// tvaDay[3].setText("" + null_serv);

							// tvaWeekday[4].setText("e_serv");
							// tvaDay[4].setText("" + e_service);

							// error133.setText("e133:" + error133i);
							// error8.setText("e8:" + error8i);
							// error19.setText("e19:" + error19i);
							// erroro.setText("eo:" + erroros);

							// tvaWeekday[5].setText("loss");
							// tvaDay[5].setText("" + Debug_lose_package);

						}

					}
				});
			}
			sec++;
			sec_disconnect++;
			sec_receive++;

			if (ble_status == -1) {
				// ??��?��le==-1 ���?���???����?�隤�??? homeview��迨��??�蔭
				// ��??��??���??���?��??��?���?��??��?����?��?����?���???
				mBluetoothAdapter.enable();
				ble_status = 4;
				sec_disconnect = 0;
			}
			//ble_connet
			if (ble_status == 2) {

				if (!menuOut && !bECGMode && mea_ble_status_v.getVisibility() == View.VISIBLE) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							before.setVisibility(View.INVISIBLE);
							mea_context2.setVisibility(View.VISIBLE);
							iv_ECG1.setImageResource(R.drawable.dotted_line);//sandy
							//mea_context2.invalidate();
							mea_ble_status_v.setVisibility(View.INVISIBLE);
							
							//time Sync
							byte[] senddata = new byte[20];
							byte yyyy, MM, dd, hh, mm, ss;

							Date date = new Date();
							yyyy = (byte) (date.getYear() - 100);
							MM = (byte) (date.getMonth() + 1);
							dd = (byte) (date.getDate());
							hh = (byte) (date.getHours());
							mm = (byte) (date.getMinutes());
							ss = (byte) (date.getSeconds());
//Log.d("sandy","yyyy="+yyyy);
							for (int i = 0; i < 20; i++)
								data[i] = 0;

							senddata[1] = BT_SETUP;
							senddata[2] = BT_SETUP_700X;
							senddata[3] = yyyy;
							senddata[4] = MM;
							senddata[5] = dd;
							senddata[6] = hh;
							senddata[7] = mm;
							senddata[8] = ss;
							if (characteristic2 != null) {

								try {
									mBluetoothLeService.writeCharacteristic(characteristic2, data);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									Log.e("alex", "writeCharacteristic error");
								}

							}
						//Sync_END
						
						
						}
					});
				}

			} else {
				if (!menuOut && !bECGMode && mea_ble_status_v.getVisibility() == View.INVISIBLE) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mea_ble_status_v.setVisibility(View.VISIBLE);
							
							mea_context2.setVisibility(View.INVISIBLE);
							before.setVisibility(View.VISIBLE);
							home_bg.setVisibility(View.INVISIBLE);
						}
					});
				}

			}

			if (!mBluetoothAdapter.isEnabled() && sec_disconnect > 2) {
				// ??��?���??���??��?����?���??? �歇蝬?����?��??��?��府���?��??��?����?�??��
				// ��??��??���??���?��??��?���?��??��?����?��?����?���???
				mBluetoothAdapter.enable();
				ble_status = 4;
				sec_disconnect = 0;
			}

			if (sec_disconnect == 61) {
				// ??��?���?��??����?��??60??��
				// ��??��?����?���??? ��?����?��??��?�����?���??���???

				final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
				mBluetoothAdapter = bluetoothManager.getAdapter();
				try {
					mBluetoothLeService.disconnect();
					mBluetoothLeService.close();
				} catch (Exception  e) {
					// TODO Auto-generated catch block
					Log.e("alex", "close/disconnect error");
				}

				ble_status = 4;
				sec_disconnect = 4;
				// mBluetoothAdapter.disable();
				// ble_status = 3;
				// sec_disconnect = 0;

			}

			if (ble_status == 5) {
				// ??��?���?��?��?��?��??�隤?��?��
				// ��?��??����?��??���??��?����?���??? ��??�蔭60??��?��?��???
				if (Debug_error_code == 8) {
					// ??��?��??��??��?��?�蝺�???
					// ??��?�岫 ��?�disconnect ���?��?��?��賊�??���???
					ble_status = 4;
					sec_disconnect = 4;

					try {
						mBluetoothLeService.disconnect();
						mBluetoothLeService.close();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Log.e("alex", "close/disconnect error");
					}

					Debug_error_code = 0;
					if (!menuOut && !bECGMode) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Log.d("sandy","459_error==="+error133i);
								error133.setText("e133:" + error133i);
								error8.setText("e8:" + error8i);
								error19.setText("e19:" + error19i);
								erroro.setText("eo:" + erroros);

							}
						});
					}
				} else {

					if (!menuOut && !bECGMode) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {

								error133.setText("e133:" + error133i);
								error8.setText("e8:" + error8i);
								error19.setText("e19:" + error19i);
								erroro.setText("eo:" + erroros);

							}
						});
					}
					ble_status = 4;
					sec_disconnect = 4;
					try {
						mBluetoothLeService.disconnect();
						mBluetoothLeService.close();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Log.e("alex", "close/disconnect error");
					}

				}

			}
			//sec_disconnect == 3  
			if (ble_status == 4 && sec_disconnect == 3) {
				// ??��?���?�蝺�??��?��?��?��?���� ��??�身??��?��?��?�����???

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (bECGMode)
						{
							iv_ECG1.invalidate(new Rect(0, 0, iECGWidth1, iECGHeight1));
							iv_ECG.invalidate(new Rect(0, 0, iECGWidth, iECGHeight));
							Log.e(TAG, "iv_ECG invalidate (ble_status == 4 && sec_disconnect == 3)");
						}
						Log.d("sandy", "507===="+bECGMode);
						startMeasure();
						switchNewMeasure();

					}
				});

			}

			if (ble_status == 4 && sec_disconnect == 5) {
				// ��??���?����?���??? ??���?��?��?��5??��
				// ���?�����?��??����蝺�???, initialize service

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (bECGMode)
						{
							iv_ECG1.invalidate(new Rect(0, 0, iECGWidth1, iECGHeight1));
							iv_ECG.invalidate(new Rect(0, 0, iECGWidth, iECGHeight));
							Log.e(TAG, "iv_ECG invalidate (ble_status == 4 && sec_disconnect == 5)");
						}
						Log.d("sandy", "527===="+bECGMode);
						//menuOut = false;//view?��??�error??�右移�??
						startMeasure();
						switchNewMeasure();

					}
				});

				if (mBluetoothLeService != null) {
					try {
						mBluetoothLeService.disconnect();
						mBluetoothLeService.close();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Log.e("alex", "close/disconnect error");
					}
					Log.d("alex", "closed");
				}
				if (mBluetoothLeService == null) {
					gattServiceIntent = new Intent(context, BluetoothLeService.class);
					bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
				}
			}
			if (ble_status == 4 && sec_disconnect == 7) {

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (bECGMode)
						{
							iv_ECG1.invalidate(new Rect(0, 0, iECGWidth1, iECGHeight1));
							iv_ECG.invalidate(new Rect(0, 0, iECGWidth, iECGHeight));
							Log.e(TAG, "iv_ECG invalidate (ble_status == 4 && sec_disconnect == 7)");
						}
						Log.d("sandy", "560===="+bECGMode);
						
						//menuOut = false;//view?��??�error??�右移�??
					    startMeasure();
						switchNewMeasure();

					}
				});

				// ��??���?����?���??? ??���?��?��?��5??��
				// ��岫����??
				ble_status = 0;
				if (mDeviceAddress != "00:00:00:00:00:00" && mBluetoothLeService != null) {
					if (!mBluetoothLeService.initialize()) {
						ble_status = 5;
						sec_disconnect = 59;
						// finish();
					}
					try {
						Log.e("tina", "mBluetoothLeService.connect");
						mBluetoothLeService.connect(mDeviceAddress);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Log.e("alex", "connect error");
					}
				}
			}

			// if (ble_status == 2 && sec_disconnect == 313) {
			// data[1] = BT_CONFIG_INFO;
			// data[2] = BT_CONFIG_INFO_DEVICE;
			// if (characteristic2 != null)
			// mBluetoothLeService.writeCharacteristic(characteristic2, data);
			//
			// }

			if (ble_status == 6) {
				// ??��?���??��?�����?��??���??�挾

				// ??�����5??�� �?���?�隤?��?�����??? ��??�蔭��??���???
				discover_service_wait++;
				if (discover_service_wait > 5) {
					// Toast.makeText(context, "service error",
					// Toast.LENGTH_SHORT).show();
					Log.e("alex", "service error");
					discover_service_wait = 0;
					ble_status = 5;
					sec_disconnect = 59;
					e_service++;
				}
			}

			if (ble_status == 2 && sendcmdflag && sec_disconnect == 312) {
				Log.d(TAG, "Measure...");
				// sendcmdflag = false;
				
				data[1] = BT_STANDBY;
				if (characteristic2 != null) {

					try {
						mBluetoothLeService.writeCharacteristic(characteristic2, data);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Log.e("alex", "writeCharacteristic error");
					}

				}
				
				
			}

		}
	};
//download server data
	
	public class DownloadAsyncTask extends AsyncTask<String, Void, String> {
		//private ProgressDialog mdialog;

		@Override
		protected void onPreExecute() {
			//Log.d("vion", "Dialog start!");
			//mdialog = new ProgressDialog(AccountSignInView.this);
			// mdialog.setTitle("請稍候");
			//mdialog.setMessage("Loading...");
			//mdialog.setCancelable(false);
			//mdialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {

			String result;
			try {
				Log.d("sandy", "doInBackground start!");
				String jsonString = HttpUtils.getData(HomeView.this, params[0]);

				if (jsonString != null) {

					if (!jsonString.equals("TIME_OUT")) {
						//Log.d("sandy", jsonString);
						user = authorityService.downloadreocrd(jsonString);
						Log.d("sandy", "user="+user);
						if (user == null) {//抓不到資料
	
						//	mainService.updateScore(user.getUserPlayRecords(), false);
						//	mainService.updateFriendList(user.getUserFriends());

							
							Log.e("sandy", "download error : user = null");
							//return null;
							result = "Failed";
							return result;
//							Log.d("vion", "saveSetting done!");
//							result = "OK";
//							return result;
						
							
						} else {
							
							databaseHelper.addRecords(user.userECGRecords);
							databaseHelper.addRecords(user.userBPRecords);
							//mainService.updateScore(user.getUserPlayRecords(), false);
							//mainService.updateFriendList(user.getUserFriends());

							Log.d("sandy", "saveSetting done!");
							result = "OK";
							return result;
						}

					} else {
						result = "TIME_OUT";
						return result;
					}
				} else {
					Log.e("sandy", "download records error : jsonString = null");
					return null;
				}

			} catch (Exception e) {
				Log.e("sandy", "download records error="+e.getMessage());
				return null;
			}
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {
				Log.d("sandy", "result="+result);
				if (result.equals("TIME_OUT")) {
					Log.e("sandy", "Download_TIME_OUT)");
					downloadflag=0;
				//	mdialog.dismiss();
				//	Toast.makeText(getApplicationContext(), getString(R.string.internet_slow), Toast.LENGTH_SHORT).show();
				//	signinFlag = true;
				} 
				else if(result.equals("Failed"))//sandy_0512
				{
					Log.e("sandy", "Server No data");
					//mdialog.dismiss();
					//Toast.makeText(AccountSignInView.this, "email or password error", Toast.LENGTH_LONG).show();
					//Toast.makeText(getApplicationContext(), getString(R.string.internet_slow), Toast.LENGTH_SHORT).show();
					//signinFlag = true;
					
					//Intent intent3 = new Intent(AccountSignInView.this, AboutView.class);//前往註冊
					//startActivityForResult(intent3, 3);
				}
				else {
					Log.e("sandy", "Download Success");
					//downloadflag=1;
				//	mdialog.dismiss();
				//	Toast.makeText(AccountSignInView.this, "Login Success", Toast.LENGTH_LONG).show();
				//	Intent intent3 = new Intent(AccountSignInView.this, HomeView.class);
				//	startActivityForResult(intent3, 3);
				}
			} else {
				Log.e("sandy", "Download Error");
				downloadflag=0;
				//serverflag=0;
				
//				mdialog.dismiss();
//				// Toast.makeText(AccountSignInView.this,"Login Failed!!",
//				// Toast.LENGTH_LONG).show();
//				Toast.makeText(getApplicationContext(), "Login Failed:" + getString(R.string.unable_connect), Toast.LENGTH_SHORT).show();
//				signinFlag = true;
				//mdialog.dismiss();
				
			}

		}

		protected void onCancelled() {
			//signinFlag = true;
			//mdialog.dismiss();
			super.onCancelled();
		}

	}
	//
	private final ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			try {
				mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
				if (!mBluetoothLeService.initialize()) {
					Toast.makeText(HomeView.this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
					ble_status = 5;
					sec_disconnect = 59;
					// finish();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("alex", "mBluetoothLeService initialize or getservice error");
				ble_status = 5;
				sec_disconnect = 59;
			}

			SharedPreferences settings = getSharedPreferences(ScanView.BLE_SETTING, 0);
			mDeviceAddress = settings.getString(ScanView.BLE_MAC, "00:00:00:00:00:00");
			// mBluetoothLeService.connect(mDeviceAddress);
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			try {
				mBluetoothLeService.close();
				mBluetoothLeService = null;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("alex", "close error");
				ble_status = 4;
				sec_disconnect = 4;
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			Log.e("alex", "Bundle is null");
		} else {
			Log.e("alex", "Bundle is NOT  null");
		}


		getWindowManager().getDefaultDisplay().getMetrics(dm);
		DeviceConstant.screenWidth = dm.widthPixels;
		DeviceConstant.screenHeight = dm.heightPixels;
		DeviceConstant.screenDPI = dm.densityDpi;

		Log.d("alex", "HomeView ONCREATE");
		Log.d("alex", "dateFormat=" + dateFormat.format(Calendar.getInstance().getTime()).toString());
		Log.d("alex", "dateTimeInstance=" + dateTimeInstance.format(Calendar.getInstance().getTime()));
		sendcmdflag = true;
		context = this;
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.home_activity);
//double_click_ecg
//		gd = new GestureDetector(this, new OnDoubleClick());
//		gestureListener = new View.OnTouchListener() {
//			public boolean onTouch(View v, MotionEvent event) {
//				if (gd.onTouchEvent(event)) {
//					return false;
//				}
//
//				return true;
//			}
//		};
		ecg_changer = (ImageView) findViewById(R.id.button_ecg);
//		ecg_changer.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				chkECG();
//			}
//		});
		ecg_changer.setVisibility(View.INVISIBLE);
		setResult(3);
		init();
		getStatusBarHeight();
		iv_ECG.setOnTouchListener(gestureListener);
		iv_ECG1.setOnTouchListener(gestureListener);
		home_bg.setOnTouchListener(gestureListener);
		//update server
		loadData();
		mainService = new MainService(this.getApplicationContext());
		user = mainService.loadUserDataItem();//0427
		password=user.getPassword();
		//
		timer.cancel();
		timer = new Timer(true);
		timer.schedule(new timerTask(), 1000, 1000);
		
		
		
		
		
		
		
		
		
		// Use this check to determine whether BLE is supported on the device.
		// Then you can
		// selectively disable BLE-related features.
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
			finish();
		}

		// Initializes a Bluetooth adapter. For API level 18 and above, get a
		// reference to
		// BluetoothAdapter through BluetoothManager.
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		// mBluetoothAdapter.disable();
		mBluetoothAdapter.enable();
		// Checks if Bluetooth is supported on the device.
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		// scanLeDevice(true);
		// Handler mainThread = new Handler(context.getMainLooper());
		// mainThread.postDelayed(new Runnable() {
		// @Override
		// public void run() {
		// gattServiceIntent = new Intent(context, BluetoothLeService.class);
		// bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
		// ble_status = 0;
		// }
		// }, 5000);
		// gattServiceIntent = new Intent(this, BluetoothLeService.class);
		// bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

	}
	public void loadData() {
		newRecordList = new ArrayList<RecordList>();
		newRecordList = databaseHelper.getRecords();
		Log.d("sandy", "1224_newRecordList="+newRecordList.size());
	}
	private void getStatusBarHeight() {
		int contentTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();

		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 38;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = this.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (statusBarHeight != 0) {
			int xdir = (int) (statusBarHeight + (DeviceConstant.screenHeight * 0.078f));
			DeviceConstant.statusBarHeight = statusBarHeight;
			DeviceConstant.xdir = xdir;

		}
	}
	
	private void init() {
		//sandy0914
				getWindowManager().getDefaultDisplay().getMetrics(dm);
				DeviceConstant.screenWidth = dm.widthPixels;
				DeviceConstant.screenHeight = dm.heightPixels;
				DeviceConstant.screenDPI = dm.densityDpi;
	 //sandy
				before = (LinearLayout) findViewById(R.id.before);
				mea_context2= (LinearLayout) findViewById(R.id.mea_context2);
				records_hr1= (TextView) findViewById(R.id.records_hr1);
				mea_datetime1= (TextView) findViewById(R.id.mea_datetime1);
				mea_context_ble1= (ImageView) findViewById(R.id.mea_context_ble1);
				mea_context2_img= (ImageView) findViewById(R.id.mea_context2_img);
				mea_context2_text= (TextView) findViewById(R.id.mea_context2_text);
				iv_ECG1 = (ECGView) findViewById(R.id.iv_ECG1);
				mea_context2_heart=(LinearLayout) findViewById(R.id.mea_context2_heart);
				mea_context2_bp=(LinearLayout) findViewById(R.id.mea_context2_bp);
				
		error133 = (TextView) findViewById(R.id.error133);
		error8 = (TextView) findViewById(R.id.error8);
		error19 = (TextView) findViewById(R.id.error19);
		erroro = (TextView) findViewById(R.id.erroro);
		error133.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.02f));
		error8.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.02f));
		error19.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.02f));
		erroro.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.02f));

		mea_ble_view = (LinearLayout) findViewById(R.id.mea_ble_view);
		ll_mea_result = (LinearLayout) findViewById(R.id.ll_mea_result);
		ll_after_mea = (LinearLayout) findViewById(R.id.ll_after_mea);
		ll_before_mea = (LinearLayout) findViewById(R.id.ll_before_mea);
		mea_user = (ImageView) findViewById(R.id.mea_user);
		mea_share = (ImageView) findViewById(R.id.mea_share);
		mea_share.setVisibility(View.INVISIBLE);
		records_pulse = (TextView) findViewById(R.id.records_pulse);
		records_sys = (TextView) findViewById(R.id.records_sys);
		records_dia = (TextView) findViewById(R.id.records_dia);
		records_pulse_txt = (TextView) findViewById(R.id.records_pulse_txt);

		hr = (TextView) findViewById(R.id.records_hr);
		mmHg = (TextView) findViewById(R.id.records_bp);

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

		tv_mea_datetime = (TextView) findViewById(R.id.mea_datetime);
		TextView tv_home_title = (TextView) findViewById(R.id.tv_home_title);
		iv_home_menu = (ImageView) findViewById(R.id.iv_home_menu);
		ll_main_bg = (LinearLayout) findViewById(R.id.ll_main_bg);
		ll_menu = (LinearLayout) findViewById(R.id.ll_menu);
		iv_ECG = (ECGImageView) findViewById(R.id.iv_ECG);
		hrTrendGraph = (HrTrendView) findViewById(R.id.hrTrendGraph);
		hrTrendGraph.invalidate();
		home_bg = (LinearLayout) findViewById(R.id.content_out);

		mea_ble_status_v = (LinearLayout) findViewById(R.id.mea_ble_status_v);
		mea_context = (LinearLayout) findViewById(R.id.mea_context);
		mea_ble_state = (TextView) findViewById(R.id.mea_ble_state);
		records_bar1 = (LinearLayout) findViewById(R.id.records_bar1);
		records_bar2 = (LinearLayout) findViewById(R.id.records_bar2);

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

		// mea_ble_state.setTextSize(TypedValue.COMPLEX_UNIT_PX,
		// mea_ble_state.getTextSize() + 20);

		records_pulse.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.03f));
		records_sys.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.03f));
		records_dia.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.03f));

		tv_home_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.03f));
		hr.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.065f));
		mmHg.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.065f));
		records_pulse.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.065f));
		records_sys.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.065f));
		records_dia.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.065f));
		Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/FuturaCondensed.ttf");
		hr.setTypeface(tf);
		mmHg.setTypeface(tf);
		records_pulse.setTypeface(tf);
		records_sys.setTypeface(tf);
		records_dia.setTypeface(tf);
		//sandy
		records_hr1.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.125f));
		records_hr1.setTypeface(tf);
		
		
		menu_records = (LinearLayout) findViewById(R.id.menu_records);

		ViewTreeObserver vto2 = hrTrendGraph.getViewTreeObserver();
		vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				hrTrendGraph.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				RecordDetail.iHrHeight = hrTrendGraph.getHeight();
				RecordDetail.iHrWidth = hrTrendGraph.getWidth();

			}
		});

		ViewTreeObserver vto1 = iv_ECG.getViewTreeObserver();
		vto1.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				iv_ECG.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				iECGWidth = iv_ECG.getWidth();
				iECGHeight = iv_ECG.getHeight();
				
			}
		});
		ViewTreeObserver vto3 = iv_ECG1.getViewTreeObserver();
		vto3.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				Log.d("sandy", "929_onGlobalLayout()");
				iv_ECG1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				//iv_ECG1.setImageResource(R.drawable.dotted_line);
				iECGWidth1 = iv_ECG1.getWidth();
				iECGHeight1 = iv_ECG1.getHeight();
				
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		sendcmdflag = true;
		bActivated = true;
		bRunning = true;
		home_pressed = "disable";
		
		
		RecordDetail.iAnalysisType = RecordsView.TYPE_ECG;//20160919
		date = new Date();
		Log.d("sandy", "onresume");
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		

		if (ble_status == 2 && sec_disconnect > 314) {
			if (characteristic2 != null && mBluetoothLeService != null) {
				data[1] = BT_STANDBY;
				try {
					mBluetoothLeService.writeCharacteristic(characteristic2, data);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.e("alex", "writeCharacteristic error");
					ble_status = 4;
					sec_disconnect = 4;
				}
			}
		}
		
		// ��??��??�ac?��??�蔭??����?���蝺���?����??����?���???
		SharedPreferences settings = getSharedPreferences(ScanView.BLE_SETTING, 0);
		mDeviceAddress = settings.getString(ScanView.BLE_MAC, "00:00:00:00:00:00");

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, -2);

		// for (int i = 1; i < tvaDay.length - 1; i++) {
		// Date tdt = calendar.getTime();
		// dateFormat.applyPattern("dd");
		// today = dateFormat.format(tdt).toString();
		// tvaDay[i].setText(today);
		// dateFormat.applyPattern("EE");
		// today = dateFormat.format(tdt).toString();
		// tvaWeekday[i].setText(today);
		// calendar.add(Calendar.DAY_OF_MONTH, 1);
		// }
		Log.e(TAG, "onResume handler.postDelayed");
		handler.postDelayed(updateTimer, 0);

		if (menuOut == false) {

			ll_menu.setVisibility(View.INVISIBLE);
		} else {
			Log.d("sandy", "996..menu_true");
			Log.d("alex", "menuOut  VISIBLE");
			ll_menu.setVisibility(View.VISIBLE);

		}
		ll_main_bg.invalidate();
		
        records_hr1.setText("- -");//sandy
		Log.e(TAG, "startMeasure onResume");
		Log.d("sandy", "1000");
		
		startMeasure();

		Timer single_timer = new Timer();
		single_timer.schedule(new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (bRunning) {
							try {
								drawECGBkgd();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								Log.e("alex", "	drawECGBkgd() error");
							}

						}
					}
				});
			}
		}, 500);
		//RecordDetail.iHrCnt = 0;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("alex", "ondestroy");
		timer.cancel();
		unregisterReceiver(mGattUpdateReceiver);
		ble_status = 4;
		sec_disconnect = 0;
		if (mBluetoothLeService != null) {

			try {
				mBluetoothLeService.disconnect();
				mBluetoothLeService.close();
				mBluetoothLeService = null;
				mBluetoothAdapter.cancelDiscovery();
				unbindService(mServiceConnection);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.d("alex", "not binded or mBluetoothLeService error ");
			}
		}

		Log.e(TAG, "onDestroy handler.removeCallbacks");
		handler.removeCallbacks(updateTimer);
		if (downloadPhotoTaskUtils != null) {
			downloadPhotoTaskUtils.cancel(true);
		}
		System.gc();
		finish();

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Log.d("alex", "on STOP");
		super.onStop();
		bActivated = false;
		bRunning = false;

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d("alex", "on Pause");
		bRunning = false;
		home_pressed = "wait";

		if (menuOut == false) {
			ll_menu.setVisibility(View.INVISIBLE);
			// ?��?��?��?�Ｘ��enuout??��?��?���誨銵?��?����?���???��?����?�ome

		} else {

			ll_menu.setVisibility(View.VISIBLE);
			layoutApp(menuOut);
		}
		Log.e(TAG, "onPause handler.removeCallbacks");
		handler.removeCallbacks(updateTimer);

	}

	@Override
	public void onBackPressed() {

		if (menuOut == true) {

			iv_ECG.setVisibility(View.INVISIBLE);
			SlidingMenu(ll_main_bg.findViewById(R.id.iv_home_menu));
			return;
		}
		if (iv_ECG.getVisibility() == View.VISIBLE) {
			chkECG();
			return;
		}
		setResult(RESULT_OK);
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
	}

	public void SlidingMenu(View v) {

		date = new Date();

		w = ll_main_bg.getMeasuredWidth();
		h = ll_main_bg.getMeasuredHeight();
		left = (int) (ll_main_bg.getMeasuredWidth() * 0.85);
//visible
		if (menuOut == false) {
			Log.d("sandy", "menuOut_false"+left+",w="+w);
			//menuOut =true;
			animMenuTranslate = new TranslateAnimation(0, left, 0, 0);
			ll_menu.setVisibility(View.VISIBLE);
			animParams.init(left, 0, left + w, h);

		}
//invisible
		else {
			Log.d("sandy", "menuOut_true"+left+",w="+w);
			//menuOut =false;
			animMenuTranslate = new TranslateAnimation(0, -left, 0, 0);
			//ll_menu.setVisibility(View.INVISIBLE);
			animParams.init(0, 0, w, h);
			//iv_ECG.setVisibility(View.INVISIBLE);

		}

		animMenuTranslate.setDuration(animDuration);

		animMenuTranslate.setAnimationListener(me);

		animMenuTranslate.setFillBefore(true);
		// Only use fillEnabled and fillAfter if we don't call layout ourselves.
		// We need to do the layout ourselves and not use fillEnabled and
		// fillAfter because when the anim is finished
		// although the View appears to have moved, it is actually just a
		// drawing effect and the View hasn't moved.
		// Therefore clicking on the screen where the button appears does not
		// work, but clicking where the View *was* does
		// work.
		// anim.setFillEnabled(true);
		// anim.setFillAfter(true);

		ll_main_bg.startAnimation(animMenuTranslate);

	}

	void layoutApp(boolean menuOut) {

		ll_main_bg.layout(animParams.left, animParams.top, animParams.right, animParams.bottom);

		// Now that we've set the app.layout property we can clear the
		// animation, flicker avoided :)
		ll_main_bg.clearAnimation();

	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationEnd(Animation animation) {

		if (animation.equals(animMenuTranslate)) {

			System.out.println("animMenuTranslate on onAnimationEnd");

			ViewUtils.printView("menu", ll_menu);
			ViewUtils.printView("app", ll_main_bg);//1108
			menuOut = !menuOut;

			layoutApp(menuOut);

		} else {
			layoutApp(menuOut);
		}

		if (settingResult) {
			ll_main_bg.setVisibility(View.INVISIBLE);
			if (menuOut == false) {

				settingResult = false;
				settingResultEnd = true;
				SlidingMenu(ll_main_bg.findViewById(R.id.iv_home_menu));

			}
		}

		if (settingResultEnd) {
			ll_main_bg.setVisibility(View.VISIBLE);
			settingResultEnd = false;

		}

	}

	static class AnimParams {
		int left, right, top, bottom;

		void init(int left, int top, int right, int bottom) {
			this.left = left;
			this.top = top;
			this.right = right;
			this.bottom = bottom;
		}
	}

	private Runnable updateTimer = new Runnable() {
		int iCnt = 0;

		public void run() {
			// Log.d("updateTimer", "running sec=" + sec_disconnect);
			if (bECGMode == true) {
				if (ecgCount - iDrawCnt > 10) {

					iv_ECG.invalidate(new Rect(0, 0, iECGWidth, iECGHeight));
					Log.e(TAG, "iv_ECG invalidate Rect updateTimer");
				}

				handler.postDelayed(this, 20);
			} else {
				if (mBluetoothLeService != null) {
					if (state == STATE_CONNECTED) {
						// mea_ble_status_v.setVisibility(View.INVISIBLE);
					} else if (state == STATE_CONNECTING) {

					} else {
						if (mea_ble_status_v.getVisibility() == View.INVISIBLE) {
							// if (bNewMeasure == true) {
							// bNewMeasure = false;
							// startMeasure();
							// switchNewMeasure();
							// if (menuOut && bRunning) {
							// Log.d("alex", "sett bRunning menouOUT");
							// iv_ECG.setVisibility(View.INVISIBLE);
							// SlidingMenu(ll_main_bg.findViewById(R.id.iv_home_menu));
							//
							// }
							// }
							// mea_ble_status_v.setVisibility(View.VISIBLE);
						}
						if (menuOut == false) {
							// mea_ble_state.setText("Disconnected");
							hasDevice = false;
						}
					}
				}

				handler.postDelayed(this, 500);
			}
		}
	};

	void startMeasure() {
		Log.d("sandy","startmeasure()");
		//iv_ECG1.setImageResource(R.drawable.dotted_line);//sandy
		lLineStartTime = lPreTimestamp = SystemClock.elapsedRealtime();
		iDrawCnt = ecgCount = displayCount = 0;
		iHrCnt = 0;
		//RecordDetail.iHrCnt = 0;
		MDTrendIndexTmp = 0;
		iirlowcut.resetIIRlowcut((short) 256, 1.6f);
		firFilter.resetFIRfilter();
		hrTrendGraph.invalidate();
		bpPulseRate = 0;
	}

	void drawECGBkgd() {
		Log.e("tina", "drawECG");
		tempBitmap = Bitmap.createBitmap(iECGWidth, iECGHeight, Bitmap.Config.RGB_565);

		tempCanvas = new Canvas(tempBitmap);

		myPaint.setColor(Color.rgb(255, 255, 255));
		myPaint.setStyle(Paint.Style.FILL);
		tempCanvas.drawRect(new RectF(0, 0, iECGWidth, iECGHeight), myPaint);

		int ecg_grid1 = getResources().getColor(R.color.ecg_grid1);
		int ecg_grid2 = getResources().getColor(R.color.ecg_grid2);
		int ecg_grid3 = getResources().getColor(R.color.ecg_grid3);
		float iGridHeight = iECGHeight / 5; // always display 5 seconds
		float iCell = iECGHeight / 25;
		float iSmallCell = iECGHeight / 125;
		for (float i = 0; i < iECGHeight; i += iGridHeight) {
			myPaint.setColor(ecg_grid3);
			myPaint.setStrokeWidth(1);
			tempCanvas.drawLine(0, i, iECGWidth, i, myPaint);
			myPaint.setStrokeWidth(1);
			for (float j = i; j < i + iGridHeight - iCell + 1; j += iCell) { // minus
																				// 5
																				// to
																				// skip
																				// fraction
				myPaint.setColor(ecg_grid2);
				myPaint.setStrokeWidth(2);
				tempCanvas.drawLine(0, j, iECGWidth, j, myPaint);
				myPaint.setStrokeWidth(1);
				myPaint.setColor(ecg_grid1);
				for (float k = iSmallCell; k < iCell - iSmallCell + 1; k += iSmallCell) {
					tempCanvas.drawLine(0, j + k, iECGWidth, j + k, myPaint);
				}
			}
		}
		myPaint.setStyle(Paint.Style.STROKE);
		myPaint.setStrokeWidth(1);
		for (int i = iECGWidth; i > 0; i -= iCell) {
			myPaint.setStrokeWidth(2);
			tempCanvas.drawLine(i, 0, i, iECGHeight, myPaint);
			myPaint.setStrokeWidth(1);
			for (int k = 1; k < 5; k++) {
				tempCanvas.drawLine(i - k * iSmallCell, 0, i - k * iSmallCell, iECGHeight, myPaint);
			}
		}

		myPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		myPaint.setStrokeWidth(2);
		myPaint.setColor(Color.BLACK);

		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.heart_s);
		tempCanvas.save();
		tempCanvas.rotate(90, iECGWidth - iCell, iECGHeight - iGridHeight);
		myPaint.setTextSize(38);
		tempCanvas.drawText("HR", iECGWidth - iCell, iECGHeight - iGridHeight + 42, myPaint);
		myPaint.setTextSize(22);
		tempCanvas.drawText("BPM", iECGWidth - iCell, iECGHeight - iGridHeight + 42 + 34, myPaint);
		tempCanvas.drawBitmap(bmp, iECGWidth - iCell, iECGHeight - iGridHeight + 42 - 70, myPaint);
		bmp.recycle();
		bmp = null;
		tempCanvas.restore();

		// Attach the canvas to the ImageView
		iv_ECG.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));

		iv_ECG.bBkgd = false;
	}

	public void menu_goto(View v) {

		if (menuOut) {

			switch (v.getId()) {

			case R.id.menu_measure:
				iv_ECG.setVisibility(View.INVISIBLE);
				if (menuOut) {
					iv_ECG.setVisibility(View.INVISIBLE);
					SlidingMenu(ll_main_bg.findViewById(R.id.iv_home_menu));

				}

				lastview = "menu_measure";
				if (sec_disconnect > 314 && ble_status == 2) {
					data[1] = BT_STANDBY;
					try {
						mBluetoothLeService.writeCharacteristic(characteristic2, data);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Log.d("alex", "writeCharacteristic error ");
					}

				}
				break;

			case R.id.menu_records:
				intent = new Intent();
				//intent.setClass(HomeView.this, RecordsView.class);
				intent.setClass(HomeView.this, RecordUserView.class);
				lastview = "menu_records";
				startActivityForResult(intent, 136);
				overridePendingTransition(R.anim.slide_in_right, R.anim.left_out);

				break;

			case R.id.menu_analysis:
				intent = new Intent();
				intent.setClass(HomeView.this, AnalysisView.class);
				lastview = "menu_analysis";
				startActivityForResult(intent, 136);
				overridePendingTransition(R.anim.slide_in_right, R.anim.left_out);

				break;

			case R.id.menu_download:
				intent = new Intent();
				intent.setClass(HomeView.this, DownloadView.class);
				lastview = "menu_download";
				startActivityForResult(intent, 136);
				overridePendingTransition(R.anim.slide_in_right, R.anim.left_out);

				break;
			case R.id.menu_connectDevice:
				intent = new Intent();
				intent.setClass(this, ScanView.class);
				lastview = "menu_connectDevice";
				startActivityForResult(intent, 134);
				overridePendingTransition(R.anim.slide_in_right, R.anim.left_out);

				break;
			case R.id.menu_settings:
				intent = new Intent();
				intent.setClass(this, SettingsView.class);
				lastview = "menu_settings";
				startActivityForResult(intent, 134);
				overridePendingTransition(R.anim.slide_in_right, R.anim.left_out);

				break;

			case R.id.menu_information:
				intent = new Intent();
				intent.setClass(HomeView.this, InfoStepView.class);
				lastview = "menu_information";
				startActivityForResult(intent, 136);
				overridePendingTransition(R.anim.slide_in_right, R.anim.left_out);
				break;
			case R.id.menu_tutorial_step:
				intent = new Intent();
				intent.setClass(HomeView.this, TutorialStepView.class);
				lastview = "menu_tutorial_step";
				startActivityForResult(intent, 136);
				overridePendingTransition(R.anim.slide_in_right, R.anim.left_out);
				break;
			case R.id.menu_about:
				intent = new Intent();
				intent.setClass(HomeView.this, AboutView.class);
				lastview = "menu_about";
				startActivityForResult(intent, 136);
				overridePendingTransition(R.anim.slide_in_right, R.anim.left_out);


				break;
			default:
				// Toast.makeText(HomeView.this, "null", Toast.LENGTH_SHORT)
				// .show();

				break;

			}

		} else {

		}

	}

	void switchNewMeasure() {
		Log.d("sandy","switchNewMeasure()");
		//iv_ECG1.setImageDrawable(getResources().getDrawable( R.drawable.dotted_line));
        // iv_ECG1.invalidate();
		//iv_ECG1.setImageResource(R.drawable.dotted_line);
		home_bg.setVisibility(View.INVISIBLE);
		//ll_mea_result.setBackgroundResource(R.drawable.mea_bg_1a);
		ll_before_mea.setVisibility(View.VISIBLE);
		ll_after_mea.setVisibility(View.INVISIBLE);
		mea_ble_view.setVisibility(View.VISIBLE);
		ecg_changer.setVisibility(View.INVISIBLE);
		mea_share.setVisibility(View.INVISIBLE);
		//ecg_changer.setVisibility(View.INVISIBLE);
		//RecordDetail.iHrCnt = 0;//105.10.13
		img=0;
		displayHR("- -");
		displaymmHg("- -");
	}

	void switchMeasuredResult(RecordList rec) {
		ObjectAnimator anim = ObjectAnimator.ofFloat(mea_context, "alpha", 0.0f, 1.0f);
		anim.setDuration(1000);
		anim.start();
		Log.e("sandy","result");
		img=0;
		mea_context2.setVisibility(View.INVISIBLE);
		home_bg.setVisibility(View.VISIBLE);
		ll_mea_result.setBackgroundResource(R.drawable.bg_1a);
		ll_before_mea.setVisibility(View.INVISIBLE);
		ll_after_mea.setVisibility(View.VISIBLE);
		// mea_share.setVisibility(View.VISIBLE);
		tv_mea_datetime.setText(dateTimeInstance.format(Calendar.getInstance().getTime()));//20170509

		if (rec.AnalysisType == RecordsView.TYPE_BP) {

			records_pulse_txt.setText(getResources().getString(R.string.pulse));
			// mea_ble_view.setVisibility(View.INVISIBLE);
			if (rec.UserMode == 0) {
				mea_user.setImageResource(R.drawable.user1);
				Log.d("alex", "0");
			} else if (rec.UserMode == 1) {
				mea_user.setImageResource(R.drawable.user2);
				Log.d("alex", "1");
			}

			if (rec.BPMNoiseFlag == 0) {
				rec.BPHeartRate = (rec.BPHeartRate & 0x00FF);
				records_pulse.setText(String.valueOf(rec.BPHeartRate));

				rec.HighBloodPressure = (rec.HighBloodPressure & 0x00FF);
				records_sys.setText(String.valueOf(rec.HighBloodPressure));

				rec.LowBloodPressure = (rec.LowBloodPressure & 0x00FF);
				records_dia.setText(String.valueOf(rec.LowBloodPressure));

				if (rec.HighBloodPressure < 140 && rec.LowBloodPressure < 90)
				{
					records_sys.setTextColor(getResources().getColor(R.color.bp_N));
					records_dia.setTextColor(getResources().getColor(R.color.bp_N));
				}
				else if (rec.HighBloodPressure < 160 && rec.LowBloodPressure < 100)
				{
					records_sys.setTextColor(getResources().getColor(R.color.bp_1));
					records_dia.setTextColor(getResources().getColor(R.color.bp_1));
				}
				else if (rec.HighBloodPressure < 180 && rec.LowBloodPressure < 110)
				{
					records_sys.setTextColor(getResources().getColor(R.color.bp_2));
					records_dia.setTextColor(getResources().getColor(R.color.bp_2));
				}

				if (rec.HighBloodPressure >=180 || rec.LowBloodPressure >=110)
				{
					records_sys.setTextColor(getResources().getColor(R.color.bp_3));
					records_dia.setTextColor(getResources().getColor(R.color.bp_3));
				}
				

			} else {
				records_pulse.setText("- -");
			}
		} else {
			if (rec.UserMode == 0) {
				mea_user.setImageResource(R.drawable.user1);
				Log.d("alex", "0");
			} else if (rec.UserMode == 1) {
				mea_user.setImageResource(R.drawable.user2);
				Log.d("alex", "1");
			}
			//ecg_changer.setVisibility(View.VISIBLE);
			mea_ble_view.setVisibility(View.VISIBLE);
			records_pulse_txt.setText(getResources().getString(R.string.hr));
			records_sys.setText("- -");
			records_dia.setText("- -");
			if (rec.Noise == 0){
				Log.d("sandy","rec.HeartRate"+rec.HeartRate);
				if(rec.HeartRate<0)
					records_pulse.setText(String.valueOf(rec.HeartRate+256));
				else
				records_pulse.setText(String.valueOf(rec.HeartRate));
				}
			else
				records_pulse.setText("EE");
		}
		hr.setText("- -");
		mmHg.setText("- -");
		records_hr1.setText("- -");//sandy
		hrTrendGraph.invalidate();

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		HomeView v = HomeView.this;

		if (resultCode == RESULT_OK) {
			//
			Log.d("sandy", "1575..requestCode="+requestCode);
			if (requestCode == 135 || requestCode == 136) {
				animMenuTranslateOnResume = new TranslateAnimation(0, left, 0, 0);
				animParams.init(left, 0, left + w, h);
				animMenuTranslateOnResume.setDuration(0);
				animMenuTranslateOnResume.setFillEnabled(true);
			    animMenuTranslateOnResume.setFillAfter(true);
				animMenuTranslateOnResume.setAnimationListener(v);
				ll_main_bg.startAnimation(animMenuTranslateOnResume);
			} else if (requestCode == 134) {
				animMenuTranslateOnResume = new TranslateAnimation(0, left, 0, 0);
				animParams.init(left, 0, left + w, h);
				animMenuTranslateOnResume.setDuration(0);
				animMenuTranslateOnResume.setFillEnabled(true);
				animMenuTranslateOnResume.setFillAfter(true);
				animMenuTranslateOnResume.setAnimationListener(v);
				ll_main_bg.startAnimation(animMenuTranslateOnResume);
			}

		}

	}

	public class SendBTCmd extends TimerTask {
		public void run() {
			Log.d("alex", "SendBTCmd running");
			sec++;
			if (mBluetoothLeService != null) {

				state = HomeView.ble_status;

				if (state == STATE_CONNECTED && sendcmdflag && sec_disconnect == 311) {
					Log.d("alex", "Measure...");//測量
					// sendcmdflag = false;
					data[1] = BT_STANDBY;
					if (characteristic2 != null)
						try {
							mBluetoothLeService.writeCharacteristic(characteristic2, data);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							Log.d("alex", "writeCharacteristic error ");
						}
				}
				/*
				 * if (writeFlag == 1 && sec > 1) { if (characteristic2 != null)
				 * mBluetoothLeService.writeCharacteristic(characteristic2,
				 * data); writeFlag = 1; sec = 0; retryCmdCount++; }
				 * 
				 * if (retryCmdCount > 3 && data[1] == BT_STANDBY) { writeFlag =
				 * 0; retryCmdCount = 0; }
				 */
			}

		}
	};

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			// Log.d("alex", "mGattUpdateReceiver Home Get : " + action);
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				Log.d("alex", "Home Get ACTION_GATT_CONNECTED ble_status=" + ble_status);
				sec_disconnect = 200;
				mConnected = true;
				state = STATE_CONNECTED;
				ble_status = 6;
				// invalidateOptionsMenu();

			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				// Log.d(TAG, "BroadcastReceiver onReceive  [" + action + "]");
				mConnected = false;

				error_disconnect++;
				if (error_disconnect > 10) {
					ble_status = 5;
					sec_disconnect = 59;
					error_disconnect = 0;

					return;
				}

				if (ble_status != 5) {
					ble_status = 4;
					sec_disconnect = 59;
					Log.d("alex", "get disconnect in homeview");
				}
				characteristic1 = null;
				characteristic2 = null;
				state = STATE_DISCONNECTED;
				isReceive = true;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						Log.e(TAG, "startMeasure BroadcastReceiver ACTION_GATT_DISCONNECTED");
						Log.e(TAG, "switchNewMeasure BroadcastReceiver ACTION_GATT_DISCONNECTED");
						Log.d("sandy", "1668");
						startMeasure();
						switchNewMeasure();

					}
				});
				// Log.d(TAG, "BroadcastReceiver*************************state="
				// + state);
				// clearUI();
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				sec_disconnect = 309;
				ble_status = 2;
				discover_service_wait = 0;
				Debug_connected = 1;

				sendcmdflag = true;
				// Log.d(TAG, "BroadcastReceiver onReceive  [" + action + "]");
				// Show all the supported services and characteristics on the
				// user interface.
				try {
					displayGattServices(mBluetoothLeService.getSupportedGattServices());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.e("alex", "displayGattServices error");
				}

				mNotifyCharacteristic = characteristic1;
				if (characteristic1 != null) {
					try {
						mBluetoothLeService.setCharacteristicNotification(characteristic1, true);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Log.e("alex", "setCharacteristicNotification error");
					}

				} else {
					Log.d("alex", "characteristic1==null");
					sec_disconnect = 59;
					ble_status = 4;
					null_serv++;
				}

			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

				isNotify = true;
				isReceive = true;

				final int[] data = intent.getIntArrayExtra(BluetoothLeService.EXTRA_DATA);

				format = data[1];
				if (data.length > 2)
					bpCmd = data[2];
				String tempLog = "";
				for (int x = 0; x < data.length; x++) {
					tempLog = tempLog + data[x] + ",";
				}
				 Log.d("sandy", "tempLog="+tempLog);
				 Log.i("Ivy","tempLog="+tempLog);
				// if (bActivated == false)
				// return;

				if (format == BT_MEASURE) {

					if (bNewMeasure == true) {
						bNewMeasure = false;
						Log.e(TAG, "startMeasure BroadcastReceiver BT_MEASURE");
						Log.e(TAG, "switchNewMeasure BroadcastReceiver BT_MEASURE");
						startMeasure();
						switchNewMeasure();
						//sandy
						//Log.d("sandy", "1730");
						mea_context2.setVisibility(View.VISIBLE);
						
						iv_ECG1.setImageDrawable(getResources().getDrawable( R.drawable.dotted_line));
						records_hr1.setText("- -");//sandy
						RecordDetail.iHrCnt = 0; //20160919
						if (menuOut && bRunning) {
							iv_ECG.setVisibility(View.INVISIBLE);
							SlidingMenu(ll_main_bg.findViewById(R.id.iv_home_menu));

						}
					}

					m_seq = data[0];
					if (m_seq - m_tmpseq != 1 && m_tmpseq - m_seq != 255) {
						Log.e(TAG, "**********************m_seq=" + Integer.toString(m_seq) + ",m_tmpseq=" + Integer.toString(m_tmpseq));

						Debug_lose_package++;
						Log.e("tina", "lose_package = "+Debug_lose_package);

					}
					m_tmpseq = m_seq;
					for (int i = 4; i < 20;) {     //  前四碼對應的值
						ChannelNo = data[i++] & 0xff; //
						ChannelMSB = data[i++] & 0xff; 
						ChannelData = (data[i++] & 0xff) * 256;
						ChannelData = ChannelData + (data[i++] & 0xff);
						
						
						
						
						if (ChannelNo == 14 )
						
						
						
						
						
						

						if (ChannelNo == CH_HR) {
							
							//menuOut = false;//view?��??�error??�右移�??
							//Log.d("sandy","ChannelNo == CH_HR");
							iv_ECG1.setImageResource(0);
							//sandy
							//Log.e("sandy","img="+img);
							if(img==0)
							{
								Log.e("sandy","Changeimage");
								mea_context2_text.setText(getResources().getString(R.string.bpm));
								mea_context2_bp.setVisibility(View.INVISIBLE);
								mea_context2_heart.setVisibility(View.VISIBLE);
								img=1;
							}
							
							bpPulseRate = ChannelMSB * 256 + ChannelData;
							displayHR(String.valueOf(bpPulseRate));
							
							//mmHg.setText("- -");
							HRData[iHrCnt++] = (short) (bpPulseRate);
							RecordDetail.HRData[RecordDetail.iHrCnt++] = (short) (bpPulseRate);
						}
						if (ChannelNo == CH_MMHG) {
							//iv_ECG1.invalidate();
							
							//menuOut = false;//view?��??�error??�右移�??
							bpDiastolic = ChannelMSB * 256 + ChannelData;
							displaymmHg(String.valueOf(bpDiastolic));
							hr.setText("- -");
						
							//sandy
							if(img==0)
							{
								Log.d("sandy", "1814_img="+img);
								mea_context2_text.setText(getResources().getString(R.string.mmhg));
							
								mea_context2_bp.setVisibility(View.VISIBLE);
								mea_context2_heart.setVisibility(View.INVISIBLE);
								iv_ECG1.setImageResource(R.drawable.dotted_line);
								img=1;
							}
							//Log.d("sandy","ChannelNo == CH_MMHG");
							
						}
						if (ChannelNo == CH_BEAT_INDEX) {
							//Log.d("sandy","ChannelNo == CH_BEAT_INDEX");
							HRDataTimestamp[iHrCnt] = ChannelMSB * 256 + ChannelData;
							RecordDetail.HRDataTimestamp[RecordDetail.iHrCnt] = ChannelMSB * 256 + ChannelData;
							
							
							hrTrendGraph.invalidate();

						}
						if (ChannelNo == CH_ECG) {
							
							//menuOut = false;//view?��??�error??�右移�??
							//sandy error
							//mea_context2_text.setText(getResources().getString(R.string.bpm));
							//mea_context2_img.setImageResource(R.drawable.heart1);
							iv_ECG1.setImageResource(0);
							//if (ecg_changer.getVisibility() == View.INVISIBLE) {
								//ecg_changer.setVisibility(View.VISIBLE);
							//}
							short iData;
							MDRawData = ChannelMSB * 256 + ChannelData;
							if (ecgCount < rawData.length) {
								rawData[ecgCount] = (short) (MDRawData);
								iData = HomeView.iirlowcut.IIRlowcutFiltering(rawData[ecgCount]);
								filteredData[ecgCount] = HomeView.firFilter.FIRfiltering(iData);
								ecgCount++;
								if (ecgCount > 768) {
									displayData[displayCount] = filteredData[ecgCount - 1];
									displayCount++;

								}
							}

						}
						if (ChannelNo == CH_AUTOSCALE) {
							ecgSize = ChannelMSB * 256 + ChannelData;
							Log.e("tina", "ecgSize = "+ecgSize);

						}

					}

				} else if (format == BT_HEADER) {

					if (bpCmd < 15) {
						for (int i = 0; i < 16; i++)
							FlashBuffer1[bpCmd * 16 + i] = (byte) data[i + 4];
					} else if (bpCmd == 15) {
						for (int i = 0; i < 1; i++)
							FlashBuffer1[bpCmd * 16 + i] = (byte) data[i + 4];
						for(int i=0;i<FlashBuffer1.length;i++)
						{
							FlashBuffer[i]=0xFF&FlashBuffer1[i];
						}
						FileOutputStream out = null;
						DataOutputStream outS;
						try {

							String datetime = String.format("%02d%02d%02d%02d%02d%02d", FlashBuffer[20], FlashBuffer[21], FlashBuffer[22], FlashBuffer[23], FlashBuffer[24], FlashBuffer[25]);

							out = openFileOutput(datetime + ".dat", Context.MODE_PRIVATE);
							outS = new DataOutputStream(new BufferedOutputStream(out));
							outS.write(FlashBuffer1);
							outS.writeInt(iHrCnt);
							for (int i = 0; i < HRData.length; i++) {
								outS.writeInt(HRDataTimestamp[i]);
								outS.writeShort(HRData[i]);
							}
							outS.writeInt(ecgCount);
							for (int i = 0; i < rawData.length; i++) {
								outS.writeShort(rawData[i]);
							}
							outS.close();
							
							RecordList rec = new RecordList();
							//0508
							rec.Seq = FlashBuffer[0];
							rec.Signature = String.format("%c%c%c", FlashBuffer[1], FlashBuffer[2], FlashBuffer[3]);
							rec.FirmwareVersion = FlashBuffer[4] + FlashBuffer[5] * 256;
							rec.HardwareVersion = FlashBuffer[6] + FlashBuffer[7] * 256;
							rec.HeaderSize = FlashBuffer[8] + FlashBuffer[9] * 256;
							rec.VersionTag = String.format("%c%c%c%c%c%c", FlashBuffer[10], FlashBuffer[11], FlashBuffer[12], FlashBuffer[13], FlashBuffer[14], FlashBuffer[15]);
							rec.DeviceID = FlashBuffer[16] + FlashBuffer[17] * 256 + FlashBuffer[18] * 256 * 256 + FlashBuffer[19] * 256 * 256 * 256;
							rec.SamplingRate = FlashBuffer[26] + FlashBuffer[27] * 256;
							rec.GainSetting = FlashBuffer[28];
							rec.Resolution = FlashBuffer[29];
							//高位元有負號，低位元要轉正
							rec.PhysicalMinimum = FlashBuffer[31] + FlashBuffer1[32] * 256;
							rec.PhysicalMaximum = FlashBuffer[33] + FlashBuffer1[34] * 256;
							rec.DigitalMinimum = FlashBuffer[35] + FlashBuffer1[36] * 256;
							rec.DigitalMaximum = FlashBuffer[37] + FlashBuffer1[38] * 256;
							//
							rec.Prefiltering = FlashBuffer[39];
							rec.TotalSize = FlashBuffer[40] + FlashBuffer[41] * 256 + FlashBuffer[42] * 256 * 256 + FlashBuffer[43] * 256 * 256 * 256;
							rec.UserMode = FlashBuffer[44];
							rec.RSensitivity = FlashBuffer[45];
							rec.WSensitivity = FlashBuffer[46];
							rec.Tachycardia = FlashBuffer[48];
							rec.Bradycardia = FlashBuffer[49];
							rec.Pause = FlashBuffer[50];
							rec.PauseValue = FlashBuffer[51];
							rec.WaveformStable = FlashBuffer[54];
							rec.EntryPosition = FlashBuffer[55];
							rec.TachycardiaValue = FlashBuffer[56];
							rec.BradycardiaValue = FlashBuffer[57];
							rec.MID = FlashBuffer[58] + FlashBuffer[59] * 256 + FlashBuffer[60] * 256 * 256 + FlashBuffer[61] * 256 * 256 * 256;
							rec.BPMNoiseFlag = FlashBuffer[62];
							rec.DCValue = FlashBuffer[69] + FlashBuffer[70] * 256;
							rec.AnalysisType = FlashBuffer[71];
							rec.CheckSum = FlashBuffer[255];
							//
							rec.sDatetime = datetime;
							rec.Noise = FlashBuffer[30];
							//rec.UserMode = FlashBuffer[44];
							//rec.BPMNoiseFlag = FlashBuffer[62];
							//rec.AnalysisType = FlashBuffer[71];
							if (rec.AnalysisType == RecordsView.TYPE_BP) {
								rec.BPHeartRate = FlashBuffer[63];
								rec.HighBloodPressure = FlashBuffer[64] + FlashBuffer[65] * 256;

								rec.LowBloodPressure = FlashBuffer[66] + FlashBuffer[67] * 256;
								rec.WHOIndicate = FlashBuffer[68];
							} else {
								//sandy
								rec.Tachycardia = FlashBuffer[48];
								rec.Bradycardia = FlashBuffer[49];
								rec.Pause = FlashBuffer[50];
								//
								rec.HeartRate = FlashBuffer[47];
								rec.Rhythm = FlashBuffer[52];
								rec.Waveform = FlashBuffer[53];
								 //1202
								bpPulseRate=rec.HeartRate;//realtime ecg hr value
							}
							rec.sFilename = datetime + ".dat";
							//Log.e("sandy","error_1912");
							databaseHelper.addRecord(rec);
							Log.e("sandy","error_1914");
							switchMeasuredResult(rec);

						} catch (Exception e) {

						}

						bNewMeasure = true;

						bECGMode = false;
						iv_ECG.setVisibility(View.INVISIBLE);
						hrTrendGraph.invalidate();
						hr.setText("- -");
						records_hr1.setText("- -");//sandy
						mmHg.setText("- -");
						if (menuOut && bRunning) {
							SlidingMenu(ll_main_bg.findViewById(R.id.iv_home_menu));

						}

					} else if (bpCmd >= 96 && bpCmd <= 98) {
						Log.e(TAG, "switchNewMeasure (bpCmd >= 96 && bpCmd <= 98)");
						Log.d("sandy", "1906");
						switchNewMeasure();
						if (menuOut && bRunning) {
							iv_ECG.setVisibility(View.INVISIBLE);
							SlidingMenu(ll_main_bg.findViewById(R.id.iv_home_menu));

						}

					} else if (bpCmd >= 96 && bpCmd <= 99) {
						displayHR("- -");
						displaymmHg("- -");
					}

				} else if (format == BT_STANDBY) {
					sendcmdflag = false;

				}

				else if (format == BT_CONFIG_INFO && bRunning) {
					if (bpCmd == BT_CONFIG_INFO_DEVICE) {
						Log.d(TAG, "BT_CONFIG_INFO_DEVICE");
						byte[] senddata = new byte[20];
						senddata[1] = BT_CONFIG_INFO;
						senddata[2] = BT_CONFIG_INFO_SETTING;
						if (characteristic2 != null) {
							try {
								mBluetoothLeService.writeCharacteristic(characteristic2, senddata);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								Log.d("alex", "writeCharacteristic error ");
							}
						}

					} else if (bpCmd == BT_CONFIG_INFO_SETTING) {
						Log.d("alex", "BT_CONFIG_INFO_SETTING");
						bKey_beep = data[5] > 0 ? true : false;
						bHeartbeat_beep = data[6] > 0 ? true : false;
						// eric
						// benabled12Mod = value[7] > 0 ? true : false;
						byte[] senddata = new byte[20];
						byte yyyy, MM, dd, hh, mm, ss;

						Date date = new Date();
						yyyy = (byte) (date.getYear() - 100);
						MM = (byte) (date.getMonth() + 1);
						dd = (byte) (date.getDate());
						hh = (byte) (date.getHours());
						mm = (byte) (date.getMinutes());
						ss = (byte) (date.getSeconds());

						for (int i = 0; i < 20; i++)
							data[i] = 0;

						senddata[1] = BT_SETUP;
						senddata[2] = BT_SETUP_700X;
						senddata[3] = yyyy;
						senddata[4] = MM;
						senddata[5] = dd;
						senddata[6] = hh;
						senddata[7] = mm;
						senddata[8] = ss;
						senddata[9] = (byte) (bKey_beep == true ? 1 : 0);
						senddata[10] = (byte) (bHeartbeat_beep == true ? 1 : 0);
						// eric
						// data[11] = (byte) (benabled12Mod == true ? 1 : 0);

						if (characteristic2 != null) {
							try {
								mBluetoothLeService.writeCharacteristic(characteristic2, senddata);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								Log.d("alex", "writeCharacteristic error ");
							}
						}

					}

				}

			}
		}
	};

/*	private void displayHR(String data) {
		if (data != null) {
			hr.setText(data);
			records_hr1.setText(data);//sandy  //主頁面數據顯示()
		}
	}*/
	
	private void displayHR(String data) {
		if (data != null) {
			hr.setText(data); //主頁面數據顯示()
			records_hr1.setText(data);//sandy 
		}
	}

	public void displaymmHg(String data) {
		if (data != null) {
			mmHg.setText(data);
			records_hr1.setText(data);//sandy
		}
	}

	private void chkECG() {

		if (bECGMode == false) {
			bECGMode = true;
			iDrawCnt = (ecgCount / (256 * 5)) * (256 * 5) + 5;

			iv_ECG.setVisibility(View.VISIBLE);
			Log.e(TAG, "iv_ECG invalidate  chkECG");
			iv_ECG.invalidate();
			// handler.post(updateTimer);
		} else {
			bECGMode = false;
			iv_ECG.setVisibility(View.INVISIBLE);
			ll_main_bg.invalidate();
			if (bNewMeasure == true) {
				hr.setText("- -");
				mmHg.setText("- -");
			} else {
				if (bpDiastolic > 0) {
					if (bpPulseRate != 0) {
						displayHR(String.valueOf(bpPulseRate));
					} else {
						displayHR("- -");
					}
					displaymmHg(String.valueOf(bpDiastolic));
				}
			}
		}
	}

	// Demonstrates how to iterate through the supported GATT
	// Services/Characteristics.
	// In this sample, we populate the data structure that is bound to the
	// ExpandableListView
	// on the UI.
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

				if (uuid.equals("00002a36-0000-1000-8000-00805f9b34fb")) {
					Log.d(TAG, "uuid equal = " + uuid);
					Log.d("alex", "uuid equal =" + uuid);
					characteristic1 = gattCharacteristic;
				}
				if (uuid.equals("00002a37-0000-1000-8000-00805f9b34fb")) {
					Log.d(TAG, "uuid equal =" + uuid);
					Log.d("alex", "uuid equal =" + uuid);
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

	public void showMessage(Context context, int title, int msg) {
		new AlertDialog.Builder(context).setTitle(title).setMessage(msg).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface df, int i) {
			}
		}).show();

	}

	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (device.getAddress().equals(mDeviceAddress))
						hasDevice = true;
					// mLeDeviceListAdapter.addDevice(device);
					// mLeDeviceListAdapter.notifyDataSetChanged();
				}
			});
		}
	};

	public class OnDoubleClick extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {

			// chkECG();
			// if (mConnected == false) {
			// mBluetoothLeService.close();
			// mBluetoothLeService.initialize();
			// mBluetoothLeService.connect(mDeviceAddress);
			// }
			if (menuOut) {
				iv_ECG.setVisibility(View.INVISIBLE);
				SlidingMenu(ll_main_bg.findViewById(R.id.iv_home_menu));

			}
			return false;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			chkECG();

			return false;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			return super.onDoubleTapEvent(e);
		}
	}
//    class MyUncaughtHandler implements UncaughtExceptionHandler
//    {
//        @Override
//        public void uncaughtException(Thread thread, Throwable ex)
//        {
//            Log.e("MyUncaughtHandler", ex.getMessage(), ex);
//        }
//    }
}
