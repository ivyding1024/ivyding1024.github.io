package com.mdbiomedical.app.vion.vian_health.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONArray;

import com.mdbiomedical.app.vion.vian_health.R;
import com.mdbiomedical.app.vion.vian_health.model.SettingItem;
import com.mdbiomedical.app.vion.vian_health.service.AuthorityService;
import com.mdbiomedical.app.vion.vian_health.service.MainService;
import com.mdbiomedical.app.vion.vian_health.service.SystemService;
import com.mdbiomedical.app.vion.vian_health.service.model.ConstantS;
import com.mdbiomedical.app.vion.vian_health.service.model.UserDataItem;
import com.mdbiomedical.app.vion.vian_health.util.ChangeView;
import com.mdbiomedical.app.vion.vian_health.util.DeviceConstant;
import com.mdbiomedical.app.vion.vian_health.util.HttpUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



public class ChangePassword extends Activity {

	AuthorityService authorityService = new AuthorityService();
	SystemService systemService;
	MainService mainService;

	TextView tv_signin_forgot_password;
	LinearLayout ll_signin_back, ll_signin_forgot_password;
	EditText ll_old_password, ll_new_password, ll_re_new_password;
	LinearLayout ll_Change_password_btn;
	String old_password = "", new_password = "", re_new_password = "";

	com.mdbiomedical.app.vion.vian_health.model.User user = null;
	FTPClient ftpclient;
	boolean connectSuccess = false;
	Boolean signinFlag = true;
	private final static String TAG ="sandy";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_password_activity);

		// 初始化
		init();

	}

	public void init() {

		mainService = new MainService(this.getApplicationContext());
		user = mainService.loadUserDataItem();
		systemService = new SystemService(this.getApplicationContext());
		tv_signin_forgot_password = (TextView) findViewById(R.id.tv_signin_forgot_password);
		ll_old_password = (EditText) findViewById(R.id.ll_old_password);
		ll_new_password = (EditText) findViewById(R.id.ll_new_password);
		ll_re_new_password = (EditText) findViewById(R.id.ll_re_new_password);
		ll_Change_password_btn = (LinearLayout) findViewById(R.id.ll_Change_password_btn);
		ll_signin_back = (LinearLayout) findViewById(R.id.ll_signin_back);
		ll_signin_forgot_password = (LinearLayout) findViewById(R.id.ll_signin_forgot_password);
		TextView ll_change_password_title = (TextView) findViewById(R.id.ll_change_password_title);

		ll_change_password_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.03f));

		// 修改密碼
		ll_Change_password_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// 隱藏鍵盤
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(ll_old_password.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(ll_new_password.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(ll_re_new_password.getWindowToken(), 0);

				if (signinFlag) {
					signinFlag = false;
					getStatusBarHeight();
					old_password = ll_old_password.getText().toString();
					new_password = ll_new_password.getText().toString();
					re_new_password = ll_re_new_password.getText().toString();

					if (old_password.equals("")||new_password.equals("")) {
						Toast.makeText(ChangePassword.this, getString(R.string.tv_account_password) +" "+ getString(R.string.is_empty), Toast.LENGTH_SHORT).show();
						signinFlag = true;
					} else if (new_password.contains(" ")) {
						Toast.makeText(ChangePassword.this, getString(R.string.tv_account_password) + getString(R.string.format_error), Toast.LENGTH_SHORT).show();
						signinFlag = true;
					} else if (new_password.length() < 6) {
						Toast.makeText(ChangePassword.this, getString(R.string.tv_account_password) + getString(R.string.format_error), Toast.LENGTH_SHORT).show();
						signinFlag = true;
					} else if (!re_new_password.equals(new_password)) {
						Toast.makeText(ChangePassword.this,  getString(R.string.password_unmatch), Toast.LENGTH_SHORT).show();
						signinFlag = true;
					} else {
						String url = "http://" + ConstantS.SERVER_ADDRESS + "/updatePassword.php?OriginalUserEmail=" + user.getEmail().replaceAll(" ", "+") + "&OriginalPassword=" + old_password + "&Password=" + new_password+"&NewUserEmail=" + "null";
						Log.d(TAG, "url=" + url);
						// change password
						new ChangePasswordAsyncTask().execute(url);
						ll_old_password.setText("");
						ll_new_password.setText("");
						ll_re_new_password.setText("");
						signinFlag = true;//若停留在此畫面則加
					}

				}
			}

			private void getStatusBarHeight() {

				Rect rect = new Rect();
				Window win = getWindow();
				win.getDecorView().getWindowVisibleDisplayFrame(rect);
				int statusBarHeight = rect.top;
				if (statusBarHeight != 0) {
					int xdir = (int) (statusBarHeight + (DeviceConstant.screenHeight * 0.078f));
					DeviceConstant.statusBarHeight = statusBarHeight;
					DeviceConstant.xdir = xdir;
				}
				Log.d("vion", "statusBarHeight = " + DeviceConstant.statusBarHeight);
				Log.d("vion", "xdir = " + DeviceConstant.xdir);

			}
		});

		// 回上一頁
		ll_signin_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				try {
					ChangeView.onBack();
				} catch (Exception e) {
					finish();
				}
			}
		});

		// 開啟忘記密碼頁面
		ll_signin_forgot_password.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				ChangeView.ChangeActivity(ChangePassword.this, ForgotPassWordView.class);

			}
		});

		// 文字加底線
		String udata = getString(R.string.signin_forgot_password_x);
		
		SpannableString content = new SpannableString(udata);
		content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
		tv_signin_forgot_password.setText(content);

	}

