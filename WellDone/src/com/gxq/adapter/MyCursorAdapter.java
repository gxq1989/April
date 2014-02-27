package com.gxq.adapter;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.gxq.db.TaskTableDb;
import com.gxq.db.TasksInfo.Task;
import com.gxq.utils.AlertInfo;
import com.gxq.utils.MyAlarmManager;
import com.gxq.utils.MyContentValues;
import com.gxq.utils.NetworkUtils;
import com.gxq.utils.Strings;
import com.gxq.utils.TimeUtils;
import com.gxq.utils.UserPreference;
import com.gxq.welldone.MainActivity;
import com.gxq.welldone.R;
import com.qihoo.renkj.todolist.DatabaseOperate;

public class MyCursorAdapter extends SimpleCursorAdapter {

	private static final String TAG = "MyCursorAdapter";

	private Context mContext;

	private Cursor mCursor;

	// private ViewHolder mViewHolder;

	private ViewHolder1 mViewHolder1;
	// private ViewHolder1 mViewHolder2;
	// private ViewHolder1 mViewHolder3;
	// private ViewHolder1 mViewHolder4;

	private int rowid;
	// private String mUserName;
	private int mUserId = UserPreference.getUserId();
	private int mServerId;
	private String mRemindTime;
	private int mPriority;
	private String mTitle;
	// remind pattern
	private int mAlertState;
	private int mIsComplete;
	private int mIsCommit;
	private int mDeleteFlag;
	private int mIsRemind;
	private String mEditTime;

	private TimeUtils mTimeUtils;

	private TaskTableDb mDbHelper = MainActivity.mDbHelper;

	private int mMyItemRowid;
	private int mMyItemServerId;
	// private int mMyItemUserId;
	private String mMyItemTitle;
	private String mMyItemRemindTime;
	private int mMyItemPriority;
	private int mMyItemAlertState;
	private int mMyItemIsComplete;
	private String mMyItemEditTime;
	private int mMyItemIsCommit;
	private int mMyItemIsRemind;
	private int mMyItemDeleteFlag;

	private JSONObject mJSONObject;
	private JSONArray mJSONArray;
	private boolean mFlag;

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

	private int remindtime_index;

	private int prior_index;

	private int title_index;

	private int remindpattern_index;

	private int iscomplete_index;

	private int rowid_index;

	private int serverid_index;

	private int iscommit_index;

	private int deleteflag_index;

	private int isremind_index;

	private int mViewType;

	private final static int TYPE1 = 1;
	private final static int TYPE2 = 2;
	private final static int TYPE3 = 3;
	private final static int TYPE4 = 4;

	// private boolean mTestBoolean = false;

	public MyCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		setColumnsIndex(c);
		mCursor = c;
		mContext = context;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		super.bindView(view, context, cursor);
		Log.d(TAG, "enter bindView");

		mContext = context;

		mCursor = cursor;

		final View mMyItemView = view;

		TextView titleTextView = (TextView) view
				.findViewById(R.id.tv_list_title);
		TextView remindtimeTextView = (TextView) view
				.findViewById(R.id.tv_list_alerttime);
		CheckBox isCompleteCheckBox = (CheckBox) view
				.findViewById(R.id.checkbox_state);
		ImageView remindPatternImageView = (ImageView) view
				.findViewById(R.id.iv_alarm);

		mRemindTime = cursor.getString(remindtime_index);
		mPriority = cursor.getInt(prior_index);
		mTitle = cursor.getString(title_index);
		mAlertState = cursor.getInt(remindpattern_index);
		mIsComplete = cursor.getInt(iscomplete_index);

		rowid = cursor.getInt(rowid_index);
		Log.d("rowid", "bind view rowid: " + rowid);
		// mUserName = cursor.getString(username_index);
		mServerId = cursor.getInt(serverid_index);
		mIsCommit = cursor.getInt(iscommit_index);
		mDeleteFlag = cursor.getInt(deleteflag_index);
		mIsRemind = cursor.getInt(isremind_index);

		// Log.d(TAG, "rowid: " + rowid + "alertime " + mRemindTime);

