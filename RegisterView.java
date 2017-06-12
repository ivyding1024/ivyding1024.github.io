package com.mdbiomedical.app.vion.vian_health.view;

//這是註冊頁面

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.simonvt.numberpicker.NumberPicker;

import org.json.JSONArray;














import com.mdbiomedical.app.vion.vian_health.R;
import com.mdbiomedical.app.vion.vian_health.model.SettingItem;
import com.mdbiomedical.app.vion.vian_health.model.User;
import com.mdbiomedical.app.vion.vian_health.service.AuthorityService;
import com.mdbiomedical.app.vion.vian_health.service.MainService;
import com.mdbiomedical.app.vion.vian_health.service.SystemService;
import com.mdbiomedical.app.vion.vian_health.service.model.ConstantS;
import com.mdbiomedical.app.vion.vian_health.service.model.UserDataItem;
import com.mdbiomedical.app.vion.vian_health.util.AsyncTaskUtils;
import com.mdbiomedical.app.vion.vian_health.util.ChangeView;
import com.mdbiomedical.app.vion.vian_health.util.DeviceConstant;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class RegisterView extends Activity {

	// 宣告變數

	String firstName;
	String lastName;
	String nationality;
	String height;
	String weight;
	String gender_txt;
	int gender;
	String email;
	String userName;
	String password;
	String verify;
	int join_leaderboard = 1;
	Boolean registe_ok = false;
	Boolean registerFlag = true;

	//LinearLayout ll_register_nationality;
	//LinearLayout ll_register_height;
	//LinearLayout ll_register_weight;
	//LinearLayout ll_register_gender;
	LinearLayout ll_signup_btn;
	//LinearLayout ll_already_have_account;
	//TextView tv_already_have_account;
	
	//TextView tv_register_nationality_keyin;
	//TextView tv_register_height_keyin;
	//TextView tv_register_weight_keyin;
	//TextView tv_register_gender_keyin;
	EditText et_register_email_keyin;
	//EditText et_register_username_keyin;
	EditText et_register_password_keyin;
	EditText et_register_verify_keyin;
	//EditText et_register_first_name_keyin;
	//EditText et_register_last_name_keyin;
	//ImageView iv_join_leaderboard_switch;
	LinearLayout ll_signup,ll_register_back;
	//TextView tv_signup_btn;
   
	int number_A, number_B;
	int DEFAULT_HEIGHT = 175;
	int DEFAULT_WEIGHT = 60;
	NumberPicker numberPicker1;
	NumberPicker numberPicker2;
	SystemService systemService;
	MainService mainService;
	AuthorityService service = new AuthorityService();
	Date date = new Date();
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
	AsyncTask<String, Void, Boolean> getFriendListTask0;
	AsyncTask<String, Void, Boolean> getFriendListTask1;
	User user = new User();

	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		if (getIntent().getBooleanExtra("EXIT", false)) {
			finish();
		}
		setContentView(R.layout.register_activity);

		// 初始化
		init();

		// 點擊SignUp Button新增一個User帳號
		ll_signup_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (registerFlag) {
					registerFlag = false;
					getStatusBarHeight();
					getInputInfo();

					if (join_leaderboard != 1) { // 檢查Leaderboard狀態，不加入

//						if (firstName.equals("")) {
//							Toast.makeText(
//									RegisterView.this,
//									getString(R.string.tv_name_first)
//											+ getString(R.string.is_empty),
//									Toast.LENGTH_SHORT).show();
//							registerFlag = true;
//						} else if (lastName.equals("")) {
//							Toast.makeText(
//									RegisterView.this,
//									getString(R.string.tv_name_last)
//											+ getString(R.string.is_empty),
//									Toast.LENGTH_SHORT).show();
//							registerFlag = true;
//						} else {
//
//							// 不使用leaderboard開始
//
//							UserDataItem userDataItem = new UserDataItem();
//
//							userDataItem.UserEmail = email;
//							userDataItem.UserFirstName = firstName;
//							userDataItem.UserLastName = lastName;
//							userDataItem.UserHeight = height;
//							userDataItem.UserWeight = weight;
//							userDataItem.UserNationality = nationality;
//							userDataItem.UserGender = gender;
//							userDataItem.UserName = userName;
//							userDataItem.UserPassword = password;
//							userDataItem.UserIsJoinLeaderboard = join_leaderboard;
//							userDataItem.UserId = user.getId();
//							userDataItem.UserFacebookId = user.getFacebookId();
//							registe_ok = true;
//							//addDefaultScoreLevel();
//							saveSetting();
//							mainService.updateUserDataItem(userDataItem);
//
//							Intent intent3 = new Intent(RegisterView.this,
//									HomeView.class);
//							startActivityForResult(intent3, 3);
//							Toast.makeText(RegisterView.this,
//									getString(R.string.register_success),
//									Toast.LENGTH_SHORT).show();
//							finish();
//
//						}

					} else {// 檢查Leaderboard狀態，要加入

						 if (email.equals("")) {
							Toast.makeText(
									RegisterView.this,
									getString(R.string.tv_account_email)
											+ getString(R.string.is_empty),
									Toast.LENGTH_SHORT).show();
							registerFlag = true;
						} else if (email.contains(" ")) {
							Toast.makeText(
									RegisterView.this,
									getString(R.string.tv_account_email)
											+ getString(R.string.format_error),
									Toast.LENGTH_SHORT).show();
							registerFlag = true;
						} else if (!email.contains("@")) {
							Toast.makeText(
									RegisterView.this,
									getString(R.string.tv_account_email)
											+ getString(R.string.format_error),
									Toast.LENGTH_SHORT).show();
							registerFlag = true;
						}  else if (password.equals("")) {
							Toast.makeText(
									RegisterView.this,
									getString(R.string.tv_account_password)
											+ getString(R.string.is_empty),
									Toast.LENGTH_SHORT).show();
							registerFlag = true;
						} else if (password.contains(" ")) {
							Toast.makeText(
									RegisterView.this,
									getString(R.string.tv_account_password)
											+ getString(R.string.format_error),
									Toast.LENGTH_SHORT).show();
							registerFlag = true;
						} else if (verify.equals(password)) {

							user.setFirstName(firstName);
							user.setLastName(lastName);
							user.setNationality(nationality);
							user.setHeight(height);
							user.setWeight(weight);
							user.setGender(gender);
							user.setUserName(userName);
							user.setPassword(password);
							user.setIsJoinLeaderboard(join_leaderboard);
							user.setEmail(email);

							// 先檢查email是否註冊過 回傳int 0:已使用過 1:未使用過 2:連線失敗
							String url = "http://" + ConstantS.SERVER_ADDRESS
									+ "/isEmailExist.php?UserEmail="
							+ user.getEmail();

							new AsyncTaskUtils(RegisterView.this) {

								@Override
								public void refreshUI(String jsonString) {

									if (jsonString != null
											&& !jsonString.equals("TIME_OUT")) {
										Log.d("sandy", "251_jsonString="+jsonString);
										String checkEmail = service
												.checkEmail(jsonString);
										Log.d("sandy", "254_checkemail="+checkEmail);
						if (checkEmail.equals("1")) {

						// 開始註冊
					String url1 = "http://"+ ConstantS.SERVER_ADDRESS+ "/addECGUser.php?"
													+ "UserEmail="
													+ user.getEmail()
															.replaceAll(" ",
																	"+")
													+ "&UserHeight="
													+ user.getHeight()
													+ "&UserWeight="
													+ user.getWeight()
													+ "&UserNationality="
													+ user.getNationality()
															.replaceAll(" ",
																	"+")
													+ "&UserIsJoinLeaderboard="
													+ user.getIsJoinLeaderboard()
													+ "&UserGender="
													+ user.getGender()
													+ "&UserPassword="
													+ user.getPassword()
															.replaceAll(" ",
																	"+")
													+ "&UserFirstName="
													+ user.getFirstName()
															.replaceAll(" ",
																	"+")
													+ "&UserLastName="
													+ user.getLastName()
															.replaceAll(" ",
																	"+")
													+ "&UserName="
													+ user.getUserName()
															.replaceAll(" ",
																	"+");
													;
Log.e("sandy", "293_url1="+url1);
				new AsyncTaskUtils(RegisterView.this) {

				@Override
				public void refreshUI(String jsonString) {
					if (jsonString != null&& !jsonString.equals("TIME_OUT")) {
						Log.e("sandy", "297");
									String message = service.regist(jsonString);
									Log.e("sandy", "299_message="+message);
									if (message.equals("帳號註冊成功!")) {
										Toast.makeText(RegisterView.this,getString(R.string.register_success),
																	Toast.LENGTH_SHORT).show();
															registe_ok = true;

															singIn();

														} else {
															Toast.makeText(
																	RegisterView.this,
																	getString(R.string.register_fail),
																	Toast.LENGTH_SHORT)
																	.show();
															registerFlag = true;
														}

													} else {
														registerFlag = true;
														if (jsonString != null
																&& jsonString
																		.equals("TIME_OUT")) {
															Toast.makeText(
																	getApplicationContext(),
																	getString(R.string.internet_slow),
																	Toast.LENGTH_SHORT)
																	.show();
														} else {
															Toast.makeText(
																	getApplicationContext(),
																	getString(R.string.unable_connect),
																	Toast.LENGTH_SHORT)
																	.show();
														}
													}
												}
											}.execute(url1);

										} else if (checkEmail.equals("0")) {
											Toast.makeText(
													RegisterView.this,
													getString(R.string.email_is_exist),
													Toast.LENGTH_SHORT).show();
											registerFlag = true;
										} else {
											Toast.makeText(
													RegisterView.this,
													getString(R.string.register_fail),
													Toast.LENGTH_SHORT).show();
											registerFlag = true;
										}

									} else {

										registerFlag = true;
										if (jsonString != null
												&& jsonString
														.equals("TIME_OUT")) {
											Toast.makeText(
													getApplicationContext(),
													getString(R.string.internet_slow),
													Toast.LENGTH_SHORT).show();
										} else {
											Toast.makeText(
													getApplicationContext(),
													getString(R.string.unable_connect),
													Toast.LENGTH_SHORT).show();
										}

									}

								}
							}.execute(url);

						} else {
							Toast.makeText(RegisterView.this,
									getString(R.string.password_verify_err),
									Toast.LENGTH_SHORT).show();
							registerFlag = true;
						}
					}// 檢查Leaderboard狀態 if結束

				}// register btn flag判斷

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
				Log.d("vion", "statusBarHeight = "
						+ DeviceConstant.statusBarHeight);
				Log.d("vion", "xdir = " + DeviceConstant.xdir);

			}

		});

	}

	private void init() {

		// findview
		//tv_already_have_account = (TextView) findViewById(R.id.tv_already_have_account);
		//tv_register_nationality_keyin = (TextView) findViewById(R.id.tv_p_info_nationality_keyin);
		//tv_register_height_keyin = (TextView) findViewById(R.id.tv_p_info_height_keyin);
		//tv_register_weight_keyin = (TextView) findViewById(R.id.tv_p_info_weight_keyin);
		//tv_register_gender_keyin = (TextView) findViewById(R.id.tv_p_info_gender_keyin);
		ll_register_back=(LinearLayout) findViewById(R.id.ll_register_back);
		et_register_email_keyin = (EditText) findViewById(R.id.et_account_email_keyin);
		//et_register_username_keyin = (EditText) findViewById(R.id.et_account_username_keyin);
		et_register_password_keyin = (EditText) findViewById(R.id.et_account_password_keyin);
		et_register_verify_keyin = (EditText) findViewById(R.id.et_account_verify_keyin);
		//et_register_first_name_keyin = (EditText) findViewById(R.id.et_name_first_keyin);
		//et_register_last_name_keyin = (EditText) findViewById(R.id.et_name_last_keyin);

		//iv_join_leaderboard_switch = (ImageView) findViewById(R.id.iv_join_leaderboard_switch);

		//ll_signup = (LinearLayout) findViewById(R.id.ll_signup);
		ll_signup_btn = (LinearLayout) findViewById(R.id.ll_signup_btn);
		//tv_signup_btn = (TextView) findViewById(R.id.tv_signup);

		//ll_already_have_account = (LinearLayout) findViewById(R.id.ll_already_have_account);
		//ll_register_nationality = (LinearLayout) findViewById(R.id.ll_p_info_nationality);
		//ll_register_height = (LinearLayout) findViewById(R.id.ll_p_info_height);
		//ll_register_weight = (LinearLayout) findViewById(R.id.ll_p_info_weight);
		//ll_register_gender = (LinearLayout) findViewById(R.id.ll_p_info_gender);
		TextView tv_register_title = (TextView) findViewById(R.id.tv_register_title);

		tv_register_title.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				(int) (DeviceConstant.screenHeight * 0.03f));

		mainService = new MainService(this.getApplicationContext());
		getInputInfo();
		UserDataItem userDataItem = new UserDataItem();
		userDataItem.UserEmail = email;
		userDataItem.UserFirstName = firstName;
		userDataItem.UserLastName = lastName;
		userDataItem.UserHeight = height;
		userDataItem.UserWeight = weight;
		userDataItem.UserNationality = nationality;
		userDataItem.UserGender = gender;
		userDataItem.UserName = userName;
		
