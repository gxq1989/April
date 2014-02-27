package com.gxq.welldone;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gxq.db.TaskTableDb;
import com.gxq.db.TasksInfo.Task;
import com.gxq.utils.AlertInfo;
import com.gxq.utils.MyAlarmManager;
import com.gxq.utils.MyContentValues;
import com.gxq.utils.NetworkUtils;
import com.gxq.utils.Strings;
import com.gxq.utils.TimeUtils;
import com.gxq.utils.ToastShow;
import com.gxq.utils.UserPreference;
import com.qihoo.renkj.todolist.DatabaseOperate;

public class EidtPageActivity extends Activity {

	private static final String TAG = "EidtPageActivity";

	private Context mContext;

	private String mFromWhat;
	private int mRowId;

	private EditText mEditText;
	private String mTitle;

	private Switch mSwitchAlertState;
	private int mAlertState = 0;

	private Button mButtonAlertTime;

	private String mRemindTime;
	private String mEditTime;

	private Button mButtonPriority;
	private int mPriority = 1;

	private Button mButtonSave;
	private Button mButtonCancel;

	private int mIsComplete;
	private int mIsCommit;
	private int mIsRemind;
	private int mDeleteFlag;

	private TimeUtils mTimeUtils;

	private ScrollView mScrollView;
	
	private DatePicker mDatePicker;
	private TimePicker mTimePicker;

	private int mhourOfDay;
	private int mminute;
	private int myear;
	private int mmonthOfYear;
	private int mdayOfMonth;

	private boolean mIsEmpty;

	private JSONObject mJSONObject;
	private JSONArray mJSONArray;
	private boolean mFlag;

	// 插入之后的id
	private int mInsertResult;

	// public static TaskTableDb mDbHelper;

	private TaskTableDb mDbHelper = MainActivity.mDbHelper;

	// item related values
	private int mUserId = UserPreference.getUserId();
	private int mServerId;

	private final int DIALOG_DATEPICKER = 0;
	public final static int DIALOG_TIMEPICKER = 1;
	private final int DIALOG_PRIOR = 2;

	private int serverid;

	private String title;

	private String remindtime;

	private int prior;

	private int remindpattern;

	private int iscomplete;

	private String edittime;

	private int isremind;

	private int iscommit;

	private int deleteflag;

	private ToastShow mToastShow;
	
	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_save:
				// 先把记录保存至本地数据库
				// 如果是离线模式的话，还要
				// IsCommit int 提交标志 可 0：不需提交，
				// 1：离线状态下添加。
				// 2：离线状态下修改。
				// 3：离线状态下删除。

				boolean alertState = mSwitchAlertState.isChecked();

				if (alertState) {
					mAlertState = 1;
				} else {
					mAlertState = 0;
				}
				Log.d("gxq", " save  remindPattern:" + mAlertState);

				mTimeUtils = new TimeUtils();
				mEditTime = mTimeUtils.getCurrentTime();

				Log.d("test", " mEditTime: get curent" + mEditTime);

				mDbHelper = new TaskTableDb(mContext);
				ContentValues values = new ContentValues();

				mTitle = mEditText.getText().toString();

				values.put(Task.UserId, mUserId);
				values.put(Task.Title, mTitle);
				values.put(Task.RemindPattern, mAlertState);
				values.put(Task.Priority, mPriority);
				values.put(Task.IsRemind, 0);
				values.put(Task.DeleteFlag, 0);

				values.put(Task.RemindTime, mRemindTime);
				values.put(Task.EditTime, mEditTime);

