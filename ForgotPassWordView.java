package com.mdbiomedical.app.vion.vian_health.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mdbiomedical.app.vion.vian_health.R;
import com.mdbiomedical.app.vion.vian_health.service.AuthorityService;
import com.mdbiomedical.app.vion.vian_health.service.model.ConstantS;
import com.mdbiomedical.app.vion.vian_health.service.model.UsersData;
import com.mdbiomedical.app.vion.vian_health.util.ChangeView;
import com.mdbiomedical.app.vion.vian_health.util.DeviceConstant;
import com.mdbiomedical.app.vion.vian_health.util.HttpUtils;


public class ForgotPassWordView extends Activity {
	String email;
	EditText et_forgot_email_keyin;
	LinearLayout ll_forgot_pw_back;
	LinearLayout ll_forgot_password_send_btn;
	final String STATUS_SUCCESS = "Success";
	final String STATUS_FAIL = "Failed";
	Boolean pressBackEnable = true;
	AuthorityService authorityService = new AuthorityService();
	public static DisplayMetrics dm = new DisplayMetrics();
	com.mdbiomedical.app.vion.vian_health.service.model.UsersData userData=null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgot_pw_activity);

		getWindowManager().getDefaultDisplay().getMetrics(dm);
		DeviceConstant.screenWidth = dm.widthPixels;
		DeviceConstant.screenHeight = dm.heightPixels;
		DeviceConstant.screenDPI = dm.densityDpi;
		
		
		TextView tv_forgot_pw_title = (TextView) findViewById(R.id.tv_forgot_pw_title);
		ll_forgot_pw_back = (LinearLayout) findViewById(R.id.ll_forgot_pw_back);
		ll_forgot_password_send_btn = (LinearLayout) findViewById(R.id.ll_forgot_password_semd_btn);
		et_forgot_email_keyin = (EditText) findViewById(R.id.et_forgot_email_keyin);

		tv_forgot_pw_title.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				(int) (DeviceConstant.screenHeight * 0.03f));

		// 回上一頁
		ll_forgot_pw_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				try {
					if (pressBackEnable) {
						ChangeView.onBack();
					}
				} catch (Exception e) {
					finish();
				}

			}
		});

		// 忘記密碼
		ll_forgot_password_send_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				email = et_forgot_email_keyin.getText().toString();
				if (email.equals("")) {
					Toast.makeText(
							ForgotPassWordView.this,
							getString(R.string.tv_account_email)
									+ getString(R.string.is_empty),
							Toast.LENGTH_SHORT).show();
				} else if (email.contains(" ")) {
					Toast.makeText(
							ForgotPassWordView.this,
							getString(R.string.tv_account_email)
									+ getString(R.string.format_error),
							Toast.LENGTH_SHORT).show();
				} else if (!email.contains("@")) {
					Toast.makeText(
							ForgotPassWordView.this,
							getString(R.string.tv_account_email)
									+ getString(R.string.format_error),
							Toast.LENGTH_SHORT).show();
				} else {
					new sendmailAsyncTask().execute(email);
				}

			}

		});

	}

	@Override
	public void onBackPressed() {
		if (pressBackEnable) {
			super.onBackPressed();
		}
	}

	public class sendmailAsyncTask extends AsyncTask<String, Void, String> {
		private ProgressDialog mdialog;

		@Override
		protected void onPreExecute() {
			pressBackEnable = false;
			mdialog = new ProgressDialog(ForgotPassWordView.this);
			// mdialog.setTitle("請稍候");
			mdialog.setMessage("connecting...");
			mdialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {

			String url = "http://" + ConstantS.SERVER_ADDRESS
					+ "/resetPassword.php?UserEmail="
					+ params[0].replaceAll(" ", "+");

			try {
				Log.d("sandy", "doInBackground start!");
				String jsonString = HttpUtils.getData(ForgotPassWordView.this,url);
				String result;
				//Log.d("sandy", "137orignal_"+jsonString);
				//下兩行解決jsonparseException
				jsonString=jsonString.substring(2, jsonString.length()-1);
				jsonString = "{"+jsonString+"}";
				//
				Log.d("sandy", "137_"+jsonString);
				if (jsonString != null) {
					//Log.d("sandy", "139_connect success,equal="+(!jsonString.equals("TIME_OUT")));
					if (!jsonString.equals("TIME_OUT")) {
						ObjectMapper mapper = new ObjectMapper();
						//Log.d("sandy", "142");
						//result= authorityService.returnResult(jsonString);
						//Log.d("sandy", "145_"+result);
						UsersData userData = mapper.readValue(jsonString,UsersData.class);
						Log.d("sandy", "145====_"+(userData.getStatus()));
						//if (result==STATUS_SUCCESS) {
						if (userData.getStatus().equals(STATUS_SUCCESS)) {
							Log.d("sandy", "147_"+getString(R.string.new_password_sended));
							return getString(R.string.new_password_sended);
						} else if (userData.getStatus().equals(STATUS_FAIL)) {
					//} else if (result==STATUS_FAIL) {
							return userData.getReason();
						} else {
							return null;
						}
					} else {
						return getString(R.string.internet_slow);
					}

				} else {
					return null;
				}

			} catch (Exception e) {
				Log.e("vion", e.getMessage());
				return null;
			}
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.d("sandy", "171_"+result);
			if (result != null) {
				Toast.makeText(ForgotPassWordView.this, result,
						Toast.LENGTH_SHORT).show();			
					try {
						ChangeView.onBack();
					} catch (Exception e) {
						finish();
					}
				
			} else {
				Toast.makeText(ForgotPassWordView.this,
						getString(R.string.unable_connect), Toast.LENGTH_SHORT)
						.show();
			}

			mdialog.dismiss();
			pressBackEnable = true;

		}

		protected void onCancelled() {
			mdialog.dismiss();
			super.onCancelled();
		}

	}

}
