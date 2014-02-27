package com.gxq.welldone;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.gxq.adapter.MyCursorAdapter;
import com.gxq.db.TaskTableDb;
import com.gxq.db.TaskTableDbListener;
import com.gxq.db.TasksInfo;
import com.gxq.db.TasksInfo.Task;
import com.gxq.utils.AlertInfo;
import com.gxq.utils.DisplayUtils;
import com.gxq.utils.MyAlarmManager;
import com.gxq.utils.MyContentValues;
import com.gxq.utils.NetworkUtils;
import com.gxq.utils.Strings;
import com.gxq.utils.TimeUtils;
import com.gxq.utils.ToastShow;
import com.gxq.utils.UserPreference;
import com.qihoo.renkj.todolist.DatabaseOperate;

/**
 * @author xiaqing
 *
 */
public class MainActivity extends ListActivity {

	private static final String TAG = "MainActivity";

	public final static int EDITPAGE = 12;

	// 用来标识是否是离线模式
	private boolean mIsOutline;

	private Context mContext;
	private DisplayUtils mDisplayUtils;
	private float mWidth;
	private float mHeight;

	private boolean mDeleteAllFlag;

	private ListView mListView;

	float mDownX, mDownY;
	float mUpX, mUpY;

	private MyCursorAdapter mMyAdapter;
	private Cursor mCursor;

	public static TaskTableDb mDbHelper;

	private CheckBox mCheckBox;

	private TimeUtils mTimeUtils;

	private final int DIALOG_EDIT_DELETE = 0;
	private final int DIALOG_CATAGORY = 1;

	private MenuItem mMenuItem;

	private String mCatagory;

	private Handler mHandler;

	private JSONObject mJSONObject;
	private JSONArray mJSONArray;
	private boolean mFlag;

	private int serverid_index;

	private int title_index;

	private int remindtime_index;

	private int prior_index;

	private int remindpattern_index;

	private int iscomplete_index;

	private int edittime_index;

	private int iscommit_index;

	private int isremind_index;

	private int deleteflag_index;

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

	private int mServerId;
	private int mUserId = UserPreference.getUserId();
	private String mTitle;
	private String mRemindTime;
	private int mPriority;
	private int mAlertState;
	private int mIsComplete;
	private String mEditTime;
	private int mIsCommit;
	private int mIsRemind;
	private int mDeleteFlag;

	private boolean mDeleteSuccess = false;

	private ToastShow mToastShow;

	private OnItemLongClickListener mOnItemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			Log.d("test", "position = " + position);

			final int rowid = (Integer) view.getTag(R.string.rowid);
			final int serverid = (Integer) view.getTag(R.string.serverid);
			// final String username = (String) view.getTag(R.string.username);
			final String title = (String) view.getTag(R.string.title);
			final String remindTime = (String) view.getTag(R.string.remindtime);
			final int priority = (Integer) view.getTag(R.string.prior);
			final int alertState = (Integer) view
					.getTag(R.string.remindpattern);
			final int isComplete = (Integer) view.getTag(R.string.iscomplete);
			final int remindPattern = (Integer) view
					.getTag(R.string.remindpattern);
			final int isCommit = (Integer) view.getTag(R.string.iscommit);
			final int isRemind = (Integer) view.getTag(R.string.isremind);
			final int deleteFlag = (Integer) view.getTag(R.string.deleteflag);

			mServerId = serverid;
			// mUserId = userId;
			mTitle = title;
			mRemindTime = remindTime;
			mPriority = priority;
			mAlertState = alertState;
			mIsComplete = isComplete;

			mIsCommit = isCommit;
			mIsRemind = isRemind;
			mDeleteFlag = deleteFlag;