		// set values
		if (mIsComplete == 1) {
			titleTextView.setTextColor(Color.GRAY);
			remindtimeTextView.setTextColor(Color.GRAY);
			titleTextView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		}

		titleTextView.setText(mTitle);

		// if (mAlertState == 0) {
		// remindtimeTextView.setText("");
		// }else{
		// 处理从服务器获取时间时，2012-12-3 11:23:23.0 的问题
		if (mRemindTime.endsWith(".0")) {
			int size = mRemindTime.length();
			remindtimeTextView.setText(mRemindTime.subSequence(0, size - 2));
		} else {
			remindtimeTextView.setText(mRemindTime);
		}
		// }

		switch (mPriority) {
		// case 1:
		// // 低
		// priorityTextView.setText(" ");
		// break;
		case 3:
			// 高
			// priorityTextView.setText(" ");
			titleTextView.setTextColor(Color.RED);
			break;
		default:
			// priorityTextView.setText(" ");
		}

		if (mAlertState == 0) {
//			remindPatternImageView.setVisibility(View.INVISIBLE);
			remindPatternImageView.setVisibility(View.GONE);
		} else {
			remindPatternImageView.setBackgroundResource(R.drawable.alarm);
		}

		if (mIsComplete == 0) {
			isCompleteCheckBox.setChecked(false);
		} else {
			isCompleteCheckBox.setChecked(true);
		}
		isCompleteCheckBox.setText("");

		// switch (mViewType) {
		//
		// case TYPE1:
		// mViewHolder1.isCompleteCheckBox.setText("");
		// break;
		// case TYPE2:
		// mViewHolder2.isCompleteCheckBox.setText("");
		// break;
		// case TYPE3:
		// mViewHolder3.isCompleteCheckBox.setText("");
		// case TYPE4:
		// mViewHolder4.isCompleteCheckBox.setText("");
		// break;
		// }

		isCompleteCheckBox.setText("");

		view.setTag(R.string.rowid, rowid);
		view.setTag(R.string.remindtime, mRemindTime);
		view.setTag(R.string.prior, mPriority);
		view.setTag(R.string.title, mTitle);
		view.setTag(R.string.remindpattern, mAlertState);
		view.setTag(R.string.iscomplete, mIsComplete);
		// view.setTag(R.string.username, mUserName);
		view.setTag(R.string.serverid, mServerId);
		view.setTag(R.string.iscommit, mIsCommit);
		view.setTag(R.string.deleteflag, mDeleteFlag);
		view.setTag(R.string.isremind, mIsRemind);

		OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// 要判断checkbox是哪个item的哦，。。还有啊 其实rowid里存的是这个listview里的最后一个rowid
				// 所以要自己取呀取
				mMyItemRowid = (Integer) mMyItemView.getTag(R.string.rowid);