				// 保存的时候要看是要创建新的记录还是更改记录的哦！！！
				if (mFromWhat.equals(Strings.TOADD)) {

					int length = mEditText.getText().toString().trim().length();
					if (length > 0
							&& length <= 140
							&& ((mAlertState == 1 && isValidAlertTime(mRemindTime)) || (mAlertState == 0))) {
						mIsEmpty = true;

						// 根据网络状态，对IsCommit字段进行填充
						// if (!NetworkUtils.isConnectInternet(mContext)) {
						if (!NetworkUtils.isConnectWifi(mContext)) {
							// if (!mTestBoolean) {
							mIsCommit = 1;
							values.put(Task.IsCommit, mIsCommit);
						}

						Log.d(TAG, "add : mIsCommit :" + mIsCommit);

						mInsertResult = (int) mDbHelper.insert(values);
						new InsertTask().execute("insert");

					} else if (length == 0) {

						mIsEmpty = false;
						mToastShow.toastShow("待办事项不能为空哦~");
//						Toast.makeText(mContext, "待办事项不能为空哦~",
//								Toast.LENGTH_SHORT).show();
					} else if (mEditText.getText().toString().length() > 140) {
						mIsEmpty = false;
						mToastShow.toastShow("不能超过140个字符！");
//						Toast.makeText(mContext, "不能超过140个字符！",
//								Toast.LENGTH_SHORT).show();
					} else if (mAlertState == 1
							&& !isValidAlertTime(mRemindTime)) {
						mIsEmpty = false;
						// if (mButtonAlertTime.getText().equals("设置时间")) {
						// Toast.makeText(mContext, "请设置提醒时间！",
						// Toast.LENGTH_SHORT).show();
						// } else {
						mToastShow.toastShow("所设置的提醒时间已过期，请重新设置！");
//						Toast.makeText(mContext, "所设置的提醒时间已过期，请重新设置！",
//								Toast.LENGTH_SHORT).show();
						// }
					} else {
						mIsEmpty = true;
					}

				} else if (mFromWhat.equals(Strings.TOEDIT)) {

					int length = mEditText.getText().toString().length();
					if (length > 0
							&& length <= 140
							&& ((mAlertState == 1 && isValidAlertTime(mRemindTime)) || (mAlertState == 0))) {
						mIsEmpty = true;

						// 根据网络状态，对IsCommit字段进行填充

						// if (!NetworkUtils.isConnectInternet(mContext)) {
						if (!NetworkUtils.isConnectWifi(mContext)) {
							// if (!mTestBoolean) {
							if (mIsCommit == 1) {
								Log.d(TAG, "edit : mIsCommit == 1 do nothing");
								// 如果之前是离线添加的，这里修改的话就不用设为2了！！
							} else {
								Log.d(TAG, "edit : mIsCommit :" + mIsCommit);
								mIsCommit = 2;
								Log.d(TAG, "edit : mIsCommit =2 !");
								values.put(Task.IsCommit, mIsCommit);
							}
						}

						Log.d("gxq", "edit update");
						mDbHelper.update(values, mRowId);

						// 在有网的情况下增量同步
						new UpdateTask().execute("udpate");

					} else if (length == 0) {

						mIsEmpty = false;
						mToastShow.toastShow("待办事项不能为空哦~");
//						Toast.makeText(mContext, "待办事项不能为空哦~",
//								Toast.LENGTH_SHORT).show();
					} else if (mEditText.getText().toString().length() > 140) {
						mIsEmpty = false;
						mToastShow.toastShow("不能超过140个字符！");
//						Toast.makeText(mContext, "不能超过140个字符！",
//								Toast.LENGTH_SHORT).show();
					} else if (mAlertState == 1
							&& !isValidAlertTime(mRemindTime)) {
						mIsEmpty = false;
						mToastShow.toastShow("所设置的提醒时间已过期，请重新设置！");
//						Toast.makeText(mContext, "所设置的提醒时间已过期，请重新设置！",
//								Toast.LENGTH_SHORT).show();
					} else {
						mIsEmpty = true;
					}

				}
				// start 看有没有提醒时间需要加入啊 有的话 要发个广播哦
				if (mAlertState == 1 && mIsComplete == 0) {// 需要提醒且没完成
					addRemindTime();
				}
				// end 看有没有提醒时间需要加入啊 有的话 要发个广播哦

				// 需要告诉list更改的是什么时间段的数据，以便于回到对应的list中去
				mTimeUtils = new TimeUtils();
				String catagory;