			new AlertDialog.Builder(mContext)
					.setTitle("选择操作")
					.setItems(R.array.operation,
							new DialogInterface.OnClickListener() {
								// private boolean mTestBoolean = false;

								private boolean mEnter;

								public void onClick(DialogInterface dialog,
										int whichButton) {
									switch (whichButton) {
									case 0:

										// 查看
										new AlertDialog.Builder(mContext)
												.setTitle("任务详情")
												.setMessage(title).show();
										break;
									case 1:
										// 删除
										// 不管在什么情况下，都应该先把提醒给删了
										removeRemindTime(rowid, title,
												remindTime);
										// start 网络正常的情况下删除数据
										// if (NetworkUtils
										// .isConnectInternet(mContext)) {
										if (NetworkUtils
												.isConnectWifi(mContext)) {
											Log.d(TAG, "has network");
											// if(mTestBoolean){
											mTimeUtils = new TimeUtils();
											String editTime = mTimeUtils
													.getCurrentTime();
											mEditTime = editTime;

											// JSONObject serverTimeObj =
											// DatabaseOperate.getServerTime();
											// String editTime;
											// try {
											// editTime =
											// serverTimeObj.getString("serverTime");
											// Log.d(TAG, "time: " + editTime);
											// mEditTime = editTime;
											// } catch (JSONException e) {
											// // TODO Auto-generated catch
											// block
											// e.printStackTrace();
											// }

											int delete_result = mDbHelper
													.deleteByRowid(rowid);
											if (delete_result > 0) {
												Log.d(TAG, "删除成功");
												mToastShow.toastShow("删除成功");
												// Toast.makeText(mContext,
												// "删除成功",
												// Toast.LENGTH_SHORT)
												// .show();
											} else {
												mToastShow.toastShow("删除失败");
												// Toast.makeText(mContext,
												// "删除失败",
												// Toast.LENGTH_SHORT)
												// .show();
												Log.d(TAG, "删除失败");
											}
											new DeleteTask().execute("delete");

											// 应该先从服务端删除，如果成功的话，再从本地删除
											// 如果mDeleteSuccess为true，就表示操作成功，否则的话要进入离线的状态哦
											// 没有网或者是从服务端操作失败
										} else {
											// 离线的时候删除数据，本地不是真正的删除，只是打了一个标记位，IsCommit为3
											// 这里要注意对于离线状态下添加的数据，做删除的话，就直接删了吧。也不用设为3了！！
											if (isCommit == 1) {
												// 如果之前是离线添加的，这里修改的话就不用设为2了！！
												// 直接删除
												Log.d(TAG,
														"isCommit == 1 delete!");
												int delete_result = mDbHelper
														.deleteByRowid(rowid);
												if (delete_result > 0) {
													Log.d(TAG, "删除成功");
													mToastShow.toastShow("删除成功");
//													Toast.makeText(mContext,
//															"删除成功",
//															Toast.LENGTH_SHORT)
//															.show();
												} else {
													mToastShow.toastShow("删除失败");
//													Toast.makeText(mContext,
//															"删除失败",
//															Toast.LENGTH_SHORT)
//															.show();
													Log.d(TAG, "删除失败");
												}
											} else {
												Log.d(TAG,
														"isCommit =3 update to 3");

												mTimeUtils = new TimeUtils();
												String editTime = mTimeUtils
														.getCurrentTime();

												ContentValues values = new ContentValues();
												values.put(Task.IsCommit, 3);
												values.put(Task.EditTime,
														editTime);
												mDbHelper.update(values, rowid);
											}

										}
										break;
									}
								}
							}).show();
			return false;
		}
	};

	TaskTableDbListener.OnDBUpdatedListener mDBUpdatedListener = new TaskTableDbListener.OnDBUpdatedListener() {

		@Override
		public void onUpdated() {
			Log.d(TAG, "onUpdated");
			MainActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					refreshList();
				}
			});
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mContext = this;

		mToastShow = new ToastShow(mContext);

		TaskTableDbListener.getInstance().registerListener(mDBUpdatedListener);

		mDisplayUtils = new DisplayUtils(this);
		mWidth = mDisplayUtils.getWidth();
		mHeight = mDisplayUtils.getHeight();
		Log.d(TAG, "width : " + mWidth + ";height: " + mHeight);

		mListView = getListView();

		mDbHelper = new TaskTableDb(mContext);

		// 这个主要是用于后续对数据进行操作时，如果说是在离线的情况下的话，要对相应的字段做标记
		// 其实这个没有用的吧。。。。
		mIsOutline = !getIntent().getExtras().getBoolean(Strings.NETWORKVALID);
		Log.d(TAG, "mIsOutline： " + mIsOutline);

		// 第一次登陆做全量同步
		new WellDoneSyncTask().execute("welldonesync");

		// 第一次进入只显示今天的所有数据
		mTimeUtils = new TimeUtils();

		mCursor = mDbHelper.queryByTime(mTimeUtils.getToday(), mUserId);
		mMyAdapter = new MyCursorAdapter(mContext, R.layout.mylistitem_xml,
				mCursor, Strings.FROM, Strings.TO);
		setListAdapter(mMyAdapter);

		mListView.setOnItemLongClickListener(mOnItemLongClickListener);

		// mListView.setOnTouchListener(new OnTouchListener() {
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// Log.d(TAG, "action is " + event.getAction());
		//
		// if (event.getAction() == MotionEvent.ACTION_DOWN) {
		// mDownX = event.getX();
		// mDownY = event.getY();
		// Log.d(TAG, "downX = " + mDownX + ";downY = " + mDownY);
		// } else if (event.getAction() == MotionEvent.ACTION_UP) {
		// mUpX = event.getX();
		// mUpY = event.getY();
		// Log.d(TAG, "upX = " + mUpX + ";upY = " + mUpY);
		//
		// int position1 = ((ListView) v).pointToPosition(
		// (int) mDownX, (int) mDownY);
		// int position2 = ((ListView) v).pointToPosition((int) mUpX,
		// (int) mUpY);
		//
		// Log.d(TAG, "position1 = " + position1 + ";position2 = "
		// + position2);
		//
		// // there must be at least one item in the list ，that is
		// // position must
		// // >=0
		// if (position1 >= 0 && position1 == position2
		// && Math.abs(mDownX - mUpX) > mWidth / 3) {
		// Log.d(TAG, "downX = " + mDownX + ";upY = " + mUpY);
		// Log.d(TAG, "remove!");
		// View view = ((ListView) v).getChildAt(position1
		// - mListView.getFirstVisiblePosition());
		//
		// // removeListItem(view, position1);
		// // removeListItem(position1);o
		// }
		// }
		// return false;
		// }
		// });

		// mEditText = (EditText) findViewById(R.id.editText1);

		// mImageView = (ImageView) this.findViewById(R.id.imageView1);
		// mImageView.setOnClickListener(mOnClickListener);

		// mSortButton = (Button) this.findViewById(R.id.btn_sort);
		// mSortButton.setOnClickListener(mOnClickListener);

		// mRefreshButton = (Button) this.findViewById(R.id.bt_refresh);
		// mRefreshButton.setOnClickListener(mOnClickListener);

	}

	protected void refreshList() {
		Log.d("deleteflag", "enter refreshList");
		Log.d("deleteflag", "mDeleteAllFlagbefore: " + mDeleteAllFlag);
		mCursor.requery();
		mListView.setAdapter(mMyAdapter);

		if (mDeleteAllFlag) {
			getListView().setBackgroundResource(R.drawable.editback);
		} else if (mCursor.getCount() == 0) {
			getListView().setBackgroundResource(R.drawable.emptylistback);
		} else {
			getListView().setBackgroundResource(R.drawable.editback);
		}

		// if (mCursor.getCount() == 0) {
		// if (mDeleteAllFlag) {
		// getListView().setBackgroundResource(R.drawable.editback);
		// } else {
		// getListView().setBackgroundResource(R.drawable.emptylistback);
		// }
		// } else {
		// getListView().setBackgroundResource(R.drawable.editback);
		// }

		Log.d("deleteflag", "mDeleteAllFlag after : " + mDeleteAllFlag);
	}

	protected void refreshList(Cursor myCursor) {
		mCursor = myCursor;
		mMyAdapter = new MyCursorAdapter(mContext, R.layout.mylistitem_xml,
				myCursor, Strings.FROM, Strings.TO);
		setListAdapter(mMyAdapter);

		if (mCursor.getCount() == 0) {
			getListView().setBackgroundResource(R.drawable.emptylistback);
		} else {
			getListView().setBackgroundResource(R.drawable.editback);
		}
	}

	// protected void removeListItem(View rowView, final int position) {
	//
	// final Animation animation = (Animation) AnimationUtils.loadAnimation(
	// rowView.getContext(), R.layout.anim_xml);
	// animation.setAnimationListener(new AnimationListener() {
	// public void onAnimationStart(Animation animation) {
	// Log.d(TAG, "onAnimationStart");
	// }
	//
	// public void onAnimationRepeat(Animation animation) {
	// Log.d(TAG, "onAnimationRepeat");
	// }
	//
	// public void onAnimationEnd(Animation animation) {
	// Log.d(TAG, "onAnimationEnd");
	// // mMyAdapter.mMyListData.mArrayList.remove(position);
	// mMyAdapter.notifyDataSetChanged();
	// }
	// });
	//
	// rowView.startAnimation(animation);
	// }
	//
	// protected void removeListItem(final int position) {
	// // mMyAdapter.mMyListData.mArrayList.remove(position);
	// mMyAdapter.notifyDataSetChanged();
	// }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		mMenuItem = menu.getItem(0);
		Log.d(TAG, "mMenuItem title: " + mMenuItem.getTitle());
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Log.d(TAG, "position : " + position);

		int isComplete = (Integer) v.getTag(R.string.iscomplete);
		// 已完成的禁止进入编辑哦 但是可以把已完成的变成未完成的
		if (isComplete == 0) {
			// 编辑
			int rowid = (Integer) v.getTag(R.string.rowid);
			Log.d("rowid", " on item click rowid : " + rowid);
			String title = (String) v.getTag(R.string.title);
			String remindTime = (String) v.getTag(R.string.remindtime);
			int priority = (Integer) v.getTag(R.string.prior);
			int remindPattern = (Integer) v.getTag(R.string.remindpattern);
			Log.d("gxq", " remindPattern:" + remindPattern);
			int serverid = (Integer) v.getTag(R.string.serverid);
			// String username = (String) v.getTag(R.string.username);
			int iscommit = (Integer) v.getTag(R.string.iscommit);

			int isremind = (Integer) v.getTag(R.string.isremind);
			int deleteflag = (Integer) v.getTag(R.string.deleteflag);

			Intent intent = new Intent(mContext, EidtPageActivity.class);
			intent.putExtra(Strings.ID, Strings.TOEDIT);
			intent.putExtra(Task.ID, rowid);
			intent.putExtra(Task.Title, title);
			intent.putExtra(Task.RemindTime, remindTime);
			intent.putExtra(Task.Priority, priority);
			intent.putExtra(Task.IsComplete, isComplete);
			intent.putExtra(Task.RemindPattern, remindPattern);
			intent.putExtra(Task.Server_Id, serverid);
			// intent.putExtra(Task.UserName, username);
			intent.putExtra(Task.IsCommit, iscommit);

			intent.putExtra(Task.UserId, mUserId);
			intent.putExtra(Task.IsRemind, isremind);
			intent.putExtra(Task.DeleteFlag, deleteflag);

			startActivityForResult(intent, EDITPAGE);
		} else {
			// Toast.makeText(mContext, "已完成的事项不支持编辑！",
			// Toast.LENGTH_SHORT).show();
			mToastShow.toastShow("已完成的事项不支持编辑！");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mCatagory == null) {
			refreshList();
			// query_today();
		} else

		// 再次回来的时候要根据新添加数据的时间来确定要载入哪个list 今天。。明天。。以后。。
		if (mCatagory.equals(Strings.TODAY)) {
			query_today();
		} else if (mCatagory.equals(Strings.TOMMORROW)) {
			query_tommorrow();
		} else if (mCatagory.equals(Strings.LATER)) {
			query_later();
		} else if (mCatagory.equals(Strings.UNFINISHED)) {
			query_unfinished();
		} else if (mCatagory.equals(Strings.FINISHED)) {
			query_finished();
		} else if (mCatagory.equals(Strings.BACKKEY)) {
			query_today();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mCursor.close();
		mDbHelper.close();
		TaskTableDbListener.getInstance()
				.unregisterListener(mDBUpdatedListener);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_EDIT_DELETE:
			return new AlertDialog.Builder(mContext)
					.setTitle("选择操作")
					.setItems(R.array.operation,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									switch (whichButton) {

									}
								}
							}).create();
		case DIALOG_CATAGORY:
			return new AlertDialog.Builder(mContext)
					.setTitle("分类查看")
					.setItems(R.array.catagory,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									switch (whichButton) {
									case 0:// today
										query_today();
										break;
									case 1:// tommorrow
										query_tommorrow();
										break;
									case 2: // later
										query_later();
										break;
									case 3: // unfinished
										query_unfinished();
										break;
									case 4: // finished
										query_finished();
										break;
									}
								}
							}).create();

		}
		return null;
	}

	protected void loadFromServer() throws JSONException {
		// if (NetworkUtils.isConnectInternet(mContext)) {
		if (NetworkUtils.isConnectWifi(mContext)) {
			Log.d(TAG, "isConnectInternet true");
			// 如果有网的话，才会做这些操作哦
			// 先检查本地是不是有离线的数据，如果有的话，要先提交离线数据啊。。。
			Cursor cursor_offline = mDbHelper.queryOfflineData(mUserId);
			int size = cursor_offline.getCount();
			Log.d(TAG, "offline data size : " + size);

			// index
			setIndexes(cursor_offline);

			// start 构造JSONObject
			JSONObject parent = new JSONObject();
			mFlag = setFlag(size);
			Log.d(TAG, "flag ：" + mFlag);
			parent.put(Strings.FLAG, mFlag);

			if (mFlag) {
				// true，说明有离线数据需要上传
				// 存在JSONArray中
				JSONArray ja = new JSONArray();

				// 遍历每条记录
				while (cursor_offline.moveToNext()) {
					// set values
					setValues(cursor_offline);

					JSONObject sub = new JSONObject();

					sub.put(Task.Server_Id, serverid);
					sub.put(Task.UserId, mUserId);
					sub.put(Task.Title, title);
					sub.put(Task.RemindTime, remindtime);
					sub.put(Task.Priority, prior);
					sub.put(Task.RemindPattern, remindpattern);
					sub.put(Task.IsComplete, iscomplete);
					sub.put(Task.EditTime, edittime);
					sub.put(Task.IsCommit, iscommit);
					sub.put(Task.IsRemind, isremind);
					sub.put(Task.DeleteFlag, deleteflag);
					// 加到JSONArray中
					ja.put(sub);
				}
				// 放到parent中发送出去
				parent.put(Strings.RESULT, ja);

			} else {
				// 没有离线数据需要上传
				// 把userid传给服务端，服务端要根据这个来决定更新的数据哦
				parent.put(Task.UserId, mUserId);
			}

			mJSONObject = DatabaseOperate.insertOffline(parent);

			// end 构造JSONObject

			// 解析服务端返回的mJSONObject，做全量同步哦
			mFlag = mJSONObject.getBoolean(Strings.FLAG);
			if (mFlag) {
				// 有数据需要更新
				// 在delete all 之前 应该要删除所有的alarm
				// 对alarm进行清空的操作
				Cursor cursor = mDbHelper.queryRemindPattern(mUserId);
				while (cursor.moveToNext()) {
					int myrowid = cursor.getInt(cursor.getColumnIndex(Task.ID));
					String mytitle = cursor.getString(cursor
							.getColumnIndex(Task.Title));
					String myremindTime = cursor.getString(cursor
							.getColumnIndex(Task.RemindTime));
					removeRemindTime(myrowid, mytitle, myremindTime);
				}
				cursor.close();

				// 删除本地数据库
				mDbHelper.deleteAll(mUserId);
				mDeleteAllFlag = true;
				Log.d("deleteflag",
						"mDeleteAllFlag is set to be true after deleteall");
				mJSONArray = mJSONObject.getJSONArray(Strings.RESULT);
				Log.d(TAG, "load from server array: " + mJSONArray);
				// 逐条取出来
				// 写入本地数据库
				int length = mJSONArray.length();
				for (int i = 0; i < length; i++) {
					mDeleteAllFlag = true;
					Log.d("deleteflag", "i = : " + i);
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

					MyContentValues myContentValues = new MyContentValues(
							serverid, mUserId, title, remindtime, prior,
							remindpattern, iscomplete, edittime, isremind,
							iscommit, deleteflag);
					ContentValues cv = myContentValues.getContentValues();

					mDbHelper.insert(cv);
					Log.d("deleteflag", "later i : " + i);

				}

			} else {
				mDeleteAllFlag = false;
				// 无数据需要更新 ，直接导入本地数据库
				Log.d("deleteflag", "无数据需要更新 ，直接导入本地数据库");
				Log.d("deleteflag", "mDeleteAllFlag :" + mDeleteAllFlag);
			}
			mDeleteAllFlag = false;
		} else {
			// 无网络 ，直接导入本地数据库
			MainActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mToastShow.toastShow("网络状况不佳，请检查网络连接！");
					// Toast.makeText(mContext, "网络状况不佳，请检查网络连接！",
					// Toast.LENGTH_SHORT).show();
				}
			});
		}

		addRemindTime();
		// mDeleteAllFlag = false;

	}

	private void setValues(Cursor cursor_offline) {
		serverid = cursor_offline.getInt(serverid_index);
		title = cursor_offline.getString(title_index);
		remindtime = cursor_offline.getString(remindtime_index);
		prior = cursor_offline.getInt(prior_index);
		remindpattern = cursor_offline.getInt(remindpattern_index);
		iscomplete = cursor_offline.getInt(iscomplete_index);
		edittime = cursor_offline.getString(edittime_index);
		iscommit = cursor_offline.getInt(iscommit_index);
		isremind = cursor_offline.getInt(isremind_index);
		deleteflag = cursor_offline.getInt(deleteflag_index);

	}

	private void setIndexes(Cursor cursor_offline) {
		serverid_index = cursor_offline.getColumnIndex(Task.Server_Id);
		title_index = cursor_offline.getColumnIndex(Task.Title);
		remindtime_index = cursor_offline.getColumnIndex(Task.RemindTime);
		prior_index = cursor_offline.getColumnIndex(Task.Priority);
		remindpattern_index = cursor_offline.getColumnIndex(Task.RemindPattern);
		iscomplete_index = cursor_offline.getColumnIndex(Task.IsComplete);
		edittime_index = cursor_offline.getColumnIndex(Task.EditTime);
		iscommit_index = cursor_offline.getColumnIndex(Task.IsCommit);
		isremind_index = cursor_offline.getColumnIndex(Task.IsRemind);
		deleteflag_index = cursor_offline.getColumnIndex(Task.DeleteFlag);
	}

	private void addRemindTime() {
		// start test for alert
		// 查询需要提醒的时间们，存在一个数组里
		Cursor cursor = mDbHelper.queryRemindPattern(mUserId);
		Log.d(TAG, "count: " + cursor.getCount());

		ArrayList<AlertInfo> alertInfos = new ArrayList<AlertInfo>();

		String title;
		String remindTime;
		int id;
		int iscomplete;

		int index_title = cursor.getColumnIndex(Task.Title);
		int index_remindtime = cursor.getColumnIndex(Task.RemindTime);
		int index_id = cursor.getColumnIndex(Task.ID);
		int iscomplete_id = cursor.getColumnIndex(Task.IsComplete);

		while (cursor.moveToNext()) {
			title = cursor.getString(index_title);
			remindTime = cursor.getString(index_remindtime);
			id = cursor.getInt(index_id);
			iscomplete = cursor.getInt(iscomplete_id);

			AlertInfo alertInfo = new AlertInfo(title, remindTime, id);
			alertInfo.writeToParcel(AlertInfo.alertInfoParcel, 0);
			alertInfos.add(alertInfo);
		}
		Log.d(TAG, "size: " + alertInfos.size());

		cursor.close();

		Intent intent = new Intent(this, MyAlarmManager.class);
		intent.putParcelableArrayListExtra(Strings.ALERTINFO, alertInfos);
		this.sendBroadcast(intent);

		Log.d("alert", "sendBroadcast");
		// end test for alert
	}

	private boolean setFlag(int size) {
		if (size > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_add:
			// add
			Intent intent = new Intent(mContext, EidtPageActivity.class);
			intent.putExtra(Strings.ID, Strings.TOADD);
			intent.putExtra(Task.IsComplete, 0);
			startActivityForResult(intent, EDITPAGE);
			break;
		case R.id.item_show:
			showDialog(DIALOG_CATAGORY);
			break;
		case R.id.item_refresh:
			Log.d(TAG, "refresh!");
			new WellDoneSyncTask().execute("welldonesync");
			break;
		case R.id.item_exit:
			exit();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void query_finished() {
		mMenuItem.setTitle("已完成");
		Cursor cursor_finished = mDbHelper.queryFinished(mUserId);
		refreshList(cursor_finished);
	}

	private void query_unfinished() {
		mMenuItem.setTitle("未完成");
		Cursor cursor_unfinished = mDbHelper.queryUnfinished(mUserId);
		refreshList(cursor_unfinished);
	}

	private void query_later() {
		mMenuItem.setTitle("以后");
		mTimeUtils = new TimeUtils();
		Cursor cursor_later = mDbHelper.queryLater(mUserId);
		refreshList(cursor_later);

	}

	private void query_tommorrow() {
		mMenuItem.setTitle("明天");
		mTimeUtils = new TimeUtils();
		Cursor cursor_tommorrow = mDbHelper.queryByTime(
				mTimeUtils.getTommorrow(), mUserId);
		refreshList(cursor_tommorrow);
	}

	private void query_today() {
		mMenuItem.setTitle("今天");
		mTimeUtils = new TimeUtils();
		Cursor cursor_today = mDbHelper.queryByTime(mTimeUtils.getToday(),
				mUserId);
		refreshList(cursor_today);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mCatagory = data.getStringExtra(Strings.CATAGORY);
		Log.d(TAG, "mCatagory: " + mCatagory);
	}

	@Override
	public void onBackPressed() {
		Intent startMain = new Intent(Intent.ACTION_MAIN);
		startMain.addCategory(Intent.CATEGORY_HOME);
		startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(startMain);
		// System.exit(0);
		// finish();
	}

	private void exit() {
		Intent startMain = new Intent(Intent.ACTION_MAIN);
		startMain.addCategory(Intent.CATEGORY_HOME);
		startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(startMain);
		System.exit(0);
		finish();
	}

	private void removeRemindTime(int rowid, String title, String remindTime) {
		ArrayList<AlertInfo> alertInfos = new ArrayList<AlertInfo>();
		AlertInfo alertInfo = new AlertInfo(title, remindTime, rowid);
		alertInfo.writeToParcel(AlertInfo.alertInfoParcel, 0);
		alertInfos.add(alertInfo);
		Intent intent = new Intent(mContext, MyAlarmManager.class);
		Log.d(TAG, "removeRemindTime rowid: " + rowid);
		intent.putExtra(Strings.DELETE_FLAG, true);
		intent.putParcelableArrayListExtra(Strings.ALERTINFO, alertInfos);
		sendBroadcast(intent);
	}

	private void incrementSyncWhenDelete() throws JSONException {
		// start 有网的话就提交呗
		// if (NetworkUtils.isConnectInternet(mContext)) {
		if (NetworkUtils.isConnectWifi(mContext)) {

			// mTimeUtils = new TimeUtils();
			// mEditTime = mTimeUtils.getCurrentTime();

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
				sub.put(Task.IsCommit, mIsCommit);
				//
				sub.put(Task.IsRemind, mIsRemind);
				sub.put(Task.DeleteFlag, mDeleteFlag);

				parent.put(Strings.RESULT, sub);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 增量同步，最后一条是这条新添加的数据，要对它做update操作，update它的serverid
			mJSONObject = DatabaseOperate.delete(parent);

			// // 先来判断是不是连接超时了
			boolean timeout = mJSONObject.getBoolean(Strings.TIMEOUT);
			if (timeout) {

				MainActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mDeleteSuccess = false;
						mToastShow.toastShow("网络状态不佳，请稍后重试！");
						// Toast.makeText(mContext, "网络状态不佳，请稍后重试！",
						// Toast.LENGTH_SHORT).show();
					}
				});

			} else {
				try {
					mFlag = mJSONObject.getBoolean(Strings.FLAG);
					mDeleteSuccess = true;
					if (mFlag) {
						// 有数据需要更新
						mJSONArray = mJSONObject.getJSONArray(Strings.RESULT);
						Log.d(TAG, "delete return :" + mJSONArray.toString());
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

							MyContentValues myupdateContentValues = new MyContentValues(
									serverid, mUserId, title, remindtime,
									prior, remindpattern, iscomplete, edittime,
									isremind, iscommit, deleteflag);
							ContentValues cv_update = myupdateContentValues
									.getContentValues();

							if (mDbHelper.isServerIdExists(serverid)) {
								// 如果serverid在本地数据库中存在，则update 或
								// delete
								if (deleteflag == 1) {
									// 删除
									Log.d(TAG, "deleteflag == 1");
									mDbHelper.deleteByServerid(serverid);
								} else if (deleteflag == 0) {
									// 更新
									Log.d(TAG, "deleteflag == 0");
									mDbHelper.updateByServerId(cv_update,
											serverid);
								}

							} else {
								// 直接插入
								Log.d(TAG, "insert");
								if (deleteflag == 1) {
									// 如果deleteflag=1的话，说明是之前本地删除的数据，这里不做处理
								} else {
									mDbHelper.insert(cv_update);
								}
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
	}

	class DeleteTask extends AsyncTask<String, Integer, String> {

		public DeleteTask() {

		}

		@Override
		protected String doInBackground(String... params) {
			Log.d(TAG, "enter doInBackground");
			// long id = Thread.currentThread().getId();
			// Log.d(TAG, "thread id : " + id);
			try {
				incrementSyncWhenDelete();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}

	class WellDoneSyncTask extends AsyncTask<String, Integer, String> {

		ProgressDialog progressDialog;

		public WellDoneSyncTask() {
			progressDialog = new ProgressDialog(mContext);
		}

		@Override
		protected void onPreExecute() {
			progressDialog.setMessage("同步中...");
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
				loadFromServer();
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

}