				// // 因为serverid的问题 要给他所有的数据！！！！
				// mServerId = (Integer) mMyItemView.getTag(R.string.serverid);
				// mTitle = (String) mMyItemView.getTag(R.string.title);
				// mRemindTime = (String)
				// mMyItemView.getTag(R.string.remindtime);
				// mPriority = (Integer) mMyItemView.getTag(R.string.prior);
				// mAlertState = (Integer) mMyItemView
				// .getTag(R.string.remindpattern);
				// mIsCommit = (Integer) mMyItemView.getTag(R.string.iscommit);
				// mIsRemind = (Integer) mMyItemView.getTag(R.string.isremind);
				// mDeleteFlag = (Integer)
				// mMyItemView.getTag(R.string.deleteflag);
				//
				// // mMyItemServerId = (Integer)
				// mMyItemView.getTag(R.string.serverid);
				// // mMyItemTitle = (String)
				// mMyItemView.getTag(R.string.title);
				// // mMyItemRemindTime = (String)
				// mMyItemView.getTag(R.string.remindtime);
				// // mMyItemPriority = (Integer)
				// mMyItemView.getTag(R.string.prior);
				// // mMyItemAlertState = (Integer) mMyItemView
				// // .getTag(R.string.remindpattern);
				// // mMyItemIsCommit = (Integer)
				// mMyItemView.getTag(R.string.iscommit);
				// // mMyItemIsRemind = (Integer)
				// mMyItemView.getTag(R.string.isremind);
				// // mMyItemDeleteFlag = (Integer)
				// mMyItemView.getTag(R.string.deleteflag);
				//
				// // 在变化的时候 应该写进数据库哦~~~
				// Log.d("rowid", "isChecked : " + isChecked);
				// Log.d("rowid", "myItemRowid rowid : " + mMyItemRowid);
				// Log.d("rowid", "mMyItemServerId rowid : " + mMyItemServerId);
				//
				// mIsComplete = myischecked(isChecked);
				// Log.d("rowid", "mIsComplete" + mIsComplete);
				//
				// mTimeUtils = new TimeUtils();
				// mEditTime = mTimeUtils.getCurrentTime();
				//
				// ContentValues values = new ContentValues();
				// values.put(Task.IsComplete, mIsComplete);
				// values.put(Task.EditTime, mEditTime);
				//
				// // 根据网络状态，对IsCommit字段进行填充
				//
				// // if (!NetworkUtils.isConnectInternet(mContext)) {
				// if (!NetworkUtils.isConnectWifi(mContext)) {
				// if (mIsCommit == 1) {
				// Log.d(TAG, "edit : mIsCommit == 1 do nothing");
				// // 如果之前是离线添加的，这里修改的话就不用设为2了！！
				// } else {
				// Log.d(TAG, "edit : mIsCommit :" + mIsCommit);
				// mIsCommit = 2;
				// Log.d(TAG, "edit : mIsCommit =2 !");
				// values.put(Task.IsCommit, mIsCommit);
				// }
				// }
				//
				// int updateresult = mDbHelper.update(values, mMyItemRowid);
				// Log.d("rowid", "update rowid " + mMyItemRowid);
				// Log.d("rowid", "update result " + updateresult);
				//
				// // 有网的话则提交数据
				// new UpdateTask().execute("update");
				//
				// if (mAlertState == 1 && mIsComplete == 1) {//
				// 有闹钟且标记已完成，就删掉这个闹钟啊
				// Log.d(TAG, "remove the alarm!");
				// removeRemindTime(mMyItemRowid, mTitle, mRemindTime);
				// }
				//
				// if (mAlertState == 1 && mIsComplete == 0) {//再次打开时，再加上啊
				// addRemindTime(mMyItemRowid, mTitle, mRemindTime);
				// }

				// 因为serverid的问题 要给他所有的数据！！！！
				mMyItemServerId = (Integer) mMyItemView
						.getTag(R.string.serverid);
				mMyItemTitle = (String) mMyItemView.getTag(R.string.title);
				mMyItemRemindTime = (String) mMyItemView
						.getTag(R.string.remindtime);
				mMyItemPriority = (Integer) mMyItemView.getTag(R.string.prior);
				mMyItemAlertState = (Integer) mMyItemView
						.getTag(R.string.remindpattern);
				mMyItemIsCommit = (Integer) mMyItemView
						.getTag(R.string.iscommit);
				mMyItemIsRemind = (Integer) mMyItemView
						.getTag(R.string.isremind);
				mMyItemDeleteFlag = (Integer) mMyItemView
						.getTag(R.string.deleteflag);

				// 在变化的时候 应该写进数据库哦~~~
				Log.d("rowid", "isChecked : " + isChecked);
				Log.d("rowid", "myItemRowid rowid : " + mMyItemRowid);
				Log.d("rowid", "mMyItemServerId rowid : " + mMyItemServerId);

				mMyItemIsComplete = myischecked(isChecked);
				Log.d("rowid", "mMyItemIsComplete" + mMyItemIsComplete);

				if (!NetworkUtils.isConnectWifi(mContext)) {
					mTimeUtils = new TimeUtils();
					mMyItemEditTime = mTimeUtils.getCurrentTime();
				} else {
					JSONObject serverTimeObj = DatabaseOperate.getServerTime();
					String editTime;
					try {
						editTime = serverTimeObj.getString("serverTime");
						Log.d(TAG, "time: " + editTime);
						mMyItemEditTime = editTime;
					} catch (JSONException e) {
						Log.d(TAG, "JSONException when get servertime");
						e.printStackTrace();
					}
				}

