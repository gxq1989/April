package com.qihoo.renkj.todolist;

import android.app.Activity;

public class NewsManageActivity extends Activity {

//    /** Called when the activity is first created. */
//
// 
//
// EditText titleText;
//
// EditText lengthText;
//
// Button button;
//
//    @Override
//
//    public void onCreate(Bundle savedInstanceState) {
//
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.main);
//
//       
//
//        titleText = (EditText) this.findViewById(R.id.title);
//
//        lengthText = (EditText) this.findViewById(R.id.timelength);
//
//       
//
//        button = (Button) this.findViewById(R.id.button);
//
//       
//
//    }
//
//   
//
//    public void save(View v) throws Exception{
//
//     String title = titleText.getText().toString();
//
//     String timelength = new String(lengthText.getText().toString());
////     Log.d("timelength", timelength);
//     
//     JSONObject obj = new JSONObject();
//     obj.put("title", title);
//     obj.put("aa", timelength);
//     obj.put("name", "名字");
//     obj.put("fun", "121");
//     
//     
////     JSONObject res = DatabaseOperate.find();
////    
////     
////     @SuppressWarnings("unchecked")
////	Iterator<String> it = res.keys();
////     while(it.hasNext()) {
////    	 String key = it.next();
////    	 String value = res.getString(key);
////    	 Log.d(key, value);
////     }
//
//     boolean save = DatabaseOperate.save(obj);
////     Log.d("save", save+"");
////     boolean update = DatabaseOperate.update(obj);
////     Log.d("update", update+"");
//     boolean delet = DatabaseOperate.delet(obj);
////     Log.d("delet", delet+"");
//     JSONObject find = DatabaseOperate.find("renkj");
//     
//     
////     JSONObject parent = new JSONObject();
////		
////		for(int i=0;i<5;i++) {
////			JSONObject son = new JSONObject();
////			son.put("name"+i, "renkj"+i);
////			son.put("age"+i, 20+i);
////			parent.put("id"+i, son);
////		}
////     
////     boolean saveOffline = DatabaseOperate.saveOffline(parent);
////     Log.d("saveOffline", saveOffline+"");
//
//     
////     Log.d("status", result+"");
////
////     if(result){
////
////      Toast.makeText(getApplicationContext(), R.string.success, Toast.LENGTH_LONG).show();
////
////     } else {
////
////      Toast.makeText(getApplicationContext(), R.string.fail, Toast.LENGTH_LONG).show();
////
////     }
//
//    }

}