//		userDataItem.UserFirstName = user.getFirstName();
//		userDataItem.UserLastName = user.getLastName();
//		userDataItem.UserHeight = user.getHeight();
//		userDataItem.UserWeight = user.getWeight();
//		userDataItem.UserNationality = user.getNationality();
//		userDataItem.UserGender = user.getGender();
//		userDataItem.UserName = user.getUserName();
		
		userDataItem.UserPassword = password;
		userDataItem.UserIsJoinLeaderboard = join_leaderboard;
		userDataItem.UserId = user.getId();
		userDataItem.UserFacebookId = user.getFacebookId();
		//userDataItem.UserBirthDate=user.getUserBirthDate();
		saveSetting();
		mainService.updateUserDataItem(userDataItem);
		
		// 回上一頁
				ll_register_back.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {

						try {
							ChangeView.onBack();
						} catch (Exception e) {
							finish();
						}
					}
				});
		
		// 文字加底線
//		String udata = getString(R.string.already_have_account_x);
//		SpannableString content = new SpannableString(udata);
//		content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
//		tv_already_have_account.setText(content);

		// 已經有帳號 前往Sign in頁面
//		ll_already_have_account.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//
//				Intent intent = new Intent();
//				intent.setClass(RegisterView.this, AccountSignInView.class);
//				RegisterView.this.startActivityForResult(intent, 2);
//
//			}
//		});

	}