//	private void addDefaultScoreLevel() {
//
//		int[] expList = { 0, 10005, 20005, 30005, 40005, 50005, 60005, 70005, 80005, 90005, 100005 };
//		List<UserLevelItem> scoreLevel = new ArrayList<UserLevelItem>();
//		for (int i = 0; i < expList.length; i++) {
//
//			UserLevelItem item = new UserLevelItem();
//			item.setEXP(expList[i]);
//			item.setLV(i);
//			scoreLevel.add(item);
//		}
//		mainService.updateScoreLevel(scoreLevel);
//
//	}

	public void saveSetting() {
		Resources r = this.getResources();
		systemService = new SystemService(this);

		List<SettingItem> settingItem = new ArrayList<SettingItem>();

		settingItem.add(new SettingItem(r.getResourceEntryName(R.string.setting_is_real_register), "true"));
		settingItem.add(new SettingItem(r.getResourceEntryName(R.string.setting_is_new_game), "false"));
		settingItem.add(new SettingItem(r.getResourceEntryName(R.string.setting_join_leaderboard), String.valueOf(user.getIsJoinLeaderboard())));
		settingItem.add(new SettingItem(r.getResourceEntryName(R.string.setting_statusbar_height), String.valueOf(DeviceConstant.statusBarHeight)));
		settingItem.add(new SettingItem(r.getResourceEntryName(R.string.setting_xdir), String.valueOf(DeviceConstant.xdir)));

		systemService.setSetting(settingItem);

	}