				ContentValues values = new ContentValues();
				values.put(Task.IsComplete, mMyItemIsComplete);
				values.put(Task.EditTime, mMyItemEditTime);

				// 根据网络状态，对IsCommit字段进行填充

				// if (!NetworkUtils.isConnectInternet(mContext)) {
				if (!NetworkUtils.isConnectWifi(mContext)) {
					if (mMyItemIsCommit == 1) {
						Log.d(TAG, "edit : mIsCommit == 1 do nothing");
						// 如果之前是离线添加的，这里修改的话就不用设为2了！！
					} else {
						Log.d(TAG, "edit : mIsCommit :" + mIsCommit);
						mMyItemIsCommit = 2;
						Log.d(TAG, "edit : mIsCommit =2 !");
						values.put(Task.IsCommit, mMyItemIsCommit);
					}
				}

				int updateresult = mDbHelper.update(values, mMyItemRowid);
				Log.d("rowid", "update rowid " + mMyItemRowid);
				Log.d("rowid", "update result " + updateresult);

				// 有网的话则提交数据
				new UpdateTask().execute("update");

				if (mMyItemAlertState == 1 && mMyItemIsComplete == 1) {// 有闹钟且标记已完成，就删掉这个闹钟啊
					Log.d(TAG, "remove the alarm!");
					removeRemindTime(mMyItemRowid, mMyItemTitle,
							mMyItemRemindTime);
				}

