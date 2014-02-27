package com.gxq.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.gxq.db.TasksInfo.Task;
import com.gxq.utils.Strings;
import com.gxq.utils.WellDoneApp;

public class TaskTableDb extends SQLiteOpenHelper {

	private static final String TAG = "TaskTableDb";
	private static final String DB_NAME = "welldone.s3db";
	private static final int DB_VERSION = 2;

	private static final String ORDER_BY = Task.IsComplete + ","
			+ Task.RemindTime + " asc";
	// private static final String ORDER_BY = Task.IsComplete + " asc";

	private static final String EXCEPT_delete_offline = " and " + Task.IsCommit
			+ "<>3";

	private static final String EXCEPT_iscomplete = " and " + Task.IsComplete + "<> 1";  
	
	private static final String QUERY_by_userid = " and " + Task.UserId + "= ?";

	private static final String ORDER_BY_REMINDTIME = Task.RemindTime + " asc";

	public static final String TABLE_NAME_TASK_TABLE = "tablename_tasktable";
	private static final String TABLE_CREATE_TASK = "create table "
			+ TABLE_NAME_TASK_TABLE
			+ "("
			+ Task.ID
			+ " integer primary key autoincrement,"
			+ Task.Server_Id
			+ " integer default -1 not null,"
			// userid
			+ Task.UserId
			+ " integer default 1  not null,"
			//
			+ Task.UserName
			+ " varchar(50),"
			+ Task.Title
			+ " varchar(140) default \'study sqlite\' not null,"
			// + Task.RemindTime + " text default \'2013-08-20 11:35:00\',"
			+ Task.RemindTime + " datetime," + Task.Priority
			+ " integer default 1 not null," + Task.RemindPattern
			+ " integer default 0 not null," + Task.IsComplete
			+ " integer default 0 not null," + Task.EditTime

			// is remind
			+ " integer," + Task.IsRemind
			// deleteflag
			+ " integer," + Task.DeleteFlag

			+ " text not null," + Task.IsCommit + " integer default 0)";

	// to adapt to the _id issue
	// rowid as _id
	private String[] TASK_COLUMNS = new String[] { "rowid as _id",
			Task.Server_Id, Task.UserId, Task.UserName, Task.Title,
			Task.RemindTime, Task.Priority, Task.RemindPattern,
			Task.IsComplete, Task.EditTime, Task.IsRemind, Task.IsCommit,
			Task.DeleteFlag };

