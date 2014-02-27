package com.gxq.utils;

import com.gxq.db.TasksInfo.Task;
import com.gxq.welldone.R;

public class Strings {
	public final static String NETWORKVALID = "network valid";
	public final static String ID = "from_id";
	public final static String TOADD = "to add";
	public final static String TOEDIT = "to edit";
	
	public final static String[] FROM = { Task.Title, Task.RemindTime,
			 Task.RemindPattern, Task.IsComplete };
	public final static int[] TO = { R.id.tv_list_title,
			R.id.tv_list_alerttime, R.id.iv_alarm, R.id.checkbox_state };
	
	public final static String CATAGORY = "catagory";
	public final static String TODAY = "today";
	public final static String TOMMORROW = "tommorrow";
	public final static String LATER = "later";
	public final static String UNFINISHED = "unfinished";
	public final static String FINISHED = "finished";
	public final static String BACKKEY = "back key";
	
	public final static String ALERTINFO = "alertinfo";
	public final static String TITLE = "title";
	
	public final static String DELETE_FLAG = "delte flag";
	
	public final static int INSERT = 0;
	public final static int DELETE = 1;
	public final static int UPDATE = 2;
	public final static int QUERY = 3;
	
	public final static String FLAG = "flag";
	public final static String RESULT = "result";
	
	public final static String TIMEOUT = "timeout";
	public final static String ISCOMPLETE = "iscomplete";
	
	
	
	

}
