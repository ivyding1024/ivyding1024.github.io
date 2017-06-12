package com.mdbiomedical.app.vion.vian_health.view;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

import com.mdbiomedical.app.vion.vian_health.MainActivity;
import com.mdbiomedical.app.vion.vian_health.R;
import com.mdbiomedical.app.vion.vian_health.helper.DatabaseHelper;
import com.mdbiomedical.app.vion.vian_health.model.SettingItem;
import com.mdbiomedical.app.vion.vian_health.model.User;
import com.mdbiomedical.app.vion.vian_health.service.AuthorityService;
import com.mdbiomedical.app.vion.vian_health.service.MainService;
import com.mdbiomedical.app.vion.vian_health.service.SystemService;
import com.mdbiomedical.app.vion.vian_health.service.model.ConstantS;
import com.mdbiomedical.app.vion.vian_health.service.model.UserDataItem;
import com.mdbiomedical.app.vion.vian_health.util.ChangeView;
import com.mdbiomedical.app.vion.vian_health.util.DeviceConstant;
import com.mdbiomedical.app.vion.vian_health.util.HttpUtils;
import com.mdbiomedical.app.vion.vian_health.view.ChangePassword.ChangePasswordAsyncTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.text.Spanned; 
public class UserInformationView extends Activity {
	WheelView year, month, day;
	EditText firstNameText ;
	EditText lastNameText;
	TextView genderText;
	TextView birthDateText ;
	TextView heightText;
	TextView weightText ;
	TextView bmiText ;
	int gender_order=0;
	DatabaseHelper databaseHelper;
	SharedPreferences settings ;
	int number_A, number_B;
	
	LinearLayout ll_ana_sel_date;
	int lastYear;
	Calendar calendar;
	
	//0426
	LinearLayout ll_change_password;
	User user ;
	MainService mainService;
	TextView tv_Mail;
	SystemService systemService;
	private final static String TAG ="sandy";
	AuthorityService authorityService = new AuthorityService();
	//UserDataItem userDataItem = new UserDataItem();
	
	//sandy0914
		public static DisplayMetrics dm = new DisplayMetrics();
	String dateString = DateFormat.getBestDateTimePattern(java.util.Locale.getDefault(), "yyyy/MM/dd ");
	java.text.DateFormat dateTimeInstance = new SimpleDateFormat(dateString, java.util.Locale.getDefault());
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);//鍵盤顯示問題_0428
		setContentView(R.layout.user_information);
		
		//sandy0914
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		DeviceConstant.screenWidth = dm.widthPixels;
		DeviceConstant.screenHeight = dm.heightPixels;
		DeviceConstant.screenDPI = dm.densityDpi;
		
		
		
		settings = getSharedPreferences("UserInformation", 0);
		calendar = Calendar.getInstance();;
		long l = settings.getLong("BIRTHDATE", calendar.getTime().getTime());
		Date d = new Date();
		d.setTime(l);
		calendar.setTime(d);
		int tempYear =calendar.get(Calendar.YEAR);
		int tempMonth=calendar.get(Calendar.MONTH);
		int tempday=calendar.get(Calendar.DAY_OF_MONTH)-1;
		Log.e("tempday", tempday+"");
		
		firstNameText= (EditText) findViewById(R.id.firstNameText);
		lastNameText = (EditText) findViewById(R.id.lastNameText);
		genderText = (TextView) findViewById(R.id.genderText);
		birthDateText = (TextView) findViewById(R.id.birthDateText);
		heightText = (TextView) findViewById(R.id.heightText);
		weightText = (TextView) findViewById(R.id.weightText);
		bmiText = (TextView) findViewById(R.id.bmiText);
		databaseHelper = new DatabaseHelper(this);
		settings = getSharedPreferences("UserInformation", 0);
		ll_ana_sel_date = (LinearLayout) findViewById(R.id.ll_ana_sel_date);
		
	//update	
		ll_change_password=(LinearLayout) findViewById(R.id.ll_change_password);//0426
		mainService = new MainService(this.getApplicationContext());
		user = mainService.loadUserDataItem();//0427
		//systemService = new SystemService(this.getApplicationContext());
		tv_Mail= (TextView) findViewById(R.id.tv_Mail);		
		//Log.d("sandy", "tv_Mail="+user.getFirstName());
		Log.d("sandy", "Password="+user.getPassword());
		tv_Mail.setText(user.getEmail());
		
		
		