//	private void addDefaultScoreLevel() {
//
//		int[] expList = { 0, 10005, 20005, 30005, 40005, 50005, 60005, 70005,
//				80005, 90005, 100005 };
//		List<UserLevelItem> scoreLevel = new ArrayList<UserLevelItem>();
//		for (int i = 0; i < expList.length; i++) {
//
//			UserLevelItem item = new UserLevelItem();
//			item.setEXP(expList[i]);
//			if (i == 0) {
//				item.setLV(1);
//			} else {
//				item.setLV(i);
//			}
//			scoreLevel.add(item);
//		}
//		mainService.updateScoreLevel(scoreLevel);
//
//	}

	// switch控制
//	public void clickSwitch(View v) {
//
//		switch (v.getId()) {
//
//		case R.id.ll_p_info_nationality:
//			ChangeView.ChangeActivity(RegisterView.this, NationalityView.class);
//			break;
//		case R.id.ll_p_info_gender:
//			openGenderPopupWindow(v);
//			break;
//		default:
//			openPopupWindow(v);
//			break;
//
//		}
//
//	}

//	public void joinLeaderboardSwitch(View v) {
//
//		if (join_leaderboard == 1) {
//			iv_join_leaderboard_switch
//					.setImageResource(R.drawable.toggle_buttons_off);
//			join_leaderboard = 0;
//		} else {
//			iv_join_leaderboard_switch
//					.setImageResource(R.drawable.toggle_buttons_on);
//			join_leaderboard = 1;
//		}
//
//	}

