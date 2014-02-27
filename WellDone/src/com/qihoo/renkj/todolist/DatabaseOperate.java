package com.qihoo.renkj.todolist;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.gxq.utils.Strings;

/**
 * �ͻ��������˽��н����Ĳ���
 * 
 * @author renkejiang
 * 
 */

public class DatabaseOperate {

	static ExecutorService exs = null;

	private static final String TAG = "DatabaseOperate";
	
	static String host = "http://10.16.15.88:9091/welldone/";
//	 static String host = "http://10.18.60.212:8080/welldone/";

	/**
	 * ������ݣ����ݲ����web�������
	 * 
	 * @param obj
	 *            Ҫ�־û������
	 * @return �Ƿ�洢�ɹ�
	 */

	public static JSONObject insert(JSONObject obj) {

		String home = "android/CreateAndriodServlet";
		String url = host + home;
		Log.d("database", "enter insert!");
		Log.d("database", "url: " + url);
		try {
			obj.put("terminal", "android");
			Log.d(TAG, "insert send to server: " + obj.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return execute(url, obj);
	}

	/**
	 * �������
	 * 
	 * @param obj
	 *            Ҫ���µ����
	 * @return
	 */
	public static JSONObject update(JSONObject obj) {
		Log.d(TAG, "update");
		String home = "android/UpdateAndroidServlet";

		String url = host + home;
		try {
			obj.put("terminal", "android");
			Log.d(TAG, "update send to server: " + obj.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return execute(url, obj);
	}

	/**
	 * ɾ���¼
	 * 
	 * @param obj
	 *            Ҫɾ���¼����Ϣ
	 * @return
	 */
	public static JSONObject delete(JSONObject obj) {
		String home = "android/DeleteAndroidServlet";

		String url = host + home;
		try {
			obj.put("terminal", "android");
			Log.d(TAG, "delete send to server: " + obj.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return execute(url, obj);
	}

	//
	public static JSONObject find(String userName) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("UserName", userName);
			obj.put("operate", "find");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String home = "android/FindAndriodServlet";

		String url = host + home;
		return execute(url, obj);

	}

	public static JSONObject insertOffline(JSONObject obj) {
		try {
			obj.put("terminal", "android");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d(TAG, "enter insertOffline");
		Log.d(TAG, "length: " + obj.length());
		Log.d(TAG, "insertOffline send to server: " + obj.toString());
		String home = "android/OfflineAndroidServlet";

		String url = host + home;
		
		return execute(url, obj);
	}

	public static JSONObject login(JSONObject obj) {
		String home = "Login";
		try {
			Log.d(TAG, "login : " + obj.toString());
			obj.put("terminal", "android");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String url = host + home;
		return execute(url, obj);
	}

	
	public static JSONObject getServerTime() {
		String home = "android/GetServerTime";
		JSONObject obj = new JSONObject();
		try {
			Log.d(TAG, "GetServerTime : " + obj.toString());
			obj.put("terminal", "android");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String url = host + home;
		return execute(url, obj);
	}
	
	
	private static JSONObject execute(String path, JSONObject obj) {
		Future<JSONObject> future = request(path, obj);
		if (path.contains("Login")) {
			try {
				future.get(5000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e1) {
				Log.d(TAG, "InterruptedException");
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				Log.d(TAG, "ExecutionException");
				e1.printStackTrace();
			} catch (TimeoutException e1) {
				e1.printStackTrace();

				Log.d(TAG, "TimeoutException");
				future.cancel(false);
				Log.d(TAG, "is future cancelled: " + future.isCancelled());

				JSONObject parent = new JSONObject();
				try {
					parent.put(Strings.TIMEOUT, true);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return parent;
			}
		}

		// boolean futureState = future.isCancelled();
		//
		// Log.d(TAG, "futureState： " + futureState);

		try {
			Log.d(TAG, "futureState enter try");

			JSONObject json = future.get();
			Log.d(TAG, "get from server :" + json.toString());
			return future.get();
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * ����һ���߳̽��н���
	 * 
	 * @param obj
	 * @return ����˷��ص���Ϣ
	 */
	public static Future<JSONObject> request(String path, JSONObject obj) {
		if (exs == null) {
			exs = Executors.newCachedThreadPool();

		}
		RequestCallable t = new RequestCallable(path, parserJSONObject(obj),
				"utf-8");
		return exs.submit(t);
	}

	public static void shutdown() {
		exs.shutdownNow();
	}

	/**
	 * ��jsoNObject����ת��
	 * 
	 * @param obj
	 * @return
	 */
	public static Map<String, String> parserJSONObject(JSONObject obj) {
		Map<String, String> params = new HashMap<String, String>();

		@SuppressWarnings("unchecked")
		Iterator<String> it = obj.keys();

		while (it.hasNext()) {
			String key = it.next();
			try {
				String value = obj.get(key).toString();
				params.put(key, URLEncoder.encode(value, "UTF-8"));
				// Log.d(TAG, value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return params;

	}

}