// loading server data 			
		//Log.d("sandy", "139_name="+user.getFirstName());
		settings = getSharedPreferences("UserInformation", 0);
		settings.edit().putString("FIRST_NAME",user.getFirstName()).commit();
		Log.d("sandy", "139_name="+user.getFirstName());
		settings.edit().putString("LAST_NAME",user.getLastName()).commit();
		settings.edit().putInt("GENDER",user.getGender()).commit();
		settings.edit().putString("HEIGHT",user.getHeight()).commit();
		settings.edit().putString("WEIGHT",user.getWeight()).commit();
		birthDateText.setText(user.getUserBirthDate());
		//settings.edit().putLong("BIRTHDATE", user.getUserBirthDate()).commit();
		//	
		
		
		
		calendar = Calendar.getInstance();
		TextView tv_list_title = (TextView) findViewById(R.id.tv_list_title);
		tv_list_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (DeviceConstant.screenHeight * 0.03f));
		//long l = settings.getLong("BIRTHDATE", calendar.getTime().getTime());
		long temp = calendar.getTime().getTime();
		Date tempdate = new Date();
		tempdate.setTime(temp);
		calendar.setTime(tempdate);
		int curYear = calendar.get(Calendar.YEAR);
		
		LinearLayout ll_list_back;
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(firstNameText.getWindowToken(), 0);
		
		ll_list_back = (LinearLayout) findViewById(R.id.ll_list_back);
		// 回上一頁
		ll_list_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				try {
					updateData();
					
					
					
					
				} catch (Exception e) {
					finish();
				}

			}
		});
		
		LinearLayout ll_logOut=(LinearLayout) findViewById(R.id.ll_logOut);
		ll_logOut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showMessage(UserInformationView.this, R.string.logout, R.string.logout_message);

//				databaseHelper.logout();
//				Log.d("sandy", "log out click");
//				saveSetting();
//				
//				ChangeView.ChangeActivity(UserInformationView.this, MainActivity.class);
//				finish();
			}
		});
		
		
		ll_change_password.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				ChangeView.ChangeActivity(UserInformationView.this, ChangePassword.class);

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
				String months[] = new String[12] ;
				for(int x=0;x<12;x++)
				{
					months[x]=getMonth(x);
				}
				month.setViewAdapter(new DateArrayAdapter(this, months, curMonth));
				month.setCurrentItem(tempMonth);
				month.addChangingListener(listener);
				month.setCyclic(true);

				final int iYearCount = 100;
				// year
				lastYear = curYear;
				year.setViewAdapter(new DateNumericAdapter(this, curYear - iYearCount, curYear, 0));
				Log.d("alex","curYear="+curYear);
				year.setCurrentItem(tempYear-(curYear - iYearCount));
				year.addChangingListener(listener);

				// day
				updateDays(year, month, day);
				day.setCurrentItem(tempday);
				day.setCyclic(true);