//	private void openPopupWindow(View v) {
//
//		LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
//				.getSystemService(LAYOUT_INFLATER_SERVICE);
//		View popupView = layoutInflater.inflate(
//				R.layout.number_picker_popup_activity, null);
//		final PopupWindow popupWindow = new PopupWindow(popupView,
//
//		LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//		popupWindow.setOutsideTouchable(false);
//		LinearLayout ll_number_picker_done = (LinearLayout) popupView
//				.findViewById(R.id.ll_number_picker_done);
//		// setTheme(R.style.SampleTheme_Light);
//
//		final TextView tv_title = (TextView) popupView
//				.findViewById(R.id.tv_title);
//		TextView tv_unit = (TextView) popupView.findViewById(R.id.tv_unit);
//		LinearLayout ll_back1 = (LinearLayout) popupView
//				.findViewById(R.id.ll_back1);
//		LinearLayout ll_back2 = (LinearLayout) popupView
//				.findViewById(R.id.ll_back2);
//
//		numberPicker1 = (NumberPicker) popupView
//				.findViewById(R.id.numberPicker1);
//		numberPicker2 = (NumberPicker) popupView
//				.findViewById(R.id.numberPicker2);
//
//		int heightDot = tv_register_height_keyin.getText().toString()
//				.lastIndexOf(".");
//		String userHeightA = tv_register_height_keyin.getText().toString()
//				.substring(0, heightDot);
//		String userHeightB = tv_register_height_keyin.getText().toString()
//				.substring(heightDot + 1);
//
//		int weightDot = tv_register_weight_keyin.getText().toString()
//				.lastIndexOf(".");
//		String userWeightA = tv_register_weight_keyin.getText().toString()
//				.substring(0, weightDot);
//		String userWeightB = tv_register_weight_keyin.getText().toString()
//				.substring(weightDot + 1);
//
//		switch (v.getId()) {
//
//		case R.id.ll_p_info_height:
//			tv_title.setText(getString(R.string.tv_basic_info1_height));
//			tv_unit.setText(getString(R.string.cm));
//			numberPicker1.setMaxValue(220);
//			numberPicker1.setMinValue(30);
//			numberPicker1.setValue(Integer.parseInt(userHeightA));
//			numberPicker2.setMaxValue(9);
//			numberPicker2.setMinValue(0);
//			numberPicker2.setValue(Integer.parseInt(userHeightB));
//			number_A = Integer.parseInt(userHeightA);
//			number_B = Integer.parseInt(userHeightB);
//			break;
//		case R.id.ll_p_info_weight:
//			tv_title.setText(getString(R.string.tv_basic_info1_weight));
//			tv_unit.setText(getString(R.string.kg));
//
//			numberPicker1.setMaxValue(120);
//			numberPicker1.setMinValue(5);
//			numberPicker1.setValue(Integer.parseInt(userWeightA));
//			numberPicker2.setMaxValue(9);
//			numberPicker2.setMinValue(0);
//			numberPicker2.setValue(Integer.parseInt(userWeightB));
//			number_A = Integer.parseInt(userWeightA);
//			number_B = Integer.parseInt(userWeightB);
//
//			break;
//
//		}
//
//		numberPicker1.setFocusable(true);
//		numberPicker1.setFocusableInTouchMode(false);
//
//		numberPicker2.setFocusable(true);
//		numberPicker2.setFocusableInTouchMode(true);
//
//		numberPicker1
//				.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//					public void onValueChange(NumberPicker view, int oldValue,
//							int newValue) {
//						number_A = newValue;
//
//					}
//				});
//
//		numberPicker2
//				.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//					public void onValueChange(NumberPicker view, int oldValue,
//							int newValue) {
//						number_B = newValue;
//
//					}
//				});
//
//		ll_number_picker_done.setOnClickListener(new Button.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//
//				String titleName = tv_title.getText().toString();
//				String value = String.valueOf(number_A + "." + number_B);
//				float value_float = Float.parseFloat(value);
//
//				if (titleName.equals(getString(R.string.tv_basic_info1_height))) {
//					tv_register_height_keyin.setText(value);
//
//				}
//				if (titleName.equals(getString(R.string.tv_basic_info1_weight))) {
//					tv_register_weight_keyin.setText(value);
//				}
//
//				// saveData(titleName,value);
//
//				popupWindow.dismiss();
//			}
//		});
//
//		ll_back1.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				popupWindow.dismiss();
//			}
//		});
//		ll_back2.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				popupWindow.dismiss();
//			}
//		});
//
//		popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
//		// popupWindow.setWindowLayoutMode(getWallpaperDesiredMinimumWidth(),
//		// getWallpaperDesiredMinimumWidth());
//		popupWindow.setFocusable(true);
//		popupWindow.update();
//	}