				try {
					catagory = mTimeUtils.getCatagory(mRemindTime, mIsComplete);
					Intent myintent = new Intent();
					myintent.putExtra(Strings.CATAGORY, catagory);
					setResult(MainActivity.EDITPAGE, myintent);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (mIsEmpty) {
					finish();
				}

				break;

			case R.id.bt_cancel:
				mTimeUtils = new TimeUtils();
				String catagory1 = null;

				try {
					catagory = mTimeUtils.getCatagory(mRemindTime, mIsComplete);
					Intent myintent = new Intent();
					myintent.putExtra(Strings.CATAGORY, catagory1);
					setResult(MainActivity.EDITPAGE, myintent);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finish();
				break;

			case R.id.bt_alerttime:
				// if (!mSwitchAlertState.isChecked()) {
				// Toast.makeText(mContext, "设置时间需要打开提醒开关！",
				// Toast.LENGTH_SHORT).show();
				// } else {
				Intent intent = new Intent(mContext, TimePickerActivity.class);
				startActivityForResult(intent, DIALOG_TIMEPICKER);
				// }
				break;

			case R.id.bt_prior:
				showDialog(DIALOG_PRIOR);
				break;
			}
		}

	};

	// private boolean mTestBoolean = false;

	// OnCheckedChangeListener mCheckedChangeListener = new
	// OnCheckedChangeListener() {
	//
	// @Override
	// public void onCheckedChanged(CompoundButton buttonView,
	// boolean isChecked) {
	// if (!isChecked) {
	// mButtonAlertTime.setText("设置时间");
	// }
	//
	// }
	//
	// };

	private TextWatcher mTextWatcher = new TextWatcher() {

		private CharSequence temp;  
        private int editStart ;  
        private int editEnd ;  
        private int max = 140;  
        
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			 temp = s;  

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if(s.length() > max){
				mToastShow.toastShow("不能超过140个字符！");
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
//			 editStart = mEditText.getSelectionStart();  
//             editEnd = mEditText.getSelectionEnd();  
//             Log.e("editStart", editStart+"");  
//             Log.e("editEnd", editEnd+"");  
//             
//             if(temp.length() > max){
//            		mToastShow.toastShow("不能超过140个字符！");
////            	 Toast.makeText(mContext, "不能超过140个字符！",
////							Toast.LENGTH_SHORT).show();
//                 s.delete(editStart-1, editEnd);  
//                 int tempSelection = editStart;  
//                 mEditText.setText(s);  
//                 mEditText.setSelection(tempSelection);  
//             }
		}

	};

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit);

		mContext = this;
		
		mToastShow = new ToastShow(mContext);
		
		findViews();
		
		
		mScrollView.setVerticalScrollBarEnabled(false);
		mScrollView.setScrollbarFadingEnabled(true);

		mEditText.addTextChangedListener(mTextWatcher);

		mFromWhat = this.getIntent().getExtras().getString(Strings.ID);

		if (mFromWhat.equals(Strings.TOADD)) {

			mServerId = -1;
			mIsComplete = 0;

			mTimeUtils = new TimeUtils();
			mRemindTime = mTimeUtils.getCurrentTime();

			// 这时候时间还没有设置，按钮显示‘当前时间’
			mButtonAlertTime.setText(mRemindTime);

			// 显示设置时间
			// mButtonAlertTime.setText("设置时间");

		} else {
			Bundle extras = getIntent().getExtras();

			// mUserId = extras.getInt(Task.UserId);

			mRowId = extras.getInt(Task.ID);

			mTitle = extras.getString(Task.Title);
			mEditText.setText(mTitle);
			mEditText.setSelection(mTitle.length());

			mServerId = extras.getInt(Task.Server_Id);
			mIsComplete = extras.getInt(Task.IsComplete);
			Log.d("test", " mIsComplete " + mIsComplete);

			mIsCommit = extras.getInt(Task.IsCommit);

			mRemindTime = extras.getString(Task.RemindTime);

			mAlertState = extras.getInt(Task.RemindPattern);
			Log.d("gxq", " get remindPattern:" + mAlertState);
			switch (mAlertState) {
			case 0:
				mSwitchAlertState.setChecked(false);
				break;
			case 1:
				mSwitchAlertState.setChecked(true);
				break;
			}

			// if (mSwitchAlertState.isChecked()) {

			// handle .0
			if (mRemindTime.endsWith(".0")) {
				int size = mRemindTime.length();
				mButtonAlertTime.setText(mRemindTime.subSequence(0, size - 2));
			} else {
				mButtonAlertTime.setText(mRemindTime);
			}
			// }
			// else {
			// mButtonAlertTime.setText("设置时间");
			// }

			mPriority = extras.getInt(Task.Priority);
			switch (mPriority) {
			case 1:
				mButtonPriority.setText("低");
				break;
			case 3:
				mButtonPriority.setText("高");
				break;
			default:
				mButtonPriority.setText("低");
			}

			mIsRemind = extras.getInt(Task.IsRemind);
			mDeleteFlag = extras.getInt(Task.DeleteFlag);
		}

	}

	protected void addRemindTime() {
		Log.d(TAG, "enter addRemindTime");
		ArrayList<AlertInfo> alertInfos = new ArrayList<AlertInfo>();
		AlertInfo alertInfo = new AlertInfo(mTitle, mRemindTime, mRowId);
		alertInfo.writeToParcel(AlertInfo.alertInfoParcel, 0);
		alertInfos.add(alertInfo);
		Intent intent = new Intent(mContext, MyAlarmManager.class);
		intent.putExtra(Strings.DELETE_FLAG, false);
		intent.putParcelableArrayListExtra(Strings.ALERTINFO, alertInfos);
		sendBroadcast(intent);
	}

	private void findViews() {
		mScrollView = (ScrollView)findViewById(R.id.editscrollview);
		
		mEditText = (EditText) EidtPageActivity.this
				.findViewById(R.id.et_title);
		mSwitchAlertState = (Switch) EidtPageActivity.this
				.findViewById(R.id.switch_alert);
		mButtonAlertTime = (Button) EidtPageActivity.this
				.findViewById(R.id.bt_alerttime);
		mButtonPriority = (Button) EidtPageActivity.this
				.findViewById(R.id.bt_prior);
		mButtonSave = (Button) EidtPageActivity.this.findViewById(R.id.bt_save);
		mButtonCancel = (Button) EidtPageActivity.this
				.findViewById(R.id.bt_cancel);

		mButtonSave.setOnClickListener(mOnClickListener);
		mButtonCancel.setOnClickListener(mOnClickListener);
		mButtonAlertTime.setOnClickListener(mOnClickListener);
		mButtonPriority.setOnClickListener(mOnClickListener);
		
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_PRIOR:
			   Log.d(TAG, "mPriority :" + mPriority);
			   int checkedItem = getCheckedItem(mPriority);
			   Log.d(TAG, "checkedItem :" + checkedItem);
					   
			return new AlertDialog.Builder(mContext)
					.setTitle("选择优先级")
					.setSingleChoiceItems(R.array.prior,
							checkedItem,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									mPriority = setCheckedItem(whichButton);
									mButtonPriority
											.setText(setPriorButton(mPriority));
									dialog.dismiss();
								}
							}).create();
		}
		return null;
	}

	private int getCheckedItem(int prior) {
		switch (prior) {
		case 3:
			return 0;
		case 1:
			return 1;
		default:
			return -1;
		}
	}

	private int setCheckedItem(int whichButton) {
		switch (whichButton) {
		case 0:
			return 3;
		case 1:
			return 1;
		default:
			return -1;
		}
	}

	private String setPriorButton(int prior) {
		String str = "低";
		switch (prior) {
		case 3:
			str = "高";
			break;
		case 1:
			str = "低";
			break;
		}
		return str;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		String isEdit = data.getStringExtra("edit");
		if (isEdit.equals("edit")) {
			mRemindTime = data.getStringExtra(Task.RemindTime);
			Log.d("test", "mremindtime is : " + mRemindTime);
			mButtonAlertTime.setText(mRemindTime);
		}
	}

	@Override
	public void onBackPressed() {
		Intent myintent = new Intent();
		myintent.putExtra(Strings.CATAGORY, Strings.BACKKEY);
		setResult(MainActivity.EDITPAGE, myintent);
		super.onBackPressed();
	}

	private void incrementSyncWhenUpate() {
		// start 有网的话就提交呗
		Log.d(TAG, "incrementSyncWhenUpate");
		// if (NetworkUtils.isConnectInternet(mContext)) {
		if (NetworkUtils.isConnectWifi(mContext)) {
			// if (mTestBoolean) {
			Log.d(TAG, "isConnectInternet");

			JSONObject serverTimeObj = DatabaseOperate.getServerTime();
			String editTime;
			try {
				editTime = serverTimeObj.getString("serverTime");
				Log.d(TAG, "time: " + editTime);
				mEditTime = editTime;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// mTimeUtils = new TimeUtils();
			// mEditTime = mTimeUtils.getCurrentTime();

			JSONObject parent = new JSONObject();

			try {
				parent.put(Strings.FLAG, true);

				JSONObject sub = new JSONObject();

				sub.put(Task.Server_Id, mServerId);
				sub.put(Task.UserId, mUserId);
				sub.put(Task.Title, mTitle);
				sub.put(Task.RemindTime, mRemindTime);
				sub.put(Task.Priority, mPriority);
				sub.put(Task.RemindPattern, mAlertState);
				sub.put(Task.IsComplete, mIsComplete);
				sub.put(Task.EditTime, mEditTime);
				Log.d(TAG, "mEditTime: " + mEditTime);
				sub.put(Task.IsCommit, mIsCommit);
				sub.put(Task.IsRemind, mIsRemind);
				sub.put(Task.DeleteFlag, mDeleteFlag);

				parent.put(Strings.RESULT, sub);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 增量同步，最后一条是这条新添加的数据，要对它做update操作，update它的serverid
			mJSONObject = DatabaseOperate.update(parent);
			try {
				mFlag = mJSONObject.getBoolean(Strings.FLAG);
				if (mFlag) {
					// 有数据需要更新
					Log.d(TAG, "有数据需要更新");
					mJSONArray = mJSONObject.getJSONArray(Strings.RESULT);
					Log.d(TAG, "update return : " + mJSONArray.toString());
					// 逐条取出来
					// 写入本地数据库
					int length = mJSONArray.length();
					for (int i = 0; i < length; i++) {
						JSONObject sub = mJSONArray.getJSONObject(i);

						serverid = sub.getInt(Task.Server_Id);
						title = sub.getString(Task.Title);
						remindtime = sub.getString(Task.RemindTime);
						prior = sub.getInt(Task.Priority);
						remindpattern = sub.getInt(Task.RemindPattern);
						iscomplete = sub.getInt(Task.IsComplete);
						edittime = sub.getString(Task.EditTime);
						iscommit = sub.getInt(Task.IsCommit);
						isremind = sub.getInt(Task.IsRemind);
						deleteflag = sub.getInt(Task.DeleteFlag);

						Log.d(TAG, "udapte title: " + title + "server id："
								+ serverid);

						MyContentValues myupdateContentValues = new MyContentValues(
								serverid, mUserId, title, remindtime, prior,
								remindpattern, iscomplete, edittime, isremind,
								iscommit, deleteflag);
						ContentValues cv_update = myupdateContentValues
								.getContentValues();

						if (mDbHelper.isServerIdExists(serverid)) {
							// 如果serverid在本地数据库中存在，则update 或
							// delete
							if (deleteflag == 1) {
								// 删除
								Log.d(TAG, "deleteflag == 1 delete");
								mDbHelper.deleteByServerid(serverid);
							} else if (deleteflag == 0) {
								// 更新
								Log.d(TAG, "deleteflag == 0 update");
								mDbHelper.updateByServerId(cv_update, serverid);
							}

						} else {
							// 直接插入
							Log.d(TAG, "insert!");
							mDbHelper.insert(cv_update);
						}
					}
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// end 有网的话就提交呗
		}
	}

	private void incrementSyncWhenInsert() {
		Log.d(TAG, "enter incrementSyncWhenInsert");
		// start 有网的话就提交呗
		// if (NetworkUtils.isConnectInternet(mContext)) {
		if (NetworkUtils.isConnectWifi(mContext)) {
			// if (mTestBoolean) {

			JSONObject serverTimeObj = DatabaseOperate.getServerTime();
			String editTime;
			try {
				editTime = serverTimeObj.getString("serverTime");
				Log.d(TAG, "time: " + editTime);
				mEditTime = editTime;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// mTimeUtils = new TimeUtils();
			// mEditTime = mTimeUtils.getCurrentTime();

			JSONObject parent = new JSONObject();
			Log.d(TAG, "enter isConnectInternet");
			try {
				parent.put(Strings.FLAG, true);
				// JSONArray ja = new JSONArray();

				JSONObject sub = new JSONObject();
				// 由于是新添加的，默认为-1
				sub.put(Task.Server_Id, mServerId);
				sub.put(Task.UserId, mUserId);
				sub.put(Task.Title, mTitle);
				sub.put(Task.RemindTime, mRemindTime);
				sub.put(Task.Priority, mPriority);
				sub.put(Task.RemindPattern, mAlertState);
				sub.put(Task.IsComplete, mIsComplete);
				sub.put(Task.EditTime, mEditTime);
				sub.put(Task.IsCommit, mIsCommit);
				sub.put(Task.IsRemind, 0);
				// 在线添加时，把-rowid放在DeleteFlag中，发给服务器
				sub.put(Task.DeleteFlag, -mInsertResult);

				// ja.put(sub);
				// parent.put(Strings.RESULT, ja);
				parent.put(Strings.RESULT, sub);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 增量同步，最后一条是这条新添加的数据，要对它做update操作，update它的serverid
			mJSONObject = DatabaseOperate.insert(parent);
			try {
				mFlag = mJSONObject.getBoolean(Strings.FLAG);
				if (mFlag) {
					// 有数据需要更新

					Log.d(TAG, "has " + mJSONObject.length()
							+ " data to update!");
					mJSONArray = mJSONObject.getJSONArray(Strings.RESULT);
					Log.d(TAG, "insert return : " + mJSONArray.toString());
					// 逐条取出来
					// 写入本地数据库
					int length = mJSONArray.length();
					for (int i = 0; i < length - 1; i++) {
						JSONObject sub = mJSONArray.getJSONObject(i);

						serverid = sub.getInt(Task.Server_Id);
						title = sub.getString(Task.Title);
						remindtime = sub.getString(Task.RemindTime);
						prior = sub.getInt(Task.Priority);
						remindpattern = sub.getInt(Task.RemindPattern);
						iscomplete = sub.getInt(Task.IsComplete);
						edittime = sub.getString(Task.EditTime);
						iscommit = sub.getInt(Task.IsCommit);
						isremind = sub.getInt(Task.IsRemind);
						deleteflag = sub.getInt(Task.DeleteFlag);

						Log.d(TAG, "udapte title: " + title + "server id："
								+ serverid);

						MyContentValues myupdateContentValues = new MyContentValues(
								serverid, mUserId, title, remindtime, prior,
								remindpattern, iscomplete, edittime, isremind,
								iscommit, deleteflag);
						ContentValues cv_update = myupdateContentValues
								.getContentValues();

						if (mDbHelper.isServerIdExists(serverid)) {
							Log.d(TAG, "isServerIdExists");

							// 如果serverid在本地数据库中存在，则update 或
							// delete
							if (deleteflag == 1) {
								// 删除
								Log.d(TAG, "deleteflag == 1 delete");
								mDbHelper.deleteByServerid(serverid);

								// end test thread
							} else if (deleteflag == 0) {
								// 更新
								Log.d(TAG, "deleteflag == 0 update");
								mDbHelper.updateByServerId(cv_update, serverid);
							}

						} else {
							// 直接插入
							Log.d(TAG, "insert new record");
							mDbHelper.insert(cv_update);
						}
					}

					// 最后一条数据，服务端返回数据时，-rowid在deleteflag中哦
					JSONObject sub = mJSONArray.getJSONObject(length - 1);
					Log.d(TAG, "handle with last record");

					serverid = sub.getInt(Task.Server_Id);
					Log.d(TAG, "serverid is : " + serverid);
					title = sub.getString(Task.Title);
					remindtime = sub.getString(Task.RemindTime);
					prior = sub.getInt(Task.Priority);
					remindpattern = sub.getInt(Task.RemindPattern);
					iscomplete = sub.getInt(Task.IsComplete);
					edittime = sub.getString(Task.EditTime);
					iscommit = sub.getInt(Task.IsCommit);
					isremind = sub.getInt(Task.IsRemind);
					deleteflag = sub.getInt(Task.DeleteFlag);

					MyContentValues myupdateContentValues = new MyContentValues(
							serverid, mUserId, title, remindtime, prior,
							remindpattern, iscomplete, edittime, isremind,
							iscommit, deleteflag);
					ContentValues cv_update = myupdateContentValues
							.getContentValues();

					// 取出rowid
					int rowid = -deleteflag;
					// 更新这条记录
					mDbHelper.update(cv_update, rowid);
				} else {
					Log.d(TAG, "has no data to update");
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// end 有网的话就提交呗
		}
	}

	class InsertTask extends AsyncTask<String, Integer, String> {

		public InsertTask() {

		}

		@Override
		protected String doInBackground(String... params) {
			Log.d(TAG, "enter doInBackground");
			// long id = Thread.currentThread().getId();
			// Log.d(TAG, "thread id : " + id);
			incrementSyncWhenInsert();
			return null;
		}
	}

	class UpdateTask extends AsyncTask<String, Integer, String> {

		public UpdateTask() {

		}

		@Override
		protected String doInBackground(String... params) {
			Log.d(TAG, "enter doInBackground");
			// long id = Thread.currentThread().getId();
			// Log.d(TAG, "thread id : " + id);
			incrementSyncWhenUpate();
			return null;
		}
	}

	private boolean isValidAlertTime(String alertTime) {
		long alertLong = 0;
		long currentLong = 0;

		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");

		Date currentDate = new Date();
		String currentString = dateFormat.format(currentDate);

		try {
			alertLong = dateFormat.parse(alertTime).getTime();
			currentLong = dateFormat.parse(currentString).getTime();
			Log.d("aaa", "alertLong: " + alertLong);
			Log.d("aaa", "currentLong: " + currentLong);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (alertLong < currentLong) {
			Log.d("aaa", "isNotValidAlertTime");
			return false;
		} else {
			Log.d("aaa", "isValidAlertTime");
			return true;
		}
	}

}