//	private void downloadScoreLevel() {
//		String jsonString = null;
//		String url = "http://" + ConstantS.SERVER_ADDRESS + "/getScoreLevels.php";
//
//		try {
//
//			jsonString = HttpUtils.getData(ChangePassword.this, url);
//			if (jsonString != null && !jsonString.equals("TIME_OUT")) {
//				JSONArray result = new JSONArray(jsonString);
//				List<UserLevelItem> scoreLevel = new ArrayList<UserLevelItem>();
//
//				Score score = new Score();
//				int allTotalScore = score.allTotalScore(user.getUserPlayRecords());
//				int currentLV = 0;
//				if (result != null) {
//					Log.d("vion", "result.length = " + result.length());
//					for (int i = 0; i < result.length(); i++) {
//
//						UserLevelItem item = new UserLevelItem();
//						item.setEXP(result.getInt(i));
//						item.setLV(i);
//						scoreLevel.add(item);
//					}
//
//					for (int i = 0; i < result.length(); i++) {
//						Log.d("vion", "allTotalScore = " + allTotalScore);
//						allTotalScore = allTotalScore - result.getInt(i);
//						if (allTotalScore <= 0) {
//							currentLV = i;
//							break;
//						}
//
//					}
//
//					scoreLevel.get(0).setLV(currentLV);
//				}
//
//				Log.d("vion", "updateScoreLevel start scoreLevel.size() = " + scoreLevel.size());
//
//				mainService.updateScoreLevel(scoreLevel);
//				Log.d("vion", "updateScoreLevel done!");
//			}
//
//		} catch (Exception e) {
//			Log.d("voin", "download score level errpr");
//		}
//
//	}
	public void showMessage(Context context, int title, int msg) {
		new AlertDialog.Builder(context).setTitle(title).setMessage(msg).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface df, int i) {
//				if (HomeView.ble_status != 2)
//					onBackPressed();
				//Intent intent3 = new Intent(ChangePassword.this, HomeView.class);
				//startActivityForResult(intent3, 3);
			}
		}).show();
		//.show();
		

	}
	public class ChangePasswordAsyncTask extends AsyncTask<String, Void, String> {
		private ProgressDialog mdialog;

		@Override
		protected void onPreExecute() {
			Log.d(TAG, "Dialog start!");
			mdialog = new ProgressDialog(ChangePassword.this);
			// mdialog.setTitle("請稍候");
			mdialog.setMessage("Loading...");
			mdialog.setCancelable(false);
			mdialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {

			String result;
			try {
				Log.d(TAG, "doInBackground start!");
				String jsonString = HttpUtils.getData(ChangePassword.this, params[0]);
				Log.d(TAG, "changePassword_jdonString="+jsonString);
				if (jsonString != null) {

					if (!jsonString.equals("TIME_OUT")) {

						boolean is_change_password = authorityService.changePassword(jsonString);
						Log.d(TAG,"291....is_change_password="+ is_change_password);
						if (is_change_password) {
							Log.e(TAG, "change password success");
							result = "success";
							return result;
						} else {
							Log.e(TAG, "change password fail");
							result = "fail";
							return result;
						}

					} else {
						result = "TIME_OUT";
						return result;
					}
				} else {
					return null;
				}

			} catch (Exception e) {
				Log.e("vion", "change password error");
				return null;
			}
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (result.equals("TIME_OUT")) {
				mdialog.dismiss();
				Toast.makeText(getApplicationContext(), getString(R.string.internet_slow), Toast.LENGTH_SHORT).show();
				signinFlag = true;
			} else if (result.equals("success")) {
				mdialog.dismiss();
				showMessage(ChangePassword.this, R.string.ll_change_password_title, R.string.change_password_success);
				UserDataItem userDataItem = new UserDataItem();

				userDataItem.UserEmail = user.getEmail();
				userDataItem.UserFirstName = user.getFirstName();
				userDataItem.UserLastName = user.getLastName();
				userDataItem.UserHeight = user.getHeight();
				userDataItem.UserWeight = user.getWeight();
				userDataItem.UserNationality = user.getNationality();
				userDataItem.UserGender = user.getGender();
				userDataItem.UserName = user.getUserName();
				userDataItem.UserPassword = new_password;
				userDataItem.UserIsJoinLeaderboard = user.getIsJoinLeaderboard();
				userDataItem.UserId = user.getId();
				userDataItem.UserFacebookId = user.getFacebookId();
				userDataItem.UserBirthDate=user.getUserBirthDate();
				mainService.updateUserDataItem(userDataItem);
				//Toast.makeText(ChangePassword.this, "change password success", Toast.LENGTH_LONG).show();
				
			} else if (result.equals("fail")) {
				mdialog.dismiss();
				showMessage(ChangePassword.this, R.string.ll_change_password_title, R.string.wrong_password);
				// Toast.makeText(AccountSignInView.this,"Login Failed!!",
				// Toast.LENGTH_LONG).show();
				//Toast.makeText(getApplicationContext(), "change password  Failed:" , Toast.LENGTH_SHORT).show();
				signinFlag = true;

			}
		}

		protected void onCancelled() {
			signinFlag = true;
			mdialog.dismiss();
			super.onCancelled();
		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 3) {
			setResult(2);
			finish();
		}
	}

}