//	private void openGenderPopupWindow(View v) {
//		LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
//				.getSystemService(LAYOUT_INFLATER_SERVICE);
//		View genderPopupView = layoutInflater.inflate(
//				R.layout.popup_gender_activity, null);
//		final PopupWindow popupWindow = new PopupWindow(genderPopupView,
//				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//		popupWindow.setOutsideTouchable(false);
//		// initListView();
//
//		LinearLayout done = (LinearLayout) genderPopupView
//				.findViewById(R.id.ll_gender_done);
//		LinearLayout ll_back1 = (LinearLayout) genderPopupView
//				.findViewById(R.id.ll_back1);
//		LinearLayout ll_back2 = (LinearLayout) genderPopupView
//				.findViewById(R.id.ll_back2);
//		LinearLayout ll_gender_male_btn = (LinearLayout) genderPopupView
//				.findViewById(R.id.ll_gender_male_btn);
//		LinearLayout ll_gender_female_btn = (LinearLayout) genderPopupView
//				.findViewById(R.id.ll_gender_female_btn);
//		final ImageView iv_gender_male_btn_pic = (ImageView) genderPopupView
//				.findViewById(R.id.iv_gender_male_btn_pic);
//		final ImageView iv_gender_female_btn_pic = (ImageView) genderPopupView
//				.findViewById(R.id.iv_gender_female_btn_pic);
//
//		if (gender == 0) {
//			iv_gender_male_btn_pic.setVisibility(View.VISIBLE);
//			iv_gender_female_btn_pic.setVisibility(View.INVISIBLE);
//		} else {
//			iv_gender_male_btn_pic.setVisibility(View.INVISIBLE);
//			iv_gender_female_btn_pic.setVisibility(View.VISIBLE);
//		}
//
//		ll_gender_male_btn.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				iv_gender_male_btn_pic.setVisibility(View.VISIBLE);
//				iv_gender_female_btn_pic.setVisibility(View.INVISIBLE);
//				gender = 0;
//			}
//		});
//
//		ll_gender_female_btn.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				iv_gender_male_btn_pic.setVisibility(View.INVISIBLE);
//				iv_gender_female_btn_pic.setVisibility(View.VISIBLE);
//				gender = 1;
//			}
//		});
//
//		done.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//
//				if (gender == 0) {
//					tv_register_gender_keyin.setText(getString(R.string.male));
//				} else {
//					tv_register_gender_keyin
//							.setText(getString(R.string.female));
//				}
//				popupWindow.dismiss();
//			}
//
//		});
//
//		ll_back1.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				popupWindow.dismiss();
//			}
//		});
//		ll_back2.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				popupWindow.dismiss();
//			}
//		});
//		popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
//		popupWindow.setFocusable(true);
//		popupWindow.update();
//	}

	private void getInputInfo() {

		// 取得欄位資訊
		//firstName = et_register_first_name_keyin.getText().toString();
		
		//lastName = et_register_last_name_keyin.getText().toString();
		//userName = et_register_username_keyin.getText().toString();
		//gender_txt = tv_register_gender_keyin.getText().toString();
		//nationality = tv_register_nationality_keyin.getText().toString();
		
		firstName = "";
		lastName = "";
		userName ="";
		//gender_txt = null;
		gender=0;
		nationality = "";
		email = et_register_email_keyin.getText().toString();
		password = et_register_password_keyin.getText().toString();
		verify = et_register_verify_keyin.getText().toString();
		//weight = tv_register_weight_keyin.getText().toString();
		//height = tv_register_height_keyin.getText().toString();
		weight = "";
		height = "";
		
//		if (gender_txt.equals(getString(R.string.male))) {
//			gender = 0;
//		} else {
//			gender = 1;
//		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 2) {
			finish();
		}
	}

	@Override
	protected void onResume() {

		super.onResume();
		registerFlag = true;
		loadData();
		if (getIntent().getBooleanExtra("EXIT", false)) {
			finish();
		}
	}

	@Override
	protected void onPause() {

		try {
			super.onPause();
		} catch (Exception e) {
			finish();
		}

	}

	private void loadData() {
		user = mainService.loadUserDataItem();
//		if (user.getNationality() != null)
//			tv_register_nationality_keyin.setText(user.getNationality()
//					.toString());

	}

	private void singIn() {

		// sign in
		String url = "http://" + ConstantS.SERVER_ADDRESS
				+ "/signin.php?UserEmail=" + email + "&UserPassword="
				+ password;

		new AsyncTaskUtils(RegisterView.this) {

			@Override
			public void refreshUI(String jsonString) {

				if (jsonString != null && !jsonString.equals("TIME_OUT")) {

					com.mdbiomedical.app.vion.vian_health.model.User user = service
							.login(jsonString);

					if (user == null) {

						Toast.makeText(RegisterView.this, "Login fail",
								Toast.LENGTH_LONG).show();
						registerFlag = true;

					} else {

						Toast.makeText(RegisterView.this, "Login Success",
								Toast.LENGTH_LONG).show();

						UserDataItem userDataItem = new UserDataItem();

						userDataItem.UserEmail = user.getEmail();
						userDataItem.UserFirstName = user.getFirstName();
						userDataItem.UserLastName = user.getLastName();
						userDataItem.UserHeight = user.getHeight();
						userDataItem.UserWeight = user.getWeight();
						userDataItem.UserNationality = user.getNationality();
						userDataItem.UserGender = user.getGender();
						userDataItem.UserName = user.getUserName();
						userDataItem.UserPassword = user.getPassword();
						userDataItem.UserIsJoinLeaderboard = user
								.getIsJoinLeaderboard();
						userDataItem.UserId = user.getId();
						userDataItem.UserFacebookId = user.getFacebookId();
						userDataItem.UserBirthDate=user.getUserBirthDate();
						
						//addDefaultScoreLevel();
						//downloadScoreLevel();
						//getFriendList();
						saveSetting();

						mainService.updateUserDataItem(userDataItem);
						//mainService.updateFriendList(user.getUserFriends());
						//mainService.updateScore(user.getUserPlayRecords(),false);
								

						Intent intent3 = new Intent(RegisterView.this,
								HomeView.class);
						startActivityForResult(intent3, 3);
						finish();

					}
				} else {

					registerFlag = true;
					if (jsonString != null && jsonString.equals("TIME_OUT")) {
						Toast.makeText(getApplicationContext(),
								getString(R.string.internet_slow),
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getApplicationContext(),
								getString(R.string.unable_connect),
								Toast.LENGTH_SHORT).show();
					}

				}

			}
		}.execute(url);

		// keep user object
		// systemService.setLoginUser(user);
		// List<PlayResult> results = database.getPlayResults();
		//
		// for (PlayResult result : results ) {
		// Log.i(TAG, result.toString());
		// }

		// }

	}

	public void saveSetting() {
		Resources r = this.getResources();
		systemService = new SystemService(this);

		List<SettingItem> settingItem = new ArrayList<SettingItem>();
		if (join_leaderboard == 1) {
			settingItem.add(new SettingItem(r
					.getResourceEntryName(R.string.setting_is_real_register),
					"true"));
		} else {
			settingItem.add(new SettingItem(r
					.getResourceEntryName(R.string.setting_is_real_register),
					"false"));
		}

		settingItem.add(new SettingItem(r
				.getResourceEntryName(R.string.setting_join_leaderboard),
				String.valueOf(join_leaderboard)));

		if (registe_ok) {
			settingItem.add(new SettingItem(r
					.getResourceEntryName(R.string.setting_is_new_game),
					"false"));
		}

		settingItem.add(new SettingItem(r
				.getResourceEntryName(R.string.setting_statusbar_height),
				String.valueOf(DeviceConstant.statusBarHeight)));
		settingItem.add(new SettingItem(r
				.getResourceEntryName(R.string.setting_xdir), String
				.valueOf(DeviceConstant.xdir)));

		systemService.setSetting(settingItem);

	}

	// 預先下載朋友成績清單 和 排行榜清單
