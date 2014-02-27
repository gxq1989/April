package com.gxq.data;


public class MyListData {

//	public static final String KEY_TITLE = "title";
//	public static final String KEY_PRIORITY = "priority";
//
//	public ArrayList<HashMap<String, Object>> mArrayList;
//	
//	private HashMap<String, Object> mMap;
//
//	public MyListData() {
//		mArrayList = new ArrayList<HashMap<String, Object>>();
//		getData();
//
//	}
//
//	private void getData() {
//		try {
//			JSONTokener jsonParser = new JSONTokener(MyJsonTest.JSON);
//			JSONObject person = (JSONObject) jsonParser.nextValue();
//
//			JSONArray jsonArray = person.getJSONArray("people");
//			int length = jsonArray.length();
//			Log.d("gxq", "array size is " + length);
//			for (int i = 0; i < length; i++) {
//				JSONObject jsonObject = jsonArray.getJSONObject(i);
//				String firstname = jsonObject.getString("firstName");
//				String lastname = jsonObject.getString("lastName");
//				Log.d("gxq", "firstname: " + firstname);
//				Log.d("gxq", "lastname: " + lastname);
//
//				mMap = new HashMap<String, Object>();
//				mMap.put("firstName", firstname);
//				mMap.put("lastName", lastname);
//				mArrayList.add(mMap);
//				
//				Log.d("gxq", "arrayList size is " + mArrayList.size());
//			}
//
//		} catch (JSONException ex) {
//			// 异常处理代码
//			// tv.setText(ex.toString());
//		}
//	}
//
//	public ArrayList<HashMap<String, Object>> getArrayList() {
//		return mArrayList;
//	}
//
//	public int getSize() {
//		return mArrayList.size();
//	}

}
