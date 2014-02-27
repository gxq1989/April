package com.qihoo.renkj.todolist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.gxq.utils.Strings;

/**
 * �߳��࣬�����˽��н���
 * @author renkejiang
 *
 */

class RequestCallable implements Callable<JSONObject>{
	
	private static final String  TAG = "RequestCallable";
	 /**
	  * �ͻ�������ķ���˵�ַ
	  */
	 String path = "";
	 
	 /**
	  * �ͻ����������
	  */
	 Map<String, String> params = new HashMap<String, String>();
	 /**
	  * ������룬Ĭ����"utf-8"
	  */
	 String encoding = "utf-8";
	 
	 /**
	  * �������Ӧ������
	  */
	 InputStream responseInput;
	 
	 String sessionid =null;
	 
	 /**
	  * 
	  * @param params
	  * @param encoding
	  */
	 public RequestCallable(String url, Map<String, String> params, String encoding) {
		 this.params = params;
		 this.encoding = encoding;
		 this.path = url;
		 Log.d("myparams", "params" + params.toString() + "encoding:" + encoding);
		 
	 }
	 
	 /**
	  * 
	  * @param params
	  */
	 public RequestCallable(Map<String, String> params) {
		this.params = params;
		Log.d("myparams", params.toString());
		this.encoding = "utf-8";
	 }
	 
	 /**
	  * �̻߳ص���ִ���߳���Ҫ������
	  * @param string ����ֵ��������Future
	  */
	 public JSONObject call(){
		 JSONObject obj = null;
		 try {
			 
			 //get ��ʽ
//			status = sendGETRequest();
			 
			 //post����ʽ
//			status = sendPOSTRequest();
			 
			//
			boolean status = sendHttpClientPOSTRequest();
			//
			obj = getHttpClientResponse();
			obj.put(Strings.TIMEOUT, false);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		 return obj;
	 }

	 /**

	  * ͨ��HttpClient����post����
	  * @return ����״̬��
	  * @throws Exception

	  */

	 public boolean sendHttpClientPOSTRequest() throws Exception {
		 Log.d(TAG, "enter sendHttpClientPOSTRequest");

	  List<NameValuePair> pairs = new ArrayList<NameValuePair>();//����������

	  for(Map.Entry<String, String> entry:params.entrySet()){

	   pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));

	  }

	  //��ֹ�ͻ��˴��ݹ�ȥ�Ĳ��������룬��Ҫ�Դ����±����UTF-8

	  UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs,"UTF-8");

	  HttpPost httpPost = new HttpPost(path);

	  httpPost.setEntity(entity);

	  DefaultHttpClient client = new DefaultHttpClient();
	  
//	  if(Global.sessionId != null) {
//		  Log.d(TAG, "Global.sessionId != null");
////		  httpPost.setHeader("Set-Cookie", sessionId);
//		  httpPost.setHeader("Cookie", "JSESSIONID=" + Global.sessionId);
//	  }
	  
	  
	  HttpResponse response = client.execute(httpPost);
	  
	  Header header = response.getFirstHeader("Set-Cookie");
//	  if(Global.sessionId == null) {
//		  Log.d(TAG, "Global.sessionId == null");
//		  getSessionId(header);
//	  }

	  int statusCode = response.getStatusLine().getStatusCode();
	  
//	  Log.d("statusCode", statusCode+"");
	  responseInput = response.getEntity().getContent();
	  Log.d("my request", responseInput.toString());
	  if(statusCode == HttpStatus.SC_OK){

		  
		  return true;

	  }

	  return false;

	 }
	 
	 /**
	  * ��ȡ����˷��ص���ݣ��������װΪJSONObject����
	  */
	 public JSONObject getHttpClientResponse() throws IOException, JSONException {
		 InputStreamReader dis = new InputStreamReader(responseInput, "utf-8");
		 BufferedReader reader = new BufferedReader(dis);
		 StringBuffer buf = new StringBuffer();
		 String readline = "";
		 while((readline = reader.readLine()) != null) {
			  buf.append(readline);
		 }
//		 Log.d("buf", buf.toString());
		 if(!buf.toString().equals("")) {
			 Log.d("my response", buf.toString());
			 return new JSONObject(buf.toString());
		 }
		 else {
			 return new JSONObject();
		 }
	 }

	 

	 /**

	  * ����post����
	  * @return �����Ƿ�ɹ�

	  */

	 public boolean sendPOSTRequest() throws Exception{

	  StringBuilder data = new StringBuilder(path);

	  for(Map.Entry<String, String> entry:params.entrySet()){

	  data.append(entry.getKey()).append("=");

	   //��ֹ�ͻ��˴��ݹ�ȥ�Ĳ��������룬��Ҫ�Դ����±����UTF-8

	  data.append(URLEncoder.encode(entry.getValue(),encoding));

	   data.append("&");

	  }

	 

	  data.deleteCharAt(data.length() - 1);

	 

	  byte[] entity = data.toString().getBytes();//�õ�ʵ�����

	  HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();

	  conn.setConnectTimeout(5000);

	  conn.setRequestMethod("POST");

	 

	  conn.setDoOutput(true);//����Ϊ�������������

	 

	 conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

	 conn.setRequestProperty("Content-Length", String.valueOf(entity.length));

	 

	  OutputStream outStream = conn.getOutputStream();

	  outStream.write(entity);//д������

	  if(conn.getResponseCode()==200){//ֻ��ȡ�÷�����ص�httpЭ����κ�һ������ʱ���ܰ������ͳ�ȥ

	   return true;

	  }

	 

	  return false;

	 }

	 

	 /**

	  * ����GET����
	  * @return �����Ƿ�ɹ�

	  * @throws Exception

	  */

	 public boolean sendGETRequest() throws Exception {

	  StringBuilder url = new StringBuilder(path);

	  url.append("?");

	  for(Map.Entry<String, String> entry:params.entrySet()){

	  url.append(entry.getKey()).append("=");

	   //get��ʽ�������ʱ�Բ������utf-8���룬URLEncoder

	   //��ֹ�ͻ��˴��ݹ�ȥ�Ĳ��������룬��Ҫ�Դ����±����UTF-8

	  url.append(URLEncoder.encode(entry.getValue(), encoding));

	  url.append("&");

	  }

	  url.deleteCharAt(url.length()-1);

	  HttpURLConnection conn = (HttpURLConnection) new URL(url.toString()).openConnection();

	  conn.setConnectTimeout(5000);

	  conn.setRequestMethod("GET");
	  

	  if(conn.getResponseCode() == 200){

	   return true;

	  }

	  return false;

	 }
	 
	 public static void getSessionId(Header h) {
		 //JSESSIONID=2D20D0E699B02D6C3218AB60F270712A; Path=/welldone
		String value = h.getValue();
		int end = value.indexOf(';');
		Global.sessionId = value.substring(11, end);
		Log.d("cookie", Global.sessionId);
	 }
	 
}