//	private void getFriendList() {
//
//		dateFormat.applyPattern("yyyyMM");
//		// 排行榜(月)
//		String url0 = "http://" + ConstantS.SERVER_ADDRESS
//				+ "/getLeaderboard.php?PeriodType=" + "PlayMonth"
//				+ "&Parameter=" + dateFormat.format(date);
//
//		getFriendListTask0 = new GetLeaderboardListTaskUtils(
//				getApplicationContext()).execute(url0, "排行榜(月)",
//				"setting_leaderboard_week_download_ok", "true");
//
//		dateFormat.applyPattern("yyyyMMw");
//		// 排行榜(週)
//		String url3 = "http://" + ConstantS.SERVER_ADDRESS
//				+ "/getLeaderboard.php?PeriodType=" + "PlayWeek"
//				+ "&Parameter=" + dateFormat.format(date);
//
//		getFriendListTask1 = new GetLeaderboardListTaskUtils(
//				getApplicationContext()).execute(url3, "排行榜(週)",
//				"setting_leaderboard_week_download_ok", "false");
//
//		// getFriendListTask = new
//		// GetFriendListTask().execute(url0,url1,url2,url3,url4,url5);
//		// getFriendListTask = new
//		// GetFriendListTaskUtils(getApplicationContext()).execute(url0,url1,url2,url3,url4,url5);
//
//	}

