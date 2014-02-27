package com.gxq.login;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.gxq.db.TaskTableDb;
import com.gxq.db.TasksInfo.Task;
import com.gxq.utils.NetworkUtils;
import com.gxq.utils.Strings;
import com.gxq.utils.ToastShow;
import com.gxq.utils.UserPreference;
import com.gxq.utils.WellDoneApp;
import com.gxq.welldone.MainActivity;
import com.gxq.welldone.R;
import com.qihoo.renkj.todolist.DatabaseOperate;

public class LogInActivity extends Activity {

	private static final String TAG = "LogInActivity";

	private Context mContext;
	private EditText mEditTextUsername;
	private EditText mEditTextPassword;
	private Button mButtonLogin;
	private String mUsername;
	private String mPassword;
	private int mUserId;

	private ScrollView mScrollView;

	private boolean isUserValid;

	private TaskTableDb mDbHelper = new TaskTableDb(WellDoneApp.getInstance());

	private ToastShow mToastShow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_xml);
		mContext = this;

		mToastShow = new ToastShow(mContext);

		mScrollView = (ScrollView) findViewById(R.id.loginscrollview);
		mScrollView.setVerticalScrollBarEnabled(false);
		mScrollView.setScrollbarFadingEnabled(true);

		mEditTextUsername = (EditText) findViewById(R.id.editText1);
		mEditTextUsername.addTextChangedListener(mTextWatcher);
		mEditTextPassword = (EditText) findViewById(R.id.editText2);
		mEditTextPassword.addTextChangedListener(mTextWatcher1);

		// 如果之前登录过的话，自动的载入用户名和密码哦
		mUsername = UserPreference.getName();
		Log.d(TAG, " on create UserPreference.getName():" + mUsername);

		if (mUsername != null) {
			Log.d(TAG, "mUsername != null");
			mEditTextUsername.setText(mUsername);
			mEditTextUsername.setSelection(mUsername.length());
			mPassword = UserPreference.getPassword();
			mEditTextPassword.setText(mPassword);
		}

		mButtonLogin = (Button) findViewById(R.id.imageButton1);

		mButtonLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mUsername = mEditTextUsername.getText().toString();
				Log.d(TAG, "onClick mUsername:" + mUsername);
				mPassword = mEditTextPassword.getText().toString();

				// if (NetworkUtils.isConnectInternet(mContext)) {
				if (NetworkUtils.isConnectWifi(mContext)) {
					// TextUtils.isEmpty(str)
					if (mUsername == null || mPassword == null
							|| mUsername.equals("") || mPassword.equals("")) {

						mToastShow.toastShow("用户名或密码不能为空！");

						// Toast.makeText(mContext, "用户名或密码不能为空！",
						// Toast.LENGTH_SHORT).show();

					} else {
						new LoginTask().execute("login");
					}
				} else if (hasEverLogin()) {
					Log.d(TAG, "is not ConnectInternet and hasEverLogin");
					// 如果没有网络
					// 检查是不是曾经登陆过
					// true
					mToastShow.toastShow("网络状况不佳，您将进入离线模式！");
					// Toast.makeText(mContext, "网络状况不佳，您将进入离线模式！",
					// Toast.LENGTH_SHORT).show();

					Intent intent = new Intent(mContext, MainActivity.class);
					intent.putExtra(Task.UserId, mUserId);
					intent.putExtra(Strings.NETWORKVALID, false);
					startActivity(intent);
				} else {
					// false
					// mToastShow.toastShow("网络状况不佳，请检查网络连接！");
					// Toast.makeText(mContext, "网络状况不佳，请检查网络连接！",
					// Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private boolean hasEverLogin() {
		boolean result;
		if (mUsername == null || mPassword == null || mUsername.equals("")
				|| mPassword.equals("")) {
			result = false;
			mToastShow.toastShow("用户名或密码不能为空！");
			// Toast.makeText(mContext, "用户名或密码不能为空！",
			// Toast.LENGTH_SHORT).show();
		} else {
			// 检查是否登录过，如果是，则支持离线模式，否则不支持

			result = UserPreference.hasUserLoginBefore(mUsername, mPassword);
			if (!result) {
				// 登录失败
				if (TextUtils.isEmpty(UserPreference.getName())) {
					mToastShow.toastShow("网络状况不佳，请检查网络连接！");
				} else {
					mToastShow.toastShow("用户名或密码错误！");
				}
			}
			Log.d(TAG, "hasEverLogin:" + result);
		}
//		return result;
		return true;
	}

	private boolean checkUserValid(String name, String password)
			throws JSONException {

		Log.d(TAG, "isConnectInternet");
		// 如果有网络，
		// 提交至服务端，查询用户名密码是否正确
		JSONObject parent = new JSONObject();

		parent.put("UserName", mUsername);

		byte[] s = Base64.encode(mPassword.getBytes(), -1);
		Log.d("mima", new String(s));
		parent.put("Password", new String(s));

		// parent.put("Password", mPassword);

		JSONObject result = DatabaseOperate.login(parent);

		Log.d(TAG, "login result: " + result.toString());

		// // 先来判断是不是连接超时了
		// boolean timeout = result.getBoolean(Strings.TIMEOUT);
		// if (timeout) {
		//
		// LogInActivity.this.runOnUiThread(new Runnable() {
		//
		// @Override
		// public void run() {
		// Toast.makeText(mContext, "网络状态不佳，请稍后重试！",
		// Toast.LENGTH_SHORT).show();
		// }
		// });
		//
		// return false;
		// } else {
		boolean success = result.getBoolean("success");

		if (success) {
			// true
			Log.d(TAG, "success");
			Intent intent = new Intent(mContext, MainActivity.class);
			intent.putExtra(Strings.NETWORKVALID, true);

			mUserId = result.getInt(Task.UserId);
			intent.putExtra(Task.UserId, mUserId);

			UserPreference.setUserInfo(mUsername, mPassword, mUserId);
			startActivity(intent);
			return true;
		} else {
			// false
			Log.d(TAG, "false");
			LogInActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// Toast.makeText(mContext, "用户名或密码错误！", Toast.LENGTH_SHORT)
					// .show();
					mToastShow.toastShow("用户名或密码错误！");

				}
			});
			return false;
		}
		// }
	}

	class LoginTask extends AsyncTask<String, Integer, String> {

		ProgressDialog progressDialog;

		public LoginTask() {
			progressDialog = new ProgressDialog(mContext);
		}

		@Override
		protected void onPreExecute() {
			progressDialog.setMessage("登录中...");
			progressDialog.setCancelable(false);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			Log.d(TAG, "enter doInBackground");
			// long id = Thread.currentThread().getId();
			// Log.d(TAG, "thread id : " + id);
			try {
				checkUserValid(mUsername, mPassword);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
		}

	}

	private TextWatcher mTextWatcher = new TextWatcher() {

		private CharSequence temp;
		private int editStart;
		private int editEnd;
		private int max = 30;

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			temp = s;

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (s.length() > max) {
				mToastShow.toastShow("您的用户名超长了哦");
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			// editStart = mEditTextUsername.getSelectionStart();
			// editEnd = mEditTextUsername.getSelectionEnd();
			// Log.e("editStart", editStart + "");
			// Log.e("editEnd", editEnd + "");
			//
			// if (temp.length() > max) {
			// Toast.makeText(mContext, "您的用户名超长了哦", Toast.LENGTH_SHORT)
			// .show();
			// s.delete(editStart - 1, editEnd);
			// int tempSelection = editStart;
			// mEditTextUsername.setText(s);
			// mEditTextUsername.setSelection(tempSelection);
			// }
		}

	};

	private TextWatcher mTextWatcher1 = new TextWatcher() {

		private CharSequence temp;
		private int editStart;
		private int editEnd;
		private int max = 35;

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			temp = s;

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (s.length() > max) {
				mToastShow.toastShow("密码这么长，是在测试么。。");
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			// editStart = mEditTextPassword.getSelectionStart();
			// editEnd = mEditTextPassword.getSelectionEnd();
			// Log.e("editStart", editStart + "");
			// Log.e("editEnd", editEnd + "");
			//
			// if (temp.length() > max) {
			// Toast.makeText(mContext, "密码这么长，是在测试么。。", Toast.LENGTH_SHORT)
			// .show();
			// s.delete(editStart - 1, editEnd);
			// int tempSelection = editStart;
			// mEditTextPassword.setText(s);
			// mEditTextPassword.setSelection(tempSelection);
			// }
		}

	};
}