//		firstNameText.setOnFocusChangeListener(new OnFocusChangeListener() {
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				if (hasFocus) {
//
//					firstNameText.setCursorVisible(true);
//				}
//			}
//		});
				InputFilter filter = new InputFilter() {
				    @Override
				    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
				        for (int i = start; i < end; i++) {
				            //int type = Character.getType(src.charAt(i));
				            //System.out.println("Type : " + type);
				            //if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
				            //    return "";
				            //}
							if(!Character.isLetter(source.charAt(i)))
							{
								return "";
							}
				        }
				        return null;
				    }
				};
		firstNameText.setFilters(new InputFilter[]{filter});
		firstNameText.addTextChangedListener(new TextWatcher() {
			String lastdata = "";

			@Override
			public void onTextChanged(CharSequence text, int start, int before, int count) {
				try {
					if (firstNameText.getLineCount() > 1||firstNameText.length()>15) {
						firstNameText.setText(lastdata);
						firstNameText.setSelection(firstNameText.length());
						Log.d("sandy"," 262_lastdata="+lastdata);
					} else {
						
						lastdata = text.toString();
						
						Log.d("sandy"," CC lastdata="+lastdata);
//						if(lastdata==null)
//						{
//							firstNameText.setText(user.getFirstName());
//							lastdata=user.getFirstName();
//						}
//						Log.d("sandy"," 272 lastdata="+lastdata);
						settings = getSharedPreferences("UserInformation", 0);
						settings.edit().putString("FIRST_NAME",lastdata).commit();
						
						
						//user.setFirstName(lastdata);
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
		
		lastNameText.setFilters(new InputFilter[]{filter});
		lastNameText.addTextChangedListener(new TextWatcher() {
			String lastdata = "";

			@Override
			public void onTextChanged(CharSequence text, int start, int before, int count) {
				try {
					if (lastNameText.getLineCount() > 1||lastNameText.length()>15) {
						lastNameText.setText(lastdata);
						lastNameText.setSelection(lastNameText.length());
					} else {
						lastdata = text.toString();
						settings = getSharedPreferences("UserInformation", 0);
						settings.edit().putString("LAST_NAME",lastdata).commit();
						
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
		
//		heightText.addTextChangedListener(new TextWatcher() {
//			String lastdata = "";
//
//			@Override
//			public void onTextChanged(CharSequence text, int start, int before, int count) {
//				try {
//					if (heightText.getLineCount() > 1) {
//						heightText.setText(lastdata);
//						heightText.setSelection(heightText.length());
//					} else {
//						lastdata = text.toString();
//						settings = getSharedPreferences("UserInformation", 0);
//						settings.edit().putString("HEIGHT",lastdata).commit();
//						setBMI();
//					}
//
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//				// TODO Auto-generated method stub
//				lastdata = s.toString();
//
//			}
//
//			@Override
//			public void afterTextChanged(Editable s) {
//				// TODO Auto-generated method stub
//
//			}
//		});
//		weightText.addTextChangedListener(new TextWatcher() {
//			String lastdata = "";
//
//			@Override
//			public void onTextChanged(CharSequence text, int start, int before, int count) {
//				try {
//					if (weightText.getLineCount() > 1) {
//						weightText.setText(lastdata);
//						weightText.setSelection(weightText.length());
//					} else {
//						lastdata = text.toString();
//						settings = getSharedPreferences("UserInformation", 0);
//						settings.edit().putString("WEIGHT",lastdata).commit();
//						setBMI();
//						
//					}
//
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//				// TODO Auto-generated method stub
//				lastdata = s.toString();
//
//			}
//
//			@Override
//			public void afterTextChanged(Editable s) {
//				// TODO Auto-generated method stub
//
//			}
//		});
	}
	public void showMessage(Context context, int title, int msg) {
		new AlertDialog.Builder(context).setTitle(title).setMessage(msg).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface df, int i) {
				databaseHelper.logout();
				Log.d("sandy", "log out click");
				saveSetting();
				
				//ChangeView.ChangeActivity(UserInformationView.this, MainActivity.class);
				//finish();
				Intent intent = new Intent(UserInformationView.this, MainActivity.class);
				 intent.putExtra("finish", true);
				 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//To clean up all activities
				 startActivity(intent);
				 finish();
//				Intent intent3 = new Intent(UserInformationView.this, MainActivity.class);
//				startActivityForResult(intent3, 3);
//				Intent broadcastIntent = new Intent();
//				broadcastIntent.setAction("com.package.ACTION_LOGOUT");
//				sendBroadcast(broadcastIntent);

			}
		}).setNegativeButton(R.string.cancel,  new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface df, int i) {

			}
		}).show();
		//.show();
		

	}
	public void saveSetting() {
		Resources r = this.getResources();
		systemService = new SystemService(this);

		List<SettingItem> settingItem = new ArrayList<SettingItem>();

		settingItem.add(new SettingItem(r.getResourceEntryName(R.string.setting_is_real_register), "true"));
		settingItem.add(new SettingItem(r.getResourceEntryName(R.string.setting_is_new_game), "true"));
		//settingItem.add(new SettingItem(r.getResourceEntryName(R.string.setting_join_leaderboard), String.valueOf(user.getIsJoinLeaderboard())));
		settingItem.add(new SettingItem(r.getResourceEntryName(R.string.setting_statusbar_height), String.valueOf(DeviceConstant.statusBarHeight)));
		settingItem.add(new SettingItem(r.getResourceEntryName(R.string.setting_xdir), String.valueOf(DeviceConstant.xdir)));

		systemService.setSetting(settingItem);

	}

	public void updateData()
	{
		
		UserDataItem userDataItem = new UserDataItem();

		userDataItem.UserEmail = user.getEmail();
		userDataItem.UserFirstName = firstNameText.getText().toString();
		userDataItem.UserLastName =lastNameText.getText().toString();
		userDataItem.UserHeight = heightText.getText().toString();
		userDataItem.UserWeight = weightText.getText().toString();
		userDataItem.UserNationality = user.getNationality();
		userDataItem.UserGender = gender_order;
		userDataItem.UserName = user.getUserName();
		userDataItem.UserPassword = user.getPassword();
		userDataItem.UserIsJoinLeaderboard = user
				.getIsJoinLeaderboard();
		userDataItem.UserId = user.getId();
		userDataItem.UserFacebookId = user.getFacebookId();
		userDataItem.UserBirthDate=birthDateText.getText().toString();
		mainService.updateUserDataItem(userDataItem);
		
		String url1 = "http://"
				+ ConstantS.SERVER_ADDRESS
				+ "/updateECGUser.php?"
				+ "UserEmail="+ user.getEmail().replaceAll(" ","+")
				+ "&UserHeight="+ heightText.getText().toString()
				+ "&UserWeight="+ weightText.getText().toString()
				+ "&UserNationality="+ user.getNationality().replaceAll(" ","+")
				+ "&UserIsJoinLeaderboard="+ user.getIsJoinLeaderboard()
				+ "&UserGender="+ gender_order
				//+ "&UserPassword="+ user.getPassword().replaceAll(" ","+")
				+ "&UserFirstName="+ firstNameText.getText().toString().replaceAll(" ","+")
				+ "&UserLastName="+ lastNameText.getText().toString().replaceAll(" ","+")
				+ "&UserName="+ user.getUserName().replaceAll(" ","+")
				+"&UserBirthDate="+birthDateText.getText().toString();
		Log.d("sandy", "UpdateUser_url=" + url1);
		// change password
		new UserInformationViewAsyncTask().execute(url1);
	}
	public class UserInformationViewAsyncTask extends AsyncTask<String, Void, String> {
		private ProgressDialog mdialog;

		@Override
		protected void onPreExecute() {
			Log.d("sandy", "Dialog start!");
			mdialog = new ProgressDialog(UserInformationView.this);
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
				String jsonString = HttpUtils.getData(UserInformationView.this, params[0]);
				Log.d(TAG, "updateUser_jdonString="+jsonString);
				if (jsonString != null) {

					if (!jsonString.equals("TIME_OUT")) {

						boolean is_updateUser = authorityService.updateUser(jsonString);
						Log.d(TAG,"291....is_updateUser="+ is_updateUser);
						if (is_updateUser) {
							Log.e(TAG, "updateUser success");
							result = "success";
							return result;
						} else {
							Log.e(TAG, "updateUser fail");
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
				//signinFlag = true;
			} else if (result.equals("success")) {
				mdialog.dismiss();
				setResult(RESULT_OK);
				finish();
				overridePendingTransition(R.anim.slide_no, R.anim.slide_out_left);
				//showMessage(ChangePassword.this, R.string.ll_change_password_title, R.string.change_password_success);
				//Toast.makeText(ChangePassword.this, "change password success", Toast.LENGTH_LONG).show();
				//Intent intent3 = new Intent(ChangePassword.this, HomeView.class);
				//startActivityForResult(intent3, 3);
			} else if (result.equals("fail")) {
				mdialog.dismiss();
				// Toast.makeText(AccountSignInView.this,"Login Failed!!",
				// Toast.LENGTH_LONG).show();
				Toast.makeText(getApplicationContext(), "change password  Failed:" , Toast.LENGTH_SHORT).show();
				//signinFlag = true;

			}
		}

		protected void onCancelled() {
			//signinFlag = true;
			mdialog.dismiss();
			super.onCancelled();
		}

	}
	// switch嚙踝蕭嚙踝蕭
	public void clickSwitch(View v) {

		openPopupWindow(v);

		}
	
	public String getMonth(int month) {
	    return new DateFormatSymbols().getMonths()[month];
	}
	@Override
	public void onBackPressed() {
		updateData();

//		setResult(RESULT_OK);
//		finish();
//		overridePendingTransition(R.anim.slide_no, R.anim.slide_out_left);
	}

	@Override
	public void onResume() {
		// TODO LC: preliminary support for views transitions
		this.overridePendingTransition(R.anim.left_in, R.anim.left_out);
		super.onResume();
		HomeView.home_pressed = "disable";
		settings = getSharedPreferences("UserInformation", 0);
		String lastdata= settings.getString("FIRST_NAME", "");
		firstNameText.setText(lastdata);
		Log.e("sandy", "540_firstname="+lastdata);
		
		lastdata= settings.getString("LAST_NAME", "");
		lastNameText.setText(lastdata);
		//Log.e("sandy", "settings");
		
		gender_order = settings.getInt("GENDER", 0);
		Log.e("GENDER", "gender_order = "+gender_order);
		if(gender_order==0)
			genderText.setText(getString(R.string.Male));
		else
			genderText.setText(getString(R.string.Female));
			
		
		lastdata= settings.getString("HEIGHT", "170.0");
		heightText.setText(lastdata);
		lastdata= settings.getString("WEIGHT", "65.0");
		weightText.setText(lastdata);
		long l = settings.getLong("BIRTHDATE", calendar.getTime().getTime());
		//Log.d("sandy", "birthday="+l);
		Date d = new Date();
		d.setTime(l);
		Calendar calendar2 = Calendar.getInstance();;
		calendar2.setTime(d);
		//birthDateText.setText(dateTimeInstance.format(calendar2.getTime()));
		setBMI();

		
		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d("alex", "on Pause");
		HomeView.home_pressed = "wait";
	}
	
	public void onFirstName(View v) {

		
	}
	public void onLastName(View v) {
		hideKeyboard();
		
	}
	
	private void openPopupWindow(View v){//Lidl 變更3.

		LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
		View popupView = layoutInflater.inflate(R.layout.number_height_picker_popup_activity, null);
		final PopupWindow popupWindow = new PopupWindow(popupView,

		LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		popupWindow.setOutsideTouchable(false);
		LinearLayout ll_number_picker_done = (LinearLayout) popupView.findViewById(R.id.ll_number_picker_done);
		// setTheme(R.style.SampleTheme_Light);

		final TextView tv_title = (TextView) popupView.findViewById(R.id.tv_title);
		TextView tv_unit = (TextView) popupView.findViewById(R.id.tv_unit);
		LinearLayout ll_number_picker_cancel = (LinearLayout) popupView.findViewById(R.id.ll_number_picker_cancel);
		NumberPicker numberPicker1 = (NumberPicker) popupView.findViewById(R.id.numberPicker1);
		NumberPicker numberPicker2 = (NumberPicker) popupView.findViewById(R.id.numberPicker2);
		numberPicker1.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		numberPicker2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

		int heightDot = heightText.getText().toString().lastIndexOf(".");
		Log.e("test","user heightDot = "+heightDot);
		String userHeightA = heightText.getText().toString().substring(0, heightDot);
		Log.e("test","user userHeightA = "+userHeightA);
		String userHeightB = heightText.getText().toString().substring(heightDot + 1);
		Log.e("test","user userHeightB = "+userHeightB);

		int weightDot = weightText.getText().toString().lastIndexOf(".");
		Log.e("test","user weightDot = "+weightDot);
		String userWeightA = weightText.getText().toString().substring(0, weightDot);
		Log.e("test","user userWeightA = "+userWeightA);
		String userWeightB = weightText.getText().toString().substring(weightDot + 1);
		Log.e("test","user userWeightB = "+userWeightB);

		switch (v.getId()) {

		case R.id.ll_register_height:
			tv_title.setText(getString(R.string.height));
			tv_unit.setText("cm");
			numberPicker1.setMaxValue(220);
			numberPicker1.setMinValue(40);
			numberPicker1.setValue(Integer.parseInt(userHeightA));
			numberPicker2.setMaxValue(9);
			numberPicker2.setMinValue(0);
			numberPicker2.setValue(Integer.parseInt(userHeightB));
			number_A = Integer.parseInt(userHeightA);
			number_B = Integer.parseInt(userHeightB);
			break;
		case R.id.ll_register_weight:
			tv_title.setText(getString(R.string.weight));
			tv_unit.setText("kg");

			numberPicker1.setMaxValue(220);
			numberPicker1.setMinValue(10);
			numberPicker1.setValue(Integer.parseInt(userWeightA));
			numberPicker2.setMaxValue(9);
			numberPicker2.setMinValue(0);
			numberPicker2.setValue(Integer.parseInt(userWeightB));
			number_A = Integer.parseInt(userWeightA);
			number_B = Integer.parseInt(userWeightB);

			break;

		}

		numberPicker1.setFocusable(true);
		numberPicker1.setFocusableInTouchMode(false);

		numberPicker2.setFocusable(true);
		numberPicker2.setFocusableInTouchMode(true);

		numberPicker1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker view, int oldValue, int newValue) {
                number_A = newValue;

            }
        });

		numberPicker2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker view, int oldValue, int newValue) {
                number_B = newValue;

            }
        });

		ll_number_picker_done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String titleName = tv_title.getText().toString();
				String value = String.valueOf(number_A + "." + number_B);
				float value_float = Float.parseFloat(value);

				if (titleName.equals(getString(R.string.height))) {
					heightText.setText(value);
					settings = getSharedPreferences("UserInformation", 0);
					settings.edit().putString("HEIGHT",heightText.getText().toString()).commit();
					setBMI();


				}
				if (titleName.equals(getString(R.string.weight))) {
					weightText.setText(value);
					settings = getSharedPreferences("UserInformation", 0);
					settings.edit().putString("WEIGHT",weightText.getText().toString()).commit();
					setBMI();
				}


				popupWindow.dismiss();
			}
		});

        ll_number_picker_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				popupWindow.dismiss();
			}
		});


		popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
		// popupWindow.setWindowLayoutMode(getWallpaperDesiredMinimumWidth(),
		// getWallpaperDesiredMinimumWidth());
		popupWindow.setFocusable(true);
		popupWindow.update();
	}
	public void onGender(View v) {
		hideKeyboard();
		new AlertDialog.Builder(this).setTitle(getString(R.string.gender)).setIcon(
				android.R.drawable.ic_dialog_info).setSingleChoiceItems(
						new String[] { getString(R.string.Male),getString(R.string.Female) }, gender_order,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								if(which==0)
								{
									settings = getSharedPreferences("UserInformation", 0);
									settings.edit().putInt("GENDER",0).commit();
									genderText.setText(getString(R.string.Male));
									gender_order=0;
								}
								else if(which==1)
								{
									
										settings = getSharedPreferences("UserInformation", 0);
										settings.edit().putInt("GENDER",1).commit();
										genderText.setText(getString(R.string.Female));
										gender_order=1;
									
								}
								dialog.dismiss();
								}
							}).setNegativeButton(getString(R.string.cancel), null).show();
	}
	public void onBirthDate(View v) {
		hideKeyboard();
		ll_ana_sel_date.getLayoutParams().height = 800;
		ll_ana_sel_date.requestLayout();
		
	}
	public void onHeight(View v) {
		hideKeyboard();
		
	}
	
	public void onWeight(View v) {
		hideKeyboard();
		
	}
	public void setBMI(){
		
		float height=Float.valueOf( settings.getString("HEIGHT", "170.0"))/100;
		float weight=Float.valueOf( settings.getString("WEIGHT", "65.0"));
		bmiText.setText(""+String.format("%.1f", weight/(height*height)));
	}
	public void clickCancel(View v) {
		ll_ana_sel_date.getLayoutParams().height = 0;
		ll_ana_sel_date.requestLayout();
	}

	public void clickDone(View v) {
		ll_ana_sel_date.getLayoutParams().height = 0;
		ll_ana_sel_date.requestLayout();

		calendar.set(lastYear - 100 + year.getCurrentItem(), month.getCurrentItem(), day.getCurrentItem() + 1);

		// String today = dateFormat.format(calendar.getTime()).toString();
		birthDateText.setText(dateTimeInstance.format(calendar.getTime()));

		SharedPreferences settings;
		settings = getSharedPreferences("UserInformation", 0);
		settings.edit().putLong("BIRTHDATE", calendar.getTime().getTime()).commit();

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
