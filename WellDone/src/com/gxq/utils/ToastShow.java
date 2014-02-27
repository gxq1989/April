package com.gxq.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class ToastShow {
	
	private static final String TAG = "ToastShow";
	
	private Context mContext;
	private Toast mToast = null;

	public ToastShow(Context context) {
		this.mContext = context;
	}

//	使用这段代码用于显示Toast就不会出现时间地叠加，下面分析一下。个人认为只是一个Toast在不停的显示，
//	只是其内容（也就是出现的提示消息）不同，当Toast=null时，出现一个Toast，
//	如果这个Toast还没有消失时就要出现第二个，那么只是使用其setText()方法改变了内容，文档对setText()方法地解释是：
//	Update the text in a Toast that was previously created using one of the makeText() methods.
//	F这里要注意只是更新哦，并不是把新的toast插入队列
//	也就是：更新之前创建的一个使用makeText()方法的Toast里的文本。看到这也许你就明白了。
	public void toastShow(String text) {
		if (mToast == null) {
			Log.d(TAG, "mToast == null");
			mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
		} else {
			Log.d(TAG, "mToast != null");
			mToast.setText(text);
		}
		mToast.show();

		
	}
}