//	private void downloadScoreLevel() {
//
//		String url = "http://" + ConstantS.SERVER_ADDRESS
//				+ "/getScoreLevels.php";
//		new AsyncTaskUtils_No_Dialog(RegisterView.this) {
//
//			@Override
//			public void refreshUI(String jsonString) {
//
//				try {
//					if (jsonString != null && !jsonString.equals("TIME_OUT")) {
//						JSONArray result = new JSONArray(jsonString);
//						List<UserLevelItem> scoreLevel = new ArrayList<UserLevelItem>();
//
//						Score score = new Score();
//						int allTotalScore = score.allTotalScore(user
//								.getUserPlayRecords());
//						int currentLV = 0;
//						if (result != null) {
//
//							for (int i = 0; i < result.length(); i++) {
//
//								UserLevelItem item = new UserLevelItem();
//								item.setEXP(result.getInt(i));
//								if (i == 0) {
//									item.setLV(1);
//								} else {
//									item.setLV(i);
//								}
//								scoreLevel.add(item);
//							}
//
//							// for (int i=0;i<result.length();i++){
//							// allTotalScore = allTotalScore - result.getInt(i);
//							// if(allTotalScore <= 0){
//							// currentLV = i;
//							// break;
//							// }
//							// }
//							// scoreLevel.get(0).setLV(currentLV);
//						}
//
//						mainService.updateScoreLevel(scoreLevel);
//					} else {
//
//						if (jsonString != null && jsonString.equals("TIME_OUT")) {
//							Log.d("voin",
//									"download score level error : time out");
//						} else {
//							Log.d("voin",
//									"download score level error : jsonstring = null");
//						}
//					}
//
//				} catch (Exception e) {
//					Log.d("voin", "download score level error");
//				}
//
//			}
//		}.execute(url);
//
//	}

}