	// constructor function
	public TaskTableDb(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	public TaskTableDb(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	public TaskTableDb() {
		super(WellDoneApp.getInstance(), DB_NAME, null, DB_VERSION);
	}

	/**
	 * Called when the database is created for the first time. This is where the
	 * creation of tables and the initial population of the tables should
	 * happen.
	 * 
	 * @param db
	 *            The database.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "enter onCreate");
		db.execSQL(TABLE_CREATE_TASK);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists TABLE_CREATE_TASK");
		onCreate(db);
	}

	// insert
	public long insert(ContentValues values) {
		Log.d(TAG, "enter insert");
		SQLiteDatabase db = getWritableDatabase();
		long result = db.insert(TABLE_NAME_TASK_TABLE, null, values);
		if (result >= 0) {
			Log.d(TAG, "insert successfully");
			String title = (String) values.get(Task.Title);
			Log.d(TAG, "title : " + title);
			TaskTableDbListener.getInstance().notifyUpdated();
		} else {
			Log.d(TAG, "insert failed");
		}
		return result;
	}

	public int update(ContentValues values, int rowid) {
		Log.d("rowid", "update rowid = : " + rowid);
		Log.d("rowid", "iscomplete: " + values.get(Task.IsComplete));
		SQLiteDatabase db = getWritableDatabase();
		int result = db.update(TABLE_NAME_TASK_TABLE, values, Task.ID + "=?",
				new String[] { String.valueOf(rowid) });
		if (result > 0) {
			// 因为checkbox的状态是在adapter里监听并保存的，在checkbox状态改变后，界面已经被触发刷新了，这时就不再需要再次触发刷新
			// 而对于其他的update操作来说，都是在另外一个activity里做的，再次回到这个list时，可以通过onResume()里刷新来解决数据的更新问题
			TaskTableDbListener.getInstance().notifyUpdated();
		} else {
		}
		Log.d("rowid", "update result : " + result);
		return result;

	}

	// udpate by server id
	public int updateByServerId(ContentValues values, int serverid) {
		Log.d("rowid", "update serverid = : " + serverid);
		Log.d("rowid", "iscomplete: " + values.get(Task.IsComplete));
		SQLiteDatabase db = getWritableDatabase();
		int result = db.update(TABLE_NAME_TASK_TABLE, values, Task.Server_Id
				+ "=?", new String[] { String.valueOf(serverid) });
		if (result > 0) {
			TaskTableDbListener.getInstance().notifyUpdated();
		} else {
		}
		Log.d(TAG, "update result : " + result);
		return result;

	}

	// query
	public Cursor query(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy) {
		SQLiteDatabase db = getReadableDatabase();
		return db.query(table, columns, selection, selectionArgs, groupBy,
				having, orderBy);
	}

	// public Cursor queryAll() {
	// // Log.d(TAG, "ORDER_BY: " + ORDER_BY);
	// SQLiteDatabase db = getReadableDatabase();
	// return db.query(TABLE_NAME_TASK_TABLE, TASK_COLUMNS, null, null, null,
	// null, null);
	// }

	// query today or tommorrow
	public Cursor queryByTime(String time, int userid) {
		SQLiteDatabase db = getReadableDatabase();
		// Log.d(TAG, ORDER_BY);
		// remindtime like '2013-8-21%'
		Log.d(TAG, "like string: " + Task.RemindTime + " like \'" + time
				+ "%\'");
		return db.query(TABLE_NAME_TASK_TABLE, TASK_COLUMNS, Task.RemindTime
				+ " like \'" + time + "%\'" + EXCEPT_delete_offline
				+ QUERY_by_userid, new String[] { String.valueOf(userid) },
				null, null, ORDER_BY, null);
	}

	public boolean isServerIdExists(int serverid) {

		SQLiteDatabase db = getReadableDatabase();
		Log.d(TAG, "serverid " + serverid);

		Cursor cursor = db.query(TABLE_NAME_TASK_TABLE, TASK_COLUMNS,
				Task.Server_Id + "=?",
				new String[] { String.valueOf(serverid) }, null, null, null);
		if (cursor == null) {
			Log.d(TAG, "isServerIdExists false");
			return false;
		} else if (cursor.getCount() > 0) {
			Log.d(TAG, "isServerIdExists true");
			cursor.close();
			return true;
		} else {
			Log.d(TAG, "isServerIdExists false");
			cursor.close();
			return false;
		}

	}

	// query later
	// 这里time就是明天的时间哦，later是指的明天以后。。。
	public Cursor queryLater(int userid) {

		SQLiteDatabase db = getReadableDatabase();
		Log.d(TAG, "julianday(" + Task.RemindTime
				+ ") - julianday(\'now\') > 2");
		return db.query(TABLE_NAME_TASK_TABLE, TASK_COLUMNS, "julianday("
				+ Task.RemindTime + ") - julianday(\'now\') > 2"
				+ EXCEPT_delete_offline + QUERY_by_userid,
				new String[] { String.valueOf(userid) }, null, null, ORDER_BY,
				null);
	}

	public Cursor queryUnfinished(int userid) {
		SQLiteDatabase db = getReadableDatabase();
		return db
				.query(TABLE_NAME_TASK_TABLE, TASK_COLUMNS, Task.IsComplete
						+ "= 0" + EXCEPT_delete_offline + QUERY_by_userid,
						new String[] { String.valueOf(userid) }, null, null,
						ORDER_BY_REMINDTIME, null);
	}

	public Cursor queryFinished(int userid) {
		SQLiteDatabase db = getReadableDatabase();
		return db
				.query(TABLE_NAME_TASK_TABLE, TASK_COLUMNS, Task.IsComplete
						+ "= 1" + EXCEPT_delete_offline + QUERY_by_userid,
						new String[] { String.valueOf(userid) }, null, null,
						ORDER_BY_REMINDTIME, null);
	}

//	public Cursor queryById(int rowid, int userid) {
//		SQLiteDatabase db = getReadableDatabase();
//		return db
//				.query(TABLE_NAME_TASK_TABLE, TASK_COLUMNS, Task.ID
//						+ "= ?" + EXCEPT_delete_offline + QUERY_by_userid,
//						new String[] { String.valueOf(rowid), String.valueOf(userid) }, null, null,
//						null, null);
//	}
	
	// 查询离线的状态下改动过的数据们
	public Cursor queryOfflineData(int userid) {
		SQLiteDatabase db = getReadableDatabase();
		return db
				.query(TABLE_NAME_TASK_TABLE, TASK_COLUMNS, Task.IsCommit
						+ "<> 0" + QUERY_by_userid,
						new String[] { String.valueOf(userid) }, null, null,
						null, null);
	}

	// 查询需要提醒的时间们
	// 最好是提醒只返回大于当前时间的提醒时间们。。之前的又没有用的。。
	public Cursor queryRemindPattern(int userid) {
		SQLiteDatabase db = getReadableDatabase();
		return db
				.query(TABLE_NAME_TASK_TABLE, TASK_COLUMNS, Task.RemindPattern
						+ "= 1" + EXCEPT_delete_offline + QUERY_by_userid + EXCEPT_iscomplete,
						new String[] { String.valueOf(userid) }, null, null,
						null, null);
	}

	public int deleteByRowid(int rowid_int) {
		SQLiteDatabase db = getWritableDatabase();

		String rowid_string = String.valueOf(rowid_int);
		String whereClause = "rowid" + "=" + rowid_string;
		int result = db.delete(TABLE_NAME_TASK_TABLE, whereClause, null);
		if (result > 0) {
			Log.d(TAG, "delete success");
			TaskTableDbListener.getInstance().notifyUpdated();
		} else {
			Log.d(TAG, "delete fail");
		}
		return result;
	}

	public int deleteByServerid(int serverid_int) {
		SQLiteDatabase db = getWritableDatabase();

		String serverid_string = String.valueOf(serverid_int);
		String whereClause = Task.Server_Id + "=" + serverid_string;
		int result = db.delete(TABLE_NAME_TASK_TABLE, whereClause, null);
		if (result > 0) {
			Log.d(TAG, "delete success");
			TaskTableDbListener.getInstance().notifyUpdated();
		} else {
			Log.d(TAG, "delete fail");
		}
		return result;
	}

	public int deleteAll(int userid) {
		SQLiteDatabase db = getWritableDatabase();

		int result = db.delete(TABLE_NAME_TASK_TABLE, Task.UserId + "= ?",
				new String[] { String.valueOf(userid) });
		if (result > 0) {
			Log.d(TAG, "delete success");
			TaskTableDbListener.getInstance().notifyUpdated();
		} else {
			Log.d(TAG, "delete fail");
		}
		return result;
	}
}
