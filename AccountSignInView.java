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
import com.mdbiomedical.app.vion.vian_health.service.FTPService;
import com.mdbiomedical.app.vion.vian_health.service.MainService;
import com.mdbiomedical.app.vion.vian_health.service.SystemService;
import com.mdbiomedical.app.vion.vian_health.service.model.ConstantS;
import com.mdbiomedical.app.vion.vian_health.service.model.UserDataItem;
import com.mdbiomedical.app.vion.vian_health.util.ChangeView;
import com.mdbiomedical.app.vion.vian_health.util.DeviceConstant;
import com.mdbiomedical.app.vion.vian_health.util.FileUtils;
import com.mdbiomedical.app.vion.vian_health.util.HttpUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class AccountSignInView extends Activity {

	AuthorityService authorityService = new AuthorityService();
	SystemService systemService;
	MainService mainService;

	TextView tv_signin_forgot_password;
	LinearLayout  ll_signin_forgot_password;
	LinearLayout ll_signin_back;
	EditText et_signin_email_keyin;
	LinearLayout ll_signin_btn;
	EditText et_signin_password_keyin;
	String password = "";
	String email = "";
	com.mdbiomedical.app.vion.vian_health.model.User user = null;
	FTPClient ftpclient;
	boolean connectSuccess = false;
	ArrayList<String> images;
	String ftpAddress = ConstantS.SERVER_ADDRESS;
	String userName = ConstantS.FTP_USERMAME;
	String passWord = ConstantS.FTP_PASSWORD;
	String retrieveFromFTPFolder = ConstantS.FTP_FOLODER;
	int port = ConstantS.FTP_IP;
	FTPService ftpServer;
	Boolean signinFlag = true;
	Date date = new Date();
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
	AsyncTask<String, Void, Void> getFriendListTask0;
	AsyncTask<String, Void, Void> getFriendListTask1;
	AsyncTask<String, Void, Void> getFriendListTask2;
	AsyncTask<String, Void, Void> getFriendListTask3;
	AsyncTask<String, Void, Void> getFriendListTask4;
	AsyncTask<String, Void, Void> getFriendListTask5;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_sign_in_activity);

		// 初始化
		init();

	}

	public void init() {

		mainService = new MainService(this.getApplicationContext());
		systemService = new SystemService(this.getApplicationContext());
		tv_signin_forgot_password = (TextView) findViewById(R.id.tv_signin_forgot_password);
		et_signin_email_keyin = (EditText) findViewById(R.id.et_signin_email_keyin);
		et_signin_password_keyin = (EditText) findViewById(R.id.et_signin_password_keyin);
		ll_signin_btn = (LinearLayout) findViewById(R.id.ll_signin_btn);
		ll_signin_back = (LinearLayout) findViewById(R.id.ll_signin_back);
		ll_signin_forgot_password = (LinearLayout) findViewById(R.id.ll_signin_forgot_password);
		TextView tv_signin_title = (TextView) findViewById(R.id.tv_signin_title);

		tv_signin_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.03f));

		ftpServer = new FTPService(userName, passWord, ftpAddress, port, retrieveFromFTPFolder);

		// sign in Button 使用已經擁有的帳號登入
		ll_signin_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// 隱藏鍵盤
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(et_signin_email_keyin.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(et_signin_password_keyin.getWindowToken(), 0);

				if (signinFlag) {
					signinFlag = false;
					getStatusBarHeight();
					password = et_signin_password_keyin.getText().toString();
					email = et_signin_email_keyin.getText().toString();

					if (email.equals("")) {
						Toast.makeText(AccountSignInView.this, getString(R.string.tv_account_email) + getString(R.string.is_empty), Toast.LENGTH_SHORT).show();
						signinFlag = true;
					} else if (email.contains(" ")) {
						Toast.makeText(AccountSignInView.this, getString(R.string.tv_account_email) + getString(R.string.format_error), Toast.LENGTH_SHORT).show();
						signinFlag = true;
					} else if (!email.contains("@")) {
						Toast.makeText(AccountSignInView.this, getString(R.string.tv_account_email) + getString(R.string.format_error), Toast.LENGTH_SHORT).show();
						signinFlag = true;
					} else if (password.equals("")) {
						Toast.makeText(AccountSignInView.this, getString(R.string.tv_account_password) + getString(R.string.is_empty), Toast.LENGTH_SHORT).show();
						signinFlag = true;
					} else {
						String url = "http://" + ConstantS.SERVER_ADDRESS + "/signinECG.php?UserEmail=" + email.replaceAll(" ", "+") + "&UserPassword=" + password.replaceAll(" ", "+");
						// sign in
						new SigninAsyncTask().execute(url);
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
//sandy0330
				ChangeView.ChangeActivity(AccountSignInView.this, ForgotPassWordView.class);
				//ChangeView.ChangeActivity(AccountSignInView.this, HomeView.class);

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
//	@Override
//	protected void onResume() {
//
//		super.onResume();
//		Log.i("sandy", "signin_onresume");
//		signinFlag = true;
//		loadData();
//		if (getIntent().getBooleanExtra("EXIT", false)) {
//			finish();
//		}
//	}
//
//	@Override
//	protected void onPause() {
//
//		try {
//			super.onPause();
//		} catch (Exception e) {
//			finish();
//		}
//
//	}
//	private void loadData() {
//		user = mainService.loadUserDataItem();
////		if (user.getNationality() != null)
////			tv_register_nationality_keyin.setText(user.getNationality()
////					.toString());
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
//			jsonString = HttpUtils.getData(AccountSignInView.this, url);
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

	public class SigninAsyncTask extends AsyncTask<String, Void, String> {
		private ProgressDialog mdialog;

		@Override
		protected void onPreExecute() {
			Log.d("vion", "Dialog start!");
			mdialog = new ProgressDialog(AccountSignInView.this);
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
				Log.d("vion", "doInBackground start!");
				Log.d("sandy", "326_"+params[0]);
				String jsonString = HttpUtils.getData(AccountSignInView.this, params[0]);
				Log.d("sandy", "328_"+jsonString);
				if (jsonString != null) {//成功連線

					if (!jsonString.equals("TIME_OUT")) {
						//下兩行解決jsonparseException
						//jsonString=jsonString.substring(2, jsonString.length()-1);
						//jsonString = "{"+jsonString+"}";
						//
						user = authorityService.login(jsonString);
						//Log.d("sandy", "signin_user="+String.valueOf(user.getEmail()));
						Log.d("sandy", "signin_user="+user);
						if (user == null) {//抓不到資料
							
							Log.e("sandy", "login error : user = null");
							//return null;
							result = "Failed";
							return result;


							//UserDataItem userDataItem = new UserDataItem();

							
							
//							userDataItem.UserEmail = user.getEmail();
//							userDataItem.UserFirstName = user.getFirstName();
//							userDataItem.UserLastName = user.getLastName();
//							userDataItem.UserHeight = user.getHeight();
//							userDataItem.UserWeight = user.getWeight();
//							userDataItem.UserNationality = user.getNationality();
//							userDataItem.UserGender = user.getGender();
//							userDataItem.UserName = user.getUserName();
//							userDataItem.UserPassword = user.getPassword();
//							userDataItem.UserIsJoinLeaderboard = user.getIsJoinLeaderboard();
//							userDataItem.UserId = user.getId();
//							userDataItem.UserFacebookId = user.getFacebookId();
//							Log.d("vion", "userDataItem done!");
							
							
//							userDataItem.UserEmail = "test";
//							userDataItem.UserFirstName= "test";
//							userDataItem.UserLastName = "test";
//							userDataItem.UserHeight= "test";
//							userDataItem.UserWeight = "test";
//							userDataItem.UserNationality = "test";
//							userDataItem.UserGender = 1;
//							userDataItem.UserName = "test";
//							userDataItem.UserPassword = "e10adc3949ba59abbe56e057f20f883e";
//							userDataItem.UserIsJoinLeaderboard = 1;
//							userDataItem.UserId = 1;
//							userDataItem.UserFacebookId = "test";
							// getFriendList(user.getEmail());
							
							
							//sandy
//							addDefaultScoreLevel();
							
							// 下載LEVEL
							//downloadScoreLevel();
							// 下載本人照片
							//new downloadPhotoAsyncTask().execute(user.getEmail());

//							Log.d("vion", "saveSetting start!");
//							saveSetting();
//							mainService.updateUserDataItem(userDataItem);
							//mainService.updateScore(user.getUserPlayRecords(), false);
							//mainService.updateFriendList(user.getUserFriends());

//							Log.d("vion", "saveSetting done!");
//							result = "OK";
//							return result;
						
							
						} else {
							Log.d("sandy", "signin_user="+String.valueOf(user.getEmail()));
							UserDataItem userDataItem = new UserDataItem();

							
							
							userDataItem.UserEmail = user.getEmail();
							userDataItem.UserFirstName = user.getFirstName();
							userDataItem.UserLastName = user.getLastName();
							userDataItem.UserHeight = user.getHeight();
							userDataItem.UserWeight = user.getWeight();
							userDataItem.UserNationality = user.getNationality();
							userDataItem.UserGender = user.getGender();
							userDataItem.UserName = user.getUserName();
							userDataItem.UserPassword = password.toString();//使用未加密的密碼儲存
							userDataItem.UserIsJoinLeaderboard = user.getIsJoinLeaderboard();
							userDataItem.UserId = user.getId();
							userDataItem.UserFacebookId = user.getFacebookId();
							userDataItem.UserBirthDate=user.getUserBirthDate();
							
//							Log.d("vion", "userDataItem done!");
//							userDataItem.UserEmail = "test";
//							userDataItem.UserFirstName= "test";
//							userDataItem.UserLastName = "test";
//							userDataItem.UserHeight= "test";
//							userDataItem.UserWeight = "test";
//							userDataItem.UserNationality = "test";
//							userDataItem.UserGender = 1;
//							userDataItem.UserName = "test";
//							userDataItem.UserPassword = "e10adc3949ba59abbe56e057f20f883e";
//							userDataItem.UserIsJoinLeaderboard = 1;
//							userDataItem.UserId = 1;
//							userDataItem.UserFacebookId = "test";
//							userDataItem.UserBirthDate="2017-04-05";
							// getFriendList(user.getEmail());
							//sandy
							//addDefaultScoreLevel();
							
							
							
							// 下載LEVEL
							//downloadScoreLevel();
							// 下載本人照片
							//new downloadPhotoAsyncTask().execute(user.getEmail());

							Log.d("vion", "saveSetting start!");
							saveSetting();
							mainService.updateUserDataItem(userDataItem);
							//mainService.updateScore(user.getUserPlayRecords(), false);
							//mainService.updateFriendList(user.getUserFriends());

							Log.d("vion", "saveSetting done!");
							result = "OK";
							return result;
						}

					} else {
						result = "TIME_OUT";
						return result;
					}
				} else {
					//Log.e("sandy", "login error : jsonString = null");
					Log.e("vion", "login error : jsonString = null");
					return null;
				}

			} catch (Exception e) {
				Log.e("sandy", "login error="+e.getMessage());
				Log.e("vion", "login error");
				return null;
			}
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {

				if (result.equals("TIME_OUT")) {
					mdialog.dismiss();
					Toast.makeText(getApplicationContext(), getString(R.string.internet_slow), Toast.LENGTH_SHORT).show();
					signinFlag = true;
				} 
				else if(result.equals("Failed"))//sandy_0331
				{
					mdialog.dismiss();
					Toast.makeText(AccountSignInView.this, "email or password error", Toast.LENGTH_LONG).show();
					//Toast.makeText(getApplicationContext(), getString(R.string.internet_slow), Toast.LENGTH_SHORT).show();
					signinFlag = true;
					
					//Intent intent3 = new Intent(AccountSignInView.this, AboutView.class);//前往註冊
					//startActivityForResult(intent3, 3);
				}
				else {
					mdialog.dismiss();
					Toast.makeText(AccountSignInView.this, "Login Success", Toast.LENGTH_LONG).show();
					Intent intent3 = new Intent(AccountSignInView.this, HomeView.class);
					startActivityForResult(intent3, 3);
				}
			} else {
				mdialog.dismiss();
				// Toast.makeText(AccountSignInView.this,"Login Failed!!",
				// Toast.LENGTH_LONG).show();
				Toast.makeText(getApplicationContext(), "Login Failed:" + getString(R.string.unable_connect), Toast.LENGTH_SHORT).show();
				signinFlag = true;
//				mdialog.dismiss();
//				Toast.makeText(AccountSignInView.this, "Login Success", Toast.LENGTH_LONG).show();
//				Intent intent3 = new Intent(AccountSignInView.this, HomeView.class);
//				startActivityForResult(intent3, 3);
			}

		}

		protected void onCancelled() {
			signinFlag = true;
			mdialog.dismiss();
			super.onCancelled();
		}

	}

	public class downloadPhotoAsyncTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... email) {
			int tryCount = 0;
			try {
				Log.d("vion", "download photo start!");
				ftpclient = ftpServer.getClient();
				while (tryCount < 10) {
					if (ftpclient != null) {
						tryCount = 100;
					}
					Thread.sleep(500);
					tryCount++;
				}

				if (ftpclient != null) {
					String saveString = "/data/data/" + AccountSignInView.this.getPackageName() + "/";
					Log.d("ftplog", "connect success");
					connectSuccess = true;

					String downloadString = email[0] + ".png";
					Log.d("ftplog", "downloadString:" + downloadString);

					ftpServer.ftpDownload(ftpclient, retrieveFromFTPFolder + downloadString, saveString + downloadString);

					String bmpPathName = "/data/data/" + AccountSignInView.this.getPackageName() + "/" + email[0] + ".png";
					try {
						Bitmap bmp = BitmapFactory.decodeFile(bmpPathName);
						//FileUtils.savePhoto(getApplicationContext(), "no_email_232" + ".png", bmp);
						Log.d("vion", "download photo done!");
					} catch (Exception e) {
						Log.d("vion", "download user photo err!");
					}
				}
			} catch (Exception e) {

				Log.d("ftplog", "connect failed : " + e.toString());

			}

			return null;
		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 3) {
			setResult(2);
			finish();
		}
	}

}