				if (mMyItemAlertState == 1 && mMyItemIsComplete == 0) {// 再次打开时，再加上啊
					addRemindTime(mMyItemRowid, mMyItemTitle, mMyItemRemindTime);
				}

			}

			protected void addRemindTime(int rowid, String title,
					String remindTime) {
				Log.d(TAG, "enter addRemindTime");
				ArrayList<AlertInfo> alertInfos = new ArrayList<AlertInfo>();
				AlertInfo alertInfo = new AlertInfo(title, remindTime, rowid);
				alertInfo.writeToParcel(AlertInfo.alertInfoParcel, 0);
				alertInfos.add(alertInfo);
				Intent intent = new Intent(mContext, MyAlarmManager.class);
				intent.putExtra(Strings.DELETE_FLAG, false);
				intent.putParcelableArrayListExtra(Strings.ALERTINFO,
						alertInfos);
				mContext.sendBroadcast(intent);
			}

			private int myischecked(boolean isChecked) {
				if (isChecked) {
					mMyItemView.setEnabled(false);
					Log.d("rowid", "myItemView.setClickable(false)");
					return 1;
				} else {
					return 0;
				}
			}

		};

		isCompleteCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);

		// switch (mViewType) {
		//
		// case TYPE1:
		// mViewHolder1.isCompleteCheckBox
		// .setOnCheckedChangeListener(mCheckedChangeListener);
		// break;
		// case TYPE2:
		// mViewHolder2.isCompleteCheckBox
		// .setOnCheckedChangeListener(mCheckedChangeListener);
		// break;
		// case TYPE3:
		// mViewHolder3.isCompleteCheckBox
		// .setOnCheckedChangeListener(mCheckedChangeListener);
		// break;
		// case TYPE4:
		// mViewHolder4.isCompleteCheckBox
		// .setOnCheckedChangeListener(mCheckedChangeListener);
		// break;
		// }

		// mViewHolder.isCompleteCheckBox
		// .setOnCheckedChangeListener(mCheckedChangeListener);
	}

	private void setColumnsIndex(Cursor cursor) {
		Log.d(TAG, "setColumnsIndex");
		remindtime_index = cursor.getColumnIndex(Task.RemindTime);
		prior_index = cursor.getColumnIndex(Task.Priority);
		title_index = cursor.getColumnIndex(Task.Title);
		remindpattern_index = cursor.getColumnIndex(Task.RemindPattern);
		iscomplete_index = cursor.getColumnIndex(Task.IsComplete);

		rowid_index = cursor.getColumnIndex(Task.ID);
		// int username_index = cursor.getColumnIndex(Task.UserName);
		serverid_index = cursor.getColumnIndex(Task.Server_Id);
		iscommit_index = cursor.getColumnIndex(Task.IsCommit);
		deleteflag_index = cursor.getColumnIndex(Task.DeleteFlag);
		isremind_index = cursor.getColumnIndex(Task.IsRemind);
	}

	private void incrementSyncWhenUpate() {
		// start 有网的话就提交呗
		Log.d(TAG, "incrementSyncWhenUpate");
		// if (NetworkUtils.isConnectInternet(mContext)) {
		if (NetworkUtils.isConnectWifi(mContext)) {
			// if(mTestBoolean ){
			Log.d(TAG, "isConnectInternet");

			// JSONObject serverTimeObj = DatabaseOperate.getServerTime();
			// String editTime;
			// try {
			// editTime = serverTimeObj.getString("serverTime");
			// Log.d(TAG, "time: " + editTime);
			// mMyItemEditTime = editTime;
			// } catch (JSONException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

			// mTimeUtils = new TimeUtils();
			// mMyItemEditTime = mTimeUtils.getCurrentTime();

			JSONObject parent = new JSONObject();

			try {
				parent.put(Strings.FLAG, true);

				JSONObject sub = new JSONObject();

				// sub.put(Task.Server_Id, mServerId);
				// Log.d(TAG, "serverid: " + mServerId);
				sub.put(Task.Server_Id, mMyItemServerId);
				Log.d(TAG, "serverid: " + mMyItemServerId);

				sub.put(Task.UserId, mUserId);
				sub.put(Task.Title, mMyItemTitle);
				Log.d(TAG, "mTitle: " + mMyItemTitle);
				sub.put(Task.RemindTime, mMyItemRemindTime);
				sub.put(Task.Priority, mMyItemPriority);
				sub.put(Task.RemindPattern, mMyItemAlertState);
				sub.put(Task.IsComplete, mMyItemIsComplete);
				sub.put(Task.EditTime, mMyItemEditTime);
				Log.d(TAG, "mEditTime: " + mMyItemEditTime);
				sub.put(Task.IsCommit, mMyItemIsCommit);
				//
				sub.put(Task.IsRemind, mMyItemIsRemind);
				sub.put(Task.DeleteFlag, mMyItemDeleteFlag);
				Log.d("gxq", "update submit : " + sub.toString());
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
					Log.d("gxq", "update return : " + mJSONArray.toString());
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

	private void removeRemindTime(int rowid, String title, String remindTime) {
		ArrayList<AlertInfo> alertInfos = new ArrayList<AlertInfo>();
		AlertInfo alertInfo = new AlertInfo(title, remindTime, rowid);
		alertInfo.writeToParcel(AlertInfo.alertInfoParcel, 0);
		alertInfos.add(alertInfo);
		Intent intent = new Intent(mContext, MyAlarmManager.class);
		intent.putExtra(Strings.DELETE_FLAG, true);
		Log.d(TAG, "removeRemindTime rowid: " + rowid);
		intent.putParcelableArrayListExtra(Strings.ALERTINFO, alertInfos);
		mContext.sendBroadcast(intent);
	}

	// static class ViewHolder {
	// public TextView titleTextView;
	// public TextView remindtimeTextView;
	// public ImageView remindPatternImageView;
	// public CheckBox isCompleteCheckBox;
	// }

	static class ViewHolder1 {
		public TextView titleTextView;
		public TextView remindtimeTextView;
		public ImageView remindPatternImageView;
		public CheckBox isCompleteCheckBox;
	}

	//
	// static class ViewHolder2 {
	// // public TextView titleTextView;
	// // public TextView remindtimeTextView;
	// public ImageView remindPatternImageView;
	// public CheckBox isCompleteCheckBox;
	// }
	//
	// static class ViewHolder3 {
	// // public TextView titleTextView;
	// // public TextView remindtimeTextView;
	// public ImageView remindPatternImageView;
	// public CheckBox isCompleteCheckBox;
	// }
	//
	// static class ViewHolder4 {
	// // public TextView titleTextView;
	// // public TextView remindtimeTextView;
	// public ImageView remindPatternImageView;
	// public CheckBox isCompleteCheckBox;
	// }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Log.d(TAG, "enter getView");
		if (!mCursor.moveToPosition(position)) {
			throw new IllegalStateException("couldn't move cursor to position "
					+ position);
		}

		// String title = mCursor.getString(title_index);
		// Log.d("cursoraa", "title: " + title);

		// 利用mAlertState，mIsComplete来决定用哪个viewType
		// int alertStateForGetView = mCursor.getInt(remindpattern_index);
		// int isCompleteForGetView = mCursor.getInt(iscomplete_index);
		//
		// mViewType = getType(alertStateForGetView, isCompleteForGetView);

		// Log.d(TAG, " getView mViewType" + mViewType);
		// Log.d(TAG, "title: " + title);

		// View v = convertView;

		// mViewHolder = new ViewHolder();

		// if (convertView == null) {
		// Log.d(TAG, "convertView == null");
		// convertView = newView(mContext, mCursor, parent);
		//
		// v = convertView;
		//
		// mViewHolder.titleTextView = (TextView) v
		// .findViewById(R.id.tv_list_title);
		// mViewHolder.remindtimeTextView = (TextView) v
		// .findViewById(R.id.tv_list_alerttime);
		// mViewHolder.remindPatternImageView = (ImageView) v
		// .findViewById(R.id.iv_alarm);
		// mViewHolder.isCompleteCheckBox = (CheckBox) v
		// .findViewById(R.id.checkbox_state);
		// v.setTag(mViewHolder);
		//
		// } else {
		// Log.d(TAG, "convertView != null");
		// v = convertView;
		// mViewHolder = (ViewHolder) v.getTag();
		// }
		// bindView(v, mContext, mCursor);
		// return v;

		// mViewHolder1 = new ViewHolder1();
		// mViewHolder2 = new ViewHolder1();
		// mViewHolder3 = new ViewHolder1();
		// mViewHolder4 = new ViewHolder1();

		// if (convertView == null) {
		Log.d(TAG, "convertView == null");
		convertView = newView(mContext, mCursor, parent);

		// mViewHolder1.titleTextView = (TextView) convertView
		// .findViewById(R.id.tv_list_title);
		// mViewHolder1.remindtimeTextView = (TextView) convertView
		// .findViewById(R.id.tv_list_alerttime);
		// mViewHolder1.remindPatternImageView = (ImageView) convertView
		// .findViewById(R.id.iv_alarm);
		// mViewHolder1.isCompleteCheckBox = (CheckBox) convertView
		// .findViewById(R.id.checkbox_state);
		// convertView.setTag(mViewHolder1);

		// switch (mViewType) {
		// case TYPE1:
		// Log.d(TAG, " set tag TYPE1");
		// mViewHolder1 = new ViewHolder1();
		// mViewHolder1.remindPatternImageView = (ImageView) convertView
		// .findViewById(R.id.iv_alarm);
		// mViewHolder1.isCompleteCheckBox = (CheckBox) convertView
		// .findViewById(R.id.checkbox_state);
		// convertView.setTag(mViewHolder1);
		// break;
		// case TYPE2:
		// Log.d(TAG, "set tag TYPE2");
		// mViewHolder2 = new ViewHolder2();
		// mViewHolder2.remindPatternImageView = (ImageView) convertView
		// .findViewById(R.id.iv_alarm);
		// mViewHolder2.isCompleteCheckBox = (CheckBox) convertView
		// .findViewById(R.id.checkbox_state);
		// convertView.setTag(mViewHolder2);
		// break;
		// case TYPE3:
		// Log.d(TAG, "set tag TYPE3");
		// mViewHolder3 = new ViewHolder3();
		// mViewHolder3.remindPatternImageView = (ImageView) convertView
		// .findViewById(R.id.iv_alarm);
		// mViewHolder3.isCompleteCheckBox = (CheckBox) convertView
		// .findViewById(R.id.checkbox_state);
		// convertView.setTag(mViewHolder3);
		// break;
		// case TYPE4:
		// Log.d(TAG, "set tag TYPE4");
		// mViewHolder4 = new ViewHolder4();
		// mViewHolder4.remindPatternImageView = (ImageView) convertView
		// .findViewById(R.id.iv_alarm);
		// mViewHolder4.isCompleteCheckBox = (CheckBox) convertView
		// .findViewById(R.id.checkbox_state);
		// convertView.setTag(mViewHolder4);
		// break;
		// }
		// } else {
		// Log.d(TAG, "convertView != null");
		//
		// mViewHolder1 = (ViewHolder1) convertView.getTag();

		// switch (mViewType) {
		// case TYPE1:
		// Log.d(TAG, "get tag TYPE1");
		// mViewHolder1 = (ViewHolder1) convertView.getTag();
		// break;
		// case TYPE2:
		// Log.d(TAG, "get tag TYPE2");
		// mViewHolder2 = (ViewHolder2) convertView.getTag();
		// break;
		// case TYPE3:
		// Log.d(TAG, "get tag TYPE3");
		// mViewHolder3 = (ViewHolder3) convertView.getTag();
		// break;
		// case TYPE4:
		// Log.d(TAG, "get tag TYPE4");
		// mViewHolder4 = (ViewHolder4) convertView.getTag();
		// break;
		// }

		// }

		// 设置预先的样子们 ，这样的话 在bindView中就不用再做了
		// switch (mViewType) {
		// case TYPE1:
		// Log.d(TAG, "set value TYPE1");
		// mViewHolder1.isCompleteCheckBox.setChecked(true);
		// mViewHolder1.remindPatternImageView.setVisibility(View.INVISIBLE);
		// break;
		// case TYPE2:
		// Log.d(TAG, "set value TYPE2");
		// mViewHolder1.isCompleteCheckBox.setChecked(true);
		// mViewHolder1.remindPatternImageView
		// .setBackgroundResource(R.drawable.alarm);
		// break;
		// case TYPE3:
		// Log.d(TAG, "set value TYPE3");
		// mViewHolder1.isCompleteCheckBox.setChecked(false);
		// mViewHolder1.remindPatternImageView.setVisibility(View.INVISIBLE);
		// break;
		// case TYPE4:
		// Log.d(TAG, "set value TYPE4");
		// mViewHolder1.isCompleteCheckBox.setChecked(false);
		// mViewHolder1.remindPatternImageView
		// .setBackgroundResource(R.drawable.alarm);
		// break;

		// case TYPE2:
		// Log.d(TAG, "set value TYPE2");
		// mViewHolder2.isCompleteCheckBox.setChecked(true);
		// mViewHolder2.remindPatternImageView
		// .setBackgroundResource(R.drawable.alarm);
		// break;
		// case TYPE3:
		// Log.d(TAG, "set value TYPE3");
		// mViewHolder3.isCompleteCheckBox.setChecked(false);
		// mViewHolder3.remindPatternImageView.setVisibility(View.INVISIBLE);
		// break;
		// case TYPE4:
		// Log.d(TAG, "set value TYPE4");
		// mViewHolder4.isCompleteCheckBox.setChecked(false);
		// mViewHolder4.remindPatternImageView
		// .setBackgroundResource(R.drawable.alarm);
		// break;
		// }

		bindView(convertView, mContext, mCursor);
		return convertView;

	}

	private int getType(int alertStateForGetView, int isCompleteForGetView) {
		if (alertStateForGetView == 0 && isCompleteForGetView == 1) {
			return TYPE1;
		} else if (alertStateForGetView == 1 && isCompleteForGetView == 1) {
			return TYPE2;
		} else if (alertStateForGetView == 0 && isCompleteForGetView == 0) {
			return TYPE3;
		} else if (alertStateForGetView == 1 && isCompleteForGetView == 0) {
			return TYPE4;
		}
		return 0;
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

	@Override
	public int getItemViewType(int position) {
		Log.d(TAG, "enter getItemViewType");
		Log.d(TAG, "mViewType" + mViewType);
		return mViewType;
	}

	@Override
	public int getViewTypeCount() {
		Log.d(TAG, "enter getViewTypeCount");
		return 4;
	}

}
