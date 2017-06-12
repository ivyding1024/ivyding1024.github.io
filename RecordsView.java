package com.mdbiomedical.app.vion.vian_health.view;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mdbiomedical.app.vion.vian_health.R;
import com.mdbiomedical.app.vion.vian_health.helper.DatabaseHelper;
import com.mdbiomedical.app.vion.vian_health.model.RecordList;
import com.mdbiomedical.app.vion.vian_health.service.MyBaseAdapter;
import com.mdbiomedical.app.vion.vian_health.util.ChangeView;
import com.mdbiomedical.app.vion.vian_health.util.DeviceConstant;

public class RecordsView extends Activity {

	public final static int TYPE_ECG = 0x26;
	public final static int TYPE_BP = 0x27;
	private int i = -2;
	private Handler handler = new Handler();
	final int TABLE_LIST_SIZE = 6;
	private ListView listView;
	Comparator<RecordList> comparator ;
	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
	private BaseAdapter adapter;

	LinearLayout ll_records_table, ll_rec_button;
	RelativeLayout rl_rec_view;
	int recordViewCount = 0;
	List<RecordList> newRecordList = new ArrayList<RecordList>();
	DatabaseHelper databaseHelper = new DatabaseHelper(this);
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd",java.util.Locale.getDefault());
    String dateString= DateFormat.getBestDateTimePattern(java.util.Locale.getDefault(), "yyyy/MM/dd");
    java.text.DateFormat dateTimeInstance =  new SimpleDateFormat(dateString,java.util.Locale.getDefault());
	NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
	public static boolean bDelMode = false;
	Toast tProc = null;

	ListView lvDetail;

