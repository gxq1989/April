package com.gxq.welldone.test;

import junit.framework.Assert;
import android.content.Context;
import android.test.AndroidTestCase;

import com.gxq.db.TaskTableDb;
import com.gxq.utils.TimeUtils;

public class MyTest extends AndroidTestCase {
	private static final String Tag = "MyTest";
	
	private Context mContext;
	private TimeUtils mTimeUtils;
	private String mToday;
	public static TaskTableDb mDbHelper;

	public void testGetTodayRight() throws Throwable

	{
		Assert.assertEquals("2013-10-11", mToday);
		getContext();

	}

	public void testGetTodayError() throws Throwable

	{
		
		Assert.assertEquals("2013-10-10", mToday);

	}

	public void testSomethingElse() throws Throwable {

		Assert.assertTrue(1 + 1 == 2);

	}
	
	public void testDeleteByRowid(){
		int result = mDbHelper.deleteByRowid(-1);
		Assert.assertTrue(result > 0);
	}

	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		mContext = getContext();
		mTimeUtils = new TimeUtils();
		mToday = mTimeUtils.getToday();
		mDbHelper = new TaskTableDb(mContext);
	}

	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}

}