	// ArrayList<RecordListData> myList = new ArrayList<RecordListData>();
	Context context = RecordsView.this;

	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}
	//
	public static DisplayMetrics dm = new DisplayMetrics();
	//
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.records_activity);
		
		
		init();
		 comparator = new Comparator<RecordList>() {
			public int compare(RecordList s1, RecordList s2) {

				return s2.sDatetime.compareTo(s1.sDatetime);
			}
		};

		loadData();
		Collections.sort(newRecordList, comparator);
		adapter = new MyBaseAdapter(context, newRecordList);
		lvDetail.setAdapter(adapter);
		adapter.notifyDataSetChanged();

		lvDetail.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				int i = arg2;
				// i = newRecordList.size() - arg2 - 1;// 倒轉列表
				if (bDelMode == true) {
					if (newRecordList.get(i).checked == 1)
						newRecordList.get(i).checked = 0;
					else
						newRecordList.get(i).checked = 1;
					adapter.notifyDataSetChanged();
					return;
				}

				Intent intent = new Intent();
				intent.setClass(RecordsView.this, RecordDetail.class);
				Bundle bundle = new Bundle();
				bundle.putString("Datetime", newRecordList.get(i).sDatetime);
				bundle.putInt("AnalysisType", newRecordList.get(i).AnalysisType);
				bundle.putString("DeviceID", String.valueOf(newRecordList.get(i).DeviceID));
				bundle.putString("Filename", newRecordList.get(i).sFilename);
				bundle.putString("UserMode", String.valueOf(newRecordList.get(i).UserMode));
				bundle.putString("HRData", newRecordList.get(i).HRData);
				bundle.putString("HRDataTimeStamp", newRecordList.get(i).HRDataTimeStamp);
				bundle.putString("ECG", newRecordList.get(i).ECG);
				if (newRecordList.get(i).AnalysisType == TYPE_BP) {
					if (newRecordList.get(i).BPMNoiseFlag == 0) {
						bundle.putString("Pulse", String.valueOf(newRecordList.get(i).BPHeartRate));
						bundle.putString("Sys", String.valueOf(newRecordList.get(i).HighBloodPressure));
						bundle.putString("Dia", String.valueOf(newRecordList.get(i).LowBloodPressure));
						
					} else {
						bundle.putString("Pulse", "EE");// ee
						bundle.putString("Sys", "- -");
						bundle.putString("Dia", "- -");
					}
				} else {
					if (newRecordList.get(i).Noise == 0)
						bundle.putString("Pulse", String.valueOf(newRecordList.get(i).HeartRate));
					else
						bundle.putString("Pulse", "EE");// ee
					bundle.putString("Sys", "- -");
					bundle.putString("Dia", "- -");
				}
				
				intent.putExtras(bundle);
				startActivityForResult(intent, 136);
				overridePendingTransition(R.anim.slide_in_right, R.anim.left_out);

			}
		});

		lvDetail.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View v, int index, long arg3) {
				// TODO Auto-generated method stub
				// View vi;

				newRecordList.get(index).checked = 1;

				adapter.notifyDataSetChanged();

				ll_rec_button.getLayoutParams().height = 144;
				ll_rec_button.requestLayout();

				// CheckBox chk = (CheckBox) v.findViewById(R.id.chk_record);
				// if (chk != null)
				// chk.setChecked(true);
				bDelMode = true;
				return true;
			}
		});
	}

	public void chkECG() {
		if (newRecordList == null)
			return;
		if (i == -2)
			i = newRecordList.size();
		if (i >= 0) {
			try {
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			i--;
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
		HomeView.home_pressed="disable";
		//
		//tProc=Toast.makeText(this,String.valueOf(DeviceConstant.screenHeight) , Toast.LENGTH_SHORT);
		//tProc.show();
		//
		int version = Integer.valueOf(android.os.Build.VERSION.SDK);
		if (version >= 11) {
		} else {
		}

		if (tProc == null) {
			tProc = Toast.makeText(this, "Please wait...", Toast.LENGTH_SHORT);
			tProc.show();
		}

		// newRecordList.clear();
		// ((MyBaseAdapter) adapter).clear();
		// adapter.notifyDataSetChanged();
		// if (newRecordList.size() == 0)
		// loadData();
		// adapter.notifyDataSetChanged();
		// Log.d("alex","getcount:"+adapter.getCount());
	}

@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d("alex", "on Pause");
		HomeView.home_pressed = "wait";
		}
	private void init() {
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		DeviceConstant.screenWidth = dm.widthPixels;
		DeviceConstant.screenHeight = dm.heightPixels;
		DeviceConstant.screenDPI = dm.densityDpi;
		
		
		
		ImageView iv_record_menu;
		iv_record_menu = (ImageView) findViewById(R.id.ll_go_back);
		ll_records_table = (LinearLayout) findViewById(R.id.ll_record_table);
		ll_rec_button = (LinearLayout) findViewById(R.id.ll_rec_button);
		rl_rec_view = (RelativeLayout) findViewById(R.id.rl_rec_view);
		TextView tv_records_title = (TextView) findViewById(R.id.tv_records_title);
		lvDetail = (ListView) findViewById(R.id.listView);
		Log.d("sandy","choose_user="+RecordUserView.choose_user);
	//20170512	
		if(RecordUserView.choose_user==0)
			tv_records_title.setText(R.string.user1_title);
		else
			tv_records_title.setText(R.string.user2_title);
		
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
//		if (DeviceConstant.statusBarHeight != 0) {
//			View view = getWindow().getDecorView().findViewById(android.R.id.content);
//			try {
//				Bitmap leftSideImage = UIUtils.creatMenuLeftSidePic(view);
//
//				if (leftSideImage != null) {
//					HomeView.iv_home_left_side_pic.setVisibility(View.VISIBLE);
//					HomeView.iv_home_left_side_pic.setImageBitmap(leftSideImage);
//				}
//			} catch (Exception e) {
//				HomeView.iv_home_left_side_pic.setVisibility(View.INVISIBLE);
//			}
//
//		} else {
//			HomeView.iv_home_left_side_pic.setImageResource(R.drawable.transparent_10x10);
//		}
		
		
		
		
		setResult(RESULT_OK);
		finish();
		overridePendingTransition(R.anim.slide_no, R.anim.slide_out_left);
	}


	public void loadData() {

		newRecordList = databaseHelper.getRecords(RecordUserView.choose_user);
		// addRecordString();
	}

	private void addEmptytable(int addList) {

		for (int i = 0; i < addList; i++) {

			LayoutInflater factory = LayoutInflater.from(this);
			View myView = factory.inflate(R.layout.records_content_activity, null);

			LinearLayout ll_record_content;
			ll_record_content = (LinearLayout) myView.findViewById(R.id.ll_record_content);
			ll_record_content.removeAllViews();
			ll_record_content.getLayoutParams().height = (int) (DeviceConstant.screenHeight * 0.17);
			ll_record_content.requestLayout();
			ll_records_table.addView(myView);
		}

	}

	public void onCancelClick(View v) {
		bDelMode = false;
		for (int i = 0; i < newRecordList.size(); i++)
			newRecordList.get(i).checked = 0;

		ll_rec_button.getLayoutParams().height = 0;
		ll_rec_button.requestLayout();

		adapter.notifyDataSetChanged();
	}

	public void onDelClick(View v) {

		new AlertDialog.Builder(this).setTitle(R.string.app_name).setMessage(R.string.sure_removed).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface df, int i) {
			}
		}).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface df, int j) {

				View vi;
				for (int i = 0; i < newRecordList.size(); i++) {
					if (newRecordList.get(i).checked == 0)
						continue;
					File file = new File(newRecordList.get(i).sFilename);
					file.delete();
					databaseHelper.delRecord(newRecordList.get(i).sDatetime);
				}

				loadData();
				Collections.sort(newRecordList, comparator);
				adapter = new MyBaseAdapter(context, newRecordList);
				lvDetail.setAdapter(adapter);
				adapter.notifyDataSetChanged();

				onCancelClick(null);

			}
		}).show();

	}

}
